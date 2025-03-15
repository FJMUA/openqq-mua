package cn.byteforge.openqq.task;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ThreadPoolMonitor {
    private final String prefix;
    private final ThreadPoolExecutor executor; // 要监控的线程池
    private final ScheduledExecutorService scheduler; // 用于定时任务
    private final long monitorInterval; // 监控间隔（毫秒）
    private final long taskTimeout; // 任务执行超时阈值（毫秒）
    private final int queueSizeThreshold; // 任务队列大小阈值
    private final Set<MonitoredThread> runningThreads = new CopyOnWriteArraySet<>(); // 记录运行中的线程

    // 构造函数
    public ThreadPoolMonitor(String prefix, ThreadPoolExecutor executor, long monitorInterval,
                             long taskTimeout, int queueSizeThreshold) {
        this.prefix = prefix;
        this.executor = executor;
        this.monitorInterval = monitorInterval;
        this.taskTimeout = taskTimeout;
        this.queueSizeThreshold = queueSizeThreshold;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        // 设置自定义线程工厂，以便监控线程
        executor.setThreadFactory(r -> {
            MonitoredThread thread = new MonitoredThread(r);
            runningThreads.add(thread);
            return thread;
        });
    }

    // 启动监控
    public static ThreadPoolMonitor startMonitoring(String prefix, ThreadPoolExecutor executor, int queueSizeThreshold) {
        // 每60秒监控一次，任务超时30秒
        ThreadPoolMonitor monitor = new ThreadPoolMonitor(prefix, executor, 60 * 1000, 30 * 1000, queueSizeThreshold);
        monitor.scheduler.scheduleAtFixedRate(monitor::monitor, 0, monitor.monitorInterval, TimeUnit.MILLISECONDS);
        return monitor;
    }

    // 停止监控
    public void stopMonitoring() {
        scheduler.shutdown();
    }

    // 监控方法
    private void monitor() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        int poolSize = executor.getPoolSize();
        int activeCount = executor.getActiveCount();
        int queueSize = executor.getQueue().size();
        long completedTaskCount = executor.getCompletedTaskCount();

        // 打印线程池基本状态
        System.out.println(String.format(
                "[%s] [线程池监控-%s] 当前线程数: %d, 活跃线程数: %d, 堆积队列数: %d, 已完成任务数: %d",
                timestamp, prefix, poolSize, activeCount, queueSize, completedTaskCount));

        // 检测任务堆积
        if (queueSize > queueSizeThreshold) {
            System.out.println(String.format(
                    "[%s] [线程池监控-%s] 队列数 (%d) 超过预计的队列阈值 (%d).",
                    timestamp, prefix, queueSize, queueSizeThreshold));
        }

        // 检测长时间运行的线程
        for (MonitoredThread thread : runningThreads) {
            if (thread.isAlive()) {
                long runningTime = System.currentTimeMillis() - thread.getStartTime();
                if (runningTime > taskTimeout) {
                    System.out.println(String.format(
                            "[%s] [线程池监控-%s] 线程 %s 运行超过 %d ms, 超出预计 (%d ms).",
                            timestamp, prefix, thread.getName(), runningTime, taskTimeout));
                }
            } else {
                runningThreads.remove(thread); // 清理已结束的线程
            }
        }
    }

    // 自定义线程类，用于记录开始时间
    private static final class MonitoredThread extends Thread {
        private final long startTime;

        public MonitoredThread(Runnable target) {
            super(target);
            this.startTime = System.currentTimeMillis();
        }

        public long getStartTime() {
            return startTime;
        }
    }

}

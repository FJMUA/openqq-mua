package cn.byteforge.openqq.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolManager {

    private static List<ThreadPoolMonitor> monitorList = new ArrayList<>();

    public static ExecutorService newFixedThreadPool(int poolSize, String prefix) {
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        addMonitor(prefix, service, 1);
        return service;
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String prefix) {
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        addMonitor(prefix, service, 1);
        return service;
    }

    protected static void addMonitor(String prefix, ExecutorService service, int queueSizeThreshold) {
        ThreadPoolMonitor monitor = ThreadPoolMonitor.startMonitoring(prefix, (ThreadPoolExecutor) service, queueSizeThreshold);
        monitorList.add(monitor);
    }

    public static void shutdown() {
        monitorList.forEach(ThreadPoolMonitor::stopMonitoring);
    }

}

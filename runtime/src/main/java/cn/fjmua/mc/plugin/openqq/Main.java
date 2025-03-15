package cn.fjmua.mc.plugin.openqq;

import cn.byteforge.openqq.QQHelper;
import cn.byteforge.openqq.http.APIEnum;
import cn.byteforge.openqq.http.OpenAPI;
import cn.byteforge.openqq.message.MessageBuilder;
import cn.byteforge.openqq.task.ThreadPoolManager;
import cn.byteforge.openqq.ws.BotContext;
import cn.byteforge.openqq.ws.QQConnection;
import cn.byteforge.openqq.ws.WebSocketAPI;
import cn.byteforge.openqq.ws.event.Event;
import cn.byteforge.openqq.ws.event.EventListener;
import cn.byteforge.openqq.ws.event.type.group.GroupAtMessageEvent;
import cn.byteforge.openqq.ws.event.type.guild.MessageCreateEvent;
import cn.byteforge.openqq.ws.handler.ChainHandler;
import cn.byteforge.openqq.ws.pojo.Intent;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class Main {

    public static void main(String[] args) throws Exception {
        String appId = getParam("-appId", args);
        String clientSecret = getParam("-secret", args);
        if (appId == null || clientSecret == null) {
            System.err.println("-appId or -secret argument are missing");
            return;
        }

        OpenAPI.addBeforeGetAuthResponseCheck(APIEnum.SEND_GROUP_MESSAGE, data -> {
            System.out.println("全局预检查数据函数 ... " + data);
            return true;
        });
        BotContext context = BotContext.create(appId, clientSecret, ThreadPoolManager.newFixedThreadPool(20, "上下文"));

        Supplier<ChainHandler> handlerSupplier = () -> {
            try {
                return ChainHandler.defaultChainGroup(null,
                        new EventListener<Event>() {
                            @Override
                            public void onEvent(Event e) {
                                if (e instanceof GroupAtMessageEvent event) {
                                    if (event.getD().getContent().contains("/绑定")) {
                                        event.reply("测试 绑定 成功响应");
                                    } else if (event.getD().getContent().contains("/我的信息")) {
                                        event.reply("测试 我的信息 成功响应");
                                    }
                                    // TODO AT_MESSAGE_CREATE
                                } else if (e instanceof MessageCreateEvent event) {
                                    if (event.getD().getContent().contains("/绑定")) {
                                        OpenAPI.sendChannelMessage(event.getD().getChannelId(), new MessageBuilder()
                                                .addText("测试 频道-绑定 成功响应")
                                                .setPassive(event.getD().getId())
                                                .build(), event.getContext().getCertificate());
                                    } else if (event.getD().getContent().contains("/我的信息")) {
                                        OpenAPI.sendChannelMessage(event.getD().getChannelId(), new MessageBuilder()
                                                .addText("测试 频道-我的信息 成功响应")
                                                .setPassive(event.getD().getId())
                                                .build(), event.getContext().getCertificate());
                                    }
                                }
                            }

                            @Override
                            public Intent eventIntent() {
                                return Intent.register().withAll().done();
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };

        QQConnection.connect(context, handlerSupplier,
                uuid -> WebSocketAPI.newStandaloneSession(Intent.register().withAll().done(), uuid, null, context),
                uuid -> {
                    // TODO embed
                    System.out.println("当前上下文: " + context);
                });
        // If you are running in main, you need to block the main thread
        System.in.read();
    }

    private static String getParam(String key, String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (key.equals(args[i])) {
                if (i + 1 < args.length) {
                    return args[i + 1];
                }
            }
        }
        return null;
    }

}
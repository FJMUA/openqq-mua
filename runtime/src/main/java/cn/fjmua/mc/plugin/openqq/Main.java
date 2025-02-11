package cn.fjmua.mc.plugin.openqq;

import cn.byteforge.openqq.http.APIEnum;
import cn.byteforge.openqq.http.OpenAPI;
import cn.byteforge.openqq.ws.BotContext;
import cn.byteforge.openqq.ws.QQConnection;
import cn.byteforge.openqq.ws.WebSocketAPI;
import cn.byteforge.openqq.ws.event.Event;
import cn.byteforge.openqq.ws.event.EventListener;
import cn.byteforge.openqq.ws.handler.ChainHandler;
import cn.byteforge.openqq.ws.pojo.Intent;

import java.util.concurrent.Executors;
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
        BotContext context = BotContext.create(appId, clientSecret, Executors.newFixedThreadPool(20));

        Supplier<ChainHandler> handlerSupplier = () -> {
            return ChainHandler.defaultChainGroup(null,
                    new EventListener<Event>() {
                        @Override
                        public void onEvent(Event event) {
                            System.out.println(event);
                        }

                        @Override
                        public Intent eventIntent() {
                            return Intent.register().withAll().done();
                        }
                    });
        };

        QQConnection.connect(context, handlerSupplier,
                uuid -> WebSocketAPI.newStandaloneSession(Intent.register().withAll().done(), uuid, null, context),
                uuid -> {
                    // TODO embed
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
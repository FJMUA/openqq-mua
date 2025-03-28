import cn.byteforge.openqq.http.APIEnum;
import cn.byteforge.openqq.http.OpenAPI;
import cn.byteforge.openqq.ws.BotContext;
import cn.byteforge.openqq.ws.QQConnection;
import cn.byteforge.openqq.ws.WebSocketAPI;
import cn.byteforge.openqq.ws.pojo.Intent;
import cn.byteforge.openqq.ws.event.EventListener;
import cn.byteforge.openqq.ws.event.type.group.GroupAtMessageEvent;
import cn.byteforge.openqq.ws.handler.ChainHandler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class TestMain {

    private static final String rowsJson = "[\n" +
            "    {\n" +
            "      \"buttons\": [\n" +
            "        {\n" +
            "          \"id\": \"1\",\n" +
            "          \"render_data\": {\n" +
            "            \"label\": \"⬅\uFE0F上一页\",\n" +
            "            \"visited_label\": \"⬅\uFE0F上一页\"\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"type\": 1,\n" +
            "            \"permission\": {\n" +
            "              \"type\": 1,\n" +
            "              \"specify_role_ids\": [\n" +
            "                \"1\",\n" +
            "                \"2\",\n" +
            "                \"3\"\n" +
            "              ]\n" +
            "            },\n" +
            "            \"click_limit\": 10,\n" +
            "            \"unsupport_tips\": \"兼容文本\",\n" +
            "            \"data\": \"data\",\n" +
            "            \"at_bot_show_channel_list\": true\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"2\",\n" +
            "          \"render_data\": {\n" +
            "            \"label\": \"➡\uFE0F下一页\",\n" +
            "            \"visited_label\": \"➡\uFE0F下一页\"\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"type\": 1,\n" +
            "            \"permission\": {\n" +
            "              \"type\": 1,\n" +
            "              \"specify_role_ids\": [\n" +
            "                \"1\",\n" +
            "                \"2\",\n" +
            "                \"3\"\n" +
            "              ]\n" +
            "            },\n" +
            "            \"click_limit\": 10,\n" +
            "            \"unsupport_tips\": \"兼容文本\",\n" +
            "            \"data\": \"data\",\n" +
            "            \"at_bot_show_channel_list\": true\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"buttons\": [\n" +
            "        {\n" +
            "          \"id\": \"3\",\n" +
            "          \"render_data\": {\n" +
            "            \"label\": \"\uD83D\uDCC5 打卡（5）\",\n" +
            "            \"visited_label\": \"\uD83D\uDCC5 打卡（5）\"\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"type\": 1,\n" +
            "            \"permission\": {\n" +
            "              \"type\": 1,\n" +
            "              \"specify_role_ids\": [\n" +
            "                \"1\",\n" +
            "                \"2\",\n" +
            "                \"3\"\n" +
            "              ]\n" +
            "            },\n" +
            "            \"click_limit\": 10,\n" +
            "            \"unsupport_tips\": \"兼容文本\",\n" +
            "            \"data\": \"data\",\n" +
            "            \"at_bot_show_channel_list\": true\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]";

    public static void main(String[] args) throws Exception {
        runTest();
    }

    private static void runTest() throws Exception {
        String appId = new String(Files.readAllBytes(Paths.get("openqq-for-java/secrets/appId.txt")));
        String clientSecret = new String(Files.readAllBytes(Paths.get("openqq-for-java/secrets/clientSecret.txt")));
        OpenAPI.addBeforeGetAuthResponseCheck(APIEnum.SEND_GROUP_MESSAGE, data -> {
            System.out.println("全局预检查数据函数 ... " + data);
            return true;
        });
        BotContext context = BotContext.create(appId, clientSecret, Executors.newFixedThreadPool(20));

        Supplier<ChainHandler> handlerSupplier = () -> {
            return ChainHandler.defaultChainGroup(null,
                    new EventListener<GroupAtMessageEvent>() {
                        @Override
                        public void onEvent(GroupAtMessageEvent event) {
                            if (event.getD().getContent().contains("session test")) {
                                event.reply("1");
                            }
                        }

                        @Override
                        public Intent eventIntent() {
                            return Intent.register().withCustom(1 << 25).done();
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

}

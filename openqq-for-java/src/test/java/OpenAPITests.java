import cn.byteforge.openqq.ws.BotContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OpenAPITests {

    private static final ExecutorService SERVICE = Executors.newFixedThreadPool(3);
    private static BotContext context;
    private static String wssUrl;

    @BeforeAll
    static void setup() throws Exception {
//        String appId = new String(Files.readAllBytes(Paths.get("../secrets/appId.txt")));
//        String clientSecret = new String(Files.readAllBytes(Paths.get("../secrets/clientSecret.txt")));
//        AccessToken token = OpenAPI.getAppAccessToken(appId, clientSecret);
//        Certificate certificate = new Certificate(appId, clientSecret, token);
//        context = BotContext.create();
//        RecommendShard shard = OpenAPI.getRecommendShardWssUrls(certificate);
//        wssUrl = shard.getUrl();
//        System.out.println(shard);
    }

    @Test
    void testStandalone() throws Exception {
    }

}

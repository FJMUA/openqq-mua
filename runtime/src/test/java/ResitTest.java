import cn.fjmua.mc.plugin.openqq.util.RedisUtil;
import org.junit.jupiter.api.Test;

public class ResitTest {

    @Test
    public void testAvailable() {
        RedisUtil.STRINGS.set("openqq-mua", "key");
        RedisUtil.expire("openqq-mua", 5);
        assert "key".equals(RedisUtil.STRINGS.get("openqq-mua"));
    }

    @Test
    public void testProject() {
        for (int i = 0; i < 2; i++) {
            RedisUtil.HASH.hset("openqq-mua:bind:uuid" + i, "memberOpenid", "QQXX");
        }
    }

}

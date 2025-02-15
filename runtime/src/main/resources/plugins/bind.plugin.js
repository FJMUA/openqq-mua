/**
 * 群与游戏绑定插件
 * @author IllTamer
 * */

// 声明插件基本信息（可省略）
const pluginInfo = {
    // 脚本名称：默认为文件名
    "name": "群成员绑定游戏脚本",
    // 脚本版本：默认 unknown
    "version": "1.0-SNAPSHOT",
    // 脚本作者：默认 unknown
    "author": "IllTamer",
    // 优先级：int 类型，升序排序，越小越先加载，默认 100
    "priority": 100,
    "invokedMapper": {
        "init": "onInit",
        "event": "onOpenQQEvent,onAsyncPlayerChatEvent",
        "destroy": ""
    }
};

// 导入 Redis 工具类
const RedisUtil = Packages.cn.fjmua.mc.plugin.openqq.util.RedisUtil;
const Bukkit = Packages.org.bukkit.Bukkit;

// 导入需要 instance of 判断的类
const OpenQQEvent = Packages.cn.fjmua.mc.plugin.openqq.api.OpenQQEvent;
const AsyncPlayerChatEvent = Packages.org.bukkit.event.player.AsyncPlayerChatEvent;

function onInit() {
    console.log(`${pluginInfo.name} onInit 方法调用`)
}

// 绑定：存5m过期时间 string
function onOpenQQEvent(event) {
    if (!event.getClass().isInstance(OpenQQEvent)) {
        console.log(`${event.getClass()} 触发 js.onGroupAtMessageEvent，忽略`)
        return;
    }
    // OpenQQEvent 获取原事件类型 进行筛选
    let originEvent = event.getOriginEvent();
    if (originEvent.getEventType() !== "GroupAtMessageEvent") {
        return;
    }

    let data = originEvent.getD()
    // 绑定 IllTamer
    let content = String(data.getContent()).trim();
    if (!content.startsWith("绑定")) {
        return;
    }
    let username = content.split("绑定")[1].trim();
    if (username.length === 0) {
        originEvent.reply("请发送 '绑定 玩家名称' 来绑定游戏账号\n如：绑定 PlayerName")
        return;
    }
    let memberOpenid = event.getD().getAuthor().getMemberOpenid();
    RedisUtil.STRINGS.set(username, memberOpenid);
    RedisUtil.expire(username, 5*60);
    // 确认绑定qqxx
    Bukkit.getPlayer(username).sendMessage(`请发送 确认绑定 ${memberOpenid} 与QQ账号进行绑定`);
    originEvent.reply(`已向玩家 ${username} 发送绑定申请，请在五分钟内确认`)
}

function onAsyncPlayerChatEvent(event) {
    if (!event.getClass().isInstance(AsyncPlayerChatEvent)) {
        console.log(`${event.getClass()} 触发 js.onAsyncPlayerChatEvent，忽略`)
        return;
    }
    let message = event.getMessage().trim()
    if (!message.startsWith("确认绑定")) {
        return;
    }
    let player = event.getPlayer();
    let memberOpenid = message.split("确认绑定")[1].trim();
    if (memberOpenid.length === 0) {
        player.sendMessage("请发送 '确认绑定 memberOpenid' 来绑定QQ账号\n如：确认绑定 abc")
        return;
    }

    let memberOpenidRedis = RedisUtil.STRINGS.get(event.getPlayer().getName())
    if (memberOpenidRedis !== memberOpenid) {
        player.sendMessage("绑定请求不存在或过期");
        return;
    }
    RedisUtil.HASH.hset(`openqq-mua:bind:${player.getUniqueId()}`, "memberOpenid", memberOpenidRedis);
    player.sendMessage("§a绑定成功");
}
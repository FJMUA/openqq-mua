/**
 * 脚本信息描述方法
 * 本方法包括其内返回的所有字段均可省略
 * */
function getPluginInfo() {
    return {
        // 脚本名称：默认为文件名
        "name": "测试脚本",
        // 脚本版本：默认 unknown
        "version": "1.0.0",
        // 脚本作者：默认 unknown
        "author": "IllTamer",
        // 优先级：int 类型，升序排序，越小越先加载，默认 100
        "priority": 100
    }
}

/**
 * 事件监听方法
 * 本方法包括其内返回的所有字段均可省略
 * */
function onEvent(event) {

}
package cn.byteforge.openqq.http;

import cn.byteforge.openqq.Global;
import cn.byteforge.openqq.exception.APIInvokeException;
import cn.byteforge.openqq.http.entity.*;
import cn.byteforge.openqq.message.Message;
import cn.byteforge.openqq.model.Certificate;
import cn.byteforge.openqq.util.Maps;
import cn.hutool.http.HttpGlobalConfig;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * QQ 机器人 服务端开放的 openapi 接口对接
 * */
// TODO 更改url储存位置
@Slf4j
public class OpenAPI {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<String, Function<Map<String, Object>, Boolean>> beforeGetAuthResponseCheckMap = new HashMap<>();

    /**
     * 发送群聊消息
     * @param channelId 频道的 openid
     * @param message 消息实例
     * @param cert 访问凭证
     * */
    public static MessageResponse sendChannelMessage(String channelId, Message message, Certificate cert) {
        return getAuthResponse(APIEnum.SEND_CHANNEL_MESSAGE.format(channelId), message.getData(), Method.POST, cert, MessageResponse.class);
    }

    /**
     * 发送群聊消息
     * @param groupId 群聊的 openid
     * @param message 消息实例
     * @param cert 访问凭证
     * */
    public static MessageResponse sendGroupMessage(String groupId, Message message, Certificate cert) {
        return getAuthResponse(APIEnum.SEND_GROUP_MESSAGE.format(groupId), message.getData(), Method.POST, cert, MessageResponse.class);
    }

    /**
     * 上传文件到用户
     * @param userId 用户的 openid
     * @param url 需要发送媒体资源的url
     * @param send 设置 true 会直接发送消息到目标端，且会占用主动消息频次
     * @param type 媒体类型：1 图片，2 视频，3 语音，4 文件（暂不开放）
     * 资源格式要求
     * 图片：png/jpg，视频：mp4，语音：silk
     * @param cert 访问凭证
     * */
    public static FileInfo uploadPrivateFile(String userId, String url, boolean send, UploadFileType type, Certificate cert) {
        return doUploadFile("users", userId, url, send, type, cert);
    }

    /**
     * 上传文件到群组
     * @param groupId 群聊的 openid
     * @param url 需要发送媒体资源的url
     * @param send 设置 true 会直接发送消息到目标端，且会占用主动消息频次
     * @param type 媒体类型：1 图片，2 视频，3 语音，4 文件（暂不开放）
     * 资源格式要求
     * 图片：png/jpg，视频：mp4，语音：silk
     * @param cert 访问凭证
     * */
    public static FileInfo uploadGroupFile(String groupId, String url, boolean send, UploadFileType type, Certificate cert) {
        return doUploadFile("groups", groupId, url, send, type, cert);
    }

    private static FileInfo doUploadFile(String api, String openId, String url, boolean send, UploadFileType type, Certificate cert) {
        return getAuthResponse(APIEnum.UPLOAD_FILE.format(api, openId), Maps.of(
                "file_type", type.getValue(),
                "url", url,
                "srv_send_msg", send
        ), Method.POST, cert, FileInfo.class);
    }

    /**
     * 回调 QQ 后台，告知交互事件已经收到
     * @param interactionId 事件 data.id
     * @param result 交互结果
     * @param cert 访问凭证
     * */
    public static void callbackInteraction(String interactionId, InteractResult result, Certificate cert) {
        getAuthResponse(APIEnum.INTERACTION_CALLBACK.format(interactionId), Maps.of(
                "code", result.getCode()
        ), Method.PUT, cert, JsonObject.class);
    }

    /**
     * 获取调用凭证
     * @param appId 在开放平台管理端上获得。
     * @param clientSecret 在开放平台管理端上获得。
     * */
    public static AccessToken getAppAccessToken(String appId, String clientSecret) {
        return getResponse(APIEnum.GET_APP_ACCESS_TOKEN.getUrl(), Maps.of(
                "appId", appId,
                "clientSecret", clientSecret
        ), Method.POST, AccessToken.class, null);
    }

    /**
     * 获取通用 WSS 接入点
     * @param cert 访问凭证
     * */
    public static String getUniversalWssUrl(Certificate cert) {
        return getAuthResponse(APIEnum.GET_WSS_URL.format(), null, Method.GET, cert, JsonObject.class)
                .get("url").getAsString();
    }

    /**
     * 获取带推荐分片数的 WSS 接入点
     * @param cert 访问凭证
     * */
    public static RecommendShard getRecommendShardWssUrls(Certificate cert) {
        return getAuthResponse(APIEnum.GET_SHARD_WSS_URL.format(), null, Method.GET, cert, RecommendShard.class);
    }

    /**
     * 添加全局发送数据前预检查方法
     * @param apiEnum 准备检查的 API 枚举
     * @param checkFunc 检查方法, <负载数据, 是否通过>
     * */
    public static void addBeforeGetAuthResponseCheck(APIEnum apiEnum, Function<Map<String, Object>, Boolean> checkFunc) {
        beforeGetAuthResponseCheckMap.put(apiEnum.getUrl(), checkFunc);
    }

    /**
     * @return 响应超时或检查未通过时返回为空
     * */
    @Nullable
    private static <T> T getAuthResponse(APIEnum.Url url, @Nullable Map<String, Object> data, Method method, Certificate cert, Class<T> clazz) {
        Function<Map<String, Object>, Boolean> checkFunc = beforeGetAuthResponseCheckMap.get(url.getUrl());
        if (checkFunc != null && !checkFunc.apply(data)) return null;
        return getResponse(url.toString(), data, method, clazz, Maps.of(
                "Authorization", String.format(Global.Authorization, cert.getAccessToken().getContent()),
                "X-Union-Appid", cert.getAppId()
        ));
    }

    /**
     * <a href="https://bot.q.qq.com/wiki/develop/api-v2/dev-prepare/error-trace/openapi.html">错误码介绍</a>
     * */
    private static <T> T getResponse(String url, Map<String, Object> data, Method method, Class<T> clazz, Map<String, String> headers) {
        String body = GSON.toJson(data);
        log.debug("Send json object to url: {}, json: {}", url, body);
        try (HttpResponse response = HttpRequest.of(url)
                .method(method)
                .timeout(HttpGlobalConfig.getTimeout())
                .body(body)
                .headerMap(headers, true)
                .execute())
        {
            Status.Http httpStatus = Status.Http.parse(response.getStatus());
            if (!httpStatus.isSuccess() && response.bodyBytes().length == 0) {
                throw new APIInvokeException(httpStatus.getCode(), httpStatus.getMessage(), body);
            }
            JsonObject object = GSON.fromJson(response.body(), JsonObject.class);
            if (object == null) return null;
            log.debug("Received json object from url: {}, json: {}", url, object);
            if (object.has("code")) {
                throw new APIInvokeException(object.get("code").getAsInt(), object.get("message").getAsString(), body);
            }
            return GSON.fromJson(object, clazz);
        }
    }

}
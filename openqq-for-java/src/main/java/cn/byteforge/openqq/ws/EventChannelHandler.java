package cn.byteforge.openqq.ws;

import cn.byteforge.openqq.exception.ErrorCheckException;
import cn.byteforge.openqq.ws.entity.enumerate.OpCode;
import cn.byteforge.openqq.ws.event.Event;
import cn.byteforge.openqq.ws.handler.AutoReconnectHandler;
import cn.byteforge.openqq.ws.handler.ChainHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Slf4j
class EventChannelHandler extends SimpleChannelInboundHandler<Object> {

    private final ChainHandler chainHandler;
    private final EventLoopGroup group;

    @Setter
    private WebSocketClientHandshaker handshaker;

    @Getter
    private ChannelPromise handshakeFuture;

    public EventChannelHandler(ChainHandler chainHandler, EventLoopGroup group) {
        this.chainHandler = chainHandler;
        this.group = group;
    }

    // 使用 wss 连接时，应该在 added 时就进行挥手（ws 连接时可以在 active 时挥手）
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (!this.handshaker.isHandshakeComplete()){
            FullHttpResponse response = (FullHttpResponse) msg;
            this.handshaker.finishHandshake(ctx.channel(), response);
            this.handshakeFuture.setSuccess();
            return;
        }

        if (msg instanceof TextWebSocketFrame) {
            JsonObject json = new Gson().fromJson(((TextWebSocketFrame) msg).text(), JsonObject.class);
            log.debug("Received json message: {}", json);
            try {
                chainHandler.handle(json);
            } catch (ErrorCheckException e) {
                log.error("Error check exception: ", e);
            } catch (Exception e) {
                log.error("Handler exception: ", e);
            }
        } else if (msg instanceof CloseWebSocketFrame){
            log.info("WebSocket client closed with signal");
            ctx.channel().close();
        }
    }

    // op7 时重连方法会关闭channel，重复触发本方法
//    /**
//     * 自动重连
//     * 有很多原因可能会导致 websocket 长连接断开，断开之后短时间内重连会补发中间遗漏的事件，以保障业务逻辑的正确性。
//     * 断开重连 gateway 后不需要发送重新登录 Opcode 2 Identify请求。在连接到 Gateway 之后，需要发送 Opcode 6 Resume消息
//     * @apiNote 延迟 3 秒后自动重连
//     * */
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) {
//        log.info("Channel inactive, sending auto reconnect event ...");
//        group.schedule(() -> {
//            try {
//                // TODO invoke WebSocketAPI#resumeSession
//                Event event = new Event();
//                event.setOpcode(OpCode.RECONNECT);
//                chainHandler.find(AutoReconnectHandler.class).handle(event);
//            } catch (Exception e) {
//                log.error("Error reconnecting to websocket", e);
//            }
//        }, 3, TimeUnit.SECONDS);
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("WebSocket connection closed with exception: ", cause);
        ctx.close();
    }

}

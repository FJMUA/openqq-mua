package cn.byteforge.openqq.ws.event.type.guild;

import cn.byteforge.openqq.ws.entity.guild.data.MessageCreateData;
import cn.byteforge.openqq.ws.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 频道消息创建事件
 * */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class MessageCreateEvent extends Event {

    /**
     * 事件字段数据
     * */
    private MessageCreateData d;

}

package cn.byteforge.openqq.ws.event.type.guild;

import cn.byteforge.openqq.ws.entity.guild.data.GuildMessageData;
import cn.byteforge.openqq.ws.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 频道@机器人
 * */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class AtMessageCreateEvent extends Event {

    /**
     * 事件字段数据
     * */
    private GuildMessageData d;

}

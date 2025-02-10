package cn.byteforge.openqq.ws.entity.guild.data;

import cn.byteforge.openqq.ws.entity.guild.Author;
import cn.byteforge.openqq.ws.entity.guild.Member;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@ToString(callSuper = true)
@Data
public class MessageCreateData {

    private Author author;

    @SerializedName("channel_id")
    private String channelId;

    private String content;

    @SerializedName("guild_id")
    private String guildId;

    private String id;

    private Member member;

    /**
     * 提及的频道成员
     * */
    private List<Author> mentions;

    private Integer seq;

    @SerializedName("seq_in_channel")
    private String seqInChannel;

    private Date timestamp;

}

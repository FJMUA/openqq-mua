package cn.byteforge.openqq.ws.entity.guild;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 频道成员
 * */
@Data
public class Member {

    /**
     * 加入事件
     * @apiNote 2024-08-08T14:13:27+08:00
     * */
    @SerializedName("joined_at")
    private Date joinedAt;

    private String nick;

    private List<String> roles;

}

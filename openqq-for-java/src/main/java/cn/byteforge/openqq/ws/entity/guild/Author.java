package cn.byteforge.openqq.ws.entity.guild;

import lombok.Data;

/**
 * 频道消息发送者
 * */
@Data
public class Author {

    /**
     * 头像 url
     * <a href="https://qqchannel-profile-1251316161.file.myqcloud.com/16539798000b98f6f6d15928ec?t=1653979800">...</a>
     * */
    private String avatar;

    /**
     * 是否是机器人用户
     * */
    private Boolean bot;

    private String id;

    /**
     * OpenID
     * */
    private String unionOpenid;

    private String username;

}

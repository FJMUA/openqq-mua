package cn.fjmua.mc.plugin.openqq.exception;

import lombok.Getter;

@Getter
public class ScriptMemberNotFoundException extends RuntimeException {

    private final String memberName;

    public ScriptMemberNotFoundException(String memberName) {
        super(memberName);
        this.memberName = memberName;
    }

}

package com.jd.platform.gobrs.async.enums;

/**
 * @program: gobrs-async
 * @ClassName ParseEnum
 * @description:
 * @author: sizegang
 * @create: 2022-01-27 15:28
 * @Version 1.0
 **/
public enum ParseEnum {

    TREE("TREE", "树形结构"),
    FLAT("FLAT", "扁平"),

    ;
    ParseEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private String type;

    private String desc;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

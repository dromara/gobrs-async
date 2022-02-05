package com.jd.gobrs.async.enums;

/**
 *
 * @author sizegang1
 * @date 2022-01-27 22:05 2022-01-27 22:05
 */

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

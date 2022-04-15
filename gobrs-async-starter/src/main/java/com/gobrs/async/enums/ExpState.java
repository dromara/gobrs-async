package com.gobrs.async.enums;

/**
 * @program: gobrs-async-core
 * @ClassName ExpState
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/
public enum ExpState {

    DEFAULT(100, " default interrupt");

    private Integer code;

    private String desc;

    ExpState(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

package com.gobrs.async.core.common.enums;

/**
 * The enum Exp state.
 *
 * @program: gobrs -async-core
 * @ClassName ExpState
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
public enum ExpState {

    /**
     * The Default.
     */

    SUCCESS(100, "Success Default"),
    ERROR(200, "When there are abnormal tasks in the process"),
    STOP_ASYNC(300, "Call stopAsync manually"),
    TASK_INTERRUPT(400, "Automatic interrupt configuration taskInterrupt");


    private Integer code;

    private String desc;

    ExpState(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Sets code.
     *
     * @param code the code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}

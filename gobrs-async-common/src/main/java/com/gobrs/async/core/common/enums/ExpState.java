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

    SUCCESS(100, "成功 默认"),
    ERROR(200, "流程中存在异常任务时"),
    STOP_ASYNC(300, "手动调用 stopAsync"),
    TASK_INTERRUPT(400, "自动中断 配置 taskInterrupt");


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

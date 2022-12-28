package com.gobrs.async.core.common.enums;

/**
 * The enum Interrupt enum.
 *
 * @program: gobrs -async
 * @ClassName InterruptEnum
 * @description:
 * @author: sizegang
 * @create: 2022 -12-19
 */
public enum InterruptEnum {
    /**
     * Init interrupt enum.
     */
    INIT(0, "初始化"),
    /**
     * Interruptting interrupt enum.
     */
    INTERRUPTTING(1, "中断中"),
    /**
     * Interrupted interrupt enum.
     */
    INTERRUPTED(2, "已中断");
    ;


    /**
     * Gets state.
     *
     * @return the state
     */
    public Integer getState() {
        return state;
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(Integer state) {
        this.state = state;
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

    private Integer state;

    private String desc;


    InterruptEnum(Integer state, String desc) {
        this.state = state;
        this.desc = desc;
    }
}

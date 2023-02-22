package com.gobrs.async.core.common.enums;

/**
 * The enum Task enum.
 *
 * @program: gobrs -async
 * @ClassName TaskEnum
 * @description:
 * @author: sizegang
 * @create: 2023 -01-03
 */
public enum TaskEnum {
    /**
     * Class task enum.
     */
    CLASS(1, "类任务"),
    /**
     * Method task enum.
     */
    METHOD(2, "方法任务"),

    /**
     * Dynamic task enum.
     */
    DYNAMIC(3, "动态任务");

    ;

    private Integer type;

    private String desc;

    TaskEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(Integer type) {
        this.type = type;
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

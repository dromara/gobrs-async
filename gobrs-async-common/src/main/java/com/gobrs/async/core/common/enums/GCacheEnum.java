package com.gobrs.async.core.common.enums;

/**
 * The enum G cache enum.
 *
 * @program: gobrs -async
 * @ClassName GCacheEnum
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
public enum GCacheEnum {
    /**
     * Method task g cache enum.
     */
    METHOD_TASK(1, "方法任务缓存");;

    private Integer type;

    private String desc;


    GCacheEnum(Integer type, String desc) {
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

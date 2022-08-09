package com.gobrs.async.rule;

import java.io.Serializable;

/**
 * The type Rule.
 *
 * @author sizegang1
 * @program: gobrs -async
 * @ClassName Rule
 * @description:
 * @author: sizegang
 * @create: 2022 -01-26 01:39
 * @Version 1.0
 * @date 2022 -01-27
 */
public class Rule implements Serializable {

    private String name;

    private String content;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }
}

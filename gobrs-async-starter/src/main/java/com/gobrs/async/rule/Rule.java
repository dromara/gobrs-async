package com.gobrs.async.rule;

import java.io.Serializable;

/**
 * @program: gobrs-async
 * @ClassName Rule
 * @description:
 * @author: sizegang
 * @create: 2022-01-26 01:39
 * @Version 1.0
 * @date 2022-01-27
 * @author sizegang1
 **/

public class Rule implements Serializable {

    private String name;

    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

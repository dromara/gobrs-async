package com.gobrs.async.core.common.util;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The type Params tool.
 *
 * @program: gobrs -async
 * @ClassName ParamsTool
 * @description:
 * @author: sizegang
 * @create: 2023 -01-07
 */
public class ParamsTool {
    /**
     * New array list array list.
     *
     * @param <T>      the type parameter
     * @param elements the elements
     * @return the array list
     */
    @SafeVarargs
    public static <T> ArrayList<T> asParams(T... elements) {
        if (elements == null) {
            return null;
        } else {
            ArrayList<T> list = newArrayList();
            Collections.addAll(list, elements);
            return list;
        }
    }

    /**
     * New array list array list.
     *
     * @param <T> the type parameter
     * @return the array list
     */
    private static <T> ArrayList<T> newArrayList() {
        return new ArrayList();
    }
}

package com.jd.gobrs.async.util;

/**
 * @program: gobrs-async
 * @ClassName CommonUtils
 * @description:
 * @author: sizegang
 * @create: 2022-02-26 14:51
 * @Version 1.0
 **/
public class CommonUtils {

    public static String depKey(Class clazz) {
        char[] cs = clazz.getSimpleName().toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);
    }
}

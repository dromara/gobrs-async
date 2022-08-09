package com.gobrs.async.report;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Reporter.
 *
 * @program: gobrs -async-core
 * @ClassName Reporter
 * @description:
 * @author: sizegang
 * @create: 2022 -04-17
 */
public class Reporter {
    private Map<String, Entry> report = new ConcurrentHashMap<String, Entry>();

}

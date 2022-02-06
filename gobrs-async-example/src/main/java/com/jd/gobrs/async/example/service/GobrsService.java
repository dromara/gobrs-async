package com.jd.gobrs.async.example.service;

import com.jd.gobrs.async.gobrs.GobrsTaskFlow;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @program: gobrs-async-example
 * @ClassName GobrsService
 * @description:
 * @author: sizegang
 * @create: 2022-01-29 21:00
 * @Version 1.0
 **/
@Service
public class GobrsService {

    @Resource
    private GobrsTaskFlow taskFlow;

    public void testGobrs() {
        try {
            taskFlow.taskFlow("test", () -> {
                Map<String, Object> params = new HashMap<>();
                params.put("AService", "AService param");
                return params;
            }, 100001);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

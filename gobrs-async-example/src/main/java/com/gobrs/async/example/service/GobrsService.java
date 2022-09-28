package com.gobrs.async.example.service;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.engine.RuleThermalLoad;
import com.gobrs.async.example.task.*;
import com.gobrs.async.rule.Rule;
import com.gobrs.async.task.AsyncTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * The type Gobrs service.
 *
 * @program: gobrs -async-core
 * @ClassName GobrsService
 * @description:
 * @author: sizegang
 * @create: 2022 -03-28
 */
@Service
public class GobrsService {



    @Autowired(required = false)
    private GobrsAsync gobrsAsync;




    /**
     * Gobrs async.
     */
    public void gobrsAsync() {
        gobrsAsync.go("test", () -> new Object());
    }


    /**
     * Future.
     */


    /**
     * Update rule.
     *
     * @param rule the rule
     */

}

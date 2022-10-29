package com.gobrs.async.core.example.service;

import com.gobrs.async.core.GobrsAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * Update com.gobrs.async.rule.
     *
     * @param com.gobrs.async.rule the com.gobrs.async.rule
     */

}

package com.gobrs.async.test.param;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.common.domain.GobrsParamSupport;
import com.gobrs.async.test.task.sence.casefive.CaseFiveTaskA;
import lombok.Builder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 * @program: gobrs-async
 * @ClassName CaseParam
 * @description:
 * @author: sizegang
 * @create: 2023-02-22
 **/
public class CaseParam {

    /**
     * A -> B,C,D
     */

    @Autowired
    private GobrsAsync gobrsAsync;


    @Builder
    public static class DataContext {
        private String userName;

        private String password;

        private String address;
    }


    @Test
    public void caseParams1() {
        DataContext dataContext = DataContext.builder().userName("sizegang").password("1234").address("Beijing").build();
        gobrsAsync.go("param", () -> dataContext);
    }

    @Test
    public void caseParams2() {
        HashMap<Class<?>, Object> params = new HashMap<>();
        params.put(CaskParamTask.class, "CaskParamTask参数");
        gobrsAsync.go("param", () -> params);
    }

    @Test
    public void caseParams3() {
        /**
         *  key 是 class类型
         */
        GobrsParamSupport paramSupport = GobrsParamSupport.create().putNext(CaseFiveTaskA.class, "CaseFiveTaskA的参数");
        gobrsAsync.go("param", () -> paramSupport);
    }

}

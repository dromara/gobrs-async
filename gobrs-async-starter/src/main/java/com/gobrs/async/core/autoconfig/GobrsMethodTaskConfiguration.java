package com.gobrs.async.core.autoconfig;

import com.gobrs.async.core.cache.GCacheManager;
import com.gobrs.async.core.cache.MethodTaskCache;
import com.gobrs.async.core.property.GobrsAsyncProperties;
import com.gobrs.async.core.scan.MethodComponentScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Gobrs method task configuration.
 *
 * @program: gobrs -async
 * @ClassName GobrsMethodTaskConfiguration
 * @description:
 * @author: sizegang
 * @create: 2023 -01-03
 */
public class GobrsMethodTaskConfiguration {


    /**
     * Method task cache method task cache.
     *
     * @return the method task cache
     */
    @Bean
    public MethodTaskCache methodTaskCache() {
        return new MethodTaskCache();
    }

    /**
     * Method component scanner method component scanner.
     *
     * @param gCacheManager        the g cache manager
     * @param gobrsAsyncProperties the gobrs async properties
     * @return the method component scanner
     */
    @Bean
    public MethodComponentScanner methodComponentScanner(GCacheManager gCacheManager, GobrsAsyncProperties gobrsAsyncProperties) {
        return new MethodComponentScanner(gCacheManager, gobrsAsyncProperties);
    }


    /**
     * Method task factory bean method task factory bean.
     *
     * @return the method task factory bean
     */
    @Bean
    public MethodComponentScanner.MethodTaskFactoryBean methodTaskFactoryBean() {
        return new MethodComponentScanner.MethodTaskFactoryBean();
    }

}

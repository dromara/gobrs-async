package com.gobrs.async.core.config;

/**
 * @program: gobrs-async
 * @ClassName LogConfig
 * @description:
 * @author: sizegang
 * @create: 2022-10-30
 **/

import com.gobrs.async.core.common.def.DefaultConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static com.gobrs.async.core.common.constant.ConfigPropertiesConstant.RULES;

/**
 * The type Log config.
 */
@ConfigurationProperties(prefix = RULES)
@Component
public class LogConfig {
    /**
     * 执行异常trace log打印
     */
    private Boolean errLogabled = DefaultConfig.ERR_LOGABLED;
    /**
     * 任务执行过程中耗时打印
     */
    private Boolean costLogabled = DefaultConfig.COST_LOGABLED;

    /**
     * Gets err logabled.
     *
     * @return the err logabled
     */
    public Boolean getErrLogabled() {
        return errLogabled;
    }

    /**
     * Sets err logabled.
     *
     * @param errLogabled the err logabled
     */
    public void setErrLogabled(Boolean errLogabled) {
        this.errLogabled = errLogabled;
    }

    /**
     * Gets cost logabled.
     *
     * @return the cost logabled
     */
    public Boolean getCostLogabled() {
        return costLogabled;
    }

    /**
     * Sets cost logabled.
     *
     * @param costLogabled the cost logabled
     */
    public void setCostLogabled(Boolean costLogabled) {
        this.costLogabled = costLogabled;
    }
}

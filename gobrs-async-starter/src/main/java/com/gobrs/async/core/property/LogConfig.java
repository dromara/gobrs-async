package com.gobrs.async.core.property;

/**
 * @program: gobrs-async
 * @ClassName LogConfig
 * @description:
 * @author: sizegang
 * @create: 2022-10-30
 **/

import com.gobrs.async.core.common.def.DefaultConfig;
import lombok.Data;

/**
 * The type Log config.
 */
@Data
public class LogConfig {
    /**
     * 执行异常trace log打印
     */
    private Boolean errLogabled = DefaultConfig.ERR_LOGABLED;
    /**
     * 任务执行过程中耗时打印
     */
    private Boolean costLogabled = DefaultConfig.COST_LOGABLED;
}

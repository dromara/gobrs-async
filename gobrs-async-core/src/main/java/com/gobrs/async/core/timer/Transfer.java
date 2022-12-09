package com.gobrs.async.core.timer;

import lombok.Builder;
import lombok.Data;

import java.lang.ref.Reference;

/**
 * @program: gobrs-async
 * @ClassName Transfer
 * @description:
 * @author: sizegang
 * @create: 2022-12-09
 **/
@Builder
@Data
public class Transfer {
    private Reference<GobrsTimer.TimerListener> timerListenerReference;
}

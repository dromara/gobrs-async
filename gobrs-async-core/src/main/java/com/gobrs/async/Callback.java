package com.gobrs.async;

import com.gobrs.async.domain.AsyncParam;

/**
 * @program: gobrs-async-starter
 * @ClassName Callback
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/

public interface Callback<Event> {
    public void onSuccess(AsyncParam param);

    public void onError(AsyncParam param, Throwable throwable);

}

package com.gobrs.async;

import com.gobrs.async.callback.ErrorCallback;
import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.task.AsyncTask;

/**
 * @program: gobrs-async-starter
 * @ClassName Callback
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/

public interface Callback {
    void onSuccess(AsyncParam param);

    void onError(ErrorCallback errorCallback);

}

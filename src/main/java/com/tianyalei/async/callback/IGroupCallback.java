package com.tianyalei.async.callback;

import java.util.List;

/**
 * @author wuweifeng wrote on 2019-11-19.
 */
public interface IGroupCallback {
    void success(List<?> result);

    void failure(Exception e);
}

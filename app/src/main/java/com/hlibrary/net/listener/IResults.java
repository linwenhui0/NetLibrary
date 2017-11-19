package com.hlibrary.net.listener;

import java.util.List;

/**
 * Created by linwenhui on 2017/2/28.
 */

public interface IResults<T> extends IResultError {
    void onSuccee(List<T> t);
}

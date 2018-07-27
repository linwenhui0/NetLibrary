package com.hlibrary.net.callback;

import java.util.List;

public interface IMulResultCallback<T> extends IResultErrorCallback {

    void onSuccee(List<T> t);

}

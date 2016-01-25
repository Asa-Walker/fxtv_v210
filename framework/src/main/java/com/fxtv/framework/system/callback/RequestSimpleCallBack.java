package com.fxtv.framework.system.callback;

import com.fxtv.framework.model.Response;

/**
 * Created by wzh on 2015/12/31.
 * 可选择性重写的RequestCallBack
 */
public abstract class RequestSimpleCallBack<T> extends RequestCallBack<T> {

    @Override
    public void onFailure(Response failureResp) {

    }

    @Override
    public void onComplete() {

    }
}



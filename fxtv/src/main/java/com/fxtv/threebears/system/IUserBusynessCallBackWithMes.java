package com.fxtv.threebears.system;

import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.util.Utils;

/**
 * Created by Administrator on 2016/1/19.
 */
public abstract class IUserBusynessCallBackWithMes extends RequestCallBack<String> {

    @Override
    public void onSuccess(String json, Response resp) {

        onResult(true, json, resp.msg);
    }

    @Override
    public void onFailure(Response failureResp) {
        onResult(false, null, failureResp.msg);
    }

    @Override
    public void onComplete() {
        Utils.dismissProgressDialog();
    }

    public abstract void onResult(boolean result, String arg, String mesaage);
}

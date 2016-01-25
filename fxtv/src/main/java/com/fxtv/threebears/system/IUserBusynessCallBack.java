package com.fxtv.threebears.system;

import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.util.Utils;

public abstract class IUserBusynessCallBack extends RequestCallBack<String> {
        public boolean isMsg;
        @Override
        public void onSuccess(String json,Response resp) {
            if(!isMsg)
                onResult(true, json);
            else
                onResult(true, resp.msg);

        }
        @Override
        public void onFailure(Response failureResp) {
            onResult(false, failureResp.msg);
        }

        @Override
        public void onComplete() {
            Utils.dismissProgressDialog();
        }
        public abstract void onResult(boolean result, String arg);
    }
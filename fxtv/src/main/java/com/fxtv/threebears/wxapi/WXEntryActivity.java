package com.fxtv.threebears.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.fxtv.framework.Logger;
import com.fxtv.framework.system.components.ShareWeChatComponent;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI mApi;
    private String TAG = "WXEntryActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.fxtv.threebears.R.layout.wx_layout);
        mApi = WXAPIFactory.createWXAPI(this, ShareWeChatComponent.WETCHAT_APPID, false);
        mApi.registerApp(ShareWeChatComponent.WETCHAT_APPID);
        mApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mApi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Intent intent = new Intent();
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Logger.d(TAG, "ERR_USER_OK");
                try{
                    String code = ((SendAuth.Resp) baseResp).code;
                    if (code!=null){
                        intent.putExtra("code",code);
                    }
                }catch(Exception e){
                }
                intent.setAction(ShareWeChatComponent.SHARE_OK);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Logger.d(TAG, "ERR_USER_CANCEL");
                intent.setAction(ShareWeChatComponent.SHARE_CANCLE);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Logger.d(TAG, "ERR_AUTH_DENIED:" + baseResp.errStr);
                intent.setAction(ShareWeChatComponent.SHARE_FARIL);
                break;
            default:
                Logger.d(TAG, "ERR_AUTH_DENIED");
                intent.setAction(ShareWeChatComponent.SHARE_FARIL);
                break;
        }
        sendBroadcast(intent);
        this.finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mApi != null) {
            mApi = null;
        }
    }


}
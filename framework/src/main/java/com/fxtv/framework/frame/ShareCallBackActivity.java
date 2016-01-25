package com.fxtv.framework.frame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.fxtv.framework.Logger;
import com.fxtv.framework.R;
import com.fxtv.framework.system.components.ShareSinaComponent;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

/**
 *The following unrelated icon files have identical contents: banner_logo.png, logo.png
 * 统一处理的新浪回调页
 * Created by Administrator on 2015/12/22.
 */
public class ShareCallBackActivity extends Activity implements IWeiboHandler.Response {
    private IWeiboShareAPI mWeiboShareAPI;
    private  final  static  String TAG="ShareCallBackActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_layout);
        mWeiboShareAPI= WeiboShareSDK.createWeiboAPI(this, ShareSinaComponent.SINA_APP_KEY);
        mWeiboShareAPI.registerApp();
        mWeiboShareAPI.handleWeiboResponse(getIntent(),this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        Intent intent=new Intent();
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Logger.d(TAG,"SINA SHARE OK");
                intent.setAction(ShareSinaComponent.ACTION_OK);
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Logger.d(TAG,"SINA SHARE ERROR："+baseResponse.errMsg);
                intent.setAction(ShareSinaComponent.ACTION_FAILE);
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Logger.d(TAG,"SINA SHARE CANCEL：");
                intent.setAction(ShareSinaComponent.ACTION_CANCLE);
                break;
            default:
                intent.setAction(ShareSinaComponent.ACTION_FAILE);
                break;
        }
        sendBroadcast(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWeiboShareAPI!=null){
            mWeiboShareAPI=null;
        }
    }
}

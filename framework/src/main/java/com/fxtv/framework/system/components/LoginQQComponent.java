package com.fxtv.framework.system.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.fxtv.framework.Logger;
import com.fxtv.framework.system.SystemThirdPartyLogin;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/12/18.
 * QQ登录组件
 */
public class LoginQQComponent extends BaseLoginComponent{
    public static final String QQAPPID = "1104731238";
    private   static final  String TAG="LoginQQComponent";
    private  Context mContext;
    private Tencent mTencent;
    private QQLoginListener qqLoginListener;
    private SystemThirdPartyLogin.ICallBackSystemLogin mCallBack;
    private UserInfo mInfo;
    public LoginQQComponent(Context context,SystemThirdPartyLogin.ICallBackSystemLogin callBack){
        mContext=context;
        mTencent=Tencent.createInstance(ShareQQComponent.QQAPPID, context);
        qqLoginListener=new QQLoginListener();
        mCallBack=callBack;
    }

    /**
     * 登录接口方法
     */
    @Override
    public void Login() {
        super.Login();
        mTencent.login((Activity) mContext, "all", qqLoginListener);
    }

    /**
     * 登录的回调接口实现类
     */
    class QQLoginListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            if (response == null) {
                Logger.d(TAG, "QQLogin Fail");
            } else {
                JSONObject jsonResponse = (JSONObject) response;
                Logger.d(TAG, "QQLogin Success" + jsonResponse);
                initOpenidAndToken(jsonResponse);
            }
        }

        @Override
        public void onError(UiError uiError) {
            Logger.d(TAG, "QQLogin Error" + uiError.errorMessage);
            mCallBack.onFailure(uiError.errorMessage);
        }

        @Override
        public void onCancel() {
            Logger.d(TAG, "QQLogin Cancle");
            mCallBack.onCancle();
        }
    }

    /**
     * Activity中实现此方法接收回调内容
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            super.onActivityResult(requestCode, resultCode, intent);
            if (requestCode == Constants.REQUEST_LOGIN ||requestCode == Constants.REQUEST_APPBAR) {
                Tencent.onActivityResultData(requestCode,resultCode,intent,qqLoginListener);
            }
    }

    public  void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (mCallBack!=null){
                Logger.d(TAG,"openId"+openId);
                mCallBack.onSuccess(openId);
            }
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }
    @Override
    public void destory() {
        super.destory();
        if (mTencent!=null){
            mTencent.releaseResource();
            mTencent=null;
        }
        if (mContext!=null){
            mContext=null;
        }
        if (mInfo!=null){
            mInfo=null;
        }
    }
}

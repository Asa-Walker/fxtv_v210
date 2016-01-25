package com.fxtv.framework.system.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fxtv.framework.Logger;
import com.fxtv.framework.system.SystemThirdPartyLogin;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * Created by Administrator on 2015/12/18.
 * 新浪登录组件
 */
public class LoginSinaComponent extends BaseLoginComponent{
    public  static  final String SINA_APP_KEY="274394947";
    public  static  final  String SINA_REDIRECT_URL="http://www.sharesdk.cn";
    private  Context mContext;
    private AuthInfo mAuthInfo;
    private AuthListener  mLoginListener;
    private SsoHandler mSsoHandler;
    private SystemThirdPartyLogin.ICallBackSystemLogin mCallBack;
    private final static String TAG="LoginSinaComponentt";
    public LoginSinaComponent(Context context,SystemThirdPartyLogin.ICallBackSystemLogin callBack){
        mContext=context;
        mAuthInfo=new AuthInfo(context,SINA_APP_KEY,SINA_REDIRECT_URL,null);
        mLoginListener=new AuthListener();
        mCallBack=callBack;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, intent);
        }
    }
    @Override
    public void Login() {
        super.Login();
        if (null == mSsoHandler && mAuthInfo != null) {
            mSsoHandler = new SsoHandler((Activity)mContext, mAuthInfo);
        }
        if (mSsoHandler != null) {
            mSsoHandler.authorize(mLoginListener);
        } else {
            LogUtil.e(TAG, "Please setWeiboAuthInfo(...) for first");
        }

    }

    @Override
    public void destory() {
        super.destory();
        if (mContext!=null){
            mContext=null;
        }
        if (mAuthInfo!=null){
            mAuthInfo=null;
        }
        if (mSsoHandler!=null){
            mSsoHandler=null;
        }
        if (mLoginListener!=null){
            mLoginListener=null;
        }
        if (mCallBack!=null){
            mCallBack=null;
        }
    }
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            Logger.d(TAG, accessToken.getUid());
            if (mCallBack!=null){
                mCallBack.onSuccess(accessToken.getUid());
            }
        }
        @Override
        public void onWeiboException(WeiboException e) {
            mCallBack.onFailure(e.getMessage());
        }

        @Override
        public void onCancel() {
            mCallBack.onCancle();
        }
    }
}

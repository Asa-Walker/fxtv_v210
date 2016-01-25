package com.fxtv.framework.system;

import android.content.Context;
import android.content.Intent;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.system.components.BaseLoginComponent;
import com.fxtv.framework.system.components.LoginQQComponent;
import com.fxtv.framework.system.components.LoginSinaComponent;
import com.fxtv.framework.system.components.LoginWechatComponent;

/**
 * 第三方登录系统
 *
 * @author FXTV-Android
 */
public class SystemThirdPartyLogin extends SystemBase {
    private static final String TAG = "SystemThirdPartyLogin";
    public static final int LOGIN_TYPE_QQ = 1;
    public static final int LOGIN_TYPE_SINA = 2;
    public static final int LOGIN_TYPE_WECHAT = 3;
    private BaseLoginComponent mLoginComponent;

    private ICallBackSystemLogin mLoginCallBack;
    private ICallBackSystemLogin mCurentCallBack;

    @Override
    public void createSystem(Context context) {
        super.createSystem(context);
    }

    @Override
    protected void init() {
        super.init();
        mLoginCallBack = new ICallBackSystemLogin() {

            @Override
            public void onSuccess(String msg) {
                if (mCurentCallBack != null) {
                    mCurentCallBack.onSuccess(msg);
                    onDestoryComponent();
                }

            }

            @Override
            public void onFailure(String msg) {
                if (mCurentCallBack != null) {
                    mCurentCallBack.onFailure(msg);
                    onDestoryComponent();
                }
            }

            @Override
            public void onCancle() {
                if (mCurentCallBack != null) {
                    mCurentCallBack.onCancle();
                    onDestoryComponent();
                }
            }
        };

    }

    @Override
    public void destroySystem() {
        super.destroySystem();
        if (mLoginComponent != null) {
            mLoginComponent.destory();
        }
        if (mLoginCallBack != null) {
            mLoginCallBack = null;
        }
        if (mCurentCallBack != null) {
            mCurentCallBack = null;
        }
    }

    /**
     * 第三方QQ登录
     * @param context
     */
    public void qqLogin(final Context context) {
        mLoginComponent = new LoginQQComponent(context, mLoginCallBack);
        mLoginComponent.Login();
    }

    /**
     * 使用登录系统的在所在的Activity的onActivityResult调用此方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mLoginComponent != null) {
            mLoginComponent.onActivityResult(requestCode, resultCode, data);
        }

    }

    /**
     * 第三方新浪客户端登录
     * @param context
     */
    public void sinaLogin(final Context context) {
        mLoginComponent = new LoginSinaComponent(context, mLoginCallBack);
        mLoginComponent.Login();

    }

    /**
     * 第三方微信登录
     * @param context
     */
    public void weChatLogin(final Context context) {
        mLoginComponent = new LoginWechatComponent(context, mLoginCallBack);
        mLoginComponent.Login();
    }

    /**
     * 调用第三方用户登录的接口
     * @param context
     * @param type
     * @param callBack
     */
    public void thirdLogin(final Context context, final int type, final ICallBackSystemLogin callBack) {
        mCurentCallBack = callBack;
        switch (type) {
            case LOGIN_TYPE_QQ:
                qqLogin(context);
                break;
            case LOGIN_TYPE_SINA:
                sinaLogin(context);
                break;
            case LOGIN_TYPE_WECHAT:
                weChatLogin(context);
                break;
            default:
                Logger.e(TAG, "Error,not find the type=" + type);
                break;
        }
    }

    /**
     * 销毁登录系统
     */
    public void onDestoryComponent() {
        if (mLoginComponent != null) {
            mLoginComponent.destory();
            mLoginComponent = null;
        }
        if (mCurentCallBack != null) {
            mCurentCallBack = null;
        }

    }


    public interface ICallBackSystemLogin {
        public void onSuccess(String msg);

        public void onFailure(String msg);

        public void onCancle();

    }

}

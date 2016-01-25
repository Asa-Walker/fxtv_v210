package com.fxtv.framework.system;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemBase;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 短信验证系统
 *
 * @author FXTV-Android
 */
public class SystemMsmAuth extends SystemBase {
    private static final String TAG = "SystemMsmAuth";

    public static final String APPKEY = "d664406ed3f0";
    public static final String APPSECRET = "542088ef34d34bf2884ea9c1bad33e8a";

    private static EventHandler mEventHandler;
    private IMsmAuthCallBack mCallBack;

    @Override
    protected void init() {
        super.init();
        //		SMSSDK.initSDK(mContext, APPKEY, APPSECRET);
//		mEventHandler = new EventHandler() {
//
//			@Override
//			public void afterEvent(int arg0, int arg1, Object arg2) {
//				Message msg = new Message();
//				msg.arg1 = arg0;
//				msg.arg2 = arg1;
//				msg.obj = arg2;
//				mHandler.sendMessage(msg);
//			}
//		};
//		SMSSDK.registerEventHandler(mEventHandler);
    }

    @Override
    protected void destroy() {
        super.destroy();
        if (mCallBack != null) {
            mCallBack = null;
        }
        SMSSDK.unregisterEventHandler(mEventHandler);
        mEventHandler = null;
    }

    public void initSharedSDK(Context context) {
        SMSSDK.initSDK(context, APPKEY, APPSECRET);
        mEventHandler = new EventHandler() {

            @Override
            public void afterEvent(int arg0, int arg1, Object arg2) {
                Message msg = new Message();
                msg.arg1 = arg0;
                msg.arg2 = arg1;
                msg.obj = arg2;
                mHandler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(mEventHandler);
    }

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;

            if (result == SMSSDK.RESULT_COMPLETE) {// 回调完成
                Logger.d(TAG, "handle message,RESULT_COMPLETE");
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    // 提交验证码成功
                    Logger.d(TAG, "handle message,EVENT_SUBMIT_VERIFICATION_CODE");
                    if (mCallBack != null) {
                        mCallBack.onSuccessAuthMsmCode();
                        destroySystem();
                    }
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    // 获取验证码成功
                    Logger.d(TAG, "handle message,EVENT_GET_VERIFICATION_CODE");
                    if (mCallBack != null) {
                        mCallBack.onSuccessSendMsmCode();
                    }
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    // 返回支持发送验证码的国家列表
                    Logger.d(TAG, "handle message,EVENT_GET_SUPPORTED_COUNTRIES");
                }
            } else {
                Logger.d(TAG, "handle message,run error");
                ((Throwable) data).printStackTrace();
                Log.i("aaaa", "data=" + data.toString());
                mCallBack.onFailure("验证失败");
                destroySystem();
            }
        }
    };

    /**
     * 发送验证码
     *
     * @param phone
     */
    public void sendMsmCode(String phone) {
        Logger.d(TAG, "sendMsmCode,phone=" + phone);
        SMSSDK.getVerificationCode("86", phone);
    }

    /**
     * 提交验证码
     *
     * @param phone
     * @param code
     */
    public void submitMsmCode(String phone, String code) {
        Logger.d(TAG, "submitMsmCode,phone=" + phone + ",code=" + code);
        SMSSDK.submitVerificationCode("86", phone, code);
    }

    /**
     * 设置回调函数
     *
     * @param callBack
     */
    public void setCallBack(IMsmAuthCallBack callBack) {
        mCallBack = callBack;
    }

    public interface IMsmAuthCallBack {
        public void onFailure(String msg);

        public void onSuccessSendMsmCode();

        public void onSuccessAuthMsmCode();
    }
}

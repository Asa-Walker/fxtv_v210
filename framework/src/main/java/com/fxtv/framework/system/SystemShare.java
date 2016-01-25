package com.fxtv.framework.system;


import android.content.Context;
import android.content.Intent;

import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.components.BaseShareComponent;
import com.fxtv.framework.system.components.ShareQQComponent;
import com.fxtv.framework.system.components.ShareQzonComponent;
import com.fxtv.framework.system.components.ShareSinaComponent;
import com.fxtv.framework.system.components.ShareWeChatCircleComponent;
import com.fxtv.framework.system.components.ShareWeChatComponent;


/**
 * 分享系统
 *
 * @author FXTV-Android
 */
public class SystemShare extends SystemBase {
    private final static String TAG = "SystemShare";
    public final static int SHARE_TYPE_QQ = 0;
    public final static int SHARE_TYPE_QQ_QZONE = 1;
    public final static int SHARE_TYPE_SINA = 2;
    public final static int SHARE_TYPE_WECHAT = 3;
    public final static int SHARE_TYPE_WECHAT_CIRCLE = 4;
    private BaseShareComponent mCurrentComponent;
    private ICallBackSystemShare mCurrentCallBack;
    private ICallBackSystemShare mShareCallBack;

    @Override
    protected void init() {
        super.init();
        mShareCallBack = new ICallBackSystemShare() {
            @Override
            public void onSuccess() {
                if (mCurrentCallBack != null) {
                    mCurrentCallBack.onSuccess();
                    destroyComponent();
                }
            }

            @Override
            public void onFailure(String msg) {
                if (mCurrentCallBack != null) {
                    mCurrentCallBack.onFailure(msg);
                    destroyComponent();
                }
            }

            @Override
            public void onCancle() {
                if (mCurrentCallBack != null) {
                    mCurrentCallBack.onCancle();
                    destroyComponent();
                }
            }
        };
    }

    @Override
    protected void destroy() {
        super.destroy();
        if(mShareCallBack != null){
            mShareCallBack = null;
        }

        if(mCurrentComponent != null){
            mCurrentComponent.destory();
        }

        if(mShareCallBack != null){
            mShareCallBack = null;
        }
    }
    /**
     * 分享
     * @param context  上下文
     * @param type 分享平台
     * @param model 分享实体
     * @param callBack 分享回调
     */
    public void share(final Context context, final int type, final ShareModel model, final ICallBackSystemShare callBack) {
        mCurrentCallBack = callBack;
        switch (type) {
            case SHARE_TYPE_QQ:
                shareQQ(context, model);
                break;
            case SHARE_TYPE_QQ_QZONE:
                shareQZone(context, model);
                break;
            case SHARE_TYPE_SINA:
                shareSina(context, model);
                break;
            case SHARE_TYPE_WECHAT:
                shareWeChat(mContext, model);
                break;
            case SHARE_TYPE_WECHAT_CIRCLE:
                shareWeChatCircle(mContext, model);
                break;
            default:
                if (callBack != null) {
                    callBack.onFailure("不支持此类型分享");
                }
                break;
        }
    }

    /**
     * 调用分享的activity中 必须调用此函数
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCurrentComponent != null) {
            mCurrentComponent.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * QQ分享
     * @param context
     * @param model
     */
    private void shareQQ(Context context, final ShareModel model) {
        mCurrentComponent = new ShareQQComponent(context, mShareCallBack);
        mCurrentComponent.share(model);
    }

    /**
     * QQ空间分享
     * @param context
     * @param model
     */
    private void shareQZone(Context context, final ShareModel model) {
        mCurrentComponent = new ShareQzonComponent(context, mShareCallBack);
        mCurrentComponent.share(model);
    }

    /**
     * 新浪分享
     * @param context
     * @param model
     */
    private void shareSina(Context context, final ShareModel model) {
        mCurrentComponent = new ShareSinaComponent(context, mShareCallBack);
        mCurrentComponent.share(model);
    }

    /**
     * 微信分享
     * @param context
     * @param model
     */
    private void shareWeChat(Context context, final ShareModel model) {
        mCurrentComponent = new ShareWeChatComponent(context, mShareCallBack);
        mCurrentComponent.share(model);
    }

    /**
     * 微信朋友圈分享
     * @param context
     * @param model
     */
    private void shareWeChatCircle(Context context, final ShareModel model) {
        mCurrentComponent = new ShareWeChatCircleComponent(context, mShareCallBack);
        mCurrentComponent.share(model);
    }

    /**
     * 分享控件销毁
     */
    private void destroyComponent(){
        if(mCurrentComponent != null){
            mCurrentComponent.destory();
            mCurrentComponent = null;
        }
        mCurrentCallBack = null;
    }


    public interface ICallBackSystemShare {
        public void onSuccess();

        public void onFailure(String msg);

        public void onCancle();
    }

}

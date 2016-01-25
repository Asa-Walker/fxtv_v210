package com.fxtv.framework.system.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fxtv.framework.Logger;
import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemShare;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.open.utils.ThreadManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * 关于分享功能
 *
 * @author Android2
 */
public class ShareQQComponent extends BaseShareComponent {
    private static final String TAG = "ShareQQComponent";
    private Context mContext;
    public static final String QQAPPID = "1104731238";
    private Tencent mTencent;
    private IUiListener mIqListener;
    private SystemShare.ICallBackSystemShare mCallBack;
    public ShareQQComponent(Context context, SystemShare.ICallBackSystemShare callBack) {
        this.mContext = context;
        this.mCallBack = callBack;
        mTencent = Tencent.createInstance(QQAPPID, mContext);
        mIqListener = new IqListener();
    }

    @Override
    public void destory() {
        super.destory();
        mContext = null;
        if (mTencent != null) {
            mTencent.releaseResource();
            mTencent = null;
        }
        if (mCallBack != null) {
            mCallBack = null;
        }
        if (mIqListener != null) {
            mIqListener = null;
        }
    }

    /**
     * 实现QQ分享
     * @param model
     */
    @Override
    public void share(ShareModel model) {
        super.share(model);
        final Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TITLE, model.shareTitle);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, model.shareSummary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, model.shareUrl);
        if (model.fileImageUrl!=null&&!model.fileImageUrl.equals("")){
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, model.fileImageUrl);
        }
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "飞熊视频");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0x00);
        ThreadManager.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                Logger.d(TAG, "shareQQ");
                mTencent.shareToQQ((Activity) mContext, params, mIqListener);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, intent, mIqListener);
        }
    }

    /**
     * 分享回调接口
     */
    private class IqListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            if (mCallBack != null) {
                Logger.d(TAG, "QQ  SHARE SUCCESS");
                mCallBack.onSuccess();
            }
        }

        @Override
        public void onError(UiError uiError) {
            if (mCallBack != null) {
                Logger.d(TAG, "QQ SHARE ERROR:"+uiError.errorMessage);
                mCallBack.onFailure(uiError.errorMessage);
            }
        }

        @Override
        public void onCancel() {
            if (mCallBack != null) {
                Logger.d(TAG, "QQ SHARE CANCLE");
                mCallBack.onCancle();
            }
        }
    }
}

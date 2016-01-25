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
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.utils.ThreadManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

/**
 * 关于QQ空间分享功能
 * @author Android2
 *
 */
public class ShareQzonComponent extends BaseShareComponent {
	private static final String TAG = "ShareQQComponent";
	private Context mContext;
	public static final String QQAPPID = "1104731238";
	private Tencent mTencent;
	private IUiListener mIqListener;
	private SystemShare.ICallBackSystemShare mCallBack;

	public ShareQzonComponent(Context context, SystemShare.ICallBackSystemShare callBack){
		this.mContext = context;
		this.mCallBack = callBack;
		mTencent = Tencent.createInstance(QQAPPID, mContext);
		mIqListener = new IqListener();
	}
	@Override
	public void share(ShareModel model) {
		final Bundle params = new Bundle();
		ArrayList<String> imageUrls = new ArrayList<String>();
		if (model.fileImageUrl!=null&&!model.fileImageUrl.equals("")){
			imageUrls.add(model.fileImageUrl);
		}
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, model.shareTitle);
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, model.shareSummary);
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, model.shareUrl);
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
		params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, "飞熊视频");
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);

		ThreadManager.getMainHandler().post(new Runnable() {
			@Override
			public void run() {
				mTencent.shareToQzone((Activity)mContext, params, mIqListener);
			}
		});
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == Constants.REQUEST_QZONE_SHARE){
			Tencent.onActivityResultData(requestCode,resultCode,intent,mIqListener);
		}
	}

	/**
	 * 分享信息回调
	 */
	private class IqListener implements IUiListener {

		@Override
		public void onComplete(Object o) {
			if (mCallBack != null) {
				Logger.d(TAG,"QQZone share Success");
				mCallBack.onSuccess();
			}

		}

		@Override
		public void onError(UiError uiError) {
			if (mCallBack != null) {
				Logger.d(TAG,"QQZone share Failure"+uiError.errorMessage);
				mCallBack.onFailure(uiError.errorDetail);
			}
		}

		@Override
		public void onCancel() {
			if (mCallBack != null) {
				Logger.d(TAG,"QQZone share Cancel");
				mCallBack.onCancle();
			}
		}
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

}

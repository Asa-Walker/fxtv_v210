package com.fxtv.threebears.view.mediaplayer.components;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.mediaplayer.DanmakuController;
import com.fxtv.threebears.view.mediaplayer.DanmakuController.IDanmakuListeners;
import com.fxtv.threebears.view.mediaplayer.MediaController;
import com.google.gson.JsonObject;

import master.flame.danmaku.controller.IDanmakuView;

/**
 * 弹幕组件
 * 
 * @author Administrator
 * 
 */
public class ComponentControllerDanmaku {
	private static final String TAG = "ComponentControllerDanmaku";

	private MediaController mController;
	private ViewGroup mParentView, mChildView;
	private LayoutInflater mInflater;

	private DanmakuController mDanmakuController;

	public ComponentControllerDanmaku(MediaController controller, ViewGroup parent, LayoutInflater inflater) {
		this.mController = controller;
		this.mParentView = parent;
		this.mInflater = inflater;

		init();
	}

	public void setCallBack(IDanmakuListeners callBack) {
		mDanmakuController.setOnPrepareListener(callBack);
	}

	public void setDanmakuUrl(String url) {
		if (mController.isDownloaded())
			return;
		mDanmakuController.loadDanmaku(url);
	}
	public void start() {
		if (mController.isDownloaded())
			return;
		mDanmakuController.start();
	}

	public void pause() {
		if (mController.isDownloaded())
			return;
		mDanmakuController.pause();
	}

	public void resume() {
		if (mController.isDownloaded())
			return;
		mDanmakuController.resume();
	}

	public void seekTo(long ms) {
		if (mController.isDownloaded())
			return;
		mDanmakuController.seekTo(ms);
	}

	public void show() {
		if (mController.isDownloaded())
			return;
		mDanmakuController.show();
	}

	public void release() {
		if (mController.isDownloaded())
			return;
		mDanmakuController.release();
	}

	public long getCurrentTime() {
		if (mController.isDownloaded())
			return 0;
		return mDanmakuController.getCurrentTime();
	}

	public void hide() {
		mDanmakuController.hide();
	}
	
	public boolean isShown() {
		return mDanmakuController.isShown();
	}

	public void sendDanmaku(String vid, long pos, String content) {
		if (mController.isDownloaded())
			return;
		sendDanmakuForNative(content);
		sendDanmakuForService(vid, pos / 1000, content);
	}

	/**
	 * 发送弹幕(本地)
	 * 
	 * @param content
	 */
	private void sendDanmakuForNative(String content) {
		mDanmakuController.addDanmaku(content);
	}

	/**
	 * 发送弹幕(服务器)
	 * 
	 * @param content
	 */
	private void sendDanmakuForService(String vid, long pos, String content) {
		JsonObject params = new JsonObject();
		params.addProperty("id", vid);
		params.addProperty("node_time", pos + "");
		params.addProperty("style", "1");
		params.addProperty("size", "25");
		params.addProperty("color", "ffffff");
		params.addProperty("content", content);

		String url = Utils.processUrl(ModuleType.USER, ApiType.USER_sendBarrage, params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get(mController.getContext(), url, "sendDanmaku", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				Logger.d(TAG, "发送弹幕 onSuccess");
				showToast(resp.msg);
			}

			@Override
			public void onFailure(Response resp) {
				Logger.d(TAG, "发送弹幕成 onFailure");
				showToast("弹幕上传服务器失败");
			}

			@Override
			public void onComplete() {

			}
		});

	}

	private void init() {
		mChildView = (ViewGroup) mInflater.inflate(R.layout.mediaplayer_controller_danmaku_layout, mParentView);
		IDanmakuView danmakuView = (IDanmakuView) mChildView.findViewById(R.id.sv_danmaku);
		mDanmakuController = new DanmakuController(mController.getContext(), danmakuView);
	}

	private void showToast(String msg) {
		FrameworkUtils.showToast(mController.getContext(), msg);
	}
}

package com.fxtv.threebears.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestSimpleCallBack;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ServiceMessage extends Service {
	private List<IMessageCallBack> mListCallBacks;
	private IBinder mBinder;
	private boolean mThreadFlag = true;
	private MyThread mThread;
	// wifi 环境下，心跳时间间隔 10s
	private final int TIME_WIFI = 10 * 1000;
	// 流量 环境下，心跳时间间隔 30s
	private final int TIME_MOB = 30 * 1000;
	private boolean mLastStatus;

	@Override
	public void onCreate() {
		super.onCreate();
		mListCallBacks = new ArrayList<ServiceMessage.IMessageCallBack>();
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (mBinder == null)
			mBinder = new MsgBinder();
		return mBinder;
	}

	@Override
	public void unbindService(ServiceConnection conn) {
		super.unbindService(conn);
		mThreadFlag = false;
		mBinder = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mListCallBacks.clear();
		mListCallBacks = null;
	}

	public class MsgBinder extends Binder {
		public ServiceMessage getService() {
			return ServiceMessage.this;
		}
	}

	public void addCallBack(IMessageCallBack callBack) {
		boolean isHas = false;
		for (IMessageCallBack tmpCallBack : mListCallBacks) {
			if (callBack == tmpCallBack) {
				isHas = true;
				break;
			}
		}
		if (!isHas) {
			mListCallBacks.add(callBack);
			// why call back once? Because delay !
			callBack.onHasMessage(mLastStatus);
		}
	}

	public void removeCallBack(IMessageCallBack callBack) {
		mListCallBacks.remove(callBack);
	}

	public void checkHasMessage() {
		if (mThread == null) {
			mThread = new MyThread();
			mThread.start();
		}
	}

	private class MyThread extends Thread {
		private int mTime;

		@Override
		public void run() {
			try {
				while (mThreadFlag) {
					Thread.sleep(1000);
					mTime += 1000;
					if (FrameworkUtils.isNetworkConnected(getApplicationContext())) {// network
						// ok
						if (FrameworkUtils.isWifiConnected(getApplicationContext())) {// wifi
							if (mTime >= TIME_WIFI) {
								netGetHasMessage();
								mTime = 0;
							}
						} else { // mob env
							if (mTime >= TIME_MOB) {
								netGetHasMessage();
								mTime = 0;
							}
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void netGetHasMessage() {
		if (!SystemManager.getInstance().getSystem(SystemUser.class).isLogin())
			return;
		JsonObject params = new JsonObject();
		params.addProperty("user_id",
				SystemManager.getInstance().getSystem(SystemUser.class).mUser.user_id);

		String url = Utils.processUrl(ModuleType.USER, ApiType.USER_newMessage, params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get(getApplicationContext(), url, "checkNewMessageApi", false, false, new RequestSimpleCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				try {
					JSONObject jObject = new JSONObject(data);
					String hasMessage = jObject.getString("new_message");
					callBack(hasMessage.equals("1"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		});

	}

	private void callBack(boolean hasMessage) {
		mLastStatus = hasMessage;
		for (IMessageCallBack callBack : mListCallBacks) {
			callBack.onHasMessage(hasMessage);
		}
	}

	public interface IMessageCallBack {
		public void onHasMessage(boolean hasMessage);
	}
}

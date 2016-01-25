package com.fxtv.framework.frame;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.system.SystemAnalytics;
import com.fxtv.framework.system.SystemCrashLogCollect;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.SystemPage;
import com.fxtv.framework.system.SystemPush;

import java.io.Serializable;

public class BaseActivity extends Activity {
	protected SystemManager mSystemManager;
	protected LayoutInflater mLayoutInflater;
	//Activity获取传输数据，请用：baseSavedInstance.getSerializable("key")...
	protected Bundle baseSavedInstance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		baseSavedInstance=savedInstanceState;
		if(baseSavedInstance==null){
			baseSavedInstance=getIntent().getExtras();
		}

		mLayoutInflater = LayoutInflater.from(this);
		mSystemManager = SystemManager.getInstance();
		mSystemManager.getSystem(SystemPage.class).addActivity(this);
		mSystemManager.getSystem(SystemCrashLogCollect.class).activityOnCreate();
		mSystemManager.getSystem(SystemPush.class).activityOnCreate();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSystemManager.getSystem(SystemAnalytics.class).activityResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSystemManager.getSystem(SystemAnalytics.class).activityPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLayoutInflater = null;
		mSystemManager.getSystem(SystemHttp.class).cancelRequest(this, true);
		mSystemManager.getSystem(SystemPage.class).finishActivity(this);
	}

	public void showToast(String msg) {
		FrameworkUtils.showToast(this, msg);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(baseSavedInstance!=null){
			outState.putAll(baseSavedInstance);
		}
	}
	//Activity取传输String数据直接用这个，较安全
	protected String getStringExtra(String key){
		return baseSavedInstance==null?null:baseSavedInstance.getString(key);
	}
	protected Serializable getSerializable(String key){
		return baseSavedInstance==null?null:baseSavedInstance.getSerializable(key);
	}
	//SystemFrameworkConfig
	protected <T extends SystemBase> T getSystem(Class<T> className) {
		return SystemManager.getInstance().getSystem(className);
	}

	/**
	 * 返回点击事件
	 */
	public void backClick(View v){
		finish();
	}
}

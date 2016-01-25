package com.fxtv.threebears.frame;

import android.annotation.SuppressLint;
import android.content.res.Configuration;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.FrameworkApplication;
import com.fxtv.threebears.romlite.DatabaseHelper;

public class CustomApplication extends FrameworkApplication {
	private final String TAG = "CustomApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.d(TAG, "onCreate");
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@SuppressLint("NewApi")
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}

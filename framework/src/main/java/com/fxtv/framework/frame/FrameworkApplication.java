package com.fxtv.framework.frame;

import android.app.Application;
import android.content.res.Configuration;

import com.fxtv.framework.Logger;

public class FrameworkApplication extends Application {
	private final String TAG = "FragmentApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		SystemManager.getInstance().initSystem(this);
		Logger.d(TAG, "onCreate");
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Logger.d(TAG, "onLowMemory");
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		Logger.d(TAG, "onTrimMemory,level="+level);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Logger.d(TAG, "onTerminate");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Logger.d(TAG, "onConfigurationChanged");
	}
}

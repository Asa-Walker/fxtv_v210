package com.fxtv.framework.frame;

import android.content.Context;

public interface ISystemManager {

	public void initSystem(Context context);
	
	public void destorySystem(String key);
	
	public void destoryAllSystem();
}

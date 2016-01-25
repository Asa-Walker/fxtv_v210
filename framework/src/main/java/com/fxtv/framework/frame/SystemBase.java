package com.fxtv.framework.frame;

import android.content.Context;

public class SystemBase implements ISystem {
	protected Context mContext;

	@Override
	public void createSystem(Context context) {
		mContext = context;
		init();
	}

	@Override
	public void destroySystem() {
		destroy();
		mContext = null;
	}

	protected void init(){
	}
	
	protected void destroy(){
	}

	//SystemFrameworkConfig
	protected <T extends SystemBase> T getSystem(Class<T> className) {
		return SystemManager.getInstance().getSystem(className);
	}

}

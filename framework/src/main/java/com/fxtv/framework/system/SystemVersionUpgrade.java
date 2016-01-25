package com.fxtv.framework.system;

import android.content.Context;

import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.components.ComponentUpgradeApk;

/**
 * app版本升级系统
 * 
 * @author FXTV-Android
 * 
 */
public class SystemVersionUpgrade extends SystemBase {
	private static final String TAG = "SystemVersionUpgrade";

	private ComponentUpgradeApk mComponentUpgradeApk;

	@Override
	protected void init() {
		super.init();
		mComponentUpgradeApk = new ComponentUpgradeApk(mContext, SystemManager.getInstance()
				.getSystem(SystemFrameworkConfig.class).mCacheDir);
	}

	@Override
	protected void destroy() {
		super.destroy();
		if (mComponentUpgradeApk != null) {
			mComponentUpgradeApk = null;
		}
	}

	public void checkApkUpdate(final String url, final IApkUpgradeCallBack callBack) {
		mComponentUpgradeApk.checkApkUpdate(url, callBack);
	}

	public void upgradeApk() {
		mComponentUpgradeApk.upgradeApk();
	}

	public interface IApkUpgradeCallBack {
		public void onResult(boolean shouldUpgrade, boolean compulsive);

		public void onError(String msg);
	}
}

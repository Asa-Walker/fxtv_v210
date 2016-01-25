package com.fxtv.threebears.system;

import android.content.Context;
import android.content.SharedPreferences;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.frame.SystemManager;

public class SystemPreference extends SystemBase {
	private static final String TAG = "SystemPreference";
	private static final String SHAREDPREFERENCES_NAME = "fxtv_config";
	// 首次启动
	private static final String FIRST_LAUNCH = "first_launch";
	private static final String FIRST_LOGIN = "application_first_login";
	private static final String FIRST_INTO_MAIN = "first_into_main";
	private static final String UC_CODE_LOGIN = "uc_code_login";
	private static final String UC_CODE_LOGOUT = "uc_code_logout";
	private static final String FIRST_INTO_EXPLORER = "first_into_explorer";
	private static final String FIRST_INTO_PERSONAL = "first_into_personal";
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mEditor;

	@Override
	protected void init() {
		super.init();
		mSharedPreferences = mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
	}

	@Override
	protected void destroy() {
		super.destroy();
		mSharedPreferences = null;
		mEditor = null;
	}

	/** ------------------业务逻辑相关--------------------- */
	public boolean isFirstLaunch() {
		return mSharedPreferences.getBoolean(FIRST_LAUNCH, true);
	}

	public void setFirstLaunch(boolean firstLaunch) {
		mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(FIRST_LAUNCH, firstLaunch);
		mEditor.commit();
	}

	public boolean isFirstIntoMain() {
		return mSharedPreferences.getBoolean(FIRST_INTO_MAIN, true);
	}

	public boolean isFirstIntoExplorer() {
		return mSharedPreferences.getBoolean(FIRST_INTO_EXPLORER, true);
	}

	public boolean isFirstIntoPersonal() {
		return mSharedPreferences.getBoolean(FIRST_INTO_PERSONAL, true);
	}

	public void setFirstIntoMain(boolean firstIntoMain) {
		mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(FIRST_INTO_MAIN, firstIntoMain);
		mEditor.commit();
	}
	public void setFirstIntoExplorer(boolean firstIntoExplorer) {
		mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(FIRST_INTO_EXPLORER, firstIntoExplorer);
		mEditor.commit();
	}
	public void setFirstIntoPersonal(boolean firstIntoPersonal) {
		mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(FIRST_INTO_PERSONAL, firstIntoPersonal);
		mEditor.commit();
	}

	public void isApplicationFirstLogin() {
		mSharedPreferences.getBoolean(FIRST_LOGIN, false);
	}

	public void setApplicationFirstLogin(boolean firstLogin) {
		mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(FIRST_LOGIN, firstLogin);
		mEditor.commit();
	}

	public void setUCLogin(String uc) {
		mEditor = mSharedPreferences.edit();
		mEditor.putString(UC_CODE_LOGIN, uc);
		mEditor.commit();
	}

	public void setUCLogout(String uc) {
		mEditor = mSharedPreferences.edit();
		mEditor.putString(UC_CODE_LOGOUT, uc);
		mEditor.commit();
	}

	/**
	 * 获取存储的UC
	 * 
	 * @param type
	 *            0:logout,1:login,2:current
	 * @return
	 */
	public String getUC(int type) {
		String key = "";
		if (type == 0) {
			key = UC_CODE_LOGOUT;
		} else if (type == 1) {
			key = UC_CODE_LOGIN;
		} else {
			if (SystemManager.getInstance().getSystem(SystemUser.class).isLogin()) {
				key = UC_CODE_LOGIN;
			} else {
				key = UC_CODE_LOGOUT;
			}
		}

		return mSharedPreferences.getString(key, "");
	}
}

package com.fxtv.framework.system;

import android.content.Context;

import com.fxtv.framework.frame.SystemBase;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * 数据统计系统
 * 
 * @author FXTV-Android
 * 
 */
public class SystemAnalytics extends SystemBase {
	@Override
	protected void init() {
		super.init();
		// 友盟 禁止默认的页面统计方式，这样将不会再自动统计Activity。
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.setDebugMode(true);
		// 友盟 日志加密设置
		AnalyticsConfig.enableEncrypt(false);
	}

	@Override
	protected void destroy() {
		super.destroy();
	}

	public void activityResume(Context context) {
		MobclickAgent.onResume(context);
	}

	public void activityPause(Context context) {
		MobclickAgent.onPause(context);
	}

	public void fragmentResume(String name) {
		MobclickAgent.onPageStart(name);
	}

	public void fragmentPause(String name) {
		MobclickAgent.onPageEnd(name);
	}
}

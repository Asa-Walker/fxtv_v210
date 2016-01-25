package com.fxtv.framework;

import android.util.Log;

import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemFrameworkConfig;

public class Logger {

	public static void d(String msg) {
		d("",msg);
	}
	public static void d(String tag, String msg) {
		if (SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mLog) {
			Log.d("fxtv_" + tag, msg + "\n" + getAutoJumpLogInfos());
		}
	}

	public static void i(String tag, String msg) {
		if (SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mLog) {
			Log.i("fxtv_" + tag, msg + "\n" + getAutoJumpLogInfos());
		}
	}

	public static void e(String tag, String msg) {
		if (SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mLog) {
			Log.e("fxtv_" + tag, msg + "\n" + getAutoJumpLogInfos());
		}
	}

	private static String getAutoJumpLogInfos() {
		StackTraceElement[] caller = Thread.currentThread().getStackTrace();
		if (caller.length < 5) {
			return "";
		}
		return "";
	}

	private static String generateTag(StackTraceElement caller) {
		String tag = "%s.%s(Line:%d)"; // 占位符
		String callerClazzName = caller.getClassName(); // 获取到类名
		callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
		tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber()); // 替换
		return tag;
	}
}

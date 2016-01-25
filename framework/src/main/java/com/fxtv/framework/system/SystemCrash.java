package com.fxtv.framework.system;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.frame.SystemManager;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 程序错误处理系统
 *
 * @author FXTV-Android
 *
 */
public class SystemCrash extends SystemBase {
	private static final String TAG = "SystemCrash";
	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	@Override
	protected void init() {
		super.init();
		if (SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mCrashHander) {
			open();
		}
	}

	@Override
	protected void destroy() {
		super.destroy();
		if (mDefaultHandler != null) {
			mDefaultHandler = null;
		}
	}

	public void close() {
		if (mDefaultHandler == null) {
			mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		}
		Thread.setDefaultUncaughtExceptionHandler(mDefaultHandler);
	}

	public void open() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				String message = ex.getMessage();
				Logger.e(TAG, "uncaughtException,happen error=" + message);

				if (SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mCrashLogNative) {
					String crash = savaInfoToSD(mContext, ex);
					Logger.e(TAG, "crash=" + crash);
					// upLoadCrash(crash);
				}

				if (SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mCrashReset) {
					// showToast(mContext, "很抱歉，程序遭遇异常，即将重启");
					resetApp(0);
				}
			}
		});
	}

	private void resetApp(long delayMillis) {
		try {
			Thread.sleep(delayMillis);
			FrameworkUtils.restartApplication(mContext,mContext.getPackageName());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void upLoadCrash(String crash) {
		JsonObject params = new JsonObject();
		params.addProperty("log", crash);
		// String url = SystemHttp.getInstance().processUrl("base", "log",
		// params.toString());
		// SystemHttp.getInstance().get(mContext, url, "crashUploadApi", false,
		// false, null);
	}

	/**
	 * 获取系统未捕捉的错误信息
	 *
	 * @param throwable
	 * @return
	 */
	private String obtainExceptionInfo(Throwable throwable) {
		StringWriter mStringWriter = new StringWriter();
		PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
		throwable.printStackTrace(mPrintWriter);
		mPrintWriter.close();

		return mStringWriter.toString();
	}

	/**
	 * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
	 *
	 * @param context
	 * @return
	 */
	private HashMap<String, String> obtainSimpleInfo(Context context) {
		HashMap<String, String> map = new HashMap<String, String>();
		PackageManager mPackageManager = context.getPackageManager();
		PackageInfo mPackageInfo = null;
		try {
			mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			map.put("versionName", mPackageInfo.versionName);
			map.put("versionCode", "" + mPackageInfo.versionCode);

			map.put("MODEL", "" + Build.MODEL);
			map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
			map.put("PRODUCT", "" + Build.PRODUCT);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 保存获取的 软件信息，设备信息和出错信息保存在SDcard中
	 *
	 * @param context
	 * @param ex
	 * @return
	 */
	private String savaInfoToSD(Context context, Throwable ex) {
		String fileName = null;
		StringBuffer sb = new StringBuffer();

		for (Map.Entry<String, String> entry : obtainSimpleInfo(context).entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key).append(" = ").append(value).append("\n");
		}

		sb.append(obtainExceptionInfo(ex));

		File dir = new File(SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mCacheDir
				+ "/crash");
		if (!dir.exists()) {
			dir.mkdir();
		}

		try {
			fileName = dir.toString() + File.separator + paserTime(System.currentTimeMillis())
					+ ".log";
			FileOutputStream fos = new FileOutputStream(fileName);
			fos.write(sb.toString().getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	/**
	 * 将毫秒数转换成yyyy-MM-dd-HH-mm-ss的格式
	 *
	 * @param milliseconds
	 * @return
	 */
	private String paserTime(long milliseconds) {
		System.setProperty("user.timezone", "Asia/Shanghai");
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone.setDefault(tz);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String times = format.format(new Date(milliseconds));

		return times;
	}
}

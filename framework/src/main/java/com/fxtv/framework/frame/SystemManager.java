package com.fxtv.framework.frame;

import android.content.Context;
import android.nfc.Tag;

import com.fxtv.framework.Logger;
import com.fxtv.framework.system.SystemCrash;
import com.fxtv.framework.system.SystemFrameworkConfig;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class SystemManager implements ISystemManager {

	protected Context mContext;
	private static SystemManager mInstance;
	protected HashMap<String, SystemBase> mSystemPool;

	protected SystemManager() {
		mSystemPool = new HashMap<>();
	}

	public static SystemManager getInstance() {
		if (mInstance == null) {
			synchronized (SystemManager.class) {
				mInstance = new SystemManager();
			}
		}
		return mInstance;
	}

	/**
	 * 初始化各个系统
	 * 
	 * @param context
	 */
	@Override
	public void initSystem(Context context) {
		mContext = context.getApplicationContext();
		// 特殊
		getSystem(SystemCrash.class);
		getSystem(SystemFrameworkConfig.class);
	}

	@Override
	public void destoryAllSystem() {
		Iterator<Entry<String, SystemBase>> iterator = mSystemPool.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, SystemBase> next = iterator.next();
			next.getValue().destroySystem();
		}
		mSystemPool.clear();
//		mSystemPool = null;
//		mInstance = null;
//		mContext = null;
	}

	/**
	 * 销毁系统
	 * 
	 * @param key
	 */
	@Override
	public void destorySystem(String key) {
		SystemBase systemBase;
		if ((systemBase = mSystemPool.get(key)) != null) {
			systemBase.destroySystem();
			mSystemPool.remove(systemBase);
		}
	}

	/**
	 * SystemHttp systemHttp=getSystem(SystemHttp.class);
	 * @param className {
	 *                  SystemFrameworkConfig.class Framework 配置系统
	 *                  SystemPage 页面管理系统
	 *                  SystemMsmAuth 短信验证系统
	 *                  SystemUpload 文件上传系统
	 *                  SystemVersionUpgrade 版本升级系统
	 *                  SystemShare 分享系统
	 *                  SystemPush 推送系统
	 *                  SystemAnalytics 数据统计系统
	 *                  SystemImageLoader 图片加载系统
	 *                  SystemHttpCache 接口缓存系统
	 *                  SystemHttp 接口系统
	 *                  SystemFragmentManager  fragment管理系统
	 *                  SystemThirdPartyLogin
	 *                  SystemCrash
	 *                  SystemCrashLogCollect
	 *                  SystemOther
	 *                     }
	 * app层，业务逻辑相关System
	 * ApplicationSystem{
	 *                  SystemUser 用户系统
	 *                  SystemConfig 配置系统
	 *                  SystemHistory 历史系统
	 *                  SystemPreference 存储系统
	 *                  SystemCommon 通用功能
	 *                  SystemAnalyze 统计系统
	 *                  SystemDownloadVideoManager 下载视频
	 *                  }
	 * @param <T>
	 * @return
	 */
	public <T extends SystemBase> T getSystem(Class<T> className) {
		if(className==null){
			return null;
		}
		T instance= (T) mSystemPool.get(className.getName());
		if(instance==null){
			try {
				instance=className.newInstance();
				className.getMethod("createSystem",Context.class).invoke(instance, mContext);
				mSystemPool.put(className.getName(),instance);
			} catch (Exception e) {
				e.printStackTrace();
				Logger.e("SystemManager","getSystem error="+e.getMessage());
				return null;
			}

		}
		return instance;
	}
}

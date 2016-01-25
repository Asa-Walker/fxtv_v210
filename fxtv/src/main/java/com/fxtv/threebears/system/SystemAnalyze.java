package com.fxtv.threebears.system;

import android.text.TextUtils;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import org.json.JSONObject;

public class SystemAnalyze extends SystemBase {

	private static final String TAG = "SystemAnalyze";

	private String mAppStartId;
	private String mVideoPlayId;

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void destroySystem() {
		super.destroySystem();
	}

	/** -------------------业务---------------- */

	/**
	 * App 启动统计
	 */
	public void analyzeAppStart() {
		JsonObject params = new JsonObject();
		params.addProperty("sys_version", android.os.Build.VERSION.RELEASE);
		params.addProperty("app_version", FrameworkUtils.getVersion(mContext));
		params.addProperty("channel", FrameworkUtils.getMetaData(mContext, "UMENG_CHANNEL"));
		String url= Utils.processUrl(ModuleType.Log, ApiType.LOG_deviceStart,params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get(mContext, url, "analyzeAppStart", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				try {
					JSONObject value = new JSONObject(data);
					mAppStartId = value.getString("id");
				} catch (Exception e) {
				}
				Logger.d(TAG, "analyzeAppStart,onSuccess,id=" + mAppStartId);
			}

			@Override
			public void onFailure(Response resp) {
				Logger.e(TAG, resp.msg);
			}

			@Override
			public void onComplete() {

			}
		});
//		SystemManager.getInstance().getSystem(SystemHttp.class).get(url, "analyzeAppStart", false, false, callBack);
//		getHttpRequests().analyzeAppStart(params.toString(), new RequestCallBack2() {
//
//			@Override
//			public void onSuccess(String json, boolean fromCache, String msg) {
//				try {
//					JSONObject data = new JSONObject(json);
//					mAppStartId = data.getString("id");
//				} catch (Exception e) {
//				}
//				Logger.d(TAG, "analyzeAppStart,onSuccess,id=" + mAppStartId);
//			}
//
//			@Override
//			public void onFailure(String msg, boolean fromCache) {
//				Logger.e(TAG, msg);
//			}
//
//			@Override
//			public void onComplete() {
//			}
//		});
	}

	/**
	 * App 关闭统计
	 */
	public void analyzeAppEnd() {
		if (TextUtils.isEmpty(mAppStartId)) {
			Logger.e(TAG, "analyzeAppEnd,id is null");
			return;
		}

		Logger.d(TAG, "analyzeAppEnd,run here,mAppStartId=" + mAppStartId);
		JsonObject params = new JsonObject();
		params.addProperty("id", mAppStartId);
		String url=Utils.processUrl(ModuleType.Log, ApiType.LOG_deviceEnd, params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get(mContext, url, "analyzeAppDestory", false, false, new RequestCallBack() {
			@Override
			public void onSuccess(Object data, Response resp) {
				Logger.d(TAG, "analyzeAppEnd,onSuccess");
			}

			@Override
			public void onFailure(Response resp) {
				Logger.d(TAG, "analyzeAppEnd,onFailure");
			}

			@Override
			public void onComplete() {
				Logger.d(TAG, "analyzeAppEnd,onComplete");
				mAppStartId = null;
			}
		});
//		SystemManager.getInstance().getSystem(SystemHttp.class).get(url, "analyzeAppDestory", false, false, callBack);
//		getHttpRequests().analyzeAppDestory(params.toString(), new RequestCallBack2() {
//
//			@Override
//			public void onSuccess(String json, boolean fromCache, String msg) {
//				Logger.d(TAG, "analyzeAppEnd,onSuccess");
//			}
//
//			@Override
//			public void onFailure(String msg, boolean fromCache) {
//				Logger.d(TAG, "analyzeAppEnd,onFailure");
//			}
//
//			@Override
//			public void onComplete() {
//				Logger.d(TAG, "analyzeAppEnd,onComplete");
//				mAppStartId = null;
//			}
//		});
	}

	/**
	 * 视频开始播放
	 * 
	 * @param vid
	 */
	public void analyzeVideoStart(String vid) {
		JsonObject params = new JsonObject();
		params.addProperty("id", vid);
		String url=Utils.processUrl(ModuleType.Log,ApiType.LOG_startPlay,params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get(mContext,url, "analyzeVideoPlayStart", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				try {
					JSONObject value = new JSONObject(data);
					mVideoPlayId = value.getString("id");
				} catch (Exception e) {
				}
				Logger.d(TAG, "analyzeVideoStart,onSuccess,id=" + mVideoPlayId);
			}

			@Override
			public void onFailure(Response resp) {
				Logger.e(TAG, resp.msg);
			}

			@Override
			public void onComplete() {

			}
		});
//		String url = processUrl("Log", "startPlay", params);
//		SystemManager.getInstance().getSystem(SystemHttp.class).get(url, "analyzeVideoPlayStart", false, false, callBack);
//		getHttpRequests().analyzeVideoPlayStart(params.toString(), new RequestCallBack2() {
//
//			@Override
//			public void onSuccess(String json, boolean fromCache, String msg) {
//				Gson gson = new Gson();
//				// JsonObject data = gson.fromJson(json, JsonObject.class);
//				// mVideoPlayId = data.getAsJsonPrimitive("id").toString();
//				try {
//					JSONObject data = new JSONObject(json);
//					mVideoPlayId = data.getString("id");
//				} catch (Exception e) {
//				}
//				Logger.d(TAG, "analyzeVideoStart,onSuccess,id=" + mVideoPlayId);
//			}
//
//			@Override
//			public void onFailure(String msg, boolean fromCache) {
//				Logger.e(TAG, msg);
//			}
//
//			@Override
//			public void onComplete() {
//			}
//		});
	}

	/**
	 * 视频播放结束
	 */
	public void analyzeVideoEnd() {
		if (TextUtils.isEmpty(mVideoPlayId)) {
			Logger.e(TAG, "analyzeVideoEnd,id is null");
			return;
		}
		Logger.d(TAG, "analyzeVideoEnd,run here,mVideoPlayId=" + mVideoPlayId);
		JsonObject params = new JsonObject();
		params.addProperty("id", mVideoPlayId);
		String url=Utils.processUrl(ModuleType.Log, ApiType.LOG_endPlay, params);
		getSystem(SystemHttp.class).get(mContext, url, "analyzeVideoPlayEnd", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				Logger.d(TAG, "analyzeVideoEnd,onSuccess");
			}

			@Override
			public void onFailure(Response resp) {
				Logger.d(TAG, "analyzeVideoEnd,onFailure,msg=" + resp.msg);
			}

			@Override
			public void onComplete() {
				mVideoPlayId = null;
			}
		});
//		String url = processUrl("Log", "endPlay", params);
//		SystemManager.getInstance().getSystem(SystemHttp.class).get(url, "analyzeVideoPlayEnd", false, false, callBack);
//	getHttpRequests().analyzeVideoPlayEnd(params.toString(), new RequestCallBack2() {
//
//		@Override
//		public void onSuccess(String json, boolean fromCache, String msg) {
//			Logger.d(TAG, "analyzeVideoEnd,onSuccess");
//		}
//
//		@Override
//		public void onFailure(String msg, boolean fromCache) {
//			Logger.d(TAG, "analyzeVideoEnd,onFailure,msg=" + msg);
//		}
//
//		@Override
//		public void onComplete() {
//			mVideoPlayId = null;
//		}
//	});
}

	/**
	 * 弹幕开关统计
	 * 
	 * @param vid
	 *            视频id
	 * @param toggle
	 *            1：开,2:关
	 */
	public void analyzeToggleDanmaku(String vid, String toggle) {
		JsonObject params = new JsonObject();
		params.addProperty("id", vid);
		params.addProperty("type", "2");
		params.addProperty("switch", toggle);

		String url = Utils.processUrl(ModuleType.Log, ApiType.LOG_barrageSwitch, params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get(mContext,url, "analyzeVideoPlayEnd", false, false, null);

	}

	/**
	 * 记录是否允许非wifi网络缓存/观看
	 * 
	 * @param type
	 *            1:非wifi下载,2:非wifi观看
	 * @param status
	 *            0：打开,1:关闭
	 */
	public void analyzeToggleMobileDownloadPlay(String type, String status) {
		JsonObject params = new JsonObject();
		params.addProperty("type", type);
		params.addProperty("status", "status");

		String url = Utils.processUrl(ModuleType.Log,ApiType.LOG_wifi, params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get(mContext, url, "analyzeToggleMobileDownloadPlay", false, false, null);

	}

	/**
	 * 统计用户行为
	 * 
	 * @param module
	 * @param area
	 * @param type
	 */
	public void analyzeUserAction(String module, String area, String type) {
		JsonObject params = new JsonObject();
		params.addProperty("module", module);
		params.addProperty("area", area);
		if (!TextUtils.isEmpty(type)) {
			params.addProperty("type", type);
		}
		String url = Utils.processUrl(ModuleType.Log, ApiType.LOG_record, params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get(mContext,url, "analyzeUserAction", false, false, null);
	}

}

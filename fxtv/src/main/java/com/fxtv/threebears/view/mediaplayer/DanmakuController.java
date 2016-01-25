package com.fxtv.threebears.view.mediaplayer;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.fxtv.framework.Logger;

import java.util.HashMap;

import master.flame.danmaku.controller.DrawHandler.Callback;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoadCallBack;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.loader.android.SelfDanmakuLoader;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.SelfDanmakuParser;

public class DanmakuController {
	private static final String TAG = "DanmakuController";

	private Context mContext;
	private IDanmakuView mDanmakuView;
	private DanmakuContext mDanmakuContext;
	private BaseDanmakuParser mParser;
	private IDanmakuListeners mDanmakuListeners;
	private boolean mIsPrepared;

	public DanmakuController(Context context, IDanmakuView danmakuView) {
		this.mContext = context;
		this.mDanmakuView = danmakuView;
		init();
	}

	/**
	 * 设置监听
	 * 
	 * @param listener
	 */
	public void setOnPrepareListener(IDanmakuListeners listener) {
		mDanmakuListeners = listener;
	}

	/**
	 * 加载弹幕
	 * 
	 * @param url
	 */
	public void loadDanmaku(String url) {
		Logger.d(TAG, "loadDanmaku,url=" + url);
		mParser = new SelfDanmakuParser();
		if (TextUtils.isEmpty(url)) {
			mDanmakuView.prepare(mParser, mDanmakuContext);
		} else {
			final ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_SELF);
			if(loader==null) return;
			((SelfDanmakuLoader) loader).load(mContext, url, new ILoadCallBack() {

				@Override
				public void onLoadCallBack(boolean result) {
					Logger.d(TAG, "loadDanmaku,onLoadCallBack result=" + result);
					if (result) {
						IDataSource<?> dataSource = loader.getDataSource();
						mParser.load(dataSource);
					} else {
						if (mDanmakuListeners != null) {
							mDanmakuListeners.onLoadDanmakuError();
						}
					}
					mDanmakuView.prepare(mParser, mDanmakuContext);
				}
			});
		}
	}

	public void hide() {
		Logger.d(TAG, "hide");
		if (isShown()) {
			mDanmakuView.hide();
		}
	}

	public void show() {
		Logger.d(TAG, "show");
		if (!isShown()) {
			mDanmakuView.show();
		}
	}

	public boolean isShown() {
		return mDanmakuView.isShown();
	}

	public void start() {
		Logger.d(TAG, "start");
		if (mIsPrepared) {
			mDanmakuView.start();
		}
	}

	public void resume() {
		Logger.d(TAG, "resume");
		if (mIsPrepared) {
			mDanmakuView.resume();
		}
	}

	public void pause() {
		Logger.d(TAG, "pause");
		if (mIsPrepared) {
			mDanmakuView.pause();
		}
	}

	public void stop() {
		Logger.d(TAG, "stop");
		if (mIsPrepared) {
			mDanmakuView.stop();
		}
	}

	public void release() {
		if (mIsPrepared) {
			mDanmakuView.release();
		}
	}

	public void seekTo(long ms) {
		if (mIsPrepared) {
			mDanmakuView.seekTo(ms);
		}
	}

	/**
	 * 获取弹幕时间轴当前时间
	 * 
	 * @return
	 */
	public long getCurrentTime() {
		return mDanmakuView.getCurrentTime();
	}

	public void addDanmaku(String content) {
		BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
		if (danmaku == null || mDanmakuView == null) {
			return;
		}
		danmaku.text = content;
		danmaku.padding = 5;
		danmaku.priority = 1; // 可能会被各种过滤器过滤并隐藏显示
		danmaku.isLive = false;
		danmaku.time = mDanmakuView.getCurrentTime() + 1200;
		danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
		danmaku.textColor = Color.RED;
		danmaku.textShadowColor = Color.WHITE;
		// danmaku.underlineColor = Color.GREEN;
		danmaku.borderColor = Color.GREEN;
		mDanmakuView.addDanmaku(danmaku);
		Logger.d(TAG, "addDanmaku,content=" + content + ",time=" + danmaku.time);
	}

	private void init() {
		// 设置最大显示行数
		HashMap<Integer,Integer> maxLinesPair = new HashMap<>();
		maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示3行
		// 设置是否禁止重叠
		HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
		overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
		overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
		mDanmakuContext = DanmakuContext.create();
		mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false)
				.setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
				// .setCacheStuffer(new BackgroundCacheStuffer()) //
				// 绘制背景使用BackgroundCacheStuffer
				.setMaximumLines(maxLinesPair).preventOverlapping(overlappingEnablePair);

		mDanmakuView.setCallback(new Callback() {
			@Override
			public void updateTimer(DanmakuTimer timer) {
			}

			@Override
			public void prepared() {
				Logger.d(TAG, "prepared");
				mIsPrepared = true;
				if (mDanmakuListeners != null) {
					Logger.d(TAG, "prepared run here 111");
					mDanmakuListeners.onPrapared();
				}
			}

			@Override
			public void drawingFinished() {
			}
		});

		mDanmakuView.showFPS(false);
		mDanmakuView.enableDanmakuDrawingCache(true);
	}

	public interface IDanmakuListeners {
		public void onPrapared();

		public void onLoadDanmakuError();
	}
}

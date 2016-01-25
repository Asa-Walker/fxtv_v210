package com.fxtv.threebears.system;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemBase;
import com.fxtv.threebears.model.RecentPlayHistory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SystemHistory extends SystemBase {
	private static final String TAG = "SystemHistory";
	private static final String FILE_NAME = "recent_play_history";
	private static final String KEY = "history";
	private SharedPreferences mSharedPreferences;
	public List<RecentPlayHistory> mRecentPlayHistories;

	@Override
	protected void init() {
		super.init();
		mRecentPlayHistories = new ArrayList<RecentPlayHistory>(50);
		mSharedPreferences = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		revertHistory();
	}

	@Override
	protected void destroy() {
		super.destroy();
		mRecentPlayHistories.clear();
		mRecentPlayHistories = null;
	}

	public void updateRecentPlayHistory(RecentPlayHistory recentPlayHistory) {
		for (RecentPlayHistory temp : mRecentPlayHistories) {
			if (temp.vId.equals(recentPlayHistory.vId)) {
				mRecentPlayHistories.remove(temp);
				break;
			}
		}

		mRecentPlayHistories.add(0, recentPlayHistory);

		if (mRecentPlayHistories.size() > 50) {
			mRecentPlayHistories.remove(mRecentPlayHistories.size() - 1);
		}

		updateData();
	}

	/**
	 * 获取单个播放历史
	 * 
	 * @param vid
	 *            视频id
	 * @return
	 */
	public RecentPlayHistory getRecentPlayHistory(String vid) {
		RecentPlayHistory recentPlayHistory = null;
		for (RecentPlayHistory temp : mRecentPlayHistories) {
			if (vid.equals(temp.vId)) {
				recentPlayHistory = temp;
				break;
			}
		}

		return recentPlayHistory;
	}

	/**
	 * 获取所有播放历史
	 * 
	 * @return
	 */
	public List<RecentPlayHistory> getAllRecentPlayHistories() {
		return mRecentPlayHistories;
	}

	/**
	 * 删除视频播放历史
	 * 
	 * @param video
	 */
	public void deleteHistory(RecentPlayHistory recentPlayHistory) {
		mRecentPlayHistories.remove(recentPlayHistory);
		updateData();
	}

	/**
	 * 删除播放历史
	 * 
	 * @param vid
	 *            video id
	 */
	public void deleteHistory(String vid) {
		deleteHistory(getRecentPlayHistory(vid));
	}

	/**
	 * 删除所有视频播放历史
	 * 
	 * @param video
	 */
	public void deleteAllHistory() {
		mRecentPlayHistories.clear();
		updateData();
	}

	/**
	 * 更新本地数据
	 */
	private void updateData() {
		Editor edit = mSharedPreferences.edit();
		String data = "";
		if (mRecentPlayHistories.size() > 0) {
			Gson gson = new Gson();
			data = gson.toJson(mRecentPlayHistories, new TypeToken<List<RecentPlayHistory>>() {
			}.getType());
		}
		edit.putString(KEY, data);
		edit.commit();
	}

	/**
	 * 恢复播放历史数据
	 */
	private void revertHistory() {
		String data = mSharedPreferences.getString(KEY, null);
		if (!TextUtils.isEmpty(data)) {
			Gson gson = new Gson();
			mRecentPlayHistories = gson.fromJson(data, new TypeToken<List<RecentPlayHistory>>() {
			}.getType());
		}
		Logger.d(TAG, "recent play histories size=" + mRecentPlayHistories.size());
	}
}

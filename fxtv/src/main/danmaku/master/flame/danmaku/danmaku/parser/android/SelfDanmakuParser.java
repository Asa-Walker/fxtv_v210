/*
 * Copyright (C) 2013 Chen Hui <calmer91@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package master.flame.danmaku.danmaku.parser.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fxtv.framework.Logger;

import android.graphics.Color;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.util.DanmakuUtils;

public class SelfDanmakuParser extends BaseDanmakuParser {
	private final static String TAG = "SelfDanmakuParser";

	@Override
	public Danmakus parse() {
		if (mDataSource != null && mDataSource instanceof JSONSource) {
			JSONSource jsonSource = (JSONSource) mDataSource;
			return doParse(jsonSource.data());
		}
		return new Danmakus();
	}

	/**
	 * @param danmakuListData
	 *            弹幕数据 传入的数组内包含普通弹幕，会员弹幕，锁定弹幕。
	 * @return 转换后的Danmakus
	 */
	private Danmakus doParse(JSONArray danmakuListData) {
		Danmakus danmakus = new Danmakus();
		if (danmakuListData == null || danmakuListData.length() == 0) {
			return danmakus;
		}
		for (int i = 0; i < danmakuListData.length(); i++) {
			try {
				JSONObject danmakuArray = danmakuListData.getJSONObject(i);
				if (danmakuArray != null) {
					danmakus = _parse(danmakuArray, danmakus);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return danmakus;
	}

	private Danmakus _parse(JSONObject jsonObject, Danmakus danmakus) {
		if (danmakus == null) {
			danmakus = new Danmakus();
		}
		if (jsonObject == null || jsonObject.length() == 0) {
			return danmakus;
		}

		int i = 0;
		try {
			long time = (long)Double.parseDouble(jsonObject.getString("node_time")) * 1000;
			// int type = jsonObject.getInt("style");
			// int size = jsonObject.getInt("size");
			// int color = jsonObject.getInt("color");
			String content = jsonObject.getString("content");
			int type = 1;
			int size = 25;
			Logger.d(TAG, "_parse, content=" + content + ",time=" + time);
			BaseDanmaku item = mContext.mDanmakuFactory.createDanmaku(type, mContext);
			item.time = time;
			item.textSize = size * (mDispDensity - 0.6f);
			item.textColor = Color.WHITE;
			item.textShadowColor = Color.WHITE;
			DanmakuUtils.fillText(item, content);
			item.index = i++;
			item.setTimer(mTimer);
			danmakus.addItem(item);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return danmakus;
	}
}

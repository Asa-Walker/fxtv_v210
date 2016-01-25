package com.fxtv.threebears.activity.game;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragmentActivity;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.pagerindicator.TabPageIndicator;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.search.ActivitySearch;
import com.fxtv.threebears.activity.user.download.ActivityNewMyCache;
import com.fxtv.threebears.adapter.TabPageIndicatorAdapter;
import com.fxtv.threebears.fragment.module.game.FragmentTabGameModel;
import com.fxtv.threebears.model.Game;
import com.fxtv.threebears.model.GameTab;
import com.fxtv.threebears.system.SystemAnalyze;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author FXTV-Android 游戏详情界面
 * 
 */
public class ActivityGame extends BaseFragmentActivity {
	private Game mGame;
	private ArrayList<Fragment> mFragmentList;
	private List<GameTab> mGameTabs;
	private ViewPager mViewPager;
	private String mGameId, mGameTitle, mGameType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		mGameId = getStringExtra("game_id");
		mGameTitle = getStringExtra("game_name");
		mGameType = getStringExtra("game_type");
		handleIntent();
		initActionBar();
		getData();
	}

	private void handleIntent() {
		JSONObject jsonObject = getSystem(SystemCommon.class).getH5Content(this);
		if (jsonObject != null) {
			mGame = new Game();
			try {
				String name = jsonObject.getString("game_name");
				String id = jsonObject.getString("game_id");
				if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(id)) {
					mGame.title = URLDecoder.decode(name, "UTF-8");
					mGame.id = id;
				}
			} catch (Exception e) {
				e.printStackTrace();
				showToast("H5内链跳转失败");
			}
		} else {
			mGame = (Game) getSerializable("game");
		}
		if (mGameId != null && !"".equals(mGameId)) {
			getSystem(SystemAnalyze.class).analyzeUserAction("game", mGameId, "");
		} else {
			try {
				getSystem(SystemAnalyze.class).analyzeUserAction("game", mGame.id, "");
			} catch (Exception e) {
			}
		}
	}

	private void getData() {
		if (mGameTabs == null) {
			mGameTabs = new ArrayList<GameTab>();
			mFragmentList = new ArrayList<Fragment>();
		}
		JsonObject params = new JsonObject();
		try {
			params.addProperty("id", mGame.id);
		} catch (Exception e) {
			if (mGameId != null && !"".equals(mGameId)) {
				params.addProperty("id", mGameId);
			} else {
				return;
			}
		}
		Utils.showProgressDialog(this);

		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.GAME, ApiType.GAME_menu, params),"gameGameMenusApi", true, true, new RequestCallBack<List<GameTab>>() {

			@Override
			public void onSuccess(List<GameTab> data, Response resp) {
				if (data != null && data.size() != 0) {
					mGameTabs = data;
					initView();
				}
			}

			@Override
			public void onFailure(Response resp) {
				showToast(resp.msg);
			}

			@Override
			public void onComplete() {

			}
		});

	}

	private void initView() {
		for (int i = 0; i < mGameTabs.size(); i++) {
			FragmentTabGameModel model = new FragmentTabGameModel();
			mFragmentList.add(model);
		}
		mViewPager = (ViewPager) findViewById(R.id.activity_game_vp);
		mViewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.fragment_tab_main_tab);
		indicator.setViewPager(mViewPager);
		//mViewPager.setOffscreenPageLimit(0);
	}

	private void initActionBar() {
		try {
			((TextView) findViewById(R.id.ab_title)).setText(mGame.title);
		} catch (Exception e) {
			if (mGameTitle != null && !"".equals(mGameTitle)) {
				((TextView) findViewById(R.id.ab_title)).setText(mGameTitle);
			} else {
				((TextView) findViewById(R.id.ab_title)).setText("标题");
			}
		}
		// ((TextView) findViewById(R.id.ab_title)).setText(mGame.title);
		ImageView backBtn = (ImageView) findViewById(R.id.ab_left_img);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ImageView seach = (ImageView) findViewById(R.id.ab_right_img1);
		seach.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FrameworkUtils.skipActivity(ActivityGame.this, ActivitySearch.class);
			}
		});
		ImageView down = (ImageView) findViewById(R.id.ab_right_img2);
		down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FrameworkUtils.skipActivity(ActivityGame.this, ActivityNewMyCache.class);
			}
		});
	}

	public class MyViewPagerAdapter extends TabPageIndicatorAdapter {
		public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			// 将Tab ID 传给 fragment，并以此获取数据
			Bundle bundle = new Bundle();
			if (mGame == null) {
				mGame = new Game();
				mGame.game_type = mGameType;
			}
			bundle.putSerializable("game", mGame);
			bundle.putString("menu_id", mGameTabs.get(arg0).id);
			bundle.putString("menu_type", mGameTabs.get(arg0).type);
			Fragment fragment = mFragmentList.get(arg0);
			fragment.setArguments(bundle);
			return fragment;
		}
		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		@Override
		public int getItemType(int position) {
			if (TextUtils.isEmpty(mGame.game_type)) {
				return 0;
			} else {
				return Integer.parseInt("1" + mGame.game_type);
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mGameTabs.get(position).title;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mGame = null;
		mGameTabs = null;
		mViewPager = null;
		FrameworkUtils.setEmptyList(mFragmentList);
	}
}

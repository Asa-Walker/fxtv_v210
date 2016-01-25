package com.fxtv.threebears.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.pagerindicator.TabPageIndicator;
import com.fxtv.threebears.R;
import com.fxtv.threebears.adapter.TabPageIndicatorAdapter;
import com.fxtv.threebears.fragment.module.main.FragmentTabMainFirst;
import com.fxtv.threebears.model.Game;
import com.fxtv.threebears.system.SystemAnalyze;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.MyPopuWindow;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FragmentTabMain extends BaseFragment{
	private static final String TAG = "FragmentTabMain";

	private ViewPager mViewPager;
	private ViewPageAdapter mAdapter;
	private MyPopuWindow mPop;
	private TabPageIndicator mIndicator;
	private BroadcastReceiver mReceiver;
	private List<Game> mShouldShowGameMenus;
	private List<Game> mGameMenus;
	private Game mFirstGame;//每日精选

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_tab_main, container, false);
		mShouldShowGameMenus = new ArrayList<Game>();
		mGameMenus = new ArrayList<Game>();

		initBroadcastReceiver();
		updateGameMenus(true);

		Logger.d("TAG", "FragmentTabMain onCreateView ==");
		return mRoot;
	}

	private void initBroadcastReceiver() {
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (SystemUser.ACTION_LOGIN.equals(action) || SystemUser.ACTION_LOGOUT.equals(action)) {
					updateGameMenus(false);
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(SystemUser.ACTION_LOGIN); // 只有持有相同的action的接受者才能接收此广播
		filter.addAction(SystemUser.ACTION_LOGOUT); // 只有持有相同的action的接受者才能接收此广播
		getActivity().registerReceiver(mReceiver, filter);
	}

	public void callUpdate(List<Game> gameMenus) {
		mShouldShowGameMenus.clear();
		mGameMenus.clear();
		for (Game tmp : gameMenus) {
			if (tmp.status.equals("1")) {
				mShouldShowGameMenus.add(tmp);
			}
			mGameMenus.add(tmp);
		}
		if(mFirstGame!=null && !mShouldShowGameMenus.contains(mFirstGame))
			mShouldShowGameMenus.add(0,mFirstGame);
		notifyDataSetChanged();
	}

	private void notifyDataSetChanged() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		if (mIndicator != null) {
			mIndicator.notifyDataSetChanged();
		}
	}

	/**
	 * 登录时，下拉刷新call it 未登录时，1、进入此界面 call it, 2、下拉刷新 call it.
	 * 
	 */
	private void updateGameMenus(boolean useCache) {
		JsonObject params = new JsonObject();
		Utils.showProgressDialog(getActivity());
		getSystem(SystemHttp.class).get(getActivity(),
				Utils.processUrl(ModuleType.INDEX, ApiType.INDEX_menu, params),
				useCache,
				true,
				new RequestCallBack<List<Game>>() {
					@Override
					public void onSuccess(List<Game> data, Response resp) {
						mGameMenus = data;
						if (!FrameworkUtils.isListEmpty(mGameMenus)) {
							mShouldShowGameMenus.clear();
							for (Game tmp : mGameMenus) {
								if (tmp.status.equals("1")) {
									mShouldShowGameMenus.add(tmp);
								}
							}
							mFirstGame = mGameMenus.remove(0);//删除第一个每日精选
						}

						initView();
						getSystem(SystemCommon.class).checkVersion(getActivity(), false);
					}

					@Override
					public void onFailure(Response resp) {
						FrameworkUtils.showToast(getActivity(), resp.msg);
					}

					@Override
					public void onComplete() {
						notifyDataSetChanged();
					}

				});

	}

	private void initView() {
		if(mViewPager==null){
			mViewPager = (ViewPager) mRoot.findViewById(R.id.fragment_tab_main_vp);
			mIndicator = (TabPageIndicator) mRoot.findViewById(R.id.fragment_tab_main_tab);
		}
		if (mAdapter == null) {
			mAdapter = new ViewPageAdapter(getChildFragmentManager());
			mViewPager.setAdapter(mAdapter);
			mViewPager.setOffscreenPageLimit(0);
			mIndicator.setViewPager(mViewPager);
		}
		mIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int i) {
				// 数据统计，点击才统计
				if (mShouldShowGameMenus != null && i < mShouldShowGameMenus.size()) {
					getSystem(SystemAnalyze.class).analyzeUserAction("index", mShouldShowGameMenus.get(i).id, null);
				}
			}

		});
		initPop();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		Logger.d(TAG, "main onHiddenChanged = " + hidden);
		if (!hidden) {
			// 数据统计
			getSystem(SystemAnalyze.class).analyzeUserAction("main_menu", "1", null);
		}
	}


	private void initPop() {

		final View mActionBarLayout = getActivity().findViewById(R.id.activity_main_actonbar);
		final View viewPulldown=mRoot.findViewById(R.id.fragment_tab_main_pulldown_imageview);
		viewPulldown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!getSystem(SystemUser.class).isLogin()) {
					getSystem(SystemCommon.class).noticeAndLogin(getActivity());
				} else {
					if (mPop == null) {
						mPop = new MyPopuWindow(getActivity(), FragmentTabMain.this, mGameMenus);
					} else {
						mPop.setGameMenus(mGameMenus);
					}
					if (mPop.isShowing()) {
						mPop.dismiss();
					} else {
						mPop.showAsDropDown(mActionBarLayout);
					}


				}
			}
		});
	}



	public class ViewPageAdapter extends TabPageIndicatorAdapter {

		public ViewPageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment= new FragmentTabMainFirst();
			Bundle bundle = new Bundle();
			bundle.putString("game_id", mShouldShowGameMenus.get(position).id);
			fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public int getCount() {
			return mShouldShowGameMenus == null ? 0:mShouldShowGameMenus.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;//deach所有Fragment
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mShouldShowGameMenus.get(position).title;
		}

	}

	@Override
	public void onDestroyView() {
		mViewPager = null;
		mAdapter = null;
		mPop = null;
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// 解决 exception:activity has destroy.
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	public void setCurrentItem(int currentItem){
		if(mIndicator!=null)
			mIndicator.setCurrentItem(currentItem);
	}
}

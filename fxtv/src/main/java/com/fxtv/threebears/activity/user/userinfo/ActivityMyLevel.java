package com.fxtv.threebears.activity.user.userinfo;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.framework.widget.xlistview.XListView.IXListViewListener;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.h5.ActivityWebView;
import com.fxtv.threebears.model.Mission;
import com.fxtv.threebears.model.User;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 我的等级界面
 * 
 * @author Administrator
 * 
 */
public class ActivityMyLevel extends BaseActivity {
	private XListView mListView;
	//private List<Mission> mList;
	private MyAdapter mAdapter;
	private int mPageNum;
	private PopupWindow mPop;
	private View mPopLayout;
	private final int mPageSize = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_level);
		initView();
		getData();
	}

	// 友盟统计
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void getData() {
		mPageNum++;
		JsonObject params = new JsonObject();
		params.addProperty("type", "1");
		params.addProperty("page", mPageNum++);
		params.addProperty("pagesize", mPageSize + "");
		Utils.showProgressDialog(this);

		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_tasksDetail, params), "getTaskDetails", false, false, new RequestCallBack<List<Mission>>() {
			@Override
			public void onSuccess(List<Mission> data, Response resp) {
				if (data != null && data.size() != 0) {
					mAdapter.addData(data);
				} else {
					FrameworkUtils.showToast(ActivityMyLevel.this, "没有更多数据");
					mListView.noMoreData();
				}
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActivityMyLevel.this, resp.msg);
			}

			@Override
			public void onComplete() {
				mListView.stopLoadMore();
				Utils.dismissProgressDialog();
			}
		});

	}

	private void initView() {
		initActionBar();
		ImageView img = (ImageView) findViewById(R.id.activity_my_level_user_pic);
		/*SystemManager.getInstance().getSystem(SystemImageLoader.class)
				.displayImageDefault(getSystem(SystemUser.class).mUser.image, img);*/
		getSystem(SystemCommon.class).displayDefaultImage(ActivityMyLevel.this, img, getSystem(SystemUser.class).mUser.image);

		getLevel();
		initListView();
	}

	private void getLevel() {
		final TextView nextLevel = (TextView) findViewById(R.id.activity_my_level_next_level);
		final TextView level = (TextView) findViewById(R.id.activity_my_level);
		JsonObject params = new JsonObject();
		// params.addProperty("user_id",
		// getSystem(SystemUser.class).mUser.user_id);
		Utils.showProgressDialog(this);
		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_userInfo, params), "getMyLevel", false, false, new RequestCallBack<User>() {
			@Override
			public void onSuccess(User data, Response resp) {
				if (data != null) {
					getSystem(SystemUser.class).mUser.exp = data.exp;
					getSystem(SystemUser.class).mUser.level = data.level;
					getSystem(SystemUser.class).mUser.upgrade_exp = data.upgrade_exp;
				}
				level.setText("LV" + getSystem(SystemUser.class).mUser.level);
				nextLevel.setText(Html.fromHtml("<font color=\'#616161\'>" + "还有"
						+ "</font><font color=\'#26a9e1\'>"
						+ getSystem(SystemUser.class).mUser.upgrade_exp
						+ "</font><font color=\'#616161\'>点经验升到下一级</font>"));
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActivityMyLevel.this, resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

	}

	private void initListView() {
		mListView = (XListView) findViewById(R.id.activity_my_level_lv);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(true);
		mListView.setPageSize(mPageSize);
		mAdapter = new MyAdapter(null);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
			}

			@Override
			public void onLoadMore() {
				getData();
			}
		});
	}

	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		TextView myBiscuit = (TextView) findViewById(R.id.ab_right_tv);
		title.setText("我的等级");
		myBiscuit.setText("详细说明");
		myBiscuit.setCompoundDrawables(null, null, null, null);
		myBiscuit.setPadding(0, 0, 20, 0);
		ImageView btnBack = (ImageView) findViewById(R.id.ab_left_img);
		btnBack.setImageResource(R.drawable.icon_arrow_left1);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		myBiscuit.setVisibility(View.VISIBLE);
		myBiscuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("url", "http://www.feixiong.tv/sm/djsm.html");
				bundle.putString("title", "等级说明");
				bundle.putBoolean("share_enable", false);
				FrameworkUtils.skipActivity(ActivityMyLevel.this, ActivityWebView.class, bundle);
			}
		});
	}

	class MyAdapter extends BaseListGridAdapter<Mission> {

		public MyAdapter(List<Mission> listData) {
			super(listData);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				convertView = View.inflate(ActivityMyLevel.this, R.layout.item_my_biscuit_level, null);
				holder = new Holder();
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.result = (TextView) convertView.findViewById(R.id.result);
				holder.publishTime = (TextView) convertView.findViewById(R.id.publish_time);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			Mission mis = getItem(position);
			if (mis != null) {
				holder.title.setText(mis.title);
				holder.result.setText(mis.result);
				holder.publishTime.setText(mis.create_time);
			}
			return convertView;
		}

		class Holder {
			TextView title;
			TextView result;
			TextView publishTime;
		}
	}
}

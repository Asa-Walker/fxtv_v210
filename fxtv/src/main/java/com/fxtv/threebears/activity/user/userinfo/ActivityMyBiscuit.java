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

public class ActivityMyBiscuit extends BaseActivity {
	private XListView mListView;
	//private List<Mission> mList;
	private int mPageNum;
	private MyAdapter mAdapter;
	private PopupWindow mpop;
	private View mPopLayout;
	private final int mPageSize = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_biscuit);
		initView();
		getData(true);
	}

	private void getData(final boolean isRefresh) {
		mPageNum++;
		JsonObject params = new JsonObject();
		params.addProperty("type", "2");
		params.addProperty("page", mPageNum++);
		params.addProperty("pagesize", mPageSize + "");
		Utils.showProgressDialog(this);
		/*String url = processUrl("User", "tasksDetail", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, urlcallBack);*/
		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_tasksDetail, params), "getTaskDetails", false, false, new RequestCallBack<List<Mission>>() {
			@Override
			public void onSuccess(List<Mission> data, Response resp) {
				if (data != null && data.size() != 0) {
					if (isRefresh) {
						mAdapter.setListData(data);
					} else {
						mAdapter.addData(data);
					}
				} else {
					FrameworkUtils.showToast(ActivityMyBiscuit.this, "没有更多数据");
					mListView.stopRefresh();
					mListView.noMoreData();
				}
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActivityMyBiscuit.this, resp.msg);
			}

			@Override
			public void onComplete() {
				mListView.stopLoadMore();
				mListView.stopRefresh();
				Utils.dismissProgressDialog();
			}
		});

	}

	private void initView() {
		initActionBar();
		ImageView img = (ImageView) findViewById(R.id.activity_my_biscuit_user_pic);
		/*SystemManager.getInstance().getSystem(SystemImageLoader.class)
				.displayImageDefault(getSystem(SystemUser.class).mUser.image, img);*/

		getSystem(SystemCommon.class).displayDefaultImage(ActivityMyBiscuit.this, img, getSystem(SystemUser.class).mUser.image);
		getBiscuit();
		initListView();
	}

	private void initListView() {
		mListView = (XListView) findViewById(R.id.activity_my_biscuit_lv);
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(true);
		mListView.setPageSize(mPageSize);
		mListView.setEmptyText(getString(R.string.empty_str_biscuit));
		mListView.setEmptyDrawable(R.drawable.empty_cookie);
		mAdapter = new MyAdapter(null);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				mPageNum=0;
				getData(true);
			}

			@Override
			public void onLoadMore() {
				getData(false);
			}
		});
	}

	private void getBiscuit() {
		final TextView biscuit = (TextView) findViewById(R.id.activity_my_biscuit);
		JsonObject params = new JsonObject();
		// params.addProperty("user_id",
		// getSystem(SystemUser.class).mUser.user_id);
		Utils.showProgressDialog(this);

		/*String url = processUrl("User", "userInfo", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url callBack);
		*/
		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_userInfo, params), "getMyLevel", false, false, new RequestCallBack<User>() {
			@Override
			public void onSuccess(User data, Response resp) {
				if (data != null) {
					getSystem(SystemUser.class).mUser.currency = data.currency;
				}
				biscuit.setText(Html.fromHtml("<font color=\'#616161\'>" + "共"
						+ "</font><font color=\'#26a9e1\'>"
						+ getSystem(SystemUser.class).mUser.currency
						+ "</font><font color=\'#616161\'>个饼干</font>"));
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActivityMyBiscuit.this, resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

	}

	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		TextView myBiscuit = (TextView) findViewById(R.id.ab_right_tv);
		title.setText("我的饼干");
		myBiscuit.setText("饼干说明");
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
				bundle.putString("url", "http://www.feixiong.tv/sm/bgsm.html");
				bundle.putString("title", "饼干说明");
				bundle.putBoolean("share_enable", false);
				FrameworkUtils.skipActivity(ActivityMyBiscuit.this, ActivityWebView.class, bundle);
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
				convertView = View.inflate(ActivityMyBiscuit.this, R.layout.item_my_biscuit_level, null);
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

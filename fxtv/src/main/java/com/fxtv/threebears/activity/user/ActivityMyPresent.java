package com.fxtv.threebears.activity.user;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Present;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 我的抽奖记录
 * 
 * @author FXTV-Android
 * 
 */
public class ActivityMyPresent extends BaseActivity {
	private XListView mListView;
	//private List<Present> mList;
	private Resources mResources;
	private MyAdapter mAdapter;
	private PopupWindow mPopupWindow;
	private View mPop;
	private int mPageNum;
	private int mPageSize = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_biscuit);
		mResources = getResources();
		initView();
		getData(false, true);
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

	private void getData(final boolean refresh, boolean showDialog) {
		if (refresh) {
			mPageNum = 1;
		} else {
			mPageNum++;
		}
		JsonObject params = new JsonObject();
		params.addProperty("page", mPageNum + "");
		params.addProperty("pagesize", 20 + "");
		if (showDialog) {
			Utils.showProgressDialog(this);
		}
		/*String url = processUrl("User", "myLottery", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, urlcallBack);
		*/
		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_myLottery, params), "getMyLottery", false, false, new RequestCallBack<List<Present>>() {
			@Override
			public void onSuccess(List<Present> data, Response resp) {
				if (data != null && data.size() != 0) {
					if (refresh) {
						mAdapter.setListData(data);
					} else {
						mAdapter.addData(data);
					}
				} else {
					FrameworkUtils.showToast(ActivityMyPresent.this, getString(R.string.notice_no_more_data));
					mListView.noMoreData();
				}
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActivityMyPresent.this, resp.msg);
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
		removeView();
		initActionbar();
		initListView();
	}

	private void initListView() {
		mListView = (XListView) findViewById(R.id.activity_my_biscuit_lv);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(true);
		mListView.setEmptyText(getString(R.string.empty_str_present));
		mListView.setEmptyDrawable(R.drawable.empty_lottery);
		mListView.setPageSize(mPageSize);
		mAdapter = new MyAdapter(null);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				mListView.setRefreshTime("刚刚");
				mListView.setPullLoadEnable(true);
				getData(true, false);
			}

			@Override
			public void onLoadMore() {
				getData(false, false);
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position >= 1) {
					Bundle bundle = new Bundle();
					bundle.putString("video_id",mAdapter.getItem(position - 1).id);
					FrameworkUtils.skipActivity(ActivityMyPresent.this, ActivityVideoPlay.class, bundle);
				}
			}
		});
	}

	private void removeView() {
		findViewById(R.id.my_biscuit_linear).setVisibility(View.GONE);
	}

	private void initActionbar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		TextView myBiscuit = (TextView) findViewById(R.id.ab_right_tv);
		title.setText("我的抽奖");
		myBiscuit.setText("抽奖说明");
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
				bundle.putString("url", "http://www.feixiong.tv/sm/choujiang.html");
				bundle.putString("title", "抽奖说明");
				bundle.putBoolean("share_enable", false);
				FrameworkUtils.skipActivity(ActivityMyPresent.this, ActivityWebView.class, bundle);
			}
		});
	}

	class MyAdapter extends BaseListGridAdapter<Present> {
		ColorStateList stateList1 = mResources.getColorStateList(R.color.text_color_default);
		ColorStateList stateList2 = mResources.getColorStateList(R.color.color_red);

		public MyAdapter(List<Present> listData) {
			super(listData);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				convertView = View.inflate(ActivityMyPresent.this, R.layout.item_presnet, null);
				holder = new Holder();
				holder.tilte = (TextView) convertView.findViewById(R.id.title);
				holder.presentDetail = (TextView) convertView.findViewById(R.id.present_detail);
				holder.presentTime = (TextView) convertView.findViewById(R.id.present_time);
				holder.getPresent = (TextView) convertView.findViewById(R.id.get_present);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			final Present present = getItem(position);
			holder.tilte.setText(present.title);
			holder.presentDetail.setText(present.prize);
			holder.presentTime.setText(present.start_time);
			holder.getPresent.setText(present.lottery_status);
			if (present.lottery_status.equals("1")) {
				holder.getPresent.setText("恭喜中奖");
				holder.getPresent.setTextColor(stateList2);
			} else if (present.lottery_status.equals("0")) {
				holder.getPresent.setText("没有中奖");
				holder.getPresent.setTextColor(stateList1);
			} else {
				holder.getPresent.setText("");
			}
			return convertView;
		}

		class Holder {
			TextView tilte;
			TextView presentTime;
			TextView presentDetail;
			TextView getPresent;
		}
	}
}

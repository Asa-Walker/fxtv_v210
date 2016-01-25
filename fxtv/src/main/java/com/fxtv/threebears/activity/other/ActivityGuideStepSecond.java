package com.fxtv.threebears.activity.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.MyGridView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.Game;
import com.fxtv.threebears.model.Guide;
import com.fxtv.threebears.model.Order;
import com.fxtv.threebears.model.Subscribe;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityGuideStepSecond extends BaseActivity {
	private ListView mListView;
	private List<Subscribe> mList;
	private List<Guide> mTempList;
	private int mCount = 0;
	private MyAdapter mAdapter;
	private Button mSubscribe;
	private StringBuffer mStr = new StringBuffer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide_second);
		List<Game> list = (List<Game>) getIntent().getSerializableExtra("list");
		initView();
		getData(list);
	}

	private void getData(List<Game> list) {
		if (mTempList == null) {
			mTempList = new ArrayList<Guide>();
		}
		if (mTempList != null && mTempList.size() != 0) {
			mTempList.clear();
		}
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).status.equals("1")) {
				Guide guide = new Guide();
				guide.gid = list.get(i).id;
				guide.game_name = list.get(i).title;
				mTempList.add(guide);
			}
		}
		JsonObject params = new JsonObject();
		JsonArray array = new JsonArray();
		for (Guide guide : mTempList) {
			JsonObject obj = new JsonObject();
			obj.addProperty("id", guide.gid);
			obj.addProperty("title", guide.game_name);
			array.add(obj);
		}
		params.add("game", array);
		Utils.showProgressDialog(this);
		/*String url = processUrl("User", "guideOrderList", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url, "guideSecondStep", false, false, callBack);*/
		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_guideOrderList,params),false,false,new RequestCallBack<List<Subscribe>>(){

			@Override
			public void onSuccess(List<Subscribe> data, Response resp) {
				if (data != null && data.size() != 0) {
					mList = data;
				}
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActivityGuideStepSecond.this, resp.msg);
			}

			@Override
			public void onComplete() {
				firstCheck();
				Utils.dismissProgressDialog();
			}
		});

	}

	protected void firstCheck() {
		for (int i = 0; i < mList.size(); i++) {
			for (int j = 0; j < mList.get(i).order_list.size(); j++) {
				if (mList.get(i).order_list.get(j).status.equals("1")) {
					mList.get(i).order_list.get(j).is_selected = R.drawable.choose_icon;
					mCount++;
				} else {
					mList.get(i).order_list.get(j).is_selected = R.color.touming;
				}
			}
		}
		if (mAdapter == null) {
			mAdapter = new MyAdapter();
		}
		mAdapter.notifyDataSetChanged();
		checkButton();
	}

	private void initView() {
		mSubscribe = (Button) findViewById(R.id.activity_guide_second_subscribe);
		// 上一步
		findViewById(R.id.activity_guide_first_skip).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// FrameworkUtils.skipActivity(ActivityGuideStepSecond.this,
				// ActivityGuideStepFirst.class);
				finish();
			}
		});
		// 一键订阅
		mSubscribe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				oneKeySubscribe();
			}
		});
		initListView();
	}

	protected void oneKeySubscribe() {
		JsonObject params = new JsonObject();
		params.addProperty("user_id", getSystem(SystemUser.class).mUser.user_id);
		JsonArray array = new JsonArray();
		for (Subscribe subscribe : mList) {
			for (Order order : subscribe.order_list) {
				if (order.status.equals("1")) {
					JsonObject obj = new JsonObject();
					obj.addProperty("id", order.id);
					obj.addProperty("type", order.type);
					obj.addProperty("status", order.status);
					array.add(obj);
				}
			}
		}
		params.add("order", array);
		Utils.showProgressDialog(ActivityGuideStepSecond.this);
	/*	String url = processUrl("User", "order", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url, "", false, false, callBack);*/
		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_order,params), "orderOrUnOrder", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				showToast(resp.msg);
				// 同步用户数据
				// ApplicationSystemManager.getInstance()
				// .getSystem(SystemUser.class).syncUserData();
				Intent intent = new Intent();
				ActivityGuideStepSecond.this.setResult(20, intent);
				finish();
			}

			@Override
			public void onFailure(Response resp) {
				showToast(resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

	}

	private void initListView() {
		mListView = (ListView) findViewById(R.id.activity_guide_second_listview);
		initData();
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
	}

	private void initData() {
		mList = new ArrayList<Subscribe>();
	}

	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList == null ? 0 : mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				convertView = View.inflate(ActivityGuideStepSecond.this, R.layout.item_guide_second, null);
				holder = new Holder();
				holder.mGridView = (MyGridView) convertView.findViewById(R.id.item_gridview);
				holder.textView = (TextView) convertView.findViewById(R.id.item_title);
				holder.childAdapter = new ChildAdapter();
				holder.mGridView.setAdapter(holder.childAdapter);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			final Subscribe temp = mList.get(position);
			holder.childAdapter.setList(temp.order_list);
			final ChildAdapter tempAdapter = holder.childAdapter;
			holder.textView.setText(temp.game_title);
			holder.mGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Boolean flag = true;
					if (mStr.toString().contains(temp.order_list.get(position).name)) {
						flag = false;
					}
					if (!flag && temp.order_list.get(position).is_selected != R.drawable.icon_choose) {
						FrameworkUtils.showToast(ActivityGuideStepSecond.this, "不能重复订阅");
					}
					if (!flag && temp.order_list.get(position).is_selected == R.drawable.icon_choose) {
						temp.order_list.get(position).is_selected = R.color.touming;
						temp.order_list.get(position).status = "0";
						String string = mStr.toString().replace(temp.order_list.get(position).name, "");
						mStr = new StringBuffer(string);
						mCount--;
					}
					if (flag && temp.order_list.get(position).is_selected != R.drawable.icon_choose) {
						temp.order_list.get(position).is_selected = R.drawable.icon_choose;
						temp.order_list.get(position).status = "1";
						mStr.append(temp.order_list.get(position).name);
						mCount++;
					}
					checkButton();
					tempAdapter.notifyDataSetChanged();
					// if (temp.order_list.get(position).is_selected ==
					// R.drawable.icon_choose) {
					// temp.order_list.get(position).is_selected =
					// R.color.touming;
					// temp.order_list.get(position).order_status = "0";
					// mCount--;
					// } else {
					// temp.order_list.get(position).is_selected =
					// R.drawable.icon_choose;
					// temp.order_list.get(position).order_status = "1";
					// mCount++;
					// }
					// checkButton();
					// tempAdapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}

		class Holder {
			MyGridView mGridView;
			TextView textView;
			ChildAdapter childAdapter;
		}
	}

	public void checkButton() {
		if (mCount <= 0) {
			mSubscribe.setClickable(false);
			mSubscribe.setBackgroundResource(R.color.color_line);
		} else {
			mSubscribe.setClickable(true);
			mSubscribe.setBackgroundResource(R.color.color_orange);
		}
	}

	class ChildAdapter extends BaseAdapter {
		private List<Order> mChildList;

		public void setList(List<Order> list) {
			mChildList = list;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mChildList == null ? 0 : mChildList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				convertView = View.inflate(ActivityGuideStepSecond.this,
						R.layout.item_fragment_anchor_space_message_gridview, null);
				holder = new Holder();
				holder.image = (ImageView) convertView.findViewById(R.id.item_fragment_anchor_space_message_image);
				holder.anchorName = (TextView) convertView.findViewById(R.id.item_fragment_anchor_space_message_anchor);
				holder.isSelected = (ImageView) convertView.findViewById(R.id.is_selected);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			getSystem(SystemCommon.class).displayDefaultImage(ActivityGuideStepSecond.this, holder.image,mChildList.get(position).image);
			holder.anchorName.setText(mChildList.get(position).name);
			holder.isSelected.setBackgroundResource(mChildList.get(position).is_selected);
			return convertView;
		}

		class Holder {
			ImageView image;
			ImageView isSelected;
			TextView anchorName;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mTempList.clear();
	}
}

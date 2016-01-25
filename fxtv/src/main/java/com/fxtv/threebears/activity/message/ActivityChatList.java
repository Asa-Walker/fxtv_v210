package com.fxtv.threebears.activity.message;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.framework.widget.xlistview.XListView.IXListViewListener;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.MyMessageItem;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 我的消息界面
 * 
 * @author FXTV-Android
 * 
 */
public class ActivityChatList extends BaseActivity {
	private XListView mListView;
	private MyAdapter mAdapter;
	//private List<MyMessageItem> mDataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_list);
		initView();
		getData(false);
	}

	private void getData(boolean fromRefresh) {
		if (!fromRefresh) {
			Utils.showProgressDialog(this);
		}

		/*String url = processUrl("User", "dialogList", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url, "dialogListApi", false, false, callBack);*/

		SystemManager.getInstance().getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_dialogList,new JsonObject()),"dialogListApi", false, false, new RequestCallBack<List<MyMessageItem>>() {
			@Override
			public void onSuccess(List<MyMessageItem> data, Response resp) {
				if (data != null) {
					mAdapter.setListData(data);
				}
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActivityChatList.this, resp.msg);
			}

			@Override
			public void onComplete() {
				mListView.stopRefresh();
				mListView.setEmptyText("暂无消息");
				Utils.dismissProgressDialog();
			}
		});

	}

	private void initView() {
		initActionbar();
		mListView = (XListView) findViewById(R.id.activity_messagelist);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		if (mAdapter == null) {
			mAdapter = new MyAdapter(null);
		}
		mListView.setAdapter(mAdapter);
		mListView.setEmptyText("");
		mListView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				getData(true);
			}

			@Override
			public void onLoadMore() {
				mListView.stopLoadMore();
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				view.findViewById(R.id.circle).setVisibility(View.GONE);
				Bundle bundle = new Bundle();
				bundle.putSerializable("item", mAdapter.getItem(position - 1));
				FrameworkUtils.skipActivity(ActivityChatList.this, ActivityChat.class, bundle);
			}
		});
	}

	private void initActionbar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		title.setText("我的消息");
		ImageView btnBack = (ImageView) findViewById(R.id.ab_left_img);
		btnBack.setImageResource(R.drawable.icon_arrow_left1);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	class MyAdapter extends BaseListGridAdapter<MyMessageItem> {

		public MyAdapter(List<MyMessageItem> listData) {
			super(listData);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.item_my_message, null);
				viewHolder = new ViewHolder();
				viewHolder.img = (ImageView) convertView
						.findViewById(R.id.item_my_message_imageview);
				viewHolder.nameTextView = (TextView) convertView
						.findViewById(R.id.item_my_message_name);
				viewHolder.date = (TextView) convertView.findViewById(R.id.item_my_message_date);
				viewHolder.circleImage = (ImageView) convertView.findViewById(R.id.circle);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			MyMessageItem item = getItem(position);
			viewHolder.nameTextView.setText(item.nickname);
			viewHolder.date.setText(item.last_time);
			/*SystemManager.getInstance().getSystem(SystemImageLoader.class)
					.displayImageSquare(item.image, viewHolder.img);*/

			getSystem(SystemCommon.class).displayDefaultImage(ActivityChatList.this, viewHolder.img, item.image, SystemCommon.SQUARE);
			if (item.readed.equals("1")) {
				viewHolder.circleImage.setVisibility(ImageView.INVISIBLE);
			} else {
				viewHolder.circleImage.setVisibility(ImageView.VISIBLE);
			}
			return convertView;
		}

		class ViewHolder {
			ImageView img;
			TextView nameTextView;
			TextView date;
			ImageView circleImage;
		}
	}
}

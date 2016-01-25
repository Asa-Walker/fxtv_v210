package com.fxtv.threebears.activity.message;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.MessageContentItem;
import com.fxtv.threebears.model.MyMessageItem;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityChat extends BaseActivity {
	private MyMessageItem item;
	private XListView mListView;
	//private List<MessageContentItem> mDataList;
	private MyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		item = (MyMessageItem) getSerializable("item");
		initView();
		getData();
	}

	private void initPublish() {
		if(item==null || "0".equals(item.id)){//官方公告，隐藏输入框
			findViewById(R.id.edit_layout).setVisibility(View.GONE);
		}

		Button mButton = (Button) findViewById(R.id.message_content_button);
		final EditText edit = (EditText) findViewById(R.id.message_content_edittext);
		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = edit.getText().toString();
				if (text.trim().equals("")) {
					FrameworkUtils.showToast(ActivityChat.this,
							getString(R.string.notice_input_message));
					return;
				}
				sendMessage(FrameworkUtils.string2Unicode(text));
				edit.setText("");
			}
		});
	}

	private void sendMessage(final String msg) {
		JsonObject params = new JsonObject();
		params.addProperty("accept", item.send_id);
		params.addProperty("message", msg);
		Utils.showProgressDialog(this);
		String uri=Utils.processUrl(ModuleType.USER, ApiType.USER_sendMessage, params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get(this, uri, "sendMessageListApi", false, false, new RequestCallBack<MessageContentItem>() {

			@Override
			public void onSuccess(MessageContentItem data, Response resp) {
				Log.i("aaaa", "msg1 == null?" + (data == null));
				if (msg != null) {

					if (mAdapter.getListData() == null) {
						mAdapter.setListData(new ArrayList<MessageContentItem>());
					}
					mAdapter.getListData().add(data);
					mAdapter.notifyDataSetChanged();
				}
				mListView.setSelection(mAdapter.getCount() - 1);
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActivityChat.this, resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

		/*getSystemHttpRequests()
				.sendMessageListApi
						(, );*/
	}

	private void getData() {

		JsonObject params = new JsonObject();
		params.addProperty("id", item.id);

		Utils.showProgressDialog(this);
		String url = Utils.processUrl(ModuleType.USER, ApiType.USER_dialogMessage, params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get(this, url, false, false, new RequestCallBack<List<MessageContentItem>>() {
					@Override
					public void onSuccess(List<MessageContentItem> data, Response resp) {
						mAdapter.addData(data);
						mListView.setSelection(mAdapter.getCount() - 1);
					}

					@Override
					public void onFailure(Response resp) {
						FrameworkUtils.showToast(ActivityChat.this, resp.msg);
					}

					@Override
					public void onComplete() {
							Utils.dismissProgressDialog();
					}
				});

	}

	private void initView() {
		initActionbar();
		initListView();
		initPublish();
	}

	private void initListView() {
		mListView = (XListView) findViewById(R.id.activity_messagecontent);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		if (mAdapter == null) {
			mAdapter = new MyAdapter(null);
		}
		mListView.setAdapter(mAdapter);
	}

	private void initActionbar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		title.setText(item.nickname);
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

	class MyAdapter extends BaseListGridAdapter<MessageContentItem> {

		public MyAdapter(List<MessageContentItem> listData) {
			super(listData);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.item_chat, null);
				viewHolder = new ViewHolder();
				viewHolder.imgLeft = (ImageView) convertView.findViewById(R.id.img_left);
				viewHolder.contentLeft = (TextView) convertView.findViewById(R.id.content_left);
				viewHolder.dateLeft = (TextView) convertView.findViewById(R.id.data_left);
				viewHolder.layoutLeft = (RelativeLayout) convertView.findViewById(R.id.chat_left);
				viewHolder.imgRight = (ImageView) convertView.findViewById(R.id.img_right);
				viewHolder.contentRight = (TextView) convertView.findViewById(R.id.content_right);
				viewHolder.dateRight = (TextView) convertView.findViewById(R.id.data_right);
				viewHolder.layoutRight = (RelativeLayout) convertView.findViewById(R.id.chat_right);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			MessageContentItem item = getItem(position);
			Log.i("aaaa", "item.position==null?" + (item.position == null));
			if (item.position.equals("1")) {
				viewHolder.layoutLeft.setVisibility(View.VISIBLE);
				viewHolder.layoutRight.setVisibility(View.GONE);
				viewHolder.contentLeft.setText(FrameworkUtils.unicode2String(item.content));
				viewHolder.dateLeft.setText(item.create_time);
				/*SystemManager.getInstance().getSystem(SystemImageLoader.class)
						.displayImageSquare(item.image, viewHolder.imgLeft);*/
				getSystem(SystemCommon.class).displayDefaultImage(ActivityChat.this, viewHolder.imgLeft,item.image, SystemCommon.SQUARE);
			} else {
				viewHolder.layoutLeft.setVisibility(View.GONE);
				viewHolder.layoutRight.setVisibility(View.VISIBLE);
				viewHolder.contentRight
						.setText(FrameworkUtils.unicode2String(item.content));
				viewHolder.dateRight.setText(item.create_time);
				/*SystemManager.getInstance().getSystem(SystemImageLoader.class)
						.displayImageSquare(item.image, viewHolder.imgRight);*/
				getSystem(SystemCommon.class).displayDefaultImage(ActivityChat.this, viewHolder.imgRight, item.image, SystemCommon.SQUARE);

			}
			return convertView;
		}

		class ViewHolder {
			ImageView imgLeft;
			ImageView imgRight;
			TextView dateLeft;
			TextView dateRight;
			TextView contentLeft;
			TextView contentRight;
			ViewGroup layoutLeft;
			ViewGroup layoutRight;
		}
	}
}

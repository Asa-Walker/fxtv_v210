package com.fxtv.threebears.activity.user;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.Waiter;

import java.util.ArrayList;
import java.util.List;

public class ActivityMyMessage extends BaseActivity {
	XListView mListView;
	List<Waiter> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_message);
		initView();
	}

	private void initView() {
		initActionBar();
		initXListView();
	}

	private void initXListView() {
		mListView = (XListView) findViewById(R.id.activity_my_message_listview);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mList = new ArrayList<Waiter>();
		for (int i = 0; i < 20; i++) {
			Waiter w = new Waiter();
			w.imageUrl = R.drawable.icon_favorite1;
			w.date = "5-" + (10 + i);
			w.name = "客服" + i + "号";
			mList.add(w);
		}
		mListView.setAdapter(new MyAdapter(mList));

	}

	private void initActionBar() {
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

	class MyAdapter extends BaseListGridAdapter<Waiter> {


		public MyAdapter(List<Waiter> listData) {
			super(listData);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				convertView = View.inflate(ActivityMyMessage.this,
						R.layout.activity_my_message_listview_item, null);
				holder = new Holder();
				holder.img = (ImageView) convertView.findViewById(R.id.picture);
				holder.name = (TextView) convertView.findViewById(R.id.aguest_service);
				holder.date = (TextView) convertView.findViewById(R.id.item_date);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.img.setImageResource(mList.get(position).imageUrl);
			holder.date.setText(mList.get(position).date);
			holder.name.setText(mList.get(position).name);

			return convertView;
		}

		class Holder {
			ImageView img;
			TextView name;
			TextView date;
		}

	}

}

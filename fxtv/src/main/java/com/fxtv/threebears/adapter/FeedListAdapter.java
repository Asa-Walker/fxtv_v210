package com.fxtv.threebears.adapter;
 

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.threebears.R;

public class FeedListAdapter extends BaseListGridAdapter {

	public LayoutInflater inflater;

	public FeedListAdapter(Context context) {
		super(null);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 20;
	}

	class ViewHolder {
		TextView FeednameTextView;
		TextView contentTextView;
		TextView publishtimeTextView;
		ImageView feedPersonImageView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_message, null);
			holder = new ViewHolder();
//			holder.contentTextView = (TextView) convertView
//					.findViewById(R.id.activity_use_agreement_content);
//			holder.FeednameTextView=(TextView) convertView
//					.findViewById(R.id.feed_name);
//			holder.publishtimeTextView=(TextView) convertView
//					.findViewById(R.id.feed_publish_time);
//			holder.feedPersonImageView= (ImageView) convertView
//					.findViewById(R.id.feed_person_image);
//			convertView.setTag(holder);
		}
//		else {
//			
//			holder=	(ViewHolder) convertView.getTag();
//		}
//		holder.feedPersonImageView.setImageResource(R.drawable.test_img5);
//		holder.publishtimeTextView.setText("发表于100分钟前");
//		holder.contentTextView.setText("我就吐槽了！我就吐槽了！我就吐槽了！我就吐槽了！我就吐槽了！我就吐槽了！我就吐槽了！我就吐槽了！");
//		holder.FeednameTextView.setText("小龙女");
		return convertView;
	}

}

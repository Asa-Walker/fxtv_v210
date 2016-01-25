package com.fxtv.threebears.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.threebears.model.HotWord;

import java.util.List;

public class SearchAdapter extends BaseListGridAdapter<HotWord> {

	public LayoutInflater inflater;
	private Context context;

	public SearchAdapter(Context context, List<HotWord> list) {
		super(list);
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		/*
		 * RelativeLayout layout=new RelativeLayout(context);
		 * 
		 * RelativeLayout.LayoutParams lParams= new
		 * RelativeLayout.LayoutParams(LayoutParams
		 * .MATCH_PARENT,LayoutParams.MATCH_PARENT);
		 * lParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		 * layout.setLayoutParams(new
		 * RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT
		 * ,LayoutParams.MATCH_PARENT));
		 */

		RelativeLayout mLayout = new RelativeLayout(context);
		mLayout.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT,
				ListView.LayoutParams.FILL_PARENT));

		RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		mParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		TextView textView = new TextView(context);
		textView.setText(getItem(position).name);
		textView.setTextSize(13);
		textView.setLayoutParams(mParams);
		mLayout.addView(textView);
		return mLayout;
	}

}

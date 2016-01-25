package com.fxtv.threebears.activity.explorer;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.threebears.R;

/**
 * 功能说明
 * 
 * @author FXTV-Android
 * 
 */
public class ActivityExplorerInstruction extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explorer_instruction);
		((TextView) findViewById(R.id.ab_title)).setText("功能介绍");

		ImageView leftImg = (ImageView) findViewById(R.id.ab_left_img);
		leftImg.setVisibility(View.VISIBLE);
		leftImg.setImageResource(R.drawable.icon_arrow_left1);
		leftImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityExplorerInstruction.this.finish();
			}
		});
	}
}

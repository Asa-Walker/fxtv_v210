package com.fxtv.threebears.activity.user.userinfo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseFragmentActivity;
import com.fxtv.threebears.R;



/**
 * 图库activity
 * @author 薛建浩
 *
 */
public class ActivityImageDepot extends BaseFragmentActivity {

	private TextView title;
	
	private ImageView back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_depot);
		
		initView();
	}

	private void initView() {
		title=(TextView) findViewById(R.id.my_title);
		title.setText("选择头像");
		findViewById(R.id.ab_editor).setVisibility(View.GONE);
	
		
		back=(ImageView) findViewById(R.id.img_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ActivityImageDepot.this.finish();
			}
		});
		
		
	}
}

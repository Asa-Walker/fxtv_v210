package com.fxtv.threebears.activity.user.settings;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.threebears.R;
import com.fxtv.threebears.system.SystemUser;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择清晰度
 * 
 * @author Android2
 * 
 */
public class ActivityChooseDifinition extends BaseActivity implements OnClickListener {

	private TextView mSuper, mHigh, mNormal, mLow, mAuto;

	private ColorStateList mMainColor, mDefaultColor;

	private List<TextView> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chooose_difinition);
		mMainColor = getResources().getColorStateList(R.color.main_color);
		mDefaultColor = getResources().getColorStateList(R.color.text_color_default);
		initView();
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

	private void initView() {
		initActionBar();
		mSuper = (TextView) findViewById(R.id.super_digital);
		mSuper.setTag(SystemUser.RATE_SUPPER);
		mHigh = (TextView) findViewById(R.id.high_digital);
		mHigh.setTag(SystemUser.RATE_HEIGHTY);
		mNormal = (TextView) findViewById(R.id.normal_digital);
		mNormal.setTag(SystemUser.RATE_NORMAL);
		mLow = (TextView) findViewById(R.id.low_digital);
		mLow.setTag(SystemUser.RATE_FLUENT);
		mAuto = (TextView) findViewById(R.id.auto_choose);
		mAuto.setTag(SystemUser.RATE_AUTO);

		mSuper.setOnClickListener(this);
		mHigh.setOnClickListener(this);
		mNormal.setOnClickListener(this);
		mLow.setOnClickListener(this);
		mAuto.setOnClickListener(this);

		mList = new ArrayList<TextView>();
		mList.add(mAuto);
		mList.add(mSuper);
		mList.add(mHigh);
		mList.add(mNormal);
		mList.add(mLow);

//		changeColor(getSystem(SystemConfig.class).mDifinitionChoosen);
	}

	private void changeColor(String mDifinitionChoosen) {
//		getSystem(SystemConfig.class).mDifinitionChoosen = mDifinitionChoosen;
//		for (int i = 0; i < mList.size(); i++) {
//			if (mList.get(i).getTag().toString().equals(mDifinitionChoosen)) {
//				mList.get(i).setTextColor(mMainColor);
//			} else {
//				mList.get(i).setTextColor(mDefaultColor);
//			}
//		}
	}

	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		title.setText("清晰度");

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.auto_choose:
			changeColor(SystemUser.RATE_AUTO);
			break;
		case R.id.super_digital:
			changeColor(SystemUser.RATE_SUPPER);
			break;
		case R.id.high_digital:
			changeColor(SystemUser.RATE_HEIGHTY);
			break;
		case R.id.normal_digital:
			changeColor(SystemUser.RATE_NORMAL);
			break;
		case R.id.low_digital:
			changeColor(SystemUser.RATE_FLUENT);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
//		getSystem(SystemConfig.class).setDownloadDefinition(
//				getSystem(SystemConfig.class).mDifinitionChoosen);
	}
}

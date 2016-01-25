package com.fxtv.threebears.activity.user.userinfo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.threebears.R;
import com.fxtv.threebears.system.SystemConfig;

import java.util.ArrayList;

public class ActivityDeviceInfo extends BaseActivity {

	private ArrayList<String> mInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_info);
		initView();
	}

	private void initView() {
		initActionBar();
		initListView();
	}

	private void initListView() {
		String deviceId = getStringExtra("deviceId");
		String deviceName = getStringExtra("deviceName");
		String deviceType = getStringExtra("deviceType");
		String androidVersion = getStringExtra("androidVersion");
		String appVersion = getStringExtra("appVersion");
		String UMengWay = getStringExtra("UMengWay");

		TextView tv_deviceId = (TextView) findViewById(R.id.device_id);
		TextView tv_deviceName = (TextView) findViewById(R.id.device_name);
		TextView tv_deviceType = (TextView) findViewById(R.id.device_model);
		TextView tv_androidVersion = (TextView) findViewById(R.id.device_sdk);
		TextView tv_appVersion = (TextView) findViewById(R.id.device_version);
		TextView tv_UMengWay = (TextView) findViewById(R.id.device_channle);

		tv_deviceId.setText(deviceId);
		tv_deviceName.setText(deviceName);
		tv_deviceType.setText(deviceType);
		tv_androidVersion.setText(androidVersion);
		tv_appVersion.setText(appVersion);
		tv_UMengWay.setText(UMengWay);

		final EditText editText = (EditText) findViewById(R.id.face);
		editText.setText(getSystem(SystemConfig.class).HTTP_BASE_URL_TEST_2_0);

		findViewById(R.id.save).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String string = editText.getText().toString();
				if (!TextUtils.isEmpty(string)) {
					getSystem(SystemConfig.class).HTTP_BASE_URL_TEST_2_0 = string;
					showToast("设置成功");
				} else {
					showToast("接口地址不能为NULL");
				}
			}
		});
	}

	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		title.setText("设备信息");
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
}

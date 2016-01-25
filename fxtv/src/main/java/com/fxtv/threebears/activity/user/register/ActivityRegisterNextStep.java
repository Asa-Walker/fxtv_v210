package com.fxtv.threebears.activity.user.register;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.other.ActivityGuideStepFirst;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemUser;



public class ActivityRegisterNextStep extends BaseActivity {
	private String mPassRule = "[a-zA-Z0-9]{6,16}";
	private Button mChangePass;
	private String mPhoneNumber, mVerifyCode;
	private EditText mPass, mRepass, mRecomendCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password_next_step);
		mPhoneNumber = getStringExtra("phoneNumber");
		mVerifyCode = getStringExtra("testCode");
		initView();
	}

	private void initView() {
		initActionBar();
		mChangePass = (Button) findViewById(R.id.activity_find_password_next_step_btn_change_pass);
		mPass = (EditText) findViewById(R.id.activity_find_password_next_step_password);
		mRepass = (EditText) findViewById(R.id.activity_find_password_next_step_repassword);
		mRecomendCode = (EditText) findViewById(R.id.activity_find_password_next_step_reconmend_code);
		mChangePass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				verifyPass();
			}
		});
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

	/**
	 * 验证客户端输入的密码的合法性
	 * 
	 */
	private void verifyPass() {
		String passWord = mPass.getText().toString();
		String rePassWord = mRepass.getText().toString();
		if (passWord.trim().isEmpty() || rePassWord.trim().isEmpty()) {
			FrameworkUtils.showToast(this, "密码或重复密码不能为空");
		} else {
			if (passWord.matches(mPassRule)) {
				if (passWord.equals(rePassWord)) {
					// changePassWord(passWord);
					userRegister(passWord);
				} else {
					FrameworkUtils.showToast(this, "两次密码输入不一致");
				}
			} else {
				FrameworkUtils.showToast(this, "密码输入错误(应为6-16位数字字母组合)");
			}
		}
	}

	/**
	 * 用户注册
	 * 
	 * @param passWord
	 */
	private void userRegister(final String passWord) {

		String value = mRecomendCode.getText().toString().trim();

		getSystem(SystemUser.class)
				.register(mPhoneNumber, passWord, mVerifyCode, value, new IUserBusynessCallBack() {
					@Override
					public void onResult(boolean result, String arg) {
						if (result) {
							showToast("注册成功");
							FrameworkUtils.skipActivity(ActivityRegisterNextStep.this, ActivityGuideStepFirst.class);
							initFragment(arg, passWord);
							finish();
						} else {
							showToast(""+arg);
						}
					}
				});

	}

	private void initFragment(String json, String passWord) {
//		getSystem(SystemUser.class)
//				.loginHandle(ActivityRegisterNextStep.this, SystemUser.LOGIN_TYPE_NORMAL, json, mPhoneNumber, passWord);
	}

	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		title.setText("注册");
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

package com.fxtv.threebears.activity.user.userinfo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

public class ActivityFindPasswordNextstep extends BaseActivity {
	private String mPassRule = "[a-zA-Z0-9]{5,16}";
	private Button mChangePass;
	private String mPhoneNumber, mVerifyCode;
	private EditText mPass, mRepass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password_next_step);
		mPhoneNumber = getStringExtra("phoneNumber");
		mVerifyCode = getStringExtra("verifycode");
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
		
		findViewById(R.id.activity_find_password_next_step_reconmend_code).setVisibility(View.GONE);
		mChangePass = (Button) findViewById(R.id.activity_find_password_next_step_btn_change_pass);
		mPass = (EditText) findViewById(R.id.activity_find_password_next_step_password);
		mRepass = (EditText) findViewById(R.id.activity_find_password_next_step_repassword);
		mChangePass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				verifyPass();
			}
		});
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
					changePassWord(passWord);
				} else {
					FrameworkUtils.showToast(this, "两次密码输入不一致");
				}
			} else {
				FrameworkUtils.showToast(this, "密码输入错误(应为6-16位数字字母组合)");
			}
		}
	}

	/**
	 * 修改密码
	 * 
	 * @param passWord
	 */
	private void changePassWord(String passWord) {
		JsonObject params = new JsonObject();
		params.addProperty("phone", mPhoneNumber);
		params.addProperty("password", passWord);
		params.addProperty("verify_code", mVerifyCode);
		Utils.showProgressDialog(this);
		/*String url = processUrl("User", "retrievePassword", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url callBack);*/
		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_retrievePassword, params), "modify_password", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				FrameworkUtils.showToast(ActivityFindPasswordNextstep.this, "密码修改成功");
				finish();
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActivityFindPasswordNextstep.this, resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

	}

	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		title.setText("找回密码");
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

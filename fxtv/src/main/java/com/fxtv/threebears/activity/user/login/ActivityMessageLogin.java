package com.fxtv.threebears.activity.user.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemMsmAuth;
import com.fxtv.threebears.R;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemUser;

import cn.smssdk.SMSSDK;

;

public class ActivityMessageLogin extends BaseActivity {
	private String mPhoneRule = "[0-9]{11}";
	private String mTestCodeRule = "[0-9]{4}";
	private String mPhoneNumber, testCode;
	private Button mSendTestCode;
	private Thread mThread;
	private boolean mThreadFlag;
	private final int mTime = 90;
	private final int SIGN = 1990;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == SIGN) {
				if (msg.arg1 == 0) {
					mThreadFlag = false;
					mSendTestCode.setBackgroundResource(R.drawable.selector_btn2);
					mSendTestCode.setEnabled(true);
					mSendTestCode.setText("发送验证码");
				} else {
					mSendTestCode.setText("重新发送(" + msg.arg1 + "s)");
				}
			} else {
				int event = msg.arg1;
				int result = msg.arg2;
				Object data = msg.obj;
				if (result == SMSSDK.RESULT_COMPLETE) {
					// 短信注册成功后，返回MainActivity,然后提示新好友
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){//发送验证码成功
					}
				} else {
					((Throwable) data).printStackTrace();
					showToast("验证码发送失败");
				}
			};
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_login);
		initView();
	}

	protected void initWorkThread() {
		mThread = new Thread() {
			int pos = mTime;

			@Override
			public void run() {
				while (mThreadFlag) {
					Message msg = new Message();
					msg.arg1 = pos--;
					msg.what = SIGN;
					mHandler.sendMessage(msg);
					SystemClock.sleep(1000);
				}
			}
		};
		mThreadFlag = true;
		mThread.start();
	}

	/**
	 * 短信登录
	 */
	protected void messageLogin(String phone, String code) {
		getSystem(SystemUser.class).messageLogin(phone, code, new IUserBusynessCallBack() {

			@Override
			public void onResult(boolean result, String arg) {
				showToast(arg);
				if (result) {
					finish();
				}
			}
		});
	}

	protected void noticeLogin() {
		Intent intent = new Intent("login");
		sendBroadcast(intent);
	}

	private void initView() {
		initActionBar();
		initSharedSDK();
		setListener();
	}

	private void initSharedSDK() {
		SystemManager.getInstance().getSystem(SystemMsmAuth.class).initSharedSDK(this);
		SystemManager.getInstance().getSystem(SystemMsmAuth.class).setCallBack(new SystemMsmAuth.IMsmAuthCallBack() {

			@Override
			public void onSuccessSendMsmCode() {
				showToast("验证码已经发送");
			}

			@Override
			public void onSuccessAuthMsmCode() {

			}

			@Override
			public void onFailure(String msg) {
				showToast(msg);
			}
		});
		/*
		SMSSDK.initSDK(this, SystemManager.getInstance().getSystem(SystemMsmAuth.class).APPKEY, SystemManager.getInstance()
				.getSystem(SystemMsmAuth.class).APPSECRET);
		EventHandler en = new EventHandler() {
			@Override
			public void afterEvent(int arg0, int arg1, Object arg2) {
				Message msg = new Message();
				msg.arg1 = arg0;
				msg.arg2 = arg1;
				msg.obj = arg2;
				mHandler.sendMessage(msg);
			}
		};
		SMSSDK.registerEventHandler(en);*/
	}

	private void setListener() {
		final EditText code = (EditText) findViewById(R.id.activity_message_login_testcode);
		final EditText phone = (EditText) findViewById(R.id.activity_message_login_phone);
		mSendTestCode = (Button) findViewById(R.id.activity_message_Login_send_testcode);
		mSendTestCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPhoneNumber = phone.getText().toString();
				if (mPhoneNumber.trim().isEmpty()) {
					FrameworkUtils.showToast(ActivityMessageLogin.this, "手机号不能为空");
				} else {
					if (mPhoneNumber.matches(mPhoneRule)) {
						FrameworkUtils.showToast(ActivityMessageLogin.this, "验证码已经发送");
						mSendTestCode.setEnabled(false);
						mSendTestCode.setBackgroundResource(R.drawable.shape_message);
						initWorkThread();
						SMSSDK.getVerificationCode("86", mPhoneNumber);
					} else {
						FrameworkUtils.showToast(ActivityMessageLogin.this, "手机号输入错误");
					}
				}
			}
		});
		findViewById(R.id.activity_message_Login_login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				testCode = code.getText().toString();
				if (!testCode.trim().isEmpty()) {
					if (testCode.matches(mTestCodeRule) && mPhoneNumber != null && !mPhoneNumber.trim().isEmpty()) {
						messageLogin(mPhoneNumber, testCode);
					} else {
						FrameworkUtils.showToast(ActivityMessageLogin.this, "请先获取验证码或正确输入验证码");
					}
				} else {
					FrameworkUtils.showToast(ActivityMessageLogin.this, "验证码不能为空");
				}
			}
		});
	}

	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		title.setText("登录");

		findViewById(R.id.ab_left_img).setVisibility(View.VISIBLE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mThreadFlag = false;
		/*SMSSDK.unregisterAllEventHandler();*/
		SystemManager.getInstance().getSystem(SystemMsmAuth.class).destroySystem();
	}
}

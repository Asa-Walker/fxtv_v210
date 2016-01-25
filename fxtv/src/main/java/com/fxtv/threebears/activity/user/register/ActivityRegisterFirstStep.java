package com.fxtv.threebears.activity.user.register;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemMsmAuth;
import com.fxtv.framework.system.SystemMsmAuth.IMsmAuthCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemUser;

import cn.smssdk.EventHandler;



public class ActivityRegisterFirstStep extends BaseActivity {
	private Button mNextStep, mSendTestCode;
	private EditText mPhoneNumber, mTestCode;
	private String mNameRule = "[0-9]{11}";
	private String mTestCodeRule = "[0-9]{4}";
	private String mNumberTemp;
	private Thread mThread;
	private boolean mThreadFlag;
	private final int mTime = 90;
	private final int SIGN = 1990;
	private EventHandler mEventHandler;
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
			} 
//			else {
//				int event = msg.arg1;
//				int result = msg.arg2;
//				Object data = msg.obj;
//				if (result == SMSSDK.RESULT_COMPLETE) {
//					// 短信注册成功后，返回MainActivity,然后提示新好友
//					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
//						Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
//						Bundle bundle = new Bundle();
//						bundle.putString("phoneNumber", mPhoneNumber.getText().toString());
//						FrameworkUtils.skipActivity(ActivityRegisterFirstStep.this, ActivityRegisterNextStep.class,
//								bundle);
//						finish();
//					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
//					}
//				} else {
//					((Throwable) data).printStackTrace();
//					showToast("验证码错误");
//				}
//			}
		}
	};

	protected void initWorkThread() {
		mThread = new Thread() {
			int pos = mTime;

			@Override
			public void run() {
				super.run();
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password);
		initView();
	}

	private void initView() {
		initActionBar();
		initSharedSDK();
		mNextStep = (Button) findViewById(R.id.activity_find_password_nextstep);
		mSendTestCode = (Button) findViewById(R.id.activity_find_password_sendcode);
		mPhoneNumber = (EditText) findViewById(R.id.activity_find_password_phone);
		mTestCode = (EditText) findViewById(R.id.activity_find_password_testcode);
		mNextStep.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String code = mTestCode.getText().toString();
				// String phone = mPhoneNumber.getText().toString();
				if (code.trim().isEmpty() || mNumberTemp == null || mNumberTemp.trim().isEmpty()) {
					FrameworkUtils.showToast(ActivityRegisterFirstStep.this, "请先获取验证或输入验证码");
				} else {
					if (code.matches(mTestCodeRule)) {
						// SMSSDK.submitVerificationCode("86", mNumberTemp,
						// code);
						Bundle bundle = new Bundle();
						bundle.putString("phoneNumber", mNumberTemp);
						bundle.putString("testCode", code);
						FrameworkUtils.skipActivity(ActivityRegisterFirstStep.this, ActivityRegisterNextStep.class,
								bundle);
						finish();
					} else {
						FrameworkUtils.showToast(ActivityRegisterFirstStep.this, "验证码输入有误");
					}
				}
				// // 测试
				// Bundle bundle = new Bundle();
				// bundle.putString("phoneNumber",
				// mPhoneNumber.getText().toString());
				// FrameworkUtils.skipActivity(ActivityRegisterFirstStep.this,
				// ActivityRegisterNextStep.class, bundle);
				// finish();
			}
		});
		mSendTestCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String number = mPhoneNumber.getText().toString();
				if (number.trim().isEmpty()) {
					FrameworkUtils.showToast(ActivityRegisterFirstStep.this, "手机号不能为空");
				} else {
					if (number.matches(mNameRule)) {
						verifyUserName(number);
					} else {
						FrameworkUtils.showToast(ActivityRegisterFirstStep.this, "手机号输入有误");
					}
				}
			}
		});
	}

	/**
	 * 验证用户是否存在
	 *
	 */
	protected void verifyUserName(final String number) {
		getSystem(SystemUser.class).verifyPhoneUse(number, new IUserBusynessCallBack() {

			@Override
			public void onResult(boolean result, String arg) {
				if (result) {
					mNumberTemp = number;
					showToast("正在获取验证码,请稍后...");
					SystemManager.getInstance().getSystem(SystemMsmAuth.class).sendMsmCode(number);
					mSendTestCode.setEnabled(false);
					mSendTestCode.setBackgroundResource(R.drawable.shape_message);
					mSendTestCode.setEnabled(false);
					initWorkThread();
				} else {
					showToast(arg);
				}
			}
		});
	}

	private void initSharedSDK() {
		SystemManager.getInstance().getSystem(SystemMsmAuth.class).initSharedSDK(this);
		SystemManager.getInstance().getSystem(SystemMsmAuth.class).setCallBack(new IMsmAuthCallBack() {

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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mThreadFlag = false;
		SystemManager.getInstance().getSystem(SystemMsmAuth.class).destroySystem();
	}
}

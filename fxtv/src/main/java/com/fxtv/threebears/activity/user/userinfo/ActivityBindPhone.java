package com.fxtv.threebears.activity.user.userinfo;

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
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.SystemMsmAuth;
import com.fxtv.framework.system.SystemMsmAuth.IMsmAuthCallBack;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

;

public class ActivityBindPhone extends BaseActivity {
	private String mPhoneRule = "[0-9]{11}";
	private String mTestCodeRule = "[0-9]{4}";
	private String mPhoneNumber, testCode;
	private String APPKEY = "674f509478f6";
	private String APPSECRET = "625f445eeb48b14bc18aa251b8b891a3";
	private Button mSendTestCode;
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
			}
			// else {
			// int event = msg.arg1;
			// int result = msg.arg2;
			// Object data = msg.obj;
			// if (result == SMSSDK.RESULT_COMPLETE) {
			// // 短信注册成功后，返回MainActivity,然后提示新好友
			// if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
			// Toast.makeText(getApplicationContext(), "提交验证码成功",
			// Toast.LENGTH_SHORT).show();
			// bindPhone();
			// } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
			// }
			// } else {
			// ((Throwable) data).printStackTrace();
			// showToast("验证码发送失败");
			// }
			// };
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_login);
		initView();
	}

	/**
	 * 绑定手机号
	 */
	protected void bindPhone() {
		JsonObject params = new JsonObject();
		params.addProperty("phone", mPhoneNumber);
		params.addProperty("verify_code", testCode);
		Utils.showProgressDialog(this);
		/*String url = processUrl("User", "modifyPhone", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, urlcallBack);*/
		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_modifyPhone, params), "modifyPhone", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				getSystem(SystemUser.class).mUser.phone = mPhoneNumber;
				finish();
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActivityBindPhone.this, resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

	}

	private void initView() {
		Button btn = (Button) findViewById(R.id.activity_message_Login_login);
		btn.setText("确定");
		EditText edt = (EditText) findViewById(R.id.activity_message_login_phone);
		edt.setHint("请输入未绑定的手机号");
		initActionBar();
		initSharedSDK();
		setListener();
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
					FrameworkUtils.showToast(ActivityBindPhone.this, "手机号不能为空");
				} else {
					if (mPhoneNumber.matches(mPhoneRule)) {
						checkPohone(mPhoneNumber);
					} else {
						FrameworkUtils.showToast(ActivityBindPhone.this, "手机号输入错误");
					}
				}
			}
		});
		findViewById(R.id.activity_message_Login_login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				testCode = code.getText().toString();
				if (!testCode.trim().isEmpty()) {
					if (testCode.matches(mTestCodeRule)) {
						bindPhone();
					} else {
						FrameworkUtils.showToast(ActivityBindPhone.this, "验证码输入错误");
					}
				} else {
					FrameworkUtils.showToast(ActivityBindPhone.this, "验证码不能为空");
				}
			}
		});
	}

	/**
	 * 验证手机是否绑定
	 * 
	 * @param mPhoneNumber
	 */
	protected void checkPohone(final String mPhoneNumber) {
		getSystem(SystemUser.class)
				.verifyPhoneUse(mPhoneNumber, new IUserBusynessCallBack() {

					@Override
					public void onResult(boolean result, String arg) {
						if (result) {
							showToast("正在获取验证码,请稍后...");
							mSendTestCode.setEnabled(false);
							mSendTestCode.setBackgroundResource(R.drawable.shape_message);
							initWorkThread();
							SystemManager.getInstance().getSystem(SystemMsmAuth.class).sendMsmCode(mPhoneNumber);
						} else {
							showToast(arg);
						}
					}
				});
	}

	protected void initWorkThread() {
		Thread mThread = new Thread() {
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
		title.setText("绑定手机");
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

package com.fxtv.threebears.activity.user.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemThirdPartyLogin;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.user.register.ActivityRegisterFirstStep;
import com.fxtv.threebears.activity.user.userinfo.ActivityFindPassword;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;

;

/**
 * @author 薛建浩
 * 
 *         登陆
 */
public class ActivityLogin extends BaseActivity implements OnClickListener{
	private static final String TAG = "ActivityLogin";
	private static final int EVENT_LOGIN_COMPLETE = 2000;
	private static final int EVENT_LOGIN_ERROR = 1000;
	private static final int EVENT_LOGIN_CANCLE = 1001;
	private EditText mEditTextUserName, mEditTextPassWord;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == EVENT_LOGIN_COMPLETE) {
				String type=null;
				if (msg.arg1 == SystemThirdPartyLogin.LOGIN_TYPE_QQ) {
					type="QQ";
				} else if (msg.arg1 == SystemThirdPartyLogin.LOGIN_TYPE_SINA) {
					type="SINA";
				} else if (msg.arg1 == SystemThirdPartyLogin.LOGIN_TYPE_WECHAT) {
					type="WEIXIN";
				} else {
					Logger.e(TAG, "Error,not find the arg1=" + msg.arg1);
				}
				if(!TextUtils.isEmpty(type)){
					thirdLogin((String)msg.obj, type);
				}
			} else if (msg.what == EVENT_LOGIN_ERROR) {
				showToast(msg.obj.toString());
			} else if (msg.what == EVENT_LOGIN_CANCLE) {
				showToast("取消登录");
			} else {
				Logger.e(TAG, "Error,not find the what=" + msg.what);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initActionbar();
		initView();
	}

	private void initView() {
		mEditTextUserName = (EditText) findViewById(R.id.activity_login_user_name);
		mEditTextPassWord = (EditText) findViewById(R.id.activity_login_password);

		findViewById(R.id.message_test).setOnClickListener(this);
		findViewById(R.id.forget_password).setOnClickListener(this);
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.qq_login).setOnClickListener(this);
		findViewById(R.id.sina_login).setOnClickListener(this);
		findViewById(R.id.wechat_login).setOnClickListener(this);
	}

	public  void thridLogin(final int type){
		SystemManager.getInstance().getSystem(SystemThirdPartyLogin.class).thirdLogin(ActivityLogin.this, type, new SystemThirdPartyLogin.ICallBackSystemLogin() {
			@Override
			public void onSuccess(String msg) {
				Message message=mHandler.obtainMessage();
				message.what=EVENT_LOGIN_COMPLETE;
				message.arg1=type;
				message.obj=msg;
				message.sendToTarget();
				FrameworkUtils.showToast(ActivityLogin.this,"登录成功");
			}

			@Override
			public void onFailure(String msg) {
				Message message=mHandler.obtainMessage();
				message.what=EVENT_LOGIN_ERROR;
				message.obj=msg;
				message.sendToTarget();
				FrameworkUtils.showToast(ActivityLogin.this,"登录成功");
			}

			@Override
			public void onCancle() {
				FrameworkUtils.showToast(ActivityLogin.this,"取消登录");

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		SystemManager.getInstance().getSystem(SystemThirdPartyLogin.class).onActivityResult(requestCode,resultCode,data);
	}

	private boolean checkInputFormat(String userName, String passWord) {
		userName = userName.trim();
		passWord = passWord.trim();
		if (userName.isEmpty() || passWord.isEmpty()) {
			showToast(getString(R.string.notice_userid_or_password_can_not_empty));
			return false;
		}
		if (!FrameworkUtils.isMoblePhone(userName)) {
			showToast(getString(R.string.notice_wrong_userid_format));
			return false;
		}
		if (!Utils.checkPassWord(passWord)) {
			showToast(getString(R.string.notice_wrong_password));
		}
		return true;
	}


	private void initActionbar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		title.setText("登录");
		ImageView btnBack = (ImageView) findViewById(R.id.ab_left_img);
		btnBack.setImageResource(R.drawable.icon_arrow_left1);
		btnBack.setVisibility(View.VISIBLE);
		TextView close=(TextView)findViewById(R.id.ab_right_tv);
		close.setVisibility(View.VISIBLE);
		close.setText("注册");
		close.setOnClickListener(this);
	}

	/**
	 * 正常登录
	 * 
	 * @param username
	 * @param passWord
	 */
	private void userLogin(final String username, final String passWord) {
		getSystem(SystemUser.class)
				.userLogin(username, passWord, new IUserBusynessCallBack() {

					@Override
					public void onResult(boolean result, String arg) {
						showToast(arg);
						if (result) {
							finish();
						}
					}
				});
	}

	/**
	 * 第三方登录
	 * @param thirdLoginId
	 * @param
	 */
	protected void thirdLogin(final String thirdLoginId, final String thirdName) {
		getSystem(SystemUser.class)
				.thirdLogin(thirdLoginId, thirdName, new IUserBusynessCallBack() {
					@Override
					public void onResult(boolean result, String arg) {
						showToast(arg);

						if (result) {
							finish();
						}
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.ab_right_tv://注册
				FrameworkUtils.skipActivity(ActivityLogin.this, ActivityRegisterFirstStep.class);
				finish();
				break;
			case R.id.message_test:// 短信登录
				FrameworkUtils.skipActivity(ActivityLogin.this, ActivityMessageLogin.class);
				finish();
				break;
			case R.id.forget_password:// 忘记密码
				FrameworkUtils.skipActivity(ActivityLogin.this, ActivityFindPassword.class);
				break;
			case R.id.login:	// 登录
				String userName = mEditTextUserName.getText().toString().trim();
				String passWord = mEditTextPassWord.getText().toString().trim();
				if (checkInputFormat(userName, passWord)) {
					userLogin(userName, passWord);
				}
				break;
			case R.id.qq_login:// qq登录
				thridLogin(SystemThirdPartyLogin.LOGIN_TYPE_QQ);
				break;
			case R.id.sina_login:// 新浪登录
				thridLogin(SystemThirdPartyLogin.LOGIN_TYPE_SINA);
				break;
			case R.id.wechat_login:// 微信登录
				thridLogin(SystemThirdPartyLogin.LOGIN_TYPE_WECHAT);
				break;

		}
	}
}

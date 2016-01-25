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

public class ActivityFindPassword extends BaseActivity {
    private Button mNextStep, mSendTestCode;
    private EditText mPhoneNumber, mTestCode;
    private String mNameRule = "[0-9]{11}";
    private String mTestCodeRule = "[0-9]{4}";
    private String mNumberTemp;
    private Thread mThread;
    private String TAG = "ActivityFindPassword";
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
            // // verifyUser();
            // } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
            // Logger.d(TAG, "发送验证码成功");
            //
            // }
            // } else {
            // ((Throwable) data).printStackTrace();
            // Logger.d(TAG, "发送验证码失败");
            // showToast("验证码错误");
            // }
            // };
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        initView();
    }

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

    /**
     * 验证用户是否存在
     */
    protected void verifyUser(final String code) {
        JsonObject params = new JsonObject();
        params.addProperty("username", mNumberTemp);
        Utils.showProgressDialog(this);
        /*
        String url = processUrl("User", "verifyPhone", params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, urlcallBack);
        */
        getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_verifyPhone, params), "verify_username", false, false, new RequestCallBack<String>() {
            @Override
            public void onSuccess(String data, Response resp) {
                Bundle bundle = new Bundle();
                bundle.putString("phoneNumber", mNumberTemp);
                bundle.putString("verifycode", code);
                FrameworkUtils.skipActivity(ActivityFindPassword.this, ActivityFindPasswordNextstep.class,
                        bundle);
                finish();
            }

            @Override
            public void onFailure(Response resp) {
                FrameworkUtils.showToast(ActivityFindPassword.this, resp.msg);
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        initActionBar();
        initSharedSDK();
        mNextStep = (Button) findViewById(R.id.activity_find_password_nextstep);
        mSendTestCode = (Button) findViewById(R.id.activity_find_password_sendcode);
        mPhoneNumber = (EditText) findViewById(R.id.activity_find_password_phone);
        mTestCode = (EditText) findViewById(R.id.activity_find_password_testcode);
        // 下一步
        mNextStep.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mTestCode.getText().toString();
                if (code.trim().isEmpty()) {
                    FrameworkUtils.showToast(ActivityFindPassword.this, "验证码不能为空");
                } else {
                    if (code.matches(mTestCodeRule)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("phoneNumber", mNumberTemp);
                        bundle.putString("verifycode", code);
                        FrameworkUtils.skipActivity(ActivityFindPassword.this, ActivityFindPasswordNextstep.class,
                                bundle);
                        finish();
                    } else {
                        FrameworkUtils.showToast(ActivityFindPassword.this, "验证码输入有误");
                    }
                }
            }
        });
        // 发送验证码
        mSendTestCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = mPhoneNumber.getText().toString();
                if (number.trim().isEmpty()) {
                    FrameworkUtils.showToast(ActivityFindPassword.this, "手机号不能为空");
                } else {
                    if (number.matches(mNameRule)) {
                        verifyUserName(number);
                    } else {
                        FrameworkUtils.showToast(ActivityFindPassword.this, "手机号输入有误");
                    }
                }
            }
        });
    }

    /**
     * 验证用户是否存在
     *
     * @param number
     */
    protected void verifyUserName(final String number) {
        getSystem(SystemUser.class).verifyRegisterUser(number, new IUserBusynessCallBack() {
            @Override
            public void onResult(boolean result, String arg) {
                if (result) {
                    showToast("正在获取验证码,请稍后...");
                    mSendTestCode.setBackgroundResource(R.drawable.shape_message);
                    mSendTestCode.setEnabled(false);
                    initWorkThread();
                    mNumberTemp = number;
                    SystemManager.getInstance().getSystem(SystemMsmAuth.class).sendMsmCode(mNumberTemp);
                } else {
                    showToast("手机号未注册或不存在");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThreadFlag = false;
        SystemManager.getInstance().getSystem(SystemMsmAuth.class).destroySystem();
    }
}

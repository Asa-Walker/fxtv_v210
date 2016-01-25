package com.fxtv.threebears.activity.user.other;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.fxtv.threebears.activity.user.userinfo.ActivityDeviceInfo;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

/**
 * @author FXTV-Android
 * 
 *         用户意见反馈界面
 */
public class ActivitySuggestion extends BaseActivity {
	/**
	 * content--反馈内容 connact--联系方式
	 */
	private EditText content, connact;
	private Resources mResources;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion);
		mResources = getResources();
		initView();
	}

	private String getVersionName() {
		String version = null;
		try {
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			version = info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
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
		initActionbar();
		content = (EditText) findViewById(R.id.activity_suggestion_actv_content);
		content.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		content.setSingleLine(false);
		content.setHorizontallyScrolling(false);
		connact = (EditText) findViewById(R.id.activity_suggetion_connact);
		findViewById(R.id.submit_suggetion).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取输入的内容发送给服务器
				String contentValue = content.getText().toString();
				String connactValue = connact.getText().toString();
				if ("xunleen".equals(contentValue)) {
					Bundle bundle = new Bundle();
					bundle.putString("deviceId",
							((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId());
					bundle.putString("deviceName", Build.MANUFACTURER);
					bundle.putString("androidVersion", Build.VERSION.RELEASE);
					bundle.putString("appVersion", getVersionName());
					bundle.putString("UMengWay", getApplicationInf());
					bundle.putString("deviceType", Build.MODEL);
					FrameworkUtils.skipActivity(ActivitySuggestion.this, ActivityDeviceInfo.class, bundle);
				} else {
					sendToService(contentValue, connactValue);
				}
			}
		});
	}

	/**
	 * 发送消息给服务器
	 * 
	 * @param contentValue
	 *            --反馈内容
	 * @param connactValue
	 *            --联系方式
	 */
	protected void sendToService(String contentValue, String connactValue) {
		JsonObject params = new JsonObject();
		if(contentValue.equals("")){
			showToast("内容不能为空");
			return;
		}
		params.addProperty("content", contentValue);
		params.addProperty("contact", connactValue);
		params.addProperty("device_model", Build.MANUFACTURER + ":" + Build.MODEL);
		params.addProperty("system_version", Build.VERSION.RELEASE);
//		params.addProperty("app_version", getVersionName());
		Utils.showProgressDialog(this);

		/*String url = processUrl("User", "feedback", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url callBack);
		*/
		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_feedback, params), "otherFeedBackApi", false, false, new RequestCallBack() {
			@Override
			public void onSuccess(Object data, Response resp) {
				FrameworkUtils.showToast(ActivitySuggestion.this,
						mResources.getString(R.string.notice_send_success));
				content.setText("");
				connact.setText("");
				finish();
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActivitySuggestion.this, resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

	}

	private void initActionbar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		title.setText("意见反馈");
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

	public String getApplicationInf() {
		String value = "";
		try {
			ApplicationInfo aInfo = getPackageManager().getApplicationInfo(getPackageName(),
					PackageManager.GET_META_DATA);
			value = aInfo.metaData.getString("UMENG_CHANNEL");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return value;
	}
}

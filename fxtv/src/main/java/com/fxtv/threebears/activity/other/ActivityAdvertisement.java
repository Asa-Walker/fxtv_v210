package com.fxtv.threebears.activity.other;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.MainActivity;
import com.fxtv.threebears.R;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 实时更新的广告页
 * 
 * @author Android2
 * 
 */
public class ActivityAdvertisement extends BaseActivity {
	private ImageView mLoadingView;
	private TextView mSkipView;
	private String TAG = "ActivityAdvertisement";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_advertise);
		initView();
		getLoadingImage();
	}

	private void getLoadingImage() {
		JsonObject params = new JsonObject();
		params.addProperty("version", FrameworkUtils.getVersion(this));
		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.BASE, ApiType.BASE_loading, params),"getLoadingImageUrl", true, true, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				String iamgeUrl = "";
				try {
					JSONObject jobj = new JSONObject(data);
					iamgeUrl = jobj.getString("image");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (!"".equals(iamgeUrl)) {
					mSkipView.setVisibility(View.VISIBLE);
					mLoadingView.setVisibility(View.VISIBLE);
					getSystem(SystemCommon.class).displayDefaultImage(ActivityAdvertisement.this,mLoadingView,iamgeUrl,SystemCommon.ADVERTISE);
				}
			}

			@Override
			public void onFailure(Response resp) {
				if(!TextUtils.isEmpty(resp.msg)){
					Log.d(TAG, resp.msg);
				}
			}

			@Override
			public void onComplete() {
				goMain(3000);
			}
		});

	}

	private void initView() {
		mSkipView = (TextView) findViewById(R.id.skip);
		mSkipView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FrameworkUtils.skipActivity(ActivityAdvertisement.this, MainActivity.class);
				finish();
			}
		});
		mLoadingView = (ImageView) findViewById(R.id.loading_img);
		mLoadingView.setVisibility(View.GONE);
	}

	private void goMain(long delayed) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				FrameworkUtils.skipActivity(ActivityAdvertisement.this, MainActivity.class);
				finish();
			}
		}, delayed);
	}

	@Override
	public void onBackPressed() {
	}
}

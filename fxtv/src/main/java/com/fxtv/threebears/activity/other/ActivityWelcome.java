package com.fxtv.threebears.activity.other;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.WindowManager;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.threebears.R;
import com.fxtv.threebears.system.SystemAnalyze;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemCommon.ISystemCommonCallBack;
import com.fxtv.threebears.system.SystemPreference;

/**
 * 欢迎页面
 * 
 * @author 薛建浩
 * 
 */
public class ActivityWelcome extends BaseActivity {
	private int counts;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);

		String logoutUc = getSystem(SystemPreference.class).getUC(0);
		if (TextUtils.isEmpty(logoutUc)) {
			getUC();
		} else {
			toNextPage();
		}
	}

	private void getUC() {
		getSystem(SystemCommon.class).getUCCode(0, false, new ISystemCommonCallBack() {
			@Override
			public void onResult(boolean result, String arg) {
				if (result) {
					toNextPage();
				} else {
					showToast(arg + ",请检查网络");
					counts++;
					if (counts < 2) {
						getUC();
					} else {
						showToast("请退出,重新进入App");
					}
				}
			}
		});
	}

	private void toNextPage() {
		getSystem(SystemAnalyze.class).analyzeAppStart();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				FrameworkUtils.skipActivity(ActivityWelcome.this, ActivityAdvertisement.class);
				finish();
			}
		}, 2000);
	}

	@Override
	public void onBackPressed() {
	}
}

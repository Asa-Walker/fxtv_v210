package com.fxtv.threebears.activity.explorer;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.Action;
import com.fxtv.threebears.system.SystemPreference;
import com.fxtv.threebears.util.Utils;

public class ActivityExplorerWeb extends BaseActivity {
	private WebView mWebView;
	private String mUrl;
	private boolean mShowShare;
	private int mCount = 0;
	private String mTitle = "";
	//private OnekeyShare oks;
	private String TAG = "ActivityExplorerWeb";
	private String testImage;
	private Action mcCenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explorer_web);
		mUrl = getStringExtra("url");
		mTitle = getStringExtra("title");
		mcCenter = (Action) getSerializable("center");
		mShowShare = baseSavedInstance==null && baseSavedInstance.getBoolean("showShare", false);
		if (mcCenter != null) {
			initShare();
		}
		if (mUrl == null || "".equals(mUrl)) {
			finish();
		}
		StringBuilder builder = new StringBuilder(mUrl);
		if (mUrl.contains("?")) {
			builder.append("&uc=").append(getSystem(SystemPreference.class).getUC(2));
		} else {
			builder.append("?uc=").append(getSystem(SystemPreference.class).getUC(2));
		}
		mUrl = builder.toString();
		initView();

	}

	private void initView() {
		Utils.showProgressDialog(this);
		initActionBar();
		mWebView = (WebView) findViewById(R.id.activity_explorer_web);
		// 设置WebView属性，能够执行Javascript脚本
		mWebView.getSettings().setJavaScriptEnabled(true);
		// 加载需要显示的网页
		mWebView.loadUrl(mUrl);
		// 设置可以支持缩放
		mWebView.getSettings().setSupportZoom(true);
		// 设置出现缩放工具
		mWebView.getSettings().setBuiltInZoomControls(true);
		// 扩大比例的缩放
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setAppCacheEnabled(false);
		// 自适应屏幕
		mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (mCount < 0) {
					ActivityExplorerWeb.this.finish();
				} else {
					view.loadUrl(url);
					mCount++;
				}
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Utils.dismissProgressDialog();
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
			}
		});
		mWebView.setWebChromeClient(new WebChromeClient());
		// 点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
		mWebView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
						mWebView.goBack(); // 后退
						mCount--;
						return true; // 已处理
					}
				}
				return false;
			}
		});
	}
	private void initActionBar() {
		if (mTitle != null && !mTitle.equals("")) {
			((TextView) findViewById(R.id.ab_title)).setText(mTitle);
		} else {
			((TextView) findViewById(R.id.ab_title)).setText("活动详情");
		}
		TextView colse = (TextView) findViewById(R.id.close);
		colse.setVisibility(View.VISIBLE);
		colse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ImageView shareImage = (ImageView) findViewById(R.id.ab_right_img1);
		if (mcCenter == null) {
			shareImage.setVisibility(View.GONE);
		} else {
			shareImage.setVisibility(View.VISIBLE);
			shareImage.setImageResource(R.drawable.icon_share_white);
			shareImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//share();
				}
			});
		}
	}

	/**
	 * 添加分享内容
	 *//*
	protected void share() {
		// TODO Auto-generated method stub
		oks = new OnekeyShare();
		oks.setCallback(new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				Logger.d(TAG, "share,onError...");
				runOnUiThread(new Runnable() {
					public void run() {
						FrameworkUtils.showToast(ActivityExplorerWeb.this, "分享失败");
					}
				});
			}

			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				Logger.d(TAG, "share,onComplete...");
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				Logger.d(TAG, "share,onCancel...");
				FrameworkUtils.showToast(ActivityExplorerWeb.this, "取消分享");
			}
		});
		oks.setTitle(mcCenter.title);
		oks.setTitleUrl(mUrl);
		oks.setText("飞熊视频，内容丰富无广告，还有视频抽奖活动.@飞熊视频" + mUrl);
		oks.setImageUrl(mcCenter.image);
		oks.setUrl(mUrl);
		oks.setFilePath(testImage);
		oks.setComment(getString(R.string.share));
		oks.setSite(getString(R.string.app_name));
		oks.setSiteUrl(mUrl);
		oks.setVenueName("ShareSDK");
		oks.setVenueDescription("This is a beautiful place!");
		oks.setLatitude(23.056081f);
		oks.setLongitude(113.385708f);
		oks.disableSSOWhenAuthorize();
		oks.show(ActivityExplorerWeb.this);
	}*/

	/*protected void initImagePath() {
		try {
			String cachePath = cn.sharesdk.framework.utils.R.getCachePath(this, null);
			testImage = cachePath + "/share_pic.jpg";
			File file = new File(testImage);
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable.share_pic);
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			testImage = null;
		}
	}*/

	private void initShare() {
		//ShareSDK.initSDK(this);
		new Thread() {
			public void run() {
				//initImagePath();
			}
		}.start();
	}
}

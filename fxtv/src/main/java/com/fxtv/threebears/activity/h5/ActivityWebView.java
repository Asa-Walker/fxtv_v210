package com.fxtv.threebears.activity.h5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemShare;
import com.fxtv.threebears.R;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemPreference;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.ShareDialog;

public class ActivityWebView extends BaseActivity {
    private WebView mWebView;
    private String mUrl, mLinkUrl;
    private int mCount = 0;
    //    private OnekeyShare oks;
    private String testImage;
    private String mShareTitle;
    private String mShareImg;
    private boolean mCanShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mUrl = getIntent().getStringExtra("url");
        mShareTitle = getIntent().getStringExtra("share_title");
        mShareImg = getIntent().getStringExtra("share_img");
        mCanShare = getIntent().getBooleanExtra("share_enable", true);
        if (TextUtils.isEmpty(mUrl)) {
            finish();
        }
//        else {
//            StringBuilder builder = new StringBuilder(mUrl);
//            if (mUrl.contains("?")) {
//                builder.append("&uc=").append(getSystem(SystemPreference.class).getUC(2));
//            } else {
//                builder.append("?uc=").append(getSystem(SystemPreference.class).getUC(2));
//            }
//            mLinkUrl = builder.toString();
        initView();
//        }
    }

    private void initView() {
        Utils.showProgressDialog(this);
        initActionBar();
        mWebView = (WebView) findViewById(R.id.activity_explorer_web);
        // 设置WebView属性，能够执行Javascript脚本
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 加载需要显示的网页
        mWebView.loadUrl(getUrl());
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
                    ActivityWebView.this.finish();
                } else {
                    if (url != null && url.indexOf("fxtv://") == 0) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, (Uri.parse(url))).addCategory(
                                Intent.CATEGORY_BROWSABLE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        view.loadUrl(url);
                    }
                    mCount++;
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Utils.dismissProgressDialog();
                mTitleView.setText(view.getTitle());
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

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    private TextView mTitleView;

    private void initActionBar() {
        mTitleView = ((TextView) findViewById(R.id.ab_title));
        TextView colse = (TextView) findViewById(R.id.close);
        colse.setVisibility(View.VISIBLE);
        colse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView shareImage = (ImageView) findViewById(R.id.ab_right_img1);
        if (mCanShare) {
            shareImage.setVisibility(View.VISIBLE);
            shareImage.setImageResource(R.drawable.icon_share_white);
            shareImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    share();
                }
            });
        } else {
            shareImage.setVisibility(View.GONE);
        }
    }

    /**
     * 添加分享内容
     */
    private void share() {
        ShareModel shareModel = new ShareModel();
        shareModel.shareUrl = mUrl;
        shareModel.shareTitle = mTitleView.getText().toString();
        shareModel.shareSummary = "飞熊视频,内容丰富无广告,还有视频抽奖活动。";
        shareModel.fileImageUrl = mShareImg;
        getSystem(SystemCommon.class).showShareDialog(ActivityWebView.this, shareModel, new ShareDialog.ShareCallBack() {
            @Override
            public void onShareSuccess() {
                showToast("分享成功");
            }

            @Override
            public void onShareFailure(String msg) {
                showToast(msg + "分享失败");
            }

            @Override
            public void onCancel() {
                showToast("取消分享");
            }
        });
//        oks.setTitle(mShareTitle);
//        oks.setTitleUrl(mUrl);
//        oks.setText("飞熊视频，内容丰富无广告，还有视频抽奖活动.@飞熊视频" + mUrl);
//        oks.setImageUrl(mShareImg);
//        oks.setUrl(mUrl);
////		oks.setFilePath(testImage);
//        oks.setComment(getString(R.string.share));
//        oks.setSite(getString(R.string.app_name));
//        oks.setSiteUrl(mUrl);
//        oks.setVenueName("ShareSDK");
//        oks.setVenueDescription("This is a beautiful place!");
//        oks.setLatitude(23.056081f);
//        oks.setLongitude(113.385708f);
//        oks.disableSSOWhenAuthorize();
//        oks.show(ActivityWebView.this);
    }

    protected void initImagePath() {
//		try {
//			String cachePath = cn.sharesdk.framework.utils.R.getCachePath(this, null);
//			testImage = cachePath + "/share_pic.jpg";
//			File file = new File(testImage);
//			if (!file.exists()) {
//				file.createNewFile();
//				Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable.share_pic);
//				FileOutputStream fos = new FileOutputStream(file);
//				pic.compress(CompressFormat.JPEG, 100, fos);
//				fos.flush();
//				fos.close();
//			}
//		} catch (Throwable t) {
//			t.printStackTrace();
//			testImage = null;
//		}
    }

    private void initShare() {
        //ShareSDK.initSDK(this);
//        new Thread() {
//            public void run() {
//                initImagePath();
//            }
//        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    private String getUrl() {
        StringBuilder builder = new StringBuilder(mUrl);
        String params = "";
        if (SystemManager.getInstance().getSystem(SystemUser.class).isLogin()) {
            params = "uc=" + getSystem(SystemPreference.class).getUC(2);
        } else {
            params = "uc=" + getSystem(SystemPreference.class).getUC(2);
        }
        if (mUrl.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }
        return builder.append(params).toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SystemManager.getInstance().getSystem(SystemShare.class).onActivityResult(requestCode, resultCode, data);
    }
}

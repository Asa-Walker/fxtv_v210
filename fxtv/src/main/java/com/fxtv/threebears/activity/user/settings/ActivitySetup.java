package com.fxtv.threebears.activity.user.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemImageLoader;
import com.fxtv.framework.system.SystemPush;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.h5.ActivityWebView;
import com.fxtv.threebears.activity.user.other.ActivitySuggestion;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemCommon.ISystemCommonCallBack;
import com.fxtv.threebears.system.SystemConfig;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.view.MyDialog;
import com.umeng.message.PushAgent;

public class ActivitySetup extends BaseActivity {

    private RadioGroup mDownLoadMode, mVideoPlayMode, mReceiveMode;
    private TextView mLogOut, mCacheSize, mVersion;

    private String cacheSizeString;
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            mCacheSize.setText(cacheSizeString);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setup);
        initView();
    }

    private void initView() {
        mCacheSize = (TextView) findViewById(R.id.activity_my_setup_cache);
        setVersion();
        calculate();
        initActionbar();
        initRadioGroup();
        setListener();
    }

    /**
     * 设置版本
     */
    private void setVersion() {
        String version = "";
        version = FrameworkUtils.getVersion(this);
        mVersion = (TextView) findViewById(R.id.activity_my_setup_check_update);
        if (!"".equals(version)) {
            mVersion.setText("检查更新(当前版本:" + version + ")");
        } else {
            mVersion.setText("检查更新");
        }
    }

    private void calculate() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                cacheSizeString = FrameworkUtils.Files.getFileOrFilesSize(getSystem(SystemImageLoader.class).getCachePath(), 3)
                        + "M";
                mHandler.sendEmptyMessage(0);
            }

            ;
        });
        thread.start();
    }

    private void setListener() {
        mLogOut = (TextView) findViewById(R.id.activity_setup_logout);
        if (getSystem(SystemUser.class).isLogin()) {
            mLogOut.setVisibility(View.VISIBLE);
        } else {
            mLogOut.setVisibility(View.GONE);
        }
        mLogOut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                initDialog("您确定要退出当前帐号么？");
            }
        });
        // 意见反馈
        findViewById(R.id.activity_my_setup_suggestion).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FrameworkUtils.skipActivity(ActivitySetup.this, ActivitySuggestion.class);
            }
        });

        findViewById(R.id.activity_my_setup_user_argeement).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://www.feixiong.tv/sm/yhxy.html");
                bundle.putBoolean("share_enable", false);
                FrameworkUtils.skipActivity(ActivitySetup.this, ActivityWebView.class, bundle);
            }
        });

        findViewById(R.id.activity_my_setup_check_update).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getSystem(SystemCommon.class).checkVersion(ActivitySetup.this,true);
            }
        });

        findViewById(R.id.activity_my_setup_clear_cache).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog("是否清除图片缓存");
            }
        });

        findViewById(R.id.activity_my_setup_about_us).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://www.feixiong.tv/sm/gywm.html");
                bundle.putBoolean("share_enable", false);
                FrameworkUtils.skipActivity(ActivitySetup.this, ActivityWebView.class, bundle);
            }
        });

        // findViewById(R.id.activity_my_setup_difinition).setOnClickListener(new
        // OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // FrameworkUtils.skipActivity(ActivitySetup.this,
        // ActivityChooseDifinition.class);
        // }
        // });
        findViewById(R.id.activity_my_setup_save_path).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FrameworkUtils.skipActivity(ActivitySetup.this, ActivitySavaPath.class);
            }
        });
    }

    private void initRadioGroup() {
        mDownLoadMode = (RadioGroup) findViewById(R.id.activity_my_setup_radiogroup_wifi);
        mVideoPlayMode = (RadioGroup) findViewById(R.id.activity_my_setup_radiogroup_2G);
        mReceiveMode = (RadioGroup) findViewById(R.id.activity_my_setup_radiogroup_receive_message);
        if (getSystem(SystemConfig.class).mCanDownloadUnderFlowEvn) {
            ((RadioButton) mDownLoadMode.getChildAt(1)).setChecked(true);
            mDownLoadMode.setBackgroundResource(R.drawable.wifi_shape_on);
        } else {
            ((RadioButton) mDownLoadMode.getChildAt(0)).setChecked(true);
            mDownLoadMode.setBackgroundResource(R.drawable.wifi_shape_off);
        }

        if (getSystem(SystemConfig.class).mCanPlayUnderFlowEvn) {
            ((RadioButton) mVideoPlayMode.getChildAt(1)).setChecked(true);
            mVideoPlayMode.setBackgroundResource(R.drawable.wifi_shape_on);
        } else {
            ((RadioButton) mVideoPlayMode.getChildAt(0)).setChecked(true);
            mVideoPlayMode.setBackgroundResource(R.drawable.wifi_shape_off);
        }
        if (getSystem(SystemConfig.class).mCanReceiveMessage) {
            ((RadioButton) mReceiveMode.getChildAt(1)).setChecked(true);
            mReceiveMode.setBackgroundResource(R.drawable.wifi_shape_on);
        } else {
            ((RadioButton) mReceiveMode.getChildAt(0)).setChecked(true);
            mReceiveMode.setBackgroundResource(R.drawable.wifi_shape_off);
        }

        mDownLoadMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.activity_my_setup_wifi_on == checkedId) {
                    FrameworkUtils
                            .showToast(ActivitySetup.this, getString(R.string.notice_you_can_download_under_wifi));
                    mDownLoadMode.setBackgroundResource(R.drawable.wifi_shape_on);

                    getSystem(SystemConfig.class).setMayDownloadUnderFlowEnv(true);
                } else {
                    FrameworkUtils.showToast(ActivitySetup.this,
                            getString(R.string.notice_you_can_download_not_under_wifi));
                    mDownLoadMode.setBackgroundResource(R.drawable.wifi_shape_off);

                    getSystem(SystemConfig.class).setMayDownloadUnderFlowEnv(false);
                }
            }
        });

        mVideoPlayMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.activity_my_setup_2G_on == checkedId) {
                    mVideoPlayMode.setBackgroundResource(R.drawable.wifi_shape_on);
                    FrameworkUtils.showToast(ActivitySetup.this,
                            getString(R.string.notice_you_can_download_or_playvideo_under_3g4gnet));

                    getSystem(SystemConfig.class).setMayPlayUnderFlowEnv(true);
                } else {
                    mVideoPlayMode.setBackgroundResource(R.drawable.wifi_shape_off);
                    FrameworkUtils.showToast(ActivitySetup.this,
                            getString(R.string.notice_you_can_not_download_or_playvideo_under_3g4gnet));

                    getSystem(SystemConfig.class).setMayPlayUnderFlowEnv(false);
                }
            }
        });

        mReceiveMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.activity_my_setup_receive_message_on == checkedId) {
                    mReceiveMode.setBackgroundResource(R.drawable.wifi_shape_on);
                    FrameworkUtils.showToast(ActivitySetup.this, getString(R.string.notice_you_can_receive_message));
                    SystemManager.getInstance().getSystem(SystemPush.class).openPush();
                    getSystem(SystemConfig.class).setMayReceive(true);
                } else {
                    mReceiveMode.setBackgroundResource(R.drawable.wifi_shape_off);
                    FrameworkUtils
                            .showToast(ActivitySetup.this, getString(R.string.notice_you_can_not_receive_message));
                    SystemManager.getInstance().getSystem(SystemPush.class).closePush();
                    getSystem(SystemConfig.class).setMayReceive(false);
                }
            }
        });
    }

    private void initActionbar() {
        TextView title = (TextView) findViewById(R.id.ab_title);
        title.setText("设置");

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

    public void initDialog(final String str) {
        getSystem(SystemCommon.class)
                .showDialog(ActivitySetup.this,"提示", str, new MyDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, View view, String value) {

                        if ("您确定要退出当前帐号么？".equals(str)) {
                            new Thread() {
                                public void run() {
                                    PushAgent mPushAgent = PushAgent.getInstance(ActivitySetup.this);
                                    try {
                                        // 从友盟删除用户
                                        PushAgent.getInstance(ActivitySetup.this).removeAlias(
                                                getSystem(SystemUser.class).mUser.user_id,
                                                "userId");
                                        mPushAgent.getTagManager().reset();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                ;
                            }.start();
                            getSystem(SystemUser.class).logout(new ISystemCommonCallBack() {

                                @Override
                                public void onResult(boolean result, String arg) {
                                    if (result) {
                                        FrameworkUtils.showToast(ActivitySetup.this, "退出成功");
                                        mLogOut.setVisibility(View.GONE);
                                    } else {
                                        showToast("退出失败");
                                    }
                                }
                            });
                        } else if ("是否清除图片缓存".equals(str)) {
//							FrameworkUtils.Files
//									.delAllFile(SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mImageCacheDir);
//							mCacheSize.setText(FrameworkUtils.Files.getFileOrFilesSize(SystemManager.getInstance()
//									.getSystem(SystemFrameworkConfig.class).mImageCacheDir, 3)
//									+ "M");
                            //FrameworkUtils.Files.delAllFile(ActivitySetup.this.getExternalCacheDir() + "/fxtv/images");
                            FrameworkUtils.Files.delAllFile(getSystem(SystemImageLoader.class).getCachePath());
                            mCacheSize.setText(FrameworkUtils.Files.getFileOrFilesSize(getSystem(SystemImageLoader.class).getCachePath(), 3)
                                    + "M");
                        }
                        dialog.dismiss();
                    }

                }, new MyDialog.OnClickListener() {

                    @Override
                    public void onClick(Dialog dialog, View view, String value) {
                        dialog.dismiss();
                    }
                });
    }

}

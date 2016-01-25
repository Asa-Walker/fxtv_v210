package com.fxtv.threebears.activity.user.settings;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.threebears.R;
import com.fxtv.threebears.system.SystemConfig;

public class ActivitySavaPath extends BaseActivity {

    private Drawable mDrawable;
    private TextView mRom, mSD;

    private boolean mCurrentIsRom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawable = getResources().getDrawable(R.drawable.vote_check);
        if(mDrawable!=null)
            mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        setContentView(R.layout.activity_save_path);
        initView();

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
        initActionBar();
        mRom = (TextView) findViewById(R.id.mobile_rom);
        mSD = (TextView) findViewById(R.id.mobile_SD);
        if (getSystem(SystemConfig.class).mSaveSDCard) {
            mCurrentIsRom = false;
            changeSelected(mSD, mRom);
        } else {
            mCurrentIsRom = true;
            changeSelected(mRom, mSD);
        }

        // 内部存储
        mRom.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mCurrentIsRom) {
                    boolean result = getSystem(SystemConfig.class).setDownloadPosition(
                            SystemConfig.DOWNLOAD_POSITION_INTERNAL);
                    if (result) {
                        changeSelected(mRom, mSD);
                        mCurrentIsRom = true;
                    }
                }

            }
        });
        // SD卡存储
        mSD.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCurrentIsRom) {
                    boolean result = getSystem(SystemConfig.class).setDownloadPosition(
                            SystemConfig.DOWNLOAD_POSITION_SDCARD);
                    if (result) {
                        mCurrentIsRom = false;
                        changeSelected(mSD, mRom);
                    } else {
                        showToast("外置存储卡无法使用");
                    }
                }
            }
        });
    }

    private void changeSelected(TextView select, TextView notSelected) {
        select.setCompoundDrawables(null, null, mDrawable, null);
        notSelected.setCompoundDrawables(null, null, null, null);
    }

    private void initActionBar() {
        TextView title = (TextView) findViewById(R.id.ab_title);
        title.setText("视频缓存路径");

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
}

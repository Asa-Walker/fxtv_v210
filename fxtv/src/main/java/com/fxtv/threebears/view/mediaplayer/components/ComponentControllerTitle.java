package com.fxtv.threebears.view.mediaplayer.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.widget.BatteryView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.view.mediaplayer.MediaController;

import java.text.SimpleDateFormat;

public class ComponentControllerTitle {

    private MediaController mController;
    private ViewGroup mParentView, mChildView;
    private LayoutInflater mInflater;
    private ImageView mBack, mVideoProportion;
    private TextView mTitle, mTime;
    private BatteryView mBattery;

    private boolean mIsLandscape;

    public ComponentControllerTitle(MediaController controller, ViewGroup parent, LayoutInflater inflater) {
        this.mController = controller;
        this.mParentView = parent;
        this.mInflater = inflater;
        initView();
    }

    public void show() {
        mChildView.setVisibility(View.VISIBLE);
        update();
    }

    public void hide() {
        mChildView.setVisibility(View.GONE);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    private void initView() {
        mChildView = (ViewGroup) mInflater.inflate(R.layout.mediaplayer_controller_title_layout, mParentView);

        mBack = (ImageView) mChildView.findViewById(R.id.back);
        mTitle = (TextView) mChildView.findViewById(R.id.title);
        mTime = (TextView) mChildView.findViewById(R.id.time);
        mBattery = (BatteryView) mChildView.findViewById(R.id.batter);
        mVideoProportion = (ImageView) mChildView.findViewById(R.id.video_proportion);
        mTime.setText(getTime());
        mBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mController.onBackPressed();
            }
        });
        mVideoProportion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.showChooseVideoProportion();
            }
        });

        batteryLevel();
    }

    private void update() {
        mTime.setText(getTime());
        if (mIsLandscape) {
            mBattery.setVisibility(View.VISIBLE);
            mTime.setVisibility(View.VISIBLE);
            mVideoProportion.setVisibility(View.VISIBLE);
        } else {
            mTime.setVisibility(View.INVISIBLE);
            mBattery.setVisibility(View.INVISIBLE);
            mVideoProportion.setVisibility(View.INVISIBLE);
        }
    }

    private String getTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm");
        return sDateFormat.format(new java.util.Date());
    }

    private void batteryLevel() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int rawlevel = intent.getIntExtra("level", -1);// 获得当前电量
                int scale = intent.getIntExtra("scale", -1);
                // 获得总电量
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                mBattery.setPower(level);
                // mController.getActivity().unregisterReceiver(this);
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mController.getActivity().registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    public void setScreenOrientation(boolean isLandscape) {
        mIsLandscape = isLandscape;
        update();
    }
}

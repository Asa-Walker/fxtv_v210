package com.fxtv.framework.system;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fxtv.framework.Logger;
import com.fxtv.framework.R;
import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.frame.SystemManager;
import com.umeng.analytics.MobclickAgent;

/**
 * @author FXTV-Android
 */
public class SystemOther extends SystemBase {
    private static Dialog mProgressDialog;

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void destroy() {
        super.destroy();
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    public void showProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.progressbar, null);// 得到加载view
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.progress_bar_img);
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(mContext, R.anim.progress_bar);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        mProgressDialog = new Dialog(SystemManager.getInstance().getSystem(SystemPage.class).getCurrActivity(),
                R.style.loading_dialog);// 创建自定义样式dialog
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setContentView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        mProgressDialog.show();
    }

    public static void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}

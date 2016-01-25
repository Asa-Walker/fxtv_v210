package com.fxtv.framework.system;

import android.content.Context;

import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.frame.SystemManager;
import com.umeng.analytics.MobclickAgent;

/**
 * 崩溃log收集系统
 *
 * @author FXTV-Android
 */
public class SystemCrashLogCollect extends SystemBase {

    @Override
    protected void init() {
        super.init();
        // umeng
        MobclickAgent.setCatchUncaughtExceptions(SystemManager.getInstance()
                .getSystem(SystemFrameworkConfig.class).mCrashLogNet);
    }

    @Override
    protected void destroy() {
        super.destroy();
    }

    public void activityOnCreate() {
    }
}

package com.fxtv.framework.system;

import android.app.Activity;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 界面管理系统
 *
 * @author FXTV-Android
 */
public class SystemPage extends SystemBase {
    private static final String TAG = "SystemPage";

    private List<Activity> mList;
    private Activity mCurrActivity;

    @Override
    protected void init() {
        super.init();
        mList = new ArrayList<Activity>();
    }

    @Override
    protected void destroy() {
        super.destroy();
        finishAllActivity();
        mList.clear();
        mList = null;
        mCurrActivity = null;
    }

    public void addActivity(Activity activity) {
        if (activity != null) {
            Logger.d(TAG, "addActivity name=" + activity.getClass().getSimpleName());
            mList.add(activity);
            mCurrActivity = activity;
        }
    }

    public void finishActivity(Activity activity) {
        if (activity != null) {
            Logger.d(TAG, "finishActivity name=" + activity.getClass().getSimpleName());
            activity.finish();
        }
    }

    public void finishActivityNotRemove(Activity activity) {
        if (activity != null) {
            Logger.d(TAG, "finishActivity name=" + activity.getClass().getSimpleName());
            activity.finish();
        }
    }

    public void finishAllActivity() {
            for (Activity activity : mList) {
//                    finishActivityNotRemove(activity);
                    finishActivity(activity);
                    Logger.d("debug", "finishAllActivity name="+activity.getClass().getSimpleName());
            }
            mList.clear();
    }

    public Activity getCurrActivity() {
        return mCurrActivity;
    }

}

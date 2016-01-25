package com.fxtv.framework.system;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.fxtv.framework.R;
import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.frame.SystemManager;

/**
 * @author FXTV-Android
 */
public class SystemFragmentManager extends SystemBase {

    private HashMap<Context, FragmentManager> mFragmentManagerPool;

    @Override
    protected void init() {
        super.init();
        mFragmentManagerPool = new HashMap<Context, FragmentManager>();
    }

    @Override
    protected void destroy() {
        super.destroy();
        mFragmentManagerPool.clear();
        mFragmentManagerPool = null;
    }

    /**
     * 带过度动画的Fragment
     *
     * @param context
     * @param containerViewId
     * @param className
     */
    public void addAnimFragment(final int containerViewId, final String className, Activity activity) {
        try {
            String tag = className;
            FragmentManager fragmentManager = getFragmentManager(activity);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            Fragment currentFragment = getCurrentFragment(fragmentManager);

            if (fragment == null) {
                fragment = (Fragment) Class.forName(className).newInstance();
                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }
                currentFragment = fragment;
                transaction.add(containerViewId, fragment, tag);

            } else {
                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }
                currentFragment = fragment;
            }
            transaction.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
            transaction.show(fragment);
            transaction.commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 带过度动画的Fragment
     *
     * @param
     * @param containerViewId
     * @param className
     */
    public void addAnimFragment(final int containerViewId, final String className, final Bundle bundle, Activity activity) {
        Fragment fragment = null;
        try {
            FragmentManager fragmentManager = getFragmentManager(activity);
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            fragment = (Fragment) Class.forName(className).newInstance();
            fragment.setArguments(bundle);
            transaction.add(containerViewId, fragment, className);
            transaction.setCustomAnimations(R.anim.fragment_in, 0, 0, R.anim.fragment_out);
            transaction.show(fragment);
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

//            transaction.setCustomAnimations(R.anim.fragment_in, 0, 0, R.anim.fragment_out);
//            Log.i("aaaa", "1");
//            transaction.show(fragment);
//            Log.i("aaaa", "2");
//            transaction.commitAllowingStateLoss();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.i("aaaa", "e=" + e);
//        }
    }

    public void addAnimFragment(final int containerViewId, final String className, final Bundle bundle) {
        try {
            String tag = className;
            FragmentManager fragmentManager = getFragmentManager(getCurrentActivity());
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            Fragment currentFragment = getCurrentFragment(fragmentManager);

            if (fragment == null) {
                fragment = (Fragment) Class.forName(className).newInstance();
                fragment.setArguments(bundle);

                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }
                currentFragment = fragment;
                transaction.add(containerViewId, fragment, tag);

            } else {
                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }
                currentFragment = fragment;
            }

            transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
            transaction.setCustomAnimations(R.anim.fragment_in, 0, 0, R.anim.fragment_out);
            transaction.show(fragment);
            transaction.commitAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Fragment addAloneFragment(final int containerViewId, final String className, final Bundle bundle) {
        Fragment fragment = null;
        try {
            FragmentManager fragmentManager = getFragmentManager(getCurrentActivity());
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            fragment = (Fragment) Class.forName(className).newInstance();
            fragment.setArguments(bundle);
            transaction.add(containerViewId, fragment);
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fragment;

    }

    public Fragment addAloneFragment(final int containerViewId, final String className, final Bundle bundle, Context context) {
        Fragment fragment = null;
        try {
            FragmentManager fragmentManager = getFragmentManager(context);
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            fragment = (Fragment) Class.forName(className).newInstance();
            fragment.setArguments(bundle);
            transaction.add(containerViewId, fragment);
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fragment;

    }

    public void destoryFragmentManager(Context context) {
        mFragmentManagerPool.remove(context);
    }

    private FragmentManager getFragmentManager(Context context) {
        FragmentManager fragmentManager = null;
        if ((fragmentManager = mFragmentManagerPool.get(context)) != null && !fragmentManager.isDestroyed()) {
            return fragmentManager;
        } else {
            return createFragmentManager(context);
        }
    }

    public FragmentTransaction getTransaction(Context context) {
        return getFragmentManager(context).beginTransaction();
    }

    private FragmentManager createFragmentManager(Context context) {
        FragmentManager fragmentManager = null;
        fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        mFragmentManagerPool.put(context, fragmentManager);
        return fragmentManager;
    }

    private Fragment getCurrentFragment(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null && fragments.size() != 0) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    public void hideFragment(Context context, String fragmentName) {
        FragmentManager manager = getFragmentManager(context);
        Fragment fragment = manager.findFragmentByTag(fragmentName);
        FragmentTransaction transaction = getTransaction(context);
        transaction.hide(fragment).commit();
    }

    private Activity getCurrentActivity() {
        return SystemManager.getInstance().getSystem(SystemPage.class).getCurrActivity();
    }
}

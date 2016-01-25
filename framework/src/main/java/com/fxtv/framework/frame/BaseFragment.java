package com.fxtv.framework.frame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.system.SystemAnalytics;

public abstract class BaseFragment extends Fragment{
	protected ViewGroup mRoot;
	protected SystemManager mSystemManager;
	protected LayoutInflater mLayoutInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSystemManager = SystemManager.getInstance();
	}

	@Override
	public void onResume() {
		super.onResume();
		mSystemManager.getSystem(SystemAnalytics.class).fragmentResume(getClass().getSimpleName());
	}

	@Override
	public void onPause() {
		super.onPause();
		mSystemManager.getSystem(SystemAnalytics.class).fragmentPause(getClass().getSimpleName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mLayoutInflater = inflater;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mRoot != null)
			mRoot.destroyDrawingCache();
		mRoot = null;
		mLayoutInflater = null;
	}

	public void showToast(String msg) {
		FrameworkUtils.showToast(getActivity(), msg);
	}

	protected <T extends SystemBase> T getSystem(Class<T> className) {
		return SystemManager.getInstance().getSystem(className);
	}
}

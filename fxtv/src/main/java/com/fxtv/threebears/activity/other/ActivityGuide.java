package com.fxtv.threebears.activity.other;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.threebears.R;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemCommon.ISystemCommonCallBack;
import com.fxtv.threebears.system.SystemPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * 开机引导页
 * 
 * @author 薛建浩
 * 
 */
public class ActivityGuide extends BaseActivity {
	private List<ImageView> mViewList;
	private ViewPager mViewPager;
	private int[] mImageArray = {R.drawable.boot_1, R.drawable.boot_2, R.drawable.boot_3};
	private boolean mUcIsReady;
	Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (getSystem(SystemPreference.class).isFirstLaunch()) {
			setContentView(R.layout.activity_advertisements);
			initView();
		} else {
			// 程序启动---->闪屏界面
			mUcIsReady = true;
			goLoading();
		}
	}

	private int counts;
	private void getUC() {
		getSystem(SystemCommon.class).getUCCode(2, false, new ISystemCommonCallBack() {
			@Override
			public void onResult(boolean result, String arg) {
				if (result) {
					mUcIsReady = true;
					getSystem(SystemPreference.class).setFirstLaunch(false);
				} else {
					showToast(arg + ",请检查网络");
					counts++;
					if (counts < 6) {
						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								getUC();
							}
						}, 3000);
					} else {
						showToast("请退出,重新进入App");
					}
				}
			}
		});
	}

	private void goLoading() {
		if (mUcIsReady) {
			FrameworkUtils.skipActivity(this, ActivityWelcome.class);
			finish();
		}
	}

	int mPageIndex;

	@SuppressWarnings("deprecation")
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.adv_viewpager);
		mViewList = new ArrayList<ImageView>();
		for (int i = 0; i < mImageArray.length; i++) {
			ImageView img = new ImageView(this);
			img.setScaleType(ScaleType.FIT_XY);
			img.setImageResource(mImageArray[i]);
			mViewList.add(img);
		}
		// 最后一张图片点击跳转到主界面
		mViewList.get(mViewList.size() - 1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goLoading();
			}
		});

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mPageIndex = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				if (arg0 == 0 && mPageIndex == 2) {
					goLoading();
				}
			}
		});
		mViewPager.setAdapter(new MyAdvAdapter());
	}

	class MyAdvAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return mViewList != null ? mViewList.size() : 0;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mViewList.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mViewList.get(position));
			return mViewList.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getSystem(SystemPreference.class).isFirstLaunch()) {
			counts = 0;
			getUC();
		}
	}
}

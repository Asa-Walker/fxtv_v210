package com.fxtv.threebears.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import java.io.Serializable;

/**
 * @author FXTV-Android
 * 
 *         1、悬浮 header
 * 
 *         2、ScrollView 嵌套 ViewPage 优化滑动
 * 
 */
public class ScrollViewSuspend extends ScrollView implements Serializable {

	private boolean canScroll;
	private GestureDetector mGestureDetector;
	View.OnTouchListener mGestureListener;

	private static final long serialVersionUID = 1L;
	private OnScrollListener onScrollListener;

	public ScrollViewSuspend(Context context) {
		this(context, null);
	}

	public ScrollViewSuspend(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScrollViewSuspend(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mGestureDetector = new GestureDetector(new YScrollDetector());
		canScroll = true;
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (onScrollListener != null) {
			onScrollListener.onScroll(t);
		}
	}

	public interface OnScrollListener {
		public void onScroll(int scrollY);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP)
			canScroll = true;
		return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
	}

	class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (canScroll)
				canScroll=Math.abs(distanceY) >= Math.abs(distanceX);
			return canScroll;
		}
	}
}

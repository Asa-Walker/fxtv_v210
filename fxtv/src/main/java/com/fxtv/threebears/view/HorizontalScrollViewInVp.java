package com.fxtv.threebears.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class HorizontalScrollViewInVp extends HorizontalScrollView {

	public HorizontalScrollViewInVp(Context context) {
		super(context);
	}

	public HorizontalScrollViewInVp(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent p_event) {
		if (p_event.getAction() == MotionEvent.ACTION_MOVE && getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}

		return super.onTouchEvent(p_event);
	}

}

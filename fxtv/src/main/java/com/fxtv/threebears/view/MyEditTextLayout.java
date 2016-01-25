package com.fxtv.threebears.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class MyEditTextLayout extends RelativeLayout {

	private PopupWindow mPop;

	public MyEditTextLayout(Context context) {
		super(context);
	}

	public MyEditTextLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyEditTextLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (mPop != null && mPop.isShowing()) {
				mPop.dismiss();
			}
		}
		return super.dispatchKeyEventPreIme(event);
	}

	public void setPop(PopupWindow pop) {
		mPop = pop;
	}

}

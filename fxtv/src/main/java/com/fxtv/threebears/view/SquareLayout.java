package com.fxtv.threebears.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by wzh on 2015/12/29.
 * 正方形layout
 */
public class SquareLayout extends RelativeLayout{
    public SquareLayout(Context context) {
        super(context);
    }
    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}

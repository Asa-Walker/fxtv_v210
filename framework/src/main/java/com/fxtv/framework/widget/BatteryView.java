package com.fxtv.framework.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.fxtv.framework.R;
import com.fxtv.framework.FrameworkUtils;

public class BatteryView extends View {
	private Context mContext;

	private int mPower = 100;

	private int mBatterLeft = 0;
	private int mBatterTop = 0;
	private int mBatteryWidth = 20;
	private int mBatterHeight = 10;

	private int mBatterHeadWidth = 3;
	private int mBatterHeadHeight = 4;

	private int mBatterInsideMargin = 1;

	private int mColorFrame = getResources().getColor(R.color.color_white);
	private int mColorContent = getResources().getColor(R.color.color_white);

	private Paint mPaintFrame;
	private Paint mPaintHeader;
	private Paint mPaintContent;

	private Rect mRectFrame;
	private Rect mRectContent;
	private Rect mRectHeader;

	public BatteryView(Context context) {
		this(context, null);
	}

	public BatteryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	private void initView() {
		mBatteryWidth = FrameworkUtils.dip2px(mContext, mBatteryWidth);
		mBatterHeight = FrameworkUtils.dip2px(mContext, mBatterHeight);
		mBatterHeadWidth = FrameworkUtils.dip2px(mContext, mBatterHeadWidth);
		mBatterHeadHeight = FrameworkUtils.dip2px(mContext, mBatterHeadHeight);
		mBatterInsideMargin = FrameworkUtils.dip2px(mContext, mBatterInsideMargin);

		mPaintFrame = new Paint();
		mPaintFrame.setColor(mColorFrame);
		mPaintFrame.setAntiAlias(true);
		mPaintFrame.setStyle(Style.STROKE);

		mPaintHeader = new Paint();
		mPaintHeader.setColor(mColorFrame);
		mPaintHeader.setAntiAlias(true);
		mPaintHeader.setStyle(Style.FILL_AND_STROKE);

		mPaintContent = new Paint();
		mPaintContent.setColor(mColorContent);
		mPaintContent.setAntiAlias(true);
		mPaintContent.setStyle(Style.FILL);

		mRectContent = new Rect();

		mRectFrame = new Rect(mBatterLeft, mBatterTop, mBatterLeft + mBatteryWidth, mBatterTop
				+ mBatterHeight);

		int h_left = mBatterLeft + mBatteryWidth;
		int h_top = mBatterTop + mBatterHeight / 2 - mBatterHeadHeight / 2;
		int h_right = h_left + mBatterHeadWidth;
		int h_bottom = h_top + mBatterHeadHeight;

		mRectHeader = new Rect(h_left, h_top, h_right, h_bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawRect(mRectFrame, mPaintFrame);

		float power_percent = mPower / 100.0f;
		// 画电量
		if (power_percent != 0) {
			int p_left = mBatterLeft + mBatterInsideMargin;
			int p_top = mBatterTop + mBatterInsideMargin;
			int p_right = p_left - mBatterInsideMargin
					+ (int) ((mBatteryWidth - mBatterInsideMargin) * power_percent);
			int p_bottom = p_top + mBatterHeight - mBatterInsideMargin * 2;
			mRectContent.left = p_left;
			mRectContent.top = p_top;
			mRectContent.right = p_right;
			mRectContent.bottom = p_bottom;
			canvas.drawRect(mRectContent, mPaintContent);
		}

		// 画电池头
		canvas.drawRect(mRectHeader, mPaintHeader);
	}

	public void setPower(int power) {
		mPower = power;
		if (mPower < 0) {
			mPower = 0;
		}
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width, height;

		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width = getPaddingLeft() + getPaddingRight() + mBatteryWidth + mBatterHeadWidth
					+ mBatterInsideMargin;
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = getPaddingTop() + getPaddingBottom() + mBatterHeight;
		}

		setMeasuredDimension(width, height);
	}
}

package com.fxtv.threebears.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FixGridLayout extends ViewGroup {
	private int mCellWidth;
	private int mCellHeight;

	public FixGridLayout(Context context) {
		super(context);
	}

	public FixGridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FixGridLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setmCellWidth(int w) {
		mCellWidth = w;
		requestLayout();
	}

	public void setmCellHeight(int h) {
		mCellHeight = h;
		requestLayout();
	}

	/**
	 * �����ӿؼ��Ļ���
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int cellWidth = mCellWidth;
		int cellHeight = mCellHeight;
		int columns = (r - l) / cellWidth;
		if (columns < 0) {
			columns = 1;
		}
		int x = 0;
		int y = 0;
		int i = 0;
		int count = getChildCount();
		for (int j = 0; j < count; j++) {
			final View childView = getChildAt(j);
			// ��ȡ�ӿؼ�Child�Ŀ��
			int w = childView.getMeasuredWidth();
			int h = childView.getMeasuredHeight();
			// �����ӿؼ��Ķ�������
			int left = x + ((cellWidth - w) / 2);
			int top = y + ((cellHeight - h) / 2);
			// int left = x;
			// int top = y;
			// �����ӿؼ�
			childView.layout(left, top, left + w, top + h);

			if (i >= (columns - 1)) {
				i = 0;
				x = 0;
				y += cellHeight;
			} else {
				i++;
				x += cellWidth;

			}
		}
	}

	/**
	 * ����ؼ����ӿؼ���ռ����
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// ������������
		int cellWidthSpec = MeasureSpec.makeMeasureSpec(mCellWidth,
				MeasureSpec.EXACTLY);
		int cellHeightSpec = MeasureSpec.makeMeasureSpec(mCellHeight,
				MeasureSpec.EXACTLY);
		// ��¼ViewGroup��Child���ܸ���
		int count = getChildCount();
		// �����ӿռ�Child�Ŀ��
		for (int i = 0; i < count; i++) {
			View childView = getChildAt(i);
			/*
			 * 090 This is called to find out how big a view should be. 091 The
			 * parent supplies constraint information in the width and height
			 * parameters. 092 The actual mesurement work of a view is performed
			 * in onMeasure(int, int), 093 called by this method. 094 Therefore,
			 * only onMeasure(int, int) can and must be overriden by subclasses.
			 * 095
			 */
			childView.measure(cellWidthSpec, cellHeightSpec);
		}
		// ���������ؼ���ռ�����С
		// ע��setMeasuredDimension��resolveSize���÷�
		int colums = (int) Math.ceil(count / 3.0);
		setMeasuredDimension(resolveSize(mCellWidth * count, widthMeasureSpec),
				resolveSize(mCellHeight * colums, heightMeasureSpec));
		// setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

		// ����Ҫ���ø���ķ���
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * Ϊ�ؼ���ӱ߿�
	 */
	// @Override
	// protected void dispatchDraw(Canvas canvas) {
	// // ��ȡ���ֿؼ����
	// int width = getWidth();
	// int height = getHeight();
	// // ��������
	// Paint mPaint = new Paint();
	// // ���û��ʵĸ�������
	// mPaint.setColor(Color.BLUE);
	// mPaint.setStyle(Paint.Style.STROKE);
	// mPaint.setStrokeWidth(10);
	// mPaint.setAntiAlias(true);
	// // �������ο�
	// Rect mRect = new Rect(0, 0, width, height);
	// // ���Ʊ߿�
	// canvas.drawRect(mRect, mPaint);
	// // ��������ø���ķ���
	// super.dispatchDraw(canvas);
	// }

}

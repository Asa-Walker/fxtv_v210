package com.fxtv.threebears.view.wheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.LinearLayout;

import com.fxtv.threebears.R;

public class WheelView extends TosGallery {
	/**
	 * The selector.
	 */
	private Drawable mSelectorDrawable = null;

	/**
	 * The bound rectangle of selector.
	 */
	private Rect mSelectorBound = new Rect();

	/**
	 * The top shadow.
	 */
	private GradientDrawable mTopShadow = null;

	/**
	 * The bottom shadow.
	 */
	private GradientDrawable mBottomShadow = null;

	/**
	 * Shadow colors
	 */
	private static final int[] SHADOWS_COLORS = { 0x00111111, 0x00AAAAAA, 0x00AAAAAA };

	/**
	 * The constructor method.
	 * 
	 * @param context
	 */
	public WheelView(Context context) {
		this(context, null);
	}

	/**
	 * The constructor method.
	 * 
	 * @param context
	 * @param attrs
	 */
	public WheelView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	private int mSelectorResource;

	/**
	 * 用来判断中心方块的样式
	 */
	private boolean mflag;

	// Item height
	private int mItemHeight = 0;

	private LinearLayout mItemsLayout;

	/**
	 * 可是item的默认数量5
	 */
	private int mVisibleItems = 5;

	/**
	 * The constructor method.
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.wheel);// TypedArray是一个数组容器
			mSelectorResource = a.getResourceId(R.styleable.wheel_selectbg, R.drawable.wheel_val);
			mflag = a.getBoolean(R.styleable.wheel_change, false);
			a.recycle();
		} else {
			mSelectorResource = R.drawable.wheel_val;
		}

		initialize(context);
	}

	/**
	 * Initialize.
	 * 
	 * @param context
	 */
	private void initialize(Context context) {
		this.setVerticalScrollBarEnabled(false);
		this.setSlotInCenter(true);
		this.setOrientation(TosGallery.VERTICAL);
		this.setGravity(Gravity.CENTER_HORIZONTAL);
		this.setUnselectedAlpha(1.0f);

		// This lead the onDraw() will be called.
		this.setWillNotDraw(false);

		// The selector rectangle drawable.
		this.mSelectorDrawable = getContext().getResources().getDrawable(mSelectorResource);
		this.mTopShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
		this.mBottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);

		// The default background.

		// Disable the sound effect default.
		this.setSoundEffectsEnabled(false);
	}

	/**
	 * Called by draw to draw the child views. This may be overridden by derived
	 * classes to gain control just before its children are drawn (but after its
	 * own view has been drawn).
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		// After draw child, we do the following things:
		// +1, Draw the center rectangle.
		// +2, Draw the shadows on the top and bottom.

		drawCenterRect(canvas);

		drawShadows(canvas);
	}

	/**
	 * setOrientation
	 */
	@Override
	public void setOrientation(int orientation) {
		if (TosGallery.HORIZONTAL == orientation) {
			throw new IllegalArgumentException("The orientation must be VERTICAL");
		}

		super.setOrientation(orientation);
	}

	/**
	 * Call when the ViewGroup is layout.
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		int galleryCenter = getCenterOfGallery();
		View v = this.getChildAt(0);

		int height = (null != v) ? v.getMeasuredHeight() : 50;
		int top = galleryCenter - height / 2;
		int bottom = top + height;

		mSelectorBound.set(getPaddingLeft(), top, getWidth() - getPaddingRight(), bottom);
	}

	/**
	 * @see com.nj1s.lib.widget.TosGallery#setSelectedPositionInt(int)
	 */
	@Override
	protected void selectionChanged() {
		super.selectionChanged();

		playSoundEffect(SoundEffectConstants.CLICK);
	}

	/**
	 * Draw the selector drawable.
	 * 
	 * @param canvas
	 */
	private void drawCenterRect(Canvas canvas) {
		if (mflag) {
			int center = getHeight() / 2;
			int offset = getItemHeight() / 2 ;
			/*
			 * / Remarked by wulianghuan 2014-11-27 使用自己的画线，而不是描边 Rect rect =
			 * new Rect(left, top, right, bottom)
			 * centerDrawable.setBounds(bounds) centerDrawable.setBounds(0,
			 * center - offset, getWidth(), center + offset);
			 * centerDrawable.draw(canvas); //
			 */
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.province_line_border));
			// 设置线宽
			paint.setStrokeWidth((float) 3);
			// 绘制上边直线
			canvas.drawLine(0, center - offset, getWidth(), center - offset, paint);
			// 绘制下边直线
			canvas.drawLine(0, center + offset, getWidth(), center + offset, paint);
		} else {
			if (null != mSelectorDrawable) {
				mSelectorDrawable.setBounds(mSelectorBound);
				mSelectorDrawable.draw(canvas);
			}
		}

	}

	private int getItemHeight() {

		if (mItemHeight != 0) {
			return mItemHeight;
		}

		if (mItemsLayout != null && mItemsLayout.getChildAt(0) != null) {
			mItemHeight = mItemsLayout.getChildAt(0).getHeight();
			return mItemHeight;
		}

		return getHeight() / mVisibleItems;
	}

	/**
	 * Draw the shadow
	 * 
	 * @param canvas
	 */
	private void drawShadows(Canvas canvas) {
		int height = (int) (2.0 * mSelectorBound.height());
		mTopShadow.setBounds(0, 0, getWidth(), height);
		mTopShadow.draw(canvas);

		mBottomShadow.setBounds(0, getHeight() - height, getWidth(), getHeight());
		mBottomShadow.draw(canvas);
	}
}

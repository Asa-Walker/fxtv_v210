package com.fxtv.threebears.view.mediaplayer.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.threebears.R;
import com.fxtv.threebears.view.mediaplayer.MediaController;

import java.util.Locale;

public class ComponentControllerTips {
	private static final String TAG = "ComponentControllerTips";

	private ViewGroup mParentView, mChildView;
	private LayoutInflater mInflater;

	private ViewGroup mBufferView, mSeekView;

	private boolean mIsBufferShowing, mIsSeekShowing;
	private TextView mBufferMsg;

	private ImageView mSeekFlag;
	private TextView mSeekTv;
	private int mDuration;

	public ComponentControllerTips(MediaController controller, ViewGroup parent, LayoutInflater inflater) {
		this.mParentView = parent;
		this.mInflater = inflater;
		initView();
	}

	public void setDuration(int duration) {
		this.mDuration = duration;
	}

	public void showBuffer(String msg) {
		mIsBufferShowing = true;
		mChildView.setVisibility(View.VISIBLE);
		mBufferView.setVisibility(View.VISIBLE);
		mBufferMsg.setText(msg);
	}

	public void hideBuffer() {
		mIsBufferShowing = false;
		mBufferView.setVisibility(View.GONE);
		if (!mIsSeekShowing) {
			mChildView.setVisibility(View.GONE);
		}
	}

	public void showSeek(long pos, boolean isForward) {
		mChildView.setVisibility(View.VISIBLE);
		// mSeekView.setVisibility(View.VISIBLE);
		if (isForward) {
			// forward
			mSeekFlag.setImageResource(R.drawable.icon_player_forward);
		} else {
			// backward
			mSeekFlag.setImageResource(R.drawable.icon_player_backward);
		}
		mSeekTv.setText(generateTime(pos) + "/" + generateTime(mDuration));
		Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
		alphaAnimation.setStartOffset(2000);
		alphaAnimation.setDuration(500);
		mSeekView.startAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				mSeekView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mSeekView.setVisibility(View.GONE);
				if (!mIsBufferShowing) {
					mChildView.setVisibility(View.GONE);
				}
			}
		});
	}

	private void initView() {
		mChildView = (ViewGroup) mInflater.inflate(R.layout.mediaplayer_controller_tips_layout, mParentView);
		mChildView.setVisibility(View.GONE);
		initBufferLayout();

		initSeekLayout();
	}

	private void initSeekLayout() {
		mSeekView = (ViewGroup) mChildView.findViewById(R.id.forward_backward_layout);
		mSeekView.setVisibility(View.GONE);

		mSeekFlag = (ImageView) mChildView.findViewById(R.id.forward_backward_img);
		mSeekTv = (TextView) mChildView.findViewById(R.id.forward_backward_txt);
	}

	private void initBufferLayout() {
		mBufferView = (ViewGroup) mChildView.findViewById(R.id.buffering_indicator);
		mBufferView.setVisibility(View.VISIBLE);
		mBufferMsg = (TextView) mBufferView.findViewById(R.id.buffering_text);
	}

	private static String generateTime(long position) {
		int totalSeconds = (int) (position / 1000);
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60);
		return String.format(Locale.US, "%02d:%02d", minutes, seconds);
	}
}

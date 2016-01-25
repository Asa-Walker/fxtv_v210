package com.fxtv.threebears.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fxtv.threebears.R;

public class MyDialog extends Dialog {

	private String mTitle, mContent;
	private TextView mTitleView, mContentView;
	private Button mConfirmBtn, mCancelBtn;
	private OnClickListener mConfirmListener, mCancelListener;
	private View mView;
	private EditText mInput;
	private boolean mFlag = false;

	public MyDialog(Context context, String title, String content, OnClickListener confirmListener,
			OnClickListener cancelListener, boolean... hasEditText) {
		super(context, R.style.my_dialog);
		this.mTitle = title;
		this.mContent = content;
		this.mConfirmListener = confirmListener;
		this.mCancelListener = cancelListener;
		if (hasEditText != null && hasEditText.length > 0) {
			mFlag = true;
		}
		setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_dialog);
		setCanceledOnTouchOutside(false);
		init();
	}

	private void init() {
		mTitleView = (TextView) findViewById(R.id.title);
		mContentView = (TextView) findViewById(R.id.content);
		mConfirmBtn = (Button) findViewById(R.id.confirm);
		mCancelBtn = (Button) findViewById(R.id.cancel);
		mView = findViewById(R.id.my_view);

		if (!TextUtils.isEmpty(mTitle)) {
			mTitleView.setVisibility(View.VISIBLE);
			mTitleView.setText(mTitle);
		} else {
			mTitleView.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(mContent)) {
			mContentView.setVisibility(View.VISIBLE);
			mContentView.setText(mContent);
		} else {
			mContentView.setVisibility(View.GONE);
		}

		if (mFlag) {
			findViewById(R.id.input_layout).setVisibility(View.VISIBLE);
			mInput = (EditText) findViewById(R.id.input);
			if (mTitle.equals("请输入QQ")) {
				mInput.setInputType(InputType.TYPE_CLASS_NUMBER);
				//findViewById(R.id.input_value).setVisibility(View.VISIBLE);
			}
		}

		if (mConfirmListener != null) {
			mConfirmBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mFlag) {
						mConfirmListener.onClick(MyDialog.this, v, mInput.getText().toString());
					} else {
						mConfirmListener.onClick(MyDialog.this, v, "");
					}
				}
			});
		} else {
			mView.setVisibility(View.GONE);
		}

		if (mCancelListener != null) {
			mCancelBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mFlag) {
						mCancelListener.onClick(MyDialog.this, v, mInput.getText().toString());
					} else {
						mCancelListener.onClick(MyDialog.this, v, "");
					}
				}
			});
		} else {
			mConfirmBtn.setBackgroundResource(R.drawable.selector_bt_white_left_right_radius);
			mView.setVisibility(View.GONE);
			mCancelBtn.setVisibility(View.GONE);
		}
	}

	public interface OnClickListener {
		public void onClick(Dialog dialog, View view, String value);

	}

	/**
	 * 获得editext的内容
	 * 
	 * @return
	 */
	public String getInputValue() {
		if (mInput == null) {
			mInput = (EditText) findViewById(R.id.input);
		}
		return mInput.getText().toString();
	}
}

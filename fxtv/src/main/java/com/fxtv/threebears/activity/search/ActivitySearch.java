package com.fxtv.threebears.activity.search;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.threebears.R;
import com.fxtv.threebears.fragment.module.search.FragmentSearchHotWord;
import com.fxtv.threebears.fragment.module.search.FragmentSearchResult;

public class ActivitySearch extends FragmentActivity {
	private EditText mEditText;
	private TextView mSearchText;
	private ImageView mCancelImageView;
	FragmentManager mFragmentManager;
	private FragmentSearchHotWord mFragmentSearchHotWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initView();
		initActionbar();
		initFragment();
		initListener();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			FragmentSearchResult.fragmentSearchResultinstance = null;
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initFragment() {
		mFragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		mFragmentSearchHotWord = new FragmentSearchHotWord();
		transaction.replace(R.id.search_fragment, mFragmentSearchHotWord);
		transaction.commit();

	}

	private void initListener() {
		mSearchText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mEditText.getText().toString().trim().equals("")) {
					FrameworkUtils.showToast(ActivitySearch.this,
							getString(R.string.notice_search_context_can_not_be_empty));
					return;
				}

				FragmentTransaction transaction = mFragmentManager.beginTransaction();
				if (FragmentSearchResult.fragmentSearchResultinstance == null) {
					FragmentSearchResult fragmentsearch = new FragmentSearchResult();
					Bundle mBundle = new Bundle();
					mBundle.putString("key", mEditText.getText().toString());
					fragmentsearch.setArguments(mBundle);
					transaction.replace(R.id.search_fragment, fragmentsearch).commit();
				} else {
					transaction.hide(mFragmentSearchHotWord).commit();
					FragmentSearchResult.fragmentSearchResultinstance.initData(mEditText.getText()
							.toString());
				}

			}
		});
		mCancelImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mEditText.setText("");
			}
		});
		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!s.toString().equals("")) {
					mCancelImageView.setVisibility(View.VISIBLE);
				} else {
					mCancelImageView.setVisibility(View.GONE);
				}
			}
		});
	}

	private void initActionbar() {
		ImageView back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentSearchResult.fragmentSearchResultinstance = null;
				finish();
			}
		});
	}

	private void initView() {
		mEditText = (EditText) findViewById(R.id.edit_text);
		mSearchText = (TextView) findViewById(R.id.search);
		mCancelImageView = (ImageView) findViewById(R.id.cancel_text_imageview);

	}
}

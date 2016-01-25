package com.fxtv.threebears.activity.user.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

/**
 * 感兴趣的游戏
 * 
 * @author 薛建浩
 * 
 */
public class ActivityIntrestingGame extends BaseActivity {
	private EditText mIntrestingGames;
	private TextView mTextLimit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion);
		initView();
		mIntrestingGames.setText("" + getStringExtra("intro"));
		mIntrestingGames.setSelection(mIntrestingGames.getText().length());
	}

	private void initView() {
		initActionBar();
		findViewById(R.id.activity_suggestion_value).setVisibility(View.GONE);
		findViewById(R.id.activity_suggetion_connact).setVisibility(View.GONE);
		findViewById(R.id.submit_suggetion).setVisibility(View.GONE);
		mTextLimit = (TextView) findViewById(R.id.activity_suggestion_length_limit);
		mIntrestingGames = (EditText) findViewById(R.id.activity_suggestion_actv_content);
		mIntrestingGames.setHint("请输入感兴趣的游戏，以逗号隔开:");
		mIntrestingGames.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		mIntrestingGames.setSingleLine(false);
		mIntrestingGames.setHorizontallyScrolling(false);
		mIntrestingGames.setFilters(new InputFilter[] { new InputFilter.LengthFilter(200) });
		mIntrestingGames.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if ((start + count) > 0) {
					if (mTextLimit.getVisibility() == View.GONE) {
						mTextLimit.setVisibility(View.VISIBLE);
					}
					mTextLimit.setText((start + count) + "/200");
				} else {
					mTextLimit.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		title.setText("感兴趣的游戏");
		ImageView btnBack = (ImageView) findViewById(R.id.ab_left_img);
		btnBack.setImageResource(R.drawable.icon_arrow_left1);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		TextView submit = (TextView) findViewById(R.id.ab_right_tv);
		submit.setVisibility(View.VISIBLE);
		submit.setText("完成");
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mIntrestingGames.getText().toString().trim().equals("")) {
					submitUserInfo(mIntrestingGames.getText().toString());
				}
			}
		});
	}

	private void submitUserInfo(final String userInfo) {
		JsonObject params = new JsonObject();
		// params.addProperty("user_id",
		// getSystem(SystemUser.class).mUser.user_id);
		params.addProperty("type", "5");
		params.addProperty("value", userInfo);
		Utils.showProgressDialog(this);
		/*String url = processUrl("User", "setUserInfo", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url callBack);*/
		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_setUserInfo, params), "modifyUserInfo", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				showToast(resp.msg);
				Intent intent = new Intent();
				intent.putExtra("userInfo", userInfo);
				setResult(1990, intent);
				finish();
			}

			@Override
			public void onFailure(Response resp) {
				showToast(resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

	}
}

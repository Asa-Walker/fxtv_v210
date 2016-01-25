package com.fxtv.threebears.activity.other;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.MyGridView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.Game;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.MyList;
import com.google.gson.JsonObject;

/**
 * 新手引导第一页
 * 
 * @author Android2
 * 
 */
public class ActivityGuideStepFirst extends BaseActivity {
	private MyGridView mGridView;
	private MyAdapter mAdapter;
	private Button mNextStep;
	private int mCount = 0;
	private View first_guider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide_first);
		initView();
		getData();
		// firstCheck();
		// checkButton();
		if(getSystem(SystemUser.class).mUser!=null && !TextUtils.isEmpty(getSystem(SystemUser.class).mUser.reward_tips)){
			first_guider.setVisibility(View.VISIBLE);
			getSystem(SystemCommon.class).displayDefaultImage(ActivityGuideStepFirst.this,((ImageView)findViewById(R.id.guide_mine)),getSystem(SystemUser.class).mUser.reward_tips);
		}

	}

	private void firstCheck(MyList<Game> data) {
		if(data==null) return;
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).status.equals("1")) {
				data.get(i).game_is_selected = R.drawable.icon_choose;
				mCount++;
			}
		}
		checkButton();
	}

	private void getData() {
		JsonObject params = new JsonObject();
//		params.addProperty("user_id", getSystem(SystemUser.class).mUser.user_id);
		Utils.showProgressDialog(this);

		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_guideGameList, params), "guideFirstStep", false, false, new RequestCallBack<MyList<Game>>() {

			@Override
			public void onSuccess(MyList<Game> data, Response resp) {
				firstCheck(data);
				if (mAdapter == null) {
					mAdapter = new MyAdapter();
				}
				mAdapter.setListData(data);


					}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActivityGuideStepFirst.this,
						resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

	}

	private void initView() {
		mNextStep = (Button) findViewById(R.id.activity_guide_first_next_step);
		findViewById(R.id.activity_guide_first_skip).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// FrameworkUtils.skipActivity(ActivityGuideStepFirst.this,
						// ActivityPersonal.class);
						ActivityGuideStepFirst.this.finish();
					}
				});
		findViewById(R.id.activity_guide_first_img).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// FrameworkUtils.skipActivity(ActivityGuideStepFirst.this,
						// ActivityPersonal.class);
						ActivityGuideStepFirst.this.finish();
					}
				});

		mNextStep.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityGuideStepFirst.this,
						ActivityGuideStepSecond.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("list", (MyList<Game>) mAdapter.getListData());
				intent.putExtras(bundle);
				startActivityForResult(intent, 99);
			}
		});
		first_guider=findViewById(R.id.first_guide);
		initGridView();
		first_guider.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				first_guider.setVisibility(View.GONE);
			}
		});
	}

	private void initGridView() {
		mGridView = (MyGridView) findViewById(R.id.activity_guide_first_gridview);
		mAdapter = new MyAdapter();
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Game game=mAdapter.getItem(position);
				if (game.game_is_selected == R.drawable.icon_choose) {
					game.game_is_selected = R.color.touming;
					game.status = "0";
					mCount--;
				} else {
					game.game_is_selected = R.drawable.icon_choose;
					game.status = "1";
					mCount++;
				}
				checkButton();
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	protected void checkButton() {
		if (mCount <= 0) {
			mNextStep.setClickable(false);
			mNextStep.setBackgroundResource(R.color.color_line);
		} else {
			mNextStep.setClickable(true);
			mNextStep.setBackgroundResource(R.color.color_orange);
		}
	}

	class MyAdapter extends BaseListGridAdapter<Game> {


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				convertView = View.inflate(ActivityGuideStepFirst.this,R.layout.item_fragment_anchor_space_message_gridview,null);
				holder = new Holder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_fragment_anchor_space_message_image);
				holder.anchorName = (TextView) convertView
						.findViewById(R.id.item_fragment_anchor_space_message_anchor);
				holder.isSelected = (ImageView) convertView
						.findViewById(R.id.is_selected);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			Game game=getItem(position);
			getSystem(SystemCommon.class).displayDefaultImage(ActivityGuideStepFirst.this,holder.image,game.image);
			holder.anchorName.setText(game.title);
			holder.isSelected
					.setBackgroundResource(game.game_is_selected);
			return convertView;
		}

		class Holder {
			ImageView image;
			ImageView isSelected;
			TextView anchorName;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("aaaa", "resultCode="+resultCode);
		switch (resultCode) {
		case 20:
			finish();
			break;
		default:
			break;
		}
	}
}

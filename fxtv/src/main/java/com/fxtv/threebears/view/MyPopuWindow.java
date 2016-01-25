package com.fxtv.threebears.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.fragment.FragmentTabMain;
import com.fxtv.threebears.model.AnimModel;
import com.fxtv.threebears.model.Game;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class MyPopuWindow extends PopupWindow implements Animator.AnimatorListener{
	private Context mContext;
	public ViewGroup mLayout;
	private GridView mGridView;
	private List<Game> mGameMenus;
	private List<Game> mTempGameMenus= new ArrayList<Game>();
	private LayoutInflater mInflater;
	private MyAdapter mAdapter;
	private Resources mResources;
	private int mCheckedNum;
	private FragmentTabMain mParent;

	public MyPopuWindow(Context context, FragmentTabMain parent,List<Game> mGameMenus) {
		super(context);
		this.mContext = context;
		this.mParent = parent;
		setGameMenus(mGameMenus);
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResources = context.getResources();
		initPopupWindow();
	}

	public void setGameMenus(List<Game> mGameMenus) {
		this.mGameMenus = mGameMenus;
		copyList();
	}

	@Override
	public void showAsDropDown(View anchor) {
		this.showAsDropDown(anchor, 0, 0);
	}
	View layout_title;
	ObjectAnimator animator;
	@Override
	public void showAsDropDown(final View anchor, int xoff, int yoff) {
		super.showAsDropDown(anchor, xoff, yoff);
		copyList();
		mAdapter.notifyDataSetChanged();

		//Logger.d("MyPopuWindow", "height=" + layout_title.getHeight() + " "+layout_title.getLayoutParams().height+"  " + mGridView.getHeight() + " " + mGridView.getLayoutParams().height);
		layout_title.setVisibility(View.VISIBLE);
		if(animator==null) {
			animator = ObjectAnimator.ofFloat(layout_title, AnimModel.TransY, -layout_title.getLayoutParams().height, 0).setDuration(AnimModel.Duration_300);
			animator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					mGridView.setVisibility(View.VISIBLE);
					ObjectAnimator animator1=ObjectAnimator.ofFloat(mGridView, AnimModel.TransY, -mGridView.getHeight(), 0).setDuration(300);
					animator1.setInterpolator(new DecelerateInterpolator(1));
					animator1.setStartDelay(200);
					animator1.start();
				}
			});
		}
		animator.start();
	}

	private void copyList() {
		mCheckedNum = 0;
		if (mTempGameMenus == null || mTempGameMenus.size() == 0) {
			for (Game game : this.mGameMenus) {
				if (game.status.equals("1")) {
					mCheckedNum++;
				}
				Game temp = new Game();
				temp.id = game.id;
				temp.title = game.title;
				temp.image = game.image;
				temp.status = game.status;
				mTempGameMenus.add(temp);
			}
		} else {
			for (int i = 0; i < this.mGameMenus.size(); i++) {
				if (this.mGameMenus.get(i).status.equals("1")) {
					mCheckedNum++;
				}
				mTempGameMenus.get(i).status = this.mGameMenus.get(i).status;
			}
		}
	}

	private void initPopupWindow() {
		mLayout = (ViewGroup) mInflater.inflate(R.layout.pop_layout, null);
		layout_title=mLayout.findViewById(R.id.layout_title);
		setContentView(mLayout);
		setBackgroundDrawable(mContext.getResources().getDrawable(R.color.color_transparency));
		//setWidth(FrameworkUtils.getScreenWidth(mContext));
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setOutsideTouchable(true);
		mLayout.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (isEdit())
					submitGameMenus();
				else {
					dismiss();
				}
			}
		});
		initGridView();
	}

	private boolean isEdit() {
		for (int i = 0; i < mGameMenus.size(); i++) {
			if (!mGameMenus.get(i).status.equals(mTempGameMenus.get(i).status)) {
				return true;
			}
		}
		return false;
	}

	private void initGridView() {
		mGridView = (GridView) mLayout.findViewById(R.id.pop_gridview);
		mAdapter = new MyAdapter(mTempGameMenus);
		mGridView.setAdapter(mAdapter);
	}



	class MyAdapter extends BaseListGridAdapter<Game> {

		int width;
		public MyAdapter(List<Game> listData) {
			super(listData);
			width=(FrameworkUtils.getScreenWidth(mContext)-FrameworkUtils.dip2px(mContext,2))/3;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.pop_gridview_item, parent,false);
				holder = new ViewHolder();
				holder.bgLayout = (LinearLayout) convertView.findViewById(R.id.bg_lay);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.name = (TextView) convertView.findViewById(R.id.game_name);
				holder.img_flag = (ImageView) convertView.findViewById(R.id.img_flag);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			convertView.getLayoutParams().height=width;

			final Game item = mTempGameMenus.get(position);
			holder.name.setText(item.title);

			if (item.status.equals("1")) {
				holder.bgLayout.setBackgroundResource(R.drawable.bg_frist_page_game_menu);
				holder.img_flag.setVisibility(View.VISIBLE);
			} else {
				holder.bgLayout.setBackgroundResource(R.color.color_transparency);
				holder.img_flag.setVisibility(View.INVISIBLE);
			}

			final LinearLayout mRelativeLayout = holder.bgLayout;
			//SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageSquare(item.image, holder.img);
			SystemManager.getInstance().getSystem(SystemCommon.class).displayDefaultImage(mContext, holder.img, item.image, SystemCommon.SQUARE);
			holder.bgLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (item.status.equals("0")) {
						mCheckedNum++;
						item.status = "1";
						holder.img_flag.setVisibility(View.VISIBLE);
						mRelativeLayout.setBackgroundResource(R.drawable.bg_frist_page_game_menu);
					} else {
						if (mCheckedNum <= 3) {
							FrameworkUtils.showToast(mContext, mResources.getString(R.string.notice_at_least_three));
							return;
						}
						item.status = "0";
						mCheckedNum--;
						mRelativeLayout.setBackgroundResource(0);
						holder.img_flag.setVisibility(View.INVISIBLE);
					}
				}
			});
			return convertView;
		}
		class ViewHolder {
			LinearLayout bgLayout;
			ImageView img;
			TextView name;
			ImageView img_flag;
		}
	}

	private void submitGameMenus() {
		JsonObject params = new JsonObject();
		JsonArray array = new JsonArray();
		for (Game game : mTempGameMenus) {
			JsonObject obj = new JsonObject();
			obj.addProperty("id", game.id);
			obj.addProperty("status", game.status);
			array.add(obj);
		}
		params.add("menu", array);
		Utils.showProgressDialog((Activity) mContext);

		String url = Utils.processUrl(ModuleType.INDEX, ApiType.INDEX_setMenu, params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get(mContext, url, "mainSetGameTabsApi", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				FrameworkUtils.showToast(mContext, mResources.getString(R.string.notice_change_success));
						/*for (int i = 0; i < mTempGameMenus.size(); i++) {
							mGameMenus.get(i).status = mTempGameMenus.get(i).status;
						}*/
				mParent.callUpdate(mTempGameMenus);
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(mContext, resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
				dismiss();
			}
		});


	}

	@Override
	public void dismiss() {
		ObjectAnimator animator=ObjectAnimator.ofFloat(mGridView, AnimModel.TransY, 0, -mGridView.getHeight()).setDuration(AnimModel.Duration_300);
		animator.setInterpolator(new DecelerateInterpolator(1));
		animator.addListener(this);
		animator.start();
	}

	@Override
	public void onAnimationStart(Animator animation) {

	}

	@Override
	public void onAnimationEnd(Animator animation) {
		if (mGridView.getVisibility() == View.VISIBLE) {
			mGridView.setVisibility(View.GONE);
			ObjectAnimator animator1 = ObjectAnimator.ofFloat(layout_title, AnimModel.TransY, 0, -layout_title.getHeight()).setDuration(AnimModel.Duration_300);
			animator1.addListener(this);
			animator1.start();
		} else if (layout_title.getVisibility() == View.VISIBLE) {
			layout_title.setVisibility(View.GONE);
			super.dismiss();
		}
	}

	@Override
	public void onAnimationCancel(Animator animation) {

	}

	@Override
	public void onAnimationRepeat(Animator animation) {

	}
}

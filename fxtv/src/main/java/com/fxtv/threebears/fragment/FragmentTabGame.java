package com.fxtv.threebears.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.game.ActivityGame;
import com.fxtv.threebears.model.Game;
import com.fxtv.threebears.system.SystemAnalyze;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * @author FXTV-Android Tab--游戏 列表
 */
public class FragmentTabGame extends BaseFragment {
	private GridView mGridView;
	//private List<Game> mData;
	private MyAdapter mMyAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_tab_game, container, false);
		initView();
		getData();

		Logger.d("TAG", "FragmentTabGame onCreateView ==");
		return mRoot;
	}

	private void getData() {
		JsonObject params = new JsonObject();
		params.addProperty("page", "1");
		params.addProperty("pagesize", 99 + "");

		Utils.showProgressDialog(getActivity());
		getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.GAME, ApiType.GAME_gameList, params), "gameGamesOfAll", true, true, new RequestCallBack<List<Game>>() {

			@Override
			public void onSuccess(List<Game> data, Response resp) {
				if (data != null && data.size() != 0) {
					mMyAdapter.addData(data);
				}
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(getActivity(), resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});
	}

	private void initView() {
		initGridView();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		Logger.d("TAG", "game onHiddenChanged = " + hidden);
		if (!hidden) {
			// 数据统计
			getSystem(SystemAnalyze.class).analyzeUserAction("main_menu", "2", null);
		}
	}
	private void initGridView() {
		mGridView = (GridView) mRoot.findViewById(R.id.fragment_tab_game_gv);
		mMyAdapter = new MyAdapter(null);
		mGridView.setAdapter(mMyAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
				//bundle.putSerializable("game", mData.get(position));
				bundle.putSerializable("game", mMyAdapter.getItem(position));
				FrameworkUtils.skipActivity(getActivity(), ActivityGame.class, bundle);
				
			}
		});
	}

	class MyAdapter extends BaseListGridAdapter<Game> {
		private int width;
		public MyAdapter(List<Game> listData) {
			super(listData);
			width=(FrameworkUtils.getScreenWidth(getActivity())-FrameworkUtils.dip2px(getActivity(),2))/3;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.item_new_gv_game, parent,false);
				viewHolder = new ViewHolder();
				viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
				viewHolder.title = (TextView) convertView.findViewById(R.id.game_name);
				viewHolder.source = (TextView) convertView.findViewById(R.id.video_num);
				viewHolder.num = (TextView) convertView.findViewById(R.id.update_latest_num);
				convertView.setTag(viewHolder);

				if(convertView.getLayoutParams()!=null){
					ViewGroup.LayoutParams params=convertView.getLayoutParams();
					params.height=width;
					convertView.requestLayout();
				}

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Game itemData=getItem(position);

			getSystem(SystemCommon.class).displayDefaultImage(FragmentTabGame.this, viewHolder.img,itemData.image);
			viewHolder.title.setText(itemData.title);
			viewHolder.source.setText("视频数:" + itemData.video_num);
			if ("0".equals(itemData.daily_video_num)) {
				viewHolder.num.setVisibility(View.INVISIBLE);
			} else {
				viewHolder.num.setVisibility(View.VISIBLE);
			}
			viewHolder.num.setText("+" + itemData.daily_video_num);
			return convertView;
		}

		class ViewHolder {
			ImageView img;
			TextView title;
			TextView source;
			TextView num;
		}
	}

	@Override
	public void onDestroy() {
		//FrameworkUtils.setEmptyList(mData);
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		mGridView = null;
		mMyAdapter = null;
		super.onDestroyView();
	}
}

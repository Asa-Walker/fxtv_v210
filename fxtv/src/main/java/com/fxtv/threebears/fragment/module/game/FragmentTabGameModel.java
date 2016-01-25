package com.fxtv.threebears.fragment.module.game;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.fxtv.framework.widget.circular.CircularImage;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.framework.widget.xlistview.XListView.IXListViewListener;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Game;
import com.fxtv.threebears.model.GameOrderMode;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;



/**
 * @author FXTV-Android 游戏--精选、主播、专辑、and so on...
 */
public class FragmentTabGameModel extends BaseFragment {
	private XListView orderListView;
	private Orderdapter mOrderAdapter;
	private XListView mListView;
	private GameOrderMode lastGameOrder;
	private MyAdapter mMyAdapter;
	private Game mGame;
	private String mMenuId;
	private String mMenuType;
	private int mCurrentPos=0;
	private int mPageNum = 0;
	private int mPageSize = 20;
	private boolean mIsVisibleToUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mGame = (Game) getArguments().getSerializable("game");
		if (mGame!=null && !TextUtils.isEmpty(mGame.game_type) && mGame.game_type.equals("1")) {
			mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_tab_game_model1, container, false);
		} else {
			mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_tab_game_model, container, false);
		}
		mMenuId = getArguments().getString("menu_id");
		mMenuType = getArguments().getString("menu_type");
		Logger.d("","mMenuId="+mMenuId+" mMenuType="+mMenuType+" game="+mGame);
		initView();
		getData();
		return mRoot;
	}

	private void initView() {
		initWheelView();
		initListView();
	}

	public boolean isOredered(GameOrderMode mode) {
		return mode!=null && "1".equals(mode.status);
	}
	private void initWheelView() {
		orderListView = (XListView) mRoot.findViewById(R.id.fragment_tab_order_model);
		orderListView.setPullRefreshEnable(false);
		orderListView.setPullLoadEnable(false);
		orderListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position--;
				if (mCurrentPos != position) {
					mListView.setPullLoadEnable(true);
					mCurrentPos = position;
					mListView.setSelection(0);
					lastGameOrder = mOrderAdapter.getItem(position);
					mPageNum = 0;
					Utils.showProgressDialog(getActivity());
					getOrderVideosData(mOrderAdapter.getItem(position), true);
					mOrderAdapter.notifyDataSetChanged();
				}
			}

		});
		if (mOrderAdapter == null) {
			mOrderAdapter = new Orderdapter(null);
		}
		orderListView.setAdapter(mOrderAdapter);

	}

	private void initListView() {
		mListView = (XListView) mRoot.findViewById(R.id.fragment_tab_game_model_lv);
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(true);
		mListView.setPageSize(mPageSize);
		if (mMyAdapter == null) {
			mMyAdapter = new MyAdapter(null);
		}
		mListView.setAdapter(mMyAdapter);
		mListView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				mPageNum = 0;
				Utils.showProgressDialog(getActivity());
				getOrderVideosData(lastGameOrder, true);
			}

			@Override
			public void onLoadMore() {
				getOrderVideosData(lastGameOrder, false);
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putString("video_id", mMyAdapter.getItem(position - 1).id);
				bundle.putString("skipType", "21");
				FrameworkUtils.skipActivity(getActivity(), ActivityVideoPlay.class, bundle);
			}
		});
	}
	//private View lastView;
	protected class Orderdapter extends BaseListGridAdapter<GameOrderMode> {

		public Orderdapter(List<GameOrderMode> list) {
			super(list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if(convertView==null){
				holder=new Holder();
				convertView=mLayoutInflater.inflate(R.layout.item_list_game_order,null);
				holder.im_pic= (CircularImage) convertView.findViewById(R.id.im_userpic);
				holder.tv_name= (TextView) convertView.findViewById(R.id.tv_name);
				holder.relative=convertView.findViewById(R.id.relative);
				holder.tv_order= (TextView) convertView.findViewById(R.id.tv_order);
				convertView.setTag(holder);
			}else{
				holder= (Holder) convertView.getTag();
			}

			final GameOrderMode info = getItem(position);
			if(position==mCurrentPos){
				holder.relative.setSelected(true);
				lastGameOrder=info;
				holder.tv_order.setVisibility(View.VISIBLE);

				if (isOredered(info)) {
					holder.tv_order.setText("已订阅");
					holder.tv_order.setBackgroundResource(R.drawable.order_false);
				} else {
					holder.tv_order.setText("订阅");
					holder.tv_order.setBackgroundResource(R.drawable.order_true);
				}
			}else{
				holder.relative.setSelected(false);
				holder.tv_order.setVisibility(View.GONE);
			}
			holder.tv_name.setText(info.name);
			holder.tv_name.setTextColor(info.mColor);

			/*SystemManager.getInstance().getSystem(SystemImageLoader.class)
					.displayImageSquare(info.image, holder.im_pic);*/
			getSystem(SystemCommon.class).displayDefaultImage(FragmentTabGameModel.this,holder.im_pic,info.image);
			holder.tv_order.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!getSystem(SystemUser.class).isLogin()) {
						getSystem(SystemCommon.class).noticeAndLogin(getActivity());
					} else {
						if (!isOredered(info))  {
							orderGame((TextView) v, info);
						}
					}
				}
			});
			return convertView;
		}
		class Holder{
			CircularImage im_pic;
			TextView tv_name;
			TextView tv_order;
			View relative;
		}
	}

	private void getData() {
		if (mIsVisibleToUser) {
			Utils.showProgressDialog(getActivity());
		}
		getOrderData();

	}
	private int listWidth=0;
	private void getOrderData() {
		JsonObject params = new JsonObject();
		params.addProperty("id", mMenuId);
		params.addProperty("type", mMenuType);

		getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.GAME, ApiType.GAME_orderList,params), "getOrderList", false, false, new RequestCallBack<List<GameOrderMode>>() {
			@Override
			public void onSuccess(List<GameOrderMode> data, Response resp) {
				listWidth=mListView.getWidth();
				if (data != null && data.size() != 0) {
					if(mOrderAdapter==null){
						mOrderAdapter=new Orderdapter(data);
					}else{
						mOrderAdapter.setListData(data);
					}
					getOrderVideosData(data.get(0),true);
				}
			}

			@Override
			public void onFailure(Response resp) {
				Utils.dismissProgressDialog();
			}

			@Override
			public void onComplete() {

			}
		});
	}

	private void getOrderVideosData(GameOrderMode mode,final boolean isRefresh) {

		mPageNum++;
		JsonObject params = new JsonObject();
		params.addProperty("id", mode == null ? "" : mode.id);
		params.addProperty("type", mode == null ? "" : mode.type);
		params.addProperty("page", mPageNum + "");
		params.addProperty("pagesize", mPageSize + "");
		params.addProperty("game_id", mode == null ? "" : mode.game_id + "");

		getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.GAME, ApiType.GAME_orderVideo, params), "gameVideosOfOrder", !isRefresh, true, new RequestCallBack<List<Video>>() {
			@Override
			public void onSuccess(List<Video> data, Response resp) {
				if(listWidth<=0)
					listWidth=mListView.getWidth();
				if (mMyAdapter == null) {
					mMyAdapter = new MyAdapter(null);
				}
				if (data != null && data.size() != 0) {
					if (isRefresh) {
						mMyAdapter.setListData(data);
					} else {
						mMyAdapter.addData(data);
					}

				} else {
					FrameworkUtils.showToast(getActivity(), "没有更多数据");
				}
			}

			@Override
			public void onFailure(Response resp) {
				//showToast(resp.msg);
				if (isRefresh) {
					mMyAdapter.setListData(null);//清空
				}
				if (mListView != null) {
					mListView.noMoreData();
				}
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
				if (mListView != null) {
					mListView.stopLoadMore();
					mListView.stopRefresh();
				}
			}
		});

	}

	class MyAdapter extends BaseListGridAdapter<Video> {


		public MyAdapter(List<Video> listData) {
			super(listData);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.item_fragmen_tab_game_mode_lv, parent,false);
				holder = new ViewHolder();
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
				holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
				holder.prize = (ImageView) convertView.findViewById(R.id.prize);
				holder.logo = (ImageView) convertView.findViewById(R.id.logo);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ViewGroup.LayoutParams params=convertView.getLayoutParams();
			int width=listWidth;
			if(width<=0 && params!=null){
				width=Utils.getViewWidthHeight(holder.img)[0];
			}
			if(params!=null)
				params.height=(int)(((double)width)*9/16);
			convertView.setLayoutParams(params);

			Video item = getItem(position);
			getSystem(SystemCommon.class).displayDefaultImage(FragmentTabGameModel.this, holder.img, item.image);
			holder.tv_time.setText(item.duration);
			holder.tv_title.setText(item.title);

			Utils.setVideoLogo(holder.prize, holder.logo, item.lottery_status);
			return convertView;
		}
		class ViewHolder {
			TextView tv_time;
			TextView tv_title;
			ImageView img,prize,logo;
		}
	}

	private void orderGame(final TextView tv_order,final GameOrderMode gameOrderMode) {
		JsonObject params = new JsonObject();
		JsonArray array = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("id", gameOrderMode==null?"":gameOrderMode.id);
		obj.addProperty("type", gameOrderMode==null?"":gameOrderMode.type);
		obj.addProperty("status", "1");
		array.add(obj);
		params.add("order", array);

		getSystem(SystemUser.class)
				.orderOrUnOrder(params, new IUserBusynessCallBack() {

					@Override
					public void onResult(boolean result, String arg) {
						showToast(arg);
						if (result) {
							tv_order.setText("已订阅");
							if(gameOrderMode!=null)
								gameOrderMode.status="1";
							tv_order.setBackgroundResource(R.drawable.order_false);
						}
					}
				});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mGame = null;
	}

	@Override
	public void onDestroyView() {
		orderListView = null;
		mOrderAdapter = null;
		mListView = null;
		mMyAdapter = null;
		mPageNum=0;
		super.onDestroyView();
	}
}

package com.fxtv.threebears.activity.self;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.model.Game;
import com.fxtv.threebears.model.GameOrderMode;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.MyDialog;
import com.google.gson.JsonObject;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的订阅 主播、游戏
 * 
 * @author FXTV-Android
 * 
 */
public class ActivitySelfMyOrder extends BaseActivity {
	private ExpandableListView mExpandableListView;
	private ExpandableAdapter mEListAdapter;
	private ViewGroup mAnchorLayout;
	private List<Anchor> mListData = new ArrayList<Anchor>();
	private ViewGroup mHeader;
	private TextView deleteAllGamesText, deleteAllAnchorsText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_self_my_order);
		initView();
		getAnchorListData();

	}
	private void getAnchorListData() {
		JsonObject params = new JsonObject();
		Utils.showProgressDialog(this);
		/*String url = processUrl("Mine", "anchorList", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url, "selfAnchorsOfBook", false, false, callBack);*/
		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.MINE, ApiType.MINE_anchorList, params), "selfAnchorsOfBook", false, false, new RequestCallBack<List<Anchor>>() {
			@Override
			public void onSuccess(List<Anchor> data, Response resp) {
				mListData = data;
			}

			@Override
			public void onFailure(Response resp) {
				showToast(resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
				initHeader();
				getGameListData();
			}
		});

	}

	private void initView() {
		initActionBar();
		 initExpandableListView();
	}


	private void initActionBar() {
		TextView title = (TextView) findViewById(R.id.ab_title);
		title.setText(getString(R.string.self_page_mybook));
		TextView mTextView = (TextView) findViewById(R.id.ab_left_tv);
		mTextView.setVisibility(View.GONE);
		mTextView.setText(getString(R.string.self_page_myvideo));
		mTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivitySelfMyOrder.this.finish();
			}
		});
		TextView rightTv = (TextView) findViewById(R.id.ab_right_tv);
		rightTv.setVisibility(View.GONE);
		ImageView leftImg = (ImageView) findViewById(R.id.ab_left_img);
		leftImg.setVisibility(View.VISIBLE);
		leftImg.setImageResource(R.drawable.icon_arrow_left1);
		leftImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivitySelfMyOrder.this.finish();
			}
		});
		ImageView rightImg1 = (ImageView) findViewById(R.id.ab_right_img1);
		rightImg1.setVisibility(View.GONE);
		ImageView rightImg2 = (ImageView) findViewById(R.id.ab_right_img2);
		rightImg2.setVisibility(View.GONE);
	}
	private void getGameListData() {
		JsonObject params = new JsonObject();
		Utils.showProgressDialog(this);
		/*String url = processUrl("Mine", "gameList", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url, "selfGamesOfBook", false, false, callBack);*/

		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.MINE, ApiType.MINE_gameList, params), "selfGamesOfBook", false, false, new RequestCallBack<List<Game>>() {
			@Override
			public void onSuccess(List<Game> data, Response resp) {
				if (mEListAdapter == null) {
					mEListAdapter = new ExpandableAdapter();
				}
				mEListAdapter.setEListData(data);
				if (data != null && data.size() != 0) {
					deleteAllGamesText.setVisibility(View.VISIBLE);
					deleteAllGamesText.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (mEListAdapter != null && mEListAdapter.getEListData() != null && mEListAdapter.getEListData().size() != 0) {
								initDialog("2", "game", "是否退订全部的订阅内容");
							}
						}
					});
				}
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
	private void initHeader() {
		if(mHeader==null){
			mHeader = (ViewGroup) mLayoutInflater.inflate(R.layout.fragment_tab_self_mybook_header, null);
		}
		mAnchorLayout = (ViewGroup) mHeader.findViewById(R.id.fragment_tab_self_mybook_header_hs);
		deleteAllAnchorsText = (TextView) mHeader.findViewById(R.id.fragment_tab_self_mybook_header_del);
		if (mListData == null || mListData.size() == 0) {
			deleteAllAnchorsText.setVisibility(View.GONE);
		} else {
			deleteAllAnchorsText.setVisibility(View.VISIBLE);
			deleteAllAnchorsText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mListData != null && mListData.size() != 0) {
						initDialog("1", "anchor", "是否退订全部的主播?");
					}
				}
			});
		}
		deleteAllGamesText = (TextView) mHeader.findViewById(R.id.deleteallgames);
		updateOrderAnchors();
	}

	/**
	 * 弹出对话框
	 * 
	 * @param type
	 * @param listType
	 */
	private void initDialog(final String type, final String listType, String title) {
		getSystem(SystemCommon.class)
				.showDialog(ActivitySelfMyOrder.this, title, new MyDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog, View view, String value) {
						allUnsubscribe(type, listType);
						dialog.dismiss();
					}
				}, new MyDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog, View view, String value) {
						dialog.dismiss();
					}
				});
	}

	private void initExpandableListView() {
		mExpandableListView = (ExpandableListView) findViewById(R.id.fragment_tab_self_mybook_elv);
		mExpandableListView.setGroupIndicator(null);
		if(mHeader==null){
			mHeader = (ViewGroup) mLayoutInflater.inflate(R.layout.fragment_tab_self_mybook_header, null);
		}
		mExpandableListView.addHeaderView(mHeader);
		if(mEListAdapter==null){
			mEListAdapter = new ExpandableAdapter();
		}

		View emptyLayout = LayoutInflater.from(this).inflate(com.fxtv.threebears.R.layout.xlistview_empty, null);
		emptyLayout.setVisibility(View.GONE);
		((TextView) emptyLayout.findViewById(R.id.tv_empty)).setText("无订阅内容");
		addContentView(emptyLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		//((ViewGroup)mExpandableListView.getParent()).addView(emptyLayout);

	//	mExpandableListView.setEmptyView(emptyLayout);
		mExpandableListView.setAdapter(mEListAdapter);
		mExpandableListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				Bundle bundle = new Bundle();
				GameOrderMode gameOrder = mEListAdapter.getChild(groupPosition, childPosition);
				if (gameOrder != null) {
					bundle.putString("oid", gameOrder.id);
					bundle.putString("type", gameOrder.type);
					bundle.putString("name", gameOrder.title);
					FrameworkUtils.skipActivity(ActivitySelfMyOrder.this, ActivityVideoList.class, bundle);
				}
				return false;
			}
		});
	}

	class ExpandableAdapter extends BaseExpandableListAdapter {
		private List<Game> mEListData;

		public List<Game> getEListData() {
			return mEListData;
		}

		public void setEListData(List<Game> mEListData) {
			this.mEListData = mEListData;
		}

		@Override
		public int getGroupCount() {
			return mEListData==null?0:mEListData.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mEListData==null?0:mEListData.get(groupPosition).order_list.size();
		}

		@Override
		public Game getGroup(int groupPosition) {
			return mEListData==null?null:mEListData.get(groupPosition);
		}

		@Override
		public GameOrderMode getChild(int groupPosition, int childPosition) {
			return mEListData==null?null:mEListData.get(groupPosition).order_list.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean isEmpty() {
			return getGroupCount()==0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			ViewHolderGroup viewHolder = null;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.item_expandablelistview_group, null);
				viewHolder = new ViewHolderGroup();
				viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
				viewHolder.flag = (ImageView) convertView.findViewById(R.id.flag);
				viewHolder.name = (TextView) convertView.findViewById(R.id.name);
				viewHolder.num = (TextView) convertView.findViewById(R.id.num);
				viewHolder.tag = (TextView) convertView.findViewById(R.id.tag);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolderGroup) convertView.getTag();
			}
			Game game=getGroup(groupPosition);
			/*SystemManager.getInstance().getSystem(SystemImageLoader.class)
					.displayImageDefault(game.image, viewHolder.img);*/
			getSystem(SystemCommon.class).displayDefaultImage(ActivitySelfMyOrder.this,  viewHolder.img, game.image);
			viewHolder.name.setText(game.title);
			// viewHolder.tag.setText(mEListData.get(groupPosition).game_description);
			viewHolder.num.setText("已订阅" + game.order_list.size() + "个内容");
			if (isExpanded) {
				viewHolder.flag.setImageResource(R.drawable.self_expand);
			} else {
				viewHolder.flag.setImageResource(R.drawable.self_contract);
			}
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
				View convertView, ViewGroup parent) {
			ViewHolder2 viewHolder = null;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.item_elv, null);
				viewHolder = new ViewHolder2();
				viewHolder.name = (TextView) convertView.findViewById(R.id.name);
				viewHolder.book = (Button) convertView.findViewById(R.id.book);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder2) convertView.getTag();
			}
			final GameOrderMode gameOrder = getChild(groupPosition,childPosition);
			viewHolder.name.setText(gameOrder.title);
			viewHolder.book.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getSystem(SystemUser.class)
							.orderOrUnOrderModel(gameOrder.id, "0", gameOrder.type,new IUserBusynessCallBack() {

								@Override
								public void onResult(boolean result, String arg) {
									showToast(arg);
									getGroup(groupPosition).order_list.remove(gameOrder);
									if (getChildrenCount(groupPosition) == 0) {
										mEListData.remove(groupPosition);
									}
									if (getGroupCount() == 0) {
										if (deleteAllGamesText == null) {
											deleteAllGamesText = (TextView) findViewById(R.id.deleteallgames);
										}
										deleteAllGamesText.setVisibility(View.GONE);
									}
									notifyDataSetChanged();
								}
							});
				}
			});
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		class ViewHolder2 {
			TextView name;
			Button book;
		}

		class ViewHolderGroup {
			ImageView img;
			ImageView flag;
			TextView name;
			TextView tag;
			TextView num;
		}
	}



	/**
	 * 退订所有(主播或游戏或收藏) type--接口参数（1--退订全部主播 2--退订全部标签 3--删除全部收藏)
	 * listType--集合的类型(用作判断)
	 */
	protected void allUnsubscribe(String type, final String listType) {
		JsonObject params = new JsonObject();
		params.addProperty("type", type);
		Utils.showProgressDialog(ActivitySelfMyOrder.this);
		/*String url = processUrl("User", "delUserData", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url, "allUnsubscribe", false, false, callBack);*/

		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_delUserData, params), "allUnsubscribe", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				if (listType.equals("anchor")) {
					new Thread() {
						public void run() {
							PushAgent mPushAgent = PushAgent.getInstance(ActivitySelfMyOrder.this);
							try {
								String[] temp = new String[mListData.size()];
								for (int i = 0; i < mListData.size(); i++) {
									temp[i] = mListData.get(i).id;
								}
								if (temp != null && temp.length > 0) {
									mPushAgent.getTagManager().delete(temp);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						};
					}.start();
					// getSystem(SystemUser.class).delAllOrderAnchor();
					mAnchorLayout.removeAllViews();
					deleteAllAnchorsText.setVisibility(View.GONE);
				} else {
					// getSystem(SystemUser.class).delAllOrderGame();
					mEListAdapter.getEListData().clear();
					mEListAdapter.notifyDataSetChanged();
					deleteAllGamesText.setVisibility(View.GONE);
				}
				FrameworkUtils.showToast(ActivitySelfMyOrder.this,
						getString(R.string.notice_quit_order_success));

			}

			@Override
			public void onFailure(Response resp) {

			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

	}

	private void updateOrderAnchors() {
		if (mListData == null)
			return;
		mAnchorLayout.removeAllViews();
		for (int i = 0; i < mListData.size(); i++) {
			final Anchor anchor = mListData.get(i);
			final View item = mLayoutInflater.inflate(R.layout.item_anchor, null);
			View layout = item.findViewById(R.id.layout);
			if (i != 0) {
				LayoutParams layoutParams = (LayoutParams) layout.getLayoutParams();
				layoutParams.leftMargin = FrameworkUtils.dip2px(this, 22);
				layout.setLayoutParams(layoutParams);
			}
			ImageView img = (ImageView) item.findViewById(R.id.photo);
			TextView name = (TextView) item.findViewById(R.id.name);
			/*SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageSquare(anchor.image, img);*/

			getSystem(SystemCommon.class).displayDefaultImage(ActivitySelfMyOrder.this, img, anchor.image, SystemCommon.SQUARE);
			name.setText(anchor.name);
			Button book = (Button) item.findViewById(R.id.book);
			book.setVisibility(View.VISIBLE);
			book.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getSystem(SystemUser.class)
							.orderOrUnOrderAnchor(anchor.id, "0", new IUserBusynessCallBack() {
								@Override
								public void onResult(boolean result, String arg) {
									mAnchorLayout.removeView(item);
									if (mListData != null) {
										mListData.remove(anchor);
									}
									if (mListData.size() == 0) {
										deleteAllAnchorsText.setVisibility(View.GONE);
									}
								}
							});
				}
			});
			img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putSerializable("anchor_id", anchor.id);
					bundle.putString("skipType", "32");
					bundle.putString("anchorFrom", "focus");
					FrameworkUtils.skipActivity(ActivitySelfMyOrder.this, ActivityAnchorZone.class, bundle);
				}
			});
			mAnchorLayout.addView(item);
		}
	}
}

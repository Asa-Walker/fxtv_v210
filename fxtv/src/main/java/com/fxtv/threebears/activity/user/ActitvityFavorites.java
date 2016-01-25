package com.fxtv.threebears.activity.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.framework.widget.xlistview.XListView.IXListViewListener;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.MyDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FXTV-Android
 * 
 *         用户收藏
 * 
 */
public class ActitvityFavorites extends BaseActivity {
	XListView favoritesListView;
	private TextView deleteAll;
	private List<Video> favorList = new ArrayList<Video>();
	private MyAdapter mAdapter;
	private Resources myResources;
	/**
	 * 页号
	 */
	private int pageNo = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites_and_history);
		initView();
		initData();
		judeUserIsNull();
		setListener();
	}

	/**
	 * 判断用户是否登录
	 */
	private void judeUserIsNull() {
		if (getSystem(SystemUser.class).isLogin()) {
			getMoreData();
		} else {
			setNotify("请先登录才能收藏!");
		}
	}

	// 友盟统计
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * 列表为空时设置提示
	 * 
	 * @param value
	 */
	private void setNotify(String value) {
		TextView tv = new TextView(ActitvityFavorites.this);
		tv.setTextSize(20);
		tv.setText(value);
		favoritesListView.addHeaderView(tv);
		favoritesListView.setPullLoadEnable(false);
	}

	private void setListener() {
		favoritesListView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
			}

			@Override
			public void onLoadMore() {
				getMoreData();
			}
		});
	}

	/**
	 * 获取更多数据
	 */
	protected void getMoreData() {
		pageNo++;
		JsonObject params = new JsonObject();
//		params.addProperty("user_id",
//				getSystem(SystemUser.class).mUser.user_id);
		params.addProperty("page", pageNo + "");
		params.addProperty("pagesize", 20 + "");
		Utils.showProgressDialog(this);

		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_collectVideoList, params), "userVideosOfCollection", false, false, new RequestCallBack<List<Video>>() {
			@Override
			public void onSuccess(List<Video> data, Response resp) {
				if (data != null && data.size() != 0) {
					favorList.addAll(data);
					mAdapter.notifyDataSetChanged();
				}
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

	private void initData() {
		mAdapter = new MyAdapter(favorList);
		favoritesListView.setAdapter(mAdapter);
	}

	private void initView() {
		myResources = getResources();
		initActionbar();
		favoritesListView = (XListView) findViewById(R.id.activity_favor_history_listview);
		deleteAll = (TextView) findViewById(R.id.ab_editor);
		deleteListener();
		favoritesListView.setPullLoadEnable(true);
		favoritesListView.setPullRefreshEnable(false);
		favoritesListView.setEmptyText(getString(R.string.empty_str_favorites));
		favoritesListView.setEmptyDrawable(R.drawable.empty_favorite);
	}

	/**
	 * 删除视频的监听
	 */
	private void deleteListener() {
		deleteAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// initDialog();
				SystemManager
						.getInstance()
						.getSystem(SystemCommon.class)
						.showDialog(ActitvityFavorites.this, "是否取消全部收藏?",
								new MyDialog.OnClickListener() {
									@Override
									public void onClick(Dialog dialog, View view, String value) {
										cancelAllFavor();
										favorList.clear();
										dialog.dismiss();
										mAdapter.notifyDataSetChanged();
									}
								}, new MyDialog.OnClickListener() {
									@Override
									public void onClick(Dialog dialog, View view, String value) {
										dialog.dismiss();
									}
								});
			}
		});
		favoritesListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
					long id) {
				// initDialog(new int[] { position - 1 });
				SystemManager
						.getInstance()
						.getSystem(SystemCommon.class)
						.showDialog(ActitvityFavorites.this, "是否取消收藏该视频?",
								new MyDialog.OnClickListener() {
									@Override
									public void onClick(Dialog dialog, View view, String value) {
										cancelFavor(position - 1);
										favorList.remove(position - 1);
										dialog.dismiss();
										mAdapter.notifyDataSetChanged();
									}
								}, new MyDialog.OnClickListener() {
									@Override
									public void onClick(Dialog dialog, View view, String value) {
										dialog.dismiss();
									}
								});
				return true;
			}
		});
		favoritesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putString("video_id", favorList.get(position - 1).id);
				FrameworkUtils.skipActivity(ActitvityFavorites.this, ActivityVideoPlay.class,
						bundle);
			}
		});
	}

	/**
	 * 弹出对话框
	 * 
	 * @param params
	 */
	private void initDialog(final int... params) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ActitvityFavorites.this);
		builder.setMessage("是否取消收藏该视频?").setCancelable(false)
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (params.length == 0) {
							cancelAllFavor();
							favorList.clear();
						} else {
							cancelFavor(params[0]);
							favorList.remove(params[0]);
						}
						mAdapter.notifyDataSetChanged();
					}
				}).setNegativeButton("否", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.create().show();
	}

	/**
	 * 取消全部收藏
	 */
	protected void cancelAllFavor() {
		JsonObject params = new JsonObject();
//		params.addProperty("user_id",
//				getSystem(SystemUser.class).mUser.user_id);
		params.addProperty("type", "3");
		Utils.showProgressDialog(ActitvityFavorites.this);

		/*String url = processUrl("User", "delUserData", params);
		SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url callBack);
		*/
		getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.USER, ApiType.USER_delUserData, params), "allUnsubscribe", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				favorList.clear();
				FrameworkUtils.showToast(ActitvityFavorites.this,
						myResources.getString(R.string.notice_delete_success));
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActitvityFavorites.this, resp.msg);
			}

			@Override
			public void onComplete() {
				favoritesListView.stopLoadMore();
				favoritesListView.stopRefresh();
				mAdapter.notifyDataSetChanged();
				Utils.dismissProgressDialog();
			}
		});

	}

	/**
	 * 取消单个收藏
	 * 
	 * @param index
	 */
	protected void cancelFavor(int index) {
		JsonObject params = new JsonObject();
//		params.addProperty("user_id",
//				getSystem(SystemUser.class).mUser.user_id);
		// 数组的添加
		JsonArray array = new JsonArray();
		JsonObject child = new JsonObject();
		child.addProperty("id", favorList.get(index).id);
		child.addProperty("status", "0");
		array.add(child);
		params.add("collect", array);
		Utils.showProgressDialog(this);

		getSystem(SystemHttp.class).get(this,Utils.processUrl(ModuleType.USER,ApiType.USER_collectVideo,params), "userCollectOrDisVideo", false, false, new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				Utils.dismissProgressDialog();
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(ActitvityFavorites.this,
						myResources.getString(R.string.notice_no_more_data));
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

	}

	private void initActionbar() {
		ImageView back = (ImageView) findViewById(R.id.img_back);
		TextView title = (TextView) findViewById(R.id.my_title);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		title.setText("收藏管理");
	}

	class MyAdapter extends BaseListGridAdapter<Video> {

		public MyAdapter(List<Video> listData) {
			super(listData);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = View.inflate(ActitvityFavorites.this, R.layout.item_video, null);
				viewHolder = new ViewHolder();
				viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
				viewHolder.title = (TextView) convertView.findViewById(R.id.title);
				viewHolder.name = (TextView) convertView.findViewById(R.id.lable1);
				viewHolder.time = (TextView) convertView.findViewById(R.id.lable2);
				viewHolder.author = (TextView) convertView.findViewById(R.id.lable3);
				viewHolder.download = (ImageView) convertView.findViewById(R.id.down);
				viewHolder.lastTime = (TextView) convertView.findViewById(R.id.lable4);
				viewHolder.present = (ImageView) convertView.findViewById(R.id.present_icon);
				viewHolder.logo = (ImageView) convertView.findViewById(R.id.logo);
				viewHolder.download.setVisibility(View.INVISIBLE);
				viewHolder.lastTime.setVisibility(View.INVISIBLE);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final Video video = getItem(position);
			/*SystemManager.getInstance().getSystem(SystemImageLoader.class)
					.displayImageDefault(video.image, viewHolder.img);*/

			getSystem(SystemCommon.class).displayDefaultImage(ActitvityFavorites.this,viewHolder.img,video.image);
			viewHolder.title.setText(video.title);
			viewHolder.name.setText(video.game_title);
			viewHolder.time.setText(video.duration);
			viewHolder.author.setText(video.anchor_name);
			if (video.lottery_status.equals("1")) {
				viewHolder.present.setVisibility(View.VISIBLE);
				viewHolder.logo.setVisibility(View.GONE);
			} else {
				viewHolder.present.setVisibility(View.GONE);
				viewHolder.logo.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

		class ViewHolder {
			ImageView download;
			ImageView img;
			ImageView present;
			ImageView logo;
			TextView title;
			TextView name;
			TextView time;
			TextView author;
			TextView lastTime;
		}
	}
}

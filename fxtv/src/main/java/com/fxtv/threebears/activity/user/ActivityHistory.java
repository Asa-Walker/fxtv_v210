package com.fxtv.threebears.activity.user;

import android.app.Dialog;
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
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.RecentPlayHistory;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemHistory;
import com.fxtv.threebears.view.MyDialog;

import java.util.List;

/**
 * @author FXTV-Android
 * 
 *         用户观看记录
 */
public class ActivityHistory extends BaseActivity {
	private XListView mListView;
	private TextView deleteAll;
	private MyAdater mAdapter;
	private List<RecentPlayHistory> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites_and_history);
		initData();
		initView();
	}

	private void initData() {
		mList = getSystem(SystemHistory.class).getAllRecentPlayHistories();
	}

	private void initView() {
		initActionbar();
		initListView();
		deleteAll = (TextView) findViewById(R.id.ab_editor);
		deleteListener();
	}

	private void initListView() {
		mListView = (XListView) findViewById(R.id.activity_favor_history_listview);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setEmptyText(getString(R.string.empty_str_history));
		mListView.setEmptyDrawable(R.drawable.empty_history);
		mAdapter = new MyAdater(mList);
		mListView.setAdapter(mAdapter);
	}

	/**
	 * 设置删除视频与播放历史视频的监听事件
	 */
	private void deleteListener() {
		deleteAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// initDialog();
				getSystem(SystemCommon.class)
						.showDialog(ActivityHistory.this, "是否删除所有视频?", new MyDialog.OnClickListener() {
							@Override
							public void onClick(Dialog dialog, View view, String value) {
								getSystem(SystemHistory.class).deleteAllHistory();
								mList.clear();
								mAdapter.notifyDataSetChanged();
								dialog.dismiss();
							}
						}, new MyDialog.OnClickListener() {

							@Override
							public void onClick(Dialog dialog, View view, String value) {
								dialog.dismiss();
							}
						});
			}
		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				// initDialog(new int[] { position - 1 });
				getSystem(SystemCommon.class)
						.showDialog(ActivityHistory.this, "是否删除该视频?", new MyDialog.OnClickListener() {
							@Override
							public void onClick(Dialog dialog, View view, String value) {
								// getSystem(SystemHistory.class).mHistoryVideoList
								// .remove(position - 1);
								getSystem(SystemHistory.class)
										.deleteHistory(mList.get(position - 1));
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

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putString("video_id", mList.get(position - 1).vId);
				FrameworkUtils.skipActivity(ActivityHistory.this, ActivityVideoPlay.class, bundle);
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

		title.setText("历史记录");
	}

	class MyAdater extends BaseListGridAdapter<RecentPlayHistory> {

		public MyAdater(List<RecentPlayHistory> listData) {
			super(listData);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				convertView = View.inflate(ActivityHistory.this, R.layout.item_video, null);
				holder = new Holder();
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.duration = (TextView) convertView.findViewById(R.id.lable2);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.anchorName = (TextView) convertView.findViewById(R.id.lable3);
				convertView.findViewById(R.id.lable1).setVisibility(View.INVISIBLE);
				convertView.findViewById(R.id.lable4).setVisibility(View.INVISIBLE);
				convertView.findViewById(R.id.down).setVisibility(View.INVISIBLE);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			RecentPlayHistory recentPlayHistory =getItem(position);
			/*SystemManager.getInstance().getSystem(SystemImageLoader.class)
					.displayImageDefault(recentPlayHistory.vImage, holder.img);*/

			getSystem(SystemCommon.class).displayDefaultImage(ActivityHistory.this, holder.img, recentPlayHistory.vImage);
			holder.title.setText(recentPlayHistory.vTitle);
			holder.duration.setText(recentPlayHistory.vDuration);
			holder.anchorName.setText("已观看至" + recentPlayHistory.vLastPosStr);

			return convertView;
		}

		class Holder {
			ImageView img;
			TextView gameName;
			TextView duration;
			TextView title;
			TextView anchorName;
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}

}

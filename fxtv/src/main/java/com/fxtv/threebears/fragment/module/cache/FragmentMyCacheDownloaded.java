package com.fxtv.threebears.fragment.module.cache;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlayLandscape;
import com.fxtv.threebears.model.VideoCache;
import com.fxtv.threebears.service.DownloadVideoService;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemDownloadVideoManager;
import com.fxtv.threebears.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 新版缓存界面下的缓存Fragment
 *
 * @author Android2
 */
public class FragmentMyCacheDownloaded extends BaseFragment {
    private List<VideoCache> mList = new ArrayList<>();
    private boolean isShowDeleteIcon = false;
    private MyAdapter mAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private LinearLayout mLinearLayout;
    private ListView mListView;
    private List<String> mPosList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_my_cache_downloaded, container, false);
        initView();
        getData();
        initBroadCast();
        return mRoot;
    }

    private void initBroadCast() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getData();
            }
        };
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(DownloadVideoService.ACTION_DOWNLOAD_SUCCESS);
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private void initView() {
        mPosList = new ArrayList<String>();
        initListView();
        initBottomLayout();
    }

    private void initBottomLayout() {
        mLinearLayout = (LinearLayout) mRoot.findViewById(R.id.lineaer);
        // 全选
        mRoot.findViewById(R.id.all_pause).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosList.clear();
                for (int i = 0; i < mList.size(); i++) {
                    mPosList.add(i + "");
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        // 删除
        mRoot.findViewById(R.id.all_start).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showProgressDialog(getActivity());
                List<VideoCache> temp = new ArrayList<VideoCache>();
                if (mList != null && mList.size() != 0) {
                    for (int i = 0; i < mPosList.size(); i++) {
                        int pos = Integer.parseInt(mPosList.get(i));
                        VideoCache videoCache = mList.get(pos);
                        temp.add(videoCache);
                        getSystem(SystemDownloadVideoManager.class).deleteDownloadedVideo(videoCache.vid, videoCache.downloadPath);
                    }
                    mPosList.clear();
                    mList.removeAll(temp);
                    mAdapter.notifyDataSetChanged();
                }
                Utils.dismissProgressDialog();
            }
        });
    }

    private void initListView() {
        mListView = (ListView) mRoot.findViewById(R.id.fragment_explorer_anchor_child_listview);
        mAdapter = new MyAdapter(mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                VideoCache video = mList.get(position);
                if (FrameworkUtils.Files.isFileExist(video.url)) {
                    if (!isShowDeleteIcon) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("video", mList.get(position));
                        FrameworkUtils.skipActivity(getActivity(), ActivityVideoPlayLandscape.class, bundle);
                    } else {
                        changeIcon(position);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("没有找到视频的缓存路径");
                    builder.setCancelable(false).setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                }
            }
        });
    }

    private void getData() {
        List<VideoCache> tmp = getSystem(SystemDownloadVideoManager.class).getDownloadedVideos();
        View empytView = mRoot.findViewById(R.id.view_empty);
        TextView tv_empty = (TextView) empytView.findViewById(R.id.tv_empty);
        tv_empty.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.empty_cache, 0, 0);
        tv_empty.setText(String.format("你还没有缓存视频，快去缓存喜爱的视频吧!"));
        mListView.setEmptyView(empytView);
        if (tmp != null && tmp.size() != 0) {
            mList = tmp;
            mAdapter.setListData(mList);
        } else {
            //mList.clear();
        }
    }

    class MyAdapter extends BaseListGridAdapter<VideoCache> {


        public MyAdapter(List<VideoCache> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_my_cache_downloaded, null);
                holder = new Holder();
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.deleteIcon = (ImageView) convertView.findViewById(R.id.downloaded_delete_icon);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.size = (TextView) convertView.findViewById(R.id.download_speed);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final VideoCache video = mList.get(position);
            if (isShowDeleteIcon) {
                holder.deleteIcon.setVisibility(View.VISIBLE);
            } else {
//                video.video_download_Img = R.drawable.icon_choose_gray;
                holder.deleteIcon.setImageResource(R.drawable.icon_choose_gray);
                holder.deleteIcon.setVisibility(View.GONE);
            }
            boolean flag = false;
            for (int i = 0; i < mPosList.size(); i++) {
                if (mPosList.get(i).equals(position + "")) {
                    flag = true;
                    break;
                } else {
                    flag = false;
                }
            }
            if (flag) {
                holder.deleteIcon.setImageResource(R.drawable.icon_choose_blue);
            } else {
                holder.deleteIcon.setImageResource(R.drawable.icon_choose_gray);
            }
            getSystem(SystemCommon.class).displayDefaultImage(getActivity(), holder.img, video.image);
            holder.title.setText(video.title);
            holder.size.setText(video.size + "M");
            return convertView;
        }

        class Holder {
            ImageView img;
            ImageView deleteIcon;
            TextView title;
            TextView size;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    public void changeIcon(boolean change) {
        isShowDeleteIcon = change;
        if (change) {
            mLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mPosList.clear();
            mLinearLayout.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }

    class MyListenner implements OnClickListener {

        private int position;

        public MyListenner(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {

        }

    }

    private void changeIcon(int position) {
//        ImageView imageView = (ImageView) mListView.findViewWithTag("deleteIcon" + position);
        int pos = -1;
        for (int i = 0; i < mPosList.size(); i++) {
            if (mPosList.get(i).equals(position + "")) {
                pos = i;
            }
        }
        if (pos != -1) {
            mPosList.remove(pos);
        } else {
            mPosList.add(position + "");
        }
        mAdapter.notifyDataSetChanged();
    }

}

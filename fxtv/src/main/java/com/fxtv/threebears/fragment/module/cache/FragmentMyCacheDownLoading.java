package com.fxtv.threebears.fragment.module.cache;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.VideoCache;
import com.fxtv.threebears.service.DownloadVideoService;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemConfig;
import com.fxtv.threebears.system.SystemDownloadVideoManager;
import com.fxtv.threebears.view.MyDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存中的fragment
 *
 * @author Android2
 */
public class FragmentMyCacheDownLoading extends BaseFragment {
    private static final String TAG = "FragmentMyCacheDownLoading";
    private List<VideoCache> mList = new ArrayList<>();
    private MyAdapter mAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private TextView mRestSpace;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_my_cache_downloading, container, false);
        initView();
        getData();
        initBroadCast();
        return mRoot;
    }

    private void initBroadCast() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                String tag = intent.getStringExtra("tag");
                if (action.equals(DownloadVideoService.ACTION_DOWNLOAD_ING)) {
                    for (VideoCache video : mList) {
                        if (video.vid.equals(tag)) {
                            video.status = DownloadVideoService.DOWNLOAD_STATUS_ING;
                            break;
                        }
                    }
                }
                if (action.equals(DownloadVideoService.ACTION_DOWNLOAD_PAUSE)) {
                    // 下载暂停
                    for (VideoCache video : mList) {
                        if (video.vid.equals(tag)) {
                            video.status = DownloadVideoService.DOWNLOAD_STATUS_PAUSE;
                            break;
                        }
                    }
                } else if (action
                        .equals(DownloadVideoService.ACTION_DOWNLOAD_WAITING)) {
                    // 等待下载
                    for (VideoCache video : mList) {
                        if (video.vid.equals(tag)) {
                            video.status = DownloadVideoService.DOWNLOAD_STATUS_WAITING;
                            break;
                        }
                    }
                } else if (action
                        .equals(DownloadVideoService.ACTION_DOWNLOAD_FAILURE)) {
                    // 下载失败
                    for (VideoCache video : mList) {
                        String reason = intent.getStringExtra("reason");
                        if (video.vid.equals(tag)) {
                            video.status = DownloadVideoService.DOWNLOAD_STATUS_FAILURE;
                            video.failureReason = reason;
                            break;
                        }
                    }
                } else if (action
                        .equals(DownloadVideoService.ACTION_DOWNLOAD_PROGRESS)) {
                    // 更新下载进度
                    int progress = intent.getIntExtra("percentage", 0);
                    int speed = intent.getIntExtra("speed", 0);
                    for (VideoCache video : mList) {
                        if (video.vid.equals(tag)) {
                            video.status = DownloadVideoService.DOWNLOAD_STATUS_ING;
                            video.percentage = progress;
                            video.speed = speed;
                            break;
                        }
                    }
                } else if (action.equals(DownloadVideoService.ACTION_DOWNLOAD_SUCCESS)) {

                    // 下载成功
                    for (VideoCache video : mList) {
                        if (video.vid.equals(tag)) {
                            mList.remove(video);
                            break;
                        }
                    }
                } else {
                    for (VideoCache video : mList) {
                        if (video.vid.equals(tag)) {
                            video.status = DownloadVideoService.DOWNLOAD_STATUS_FAILURE;
                            break;
                        }
                    }
                    Logger.e(TAG, "not find the action=" + action);
                }
                mAdapter.notifyDataSetChanged();
            }
        };
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(DownloadVideoService.ACTION_DOWNLOAD_ING);
        myIntentFilter.addAction(DownloadVideoService.ACTION_DOWNLOAD_PAUSE);
        myIntentFilter.addAction(DownloadVideoService.ACTION_DOWNLOAD_WAITING);
        myIntentFilter.addAction(DownloadVideoService.ACTION_DOWNLOAD_FAILURE);
        myIntentFilter.addAction(DownloadVideoService.ACTION_DOWNLOAD_PROGRESS);
        myIntentFilter.addAction(DownloadVideoService.ACTION_DOWNLOAD_SUCCESS);
        myIntentFilter.addCategory(DownloadVideoService.ACTION_DOWNLOAD_CANCEL);
        // 注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private void getData() {
        List<VideoCache> tmp = getSystem(SystemDownloadVideoManager.class).getDownloadingVideos();
        if (tmp != null && tmp.size() != 0) {
            mList = tmp;
            mAdapter.setListData(mList);
        }
        initRestSpace();
    }

    private void initView() {
        initLayout();
        initListView();
    }

    /**
     * 剩余空间
     */
    private void initRestSpace() {
        mRestSpace = (TextView) mRoot.findViewById(R.id.rest_space);
        if (mList == null || mList.size() == 0) {
            mRestSpace.setVisibility(View.GONE);
        } else {
            mRestSpace.setVisibility(View.VISIBLE);
            mRestSpace.setText(getSystem(SystemConfig.class).getRestSpace());
        }
    }

    private void initLayout() {
        mRoot.findViewById(R.id.lineaer).setVisibility(View.VISIBLE);
        // 全部暂停
        mRoot.findViewById(R.id.all_pause).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList != null && mList.size() != 0) {
//                    for (int i = 0; i < mList.size(); i++) {
//                        if (mList.get(i).status == DownloadVideoService.DOWNLOAD_STATUS_ING) {
////                            getSystemDownLoad().pauseDownLoad(mList.get(i).vid);
//
//                            SystemClock.sleep(500);
//                        }
//                    }
                    getSystem(SystemDownloadVideoManager.class).pauseAllDownload();
                } else {
                    showToast("请先缓存视频!");
                }
            }
        });
        // 全部开始
        mRoot.findViewById(R.id.all_start).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList != null && mList.size() != 0) {
                    for (int i = 0; i < mList.size(); i++) {
//                        if (mList.get(i).video_download_state.equals(SystemDownLoad.DOWNLOAD_STATUS_PAUSE)
//                                || mList.get(i).video_download_state.equals(SystemDownLoad.DOWNLOAD_STATUS_FAILURE)) {
//                            getSystemDownLoad()
//                                    .downLoad2(mList.get(i).id, mList.get(i), getActivity(), null, null);
//                        }
                        if (mList.get(i).status == DownloadVideoService.DOWNLOAD_STATUS_PAUSE || mList.get(i).status == DownloadVideoService.DOWNLOAD_STATUS_FAILURE) {
                            getSystem(SystemDownloadVideoManager.class).downloadVideo(mList.get(i), null);
                        }
                    }
                } else {
                    showToast("请先缓存视频!");
                }

            }
        });
    }

    private void initListView() {
        ListView downloading = (ListView) mRoot.findViewById(R.id.fragment_explorer_anchor_child_listview);
        mAdapter = new MyAdapter(mList);
        downloading.setAdapter(mAdapter);
        downloading.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoCache video = mList.get(position);
                if (video.status == DownloadVideoService.DOWNLOAD_STATUS_ING) {
                    try {
                        // 暂停下载
//						getSystemDownLoad().pauseDownLoad(video.id);
//                        getSystem(SystemDownloadVideoManager.class).pauseDownload(video.id);
                        getSystem(SystemDownloadVideoManager.class).pauseDownload(video.vid);
                    } catch (Exception e) {
                    }
                } else if (video.status == DownloadVideoService.DOWNLOAD_STATUS_PAUSE) {
                    // 继续下载
                    // getSystemDownLoad().downLoad(video.video_id,
                    // video);
                    try {
//                        getSystemDownLoad()
//                                .downLoad2(video.id, video, getActivity(), null, null);
                        getSystem(SystemDownloadVideoManager.class).downloadVideo(video, new SystemDownloadVideoManager.IDownloadCallBack() {
                            @Override
                            public void onResult(boolean flag, String msg) {
                                if (!flag) {
                                    showToast(msg);
                                }
                            }
                        });
                    } catch (Exception e) {
                    }
                } else if (video.status == DownloadVideoService.DOWNLOAD_STATUS_FAILURE) {
                    try {
//                        getSystemDownLoad()
//                                .downLoad2(video.id, video, getActivity(), null, null);
                        getSystem(SystemDownloadVideoManager.class).downloadVideo(video, null);
                    } catch (Exception e) {
                    }
                } else if (video.status == DownloadVideoService.DOWNLOAD_STATUS_WAITING) {
                    // 继续下载
                    // getSystemDownLoad().downLoad(video.video_id,
                    // video);
                    try {
//                        getSystemDownLoad()
//                                .downLoad2(video.id, video, getActivity(), null, null);
//                        getSystem(SystemDownloadVideoManager.class).downloadVideo(video, null);
                        getSystem(SystemDownloadVideoManager.class).pauseDownload(video.vid);
                    } catch (Exception e) {
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        downloading.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                VideoCache video = mList.get(position);
                initDialog(video, "是否删除该视频?");
                return true;
            }
        });
    }

    protected void initDialog(final VideoCache video, final String str) {
        getSystem(SystemCommon.class)
                .showDialog(getActivity(), str, new MyDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, View view, String value) {
//                        String downloadState = getSystemDownLoad()
//                                .getVideoDownloadState(video.vid);
                        int status = getSystem(SystemDownloadVideoManager.class).getVideoDownloadState(video.vid);
//                        if (downloadState.equals(SystemDownLoad.DOWNLOAD_STATUS_ING)
//                                || downloadState.equals(SystemDownLoad.DOWNLOAD_STATUS_PAUSE)
//                                || downloadState.equals(SystemDownLoad.DOWNLOAD_STATUS_WAITING)
//                                || downloadState.equals(SystemDownLoad.DOWNLOAD_STATUS_FAILURE)) {

                        if (status == DownloadVideoService.DOWNLOAD_STATUS_ING
                                || status == DownloadVideoService.DOWNLOAD_STATUS_PAUSE
                                || status == DownloadVideoService.DOWNLOAD_STATUS_WAITING
                                || status == DownloadVideoService.DOWNLOAD_STATUS_FAILURE) {

//                            getSystemDownLoad().deleteDownLoading(video.vid);
                            getSystem(SystemDownloadVideoManager.class).cancelDownloadingVideo(video.vid, video.downloadPath);
                            mList.remove(video);
                            mAdapter.notifyDataSetChanged();
                            if (mList == null || mList.size() == 0) {
                                mRestSpace.setVisibility(View.GONE);
                            }
                        }
                        dialog.dismiss();
                    }
                }, new MyDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, View view, String value) {
                        dialog.dismiss();
                    }
                });
    }

    class MyAdapter extends BaseListGridAdapter<VideoCache> {

        public MyAdapter(List<VideoCache> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_downlaoding, null);
                holder = new Holder();
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.status = (TextView) convertView.findViewById(R.id.download_speed);
                holder.speed = (TextView) convertView.findViewById(R.id.download_size);
                holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            VideoCache video = mList.get(position);
            //SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(video.image, holder.img);

            getSystem(SystemCommon.class).displayDefaultImage(getActivity(), holder.img, video.image);
            holder.title.setText(video.title);
            if (DownloadVideoService.DOWNLOAD_STATUS_ING == video.status) {
                holder.status.setText(video.percentage + "%");
                // holder.speed.setText("下载中");
                holder.speed.setText(video.speed + "KB/S");
            } else if (DownloadVideoService.DOWNLOAD_STATUS_PAUSE == video.status) {
                holder.status.setText("暂停下载");
                holder.speed.setText("");
            } else if (DownloadVideoService.DOWNLOAD_STATUS_COMPLETE == video.status) {
                holder.status.setText("完成下载");
                holder.speed.setText("");
            } else if (DownloadVideoService.DOWNLOAD_STATUS_FAILURE == video.status) {
                holder.status.setText(video.failureReason);
                holder.speed.setText("");
            } else if (DownloadVideoService.DOWNLOAD_STATUS_WAITING == video.status) {
                holder.status.setText("等待下载");
                holder.speed.setText("");
            } else {
                holder.status.setText("未知状态");
                holder.speed.setText("");
            }
            holder.progressBar.setProgress(video.percentage);
            return convertView;
        }

        class Holder {
            ImageView img;
            TextView title;
            TextView speed;
            TextView status;
            ProgressBar progressBar;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}

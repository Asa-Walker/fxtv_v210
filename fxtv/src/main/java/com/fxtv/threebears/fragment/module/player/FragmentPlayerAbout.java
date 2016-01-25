package com.fxtv.threebears.fragment.module.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemDownloadVideoManager;
import com.fxtv.threebears.util.Utils;

public class FragmentPlayerAbout extends BaseFragment {
    private Video mVideo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Logger.d("debug", "onCreateView");
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_player_about, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mVideo = (Video) arguments.getSerializable("video");
        }
        initView();
        return mRoot;
    }

    private void initView() {
        initGridView();
    }

    private void initGridView() {
        GridView gv = (GridView) mRoot.findViewById(R.id.player_about_gv);
        gv.setAdapter(new MyAdapter());
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("video_id", mVideo.relate_video_list.get(position).id);
                FrameworkUtils.skipActivity(getActivity(), ActivityVideoPlay.class, bundle);
                getActivity().finish();
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        private int height;

        private MyAdapter() {
            int width = (FrameworkUtils.getScreenWidth(getActivity()) - FrameworkUtils.dip2px(getActivity(), 8) * 3) / 2;
            height = width / 16 * 9;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mVideo.relate_video_list == null ? 0 : mVideo.relate_video_list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_gv_video, null);
                holder = new ViewHolder();
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.download = (ImageView) convertView.findViewById(R.id.down);
                holder.prize = (ImageView) convertView.findViewById(R.id.prize);
                holder.name = (TextView) convertView.findViewById(R.id.lable1);
                holder.durtion = (TextView) convertView.findViewById(R.id.lable2);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.logo = (ImageView) convertView.findViewById(R.id.logo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            LayoutParams layoutParams = holder.img.getLayoutParams();
            layoutParams.height = height;
            holder.img.setLayoutParams(layoutParams);
            final Video video = mVideo.relate_video_list.get(position);
//			SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(video.image, holder.img);
            getSystem(SystemCommon.class).displayDefaultImage(FragmentPlayerAbout.this, holder.img, video.image);
//            if (video.lottery_status.equals("1")) {
//                holder.prize.setVisibility(View.VISIBLE);
//                holder.logo.setVisibility(View.GONE);
//            } else {
//                holder.prize.setVisibility(View.GONE);
//                holder.logo.setVisibility(View.VISIBLE);
//            }
            Utils.setVideoLogo(holder.prize, holder.logo, video.lottery_status);
            holder.name.setText(video.game_title);
            holder.durtion.setText(video.duration);
            holder.title.setText(video.title);
            final ImageView imgdownload = holder.download;
            holder.download.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVideoChoose = video;
                    mImageDownLoad = imgdownload;
                    getSystem(SystemCommon.class).showDownloadDialog(getActivity(), mVideoChoose, mImageDownLoad);
                }
            });
            if (getSystem(SystemDownloadVideoManager.class).isDownloaded(video.id))
                holder.download.setImageResource(R.drawable.icon_download1);
            else {
                holder.download.setImageResource(R.drawable.icon_download0);
            }
            return convertView;
        }

        class ViewHolder {
            ImageView img;
            ImageView prize;
            ImageView download;
            ImageView logo;
            TextView durtion;
            TextView name;
            TextView title;
        }
    }

    private Video mVideoChoose;
    private ImageView mImageDownLoad;
}

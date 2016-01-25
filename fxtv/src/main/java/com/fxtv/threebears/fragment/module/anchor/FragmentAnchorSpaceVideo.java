package com.fxtv.threebears.fragment.module.anchor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.widget.MyListView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorAblumVieoList;
import com.fxtv.threebears.activity.anchor.ActivityAnchorVideoList;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.model.Special;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemDownloadVideoManager;

import java.util.List;

/**
 * 新添加的主播空间的视频fragment
 *
 * @author Administrator
 */
public class FragmentAnchorSpaceVideo extends BaseFragment {
    private TextView mLatestVideo;
    private MyListView mAblumListView;
    private MyListView mLatestVideoListView;
    private Anchor mAnchor;
    private String skiptype;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_anchor_space_video, container, false);

        mAnchor = (Anchor) getArguments().get("anchor");
        skiptype = getArguments().getString("typeID");
        getData();

        initView();
        return mRoot;
    }

    private void initView() {
        mLatestVideo = (TextView) mRoot.findViewById(R.id.fragment_anchor_space_video);
        mLatestVideo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
                mBundle.putString("anchor_id", mAnchor.id);
                FrameworkUtils.skipActivity(getActivity(), ActivityAnchorVideoList.class, mBundle);
            }
        });

        initListView();

        initAblumListView();
    }

    private void initListView() {
        mLatestVideoListView = (MyListView) mRoot.findViewById(R.id.fragment_anchor_space_video_listview);

        mLatestVideoListView.setAdapter(new LastestAdapter(mAnchor.video_list));

        mLatestVideoListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = new Bundle();
                bundle.putString("video_id", mAnchor.video_list.get(position).id);
                bundle.putString("skiptype", skiptype);
                FrameworkUtils.skipActivity(getActivity(), ActivityVideoPlay.class, bundle);
            }
        });
    }

    private void initAblumListView() {
        mAblumListView = (MyListView) mRoot.findViewById(R.id.fragment_anchor_space_video_ablum_listview);

        final AblumAdapter adapter = new AblumAdapter(mAnchor.album_list);
        mAblumListView.setAdapter(adapter);

        mAblumListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("special", adapter.getItem(position));
                bundle.putString("anchor_id", mAnchor.id);
                bundle.putString("ablum_name", adapter.getItem(position).title);
                FrameworkUtils.skipActivity(getActivity(), ActivityAnchorAblumVieoList.class, bundle);
            }
        });
    }

    private void getData() {
        //mListAlbum = new ArrayList<Special>();
        //mListVideos = new ArrayList<Video>(3);

        //mListAlbum = mAnchor.album_list;
        //mListVideos = mAnchor.video_list;
    }

    class LastestAdapter extends BaseListGridAdapter<Video> {

        public LastestAdapter(List<Video> listData) {
            super(listData);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_video, null);
            }

            ImageView img = (ImageView) convertView.findViewById(R.id.img);
            TextView gameName = (TextView) convertView.findViewById(R.id.lable1);
            TextView gameTime = (TextView) convertView.findViewById(R.id.lable2);
            TextView publishTime = (TextView) convertView.findViewById(R.id.lable3);
            convertView.findViewById(R.id.lable4).setVisibility(View.INVISIBLE);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            ImageView logo = (ImageView) convertView.findViewById(R.id.logo);
            ImageView prize = (ImageView) convertView.findViewById(R.id.present_icon);
            final Video itemData = getItem(position);
            final ImageView imgdownload = (ImageView) convertView.findViewById(R.id.down);
            convertView.findViewById(R.id.down).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVideo = itemData;
                    mImageDownLoad = imgdownload;
                    getSystem(SystemCommon.class).showDownloadDialog(getActivity(), mVideo, mImageDownLoad);
                }
            });

//			SystemManager.getInstance().getSystem(SystemImageLoader.class)
//					.displayImageDefault(itemData.image, img);
            getSystem(SystemCommon.class).displayDefaultImage(FragmentAnchorSpaceVideo.this, img, itemData.image);
            if (itemData.lottery_status.equals("1")) {
                prize.setVisibility(View.VISIBLE);
                logo.setVisibility(View.GONE);
            } else {
                prize.setVisibility(View.GONE);
                logo.setVisibility(View.VISIBLE);
            }

            if (getSystem(SystemDownloadVideoManager.class).isDownloaded(itemData.id))
                imgdownload.setImageResource(R.drawable.icon_download1);
            else {
                imgdownload.setImageResource(R.drawable.icon_download0);
            }

            gameName.setText(itemData.game_title);
            gameTime.setText(itemData.duration);
            publishTime.setText(itemData.publish_time);
            title.setText(itemData.title);

            return convertView;
        }
    }

    class AblumAdapter extends BaseListGridAdapter<Special> {


        public AblumAdapter(List<Special> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_ablum, null);
                holder = new Holder();
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.videoCount = (TextView) convertView.findViewById(R.id.lable3);
                holder.ablumName = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            Special itemData = getItem(position);
//            SystemManager.getInstance().getSystem(SystemImageLoader.class)
//                    .displayImageDefault(itemData.image, holder.img);
            getSystem(SystemCommon.class).displayDefaultImage(FragmentAnchorSpaceVideo.this, holder.img, itemData.image);
            holder.videoCount.setText("共" + itemData.anchor_album_video_num + "个视频");
            holder.ablumName.setText(itemData.title);

            return convertView;
        }

        class Holder {
            ImageView img;
            TextView videoCount;
            TextView ablumName;
        }

    }

    private Video mVideo;
    private ImageView mImageDownLoad;


}

package com.fxtv.threebears.fragment.module.explorer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.explorer.ActivityRankList;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 视频排行榜下的子Fragment
 *
 * @author Android2
 */
public class FragmentExplorerVideoChild extends BaseFragment {
    //	private List<Video> mList;
    private String mListName;
    private MyAdaper mAdaper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_explorer_anchor_child, container, false);
        mListName = getArguments().getString("name");
        initView();
        getData();
        return mRoot;
    }

    public void getData() {
        if (getActivity() == null) {
            return;
        }
        JsonObject params = new JsonObject();
        params.addProperty("title", mListName);
        params.addProperty("type", ((ActivityRankList) getActivity()).TYPE);
        Utils.showProgressDialog(getActivity());
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_getRank, params);
        getSystem(SystemHttp.class).get(getActivity(), url, "getRankList", true, true, new RequestCallBack<List<Video>>() {
            @Override
            public void onSuccess(List<Video> data, Response resp) {
                if (mAdaper == null) {
                    mAdaper = new MyAdaper(null);
                }
                if (data != null && data.size() != 0) {
                    mAdaper.setListData(data);
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

    private void initView() {
        ListView listView = (ListView) mRoot.findViewById(R.id.fragment_explorer_anchor_child_listview);
        if (mAdaper == null) {
            mAdaper = new MyAdaper(null);
        }
        listView.setAdapter(mAdaper);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("video_id", mAdaper.getItem(position).id);
                FrameworkUtils.skipActivity(getActivity(), ActivityVideoPlay.class, bundle);
            }
        });
    }

    class MyAdaper extends BaseListGridAdapter<Video> {


        public MyAdaper(List<Video> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_video_rank_list, null);
                holder = new Holder();
                holder.videoImg = (ImageView) convertView.findViewById(R.id.img);
                holder.videoDurtion = (TextView) convertView.findViewById(R.id.lable2);
                holder.videoType = (TextView) convertView.findViewById(R.id.lable1);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.conmment = (TextView) convertView.findViewById(R.id.conmment);
                holder.num = (TextView) convertView.findViewById(R.id.rank_list_descirbe_num);
                holder.rankListNum = (TextView) convertView.findViewById(R.id.rank_list_num_text);
                holder.rankListImg = (ImageView) convertView.findViewById(R.id.rank_list_num);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            Video video = getItem(position);
//            SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageSquare(video.image, holder.videoImg);
            getSystem(SystemCommon.class).displayDefaultImage(FragmentExplorerVideoChild.this, holder.videoImg, video.image);
            holder.title.setText(video.title);
            holder.videoType.setText(video.game_title);
            holder.videoDurtion.setText(video.duration);
            if ("videoVisit".equals(mListName)) {
                holder.conmment.setText("播放数:" + video.nums);
            }
            if ("videoTop".equals(mListName)) {
                holder.conmment.setText("被赞数:" + video.nums);
            }
            if ("videoComment".equals(mListName)) {
                holder.conmment.setText("评论数:" + video.nums);
            }
            if (position == 0) {
                holder.rankListImg.setVisibility(View.VISIBLE);
                holder.rankListNum.setVisibility(View.GONE);
                holder.rankListImg.setImageResource(R.drawable.icon_num_one);
            } else if (position == 1) {
                holder.rankListImg.setVisibility(View.VISIBLE);
                holder.rankListNum.setVisibility(View.GONE);
                holder.rankListImg.setImageResource(R.drawable.icon_num_two);
            } else if (position == 2) {
                holder.rankListImg.setVisibility(View.VISIBLE);
                holder.rankListNum.setVisibility(View.GONE);
                holder.rankListImg.setImageResource(R.drawable.icon_num_three);
            } else {
                holder.rankListImg.setVisibility(View.INVISIBLE);
                holder.rankListNum.setVisibility(View.VISIBLE);
                holder.rankListNum.setText("" + (position + 1));
            }
            return convertView;
        }

        class Holder {
            ImageView videoImg;
            ImageView rankListImg;
            TextView num;
            TextView rankListNum;
            TextView videoType;
            TextView videoDurtion;
            TextView title;
            TextView conmment;
        }
    }
}

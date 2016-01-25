package com.fxtv.threebears.fragment.module.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemFragmentManager;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorAblumVieoList;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.model.Special;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 视频详情页--专辑Fragment
 *
 * @author Android2
 */
public class FragmentPlayerFlashAlbum extends BaseFragment {
    private XListView mListView;
    private List<Special> mList;
    private Anchor mAnchor;
    private MyListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_ablum_list, container, false);
        Video video = (Video) getArguments().getSerializable("video");
        if(video!=null){
            mAnchor = video.anchor;
        }
        // mAnchor = ((ActivityVideoPlay) getActivity()).getVideo().anchor;
        initView();
        getData();
        return mRoot;
    }

    private void getData() {
        JsonObject params = new JsonObject();
        params.addProperty("id", mAnchor.id);
        params.addProperty("page", "1");
        params.addProperty("pagesize", "50");
        String url = Utils.processUrl(ModuleType.ANCHOR, ApiType.ANCHOR_album, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getActivity(), url, "getAblumList", false, true, new RequestCallBack<List<Special>>() {
            @Override
            public void onSuccess(List<Special> data, Response resp) {
                if (data != null) {
                    mList = data;
                }
                mAdapter.notifyDataSetChanged();
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
        // 隐藏自身
        mRoot.findViewById(R.id.cancel_fragment).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).getTransaction(getActivity())
                        .hide(FragmentPlayerFlashAlbum.this).commit();
                ((ActivityVideoPlay) getActivity()).setFragmentPos(0);
            }
        });
        // 阻止点击事件向下传递
        mRoot.findViewById(R.id.parent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        initListView();
    }

    private void initListView() {
        mListView = (XListView) mRoot.findViewById(R.id.ablum_lv);
        mListView.setPullRefreshEnable(false);
        mListView.setPullLoadEnable(false);
        mAdapter = new MyListAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("special", mList.get(position - 1));
                bundle.putString("anchor_id", mAnchor.id);
                bundle.putString("ablum_name", mList.get(position - 1).title);
                FrameworkUtils.skipActivity(getActivity(), ActivityAnchorAblumVieoList.class, bundle);
            }
        });
    }

    class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mList != null ? mList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
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
//			SystemManager.getInstance().getSystem(SystemImageLoader.class)
//					.displayImageDefault(mList.get(position).image, holder.img);
            getSystem(SystemCommon.class).displayDefaultImage(FragmentPlayerFlashAlbum.this, holder.img, mList.get(position).image);
            holder.videoCount.setText("共" + mList.get(position).video_num + "个视频");
            holder.ablumName.setText(mList.get(position).title);
            return convertView;
        }

        class Holder {
            ImageView img;
            TextView videoCount;
            TextView ablumName;
        }
    }
}

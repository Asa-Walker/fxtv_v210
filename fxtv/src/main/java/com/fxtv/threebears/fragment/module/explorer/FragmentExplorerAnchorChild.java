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
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.activity.explorer.ActivityRankList;
import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 主播排行帮下的子Fragment
 *
 * @author Android2
 */
public class FragmentExplorerAnchorChild extends BaseFragment {
    //private List<Anchor> mList;
    /**
     * 排行榜的名字
     */
    private String mListName;
    private MyAdaper mAdaper;
    private String mType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_explorer_anchor_child, container, false);
        mType = ((ActivityRankList) getActivity()).TYPE;
        mListName = getArguments().getString("name");
        initView();
        getData();
        return mRoot;
    }

    public void getData() {
        JsonObject params = new JsonObject();
        params.addProperty("title", mListName);
        params.addProperty("type", ((ActivityRankList) getActivity()).TYPE);
        Utils.showProgressDialog(getActivity());
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_getRank, params);
        getSystem(SystemHttp.class).get(getActivity(), url, "getRankList", true, true, new RequestCallBack<List<Anchor>>() {
            @Override
            public void onSuccess(List<Anchor> data, Response resp) {
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
                bundle.putString("anchor_id", mAdaper.getItem(position).id);
                FrameworkUtils.skipActivity(getActivity(), ActivityAnchorZone.class, bundle);
            }
        });
    }

    class MyAdaper extends BaseListGridAdapter<Anchor> {


        public MyAdaper(List<Anchor> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_anchor_rank_list, null);
                holder = new Holder();
                holder.anchorImg = (ImageView) convertView.findViewById(R.id.rank_list_anchor_img);
                holder.anchorName = (TextView) convertView.findViewById(R.id.rank_list_anchor_name);
                holder.num = (TextView) convertView.findViewById(R.id.rank_list_descirbe_num);
                holder.rankListNum = (TextView) convertView.findViewById(R.id.rank_list_num_text);
                holder.rankListImg = (ImageView) convertView.findViewById(R.id.rank_list_num);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            Anchor anchor = getItem(position);
//            SystemManager.getInstance().getSystem(SystemImageLoader.class)
//                    .displayImageSquare(anchor.image, holder.anchorImg);
            getSystem(SystemCommon.class).displayDefaultImage(FragmentExplorerAnchorChild.this, holder.anchorImg, anchor.image);
            holder.anchorName.setText(anchor.name);
            if ("anchorOrder".equals(mListName)) {
                holder.num.setText("订阅数:" + anchor.nums);
            }
            if ("anchorGuard".equals(mListName)) {
                holder.num.setText("守护数:" + anchor.nums);
            }
            if ("anchorVisit".equals(mListName)) {
                holder.num.setText("访问数:" + anchor.nums);
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
            ImageView anchorImg;
            ImageView rankListImg;
            TextView anchorName;
            TextView num;
            TextView rankListNum;
        }
    }
}

package com.fxtv.threebears.fragment.module.anchor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemFragmentManager;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 主播空间推荐页
 *
 * @author Android2
 */
public class FragmentAnchorSpaceRecommend extends BaseFragment {
    private GridView mGridView;
    //private List<Anchor> mList;
    private String mAnchorId;
    private MyAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_anchor_space_recommend, container,
                false);
        mAnchorId = ((ActivityAnchorZone) getActivity()).getAnchor().id;
        initView();
        getData();
        return mRoot;
    }

    private void getData() {
        JsonObject params = new JsonObject();
        params.addProperty("id", mAnchorId);
        Utils.showProgressDialog(getActivity());
        String url = Utils.processUrl(ModuleType.ANCHOR, ApiType.ANCHOR_Friend, params);
//        String url = processUrl("Anchor", "Friend", params);
        getSystem(SystemHttp.class).get(getActivity(), url, "RecommendAnchor", true, true, new RequestCallBack<List<Anchor>>() {
            @Override
            public void onSuccess(List<Anchor> data, Response resp) {
                if (mAdapter == null) {
                    mAdapter = new MyAdapter(data);
                } else {
                    mAdapter.setListData(data);
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
        // 取消fragment
        mRoot.findViewById(R.id.cancel_fragment).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemManager.getInstance().getSystem(SystemFragmentManager.class)
                        .getTransaction(getActivity()).hide(FragmentAnchorSpaceRecommend.this)
                        .commit();
                ((ActivityAnchorZone) getActivity()).setFragmentPos(0);
            }
        });
        //阻止点击事件向下传递
        mRoot.findViewById(R.id.parent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        initGridView();
    }

    private void initGridView() {
        mGridView = (GridView) mRoot.findViewById(R.id.fragment_anchor_space_recommend);
        mAdapter = new MyAdapter(null);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("anchor_id", mAdapter.getItem(position).id);
                FrameworkUtils.skipActivity(getActivity(), ActivityAnchorZone.class, bundle);
                getActivity().finish();
            }
        });
    }

    class MyAdapter extends BaseListGridAdapter<Anchor> {

        public MyAdapter(List<Anchor> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(),
                        R.layout.item_fragment_anchor_space_message_gridview, null);
                holder = new Holder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_fragment_anchor_space_message_image);
                holder.anchorName = (TextView) convertView
                        .findViewById(R.id.item_fragment_anchor_space_message_anchor);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
//			SystemManager.getInstance().getSystem(SystemImageLoader.class)
//					.displayImageSquare(getItem(position).image, holder.image);
            getSystem(SystemCommon.class).displayDefaultImage(FragmentAnchorSpaceRecommend.this, holder.image, getItem(position).image);
            holder.anchorName.setText(getItem(position).name);
            return convertView;
        }

        class Holder {
            ImageView image;
            TextView anchorName;
        }
    }
}

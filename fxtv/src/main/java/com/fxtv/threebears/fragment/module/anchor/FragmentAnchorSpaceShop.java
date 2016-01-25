package com.fxtv.threebears.fragment.module.anchor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemFragmentManager;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.model.Shop;
import com.fxtv.threebears.system.SystemCommon;

import java.util.List;

public class FragmentAnchorSpaceShop extends BaseFragment {

    private ListView mListView;

    private List<Shop> mList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_anchor_space_shop, container, false);
        initView();

        return mRoot;
    }

    private void initView() {
        // 取消fragment
        mRoot.findViewById(R.id.cancel_fragment).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemManager.getInstance().getSystem(SystemFragmentManager.class)
                        .getTransaction(getActivity()).hide(FragmentAnchorSpaceShop.this).commit();
                ((ActivityAnchorZone) getActivity()).setFragmentPos(0);
            }
        });
        //阻止点击事件向下传递
        mRoot.findViewById(R.id.parent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        getData();
        initListView();
    }

    private void getData() {
        mList = ((ActivityAnchorZone) getActivity()).getAnchor().shop_list;

    }

    private void initListView() {
        mListView = (ListView) mRoot.findViewById(R.id.fragment_anchor_space_shop_lv);
        mListView.setAdapter(new MyAdapter(mList));
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mList.get(position).link != null
                        && !mList.get(position).link.equals("")) {
                    Uri uri = Uri.parse(mList.get(position).link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });
    }

    class MyAdapter extends BaseListGridAdapter<Shop> {


        public MyAdapter(List<Shop> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_shop_img, null);
                ImageView img = (ImageView) convertView.findViewById(R.id.item_img);
//				SystemManager.getInstance().getSystem(SystemImageLoader.class)
//						.displayImageDefault(getItem(position).image, img);
                getSystem(SystemCommon.class).displayDefaultImage(FragmentAnchorSpaceShop.this, img, getItem(position).image);
            }
            return convertView;
        }

    }
}

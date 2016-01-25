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
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.widget.MyGridView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorLatestAct;
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.system.SystemCommon;

import java.util.List;

/**
 * 主播空间的消息fragment
 *
 * @author Administrator
 */
public class FragmentAnchorSpaceMessage extends BaseFragment {
    private TextView mLatestAct;
    private MyGridView mGridView;
    //private List<Anchor> mList;
    private Anchor mAnchor;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_anchor_space_message, container, false);
        mAnchor = (Anchor) getArguments().getSerializable("anchor");
        initView();
        return mRoot;
    }

    private void initView() {
        mLatestAct = (TextView) mRoot.findViewById(R.id.fragment_anchor_message_latest_act);
        mLatestAct.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("anchor_id", mAnchor.id);
                bundle.putString("anchor_name", mAnchor.name);
                bundle.putString("anchor_advator", mAnchor.avatar);
                bundle.putString("anchor_new_bbs_num", mAnchor.bbs_num);
                FrameworkUtils.skipActivity(getActivity(), ActivityAnchorLatestAct.class, bundle);
            }
        });

        TextView act = (TextView) mRoot.findViewById(R.id.fragment_anchor_message_new_message);
        act.setText("共" + mAnchor.bbs_num + "条动态");

        initShopImageView();

        initGridView();
    }

    private void initShopImageView() {
        LinearLayout layout = (LinearLayout) mRoot.findViewById(R.id.fragment_message_linear);
        LinearLayout.LayoutParams params = null;
        int width = FrameworkUtils.getScreenWidth(getActivity());
        int height = width / 6;
        int topMargin = FrameworkUtils.dip2px(getActivity(), 2);

        if (mAnchor.shop_list != null && mAnchor.shop_list.size() != 0) {
            mRoot.findViewById(R.id.fragment_anchor_message_shop).setVisibility(View.VISIBLE);
            mRoot.findViewById(R.id.fragment_message_linear).setVisibility(View.VISIBLE);
            for (int i = 0; i < mAnchor.shop_list.size(); i++) {
                final ImageView img = new ImageView(getActivity());
//                ImageLoader.getInstance().displayImage(mAnchor.shop_list.get(i).image, img, new ImageLoadingListener() {
//
//                    @Override
//                    public void onLoadingStarted(String imageUri, View view) {
//
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                        img.setImageResource(R.drawable.anchor_store_image);
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String imageUri, View view) {
//                        img.setImageResource(R.drawable.anchor_store_image);
//                    }
//                });
                getSystem(SystemCommon.class).displayDefaultImage(FragmentAnchorSpaceMessage.this, img, mAnchor.shop_list.get(i).image);
                params = new LayoutParams(width, height);
                params.setMargins(0, topMargin, 0, 0);
                img.setLayoutParams(params);
                img.setScaleType(ScaleType.CENTER_INSIDE);
                layout.addView(img);

                final int a = i;
                img.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mAnchor.shop_list.get(a).link != null && !mAnchor.shop_list.get(a).link.equals("")) {
                            // Bundle bundle = new Bundle();
                            // bundle.putString("url",
                            // Uri.parse(mAnchor.anchor_shop_list.get(a).shop_link)
                            // + "");
                            // bundle.putString("title", "店铺");
                            // FrameworkUtils.skipActivity(getActivity(),
                            // ActivityExplorerWeb.class,
                            // bundle);

                            Uri uri = Uri.parse(mAnchor.shop_list.get(a).link);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);

                        }
                    }
                });
            }
        } else {
            mRoot.findViewById(R.id.fragment_anchor_message_shop).setVisibility(View.GONE);
            mRoot.findViewById(R.id.fragment_message_linear).setVisibility(View.GONE);
        }
    }

    private void initGridView() {
        mGridView = (MyGridView) mRoot.findViewById(R.id.fragment_anchor_message_gridview);
        //	getData();
        final MyAdapter adapter = new MyAdapter(mAnchor.recom);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("anchor_id", adapter.getItem(position).id);
                FrameworkUtils.skipActivity(getActivity(), ActivityAnchorZone.class, bundle);
            }
        });

    }
/*
    private void getData() {
		mList = new ArrayList<Anchor>();
		mList = mAnchor.recom;
	}*/

    class MyAdapter extends BaseListGridAdapter<Anchor> {

        public MyAdapter(List<Anchor> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_fragment_anchor_space_message_gridview, null);
                holder = new Holder();
                holder.image = (ImageView) convertView.findViewById(R.id.item_fragment_anchor_space_message_image);
                holder.anchorName = (TextView) convertView.findViewById(R.id.item_fragment_anchor_space_message_anchor);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
//			SystemManager.getInstance().getSystem(SystemImageLoader.class)
//					.displayImageSquare(getItem(position).avatar, holder.image);
            getSystem(SystemCommon.class).displayDefaultImage(FragmentAnchorSpaceMessage.this, holder.image, getItem(position).avatar, SystemCommon.SQUARE);
            holder.anchorName.setText(getItem(position).name);
            return convertView;
        }

        class Holder {
            ImageView image;
            TextView anchorName;
        }

    }

}

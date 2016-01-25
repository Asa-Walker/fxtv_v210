package com.fxtv.threebears.activity.anchor;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.Special;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 专辑列表页面
 *
 * @author Android2
 */
public class ActivityAblumList extends BaseActivity {
    private XListView mListView;
    private String mAnchorId;
    private MyListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_ablum_video_list);
        mAnchorId = getStringExtra("anchor_id");
        initView();
        getData();
    }

    private void getData() {
        JsonObject params = new JsonObject();
        params.addProperty("id", mAnchorId);
        params.addProperty("page", "1");
        params.addProperty("pagesize", "100");
        Utils.showProgressDialog(this);
        String url = Utils.processUrl(ModuleType.ANCHOR, ApiType.ANCHOR_album, params);
        getSystem(SystemHttp.class).get(this, url, "getAblumList", true, true, new RequestCallBack<List<Special>>() {
            @Override
            public void onSuccess(List<Special> data, Response resp) {
                if (mAdapter == null) {
                    mAdapter = new MyListAdapter(data);
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
                mListView.stopRefresh();
                mListView.stopLoadMore();
                Utils.dismissProgressDialog();
            }
        });
    }


    private void initView() {
        initActionBar();
        initListView();
    }

    private void initListView() {
        mListView = (XListView) findViewById(R.id.activity_anchor_ablum_video_list_xlitview);
        mListView.setPullRefreshEnable(false);
        mListView.setPullLoadEnable(false);
        mAdapter = new MyListAdapter(null);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("special", mAdapter.getItem(position - 1));
                bundle.putString("anchor_id", mAnchorId);
                bundle.putString("ablum_name", mAdapter.getItem(position - 1).title);
                FrameworkUtils.skipActivity(ActivityAblumList.this, ActivityAnchorAblumVieoList.class, bundle);
            }
        });
    }

    private void initActionBar() {
        TextView title = (TextView) findViewById(R.id.ab_title);
        title.setText("专辑列表");
        ImageView btnBack = (ImageView) findViewById(R.id.ab_left_img);
        btnBack.setImageResource(R.drawable.icon_arrow_left1);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class MyListAdapter extends BaseListGridAdapter<Special> {
        public MyListAdapter(List<Special> listData) {
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
            Special special = getItem(position);
//			SystemManager.getInstance().getSystem(SystemImageLoader.class)
//					.displayImageDefault(special.image, holder.img);
            getSystem(SystemCommon.class).displayDefaultImage(ActivityAblumList.this, holder.img, special.image);
            holder.videoCount.setText("共" + special.video_num + "个视频");
            holder.ablumName.setText(special.title);
            return convertView;
        }

        class Holder {
            ImageView img;
            TextView videoCount;
            TextView ablumName;
        }
    }
}

package com.fxtv.threebears.activity.explorer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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
import com.fxtv.framework.widget.xlistview.XListView.IXListViewListener;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.Action;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 活动中心
 *
 * @author Android2
 */
public class ActivityExplorerMyCookie extends BaseActivity {
    private XListView mListView;
    //private List<Action> mList;
    private MyAdapter mAdapter;
    private int mPageNo;
    private String skipType = null;
    private final int mBackResult = 1990;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_my_cookie);
        skipType = getStringExtra("typeID");
        initView();
        getData(false, true);
    }

    private void getData(final Boolean refresh, Boolean showDialog) {
        if (refresh) {
            mPageNo = 1;
        } else {
            mPageNo++;
        }
        JsonObject params = new JsonObject();
        params.addProperty("type", "1");
        params.addProperty("page", mPageNo + "");
        params.addProperty("pagesize", 20 + "");
        if (showDialog) {
            Utils.showProgressDialog(this);
        }
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_activityCenter, params);
        getSystem(SystemHttp.class).get(this, url, "getActionBanner", false, true, new RequestCallBack<List<Action>>() {
            @Override
            public void onSuccess(List<Action> data, Response resp) {
                if (mAdapter == null) mAdapter = new MyAdapter(null);
                if (data != null && data.size() != 0) {
                    if (refresh) {
                        mAdapter.setListData(data);
                    } else {
                        mAdapter.addData(data);
                    }
                } else {
                    FrameworkUtils.showToast(ActivityExplorerMyCookie.this,
                            getString(R.string.notice_no_more_data));
                    mListView.noMoreData();
                }
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                mListView.stopLoadMore();
                mListView.stopRefresh();
                Utils.dismissProgressDialog();
            }
        });
    }

    private void initView() {
        initActionBar();
        initListView();
    }

    private void initListView() {
        mListView = (XListView) findViewById(R.id.activity_my_cookie_listview);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mAdapter = new MyAdapter(null);
        mListView.setAdapter(mAdapter);
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                mListView.setRefreshTime("刚刚");
                mListView.setPullLoadEnable(true);
                getData(true, false);
            }

            @Override
            public void onLoadMore() {
                getData(false, false);
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ActivityExplorerMyCookie.this, ActivityMissionDetail.class);
                intent.putExtra("activity_id", mAdapter.getItem(position - 1).id);
                intent.putExtra("skipType", "41");
                startActivityForResult(intent, mBackResult);
//                Bundle bundle=new Bundle();

//                SystemManager.getInstance().getSystem(SystemCommon.class).jump(ActivityExplorerMyCookie.this, mAdapter.getItem(position - 1));
            }
        });
    }

    private void initActionBar() {
        ((TextView) findViewById(R.id.ab_title)).setText("活动中心");
        ImageView leftImg = (ImageView) findViewById(R.id.ab_left_img);
        leftImg.setVisibility(View.VISIBLE);
        leftImg.setImageResource(R.drawable.icon_arrow_left1);
        leftImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class MyAdapter extends BaseListGridAdapter<Action> {
        private int height;

        private MyAdapter(List<Action> listdata) {
            super(listdata);
            int width = (FrameworkUtils.getScreenWidth(ActivityExplorerMyCookie.this) - FrameworkUtils.dip2px(
                    ActivityExplorerMyCookie.this, 5) * 2);
            height = width / 20 * 9;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = View.inflate(ActivityExplorerMyCookie.this, R.layout.item_task_center, null);
                holder = new Holder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.status = (ImageView) convertView.findViewById(R.id.icon_have_end);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            Action mc = getItem(position);
//            holder.title.setText(mc.title);
            LayoutParams layoutParams = holder.img.getLayoutParams();
            layoutParams.height = height;
            holder.img.setLayoutParams(layoutParams);
            if ("1".equals(mc.status)) {
                holder.status.setImageResource(R.drawable.icon_will_go);
            } else if ("2".equals(mc.status)) {
                holder.status.setImageResource(R.drawable.icon_tasking);
            } else if ("3".equals(mc.status)) {
                holder.status.setImageResource(R.drawable.icon_have_end);
            }
            getSystem(SystemCommon.class).displayRoundedImage(ActivityExplorerMyCookie.this, holder.img, mc.image, 5
            );
            return convertView;
        }

        class Holder {
            TextView title;
            ImageView img;
            ImageView status;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mBackResult && resultCode == 20) {
            this.setResult(20);
            this.finish();
        }
    }
}

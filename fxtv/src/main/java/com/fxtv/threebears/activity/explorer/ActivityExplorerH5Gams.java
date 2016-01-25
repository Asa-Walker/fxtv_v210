package com.fxtv.threebears.activity.explorer;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.fxtv.threebears.activity.h5.ActivityWebView;
import com.fxtv.threebears.activity.user.login.ActivityLogin;
import com.fxtv.threebears.model.VideoFocus;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 发现--赚饼干
 */
public class ActivityExplorerH5Gams extends BaseActivity {
    private XListView mListView;
    private MyAdapter mAdapter;
    private int mPageNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_video_center);
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
        params.addProperty("page", mPageNo + "");
        params.addProperty("pagesize", 20 + "");
        if (showDialog) {
            Utils.showProgressDialog(this);
        }
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_WEBGAMES, params);
        getSystem(SystemHttp.class).get(this, url, "getVideoThemeList", true, true, new RequestCallBack<List<VideoFocus>>() {
            @Override
            public void onSuccess(List<VideoFocus> data, Response resp) {
                if (mAdapter == null) {
                    mAdapter = new MyAdapter();
                }
                if (data != null && data.size() != 0) {
                    if (refresh) {
                        mAdapter.setListData(data);
                    } else {
                        mAdapter.addData(data);
                    }
                } else {
                    FrameworkUtils.showToast(ActivityExplorerH5Gams.this,
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
        mListView = (XListView) findViewById(R.id.activity_my_video_cneter_listview);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setEmptyText("暂无游戏");
        if (mAdapter == null) {
            mAdapter = new MyAdapter();
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!getSystem(SystemUser.class).isLogin()) {
                    showToast("请先登录");
                    FrameworkUtils.skipActivity(ActivityExplorerH5Gams.this, ActivityLogin.class);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("url", mAdapter.getListData().get(position - 1).link);
                bundle.putBoolean("share_enable", true);
                bundle.putString("share_img", mAdapter.getListData().get(position - 1).image);
                FrameworkUtils.skipActivity(ActivityExplorerH5Gams.this, ActivityWebView.class, bundle);
            }
        });

    }

    private void initActionBar() {
        ((TextView) findViewById(R.id.ab_title)).setText("小游戏");
        ImageView leftImg = (ImageView) findViewById(R.id.ab_left_img);
        leftImg.setVisibility(View.VISIBLE);
        leftImg.setImageResource(R.drawable.icon_arrow_left1);
        leftImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class MyAdapter extends BaseListGridAdapter<VideoFocus> {
        private int height;

        private MyAdapter() {
            super(null);
            int width = (FrameworkUtils.getScreenWidth(ActivityExplorerH5Gams.this) - FrameworkUtils.dip2px(
                    ActivityExplorerH5Gams.this, 5) * 2);
            height = width / 20 * 9;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = View.inflate(ActivityExplorerH5Gams.this, R.layout.item_task_center, null);
                holder = new Holder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            VideoFocus mc = getItem(position);
//            holder.title.setText(mc.title);
            ViewGroup.LayoutParams layoutParams = holder.img.getLayoutParams();
            layoutParams.height = height;
            holder.img.setLayoutParams(layoutParams);
            getSystem(SystemCommon.class).displayRoundedImage(ActivityExplorerH5Gams.this, holder.img, mc.image,
                    10);
            return convertView;
        }

        class Holder {
            TextView title;
            ImageView img;
        }
    }
}

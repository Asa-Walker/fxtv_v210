package com.fxtv.threebears.activity.explorer;

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
import com.fxtv.framework.Logger;
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
 * 飞熊杯
 *
 * @author Android2
 */
public class ActivityFeiXiongCup extends BaseActivity {
    private int mPageNo;
    private XListView mListView;
    private MyAdapter mAdapter;
    private String TAG = "ActivityFeiXiongCup";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feixiong_cup);
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
        params.addProperty("type", "1");
        if (showDialog) {
            Utils.showProgressDialog(this);
        }
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_fxcupList_v2, params);
        getSystem(SystemHttp.class).get(this, url, "getFeiXiongCup", false, false, new RequestCallBack<List<Action>>() {
            @Override
            public void onSuccess(List<Action> data, Response resp) {
                if (data != null && data.size() != 0) {
                    if (refresh) {
                        mAdapter.setListData(data);
                    } else {
                        mAdapter.addData(data);
                    }
                } else {
                    FrameworkUtils.showToast(ActivityFeiXiongCup.this, getString(R.string.notice_no_more_data));
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
        mListView = (XListView) findViewById(R.id.feixiong_cup_listview);
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
//                Bundle bundle = new Bundle();
//                bundle.putString("url", mAdapter.getItem(position - 1).link);
//                bundle.putString("share_img", mAdapter.getItem(position - 1).image);
//                bundle.putString("share_title", mAdapter.getItem(position - 1).title);
//                bundle.putBoolean("share_enable", true);
//                FrameworkUtils.skipActivity(ActivityFeiXiongCup.this, ActivityWebView.class, bundle);
                try {
                    getSystem(SystemCommon.class).jump(ActivityFeiXiongCup.this, mAdapter.getItem(position - 1));
                } catch (Exception e) {
                    Logger.e(TAG, "jump_e=" + e);
                }
            }
        });
    }

    private void initActionBar() {
        ImageView back = (ImageView) findViewById(R.id.img_back);
        TextView title = (TextView) findViewById(R.id.my_title);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("飞熊杯");
        findViewById(R.id.ab_editor).setVisibility(View.GONE);
        findViewById(R.id.my_up_down).setVisibility(View.GONE);
    }

    class MyAdapter extends BaseListGridAdapter<Action> {
        private int height;

        private MyAdapter(List<Action> list) {
            super(list);
            int width = (FrameworkUtils.getScreenWidth(ActivityFeiXiongCup.this) - FrameworkUtils.dip2px(
                    ActivityFeiXiongCup.this, 5) * 2);
            height = width / 20 * 9;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = View.inflate(ActivityFeiXiongCup.this, R.layout.item_task_center, null);
                holder = new Holder();
                holder.img = (ImageView) convertView.findViewById(R.id.img);
//                holder.matchTextView = (TextView) convertView.findViewById(R.id.have_ended);
                holder.status = (ImageView) convertView.findViewById(R.id.icon_have_end);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            Action mc = getItem(position);
//            if ("1".equals(mc.is_current)) {
//                holder.matchTextView.setText("当前赛事");
//                holder.matchTextView.setVisibility(View.VISIBLE);
//            } else {
//                holder.matchTextView.setText("历史赛事");
//                holder.matchTextView.setVisibility(View.VISIBLE);
//            }
            if ("1".equals(mc.is_current)) {
                holder.status.setImageResource(R.drawable.icon_tasking);
            } else if ("-1".equals(mc.is_current)) {
                holder.status.setImageResource(R.drawable.icon_have_end);
            } else if ("0".equals(mc.is_current)) {
                holder.status.setImageResource(R.drawable.icon_will_go);
            }
            LayoutParams layoutParams = holder.img.getLayoutParams();
            layoutParams.height = height;
            holder.img.setLayoutParams(layoutParams);
//			SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(mc.image, holder.img);
            getSystem(SystemCommon.class).displayDefaultImage(ActivityFeiXiongCup.this, holder.img, mc.image);
//            holder.title.setText(mc.title);
            return convertView;
        }

        class Holder {
            ImageView img;
            //            TextView matchTextView;
            ImageView status;
            TextView title;
        }
    }
}

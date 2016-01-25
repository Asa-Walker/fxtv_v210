package com.fxtv.threebears.activity.explorer;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.MissionItem;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 任务
 *
 * @author FXTV-Android
 */
public class ActivityExplorerTask extends BaseActivity {
    private XListView mListView;
    //private List<MissionItem> mData;
    private MyAdapter mAdatper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_task);
        initActionbar();
        initView();
        initData();
    }

    private void initActionbar() {
        ((TextView) findViewById(R.id.ab_title)).setText("每日任务");
        ImageView leftImg = (ImageView) findViewById(R.id.ab_left_img);
        leftImg.setVisibility(View.VISIBLE);
        leftImg.setImageResource(R.drawable.icon_arrow_left1);
        leftImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityExplorerTask.this.finish();
            }
        });
    }

    private void initView() {
        mListView = (XListView) findViewById(R.id.activity_mission_list);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(false);
        mAdatper = new MyAdapter(null);
        mListView.setAdapter(mAdatper);
    }

    private void initData() {
        JsonObject params = new JsonObject();
        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_dailyTasks, params);
        getSystem(SystemHttp.class).get(this, url, "DailyTaskApi", false, false, new RequestCallBack<List<MissionItem>>() {
            @Override
            public void onSuccess(List<MissionItem> data, Response resp) {
                if (data != null) {
                    mAdatper.addData(data);
                }
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {

            }
        });

    }

    class MyAdapter extends BaseListGridAdapter<MissionItem> {


        public MyAdapter(List<MissionItem> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            MissionItem item = getItem(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_mission, null);
                holder = new ViewHolder();
                holder.leftView = (TextView) convertView.findViewById(R.id.left_text);
                holder.rightView = (TextView) convertView.findViewById(R.id.right_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.leftView.setText(item.title + "(" + item.user_action_num + "/"
                    + item.daily_currency_limit + ")");
            holder.rightView.setText("+" + item.get_currency + "饼干/次");
            return convertView;
        }

        class ViewHolder {
            TextView leftView;
            TextView rightView;

        }
    }
}

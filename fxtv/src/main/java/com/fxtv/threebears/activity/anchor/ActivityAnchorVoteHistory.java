package com.fxtv.threebears.activity.anchor;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.MyListView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.VoteDetail;
import com.fxtv.threebears.model.VoteItem;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

public class ActivityAnchorVoteHistory extends BaseActivity {
    private String aid;
    //private List<VoteDetail> mData = new ArrayList<VoteDetail>();
    private int width;
    private String[] mColorList;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_vote_history);
        aid = getStringExtra("aid");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        mColorList = getResources().getStringArray(R.array.color_list);
        initView();
    }

    private void initView() {
        initActionBar();
        final ListView mlist = (ListView) findViewById(R.id.vote_history_list);
        Utils.showProgressDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("id", aid);
        String url = Utils.processUrl(ModuleType.ANCHOR, ApiType.ANCHOR_voteHistory, params);
        getSystem(SystemHttp.class).get(this, url, "historyVoteApi", false, false, new RequestCallBack<List<VoteDetail>>() {
            @Override
            public void onSuccess(List<VoteDetail> data, Response resp) {
                if (mAdapter == null) {
                    mAdapter = new MyAdapter(null);
                    mlist.setAdapter(mAdapter);
                }
                if (data != null) {
                    mAdapter.addData(data);
                }

                if (data == null || data.size() == 0) {
                    findViewById(R.id.view_actionbar_no_more_data).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.view_actionbar_no_more_data).setVisibility(View.GONE);
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

    private void initActionBar() {
        TextView title = (TextView) findViewById(R.id.ab_title);
        title.setText("投票记录");
        ImageView back = (ImageView) findViewById(R.id.ab_left_img);
        back.setImageResource(R.drawable.icon_arrow_left1);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ActivityAnchorVoteHistory.this.finish();
            }
        });
    }

    class MyAdapter extends BaseListGridAdapter<VoteDetail> {

        public MyAdapter(List<VoteDetail> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            VoteDetail voteDetailItem = getItem(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_vote_info, null);
                viewHolder = new ViewHolder();
                viewHolder.info = (TextView) convertView.findViewById(R.id.vote_history_info);
                viewHolder.toupiaoshu = (TextView) convertView.findViewById(R.id.vote_history_toupiaoshu);
                viewHolder.resultItem = (MyListView) convertView.findViewById(R.id.vote_history_result);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.info.setText(voteDetailItem.title);
            viewHolder.toupiaoshu.setText("共有" + voteDetailItem.vote_count + "位玩家参与投票");
            viewHolder.resultItem.setAdapter(new MyItemAdapter(voteDetailItem.option_list, voteDetailItem.has_vote));
            return convertView;
        }

        class ViewHolder {
            TextView info;
            TextView toupiaoshu;
            MyListView resultItem;
        }
    }

    class MyItemAdapter extends BaseListGridAdapter<VoteItem> {
        String isOpenString;

        public MyItemAdapter(List<VoteItem> dataItems, String isOpen) {
            super(dataItems);
            isOpenString = isOpen;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            VoteItem voteItem = getItem(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_vote, null);
                viewHolder = new ViewHolder();
                viewHolder.colorView = convertView.findViewById(R.id.item_vote_percent_view);
                viewHolder.percentTextView = (TextView) convertView.findViewById(R.id.item_vote_percent);
                viewHolder.checkedImage = (ImageView) convertView.findViewById(R.id.item_vote_checked);
                viewHolder.contentTextView = (TextView) convertView.findViewById(R.id.item_vote_content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (voteItem.has_vote_option.equals("1")) {
                viewHolder.checkedImage.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkedImage.setVisibility(View.GONE);
            }
            if (isOpenString.equals("1")) {
                viewHolder.contentTextView.setText(voteItem.title);
                viewHolder.percentTextView.setText(voteItem.option_percent + "%");
                viewHolder.colorView.setBackgroundColor(Color.parseColor(mColorList[position]));
                viewHolder.colorView.setLayoutParams(new LayoutParams((int) (width
                        * Float.parseFloat(voteItem.option_percent) / 100), LayoutParams.MATCH_PARENT));
            } else {
                viewHolder.colorView.setVisibility(View.GONE);
                viewHolder.percentTextView.setVisibility(View.GONE);
            }
            return convertView;
        }

        class ViewHolder {
            TextView percentTextView;
            TextView contentTextView;
            View colorView;
            ImageView checkedImage;
        }
    }
}

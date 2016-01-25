package com.fxtv.threebears.activity.anchor;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.VoteDetail;
import com.fxtv.threebears.model.VoteItem;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

;

public class ActivityAnchorVote extends BaseActivity {
    private ListView mList;
    private int width;
    private String[] mColorList;
    private int checkedPosition = -1;
    private String aid;
    private VoteDetail mVoteDetail;
    private List<VoteItem> mVoteItems = new ArrayList<VoteItem>();
    // 用户投票的那个item
    private String vote_detail_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_vote);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        mColorList = getResources().getStringArray(R.array.color_list);
        aid = getStringExtra("id");
        initView();
        initData();
    }

    private void initData() {
        JsonObject params = new JsonObject();
        params.addProperty("id", aid);
        Utils.showProgressDialog(ActivityAnchorVote.this);
        String url = Utils.processUrl(ModuleType.ANCHOR, ApiType.ANCHOR_voting, params);
        getSystem(SystemHttp.class).get(this, url, "anchorVotingApi", false, false, new RequestCallBack<VoteDetail>() {
            @Override
            public void onSuccess(VoteDetail data, Response resp) {
                mVoteDetail = data;
                if (mVoteDetail != null)
                    mVoteItems = mVoteDetail.option_list;
                initUI();
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

    private MyAdapter mAdapter;

    private void initUI() {
        if (!getSystem(SystemUser.class).isLogin()) {
            showToast("若要投票请先登录");
        }
        final RelativeLayout relativeLayout;
        if (mVoteDetail != null) {
            // 判断是否可以点击
            if (mVoteDetail.has_vote.equals("0") && getSystem(SystemUser.class).isLogin()) {
                mList.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                        if (checkedPosition == -1) {
                            arg1.findViewById(R.id.item_vote_checked).setVisibility(View.VISIBLE);
                            checkedPosition = arg2;
                            vote_detail_id = mVoteItems.get(checkedPosition).id;
                        } else if (checkedPosition == arg2) {
                            arg1.findViewById(R.id.item_vote_checked).setVisibility(View.GONE);
                            checkedPosition = -1;
                            vote_detail_id = "";
                        } else {
                            mList.getChildAt(checkedPosition - mList.getFirstVisiblePosition())
                                    .findViewById(R.id.item_vote_checked).setVisibility(View.GONE);
                            arg1.findViewById(R.id.item_vote_checked).setVisibility(View.VISIBLE);
                            checkedPosition = arg2;
                            vote_detail_id = mVoteItems.get(checkedPosition).id;
                        }
                    }
                });
            }
            findViewById(R.id.vote_info).setVisibility(View.VISIBLE);
            findViewById(R.id.vote_toupiaoshu).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.vote_info)).setText(mVoteDetail.title);
            ((TextView) findViewById(R.id.vote_toupiaoshu)).setText("共有" + mVoteDetail.vote_count + "位玩家参与投票");

            mAdapter = new MyAdapter(mVoteItems);
            mList.setAdapter(mAdapter);

            relativeLayout = (RelativeLayout) findViewById(R.id.vote_submit);
            relativeLayout.setVisibility(View.VISIBLE);
            relativeLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (vote_detail_id.equals("")) {
                        FrameworkUtils.showToast(ActivityAnchorVote.this, "请选择后提交");
                        return;
                    }
                    getSystem(SystemUser.class)
                            .submitVote(mVoteDetail.id, vote_detail_id, new IUserBusynessCallBack() {
                                @Override
                                public void onResult(boolean result, String arg) {
                                    if (result) {
                                        showToast("投票成功!");
                                        mList.setOnItemClickListener(null);
                                        Gson gson = new Gson();
                                        VoteDetail tmp = gson.fromJson(arg, VoteDetail.class);
                                        mVoteDetail = tmp;
                                        if (mVoteDetail != null)
                                            mVoteItems = mVoteDetail.option_list;
                                        mAdapter.setListData(mVoteItems);
                                        findViewById(R.id.vote_submit).setVisibility(View.GONE);
                                    } else {
                                        showToast(arg);
                                    }
                                }
                            });
                }
            });

            // 对提交按钮判断是否显示
            if (getSystem(SystemUser.class).isLogin()) {
                if (mVoteDetail.has_vote.equals("1")) {
                    relativeLayout.setVisibility(View.GONE);
                } else {
                    relativeLayout.setVisibility(View.VISIBLE);
                }

            } else {
                relativeLayout.setVisibility(View.GONE);
            }

        }
    }

    private void initView() {
        initActionbar();
        mList = (ListView) findViewById(R.id.vote_result);
        // 用户没投过票并且用户登录 才设置点击事件

        View emptyView=findViewById(R.id.view_empty);
        emptyView.setVisibility(View.GONE);
        TextView tv_empty=(TextView)emptyView.findViewById(R.id.tv_empty);
        tv_empty.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.empty_vote, 0, 0);
        String name=getStringExtra("anchor_name");
        if(TextUtils.isEmpty(name)) name="这个人";
        tv_empty.setText(String.format(getString(R.string.empty_str_vote),name));

        mList.setEmptyView(emptyView);
    }

    private void initActionbar() {
        TextView title = (TextView) findViewById(R.id.ab_title);
        title.setText("进行中的投票");

        ImageView back = (ImageView) findViewById(R.id.ab_left_img);
        back.setImageResource(R.drawable.icon_arrow_left1);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ActivityAnchorVote.this.finish();
            }
        });

        TextView votehistory = (TextView) findViewById(R.id.ab_right_tv);
        votehistory.setText("查看历史");
        votehistory.setVisibility(View.VISIBLE);
        votehistory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Bundle mBundle = new Bundle();
                mBundle.putString("aid", aid);
                FrameworkUtils.skipActivity(ActivityAnchorVote.this, ActivityAnchorVoteHistory.class, mBundle);

            }
        });
    }

    class MyAdapter extends BaseListGridAdapter<VoteItem> {

        public MyAdapter(List<VoteItem> listData) {
            super(listData);
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
            viewHolder.contentTextView.setText(voteItem.title);
            if ("1".equals(mVoteDetail.has_vote)) {
                viewHolder.colorView.setVisibility(View.VISIBLE);
                viewHolder.percentTextView.setVisibility(View.VISIBLE);
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

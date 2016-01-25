package com.fxtv.threebears.activity.explorer;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.fxtv.threebears.activity.user.login.ActivityLogin;
import com.fxtv.threebears.model.Action;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 发现--热点投票
 *
 * @author Android2
 */
public class ActivityHotVote extends BaseActivity {
    private boolean isDown = true;
    private boolean isVoting = true;
    private LinearLayout mLinearLayout;
    private TextView mVoting, mVoted;
    private XListView mListView;
    //private List<Action> mList;
    // private List<Action> mVotedList;
    private MyAdapter mAdapter;
    private int mPageNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_vote);
        initView();
        getData(false, true);
    }

    /**
     * @param refresh
     * @param showDialog --(1--进行中的投票 2--已结束的投票)
     */
    private void getData(final Boolean refresh, Boolean showDialog) {
        if (refresh) {
            mPageNo = 1;
        } else {
            mPageNo++;
        }
        JsonObject params = new JsonObject();
        if (isVoting) {
            params.addProperty("type", "1");
        } else {
            params.addProperty("type", "2");
        }
        params.addProperty("page", mPageNo + "");
        params.addProperty("pagesize", 20 + "");
        if (showDialog) {
            Utils.showProgressDialog(this);
        }
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_voteList, params);
        getSystem(SystemHttp.class).get(this, url, "getMissionDetail", true, true, new RequestCallBack<List<Action>>() {
            @Override
            public void onSuccess(List<Action> data, Response resp) {
                if (data != null && data.size() != 0) {
                    if (refresh) {
                        mAdapter.setListData(data);
                    } else {
                        mAdapter.addData(data);
                    }
                } else {
                    FrameworkUtils.showToast(ActivityHotVote.this, getString(R.string.notice_no_more_data));
                    mListView.noMoreData();
                }
            }

            @Override
            public void onFailure(Response resp) {
                if (mAdapter.getListData() != null) {
                    mAdapter.getListData().clear();
                    mAdapter.notifyDataSetChanged();
                }
                FrameworkUtils.showToast(ActivityHotVote.this, resp.msg);
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
        mLinearLayout = (LinearLayout) findViewById(R.id.activity_hot_vote_linear);
        mVoting = (TextView) findViewById(R.id.activity_hot_vote_voting);
        mVoted = (TextView) findViewById(R.id.activity_hot_vote_voted);
        mVoting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mVoted.setTextColor(getResources().getColor(R.color.text_color_default));
                mVoting.setTextColor(getResources().getColor(R.color.main_color));
                isVoting = true;
                // mAdapter.setList(mList);
                getData(true, true);
            }
        });
        mVoted.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (mVotedList == null) {
                // mVotedList = new ArrayList<Action>(mList);
                // // mVotedList.remove(0);
                // }
                mVoting.setTextColor(getResources().getColor(R.color.text_color_default));
                mVoted.setTextColor(getResources().getColor(R.color.main_color));
                isVoting = false;
                // mAdapter.setList(mVotedList);
                getData(true, true);
            }
        });
        initListView();
    }

    private void initListView() {
        mListView = (XListView) findViewById(R.id.activity_hot_vote_listview);
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
                if (getSystem(SystemUser.class).isLogin()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", mAdapter.getItem(position - 1).id);
                    if (!isVoting) {
                        bundle.putBoolean("voted", true);
                    }
                    FrameworkUtils.skipActivity(ActivityHotVote.this, ActivityVoteDetail.class, bundle);
                } else {
                    showToast("请先登录");
                    FrameworkUtils.skipActivity(ActivityHotVote.this, ActivityLogin.class);
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
        title.setText("热点投票");
        findViewById(R.id.ab_editor).setVisibility(View.GONE);
        final ImageView arrow = (ImageView) findViewById(R.id.up_down_icon);
        arrow.setVisibility(View.VISIBLE);
        findViewById(R.id.anctionbar_center).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDown) {
                    arrow.setImageResource(R.drawable.arrow_up);
                    isDown = false;
                    mLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    arrow.setImageResource(R.drawable.arrow_down);
                    isDown = true;
                    mLinearLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    class MyAdapter extends BaseListGridAdapter<Action> {
        private int height;

        public MyAdapter(List<Action> list) {
            super(list);
            int width = (FrameworkUtils.getScreenWidth(ActivityHotVote.this) - FrameworkUtils.dip2px(
                    ActivityHotVote.this, 5) * 2);
            height = width / 20 * 9;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = View.inflate(ActivityHotVote.this, R.layout.item_task_center, null);
                holder = new Holder();
                holder.img = (ImageView) convertView.findViewById(R.id.img);
//                holder.view = convertView.findViewById(R.id.touming);
//                holder.hasEnded = (TextView) convertView.findViewById(R.id.have_ended);
                holder.status = (ImageView) convertView.findViewById(R.id.icon_have_end);
                convertView.findViewById(R.id.title).setVisibility(View.GONE);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            if (isVoting) {
                holder.status.setImageResource(0);
            } else {
                holder.status.setImageResource(R.drawable.icon_have_end);
            }
            Action mc = getItem(position);
            LayoutParams layoutParams = holder.img.getLayoutParams();
            layoutParams.height = height;
            holder.img.setLayoutParams(layoutParams);
//			SystemManager.getInstance().getSystem(SystemImageLoader.class)
//					.displayImageRound(mc.image, holder.img, R.drawable.default_img_banner);
            getSystem(SystemCommon.class).displayRoundedImage(ActivityHotVote.this, holder.img, mc.image, 5);
            return convertView;
        }

        class Holder {
            ImageView img;
            //            View view;
//            TextView hasEnded;
            ImageView status;
        }
    }
}

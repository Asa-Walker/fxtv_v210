package com.fxtv.threebears.activity.explorer;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
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
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.user.login.ActivityLogin;
import com.fxtv.threebears.model.HotVoteDetail;
import com.fxtv.threebears.model.VoteItem;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 投票详情
 *
 * @author Android2
 */
public class ActivityVoteDetail extends BaseActivity {
    private static final String TAG = "ActivityVoteDetail";
    private String mId;
    private String mChooseId;
    private HotVoteDetail mHotVoteDetail;
    private GridView mGridView;
    //private List<VoteItem> mList=new ArrayList<>();
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate");
        setContentView(R.layout.activity_vote_detail);
        mId = getStringExtra("id");
        getData();
    }

    private void getData() {
        JsonObject params = new JsonObject();
        params.addProperty("id", mId);
        Utils.showProgressDialog(this);
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_voteDetail, params);
        getSystem(SystemHttp.class).get(this, url, "getVoteDetail", false, false, new RequestCallBack<HotVoteDetail>() {
            @Override
            public void onSuccess(HotVoteDetail data, Response resp) {
                mHotVoteDetail = data;
                if (mAdapter == null) {
                    mAdapter = new MyAdapter(mHotVoteDetail.option_list);
                } else {
                    mAdapter.setListData(mHotVoteDetail.option_list);
                }
                initView();
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
//        SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url, "getVoteDetail", false, false, callBack);
//        getSystemHttpRequests()
//                .getVoteDetail(ActivityVoteDetail.this, params.toString(), new RequestCallBack2() {
//                    @Override
//                    public void onSuccess(String json, boolean fromCache, String msg) {
//                        Gson gson = new Gson();
//                        mHotVoteDetail = gson.fromJson(json, HotVoteDetail.class);
//                        if (mAdapter == null) {
//                            mAdapter = new MyAdapter(mHotVoteDetail.option_list);
//                        } else {
//                            mAdapter.setListData(mHotVoteDetail.option_list);
//                        }
//                        initView();
//                    }
//
//                    @Override
//                    public void onFailure(String msg, boolean fromCache) {
//                        showToast(msg);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Utils.dismissProgressDialog();
//                    }
//                });
    }

    private void initView() {
        initAcitonBar();
        initBaseInfo();
        initGridView();
    }

    private void initBaseInfo() {
        TextView title = (TextView) findViewById(R.id.activity_vote_detail_title);
        TextView voteNum = (TextView) findViewById(R.id.activity_vote_detail_num);
        title.setText(mHotVoteDetail.title);
        voteNum.setText("已有" + mHotVoteDetail.vote_count + "人参与");
        if ("0".equals(mHotVoteDetail.has_vote)) {
            findViewById(R.id.submit).setVisibility(View.VISIBLE);
            // 提交
            findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getSystem(SystemUser.class).isLogin()) {
                        submitChoose();
                    } else {
                        FrameworkUtils.skipActivity(ActivityVoteDetail.this, ActivityLogin.class);
                    }
                }
            });
        }
    }

    /**
     * 提交投票
     */
    private void submitChoose() {
        JsonObject params = new JsonObject();
        if (mChooseId == null || "".equals(mChooseId)) {
            showToast("请先选择!");
            return;
        }
        params.addProperty("id", mId);
        params.addProperty("option_id", mChooseId);
        Utils.showProgressDialog(this);
        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_vote, params);
        getSystem(SystemHttp.class).get(this, url, "submitVote", false, false, new RequestCallBack<HotVoteDetail>() {
            @Override
            public void onSuccess(HotVoteDetail data, Response resp) {
                showToast("提交成功!");
                Gson gson = new Gson();
                mHotVoteDetail = data;
                for (int i = 0; i < mHotVoteDetail.option_list.size(); i++) {
                    mHotVoteDetail.option_list.get(i).isShown = false;
                }
                mHotVoteDetail.vote_count = mHotVoteDetail.vote_count + 1;
                TextView voteNum = (TextView) findViewById(R.id.activity_vote_detail_num);
                voteNum.setText("已有" + mHotVoteDetail.vote_count + "人参与");
                mAdapter.setListData(mHotVoteDetail.option_list);
                mGridView.setOnItemClickListener(null);
                findViewById(R.id.submit).setVisibility(View.GONE);
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
//        SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url, "submitVote", false, false, callBack);
//        getSystemHttpRequests()
//                .submitVote(ActivityVoteDetail.this, params.toString(), new RequestCallBack2() {
//                    @Override
//                    public void onSuccess(String json, boolean fromCache, String msg) {
//                        showToast("提交成功!");
//                        Gson gson = new Gson();
//                        mHotVoteDetail = gson.fromJson(json, HotVoteDetail.class);
//
//                        for (int i = 0; i < mHotVoteDetail.option_list.size(); i++) {
//                            mHotVoteDetail.option_list.get(i).isShown = false;
//                        }
//                        mHotVoteDetail.vote_count = mHotVoteDetail.vote_count + 1;
//                        TextView voteNum = (TextView) findViewById(R.id.activity_vote_detail_num);
//                        voteNum.setText("已有" + mHotVoteDetail.vote_count + "人参与");
//                        mAdapter.setListData(mHotVoteDetail.option_list);
//                        mGridView.setOnItemClickListener(null);
//                        findViewById(R.id.submit).setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onFailure(String msg, boolean fromCache) {
//                        showToast(msg);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Utils.dismissProgressDialog();
//                    }
//                });
    }

    private void initGridView() {
        mGridView = (GridView) findViewById(R.id.choose_grid_view);
        mGridView.setAdapter(mAdapter);
        if ("0".equals(mHotVoteDetail.has_vote)) {
            mGridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mChooseId = mAdapter.getItem(position).id;
                    for (int i = 0; i < mAdapter.getListData().size(); i++) {
                        mAdapter.getItem(i).isShown = (position == i);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void initAcitonBar() {
        TextView title = (TextView) findViewById(R.id.ab_title);
        title.setText("投票详情");
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

    class MyAdapter extends BaseListGridAdapter<VoteItem> {

        public MyAdapter(List<VoteItem> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = View.inflate(ActivityVoteDetail.this, R.layout.item_vote_detail, null);
                holder = new Holder();
                holder.chooseImg = (ImageView) convertView.findViewById(R.id.choose_image);
                holder.percent = (TextView) convertView.findViewById(R.id.percent);
                holder.iconImage = (ImageView) convertView.findViewById(R.id.choosed);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            VoteItem voteItem = getItem(position);
            if ("0".equals(mHotVoteDetail.has_vote)) {
                holder.percent.setVisibility(View.GONE);
            } else if ("1".equals(mHotVoteDetail.has_vote)) {
                holder.percent.setVisibility(View.VISIBLE);
                holder.percent.setText(voteItem.option_percent + "%");
            }
//			SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(voteItem.image, holder.chooseImg);
            getSystem(SystemCommon.class).displayDefaultImage(ActivityVoteDetail.this, holder.chooseImg, voteItem.image);
            if (voteItem.isShown) {
                holder.iconImage.setVisibility(View.VISIBLE);
                holder.chooseImg.setPadding(0, 0, 0, 0);
            } else {
                holder.chooseImg.setPadding(10, 10, 10, 10);
                holder.iconImage.setVisibility(View.GONE);
            }
            return convertView;
        }

        class Holder {
            TextView percent;
            ImageView chooseImg;
            ImageView iconImage;
        }
    }
}

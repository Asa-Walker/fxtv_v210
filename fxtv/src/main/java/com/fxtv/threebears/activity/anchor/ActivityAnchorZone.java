package com.fxtv.threebears.activity.anchor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragmentActivity;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemFragmentManager;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.MyGridView;
import com.fxtv.framework.widget.MyListView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.fragment.module.anchor.FragmentAnchorSpaceMes;
import com.fxtv.threebears.fragment.module.anchor.FragmentAnchorSpaceRecommend;
import com.fxtv.threebears.fragment.module.anchor.FragmentAnchorSpaceShop;
import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemAnalyze;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemDownloadVideoManager;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

;

/**
 * 主播空间
 *
 * @author Administrator
 */
public class ActivityAnchorZone extends BaseFragmentActivity {
    private String mAnchorId;
    private String typeID = null;
    private Anchor mAnchor;
    private ImageView mAnchorBg;
    private ImageView mAnchorPhoto;
    private TextView mAnchorName;
    private TextView mNumGuard, mNumOrder;
    private TextView mDescribe;
    private Button mBtnGuard, mBtnOrder;
    private TextView mLatestVideos;
    private MyGridAdapter mGridAdapter;
    private MyListAdapter mListAdapter;
    private int mFragmentPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_anchor_space);
        mAnchorId = getStringExtra("anchor_id");
        typeID = getStringExtra("skipType");
        String mAnchorFrom = getStringExtra("anchorFrom");
        JSONObject jsonObject = getSystem(SystemCommon.class).getH5Content(this);
        if (jsonObject != null) {
            String value;
            try {
                value = jsonObject.getString("id");
                if (value != null && !"".equals(value)) {
                    mAnchorId = value;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (mAnchorFrom != null && !"".equals(mAnchorFrom)) {
            getSystem(SystemAnalyze.class).analyzeUserAction("zone", mAnchorId, mAnchorFrom);
        }
        initView();
        getData();
    }

    public Anchor getAnchor() {
        return mAnchor;
    }

    private void initView() {
        findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initAnchorBaseInfo();
        // initListener();
        initLatestVideoGridView();
        initAblumListView();
        getFocus();
    }

    private void getFocus() {
        mDescribe.requestFocus();
        mDescribe.requestFocusFromTouch();
        mDescribe.setFocusable(true);
        mDescribe.setFocusableInTouchMode(true);
    }

    /**
     * 初始化主播信息
     */
    private void initAnchorBaseInfo() {
        mAnchorBg = (ImageView) findViewById(R.id.anchor_bg);
        mAnchorPhoto = (ImageView) findViewById(R.id.anchor_photo);
        mAnchorName = (TextView) findViewById(R.id.anchor_name);
        mBtnGuard = (Button) findViewById(R.id.anchor_btn_guard);
        mBtnOrder = (Button) findViewById(R.id.anchor_btn_order);
        mNumGuard = (TextView) findViewById(R.id.anchor_num_guard);
        mNumOrder = (TextView) findViewById(R.id.anchor_num_order);
        mDescribe = (TextView) findViewById(R.id.anchor_describe);
        mLatestVideos = (TextView) findViewById(R.id.new_video);
    }

    private void initShopImageView() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_new_anchor_space_Image_linear);
        LinearLayout.LayoutParams params = null;
        int width = FrameworkUtils.getScreenWidth(ActivityAnchorZone.this);
        int height = width / 6;
        int topMargin = FrameworkUtils.dip2px(ActivityAnchorZone.this, 2);
        if (mAnchor.shop_list != null && mAnchor.shop_list.size() != 0) {
            findViewById(R.id.activity_new_anchor_space_anchor_shop_text).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_new_anchor_space_Image_linear).setVisibility(View.VISIBLE);
            for (int i = 0; i < mAnchor.shop_list.size(); i++) {
                final ImageView img = new ImageView(ActivityAnchorZone.this);
                getSystem(SystemCommon.class).displayDefaultImage(ActivityAnchorZone.this, img, mAnchor.shop_list.get(i).image, SystemCommon.SHOP);
                params = new LinearLayout.LayoutParams(width, height);
                params.setMargins(0, topMargin, 0, 0);
                img.setLayoutParams(params);
                img.setScaleType(ScaleType.CENTER_INSIDE);
                layout.addView(img);
                final int a = i;
                img.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAnchor.shop_list.get(a).link != null && !mAnchor.shop_list.get(a).link.equals("")) {
                            Uri uri = Uri.parse(mAnchor.shop_list.get(a).link);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    }
                });
            }
        } else {
            findViewById(R.id.activity_new_anchor_space_anchor_shop_text).setVisibility(View.GONE);
            findViewById(R.id.activity_new_anchor_space_Image_linear).setVisibility(View.GONE);
        }
    }

    /**
     * 专辑列表的ListView
     */
    private void initAblumListView() {
        MyListView mListView = (MyListView) findViewById(R.id.activity_new_anchor_space_ablum_list);
        mListAdapter = new MyListAdapter();
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("special", mAnchor.album_list.get(position));
                bundle.putString("anchor_id", mAnchor.id);
                bundle.putString("ablum_name", mAnchor.album_list.get(position).title);
                FrameworkUtils.skipActivity(ActivityAnchorZone.this, ActivityAnchorAblumVieoList.class, bundle);
            }
        });
    }

    /**
     * 最新视频的gridview
     */
    private void initLatestVideoGridView() {
        MyGridView mGridView = (MyGridView) findViewById(R.id.activity_new_anchor_space_gv);
        mGridAdapter = new MyGridAdapter();
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("video_id", mAnchor.video_list.get(position).id);
                bundle.putString("skipType", typeID);
                FrameworkUtils.skipActivity(ActivityAnchorZone.this, ActivityVideoPlay.class, bundle);
            }
        });
    }

    private void initListener() {
        initGuardListener();
        initOrderListener();
        // 最新视频的查看更多
        findViewById(R.id.activity_new_anchor_space_more_video).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
                mBundle.putString("anchor_id", mAnchor.id);
                FrameworkUtils.skipActivity(ActivityAnchorZone.this, ActivityAnchorVideoList.class, mBundle);
            }
        });
        // 专辑列表的查看更多
        findViewById(R.id.activity_new_anchor_space_more_ablum).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
                mBundle.putString("anchor_id", mAnchor.id);
                FrameworkUtils.skipActivity(ActivityAnchorZone.this, ActivityAblumList.class, mBundle);
            }
        });
        // 留言
        if ("1".equals(mAnchor.icon_show.message)) {
            TextView mes = (TextView) findViewById(R.id.activity_new_anchor_space_msg_text);
            mes.setVisibility(View.VISIBLE);
            mes.setText("留言(" + mAnchor.message_num + ")");
            findViewById(R.id.activity_new_anchor_space_msg_text).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragmentPos = 1;
                    SystemManager.getInstance().getSystem(SystemFragmentManager.class).addAnimFragment(
                            R.id.fragment_layout, FragmentAnchorSpaceMes.class.getCanonicalName(), ActivityAnchorZone.this);
                }
            });
        } else {
            findViewById(R.id.activity_new_anchor_space_msg_text).setVisibility(View.GONE);
        }
        // 动态
        if ("1".equals(mAnchor.icon_show.bbs)) {
            TextView actTextView = (TextView) findViewById(R.id.activity_new_anchor_space_act_text);
            actTextView.setVisibility(View.VISIBLE);
            actTextView.setText("动态(" + mAnchor.bbs_num + ")");
            findViewById(R.id.activity_new_anchor_space_act_text).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("anchor_id", mAnchor.id);
                    bundle.putString("anchor_name", mAnchor.name);
                    bundle.putString("anchor_advator", mAnchor.avatar);
                    bundle.putString("anchor_new_bbs_num", mAnchor.bbs_num);
                    FrameworkUtils.skipActivity(ActivityAnchorZone.this, ActivityAnchorLatestAct.class, bundle);
                }
            });
        } else {
            findViewById(R.id.activity_new_anchor_space_act_text).setVisibility(View.GONE);
        }
        // 投票
        if ("1".equals(mAnchor.icon_show.vote)) {
            findViewById(R.id.activity_new_anchor_space_vote_text).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_new_anchor_space_vote_text).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle mBundle = new Bundle();
                    mBundle.putString("id", mAnchor.id);
                    mBundle.putString("anchor_name", mAnchor.name);
                    FrameworkUtils.skipActivity(ActivityAnchorZone.this, ActivityAnchorVote.class, mBundle);
                }
            });
        } else {
            findViewById(R.id.activity_new_anchor_space_vote_text).setVisibility(View.GONE);
        }
        // 推荐
        if ("1".equals(mAnchor.icon_show.friend)) {
            findViewById(R.id.activity_new_anchor_space_recomend_text).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_new_anchor_space_recomend_text).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragmentPos = 2;
                    SystemManager
                            .getInstance()
                            .getSystem(SystemFragmentManager.class)
                            .addAnimFragment(R.id.fragment_layout,
                                    FragmentAnchorSpaceRecommend.class.getCanonicalName(), ActivityAnchorZone.this);
                }
            });
        } else {
            findViewById(R.id.activity_new_anchor_space_recomend_text).setVisibility(View.GONE);
        }
        // 店铺
        if ("1".equals(mAnchor.icon_show.shop)) {
            findViewById(R.id.activity_new_anchor_space_shop_text).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_new_anchor_space_shop_text).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragmentPos = 3;
                    if (mAnchor.shop_list != null && mAnchor.shop_list.size() != 0) {
                        SystemManager
                                .getInstance()
                                .getSystem(SystemFragmentManager.class)
                                .addAnimFragment(R.id.fragment_layout,
                                        FragmentAnchorSpaceShop.class.getCanonicalName(), ActivityAnchorZone.this);
                    }
                }
            });
        } else {
            findViewById(R.id.activity_new_anchor_space_shop_text).setVisibility(View.GONE);
        }
    }

    private void initOrderListener() {
        OnClickListener mOrderListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean canOrder = true;
                String msg = "";
                if (!getSystem(SystemUser.class).isLogin()) {
                    canOrder = false;
                    getSystem(SystemCommon.class).noticeAndLogin(ActivityAnchorZone.this);
                } else {
                    if (getSystem(SystemUser.class).checkAnchorIsOrder(mAnchor)) {
                        canOrder = false;
                        msg = "该主播已经订阅";
                    }
                }
                if (canOrder)
                    orderAnchor();
                else {
                    if (!"".equals(msg))
                        showToast(msg);
                }
            }
        };
        mBtnOrder.setOnClickListener(mOrderListener);
    }

    private void orderAnchor() {
        Utils.showProgressDialog(this);
        getSystem(SystemUser.class)
                .orderOrUnOrderAnchor(mAnchor.id, "1", new IUserBusynessCallBack() {
                    @Override
                    public void onResult(boolean result, String arg) {
                        if (result) {
                            updateOrder();
                            String num = (String) mNumOrder.getText();
                            if (!num.contains("万")) {
                                mNumOrder.setText(Integer.parseInt(num) + 1 + "");
                            }
                            mBtnOrder.setText("已订阅");
                            mBtnOrder.setBackgroundResource(R.drawable.shape_rectangle_circular_check);
                        }
                        showToast(arg);
                    }
                });
    }

    private void initGuardListener() {
        OnClickListener mGuardListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean canGuard = true;
                String msg = "";
                if (!getSystem(SystemUser.class).isLogin()) {
                    canGuard = false;
                    getSystem(SystemCommon.class).noticeAndLogin(ActivityAnchorZone.this);
                } else {
                    if (getSystem(SystemUser.class).anchorIsGurad(mAnchor.id)) {
                        canGuard = false;
                        msg = "守护无法取消，守护其他主播可更换当前守护主播";
                    }
                }
                if (canGuard)
                    guard();
                else {
                    if (!"".equals(msg)) {
                        showToast(msg);
                    }
                }
            }
        };
        mBtnGuard.setOnClickListener(mGuardListener);
    }

    private void guard() {
        getSystem(SystemUser.class).guardAnchor(mAnchorId, new IUserBusynessCallBack() {
            @Override
            public void onResult(boolean result, String arg) {
                if (result) {
                    updateGuard();
                    if (!mAnchor.guard_num.contains("万")) {
                        String num = (String) mNumGuard.getText();
                        mNumGuard.setText((Integer.parseInt(num) + 1) + "");
                        showToast(getString(R.string.notice_protect_success));
                    }
                    mBtnGuard.setText("已守护");
                    mBtnGuard.setBackgroundResource(R.drawable.shape_rectangle_circular_check);

                } else {
                    showToast(arg);
                }
            }
        });
    }

    private void getData() {
        Utils.showProgressDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("id", mAnchorId);
        String url = Utils.processUrl(ModuleType.ANCHOR, ApiType.ANCHOR_zone, params);
        getSystem(SystemHttp.class).get(this, url, "anchorSpaceDetailsApi", false, false, new RequestCallBack<Anchor>() {
            @Override
            public void onSuccess(Anchor data, Response resp) {
                mAnchor = data;
                if (mAnchor != null) {
                    updateView();
                    updateGuard();
                    updateOrder();
                    initShopImageView();
                    initListener();
                }
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
//                getFocus();
            }
        });
    }

    private void updateOrder() {
//        if ("1".equals(mAnchor.order_status)) {
        if (getSystem(SystemUser.class).anchorIsOrder(mAnchorId) || "1".equals(mAnchor.order_status)) {
            mBtnOrder.setText("已订阅");
            mBtnOrder.setBackgroundResource(R.drawable.shape_rectangle_circular_check);
        } else

        {
            mBtnOrder.setText("订阅");
            mBtnOrder.setBackgroundResource(R.drawable.shape_rectangle_circular_main);
        }
    }

    private void updateGuard() {
        if (!getSystem(SystemUser.class).anchorIsGurad(mAnchor.id) || "0".equals(mAnchor.guard_status)) {
            mBtnGuard.setBackgroundResource(R.drawable.shape_rectangle_circular_main);
            mBtnGuard.setText("守护");
        } else {
            mBtnGuard.setBackgroundResource(R.drawable.shape_rectangle_circular_check);
            mBtnGuard.setText("已守护");
        }
    }

    private void updateView() {
//		SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(mAnchor.background, mAnchorBg);
//		SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(mAnchor.image, mAnchorPhoto);
        getSystem(SystemCommon.class).displayDefaultImage(ActivityAnchorZone.this, mAnchorBg, mAnchor.background);
        getSystem(SystemCommon.class).displayDefaultImage(ActivityAnchorZone.this, mAnchorPhoto, mAnchor.image);
        mAnchorName.setText(mAnchor.name);
        mNumGuard.setText(mAnchor.guard_num);
        mNumOrder.setText(mAnchor.order_num);
        // 留言数
        ((TextView) findViewById(R.id.activity_new_anchor_space_mes_count)).setText(mAnchor.message_num);
        // 动态数
        ((TextView) findViewById(R.id.activity_new_anchor_space_act_conut)).setText(mAnchor.bbs_num);
        String describe = mAnchor.intro;
        if (TextUtils.isEmpty(describe)) {
            mDescribe.setText("该主播暂无简介");
        } else {
            mDescribe.setText(mAnchor.intro);
        }
        if (mAnchor.is_auth.equals("1"))
            findViewById(R.id.imageView1).setVisibility(View.VISIBLE);
        else {
            findViewById(R.id.imageView1).setVisibility(View.GONE);
        }
        if (mAnchor.album_list == null || mAnchor.album_list.size() == 0) {
            findViewById(R.id.activity_new_anchor_space_all_ablum).setVisibility(View.GONE);
        }
        if (mAnchor.video_list == null || mAnchor.video_list.size() == 0) {
            mLatestVideos.setText(Html.fromHtml("<font color=\'#616161\'>" + "最新视频("
                    + "</font><font color=\'#26a9e1\'>" + 0 + "</font><font color=\'#616161\'>)</font>"));
        } else {
            mLatestVideos.setText(Html.fromHtml("<font color=\'#616161\'>" + "最新视频("
                    + "</font><font color=\'#26a9e1\'>" + mAnchor.video_list.size()
                    + "</font><font color=\'#616161\'>)</font>"));
        }
        if (mGridAdapter == null) {
            mGridAdapter = new MyGridAdapter();
        }
        if (mListAdapter == null) {
            mListAdapter = new MyListAdapter();
        }
        mListAdapter.notifyDataSetChanged();
        mGridAdapter.notifyDataSetChanged();
    }

    class MyGridAdapter extends BaseAdapter {
        private int height;

        private MyGridAdapter() {
            int width = (FrameworkUtils.getScreenWidth(ActivityAnchorZone.this) - FrameworkUtils.dip2px(
                    ActivityAnchorZone.this, 8) * 3) / 2;
            height = width / 16 * 9;
        }

        @Override
        public int getCount() {
            if (mAnchor == null) {
                mAnchor = new Anchor();
            }
            return mAnchor.video_list == null ? 0 : mAnchor.video_list.size();
        }

        @Override
        public Object getItem(int position) {
            if (mAnchor == null) {
                mAnchor = new Anchor();
            }
            return mAnchor.video_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_gv_video, null);
                holder = new ViewHolder();
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.download = (ImageView) convertView.findViewById(R.id.down);
                holder.prize = (ImageView) convertView.findViewById(R.id.prize);
                holder.name = (TextView) convertView.findViewById(R.id.lable1);
                holder.durtion = (TextView) convertView.findViewById(R.id.lable2);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.logo = (ImageView) convertView.findViewById(R.id.logo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            LayoutParams layoutParams = holder.img.getLayoutParams();
            layoutParams.height = height;
            holder.img.setLayoutParams(layoutParams);
            final Video video = mAnchor.video_list.get(position);
//            SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(video.image, holder.img);
            getSystem(SystemCommon.class).displayDefaultImage(ActivityAnchorZone.this, holder.img, video.image);
//            if (video.lottery_status.equals("1")) {
//                holder.prize.setVisibility(View.VISIBLE);
//                holder.logo.setVisibility(View.GONE);
//            } else {
//                holder.prize.setVisibility(View.GONE);
//                holder.logo.setVisibility(View.VISIBLE);
//            }
            Utils.setVideoLogo(holder.prize, holder.logo, video.lottery_status);
            holder.name.setText(video.game_title);
            holder.durtion.setText(video.duration);
            holder.title.setText(video.title);
            final ImageView imgdownload = holder.download;
            holder.download.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVideo = video;
                    mImageDownLoad = imgdownload;
                    getSystem(SystemCommon.class).showDownloadDialog(ActivityAnchorZone.this, mVideo, mImageDownLoad);
                }
            });
            if (getSystem(SystemDownloadVideoManager.class).isDownloaded(video.id))
                holder.download.setImageResource(R.drawable.icon_download1);
            else {
                holder.download.setImageResource(R.drawable.icon_download0);
            }
            return convertView;
        }

        class ViewHolder {
            ImageView img;
            ImageView prize;
            ImageView download;
            ImageView logo;
            TextView durtion;
            TextView name;
            TextView title;
        }
    }

    class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (mAnchor == null) {
                mAnchor = new Anchor();
            }
            if (mAnchor.album_list == null) {
                return 0;
            }
            if (mAnchor.album_list != null && mAnchor.album_list.size() >= 2) {
                return 2;
            } else {
                return 1;
            }
        }

        @Override
        public Object getItem(int position) {
            if (mAnchor == null) {
                mAnchor = new Anchor();
            }
            return mAnchor.album_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
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
//            SystemManager.getInstance().getSystem(SystemImageLoader.class)
//                    .displayImageDefault(mAnchor.album_list.get(position).image, holder.img);
            getSystem(SystemCommon.class).displayDefaultImage(ActivityAnchorZone.this, holder.img, mAnchor.album_list.get(position).image);
            holder.videoCount.setText("共" + mAnchor.album_list.get(position).video_num + "个视频");
            holder.ablumName.setText(mAnchor.album_list.get(position).title);
            return convertView;
        }

        class Holder {
            ImageView img;
            TextView videoCount;
            TextView ablumName;
        }

    }

    @Override
    public void onBackPressed() {
        if (!canHideFragmentByPos()) {
            super.onBackPressed();
        } else {
            setFragmentPos(0);
        }

    }

    private boolean canHideFragmentByPos() {
        switch (mFragmentPos) {
            case 0:
                return false;
            case 1:
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).hideFragment(this, FragmentAnchorSpaceMes.class.getCanonicalName());
                return true;
            case 2:
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).hideFragment(this, FragmentAnchorSpaceRecommend.class.getCanonicalName());
                return true;
            case 3:
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).hideFragment(this, FragmentAnchorSpaceShop.class.getCanonicalName());
                return true;
        }
        return false;
    }

    public void setFragmentPos(int fragmentPos) {
        mFragmentPos = fragmentPos;
    }

    private Video mVideo;
    private ImageView mImageDownLoad;


    @Override
    protected void onResume() {
        super.onResume();
        updateGuard();
        updateOrder();
    }
}

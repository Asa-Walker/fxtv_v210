package com.fxtv.threebears.activity.explorer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragmentActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.SystemShare;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.circular.CircularImage;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.fragment.module.explorer.FragmentAboutTopic;
import com.fxtv.threebears.model.TopicInfo;
import com.fxtv.threebears.model.TopicMessage;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.FixGridLayout;
import com.fxtv.threebears.view.ShareDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import emojicon.EmojiconTextView;

/**
 * 发现--话题详情
 * wzh
 *
 * 下拉加载更多，如果没数据时不能加载更多
 */

public class ActivityTopicInfo extends BaseFragmentActivity implements View.OnClickListener,FragmentAboutTopic.cancelFragmentListener {

    private RelativeLayout line_title;
    private XListView xlistview;
    private String topId;//话题Id
    private int pagesize = 20;
    private int page = 1;
    private TopicMsgAdapter topicMsgAdapter;
    private View headView;
    private TopicInfo topicInfo;
    private final int REQUEST_COMPLANINTS = 1;
    private final int REQUEST_MESSAGE_INFO = 2;
    private TextView tv_attention;
    private LinearLayout layout_about_topic;
    private ImageView head_im_background;
    Fragment aboutFragment;
    public static final String TOPIC_SHARE_URL="http://api.feixiong.tv/h5/fx_topic/topic.html?tid=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topId = getStringExtra("id");
        setContentView(R.layout.activity_topic_info);
        initView();
        initData();

    }


    private void initView() {
        xlistview = (XListView) findViewById(R.id.listview);
        layout_about_topic=(LinearLayout)findViewById(R.id.layout_about_topic);
        initBar();
        initHeadView();
        tv_attention = (TextView) findViewById(R.id.tv_attention);
        findViewById(R.id.tv_question).setOnClickListener(this);
        findViewById(R.id.tv_complaints).setOnClickListener(this);
        tv_attention.setOnClickListener(this);

        xlistview.setPullRefreshEnable(true);
        xlistview.setPullLoadEnable(true);
        xlistview.setPageSize(pagesize);
        xlistview.setEmptyText(getString(R.string.empty_str_topic));
        xlistview.setEmptyDrawable(R.drawable.empty_topic);
        xlistview.setEmptyViewEnable(true);
        topicMsgAdapter = new TopicMsgAdapter();
        xlistview.setAdapter(topicMsgAdapter);
        xlistview.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                initData();
            }

            @Override
            public void onLoadMore() {
                getTopicMessage(false);
            }
        });

        xlistview.setOnScrollListener(new XListView.OnXScrollListener() {
            @Override
            public void onXScrolling(View view) {

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {//滚动事件
                if (firstVisibleItem < 2) {
                    if (line_title.getVisibility() == View.VISIBLE) {
                        line_title.startAnimation(AnimationUtils.loadAnimation(ActivityTopicInfo.this, R.anim.out_top_to_topout));
                        line_title.setVisibility(View.GONE);
                    }
                } else {
                    if (line_title.getVisibility() == View.GONE) {
                        line_title.startAnimation(AnimationUtils.loadAnimation(ActivityTopicInfo.this, R.anim.in_topout_to_top));
                        line_title.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        layout_about_topic.setOnClickListener(new View.OnClickListener() {//加上事件，防止事件穿透
            @Override
            public void onClick(View v) {
            }
        });

    }


    private void initBar() {
        line_title = (RelativeLayout) findViewById(R.id.line_title);
        line_title.setVisibility(View.GONE);
        TextView ab_title=(TextView) findViewById(R.id.ab_title);
        ab_title.setText("" + getStringExtra("title"));
        ab_title.setMaxLines(1);
        ab_title.setEllipsize(TextUtils.TruncateAt.END);
        findViewById(R.id.ab_left_img).setVisibility(View.VISIBLE);
    }

    private void initHeadView() {
        headView = mLayoutInflater.inflate(R.layout.view_topic_info_head, null);
        head_im_background=(ImageView) headView.findViewById(R.id.im_background);
        xlistview.addHeaderView(headView);

        int headHeigh=Utils.getViewWidthHeight(headView.findViewById(R.id.relative_im))[1];
        if(headHeigh<=0) headHeigh=FrameworkUtils.dip2px(this,200);

        ViewGroup.MarginLayoutParams params= (ViewGroup.MarginLayoutParams) layout_about_topic.getLayoutParams();
        params.topMargin=headHeigh;
       // layout_about_topic.requestLayout();
    }

    private void initData() {
        Utils.showProgressDialog(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", topId);

        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_topicInfo, jsonObject);
        getSystem(SystemHttp.class).get(this, url, "topicInfo", false, false, new RequestCallBack<TopicInfo>() {
            @Override
            public void onSuccess(TopicInfo data, Response resp) {
                topicInfo = data;
                ((TextView) findViewById(R.id.ab_title)).setText("" + topicInfo.title);
                ((TextView) headView.findViewById(R.id.tv_head_title)).setText("" + topicInfo.title);
                ((TextView) headView.findViewById(R.id.tv_head_content)).setText("" + topicInfo.content);

                initHeadText();
                getSystem(SystemCommon.class).displayDefaultImage(ActivityTopicInfo.this, head_im_background, topicInfo.image);

                initFollowStatus(topicInfo.follow_status);
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
        getTopicMessage(true);
    }

    private void initHeadText() {
        String color = "#A5A5A5"; //#A5A5A5 字体颜色： 灰色
        ((TextView) headView.findViewById(R.id.tv_read_num)).setText(Html.fromHtml(topicInfo.view_num + "<br/><font color='" + color + "'>阅读</font>"));
        ((TextView) headView.findViewById(R.id.tv_complaints_num)).setText(Html.fromHtml(topicInfo.join_num + "<br/><font color='" + color + "'>吐槽</font>"));
        ((TextView) headView.findViewById(R.id.tv_attention_num)).setText(Html.fromHtml(topicInfo.follow_num + "<br/><font color='" + color + "'>关注</font>"));
    }

    private void initFollowStatus(String status) {
        if ("1".equals(status)) {
            tv_attention.setText("已关注");
        } else {
            tv_attention.setText("加关注");
        }
    }

    private void getTopicMessage(final boolean isRefre) {
        if (isRefre) {
            page = 1;
        } else {
            page++;
        }
        //吐槽列表
        JsonObject jsonMessage = new JsonObject();
        jsonMessage.addProperty("id", topId);
        jsonMessage.addProperty("page", "" + page);
        jsonMessage.addProperty("pagesize", "" + pagesize);
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_topicMessage, jsonMessage);
        getSystem(SystemHttp.class).get(this, url, "FindtopicMessage", false, true, new RequestCallBack<List<TopicMessage>>() {
            @Override
            public void onSuccess(List<TopicMessage> data, Response resp) {
                if (isRefre) {
                    topicMsgAdapter.setListData(data);
                } else {
                    topicMsgAdapter.addData(data);
                }
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                xlistview.stopRefresh();
                xlistview.stopLoadMore();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(topicInfo==null){
            return;
        }
        switch (v.getId()) {
            case R.id.tv_attention://加关注
                if (!getSystem(SystemUser.class).isLogin()) {
                    getSystem(SystemCommon.class).noticeAndLogin(this);
                    return;
                }
                JsonObject json = new JsonObject();
                JsonObject follow = new JsonObject();
                follow.addProperty("id", topId);
                //1，关注；0,取消关注
                String status = "0";
                if ("0".equals(topicInfo.follow_status)) {
                    status = "1";
                }
                follow.addProperty("status", status);
                JsonArray array = new JsonArray();
                array.add(follow);

                json.add("follow", array);
                String url = Utils.processUrl(ModuleType.USER, ApiType.USER_topic_follow, json);
                getSystem(SystemHttp.class).get(this, url, "topic_follow", false, false, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(String data, Response resp) {
                        int increament=1;
                        if ("1".equals(topicInfo.follow_status)) {
                            topicInfo.follow_status = "0";
                            increament=-1;
                        } else {
                            topicInfo.follow_status = "1";
                        }
                        initFollowStatus(topicInfo.follow_status);
                        topicInfo.follow_num=Utils.getNumberString(topicInfo.follow_num,increament);
                        initHeadText();
                        showToast("" + resp.msg);
                    }

                    @Override
                    public void onFailure(Response resp) {
                        showToast(resp.msg);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.tv_complaints://发吐槽
                if (!getSystem(SystemUser.class).isLogin()) {
                    getSystem(SystemCommon.class).noticeAndLogin(this);
                    return;
                }
                Intent intent = new Intent(this, ActivityPostTopic.class);
                intent.putExtra("topicInfo", topicInfo);
                intent.putExtra("topId", topId);
                startActivityForResult(intent, REQUEST_COMPLANINTS);
                break;
            case R.id.tv_question://相关问题
                cancelFragment();
                break;

        }
    }

    @Override
    public void cancelFragment() {
        if(aboutFragment==null){
            aboutFragment= FragmentAboutTopic.newInstance(topicInfo);
            getSupportFragmentManager().beginTransaction().add(layout_about_topic.getId(), aboutFragment)
                    .show(aboutFragment).commit();
        }
        if(layout_about_topic.getVisibility()==View.GONE){
            Animation animation=AnimationUtils.loadAnimation(this, R.anim.in_right_to_left);
            layout_about_topic.startAnimation(animation);
            layout_about_topic.setVisibility(View.VISIBLE);
        }else{
            Animation animation=AnimationUtils.loadAnimation(this, R.anim.out_left_to_right);
            layout_about_topic.startAnimation(animation);
            layout_about_topic.setVisibility(View.GONE);
        }
    }

    class TopicMsgAdapter extends BaseListGridAdapter<TopicMessage> {

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = mLayoutInflater.inflate(R.layout.item_topic_info_list, parent, false);
                holder.anchor = (CircularImage) convertView.findViewById(R.id.img);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.content = (EmojiconTextView) convertView.findViewById(R.id.content);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.pic_layout = (FixGridLayout) convertView.findViewById(R.id.pic_layout);
                holder.pic_layout.setmCellHeight(FrameworkUtils.dip2px(ActivityTopicInfo.this, 110));
                holder.pic_layout.setmCellWidth(FrameworkUtils.dip2px(ActivityTopicInfo.this, 110));
                holder.line_share = (LinearLayout) convertView.findViewById(R.id.line_share);
                holder.line_post = (LinearLayout) convertView.findViewById(R.id.line_post);
                holder.line_zan = (LinearLayout) convertView.findViewById(R.id.line_zan);
                holder.zan_nums = (TextView) convertView.findViewById(R.id.zan_nums);
                holder.share_nums = (TextView) convertView.findViewById(R.id.share_nums);
                holder.post_nums = (TextView) convertView.findViewById(R.id.post_nums);
                holder.image_zan = (ImageView) convertView.findViewById(R.id.image_zan);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            if (getListData() == null || position > getListData().size()) {
                return convertView;
            }
            final TopicMessage topicMessage = getItem(position);

            getSystem(SystemCommon.class).displayDefaultImage(ActivityTopicInfo.this, holder.anchor, topicMessage.image);
            holder.tv_time.setText("" + topicMessage.create_time);
            if(TextUtils.isEmpty(topicMessage.content)){
                holder.content.setVisibility(View.GONE);
            }else{
                holder.content.setVisibility(View.VISIBLE);
            }
            holder.content.setText("" + FrameworkUtils.unicode2String(topicMessage.content));
            holder.name.setText("" + topicMessage.nickname);
            holder.share_nums.setText("" + topicMessage.share_num);
            holder.post_nums.setText("" + topicMessage.reply_num);
            holder.zan_nums.setText("" + topicMessage.like_num);
            if ("1".equals(topicMessage.like_status)) {
                holder.image_zan.setImageResource(R.drawable.icon_ding1);
            } else {
                holder.image_zan.setImageResource(R.drawable.icon_ding0);
            }
            holder.pic_layout.removeAllViews();
            if (topicMessage.images != null && topicMessage.images.size() != 0) {
                for (int i = 0; i < topicMessage.images.size(); i++) {
                    ImageView img = new ImageView(ActivityTopicInfo.this);
                    getSystem(SystemCommon.class).displayImageForAnchorCircle(ActivityTopicInfo.this, img, topicMessage.images.get(i));
                    img.setPadding(5, 10, 5, 0);
                    final int postion1 = i;
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("URLs", topicMessage.images);
                            bundle.putInt("postion", postion1);
                            FrameworkUtils.skipActivity(ActivityTopicInfo.this, ActivityExplorerImagePager.class, bundle);
                        }
                    });
                    holder.pic_layout.addView(img);
                }
            }
            ItemOnClick itemOnClick = new ItemOnClick(topicMessage);
            holder.line_share.setOnClickListener(itemOnClick);
            holder.line_zan.setOnClickListener(itemOnClick);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("TopicMessage", topicMessage);
                    bundle1.putSerializable("TopicInfo", topicInfo);
                    bundle1.putInt("position", position);
                    Intent intent = new Intent(ActivityTopicInfo.this, ActivityExplorerHotAnser.class);
                    intent.putExtras(bundle1);
                    startActivityForResult(intent, REQUEST_MESSAGE_INFO);
                }
            });

            /*if(position==getCount()-1){//最后一条，高度增加
                convertView.findViewById(R.id.view_gray_bottom).getLayoutParams().height=FrameworkUtils.dip2px(ActivityTopicInfo.this,50);
            }else{
                convertView.findViewById(R.id.view_gray_bottom).getLayoutParams().height=FrameworkUtils.dip2px(ActivityTopicInfo.this,5);
            }*/
            return convertView;
        }

        class ItemOnClick implements View.OnClickListener {
            private TopicMessage topicMessage;

            public ItemOnClick(TopicMessage topicMessage) {
                this.topicMessage = topicMessage;
            }

            @Override
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.line_share://分享
                        ShareModel shareModel = new ShareModel();
                        shareModel.shareTitle = topicInfo.title;
                        shareModel.shareSummary = topicMessage.content;
                        shareModel.fileImageUrl = topicInfo.image;
                        shareModel.shareUrl = TOPIC_SHARE_URL+topicMessage.id;
                        getSystem(SystemCommon.class).showShareDialog(ActivityTopicInfo.this, shareModel, new ShareDialog.ShareCallBack() {
                            @Override
                            public void onShareSuccess() {
                                ((TextView)v.findViewById(R.id.share_nums)).setText(Utils.getNumberString(topicMessage.share_num,1));
                                JsonObject obj = new JsonObject();
                                obj.addProperty("id", "" + topicMessage.id);
                                getSystem(SystemHttp.class).get(ActivityTopicInfo.this, Utils.processUrl(ModuleType.USER, ApiType.USER_shareTopicMessage, obj), "shareTopicMessage", false,false,null);//分享成功接口
                                showToast("分享成功");
                            }

                            @Override
                            public void onShareFailure(String msg) {
                                showToast(msg + "分享失败");
                            }

                            @Override
                            public void onCancel() {
                                showToast("分享取消");
                            }
                        });
                        break;
                    case R.id.line_zan:
                        if (!getSystem(SystemUser.class).isLogin()) {
                            getSystem(SystemCommon.class).noticeAndLogin(ActivityTopicInfo.this);
                            return;
                        }
                        JsonObject json_like = new JsonObject();
                        json_like.addProperty("id", "" + topicMessage.id);
                        json_like.addProperty("status", "1");//1，赞；0,取消赞
                        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_topicMessageLike, json_like);
                        getSystem(SystemHttp.class).get(ActivityTopicInfo.this, url, "topicMessageLike", false, false, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(String data, Response resp) {
                                topicMessage.like_status = "1";
                                topicMessage.like_num = Utils.getNumberString(topicMessage.like_num, 1);

                                TextView zan_nums = (TextView) v.findViewById(R.id.zan_nums);
                                ImageView im_zan = (ImageView) v.findViewById(R.id.image_zan);
                                im_zan.setImageResource(R.drawable.icon_ding1);
                                zan_nums.setText("" + topicMessage.like_num);
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
            }
        }

        class Holder {
            CircularImage anchor;
            TextView tv_time;
            EmojiconTextView content;
            TextView name;
            FixGridLayout pic_layout;
            LinearLayout line_share;
            LinearLayout line_post;
            LinearLayout line_zan;
            TextView zan_nums;
            TextView share_nums;
            TextView post_nums;
            ImageView image_zan;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SystemManager.getInstance().getSystem(SystemShare.class).onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_COMPLANINTS) {//吐槽成功
            TopicMessage resultMsg = (TopicMessage) data.getSerializableExtra("TopicMessage");
            if (resultMsg == null) return;

            List<TopicMessage> listdata = topicMsgAdapter.getListData();
            if (listdata == null) {
                listdata = new ArrayList<>();
            }
            listdata.add(0, resultMsg);
            topicMsgAdapter.setListData(listdata);

            topicInfo.join_num=Utils.getNumberString(topicInfo.join_num,1);
            initHeadText();
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_MESSAGE_INFO) {//吐槽详情，能点赞
            int position = data.getIntExtra("position", -1);
            TopicMessage topicMessage = (TopicMessage) data.getSerializableExtra("TopicMessage");
            if (position >= 0 && topicMessage != null && position < topicMsgAdapter.getCount()) {
                topicMsgAdapter.getListData().set(position, topicMessage);
                topicMsgAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(layout_about_topic!=null && layout_about_topic.getVisibility()==View.VISIBLE){
            cancelFragment();
        }else{
            super.onBackPressed();
        }
    }
}

package com.fxtv.threebears.activity.explorer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.SystemShare;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.TopicInfo;
import com.fxtv.threebears.model.TopicMessage;
import com.fxtv.threebears.model.TopicReply;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.FixGridLayout;
import com.fxtv.threebears.view.ShareDialog;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import emojicon.EmojiconTextView;

/**
 * 发现--热聊--评论
 */
public class ActivityExplorerHotAnser extends BaseActivity {
    private XListView mListView;
    private int mPageNum;
    private double mPXTimes;
    private final int mPageSize = 20;
    private MyAdapter mAdapter;
    private TopicMessage mTopicMessage;
    private TopicInfo mTopicInfo;
    private PopupWindow optionPw;
    private List<TopicReply> mList;
    private ImageView mZanImg;
    private EmojiconTextView content;
    private int mPos, mPosJuBao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_act_answer);
        mTopicMessage = (TopicMessage) baseSavedInstance.getSerializable("TopicMessage");
        mTopicInfo = (TopicInfo) baseSavedInstance.getSerializable("TopicInfo");
        mPos = baseSavedInstance.getInt("position");
        initView();

    }

    private void initView() {
        initActionBar();
        initListView();
        initBottom();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("TopicMessage", mTopicMessage);
        intent.putExtra("position", mPos);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private void initBottom() {
        mZanImg = (ImageView) findViewById(R.id.image_zan);
        ((TextView)findViewById(R.id.share_nums)).setText("分享");
        ((TextView)findViewById(R.id.post_nums)).setText("评论");
        ((TextView)findViewById(R.id.line_bottom).findViewById(R.id.zan_nums)).setText("赞");
        if ("1".equals(mTopicMessage.like_status)) {
            mZanImg.setImageResource(R.drawable.icon_ding1);
        } else {
            mZanImg.setImageResource(R.drawable.icon_ding0);
        }
        //转发
        findViewById(R.id.line_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareModel shareModel = new ShareModel();
                shareModel.shareTitle = mTopicInfo.title;
                shareModel.shareSummary = mTopicMessage.content;
                shareModel.fileImageUrl = mTopicInfo.image;
                shareModel.shareUrl = ActivityTopicInfo.TOPIC_SHARE_URL + mTopicMessage.id;
                getSystem(SystemCommon.class).showShareDialog(ActivityExplorerHotAnser.this, shareModel, new ShareDialog.ShareCallBack() {
                    @Override
                    public void onShareSuccess() {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("id", "" + mTopicMessage.id);
                        getSystem(SystemHttp.class).get(ActivityExplorerHotAnser.this, Utils.processUrl(ModuleType.USER, ApiType.USER_shareTopicMessage, obj), "shareTopicMessage", false, false, null);//分享成功接口
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
            }
        });
        //评论
        findViewById(R.id.line_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("topic_id", mTopicMessage.id);
                bundle.putString("title", "发评论");
                Intent intent = new Intent(ActivityExplorerHotAnser.this, ActivityExplorerAnser.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 90);
            }
        });
        //赞
        findViewById(R.id.line_zan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("1".equals(mTopicMessage.like_status)) {
                    showToast("已点赞，不能重复操作");
                } else {
                    zan(mTopicMessage.id, 1, 1);
                }
            }
        });

    }

    //吐槽点赞或取消赞
    public void zan(String id, final int type, final int status) {
        getSystem(SystemUser.class).zanOrdisZan(id, type, status, new IUserBusynessCallBack() {
            @Override
            public void onResult(boolean result, String arg) {
                showToast(arg);
                if (result) {
                    //赞
                    if (status == 1) {
                        if (type == 1) {
                            mTopicMessage.like_status = "1";
                            mZanImg.setImageResource(R.drawable.icon_ding1);
                            mLikeNums.setText(Utils.getNumberString(mTopicMessage.like_num, 1));
                            mTopicMessage.like_num = Utils.getNumberString(mTopicMessage.like_num, 1);
                        } else if (type == 0) {
                            ImageView img = (ImageView) mListView.findViewWithTag("ding" + mPosition);
                            img.setImageResource(R.drawable.icon_ding1);
                        }
                    } else {
                        mTopicMessage.like_status = "0";
                        mZanImg.setImageResource(R.drawable.icon_ding0);
                        Utils.getNumberString(mTopicMessage.like_num, -1);
                    }
                }
            }
        });
    }

    private void initListView() {
        mListView = (XListView) findViewById(R.id.activity_anchor_act_answer_ListView);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setEmptyViewEnable(false);
        mListView.setPageSize(mPageSize);
        getData(false, true);
        if (mAdapter == null) {
            mAdapter = new MyAdapter(null);
        }
        initHeader();
        mListView.setAdapter(mAdapter);
        mListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mListView.setRefreshTime("刚刚");
                getData(true, false);
            }

            @Override
            public void onLoadMore() {
                getData(false, false);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (position == 1) {
                    mPosJuBao = -1;
                    showCopyPop(convertView);
                } else {
                    mPosJuBao = position - 2;
                    showCopyPop(view);
                }
                return false;
            }
        });
    }


    private TextView mLikeNums, mCommentNums;
    private View convertView;

    private void initHeader() {

        if (mTopicMessage != null) {
            convertView = View.inflate(this, R.layout.item_anchor_latest_act_header, null);
            ImageView actImg = (ImageView) convertView.findViewById(R.id.img);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            content = (EmojiconTextView) convertView.findViewById(R.id.content);
            FixGridLayout contentImg = (FixGridLayout) convertView.findViewById(R.id.pic_layout);
            mCommentNums = (TextView) convertView.findViewById(R.id.comment_nums);
            mLikeNums = (TextView) convertView.findViewById(R.id.zan_nums);
            contentImg.setmCellHeight((int) (110 * mPXTimes));
            contentImg.setmCellWidth((int) (110 * mPXTimes));
            TextView publishTime = (TextView) convertView.findViewById(R.id.publish_time);
            name.setText(mTopicMessage.nickname);
            publishTime.setText(mTopicMessage.create_time);
            content.setText(FrameworkUtils.unicode2String(mTopicMessage.content));
            mCommentNums.setText(mTopicMessage.reply_num + "");
            mLikeNums.setText(mTopicMessage.like_num + "");
            convertView.findViewById(R.id.ding).setVisibility(View.VISIBLE);
            if (mTopicMessage.images == null || mTopicMessage.images.size() == 0) {
                contentImg.setVisibility(View.GONE);
            } else {
                contentImg.removeAllViews();
                for (int i = 0; i < mTopicMessage.images.size(); i++) {
                    ImageView img = new ImageView(ActivityExplorerHotAnser.this);
                    getSystem(SystemCommon.class).displayImageForAnchorCircle(ActivityExplorerHotAnser.this, img, mTopicMessage.images.get(i));
                    img.setPadding(5, 10, 5, 0);
                    final int postion1 = i;
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("URLs", mTopicMessage.images);
                            bundle.putInt("postion", postion1);
                            FrameworkUtils.skipActivity(ActivityExplorerHotAnser.this, ActivityExplorerImagePager.class,
                                    bundle);
                        }
                    });
                    contentImg.addView(img);
                }
            }
            if (actImg.getTag() == null || !actImg.getTag().toString().equals(mTopicMessage.image)) {
//				SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageSquare(mBBS.image, actImg);
                getSystem(SystemCommon.class).displayDefaultImage(ActivityExplorerHotAnser.this, actImg, mTopicMessage.image, SystemCommon.SQUARE);
            }
            mListView.addHeaderView(convertView);
        }
    }

    protected void showCopyPop(View parent) {
        // TODO Auto-generated method stub
        View layout = LayoutInflater.from(this).inflate(R.layout.pop_copy, null);
        if (optionPw == null) {
            optionPw = new PopupWindow(layout, 200, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
        }
        optionPw.setFocusable(true);
        optionPw.setOutsideTouchable(true);
        optionPw.setBackgroundDrawable(getResources().getDrawable(R.color.color_transparency));
        ((TextView) layout.findViewById(R.id.item_copy)).setText("举报");
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int Pwidth = layout.getMeasuredWidth();
        int Phight = layout.getMeasuredHeight();
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        optionPw.showAtLocation(parent, Gravity.NO_GRAVITY, (location[0] + parent.getWidth() / 2) - Pwidth / 2,
                location[1] - Phight + 30);
        layout.findViewById(R.id.item_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mPosJuBao == -1) {
                    getSystem(SystemUser.class).juBaoTopicOrReply(mTopicMessage.id, 1, new IUserBusynessCallBack() {
                        @Override
                        public void onResult(boolean result, String arg) {
                            showToast(arg);
                        }
                    });
                } else {
                    getSystem(SystemUser.class).juBaoTopicOrReply(mAdapter.getListData().get(mPosJuBao).id, 2, new IUserBusynessCallBack() {
                        @Override
                        public void onResult(boolean result, String arg) {
                            showToast(arg);
                        }
                    });
                }
                optionPw.dismiss();
            }
        });
    }


    protected void copy(String reply_content) {
        // TODO Auto-generated method stub
        Utils.copy(reply_content, this);
    }

    private void getData(final boolean fromRefresh, final boolean showDialog) {
        if (fromRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }
        JsonObject params = new JsonObject();
        params.addProperty("id", mTopicMessage.id);
        params.addProperty("page", mPageNum);
        params.addProperty("pagesize", mPageSize + "");
        if (showDialog) {
            Utils.showProgressDialog(this);
        }
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_messageReply, params);
        getSystem(SystemHttp.class).get(this, url, "getHotChatTopicList", false, true, new RequestCallBack<List<TopicReply>>() {
            @Override
            public void onSuccess(List<TopicReply> data, Response resp) {
                if (mAdapter == null)
                    mAdapter = new MyAdapter(null);
                mList = data;
                if (mList != null && mList.size() != 0) {
                    if (fromRefresh) {
                        mAdapter.setListData(mList);
                    } else {
                        mAdapter.addData(mList);
                    }
                }
            }

            @Override
            public void onFailure(Response resp) {
                if (!"没有更多内容".equals(resp.msg)) {
                    showToast(resp.msg);
                }
                mListView.noMoreData();
            }

            @Override
            public void onComplete() {
                mListView.stopLoadMore();
                mListView.stopRefresh();
                Utils.dismissProgressDialog();
            }
        });
    }

    private void initActionBar() {
        TextView title = (TextView) findViewById(R.id.ab_title);
        if (mTopicInfo != null && mTopicInfo.title != null) {
            title.setText(mTopicInfo.title);
        } else {
            title.setText("标题");
        }
        ImageView btnBack = (ImageView) findViewById(R.id.ab_left_img);
        mPXTimes = (btnBack.getLayoutParams().width / 50.0);
        btnBack.setImageResource(R.drawable.icon_arrow_left1);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("TopicMessage", mTopicMessage);
                intent.putExtra("position", mPos);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private int mPosition;

    class MyAdapter extends BaseListGridAdapter<TopicReply> {

        public MyAdapter(List<TopicReply> listData) {
            super(listData);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = View
                        .inflate(ActivityExplorerHotAnser.this, R.layout.item_activity_anchor_act_answer, null);
                holder = new Holder();
                holder.content = (EmojiconTextView) convertView.findViewById(R.id.answer_content);
                holder.publishTime = (TextView) convertView.findViewById(R.id.answer_publish_time);
                holder.userPic = (ImageView) convertView.findViewById(R.id.answer_user_pic);
                holder.name = (TextView) convertView.findViewById(R.id.answer_name);
                holder.ding = (ImageView) convertView.findViewById(R.id.ding_reply);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final TopicReply itemData = getItem(position);
            holder.content.setText(FrameworkUtils.unicode2String(itemData.content));
            holder.publishTime.setText(itemData.create_time);
            holder.name.setText(itemData.nickname);
            if ("1".equals(itemData.like_status)) {
                holder.ding.setImageResource(R.drawable.icon_ding1);
            } else {
                holder.ding.setImageResource(R.drawable.icon_ding0);
            }
            holder.ding.setTag("ding" + position);
            getSystem(SystemCommon.class).displayDefaultImage(ActivityExplorerHotAnser.this, holder.userPic, itemData.image, SystemCommon.SQUARE);
            holder.ding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPosition = position;
                    zan(itemData.id, 0, 1);
                }
            });
            return convertView;
        }

        class Holder {
            EmojiconTextView content;
            TextView publishTime;
            ImageView userPic;
            TextView name;
            ImageView ding;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SystemManager.getInstance().getSystem(SystemShare.class).onActivityResult(requestCode, resultCode, data);
        if (requestCode == 90 && resultCode == 110) {
            TopicReply topicReply = (TopicReply) data.getSerializableExtra("topicReply");
            if (mList == null) {
                mList = new ArrayList<TopicReply>();
            }
            mList.add(0, topicReply);
            mAdapter.setListData(mList);
            mCommentNums.setText(Utils.getNumberString(mTopicMessage.reply_num, 1));
            mTopicMessage.reply_num = Utils.getNumberString(mTopicMessage.reply_num, 1);
        } else {
            SystemManager.getInstance().getSystem(SystemShare.class).onActivityResult(requestCode, resultCode, data);
        }
    }
}

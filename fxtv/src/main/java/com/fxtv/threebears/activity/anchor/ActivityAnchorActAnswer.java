package com.fxtv.threebears.activity.anchor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.framework.widget.xlistview.XListView.IXListViewListener;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.explorer.ActivityExplorerAnser;
import com.fxtv.threebears.activity.explorer.ActivityExplorerImagePager;
import com.fxtv.threebears.model.BBS;
import com.fxtv.threebears.model.Reply;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.FixGridLayout;
import com.fxtv.threebears.view.ShareDialog;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import emojicon.EmojiconEditText;
import emojicon.EmojiconTextView;
import emojicon.EmojiconsFragment;

/**
 * 主播动态里的用户回复界面
 *
 * @author Administrator
 */
public class ActivityAnchorActAnswer extends BaseFragmentActivity {
    private XListView mListView;
    //private List<Reply> mList= new ArrayList<Reply>();
    private BBS mBBS;
    private int mPageNum, mCount;
    private MyAdapter mAdapter;
    private EmojiconEditText mUserReply;
    private TextView mContentNumber, mZanNum;
    private final int mPageSize = 20;
    private PopupWindow optionPw;
    private EmojiconsFragment emojiconsFragment;
    private double mPXTimes;
    private int mPosition, mPosJuBao, mPosParent;
    private ImageView mZanImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_act_answer);
        mBBS = (BBS) getSerializable("BBS");
        mPosParent = getIntent().getIntExtra("position", -1);
        initView();
    }

    private void initView() {
        initActionBar();
        initBottom();
        initListView();

//        mUserReply = (EmojiconEditText) findViewById(R.id.activity_anchor_act_answer_answer);
//        findViewById(R.id.activity_anchor_act_answer_comment).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendReplay();
//            }
//        });
//        final LinearLayout line_emoji = (LinearLayout) findViewById(R.id.line_emoji);
//        findViewById(R.id.im_icon).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                if (line_emoji.getVisibility() == View.GONE) {
//                    if (emojiconsFragment == null) {
//                        emojiconsFragment = new EmojiconsFragment();
//                        transaction.add(R.id.line_emoji, emojiconsFragment).show(emojiconsFragment).commit();
//                    }
//                    line_emoji.setVisibility(View.VISIBLE);
//                } else {
//                    line_emoji.setVisibility(View.GONE);
//                }
//            }
//        });
    }

    private void initBottom() {
        final ShareModel shareModel = new ShareModel();
        shareModel.shareTitle = mBBS.name;
        shareModel.shareUrl = "http://api.feixiong.tv/h5/fx_topic/recent.html?id=" + mBBS.id;
        shareModel.shareSummary = mBBS.content;
        shareModel.fileImageUrl = mBBS.image;
        mZanImg = (ImageView) findViewById(R.id.image_zan);
        ((TextView)findViewById(R.id.share_nums)).setText("分享");
        ((TextView)findViewById(R.id.post_nums)).setText("评论");
        ((TextView)findViewById(R.id.zan_nums)).setText("赞");
        if ("1".equals(mBBS.like_status)) {
            mZanImg.setImageResource(R.drawable.icon_ding1);
        } else {
            mZanImg.setImageResource(R.drawable.icon_ding0);
        }
        //转发
        findViewById(R.id.line_share).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getSystem(SystemCommon.class).showShareDialog(ActivityAnchorActAnswer.this, shareModel, new ShareDialog.ShareCallBack() {
                    @Override
                    public void onShareSuccess() {
                        getSystem(SystemUser.class).anchorActShare(mBBS.id, new IUserBusynessCallBack() {
                            @Override
                            public void onResult(boolean result, String arg) {
                                showToast(arg);
                                if (result) {
                                    mBBS.share_num = Utils.getNumberString(mBBS.share_num, 1);
                                }
                            }
                        });
                    }

                    @Override
                    public void onShareFailure(String msg) {
                        showToast(msg);
                    }

                    @Override
                    public void onCancel() {
                        showToast("取消分享");
                    }
                });
            }
        });
        //评论
        findViewById(R.id.line_post).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("bbs_id", mBBS.id);
//                FrameworkUtils.skipActivity(ActivityAnchorActAnswer.this, ActivityExplorerAnser.class, bundle);
                Intent intent = new Intent(ActivityAnchorActAnswer.this, ActivityExplorerAnser.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 90);
            }
        });
        //赞
        findViewById(R.id.line_zan).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                zan(mBBS.id, 1);
            }
        });
    }

    /**
     * 发送评论
     */
    protected void sendReplay() {
        String reply = mUserReply.getText().toString();
        if (getSystem(SystemUser.class).isLogin()) {
            if (TextUtils.isEmpty(reply)) {
                FrameworkUtils.showToast(ActivityAnchorActAnswer.this, "评论内容不能为空");
            } else {
                replyBBS(FrameworkUtils.string2Unicode(reply));
            }
        } else {
            getSystem(SystemCommon.class).noticeAndLogin(ActivityAnchorActAnswer.this);
        }
    }

    private void replyBBS(final String reply) {
        JsonObject params = new JsonObject();
        params.addProperty("id", mBBS.id);
        params.addProperty("content", reply);
        Utils.showProgressDialog(ActivityAnchorActAnswer.this);
        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_replyBbs, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(ActivityAnchorActAnswer.this, url, false, false, new RequestCallBack<Reply>() {

            @Override
            public void onSuccess(Reply data, Response resp) {
                if (data != null) {
                    List<Reply> list = mAdapter.getListData();
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(0, data);
                    mAdapter.setListData(list);
                    mCount++;
                    mContentNumber.setText("共" + (Integer.parseInt(mBBS.reply_num) + mCount) + "条回复");
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

    private void initListView() {
        mListView = (XListView) findViewById(R.id.activity_anchor_act_answer_ListView);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setEmptyViewEnable(false);
        mListView.setPageSize(mPageSize);
        getData(false, true);
        if (mAdapter == null) {
            mAdapter = new MyAdapter();
        }
        initHeader();
        mListView.setAdapter(mAdapter);
        mListView.setXListViewListener(new IXListViewListener() {
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
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (position == 1) {
                } else {
                    mPosJuBao = position - 2;
                    showCopyPop(view);
                }
                return false;
            }
        });
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
        layout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int Pwidth = layout.getMeasuredWidth();
        int Phight = layout.getMeasuredHeight();
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        optionPw.showAtLocation(parent, Gravity.NO_GRAVITY, (location[0] + parent.getWidth() / 2) - Pwidth / 2,
                location[1] - Phight + 30);
        layout.findViewById(R.id.item_copy).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getSystem(SystemUser.class).juBaoAnchorActOrReply(mAdapter.getListData().get(mPosJuBao).id, new IUserBusynessCallBack() {
                    @Override
                    public void onResult(boolean result, String arg) {
                        showToast(arg);
                    }
                });
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
        params.addProperty("id", mBBS.id);
        params.addProperty("page", mPageNum);
        params.addProperty("pagesize", mPageSize + "");
        if (showDialog) {
            Utils.showProgressDialog(this);
        }
        String url = Utils.processUrl(ModuleType.ANCHOR, ApiType.ANCHOR_bbsReplyList, params);
        getSystem(SystemHttp.class).get(this, url, "AnchorActAnswer", false, false, new RequestCallBack<List<Reply>>() {
            @Override
            public void onSuccess(List<Reply> data, Response resp) {
                if (mAdapter == null)
                    mAdapter = new MyAdapter();
                if (fromRefresh) {
                    mAdapter.setListData(data);
                } else {
                    mAdapter.addData(data);
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

    private void initHeader() {

        if (mBBS != null) {
            View convertView = View.inflate(this, R.layout.item_anchor_latest_act_header, null);
            ImageView actImg = (ImageView) convertView.findViewById(R.id.img);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView mContent = (TextView) convertView.findViewById(R.id.content);
            TextView createTime = (TextView) convertView.findViewById(R.id.create_time);
            FixGridLayout contentImg = (FixGridLayout) convertView.findViewById(R.id.pic_layout);
            contentImg.setmCellHeight((int) (110 * mPXTimes));
            contentImg.setmCellWidth((int) (110 * mPXTimes));
            TextView publishTime = (TextView) convertView.findViewById(R.id.publish_time);
            mContentNumber = (TextView) convertView.findViewById(R.id.comment_nums);
            mZanNum = (TextView) convertView.findViewById(R.id.zan_nums);
            name.setText(mBBS.name);
            publishTime.setText(mBBS.create_time);
            mContent.setText(mBBS.content);
            mContentNumber.setText(mBBS.reply_num);
            mZanNum.setText(mBBS.like_num);
            createTime.setText(mBBS.create_time);
            // actImg.setOnClickListener(new OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // Bundle bundle = new Bundle();
            // bundle.putString("anchor_id", man);
            // Utils.skipActivity(ActivityExplorerAnchorCircle.this,
            // ActivityAnchorSpace.class, bundle);
            // }
            // });
            if (mBBS.images == null || mBBS.images.size() == 0) {
                contentImg.setVisibility(View.GONE);
            } else {
                contentImg.removeAllViews();
                for (int i = 0; i < mBBS.images.size(); i++) {
                    ImageView img = new ImageView(ActivityAnchorActAnswer.this);
                    getSystem(SystemCommon.class).displayImageForAnchorCircle(ActivityAnchorActAnswer.this, img, mBBS.images.get(i));
                    img.setPadding(5, 10, 5, 0);
                    final int postion1 = i;
                    img.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("URLs", mBBS.images);
                            bundle.putInt("postion", postion1);
                            FrameworkUtils.skipActivity(ActivityAnchorActAnswer.this, ActivityExplorerImagePager.class,
                                    bundle);
                        }
                    });
                    contentImg.addView(img);
                }
            }
            if (actImg.getTag() == null || !actImg.getTag().toString().equals(mBBS.image)) {
//				SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageSquare(mBBS.image, actImg);
                getSystem(SystemCommon.class).displayDefaultImage(ActivityAnchorActAnswer.this, actImg, mBBS.image, SystemCommon.SQUARE);
            }
            mListView.addHeaderView(convertView);
        }
    }

    private void initActionBar() {
        TextView title = (TextView) findViewById(R.id.ab_title);
        title.setText("动态");
        ImageView btnBack = (ImageView) findViewById(R.id.ab_left_img);
        mPXTimes = (btnBack.getLayoutParams().width / 50.0);
        btnBack.setImageResource(R.drawable.icon_arrow_left1);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("BBS", mBBS);
                intent.putExtra("position", mPosParent);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    class MyAdapter extends BaseListGridAdapter<Reply> {

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = View
                        .inflate(ActivityAnchorActAnswer.this, R.layout.item_activity_anchor_act_answer, null);
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
            final Reply itemData = getItem(position);
            holder.content.setText(FrameworkUtils.unicode2String(itemData.content));
            holder.publishTime.setText(itemData.create_time);
            holder.name.setText(itemData.nickname);
            if ("1".equals(itemData.like_status)) {
                holder.ding.setImageResource(R.drawable.icon_ding1);
            } else {
                holder.ding.setImageResource(R.drawable.icon_ding0);
            }
            holder.ding.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPosition = position;
                    zan(itemData.id, 0);
                }
            });
            holder.ding.setTag("ding" + position);
            getSystem(SystemCommon.class).displayDefaultImage(ActivityAnchorActAnswer.this, holder.userPic, itemData.image, SystemCommon.SQUARE);
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
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("BBS", mBBS);
        intent.putExtra("position", mPosParent);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 90 && resultCode == 100) {
            Reply reply = (Reply) data.getSerializableExtra("reply");
            String message = data.getStringExtra("msg");
            showToast(message);
            List<Reply> list = mAdapter.getListData();
            if (list == null) {
                list = new ArrayList<Reply>();
            }
            list.add(0, reply);
            mAdapter.setListData(list);
            mBBS.reply_num = Utils.getNumberString(mBBS.reply_num, 1);
            mContentNumber.setText(mBBS.reply_num);
        }
        SystemManager.getInstance().getSystem(SystemShare.class).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 主播圈动态或回复点赞
     *
     * @param id
     * @param type--(1.动态,0--回复)
     */
    public void zan(String id, final int type) {
        getSystem(SystemUser.class).anchorActZan(id, type, new IUserBusynessCallBack() {
            @Override
            public void onResult(boolean result, String arg) {
                showToast(arg);
                if (result) {
                    if (type == 1) {
                        mZanImg.setImageResource(R.drawable.icon_ding1);
                        mBBS.like_status = "1";
                        mBBS.like_num = Utils.getNumberString(mBBS.like_num, 1);
                        mZanNum.setText(mBBS.like_num);
                    } else if (type == 0) {
                        ImageView ding = (ImageView) mListView.findViewWithTag("ding" + mPosition);
                        ding.setImageResource(R.drawable.icon_ding1);
                        mAdapter.getListData().get(mPosition).like_status = "1";
                    }
                }
            }
        });
    }
}

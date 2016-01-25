package com.fxtv.threebears.activity.explorer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragmentActivity;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.Reply;
import com.fxtv.threebears.model.TopicReply;
import com.fxtv.threebears.system.IUserBusynessCallBackWithMes;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.MyDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import emojicon.EmojiconEditText;
import emojicon.EmojiconGridFragment;
import emojicon.EmojiconsFragment;
import emojicon.emoji.Emojicon;

/**
 * 新版主播圈回复界面
 */
public class ActivityExplorerAnser extends BaseFragmentActivity implements EmojiconGridFragment.OnEmojiconBackspaceClickedListener, EmojiconGridFragment.OnEmojiconClickedListener {
    private EmojiconEditText mEditText;
    private String mBBSId;
    private String mTopicId;
    private LinearLayout line_emoji;
    private EmojiconsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_anser);
        mBBSId = getStringExtra("bbs_id");
        mTopicId = getStringExtra("topic_id");
        initView();
    }

    private void initView() {
        initAcionbar();
        mEditText = (EmojiconEditText) findViewById(R.id.act_info);
        line_emoji = (LinearLayout) findViewById(R.id.line_emoji);
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                line_emoji.setVisibility(View.VISIBLE);
                initEmojiFragment();
                return false;
            }
        });
        findViewById(R.id.im_emoji).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initEmojiFragment();
            }
        });

    }

    private void initEmojiFragment() {
        if (fragment == null) {
            fragment = new EmojiconsFragment();
            getSupportFragmentManager().beginTransaction().add(line_emoji.getId(), fragment).show(fragment).commit();
        }
        if (line_emoji.getVisibility() == View.VISIBLE) {
            line_emoji.setVisibility(View.GONE);
            Utils.setInputVisible(this, mEditText);
        } else {
            Utils.setInputGone(this);
            line_emoji.setVisibility(View.VISIBLE);
        }


    }

    private void initAcionbar() {
        TextView title = (TextView) findViewById(R.id.ab_title);
        title.setText("发评论");
        findViewById(R.id.ab_left_img).setVisibility(View.GONE);

        TextView send = (TextView) findViewById(R.id.ab_right_tv);
        send.setVisibility(View.VISIBLE);
        send.setText("发表");
        //提交
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = mEditText.getText().toString();
                if (check(value)) {
                    if (mBBSId == null || "".equals(mBBSId)) {
                        //吐槽的回复
                        replyTopic(FrameworkUtils.string2Unicode(value));
                    } else {
                        //主播动态的回复
                        replyBBS(FrameworkUtils.string2Unicode(value));
                    }
                }
            }
        });

        TextView ab_left_tv = (TextView) findViewById(R.id.ab_left_tv);
        ab_left_tv.setVisibility(View.VISIBLE);
        ab_left_tv.setText("取消");
        ab_left_tv.getLayoutParams().width=FrameworkUtils.dip2px(this,50);
        ab_left_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void replyTopic(String content) {
        getSystem(SystemUser.class).topicMessageReply(mTopicId, content, new IUserBusynessCallBackWithMes() {
            @Override
            public void onResult(boolean result, String json, String message) {
                if (result) {
                    showToast(message);
                    Gson gson = new Gson();
                    TopicReply topicReply = gson.fromJson(json, TopicReply.class);
                    Intent intent = new Intent();
                    intent.putExtra("topicReply", topicReply);
                    setResult(110, intent);
                    finish();
                } else {
                    showToast(message);
                }
            }
        });
    }

    private boolean check(String value) {
        if (TextUtils.isEmpty(value)) {
            showToast("内容不能为空");
            return false;
        }
        if (!getSystem(SystemUser.class).isLogin()) {
            showToast("请先登录");
            return false;
        }
        return true;
    }

    //发表评论
    public void replyBBS(final String reply) {
        JsonObject params = new JsonObject();
        params.addProperty("id", mBBSId);
        params.addProperty("content", reply);
        Utils.showProgressDialog(ActivityExplorerAnser.this);
        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_replyBbs, params);
        getSystem(SystemHttp.class).get(this, url, "sendAnchorComment", false, false, new RequestCallBack<Reply>() {
            @Override
            public void onSuccess(Reply data, Response resp) {
                Intent intent = new Intent();
                intent.putExtra("reply", data);
                try {
                    intent.putExtra("msg", resp.msg);
                } catch (Exception e) {
                    intent.putExtra("msg", "回复成功");
                }
                setResult(100, intent);
                finish();
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

    @Override
    public void onEmojiconBackspaceClicked(View arg0) {
        // TODO Auto-generated method stub
        EmojiconsFragment.backspace(mEditText);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        // TODO Auto-generated method stub
        EmojiconsFragment.input(mEditText, emojicon);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(!TextUtils.isEmpty(mEditText.getText().toString())){
            getSystem(SystemCommon.class)
                    .showDialog(this, "退出此次编辑？", null,new MyDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog, View view, String value) {
                            dialog.dismiss();
                            finish();
                        }
                    }, new MyDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog, View view, String value) {
                            dialog.dismiss();
                        }
                    });
        }else{
            super.onBackPressed();
        }
    }
}

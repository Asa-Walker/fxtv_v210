package com.fxtv.threebears.fragment.module.player;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemFragmentManager;
import com.fxtv.framework.widget.MyListView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.activity.user.login.ActivityLogin;
import com.fxtv.threebears.model.Comment;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

;

public class FragmentPlayerComment extends BaseFragment {
    private Video mVideo;
    private ImageView mUserAdater;
    private int mFatherRes;
    private MyListView mFirstListView;
    private MyCommentAdapter mCommentAdapter;
    private int mParentPos;
    private Dialog mEditDialog;
    private int mFirPos, mSecPos;
    private boolean isFirLevel = false, isSend = true;
    private String mCommentId;
    private PopupWindow mOptionPw, mChildPw;
    private String TAG = "FragmentPlayerComment";
    private String mCopyContent;
    private InputMethodManager mInputManager;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 10) {
                mEditText.setFocusable(true);
                mEditText.setFocusableInTouchMode(true);
                mEditText.requestFocus();
                mEditText.requestFocusFromTouch();
                mInputManager.showSoftInput(mEditText, 0);
            }
        }

        ;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_player_comment, container, false);
        mInputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mVideo = (Video) arguments.getSerializable("video");
            mFatherRes = arguments.getInt("layout");
        }
        initView();
        return mRoot;
    }

    public void notifyData() {
        if (mCommentAdapter != null) {
            mCommentAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        initComment();
        initCommentListView();
    }

    /**
     * 初始化评论列表
     */
    private void initCommentListView() {
        mFirstListView = (MyListView) mRoot.findViewById(R.id.activity_new_video_comment_list);
        mCommentAdapter = new MyCommentAdapter();
        mFirstListView.setAdapter(mCommentAdapter);
        mFirstListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFirPos = position;
                mCommentId = mVideo.comment_list.get(position).id;
                mCopyContent = mVideo.comment_list.get(position).content;
                isFirLevel = true;
                isSend = false;
                try {
                    showOptionPop(view);
                } catch (Exception e) {
                }
            }
        });
    }

    // 初始化评论相关控件
    private void initComment() {
        if (mUserAdater == null) {
            mUserAdater = (ImageView) mRoot.findViewById(R.id.video_play_image);
        }
        TextView commentNum = (TextView) mRoot.findViewById(R.id.video_play_comment_num);
        TextView comment = (TextView) mRoot.findViewById(R.id.video_play_comment_text);
        commentNum.setText("共" + mVideo.comment_num + "条评论");
        // 共多少条评论的点击事件
        commentNum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("video", mVideo);
                SystemManager.getInstance().getSystem(SystemFragmentManager.class)
                        .addAnimFragment(R.id.video_container_action, FragmentPlayerFlashComment.class.getCanonicalName(), bundle, getActivity());
                ((ActivityVideoPlay) getActivity()).setFragmentPos(1);
            }
        });
        if (getSystem(SystemUser.class).isLogin()) {
            String avatarUrl = getSystem(SystemUser.class).mUser.image;
//			SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageSquare(avatarUrl, mUserAdater);
            getSystem(SystemCommon.class).displayDefaultImage(FragmentPlayerComment.this, mUserAdater, avatarUrl, SystemCommon.SQUARE);
        } else {
            getSystem(SystemCommon.class).displayDefaultImage(FragmentPlayerComment.this, mUserAdater, "", SystemCommon.SQUARE);
        }
        comment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSystem(SystemUser.class).isLogin()) {
                    isSend = true;
                    showDialog();
                } else {
                    FrameworkUtils.skipActivity(getActivity(), ActivityLogin.class);
                }
            }
        });
    }

    /**
     * 一级评论适配器
     *
     * @author Android2
     */
    class MyCommentAdapter extends BaseAdapter {
        private int temp;

        @Override
        public int getCount() {
            return mVideo.comment_list == null ? 0 : mVideo.comment_list.size();
        }

        public void changeData() {
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int positionA, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_video_play_comment_listview, null);
                holder = new Holder();
                holder.userAdavater = (ImageView) convertView.findViewById(R.id.user_pic);
                holder.clickZan = (ImageView) convertView.findViewById(R.id.dian_zan);
                holder.userName = (TextView) convertView.findViewById(R.id.user_name);
                holder.userComment = (TextView) convertView.findViewById(R.id.comment);
                holder.publishTime = (TextView) convertView.findViewById(R.id.publish_time);
                holder.response = (TextView) convertView.findViewById(R.id.comment_reancer);
                holder.reAnSerLv = (ListView) convertView.findViewById(R.id.comment_reancer_reancer);
                holder.reAnserAdapter = new ReAnserAdapter();
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final Comment anser = mVideo.comment_list.get(positionA);
            holder.clickZan.setTag("clickZan" + positionA);
            holder.userName.setText(anser.nickname);
            holder.userComment.setText(FrameworkUtils.unicode2String(anser.content));
            holder.publishTime.setText(anser.create_time);
//            SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageSquare(anser.image, holder.userAdavater);
            getSystem(SystemCommon.class).displayDefaultImage(FragmentPlayerComment.this, holder.userAdavater, anser.image);
            holder.reAnserAdapter.setList(anser.reply_data);
            holder.reAnSerLv.setTag("reAnSerLv" + positionA);
            holder.reAnSerLv.setAdapter(holder.reAnserAdapter);
            if (anser.top_status == 0) {
                holder.clickZan.setImageResource(R.drawable.icon_ding0);
            } else {
                holder.clickZan.setImageResource(R.drawable.icon_ding1);
            }
            holder.reAnSerLv.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mFirPos = positionA;
                    mSecPos = position;
                    mCommentId = mVideo.comment_list.get(positionA).reply_data.get(position).id;
                    mCopyContent = mVideo.comment_list.get(positionA).reply_data.get(position).content;
                    isFirLevel = false;
                    isSend = false;
                    // showOptionPop(view);
                    if (!mSecondLevel) {
                        showChildPop(view);
                    } else {
                        topVideoComment();
                    }
                    mSecondLevel = false;
                }
            });
            // 点赞
            holder.clickZan.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (anser.top_status == 0) {
                        temp = 1;
                    } else {
//                        temp = 1;
                        showToast("已赞过！");
                        return;
                    }
                    getSystem(SystemUser.class)
                            .cryUpOrUnCryUpForVideoComment(anser.id, temp + "", new IUserBusynessCallBack() {
                                @Override
                                public void onResult(boolean result, String arg) {
                                    showToast(arg);
                                    if (result) {
                                        ImageView tempView = (ImageView) mFirstListView.findViewWithTag("clickZan"
                                                + positionA);
                                        if (temp == 1) {
                                            tempView.setImageResource(R.drawable.icon_ding1);
                                            anser.top_status = 1;
                                        }
//                                        else {
//                                            tempView.setImageResource(R.drawable.icon_ding0);
//                                            anser.top_status = 0;
//                                        }
                                    }
                                }
                            });
                }
            });
            return convertView;
        }

        class Holder {
            ImageView userAdavater;
            ImageView clickZan;
            TextView userName;
            TextView userComment;
            TextView publishTime;
            TextView response;
            ListView reAnSerLv;
            ReAnserAdapter reAnserAdapter;
        }
    }

    /**
     * 弹出popWindow
     */
    private void showOptionPop(View parent) {
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.pop_option, null);
        if (mOptionPw == null) {
            mOptionPw = new PopupWindow(layout, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
        }
        mOptionPw.setFocusable(true);
        mOptionPw.setOutsideTouchable(true);
        mOptionPw.setBackgroundDrawable(getActivity().getResources().getDrawable(R.color.color_transparency));
        layout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int Pwidth = layout.getMeasuredWidth();
        int Phight = layout.getMeasuredHeight();
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        mOptionPw.showAtLocation(parent, Gravity.NO_GRAVITY, (location[0] + parent.getWidth() / 2) - Pwidth / 2,
                location[1] - Phight + 30);
        layout.findViewById(R.id.option_copy).setVisibility(View.GONE);
        // 评论
        layout.findViewById(R.id.option_report).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                replyVideoComment();
                mOptionPw.dismiss();
            }
        });
        // 举报
        layout.findViewById(R.id.option_zan).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                juBao();
                mOptionPw.dismiss();
            }
        });
        // 点赞
//        layout.findViewById(R.id.option_copy).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                topVideoComment();
//                mOptionPw.dismiss();
//            }
//        });
        // 复制
        layout.findViewById(R.id.copy).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.copy(mCopyContent, getActivity());
                mOptionPw.dismiss();
            }
        });
    }

    public void showChildPop(View parent) {
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.pop_child_option, null);
        if (mChildPw == null) {
            mChildPw = new PopupWindow(layout, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
        }
        mChildPw.setFocusable(true);
        mChildPw.setOutsideTouchable(true);
        mChildPw.setBackgroundDrawable(getActivity().getResources().getDrawable(R.color.color_transparency));
        layout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int Pwidth = layout.getMeasuredWidth();
        int Phight = layout.getMeasuredHeight();
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        mChildPw.showAtLocation(parent, Gravity.NO_GRAVITY, (location[0] + parent.getWidth() / 2) - Pwidth / 2,
                location[1] - Phight + 30);
        // 举报
        layout.findViewById(R.id.option_zan).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                juBao();
                mChildPw.dismiss();
            }
        });
        // 点赞
//        layout.findViewById(R.id.option_copy).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                topVideoComment();
//                mChildPw.dismiss();
//            }
//        });
        // 复制
        layout.findViewById(R.id.copy).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.copy(mCopyContent, getActivity());
                mChildPw.dismiss();
            }
        });
    }

    private int temp;
    private Comment anser;

    /**
     * 点赞该视频评论
     */
    private void topVideoComment() {
        if (isFirLevel) {
            anser = mVideo.comment_list.get(mFirPos);
        } else {
            anser = mVideo.comment_list.get(mFirPos).reply_data.get(mSecPos);
        }

        getSystem(SystemUser.class)
                .cryUpOrUnCryUpForVideoComment(mCommentId, "1", new IUserBusynessCallBack() {

                    @Override
                    public void onResult(boolean result, String arg) {
                        showToast(arg);
                        if (result) {
                            ImageView tempView;
                            if (isFirLevel) {
                                tempView = (ImageView) mFirstListView.findViewWithTag("clickZan" + mFirPos);
                            } else {
                                tempView = (ImageView) (mFirstListView
                                        .findViewWithTag("reAnSerLv" + mFirPos)).findViewWithTag("secClickZan"
                                        + mSecPos);
                            }
//                            if (temp == 1) {
                            tempView.setImageResource(R.drawable.icon_ding1);
                            anser.top_status = 1;
//                            } else {
//                                tempView.setImageResource(R.drawable.icon_ding0);
//                                anser.top_status = 0;
//                            }

                        }
                    }
                });
    }

    /**
     * 举报视频的评论
     */
    private void juBao() {
        getSystem(SystemUser.class)
                .reportVideoComment(mCommentId, new IUserBusynessCallBack() {
                    @Override
                    public void onResult(boolean result, String arg) {
                        showToast(arg);
                    }
                });

    }

    private boolean mSecondLevel = false;

    /**
     * 回复评论
     */
    private void replyVideoComment() {
        showDialog();
    }

    /**
     * 二级评论适配器
     *
     * @author Android2
     */
    class ReAnserAdapter extends BaseAdapter {
        int temp;
        private List<Comment> mList;

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            // return mList == null ? 0 : mList.size() <= 10 ? mList.size() :
            // 10;
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        public void setList(List<Comment> list) {
            mList = list;
            this.notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_video_play_comment_listview, null);
                holder = new ViewHolder();
                holder.userAdavater = (ImageView) convertView.findViewById(R.id.user_pic);
                holder.clickZan = (ImageView) convertView.findViewById(R.id.dian_zan);
                holder.userName = (TextView) convertView.findViewById(R.id.user_name);
                holder.userComment = (TextView) convertView.findViewById(R.id.comment);
                holder.publishTime = (TextView) convertView.findViewById(R.id.publish_time);
                holder.response = (TextView) convertView.findViewById(R.id.comment_reancer);
                // holder.reAnSerLv = (ListView)
                // convertView.findViewById(R.id.comment_reancer_reancer);
                // holder.secondReAnserAdapter = new SecondReAnserAdapter();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Comment anser = mList.get(position);
            holder.clickZan.setTag("secClickZan" + position);
            holder.userName.setText(anser.nickname);
            holder.userComment.setText(FrameworkUtils.unicode2String(anser.content));
            holder.publishTime.setText(anser.create_time);
            if (anser.top_status == 0) {
                holder.clickZan.setImageResource(R.drawable.icon_ding0);
            } else {
                holder.clickZan.setImageResource(R.drawable.icon_ding1);
            }
//            SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageSquare(anser.image, holder.userAdavater);
            holder.clickZan.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mSecondLevel = true;
                    return false;
                }
            });
            getSystem(SystemCommon.class).displayDefaultImage(FragmentPlayerComment.this, holder.userAdavater, anser.image);
            // holder.secondReAnserAdapter.setList(anser.reply_data);
            // holder.reAnSerLv.setAdapter(holder.secondReAnserAdapter);
            return convertView;
        }

        class ViewHolder {
            ImageView userAdavater;
            ImageView clickZan;
            TextView userName;
            TextView userComment;
            TextView publishTime;
            TextView response;
            // ListView reAnSerLv;
            // SecondReAnserAdapter secondReAnserAdapter;
        }
    }

    /**
     * 三级评论适配器
     *
     * @author Android2
     */
    class SecondReAnserAdapter extends BaseAdapter {
        private List<Comment> mList;

        public void setList(List<Comment> list) {
            mList = list;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            // return mList == null ? 0 : mList.size() <= 10 ? mList.size() :
            // 10;
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                // convertView = View.inflate(ActivityNewVideoPlay.this,
                // R.layout.item_anser_reanser, null);
                convertView = mLayoutInflater.inflate(R.layout.item_anser_reanser, null);
                holder = new Holder();
                holder.userName = (TextView) convertView.findViewById(R.id.user_name);
                holder.userReAnser = (TextView) convertView.findViewById(R.id.user_reanser);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.userName.setText(mList.get(position).nickname + ":");
            holder.userReAnser.setText(FrameworkUtils.unicode2String(mList.get(position).content));
            return convertView;
        }

        class Holder {
            TextView userName;
            TextView userReAnser;
        }
    }

    private void showDialog() {
        if (mEditDialog != null) {
            mEditDialog.show();
            mHandler.sendEmptyMessageDelayed(10, 100);
        } else {
            creatDialog();
        }
    }

    private EditText mEditText;

    private void creatDialog() {
        mEditDialog = new Dialog(getActivity(), R.style.my_pop_dialog);
        mEditDialog.setContentView(R.layout.pop_comment);
        mEditDialog.setCanceledOnTouchOutside(true);
        mEditDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mInputManager != null) {
                    mInputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
    }

    );
    Window mWindow = mEditDialog.getWindow();
    WindowManager.LayoutParams lp = mWindow.getAttributes();
    lp.width=android.view.ViewGroup.LayoutParams.MATCH_PARENT;
    lp.height=200;
    mWindow.setGravity(Gravity.BOTTOM);
    mWindow.setAttributes(lp);
    mEditText=(EditText)mEditDialog.findViewById(R.id.fragment_play_page_comment_et_msg);
    Button send = (Button) mEditDialog.findViewById(R.id.comment_btn_send);
    send.setOnClickListener(new

    OnClickListener() {
        @Override
        public void onClick (View v){
            if (!getSystem(SystemUser.class).isLogin()) {
                FrameworkUtils.showToast(getActivity(), getString(R.string.notice_no_login));
                FrameworkUtils.skipActivity(getActivity(), ActivityLogin.class);
                mEditDialog.dismiss();
            } else {
                String text = mEditText.getText().toString();
                if (text.trim().equals("")) {
                    FrameworkUtils.showToast(getActivity(), getString(R.string.notice_comment_can_not_be_empty));
                    return;
                }
                if (text.contains("\\")) {
                    FrameworkUtils.showToast(getActivity(), "输入中包含非法字符，请重新输入");
                    mEditText.setText("");
                    return;
                }
                if (isSend) {
                    sendComment(FrameworkUtils.string2Unicode(text));
                } else {
                    reposeComment(FrameworkUtils.string2Unicode(text));
                }

            }
            mEditText.setText("");
            mEditDialog.dismiss();
        }
    }

    );
    mEditDialog.show();
    mHandler.sendEmptyMessageDelayed(10,100);
}

    /**
     * 回复一级评论
     *
     * @param reponse
     */
    private void reposeComment(String reponse) {
        getSystem(SystemUser.class)
                .replyVideoComment(mCommentId, reponse, new IUserBusynessCallBack() {

                    @Override
                    public void onResult(boolean result, String arg) {
                        if (result) {
                            showToast("回复成功");
                            Gson gson = new Gson();
                            Comment anser = gson.fromJson(arg, Comment.class);
                            if (isFirLevel) {
                                if (anser != null) {
                                    if (mVideo.comment_list.get(mFirPos).reply_data == null) {
                                        mVideo.comment_list.get(mFirPos).reply_data = new ArrayList<Comment>();
                                    }
                                    mVideo.comment_list.get(mFirPos).reply_data.add(0, anser);
                                    mCommentAdapter.notifyDataSetChanged();
                                }
                            }
                            // else {
                            // if (mVideo.comment_list.get(mFirPos).reply_data
                            // == null) {
                            // mVideo.comment_list.get(mFirPos).reply_data = new
                            // ArrayList<Comment>();
                            // }
                            // if
                            // (mVideo.comment_list.get(mFirPos).reply_data.get(mSecPos).reply_data
                            // == null) {
                            // mVideo.comment_list.get(mFirPos).reply_data.get(mSecPos).reply_data
                            // = new ArrayList<Comment>();
                            // }
                            // mVideo.comment_list.get(mFirPos).reply_data.get(mSecPos).reply_data.add(0,
                            // anser);
                            // mCommentAdapter.notifyDataSetChanged();
                            // }
                        } else {
                            showToast("回复失败");
                        }
                    }
                });
    }

    /**
     * 回复二级评论
     *
     * @param commentId
     * @param Fripostion
     * @param reponse
     */
    private void reposeSecComment(String commentId, final String Fripostion, String reponse) {
        getSystem(SystemUser.class)
                .replyVideoComment(commentId, reponse, new IUserBusynessCallBack() {

                    @Override
                    public void onResult(boolean result, String arg) {
                        if (result) {
                            showToast("回复成功");
                            Gson gson = new Gson();
                            Comment thridAnser = gson.fromJson(arg, Comment.class);
                            int sePos = Integer.parseInt(Fripostion);
                            List<Comment> replyData = mVideo.comment_list.get(mParentPos).reply_data.get(sePos).reply_data;
                            if (replyData == null) {
                                replyData = new ArrayList<Comment>();
                            }
                            if (replyData.size() < 10) {
                                replyData.add(0, thridAnser);
                                mCommentAdapter.notifyDataSetChanged();
                            }
                        } else {
                            showToast(arg);
                        }
                    }
                });
    }

    /**
     * 发表一级评论
     *
     * @param comment
     */
    private void sendComment(String comment) {
        getSystem(SystemUser.class)
                .sendCommentForVideo(mVideo.id, comment, new IUserBusynessCallBack() {

                    @Override
                    public void onResult(boolean result, String arg) {
                        if (result) {
                            showToast("发表成功");
                            Gson gson = new Gson();
                            Comment anser = gson.fromJson(arg, Comment.class);
                            if (mVideo.comment_list == null) {
                                mVideo.comment_list = new ArrayList<Comment>();
                                mVideo.comment_list.add(anser);
                            } else if (mVideo.comment_list.size() < 10) {
                                mVideo.comment_list.add(0, anser);
                            }
                            mCommentAdapter.notifyDataSetChanged();
                        } else {
                            showToast(arg);
                        }
                    }
                });
    }

}

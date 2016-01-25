package com.fxtv.threebears.fragment.module.player;

import android.app.Dialog;
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
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemFragmentManager;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.framework.widget.xlistview.XListView.IXListViewListener;
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
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

;

/**
 * 新版评论fragment
 *
 * @author Android2
 */
public class FragmentPlayerFlashComment extends BaseFragment {
    private Video mVideo;
    private MyCommentAdapter mAdapter;
    private int mPageNum;
    private List<Comment> mList;
    private XListView mListView;
    private Dialog editDialog;
    private PopupWindow mOptionPw, mChildPw;
    private int mfriPos, mSecPos;
    private boolean isFristLevel = false, isSend = true;
    private String mCommentId;
    private String TAG = "FragmentPlayerFlashComment";
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
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_player_flash_comment, container, false);
        mInputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        mVideo = (Video) getArguments().getSerializable("video");
        initView();
        getData(true, true);
        return mRoot;
    }

    private void initView() {
        // 隐藏自身
        mRoot.findViewById(R.id.cancel_fragment).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).getTransaction(getActivity())
                        .hide(FragmentPlayerFlashComment.this).commit();
                ((ActivityVideoPlay) getActivity()).setFragmentPos(0);
            }
        });
        // 阻止点击事件向下传递
        mRoot.findViewById(R.id.parent).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });
        initComment();
        initCommentListView();
    }

    private void initComment() {
        // 发表一级评论
        mRoot.findViewById(R.id.video_play_comment_text).setOnClickListener(new OnClickListener() {

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

    private void showDialog(String... params) {
        if (editDialog != null) {
            editDialog.show();
            mHandler.sendEmptyMessageDelayed(10, 100);
        } else {
            creatDialog(params);
        }
    }

    private EditText mEditText;

    private void creatDialog(final String... params) {
        editDialog = new Dialog(getActivity(), R.style.my_pop_dialog);
        editDialog.setContentView(R.layout.pop_comment);
        editDialog.setCanceledOnTouchOutside(false);
        Window mWindow = editDialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
//        editDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                if (mInputManager != null) {
//                    mInputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
////                    mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
//                }
//            }
//        });
        lp.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = 200;
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setAttributes(lp);
        mEditText = (EditText) editDialog.findViewById(R.id.fragment_play_page_comment_et_msg);
        Button send = (Button) editDialog.findViewById(R.id.comment_btn_send);
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getSystem(SystemUser.class).isLogin()) {
                    FrameworkUtils.showToast(getActivity(), getString(R.string.notice_no_login));
                    FrameworkUtils.skipActivity(getActivity(), ActivityLogin.class);
                    editDialog.dismiss();
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
                        try {
                            reponseComment(params, FrameworkUtils.string2Unicode(text));
                        } catch (Exception e) {
                        }
                    }
                }
                mEditText.setText("");
                editDialog.dismiss();
            }
        });
        editDialog.show();
        mHandler.sendEmptyMessageDelayed(10, 100);
    }

    /**
     * 评论的回复
     *
     * @param content
     */
    private void reponseComment(final String[] params, String content) {
        getSystem(SystemUser.class)
                .replyVideoComment(mCommentId, content, new IUserBusynessCallBack() {
                    @Override
                    public void onResult(boolean result, String arg) {
                        if (result) {
                            showToast("回复成功");
                            Gson gson = new Gson();
                            Comment anser = gson.fromJson(arg, Comment.class);
                            if (isFristLevel) {
                                if (anser != null) {
                                    if (mList.get(mfriPos).reply_data == null) {
                                        mList.get(mfriPos).reply_data = new ArrayList<Comment>();
                                    }
                                    mList.get(mfriPos).reply_data.add(0, anser);
                                    mAdapter.notifyDataSetChanged();
                                }
                            } else {
                                if (anser != null) {
                                    if (mList.get(mfriPos).reply_data == null) {
                                        mList.get(mfriPos).reply_data = new ArrayList<Comment>();
                                    }
                                    if (mList.get(mfriPos).reply_data.get(mSecPos).reply_data == null) {
                                        mList.get(mfriPos).reply_data.get(mSecPos).reply_data = new ArrayList<Comment>();
                                    }
                                    mList.get(mfriPos).reply_data.get(mSecPos).reply_data.add(0, anser);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            showToast(arg);
                        }
                    }
                });
    }

    /**
     * 发表评论
     *
     * @param msg
     */
    private void sendComment(String msg) {
        JsonObject params = new JsonObject();
        params.addProperty("id", mVideo.id);
        params.addProperty("content", msg);
        Utils.showProgressDialog(getActivity());

        getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.USER, ApiType.USER_comment, params), "mainSendCommentApi", false, false, new RequestCallBack<Comment>() {
            @Override
            public void onSuccess(Comment comment, Response resp) {
                showToast(resp.msg);
                int i = Integer.parseInt(mVideo.comment_num);
                i = i + 1;
                mVideo.comment_num = i + "";
                Gson gson = new Gson();
                if (mList == null) {
                    mList = new ArrayList<Comment>();
                }
                mList.add(0, comment);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Response resp) {
                FrameworkUtils.showToast(getActivity(), resp.msg);
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });

    }

    private void getData(final boolean isRefresh, final boolean showDialog) {
        if (isRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }
        JsonObject params = new JsonObject();
        params.addProperty("id", mVideo.id);
        params.addProperty("page", mPageNum + "");
        params.addProperty("pagesize", "20");
        if (mList == null) {
            mList = new ArrayList<Comment>();
        }
        if (showDialog) {
            Utils.showProgressDialog(getActivity());
        }
        getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.BASE, ApiType.BASE_VideoCommentList, params), "mainCommentsOfVideo", false, false, new RequestCallBack<List<Comment>>() {

            @Override
            public void onSuccess(List<Comment> data, Response resp) {
                if (data != null && data.size() != 0) {
                    if (isRefresh) {
                        mList.clear();
                        mList = data;
                    } else {
                        mList.addAll(data);
                    }
                    if (mAdapter == null) {
                        mAdapter = new MyCommentAdapter();
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    FrameworkUtils.showToast(getActivity(), getString(R.string.notice_no_more_data));
                }
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                if (mListView != null) {
                    mListView.stopLoadMore();
                    mListView.stopRefresh();
                }
                Utils.dismissProgressDialog();
            }
        });

    }

    private void initCommentListView() {
        mListView = (XListView) mRoot.findViewById(R.id.fragment_comment_listview);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setEmptyText("暂无评论");
        mAdapter = new MyCommentAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
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
                isFristLevel = true;
                isSend = false;
                mfriPos = position - 1;
                String mId = mList.get(position - 1).id;
                mCommentId = mId;
                mCopyContent = mList.get(position - 1).content;
                String[] params = {mId, "" + (position - 1)};
                try {
                    showOptionPop(view, params);
                } catch (Exception e) {
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
        @Override
        public int getCount() {
            // if (mVideo == null) {
            // return 0;
            // } else if (mVideo.comment_list == null) {
            // return 0;
            // }
            // return mVideo.comment_list.size();
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
        public View getView(final int positionA, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_fragment_comment, null);
                holder = new Holder();
                holder.userAdavater = (ImageView) convertView.findViewById(R.id.user_pic);
                holder.clickZan = (ImageView) convertView.findViewById(R.id.dian_zan);
                holder.userName = (TextView) convertView.findViewById(R.id.user_name);
                holder.userComment = (TextView) convertView.findViewById(R.id.comment);
                holder.publishTime = (TextView) convertView.findViewById(R.id.publish_time);
                holder.reAnSerLv = (ListView) convertView.findViewById(R.id.comment_reancer_reancer);
                holder.reAnserAdapter = new ReAnserAdapter();
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.reAnSerLv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mCommentId = mList.get(positionA).reply_data.get(position).id;
                    mCopyContent = mList.get(positionA).reply_data.get(position).content;
                    mfriPos = positionA;
                    mSecPos = position;
                    isFristLevel = false;
                    isSend = false;
                    String[] params = {mCommentId, "" + positionA, "" + position};
                    // showOptionPop(view, params);
                    if (!mSecondLevel) {
                        showChildPop(view, params);
                    } else {
                        topVideoComment(mCommentId);
                    }
                    mSecondLevel = false;
                }
            });
            Comment anser = mList.get(positionA);
            holder.userName.setText(anser.nickname);
            holder.userComment.setText(FrameworkUtils.unicode2String(anser.content));
            holder.publishTime.setText(anser.create_time);
            if (anser.top_status == 1) {
                holder.clickZan.setImageResource(R.drawable.icon_ding1);
            } else {
                holder.clickZan.setImageResource(R.drawable.icon_ding0);
            }
            holder.clickZan.setTag("firClickZan" + positionA);
//            holder.clickZan.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
            holder.clickZan.setOnClickListener(new MyOnClicklistenner(anser, positionA));
//            SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageSquare(anser.image, holder.userAdavater);
            getSystem(SystemCommon.class).displayDefaultImage(FragmentPlayerFlashComment.this, holder.userAdavater, anser.image);
            holder.reAnserAdapter.setList(anser.reply_data);
            holder.reAnSerLv.setAdapter(holder.reAnserAdapter);
            holder.reAnSerLv.setTag("firLV" + positionA);
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

    class MyOnClicklistenner implements OnClickListener {
        private Comment anser;
        private int position;

        public MyOnClicklistenner(Comment anser, int position) {
            this.anser = anser;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            isFristLevel = true;
            if (anser.top_status == 0) {
                getSystem(SystemUser.class)
                        .cryUpOrUnCryUpForVideoComment(anser.id, "1", new IUserBusynessCallBack() {
                            @Override
                            public void onResult(boolean result, String arg) {
                                showToast(arg);
                                if (result) {
                                    ImageView imageView = (ImageView) mListView.findViewWithTag("firClickZan" + position);
                                    imageView.setImageResource(R.drawable.icon_ding1);
                                    mList.get(position).top_status = 1;
                                }
                            }
                        });
            } else {
                showToast("已赞过!");
            }
        }
    }


    private boolean mSecondLevel = false;

    /**
     * 二级评论适配器
     *
     * @author Android2
     */
    class ReAnserAdapter extends BaseAdapter {
        private List<Comment> mList;

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        public void setList(List<Comment> list) {
            mList = list;
            notifyDataSetChanged();
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
                convertView = mLayoutInflater.inflate(R.layout.item_fragment_comment_reanser, null);
                holder = new ViewHolder();
                holder.userAdavater = (ImageView) convertView.findViewById(R.id.user_pic);
                holder.clickZan = (ImageView) convertView.findViewById(R.id.dian_zan);
                holder.userName = (TextView) convertView.findViewById(R.id.user_name);
                holder.userComment = (TextView) convertView.findViewById(R.id.comment);
                // holder.reAnSerLv = (ListView)
                // convertView.findViewById(R.id.comment_reancer_reancer);
                // holder.secondReAnserAdapter = new SecondReAnserAdapter();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Comment anser = mList.get(position);
            holder.userName.setText(anser.nickname + "回复" + anser.to_user + ":");
//            SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageSquare(anser.image, holder.userAdavater);
            getSystem(SystemCommon.class).displayDefaultImage(FragmentPlayerFlashComment.this, holder.userAdavater, anser.image, SystemCommon.SQUARE);
            if (anser.top_status == 1) {
                holder.clickZan.setImageResource(R.drawable.icon_ding1);
            } else {
                holder.clickZan.setImageResource(R.drawable.icon_ding0);
            }
            holder.clickZan.setTag("secClickZan" + position);
            holder.clickZan.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mSecondLevel = true;
                    return false;
                }
            });
            holder.userComment.setText(FrameworkUtils.unicode2String(anser.content));
            // holder.secondReAnserAdapter.setList(anser.reply_data);
            // holder.reAnSerLv.setAdapter(holder.secondReAnserAdapter);
            return convertView;
        }

        /**
         * 回复评论
         *
         * @param id      --回复的那个评论的id
         * @param content --回复的内容
         */
        private void ReAnserComment(String id, String content) {
            JsonObject params = new JsonObject();
            params.addProperty("id", id);
            params.addProperty("content", content);
            Utils.showProgressDialog(getActivity());

            getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.USER, ApiType.USER_comment, params), "mainSendCommentApi", false, false, new RequestCallBack<Comment>() {
                @Override
                public void onSuccess(Comment data, Response resp) {
                    /*getSystem(SystemCommon.class)
                            .addEXPAndBiscuits(json, getActivity(), "评论成功");*/
                    int i = Integer.parseInt(mVideo.comment_num);
                    i = i + 1;
                    mVideo.comment_num = i + "";
                    mList.add(0, data);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Response resp) {
                    FrameworkUtils.showToast(getActivity(), resp.msg);
                }

                @Override
                public void onComplete() {
                    Utils.dismissProgressDialog();
                }
            });

        }

        class ViewHolder {
            ImageView userAdavater;
            ImageView clickZan;
            TextView userName;
            TextView userComment;
            TextView publishTime;
            TextView response;
//			ListView reAnSerLv;
//			SecondReAnserAdapter secondReAnserAdapter;
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
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
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
                convertView = mLayoutInflater.inflate(R.layout.item_fragment_reancser_child, null);
                holder = new Holder();
                holder.userName = (TextView) convertView.findViewById(R.id.user_name);
                holder.userReAnser = (TextView) convertView.findViewById(R.id.comment);
                holder.dianZan = (ImageView) convertView.findViewById(R.id.dian_zan);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.userName.setText(mList.get(position).nickname + "回复" + mList.get(position).to_user + ":");
            holder.userReAnser.setText(FrameworkUtils.unicode2String(mList.get(position).content));
            return convertView;
        }

        class Holder {
            TextView userName;
            TextView userReAnser;
            TextView dianZanNum;
            ImageView dianZan;
        }
    }

    private void showOptionPop(View parent, final String... params) {
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
                try {
                    replyVideoComment(params);
                } catch (Exception e) {
                }
                mOptionPw.dismiss();
            }
        });
        // 举报
        layout.findViewById(R.id.option_zan).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                juBao(mCommentId);
                mOptionPw.dismiss();
            }
        });
        // 点赞
//        layout.findViewById(R.id.option_copy).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                topVideoComment(mCommentId);
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

    public void showChildPop(View parent, final String... params) {
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
        // // 评论
        // layout.findViewById(R.id.option_report).setOnClickListener(new
        // OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // replyVideoComment(params);
        // mChildPw.dismiss();
        // }
        // });
        // 举报
        layout.findViewById(R.id.option_zan).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                juBao(mCommentId);
                mChildPw.dismiss();
            }
        });
        // 点赞
//        layout.findViewById(R.id.option_copy).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                topVideoComment(mCommentId);
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

    /**
     * 视频评论点赞
     *
     * @param commentId
     */
    private void topVideoComment(String commentId) {
        getSystem(SystemUser.class)
                .cryUpOrUnCryUpForVideoComment(commentId, "1", new IUserBusynessCallBack() {

                    @Override
                    public void onResult(boolean result, String arg) {
                        showToast(arg);
                        if (result) {
                            if (isFristLevel) {
                                ImageView imageView = (ImageView) mListView.findViewWithTag("firClickZan" + mfriPos);
                                imageView.setImageResource(R.drawable.icon_ding1);
                                mList.get(mfriPos).top_status = 1;
                            } else {
                                ListView lv = (ListView) mListView.findViewWithTag("firLV" + mfriPos);
                                ImageView imageView = (ImageView) lv.findViewWithTag("secClickZan" + mSecPos);
                                imageView.setImageResource(R.drawable.icon_ding1);
                                mList.get(mfriPos).reply_data.get(mSecPos).top_status = 1;
                            }
                        }
                    }
                });
    }

    /**
     * 评论回复
     */
    private void replyVideoComment(String... params) {
        showDialog(params);
    }

    /**
     * 用户举报
     */
    private void juBao(String commentId) {
        getSystem(SystemUser.class)
                .reportVideoComment(commentId, new IUserBusynessCallBack() {

                    @Override
                    public void onResult(boolean result, String arg) {
                        showToast(arg);
                    }
                });
    }
}

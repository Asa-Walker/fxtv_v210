package com.fxtv.threebears.fragment.module.anchor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
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
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.model.Message;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 主播空间留言
 *
 * @author Android2
 */
public class FragmentAnchorSpaceMes extends BaseFragment {
    //private List<Message> mData;
    private XListView mListView;
    private MyAdapter mAdapter;
    private final int mPageSize = 20;
    private int mPageNum;
    private String mAnchorId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.activity_anchor_message, container, false);
        mAnchorId = ((ActivityAnchorZone) getActivity()).getAnchor().id;
        initView();
        getData(false, true);
        return mRoot;
    }

    private void initView() {
        mRoot.findViewById(R.id.cancel_fragment).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemManager.getInstance().getSystem(SystemFragmentManager.class)
                        .getTransaction(getActivity()).hide(FragmentAnchorSpaceMes.this).commit();
                ((ActivityAnchorZone) getActivity()).setFragmentPos(0);
            }
        });
        //阻止点击事件向下传递
        mRoot.findViewById(R.id.parent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        initListView();
        initPublish();
    }

    private void initPublish() {
        final EditText edit = (EditText) mRoot.findViewById(R.id.fragment_play_page_comment_et_msg);
        Button send = (Button) mRoot.findViewById(R.id.fragment_play_page_comment_btn_send);
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if
                        (getSystem(SystemUser.class).isLogin()) {
                    String text = edit.getText().toString();
                    if (text.trim().equals("")) {
                        FrameworkUtils.showToast(getActivity(),
                                getString(R.string.notice_input_message));
                        return;
                    }
                    sendMessage(FrameworkUtils.string2Unicode(text));
                } else {
                    getSystem(SystemCommon.class)
                            .noticeAndLogin(getActivity());
                }
                edit.setText("");
            }
        });
    }

    private void sendMessage(String msg) {
        JsonObject params = new JsonObject();
        params.addProperty("id", mAnchorId);
        params.addProperty("content", msg);
        Utils.showProgressDialog(getActivity());
        String url = Utils.processUrl(ModuleType.ANCHOR, ApiType.ANCHOR_messageAdd, params);
        getSystem(SystemHttp.class).get(getActivity(), url, "anchorPublish", false, false, new RequestCallBack<Message>() {
            @Override
            public void onSuccess(Message data, Response resp) {
                showToast(resp.msg);
                if (data != null) {
                    if (mAdapter.getListData() == null) {
                        mAdapter.setListData(new ArrayList<Message>());
                    }
                    mAdapter.getListData().add(0, data);
                    mAdapter.notifyDataSetChanged();
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
        mListView = (XListView) mRoot.findViewById(R.id.activity_anchor_message_lv);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setEmptyText("暂无留言");
        mListView.setPageSize(mPageSize);
        mAdapter = new MyAdapter(null);
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
    }

    private void getData(final boolean isRefresh, final boolean showDialog) {
        if (isRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }
        JsonObject params = new JsonObject();
        params.addProperty("id", mAnchorId);
        // params.addProperty("pid", "0");
        params.addProperty("page", mPageNum + "");
        params.addProperty("pagesize", mPageSize + "");
        if (showDialog) {
            Utils.showProgressDialog(getActivity());
        }
        String url = Utils.processUrl(ModuleType.ANCHOR, ApiType.ANCHOR_messageList, params);
        getSystem(SystemHttp.class).get(getActivity(), url, "anchorMessagesOfAnchor", false, true, new RequestCallBack<List<Message>>() {
            @Override
            public void onSuccess(List<Message> data, Response resp) {
                if (data != null && data.size() != 0) {
                    if (isRefresh) {
                        mAdapter.setListData(data);
                    } else {
                        mAdapter.addData(data);
                    }
                } else {
                    FrameworkUtils.showToast(getActivity(),
                            getString(R.string.notice_no_more_data));
                    mListView.noMoreData();
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

    class MyAdapter extends BaseListGridAdapter<Message> {


        public MyAdapter(List<Message> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_message,
                        null);
                viewHolder = new ViewHolder();
                viewHolder.img = (ImageView) convertView
                        .findViewById(R.id.photo);
                viewHolder.name = (TextView) convertView
                        .findViewById(R.id.name);
                viewHolder.time = (TextView) convertView
                        .findViewById(R.id.time);
                viewHolder.content = (TextView) convertView
                        .findViewById(R.id.content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Message message = getItem(position);
            if (getSystem(SystemUser.class).isLogin()
                    && message.nickname
                    .equals(getSystem(SystemUser.class).mUser.nickname)) {
                message.image = getSystem(SystemUser.class).mUser.image;
            }
            if (viewHolder.img.getTag() == null
                    || !viewHolder.img
                    .getTag()
                    .toString()
                    .equals(message.image)) {
                getSystem(SystemCommon.class).displayDefaultImage(FragmentAnchorSpaceMes.this, viewHolder.img, message.image);
            }
            viewHolder.name.setText(message.nickname);
            viewHolder.time.setText(message.create_time);
            viewHolder.content.setText(FrameworkUtils
                    .unicode2String(message.content));
            return convertView;
        }

        class ViewHolder {
            ImageView img;
            TextView name;
            TextView time;
            TextView content;
        }
    }
}

package com.fxtv.threebears.activity.anchor;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.framework.widget.xlistview.XListView.IXListViewListener;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.Message;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FXTV-Android
 *         <p/>
 *         主播-->留言
 */
public class ActivityAnchorMessage extends BaseActivity {
    private XListView mListView;
    private MyAdapter mAdapter;
    //private List<Message> mData;
    private String mAnchorId;
    private String mAnchorName;
    private TextView mNum;
    private String mReplyNumber;
    private int mPageNum;
    private final int mPageSize = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anchor_message);
        mAnchorId = getStringExtra("anchor_id");
        mAnchorName = getStringExtra("anchor_name");
        mReplyNumber = getStringExtra("anchor_message_reply_num");
        initView();
        Utils.showProgressDialog(this);
        getData(false, true);
    }

    private void initView() {
        initActionBar();
        // mNum = ((TextView) findViewById(R.id.message_count));
        updateMesNum();
        initListView();
        initPublish();
    }

    private void updateMesNum() {
        mNum.setText("共" + mReplyNumber + "条留言");
    }

    private void initPublish() {
        final EditText edit = (EditText) findViewById(R.id.fragment_play_page_comment_et_msg);
        Button send = (Button) findViewById(R.id.fragment_play_page_comment_btn_send);
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSystem(SystemUser.class).isLogin()) {
                    String text = edit.getText().toString();
                    if (text.trim().equals("")) {
                        FrameworkUtils.showToast(ActivityAnchorMessage.this,
                                getString(R.string.notice_input_message));
                        return;
                    }
                    sendMessage(FrameworkUtils.string2Unicode(text));
                } else {
                    getSystem(SystemCommon.class).noticeAndLogin(
                            ActivityAnchorMessage.this);
                }
                edit.setText("");
            }
        });
    }

    private void initListView() {
        mListView = (XListView) findViewById(R.id.activity_anchor_message_lv);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(false);
        mListView.setEmptyText("暂无留言");
        mListView.setPageSize(mPageSize);
        mAdapter = new MyAdapter();
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

    private void initActionBar() {
        TextView title = (TextView) findViewById(R.id.ab_title);
        title.setText(mAnchorName + "的留言");
        ImageView back = (ImageView) findViewById(R.id.ab_left_img);
        back.setImageResource(R.drawable.icon_arrow_left1);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class MyAdapter extends BaseListGridAdapter<Message> {

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
//				SystemManager
//						.getInstance()
//						.getSystem(SystemImageLoader.class)
//						.displayImageSquare(
//								message.image,
//								viewHolder.img);
                getSystem(SystemCommon.class).displayDefaultImage(ActivityAnchorMessage.this, viewHolder.img, message.image, SystemCommon.SQUARE);
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

    private void getData(final boolean isRefresh, final boolean showDialog) {
        if (isRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }
        JsonObject params = new JsonObject();
        params.addProperty("aid", mAnchorId);
        params.addProperty("pid", "0");
        params.addProperty("page", mPageNum + "");
        params.addProperty("pagesize", mPageSize + "");
        if (showDialog) {
            Utils.showProgressDialog(this);
        }
        /*if (mData == null) {
            mData = new ArrayList<Message>();
		}*/
        String url = Utils.processUrl(ModuleType.ANCHOR, ApiType.ANCHOR_messageList, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(this, url, "anchorMessagesOfAnchor", false, false, new RequestCallBack<List<Message>>() {
            @Override
            public void onSuccess(List<Message> data, Response resp) {
                if (data != null && data.size() != 0) {
                    if (isRefresh) {
                        mAdapter.setListData(data);
                    } else {
                        mAdapter.addData(data);
                    }
                } else {
                    FrameworkUtils.showToast(
                            ActivityAnchorMessage.this,
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

    private void sendMessage(final String msg) {
        JsonObject params = new JsonObject();
        params.addProperty("aid", mAnchorId);
        params.addProperty("user_id", getSystem(SystemUser.class).mUser.user_id);
        params.addProperty("pid", "0");
        params.addProperty("content", msg);
        Utils.showProgressDialog(this);
        String url = Utils.processUrl(ModuleType.ANCHOR, ApiType.ANCHOR_messageAdd, params);
        getSystem(SystemHttp.class).get(this, url, "anchorPublish", false, false, new RequestCallBack<Message>() {
            @Override
            public void onSuccess(Message data, Response resp) {
                showToast(resp.msg);
                if (data != null) {
                    if (mAdapter.getListData() == null) {
                        mAdapter.setListData(new ArrayList<Message>());
                    }
                    mAdapter.getListData().add(0, data);
//                    mAdapter.notifyDataSetChanged();
                }
                mAdapter.notifyDataSetChanged();
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
}

package com.fxtv.threebears.fragment.module.anchor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorMessage;
import com.fxtv.threebears.activity.anchor.ActivityAnchorVote;
import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.model.Message;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;

import java.util.List;

public class FragmentAnchorSpaceInteraction extends BaseFragment {
    private TextView mVote;
    private TextView mMessage;
    private Anchor mAnchor;
    private ListView mListView;
    //private List<Message> mDataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(
                R.layout.fragment_anchor_space_interaction, container, false);
        mAnchor = (Anchor) getArguments().getSerializable("anchor");
        initView();
        initData();
        return mRoot;
    }

    private void initData() {
        if (mAnchor.anchor_message != null)
            mListView.setAdapter(new MyAdapter(mAnchor.anchor_message));
        else
            mListView.setAdapter(new MyAdapter(null));
    }

    private void initView() {
        mListView = (ListView) mRoot.findViewById(R.id.interaction_message);
        mVote = (TextView) mRoot.findViewById(R.id.vote);
        mVote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
                mBundle.putString("anchor_id", mAnchor.id);
                mBundle.putString("anchor_name", mAnchor.name);
                FrameworkUtils.skipActivity(getActivity(),
                        ActivityAnchorVote.class, mBundle);
            }
        });
        mMessage = (TextView) mRoot.findViewById(R.id.message);
        mMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        ActivityAnchorMessage.class);
                intent.putExtra("anchor_id", mAnchor.id);
                intent.putExtra("anchor_name", mAnchor.name);
                intent.putExtra("anchor_message_reply_num",
                        mAnchor.message_num);
                startActivity(intent);
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
//			SystemManager
//					.getInstance()
//					.getSystem(SystemImageLoader.class)
//					.displayImageSquare(
//							message.image,
//							viewHolder.img);
            getSystem(SystemCommon.class).displayDefaultImage(FragmentAnchorSpaceInteraction.this, viewHolder.img, message.image);
            viewHolder.name.setText(message.nickname);
            viewHolder.time.setText(message.create_time);
            viewHolder.content
                    .setText(unicode2String(message.content));
            return convertView;
        }

        class ViewHolder {
            ImageView img;
            TextView name;
            TextView time;
            TextView content;
        }
    }

    public String unicode2String(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        string.append(hex[0]);
        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            if (hex[i].length() < 4) {
                string.append(hex[i]);
            } else {
                Log.i("test", hex[i].substring(0, 4));
                int data = Integer.parseInt(hex[i].substring(0, 4), 16);
                // 追加成string
                string.append((char) data);
                if (hex[i].length() > 4) {
                    string.append(hex[i].substring(4));
                }
            }
        }
        return string.toString();
    }
}

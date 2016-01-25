package com.fxtv.threebears.fragment.module.player;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemFragmentManager;
import com.fxtv.framework.widget.MyListView;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.model.VoteDetail;
import com.fxtv.threebears.model.VoteItem;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemUser;
import com.google.gson.Gson;

import java.util.List;

;

/**
 * 投票的Fragment
 *
 * @author Android2
 */
public class FragmentPlayerFlashVote extends BaseFragment {
    private List<VoteItem> mVoteItems;
    private VoteDetail mVote;
    private Video mVideo;
    private int width;
    private String[] mColorList;
    private int checkedPosition = -1;
    // 用户投票的那个item
    private String vote_detail_id;
    private MyListView mListView;
    private MyAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_vote, container, false);
        mVideo = (Video) getArguments().getSerializable("video");
        if (mVideo != null) {
            mVote = mVideo.vote;
            mVoteItems = mVote.option_list;
        }
        WindowManager wm = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        mColorList = getResources().getStringArray(R.array.color_list);
        initView();
        return mRoot;
    }

    private void initView() {
        ((TextView) mRoot.findViewById(R.id.title)).setText(mVote.title);
        //投票人数
        ((TextView) mRoot.findViewById(R.id.vote_num)).setText("共有" + mVote.vote_count + "位玩家参与投票");
        mRoot.findViewById(R.id.cancel_fragment).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).getTransaction(getActivity())
                        .hide(FragmentPlayerFlashVote.this).commit();
                ((ActivityVideoPlay) getActivity()).setFragmentPos(0);
            }
        });
        // 阻止点击事件向下传递
        mRoot.findViewById(R.id.parent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        initListView();

        initSubmit();
    }

    private void initSubmit() {
        final Button submit = (Button) mRoot.findViewById(R.id.submit);
        if ("1".equals(mVote.has_vote)) {
            submit.setVisibility(View.GONE);
        }
        // 提交
        mRoot.findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getSystem(SystemUser.class).isLogin()) {
                    FrameworkUtils.showToast(getActivity(), "请先登录");
                    return;
                }
                if (vote_detail_id == null || "".equals(vote_detail_id)) {
                    FrameworkUtils.showToast(getActivity(), "请选择后提交");
                    return;
                }
                submitVote(submit);
            }
        });
    }

    /**
     * 提交投票选项
     */
    private void submitVote(final View btn) {
        getSystem(SystemUser.class)
                .submitVote(mVote.id, vote_detail_id, new IUserBusynessCallBack() {

                    @Override
                    public void onResult(boolean result, String arg) {
                        if (result) {
                            showToast("投票成功!");
                            mListView.setOnItemClickListener(null);
                            Gson gson = new Gson();
                            VoteDetail tmp = gson.fromJson(arg, VoteDetail.class);
                            mVote = tmp;
                            if (mVote != null)
                                mVoteItems = mVote.option_list;
                            mAdapter.notifyDataSetChanged();
                            btn.setVisibility(View.GONE);
                        } else {
                            showToast(arg);
                        }
                    }
                });
    }

    private void initListView() {
        mListView = (MyListView) mRoot.findViewById(R.id.vote_result);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (checkedPosition == -1) {
                    arg1.findViewById(R.id.item_vote_checked).setVisibility(View.VISIBLE);
                    checkedPosition = arg2;
                    vote_detail_id = mVoteItems.get(checkedPosition).id;
                } else if (checkedPosition == arg2) {
                    arg1.findViewById(R.id.item_vote_checked).setVisibility(View.GONE);
                    checkedPosition = -1;
                    vote_detail_id = "";
                } else {
                    mListView.getChildAt(checkedPosition - mListView.getFirstVisiblePosition())
                            .findViewById(R.id.item_vote_checked).setVisibility(View.GONE);
                    arg1.findViewById(R.id.item_vote_checked).setVisibility(View.VISIBLE);
                    checkedPosition = arg2;
                    vote_detail_id = mVoteItems.get(checkedPosition).id;
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mVoteItems.size();
        }

        @Override
        public Object getItem(int arg0) {
            return mVoteItems.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            VoteItem voteItem = mVoteItems.get(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_vote, null);
                viewHolder = new ViewHolder();
                viewHolder.colorView = convertView.findViewById(R.id.item_vote_percent_view);
                viewHolder.percentTextView = (TextView) convertView.findViewById(R.id.item_vote_percent);
                viewHolder.checkedImage = (ImageView) convertView.findViewById(R.id.item_vote_checked);
                viewHolder.contentTextView = (TextView) convertView.findViewById(R.id.item_vote_content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (voteItem.has_vote_option.equals("1")) {
                viewHolder.checkedImage.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkedImage.setVisibility(View.GONE);
            }
            viewHolder.contentTextView.setText(voteItem.title);
            if ("1".equals(mVote.has_vote)) {
                viewHolder.percentTextView.setText(voteItem.option_percent + "%");
                viewHolder.colorView.setBackgroundColor(Color.parseColor(mColorList[position]));
                viewHolder.colorView.setLayoutParams(new LayoutParams((int) (width
                        * Float.parseFloat(voteItem.option_percent) / 100), LayoutParams.MATCH_PARENT));
            } else {
                viewHolder.colorView.setBackgroundColor(Color.WHITE);
                viewHolder.colorView.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT));
            }
            return convertView;
        }

        class ViewHolder {
            TextView percentTextView;
            TextView contentTextView;
            View colorView;
            ImageView checkedImage;
        }
    }
}

package com.fxtv.threebears.fragment.module.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemFragmentManager;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Video;

/**
 * 视频播放--主播描述fragment
 */
public class FragmentPlayerFlashDescription extends BaseFragment {
    private Video mVideo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_video_description, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mVideo = (Video) arguments.getSerializable("video");
        }
        initView();
        return mRoot;
    }

    private void initView() {
        // 隐藏自身
        mRoot.findViewById(R.id.cancel_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).getTransaction(getActivity())
                        .hide(FragmentPlayerFlashDescription.this).commit();
                ((ActivityVideoPlay) getActivity()).setFragmentPos(0);
            }
        });
        // 阻止点击事件向下传递
        mRoot.findViewById(R.id.parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        initTextView();
    }

    private void initTextView() {
        if (mVideo != null) {
            ((TextView) mRoot.findViewById(R.id.title)).setText(mVideo.title);
            ((TextView) mRoot.findViewById(R.id.play_date)).setText(mVideo.publish_time + " 播放 : " + mVideo.play_num);
            ((TextView) mRoot.findViewById(R.id.description)).setText(mVideo.intro);
        }
    }
}

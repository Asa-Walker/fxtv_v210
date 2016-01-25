package com.fxtv.threebears.fragment.module.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemFragmentManager;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Video;

public class FragmentPlayerDescription extends BaseFragment {
    private Video mVideo;
    private boolean isShow = false;
    private View parent;
    Bundle arguments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d("debug", "onCreateView");
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_player_desc, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mVideo = (Video) arguments.getSerializable("video");
        }
        initView();

        return mRoot;
    }

    private void initView() {
        TextView title = (TextView) mRoot.findViewById(R.id.title);
        title.setText(mVideo.title);
        // 发布时间和播放数
        TextView play_date = (TextView) mRoot.findViewById(R.id.play_date);
        play_date.setText(mVideo.publish_time + " 播放 : " + mVideo.play_num);
//        TextView description = (TextView) mRoot.findViewById(R.id.description);
//        description.setText(mVideo.intro);
        mRoot.findViewById(R.id.parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("video", mVideo);
                SystemManager
                        .getInstance()
                        .getSystem(SystemFragmentManager.class).addAnimFragment(R.id.video_container_action,
                        FragmentPlayerFlashDescription.class.getCanonicalName(), bundle,
                        getActivity());
                ((ActivityVideoPlay) getActivity()).setFragmentPos(6);
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d("debug","FragmentPlayerDescription onPause");
    }
}

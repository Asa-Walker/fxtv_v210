package com.fxtv.threebears.fragment.module.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;

;

public class FragmentPlayerAnchor extends BaseFragment {
    private Video mVideo;
    private Button mSubscribeBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_player_anchor, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mVideo = (Video) arguments.getSerializable("video");
        }
        initView();
        return mRoot;
    }

    private void initView() {
        ImageView AnchorImage = (ImageView) mRoot.findViewById(R.id.video_play_page_details_photo);
        mSubscribeBtn = (Button) mRoot.findViewById(R.id.video_play_page_details_btn_book);
        TextView subscribeNum = (TextView) mRoot.findViewById(R.id.video_play_page_details_book_num);
        TextView AnchorName = (TextView) mRoot.findViewById(R.id.video_play_page_details_name);
        subscribeNum.setText("订阅数 : " + mVideo.anchor.order_num);
        AnchorName.setText(mVideo.anchor.name);
        updateSubscribeBtn();
//		SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(mVideo.anchor.image, AnchorImage);
        getSystem(SystemCommon.class).displayDefaultImage(FragmentPlayerAnchor.this, AnchorImage, mVideo.anchor.image);
        mSubscribeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getSystem(SystemUser.class)
                        .orderOrUnOrderAnchor(mVideo.anchor.id, "1", new IUserBusynessCallBack() {
                            @Override
                            public void onResult(boolean result, String arg) {
                                if (result) {
                                    showToast(arg);
                                    mVideo.anchor.order_status = "1";
                                    updateSubscribeBtn();
                                } else {
                                    showToast(arg);
                                }
                            }
                        });
            }
        });
        AnchorImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("anchor_id", mVideo.anchor.id);
                FrameworkUtils.skipActivity(getActivity(), ActivityAnchorZone.class, bundle);
            }
        });
        AnchorName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("anchor_id", mVideo.anchor.id);
                FrameworkUtils.skipActivity(getActivity(), ActivityAnchorZone.class, bundle);
            }
        });
    }

    /**
     * 更新订阅按钮的背景
     */
    private void updateSubscribeBtn() {
        if (mVideo.anchor.order_status.equals("1")) {
            mSubscribeBtn.setText("已订阅");
            mSubscribeBtn.setBackgroundResource(R.drawable.shape_rectangle_circular_check);
        } else {
            mSubscribeBtn.setText("订阅");
            mSubscribeBtn.setBackgroundResource(R.drawable.shape_rectangle_circular_main);
        }
    }
}

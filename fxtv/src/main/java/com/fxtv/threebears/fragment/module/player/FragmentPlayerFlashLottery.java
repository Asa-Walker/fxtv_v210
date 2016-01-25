package com.fxtv.threebears.fragment.module.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemFragmentManager;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemCommon;

/**
 * 视频详情页下抽奖Fragment
 *
 * @author Android2
 */
public class FragmentPlayerFlashLottery extends BaseFragment {
    private Video mVideo;
    private double mPXTimes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_lottery, container, false);
        // mPXTimes = ((ActivityVideoPlay)getActivity()).mPXTimes;
        mVideo = (Video) getArguments().getSerializable("video");
        initView();
        return mRoot;
    }

    private void initView() {
        // 隐藏自身
        mRoot.findViewById(R.id.cancel_fragment).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).getTransaction(getActivity())
                        .hide(FragmentPlayerFlashLottery.this).commit();
                ((ActivityVideoPlay) getActivity()).setFragmentPos(0);
            }
        });
        // 阻止点击事件向下传递
        mRoot.findViewById(R.id.parent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        // 几天后截至(等待数据)
        TextView dayOver = (TextView) mRoot.findViewById(R.id.day_over);
        dayOver.setText("(" + mVideo.lottery.end_time + "截止)");
        initLotteryPercent();
    }

    private void initLotteryPercent() {
        mPXTimes = mRoot.findViewById(R.id.text_message).getLayoutParams().height / 40;
        int width = FrameworkUtils.getScreenWidth(getActivity());
        int joinNum = mVideo.lottery.join_num;
        int aTargetNum = mVideo.lottery.prize_list.get(0).target_num;
        int bTargetNum = mVideo.lottery.prize_list.get(1).target_num;
        int cTargetNum = mVideo.lottery.prize_list.get(2).target_num;
        width = (int) (width - 20 * mPXTimes);
        ProgressBar aLevel = (ProgressBar) mRoot.findViewById(R.id.video_play_one_progress_bar);
        ProgressBar bLevel = (ProgressBar) mRoot.findViewById(R.id.video_play_two_progress_bar);
        ProgressBar cLevel = (ProgressBar) mRoot.findViewById(R.id.video_play_three_progress_bar);
        // 参与抽奖的人数
        TextView num = (TextView) mRoot.findViewById(R.id.video_play_present_num);
        num.setText(mVideo.lottery.join_num + "");
        RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) num.getLayoutParams();
        int numWidth = (int) (width * (mVideo.lottery.join_percent * 0.01) - 5 * mPXTimes);
//        mLayoutParams.setMargins((int) (numWidth - (0 * mPXTimes)), 0, 0, 0);
        if (width - numWidth > 50) {
            mLayoutParams.setMargins((int) (numWidth -(5 * mPXTimes)), 0, 0, 0);
        } else {
            mLayoutParams.setMargins(width - 50, 0, 0, 0);
        }
        num.setLayoutParams(mLayoutParams);
        // 每个等级的人数
        TextView aLevelNum = (TextView) mRoot.findViewById(R.id.video_play_a_num);
        TextView bLevelNum = (TextView) mRoot.findViewById(R.id.video_play_b_num);
        TextView cLevelNum = (TextView) mRoot.findViewById(R.id.video_play_c_num);
        RelativeLayout aLevelT = (RelativeLayout) mRoot.findViewById(R.id.a_level_layout);
        RelativeLayout bLevelT = (RelativeLayout) mRoot.findViewById(R.id.b_level_layout);
        RelativeLayout cLevelT = (RelativeLayout) mRoot.findViewById(R.id.c_level_layout);
        aLevelNum.setText("(" + mVideo.lottery.prize_list.get(0).target_num + ")");
        bLevelNum.setText("(" + mVideo.lottery.prize_list.get(1).target_num + ")");
        cLevelNum.setText("(" + mVideo.lottery.prize_list.get(2).target_num + ")");
        LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) aLevelT.getLayoutParams();
        layoutParams.weight = mVideo.lottery.prize_list.get(0).percent;
        aLevelT.setLayoutParams(layoutParams);
        layoutParams = (android.widget.LinearLayout.LayoutParams) bLevelT.getLayoutParams();
        layoutParams.weight = mVideo.lottery.prize_list.get(1).percent;
        bLevelT.setLayoutParams(layoutParams);
        layoutParams = (android.widget.LinearLayout.LayoutParams) cLevelT.getLayoutParams();
        layoutParams.weight = mVideo.lottery.prize_list.get(2).percent;
        cLevelT.setLayoutParams(layoutParams);
        // 抽奖进度条
        layoutParams = (android.widget.LinearLayout.LayoutParams) aLevel.getLayoutParams();
        layoutParams.weight = mVideo.lottery.prize_list.get(0).percent;
        aLevel.setLayoutParams(layoutParams);
        layoutParams = (android.widget.LinearLayout.LayoutParams) bLevel.getLayoutParams();
        layoutParams.weight = mVideo.lottery.prize_list.get(1).percent;
        bLevel.setLayoutParams(layoutParams);
        layoutParams = (android.widget.LinearLayout.LayoutParams) cLevel.getLayoutParams();
        layoutParams.weight = mVideo.lottery.prize_list.get(2).percent;
        cLevel.setLayoutParams(layoutParams);
        aLevel.setMax(aTargetNum);
        bLevel.setMax(bTargetNum - aTargetNum);
        cLevel.setMax(cTargetNum - bTargetNum);
        if (joinNum <= aTargetNum) {
            aLevel.setProgress(joinNum);
            bLevel.setProgress(0);
            cLevel.setProgress(0);
        }
        if (aTargetNum < joinNum && joinNum <= bTargetNum) {
            aLevel.setProgress(aTargetNum);
            bLevel.setProgress(joinNum - aTargetNum);
            cLevel.setProgress(0);
        }
        if (joinNum > bTargetNum && joinNum <= cTargetNum) {
            aLevel.setProgress(aTargetNum);
            bLevel.setProgress(bTargetNum);
            cLevel.setProgress(joinNum - bTargetNum);
        }
        if (joinNum > cTargetNum) {
            aLevel.setProgress(aTargetNum);
            bLevel.setProgress(bTargetNum);
            cLevel.setProgress(cTargetNum);
        }
        // 奖品图片
        ImageView aLevelImg = (ImageView) mRoot.findViewById(R.id.a_level_present_image);
        ImageView bLevelImg = (ImageView) mRoot.findViewById(R.id.b_level_present_image);
        ImageView cLevelImg = (ImageView) mRoot.findViewById(R.id.c_level_present_image);
//		SystemManager.getInstance().getSystem(SystemImageLoader.class)
//				.displayImageDefault(mVideo.lottery.prize_list.get(0).image, aLevelImg);
//		SystemManager.getInstance().getSystem(SystemImageLoader.class)
//				.displayImageDefault(mVideo.lottery.prize_list.get(1).image, bLevelImg);
//		SystemManager.getInstance().getSystem(SystemImageLoader.class)
//				.displayImageDefault(mVideo.lottery.prize_list.get(2).image, cLevelImg);
        getSystem(SystemCommon.class).displayDefaultImage(FragmentPlayerFlashLottery.this, aLevelImg, mVideo.lottery.prize_list.get(0).image);
        getSystem(SystemCommon.class).displayDefaultImage(FragmentPlayerFlashLottery.this, bLevelImg, mVideo.lottery.prize_list.get(1).image);
        getSystem(SystemCommon.class).displayDefaultImage(FragmentPlayerFlashLottery.this, cLevelImg, mVideo.lottery.prize_list.get(2).image);
        // 奖品名字
        TextView aLevelTitle = (TextView) mRoot.findViewById(R.id.a_level_present_title);
        TextView bLevelTitle = (TextView) mRoot.findViewById(R.id.b_level_present_title);
        TextView cLevelTitle = (TextView) mRoot.findViewById(R.id.c_level_present_title);
        aLevelTitle.setText(mVideo.lottery.prize_list.get(0).title);
        bLevelTitle.setText(mVideo.lottery.prize_list.get(1).title);
        cLevelTitle.setText(mVideo.lottery.prize_list.get(2).title);
        // 奖品数量和价格
        TextView aLevelprice = (TextView) mRoot.findViewById(R.id.a_level_present_num_and_prize);
        TextView bLevelprice = (TextView) mRoot.findViewById(R.id.b_level_present_num_and_prize);
        TextView cLevelprice = (TextView) mRoot.findViewById(R.id.c_level_present_num_and_prize);
        aLevelprice.setText("数量 : " + mVideo.lottery.prize_list.get(0).num);
        bLevelprice.setText("数量 : " + mVideo.lottery.prize_list.get(1).num);
        cLevelprice.setText("数量 : " + mVideo.lottery.prize_list.get(2).num);
        // 奖品是否开启
        TextView aLevelOpen = (TextView) mRoot.findViewById(R.id.a_level_present_is_open);
        TextView bLevelOpen = (TextView) mRoot.findViewById(R.id.b_level_present_is_open);
        TextView cLevelOpen = (TextView) mRoot.findViewById(R.id.c_level_present_is_open);
        if (!"0".equals(mVideo.lottery.prize_list.get(0).status)) {
            aLevelOpen.setText("中奖人 : " + mVideo.lottery.prize_list.get(0).winner);
        }
        if (!"0".equals(mVideo.lottery.prize_list.get(1).status)) {
            bLevelOpen.setText("中奖人 : " + mVideo.lottery.prize_list.get(1).winner);
        }
        if (!"0".equals(mVideo.lottery.prize_list.get(2).status)) {
            cLevelOpen.setText("中奖人 : " + mVideo.lottery.prize_list.get(2).winner);
        }
    }
}

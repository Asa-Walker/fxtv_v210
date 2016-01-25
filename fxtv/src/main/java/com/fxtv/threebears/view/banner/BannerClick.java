package com.fxtv.threebears.view.banner;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.threebears.MainActivity;
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.activity.explorer.ActivityExplorerAnchorCircle;
import com.fxtv.threebears.activity.explorer.ActivityExplorerTask;
import com.fxtv.threebears.activity.explorer.ActivityMissionDetail;
import com.fxtv.threebears.activity.explorer.ActivityQRCode;
import com.fxtv.threebears.activity.explorer.ActivityRankList;
import com.fxtv.threebears.activity.explorer.ActivityTopicInfo;
import com.fxtv.threebears.activity.explorer.ActivityVoteDetail;
import com.fxtv.threebears.activity.game.ActivityGame;
import com.fxtv.threebears.activity.h5.ActivityWebView;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Game;

/**
 * Created by Administrator on 2016/1/21.
 */
public class BannerClick implements View.OnClickListener{
    BannerData itemData;
    BannerLayout.BannerClickListener clickListener;
    int position;
    public BannerClick(BannerData itemData) {
        this.itemData = itemData;
    }

    public BannerClick(BannerData itemData, int position,BannerLayout.BannerClickListener clickListener) {
        this.itemData = itemData;
        this.clickListener = clickListener;
        this.position=position;
    }

    @Override
    public void onClick(View v) {
        if(clickListener!=null){
            clickListener.onItemClick(v,position);
        }
        jump(v.getContext(),itemData);
    }

    private void jump(Context mContext,BannerData data) {
        if (data == null || TextUtils.isEmpty(data.type)) {
            return;
        }
        int type = Integer.parseInt(data.type);
        Bundle bundle = null;
        switch (type) {
            case 1:
                // 视频播放页
                bundle = new Bundle();
                bundle.putString("video_id", data.link);
                bundle.putString("skipType", "12");
                FrameworkUtils.skipActivity(mContext, ActivityVideoPlay.class, bundle);
                break;
            case 3:
                // 主播空间页
                bundle = new Bundle();
                bundle.putString("anchor_id", data.link);
                FrameworkUtils.skipActivity(mContext, ActivityAnchorZone.class, bundle);
                break;
            case 4:
                // 我的页
                ((MainActivity) mContext).jump2Child(4);
                break;
            case 5:
                // 每日任务
                FrameworkUtils.skipActivity(mContext, ActivityExplorerTask.class);
                break;
            case 6:
                // 活动详情页
                bundle = new Bundle();
                bundle.putString("activity_id", data.link);
                FrameworkUtils.skipActivity(mContext, ActivityMissionDetail.class, bundle);
                break;
            case 7:
                // 游戏内页
                Game game = new Game();
                game.id = data.link;
                // game.title = missionCenter.game_name;
                // game.game_type = missionCenter.game_type;
                bundle = new Bundle();
                bundle.putSerializable("game", game);
                FrameworkUtils.skipActivity(mContext, ActivityGame.class, bundle);
                break;
            case 8:
                // H5页面
                bundle = new Bundle();
                bundle.putString("url", data.link);
                bundle.putString("title", "WebView");
                bundle.putString("share_img", data.image);
                bundle.putString("share_title", data.title);
                bundle.putBoolean("share_enable", true);
                FrameworkUtils.skipActivity(mContext, ActivityWebView.class, bundle);
                break;
            //排行榜
            case 10:
                FrameworkUtils.skipActivity(mContext, ActivityRankList.class);
                break;
            case 11:
                // 热点投票
                bundle = new Bundle();
                bundle.putString("id", data.link);
                FrameworkUtils.skipActivity(mContext, ActivityVoteDetail.class, bundle);
                break;
            case 12:
                // 扫一扫
                FrameworkUtils.skipActivity(mContext, ActivityQRCode.class);
                break;
            case 13:
                // 主播圈
                FrameworkUtils.skipActivity(mContext, ActivityExplorerAnchorCircle.class);
                break;
            //热聊话题
            case 14:
                bundle = new Bundle();
                bundle.putString("id", data.link);
                FrameworkUtils.skipActivity(mContext, ActivityTopicInfo.class, bundle);
                break;
            //热聊话题--吐槽界面(暂定)
            case 18:
                bundle = new Bundle();
                bundle.putString("id", data.link);
                FrameworkUtils.skipActivity(mContext, ActivityTopicInfo.class, bundle);
                break;
            default:
                break;
        }
    }
}

package com.fxtv.threebears.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 图标显示的对象(1--显示 0--不显示)
 *
 * @author Android2
 */
public class IconShow implements Serializable {

    /**
     * 评论图标 (1--显示 0--不显示)
     */
    public String comment;

    /**
     * 抽奖图标 (1--显示 0--不显示)
     */
    public String lottery;

    /**
     * 投票图标 (1--显示 0--不显示)
     */
    public String vote;

    /**
     * 专辑图标 (1--显示 0--不显示)
     */
    public String album;

    /**
     * 相关图标 (1--显示 0--不显示)
     */
    public String relate;

    /**
     * 打赏图标 (1--显示 0--不显示)
     */
    public String give;

    /**
     * 主播空间留言图标 (1--显示 0--不显示)
     */
    public String message;

    /**
     * 主播空间动态图标 (1--显示 0--不显示)
     */
    public String bbs;

    /**
     * 主播空间推荐图标 (1--显示 0--不显示)
     */
    public String friend;

    /**
     * 主播空间店铺图标 (1--显示 0--不显示)
     */
    public String shop;

    /**
     * 通知图标（1显示；0隐藏）
     */
    public String notice;

    public boolean shouldShow(String key) {
        if (!TextUtils.isEmpty(key)) {
            return key.equals("1");
        }

        return false;
    }
}

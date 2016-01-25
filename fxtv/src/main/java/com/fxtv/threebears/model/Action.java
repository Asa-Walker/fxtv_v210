package com.fxtv.threebears.model;

import com.fxtv.threebears.view.banner.BannerData;

import java.io.Serializable;

public class Action extends BannerData implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 活动id
     */
    public String id;
    /**
     * 活动标题
     */
//	public String title;
    /**
     * 活动详情
     */
    public String detail;
    /**
     * 活动图片
     */
//	public String image;
    /**
     * 跳转类型
     */
//	public String type;
    /**
     * 跳转id（）
     */
//	public String link;
    /**
     * 游戏名
     */
    public String game_name;

    public String video_info;

    public Game game_info;

    /**
     * 是否事当前赛事 1=进行中，0=未开始，-1=已结束
     */
    public String is_current;

    /**
     * 活动的状态(1：即将开始；2：进行中；3：已结束；)
     */
    public String status;

    /**
     * 活动的内容
     */
    public String intro;
}

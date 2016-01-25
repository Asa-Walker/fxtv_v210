package com.fxtv.threebears.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 主播动态
 *
 * @author Administrator
 */
public class BBS implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 动态id
     */
    public String id;

    /**
     * 主播id
     */
    public String anchor_id;

    /**
     * 动态内容
     */
    public String content;

    /**
     * 动态添加时间
     */
    public String create_time;

    /**
     * 动态图片
     */
    public ArrayList<String> images;

    /**
     * 动态回复数
     */
    public String reply_num;

    /**
     * 主播信息
     */
    public Anchor user_info;

    /**
     * 主播名字
     */
    public String name;

    /**
     * 主播头像
     */
    public String image;

    /**
     * 是否被看过
     */
    public boolean bbs_is_checked = true;

    /**
     * 点赞的次数
     */
    public String like_num;

    /**
     * 当前用户是否赞过(1--已赞,0--未赞)
     */
    public String like_status;

    /**
     * 分享的次数
     */
    public String share_num;

}

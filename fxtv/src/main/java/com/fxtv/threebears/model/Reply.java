package com.fxtv.threebears.model;

import java.io.Serializable;

/**
 * 主播动态里的用户回复
 *
 * @author Administrator
 */
public class Reply implements Serializable {
    /**
     * 回复id
     */
    public String id;
    /**
     * 回复用户的id
     */
    public String bbs_id;
    /**
     * 回复内容
     */
    public String content;
    /**
     * 回复时间
     */
    public String create_time;

    /**
     * 用户昵称
     */
    public String nickname;

    /**
     * 用户头像
     */
    public String image;

    /**
     * 是否已点赞
     */
    public String like_status;

    /**
     * 点赞的次数
     */
    public String like_num;
}

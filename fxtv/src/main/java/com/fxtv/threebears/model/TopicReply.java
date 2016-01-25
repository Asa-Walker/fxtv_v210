package com.fxtv.threebears.model;

import java.io.Serializable;

/**
 * 吐槽的回复
 */
public class TopicReply implements Serializable {
    public String id;

    /**
     * 吐槽回复的内容
     */
    public String content;

    public String create_time;

    /**
     * 评论举报的状态：0,未举报；1，已举报
     */
    public int report_status;

    public String nickname;

    /**
     * 用户头像
     */
    public String image;

    /**
     * 评论点赞的状态
     * (1--已点赞，0--未点赞)
     */
    public String like_status;

}

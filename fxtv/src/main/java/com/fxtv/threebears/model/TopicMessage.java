package com.fxtv.threebears.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wzh on 2015/12/24. 吐槽对象
 */
public class TopicMessage implements Serializable {
    public String id,//吐槽id
            content,//吐槽内容
            create_time,//发布时间
            report_status,//举报状态（1:已举报;0:未举报;）
            like_status,//（1:已点赞;0:未点赞;）
            nickname,//用户昵称
            image;//用户头像

    public ArrayList<String> images;//吐槽图片

    public String like_num,//点赞数
            reply_num,//回复数
            share_num;//分享数
}

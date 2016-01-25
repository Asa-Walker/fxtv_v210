package com.fxtv.threebears.model;

import java.io.Serializable;

/**
 * Created by wzh on 2015/12/24.
 */
public class TopicInfo implements Serializable{
    public String id,	//话题ID
    title,	//话题标题
    content,	//话题内容
    image,	//话题banner
    create_time,	//发布时间
    follow_status,	//关注状态（1:已关注;0:未关注;）
    cate_id,	//话题分类ID
    cate_title,//	话题分类名
    cate_image;	//话题分类图片

    public String join_num,	//吐槽数
            follow_num,//	关注数
            view_num;//	阅读数

    @Override
    public String toString() {
        return "TopicInfo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", image='" + image + '\'' +
                ", join_num='" + join_num + '\'' +
                ", follow_num='" + follow_num + '\'' +
                ", create_time='" + create_time + '\'' +
                ", view_num='" + view_num + '\'' +
                ", follow_status='" + follow_status + '\'' +
                ", cate_id='" + cate_id + '\'' +
                ", cate_title='" + cate_title + '\'' +
                ", cate_image='" + cate_image + '\'' +
                '}';
    }
}

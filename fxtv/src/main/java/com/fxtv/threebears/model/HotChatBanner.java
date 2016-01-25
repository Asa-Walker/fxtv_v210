package com.fxtv.threebears.model;

import com.fxtv.threebears.view.banner.BannerData;

import java.io.Serializable;

/**
 * 热聊话题s
 */
public class HotChatBanner extends BannerData implements Serializable {

    public String id;

    /**
     * 吐槽数
     */
    public long join_num;

    /**
     * 发布时间
     */
    public String create_time;

    /**
     * 话题分类id
     */
    public String cate_id;

    /**
     * 话题分类名
     */
    public String cate_name;

    /**
     * 话题分类图片
     */
    public String cate_image;
}

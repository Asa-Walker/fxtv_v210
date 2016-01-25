package com.fxtv.threebears.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/1/7.
 */
public class Notice implements Serializable {

    /**
     * 显示的图片地址
     */
    public String image;

    /**
     * 要跳转的H5地址
     */
    public String url;

    /**
     * 通知H5分享的图片
     */
    public String share_image;
}

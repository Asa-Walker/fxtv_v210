package com.fxtv.threebears.model;

import com.fxtv.threebears.view.banner.BannerData;

import java.io.Serializable;

public class IndexBanner extends BannerData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String id;
//	public String title;
	//public String image;
	public String duration;
	public String publish_time;
	//public String type;
	//public String link;
	/**
	 * 是否有奖（1-有，0-无），返回视频类型才显示
	 */
	//public String is_lottery;


}

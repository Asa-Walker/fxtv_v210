package com.fxtv.threebears.model;

import java.io.Serializable;

/**
 * 主播动态
 * 
 * @author Administrator
 * 
 */
public class AnchorAct implements Serializable{

	/**
	 * 主播名字
	 */
	public String AnchorName;

	/**
	 * 主播发的内容
	 */
	public String AnchorContent;

	/**
	 * 主播的发布的时间
	 */
	public String AnchorPublishTime;

	/**
	 * 主播动态下的评论数
	 */
	public int CommentNumber;

	/**
	 * 主播头像
	 */
	public String AnchorPic;
	
	/**
	 * 主播发表的图片
	 */
	public String AnchorContentImg;

}

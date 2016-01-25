package com.fxtv.threebears.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论对象
 * 
 * @author Android2
 * 
 */
public class Comment implements Serializable {

	public String id;

	/**
	 * 回复的用户名称
	 */
	public String nickname;

	/**
	 * 回复用户的头像地址
	 */
	public String image;

	/**
	 * 用户的评论
	 */
	public String content;

	/**
	 * 用户评论的发布时间
	 */
	public String create_time;

	/**
	 * 用户点赞的图片资源
	 */
	public int dian_zan_resource;

	/**
	 * 用户评论回复的二级 列表
	 */
	public List<Comment> reply_data;

	/**
	 * 回复的用户名字
	 */
	public String to_user;

	/**
	 * 评论举报的状态：0,未举报；1，已举报
	 */
	public int report_status;

	/**
	 * 评论顶的状态：0,没有顶；1，已顶
	 */
	public int top_status;

}

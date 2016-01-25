package com.fxtv.threebears.model;

import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 留言ID
	 */
	public String id;
	/**
	 * 主播id
	 */
	public String user_id;
	/**
	 * 留言内容
	 */
	public String content;
	/**
	 * 留言者名称
	 */
	public String nickname;
	/**
	 * 留言头像
	 */
	public String image;
	/**
	 * 留言时间
	 */
	public String create_time;
	/**
	 * 回复数量
	 */
	public String reply_num;
	
}

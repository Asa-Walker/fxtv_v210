package com.fxtv.threebears.model;

import java.io.Serializable;

public class MyMessageItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 会话id
	 */
	public String id;
	
	/**
	 * 发起人id
	 */
	public String send_id;
	
	/**
	 * 接受人id
	 */
	public String dialog_accept;
	
	/**
	 * 发起时间
	 */
	public String dialog_create_time;
	
	/**
	 * 最后发消息的时间
	 */
	public String last_time;
	
	/**
	 * 限制会话单方发送消息上限
	 */
	public String dialog_count;
	
	/**
	 * 该对话是否有新消息
	 */
	public String readed;
	
	/**
	 * 对话发起人的账号类型
	 */
	public String dialog_type;
	
	/**
	 * 会话对方的昵称
	 */
	public String nickname;
	
	/**
	 * 会话对方的头像
	 */
	public String image;
	
	/**
	 * 会话的最后一条消息
	 */
	public String last_message;
}

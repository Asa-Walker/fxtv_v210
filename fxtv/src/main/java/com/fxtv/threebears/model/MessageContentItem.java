package com.fxtv.threebears.model;

public class MessageContentItem {

	/**
	 * 会话消息id
	 */
	public String message_id;

	/**
	 * 发起人id
	 */
	public	String message_send;

	/**
	 * 接受人id
	 */
	public	String message_accept;

	/**
	 * 消息内容
	 */
	public	String content;

	/**
	 * 消息发送时间
	 */
	public	String create_time;

	/**
	 * 消息发起人的账号类型
	 */
	public String message_type;

	/**
	 * 消息发送方的昵称
	 */
	public	String nickname;

	/**
	 * 消息发送方的头像
	 */
	public	String image;

	/**
	 * 消息显示的位置（1：左边；2：是右边）
	 */
	public String position;
}

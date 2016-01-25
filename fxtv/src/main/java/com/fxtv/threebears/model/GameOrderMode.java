package com.fxtv.threebears.model;

import java.io.Serializable;

import android.graphics.Color;

public class GameOrderMode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 订阅源id
	 */
	public String id;
	
	public String name;
	/**
	 * 订阅源名称
	 */
	public String title;
	/**
	 * 订阅源图片
	 */
	public String image;
	/**
	 * 类型
	 */
	public String type;
	/**
	 * 订阅状态
	 */
	public String status;
	
	/**
	 * 订阅源所属游戏的id
	 */
	public String game_id;

	public String order_description;

	public String order_intime;

	public int mColor = Color.BLACK;
}

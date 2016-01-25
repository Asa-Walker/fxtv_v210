package com.fxtv.threebears.model;

import java.io.Serializable;

public class UserTab implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 游戏id
	 */
	public String game_id;

	/**
	 * 游戏名
	 */
	public String game_name;

	/**
	 * 游戏图片
	 */
	public String game_image;

	/**
	 * 游戏状态 1--显示 0--隐藏
	 */
	public String game_status;

	/**
	 * tab 类型
	 */
	public String game_type;

	public Object clone() {
		UserTab o = null;
		try {
			o = (UserTab) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
}

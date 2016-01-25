package com.fxtv.threebears.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author FXTV-Android
 * 
 */
public class Game implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 游戏id
	 */
	public String id;

	/**
	 * 游戏标题
	 */
	public String title;

	/**
	 * 游戏缩略图
	 */
	public String image;
	/**
	 * 游戏描述
	 */
	public String game_description;
	/**
	 * 订阅数
	 */
	public String order_num;

	/**
	 * 游戏数
	 */
	public String video_num;

	/**
	 * 游戏tab
	 */
	public List<GameTab> game_menu;
	/**
	 * 订阅的东西
	 */
	public List<GameOrderMode> order_list;

	public String game_type;

	/**
	 * 引导页游戏是否选中
	 */
	public int game_is_selected;

	/**
	 * 游戏是否被选中(1------显示 0------不显示 )
	 */
	public String status;

	@Override
	public String toString() {
		return "Game [id=" + id + ", title=" + title + ", image=" + image + ", game_description=" + game_description
				+ ", order_num=" + order_num + ", video_num=" + video_num + ", game_menu=" + game_menu
				+ ", order_list=" + order_list + ", game_type=" + game_type + ", game_is_selected=" + game_is_selected
				+ ", status=" + status + ", daily_video_num=" + daily_video_num + "]";
	}

	public Game clone() {
		Game o = null;
		try {
			o = (Game) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * 每日新上视频数
	 */
	public String daily_video_num;
	
}

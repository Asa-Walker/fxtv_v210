package com.fxtv.threebears.model;

import java.io.Serializable;

/**
 * 奖品实体
 * 
 * @author Android2
 * 
 */
public class Prize implements Serializable {

	/**
	 * 奖品名称
	 */
	public String title;

	/**
	 * 奖品价格
	 */
	public String price;

	/**
	 * 获取奖品所需要的参与人数
	 */
	public int target_num;

	/**
	 * 奖品的缩略图
	 */
	public String image;

	/**
	 * 奖品的数量
	 */
	public String num;

	/**
	 * 获奖人姓名
	 */
	public String winner;

	/**
	 * 奖品状态 0---未开奖 1---已开奖
	 */
	public String status;

	/**
	 * 该奖品所需人数占的比例
	 */
	public int percent;
}

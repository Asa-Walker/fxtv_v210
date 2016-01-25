package com.fxtv.threebears.model;

/**
 * 订阅引导第二步的对象
 * 
 * @author Android2
 * 
 */
public class Order {

	/**
	 * 订阅的id
	 */
	public String id;
	
	/**
	 * 订阅源名称
	 */
	public String name;
	
	/**
	 * 订阅源图片
	 */
	public String image;
	
	/**
	 * 类型(1：主播；2：分类)
	 */
	public String type;
	
	/**
	 * 订阅状态(0：未订阅；1：已订阅；)
	 */
	public String status;
	
	/**
	 * 是否被选中
	 * 
	 */
	public int is_selected;
}

package com.fxtv.threebears.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

public class Shop implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主播店铺id
	 */
	@DatabaseField(columnName = "shop_id")
	public String id;
	
	/**
	 * 主播店铺名称
	 */
	@DatabaseField(columnName = "shop_name")
	public String title;
	/**
	 * 主播店铺链接
	 */
	@DatabaseField(columnName = "shop_link")
	public String link;
	/**
	 * 主播店铺图片
	 */
	@DatabaseField(columnName = "shop_image")
	public String image;
}

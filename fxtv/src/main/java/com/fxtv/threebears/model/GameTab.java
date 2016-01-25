package com.fxtv.threebears.model;

import java.io.Serializable;

public class GameTab implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 栏目id
	 */
	public String id;
	/**
	 * 栏目名称
	 */
	public String title;
	/**
	 * 栏目类型
	 */
	public String type;
	@Override
	public String toString() {
		return "GameTab [menu_id=" + id + ", title=" + title + ", type=" + type + "]";
	}
	
	

}

package com.fxtv.threebears.model;

import java.io.Serializable;

/**
 * 任务
 * @author Administrator
 *
 */
public class Mission implements Serializable{

	/**
	 * 任务明细id
	 */
	public String id;
	
	/**
	 * 记录的数值
	 */
	public String result;
	
	/**
	 * 记录的时间
	 */
	public String create_time;
	
	/**
	 * 功能名称
	 */
	public String title;
}

package com.fxtv.threebears.model;

import java.io.Serializable;

/**
 * 视频清晰度
 * 
 * @author Android2
 * 
 */
public class VideoStreamsizes implements Serializable {
	@Override
	public String toString() {
		return "VideoStreamsizes [low=" + low + ", normal=" + normal + ", high=" + high + ", hd2=" + hd2 + "]";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 流畅
	 */
	public String low;
	/**
	 * 标清
	 */
	public String normal;
	/**
	 * 高清
	 */
	public String high;
	/**
	 * 超清
	 */
	public String hd2;
	/**
	 * 流畅大小
	 */
	public int lowSize;
	/**
	 * 标清大小
	 */
	public int normalSize;
	/**
	 * 高清大小
	 */
	public int highSize;
	/**
	 * 超清大小
	 */
	public int hd2Size;
	

}

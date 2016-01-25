package com.fxtv.threebears.model;

/**
 * 视频清晰度（2.0新版）
 * 
 * @author Administrator
 * 
 */
public class StreamSize {

	/**
	 * 清晰度名称(流畅，标清，高清，超清)
	 */
	public String title;

	/**
	 * 视频清晰度 （low:流畅,normal:标清,high:高清,hd2:超清）
	 */
	public String stream_type;

	/**
	 * 视频大小单位MB
	 */
	public int size;

	/**
	 * 视频地址
	 */
	public String url;

	@Override
	public String toString() {
		return "StreamSize [title=" + title + ", stream_type=" + stream_type + ", size=" + size + ", url=" + url + "]";
	}

}

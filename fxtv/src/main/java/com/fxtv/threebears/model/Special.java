package com.fxtv.threebears.model;

import java.io.Serializable;
import java.util.List;

public class Special implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 专辑名称
	 */
	public String title;
	/**
	 * 专辑id
	 */
	public String id;
	/**
	 * 专辑下的视频数量
	 */
	public String anchor_album_video_num;
	/**
	 * 专辑简介
	 */
	public String intro;
	/**
	 * 专辑图片
	 */
	public String image;
	/**
	 * 视频集
	 */
	public List<Video> anchor_album_videos;
	
	/**
	 * 专辑下的视频数量(新版)
	 */
	public String video_num;

}

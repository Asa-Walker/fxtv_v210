package com.fxtv.threebears.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author FXTV-Android
 * 
 */
@DatabaseTable(tableName = "tb_cache_video")
public class VideoOld implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 视频id
	 */
	@DatabaseField(id = true)
	public String video_id;
	/**
	 * 视频标题
	 */
	@DatabaseField(columnName = "video_title")
	public String video_title;
	/**
	 * 视频描述
	 */
	@DatabaseField(columnName = "video_description")
	public String video_description;
	/**
	 * 视频播放地址
	 */
	@DatabaseField(columnName = "video_m3u8_mp4")
	public String video_m3u8_mp4;

	/**
	 * 视频弹幕地址
	 */
	@DatabaseField(columnName = "barrage_url", canBeNull = true)
	public String video_barrage_url;

	/**
	 * 视频缩略图
	 */
	@DatabaseField(columnName = "video_image")
	public String video_image;
	/**
	 * 视频发布时间
	 */
	@DatabaseField(columnName = "video_publish_time")
	public String video_publish_time;
	/**
	 * 视频播放数
	 */
	@DatabaseField(columnName = "video_play_num")
	public String video_play_num;
	/**
	 * 视频评论数
	 */
	@DatabaseField(columnName = "video_comment_num")
	public String video_comment_num;
	/**
	 * 视频收藏数
	 */
	@DatabaseField(columnName = "video_collect_num")
	public String video_collect_num;
	/**
	 * 赞数
	 */
	@DatabaseField(columnName = "video_up_num")
	public String video_up_num;
	/**
	 * 踩数
	 */
	@DatabaseField(columnName = "video_down_num")
	public String video_down_num;
	/**
	 * 视频时长
	 */
	@DatabaseField(columnName = "video_duration")
	public String video_duration;
	/**
	 * 主播信息
	 */
	// @DatabaseField(columnName = "video_anchor_info", foreign = true,
	// foreignAutoCreate = true, foreignAutoRefresh = true)
	public Anchor video_anchor_info;
	/**
	 * 收藏状态
	 */
	@DatabaseField(columnName = "video_collect_status")
	public String video_collect_status;
	/**
	 * 点赞状态
	 */
	@DatabaseField(columnName = "video_zan_status")
	public String video_zan_status;
	/**
	 * 抽奖信息
	 */
	public VideoIottery video_lottery_info;
	/**
	 * 游戏名称
	 */
	@DatabaseField(columnName = "video_game_name")
	public String video_game_name;
	/**
	 * 视频所属主播的name
	 */
	@DatabaseField(columnName = "video_anchor_name")
	public String video_anchor_name;
	/**
	 * 抽奖状态
	 */
	public String video_lottery_status;

	/**
	 * 视频播放的最后位置
	 */
	public String video_last_progress;

	/**
	 * 视频播放的最后时间
	 */
	public int video_last_time;

	/**
	 * 视屏播放的进度
	 */
	public String video_last_scale;
	/**
	 * 视频的下载进度
	 */
	@DatabaseField(columnName = "video_download_progress")
	public String video_download_progress = "0";

	/**
	 * 视频下载状态 （下载中和下载完成）
	 * 
	 * 0:下载完成, 1：下载中, 2：下载暂停, 3:等待中, -1:下载失败
	 */
	@DatabaseField(columnName = "video_download_state")
	public String video_download_state;

	/**
	 * 下载的视频的大小
	 */
	@DatabaseField(columnName = "video_download_size")
	public double video_download_size;
	/**
	 * 表示下载状态的小图标
	 */
	public int video_download_Img;

	/**
	 * 视频格式集合
	 */
	public VideoStreamsizes video_streamsizes;

	/**
	 * 投票信息
	 */
	public VoteDetail video_anchor_voting;
}

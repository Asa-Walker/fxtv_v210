package com.fxtv.threebears.model;

import java.io.Serializable;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author FXTV-Android
 * 
 *         主播实体类
 */
@DatabaseTable(tableName = "tb_cache_anchor")
public class Anchor implements Serializable {
	@Override
	public String toString() {
		return "Anchor [id=" + id + ", anchor_order_count=" + anchor_order_count + ", order_num=" + order_num
				+ ", message_num=" + message_num + ", guard_num=" + guard_num + ", name=" + name + ", avatar=" + avatar
				+ ", background=" + background + ", anchor_approve=" + anchor_approve + ", intro=" + intro
				+ ", anchor_bbs=" + anchor_bbs + ", anchor_shop_link=" + anchor_shop_link + ", anchor_shop_image="
				+ anchor_shop_image + ", order_status=" + order_status + ", guard_status=" + guard_status
				+ ", is_show=" + is_show + ", new_video=" + video_list + ", anchor_album_list=" + album_list
				+ ", new_bbs_num=" + bbs_num + ", anchor_shop_list=" + shop_list + ", recom=" + recom
				+ ", anchor_message=" + anchor_message + ", anchor_is_selected=" + anchor_is_selected
				+ ", anchor_first_name=" + anchor_first_name + ", is_auto=" + is_auth + ", daily_video_num="
				+ daily_video_num + ", game_arr=" + game_arr + ", server_time=" + server_time + "]";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主播的id
	 */
	@DatabaseField(id = true, columnName = "anchor_id")
	public String id;
	/**
	 * 主播订阅数 我日 上面那个是要删掉的
	 */
	@DatabaseField(columnName = "anchor_order_count")
	public String anchor_order_count;
	/**
	 * 主播订阅数
	 */
	@DatabaseField(columnName = "anchor_order_num")
	public String order_num;
	/**
	 * 主播回复数
	 */
	@DatabaseField(columnName = "anchor_message_num")
	public String message_num;
	/**
	 * 主播守护数
	 */
	@DatabaseField(columnName = "anchor_guard_num")
	public String guard_num;
	/**
	 * 主播名
	 */
	@DatabaseField(columnName = "anchor_name")
	public String name;
	/**
	 * 主播头像
	 */
	@DatabaseField(columnName = "anchor_avatar")
	public String avatar;
	/**
	 * 主播空间背景
	 */
	@DatabaseField(columnName = "anchor_background")
	public String background;
	/**
	 * 是否认证(1:已认证；0:未认证；)
	 */
	@DatabaseField(columnName = "anchor_approve")
	public String anchor_approve;
	/**
	 * 主播简介
	 */
	@DatabaseField(columnName = "anchor_intro")
	public String intro;
	/**
	 * 主播公告
	 */
	@DatabaseField(columnName = "anchor_bbs")
	public String anchor_bbs;
	/**
	 * 主播店铺链接
	 */
	@DatabaseField(columnName = "anchor_shop_link")
	public String anchor_shop_link;
	/**
	 * 主播店铺照片
	 */
	@DatabaseField(columnName = "anchor_shop_image")
	public String anchor_shop_image;
	/**
	 * 主播订阅状态 1--已订阅 0--未订阅
	 */
	@DatabaseField(columnName = "anchor_order_status")
	public String order_status;
	/**
	 * 主播守护状态 1--已守护 0--未守护
	 */
	@DatabaseField(columnName = "anchor_guard_status")
	public String guard_status;
	/**
	 * 是否可以订阅，1 可订阅 0不可订阅
	 */
	@DatabaseField(columnName = "anchor_is_show")
	public String is_show;
	/**
	 * 最新视频列表
	 */
	public List<Video> video_list;
	/**
	 * 所有专辑列表
	 */
	public List<Special> album_list;
	/**
	 * 主播新动态数
	 */
	@DatabaseField(columnName = "anchor_new_bbs_num")
	public String bbs_num;
	/**
	 * 店铺列表
	 */
	public List<Shop> shop_list;
	/**
	 * 推荐主播
	 */
	public List<Anchor> recom;
	/**
	 * 最新回复
	 */
	public List<Message> anchor_message;
	/**
	 * 新手引导页选择
	 */
	public int anchor_is_selected;
	/**
	 * 主播姓名的首字母
	 */
	public String anchor_first_name;
	/**
	 * 暂时不知道是什么意思(新接口添加的字段)
	 */
	public String is_auth;
	/**
	 * 日，不知道什么意思(新接口添加的字段)
	 */
	public String daily_video_num;
	/**
	 * 不知道什么意思(新接口添加的字段)
	 */
	public String game_arr;
	/**
	 * 服务时间?不懂(新接口添加的字段)
	 */
	public String server_time;
	
	/**
	 * 主播头像
	 */
	public String image;
	/**
	 * 实现图标的对象
	 */
	public IconShow icon_show;
	
	/**
	 * 排行榜订阅数
	 */
	public long nums;
	
	/**
	 * 视频数量
	 */
	public long video_num;
}

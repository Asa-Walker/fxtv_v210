package com.fxtv.threebears.model;

import com.fxtv.threebears.R;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

/**
 * @author FXTV-Android
 */
@DatabaseTable(tableName = "tb_cache_video_111")
public class Video implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 视频id
     */
    @DatabaseField(columnName = "id", id = true)
    public String id;
    /**
     * 视频标题
     */
    @DatabaseField(columnName = "title")
    public String title;
    /**
     * 视频描述
     */
    public String intro;
    /**
     * 视频播放地址 移动源
     */
    @DatabaseField(columnName = "url")
    public String url;

    /**
     * 视频播放地址 PC源
     */
    public String url_pc;

    /**
     * 1：使用pc源；0不使用pc源
     */
    public String use_pc;

    /**
     * 活动图标对象
     */
    public Notice notice;

    /**
     * 视频弹幕地址
     */
    public String barrage_url;
    /**
     * 视频缩略图
     */
    @DatabaseField(columnName = "image")
    public String image;
    /**
     * 视频发布时间
     */
    public String publish_time;
    /**
     * 视频播放数
     */
    public String play_num;
    /**
     * 视频播放数(排行榜里的字段，可能是因为不是同一个人写的，所以字段名不一样)
     */
    public String nums;
    /**
     * 视频评论数
     */
    public String comment_num;
    /**
     * 视频收藏数
     */
    public String video_collect_num;
    /**
     * 赞数
     */
    public String video_up_num;
    /**
     * 踩数
     */
    public String video_down_num;
    /**
     * 视频时长
     */
    @DatabaseField(columnName = "video_duration")
    public String duration;
    /**
     * 主播信息
     */
    // @DatabaseField(columnName = "video_anchor_info", foreign = true,
    // foreignAutoCreate = true, foreignAutoRefresh = true)
    public Anchor anchor;
    /**
     * 收藏状态
     */
    public String fav_status;
    /**
     * 点赞状态
     */
    public String top_status;
    /**
     * 抽奖信息
     */
    public VideoIottery lottery;
    /**
     * 游戏名称
     */
    public String game_title;
    /**
     * 视频所属主播的name
     */
    public String anchor_name;
    /**
     * 抽奖状态
     */
    public String lottery_status;
    /**
     * 视频播放的最后位置
     */
    public long video_last_progress;
    public String video_last_time;

    /**
     * 视频的下载进度
     */
    @DatabaseField(columnName = "video_download_progress")
    public String video_download_progress = "0";
    /**
     * 视频下载状态 （下载中和下载完成）
     * <p/>
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
     * 下载速度
     */
    @DatabaseField(columnName = "video_speed")
    public String video_speed = "0KB/S";

    /**
     * 下载视频选择的清晰度
     */
    @DatabaseField(columnName = "video_download_stream")
    public String video_download_stream;

    /**
     * 表示下载状态的小图标
     */
    public int video_download_Img = R.drawable.icon_choose_gray;
    /**
     * 视频格式集合
     */
    public VideoStreamsizes stream_size;
    /**
     * 投票信息
     */
    public VoteDetail vote;
    /**
     * 不知道什么意思(新接口添加的字段)
     */
    public String code;
    /**
     * 不知道什么意思(新接口添加的字段)
     */
    public String cate_id;
    /**
     * 怎么又多个anchor_id(新接口添加的字段)
     */
    public String anchor_id;
    /**
     * 视频评论列表
     */
    public List<Comment> comment_list;
    /**
     * 相关视频推荐
     */
    public List<Video> relate_video_list;
    /**
     * 图标的显示
     */
    public IconShow icon_show;
    /**
     * 视频的大小
     */
    public int size;
}

package com.fxtv.threebears.model;

import com.fxtv.threebears.R;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "tb_video_cache")
public class VideoCache implements Serializable{
    @DatabaseField(columnName = "vid", id = true)
    public String vid;
    @DatabaseField(columnName = "title")
    public String title;
    @DatabaseField(columnName = "image")
    public String image;
    @DatabaseField(columnName = "url")
    public String url;
    @DatabaseField(columnName = "duration")
    public String duration;
    @DatabaseField(columnName = "net_url")
    public String net_url;
    @DatabaseField(columnName = "size")
    public String size;
    //下载速度
    @DatabaseField(columnName = "speed")
    public int speed;
    //下载百分比
    @DatabaseField(columnName = "percentage")
    public int percentage;
    @DatabaseField(columnName = "status")
    public int status;
    /**
     * 清晰度 0:超清  1:高清  2:标清  3:流畅
     */
    @DatabaseField(columnName = "definition")
    public int definition;
    /**
     * 地址源  0:mobile  1:pc
     */
    @DatabaseField(columnName = "source")
    public int source;
    @DatabaseField(columnName = "failureReason")
    public String failureReason;
    @DatabaseField(columnName = "downloadPath")
    public String downloadPath;
}

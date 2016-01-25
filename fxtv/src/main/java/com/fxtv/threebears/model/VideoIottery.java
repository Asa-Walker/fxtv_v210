package com.fxtv.threebears.model;

import java.io.Serializable;
import java.util.List;

public class VideoIottery implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 是否参与抽奖
	 */
	public String is_join;
	/**
	 * 抽奖状态
	 */
	public String status;
	/**
	 * 开奖时间
	 */
	public String start_time;
	/**
	 * 奖品
	 */
	public String prize;
	/**
	 * 中奖用户名称
	 */
	public String winners;

	@Override
	public String toString() {
		return "VideoIottery [is_join=" + is_join + ", status=" + status + ", start_time="
				+ start_time + ", prize=" + prize + ", winners=" + winners + "]";
	}

	/**
	 * 参见抽奖的人数
	 */
	public int join_num;
	
	/**
	 * 抽奖结束的时间
	 */
	public String end_time;
	
	
	public int join_percent;
	
	/**
	 * 奖品集合
	 */
	public List<Prize> prize_list;
	
}

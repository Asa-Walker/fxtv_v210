package com.fxtv.threebears.model;

import java.io.Serializable;
import java.util.List;

public class VoteDetail implements Serializable {
	/**
	 * 投票id
	 */
	public String id;
	/**
	 * 主播id
	 */
	public String anchor_id;
	public String vote_aid;
	/**
	 * 投票标题
	 */
	public String title;
	/**
	 * 投票开始时间
	 */
	public String start_time;
	/**
	 * 投票结束时间
	 */
	public String end_time;
	/**
	 * 投票创建时间
	 */
	public String create_time;
	/**
	 * 日，什么东西(新添加的接口字段)
	 */
	public String is_open;
	/**
	 * 用户投票状态
	 */
	public String user_vote_status;
	public String vote_open;
	public String vote_flag;
	/**
	 * 投票数
	 */
	public String vote_count;
	public List<VoteItem> option_list;
	
	/**
	 * 是否投过票
	 */
	public String has_vote;
}

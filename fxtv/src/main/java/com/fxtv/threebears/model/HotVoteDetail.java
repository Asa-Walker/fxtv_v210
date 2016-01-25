package com.fxtv.threebears.model;

import java.util.List;

/**
 * 热点投票的投票详情
 * 
 * @author Android2
 * 
 */
public class HotVoteDetail {
	/**
	 * 投票的id
	 */
	public String id;
	/**
	 * 投票的标题
	 */
	public String title;
	/**
	 * 投票的 人数
	 */
	public long vote_count;
	/**
	 * 是否投过票(1--已投票 0--未投票)
	 */
	public String has_vote;
	
	/**
	 * 图片地址
	 */
	public String image;
	/**
	 * 选项
	 */
	public List<VoteItem> option_list;
	@Override
	public String toString() {
		return "HotVoteDetail [id=" + id + ", title=" + title + ", vote_count=" + vote_count + ", has_vote=" + has_vote
				+ ", option_list=" + option_list + "]";
	}
}

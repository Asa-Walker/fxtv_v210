package com.fxtv.threebears.model;

import java.io.Serializable;

import com.fxtv.threebears.R;

public class VoteItem implements Serializable {
	@Override
	public String toString() {
		return "VoteItem [id=" + id + ", title=" + title + ", option_num=" + option_num + ", option_percent="
				+ option_percent + ", has_vote_option=" + has_vote_option + ", isShown=" + isShown + "]";
	}
	/**
	 * 投票选项id
	 */
	public String id;
	/**
	 * 投票选项名称
	 */
	public String title;
	/**
	 * 投票数
	 */
	public String option_num;
	/**
	 * 投票的百分比
	 */
	public String option_percent;
	/**
	 * 投票状态（该用户是否已经投票）
	 */
	public String has_vote_option;
	/**
	 * 是否显示选中的图标
	 */
	public boolean isShown=false;

	/**
	 * 图片
	 */
	public String image;
}

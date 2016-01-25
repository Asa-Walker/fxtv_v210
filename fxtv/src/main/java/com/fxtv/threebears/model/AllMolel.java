package com.fxtv.threebears.model;

import java.util.List;

/**
 * 全部主播空间的对象
 * @author Android2
 *
 */
public class AllMolel {
	/**
	 * 非推荐的全部主播
	 */
	public List<Anchor> all;
	
	/**
	 * 推荐的主播
	 */
	public List<Anchor> recom;
	
	public long time;
}

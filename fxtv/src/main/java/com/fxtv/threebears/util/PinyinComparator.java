package com.fxtv.threebears.util;

import java.util.Comparator;

import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.view.sortListView.SortModel;

/**
 * 
 * @author xiaanming
 * 
 */
public class PinyinComparator implements Comparator<Anchor> {

	public int compare(Anchor o1, Anchor o2) {
		if (o1.anchor_first_name.equals("@") || o2.anchor_first_name.equals("荐")) {
			return -1;
		} else if (o1.anchor_first_name.equals("荐") || o2.anchor_first_name.equals("@")) {
			return 2;
		}
		if (o1.anchor_first_name.equals("@") || o2.anchor_first_name.equals("#")) {
			return -1;
		} else if (o1.anchor_first_name.equals("#") || o2.anchor_first_name.equals("@")) {
			return 1;
		} else {
			return o1.anchor_first_name.compareTo(o2.anchor_first_name);
		}
	}

}

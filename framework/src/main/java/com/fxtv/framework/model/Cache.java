package com.fxtv.framework.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_cache")
public class Cache {
	@DatabaseField(id = true)
	public String key;
	@DatabaseField(columnName = "value")
	public String value;
	@DatabaseField(columnName = "time")
	public long time;
	
	
	public Cache() {
	}

	public Cache(String key, String value) {
		this.key = key;
		this.value = value;
		this.time = System.currentTimeMillis();
	}
}

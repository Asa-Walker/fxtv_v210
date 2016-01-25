package com.fxtv.framework.frame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.fxtv.framework.Logger;
import com.fxtv.framework.model.Cache;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelperFramework extends OrmLiteSqliteOpenHelper {
	private static final String TAG = "DatabaseHelper_framework";
	private static final String TABLE_NAME = "fxtv-framework.db";
	private static DatabaseHelperFramework instance;
	private Map<String, Dao> daos;

	private DatabaseHelperFramework(Context context) {
		super(context, TABLE_NAME, null, 210);
		Logger.d(TAG, "DatabaseHelperFramework()");
		daos = new HashMap<String, Dao>();
	}

	/**
	 * 单例获取该Helper
	 * 
	 * @param context
	 * @return
	 */
	public static synchronized DatabaseHelperFramework getHelper(Context context) {
		if (instance == null) {
			synchronized (DatabaseHelperFramework.class) {
				instance = new DatabaseHelperFramework(context);
			}
		}

		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		Logger.d(TAG,"onCreate");
		try {
			TableUtils.createTable(connectionSource, Cache.class);
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.e(TAG,"onCreate,msg="+e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, Cache.class, true);
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public synchronized Dao getDao(Class clazz) throws SQLException {
		Dao dao = null;
		String className = clazz.getSimpleName();

		if (daos.containsKey(className)) {
			dao = daos.get(className);
		}
		if (dao == null) {
			dao = super.getDao(clazz);
			daos.put(className, dao);
		}
		return dao;
	}

	/**
	 * 释放资源
	 */
	@Override
	public void close() {
		super.close();

		for (String key : daos.keySet()) {
			Dao dao = daos.get(key);
			if (dao != null) {
				dao.clearObjectCache();
			}
		}
		daos.clear();
		daos = null;
		instance = null;
	}
}

package com.fxtv.threebears.romlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.fxtv.framework.Logger;
import com.fxtv.threebears.model.User;
import com.fxtv.threebears.model.UserRecoder;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.model.VideoCache;
import com.fxtv.threebears.model.VideoOld;
import com.fxtv.threebears.system.SystemUser;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "DatabaseHelper_fxtv";
    private static final String TABLE_NAME = "sqlite-fxtv.db";
    private static DatabaseHelper instance;
    private Map<String, Dao> daos;

    private DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 210);
        daos = new HashMap<String, Dao>();
    }

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                instance = new DatabaseHelper(context);
            }
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Logger.d(TAG, "onCreate");
            TableUtils.createTableIfNotExists(connectionSource, VideoCache.class);
            TableUtils.createTableIfNotExists(connectionSource, UserRecoder.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            List<Video> old200List = null;
            List<VideoOld> old133List = null;
            if(oldVersion == 133){
                old133List = getDao(VideoOld.class).queryForAll();
            }

            if(oldVersion == 200){
                old200List = getDao(Video.class).queryForAll();
            }

//            TableUtils.dropTable(connectionSource, UserRecoder.class, true);
            onCreate(database, connectionSource);


            if(old133List != null && old133List.size() != 0){
                Dao dao = getDao(VideoCache.class);
                for (VideoOld v :
                        old133List) {
                    VideoCache cache = new VideoCache();
                    cache.title = v.video_title;
                    cache.duration = v.video_duration;
                    cache.url = v.video_m3u8_mp4;
                    cache.vid = v.video_id;
                    cache.status = Integer.parseInt(v.video_download_state);
                    cache.definition = 2;
                    cache.failureReason = "";
                    cache.image = "";
                    cache.percentage = Integer.parseInt(v.video_download_progress);
                    cache.net_url = v.video_m3u8_mp4;
                    cache.size = v.video_download_size+"";
                    cache.source = 0;
                    cache.status = Integer.parseInt(v.video_download_state);
                    cache.downloadPath = v.video_m3u8_mp4.replace(v.video_id,"");
                    cache.image = v.video_image;
                    dao.createIfNotExists(cache);
                }
            }

            if(old200List != null && old200List.size() != 0){
                Dao dao = getDao(VideoCache.class);
                for (Video v :
                        old200List) {
                    VideoCache cache = new VideoCache();
                    cache.title = v.title;
                    cache.duration = v.duration;
                    cache.url = v.url;
                    cache.vid = v.id;
                    cache.status = Integer.parseInt(v.video_download_state);
                    cache.definition = 2;
                    cache.failureReason = "";
                    cache.image = "";
                    cache.percentage = Integer.parseInt(v.video_download_progress);
                    cache.net_url = v.url;
                    cache.size = v.video_download_size+"";
                    cache.source = 0;
                    cache.status = Integer.parseInt(v.video_download_state);
                    cache.downloadPath = v.url.replace(v.id,"");
                    cache.image = v.image;
                    dao.createIfNotExists(cache);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();

        if(daos == null){
            daos = new HashMap<String, Dao>();
        }

        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    @Override
    public <D extends RuntimeExceptionDao<T, ?>, T> D getRuntimeExceptionDao(Class<T> clazz) {
        return super.getRuntimeExceptionDao(clazz);
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao.clearObjectCache();
        }
        daos.clear();
        daos = null;
        instance = null;
    }

    /**
     * -----------------用户相关----------------------
     */
    public void updateAccountInfoCache(int type, String json, String arg1, String arg2) {
        Logger.d(TAG, "updateAccountInfoCache,type=" + type + ",arg1=" + arg1 + ",arg2=" + arg2);
        try {
            removeDefaultUserRecode();
            Dao dao = getDao(UserRecoder.class);
            QueryBuilder builder = dao.queryBuilder();
            if (type == SystemUser.LOGIN_TYPE_NORMAL) {
                builder.where().eq("userName", arg1).and().eq("passWord", arg2).and().eq("loginType", type);
            } else if (type == SystemUser.LOGIN_TYPE_MESSAGE) {
                builder.where().eq("userName", arg1).and().eq("loginType", type);
            } else if (type == SystemUser.LOGIN_TYPE_QQ || type == SystemUser.LOGIN_TYPE_SINA
                    || type == SystemUser.LOGIN_TYPE_WECHAT) {
                builder.where().eq("loginType", type).and().eq("thirdLoginId", arg1);
            } else {
                Logger.d(TAG, "updateAccountInfoCache,not find the type is " + type);
                return;
            }
            UserRecoder userRecode = (UserRecoder) builder.queryForFirst();
            if (userRecode == null) {
                userRecode = new UserRecoder();
                if (type == SystemUser.LOGIN_TYPE_NORMAL) {
                    userRecode.userName = arg1;
                    userRecode.passWord = arg2;
                } else if (type == SystemUser.LOGIN_TYPE_MESSAGE) {
                    userRecode.userName = arg1;
                } else if (type == SystemUser.LOGIN_TYPE_QQ || type == SystemUser.LOGIN_TYPE_SINA
                        || type == SystemUser.LOGIN_TYPE_WECHAT) {
                    userRecode.thirdLoginId = arg1;
                }
                userRecode.logout = false;
                userRecode.loginType = type;
            }
            userRecode.content = json;
            userRecode.defaultUser = true;
            dao.createOrUpdate(userRecode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除默认用户
     *
     * @return
     */
    public void removeDefaultUserRecode() {
        try {
            Dao dao = getDao(UserRecoder.class);
            UserRecoder defaultUserRecode = getDefaultUserRecode();
            if (defaultUserRecode != null) {
                defaultUserRecode.defaultUser = false;
                dao.createOrUpdate(defaultUserRecode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得默认用户
     *
     * @return
     */
    public UserRecoder getDefaultUserRecode() {
        try {
            Dao dao = getDao(UserRecoder.class);
            QueryBuilder builder = dao.queryBuilder();
            UserRecoder defaultUserRecode = (UserRecoder) builder.where().eq("defaultUser", true).queryForFirst();
            return defaultUserRecode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateNativeUserInfoCache(User user) {
        Gson gson = new Gson();
        String comtemt = gson.toJson(user);
        UserRecoder defaultUserRecode = getDefaultUserRecode();
        if (defaultUserRecode != null) {
            try {
                Dao dao = getDao(UserRecoder.class);
                defaultUserRecode.content = comtemt;
                dao.createOrUpdate(defaultUserRecode);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}

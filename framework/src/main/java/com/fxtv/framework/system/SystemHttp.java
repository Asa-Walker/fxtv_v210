package com.fxtv.framework.system;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.Cache;
import com.fxtv.framework.model.HttpCode;
import com.fxtv.framework.model.RequestHead;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

/**
 * http接口系统
 *
 * @author FXTV-Android
 */
public class SystemHttp extends SystemBase {
    private static final String TAG_RESPONSE = "http_response";
    private static final String TAG_REQUEST = "http_request";
    private static final String MODEL_CACHE_UPDATE = "bg_up_cache";
    private static final String MODEL_NORNAL = "normal_net";
    private AsyncHttpClient mClient;
    private SyncHttpClient mSyncHttpClient;
    @Override
    protected void init() {
        super.init();
        //连接数10，连接超时10s,重试次数5
        mClient = new AsyncHttpClient();
    }

    @Override
    protected void destroy() {
        super.destroy();
        if (mClient != null) {
            mClient.cancelAllRequests(true);
            mClient = null;
        }

        if (mSyncHttpClient != null) {
            mSyncHttpClient.cancelAllRequests(true);
            mSyncHttpClient = null;
        }
    }

    public AsyncHttpClient getAsyncHttpClient() {
        return mClient;
    }

    public SyncHttpClient getSyncHttpClient() {
        if (mSyncHttpClient == null) {
            mSyncHttpClient = new SyncHttpClient();
        }
        return mSyncHttpClient;
    }

    public void get(final Context context, final String url, final String apiForlog,
                    final AsyncHttpResponseHandler callBack) {
        Logger.d(TAG_RESPONSE, apiForlog + "\t url: \t" + url);
        mClient.get(context, url, callBack);
    }

    /**
     * 获取数据
     *  @param context   上下文
     * @param url       URL
     * @param useCache  是否可以使用缓存：true：可以使用缓存，false：不可以使用缓存
     * @param cacheDisc 是否缓存到本地
     * @param callBack  回调接口
     */
    public <T> void get(final Context context, final String url, final String apiForlog, boolean useCache,
                     boolean cacheDisc, final RequestCallBack<T> callBack) {
        Logger.d(TAG_REQUEST, apiForlog + ",url=" + url);
        if (useCache) {
            Cache cache = SystemManager.getInstance().getSystem(SystemHttpCache.class).getCache(url);
            if (cache != null) {
                if (callBack != null) {
                    Logger.d(TAG_RESPONSE, apiForlog + "\t from cache:" + cache.value);

                    final Response<T> resp=getResponse(cache.value, callBack);
                    resp.fromCache=true;
                    callBack.onSuccess(resp.data, resp);
                    callBack.onComplete();
                }
                if (FrameworkUtils.isNetworkConnected(context)) {
                    long tmp = System.currentTimeMillis() - cache.time;
                    if (FrameworkUtils.isMobileConnected(context)) {
                        // 数据流量
                        if (tmp > SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mHttpCacheGprsPastTime) {
                            Logger.d(TAG_RESPONSE, apiForlog + "\t 流量环境,缓存过期,后台更新缓存....");
                            netGet(context, url, apiForlog, MODEL_CACHE_UPDATE, cacheDisc, null);
                        }
                    } else if (FrameworkUtils.isWifiConnected(context)) {
                        // wifi
                        if (tmp > SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mHttpCacheWifiPastTime) {
                            Logger.d(TAG_RESPONSE, apiForlog + "\t wifi环境,缓存过期,后台更新缓存....");
                            netGet(context, url, apiForlog, MODEL_CACHE_UPDATE, cacheDisc, null);
                        }
                    }
                }
            } else {
                netGet(context, url, apiForlog, MODEL_NORNAL, cacheDisc, callBack);
            }
        } else {
            netGet(context, url, apiForlog, MODEL_NORNAL, cacheDisc, callBack);
        }
    }

    /**
     * 无apiForlog请求，
     * @param context
     * @param url
     * @param useCache
     * @param cacheDisc
     * @param callBack
     * @param <T>
     */
    public <T> void get(final Context context, final String url, boolean useCache,
                        boolean cacheDisc, final RequestCallBack<T> callBack) {
        get(context, url, "", useCache, cacheDisc, callBack);
    }
    public <T> void get(final Context context, final String url,final RequestCallBack<T> callBack){
        get(context, url, "", false, false, callBack);
    }
    public <T> void get(final Context context, final String url, final String apiForlog, final RequestCallBack<T> callBack){
        get(context, url, apiForlog, false, false, callBack);
    }

    /**
     * 网络获取数据
     *
     * @param context    上下文
     * @param url        请求URL
     * @param api        api函数
     * @param model      获取数据的模式：1、bg_up_cache 后台更新缓存 ,2、normal_net 正常联网获取数据
     * @param cacheValve 缓存阀门，true：缓存到本地，false：不缓存到本地
     * @param callBack   回调接口
     */
    private <T> void netGet(final Context context, final String url, final String api, final String model,
                        final boolean cacheValve, final RequestCallBack<T> callBack) {

        if (!FrameworkUtils.isNetworkConnected(context) && callBack != null) {
            Response<T> resp=new Response<>();
            resp.fromCache=false;
            resp.code= HttpCode.NO_NETWORK;
            resp.msg="网络未连接";
            callBack.onFailure(resp);
            callBack.onComplete();
            return;
        }
        mClient.get(context, url, new AsyncHttpResponseHandler(Looper.getMainLooper()) {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                String responseStr = new String(bytes);
                Response<T> resp = getResponse(responseStr, callBack);
                Logger.d(TAG_RESPONSE, api + "\t from netGet," + model + " onSuccess(),response=" + responseStr);
                try {
                    resp.fromCache = false;
                    if (resp.code == HttpCode.SUECCSS || resp.code == 201) {
                        if (callBack != null) {
                            callBack.onSuccess(resp.data, resp);
                        }
                        if (cacheValve) { // 缓存数据
                            SystemManager.getInstance().getSystem(SystemHttpCache.class).updateCache(url, responseStr);
                        }
                    } else {
                        Logger.e(TAG_RESPONSE, api + "\t from netGet," + model + "onSuccess(),code isn't 2000,error="
                                + resp.msg);
                        if (callBack != null)
                            callBack.onFailure(resp);
                    }
                } catch (Exception e) {
                    String errorMsg = api + "\t from netGet," + model + "Error of root json string to json object!";
                    Logger.e(TAG_RESPONSE, errorMsg);
                    e.printStackTrace();
                    if (callBack != null)
                        callBack.onFailure(resp);
                } finally {
                    if (callBack != null) {
                        callBack.onComplete();
                    }
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Logger.e(TAG_RESPONSE, api + "\t from netGet," + "onFailure(),error=" + throwable.getMessage());
                if (callBack != null) {
                    Response<T> resp = new Response<>();
                    resp.msg = throwable.getMessage();
                    callBack.onFailure(resp);
                    callBack.onComplete();
                }
            }


        });
    }

    /**
     * 解析 Respjson 转为Response<T>
     * @param respJson
     * @param callBack
     * @param <T>
     * @return
     */
    public <T> Response<T> getResponse(String respJson,RequestCallBack<T> callBack){
        try {
            JSONObject respObj=new JSONObject(respJson);
            Response<T> resp=new Response<>();

            resp.code = respObj.getInt("code");
            resp.msg = respObj.getString("message");
            resp.time = respObj.getLong("time");

            String data=respObj.getString("data");
            if (callBack!=null && callBack.respType != null && callBack.respType!=String.class) {
                resp.data=new Gson().fromJson(data, callBack.respType);
            }else{
                resp.data=(T)data;
            }
            return resp;
        }catch (Exception e){
            Logger.e(TAG_RESPONSE,"gsonResp异常:"+respJson+" \n"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void cancelRequest(Context context, boolean mayInterruptIfRunning) {
        mClient.cancelRequests(context, mayInterruptIfRunning);
    }



    /**
     * 拼接uri
     * @return
     */
    public String processUrl(RequestHead head) {
        StringBuilder builder = new StringBuilder(head.uri);
        builder.append(head.module);
        builder.append("/" + head.api);
        builder.append("?params=");
        String str = "{\"platform\":\"" + SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).platform + "\",\"version\":" + "\"" + SystemManager.getInstance().getSystem(SystemFrameworkConfig.class).mVersion + "\"";
        if (!TextUtils.isEmpty(head.uc)) {
            str += ",\"uc\":\"" + head.uc+"\"";
        }
        str += ",\"data\":%1$s}";
        String param = String.format(str, head.params.toString());
        builder.append(param);
        return builder.toString();
    }

}

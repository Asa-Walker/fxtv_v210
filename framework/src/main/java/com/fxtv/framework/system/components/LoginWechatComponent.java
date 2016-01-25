package com.fxtv.framework.system.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.SystemThirdPartyLogin;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2015/12/18.
 * 微信登录组件
 */
public class LoginWechatComponent extends BaseLoginComponent {
    public  static final String SHARE_OK="com.fxtv.framework.system.components.ok";
    public  static final String SHARE_CANCLE="com.fxtv.framework.system.components.cancle";
    public  static final String SHARE_FARIL="com.fxtv.framework.system.components.failer";
    public  static  final String WETCHAT_APPID="wxb41253954e305e63";
    private static final  String TAG="LoginWechatComponent";
    private static final String mSecret="61fd8fa7971964c5052ce1914ee4243b";
    private  Context mContext;
    private IWXAPI mApi;
    private SystemThirdPartyLogin.ICallBackSystemLogin mCallBack;
    public LoginWechatComponent(Context context,SystemThirdPartyLogin.ICallBackSystemLogin callBack){
        mContext=context;
        mApi= WXAPIFactory.createWXAPI(context, WETCHAT_APPID);
        mApi.registerApp(WETCHAT_APPID);
        mCallBack=callBack;
        initBroadcast();
    }

    /**
     * 注册接收广播
     */
    public void initBroadcast(){
        IntentFilter intenFilter=new IntentFilter();
        intenFilter.addAction(SHARE_OK);
        intenFilter.addAction(SHARE_CANCLE);
        intenFilter.addAction(SHARE_FARIL);
        mContext.registerReceiver(mWeChatReceiver, intenFilter);
    }

    /**
     * 接收广播
     */
    private BroadcastReceiver mWeChatReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case SHARE_OK:
                    String code= intent.getExtras().getString("code");
                    if (mCallBack!=null){
                        Logger.d(TAG,"Wechat login onSuccess");
                        if (code!=null&&!code.equals("")){
                            Logger.i(TAG, "Code:" + code);
                            getOpenid(code);
                        }else {
                            Logger.i(TAG,"Code is null");
                        }
                    }
                    break;
                case SHARE_CANCLE:
                    if (mCallBack!=null){
                        Logger.d(TAG,"Wechat login onCancle");
                        mCallBack.onCancle();
                    }
                    break;
                case SHARE_FARIL:
                    if (mCallBack!=null){
                        Logger.d(TAG,"Wechat login onFailure");
                        mCallBack.onFailure("登录失败！");
                    }
                    break;
            }
        }
    };

    /**
     * 获取微信的openid
     * @param code
     */
    public void getOpenid(String code) {
        String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        String httpurl=url.replace("APPID",WETCHAT_APPID).replace("SECRET",mSecret).replace("CODE",code);
        final AsyncHttpClient httpClient= SystemManager.getInstance().getSystem(SystemHttp.class).getAsyncHttpClient();
        httpClient.get(mContext,httpurl,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Logger.d(TAG,"response"+response);
                 try {
                    String openid = response.getString("openid");
                     Logger.i(TAG,"openid"+openid);
                    if (openid != null && !openid.equals("")) {
                            if (mCallBack!=null){
                                mCallBack.onSuccess(openid);
                            }
                    }else {
                        Logger.i(TAG,"openid is null");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Logger.d(TAG,"errorResponse"+errorResponse);
            }
        });

    }
    /**
     * 注销接收广播
     */
    public  void unRegegister(){
        if (mWeChatReceiver!=null&&mContext!=null){
            mContext.unregisterReceiver(mWeChatReceiver);
        }
    }

    /**
     * 登录
     */
    @Override
    public void Login() {
        super.Login();
        if (!mApi.isWXAppInstalled()) {
            FrameworkUtils.showToast(mContext,"您还未安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "diandi_wx_login";
        mApi.sendReq(req);
    }

    @Override
    public void destory() {
        super.destory();
        unRegegister();
        if (mContext!=null){
            mContext=null;
        }
        if (mApi!=null){
           mApi=null;
        }
        if (mCallBack!=null){
            mCallBack=null;
        }
    }
}

package com.fxtv.framework.system.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.net.MalformedURLException;

/**
 * Created by Administrator on 2015/12/14.
 */
public class ShareWeChatComponent extends BaseShareComponent{
    public  static  final String WETCHAT_APPID="wxb41253954e305e63";
    private IWXAPI mApi;
    private static final int THUMB_SIZE = 150;
    private ShareModel mShare;
    private  SystemShare.ICallBackSystemShare mCallBack;
    private  Context mContext;
    private String TAG="ShareWeChatComponent";
    public  static final String SHARE_OK="com.fxtv.framework.system.components.ok";
    public  static final String SHARE_CANCLE="com.fxtv.framework.system.components.cancle";
    public  static final String SHARE_FARIL="com.fxtv.framework.system.components.failer";
     android.os.Handler mHandler=new android.os.Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           Bitmap bitmap=(Bitmap)msg.obj;
           shareWeChat(mShare, bitmap);
       }
    };

    public ShareWeChatComponent(Context context, SystemShare.ICallBackSystemShare callBack){
        mApi= WXAPIFactory.createWXAPI(context, WETCHAT_APPID);
        mApi.registerApp(WETCHAT_APPID);
        mContext=context;
        mCallBack=callBack;
        initBroadcast();
    }
     private BroadcastReceiver mWeChatReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case SHARE_OK:
                    if (mCallBack!=null){
                        Logger.d(TAG, "Wechat share onSuccess");
                        mCallBack.onSuccess();
                    }
                    break;
                case SHARE_CANCLE:
                    if (mCallBack!=null){
                        Logger.d(TAG,"Wechat share onCancle");
                        mCallBack.onCancle();
                    }
                    break;
                case SHARE_FARIL:
                    if (mCallBack!=null){
                        Logger.d(TAG,"Wechat share onFailure");
                        mCallBack.onFailure("分享失败！");
                    }
                    break;
            }


        }
    };
    public void initBroadcast(){
        IntentFilter intenFilter=new IntentFilter();
        intenFilter.addAction(SHARE_OK);
        intenFilter.addAction(SHARE_CANCLE);
        intenFilter.addAction(SHARE_FARIL);
        mContext.registerReceiver(mWeChatReceiver, intenFilter);
    }
    public void shareWeChat(final ShareModel model,final Bitmap bitmap) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = model.shareUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title =model.shareTitle;
        msg.description =model.shareSummary;
        if (bitmap!=null){
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
            bitmap.recycle();
            msg.setThumbImage(thumbBmp);
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        mApi.sendReq(req);
    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

    @Override
    public void share(final ShareModel model) {
        if (!mApi.isWXAppInstalled()) {
            FrameworkUtils.showToast(mContext,"您还未安装微信客户端");
            return;
        }
        if (model!=null){
            mShare=model;
        }
        new Thread(){
            @Override
            public void run() {
                super.run();
                Message msg=new Message();
                Bitmap bitmap=null;
                try {
                    bitmap = BitmapFactory.decodeStream(FrameworkUtils.getImageByte(model.fileImageUrl));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (bitmap!=null){
                    msg.obj=bitmap;
                }
                mHandler.handleMessage(msg);
            }
        }.start();
    }
    public  void unRegegister(){
        if (mWeChatReceiver!=null&&mContext!=null){
            mContext.unregisterReceiver(mWeChatReceiver);
        }
    }

    @Override
    public void destory() {
        super.destory();
        unRegegister();
        if (mShare!=null){
            mShare=null;
        }
        if (mCallBack!=null){
            mCallBack=null;
        }
        if (mApi!=null){
            mApi=null;
        }

    }
}

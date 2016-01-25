package com.fxtv.threebears.system;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.SystemImageLoader;
import com.fxtv.framework.system.SystemPage;
import com.fxtv.framework.system.SystemShare;
import com.fxtv.framework.system.SystemVersionUpgrade;
import com.fxtv.framework.system.SystemVersionUpgrade.IApkUpgradeCallBack;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.MainActivity;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.activity.explorer.ActivityExplorerAnchorCircle;
import com.fxtv.threebears.activity.explorer.ActivityExplorerTask;
import com.fxtv.threebears.activity.explorer.ActivityMissionDetail;
import com.fxtv.threebears.activity.explorer.ActivityQRCode;
import com.fxtv.threebears.activity.explorer.ActivityRankList;
import com.fxtv.threebears.activity.explorer.ActivityTopicInfo;
import com.fxtv.threebears.activity.explorer.ActivityVoteDetail;
import com.fxtv.threebears.activity.game.ActivityGame;
import com.fxtv.threebears.activity.h5.ActivityWebView;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.activity.user.login.ActivityLogin;
import com.fxtv.threebears.model.Game;
import com.fxtv.threebears.model.StreamSize;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.service.ServiceMessage;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.DownloadDialog;
import com.fxtv.threebears.view.MyDialog;
import com.fxtv.threebears.view.MyDialog.OnClickListener;
import com.fxtv.threebears.view.ShareDialog;
import com.fxtv.threebears.view.SharePopuWindow;
import com.fxtv.threebears.view.banner.BannerData;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;

/**
 * @author FXTV-Android
 *         <p/>
 *         程序公用代码块
 */
public class SystemCommon extends SystemBase {
    private static final String TAG = "SystemCommon";

    public static final int DEFUAT = 0, BANNER = 1, SQUARE = 2, ADVERTISE = 3, ROUNDED = 4, SHOP = 5;


    public int mScreenWidth, mScreenHeight;

    @Override
    protected void init() {
        super.init();
        mScreenWidth = FrameworkUtils.getScreenWidth(mContext);
        mScreenHeight = FrameworkUtils.getScreenHeight(mContext);
    }

    @Override
    protected void destroy() {
        super.destroy();
    }

    /** --------------------业务逻辑相关-------------------- */
    /**
     * 提示用户未登录，并跳转登录界面
     *
     * @param activity
     */
    public void noticeAndLogin(Activity activity) {
        FrameworkUtils.showToast(activity, activity.getString(R.string.notice_no_login));
        FrameworkUtils.skipActivity(activity, ActivityLogin.class);
    }

    /**
     * show dialog,not title
     *
     * @param context
     * @param content
     * @param confirmListener
     * @param cancelListener
     */
    public void showDialog(Context context, String content, OnClickListener confirmListener,
                           OnClickListener cancelListener) {
        showDialog(context, null, content, confirmListener, cancelListener);
    }

    /**
     * show dialog not cancle btn.
     *
     * @param context
     * @param content
     * @param confirmListener
     */
    public void showDialog(Context context, String content, OnClickListener confirmListener) {
        showDialog(context, null, content, confirmListener, null);
    }

    /**
     * show dialog
     *
     * @param context
     * @param title
     * @param content
     * @param confirmListener
     * @param cancelListener
     */
    public void showDialog(Context context, String title, String content, OnClickListener confirmListener,
                           OnClickListener cancelListener) {
        MyDialog dialog = new MyDialog(context, title, content, confirmListener, cancelListener);
        dialog.show();
    }

    /**
     * 有editext的dialog
     *
     * @param context
     * @param context
     * @param confirmListener
     * @param cancelListener
     * @param hasEditText
     */
    public void showDialog(Context context, String title, OnClickListener confirmListener,
                           OnClickListener cancelListener, boolean hasEditText) {
        MyDialog dialog = new MyDialog(context, title, null, confirmListener, cancelListener, hasEditText);
        dialog.show();
    }


    public void showDownloadDialog(final Context context, final Video video, final ImageView imageView) {
        JsonObject params = new JsonObject();
        params.addProperty("id", video.id);
        Utils.showProgressDialog((Activity) context);
        String url = Utils.processUrl(ModuleType.BASE, ApiType.BASE_streamSizes, params);
        getSystem(SystemHttp.class).get(context, url, "searchAnchorApi", false, false, new RequestCallBack<List<StreamSize>>() {

            @Override
            public void onSuccess(List<StreamSize> data, Response resp) {
                if (data != null && data.size() != 0) {
                    DownloadDialog dialog = new DownloadDialog(context, video, data, new DownloadDialog.DowbloadCallBack() {
                        @Override
                        public void onDownLoadSuccess(boolean flag, String msg) {
                            FrameworkUtils.showToast(context, msg);
                            if (flag) {
                                imageView.setImageResource(R.drawable.icon_download1);
                            } else {

                            }
                        }
                    });
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Response resp) {
                FrameworkUtils.showToast(context, resp.msg);
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });
//        String url = processUrl("Base", "streamSizes", params);
//        SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url, "searchAnchorApi", false, false, callBack);
//        getSystemHttpRequests()
//                .getStreamSizes(context, params.toString(), new RequestCallBack2() {
//                    @Override
//                    public void onSuccess(String json, boolean fromCache, String msg) {
//                        Gson gson = new Gson();
//                        List<StreamSize> list = gson.fromJson(json, new TypeToken<List<StreamSize>>() {
//                        }.getType());
//                        if (list != null && list.size() != 0) {
//                            DownloadDialog dialog = new DownloadDialog(context, video, list, new DownloadDialog.DowbloadCallBack() {
//                                @Override
//                                public void onDownLoadSuccess(boolean flag, String msg) {
//                                    FrameworkUtils.showToast(context, msg);
//                                    if (flag) {
//                                        imageView.setImageResource(R.drawable.icon_download1);
//                                    }
//                                }
//                            });
//                            dialog.show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(String msg, boolean fromCache) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Utils.dismissProgressDialog();
//                    }
//                });
    }

    /**
     * 加经验和饼干
     *
     * @param json
     * @param context
     * @param value
     */
    public void addEXPAndBiscuits(String json, Context context, String value) {
        String showTip = "";
        String showGetExp = "";
        String showGetCurrency = "";
        try {
            JSONObject jobj = new JSONObject(json).getJSONObject("action_tip");
            showTip = jobj.getString("show_tip");
            showGetExp = jobj.getString("show_get_exp");
            showGetCurrency = jobj.getString("show_get_currency");
        } catch (Exception e) {
        }
        Logger.d(TAG, "shareVideo,run onSuccess");
        if (showTip.equals("")) {
            showTip = value;
        }
        if (!"".equals(showGetExp)) {
            getSystem(SystemUser.class).mUser.exp = (Long.parseLong(SystemManager
                    .getInstance().getSystem(SystemUser.class).mUser.exp) + Long.parseLong(showGetExp)) + "";
        }
        if (!"".equals(showGetCurrency)) {
            getSystem(SystemUser.class).mUser.currency = (Long
                    .parseLong(getSystem(SystemUser.class).mUser.currency) + Long
                    .parseLong(showGetCurrency))
                    + "";
        }
        FrameworkUtils.showToast(context, showTip);
    }

    public void checkNewMessage(Context context, final ServiceMessage.IMessageCallBack callBack) {
        if (getSystem(SystemUser.class).isLogin()) {
            JsonObject params = new JsonObject();
            params.addProperty("user_id", getSystem(SystemUser.class).mUser.user_id);
            String url = Utils.processUrl(ModuleType.USER, ApiType.USER_newMessage, params);
//            String url = processUrl("User", "newMessage", params);
            getSystem(SystemHttp.class).get(context, url, "checkNewMessageApi", false, false, new RequestCallBack<String>() {
                @Override
                public void onSuccess(String data, Response resp) {
                    try {
                        JSONObject jObject = new JSONObject(data);
                        String hasMessage = jObject.getString("new_message");
                        if (callBack != null) {
                            callBack.onHasMessage(hasMessage.equals("1"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Response resp) {

                }

                @Override
                public void onComplete() {

                }
            });
//            SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url, "checkNewMessageApi", false, false, callBack);
//            getSystemHttpRequests()
//                    .checkNewMessageApi(context, params.toString(), new RequestCallBack2() {
//                        @Override
//                        public void onSuccess(String json, boolean fromCache, String msg) {
//                            try {
//                                JSONObject jObject = new JSONObject(json);
//                                String hasMessage = jObject.getString("new_message");
//                                if (callBack != null) {
//                                    callBack.onHasMessage(hasMessage.equals("1") ? true : false);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(String msg, boolean fromCache) {
//                        }
//
//                        @Override
//                        public void onComplete() {
//                        }
//                    });
        }
    }

    public void checkVersion(final Context ctx, final boolean showLoading) {
        if (showLoading && ctx instanceof Activity) {
            Utils.showProgressDialog((Activity) ctx);
        }
        JsonObject params = new JsonObject();
//        String url = getSystemHttpRequests()
//                .processUrl("Base", "upgradeVersion", params.toString());
        String url = Utils.processUrl(ModuleType.BASE, ApiType.BASE_upgradeVersion, params);
        SystemManager.getInstance().getSystem(SystemVersionUpgrade.class).checkApkUpdate(url, new IApkUpgradeCallBack() {
            @Override
            public void onResult(boolean shouldUpgrade, boolean compulsive) {
                if (shouldUpgrade) {
                    showDialog(ctx, "版本更新", "有新版本喽", new OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog, View view, String value) {
                            SystemManager.getInstance().getSystem(SystemVersionUpgrade.class).upgradeApk();
                            dialog.dismiss();
                        }
                    }, new OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog, View view, String value) {
                            dialog.dismiss();
                        }
                    });
                } else if (showLoading) {//设置检查升级
                    showDialog(ctx, "提示", "已经是最新版本了", new OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog, View view, String value) {
                            dialog.dismiss();
                        }
                    }, null);
                }
                Utils.dismissProgressDialog();
            }

            @Override
            public void onError(String msg) {
                Utils.dismissProgressDialog();
            }
        });
    }

    /**
     * 获取UC
     *
     * @param type     0:logout,1:login,2:toggle
     * @param callBack
     */
    public void getUCCode(final int type, final boolean showDialog, final ISystemCommonCallBack callBack) {
        Logger.d(TAG, "getUCCode");
        JsonObject params = new JsonObject();
        String deviceId = FrameworkUtils.getDeviceId(mContext);
        if (TextUtils.isEmpty(deviceId)) {
            Random random = new Random();
            deviceId = System.currentTimeMillis() + "" + random.nextInt(10);
        }
        params.addProperty("device_id", deviceId);
        if (type == 1) {
            params.addProperty("user_id", getSystem(SystemUser.class).mUser.user_id);
        } else if (type == 2) {
            if (getSystem(SystemUser.class).isLogin()) {
                params.addProperty("user_id", getSystem(SystemUser.class).mUser.user_id);
            }
        } else {
            if (getSystem(SystemUser.class).isLogin()) {
                params.addProperty("user_id", getSystem(SystemUser.class).mUser.user_id);
            }
        }

        if (showDialog) {
            Utils.showProgressDialog(getCurrentActivity());
        }
        params.addProperty("name", android.os.Build.MODEL);
        params.addProperty("sys_version", android.os.Build.VERSION.RELEASE);
        params.addProperty("net_type", FrameworkUtils.getNetType(mContext));
        params.addProperty("channel_id", FrameworkUtils.getMetaData(mContext, "UMENG_CHANNEL"));
        params.addProperty("idfa", "");
        String url = Utils.processUrl(ModuleType.BASE, ApiType.BASE_getUC, params);
//        String url = processUrlForGetUC("Base", "getUC", params);
        getSystem(SystemHttp.class).get(mContext, url, "getUCCodeApi", false, false, new RequestCallBack<String>() {
            @Override
            public void onSuccess(String data, Response resp) {
                try {
                    JSONObject object = new JSONObject(data);
                    String uc = object.getString("uc");
                    if (TextUtils.isEmpty(uc)) {
                        if (callBack != null) {
                            callBack.onResult(false, "获取UC失败,无法进入程序,code=000");
                        }
                    } else {
                        if (type == 0) {
                            getSystem(SystemPreference.class).setUCLogout(uc);
                        } else if (type == 1) {
                            getSystem(SystemPreference.class).setUCLogin(uc);
                        } else {
                            if (getSystem(SystemUser.class).isLogin()) {
                                getSystem(SystemPreference.class).setUCLogin(uc);
                            } else {
                                getSystem(SystemPreference.class).setUCLogout(uc);
                            }
                        }

                        if (callBack != null) {
                            callBack.onResult(true, "获取UC成功");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e(TAG, "Error,getUCCode,resp=" + resp);
                    if (callBack != null) {
                        callBack.onResult(false, "获取UC失败,无法进入程序,code=001");
                    }
                }
            }

            @Override
            public void onFailure(Response resp) {
                if (callBack != null) {
                    callBack.onResult(false, resp.msg);
                }
            }

            @Override
            public void onComplete() {

            }
        });
//        SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url, "getUCCodeApi", false, false, callBack);
//        getSystemHttpRequests()
//                .getUCCodeApi(mContext, params.toString(), new RequestCallBack2() {
//                    @Override
//                    public void onSuccess(String json, boolean fromCache, String msg) {
//                        try {
//                            JSONObject object = new JSONObject(json);
//                            String uc = object.getString("uc");
//                            if (TextUtils.isEmpty(uc)) {
//                                if (callBack != null) {
//                                    callBack.onResult(false, "获取UC失败,无法进入程序,code=" + 000);
//                                }
//                            } else {
//                                if (type == 0) {
//                                    getSystem(SystemPreference.class).setUCLogout(uc);
//                                } else if (type == 1) {
//                                    getSystem(SystemPreference.class).setUCLogin(uc);
//                                } else {
//                                    if (getSystem(SystemUser.class).isLogin()) {
//                                        getSystem(SystemPreference.class).setUCLogin(uc);
//                                    } else {
//                                        getSystem(SystemPreference.class).setUCLogout(uc);
//                                    }
//                                }
//
//                                if (callBack != null) {
//                                    callBack.onResult(true, "获取UC成功");
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Logger.e(TAG, "Error,getUCCode,json=" + json);
//                            if (callBack != null) {
//                                callBack.onResult(false, "获取UC失败,无法进入程序,code=" + 001);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(String msg, boolean fromCache) {
//                        Logger.e(TAG, "Error,getUc,msg=" + msg);
//                        if (callBack != null) {
//                            callBack.onResult(false, msg);
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Utils.dismissProgressDialog();
//                    }
//                });
    }

    public JSONObject getH5Content(Context context) {
        JSONObject jObject = null;
        Intent i_getvalue = ((Activity) context).getIntent();
        String action = i_getvalue.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = i_getvalue.getData();
            String string = uri.toString();
            int index = string.indexOf("=");
            String str = string.substring(index + 1);
            str = str.replace("%22", "\"");
            try {
                jObject = new JSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jObject;
    }

    private Activity getCurrentActivity() {
        return SystemManager.getInstance().getSystem(SystemPage.class).getCurrActivity();
    }

    public interface ISystemCommonCallBack {
        void onResult(boolean result, String arg);
    }
    /** --------------------业务逻辑相关END-------------------- */

    /**
     * ---------------------Glide显示图片的几种方式START----------------
     */


    private int chooseDefaultImage(int type) {
        switch (type) {
            case DEFUAT:
                return R.drawable.default_img;
            case BANNER:
                return R.drawable.default_img_banner;
            case SQUARE:
                return R.drawable.default_img3;
            case ADVERTISE:
                return R.drawable.advertise;
            case ROUNDED:
                return R.drawable.default_img_banner;
            case SHOP:
                return R.drawable.anchor_store_image;
            default:
                return R.drawable.default_img;
        }
    }


    /**
     * 加载图片
     *
     * @param object--加载图片所在的Context(可以是Activity,FramentActivity,Fragment,Context)
     * @param image--要显示图片的额ImageView
     * @param url--图片的地址
     * @param type--(根据类型选择默认图片                                                    0----标准默认图片
     *                                                                             1----banner默认图片
     *                                                                             2----suqare默认图片
     *                                                                             3----advertise默认图片
     *                                                                             )
     * @param holderResouce--预加载显示的图片
     * @param errorResouce--加载出错显示的图片
     * @param emptyResouce--地址为空时显示的图片
     * @param radius--倒圆角的值
     */
    private void displayImage(Object object, ImageView image, String url, int type, int holderResouce, int errorResouce, int emptyResouce, int radius) {
        Resources resources = mContext.getResources();
        try {
            resources.getDrawable(holderResouce);
        } catch (Exception e) {
            holderResouce = chooseDefaultImage(type);
        }
        try {
            resources.getDrawable(errorResouce);
        } catch (Exception e) {
            errorResouce = chooseDefaultImage(type);
        }
        try {
            resources.getDrawable(emptyResouce);
        } catch (Exception e) {
            emptyResouce = chooseDefaultImage(type);
        } finally {
            SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImage(object, image, url, holderResouce, errorResouce, emptyResouce, radius);
        }
    }


    /**
     * 加载默认图片(无倒角)
     *
     * @param object--加载图片所在的Context(可以是Activity,FramentActivity,Fragment,Context)
     * @param image--要显示图片的额ImageView
     * @param url--图片的地址
     * @param type--(根据类型选择默认图片                                                    0----标准默认图片
     *                                                                             1----banner默认图片
     *                                                                             2----suqare默认图片
     *                                                                             3----advertise默认图片
     *                                                                             )
     * @param holderResouce--预加载显示的图片
     * @param errorResouce--加载出错显示的图片
     * @param emptyResouce--地址为空时显示的图片
     */
    public void displayDefaultImage(Object object, ImageView image, String url, int type, int holderResouce, int errorResouce, int emptyResouce) {
        displayImage(object, image, url, type, holderResouce, errorResouce, emptyResouce, 0);
    }

    /**
     * 加载有倒角的图片
     *
     * @param object--加载图片所在的Context(可以是Activity,FramentActivity,Fragment,Context)
     * @param image--要显示图片的额ImageView
     * @param url--图片的地址
     * @param type--(根据类型选择默认图片                                                    0----标准默认图片
     *                                                                             1----banner默认图片
     *                                                                             2----suqare默认图片
     *                                                                             3----advertise默认图片
     *                                                                             )
     * @param holderResouce--预加载显示的图片
     * @param errorResouce--加载出错显示的图片
     * @param emptyResouce--地址为空时显示的图片
     * @param radius--倒角的值
     */
    public void displayRoundedImage(Object object, ImageView image, String url, int type, int holderResouce, int errorResouce, int emptyResouce, int radius) {
        displayImage(object, image, url, type, holderResouce, errorResouce, emptyResouce, radius);
    }

    /**
     * 加载默认图片(无倒角)
     *
     * @param object--加载图片所在的Context(可以是Activity,FramentActivity,Fragment,Context)
     * @param image--要显示图片的额ImageView
     * @param url--图片的地址
     */
    public void displayDefaultImage(Object object, ImageView image, String url) {
        displayDefaultImage(object, image, url, DEFUAT, 0, 0, 0);
    }

    /**
     * 加载有倒角的图片
     *
     * @param object--加载图片所在的Context(可以是Activity,FramentActivity,Fragment,Context)
     * @param image--要显示图片的额ImageView
     * @param url--图片的地址
     * @param radius--圆角的值
     */
    public void displayRoundedImage(Object object, ImageView image, String url, int radius) {
        displayRoundedImage(object, image, url, BANNER, 0, 0, 0, radius);
    }

    /**
     * @param object--加载图片所在的Context(可以是Activity,FramentActivity,Fragment,Context)
     * @param image--要显示图片的额ImageView
     * @param url--图片的地址
     * @param type--种类
     * @param radius--圆角的值
     */
    public void displayRoundedImage(Object object, ImageView image, String url, int type, int radius) {
        displayRoundedImage(object, image, url, type, 0, 0, 0, radius);
    }

    public void displayDefaultImage(Object object, ImageView image, String url, int type) {
        displayDefaultImage(object, image, url, type, 0, 0, 0);
    }

    public void displayImageForAnchorCircle(Context context, ImageView image, String url) {
        SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageForAnchorCircle(context, image, url, R.drawable.default_img3, R.drawable.default_img3, R.drawable.default_img3);
    }


    /**
     * ---------------------Glide显示图片的几种方式END----------------
     */
    public void share(final Context context, final ShareModel model, final ViewGroup viewGroup) {
        final SharePopuWindow sharePopuWindow = new SharePopuWindow(context);
        sharePopuWindow.showPopWindow(viewGroup, model, new SystemShare.ICallBackSystemShare() {
            @Override
            public void onSuccess() {
                FrameworkUtils.showToast(mContext, "分享成功！");
                sharePopuWindow.onDestory();
                sharePopuWindow.dismiss();

            }

            @Override
            public void onFailure(String msg) {
                Logger.d(TAG, "onFailure:" + msg);
                FrameworkUtils.showToast(mContext, "分享失败！");
                sharePopuWindow.onDestory();
                sharePopuWindow.dismiss();
            }

            @Override
            public void onCancle() {
                FrameworkUtils.showToast(mContext, "取消分享！");
                sharePopuWindow.onDestory();
                sharePopuWindow.dismiss();
            }
        });
    }

    public void jump(Context context, BannerData data) {
        if (data == null || TextUtils.isEmpty(data.type)) {
            return;
        }
        int type = Integer.parseInt(data.type);
        Bundle bundle = null;
        switch (type) {
            case 1:
                // 视频播放页
                bundle = new Bundle();
                bundle.putString("video_id", data.link);
                bundle.putString("skipType", "12");
                FrameworkUtils.skipActivity(context, ActivityVideoPlay.class, bundle);
                break;
            case 3:
                // 主播空间页
                bundle = new Bundle();
                bundle.putString("anchor_id", data.link);
                FrameworkUtils.skipActivity(context, ActivityAnchorZone.class, bundle);
                break;
            case 4:
                // 我的页
                ((MainActivity) mContext).jump2Child(4);
                break;
            case 5:
                // 每日任务
                FrameworkUtils.skipActivity(context, ActivityExplorerTask.class);
                break;
            case 6:
                // 活动详情页
                bundle = new Bundle();
                bundle.putString("activity_id", data.link);
                FrameworkUtils.skipActivity(context, ActivityMissionDetail.class, bundle);
                break;
            case 7:
                // 游戏内页
                Game game = new Game();
                game.id = data.link;
                // game.title = missionCenter.game_name;
                // game.game_type = missionCenter.game_type;
                bundle = new Bundle();
                bundle.putSerializable("game", game);
                FrameworkUtils.skipActivity(context, ActivityGame.class, bundle);
                break;
            case 8:
                // H5页面
                bundle = new Bundle();
                bundle.putString("url", data.link);
                bundle.putString("title", "WebView");
                bundle.putString("share_img", data.image);
                bundle.putString("share_title", data.title);
                bundle.putBoolean("share_enable", true);
                FrameworkUtils.skipActivity(context, ActivityWebView.class, bundle);
                break;
            //排行榜
            case 10:
                FrameworkUtils.skipActivity(context, ActivityRankList.class);
                break;
            case 11:
                // 热点投票
                bundle = new Bundle();
                bundle.putString("id", data.link);
                FrameworkUtils.skipActivity(context, ActivityVoteDetail.class, bundle);
                break;
            case 12:
                // 扫一扫
                FrameworkUtils.skipActivity(context, ActivityQRCode.class);
                break;
            case 13:
                // 主播圈
                FrameworkUtils.skipActivity(context, ActivityExplorerAnchorCircle.class);
                break;
            //热聊话题--吐槽界面
            case 14:
                bundle = new Bundle();
                bundle.putString("id", data.link);
                FrameworkUtils.skipActivity(context, ActivityTopicInfo.class, bundle);
                break;
            //热聊话题--吐槽界面(暂定)
            case 18:
                bundle = new Bundle();
                bundle.putString("id", data.link);
                FrameworkUtils.skipActivity(context, ActivityTopicInfo.class, bundle);
                break;
            default:
                break;

        }
    }


    //-----------------------------shareDialog----------------------//

    public void showShareDialog(Context context, ShareModel shareModel, ShareDialog.ShareCallBack callback) {
        ShareDialog shareDialog = new ShareDialog(context, shareModel, callback);
        shareDialog.show();
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        android.view.WindowManager.LayoutParams p = shareDialog.getWindow().getAttributes();  //获取对话框当前的参数值
//p.height = (int) (d.getHeight() * 0.3);   //高度设置为屏幕的0.3
        Point point = new Point();
        d.getSize(point);
        p.width = point.x;    //宽度设置为全屏
        p.gravity = Gravity.BOTTOM;
        shareDialog.getWindow().setAttributes(p);     //设置生效
    }

    public boolean isNetworkConnected() {
        return FrameworkUtils.isNetworkConnected(mContext);
    }

}

package com.fxtv.threebears.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemBase;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.SystemPage;
import com.fxtv.framework.system.SystemPush;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.activity.other.ActivityGuideStepFirst;
import com.fxtv.threebears.activity.user.login.ActivityLogin;
import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.model.User;
import com.fxtv.threebears.model.UserRecoder;
import com.fxtv.threebears.romlite.DatabaseHelper;
import com.fxtv.threebears.system.SystemCommon.ISystemCommonCallBack;
import com.fxtv.threebears.util.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.umeng.message.PushAgent;
import com.umeng.message.proguard.k.e;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户系统,用户相关业务逻辑
 *
 * @author FXTV-Android
 */
public class SystemUser extends SystemBase {
    private static final String TAG = "SystemUser";
    public static final String ACTION_LOGIN = "action_login";
    public static final String ACTION_LOGOUT = "action_logout";
    // 加锁防止多线程同时引用次变量
    public volatile User mUser;
    public static final String RATE_AUTO = "auto"; // 自动
    public static final String RATE_SUPPER = "hd2"; // 超清
    public static final String RATE_HEIGHTY = "mp4"; // 高清
    public static final String RATE_NORMAL = "flv"; // 标准
    public static final String RATE_FLUENT = "3gp"; // 流畅
    public static final int LOGIN_TYPE_NORMAL = 0;
    public static final int LOGIN_TYPE_MESSAGE = 1;
    public static final int LOGIN_TYPE_QQ = 2;
    public static final int LOGIN_TYPE_SINA = 3;
    public static final int LOGIN_TYPE_WECHAT = 4;

    @Override
    protected void init() {
        super.init();
        revertUserInfo();
    }

    @Override
    protected void destroy() {
        super.destroy();
    }

    /** --------------------------用户的账户相关START-------------------------- */

    /**
     * 正常登陆
     *
     * @param username
     * @param passWord
     * @param callBack
     */
    public void userLogin(final String username, final String passWord, final IUserBusynessCallBack callBack) {
        JsonObject params = new JsonObject();
        params.addProperty("username", username);
        params.addProperty("password", passWord);
        Utils.showProgressDialog(getCurrentActivity());

        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), Utils.processUrl(ModuleType.USER, ApiType.USER_login, params), "userLoginApi", false, false, new RequestCallBack<String>() {
            @Override
            public void onSuccess(String data, Response resp) {
                loginHandle(getCurrentActivity(), SystemUser.LOGIN_TYPE_NORMAL, data, username, passWord,
                        new IUserBusynessCallBack() {

                            @Override
                            public void onResult(boolean result, String arg) {
                                Utils.dismissProgressDialog();
                                if (callBack != null) {
                                    callBack.onResult(result, arg);
                                }
                            }
                        });
            }

            @Override
            public void onFailure(Response resp) {
                if (callBack != null) {
                    callBack.onResult(false, resp.msg);
                }
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });

    }

    /**
     * 短信登录
     *
     * @param phone
     * @param code
     * @param callBack
     */
    public void messageLogin(final String phone, final String code, final IUserBusynessCallBack callBack) {
        JsonObject params = new JsonObject();
        params.addProperty("phone", phone);
        params.addProperty("verify_code", code);
        Utils.showProgressDialog(getCurrentActivity());

        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), Utils.processUrl(ModuleType.USER, ApiType.USER_verifyLogin, params), "messageLogin", false, false, new RequestCallBack<String>() {

            @Override
            public void onSuccess(String data, Response resp) {
                loginHandle(getCurrentActivity(), SystemUser.LOGIN_TYPE_MESSAGE, data, phone, null,
                        new IUserBusynessCallBack() {

                            @Override
                            public void onResult(boolean result, String arg) {
                                Utils.dismissProgressDialog();
                                if (callBack != null) {
                                    callBack.onResult(result, arg);
                                }
                            }
                        });
            }

            @Override
            public void onFailure(Response resp) {
                if (callBack != null) {
                    callBack.onResult(false, resp.msg);
                }
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });


    }

    /**
     * 第三方登陆
     *
     * @param thirdLoginId
     * @param thirdName
     * @param callBack
     */
    public void thirdLogin(final String thirdLoginId, final String thirdName, final IUserBusynessCallBack callBack) {
        JsonObject params = new JsonObject();
        params.addProperty("source", thirdName);
        params.addProperty("source_id", thirdLoginId);
        Utils.showProgressDialog(getCurrentActivity());

        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), Utils.processUrl(ModuleType.USER, ApiType.USER_thirdpartyLogin, params), "thirdLoginApi", false, false, new RequestCallBack<String>() {

            @Override
            public void onSuccess(String data, Response resp) {
                int type = SystemUser.LOGIN_TYPE_NORMAL;
                if (thirdName.equals("QQ")) {
                    type = SystemUser.LOGIN_TYPE_QQ;
                } else if (thirdName.equals("SINA")) {
                    type = SystemUser.LOGIN_TYPE_SINA;
                } else if (thirdName.equals("WEIXIN")) {
                    type = SystemUser.LOGIN_TYPE_WECHAT;
                }

                loginHandle(getCurrentActivity(), type, data, thirdLoginId, null, callBack);
            }

            @Override
            public void onFailure(Response resp) {
                if (callBack != null) {
                    callBack.onResult(false, resp.msg);
                }
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });

    }

    /**
     * 注销
     */
    public void logout(final ISystemCommonCallBack callBack) {
        String uc = getSystem(SystemPreference.class).getUC(0);
        if (TextUtils.isEmpty(uc)) {
            getSystem(SystemCommon.class).getUCCode(0, true, new ISystemCommonCallBack() {

                @Override
                public void onResult(boolean result, String arg) {
                    if (result) {
                        DatabaseHelper.getHelper(mContext).removeDefaultUserRecode();
                        mUser = null;
                        noticeLogout();
                        if (callBack != null) {
                            callBack.onResult(result, "退出成功");
                        }
                    } else {
                        if (callBack != null) {
                            callBack.onResult(result, "退出失败");
                        }
                    }
                }
            });
        } else {
            DatabaseHelper.getHelper(mContext).removeDefaultUserRecode();
            mUser = null;
            noticeLogout();
            if (callBack != null) {
                callBack.onResult(true, "退出成功");
            }
        }
    }

    /**
     * 判断用户是否登录
     *
     * @return
     */
    public boolean isLogin() {
        return mUser != null;
    }

    /** --------------------------用户具体业务逻辑相关START-------------------------- */
    /**
     * 守护主播
     *
     * @param callBack
     */
    public void guardAnchor(final String aid, final IUserBusynessCallBack callBack) {
        JsonObject params = new JsonObject();
        JsonArray array = new JsonArray();
        JsonObject child = new JsonObject();
        child.addProperty("id", aid);
        child.addProperty("status", "1");
        array.add(child);
        params.add("guard", array);
        Utils.showProgressDialog(getCurrentActivity());

        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), Utils.processUrl(ModuleType.USER, ApiType.USER_guardAnchor, params), "anchorUserGuard", false, false, new RequestCallBack<String>() {

            @Override
            public void onSuccess(String data, Response resp) {
                mUser.guard_anchor = aid;
                if (callBack != null) {
                    callBack.onResult(true, data);
                }
            }

            @Override
            public void onFailure(Response resp) {
                callBack.onResult(false, resp.msg);
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });


    }

    /**
     * 订阅or取消订阅主播
     *
     * @param aid      主播id
     * @param action   1:订阅,2:取消订阅
     * @param callBack 回调
     */

    public void orderOrUnOrderAnchor(final String aid, final String action, final IUserBusynessCallBack callBack) {
        if (!isLogin()) {
            if (callBack != null) {
                callBack.onResult(false, "请先登录!");
            }
            return;
        }

        JsonObject params = new JsonObject();
        JsonArray array = new JsonArray();
        JsonObject obj = new JsonObject();
        obj.addProperty("id", aid);
        obj.addProperty("type", "1"); // type = 1 标志主播
        obj.addProperty("status", action); // 1:订阅 2：取消订阅
        array.add(obj);
        params.add("order", array);
        Utils.showProgressDialog(getCurrentActivity());

        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), Utils.processUrl(ModuleType.USER, ApiType.USER_order, params), "orderOrUnOrder", false, false, new RequestCallBack<String>() {

            @Override
            public void onSuccess(String data, Response resp) {
                if (action.equals("1")) {// 订阅
                    // 添加友盟标签
                    SystemManager.getInstance().getSystem(SystemPush.class).addTag(aid);
                } else {
                    // 友盟删除标签
                    SystemManager.getInstance().getSystem(SystemPush.class).deleteTag(aid);
                }

                if (callBack != null) {
                    callBack.onResult(true, resp.msg);
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
                Utils.dismissProgressDialog();
            }
        });

    }

    /**
     * 订阅or取消订阅Model
     *
     * @param oid      id
     * @param action   1:订阅,2:取消订阅
     * @param callBack 回调
     */

    public void orderOrUnOrderModel(final String oid, final String action, final String type,
                                    final IUserBusynessCallBack callBack) {
        if (!isLogin())
            return;
        JsonObject params = new JsonObject();
        JsonArray array = new JsonArray();
        JsonObject obj = new JsonObject();
        obj.addProperty("id", oid);
        obj.addProperty("type", type);
        obj.addProperty("status", action); // 1:订阅 0：取消订阅
        array.add(obj);
        params.add("order", array);
        Utils.showProgressDialog(getCurrentActivity());

        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), Utils.processUrl(ModuleType.USER, ApiType.USER_order, params), "orderOrUnOrder", false, false, new RequestCallBack<String>() {
            @Override
            public void onSuccess(String data, Response resp) {
                if (callBack != null) {
                    callBack.onResult(true, resp.msg);
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
                Utils.dismissProgressDialog();
            }
        });


    }

    /**
     * 批量订阅or取消订阅
     *
     * @param params
     * @param callBack
     */
    public void orderOrUnOrder(final JsonObject params, final IUserBusynessCallBack callBack) {
        if (!isLogin())
            return;
        Utils.showProgressDialog(getCurrentActivity());
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), Utils.processUrl(ModuleType.USER, ApiType.USER_order, params), "orderOrUnOrder", false, false, new RequestCallBack<String>() {

            @Override
            public void onSuccess(String data, Response resp) {
                if (callBack != null)
                    callBack.onResult(true, resp.msg);
            }

            @Override
            public void onFailure(Response resp) {
                if (callBack != null) {
                    callBack.onResult(false, resp.msg);
                }
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });

    }

    /**
     * 检查是否有新消息
     *
     * @param callBack
     */
    public void checkNewMessage(final IUserBusynessCallBack callBack) {
        if (isLogin()) {
            JsonObject params = new JsonObject();
            Utils.showProgressDialog(getCurrentActivity());
            String url = Utils.processUrl(ModuleType.USER, ApiType.USER_newMessage, params);
            SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "checkNewMessageApi", false, false, new RequestCallBack<String>() {
                @Override
                public void onSuccess(String data, Response resp) {
                    if (callBack != null) {
                        JSONObject param;
                        String newMessage = "";
                        try {
                            param = new JSONObject(data);
                            newMessage = param.getString("new_message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callBack.onResult(true, newMessage);
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
                    {
                        Utils.dismissProgressDialog();
                    }
                }
            });
        }
    }

    /**
     * 获取用户信息
     *
     * @param callBack 回调
     */
    public void getUserInfo(final IUserBusynessCallBack callBack) {
        if (!isLogin())
            return;
        JsonObject params = new JsonObject();
        Utils.showProgressDialog(getCurrentActivity());
        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_userInfo, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "getUserInfo", false, false, new RequestCallBack<String>() {

            @Override
            public void onSuccess(String data, Response resp) {
                if (callBack != null) {
                    callBack.onResult(true, data);
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
                Utils.dismissProgressDialog();
            }
        });

    }


    /**
     * 判断该主播,用户是否已经守护
     */
    public boolean anchorIsGurad(String anchorId) {
        if (!isLogin()) {
            return false;
        } else if (TextUtils.isEmpty(anchorId)) {
            return false;
        } else {
            try {
                return mUser.guard_anchor.equals(anchorId);
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 判断主播是否订阅
     *
     * @param anchorId
     * @return
     */
    public boolean anchorIsOrder(String anchorId) {
        boolean flag = false;
        if (!isLogin()) {
            return false;
        } else {
            try {
                for (int i = 0; i < mUser.order_anchor.size(); i++) {
                    if (anchorId.equals(mUser.order_anchor.get(i).id)) {
                        flag = true;
                    }
                }
            } catch (Exception e) {
                Logger.e(TAG, "e=" + e);
            }
            return flag;
        }
    }

    /**
     * 收藏or取消收藏视频
     *
     * @param vid      id
     * @param action   1--收藏，0--取消收藏
     * @param callBack 回调
     */
    public void favoriteOrUnFavoriteVideo(String vid, int action, final IUserBusynessCallBack callBack) {
        if (!isLogin())
            return;

        JsonObject params = new JsonObject();
        JsonArray array = new JsonArray();
        JsonObject child = new JsonObject();
        child.addProperty("id", vid);
        child.addProperty("status", "" + action);
        array.add(child);
        params.add("collect", array);
        Utils.showProgressDialog(getCurrentActivity());
        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_collectVideo, params);
        callBack.isMsg = true;
        getSystem(SystemHttp.class).get(getCurrentActivity(), url, "userCollectOrDisVideo", false, false, callBack);

    }

    /**
     * 视频点赞和取消点赞
     *
     * @param vid      视频id
     * @param action   1--赞 0--取消赞
     * @param callBack 回调
     */
    public void cryUpOrUnCryUpForVideo(String vid, String action, final IUserBusynessCallBack callBack) {
        if (!isLogin())
            return;

        JsonObject params = new JsonObject();
        params.addProperty("id", vid);
        params.addProperty("status", "1");
        Utils.showProgressDialog(getCurrentActivity());

        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_videoInteract, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "userCryUpOrDisVideoApi", false, false, new RequestCallBack<String>() {
            @Override
            public void onSuccess(String data, Response resp) {
                if (callBack != null) {
                    callBack.onResult(true, resp.msg);
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
                Utils.dismissProgressDialog();
            }
        });

    }

    /**
     * 视频评论赞或取消赞
     *
     * @param cid      评论id
     * @param action   1，点赞；0，取消点赞 2.踩（暂时不知道怎么用）
     * @param callBack
     */
    public void cryUpOrUnCryUpForVideoComment(String cid, String action, final IUserBusynessCallBack callBack) {
        if (!isLogin()) {
            FrameworkUtils.skipActivity(getCurrentActivity(), ActivityLogin.class);
            return;
        }
        JsonObject params = new JsonObject();
        params.addProperty("id", cid);
        params.addProperty("status", "1");
        Utils.showProgressDialog(getCurrentActivity());

        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_videoCommentInteract, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "videoCommentInteract", false, false, new RequestCallBack<String>() {

            @Override
            public void onSuccess(String data, Response resp) {
                if (callBack != null) {
                    callBack.onResult(true, resp.msg);
                }
            }

            @Override
            public void onFailure(Response resp) {
                if (callBack != null) {
                    callBack.onResult(true, resp.msg);
                }
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });

    }

    /**
     * 举报视频评论
     *
     * @param cid      评论id
     * @param callBack 回调
     */
    public void reportVideoComment(String cid, final IUserBusynessCallBack callBack) {
        if (!isLogin())
            return;

        JsonObject params = new JsonObject();
        params.addProperty("id", cid);
        Utils.showProgressDialog(getCurrentActivity());

        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_videoCommentReport, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "videoCommentInteract", false, false, new RequestCallBack<String>() {

            @Override
            public void onSuccess(String data, Response resp) {
                if (callBack != null) {
                    callBack.onResult(true, resp.msg);
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
                Utils.dismissProgressDialog();
            }
        });

    }

    /**
     * 视频分享
     */
    public void shareVideo(String videoId, final IUserBusynessCallBack callBack) {
        JsonObject params = new JsonObject();
        params.addProperty("id", videoId);

        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_share, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "shareVideoAPI", new RequestCallBack<String>() {


            @Override
            public void onSuccess(String data, Response resp) {
                if (callBack != null) {
                    callBack.onResult(true, resp.msg);
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

    }

    /**
     * 发表视频评论
     *
     * @param vid      --视频id
     * @param content  --发送的内容
     * @param callBack
     */
    public void sendCommentForVideo(String vid, String content, final IUserBusynessCallBack callBack) {
        JsonObject params = new JsonObject();
        params.addProperty("id", vid);
        params.addProperty("content", content);
        Utils.showProgressDialog(getCurrentActivity());

        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_comment, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "mainSendCommentApi", new RequestCallBack<String>() {
            @Override
            public void onSuccess(String data, Response resp) {
                if (callBack != null) {
                    callBack.onResult(true, data);
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
                Utils.dismissProgressDialog();
            }
        });

    }

    /**
     * 视频详情页---回复
     *
     * @param cid      --评论id
     * @param content  --回复的内容
     * @param callBack
     */
    public void replyVideoComment(String cid, String content, final IUserBusynessCallBack callBack) {
        if (!isLogin())
            return;

        JsonObject params = new JsonObject();
        params.addProperty("id", cid);
        params.addProperty("content", content);
        Utils.showProgressDialog(getCurrentActivity());
        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_replyComment, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "replyComment", false, false, callBack);

    }

    /**
     * 验证手机是否已被使用
     *
     * @param phone    手机号
     * @param callBack 回调
     */
    public void verifyPhoneUse(String phone, final IUserBusynessCallBack callBack) {
        JsonObject params = new JsonObject();
        params.addProperty("phone", phone);
        Utils.showProgressDialog(getCurrentActivity());
        callBack.isMsg = true;

        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_verifyPhoneUse, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "verifyPhoneUse", false, false, callBack);

    }

    /**
     * 验证用户是否注册过
     *
     * @param phone
     * @param callBack
     */
    public void verifyRegisterUser(String phone, final IUserBusynessCallBack callBack) {
        JsonObject params = new JsonObject();
        params.addProperty("phone", phone);
        Utils.showProgressDialog(getCurrentActivity());

        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_verifyPhone, params);
        callBack.isMsg = true;
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "verify_username", false, false, callBack);

    }

    /**
     * 用户注册
     *
     * @param userName      --用户名
     * @param passWord      --密码
     * @param verifyCode    --验证码
     * @param recommendCode --推荐码(非必填)
     * @param callBack
     */
    public void register(final String userName, final String passWord, String verifyCode, String recommendCode,
                         final IUserBusynessCallBack callBack) {
        JsonObject params = new JsonObject();
        params.addProperty("username", userName);
        params.addProperty("password", passWord);
        params.addProperty("verify_code", verifyCode);
        if (!recommendCode.equals("")) {
            params.addProperty("recommend_code", recommendCode);
        }
        Utils.showProgressDialog(getCurrentActivity());
        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_register, params);
        getSystem(SystemHttp.class).get(getCurrentActivity(), url, "userRegisterApi", new RequestCallBack<String>() {

            @Override
            public void onSuccess(String data, final Response resp) {
                loginHandle(getCurrentActivity(), SystemUser.LOGIN_TYPE_NORMAL, data, userName, passWord,
                        new IUserBusynessCallBack() {
                            @Override
                            public void onResult(boolean result, String arg) {
                                if (callBack != null) {
                                    callBack.onResult(result, resp.msg);
                                }
                            }
                        });
            }

            @Override
            public void onFailure(Response resp) {
                if (callBack != null) {
                    callBack.onResult(false, resp.msg);
                }
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });
    }

    /**
     * 判断用户是否有守护主播
     *
     * @return
     */
    public boolean checkHasGuradAnchor() {
        if (!isLogin())
            return false;

        return !TextUtils.isEmpty(mUser.guard_anchor);
    }

    /**
     * 判断主播是否订阅
     *
     * @param anchor
     * @return
     */
    public boolean checkAnchorIsOrder(Anchor anchor) {
        if (!isLogin())
            return false;
        return anchor.order_status.equals("1");
    }

    /**
     * 判断游戏是否订阅
     *
     * @param gameId
     * @param orderId
     * @return
     */
    // public boolean checkGameIsOrder(String gameId, String orderId, String
    // type) {
    //
    // }

    /**
     * 动态回复(发现动态圈or主播的动态圈)
     */
    public void replyForAction() {

    }

    /**
     * 提交投票
     *
     * @param id       投票id
     * @param optionId 选择的那个选项的id
     * @param callBack 回调
     */
    public void submitVote(String id, String optionId, final IUserBusynessCallBack callBack) {
        JsonObject params = new JsonObject();
        params.addProperty("id", id);
        params.addProperty("option_id", optionId);
        Utils.showProgressDialog(getCurrentActivity());
        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_vote, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "submitVote", false, false, callBack);

    }

    /**
     * 热聊话题--吐槽--评论
     *
     * @param id
     * @param content
     */
    public void topicMessageReply(String id, String content, final IUserBusynessCallBackWithMes callBack) {
        if (!isLogin()) {
            FrameworkUtils.showToast(getCurrentActivity(), "请先登录");
            return;
        } else {
            Utils.showProgressDialog(getCurrentActivity());
            JsonObject params = new JsonObject();
            params.addProperty("id", id);
            params.addProperty("content", content);
            String url = Utils.processUrl(ModuleType.USER, ApiType.USER_topicMessageReply, params);

            SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "topicMessageReply", false, false, callBack);
        }
    }

    /**
     * 热聊话题--举报吐槽或者评论
     *
     * @param id--举报或评论的id
     * @param type--(1--吐槽，2--评论)
     */
    public void juBaoTopicOrReply(String id, int type, final IUserBusynessCallBack callBack) {
        if (!isLogin()) {
            FrameworkUtils.showToast(getCurrentActivity(), "请先登录");
            return;
        } else {
            Utils.showProgressDialog(getCurrentActivity());
            JsonObject params = new JsonObject();
            params.addProperty("id", id);
            callBack.isMsg = true;

            //举报吐槽
            if (type == 1) {
                String url = Utils.processUrl(ModuleType.USER, ApiType.USER_topicMessageReport, params);
                SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "topicMessageReport", false, false, callBack);
            }
            //举报吐槽的评论
            else if (type == 2) {
                String url = Utils.processUrl(ModuleType.USER, ApiType.USER_topicMessageReplyReport, params);
                SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "topicMessageReplyReport", false, false, callBack);
            }
        }
    }

    /**
     * 主播圈动态或回复的举报
     *
     * @param id
     * @param callBack
     */
    public void juBaoAnchorActOrReply(String id, final IUserBusynessCallBack callBack) {
        if (!isLogin()) {
            FrameworkUtils.showToast(getCurrentActivity(), "请先登录");
            return;
        } else {
            Utils.showProgressDialog(getCurrentActivity());
            JsonObject params = new JsonObject();
            params.addProperty("id", id);
            callBack.isMsg = true;
            String url = Utils.processUrl(ModuleType.USER, ApiType.USER_anchorBbsReplyReport, params);
            SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "anchorBbsReplyReport", false, false, callBack);
        }
    }


    /**
     * \
     * 吐槽或评论的点赞或取消赞
     *
     * @param id
     * @param status--（1--赞,0--取消赞）
     * @param type--（1--吐槽,0--吐槽的评论）
     * @param callBack
     */
    public void zanOrdisZan(String id, int type, int status, final IUserBusynessCallBack callBack) {
        if (!isLogin()) {
            FrameworkUtils.showToast(getCurrentActivity(), "请先登录");
            return;
        } else {
            Utils.showProgressDialog(getCurrentActivity());
            JsonObject params = new JsonObject();
            params.addProperty("id", id);
            params.addProperty("status", status + "");
            callBack.isMsg = true;

            if (type == 1) {
                String url = Utils.processUrl(ModuleType.USER, ApiType.USER_topicMessageLike, params);
                SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "topicMessageLike", false, false, callBack);

            } else if (type == 0) {
                String url = Utils.processUrl(ModuleType.USER, ApiType.USER_topicMessageReplyLike, params);
                SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "topicMessageReplyLike", false, false, callBack);
            }
        }
    }

    /**
     * 主播动态分享
     */
    public void anchorActShare(String id, final IUserBusynessCallBack callBack) {
        Utils.showProgressDialog(getCurrentActivity());
        JsonObject params = new JsonObject();
        params.addProperty("id", id);
        String url = Utils.processUrl(ModuleType.USER, ApiType.USER_anchor_share, params);
        getSystem(SystemHttp.class).get(getCurrentActivity(), url, "anchorShare", new RequestCallBack() {

            @Override
            public void onSuccess(Object data, Response resp) {
                if (callBack != null) {
                    callBack.onResult(true, resp.msg);
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
                Utils.dismissProgressDialog();
            }
        });
    }


    /**
     * 主播圈动态或评论的点赞
     *
     * @params type--(1.动态，0.回复)
     */
    public void anchorActZan(String id, int type, final IUserBusynessCallBack callBack) {
        if (!isLogin()) {
            FrameworkUtils.showToast(getCurrentActivity(), "请先登录");
            return;
        } else {
            Utils.showProgressDialog(getCurrentActivity());
            JsonObject params = new JsonObject();
            params.addProperty("id", id);
            params.addProperty("status", "1");
            callBack.isMsg = true;

            if (type == 1) {
                String url = Utils.processUrl(ModuleType.USER, ApiType.USER_anchorBbsLike, params);
                getSystem(SystemHttp.class).get(getCurrentActivity(), url, "anchorBbsLike", false, false, callBack);
            } else if (type == 0) {
                String url = Utils.processUrl(ModuleType.USER, ApiType.USER_anchorBbsReplyLike, params);
                getSystem(SystemHttp.class).get(getCurrentActivity(), url, "anchorBbsReplyLike", false, false, callBack);
            }


        }

    }

    /**
     * 下载信息
     *
     * @param vid      视频id
     * @param callBack
     */
    public void downloadInfo(final String vid, final IUserBusynessCallBack callBack) {
        JsonObject params = new JsonObject();
        params.addProperty("id", vid);
        Utils.showProgressDialog(getCurrentActivity());
        String url = Utils.processUrl(ModuleType.BASE, ApiType.BASE_streamSizes, params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get(getCurrentActivity(), url, "getDownloadInfo", false, false, callBack);

    }

    /** --------------------------private-------------------------- */
    /**
     * 登录处理
     *
     * @param type 类型
     * @param json 返回数据
     * @param arg1 占位符
     * @param arg2 占位符
     */
    private void loginHandle(Context context, int type, String json, String arg1, String arg2,
                             final IUserBusynessCallBack callBack) {
        Gson gson = new Gson();
        mUser = gson.fromJson(json, User.class);

        AddUMengPush();
        DatabaseHelper.getHelper(mContext).updateAccountInfoCache(type, json, arg1, arg2);

        getSystem(SystemCommon.class).getUCCode(1, false, new ISystemCommonCallBack() {
            @Override
            public void onResult(boolean result, String arg) {
                if (result) {
                    noticeLogin();
                    if(mUser!=null && "1".equals(mUser.guide_status)){
                        FrameworkUtils.skipActivity(getCurrentActivity(), ActivityGuideStepFirst.class);
                    }
                }
                if (callBack != null) {
                    callBack.onResult(result, result ? "登录成功" : arg);
                }
            }
        });

    }

    /**
     * 添加友盟推送
     */
    private void AddUMengPush() {
        new Thread() {
            public void run() {
                try {
                    PushAgent mPushAgent = PushAgent.getInstance(mContext);
                    if (mUser != null) {
                        mPushAgent.addAlias(mUser.user_id, "userId");
                        if (mUser.order_anchor != null && mUser.order_anchor.size() != 0) {
                            String[] uMOrders = new String[mUser.order_anchor.size()];
                            for (int i = 0; i < mUser.order_anchor.size(); i++) {
                                uMOrders[i] = mUser.order_anchor.get(i).id;
                            }
                            if (uMOrders != null && uMOrders.length > 0) {
                                mPushAgent.getTagManager().add(uMOrders);
                            }
                        }
                    }
                } catch (e e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }

    /**
     * 恢复默认用户数据
     */
    private void revertUserInfo() {
        UserRecoder defaultUserRecode = DatabaseHelper.getHelper(mContext).getDefaultUserRecode();
        if (defaultUserRecode != null) {
            Gson gson = new Gson();
            mUser = gson.fromJson(defaultUserRecode.content, User.class);
        }
    }

    private void noticeLogin() {
        mContext.sendBroadcast(new Intent(ACTION_LOGIN));
    }

    private void noticeLogout() {
        mContext.sendBroadcast(new Intent(ACTION_LOGOUT));
    }

    private Activity getCurrentActivity() {
        return SystemManager.getInstance().getSystem(SystemPage.class).getCurrActivity();
    }


}

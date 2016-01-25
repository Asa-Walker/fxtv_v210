package com.fxtv.framework.model;

/**
 * Created by wzh on 2016/1/4.
 */
public class ApiType {

    //api
    /**
     * ----------------UserApi----------------
     */
    public static final String USER_login = "login",//用户,登陆
            USER_thirdpartyLogin = "thirdpartyLogin",//第三方登录
            USER_verifyLogin = "verifyLogin",//短信验证登录
            USER_verifyPhone = "verifyPhone",//验证用户是否存在
            USER_verifyPhoneUse = "verifyPhoneUse",//验证手机号是否已被使用
            USER_retrievePassword = "retrievePassword",//修改用户的密码
            USER_register = "register",//注册
            USER_avatarList = "avatarList",//所有头像
            USER_setAvatar = "setAvatar",//设置头像
            USER_guardAnchor = "guardAnchor",//更新守护主播
            USER_guideGameList = "guideGameList",//新手引导第一步（订阅游戏）
            USER_guideOrderList = "guideOrderList",//新手引导第二步(订阅主播)
            USER_collectVideoList = "collectVideoList",//收藏的视频
            USER_myLottery = "myLottery",//我的抽奖记录
            USER_collectVideo = "collectVideo",//视频收藏or取消收藏
            USER_videoInteract = "videoInteract",//视频赞or取消赞
            USER_share = "share",//分享视频
            USER_delUserData = "delUserData",//退订所有(主播或游戏或收藏)
            USER_modifyNickname = "modifyNickname",//修改用户昵称
            USER_setUserInfo = "setUserInfo",//修改用户信息
            USER_modifyPhone = "modifyPhone",//绑定手机
            USER_tasksDetail = "tasksDetail",//获取用户的经验或者饼干
            USER_userInfo = "userInfo",//获取用户信息
            USER_signIn = "signIn",//用户签到
            USER_order = "order",//用户订阅or取消订阅
            USER_vote = "vote",//新版用户提交投票(可用于热点投票和主播空间的投票)
            USER_videoCommentReport = "videoCommentReport",//举报评论
            USER_comment = "comment",//首页，视频详情，评论，发表评论
            USER_replyComment = "replyComment",//首页，视频详情,回复评论
            USER_videoCommentInteract = "videoCommentInteract",//首页，视频详情，评论，顶or踩
            USER_dailyTasks = "dailyTasks",//每日任务
            USER_dialogList = "dialogList",//消息列表
            USER_dialogMessage = "dialogMessage",//显示对话信息接口
            USER_sendMessage = "sendMessage",//发送信息接口
            USER_newMessage = "newMessage",//检查是否有新信息接口
            USER_replyBbs = "replyBbs",//主播空间里的发送评论
            USER_feedback = "feedback",//其他，意见反馈
            USER_sendBarrage = "sendBarrage",//发送弹幕
            USER_topicMessage = "topicMessage",//话题吐槽
            USER_topicMessageReply = "topicMessageReply",//吐槽下发表评论
            USER_topicMessageReport = "topicMessageReport",//举报吐槽
            USER_topicMessageReplyReport = "topicMessageReplyReport",//举报吐槽下的评论
            USER_anchorBbsReplyReport = "anchorBbsReplyReport",//主播动态回复的举报
            USER_shareTopicMessage = "shareTopicMessage",//分享吐槽
            USER_topicMessageLike = "topicMessageLike",//话题吐槽点赞
            USER_topicMessageReplyLike = "topicMessageReplyLike",//话题吐槽的评论点赞
            USER_anchorBbsLike = "anchorBbsLike",//主播动态点赞
            USER_anchorBbsReplyLike = "anchorBbsReplyLike",//
            USER_topic_follow = "topic_follow",//话题关注，取消关注
            USER_uploadImage = "uploadImage",//上传头像
            USER_anchor_share = "shareAnchorBbs"//主播圈的分享
                    ;


    /**
     * ----------------IndexApi----------------
     */
    public static final String
            INDEX_menu = "menu",//首页,所有游戏Tab
            INDEX_setMenu = "setMenu",//首页，设置游戏列表
            INDEX_menuBanner = "menuBanner",//首页,banner列表
            INDEX_menuVideo = "menuVideo"//首页,游戏视频列表
                    ;

    /**
     * ----------------------BaseApi基础模块----------------------
     */
    public static final String
            BASE_videoPlayUrl = "videoPlayUrl",//根据视频id直接获取下载地址
            BASE_videoInfo = "videoInfo",//首页，视频详情
            BASE_VideoCommentList = "VideoCommentList",//首页，视频详情，评论列表
            BASE_relatedVideo = "relatedVideo",//首页，视频详情，相关视频
            BASE_loading = "loading",//获取loading页图片地址
            BASE_getUC = "getUC",//获取UC
            BASE_hotWord = "hotWord",//搜索接口-获取热词
            BASE_searchVideo = "searchVideo",// 搜索接口-搜索视频列表
            BASE_searchAnchor = "searchAnchor",//搜索接口-搜索主播列表
            BASE_upgradeVersion = "upgradeVersion",//版本升级接口-获取版本信息
            BASE_streamSizes = "streamSizes",//获取视频清晰度和地址
            BASE_playError = "playError"//播放失败时请求的统计接口


                    ;


    /**
     * ----------------GameApi----------------
     */
    public static final String
            GAME_gameList = "gameList",//游戏,所有
            GAME_menu = "menu",//
            GAME_orderVideo = "orderVideo",//游戏，可订阅的内部分类的视频
            GAME_orderList = "orderList"//获取订阅源列表

                    ;


    /**
     * ----------------SelfApi----------------
     */
    public static final String
            MINE_myVideo = "myVideo",//自频道,视频
            MINE_anchorList = "anchorList",//自频道，订阅的主播
            MINE_gameList = "gameList"//自频道,订阅的游戏

                    ;

    /**
     * ----------------FindApi----------------
     */
    public static final String
            FIND_activityCenter = "activityCenter",//获取活动列表
            FIND_activityInfo = "activityInfo",//获取活动详情
            FIND_voteList = "voteList",//获取热点投票列表
            FIND_voteDetail = "voteDetail",//获取投票详情
            FIND_getRank = "getRank",//获取排行榜列表
            FIND_fxcupList = "fxcupList",//获取飞熊杯列表
            FIND_anchorRing = "anchorRing",//获取主播圈的数据
            FIND_gameList = "gameList",//获取所有主播的分类列表
            FIND_anchorList = "anchorList",//按首字母获取主播列表
            FIND_hotTopic = "hotTopic",//获取热聊话题的Banner
            FIND_topic = "topic",//热聊话题列表
            FIND_followTopic = "followTopic",//获取关注列表
            FIND_topicInfo = "topicInfo",//话题详情
            FIND_topicMessage = "topicMessage",//话题吐槽列表
            FIND_messageReply = "messageReply",//获取吐槽下的评论列表
            FIND_relatedTopic = "relatedTopic",//获取吐槽下的相关列表
            FIND_WEBGAMES = "webgame",//获取h5小游戏的列表
            FIND_VIDEOTHEMELIST = "videoThemeList",//获取视频专题列表
            FIND_fxcupList_v2 = "fxcupList_v2"//新版飞熊杯接口

                    ;

    /**
     * ----------------AnchorApi----------------
     */
    public static final String
            ANCHOR_zone = "zone",//主播,详情信息
            ANCHOR_videoList = "videoList",//主播,视频
            ANCHOR_messageList = "messageList",//主播,留言
            ANCHOR_Friend = "Friend",//主播,主播空间推荐主播
            ANCHOR_albumVideo = "albumVideo",//主播,主播专辑中的视频列表
            ANCHOR_messageAdd = "messageAdd",//用户， (主播空间)发表留言
            ANCHOR_voting = "voting",//正在进行的投票信息
            ANCHOR_voteHistory = "voteHistory",//历史投票
            ANCHOR_bbs = "bbs",//获取主播的最新动态列表
            ANCHOR_bbsReplyList = "bbsReplyList",//获取主播动态的回复列表(包括发现模块的主播圈)
            ANCHOR_album = "album"//获取主播专辑列表

                    ;
    /**
     * ----------------其他相关Start----------------
     */
    public static final String
            TV_qrcodeLogin = "qrcodeLogin";//


    /**
     * ----------------AnalyzeApi----------------
     */
    public static final String
            LOG_deviceStart = "deviceStart",//
            LOG_deviceEnd = "deviceEnd",//
            LOG_startPlay = "startPlay",//
            LOG_endPlay = "endPlay",//
            LOG_barrageSwitch = "barrageSwitch",//
            LOG_wifi = "wifi",//
            LOG_record = "record"//
                    ;


}

package com.fxtv.threebears.activity.player;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseFragmentActivity;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemFragmentManager;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.SystemShare;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.h5.ActivityWebView;
import com.fxtv.threebears.fragment.module.player.FragmentPlayerAbout;
import com.fxtv.threebears.fragment.module.player.FragmentPlayerAnchor;
import com.fxtv.threebears.fragment.module.player.FragmentPlayerComment;
import com.fxtv.threebears.fragment.module.player.FragmentPlayerDescription;
import com.fxtv.threebears.fragment.module.player.FragmentPlayerFlashAbout;
import com.fxtv.threebears.fragment.module.player.FragmentPlayerFlashAlbum;
import com.fxtv.threebears.fragment.module.player.FragmentPlayerFlashComment;
import com.fxtv.threebears.fragment.module.player.FragmentPlayerFlashDescription;
import com.fxtv.threebears.fragment.module.player.FragmentPlayerFlashLottery;
import com.fxtv.threebears.fragment.module.player.FragmentPlayerFlashVote;
import com.fxtv.threebears.fragment.module.player.FragmentPlayerLottery;
import com.fxtv.threebears.fragment.module.player.FragmentPlayerVideo;
import com.fxtv.threebears.model.IconShow;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemAnalyze;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemDownloadVideoManager;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.ShareDialog;
import com.fxtv.threebears.view.mediaplayer.IjkVideoView;
import com.fxtv.threebears.view.mediaplayer.MediaController;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

;

/**
 * 新版播放详情界面
 *
 * @author Android2
 */
public class ActivityVideoPlay extends BaseFragmentActivity {
    private static final String TAG = "ActivityVideoPlay";
    private String mVideoId;
    private Video mVideo;
    private Bundle mBundle;
    private ViewGroup mContainerAbout, mContainerLottery;
    private ImageView mDownloadImg, mFavoriteImg, mCryUpImg, mShardImg;
    private String mSkipType = null;
    private String mSharedUrl = "http://api.feixiong.tv/h5/v/v3.html";
    private int mFragmentPos = 0;

    private ViewGroup mRoot;
    private IjkVideoView mVideoView;
    private MediaController mMediaController;
    private ViewGroup mVideoOther;
    private final Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_video_play);

        mRoot = (ViewGroup) findViewById(R.id.root_view);

        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mMediaController = (MediaController) findViewById(R.id.controller);
        mMediaController.setPlayer(mVideoView);
        mMediaController.setRootView(mRoot);

        mVideoOther = (ViewGroup) findViewById(R.id.video_other);

        onScreenChange(false);
        handleIntent();
        getData();
        getSystem(SystemAnalyze.class).analyzeVideoStart(mVideoId);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.removeCallbacks(this);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }
        }, 1000);
    }

    private void handleIntent() {
        JSONObject jsonObject = getSystem(SystemCommon.class).getH5Content(this);
        if (jsonObject != null) {
            try {
                String videoId = jsonObject.getString("video_id");
                if (!TextUtils.isEmpty(videoId)) {
                    mVideoId = videoId;
                } else {
                    showToast("内链跳转失败");
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showToast("内链跳转失败");
                finish();
            }
        } else {
            mVideoId = getIntent().getStringExtra("video_id");
            if (TextUtils.isEmpty(mVideoId)) {
                showToast("视频id错误");
                finish();
            }
        }
        mSkipType = getIntent().getStringExtra("skipType");
        if (mSkipType != null && mVideoId != null) {
            getSystem(SystemAnalyze.class).analyzeUserAction("video", mVideoId, mSkipType);
        }
    }

    private void getData() {
        JsonObject params = new JsonObject();
        params.addProperty("id", mVideoId);
        Utils.showProgressDialog(this);

        getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.BASE, ApiType.BASE_videoInfo, params), "", false, false, new RequestCallBack<Video>() {
            @Override
            public void onSuccess(Video data, Response resp) {
                if (data != null) {
                    mVideo = data;
                    getDataCallBack();
                }
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });

    }

    private void getDataCallBack() {
        mBundle = new Bundle();
        mBundle.putSerializable("video", mVideo);
        mMediaController.setVideo(mVideo);
        initContainer();
        initImgBut();
        initVideoAction();
    }

    private void initContainer() {
        initContainerDesc();
        initContainerAnchor();
        initContainerAbout();
        initContainerLottery();
        initContainerComment();
    }

    /**
     * 初始化描述
     */
    private void initContainerDesc() {
        SystemManager
                .getInstance()
                .getSystem(SystemFragmentManager.class)
                .addAloneFragment(R.id.video_container_description, FragmentPlayerDescription.class.getCanonicalName(),
                        mBundle);
    }

    /**
     * 初始化主播
     */
    private void initContainerAnchor() {
        SystemManager.getInstance().getSystem(SystemFragmentManager.class)
                .addAloneFragment(R.id.video_container_anchor, FragmentPlayerAnchor.class.getCanonicalName(), mBundle);
    }

    /**
     * 初始化关于
     */
    private void initContainerAbout() {
        mContainerAbout = (ViewGroup) findViewById(R.id.video_container_about);
        if (mVideo.relate_video_list != null && mVideo.relate_video_list.size() > 0) {
            SystemManager
                    .getInstance()
                    .getSystem(SystemFragmentManager.class)
                    .addAloneFragment(R.id.video_container_about, FragmentPlayerAbout.class.getCanonicalName(), mBundle);
        } else {
            mContainerAbout.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化抽奖
     */
    private void initContainerLottery() {
        mContainerLottery = (ViewGroup) findViewById(R.id.video_container_lottery);
        if (mVideo.lottery != null) {
            SystemManager
                    .getInstance()
                    .getSystem(SystemFragmentManager.class)
                    .addAloneFragment(R.id.video_container_lottery, FragmentPlayerLottery.class.getCanonicalName(),
                            mBundle);
        } else {
            mContainerLottery.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化评论
     */
    private void initContainerComment() {
//        mBundle.putInt("layout", R.id.video_container_action);
        SystemManager
                .getInstance()
                .getSystem(SystemFragmentManager.class)
                .addAloneFragment(R.id.video_container_comment, FragmentPlayerComment.class.getCanonicalName(), mBundle);

    }

    private void initVideoAction() {
        IconShow icon_show = mVideo.icon_show;
        // 评论
        if (icon_show.shouldShow(icon_show.comment)) {
            TextView mes = (TextView) findViewById(R.id.activity_new_anchor_space_msg_text);
            mes.setVisibility(View.VISIBLE);
            mes.setText("评论(" + mVideo.comment_num + ")");
            findViewById(R.id.activity_new_anchor_space_msg_text).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemManager
                            .getInstance()
                            .getSystem(SystemFragmentManager.class).addAnimFragment(R.id.video_container_action,
                            FragmentPlayerFlashComment.class.getCanonicalName(), mBundle,
                            ActivityVideoPlay.this);
                    mFragmentPos = 1;
                }
            });
        } else {
            findViewById(R.id.activity_new_anchor_space_msg_text).setVisibility(View.GONE);
        }
        // 抽奖
        if (icon_show.shouldShow(icon_show.lottery)) {
            findViewById(R.id.activity_new_anchor_space_act_text).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_new_anchor_space_act_text).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemManager
                            .getInstance()
                            .getSystem(SystemFragmentManager.class)
                            .addAnimFragment(R.id.video_container_action,
                                    FragmentPlayerFlashLottery.class.getCanonicalName(), mBundle,
                                    ActivityVideoPlay.this);
                    mFragmentPos = 2;
                }
            });
        } else {
            findViewById(R.id.activity_new_anchor_space_act_text).setVisibility(View.GONE);
        }
        // 投票
        if (icon_show.shouldShow(icon_show.vote)) {
            findViewById(R.id.activity_new_anchor_space_vote_text).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_new_anchor_space_vote_text).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemManager
                            .getInstance()
                            .getSystem(SystemFragmentManager.class)
                            .addAnimFragment(R.id.video_container_action,
                                    FragmentPlayerFlashVote.class.getCanonicalName(), mBundle, ActivityVideoPlay.this);
                    mFragmentPos = 3;
                }
            });
        } else {
            findViewById(R.id.activity_new_anchor_space_vote_text).setVisibility(View.GONE);
        }
        // 专辑
        if (icon_show.shouldShow(icon_show.album)) {
            findViewById(R.id.activity_new_anchor_space_recomend_text).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_new_anchor_space_recomend_text).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemManager
                            .getInstance()
                            .getSystem(SystemFragmentManager.class)
                            .addAnimFragment(R.id.video_container_action,
                                    FragmentPlayerFlashAlbum.class.getCanonicalName(), mBundle, ActivityVideoPlay.this);
                    mFragmentPos = 4;
                }
            });
        } else {
            findViewById(R.id.activity_new_anchor_space_recomend_text).setVisibility(View.GONE);
        }
        // 相关
        if (icon_show.shouldShow(icon_show.relate)) {
            findViewById(R.id.activity_new_anchor_space_shop_text).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_new_anchor_space_shop_text).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemManager
                            .getInstance()
                            .getSystem(SystemFragmentManager.class)
                            .addAnimFragment(R.id.video_container_action,
                                    FragmentPlayerFlashAbout.class.getCanonicalName(), mBundle, ActivityVideoPlay.this);
                    mFragmentPos = 5;
                }
            });
        } else {
            findViewById(R.id.activity_new_anchor_space_shop_text).setVisibility(View.GONE);
        }
    }

    private void initImgBut() {
        mDownloadImg = (ImageView) findViewById(R.id.image_download);
        mFavoriteImg = (ImageView) findViewById(R.id.image_soucang);
        mCryUpImg = (ImageView) findViewById(R.id.image_zan);
        mShardImg = (ImageView) findViewById(R.id.image_share);
        ImageView redBag = (ImageView) findViewById(R.id.image_red_bag);
        try {
            if ("1".equals(mVideo.icon_show.notice)) {
                redBag.setVisibility(View.VISIBLE);
                SystemManager.getInstance().getSystem(SystemCommon.class).displayDefaultImage(this, redBag, mVideo.notice.image);
                redBag.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("url", mVideo.notice.url);
                        bundle.putString("share_img", mVideo.notice.share_image);
                        FrameworkUtils.skipActivity(ActivityVideoPlay.this, ActivityWebView.class, bundle);
                    }
                });
            } else {
                redBag.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Logger.d(TAG, "redBag_e=" + e);
            redBag.setVisibility(View.GONE);
        }
        updateFavIcon(true);
        updateZanIcon(true);
        updateDownIcon();
        mDownloadImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getSystem(SystemCommon.class).showDownloadDialog(ActivityVideoPlay.this, mVideo, mDownloadImg);
            }
        });
        mFavoriteImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!getSystem(SystemUser.class).isLogin()) {
                    showToast("请先登录!");
                } else {
                    int temp;
                    if ("0".equals(mVideo.fav_status)) {
                        temp = 1;
                    } else {
                        temp = 0;
                    }
                    getSystem(SystemUser.class)
                            .favoriteOrUnFavoriteVideo(mVideoId, temp, new IUserBusynessCallBack() {
                                @Override
                                public void onResult(boolean result, String arg) {
                                    showToast(arg);
                                    if (result) {
                                        updateFavIcon(false);
                                    }
                                }
                            });
                }
            }
        });
        mCryUpImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!getSystem(SystemUser.class).isLogin()) {
                    showToast("请先登录!");
                } else {
                    int status;
                    if ("0".equals(mVideo.top_status)) {
                        status = 1;
                    } else {
                        status = 0;
                    }
                    getSystem(SystemUser.class)
                            .cryUpOrUnCryUpForVideo(mVideoId, "1", new IUserBusynessCallBack() {
                                @Override
                                public void onResult(boolean result, String arg) {
                                    showToast(arg);
                                    if (result) {
                                        updateZanIcon(false);
                                    }
                                }
                            });
                }
            }
        });
        mShardImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!getSystem(SystemUser.class).isLogin()) {
//                    FrameworkUtils.skipActivity(ActivityVideoPlay.this, ActivityLogin.class);
//                } else {
                //useOneKeyShare();
                userShare();

            }
//            }
        });
    }

    public void userShare() {
        ShareModel model = new ShareModel();
        model.shareTitle = mVideo.title;
        model.shareUrl = getShardUrl();
        model.shareSummary = mVideo.intro;
        model.fileImageUrl = mVideo.image;
        getSystem(SystemCommon.class).showShareDialog(ActivityVideoPlay.this, model, new ShareDialog.ShareCallBack() {
            @Override
            public void onShareSuccess() {
                shareVideoToNet();
            }

            @Override
            public void onShareFailure(String msg) {
                showToast(msg + "分享失败");
            }

            @Override
            public void onCancel() {
                showToast("取消分享");
            }
        });
    }


    /**
     * 分享视频
     */
    private void shareVideoToNet() {
        getSystem(SystemUser.class).shareVideo(mVideoId, new IUserBusynessCallBack() {
            @Override
            public void onResult(boolean result, String arg) {
                showToast(arg);
            }
        });
    }

    /**
     * 更新下载图标
     */
    private void updateDownIcon() {
        if (getSystem(SystemDownloadVideoManager.class).isDownloaded(mVideoId))
            mDownloadImg.setImageResource(R.drawable.icon_download1);
        else {
            mDownloadImg.setImageResource(R.drawable.icon_download0);
        }
    }

    private String getShardUrl() {
        return mSharedUrl + "?vid=" + mVideoId;
    }

    /**
     * 更新点赞图标
     *
     * @param choose
     */
    private void updateZanIcon(boolean choose) {
        if (choose) {
            if ("1".equals(mVideo.top_status)) {
                mCryUpImg.setImageResource(R.drawable.icon_ding1);
            } else {
                mCryUpImg.setImageResource(R.drawable.icon_ding0);
            }
        } else {
            if ("0".equals(mVideo.top_status)) {
                mCryUpImg.setImageResource(R.drawable.icon_ding1);
                mVideo.top_status = "1";
            } else {
                mCryUpImg.setImageResource(R.drawable.icon_ding0);
                mVideo.top_status = "0";
            }
        }
    }

    /**
     * 更新收藏图标
     *
     * @param choose
     */
    private void updateFavIcon(boolean choose) {
        if (choose) {
            if ("1".equals(mVideo.fav_status)) {
                mFavoriteImg.setImageResource(R.drawable.icon_favorite1);
            } else {
                mFavoriteImg.setImageResource(R.drawable.icon_favorite0);
            }
        } else {
            if ("0".equals(mVideo.fav_status)) {
                mFavoriteImg.setImageResource(R.drawable.icon_favorite1);
                mVideo.fav_status = "1";
            } else {
                mFavoriteImg.setImageResource(R.drawable.icon_favorite0);
                mVideo.fav_status = "0";
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!canHideFragmentByPos()) {
            mMediaController.onBackPressed();
        } else {
            setFragmentPos(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SystemManager.getInstance().getSystem(SystemShare.class).onActivityResult(requestCode, resultCode, data);
    }

    private boolean canHideFragmentByPos() {
        switch (mFragmentPos) {
            case 0:
                return false;
            case 1:
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).hideFragment(this, FragmentPlayerFlashComment.class.getCanonicalName());
                return true;
            case 2:
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).hideFragment(this, FragmentPlayerFlashLottery.class.getCanonicalName());
                return true;
            case 3:
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).hideFragment(this, FragmentPlayerFlashVote.class.getCanonicalName());
                return true;
            case 4:
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).hideFragment(this, FragmentPlayerFlashAlbum.class.getCanonicalName());
                return true;
            case 5:
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).hideFragment(this, FragmentPlayerFlashAbout.class.getCanonicalName());
                return true;
            case 6:
                SystemManager.getInstance().getSystem(SystemFragmentManager.class).hideFragment(this, FragmentPlayerFlashDescription.class.getCanonicalName());
                return true;

        }
        return false;
    }

    public void setFragmentPos(int pos) {
        mFragmentPos = pos;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onScreenChange(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    public void onScreenChange(boolean isLandscape) {
        int width = getSystem(SystemCommon.class).mScreenWidth;
        int height = getSystem(SystemCommon.class).mScreenHeight;
        ViewGroup.LayoutParams layoutParams = mVideoView.getLayoutParams();
        ViewGroup.LayoutParams layoutParamsMedia = mMediaController.getLayoutParams();

        if (!isLandscape) {
            //mHandler.post(hide);
            height = width / 16 * 9;
//            mVideoOther.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            //mHandler.post(show);
            int temp = width;
            //width = height;
            //height = temp;
            width = ViewGroup.LayoutParams.MATCH_PARENT;
            height = ViewGroup.LayoutParams.MATCH_PARENT;

//            mVideoOther.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        layoutParams.width = width;
        layoutParams.height = height;

        layoutParamsMedia.width = width;
        layoutParamsMedia.height = height;
        mVideoView.setLayoutParams(layoutParams);
        mMediaController.setLayoutParams(layoutParamsMedia);
        mMediaController.onScreenChange(isLandscape);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d(TAG,"onPause");
        mMediaController.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d(TAG, "onResume");
        mMediaController.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "onDestroy");
        getSystem(SystemAnalyze.class).analyzeVideoEnd();
        mMediaController.onDestory();
    }
}

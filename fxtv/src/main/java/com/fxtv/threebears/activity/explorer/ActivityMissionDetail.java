package com.fxtv.threebears.activity.explorer;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.activity.game.ActivityGame;
import com.fxtv.threebears.activity.h5.ActivityWebView;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Action;
import com.fxtv.threebears.model.Game;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemPreference;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 活动详情
 *
 * @author Android2
 */
public class ActivityMissionDetail extends BaseActivity {
    private String mId;
    private Action mcCenter;
    private Button mBtn;
    //private OnekeyShare oks;
    private String TAG = "ActivityMissionDetail";
    private String testImage;
    private static final String FILE_NAME = "/share_pic.jpg";
    private String mTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_detail);
        mId = getStringExtra("id");
        // typeID = getStringExtra("typeID");
        // String value =
        // getSystem(SystemCommon.class).getInnerValue("video_id",
        // this);
        // if(!"".equals(value)){
        // mId=value;
        // }
        mId = getStringExtra("activity_id");
        JSONObject jsonObject = getSystem(SystemCommon.class).getH5Content(this);
        if (jsonObject != null) {
            String videoId;
            try {
                videoId = jsonObject.getString("event_id");
                if (videoId != null && !"".equals(videoId)) {
                    mId = videoId;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (mId == null || "".equals(mId)) {
            finish();
        }
        mTypeId = getStringExtra("skipType");
        initActionBar();
        initShare();
        getData();
    }

    private void initShare() {
        //ShareSDK.initSDK(this);
        new Thread() {
            public void run() {
                //initImagePath();
            }
        }.start();
    }

	/*protected void initImagePath() {
        try {
			String cachePath = cn.sharesdk.framework.utils.R.getCachePath(this, null);
			testImage = cachePath + FILE_NAME;
			File file = new File(testImage);
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable.share_pic);
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			testImage = null;
		}
	}*/

    private void getData() {
        JsonObject params = new JsonObject();
        params.addProperty("id", mId);
        Utils.showProgressDialog(this);
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_activityInfo, params);
        getSystem(SystemHttp.class).get(this, url, "getMissionDetail", true, true, new RequestCallBack<Action>() {
            @Override
            public void onSuccess(Action data, Response resp) {
                mcCenter = data;
                initView();
            }

            @Override
            public void onFailure(Response resp) {
                findViewById(R.id.activity_mission_detai_btn).setVisibility(View.GONE);
                FrameworkUtils.showToast(ActivityMissionDetail.this, resp.msg);
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });
    }

    private void initView() {
        ImageView img = (ImageView) findViewById(R.id.activity_mission_detail_img);
        TextView title = (TextView) findViewById(R.id.activity_mission_detail_title);
        TextView comment = (TextView) findViewById(R.id.activity_mission_detai_comment);
        mBtn = (Button) findViewById(R.id.activity_mission_detai_btn);
        int width = (FrameworkUtils.getScreenWidth(this) - FrameworkUtils.dip2px(this, 5) * 2);
        LayoutParams layoutParams = img.getLayoutParams();
        layoutParams.height = width / 20 * 9;
        img.setLayoutParams(layoutParams);
//		SystemManager.getInstance().getSystem(SystemImageLoader.class)
//				.displayImageRound(mcCenter.image, img, R.drawable.default_img_banner);
        getSystem(SystemCommon.class).displayRoundedImage(ActivityMissionDetail.this, img, mcCenter.image, 10);
        title.setText(mcCenter.title);
        comment.setText(mcCenter.intro);
        if ("9".equals(mcCenter.type)) {
            mBtn.setVisibility(View.GONE);
        }
        mBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                jump();
                SystemManager.getInstance().getSystem(SystemCommon.class).jump(ActivityMissionDetail.this, mcCenter);
            }
        });
    }

    protected void jump() {
        int type = Integer.parseInt(mcCenter.type);
        Bundle bundle = null;
        switch (type) {
            case 1:
                // 视频播放页
                bundle = new Bundle();
                bundle.putString("video_id", mcCenter.link);
                bundle.putString("skipType", "12");
                FrameworkUtils.skipActivity(this, ActivityVideoPlay.class, bundle);
                break;
            case 3:
                // 主播空间页
                bundle = new Bundle();
                bundle.putString("anchor_id", mcCenter.link);
                FrameworkUtils.skipActivity(this, ActivityAnchorZone.class, bundle);
                break;
            case 4:
                // 我的页
                // ((MainActivity) mContext).jump2Child(4);
                break;
            case 5:
                // 每日任务
                FrameworkUtils.skipActivity(this, ActivityExplorerTask.class);
                break;
            case 6:
                // // 活动详情页
                break;
            case 7:
                // 游戏内页
                Game game = new Game();
                game.id = mcCenter.link;
                bundle = new Bundle();
                bundle.putSerializable("game", game);
                FrameworkUtils.skipActivity(this, ActivityGame.class, bundle);
                break;
            case 8:
                // H5页面
                bundle = new Bundle();
                bundle.putString("url", mcCenter.link);
                bundle.putString("title", "WebView");
                bundle.putString("share_img", mcCenter.image);
                bundle.putString("share_title", mcCenter.title);
                bundle.putBoolean("share_enable", true);
                FrameworkUtils.skipActivity(this, ActivityWebView.class, bundle);
                break;
            case 11:
                // 热点投票
                bundle = new Bundle();
                bundle.putString("id", mcCenter.link);
                FrameworkUtils.skipActivity(this, ActivityVoteDetail.class, bundle);
                break;
            case 12:
                // 扫一扫
                FrameworkUtils.skipActivity(this, ActivityQRCode.class);
                break;
            case 13:
                // 主播圈
                FrameworkUtils.skipActivity(this, ActivityExplorerAnchorCircle.class);
                break;

            default:
                break;
        }
    }

    private void initActionBar() {
        ((TextView) findViewById(R.id.ab_title)).setText("活动详情");
        ImageView leftImg = (ImageView) findViewById(R.id.ab_left_img);
        leftImg.setVisibility(View.VISIBLE);
        leftImg.setImageResource(R.drawable.icon_arrow_left1);
        leftImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 添加分享内容
     */
    /*protected void share() {
        // TODO Auto-generated method stub
		oks = new OnekeyShare();
		oks.setCallback(new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				Logger.d(TAG, "share,onError...");
				runOnUiThread(new Runnable() {
					public void run() {
						FrameworkUtils.showToast(ActivityMissionDetail.this, "分享失败");
					}
				});
			}

			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				Logger.d(TAG, "share,onComplete...");
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				Logger.d(TAG, "share,onCancel...");
				FrameworkUtils.showToast(ActivityMissionDetail.this, "取消分享");
			}
		});
		oks.setTitle(mcCenter.title);
		oks.setTitleUrl(getShardUrl());
		oks.setText("飞熊视频，内容丰富无广告，还有视频抽奖活动.@飞熊视频" + getShardUrl());
		oks.setImageUrl(mcCenter.image);
		oks.setUrl(getShardUrl());
		oks.setFilePath(testImage);
		oks.setComment(getString(R.string.share));
		oks.setSite(getString(R.string.app_name));
		oks.setSiteUrl(getShardUrl());
		oks.setVenueName("ShareSDK");
		oks.setVenueDescription("This is a beautiful place!");
		oks.setLatitude(23.056081f);
		oks.setLongitude(113.385708f);
		oks.disableSSOWhenAuthorize();
		oks.show(ActivityMissionDetail.this);
	}*/
    private String getShardUrl() {
        String bashUrl = "http://www.feixiong.tv/h5/e.html?";
        StringBuilder builder = new StringBuilder(bashUrl);
        builder.append("uc=").append(getSystem(SystemPreference.class).getUC(2)).append("&");
        builder.append("eid=").append(mcCenter.id);
        return builder.toString();
    }
}

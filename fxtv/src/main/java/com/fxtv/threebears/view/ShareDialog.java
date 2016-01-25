package com.fxtv.threebears.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemShare;
import com.fxtv.threebears.R;
import com.fxtv.threebears.system.SystemUser;

/**
 * 用于第三方分享的dialog
 */
public class ShareDialog extends Dialog {

    private ImageView mQQShare, mQZOneShare, mWeChatShare, mWeChatCircleShare, mSinaShare;

    private Button mCancel;

    private ShareCallBack mShareCallBack;

    private Context mContext;

    private ShareModel mShareModel;

    private String TAG = "ShareDialog";


    public ShareDialog(Context context, ShareModel shareModel, ShareCallBack callBack) {
        super(context, R.style.my_dialog);
        mContext = context;
        mShareModel = shareModel;
        if (SystemManager.getInstance().getSystem(SystemUser.class).isLogin()) {
            if (mShareModel.shareUrl.contains("?")) {
                mShareModel.shareUrl = mShareModel.shareUrl + "&uid=" + SystemManager.getInstance().getSystem(SystemUser.class).mUser.uid;
            } else {
                mShareModel.shareUrl = mShareModel.shareUrl + "?uid=" + SystemManager.getInstance().getSystem(SystemUser.class).mUser.uid;
            }
        } else {
            if (mShareModel.shareUrl.contains("?")) {
                mShareModel.shareUrl = mShareModel.shareUrl + "&uid=0";
            } else {
                mShareModel.shareUrl = mShareModel.shareUrl + "?uid=0";
            }
        }
        mShareCallBack = callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_share_layout);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        mQQShare = (ImageView) findViewById(R.id.qq_share);
        mQZOneShare = (ImageView) findViewById(R.id.qzone_share);
        mWeChatShare = (ImageView) findViewById(R.id.wechat_share);
        mWeChatCircleShare = (ImageView) findViewById(R.id.wechat_cirle_share);
        mSinaShare = (ImageView) findViewById(R.id.sina_share);
        mCancel = (Button) findViewById(R.id.cancel_share);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog.this.dismiss();
            }
        });

        mQQShare.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            SystemManager.getInstance().getSystem(SystemShare.class).share(mContext, SystemShare.SHARE_TYPE_QQ, mShareModel, new SystemShare.ICallBackSystemShare() {
                                                @Override
                                                public void onSuccess() {
                                                    Logger.i(TAG, "onSuccess");
                                                    mShareCallBack.onShareSuccess();
                                                    ShareDialog.this.dismiss();
                                                }

                                                @Override
                                                public void onFailure(String msg) {
                                                    Logger.i(TAG, "onFailure_msg=" + msg);
                                                    mShareCallBack.onShareFailure(msg);
                                                    ShareDialog.this.dismiss();
                                                }

                                                @Override
                                                public void onCancle() {
                                                    Logger.i(TAG, "onCancle");
                                                    mShareCallBack.onCancel();
                                                    ShareDialog.this.dismiss();
                                                }
                                            });
                                        }

                                    }

        );

        mQZOneShare.setOnClickListener(new View.OnClickListener()

                                       {
                                           @Override
                                           public void onClick(View v) {
                                               SystemManager.getInstance().getSystem(SystemShare.class).share(mContext, SystemShare.SHARE_TYPE_QQ_QZONE, mShareModel, new SystemShare.ICallBackSystemShare() {
                                                   @Override
                                                   public void onSuccess() {
                                                       Logger.i(TAG, "onSuccess");
                                                       mShareCallBack.onShareSuccess();
                                                       ShareDialog.this.dismiss();
                                                   }

                                                   @Override
                                                   public void onFailure(String msg) {
                                                       Logger.i(TAG, "onFailure_msg=" + msg);
                                                       mShareCallBack.onShareFailure(msg);
                                                       ShareDialog.this.dismiss();
                                                   }

                                                   @Override
                                                   public void onCancle() {
                                                       Logger.i(TAG, "onCancle");
                                                       mShareCallBack.onCancel();
                                                       ShareDialog.this.dismiss();
                                                   }
                                               });

                                           }
                                       }

        );

        mWeChatShare.setOnClickListener(new View.OnClickListener()

                                        {
                                            @Override
                                            public void onClick(View v) {
                                                SystemManager.getInstance().getSystem(SystemShare.class).share(mContext, SystemShare.SHARE_TYPE_WECHAT, mShareModel, new SystemShare.ICallBackSystemShare() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Logger.i(TAG, "onSuccess");
                                                        mShareCallBack.onShareSuccess();
                                                        ShareDialog.this.dismiss();
                                                    }

                                                    @Override
                                                    public void onFailure(String msg) {
                                                        Logger.i(TAG, "onFailure_msg=" + msg);
                                                        mShareCallBack.onShareFailure(msg);
                                                        ShareDialog.this.dismiss();
                                                    }

                                                    @Override
                                                    public void onCancle() {
                                                        Logger.i(TAG, "onCancle");
                                                        mShareCallBack.onCancel();
                                                        ShareDialog.this.dismiss();
                                                    }
                                                });

                                            }
                                        }

        );

        mWeChatCircleShare.setOnClickListener(new View.OnClickListener()

                                              {
                                                  @Override
                                                  public void onClick(View v) {
                                                      SystemManager.getInstance().getSystem(SystemShare.class).share(mContext, SystemShare.SHARE_TYPE_WECHAT_CIRCLE, mShareModel, new SystemShare.ICallBackSystemShare() {
                                                          @Override
                                                          public void onSuccess() {
                                                              Logger.i(TAG, "onSuccess");
                                                              mShareCallBack.onShareSuccess();
                                                              ShareDialog.this.dismiss();
                                                          }

                                                          @Override
                                                          public void onFailure(String msg) {
                                                              Logger.i(TAG, "onFailure_msg=" + msg);
                                                              mShareCallBack.onShareFailure(msg);
                                                              ShareDialog.this.dismiss();
                                                          }

                                                          @Override
                                                          public void onCancle() {
                                                              Logger.i(TAG, "onCancle");
                                                              mShareCallBack.onCancel();
                                                              ShareDialog.this.dismiss();
                                                          }
                                                      });

                                                  }
                                              }

        );

        mSinaShare.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View v) {
                                              SystemManager.getInstance().getSystem(SystemShare.class).share(mContext, SystemShare.SHARE_TYPE_SINA, mShareModel, new SystemShare.ICallBackSystemShare() {
                                                  @Override
                                                  public void onSuccess() {
                                                      Logger.i(TAG, "onSuccess");
                                                      mShareCallBack.onShareSuccess();
                                                      ShareDialog.this.dismiss();
                                                  }

                                                  @Override
                                                  public void onFailure(String msg) {
                                                      Logger.i(TAG, "onFailure_msg=" + msg);
                                                      mShareCallBack.onShareFailure(msg);
                                                      ShareDialog.this.dismiss();
                                                  }

                                                  @Override
                                                  public void onCancle() {
                                                      Logger.i(TAG, "onCancle");
                                                      mShareCallBack.onCancel();
                                                      ShareDialog.this.dismiss();
                                                  }
                                              });

                                          }
                                      }

        );

    }

    public void setCallback(ShareCallBack shareCallBack) {
        mShareCallBack = shareCallBack;
    }

    public interface ShareCallBack {

        void onShareSuccess();

        void onShareFailure(String msg);

        void onCancel();

    }

}

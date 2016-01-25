package com.fxtv.threebears.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemShare;
import com.fxtv.threebears.R;

/**
 * Created by Administrator on 2015/12/18.
 */
public class SharePopuWindow extends PopupWindow {
    private Context mContext;
    private ViewGroup mRootView;
    public SharePopuWindow(Context context) {
        mContext = context;
        initSharePopView();
    }

    private void initSharePopView() {
        mRootView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.view_share, null);
        setContentView(mRootView);
        setBackgroundDrawable(mContext.getResources().getDrawable(R.color.color_white));
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.SharePopuWindowAnimation);
        setFocusable(true);
        setOutsideTouchable(true);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                    onDestory();
            }
        });
    }

    public void showPopWindow(final ViewGroup viewGroup, final ShareModel model, final SystemShare.ICallBackSystemShare callBack) {
        showAtLocation(viewGroup, Gravity.BOTTOM, 0, 0);
        if (mRootView != null) {
            mRootView.findViewById(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemManager.getInstance().getSystem(SystemShare.class).share(mContext, SystemShare.SHARE_TYPE_QQ, model, callBack);
                }
            });
            mRootView.findViewById(R.id.share_qzone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemManager.getInstance().getSystem(SystemShare.class).share(mContext, SystemShare.SHARE_TYPE_QQ_QZONE, model, callBack);
                }
            });
            mRootView.findViewById(R.id.share_wechat).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemManager.getInstance().getSystem(SystemShare.class).share(mContext, SystemShare.SHARE_TYPE_WECHAT, model, callBack);
                }
            });
            mRootView.findViewById(R.id.share_wechatcircle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemManager.getInstance().getSystem(SystemShare.class).share(mContext, SystemShare.SHARE_TYPE_WECHAT_CIRCLE, model, callBack);
                }
            });
            mRootView.findViewById(R.id.share_sina).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemManager.getInstance().getSystem(SystemShare.class).share(mContext, SystemShare.SHARE_TYPE_SINA, model, callBack);
                }
            });
            mRootView.findViewById(R.id.share_cancle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        dismiss();
                }
            });
        }
    }

    public void  onDestory(){
        if (mContext!=null){
            mContext=null;
        }
       if (mRootView!=null){
           mRootView=null;

       }
    }


}

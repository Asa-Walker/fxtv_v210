package com.fxtv.framework.system.components;

import android.content.Intent;

import com.fxtv.framework.model.ShareModel;

/**
 * Created by Administrator on 2015/12/14.
 */
public class BaseShareComponent extends  BaseComponent{

    public void share(final ShareModel model){
    }

    public void onNewIntent(Intent intent){}

    public void onActivityResult(int requestCode,int resultCode,Intent intent){

    }

    public void destory(){}
}

package com.fxtv.framework.system.callback;


import com.fxtv.framework.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 必须重写
 *  void onSuccess(T data,Response resp);
    void onFailure(Response resp);
    void onComplete();
    的RequestCallBack
    要使用只重写onSuccess的callBack，请使用NewRequestSimpleCallBack

 * @param <T> 想要接收的类型 List<Game> 、TopicMessage……
 * Created by wzh on 2015/1/4.
 */
public abstract class RequestCallBack<T> implements CallBack<T> {
    public Type respType;

    public RequestCallBack() {
        //获取接口泛型T的class，Type，必须要在子类才能获取Interface的T
        Type genType = getClass().getGenericSuperclass();
        if(genType instanceof ParameterizedType){
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if(params!=null && params.length>0)
                this.respType = params[0];
        }
        //Class<T> entityClass = (Class) params[0]; //获取 class可以用此代码

        Logger.d("RequestCallBack", "respType==?" + this.respType);
    }
}
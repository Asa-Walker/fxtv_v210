package com.fxtv.framework.model;

/**
 * Created by wzh on 2015/12/30.
 * 返回结果  T 为想要的泛型类型，支持对象和List<T>
 */
public class Response<T> {
    public int code;
    public String msg;
    public T data;
    public long time;
    public boolean fromCache;//程序独有，返回结果并没有此字段
    //{"code":"4000","message":"没有更多内容","data":"","time":"1451456649"}

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", time=" + time +
                ", fromCache=" + fromCache +
                '}';
    }
}

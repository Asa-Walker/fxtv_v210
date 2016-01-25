package com.fxtv.framework.model;


import com.google.gson.JsonObject;

/**
 * Created by Administrator on 2016/1/4.
 */
public class RequestHead {
    private RequestHead() {
    }

    public RequestHead(String module, String api) {
        this.module = module;
        this.api = api;
    }

    public JsonObject params;
    public String uri, uc, module, api;
}

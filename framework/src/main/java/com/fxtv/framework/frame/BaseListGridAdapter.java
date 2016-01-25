package com.fxtv.framework.frame;

import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2015/12/7.
 * ListView GridView通用的适配器
 */
public abstract class BaseListGridAdapter<VoItem> extends BaseAdapter {
    private List<VoItem> listData;

    public BaseListGridAdapter() {
    }

    public BaseListGridAdapter(List<VoItem> listData) {
        this.listData = listData;
    }

    public void setListData(List<VoItem> listData) {
        //if(!this.listData.equals(listData)){
        this.listData = listData;
        notifyDataSetChanged();
        //}
    }

    public void onDestroy() {
        this.listData = null;
    }

    public void addData(List<VoItem> listData) {
        if (this.listData == null)
            this.listData = listData;
        else
            this.listData.addAll(listData);
        notifyDataSetChanged();
    }

    public List<VoItem> getListData() {
        return listData;
    }

    @Override
    public int getCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public VoItem getItem(int i) {
        return listData == null ? null : listData.get(i);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }
}

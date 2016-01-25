package com.fxtv.threebears.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemCommon;

import java.util.List;

/**
 * 下载列表的XListVIew的适配器
 *
 * @author 薛建浩
 */
public class XListViewAdapter extends BaseListGridAdapter<Video> {

    private Context context;

    /**
     * 改变状态用于切换下载中和完成下载两个lisiView的显示方式
     * true--切换成完成下载的样式
     * false--切换成正在下载的样式
     */
    public boolean changeState = true;


    public XListViewAdapter() {
        super(null);
    }

    public XListViewAdapter(Context context) {
        super(null);
        this.context = context;
    }

    public XListViewAdapter(Context context, List<Video> list) {
        super(list);
        this.context = context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_download, null);
            viewHolder = new ViewHolder();

            viewHolder.img = (ImageView) convertView
                    .findViewById(R.id.item_gv_game_img);
            viewHolder.title = (TextView) convertView
                    .findViewById(R.id.item_gv_game_title);
            viewHolder.name = (TextView) convertView
                    .findViewById(R.id.fragment_tab_anchor_space_name);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.author = (TextView) convertView
                    .findViewById(R.id.author);
            viewHolder.lastTime = (TextView) convertView
                    .findViewById(R.id.last_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Video video = getItem(position);
//        ImageLoader.getInstance().displayImage(video.image,
//                viewHolder.img);
        SystemManager.getInstance().getSystem(SystemCommon.class).displayDefaultImage(context, viewHolder.img, video.image);
        viewHolder.title.setText(video.title);
        viewHolder.name.setText(video.game_title);
        viewHolder.time.setText(video.duration);
        if (changeState) {
            viewHolder.author.setText(video.anchor_name);
            viewHolder.lastTime.setText(video.publish_time);
        } else {
            viewHolder.author.setText(video.anchor_name);
            viewHolder.lastTime.setText(video.publish_time);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView img;
        TextView title;
        TextView name;
        TextView time;
        TextView author;
        TextView lastTime;
    }
}

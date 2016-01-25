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
 * 下载中的列表的XListVIew的适配器
 *
 * @author 薛建浩
 */
public class DownloadingAdapter extends BaseListGridAdapter<Video> {

    private Context context;

    public DownloadingAdapter() {
        super(null);
    }

    public DownloadingAdapter(Context context) {
        super(null);
        this.context = context;
    }

    public DownloadingAdapter(Context context, List<Video> list) {
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

            viewHolder.img = (ImageView) convertView.findViewById(R.id.item_gv_game_img);
            viewHolder.title = (TextView) convertView.findViewById(R.id.item_gv_game_title);
            viewHolder.name = (TextView) convertView
                    .findViewById(R.id.fragment_tab_anchor_space_name);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.downloadstate = (TextView) convertView.findViewById(R.id.author);
            viewHolder.progress = (TextView) convertView.findViewById(R.id.last_time);
            viewHolder.downloadImg = (ImageView) convertView.findViewById(R.id.pause_start_download);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Video video = getItem(position);
//		ImageLoader.getInstance().displayImage(video.image, viewHolder.img);
        SystemManager.getInstance().getSystem(SystemCommon.class).displayDefaultImage(context, viewHolder.img, video.image);
        viewHolder.title.setText(video.title);
        viewHolder.name.setText(video.game_title);
        viewHolder.time.setText(video.duration);
        viewHolder.downloadstate.setText(video.video_download_state);
        viewHolder.progress.setText(video.video_download_progress);
        if (video.video_download_Img != 0) {
            viewHolder.downloadImg.setImageResource(video.video_download_Img);
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView img;
        TextView title;
        TextView name;
        TextView time;
        // 下载状态
        public TextView downloadstate;
        public TextView progress;
        public ImageView downloadImg;
    }
}

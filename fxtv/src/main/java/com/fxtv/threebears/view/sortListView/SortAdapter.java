package com.fxtv.threebears.view.sortListView;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.system.SystemCommon;

import java.util.List;

public class SortAdapter extends BaseListGridAdapter<Anchor> implements SectionIndexer {
    private Fragment mFragment;

    public SortAdapter(Fragment fragment, List<Anchor> list) {
        super(list);
        this.mFragment = fragment;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<Anchor> list) {
        setListData(list);
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final Anchor mContent = getItem(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mFragment.getActivity()).inflate(
                    R.layout.item_sortlistview, null);
            viewHolder.tvTitle = (TextView) view
                    .findViewById(R.id.answer_content);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.img = (ImageView) view
                    .findViewById(R.id.answer_user_pic);
            viewHolder.subAndVideo = (TextView) view
                    .findViewById(R.id.subscribe_and_videos);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.anchor_first_name);
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        viewHolder.tvTitle.setText(mContent.name);
//		SystemManager.getInstance().getSystem(SystemImageLoader.class)
//				.displayImageDefault(mContent.image, viewHolder.img);
        SystemManager.getInstance().getSystem(SystemCommon.class).displayDefaultImage(mFragment, viewHolder.img, mContent.image);
        viewHolder.subAndVideo.setText("视频数 : " + mContent.video_num
                + " 订阅数 : " + mContent.order_num);
        return view;

    }

    final static class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
        ImageView img;
        TextView subAndVideo;

    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return getItem(position).anchor_first_name.charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = getItem(i).anchor_first_name;
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
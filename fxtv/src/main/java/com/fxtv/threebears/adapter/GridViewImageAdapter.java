package com.fxtv.threebears.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.threebears.R;
import com.fxtv.threebears.model.Avatar;
import com.fxtv.threebears.system.SystemCommon;

import java.util.List;

/**
 * 图库gridView的适配器
 * 
 * @author 薛建浩
 * 
 */
public class GridViewImageAdapter extends BaseListGridAdapter<Avatar> {

	private Context context;

	public GridViewImageAdapter(List<Avatar> list, Context context) {
		super(list);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(context, R.layout.item_photo, null);
			holder.img = (ImageView) convertView
					.findViewById(R.id.fragment_image_depot_user_pic);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.fragment_image_depot_choose);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		// String url = list.get(position).avatar_url;
		// ImageLoader loader = ImageLoader.getInstance();
		// loader.init(ImageLoaderConfiguration.createDefault(context));
		// loader.displayImage(url, holder.img);
		/*SystemManager.getInstance().getSystem(SystemImageLoader.class)
				.displayImageSquare(getItem(position).image, holder.img);*/
		SystemManager.getInstance().getSystem(SystemCommon.class).displayDefaultImage(context, holder.img, getItem(position).image, SystemCommon.SQUARE);
		holder.icon.setImageResource(getItem(position).avatar_choice);

		return convertView;
	}

	public class Holder {
		ImageView img;
		public ImageView icon;
	}

}

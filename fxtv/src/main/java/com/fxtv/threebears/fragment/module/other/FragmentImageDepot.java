package com.fxtv.threebears.fragment.module.other;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.adapter.GridViewImageAdapter;
import com.fxtv.threebears.model.Avatar;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * 图库fragment
 * 
 * @author 薛建浩
 * 
 */
public class FragmentImageDepot extends BaseFragment {
	private View rootView;
	private List<Avatar> AvatarList;
	private GridViewImageAdapter adapter;
	private PullToRefreshGridView imageGv;
	private Resources myResources;
	/**
	 * 用于计数
	 */
	private int countPosition = -1, choosePosition = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_image_depot, container, false);
		myResources = getResources();
		initData();
		getData();
		setListener();
		return rootView;
	}

	private void setListener() {
		imageGv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String status = AvatarList.get(position).status;
				if (status.equals("0")) {
					Utils.showProgressDialog(getActivity());
					if (countPosition != -1) {
						AvatarList.get(countPosition).avatar_choice = R.drawable.unchoose_icon;
					} else {
						if (AvatarList.get(choosePosition).avatar_choice != R.drawable.unchoose_icon) {
							AvatarList.get(choosePosition).avatar_choice = R.drawable.unchoose_icon;
						}
					}
					AvatarList.get(position).avatar_choice = R.drawable.choose_icon;
					adapter.notifyDataSetChanged();
					sendMsgToService(position);
				}
				// countPosition = position;
			}
		});
	}

	/**
	 * 设置头像传给服务器
	 * 
	 * @param position
	 */
	protected void sendMsgToService(final int position) {
		JsonObject params = new JsonObject();
		params.addProperty("id", AvatarList.get(position).id);

		getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.USER, ApiType.USER_setAvatar,params),false, false,  new RequestCallBack<String>() {
			@Override
			public void onSuccess(String data, Response resp) {
				getSystem(SystemUser.class).mUser.image = AvatarList
						.get(position).image;
			}

			@Override
			public void onFailure(Response resp) {
				showToast(resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
				countPosition = position;
			}});

	}

	private void initData() {
		imageGv = (PullToRefreshGridView) rootView.findViewById(R.id.fragment_image_depot_gv);
		AvatarList = new ArrayList<Avatar>();
		adapter = new GridViewImageAdapter(AvatarList, getActivity());
		imageGv.setAdapter(adapter);
	}

	/**
	 * 获取网络数据
	 */
	private void getData() {
		JsonObject params = new JsonObject();
		Utils.showProgressDialog(getActivity());
		getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.USER, ApiType.USER_avatarList, params), "userPhotosOfAll", true, true, new RequestCallBack<List<Avatar>>() {
			@Override
			public void onSuccess(List<Avatar> data, Response resp) {
				if (data != null && data.size() != 0) {
					for (int i = 0; i < data.size(); i++) {
						if (data.get(i).image.equals(getSystem(SystemUser.class).mUser.image)) {
							data.get(i).avatar_choice = R.drawable.choose_icon;
							choosePosition = i;
						} else {
							data.get(i).avatar_choice = R.drawable.unchoose_icon;
						}
					}
					AvatarList.addAll(data);
					adapter.notifyDataSetChanged();
				} else {
					FrameworkUtils.showToast(getActivity(),
							myResources.getString(R.string.notice_no_more_data));
				}
			}

			@Override
			public void onFailure(Response resp) {
				FrameworkUtils.showToast(getActivity(), resp.msg);
			}

			@Override
			public void onComplete() {
				Utils.dismissProgressDialog();
			}
		});

	}
}

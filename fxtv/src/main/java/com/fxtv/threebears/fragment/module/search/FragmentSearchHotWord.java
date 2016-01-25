package com.fxtv.threebears.fragment.module.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.adapter.SearchAdapter;
import com.fxtv.threebears.model.HotWord;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearchHotWord extends BaseFragment {
	private ViewGroup mRoot;
	private GridView mGridView;
	private Context mContext;
	private List<HotWord> mHotWords = new ArrayList<HotWord>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_search_hotword, container, false);
		initView();
		initData();
		initEvent();
		return mRoot;
	}

	private void initEvent() {
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
				FragmentTransaction transaction = mFragmentManager.beginTransaction();
				if (FragmentSearchResult.fragmentSearchResultinstance == null) {
					FragmentSearchResult fragmentsearch = new FragmentSearchResult();
					Bundle mBundle = new Bundle();
					mBundle.putString("key", mHotWords.get(position).name);
					fragmentsearch.setArguments(mBundle);
					transaction.replace(R.id.search_fragment, fragmentsearch).commit();
				} else {
					Bundle mBundle = new Bundle();
					mBundle.putString("key", mHotWords.get(position).name);
					FragmentSearchResult.fragmentSearchResultinstance.setArguments(mBundle);
					transaction.hide(FragmentSearchHotWord.this)
							.show(FragmentSearchResult.fragmentSearchResultinstance).commit();
					// transaction.replace(R.id.search_fragment,
					// FragmentSearchResult.fragmentSearchResultinstance).commit();
					// FragmentSearchResult.fragmentSearchResultinstance.initData(mHotWords
					// .get(position).name);
				}
			}
		});
	}

	private void initData() {
		JsonObject params = new JsonObject();
		params.addProperty("page", "1");
		params.addProperty("pagesize", "9");
		Utils.showProgressDialog((Activity) mContext);

		getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.BASE, ApiType.BASE_hotWord, params), "searchHotWordApi", true, true, new RequestCallBack<List<HotWord>>() {
			@Override
			public void onSuccess(List<HotWord> data, Response resp) {
				mHotWords.addAll(data);
				mGridView.setAdapter(new SearchAdapter(mContext, mHotWords));
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

	private void initView() {
		mContext = getActivity();
		mGridView = (GridView) mRoot.findViewById(R.id.search_hotword_gridview);
	}
}

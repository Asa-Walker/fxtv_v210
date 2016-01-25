package com.fxtv.threebears.fragment.module.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.ResponsibilityHander;
import com.fxtv.framework.frame.SystemManager;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.framework.widget.xlistview.XListView.IXListViewListener;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemDownloadVideoManager;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearchResult extends BaseFragment {
    private ViewGroup mRoot;
    //private List<Video> mDataList = new ArrayList<Video>();
    private XListView mListView;
    private ViewGroup mHeader;
    private ViewGroup mAnchorLayout;
    private List<Anchor> mListData = new ArrayList<Anchor>();
    private String keyString;
    private TextView anchorTitle;
    private TextView videoTitle;
    View layoutNoResult;
    View layoutHaveResult;
    MyAdapter myAdapter;
    private int mPageNum = 0;
    private int videoresult = 1;
    private int anchorresult = 1;
    private int mPageSize = 20;
    public static FragmentSearchResult fragmentSearchResultinstance = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_search_result, container, false);
        fragmentSearchResultinstance = this;
        String strings = getArguments().getString("key");
        keyString = strings;
        initView();
        initHeader();
        initData(strings);
        initLinstener();
        return mRoot;
    }

    private void initLinstener() {
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                if (position <= 1) {
                    return;
                } else {
                    bundle.putString("video_id", myAdapter.getItem(position - 2).id);
                    bundle.putString("skiptype", "62");
                    FrameworkUtils.skipActivity(getActivity(), ActivityVideoPlay.class, bundle);
                }
            }
        });
    }

    public void initData(final String key) {
        keyString = key;
        anchorTitle.setText("“" + keyString + "”的相关主播");
        videoTitle.setText("“" + keyString + "”的搜索结果");
        mPageNum = 0;
        if (myAdapter.getListData() != null) {
            myAdapter.getListData().clear();
            myAdapter.notifyDataSetChanged();
        }
        if (mListData != null)
            mListData.clear();
        if (getActivity() == null)
            return;
        Utils.showProgressDialog(getActivity());
        ResponsibilityHander anchorHander = new ResponsibilityHander() {
            @Override
            public void handleRequest(boolean handle) {
                getAnchorData(this, key);
            }
        };
        ResponsibilityHander videoHander = new ResponsibilityHander() {
            @Override
            public void handleRequest(boolean handle) {
                getVideoData(this, key);
            }
        };
        ResponsibilityHander endHander = new ResponsibilityHander() {
            @Override
            public void handleRequest(boolean handle) {
                if (anchorresult == 0 && videoresult == 0) {
                    FrameworkUtils.showToast(getActivity(), getString(R.string.notice_no_more_data));
                }
                Utils.dismissProgressDialog();
            }
        };
        anchorHander.setSuccessor(videoHander);
        videoHander.setSuccessor(endHander);
        anchorHander.handleRequest(true);
    }

    private void getVideoData(final ResponsibilityHander responsibilityHander, String KeyWord) {
        mPageNum++;
        JsonObject params = new JsonObject();
        params.addProperty("keyword", KeyWord);
        params.addProperty("page", mPageNum + "");
        params.addProperty("pagesize", mPageSize + "");

        getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.BASE, ApiType.BASE_searchVideo, params), "searchVideoApi", false, false, new RequestCallBack<List<Video>>() {
            @Override
            public void onSuccess(List<Video> data, Response resp) {
                if (data == null || data.size() == 0) {
                    videoresult = 0;
                    mListView.stopLoadMore();
                    FrameworkUtils.showToast(getActivity(), getString(R.string.notice_no_more_data));
                } else {
                    myAdapter.addData(data);
                    mListView.stopLoadMore();
                }
            }

            @Override
            public void onFailure(Response resp) {
                FrameworkUtils.showToast(getActivity(), resp.msg);
            }

            @Override
            public void onComplete() {
                if (responsibilityHander != null) {
                    responsibilityHander.getSuccessor().handleRequest(true);
                }
            }
        });

    }

    private void initHeader() {
        mAnchorLayout = (ViewGroup) mHeader.findViewById(R.id.fragment_tab_self_mybook_header_hs);
    }

    private void updateOrderAnchors() {
        mAnchorLayout.removeAllViews();
        for (int i = 0; i < mListData.size(); i++) {
            final Anchor anchor = mListData.get(i);
            final View item = mLayoutInflater.inflate(R.layout.item_anchor, null);
            View layout = item.findViewById(R.id.layout);
            if (i != 0) {
                LayoutParams layoutParams = (LayoutParams) layout.getLayoutParams();
                layoutParams.leftMargin = FrameworkUtils.dip2px(getActivity(), 22);
                layout.setLayoutParams(layoutParams);
            }
            ImageView img = (ImageView) item.findViewById(R.id.photo);
            TextView name = (TextView) item.findViewById(R.id.name);
            /*SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageSquare(anchor.avatar, img);*/

            getSystem(SystemCommon.class).displayDefaultImage(FragmentSearchResult.this, img, anchor.avatar, SystemCommon.SQUARE);
            name.setText(anchor.name);
            final Button book = (Button) item.findViewById(R.id.book);
            if (anchor.order_status.equals("1")) {
                book.setText("已订阅");
                book.setBackgroundResource(R.drawable.shape_rectangle_circular_check);
            } else {
                book.setText("订阅");
                book.setBackgroundResource(R.drawable.selector_btn2);
            }
            book.setVisibility(View.VISIBLE);
            book.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!getSystem(SystemUser.class).isLogin()) {
                        getSystem(SystemCommon.class).noticeAndLogin(getActivity());
                        return;
                    }
                    if (anchor.order_status.equals("1")) {
                        return;
                    }
                    orderAnchor(book, anchor);
                }
            });
            img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("anchor_id", anchor.id);
                    bundle.putString("type", "61");
                    FrameworkUtils.skipActivity(getActivity(), ActivityAnchorZone.class, bundle);
                }
            });
            mAnchorLayout.addView(item);
        }
    }

    private void initView() {
        mListView = (XListView) mRoot.findViewById(R.id.search_video_list);
        mHeader = (ViewGroup) mLayoutInflater.inflate(R.layout.header_fragment_search_result, null);
        mListView.addHeaderView(mHeader);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(false);
        mListView.setEmptyViewEnable(false);
        mListView.setPageSize(mPageSize);
        mListView.setEmptyText("正在寻找“" + keyString + "”相关的内容");
        myAdapter = new MyAdapter(null);
        mListView.setAdapter(myAdapter);
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                getVideoData(null, keyString);
            }
        });
        anchorTitle = (TextView) mHeader.findViewById(R.id.anchor_title);
        videoTitle = (TextView) mHeader.findViewById(R.id.video_title);
        anchorTitle.setText("“" + keyString + "”的相关主播");
        videoTitle.setText("“" + keyString + "”的搜索结果");
        layoutNoResult = mHeader.findViewById(R.id.no_result);
        layoutHaveResult = mHeader.findViewById(R.id.have_result);
    }

    private void getAnchorData(final ResponsibilityHander responsibilityHander, String KeyWord) {
        JsonObject params = new JsonObject();
        if (getSystem(SystemUser.class).isLogin())
            params.addProperty("user_id", getSystem(SystemUser.class).mUser.user_id);
        params.addProperty("keyword", KeyWord);
        params.addProperty("page", "1");
        params.addProperty("pagesize", "99");

        getSystem(SystemHttp.class).get(getActivity(), Utils.processUrl(ModuleType.BASE, ApiType.BASE_searchAnchor, params), "searchAnchorApi", false, false, new RequestCallBack<List<Anchor>>() {

            @Override
            public void onSuccess(List<Anchor> data, Response resp) {
                layoutNoResult.setVisibility(View.GONE);
                layoutHaveResult.setVisibility(View.VISIBLE);
                mListData = data;
                if (mListData == null || mListData.size() == 0) {
                    anchorresult = 0;
                    layoutNoResult.setVisibility(View.VISIBLE);
                    layoutHaveResult.setVisibility(View.GONE);
                    return;
                }
                updateOrderAnchors();
            }

            @Override
            public void onFailure(Response resp) {
                FrameworkUtils.showToast(getActivity(), resp.msg);
            }

            @Override
            public void onComplete() {
                if (responsibilityHander != null) {
                    responsibilityHander.getSuccessor().handleRequest(true);
                }
            }});


    }

    private void orderAnchor(final Button book, final Anchor anchor) {
        Utils.showProgressDialog(getActivity());
        // getSystemHttpRequests()
        // .userOrderOrDisAnchorApi(getActivity(), anchor, "1", new
        // RequestCallBack() {
        //
        // @Override
        // public void onSuccess(String json, boolean fromCache) {
        // showToast(getString(R.string.notice_order_success));
        // book.setText("已订阅");
        // book.setBackgroundResource(R.drawable.shape_rectangle_circular_check);
        // }
        //
        // @Override
        // public void onFailure(String msg, boolean fromCache) {
        // showToast(msg);
        // }
        //
        // @Override
        // public void onComplete() {
        // Utils.dismissProgressDialog();
        // }
        // });
    }

    class MyAdapter extends BaseListGridAdapter<Video> {
        public MyAdapter(List<Video> listData) {
            super(listData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_video, null);
                viewHolder = new ViewHolder();
                viewHolder.imgJiang = (ImageView) convertView.findViewById(R.id.present_icon);
                viewHolder.imgdownload = (ImageView) convertView.findViewById(R.id.down);
                viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.name = (TextView) convertView.findViewById(R.id.lable1);
                viewHolder.time = (TextView) convertView.findViewById(R.id.lable2);
                viewHolder.author = (TextView) convertView.findViewById(R.id.lable3);
                viewHolder.lastTime = (TextView) convertView.findViewById(R.id.lable4);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Video video = getItem(position);
            if (video.lottery_status.equals("1")) {
                viewHolder.imgJiang.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imgJiang.setVisibility(View.GONE);
            }
            /*SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(video.image, viewHolder.img);*/

            getSystem(SystemCommon.class).displayDefaultImage(FragmentSearchResult.this, viewHolder.img, video.image);
            viewHolder.title.setText(video.title);
            viewHolder.name.setText(video.game_title);
            viewHolder.time.setText(video.duration);
            viewHolder.author.setText(video.anchor_name);
            viewHolder.lastTime.setText(video.publish_time);
            final ImageView imgdownload = viewHolder.imgdownload;
            viewHolder.imgdownload.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVideoChoose = video;
                    mImageDownLoad = imgdownload;
                    getSystem(SystemCommon.class).showDownloadDialog(getActivity(), mVideoChoose, mImageDownLoad);
                }
            });
            viewHolder.author.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("anchor_id", video.anchor.id);
                    FrameworkUtils.skipActivity(getActivity(), ActivityAnchorZone.class, bundle);
                }
            });
            if (getSystem(SystemDownloadVideoManager.class).isDownloaded(video.id))
                viewHolder.imgdownload.setImageResource(R.drawable.icon_download1);
            else {
                viewHolder.imgdownload.setImageResource(R.drawable.icon_download0);
            }
            return convertView;
        }

        class ViewHolder {
            ImageView imgJiang;
            ImageView imgdownload;
            ImageView img;
            TextView title;
            TextView name;
            TextView time;
            TextView author;
            TextView lastTime;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SystemManager.getInstance().getSystem(SystemHttp.class).cancelRequest(getActivity(), true);
    }

    private Video mVideoChoose;
    private ImageView mImageDownLoad;
}

package com.fxtv.threebears.activity.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.frame.ResponsibilityHander;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.MyGridView;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.framework.widget.xlistview.XListView.IXListViewListener;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.activity.player.ActivityVideoPlay;
import com.fxtv.threebears.adapter.SearchAdapter;
import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.model.HotWord;
import com.fxtv.threebears.model.Video;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemAnalyze;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemDownloadVideoManager;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

;

public class ActivitySearchFor extends BaseActivity {
    private XListView mListView;
    private List<Video> mDataList = new ArrayList<>();
    private TextView mTextView, mAnchorResult, mVideoResult;
    private EditText mEditText;
    private ViewGroup mHeaderView;
    private List<HotWord> mHotWordList;
    private SearchAdapter mSearchAdapter;
    private MyAdapter mAdapter;
    private List<Anchor> mAnchorList;
    private int mPageNum;
    private String mHotWordTemp;
    private LinearLayout mLinearLayout;
    private ImageView mEmptyEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for);
        getSystem(SystemAnalyze.class).analyzeUserAction("main_menu", "7", "");
        initView();
        getHotWord();
    }

    private void getHotWord() {
        JsonObject params = new JsonObject();
        params.addProperty("page", "1");
        params.addProperty("pagesize", "9");
        if (mHotWordList == null) {
            mHotWordList = new ArrayList<HotWord>();
        }
        Utils.showProgressDialog(this);
       /* String url = processUrl("Base", "hotWord", params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url,"searchHotWordApi", true, true, callBack);*/

        getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.BASE, ApiType.BASE_hotWord, params), "searchHotWordApi", true, true, new RequestCallBack<List<HotWord>>() {
            @Override
            public void onSuccess(List<HotWord> data, Response resp) {
                if (data != null && data.size() != 0) {
                    mHotWordList = data;
                }
            }

            @Override
            public void onFailure(Response resp) {
                FrameworkUtils.showToast(ActivitySearchFor.this, resp.msg);
            }

            @Override
            public void onComplete() {
                initRecomendView();
                Utils.dismissProgressDialog();
            }
        });

    }

    private void initView() {
        initAcionBar();
        initListView();
    }

    private void initListView() {
        mListView = (XListView) findViewById(R.id.activity_searchfor_listview);
        mHeaderView = (ViewGroup) View.inflate(this, R.layout.header_fragment_search_result, null);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(false);
        mListView.setEmptyViewEnable(false);
        mListView.setEmptyText("没有“" + mEditText.getText() + "”相关的内容");
        mListView.setPageSize(20);
        // headerView
        mAnchorResult = (TextView) mHeaderView.findViewById(R.id.anchor_title);
        mVideoResult = (TextView) mHeaderView.findViewById(R.id.video_title);
        mLinearLayout = (LinearLayout) mHeaderView.findViewById(R.id.fragment_tab_self_mybook_header_hs);
        mListView.addHeaderView(mHeaderView);
        if (mAdapter == null) {
            mAdapter = new MyAdapter(mDataList);
        }
        mListView.setAdapter(mAdapter);
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                getVideoData(null, mHotWordTemp);
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position - 2 < 0)
                    return;
                Bundle bundle = new Bundle();
                bundle.putString("video_id", mDataList.get(position - 2).id);
                bundle.putString("skipType", "62");
                FrameworkUtils.skipActivity(ActivitySearchFor.this, ActivityVideoPlay.class, bundle);
            }
        });
    }

    private void initAcionBar() {

        mTextView = (TextView) findViewById(R.id.search);
        mEditText = (EditText) findViewById(R.id.edit_text);
        mEmptyEdit = (ImageView) findViewById(R.id.cancel_text_imageview);
        mTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(mEditText.getText().toString().trim())) {
                    FrameworkUtils.showToast(ActivitySearchFor.this, "搜索内容不能为空");
                    return;
                } else {
                    findViewById(R.id.activity_searchfor_recommend_layout).setVisibility(View.GONE);
                    getSystem(SystemAnalyze.class)
                            .analyzeUserAction("search", mEditText.getText().toString(), "");
                    getAnchorAndVideoData(mEditText.getText().toString());
                }
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    mEmptyEdit.setVisibility(View.VISIBLE);
                } else {
                    mEmptyEdit.setVisibility(View.GONE);
                }
            }
        });
        mEmptyEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.setText("");
            }
        });
    }

    private void initRecomendView() {
        MyGridView mGridView = (MyGridView) findViewById(R.id.search_hotword_gridview);
        if (mHotWordList == null) {
            mHotWordList = new ArrayList<HotWord>();
        }
        if (mSearchAdapter == null) {
            mSearchAdapter = new SearchAdapter(this, mHotWordList);
            mGridView.setAdapter(mSearchAdapter);
        } else {
            mSearchAdapter.setListData(mHotWordList);
        }
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                findViewById(R.id.activity_searchfor_recommend_layout).setVisibility(View.GONE);
                getAnchorAndVideoData(mHotWordList.get(position).name);
            }
        });
    }

    /**
     * 责任链处理
     */
    private void getAnchorAndVideoData(final String hotWord) {
        mEditText.setText("");
        if (mDataList != null)
            mDataList.clear();
        if (mAnchorList != null)
            mAnchorList.clear();
        mPageNum = 0;
        mHotWordTemp = hotWord;
        mAnchorResult.setText("“" + hotWord + "”的相关主播");
        mVideoResult.setText("“" + hotWord + "”的搜索结果");
        Utils.showProgressDialog(this);
        ResponsibilityHander anchorHander = new ResponsibilityHander() {
            @Override
            public void handleRequest(boolean handle) {
                getAnchorData(this, hotWord);
            }
        };
        ResponsibilityHander videoHander = new ResponsibilityHander() {
            @Override
            public void handleRequest(boolean handle) {
                getVideoData(this, hotWord);
            }
        };
        ResponsibilityHander endHander = new ResponsibilityHander() {
            @Override
            public void handleRequest(boolean handle) {
                if ((mAnchorList == null || mAnchorList.size() == 0) && (mDataList == null || mDataList.size() == 0)) {
                    FrameworkUtils.showToast(ActivitySearchFor.this, getString(R.string.notice_no_more_data));
                }
                Utils.dismissProgressDialog();
            }
        };
        anchorHander.setSuccessor(videoHander);
        videoHander.setSuccessor(endHander);
        anchorHander.handleRequest(true);
    }

    private void getVideoData(final ResponsibilityHander responsibilityHander, String hotWord) {
        mPageNum++;
        JsonObject params = new JsonObject();
        params.addProperty("keyword", hotWord);
        params.addProperty("page", mPageNum + "");
        params.addProperty("pagesize", 20 + "");

      /*  String url = processUrl("Base", "searchVideo", params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url, "searchVideoApi", false, false, callBack);*/
        getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.BASE, ApiType.BASE_searchVideo, params), "searchVideoApi", false, false, new RequestCallBack<List<Video>>() {
            @Override
            public void onSuccess(List<Video> data, Response resp) {
                if (data != null && data.size() != 0) {
                    mDataList.addAll(data);
                    mAdapter.notifyDataSetChanged();
                    mListView.stopLoadMore();
                } else {
                    mListView.stopLoadMore();
                    FrameworkUtils.showToast(ActivitySearchFor.this, getString(R.string.notice_no_more_data));
                }
            }

            @Override
            public void onFailure(Response resp) {
                FrameworkUtils.showToast(ActivitySearchFor.this, resp.msg);
            }

            @Override
            public void onComplete() {
                if (responsibilityHander != null) {
                    responsibilityHander.getSuccessor().handleRequest(true);
                }
            }
        });

    }

    // 获取搜索相关主播
    private void getAnchorData(final ResponsibilityHander responsibilityHander, String hotWord) {
        JsonObject params = new JsonObject();
        params.addProperty("keyword", hotWord);
        params.addProperty("page", "1");
        params.addProperty("pagesize", "99");

        /*String url = processUrl("Base", "searchAnchor", params);
        SystemManager.getInstance().getSystem(SystemHttp.class).get2(context, url,  callBack);*/

        getSystem(SystemHttp.class).get(this, Utils.processUrl(ModuleType.BASE, ApiType.BASE_searchAnchor, params), "searchAnchorApi", false, false, new RequestCallBack<List<Anchor>>() {
            @Override
            public void onSuccess(List<Anchor> data, Response resp) {
                mHeaderView.findViewById(R.id.no_result).setVisibility(View.GONE);
                mHeaderView.findViewById(R.id.have_result).setVisibility(ViewGroup.VISIBLE);
                mAnchorList = data;
                if (mAnchorList == null || mAnchorList.size() == 0) {
                    mHeaderView.findViewById(R.id.no_result).setVisibility(View.VISIBLE);
                    mHeaderView.findViewById(R.id.have_result).setVisibility(View.GONE);
                    return;
                }
                updateOrderAnchors();
            }

            @Override
            public void onFailure(Response resp) {
                FrameworkUtils.showToast(ActivitySearchFor.this, resp.msg);
                if (mAnchorList == null || mAnchorList.size() == 0) {
                    mHeaderView.findViewById(R.id.no_result).setVisibility(View.VISIBLE);
                    mHeaderView.findViewById(R.id.have_result).setVisibility(View.GONE);
                }
            }

            @Override
            public void onComplete() {
                if (responsibilityHander != null) {
                    responsibilityHander.getSuccessor().handleRequest(true);
                }
            }
        });

    }

    /**
     * 更新搜索主播的结果ui
     */
    private void updateOrderAnchors() {
        mLinearLayout.removeAllViews();
        for (int i = 0; i < mAnchorList.size(); i++) {
            final Anchor anchor = mAnchorList.get(i);
            final View item = mLayoutInflater.inflate(R.layout.item_anchor, null);
            View layout = item.findViewById(R.id.layout);
            if (i != 0) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
                layoutParams.leftMargin = FrameworkUtils.dip2px(this, 22);
                layout.setLayoutParams(layoutParams);
            }
            ImageView img = (ImageView) item.findViewById(R.id.photo);
            TextView name = (TextView) item.findViewById(R.id.name);
            /*SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageSquare(anchor.image, img);*/
            getSystem(SystemCommon.class).displayDefaultImage(this, img, anchor.image);
            name.setText(anchor.name);
            final Button book = (Button) item.findViewById(R.id.book);
            book.setVisibility(View.VISIBLE);
            if (anchor.order_status.equals("1")) {
                book.setText("已订阅");
                book.setBackgroundResource(R.drawable.shape_rectangle_circular_check);
            } else {
                book.setText("订阅");
                book.setBackgroundResource(R.drawable.selector_btn2);
                book.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!getSystem(SystemUser.class).isLogin()) {
                            getSystem(SystemCommon.class)
                                    .noticeAndLogin(ActivitySearchFor.this);
                            return;
                        }
                        if (anchor.order_status.equals("1")) {
                            return;
                        }
                        getSystem(SystemUser.class)
                                .orderOrUnOrderAnchor(anchor.id, "1", new IUserBusynessCallBack() {

                                    @Override
                                    public void onResult(boolean result, String arg) {
                                        showToast(arg);
                                        if (result) {
                                            book.setText("已订阅");
                                            book.setBackgroundResource(R.drawable.shape_rectangle_circular_check);
                                        }
                                    }
                                });
                    }
                });
            }
            img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("anchor_id", anchor.id);
                    bundle.putString("skipType", "61");
                    bundle.putString("anchorFrom", "discovery");
                    FrameworkUtils.skipActivity(ActivitySearchFor.this, ActivityAnchorZone.class, bundle);
                }
            });
            mLinearLayout.addView(item);
        }
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
                viewHolder.logo = (ImageView) convertView.findViewById(R.id.logo);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Video video = getItem(position);
//            if (video.lottery_status.equals("1")) {
//                viewHolder.imgJiang.setVisibility(View.VISIBLE);
//            } else {
//                viewHolder.imgJiang.setVisibility(View.GONE);
//            }
            Utils.setVideoLogo(viewHolder.imgJiang, viewHolder.logo, video.lottery_status);
        /*	SystemManager.getInstance().getSystem(SystemImageLoader.class).displayImageDefault(video.image, viewHolder.img);*/
            getSystem(SystemCommon.class).displayDefaultImage(ActivitySearchFor.this, viewHolder.img, video.image);
            viewHolder.title.setText(video.title);
            viewHolder.name.setText(video.game_title);
            viewHolder.time.setText(video.duration);
            viewHolder.author.setText(video.anchor_name);
            viewHolder.lastTime.setText(video.publish_time);
            final ImageView imgdownload = viewHolder.imgdownload;
            viewHolder.imgdownload.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVideo = video;
                    mImageDownLoad = imgdownload;
                    getSystem(SystemCommon.class).showDownloadDialog(ActivitySearchFor.this, mVideo, mImageDownLoad);
                }
            });
            viewHolder.author.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("anchor_id", video.anchor_id);
                    bundle.putString("skipType", "61");
                    FrameworkUtils.skipActivity(ActivitySearchFor.this, ActivityAnchorZone.class, bundle);
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
            ImageView logo;
        }
    }

    private Video mVideo;
    private ImageView mImageDownLoad;
}

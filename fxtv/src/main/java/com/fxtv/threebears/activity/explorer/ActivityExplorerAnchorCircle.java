package com.fxtv.threebears.activity.explorer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.SystemShare;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.widget.xlistview.XListView;
import com.fxtv.framework.widget.xlistview.XListView.IXListViewListener;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorActAnswer;
import com.fxtv.threebears.activity.anchor.ActivityAnchorLatestAct;
import com.fxtv.threebears.model.BBS;
import com.fxtv.threebears.system.IUserBusynessCallBack;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemUser;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.FixGridLayout;
import com.fxtv.threebears.view.ShareDialog;
import com.google.gson.JsonObject;

import java.util.List;

import emojicon.EmojiconTextView;

/**
 * 主播圈
 *
 * @author FXTV-Android
 */
public class ActivityExplorerAnchorCircle extends BaseActivity {
    private XListView mListView;
    //private List<BBS> mList;
    private MyAdapter mAdapter;
    private int mPageNum;
    private int mPageSize = 20;
    private double mPXTimes;
    private PopupWindow optionPw;
    private final int TOACTANCSER = 10;
    private int mPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_anchor_circle);
        initView();
        getData(false, true);
    }

    private void initView() {
        initAcionBar();
        initListView();
    }

    private void initListView() {
        mListView = (XListView) findViewById(R.id.activity_anchor_latest_act_ExpandableListView);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        if (mAdapter == null) {
            mAdapter = new MyAdapter(null);
        }
        mListView.setAdapter(mAdapter);
        mListView.setEmptyText("暂无数据");
        mListView.setPageSize(mPageSize);
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                mListView.setRefreshTime("刚刚");
                mPageNum = 0;
                getData(true, false);
            }

            @Override
            public void onLoadMore() {
                getData(false, false);
            }
        });
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                mPos = position - 1;
                showCopyPop(view);
                return false;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("BBS", mAdapter.getListData().get(position - 1));
//                bundle.putInt("position", position - 1);
//                FrameworkUtils.skipActivity(ActivityExplorerAnchorCircle.this, ActivityAnchorActAnswer.class,
//                        bundle);
                Intent intent = new Intent(ActivityExplorerAnchorCircle.this, ActivityAnchorActAnswer.class);
                intent.putExtra("BBS", mAdapter.getListData().get(position - 1));
                intent.putExtra("position", position - 1);
                startActivityForResult(intent, TOACTANCSER);

            }
        });
    }

    protected void showCopyPop(View parent) {
        // TODO Auto-generated method stub
        View layout = LayoutInflater.from(this).inflate(R.layout.pop_copy, null);
        if (optionPw == null) {
            optionPw = new PopupWindow(layout, 200, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
        }
        optionPw.setFocusable(true);
        optionPw.setOutsideTouchable(true);
        optionPw.setBackgroundDrawable(getResources().getDrawable(R.color.color_transparency));
        layout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int Pwidth = layout.getMeasuredWidth();
        int Phight = layout.getMeasuredHeight();
//        final BBS bbs = mAdapter.getItem(mPos);
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        optionPw.showAtLocation(parent, Gravity.NO_GRAVITY, (location[0] + parent.getWidth() / 2), location[1]);
        ((TextView) layout.findViewById(R.id.item_copy)).setText("复制");
        layout.findViewById(R.id.item_copy).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                copy(mAdapter.getItem(mPos).content);
                optionPw.dismiss();
            }
        });
    }

    protected void copy(String bbs_content) {
        // TODO Auto-generated method stub
        Utils.copy(bbs_content, this);
    }

    protected void getData(final boolean fromRefresh, final boolean showDialog) {
        if (fromRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }
        JsonObject params = new JsonObject();
        params.addProperty("page", mPageNum);
        params.addProperty("pagesize", mPageSize);
        if (showDialog) {
            Utils.showProgressDialog(this);
        }
        /*if (mList == null) {
            mList = new ArrayList<BBS>();
		}*/
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_anchorRing, params);
        getSystem(SystemHttp.class).get(this, url, "getAnchorCircle", false, false, new RequestCallBack<List<BBS>>() {
            @Override
            public void onSuccess(List<BBS> data, Response resp) {
                if (mAdapter == null) mAdapter = new MyAdapter(null);
                if (data != null && data.size() != 0) {
                    if (fromRefresh) {
                                        /*mList.clear();
                                        mList = list;*/
                        mAdapter.setListData(data);
                    } else {
                        //mList.addAll(list);
                        mAdapter.addData(data);
                    }
                } else {
                    FrameworkUtils.showToast(ActivityExplorerAnchorCircle.this,
                            getString(R.string.notice_no_more_data));
                    mListView.noMoreData();
                }
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                mListView.stopLoadMore();
                mListView.stopRefresh();
                Utils.dismissProgressDialog();
            }
        });
    }

    private void initAcionBar() {
        TextView title = (TextView) findViewById(R.id.ab_title);
        title.setText("主播圈");
        ImageView btnBack = (ImageView) findViewById(R.id.ab_left_img);
        mPXTimes = (btnBack.getLayoutParams().width / 50.0);
        btnBack.setImageResource(R.drawable.icon_arrow_left1);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class MyAdapter extends BaseListGridAdapter<BBS> {

        public MyAdapter(List<BBS> listData) {
            super(listData);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = View.inflate(ActivityExplorerAnchorCircle.this,
                        R.layout.item_topic_info_list, null);
                holder = new Holder();
                holder.anchorImg = (ImageView) convertView.findViewById(R.id.img);
                holder.acchorName = (TextView) convertView.findViewById(R.id.name);
                holder.createTime = (TextView) convertView.findViewById(R.id.tv_time);
                holder.content = (EmojiconTextView) convertView.findViewById(R.id.content);
                holder.contentImg = (FixGridLayout) convertView.findViewById(R.id.pic_layout);
                holder.contentImg.setmCellHeight((int) (110 * mPXTimes));
                holder.contentImg.setmCellWidth((int) (110 * mPXTimes));
//                holder.publishTime = (TextView) convertView.findViewById(R.id.publish_time);
                holder.shareNum = (TextView) convertView.findViewById(R.id.share_nums);
                holder.commentNum = (TextView) convertView.findViewById(R.id.post_nums);
                holder.zanNum = (TextView) convertView.findViewById(R.id.zan_nums);
                holder.shareLayout = convertView.findViewById(R.id.line_share);
                holder.commentLayout = convertView.findViewById(R.id.line_post);
                holder.zanLayout = convertView.findViewById(R.id.line_zan);
                holder.zanImg = (ImageView) convertView.findViewById(R.id.image_zan);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            final BBS bbs = getItem(position);
            holder.anchorImg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("anchor_id", bbs.anchor_id);
                    bundle.putString("anchor_name", bbs.name);
                    FrameworkUtils
                            .skipActivity(ActivityExplorerAnchorCircle.this, ActivityAnchorLatestAct.class, bundle);
                }
            });
            holder.acchorName.setText(bbs.name);
            holder.createTime.setText(bbs.create_time);
            holder.content.setText(bbs.content);
//            holder.publishTime.setText(bbs.create_time);
            getSystem(SystemCommon.class).displayImageForAnchorCircle(ActivityExplorerAnchorCircle.this, holder.anchorImg, bbs.image);
//            holder.contentNumber.setText("共" + bbs.reply_num + "条回复");
            holder.shareNum.setText(bbs.share_num);
            holder.shareNum.setTag(position + "shareNum");
            holder.commentNum.setText(bbs.reply_num);
            holder.zanNum.setText(bbs.like_num);
            if ("1".equals(bbs.like_status)) {
                holder.zanImg.setImageResource(R.drawable.icon_ding1);
            } else {
                holder.zanImg.setImageResource(R.drawable.icon_ding0);
            }
            //分享
//            holder.shareLayout.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ShareModel shareModel = new ShareModel();
//                    shareModel.shareTitle = bbs.name;
//                    shareModel.shareUrl = "www.baidu.com";
//                    getSystem(SystemCommon.class).showShareDialog(ActivityExplorerAnchorCircle.this, shareModel, new ShareDialog.ShareCallBack() {
//                        @Override
//                        public void onShareSuccess() {
//                            showToast("分享成功");
//                        }
//
//                        @Override
//                        public void onShareFailure(String msg) {
//                            showToast(msg + "分享失败");
//                        }
//
//                        @Override
//                        public void onCancel() {
//                            showToast("分享取消");
//                        }
//                    });
//                }
//            });

//            //评论
//            holder.commentLayout.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("BBS", bbs);
//                    FrameworkUtils.skipActivity(ActivityExplorerAnchorCircle.this, ActivityAnchorActAnswer.class,
//                            bundle);
//                }
//            });
//            //点赞
//            holder.zanLayout.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showToast("点赞");
//                }
//            });
            ChildClikListenner childClikListenner = new ChildClikListenner(bbs, position);
            holder.shareLayout.setOnClickListener(childClikListenner);
//            holder.commentLayout.setOnClickListener(childClikListenner);
            holder.zanLayout.setOnClickListener(childClikListenner);

            holder.contentImg.removeAllViews();
            if (bbs.images != null && bbs.images.size() != 0) {
                for (int i = 0; i < bbs.images.size(); i++) {
                    ImageView img = new ImageView(ActivityExplorerAnchorCircle.this);
                    //getSystem(SystemCommon.class).displayDefaultImage(ActivityExplorerAnchorCircle.this, holder.anchor, topicMessage.image);
                    getSystem(SystemCommon.class).displayImageForAnchorCircle(ActivityExplorerAnchorCircle.this, img, bbs.images.get(i));
                    img.setPadding(5, 10, 5, 0);
                    final int postion1 = i;
                    img.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("URLs", bbs.images);
                            bundle.putInt("postion", postion1);
                            FrameworkUtils.skipActivity(ActivityExplorerAnchorCircle.this,
                                    ActivityExplorerImagePager.class, bundle);
                        }
                    });
                    holder.contentImg.addView(img);
                }
            }
            return convertView;
        }

        class Holder {
            ImageView anchorImg;
            FixGridLayout contentImg;
            TextView acchorName;
            EmojiconTextView content;
//            TextView publishTime;
            TextView shareNum, commentNum, zanNum;
            View shareLayout, commentLayout, zanLayout;
            TextView createTime;
            ImageView zanImg;
        }
    }

    public class ChildClikListenner implements OnClickListener {

        private BBS bbs;
        private int position;

        public ChildClikListenner(BBS bbs, int position) {
            this.bbs = bbs;
            this.position = position;
        }

        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                //分享
                case R.id.line_share:
                    ShareModel shareModel = new ShareModel();
                    shareModel.shareTitle = bbs.name;
                    shareModel.shareUrl = "http://api.feixiong.tv/h5/fx_topic/recent.html?rid=" + bbs.id;
                    shareModel.shareSummary = bbs.content;
                    shareModel.fileImageUrl = bbs.image;
                    getSystem(SystemCommon.class).showShareDialog(ActivityExplorerAnchorCircle.this, shareModel, new ShareDialog.ShareCallBack() {
                        @Override
                        public void onShareSuccess() {
                            getSystem(SystemUser.class).anchorActShare(bbs.id, new IUserBusynessCallBack() {
                                @Override
                                public void onResult(boolean result, String arg) {
                                    showToast(arg);
                                    if (result) {
                                        bbs.share_num = Utils.getNumberString(bbs.share_num, 1);
                                        mAdapter.getListData().set(position, bbs);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            });


                        }

                        @Override
                        public void onShareFailure(String msg) {
                            showToast(msg + "分享失败");
                        }

                        @Override
                        public void onCancel() {
                            showToast("分享取消");
                        }
                    });
                    break;
                //评论
                case R.id.line_post:
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("BBS", bbs);
//                    FrameworkUtils.skipActivity(ActivityExplorerAnchorCircle.this, ActivityAnchorActAnswer.class,
//                            bundle);
                    break;
                //点赞
                case R.id.line_zan:
                    if ("1".equals(bbs.like_status)) {
                        showToast("已赞过，不能重复操作");
                    } else {
                        getSystem(SystemUser.class).anchorActZan(bbs.id, 1, new IUserBusynessCallBack() {
                            @Override
                            public void onResult(boolean result, String arg) {
                                showToast(arg);
                                if (result) {
                                    bbs.like_status = "1";
                                    bbs.like_num = Utils.getNumberString(bbs.like_num, 1);
                                    ((ImageView) v.findViewById(R.id.image_zan)).setImageResource(R.drawable.icon_ding1);
                                    ((TextView) v.findViewById(R.id.zan_nums)).setText(bbs.like_num);
                                }
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TOACTANCSER && resultCode == RESULT_OK) {
            BBS bbs = (BBS) data.getSerializableExtra("BBS");
            int position = data.getIntExtra("position", -1);
            if (bbs != null && position >= 0 && position < mAdapter.getCount()) {
                mAdapter.getListData().set(position, bbs);
                mAdapter.notifyDataSetChanged();
            }
        }

        getSystem(SystemShare.class).onActivityResult(requestCode, resultCode, data);
    }
}

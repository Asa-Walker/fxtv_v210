package com.fxtv.threebears.fragment.module.anchor;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.frame.BaseFragment;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.anchor.ActivityAnchorZone;
import com.fxtv.threebears.model.AllMolel;
import com.fxtv.threebears.model.Anchor;
import com.fxtv.threebears.util.CharacterParser;
import com.fxtv.threebears.util.PinyinComparator;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.sortListView.ClearEditText;
import com.fxtv.threebears.view.sortListView.SideBar;
import com.fxtv.threebears.view.sortListView.SideBar.OnTouchingLetterChangedListener;
import com.fxtv.threebears.view.sortListView.SortAdapter;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 新的主播fragment
 *
 * @author Android2
 */
public class FragmentNewAnchorAllModel extends BaseFragment {
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser mCharacterParser;
    private List<Anchor> mSourceDateList;
    private SideBar mSideBar;
    private TextView mDialog;
    private SortAdapter mAdapter;
    private ListView mSortListView;
    private List<Anchor> mList;
    private static String TAG = "FragmentNewAnchorAllModel";
    private String mId;
    private AllMolel mAllMolel;
    private ClearEditText mClearEditText;
    private List<Anchor> filterDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_new_anchor_all_model, container, false);
        mId = getArguments().getString("id");
        // 实例化汉字转拼音类
        mCharacterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        getData();
        return mRoot;
    }

    private void initView() {
        initSideBar();
        initSortListView();
        initClearEditext();
    }

    private void initClearEditext() {
        mClearEditText = (ClearEditText) mRoot.findViewById(R.id.find_name);
        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        filterDateList = new ArrayList<Anchor>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = mList;
        } else {
            filterDateList.clear();
            for (Anchor anchor : mList) {
                String name = anchor.name;
//                    if (name.contains(filterStr) || mCharacterParser.getSelling(name).startsWith(filterStr)) {
                if (name.contains(filterStr) || name.startsWith(filterStr)) {
                    filterDateList.add(anchor);
                }
            }
        }
        // 根据a-z进行排序
//        Collections.sort(filterDateList, pinyinComparator);
        mAdapter.updateListView(filterDateList);
    }

    private void filterData(String filterStr, boolean flag) {
        if (filterDateList == null) {
            filterDateList = new ArrayList<Anchor>();
        }

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = mList;
            mAdapter.updateListView(filterDateList);
        } else {
//            filterDateList.clear();
            List<Anchor> temp = new ArrayList<Anchor>();
            if (flag && filterDateList.size() > 0) {
                for (Anchor anchor : filterDateList) {
                    String name = anchor.name;
//                if (name.contains(filterStr) || mCharacterParser.getSelling(name).startsWith(filterStr)) {
                    if (isFirstName(name, mCharacterParser.getSelling(name), filterStr)) {
                        temp.add(anchor);
                    }
                }
                mAdapter.updateListView(temp);
            } else {
                for (Anchor anchor : mList) {
                    String name = anchor.name;
//                if (name.contains(filterStr) || mCharacterParser.getSelling(name).startsWith(filterStr)) {
                    if (isFirstName(name, mCharacterParser.getSelling(name), filterStr)) {
                        filterDateList.add(anchor);
                    }
                }
                mAdapter.updateListView(filterDateList);
            }
        }
        // 根据a-z进行排序
//        Collections.sort(filterDateList, pinyinComparator);
//        mAdapter.updateListView(filterDateList);
    }

    private boolean isFirstName(String name, String str, String value) {
        int index = -1;
        String temp = "";
        index = name.indexOf(value);
        if (index != 0) {
            temp = str.substring(0, 1);
            if (value.equalsIgnoreCase(temp)) {
                index = 0;
            }
        }

        return index == 0 ? true : false;
    }

    private void initSideBar() {
        mSideBar = (SideBar) mRoot.findViewById(R.id.sidrbar);
        mDialog = (TextView) mRoot.findViewById(R.id.dialog);
        mSideBar.setTextView(mDialog);
        // 设置右侧触摸监听
        mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                if ("荐".equals(s)) {
                    mSortListView.setSelection(0);
                    return;
                }
                // 该字母首次出现的位置
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mSortListView.setSelection(position);
                }
            }
        });
    }

    private void initSortListView() {
        mSortListView = (ListView) mRoot.findViewById(R.id.country_lvcountry);
        mSortListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
//                if (filterDateList==null || ) {
//                    bundle.putString("anchor_id", mList.get(position).id);
//                    Log.i("aaaa", "mlist_id=" + mList.get(position).id);
//                } else {
//                    if (filterDateList != null && filterDateList.size() != 0) {
//                        bundle.putString("anchor_id", filterDateList.get(position).id);
//                        Log.i("aaaa", "filterDateList_id=" + filterDateList.get(position).id);
//                    }
//                }
                if (filterDateList == null || filterDateList.size() <= 0) {
                    bundle.putString("anchor_id", mList.get(position).id);
                } else {
                    bundle.putString("anchor_id", filterDateList.get(position).id);
                }
                bundle.putString("skipType", "51");
                bundle.putString("anchorFrom", "anchor");
                FrameworkUtils.skipActivity(getActivity(), ActivityAnchorZone.class, bundle);
            }
        });
        // SourceDateList = initSourceDateList();
        // 根据a-z进行排序源数据
        Collections.sort(mList, pinyinComparator);
        if (mAllMolel.recom != null) {
            for (int i = 0; i < mAllMolel.recom.size(); i++) {
                mAllMolel.recom.get(i).anchor_first_name = "荐";
            }
            mList.addAll(0, mAllMolel.recom);
        }
        if (mAdapter == null)
            mAdapter = new SortAdapter(FragmentNewAnchorAllModel.this, mList);
        mSortListView.setAdapter(mAdapter);
    }

    /**
     * 临时的主播数据
     *
     * @return
     */
    private List<Anchor> initSourceDateList() {
        List<Anchor> mSortList = new ArrayList<Anchor>();
        String[] date = getResources().getStringArray(R.array.date);
        for (int i = 0; i < date.length; i++) {
            Anchor anchor = new Anchor();
            anchor.name = date[i];
            anchor.anchor_order_count = "1203";
            anchor.avatar = "";
            // 临时数据
            anchor.guard_num = "111";
            // 汉字转换成拼音
            String pinyin = mCharacterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                anchor.anchor_first_name = sortString.toUpperCase();
            } else {
                anchor.anchor_first_name = "#";
            }
            mSortList.add(anchor);
        }
        return mSortList;
    }

    public void getData() {
        JsonObject params = new JsonObject();
        params.addProperty("id", mId);
        Utils.showProgressDialog(getActivity());
        String url = Utils.processUrl(ModuleType.FIND, ApiType.FIND_anchorList, params);
        getSystem(SystemHttp.class).get(getActivity(), url, "getFirstNamrAnchorList", true, true, new RequestCallBack<AllMolel>() {
            @Override
            public void onSuccess(AllMolel data, Response resp) {
                mAllMolel = data;
                changeAchorList();
                initView();
            }

            @Override
            public void onFailure(Response resp) {
                showToast(resp.msg);
            }

            @Override
            public void onComplete() {
                Utils.dismissProgressDialog();
            }
        });
    }

    private void changeAchorList() {
        mList = mAllMolel.all;
        if (mAdapter == null)
            mAdapter = new SortAdapter(FragmentNewAnchorAllModel.this, mList);
        else
            mAdapter.setListData(mList);
        for (int i = 0; i < mList.size(); i++) {
            Anchor anchor = mList.get(i);
            // 汉字转换成拼音
            String pinyin = mCharacterParser.getSelling(anchor.name);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                anchor.anchor_first_name = sortString.toUpperCase();
            } else {
                anchor.anchor_first_name = "#";
            }
        }
    }
}

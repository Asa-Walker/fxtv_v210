package com.fxtv.threebears.activity.explorer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.fxtv.framework.frame.BaseFragmentActivity;
import com.fxtv.threebears.R;
import com.fxtv.threebears.fragment.module.explorer.FragmentExplorerAnchorRankList;
import com.fxtv.threebears.fragment.module.explorer.FragmentExplorerVideoRankList;

import java.util.Objects;

/**
 * 发现--排行榜
 *
 * @author Android2
 */
public class ActivityRankList extends BaseFragmentActivity {
    private RadioGroup mRadioGroup;
    private boolean isAnchorRank = true;
    private FragmentExplorerAnchorRankList mFragmentAnchorRankList;
    private FragmentExplorerVideoRankList mFragmentVideoRankList;
    private String mTemp;
    private ImageView arrow;
    /**
     * 选择的日期排行
     */
    public String TYPE = "weekly";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_list);
        initView();
    }

    private void initView() {
        initActionbar();
        initRadioButton();
    }

    private void initRadioButton() {
        mTemp = TYPE;
        mFragmentAnchorRankList = new FragmentExplorerAnchorRankList();
        mFragmentVideoRankList = new FragmentExplorerVideoRankList();
        FragmentManager fragmentManager = ActivityRankList.this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.rank_list_linear, mFragmentAnchorRankList, "fea")
                .add(R.id.rank_list_linear, mFragmentVideoRankList, "fev").commit();

        mRadioGroup = (RadioGroup) findViewById(R.id.activity_rank_list_rg);
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.anchor_rank_list) {
                    FragmentManager fragmentManager = ActivityRankList.this.getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.show(mFragmentAnchorRankList).hide(mFragmentVideoRankList).commit();
                    if (!TYPE.equals(mTemp)) {
                        mTemp = TYPE;
                        mFragmentAnchorRankList.updateRankList();
                    }
                    isAnchorRank = true;
                } else {
                    FragmentManager fragmentManager = ActivityRankList.this.getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.show(mFragmentVideoRankList).hide(mFragmentAnchorRankList).commit();
                    isAnchorRank = false;
                    if (!TYPE.equals(mTemp)) {
                        mTemp = TYPE;
                        mFragmentVideoRankList.updateVideoRankList();
                    }
                }
            }
        });
        // mLinearLayout = (LinearLayout) findViewById(R.id.rank_list_linear);
        // 默认选中
        RadioButton t = (RadioButton) mRadioGroup.getChildAt(0);
        t.setChecked(true);

    }

    private void initActionbar() {
        ImageView back = (ImageView) findViewById(R.id.img_back);
        TextView title = (TextView) findViewById(R.id.my_title);
        final LinearLayout popLinearLayout = (LinearLayout) findViewById(R.id.pop_layout);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("排行榜");
        findViewById(R.id.ab_editor).setVisibility(View.GONE);
        arrow = (ImageView) findViewById(R.id.my_up_down);
        final TextView rankListName = (TextView) findViewById(R.id.rank_list);
        final TextView dayListName = (TextView) findViewById(R.id.day);
        final TextView monthListName = (TextView) findViewById(R.id.month);
        rankListName.setVisibility(View.VISIBLE);
        arrow.setVisibility(View.VISIBLE);
        findViewById(R.id.actionbar_right_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popLinearLayout.getVisibility() == View.GONE) {
                    popLinearLayout.setVisibility(View.VISIBLE);
                    arrow.setImageResource(R.drawable.arrow_up);
                } else {
                    popLinearLayout.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.arrow_down);
                }
            }
        });
        // 日排行
        dayListName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataAndUI(v, rankListName, popLinearLayout);
                return;
            }
        });
        // 月排行
        monthListName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataAndUI(v, rankListName, popLinearLayout);
                return;
            }
        });
    }

    private void updateDataAndUI(View parent, TextView rankListName, LinearLayout popLinearLayout) {
        String temp1 = ((TextView) parent).getText().toString();
        String temp2 = rankListName.getText().toString();
        setType(temp1);
        rankListName.setText(temp1);
        popLinearLayout.setVisibility(View.GONE);
        ((TextView) parent).setText(temp2);
        if (isAnchorRank) {
            mFragmentAnchorRankList.updateRankList();
        } else {
            mFragmentVideoRankList.updateVideoRankList();
        }
    }

    private void setType(String value) {
        switch (value) {
            case "日排行":
                TYPE = "daily";
                arrow.setImageResource(R.drawable.arrow_down);
                break;
            case "周排行":
                TYPE = "weekly";
                arrow.setImageResource(R.drawable.arrow_down);
                break;
            case "月排行":
                TYPE = "monthly";
                arrow.setImageResource(R.drawable.arrow_down);
                break;
            default:
                TYPE = "weekly";
                arrow.setImageResource(R.drawable.arrow_down);
                break;
        }
    }

}

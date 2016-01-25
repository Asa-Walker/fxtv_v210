package com.fxtv.threebears.activity.explorer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseFragmentActivity;
import com.fxtv.framework.frame.BaseViewPagerAdapter;
import com.fxtv.threebears.R;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.view.HackyViewPager;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * 图片查看器
 *
 * @author Administrator
 */
public class ActivityExplorerImagePager extends BaseFragmentActivity {
    private int mPosition;
    private int maxCount;
    private List<String> mData;//uri集合
    private List<Bitmap> mBitmaps;//bitmap集合
    private HackyViewPager mVP;
    private SamplePagerAdapter mAdapter;
    private TextView mPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_image_pager);
        if(baseSavedInstance!=null){
            mPosition=baseSavedInstance.getInt("postion", 0);
            mData = baseSavedInstance.getStringArrayList("URLs");//uri集合,一次只能传一组集合
            mBitmaps=baseSavedInstance.getParcelableArrayList("Bitmaps");//bitmap集合
        }
        Logger.d("TAG","(mData==null？"+(mData==null)+"  mBitmaps==null?"+ (mBitmaps==null));
        if(mData==null && mBitmaps==null){
            finish();
        }

        int uriCount=mData==null?0:mData.size();
        int bitmapCount=mBitmaps==null?0:mBitmaps.size();
        maxCount=Math.max(uriCount,bitmapCount);

        initView();
    }

    private void initView() {
        mPos = (TextView) findViewById(R.id.position);
        mPos.setText((mPosition + 1) + "/" + maxCount);
        initViewPager();
    }

    private void initViewPager() {
        mVP = (HackyViewPager) findViewById(R.id.view_pager);
        mVP.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mPos.setText((arg0 + 1) + "/" +maxCount);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        mAdapter = new SamplePagerAdapter();
        mVP.setAdapter(mAdapter);
        mVP.setCurrentItem(mPosition);
    }

    class SamplePagerAdapter extends BaseViewPagerAdapter {

        public SamplePagerAdapter() {
            super(null);
        }

        @Override
        public int getCount() {
            return maxCount;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            if(mBitmaps!=null && position<mBitmaps.size()){//bitmaps不为空就设置为bitmaps中的bitmap
                photoView.setImageBitmap(mBitmaps.get(position));
            }else{
                getSystem(SystemCommon.class).displayDefaultImage(ActivityExplorerImagePager.this, photoView, mData.get(position));
            }
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            return photoView;
        }

    }
}

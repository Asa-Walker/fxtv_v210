package com.fxtv.framework.system;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.fxtv.framework.frame.SystemBase;

/**
 * 第三方登录系统
 *
 * @author FXTV-Android
 */
public class SystemImageLoader extends SystemBase {
    private static final String TAG = "SystemImageLoader";

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void destroy() {
        super.destroy();
    }

    /**
     * @param object--可能是Activity,FragmentActivity,Fragment
     * @param image--要显示的图片
     * @param url--图片地址
     * @param holderResouce--预加载显示的图片
     * @param errorResouce--加载出粗时时显示的图片
     * @param emptyResouce--地址为空时显示的图片
     * @param radius--倒圆角的值
     */
    public void displayImage(Object object, ImageView image, String url, int holderResouce, int errorResouce, int emptyResouce, int radius) {
        if (object != null) {
            if (object instanceof FragmentActivity) {
                FragmentActivity fragmentActivity = (FragmentActivity) object;
                if (radius > 0) {
                    Glide.with(fragmentActivity).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).transform(new GlideRoundTransform(fragmentActivity, radius)).
                            placeholder(holderResouce).error(errorResouce).fallback(emptyResouce).into(image);
                } else {
                    Glide.with(fragmentActivity).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(holderResouce).error(errorResouce).fallback(emptyResouce).into(image);
                }
                return;
            }
            if (object instanceof Activity) {
                Activity activity = (Activity) object;
                if (radius > 0) {
                    Glide.with(activity).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).transform(new GlideRoundTransform(activity, radius)).
                            placeholder(holderResouce).error(errorResouce).fallback(emptyResouce).into(image);
                } else {
                    Glide.with(activity).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(holderResouce).error(errorResouce).fallback(emptyResouce).into(image);
                }
                return;
            }
            if (object instanceof Fragment) {
                Fragment fragment = (Fragment) object;
                if (radius > 0) {
                    Glide.with(fragment.getActivity()).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).transform(new GlideRoundTransform(fragment.getActivity(), radius)).
                            placeholder(holderResouce).error(errorResouce).fallback(emptyResouce).into(image);
                } else {
                    Glide.with(fragment.getActivity()).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(holderResouce).error(errorResouce).fallback(emptyResouce).into(image);
                }
                return;
            }
            if (object instanceof Context) {
                Context context = (Context) object;
                if (radius > 0) {
                    Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).transform(new GlideRoundTransform(context, radius)).
                            placeholder(holderResouce).error(errorResouce).fallback(emptyResouce).into(image);
                } else {
                    Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(holderResouce).error(errorResouce).fallback(emptyResouce).into(image);
                }
                return;
            }

        } else {
            return;
        }
    }

    public void displayImageForAnchorCircle(Context context, ImageView image, String url, int holderResouce, int errorResouce, int emptyResouce) {
        Glide.get(context).setMemoryCategory(MemoryCategory.HIGH);
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).override(120, 120).centerCrop().placeholder(holderResouce).error(errorResouce).fallback(emptyResouce).into(image);
    }

    /**
     * 为Glide加载的图片倒圆角的工具类(因为Glide没提供倒圆角的方法)
     */
    public class GlideRoundTransform extends BitmapTransformation {
        private float radius = 0f;

        public GlideRoundTransform(Context context) {
            this(context, 4);
        }

        public GlideRoundTransform(Context context, int dp) {
            super(context);
            this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;
            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName() + Math.round(radius);
        }
    }

    public String getCachePath(){
       return Glide.getPhotoCacheDir(mContext).getPath();
    }


}

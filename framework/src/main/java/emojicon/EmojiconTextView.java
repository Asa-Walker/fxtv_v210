/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package emojicon;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.widget.TextView;

import com.fxtv.framework.R;

import java.util.regex.Matcher;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com).
 */
public class EmojiconTextView extends TextView {
    private int mEmojiconSize;

    public EmojiconTextView(Context context) {
        super(context);
        init(null);
    }

    public EmojiconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            mEmojiconSize = (int) getTextSize();
        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emojicon);
            mEmojiconSize = (int) a.getDimension(R.styleable.Emojicon_emojiconSize, getTextSize());
            a.recycle();
        }
        setText(getText());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        EmojiconHandler.addEmojis(getContext(),builder, mEmojiconSize);
        super.setText(builder, type);
    }

   /* *//**
     * 带有hmtl格式的文字,不加载图片
     */
//    public void setText(String text){
//        SpannableStringBuilder builder = new SpannableStringBuilder(text);
//        EmojiconHandler.addEmojis(getContext(), builder, mEmojiconSize);
//        super.setText(builder);
//       // this.setText(getHtmlSpanned(text));
//    }
    /**
     * 带有hmtl格式的文字,异步加载图片
     */
    public void setHtmlText(String text){
        this.setText(getHtmlImgSpanned(text));
    }

    /**
     * 支持图片和超链接
     * @param text
     */
    public void appendHtml(CharSequence text) {
        try {
            SpannableString spStr=new SpannableString(getHtmlImgSpanned(text));
            Matcher m=Patterns.WEB_URL.matcher(spStr);

            //反射设置
            Class voweburl_class=Class.forName("com.yanyu.jynh.vo.VoWebUrl");
            Class UrlClick_class=Class.forName("com.yanyu.jynh.util.UrlAdOnClick");

            URLSpan[] urlSpan=spStr.getSpans(0, spStr.length(), URLSpan.class);//有值

            for(URLSpan uri:urlSpan){
                Object voweburl=voweburl_class.getConstructor(String.class).newInstance(uri.getURL());//得到参数为(String xx)的构造函数的实例对象
                Object urlclick=UrlClick_class.getConstructor(voweburl.getClass()).newInstance(voweburl);
                int start=spStr.getSpanStart(uri);
                int end=spStr.getSpanEnd(uri);
                Log.i("TAG","start="+start+" end="+end+" geturi="+uri.getURL());
                spStr.setSpan(urlclick, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            }
            super.append(spStr);

        } catch (Exception e) {
            e.printStackTrace();

            super.append(text);
        }

    }

    /**
     * @param text
     * @return 异常加载图片的Spanned，显示html
     */
    private Spanned getHtmlImgSpanned(CharSequence text){
        URLImageParser parser=new URLImageParser(this,getContext());
        Spanned spanned=Html.fromHtml("" + text, parser, null);

        return spanned;
    }
    /**
     * @param text
     * @return 不加载图片，显示html
     */
    private Spanned getHtmlSpanned(CharSequence text){
        return Html.fromHtml("" + text, new ImageGetter() {
            @Override
            public Drawable getDrawable(String s) {
                return new URLDrawable();
            }
        }, null);
    }
    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;
    }
}

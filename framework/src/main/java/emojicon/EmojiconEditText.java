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
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.fxtv.framework.R;

import java.util.regex.Matcher;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com).
 */
public class EmojiconEditText extends EditText {
    private int mEmojiconSize;
    public EmojiconEditText(Context context) {
        super(context);
        mEmojiconSize = (int) getTextSize();
        init(null);
    }

    public EmojiconEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiconEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if(attrs!=null){
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emojicon);
            mEmojiconSize = (int) a.getDimension(R.styleable.Emojicon_emojiconSize, getTextSize());
            a.recycle();
            setText(getText());
        }
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {//删除时count=0，添加时为长度
                    CharSequence charSequence = s.subSequence(start, start + count);//插入的文字

                    Matcher matcher = EmojiconHandler.pattern.matcher(charSequence);
                    while (matcher.find()) {
                        String group = matcher.group();
                        int findStart = start + matcher.start();
                        int findEnd = start + matcher.end();
                        int icon = EmojiconHandler.getEmojiResource(group);
                        if (icon > 0) {
                            ((Spannable) s).setSpan(new EmojiconSpan(getContext(), icon,mEmojiconSize), findStart, findEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;
    }
}

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
import android.text.Spannable;

import com.fxtv.framework.R;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
public final class EmojiconHandler {
    private EmojiconHandler() {
    }
    public static final Map<String,Integer> sEmojisMap = new LinkedHashMap<>(31);
    public static Pattern pattern;
    static {
        // 第一页
        sEmojisMap.put("[神气]", R.drawable.emoji_1f604);
        sEmojisMap.put("[大怒]", R.drawable.emoji_1f603);
        sEmojisMap.put("[委屈]", R.drawable.emoji_1f600);
        sEmojisMap.put("[大笑]", R.drawable.emoji_1f60a);
        sEmojisMap.put("[微笑]", R.drawable.emoji_263a);
        sEmojisMap.put("[可爱]", R.drawable.emoji_1f609);
        sEmojisMap.put("[抽烟]", R.drawable.emoji_1f60d);
        sEmojisMap.put("[色]", R.drawable.emoji_1f618);
        sEmojisMap.put("[发呆]", R.drawable.emoji_1f61a);
        sEmojisMap.put("[傲慢]", R.drawable.emoji_1f617);
        sEmojisMap.put("[扮鬼脸]", R.drawable.emoji_1f619);
        sEmojisMap.put("[晕]", R.drawable.emoji_1f61c);
        sEmojisMap.put("[得意]", R.drawable.emoji_1f61d);
        sEmojisMap.put("[大哭]", R.drawable.emoji_1f61b);
        sEmojisMap.put("[疲惫]", R.drawable.emoji_1f633);
        sEmojisMap.put("[惊吓]", R.drawable.emoji_1f601);
        sEmojisMap.put("[发愣]", R.drawable.emoji_1f614);
        sEmojisMap.put("[删除]", R.drawable.emoji_delete);

        // 第二页
        sEmojisMap.put("[睡觉]", R.drawable.emoji_1f60c);
        sEmojisMap.put("[气愤]", R.drawable.emoji_1f436);
        sEmojisMap.put("[嬉笑]", R.drawable.emoji_1f43a);
        sEmojisMap.put("[鄙视]", R.drawable.emoji_1f431);
        sEmojisMap.put("[亲亲]", R.drawable.emoji_1f42d);
        sEmojisMap.put("[害羞]", R.drawable.emoji_1f439);
        sEmojisMap.put("[抓狂]", R.drawable.emoji_1f430);
        sEmojisMap.put("[快哭了]", R.drawable.emoji_1f438);
        sEmojisMap.put("[汗]", R.drawable.emoji_1f42f);
        sEmojisMap.put("[再见]", R.drawable.emoji_1f428);
        sEmojisMap.put("[冷笑]", R.drawable.emoji_1f43b);
        sEmojisMap.put("[闭嘴]", R.drawable.emoji_1f437);
        sEmojisMap.put("[坏笑]", R.drawable.emoji_1f43d);

        pattern=Pattern.compile("\\[[\u4E00-\u9FFF]+\\]");//[//u4E00-//u9FFF]为汉字

    }


    public static int getEmojiResource(String codePoint) {
        Integer integer=sEmojisMap.get(codePoint);
        return integer==null?0:integer;
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     */
    //正则算法
    public static void addEmojis(Context context, Spannable text, int emojiSize) {
        Matcher matcher = pattern.matcher(text);
        while(matcher.find()) {
            int icon = getEmojiResource(matcher.group());
            if (icon > 0) {
                EmojiconSpan span=new EmojiconSpan(context, icon, emojiSize);
                if(icon==R.drawable.emoji_delete){//删除按钮
                    span.setWidth(emojiSize);//宽高比 52:42
                    int height=emojiSize * 42 / 52;
                    span.setHeight(height);
                    span.setTop((emojiSize-height)/2);
                }
                text.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
    /*
    自定义查找算法
    public static void addEmojis(Context context, Spannable text, int emojiSize) {
        int length = text.length();
        int skip;
        for (int i = 0; i < length; i += skip) {
            int icon = 0;
            if(text.charAt(i)==91){// 91=[ 93=]
                //此段意思为  取[神气],start=i,end为从i开始查找']' ( char ']'93;)的位置
                int end=(""+text).indexOf(93,i)+1;
                if(end<=0){//未查找到]
                    return;
                }
                CharSequence code=text.subSequence(i,end);
                skip=code.length();
                icon=getEmojiResource("" + code);
            }else{
                skip=1;
            }

            if (icon > 0) {
                text.setSpan(new EmojiconSpan(context, icon, emojiSize), i, i + skip, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }*/


   /*
    unicode 算法
    public static void addEmojis(Context context, Spannable text, int emojiSize) {
        int length = text.length();
        EmojiconSpan[] oldSpans = text.getSpans(0, length, EmojiconSpan.class);
        for (int i = 0; i < oldSpans.length; i++) {
            text.removeSpan(oldSpans[i]);
        }

        int skip;
        for (int i = 0; i < length; i += skip) {
            int icon = 0;
            int unicode = Character.codePointAt(text, i);
            skip = Character.charCount(unicode);

            if (unicode > 0xff) {
                icon = getEmojiResource(context, unicode);
            }
            if (icon > 0) {
                text.setSpan(new EmojiconSpan(context, icon, emojiSize), i, i + skip, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }*/
}

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

package emojicon.emoji;

import java.util.Iterator;

import emojicon.EmojiconHandler;

/**
 * 此处的数据只用于表情列表的显示顺序，页面个数
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
public class People {
    public static final int MAX_COUNT=18;
    public static final Emojicon[] DATA = new Emojicon[MAX_COUNT];
    static {
        Iterator iterator = EmojiconHandler.sEmojisMap.keySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            String icon=(String)iterator.next();
            if(i<MAX_COUNT){
                DATA[i] = Emojicon.fromChars(icon);
            }else{
                Nature.DATA[i-MAX_COUNT]= Emojicon.fromChars(icon);
            }
            i++;
        }

    }


}

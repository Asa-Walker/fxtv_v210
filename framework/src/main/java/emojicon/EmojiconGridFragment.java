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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fxtv.framework.R;

import emojicon.emoji.Emojicon;
import emojicon.emoji.People;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
public class EmojiconGridFragment extends Fragment implements AdapterView.OnItemClickListener {
    private OnEmojiconClickedListener mOnEmojiconClickedListener;
    private OnEmojiconBackspaceClickedListener mOnEmojiconBackspaceClickedListener;
    private Emojicon[] mData;

    protected static EmojiconGridFragment newInstance(Emojicon[] emojicons) {
        EmojiconGridFragment emojiGridFragment = new EmojiconGridFragment();
        Bundle args = new Bundle();
        args.putSerializable("emojicons", emojicons);
        emojiGridFragment.setArguments(args);
        return emojiGridFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.emojicon_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridView gridView = (GridView) view.findViewById(R.id.Emoji_GridView);

       try{
           if(savedInstanceState!=null){
               mData= (Emojicon[]) savedInstanceState.getParcelableArray("emojicons");
           }
           if(mData==null) {
               mData = getArguments() == null ? People.DATA : (Emojicon[]) getArguments().getParcelableArray("emojicons");
           }
       }catch (Exception e){
           mData=People.DATA;
           e.printStackTrace();
       }

        gridView.setAdapter(new EmojiAdapter(view.getContext(), mData));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("emojicons", mData);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof OnEmojiconClickedListener) {
            mOnEmojiconClickedListener = (OnEmojiconClickedListener) activity;
        } else if(getParentFragment() instanceof OnEmojiconClickedListener) {
            mOnEmojiconClickedListener = (OnEmojiconClickedListener) getParentFragment();
        } else {
            throw new IllegalArgumentException(activity + " must implement interface " + OnEmojiconClickedListener.class.getSimpleName());
        }

        if (getActivity() instanceof OnEmojiconBackspaceClickedListener) {
            mOnEmojiconBackspaceClickedListener = (OnEmojiconBackspaceClickedListener) getActivity();
        } else if(getParentFragment() instanceof  OnEmojiconBackspaceClickedListener) {
            mOnEmojiconBackspaceClickedListener = (OnEmojiconBackspaceClickedListener) getParentFragment();
        } else {
            throw new IllegalArgumentException(activity + " must implement interface " + OnEmojiconBackspaceClickedListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        mOnEmojiconClickedListener = null;
        mOnEmojiconBackspaceClickedListener = null;
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mOnEmojiconBackspaceClickedListener!=null && mData!=null && "[删除]".equals(mData[position].getEmoji())){
            mOnEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(view);
        }else if (mOnEmojiconClickedListener != null) {
            mOnEmojiconClickedListener.onEmojiconClicked((Emojicon) parent.getItemAtPosition(position));
        }
    }

    public interface OnEmojiconClickedListener {
        void onEmojiconClicked(Emojicon emojicon);
    }

    public interface OnEmojiconBackspaceClickedListener {
        void onEmojiconBackspaceClicked(View v);
    }

}

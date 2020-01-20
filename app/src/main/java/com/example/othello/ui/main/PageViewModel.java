package com.example.othello.ui.main;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;

import com.example.othello.BoardActivity;
import com.example.othello.R;
import com.example.othello.ScoreActivity;

import static android.content.Context.MODE_PRIVATE;

class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return ScoreActivity.getScoreTable(input-1);
        }
    });

    void setIndex(int index) {
        mIndex.setValue(index);
    }

    LiveData<String> getText() {
        return mText;
    }
}
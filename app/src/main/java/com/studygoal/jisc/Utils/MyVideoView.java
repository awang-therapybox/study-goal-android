package com.studygoal.jisc.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.studygoal.jisc.Managers.DataManager;

public class MyVideoView extends VideoView {


    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        if(DataManager.getInstance().isLandscape) {
            setMeasuredDimension(width, width * 3 / 4);
        } else {
            int height = getDefaultSize(0, heightMeasureSpec);
            setMeasuredDimension(width, height);
        }
    }
}

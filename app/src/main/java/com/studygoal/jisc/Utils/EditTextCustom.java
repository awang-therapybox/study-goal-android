package com.studygoal.jisc.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.studygoal.jisc.Fragments.FeedFragment;
import com.studygoal.jisc.R;

public class EditTextCustom extends EditText {

    public FeedFragment fragment;

    public EditTextCustom(Context context) {
        super(context);
    }

    public EditTextCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            fragment.mainView.findViewById(R.id.overlay).callOnClick();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}

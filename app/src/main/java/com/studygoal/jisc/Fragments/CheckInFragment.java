package com.studygoal.jisc.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.studygoal.jisc.Adapters.ActivitiesHistoryAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.R;

public class CheckInFragment extends Fragment {

    View mainView;
    ListView list;
    ActivitiesHistoryAdapter adapter;
    SwipeRefreshLayout layout;

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) mainView.findViewById(R.id.pin_text_edit)).setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.checkin_fragment, container, false);

        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.check_in));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);


        ((TextView) mainView.findViewById(R.id.pin_send_button)).setTypeface(DataManager.getInstance().oratorstd_typeface);
        mainView.findViewById(R.id.pin_send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        final TextView pin_text_edit = (TextView) mainView.findViewById(R.id.pin_text_edit);
        pin_text_edit.setTypeface(DataManager.getInstance().oratorstd_typeface);

        GridLayout grid = (GridLayout) mainView.findViewById(R.id.grid_layout);
        int childCount = grid.getChildCount();

        for (int i= 0; i < childCount; i++){
            final TextView text = (TextView) grid.getChildAt(i);
            text.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    // your click code here
                    pin_text_edit.setText(pin_text_edit.getText().toString() + text.getText().toString());
                }
            });
        }

        return mainView;
    }
}
package com.studygoal.jisc.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.studygoal.jisc.Adapters.FriendsGenericAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.R;
import com.studygoal.jisc.SettingsActivity;

public class FriendsScreen extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        if(DataManager.getInstance().mainActivity.isLandscape) {
            ((SettingsActivity)getActivity()).fragmentTitle.setText(DataManager.getInstance().mainActivity.getString(R.string.my_friends));
        } else {
            DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.my_friends));
            DataManager.getInstance().mainActivity.hideAllButtons();
            DataManager.getInstance().mainActivity.showCertainButtons(7);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.generic_screen, container, false);


        ((TextView)mainView.findViewById(R.id.title)).setText(R.string.all_friends_list);

        ListView listView = (ListView) mainView.findViewById(R.id.list);
        if(DataManager.getInstance().mainActivity.isLandscape)
            listView.setAdapter(new FriendsGenericAdapter(getActivity()));
        else
            listView.setAdapter(new FriendsGenericAdapter(DataManager.getInstance().mainActivity));

        return mainView;
    }

}

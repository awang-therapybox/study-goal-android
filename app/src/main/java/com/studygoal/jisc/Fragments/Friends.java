package com.studygoal.jisc.Fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Models.ReceivedRequest;
import com.studygoal.jisc.R;

public class Friends extends Fragment {

    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.friends_fragment, container, false);

        mTabHost = (FragmentTabHost) mainView.findViewById(R.id.tabhost);

        if(!DataManager.getInstance().isLandscape)
            mTabHost.setup(DataManager.getInstance().mainActivity, getChildFragmentManager(), R.id.realtabcontent);
        else
            mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("search").setIndicator(DataManager.getInstance().mainActivity.getString(R.string.search)),
                FriendsSearch.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("newrequests").setIndicator(DataManager.getInstance().mainActivity.getString(R.string.requests)),
                FriendsRequests.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("myfriends").setIndicator(DataManager.getInstance().mainActivity.getString(R.string.friends)),
                FriendsList.class, null);

        for(int i = 0; i < 3; i++) {
            View v = mTabHost.getTabWidget().getChildTabViewAt(i);
            ((TextView)v.findViewById(android.R.id.title)).setTextColor(ContextCompat.getColor(DataManager.getInstance().mainActivity, R.color.colorPrimary));
            ((TextView)v.findViewById(android.R.id.title)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView)v.findViewById(android.R.id.title)).setTextSize(16f);
            ((TextView)v.findViewById(android.R.id.title)).setAllCaps(false);
            mTabHost.getTabWidget().getChildAt(i).getLayoutParams().height = (int) (50 * this.getResources().getDisplayMetrics().density);
        }

        if(new Select().from(ReceivedRequest.class).count() > 0)
            mTabHost.setCurrentTab(1);

        return mainView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}

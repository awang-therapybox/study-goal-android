package com.studygoal.jisc.Fragments;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;

import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.R;


/**
 * Created by MarcelC on 1/14/16.
 *
 */
public class Trophies extends Fragment {

    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.trophies_fragment, container, false);

//        mTabHost = new FragmentTabHost(getActivity());
        mTabHost = (FragmentTabHost) mainView.findViewById(R.id.tabhost);

        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("mytrophies").setIndicator(DataManager.getInstance().mainActivity.getString(R.string.trophies_won)), TrophiesMy.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("alltrophies").setIndicator(DataManager.getInstance().mainActivity.getString(R.string.trophies_available)),TrophiesAll.class, null);

        for(int i = 0; i < 2; i++) {
            View v = mTabHost.getTabWidget().getChildTabViewAt(i);
            ((TextView)v.findViewById(android.R.id.title)).setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            ((TextView)v.findViewById(android.R.id.title)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView)v.findViewById(android.R.id.title)).setTextSize(16f);
            ((TextView)v.findViewById(android.R.id.title)).setAllCaps(false);
            mTabHost.getTabWidget().getChildAt(i).getLayoutParams().height = (int) (50 * this.getResources().getDisplayMetrics().density);
        }

        return mainView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}

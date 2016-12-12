package com.studygoal.jisc.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.studygoal.jisc.Adapters.TrophiesAdapter;
import com.studygoal.jisc.MainActivity;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Models.Trophy;
import com.studygoal.jisc.R;
import com.studygoal.jisc.SettingsActivity;
import com.studygoal.jisc.TrophyDetails;

/**
 * Created by MarcelC on 1/14/16.
 *
 */
public class TrophiesAll extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        if(DataManager.getInstance().mainActivity.isLandscape) {
            ((SettingsActivity)getActivity()).fragmentTitle.setText(DataManager.getInstance().mainActivity.getString(R.string.trophies_title));
        } else {
            ((MainActivity) getActivity()).setTitle(DataManager.getInstance().mainActivity.getString(R.string.trophies_title));
            ((MainActivity) getActivity()).hideAllButtons();
            DataManager.getInstance().mainActivity.showCertainButtons(7);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.trophiesall_layout, container, false);

        final GridView list = (GridView) mainView.findViewById(R.id.gridlist);
        final TrophiesAdapter adapter = new TrophiesAdapter(getActivity(),this);
        list.setAdapter(adapter);

        return mainView;
    }

    public void showTrophy(Trophy trophy)
    {
        Intent intent = new Intent(DataManager.getInstance().mainActivity, TrophyDetails.class);

        intent.putExtra("type", trophy.trophy_type.substring(0, 1).toUpperCase() + trophy.trophy_type.substring(1, trophy.trophy_type.length()));
        intent.putExtra("image", trophy.getImageName());
        intent.putExtra("title", trophy.trophy_name);
        intent.putExtra("statement", trophy.statement);

        startActivity(intent);
    }
}

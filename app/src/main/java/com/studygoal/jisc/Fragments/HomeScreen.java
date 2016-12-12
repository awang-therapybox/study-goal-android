package com.studygoal.jisc.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.studygoal.jisc.Adapters.GenericAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.R;
import com.studygoal.jisc.SettingsActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeScreen extends Fragment {

    public String selected_value;

    @Override
    public void onResume() {
        super.onResume();
        if(DataManager.getInstance().mainActivity.isLandscape) {
            ((SettingsActivity)getActivity()).fragmentTitle.setText(DataManager.getInstance().mainActivity.getString(R.string.home_screen));
        } else {
            DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.home_screen));
            DataManager.getInstance().mainActivity.hideAllButtons();
            DataManager.getInstance().mainActivity.showCertainButtons(7);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.generic_screen, container, false);

        switch (DataManager.getInstance().home_screen.toLowerCase()) {
            case "feed": {
                selected_value = getString(R.string.feed);
                break;
            }
            case "stats": {
                selected_value = getString(R.string.stats);
                break;
            }
            case "log": {
                selected_value = getString(R.string.log);
                break;
            }
            case "target": {
                selected_value = getString(R.string.target);
                break;
            }
        }
        final ArrayList<String> en_menu = new ArrayList<String>();
        en_menu.add("feed");
        en_menu.add("stats");
        en_menu.add("log");
        en_menu.add("target");

        ((TextView)mainView.findViewById(R.id.title)).setText(DataManager.getInstance().mainActivity.getString(R.string.select_home_string));

        final ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.feed).toUpperCase());
        list.add(getString(R.string.stats).toUpperCase());
        list.add(getString(R.string.log).toUpperCase());
        list.add(getString(R.string.target).toUpperCase());

        final ListView listView = (ListView) mainView.findViewById(R.id.list);
        final GenericAdapter adapter = new GenericAdapter(DataManager.getInstance().mainActivity, selected_value.toUpperCase(), list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString().equals(selected_value)) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("student_id", DataManager.getInstance().user.id);
                    map.put("setting_type", "home_screen");
                    map.put("setting_value", en_menu.get(position));
                    if (NetworkManager.getInstance().changeAppSettings(map)) {
                        DataManager.getInstance().home_screen = en_menu.get(position);
                        SharedPreferences preferences = DataManager.getInstance().mainActivity.getSharedPreferences("jisc", Context.MODE_PRIVATE);
                        preferences.edit().putString("home_screen", DataManager.getInstance().home_screen).apply();
                    }
                }
                if(!DataManager.getInstance().mainActivity.isLandscape) {
                    DataManager.getInstance().mainActivity.onBackPressed();
                } else {
                    listView.setAdapter(new GenericAdapter(getActivity(), DataManager.getInstance().home_screen.toUpperCase(), list));
                }
            }
        });

        return mainView;
    }

}

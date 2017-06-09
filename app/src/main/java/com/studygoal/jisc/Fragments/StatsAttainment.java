package com.studygoal.jisc.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.studygoal.jisc.Adapters.AttainmentAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.LinguisticManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.SocialManager;
import com.studygoal.jisc.Models.Attainment;
import com.studygoal.jisc.R;

import java.util.Timer;

public class StatsAttainment extends Fragment {

    ListView list;
    AttainmentAdapter adapter;
    View mainView;
    SwipeRefreshLayout layout;

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.attainment));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);

        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkManager.getInstance().getAssignmentRanking();
                adapter.list = new Select().from(Attainment.class).execute();
                for (int i = 0; i < adapter.list.size(); i++) {

                    Attainment attainment = adapter.list.get(i);

                    if (attainment.percent.length() > 1
                            && Integer.parseInt(attainment.percent.substring(0, attainment.percent.length() - 1)) == 0)
                        adapter.list.remove(i);
                }

                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.stats_attainment, container, false);

        layout = (SwipeRefreshLayout) mainView.findViewById(R.id.stats_swipe_refresh);

        layout.setColorSchemeResources(R.color.colorPrimary);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NetworkManager.getInstance().getAssignmentRanking();
                        adapter.list = new Select().from(Attainment.class).execute();
                        for (int i = 0; i < adapter.list.size(); i++) {
                            if (Integer.parseInt(adapter.list.get(i).percent.substring(0, adapter.list.get(i).percent.length() - 1)) == 0)
                                adapter.list.remove(i);
                        }
                        DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                call_refresh();
                            }
                        });
                    }
                }).start();
            }
        });

        list = (ListView) mainView.findViewById(R.id.list);
        list.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        adapter = new AttainmentAdapter(DataManager.getInstance().mainActivity);
        list.setAdapter(adapter);

        final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (DataManager.getInstance().user.isStaff && preferences.getBoolean("stats_alert", true)) {

            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage(R.string.statistics_admin_view);
            alertDialogBuilder.setPositiveButton("Don't show again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("stats_alert", false);
                    editor.apply();
                }
            });
            alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return mainView;
    }

    private void call_refresh() {
        layout.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

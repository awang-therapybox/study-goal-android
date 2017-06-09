package com.studygoal.jisc.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.LinguisticManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.SocialManager;
import com.studygoal.jisc.R;

public class StatsPoints extends Fragment {

    View mainView;
    SwipeRefreshLayout layout;
    boolean isThisWeek;

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.points));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);

        refreshView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.stats_points, container, false);

        isThisWeek = true;

        View.OnClickListener segmentClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isThisWeek = !isThisWeek;

                TextView segment_button_this_week = (TextView) mainView.findViewById(R.id.segment_button_this_week);
                TextView segment_button_overall = (TextView) mainView.findViewById(R.id.segment_button_overall);

                if (isThisWeek) {
                    Drawable activeDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.round_corners_segmented_active);
                    segment_button_this_week.setBackground(activeDrawable);
                    segment_button_this_week.setTextColor(Color.WHITE);

                    segment_button_overall.setBackground(null);
                    segment_button_overall.setBackgroundColor(Color.TRANSPARENT);
                    segment_button_overall.setTextColor(Color.parseColor("#3792ef"));
                } else {
                    Drawable activeDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.round_corners_segmented_active_right);
                    segment_button_overall.setBackground(activeDrawable);
                    segment_button_overall.setTextColor(Color.WHITE);

                    segment_button_this_week.setBackground(null);
                    segment_button_this_week.setBackgroundColor(Color.TRANSPARENT);
                    segment_button_this_week.setTextColor(Color.parseColor("#3792ef"));
                }

                StatsPoints.this.refreshView();
            }
        };
        mainView.findViewById(R.id.segment_button_this_week).setOnClickListener(segmentClickListener);
        mainView.findViewById(R.id.segment_button_overall).setOnClickListener(segmentClickListener);

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

    public void refreshView() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkManager.getInstance().getStudentActivityPoint(isThisWeek?"7d":"overall");
                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        call_refresh();
                    }
                });
            }
        }).start();

    }

    private void call_refresh() {

    }
}

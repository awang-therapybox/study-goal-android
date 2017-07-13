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
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.studygoal.jisc.Adapters.ActivityPointsAdapter;
import com.studygoal.jisc.MainActivity;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.LinguisticManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.SocialManager;
import com.studygoal.jisc.Models.ActivityPoints;
import com.studygoal.jisc.R;

public class StatsPoints extends Fragment {

    private View mainView;
    private TextView activity_points_value;

    private ActivityPointsAdapter adapter;

    private boolean isThisWeek = true;

    @Override
    public void onResume() {
        super.onResume();

        MainActivity a = DataManager.getInstance().mainActivity;
        a.setTitle(getString(R.string.points));
        a.hideAllButtons();
        a.showCertainButtons(5);

        refreshView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.stats_points, container, false);

        ListView activity_points_list_view = (ListView) mainView.findViewById(R.id.activity_points_list_view);
        adapter = new ActivityPointsAdapter(getContext());
        activity_points_list_view.setAdapter(adapter);

        activity_points_value = (TextView) mainView.findViewById(R.id.activity_points_value);
        SegmentClickListener l = new SegmentClickListener();
        mainView.findViewById(R.id.segment_button_this_week).setOnClickListener(l);
        mainView.findViewById(R.id.segment_button_overall).setOnClickListener(l);

        showAlertDialog();

        return mainView;
    }

    private void refreshView() {
        DataManager.getInstance().mainActivity.showProgressBar(null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkManager.getInstance().getStudentActivityPoint(isThisWeek?"7d":"overall");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        call_refresh();
                    }
                });
            }
        }).start();
    }

    private void call_refresh() {
        DataManager.getInstance().mainActivity.hideProgressBar();

        adapter.notifyDataSetChanged();

        int sum = 0;
        for(ActivityPoints p: DataManager.getInstance().user.points) {
            sum += Integer.parseInt(p.points);
        }
        activity_points_value.setText(String.valueOf(sum));
    }

    private void showAlertDialog() {
        final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean isStaff = DataManager.getInstance().user.isStaff;
        boolean isStatsAlert = preferences.getBoolean("stats_alert", true);
        if (isStaff && isStatsAlert) {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getContext());
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
    }

    private class SegmentClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isThisWeek = !isThisWeek;

            TextView segment_button_this_week = (TextView) mainView.findViewById(R.id.segment_button_this_week);
            TextView segment_button_overall = (TextView) mainView.findViewById(R.id.segment_button_overall);

            if (isThisWeek) {
                Drawable activeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.round_corners_segmented_active);
                segment_button_this_week.setBackground(activeDrawable);
                segment_button_this_week.setTextColor(Color.WHITE);

                segment_button_overall.setBackground(null);
                segment_button_overall.setBackgroundColor(Color.TRANSPARENT);
                segment_button_overall.setTextColor(Color.parseColor("#3792ef"));
            } else {
                Drawable activeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.round_corners_segmented_active_right);
                segment_button_overall.setBackground(activeDrawable);
                segment_button_overall.setTextColor(Color.WHITE);

                segment_button_this_week.setBackground(null);
                segment_button_this_week.setBackgroundColor(Color.TRANSPARENT);
                segment_button_this_week.setTextColor(Color.parseColor("#3792ef"));
            }

            StatsPoints.this.refreshView();
        }
    }
}

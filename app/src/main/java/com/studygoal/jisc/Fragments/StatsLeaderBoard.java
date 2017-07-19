package com.studygoal.jisc.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.studygoal.jisc.Adapters.GenericAdapter;
import com.studygoal.jisc.Adapters.ModuleAdapter2;
import com.studygoal.jisc.MainActivity;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.Courses;
import com.studygoal.jisc.Models.ED;
import com.studygoal.jisc.Models.Friend;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class StatsLeaderBoard extends Fragment {

    private AppCompatTextView module;
    private String selectedPeriod;
    private ListView rankListView;


    static final String[] EVENTS = new String[] { "Calculate 101", "Calculate 102", "Calculate 103", "Calculate 101" };

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(getString(R.string.leader_board));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.stats_leaderboard, container, false);

        rankListView = (ListView) mainView.findViewById(R.id.stats_leaderBoard_listView);
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.stats_event_attendance_list_view_header, rankListView, false);
        rankListView.addHeaderView(header);
        rankListView.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.list_event_attendance, EVENTS));


        setUpModule(mainView);

//        ((MainActivity) getActivity()).showProgressBar(null);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        getData();
//                        ((MainActivity) getActivity()).hideProgressBar();
//                    }
//                });
//            }
//        }).start();

        return mainView;
    }

    private void setUpModule(View mainView){
        module = (AppCompatTextView) mainView.findViewById(R.id.module_list);
        module.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        module.setTypeface(DataManager.getInstance().myriadpro_regular);
        module.setText(R.string.anymodule);
        ((TextView) mainView.findViewById(R.id.module)).setTypeface(DataManager.getInstance().myriadpro_regular);
        module.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.setCancelable(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((MainActivity) getActivity()).hideProgressBar();
                            }
                        });
                    }
                });

                if (DataManager.getInstance().mainActivity.isLandscape) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = (int) (displaymetrics.widthPixels * 0.3);

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = width;
                    dialog.getWindow().setAttributes(params);
                }

                ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_module);

                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(new ModuleAdapter2(DataManager.getInstance().mainActivity, module.getText().toString()));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String titleText = ((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString();

                        List<Courses> coursesList = new Select().from(Courses.class).execute();

                        for (int j = 0; j < coursesList.size(); j++) {
                            String courseName = coursesList.get(j).name;
                            if(courseName.equals(titleText)) {
                                return;
                            }
                        }

                        dialog.dismiss();
                        module.setText(titleText);
                    }
                });
                ((MainActivity) getActivity()).showProgressBar2("");
                dialog.show();
            }
        });

        final TextView last_7d_text_view = (TextView) mainView.findViewById(R.id.last_7d_text_view);
        final TextView last_30d_text_view = (TextView) mainView.findViewById(R.id.last_30d_text_view);
        last_7d_text_view.setTypeface(DataManager.getInstance().myriadpro_regular);
        last_30d_text_view.setTypeface(DataManager.getInstance().myriadpro_regular);
        selectedPeriod = getString(R.string.last_7_days);

        LinearLayout period_segmented = (LinearLayout) mainView.findViewById(R.id.period_segmented);
        period_segmented.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPeriod.equals(getString(R.string.last_7_days))) {
                    selectedPeriod = getString(R.string.last_30_days);
                    last_30d_text_view.setTextColor(Color.parseColor("#ffffff"));
                    last_30d_text_view.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.circle_background_blue_right));

                    last_7d_text_view.setTextColor(Color.parseColor("#3691ee"));
                    last_7d_text_view.setBackground(null);

                } else {
                    selectedPeriod = getString(R.string.last_7_days);
                    last_30d_text_view.setTextColor(Color.parseColor("#3691ee"));
                    last_30d_text_view.setBackground(null);

                    last_7d_text_view.setTextColor(Color.parseColor("#ffffff"));
                    last_7d_text_view.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.circle_background_blue_left));
                }
            }
        });
    }
}
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

public class StatsAttedance extends Fragment {

    public LineChart lineChart;
    public BarChart barchart;
    AppCompatTextView module;

    RelativeLayout chart_layout;
    List<ED> list;
    String selectedPeriod;

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(getString(R.string.attendance));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View mainView = inflater .inflate(R.layout.stats_attendance, container, false);

        selectedPeriod = getString(R.string.last_7_days);

        lineChart = (LineChart) mainView.findViewById(R.id.chart);
        barchart = (BarChart) mainView.findViewById(R.id.barchart);
        chart_layout = (RelativeLayout) mainView.findViewById(R.id.chart_layout);

//        graph_scroll = (HorizontalScrollView) mainView.findViewById(R.id.horizontal_graph_scroll);

        // no description text
        lineChart.getDescription().setEnabled(false);
        barchart.getDescription().setEnabled(false);

        // enable touch gestures
        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getAxisRight().setEnabled(false);

        barchart.setTouchEnabled(false);
        barchart.setDragEnabled(false);
        barchart.setScaleEnabled(false);
        barchart.setPinchZoom(false);
        barchart.setDrawGridBackground(false);
        barchart.getAxisRight().setEnabled(false);
        barchart.setFitBars(true);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTypeface(DataManager.getInstance().myriadpro_regular);
        yAxis.setTextColor(Color.BLACK);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setDrawGridLines(true);
        yAxis.setAxisLineColor(Color.TRANSPARENT);
        yAxis.setTextSize(14f);
        yAxis.setAxisMinimum(0.0f);
        yAxis.setGranularity(1.0f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTypeface(DataManager.getInstance().myriadpro_regular);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setTextSize(14f);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(-0.2f);

        Legend legend = lineChart.getLegend();
        legend.setTextSize(14f);
        legend.setTypeface(DataManager.getInstance().myriadpro_regular);

        yAxis = barchart.getAxisLeft();
        yAxis.setTypeface(DataManager.getInstance().myriadpro_regular);
        yAxis.setTextSize(14f);
        yAxis.setTextColor(Color.BLACK);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setDrawGridLines(true);
        yAxis.setAxisLineColor(Color.TRANSPARENT);
        yAxis.setAxisMinimum(0.0f);
        yAxis.setGranularity(1.0f);

        xAxis = barchart.getXAxis();
        xAxis.setTypeface(DataManager.getInstance().myriadpro_regular);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(14f);
        xAxis.setDrawLabels(true);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(-0.5f);

        legend = barchart.getLegend();
        legend.setTextSize(14f);
        legend.setTypeface(DataManager.getInstance().myriadpro_regular);

        module = (AppCompatTextView) mainView.findViewById(R.id.module_list);
        module.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        module.setTypeface(DataManager.getInstance().myriadpro_regular);
        module.setText(R.string.anymodule);

        final TextView description = (TextView) mainView.findViewById(R.id.description);
        description.setTypeface(DataManager.getInstance().myriadpro_regular);
        description.setText(R.string.last_week);

        ((TextView) mainView.findViewById(R.id.module)).setTypeface(DataManager.getInstance().myriadpro_regular);

        //Setting the module
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

                ((MainActivity) getActivity()).showProgressBar(null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getData();
                                ((MainActivity) getActivity()).hideProgressBar();
                            }
                        });
                    }
                }).start();
            }
        });

        ((ImageView)mainView.findViewById(R.id.change_graph_btn)).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bar_graph));
        mainView.findViewById(R.id.change_graph_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lineChart.getVisibility() == View.VISIBLE){
                    lineChart.setVisibility(View.INVISIBLE);
                    barchart.setVisibility(View.VISIBLE);
                    ((ImageView)v).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line_graph));

                }else{
                    lineChart.setVisibility(View.VISIBLE);
                    barchart.setVisibility(View.INVISIBLE);
                    ((ImageView)v).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bar_graph));
                }

                getData();
            }
        });

        ((MainActivity) getActivity()).showProgressBar(null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                        ((MainActivity) getActivity()).hideProgressBar();
                    }
                });
            }
        }).start();

        mainView.findViewById(R.id.change_graph_btn).performClick();

        return mainView;
    }


    private void getData() {

    }

    private void setData() {
    }

    public LineData getLineData(ArrayList<Entry> vals1, String name) {

        LineData data = new LineData(getLineDataSet(vals1, name));
        data.setValueTypeface(DataManager.getInstance().myriadpro_regular);
        data.setDrawValues(false);

        return data;
    }

    public BarData getBarData(ArrayList<BarEntry> vals2, String name) {
        BarData barData = new BarData(getBarDataSet(vals2, name));
        barData.setValueTypeface(DataManager.getInstance().myriadpro_regular);
        barData.setDrawValues(false);
        barData.setValueTextColor(0xff000000);
        barData.setBarWidth(0.70f);

        return barData;
    }

    public LineDataSet getLineDataSet(ArrayList<Entry> vals1, String name) {
        LineDataSet lineDataSet = new LineDataSet(vals1, name);
        lineDataSet.setCubicIntensity(0.0f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleColor(0xFF8864C8);
        lineDataSet.setColor(0xFF8864C8);
        lineDataSet.setFillColor(0xFF8864C8);
        lineDataSet.setValueTextColor(0xFF8864C8);
        if(!name.equals(getString(R.string.me))) {
            lineDataSet.setColor(0xFF3791ee);
            lineDataSet.setFillColor(0xFF3791ee);
            lineDataSet.setCircleColor(0xFF3791ee);
            lineDataSet.setValueTextColor(0xFF3791ee);
        }
        lineDataSet.setFillAlpha(0);
        lineDataSet.setDrawValues(false);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSet.setDrawHorizontalHighlightIndicator(true);

        return lineDataSet;
    }

    public BarDataSet getBarDataSet(ArrayList<BarEntry> vals2, String name) {
        BarDataSet barDataSet = new BarDataSet(vals2,name);
        barDataSet.setColor(0xFF8864C8);
        barDataSet.setValueTextColor(0xFF8864C8);
        if(!name.equals(getString(R.string.me))) {
            barDataSet.setColor(0xFF3791ee);
            barDataSet.setValueTextColor(0xFF3791ee);
        }
        barDataSet.setDrawValues(false);
        barDataSet.setValueTextSize(50);
        barDataSet.setValueTextSize(14f);
        return barDataSet;
    }
}
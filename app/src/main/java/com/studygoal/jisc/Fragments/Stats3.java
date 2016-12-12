package com.studygoal.jisc.Fragments;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
import com.studygoal.jisc.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Stats3 extends Fragment {


    public LineChart lineChart;
    public BarChart barchart;
    AppCompatTextView module;
    AppCompatTextView period;
    AppCompatTextView compareTo;
    RelativeLayout chart_layout;
    List<ED> list;
    HorizontalScrollView graph_scroll;

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(getString(R.string.engagement_graph));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(8);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.stats3, container, false);

        lineChart = (LineChart) mainView.findViewById(R.id.chart);
        barchart = (BarChart) mainView.findViewById(R.id.barchart);
        chart_layout = (RelativeLayout) mainView.findViewById(R.id.chart_layout);

        graph_scroll = (HorizontalScrollView) mainView.findViewById(R.id.horizontal_graph_scroll);

        // no description text
        lineChart.setDescription("");
        barchart.setDescription("");

        // enable touch gestures
        lineChart.setTouchEnabled(false);

        // enable scaling and dragging
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(false);

        lineChart.setDrawGridBackground(false);

        YAxis y = lineChart.getAxisLeft();
        y.setTypeface(DataManager.getInstance().myriadpro_regular);
        y.setStartAtZero(true);
        y.setTextColor(Color.BLACK);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(true);
        y.setAxisLineColor(Color.TRANSPARENT);

        XAxis x = lineChart.getXAxis();
        x.setTypeface(DataManager.getInstance().myriadpro_regular);
        x.setTextColor(Color.BLACK);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setAxisLineColor(Color.BLACK);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(true);

        Legend legend = lineChart.getLegend();
        legend.setTextSize(14f);
        legend.setTypeface(DataManager.getInstance().myriadpro_regular);

        module = (AppCompatTextView) mainView.findViewById(R.id.module_list);
        module.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        module.setTypeface(DataManager.getInstance().myriadpro_regular);
        module.setText(R.string.anymodule);

        period = (AppCompatTextView) mainView.findViewById(R.id.period_list);
        period.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        period.setTypeface(DataManager.getInstance().myriadpro_regular);
        period.setText(R.string.last_7_days);

        compareTo = (AppCompatTextView) mainView.findViewById(R.id.compareto);
        compareTo.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        compareTo.setTypeface(DataManager.getInstance().myriadpro_regular);
        compareTo.setText(R.string.no_one);

        getData();

        final TextView description = (TextView) mainView.findViewById(R.id.description);
        description.setTypeface(DataManager.getInstance().myriadpro_regular);
        description.setText(R.string.last_week);

        ((TextView) mainView.findViewById(R.id.module)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.period)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.compare_to)).setTypeface(DataManager.getInstance().myriadpro_regular);

        //Setting the module
        module.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.setCancelable(false);
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
                        dialog.dismiss();
                        module.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                 getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (module.getText().toString().replace(" -", "").equals(getString(R.string.anymodule)) && (compareTo.getText().toString().equals(getString(R.string.average)) || compareTo.getText().toString().equals(getString(R.string.top10))) ){
                                            compareTo.setText(R.string.no_one);
                                        }
                                        getData();
                                        ((MainActivity) getActivity()).hideProgressBar();
                                    }
                                });
                            }
                        }).start();

                    }
                });
                ((MainActivity) getActivity()).showProgressBar2("");
                dialog.show();
            }
        });

        period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.setCancelable(false);
                if (DataManager.getInstance().mainActivity.isLandscape) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = (int) (displaymetrics.widthPixels * 0.3);

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = width;
                    dialog.getWindow().setAttributes(params);
                }

                ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.time_period);

                ArrayList<String> items = new ArrayList<>();
//                items.add(getString(R.string.last_24_hours));
                items.add(getString(R.string.last_7_days));
                items.add(getString(R.string.last_30_days));
                items.add(getString(R.string.Overall));
                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(new GenericAdapter(DataManager.getInstance().mainActivity, period.getText().toString(), items));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        period.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
//                        if(period.getText().toString().equals(getString(R.string.last_24_hours))) {
//                            description.setText(R.string.last_day);
//                        } else
                        if (period.getText().toString().equals(getString(R.string.last_7_days))) {
                            description.setText(R.string.last_week_engagement);
                        } else if (period.getText().toString().equals(getString(R.string.last_30_days))) {
                            description.setText(R.string.last_month_engagement);
                        } else if (period.getText().toString().equals(getString(R.string.overall))) {
                            description.setText(R.string.Overall_engagement);
                        }
                        dialog.dismiss();
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
                ((MainActivity) getActivity()).showProgressBar2("");
                dialog.show();
            }
        });

        compareTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!module.getText().toString().equals(DataManager.getInstance().mainActivity.getString(R.string.any_module))) {
                    final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_spinner_layout);
                    dialog.setCancelable(false);

                    if (DataManager.getInstance().mainActivity.isLandscape) {
                        DisplayMetrics displaymetrics = new DisplayMetrics();
                        DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                        int width = (int) (displaymetrics.widthPixels * 0.3);

                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        params.width = width;
                        dialog.getWindow().setAttributes(params);
                    }

                    ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                    ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_student);

                    ArrayList<String> items = new ArrayList<>();
                    items.add(getString(R.string.no_one));
//                    items.add(getString(R.string.top10));
                    items.add(getString(R.string.average));
                    List<Friend> friendList;
                    friendList = new Select().from(Friend.class).execute();
                    for (int i = 0; i < friendList.size(); i++)
                        items.add(friendList.get(i).name);
                    final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                    listView.setAdapter(new GenericAdapter(DataManager.getInstance().mainActivity, compareTo.getText().toString(), items));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            compareTo.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                            dialog.dismiss();
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
                    ((MainActivity) getActivity()).showProgressBar2("");
                    dialog.show();
                } else {
                    final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_spinner_layout);
                    dialog.setCancelable(false);
                    if (DataManager.getInstance().mainActivity.isLandscape) {
                        DisplayMetrics displaymetrics = new DisplayMetrics();
                        DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                        int width = (int) (displaymetrics.widthPixels * 0.3);

                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        params.width = width;
                        dialog.getWindow().setAttributes(params);
                    }

                    ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                    ((TextView) dialog.findViewById(R.id.dialog_title)).setText("");

                    ArrayList<String> items = new ArrayList<>();
                    items.add(getString(R.string.no_one));
//                    items.add(getString(R.string.top10));
//                    items.add(getString(R.string.average));
                    List<Friend> friendList;
                    friendList = new Select().from(Friend.class).execute();
                    for (int i = 0; i < friendList.size(); i++)
                        items.add(friendList.get(i).name);
                    final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                    listView.setAdapter(new GenericAdapter(DataManager.getInstance().mainActivity, compareTo.getText().toString(), items));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            compareTo.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                            dialog.dismiss();
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
                    ((MainActivity) getActivity()).showProgressBar2("");
                    dialog.show();
                }
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
            }
        });

        return mainView;
    }


    private void getData() {
        String fileterType;
        String compareValue = null;
        String compareType = null;
        if (new Select().from(Module.class).where("module_name LIKE ?", "%" + module.getText().toString().replace(" -", "") + "%").exists()) {
            fileterType = "module";
        } else {
            fileterType = "course";
        }
        if (compareTo.getText().toString().contains("Top")) {
            compareValue = "10";
            compareType = "top";
        } else if (!compareTo.getText().toString().equals(getString(R.string.no_one)) && !compareTo.getText().toString().equals(getString(R.string.top10)) && !compareTo.getText().toString().equals(getString(R.string.average))) {
            compareValue = ((Friend) new Select().from(Friend.class).where("name = ?", compareTo.getText().toString()).executeSingle()).jisc_student_id.replace("[", "").replace("]", "").replace("\"", "");
            compareType = "friend";
        } else if (compareTo.getText().toString().contains("Average")){
            compareType = "average";
        }
        if (module.getText().toString().replace(" -", "").equals(getString(R.string.anymodule)) && compareTo.getText().toString().equals(getString(R.string.no_one)) && period.getText().toString().equals("Overall")) {
            list = NetworkManager.getInstance().get_ED();
        }else if(module.getText().toString().replace(" -", "").equals(getString(R.string.anymodule)) && !compareTo.getText().toString().equals(getString(R.string.no_one))){
            list = NetworkManager.getInstance().get_ED_for_time_period_module_and_compareTo_allActivity(DataManager.getInstance().api_values.get(period.getText().toString().toLowerCase()).replace(" ", "_").toLowerCase(),compareValue,compareType);
        } else if (module.getText().toString().replace(" -", "").equals(getString(R.string.anymodule)) && compareTo.getText().toString().equals(getString(R.string.no_one)) && !period.getText().toString().equals("")) {
            list = NetworkManager.getInstance().get_ED_for_time_period(DataManager.getInstance().api_values.get(period.getText().toString().toLowerCase()).replace(" ", "_").toLowerCase());
        } else if (!module.getText().toString().replace(" -", "").equals(getString(R.string.anymodule)) && compareTo.getText().toString().equals(getString(R.string.no_one)) && !period.getText().toString().equals("")) {
            if (fileterType.equals("module")) {
                list = NetworkManager.getInstance().get_ED_for_time_and_course(DataManager.getInstance().api_values.get(period.getText().toString().toLowerCase()).replace(" ", "_").toLowerCase(), fileterType, ((Module) new Select().from(Module.class).where("module_name = ?", module.getText().toString().replace(" -", "")).executeSingle()).id);
            } else {
                list = NetworkManager.getInstance().get_ED_for_time_and_course(DataManager.getInstance().api_values.get(period.getText().toString().toLowerCase()).replace(" ", "_").toLowerCase(), fileterType, ((Courses) new Select().from(Courses.class).where("course_name = ?", module.getText().toString().replace(" -", "")).executeSingle()).id);
            }
        } else if (!module.getText().toString().equals(getString(R.string.anymodule)) && !compareTo.getText().toString().equals(getString(R.string.no_one)) && !period.getText().toString().equals("")) {
            if (fileterType.equals("module") && !compareTo.getText().toString().equals(getString(R.string.average))) {
                list = NetworkManager.getInstance().get_ED_for_time_period_module_and_compareTo(DataManager.getInstance().api_values.get(period.getText().toString().toLowerCase()).replace(" ", "_").toLowerCase(), fileterType, ((Module) new Select().from(Module.class).where("module_name = ?", module.getText().toString().replace(" -", "")).executeSingle()).id, compareValue, compareType);
            } else if (!compareTo.getText().toString().equals(getString(R.string.average))) {
                list = NetworkManager.getInstance().get_ED_for_time_period_module_and_compareTo(DataManager.getInstance().api_values.get(period.getText().toString().toLowerCase()).replace(" ", "_").toLowerCase(), fileterType, ((Courses) new Select().from(Courses.class).where("course_name = ?", module.getText().toString().replace(" -", "")).executeSingle()).id, compareValue, compareType);
            } else if (fileterType.equals("module") && compareTo.getText().toString().equals(getString(R.string.average))) {
                list = NetworkManager.getInstance().get_ED_for_time_period_module_and_compareTo_average(DataManager.getInstance().api_values.get(period.getText().toString().toLowerCase()).replace(" ", "_").toLowerCase(), fileterType, ((Module) new Select().from(Module.class).where("module_name = ?", module.getText().toString().replace(" -", "")).executeSingle()).id, compareType);
            } else if (compareTo.getText().toString().equals(getString(R.string.average))) {
                list = NetworkManager.getInstance().get_ED_for_time_period_module_and_compareTo_average(DataManager.getInstance().api_values.get(period.getText().toString().toLowerCase()).replace(" ", "_").toLowerCase(), fileterType, ((Courses) new Select().from(Courses.class).where("course_name = ?", module.getText().toString().replace(" -", "")).executeSingle()).id, compareType);
            }
        }
        setData();
        lineChart.invalidate();
        DataManager.getInstance().mainActivity.hideProgressBar();
    }

    private void setData() {
        ArrayList<String> xVals = new ArrayList<>();

        if (compareTo.getText().toString().equals(getString(R.string.no_one))) {
            if (period.getText().toString().equals(getString(R.string.last_24_hours))) {

                ViewGroup.LayoutParams params_main = chart_layout.getLayoutParams();
                params_main.width = Utils.dpToPx((int) ((list.size() + 2) * 78.57142857));
                chart_layout.setLayoutParams(params_main);

                ViewGroup.LayoutParams params = lineChart.getLayoutParams();
                params.width = Utils.dpToPx((int) ((list.size() + 2) * 78.57142857));
                lineChart.setLayoutParams(params);

                String name = getString(R.string.me);
                ArrayList<Entry> vals1 = new ArrayList<>();


                for (int i = 0; i < list.size(); i++) {
                    vals1.add(new Entry(list.get(i).activity_points, xVals.size()));
                    xVals.add(list.get(i).hour);
                }

                vals1.add(new Entry(0, xVals.size()));
                xVals.add("");

                // create a dataset and give it a type
                LineDataSet set1 = new LineDataSet(vals1, name);
                set1.setDrawCubic(false);
                set1.setCubicIntensity(0.0f);
                set1.setDrawFilled(true);
                set1.setDrawCircles(false);
                set1.setLineWidth(1.2f);
                set1.setCircleSize(2f);
                set1.setCircleColor(0xFF8864C8);
                set1.setColor(0xFF8864C8);
                set1.setFillColor(0xFF8864C8);
                set1.setFillAlpha(0);
                set1.setDrawHorizontalHighlightIndicator(true);

                // create a data object with the datasets
                LineData data = new LineData(xVals, set1);
                data.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                data.setValueTextSize(14f);
                data.setDrawValues(false);

                // set data
                lineChart.setData(data);
            } else if (period.getText().toString().equals(getString(R.string.last_7_days))) {

                ViewGroup.LayoutParams params_main = chart_layout.getLayoutParams();
                params_main.width = Utils.dpToPx((int) ((list.size() + 2) * 78.57142857));
                chart_layout.setLayoutParams(params_main);

                ViewGroup.LayoutParams params = lineChart.getLayoutParams();
                params.width = Utils.dpToPx((int) ((list.size() + 2) * 78.57142857));
                lineChart.setLayoutParams(params);

                ViewGroup.LayoutParams paramsBar = barchart.getLayoutParams();
                paramsBar.width = Utils.dpToPx((int) ((list.size() + 2) * 78.57142857));
                barchart.setLayoutParams(paramsBar);

                ArrayList<Entry> vals1 = new ArrayList<>();
                ArrayList<BarEntry> vals2 = new ArrayList<>();
                String name = getString(R.string.me);

                String day;
                Date date = new Date();
                date.setTime(date.getTime() - 6*86400000);

                Collections.reverse(list);

                for (int i = 0; i < list.size(); i++) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
                    day = dateFormat.format(date);
                    date.setTime(date.getTime() + 86400000);
                    vals1.add(new Entry(list.get(i).activity_points, xVals.size()));
                    vals2.add(new BarEntry(list.get(i).activity_points, xVals.size()));
                    xVals.add(day);
                }
//                vals1.add(new Entry(0, xVals.size()));
//                xVals.add("");

                // create a dataset and give it a type
                LineDataSet set1 = new LineDataSet(vals1, name);
                set1.setDrawCubic(false);
                set1.setCubicIntensity(0.0f);
                set1.setDrawFilled(true);
                set1.setDrawCircles(false);
                set1.setLineWidth(1.2f);
                set1.setCircleSize(2f);
                set1.setCircleColor(0xFF8864C8);
                set1.setColor(0xFF8864C8);
                set1.setFillColor(0xFF8864C8);
                set1.setFillAlpha(0);
                set1.setDrawHorizontalHighlightIndicator(true);

                // create a data object with the datasets
                LineData data = new LineData(xVals, set1);
                data.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                data.setValueTextSize(14f);
                data.setDrawValues(false);

                BarDataSet barDataSet = new BarDataSet(vals2,name);
                barDataSet.setColor(0xFF8864C8);
                barDataSet.setDrawValues(true);
                barDataSet.setValueTextSize(50);
                barDataSet.setValueTextColor(0xFF000000);

                BarData barData = new BarData(xVals,barDataSet);
                barData.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                barData.setValueTextSize(50f);
                barData.setDrawValues(true);

                barchart.getAxisLeft().setDrawGridLines(false);
                barchart.getXAxis().setDrawGridLines(false);
                barchart.setDrawValueAboveBar(true);
                barchart.setDrawBorders(false);
                barchart.setBackgroundColor(Color.TRANSPARENT);
                barchart.setDrawGridBackground(false);
                barchart.setMaxVisibleValueCount(vals1.size());
                barchart.setBackgroundColor(0xFFFFFFFF);
                barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                // set data
                lineChart.setData(data);

                barchart.setData(barData);
                barchart.invalidate();
                barchart.setTouchEnabled(false);

            } else if (period.getText().toString().equals(getString(R.string.last_30_days))) {
                ViewGroup.LayoutParams params_main = chart_layout.getLayoutParams();
                params_main.width = Utils.dpToPx((int) (6 * 78.57142857));
                chart_layout.setLayoutParams(params_main);

                ViewGroup.LayoutParams params = lineChart.getLayoutParams();
                params.width = Utils.dpToPx((int) (6 * 78.57142857));
                lineChart.setLayoutParams(params);

                ViewGroup.LayoutParams paramsBar = barchart.getLayoutParams();
                paramsBar.width = Utils.dpToPx((int) (6 * 78.57142857));
                barchart.setLayoutParams(paramsBar);

                ArrayList<Entry> vals1 = new ArrayList<>();
                ArrayList<BarEntry> vals2 = new ArrayList<>();
                String name = getString(R.string.me);
                Integer val1 = 0;

//                xVals.add(0, "");
//                vals1.add(new Entry(0, 0));

                Collections.reverse(list);

                Date date = new Date();
                long time = date.getTime() - 21*86400000;
                date.setTime(time);
                String day;

                for (int i = 0; i < list.size(); i++) {
                    val1 = val1 + list.get(i).activity_points;
                    if (i == 6 || i == 13 || i == 20 || i == 27){
                        vals1.add(new Entry(val1, xVals.size()));
                        vals2.add(new BarEntry(val1, xVals.size()));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        day = dateFormat.format(date);
                        date.setTime(date.getTime() + 7*86400000);
                        xVals.add(day);
                        val1 = 0;
                    }
                }

//                vals1.add(new Entry(0, xVals.size()));
//                xVals.add("");

                // create a dataset and give it a type
                LineDataSet set1 = new LineDataSet(vals1, name);
                set1.setDrawCubic(false);
                set1.setCubicIntensity(0.0f);
                set1.setDrawFilled(true);
                set1.setDrawCircles(false);
                set1.setLineWidth(1.2f);
                set1.setCircleSize(2f);
                set1.setCircleColor(0xFF8864C8);
                set1.setColor(0xFF8864C8);
                set1.setFillColor(0xFF8864C8);
                set1.setFillAlpha(0);
                set1.setDrawHorizontalHighlightIndicator(true);

                // create a data object with the datasets
                LineData data = new LineData(xVals, set1);
                data.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                data.setValueTextSize(14f);
                data.setDrawValues(false);

                BarDataSet barDataSet = new BarDataSet(vals2,name);
                barDataSet.setColor(0xFF8864C8);
                barDataSet.setDrawValues(true);
                barDataSet.setValueTextSize(50);
                barDataSet.setValueTextColor(0xFF000000);

                BarData barData = new BarData(xVals,barDataSet);
                barData.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                barData.setValueTextSize(50f);
                barData.setDrawValues(true);

                barchart.getAxisLeft().setDrawGridLines(false);
                barchart.getXAxis().setDrawGridLines(false);
                barchart.setBackgroundColor(Color.TRANSPARENT);
                barchart.setDrawGridBackground(false);
                barchart.setDrawBorders(false);
                barchart.setDrawValueAboveBar(true);
                barchart.setMaxVisibleValueCount(vals1.size());
                barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                // set data-
                lineChart.setData(data);

                barchart.setData(barData);
                barchart.invalidate();
                barchart.setTouchEnabled(false);

            } else if (period.getText().toString().equals(getString(R.string.overall))) {
                int size = list.size();
                if (size > 38) {
                    size = 38;
                }

                ViewGroup.LayoutParams params_main = chart_layout.getLayoutParams();
                params_main.width = Utils.dpToPx((int) ((size + 1) * 78.57142857));
                chart_layout.setLayoutParams(params_main);

                ViewGroup.LayoutParams params = lineChart.getLayoutParams();
                params.width = Utils.dpToPx((int) ((size + 1) * 78.57142857));
                lineChart.setLayoutParams(params);

                ViewGroup.LayoutParams paramsBar = barchart.getLayoutParams();
                paramsBar.width = Utils.dpToPx((int) ((size + 1) * 78.57142857));
                barchart.setLayoutParams(paramsBar);

                ArrayList<Entry> vals1 = new ArrayList<>();
                ArrayList<BarEntry> vals2 = new ArrayList<>();
                String name = getString(R.string.me);
//                vals1.add(new Entry(0, 0));
//                xVals.add("");
                for (int i = 0; i < list.size(); i++) {
                    vals1.add(new Entry(list.get(i).activity_points, xVals.size()));
                    vals2.add(new BarEntry(list.get(i).activity_points, xVals.size()));
                    xVals.add(list.get(i).day);
                }
//                vals1.add(new Entry(0, xVals.size()));
//                xVals.add("");
                // create a dataset and give it a type
                LineDataSet set1 = new LineDataSet(vals1, name);
                set1.setDrawCubic(false);
                set1.setCubicIntensity(0.0f);
                set1.setDrawFilled(true);
                set1.setDrawCircles(false);
                set1.setLineWidth(1.2f);
                set1.setCircleSize(2f);
                set1.setCircleColor(0xFF8864C8);
                set1.setColor(0xFF8864C8);
                set1.setFillColor(0xFF8864C8);
                set1.setFillAlpha(0);
                set1.setDrawHorizontalHighlightIndicator(true);

                // create a data object with the datasets
                LineData data = new LineData(xVals, set1);
                data.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                data.setValueTextSize(5f);
                data.setDrawValues(true);

                BarDataSet barDataSet = new BarDataSet(vals2,name);
                barDataSet.setColor(0xFF8864C8);
                barDataSet.setDrawValues(true);
                barDataSet.setValueTextColor(0xFF000000);

                BarData barData = new BarData(xVals,barDataSet);
                barData.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                barData.setValueTextSize(50f);
                barData.setDrawValues(true);

                Log.e("aici aici aici","sjkhdfasjkhsa");

                barchart.getAxisLeft().setDrawGridLines(false);
                barchart.getXAxis().setDrawGridLines(false);
                barchart.setDrawValueAboveBar(true);
                barchart.setBackgroundColor(Color.TRANSPARENT);
                barchart.setDrawBorders(false);
                barchart.setDrawGridBackground(false);
                barchart.setMaxVisibleValueCount(vals1.size());
                barchart.setBackgroundColor(0xFFFFFFFF);
                barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                // set data
                lineChart.setData(data);
                barchart.setData(barData);
                barchart.invalidate();
                barchart.setTouchEnabled(false);
            }
        } else {
            if (period.getText().toString().equals(getString(R.string.last_7_days))) {

                ArrayList vals3 = new ArrayList<>();
                ArrayList vals4 = new ArrayList<>();
                ArrayList<Entry> vals1 = new ArrayList<>();
                ArrayList<Entry> vals2 = new ArrayList<>();
                ArrayList<BarEntry> vals5 = new ArrayList<>();
                ArrayList<BarEntry> vals6 = new ArrayList<>();
                String name = getString(R.string.me);
                String id = DataManager.getInstance().user.jisc_student_id;
                Integer value_1;
                Integer value_2;
                String day;
                Calendar c = Calendar.getInstance();
                Long curr = c.getTimeInMillis() - 518400000;
                c.setTimeInMillis(curr);

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).student_id.equals(id)) {
                        value_1 = list.get(i).activity_points;
                        vals3.add(value_1);
                    } else {
                        value_2 = list.get(i).activity_points;
                        vals4.add(value_2);
                    }
                }

                Collections.reverse(vals3);
                Collections.reverse(vals4);

                Date date = new Date();
                date.setTime(date.getTime() - 6*86400000);

                for (int i = 0; i < vals3.size(); i++) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
                    day = dateFormat.format(date);
//                    String[] days = new String[] { "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" };
//                    day = days[c.get(Calendar.DAY_OF_WEEK)-1];
//                    c.setTimeInMillis(c.getTimeInMillis() + 86400000);
                    date.setTime(date.getTime() + 86400000);
                    vals1.add(new Entry((Integer) vals3.get(i), xVals.size()));
                    vals2.add(new Entry((Integer) vals4.get(i), xVals.size()));
                    vals5.add(new BarEntry((Integer) vals3.get(i), xVals.size()));
                    vals6.add(new BarEntry((Integer) vals4.get(i), xVals.size()));
                    xVals.add(day);
                }

                xVals.add("");
                // create a dataset and give it a type
                LineDataSet set1 = new LineDataSet(vals1, name);
                set1.setDrawCubic(false);
                set1.setCubicIntensity(0.0f);
                set1.setDrawFilled(true);
                set1.setDrawCircles(false);
                set1.setLineWidth(1.2f);
                set1.setCircleSize(2f);
                set1.setCircleColor(0xFF8864C8);
                set1.setColor(0xFF8864C8);
                set1.setFillColor(0xFF8864C8);
                set1.setFillAlpha(0);
                set1.setDrawHorizontalHighlightIndicator(true);

                ArrayList<LineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1); // add the datasets

                // create a dataset and give it a type
                LineDataSet set2 = new LineDataSet(vals2, compareTo.getText().toString());
                set2.setDrawCubic(false);
                set2.setCubicIntensity(0.0f);
                set2.setDrawFilled(true);
                set2.setDrawCircles(false);
                set2.setLineWidth(1.2f);
                set2.setCircleSize(2f);
                set2.setCircleColor(0xFF3791ee);
                set2.setColor(0xFF3791ee);
                set2.setFillColor(0xFF3791ee);
                set2.setFillAlpha(0);
                set2.setDrawHorizontalHighlightIndicator(true);
                dataSets.add(set2);

                // create a data object with the datasets
                LineData data = new LineData(xVals, dataSets);
                data.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                data.setValueTextSize(14f);
                data.setDrawValues(false);

                // set data
                lineChart.setData(data);

                ArrayList<BarDataSet> barddataset = new ArrayList<>();

                BarDataSet barDataSet = new BarDataSet(vals5,name);
                barddataset.add(barDataSet);
                BarDataSet barDataSet1 = new BarDataSet(vals6,compareTo.getText().toString());
                barddataset.add(barDataSet1);
                barDataSet.setColor(0xFF8864C8);
                BarData barData = new BarData(xVals,barddataset);
                // set data

                barchart.getAxisLeft().setDrawGridLines(false);
                barchart.getXAxis().setDrawGridLines(false);
                barchart.setDrawValueAboveBar(true);
                barchart.setBackgroundColor(Color.TRANSPARENT);
                barchart.setDrawBorders(false);
                barchart.setDrawGridBackground(false);
                barchart.setMaxVisibleValueCount(vals1.size());
                barchart.setBackgroundColor(0xFFFFFFFF);
                barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                barchart.setData(barData);

            } else if (period.getText().toString().equals(getString(R.string.last_30_days))) {

                ViewGroup.LayoutParams params_main = chart_layout.getLayoutParams();
                params_main.width = Utils.dpToPx((int) (6 * 78.57142857));
                chart_layout.setLayoutParams(params_main);

                ViewGroup.LayoutParams params = lineChart.getLayoutParams();
                params.width = Utils.dpToPx((int) (6 * 78.57142857));
                lineChart.setLayoutParams(params);

                ViewGroup.LayoutParams paramsBar = barchart.getLayoutParams();
                paramsBar.width = Utils.dpToPx((int) (6 * 78.57142857));
                barchart.setLayoutParams(paramsBar);

                ArrayList vals3 = new ArrayList<>();
                ArrayList vals4 = new ArrayList<>();
                ArrayList<Entry> vals1 = new ArrayList<>();
                ArrayList<Entry> vals2 = new ArrayList<>();
                ArrayList<BarEntry> vals5 = new ArrayList<>();
                ArrayList<BarEntry> vals6 = new ArrayList<>();

                String name = getString(R.string.me);

                String id = DataManager.getInstance().user.jisc_student_id;

                Integer value_1;
                Integer value_2;
                String label;

                Calendar c = Calendar.getInstance();
                Long curr = c.getTimeInMillis() - (3 * 518400000);
                c.setTimeInMillis(curr);

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).student_id.equals(id)) {
                        value_1 = list.get(i).activity_points;
                        vals3.add(value_1);
                    } else {
                        value_2 = list.get(i).activity_points;
                        vals4.add(value_2);
                    }
                }

                Collections.reverse(vals3);
                Collections.reverse(vals4);

                Integer val1 = 0;
                Integer val2 = 0;

                Date date = new Date();
                long time = date.getTime() - 21*86400000;
                date.setTime(time);

                for (int i = 0; i < vals3.size(); i++) {
                    val1 = val1 + (Integer) vals3.get(i);
                    val2 = val2 + (Integer) vals4.get(i);
                    if (i == 6 || i == 13 || i == 20 || i == 27) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        label = dateFormat.format(date);
                        date.setTime(date.getTime() + 7*86400000);
                        vals1.add(new Entry(val1, xVals.size()));
                        vals2.add(new Entry(val2, xVals.size()));
                        vals5.add(new BarEntry(val1, xVals.size()));
                        vals6.add(new BarEntry(val2, xVals.size()));
                        xVals.add(label);
                        val1 = 0;
                        val2 = 0;
                    }
                }

                ArrayList<LineDataSet> dataSets = new ArrayList<>();

                // create a dataset and give it a type
                LineDataSet set1 = new LineDataSet(vals1, name);
                set1.setDrawCubic(false);
                set1.setCubicIntensity(0.0f);
                set1.setDrawFilled(true);
                set1.setDrawCircles(false);
                set1.setLineWidth(1.2f);
                set1.setCircleSize(2f);
                set1.setCircleColor(0xFF8864C8);
                set1.setColor(0xFF8864C8);
                set1.setFillColor(0xFF8864C8);
                set1.setFillAlpha(0);
                set1.setDrawHorizontalHighlightIndicator(true);
                dataSets.add(set1);

                LineDataSet set2 = new LineDataSet(vals2, compareTo.getText().toString());
                set2.setDrawCubic(false);
                set2.setCubicIntensity(0.0f);
                set2.setDrawFilled(true);
                set2.setDrawCircles(false);
                set2.setLineWidth(1.2f);
                set2.setCircleSize(2f);
                set2.setCircleColor(0xFF3791ee);
                set2.setColor(0xFF3791ee);
                set2.setFillColor(0xFF3791ee);
                set2.setFillAlpha(0);
                set2.setDrawHorizontalHighlightIndicator(true);
                dataSets.add(set2);

                LineData data = new LineData(xVals, dataSets);
                data.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                data.setValueTextSize(14f);
                data.setDrawValues(false);

                // set data
                lineChart.setData(data);

                ArrayList<BarDataSet> barddataset = new ArrayList<>();

                BarDataSet barDataSet = new BarDataSet(vals5,name);
                barddataset.add(barDataSet);
                BarDataSet barDataSet1 = new BarDataSet(vals6,compareTo.getText().toString());
                barddataset.add(barDataSet1);
                barDataSet.setColor(0xFF8864C8);
                BarData barData = new BarData(xVals,barddataset);
                // set data

                barchart.getAxisLeft().setDrawGridLines(false);
                barchart.getXAxis().setDrawGridLines(false);
                barchart.setDrawValueAboveBar(true);
                barchart.setBackgroundColor(Color.TRANSPARENT);
                barchart.setDrawBorders(false);
                barchart.setDrawGridBackground(false);
                barchart.setMaxVisibleValueCount(vals1.size());
                barchart.setBackgroundColor(0xFFFFFFFF);
                barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                barchart.setData(barData);

            } else if (period.getText().toString().equals(getString(R.string.overall))) {

                int size = list.size();
                if (size > 38) {
                    size = 38;
                }

                ViewGroup.LayoutParams params_main = chart_layout.getLayoutParams();
                params_main.width = Utils.dpToPx((int) ((size + 2) * 78.57142857));
                chart_layout.setLayoutParams(params_main);

                ViewGroup.LayoutParams params = lineChart.getLayoutParams();
                params.width = Utils.dpToPx((int) ((size + 2) * 78.57142857));
                lineChart.setLayoutParams(params);

                ViewGroup.LayoutParams paramsBar = barchart.getLayoutParams();
                paramsBar.width = Utils.dpToPx((int) ((size + 2) * 78.57142857));
                barchart.setLayoutParams(paramsBar);

                ArrayList<Entry> vals1 = new ArrayList<>();
                ArrayList<Entry> vals2 = new ArrayList<>();
                ArrayList<BarEntry> vals5 = new ArrayList<>();
                ArrayList<BarEntry> vals6 = new ArrayList<>();
                ArrayList vals3 = new ArrayList<>();
                ArrayList vals4 = new ArrayList<>();

                String name = getString(R.string.me);
                String id = DataManager.getInstance().user.jisc_student_id;

                Integer value_2;
                Integer value_1;

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).student_id.equals(id)) {
                        value_1 = list.get(i).activity_points;
                        vals3.add(value_1);
                    } else {
                        value_2 = list.get(i).activity_points;
                        vals4.add(value_2);
                    }
                }

                for (int i = 0; i < vals3.size(); i++) {
                    vals1.add(new Entry((Integer) vals3.get(i), xVals.size()));
                    vals2.add(new Entry((Integer) vals4.get(i), xVals.size()));
                    vals5.add(new BarEntry((Integer) vals3.get(i), xVals.size()));
                    vals6.add(new BarEntry((Integer) vals4.get(i), xVals.size()));
                    xVals.add(list.get(i).date);
                }


                ArrayList<LineDataSet> dataSets = new ArrayList<>();

                // create a dataset and give it a type
                LineDataSet set1 = new LineDataSet(vals1, name);
                set1.setDrawCubic(false);
                set1.setCubicIntensity(0.0f);
                set1.setDrawFilled(true);
                set1.setDrawCircles(false);
                set1.setLineWidth(1.2f);
                set1.setCircleSize(2f);
                set1.setCircleColor(0xFF8864C8);
                set1.setColor(0xFF8864C8);
                set1.setFillColor(0xFF8864C8);
                set1.setFillAlpha(0);
                set1.setDrawHorizontalHighlightIndicator(true);
                dataSets.add(set1);

                LineDataSet set2 = new LineDataSet(vals2, compareTo.getText().toString());
                set2.setDrawCubic(false);
                set2.setCubicIntensity(0.0f);
                set2.setDrawFilled(true);
                set2.setDrawCircles(false);
                set2.setLineWidth(1.2f);
                set2.setCircleSize(2f);
                set2.setCircleColor(0xFF3791ee);
                set2.setColor(0xFF3791ee);
                set2.setFillColor(0xFF3791ee);
                set2.setFillAlpha(0);
                set2.setDrawHorizontalHighlightIndicator(true);
                dataSets.add(set2);

                LineData data = new LineData(xVals, dataSets);
                data.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                data.setValueTextSize(14f);
                data.setDrawValues(false);

                // set data
                lineChart.setData(data);

                ArrayList<BarDataSet> barddataset = new ArrayList<>();

                BarDataSet barDataSet = new BarDataSet(vals5,name);
                barddataset.add(barDataSet);
                BarDataSet barDataSet1 = new BarDataSet(vals6,compareTo.getText().toString());
                barddataset.add(barDataSet1);
                barDataSet.setColor(0xFF8864C8);
                BarData barData = new BarData(xVals,barddataset);
                // set data

                barchart.getAxisLeft().setDrawGridLines(false);
                barchart.getXAxis().setDrawGridLines(false);
                barchart.setDrawValueAboveBar(true);
                barchart.setBackgroundColor(Color.TRANSPARENT);
                barchart.setDrawBorders(false);
                barchart.setDrawGridBackground(false);
                barchart.setMaxVisibleValueCount(vals1.size());
                barchart.setBackgroundColor(0xFFFFFFFF);
                barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                barchart.setData(barData);

            }
        }
    }
}

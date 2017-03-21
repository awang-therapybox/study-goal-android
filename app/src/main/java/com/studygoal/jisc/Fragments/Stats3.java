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
import android.widget.ImageView;
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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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

public class Stats3 extends Fragment {


    public LineChart lineChart;
    public BarChart barchart;
    AppCompatTextView module;
    AppCompatTextView period;
    AppCompatTextView compareTo;
    RelativeLayout chart_layout;
    List<ED> list;

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(getString(R.string.engagement_graph));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(8);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View mainView = inflater .inflate(R.layout.stats3, container, false);

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

        period = (AppCompatTextView) mainView.findViewById(R.id.period_list);
        period.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        period.setTypeface(DataManager.getInstance().myriadpro_regular);
        period.setText(R.string.last_7_days);

        compareTo = (AppCompatTextView) mainView.findViewById(R.id.compareto);
        compareTo.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        compareTo.setTypeface(DataManager.getInstance().myriadpro_regular);
        compareTo.setText(R.string.no_one);
        compareTo.setAlpha(0.5f);

        final View.OnClickListener compareToListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!module.getText().toString().equals(DataManager.getInstance().mainActivity.getString(R.string.anymodule))) {
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
        };

//        getData();

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

                        if(!module.getText().toString().equals(getString(R.string.anymodule))) {
                            compareTo.setOnClickListener(compareToListener);
                            compareTo.setAlpha(1.0f);
                        } else {
                            compareTo.setOnClickListener(null);
                            compareTo.setAlpha(0.5f);
                        }

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
                //items.add(getString(R.string.Overall));
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

        mainView.findViewById(R.id.change_graph_btn).performClick();

        return mainView;
    }


    private void getData() {

        if(DataManager.getInstance().user.isStaff) {
            list = new ArrayList<>();

            if (compareTo.getText().toString().equals(getString(R.string.no_one))) {
                if (period.getText().toString().equals(getString(R.string.last_7_days))) {
                    for (int i = 0; i < 7; i++) {
                        ED item = new ED();
                        item.day = "" + (i+1);
                        item.activity_points = Math.abs(new Random().nextInt()) % 100;

                        list.add(item);
                    }

                    Collections.sort(list, new Comparator<ED>() {
                        @Override
                        public int compare(ED s1, ED s2) {
                            return s1.day.compareToIgnoreCase(s2.day);
                        }
                    });
                } else if (period.getText().toString().equals(getString(R.string.last_30_days))) {

                    for (int i = 0; i < 30; i++) {
                        ED item = new ED();
                        item.day = "" + (i+1);
                        item.activity_points = Math.abs(new Random().nextInt()) % 100;

                        list.add(item);
                    }

                    Collections.sort(list, new Comparator<ED>() {
                        @Override
                        public int compare(ED s1, ED s2) {
                            return s1.day.compareToIgnoreCase(s2.day);
                        }
                    });
                }  else if (period.getText().toString().equals(getString(R.string.overall))) {

                    try {

                        Calendar calendar = Calendar.getInstance();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date startDate = dateFormat.parse("01/01/2017");
                        Date now = new Date();

                        while (now.after(startDate)) {
                            int numberOfDays = Math.abs(new Random().nextInt()) % 5 + 1;
                            calendar.setTime(startDate);
                            calendar.add(Calendar.DATE, numberOfDays);

                            startDate = calendar.getTime();

                            ED item = new ED();
                            item.day = dateFormat.format(startDate);
                            item.realDate = startDate;
                            item.activity_points = Math.abs(new Random().nextInt()) % 100;

                            list.add(item);
                        }

                        Collections.sort(list, new Comparator<ED>() {
                            @Override
                            public int compare(ED s1, ED s2) {
                                return s1.realDate.compareTo(s2.realDate);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (period.getText().toString().equals(getString(R.string.last_7_days))) {
                    for (int i = 0; i < 7; i++) {
                        ED item = new ED();
                        item.day = "" + (i+1);
                        item.activity_points = Math.abs(new Random().nextInt()) % 100;
                        item.student_id = DataManager.getInstance().user.jisc_student_id;
                        list.add(item);

                        ED item1 = new ED();
                        item1.day = "" + (i+1);
                        item1.activity_points = Math.abs(new Random().nextInt()) % 100;
                        item1.student_id = "";
                        list.add(item1);
                    }

                    Collections.sort(list, new Comparator<ED>() {
                        @Override
                        public int compare(ED s1, ED s2) {
                            return s1.day.compareToIgnoreCase(s2.day);
                        }
                    });
                } else if (period.getText().toString().equals(getString(R.string.last_30_days))) {

                    for (int i = 0; i < 30; i++) {
                        ED item = new ED();
                        item.day = "" + (i+1);
                        item.activity_points = Math.abs(new Random().nextInt()) % 100;
                        item.student_id = DataManager.getInstance().user.jisc_student_id;
                        list.add(item);

                        ED item1 = new ED();
                        item1.day = "" + (i+1);
                        item1.activity_points = Math.abs(new Random().nextInt()) % 100;
                        item1.student_id = "";
                        list.add(item1);
                    }

                    Collections.sort(list, new Comparator<ED>() {
                        @Override
                        public int compare(ED s1, ED s2) {
                            return s1.day.compareToIgnoreCase(s2.day);
                        }
                    });
                }  else if (period.getText().toString().equals(getString(R.string.overall))) {

                    try {

                        Calendar calendar = Calendar.getInstance();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date startDate = dateFormat.parse("01/01/2017");
                        Date now = new Date();

                        while (now.after(startDate)) {
                            int numberOfDays = Math.abs(new Random().nextInt()) % 5 + 1;
                            calendar.setTime(startDate);
                            calendar.add(Calendar.DATE, numberOfDays);

                            startDate = calendar.getTime();

                            ED item = new ED();
                            item.day = dateFormat.format(startDate);
                            item.realDate = startDate;
                            item.activity_points = Math.abs(new Random().nextInt()) % 100;
                            item.student_id = DataManager.getInstance().user.jisc_student_id;
                            list.add(item);

                            ED item1 = new ED();
                            item1.day = dateFormat.format(startDate);
                            item1.realDate = startDate;
                            item1.activity_points = Math.abs(new Random().nextInt()) % 100;
                            item1.student_id = "";
                            list.add(item1);
                        }

                        Collections.sort(list, new Comparator<ED>() {
                            @Override
                            public int compare(ED s1, ED s2) {
                                return s1.realDate.compareTo(s2.realDate);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            setData();
            lineChart.invalidate();
            DataManager.getInstance().mainActivity.hideProgressBar();
            return;
        }

        String filterType;
        String filterValue;
        boolean isCourse = false;

        String moduleTitleName = module.getText().toString().replace(" -", "");
        if (new Select().from(Module.class).where("module_name LIKE ?", "%" + moduleTitleName + "%").exists()) {
            filterType = "module";
            filterValue = ((Module) new Select().from(Module.class).where("module_name = ?", moduleTitleName).executeSingle()).id;
        } else {
            filterType = "course";
            if (new Select().from(Courses.class).where("course_name LIKE ?", "%" + moduleTitleName + "%").exists()) {
                filterValue = ((Courses) new Select().from(Courses.class).where("course_name = ?", moduleTitleName).executeSingle()).id;
                isCourse = true;
            } else {
                filterValue = "";
            }
        }

        String compareValue;
        String compareType;
        if (compareTo.getText().toString().contains("Top")) {
            compareValue = "10";
            compareType = "top";
        } else if (!compareTo.getText().toString().equals(getString(R.string.no_one))
                && !compareTo.getText().toString().equals(getString(R.string.top10))
                && !compareTo.getText().toString().equals(getString(R.string.average))) {
            compareValue = ((Friend) new Select().from(Friend.class).where("name = ?", compareTo.getText().toString()).executeSingle()).jisc_student_id.replace("[", "").replace("]", "").replace("\"", "");
            compareType = "friend";
        } else if (compareTo.getText().toString().contains("Average")){
            compareValue = "";
            compareType = "average";
        } else {
            compareType = "";
            compareValue = "";
        }

        String scope = DataManager.getInstance().api_values.get(period.getText().toString().toLowerCase()).replace(" ", "_").toLowerCase();

        list = NetworkManager.getInstance().getEngagementGraph(
                scope,
                compareType,
                compareValue,
                filterType,
                filterValue,
                isCourse
        );

        setData();
        lineChart.invalidate();
        DataManager.getInstance().mainActivity.hideProgressBar();
    }

    private void setData() {

        if(list == null) {
            list = new ArrayList<>();
        }

        lineChart.setData(null);
        lineChart.getXAxis().setValueFormatter(null);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

        barchart.setData(null);
        barchart.getXAxis().setValueFormatter(null);
        barchart.notifyDataSetChanged();
        barchart.invalidate();

        if (compareTo.getText().toString().equals(getString(R.string.no_one))) {
            if (period.getText().toString().equals(getString(R.string.last_7_days))) {

                final ArrayList<String> xVals = new ArrayList<>();
                ArrayList<Entry> vals1 = new ArrayList<>();
                ArrayList<BarEntry> vals2 = new ArrayList<>();

                String name = getString(R.string.me);

                Date date = new Date();
                date.setTime(date.getTime() - 6*86400000);

                Collections.reverse(list);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
                for (int i = 0; i < list.size(); i++) {
                    String day = dateFormat.format(date);
                    date.setTime(date.getTime() + 86400000);
                    vals1.add(new Entry(xVals.size(), list.get(i).activity_points));
                    vals2.add(new BarEntry(xVals.size(), list.get(i).activity_points));
                    xVals.add(day);
                }

                IAxisValueFormatter valueFormatter = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if(xVals.size() > value && value >= 0)
                            return xVals.get((int)value);
                        else
                            return "";
                    }
                };

                lineChart.getXAxis().setValueFormatter(valueFormatter);
                lineChart.setData(getLineData(vals1, name));
                lineChart.invalidate();

                barchart.getXAxis().setValueFormatter(valueFormatter);
                barchart.setData(getBarData(vals2,name));
                barchart.invalidate();

            } else if (period.getText().toString().equals(getString(R.string.last_30_days))) {

                ArrayList<Entry> vals1 = new ArrayList<>();
                ArrayList<BarEntry> vals2 = new ArrayList<>();

                final ArrayList<String> xVals = new ArrayList<>();

                Integer val1 = 0;

                Collections.reverse(list);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, -27);

                String day;

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/d");
                SimpleDateFormat dateFormatD = new SimpleDateFormat("d");
                SimpleDateFormat dateFormatM = new SimpleDateFormat("MM");
                for (int i = 0; i < list.size(); i++) {
                    val1 = val1 + list.get(i).activity_points;
                    if (i == 6 || i == 13 || i == 20 || i == 27){
                        vals1.add(new Entry(xVals.size(), val1));
                        vals2.add(new BarEntry(xVals.size(), val1));

                        day = dateFormat.format(calendar.getTime());
                        String month1 = dateFormatM.format(calendar.getTime());

                        calendar.add(Calendar.DATE, 6);
                        String month2 = dateFormatM.format(calendar.getTime());

                        if(month1.equals(month2)) {
                            day += "-"+dateFormatD.format(calendar.getTime());
                        } else {
                            day += "-"+dateFormat.format(calendar.getTime());
                        }
                        calendar.add(Calendar.DATE, 1);
                        xVals.add(day);
                        val1 = 0;
                    }
                }

                String name = getString(R.string.me);

                IAxisValueFormatter valueFormatter1 = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if(xVals.size() > value && value >= 0)
                            return xVals.get((int)value);
                        else
                            return "";
                    }
                };

                lineChart.getXAxis().setValueFormatter(valueFormatter1);
                lineChart.setData(getLineData(vals1, name));
                lineChart.invalidate();

                barchart.getXAxis().setValueFormatter(valueFormatter1);
                barchart.setData(getBarData(vals2, name));
                barchart.invalidate();
            }
        } else {
            if (period.getText().toString().equals(getString(R.string.last_7_days))) {

                final ArrayList<String> xVals = new ArrayList<>();

                ArrayList<Integer> vals3 = new ArrayList<>();
                ArrayList<Integer> vals4 = new ArrayList<>();

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
                    if (list.get(i).student_id.contains(id)) {
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

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");

                for (int i = 0; i < vals3.size(); i++) {
                    day = dateFormat.format(date);
                    date.setTime(date.getTime() + 86400000);
                    vals1.add(new Entry(xVals.size(), vals3.get(i)));
                    vals2.add(new Entry(xVals.size(), vals4.get(i)));

                    vals5.add(new BarEntry(xVals.size(), vals3.get(i)));
                    vals6.add(new BarEntry(xVals.size(), vals4.get(i)));
                    xVals.add(day);
                }

                LineDataSet set1 = getLineDataSet(vals1, name);
                LineDataSet set2 = getLineDataSet(vals2, compareTo.getText().toString());

                LineData lineData = new LineData(set1);
                lineData.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                lineData.setDrawValues(false);
                lineData.addDataSet(set2);

                BarDataSet barDataSet1 = getBarDataSet(vals5,name);
                BarDataSet barDataSet2 = getBarDataSet(vals6,compareTo.getText().toString());

                BarData barData = new BarData(barDataSet1);
                barData.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                barData.setDrawValues(true);
                barData.setValueTextColor(0xff000000);
                barData.addDataSet(barDataSet2);
                barData.setBarWidth(0.40f);
                barData.groupBars(0, 0.09f, 0.01f);
                barData.setValueTextColor(0xff000000);

                IAxisValueFormatter valueFormatter = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if(xVals.size() > value && value >= 0)
                            return xVals.get((int)value);
                        else
                            return "";
                    }
                };

                lineChart.getXAxis().setValueFormatter(valueFormatter);
                lineChart.setData(lineData);
                lineChart.invalidate();

                barchart.getXAxis().setValueFormatter(valueFormatter);
                barchart.setData(barData);
                barchart.invalidate();

            } else if (period.getText().toString().equals(getString(R.string.last_30_days))) {

                final ArrayList<String> xVals = new ArrayList<>();

                ArrayList<Integer> vals3 = new ArrayList<>();
                ArrayList<Integer> vals4 = new ArrayList<>();

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
                    if (list.get(i).student_id.contains(id)) {
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

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                for (int i = 0; i < vals3.size(); i++) {
                    val1 = val1 + vals3.get(i);
                    val2 = val2 + vals4.get(i);
                    if (i == 6 || i == 13 || i == 20 || i == 27) {

                        label = dateFormat.format(date);
                        date.setTime(date.getTime() + 7*86400000);

                        vals1.add(new Entry(xVals.size(), val1));
                        vals2.add(new Entry(xVals.size(), val2));

                        vals5.add(new BarEntry(xVals.size(), val1));
                        vals6.add(new BarEntry(xVals.size(), val2));

                        xVals.add(label);

                        val1 = 0;
                        val2 = 0;
                    }
                }

                LineDataSet set1 = getLineDataSet(vals1, name);
                LineDataSet set2 = getLineDataSet(vals2, compareTo.getText().toString());

                LineData lineData = new LineData(set1);
                lineData.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                lineData.setDrawValues(false);
                lineData.addDataSet(set2);

                BarDataSet barDataSet1 = getBarDataSet(vals5,name);
                BarDataSet barDataSet2 = getBarDataSet(vals6,compareTo.getText().toString());

                BarData barData = new BarData(barDataSet1);
                barData.setValueTypeface(DataManager.getInstance().myriadpro_regular);
                barData.setDrawValues(true);
                barData.addDataSet(barDataSet2);
                barData.setBarWidth(0.40f);
                barData.groupBars(0, 0.09f, 0.01f);
                barData.setValueTextColor(0xff000000);

                IAxisValueFormatter valueFormatter1 = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if(xVals.size() > value && value >= 0)

                            return xVals.get((int)value);
                        else
                            return "";
                    }
                };

                lineChart.getXAxis().setValueFormatter(valueFormatter1);
                lineChart.setData(lineData);
                lineChart.invalidate();

                barchart.getXAxis().setValueFormatter(valueFormatter1);
                barchart.setData(barData);
                barchart.invalidate();
            }
        }
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
        barData.setDrawValues(true);
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
        barDataSet.setDrawValues(true);
        barDataSet.setValueTextSize(50);
        barDataSet.setValueTextSize(14f);
        return barDataSet;
    }
}
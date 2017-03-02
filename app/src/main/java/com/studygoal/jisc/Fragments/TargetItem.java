package com.studygoal.jisc.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.studygoal.jisc.Adapters.GenericAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.LinguisticManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.SocialManager;
import com.studygoal.jisc.Models.ActivityHistory;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.Models.StretchTarget;
import com.studygoal.jisc.Models.Targets;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.Utils;
//import com.studygoal.jisc.Utils.YFormatter;
//import com.studygoal.jisc.Utils.YFormatterPercent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TargetItem extends Fragment {

    View mainView;
    public Targets target;
    public TargetDetails reference;
    public int position;
    PieChart mChart;
    LineChart lineChart;
    int neccesary_time;
    int spent_time;
    boolean piChart;
    TextView incomplete_textView;
    List<ActivityHistory> activityHistoryList;
    StretchTarget stretch_target;
    View.OnClickListener set_stretch_target;


    public TargetItem() {
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_spinner_layout);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(DataManager.getInstance().mainActivity.isLandscape) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = (int) (displaymetrics.widthPixels * 0.3);

            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = width;
            dialog.getWindow().setAttributes(params);
        }

        ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.add);

        ArrayList<String> items = new ArrayList<>();
//                items.add("Last 24 hours");
        items.add(getString(R.string.report_activity));
        items.add(getString(R.string.log_recent_activity));
        final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
        listView.setAdapter(new GenericAdapter(DataManager.getInstance().mainActivity, "", items));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    DataManager.getInstance().fromTargetItem = true;
                    DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment, new LogNewActivity(), "newActivity")
                            .addToBackStack(null)
                            .commit();
                } else {
                    DataManager.getInstance().fromTargetItem = true;
                    LogLogActivity fragment = new LogLogActivity();
                    fragment.isInEditMode = false;
                    DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment, fragment)
                            .addToBackStack(null)
                            .commit();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.target_title));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(4);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.target_item, container, false);

        mainView.findViewById(R.id.main_all_content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        Module module = new Select().from(Module.class).where("module_id = ?", target.module_id).executeSingle();

        TextView textView = (TextView) mainView.findViewById(R.id.target_item_text);
        textView.setTypeface(DataManager.getInstance().myriadpro_regular);

        piChart = true;
        Calendar c = Calendar.getInstance();

        stretch_target = new Select().from(StretchTarget.class).where("target_id = ?", target.target_id).executeSingle();

        try {
            Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.getInstance().images.get(target.activity)).into((ImageView) mainView.findViewById(R.id.activity_icon));
        } catch (Exception ignored) {}
        if(module != null) {
            activityHistoryList = new Select().from(ActivityHistory.class).where("module_id = ?", target.module_id).and("activity = ?", target.activity).execute();
        } else {
            activityHistoryList = new Select().from(ActivityHistory.class).where("activity = ?", target.activity).execute();
        }

        set_stretch_target = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.stretchtarget_layout);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if(DataManager.getInstance().mainActivity.isLandscape) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = (int) (displaymetrics.widthPixels * 0.3);

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = width;
                    dialog.getWindow().setAttributes(params);
                }

                final NumberPicker hourPicker = (NumberPicker)dialog.findViewById(R.id.hour_picker);
                hourPicker.setMinValue(0);
                hourPicker.setMaxValue(10);
                hourPicker.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        if (value < 10)
                            return "0" + value;
                        else
                            return value + "";
                    }
                });
                final NumberPicker minutePicker = (NumberPicker)dialog.findViewById(R.id.minute_picker);
                minutePicker.setMinValue(0);
                minutePicker.setMaxValue(59);
                minutePicker.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        if (value < 10)
                            return "0" + value;
                        else
                            return value + "";
                    }
                });

                ((TextView)dialog.findViewById(R.id.timespent_save_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                dialog.findViewById(R.id.set_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String time = hourPicker.getValue() * 60 + minutePicker.getValue() + "";
                        final HashMap<String, String> map = new HashMap<>();
                        map.put("target_id", target.target_id);
                        map.put("stretch_time", time);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DataManager.getInstance().mainActivity.showProgressBar("");
                                    }
                                });
                                if(NetworkManager.getInstance().addStretchTarget(map)) {
                                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DataManager.getInstance().mainActivity.hideProgressBar();
                                            Snackbar.make(mainView.findViewById(R.id.container), R.string.successfully_set_stretch_target, Snackbar.LENGTH_LONG).show();
                                            mainView.findViewById(R.id.target_set_stretch_btn).setVisibility(View.GONE);
//                                            ((TextView)mainView.findViewById(R.id.target_stretch_btn_text)).setText("Edit Stretch Target");
                                            NetworkManager.getInstance().getStretchTargets(DataManager.getInstance().user.id);
//                                            mainView.findViewById(R.id.target_set_stretch_btn).setOnClickListener(edit_stretch_target);
                                            PieChart mChart2 = (PieChart) mainView.findViewById(R.id.piechart2);
                                            mChart2.setDescription(new Description());
                                            mChart2.setTouchEnabled(false);

                                            // radius of the center hole in percent of maximum radius
                                            mChart2.setHoleRadius(70f);
                                            mChart2.setTransparentCircleRadius(85f);
                                            mChart2.setDrawSliceText(false);

                                            mChart2.setUsePercentValues(true);
                                            Legend l2 = mChart2.getLegend();
                                            l2.setEnabled(false);

                                            mChart2.setData(generatePieData2());
                                            mChart2.invalidate();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                                else {
                                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DataManager.getInstance().mainActivity.hideProgressBar();
                                            Snackbar.make(mainView.findViewById(R.id.container), R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                });
                ((TextView)dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);

                dialog.show();
            }
        };

        String current_date = c.get(Calendar.YEAR) +  "-";
        current_date += (c.get(Calendar.MONTH)+1)<10 ? "0" + (c.get(Calendar.MONTH)+1) + "-" : (c.get(Calendar.MONTH)+1) + "-";
        current_date += c.get(Calendar.DAY_OF_MONTH)<10 ? "0" + c.get(Calendar.DAY_OF_MONTH) + " " : c.get(Calendar.DAY_OF_MONTH) + " ";
        current_date += c.get(Calendar.HOUR_OF_DAY)<10 ? "0" + c.get(Calendar.HOUR_OF_DAY) + ":" : c.get(Calendar.HOUR_OF_DAY) + ":";
        current_date += c.get(Calendar.MINUTE)<10? "0" + c.get(Calendar.MINUTE) + ":" : c.get(Calendar.MINUTE) + ":";
        current_date += c.get(Calendar.SECOND)<10? "0" + c.get(Calendar.SECOND) : c.get(Calendar.SECOND);


        switch (target.time_span.toLowerCase()) {
            case "daily": {
                String time = current_date.split(" ")[0];
                List<ActivityHistory> tmp = new ArrayList<>();
                for(int i = 0; i < activityHistoryList.size(); i++) {
                    if(time.equals(activityHistoryList.get(i).created_date.split(" ")[0]))
                        tmp.add(activityHistoryList.get(i));
                }
                activityHistoryList.clear();
                activityHistoryList.addAll(tmp);
                break;
            }
            case "weekly": {
                List<ActivityHistory> tmp = new ArrayList<>();
                for(int i = 0; i < activityHistoryList.size(); i++) {
                    if(Utils.isInSameWeek(activityHistoryList.get(i).created_date.split(" ")[0]))
                        tmp.add(activityHistoryList.get(i));
                }
                activityHistoryList.clear();
                activityHistoryList.addAll(tmp);
                break;
            }
            case "monthly": {
                String time = current_date.split(" ")[0].split("-")[0] + "-" + current_date.split(" ")[0].split("-")[1];
                List<ActivityHistory> tmp = new ArrayList<>();
                for(int i = 0; i < activityHistoryList.size(); i++) {
                    if(time.equals(activityHistoryList.get(i).created_date.split(" ")[0].split("-")[0] + "-" + activityHistoryList.get(i).created_date.split(" ")[0].split("-")[1]))
                        tmp.add(activityHistoryList.get(i));
                }
                activityHistoryList.clear();
                activityHistoryList.addAll(tmp);
                break;
            }
        }


        neccesary_time = Integer.parseInt(target.total_time);

        spent_time = 0;
        for(int i=0; i < activityHistoryList.size(); i++) {
            spent_time += Integer.parseInt(activityHistoryList.get(i).time_spent);
        }
        if(spent_time == 0)
            mainView.findViewById(R.id.colorbar).setBackgroundColor(0xFFFF0000);
        else if(spent_time >= neccesary_time)
            mainView.findViewById(R.id.colorbar).setBackgroundColor(0xFF00FF00);
        else
            mainView.findViewById(R.id.colorbar).setBackgroundColor(0xFFff7400);

        if(spent_time == 0 || spent_time < neccesary_time) {
            incomplete_textView = (TextView) mainView.findViewById(R.id.target_item_incomplete_textview);
            incomplete_textView.setVisibility(View.VISIBLE);
            incomplete_textView.setTypeface(DataManager.getInstance().myriadpro_regular);
            incomplete_textView.setText(Utils.convertToHour(spent_time) + "/" + Utils.convertToHour(neccesary_time));

            if(spent_time == 0) {
                View set_stretch = mainView.findViewById(R.id.target_set_stretch_btn);
                set_stretch.setVisibility(View.VISIBLE);
                ((TextView)mainView.findViewById(R.id.target_stretch_btn_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.start_your_new_activity));
                set_stretch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        LogNewActivity fragment = new LogNewActivity();
//                        DataManager.getInstance().fromTargetItem = true;
//                        DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.main_fragment, fragment)
//                                .addToBackStack(null)
//                                .commit();
                        showDialog();
                    }
                });
            }

//            if(stretch_target != null) {
//                //TODO: Edit Stretch target
//                View set_stretch = mainView.findViewById(R.id.target_set_stretch_btn);
//                set_stretch.setVisibility(View.VISIBLE);
//                ((TextView)mainView.findViewById(R.id.target_stretch_btn_text)).setText("Edit Stretch Target");
//                set_stretch.setOnClickListener(edit_stretch_target);
//
//            }
//            else {
//                //SET STRETCH TARGET
//                View set_stretch = mainView.findViewById(R.id.target_set_stretch_btn);
//                set_stretch.setVisibility(View.VISIBLE);
//                ((TextView)mainView.findViewById(R.id.target_stretch_btn_text)).setText("Set Stretch Target");
//                ((TextView)mainView.findViewById(R.id.target_stretch_btn_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
//                set_stretch.setOnClickListener(set_stretch_target);
//            }
        }
        else {
            TextView complete_textView = (TextView) mainView.findViewById(R.id.target_item_complete_textview);
            complete_textView.setVisibility(View.VISIBLE);
            complete_textView.setTypeface(DataManager.getInstance().myriadpro_regular);
            complete_textView.setText(Utils.convertToHour(neccesary_time) + "/" + Utils.convertToHour(neccesary_time));

            if(stretch_target != null) {
                PieChart mChart2 = (PieChart) mainView.findViewById(R.id.piechart2);
                mChart2.setDescription(new Description());
                mChart2.setTouchEnabled(false);

                // radius of the center hole in percent of maximum radius
                mChart2.setHoleRadius(70f);
                mChart2.setTransparentCircleRadius(85f);
                mChart2.setDrawSliceText(false);

                mChart2.setUsePercentValues(true);
                Legend l2 = mChart2.getLegend();
                l2.setEnabled(false);

                mChart2.setData(generatePieData2());
                if (spent_time < neccesary_time + Integer.parseInt(stretch_target.stretch_time)) {
                    int time = neccesary_time + Integer.parseInt(stretch_target.stretch_time) - spent_time;
                    TextView textView1 = (TextView) mainView.findViewById(R.id.target_reached_stretch_present);
                    String text = LinguisticManager.getInstance().present.get(target.activity);
                    if (text.contains(DataManager.getInstance().mainActivity.getString(R.string._for))) {
                        text += " " + DataManager.getInstance().mainActivity.getString(R.string.another) + " ";
                    } else {
                        text += " " + DataManager.getInstance().mainActivity.getString(R.string.for_text) + " " + DataManager.getInstance().mainActivity.getString(R.string.another) + " ";
                    }
                    int hour = time / 60;
                    int minute = time % 60;
                    text += (hour == 1) ? "1" + " " + DataManager.getInstance().mainActivity.getString(R.string.hour) + " " : hour + " " + DataManager.getInstance().mainActivity.getString(R.string.hours) + " ";
                    if (minute > 0)
                        text += ((minute == 1) ? DataManager.getInstance().mainActivity.getString(R.string.and) + " 1" + " " + DataManager.getInstance().mainActivity.getString(R.string.minute) + " " : DataManager.getInstance().mainActivity.getString(R.string.and) + " " + minute + " " + DataManager.getInstance().mainActivity.getString(R.string.minutes) + " ");
                    text += " " + DataManager.getInstance().mainActivity.getString(R.string.this_text) + " " + target.time_span.substring(0, target.time_span.length() - 2).toLowerCase() + " " + DataManager.getInstance().mainActivity.getString(R.string.to_meet_stretch_target);
                    textView1.setText(text);
                    textView1.setVisibility(View.VISIBLE);
                }
            } else {
                if(canStretchTarget()) {
                    View set_stretch = mainView.findViewById(R.id.target_set_stretch_btn);
                    set_stretch.setVisibility(View.VISIBLE);
                    ((TextView) mainView.findViewById(R.id.target_stretch_btn_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.set_stretch_target));
                    ((TextView) mainView.findViewById(R.id.target_stretch_btn_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                    set_stretch.setOnClickListener(set_stretch_target);
                }
            }

            mainView.findViewById(R.id.target_item_complete_imageview).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.target_reached_layout).setVisibility(View.VISIBLE);
        }

        String text = "";
        text += LinguisticManager.getInstance().present.get(target.activity) + " ";
        int hour = Integer.parseInt(target.total_time) / 60;
        int minute = Integer.parseInt(target.total_time) % 60;
        text += (hour == 1) ? "1 " + DataManager.getInstance().mainActivity.getString(R.string.hour) + " " : hour + " " + DataManager.getInstance().mainActivity.getString(R.string.hours) + " ";
        if(minute > 0)
            text += ((minute == 1) ? " " + DataManager.getInstance().mainActivity.getString(R.string.and) + " 1 " + DataManager.getInstance().mainActivity.getString(R.string.minute) + " " : " " + DataManager.getInstance().mainActivity.getString(R.string.and) + " " + minute + " " + DataManager.getInstance().mainActivity.getString(R.string.minutes) + " ");
        text += target.time_span.toLowerCase();
        text += module == null ? "" : " " + DataManager.getInstance().mainActivity.getString(R.string.for_text) + " " + module.name;
        textView.setText(text);

        final String finalText = text;
        mainView.findViewById(R.id.target_reached_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SocialManager.getInstance().shareOnFacebook(getActivity().getString(R.string.target_reached_2) + " " + finalText);

//                mainView.findViewById(R.id.target_reached_layout).setVisibility(View.GONE);
//                mainView.findViewById(R.id.share_layout).setVisibility(View.VISIBLE);
//                mainView.findViewById(R.id.facebook_btn).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.facebook_not_implemented, Snackbar.LENGTH_LONG).show();
//                    }
//                });
//                mainView.findViewById(R.id.twitter_btn).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                            Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), "Not available in this build", Snackbar.LENGTH_LONG).show();
//                        SocialManager.getInstance().shareOnTwitter(getActivity().getString(R.string.target_reached_2) + " " + finalText);
//                    }
//                });
//                mainView.findViewById(R.id.mail_btn).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                            Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), "Not available in this build", Snackbar.LENGTH_LONG).show();
//                        SocialManager.getInstance().shareOnEmail(getActivity().getString(R.string.target_reached_2) + " " + finalText);
//                    }
//                });

            }
        });

        final com.daimajia.swipe.SwipeLayout swipeLayout = (com.daimajia.swipe.SwipeLayout) mainView.findViewById(R.id.swipelayout);

        mainView.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.close(true);
                AddTarget fragment = new AddTarget();
                fragment.isInEditMode = true;
                fragment.item = target;
                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        mainView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.confirmation_dialog);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if(DataManager.getInstance().mainActivity.isLandscape) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = (int) (displaymetrics.widthPixels * 0.45);

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = width;
                    dialog.getWindow().setAttributes(params);
                }

                ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.confirmation);

                ((TextView) dialog.findViewById(R.id.dialog_message)).setTypeface(DataManager.getInstance().myriadpro_regular);
                ((TextView) dialog.findViewById(R.id.dialog_message)).setText(R.string.confirm_delete_message);

                ((TextView) dialog.findViewById(R.id.dialog_no_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                ((TextView) dialog.findViewById(R.id.dialog_no_text)).setText(R.string.no);

                ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setText(R.string.yes);

                dialog.findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        swipeLayout.close(true);
                        reference.deleteTarget(target, position);
                    }
                });
                dialog.findViewById(R.id.dialog_no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        //PIE Chart Data

        mChart = (PieChart) mainView.findViewById(R.id.piechart);
        mChart.setDescription(new Description());
        mChart.setTouchEnabled(false);

        // radius of the center hole in percent of maximum radius
        mChart.setHoleRadius(70f);
        mChart.setTransparentCircleRadius(65f);
        mChart.setDrawSliceText(false);

        mChart.setUsePercentValues(true);
        Legend l = mChart.getLegend();
        l.setEnabled(false);

        mChart.setData(generatePieData());

        // ---

        // LINE Chart Data

        lineChart = (LineChart) mainView.findViewById(R.id.chart1);
//        lineChart.setViewPortOffsets(0, 20, 0, 0);
//        lineChart.setBackgroundColor(Color.rgb(104, 241, 175));

        // no description text
        lineChart.setDescription(new Description());

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
        if(target.time_span.toLowerCase().equals(getString(R.string.daily).toLowerCase())) {
//            y.setValueFormatter(new YFormatterPercent());
            y.setAxisMaxValue(100);
        } else if(target.time_span.toLowerCase().equals(getString(R.string.Weekly).toLowerCase())) {
//            y.setValueFormatter(new YFormatter(2));
        } else if(target.time_span.toLowerCase().equals(getString(R.string.monthly).toLowerCase())) {
//            y.setValueFormatter(new YFormatter(2));
        }
        y.setAxisMinValue(0);
        y.setStartAtZero(true);
        y.setTextColor(Color.BLACK);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(true);
        y.setAxisLineColor(Color.BLACK);

        XAxis x = lineChart.getXAxis();
        x.setTypeface(DataManager.getInstance().myriadpro_regular);
        x.setTextColor(Color.BLACK);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setAxisLineColor(Color.BLACK);

        lineChart.getAxisRight().setEnabled(false);

        // add data
        setData();

        lineChart.getLegend().setEnabled(false);

//        lineChart.animateXY(2000, 2000);

        // dont forget to refresh the drawing
        lineChart.invalidate();

        //-----

//        final ImageView toggle = (ImageView) mainView.findViewById(R.id.graph_toggle);
//        toggle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(piChart) {
//                    mainView.findViewById(R.id.linechart_rl).setVisibility(View.VISIBLE);
//                    mainView.findViewById(R.id.piechart).setVisibility(View.INVISIBLE);
//                    mainView.findViewById(R.id.piechart_aux).setVisibility(View.INVISIBLE);
//
//                    piChart = false;
//                    Picasso.with(DataManager.getInstance().mainActivity).load(R.drawable.graphicon_2).into(toggle);
//                }
//                else {
//                    mainView.findViewById(R.id.linechart_rl).setVisibility(View.INVISIBLE);
//                    mainView.findViewById(R.id.piechart).setVisibility(View.VISIBLE);
//                    mainView.findViewById(R.id.piechart_aux).setVisibility(View.VISIBLE);
//
//                    piChart = true;
//                    Picasso.with(DataManager.getInstance().mainActivity).load(R.drawable.graphicon_1).into(toggle);
//                }
//            }
//        });
        return mainView;
    }

    private boolean canStretchTarget() {
        boolean eligible = false;
        if(target.time_span.toLowerCase().equals(getString(R.string.daily).toLowerCase())) {

        } else if(target.time_span.toLowerCase().equals(getString(R.string.Weekly).toLowerCase())) {
            Calendar c = Calendar.getInstance();
            if(c.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY)
                eligible = true;
        } else if(target.time_span.toLowerCase().equals(getString(R.string.monthly).toLowerCase())) {
            Calendar c = Calendar.getInstance();
            if(c.getActualMaximum(Calendar.DAY_OF_MONTH) - c.get(Calendar.DAY_OF_MONTH) > 4)
                eligible = true;
        }

        return eligible;
    }


    private void setData() {
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> vals1 = new ArrayList<>();

        switch (target.time_span.toLowerCase()) {
            case "daily": {

                HashMap<String, Integer> time_spent = new HashMap<>();
                xVals.add("00:00");
                time_spent.put("00:00", 0);
                xVals.add("09:00");
                time_spent.put("09:00", 0);
                xVals.add("12:00");
                time_spent.put("12:00", 0);
                xVals.add("15:00");
                time_spent.put("15:00", 0);
                xVals.add("18:00");
                time_spent.put("18:00", 0);
                xVals.add("21:00");
                time_spent.put("21:00", 0);
                xVals.add("24:00");
                time_spent.put("24:00", 0);

                for(int i = 0; i < activityHistoryList.size(); i++) {
                    int starttime = Integer.parseInt(activityHistoryList.get(i).created_date.split(" ")[1].split(":")[0]) * 60 + Integer.parseInt(activityHistoryList.get(i).created_date.split(" ")[1].split(":")[1]);
                    int endtime = starttime + Integer.parseInt(activityHistoryList.get(i).time_spent);
                    int total_time = 0;
                    for(int j = 0; j < xVals.size(); j++) {
                        int minutes = Integer.parseInt(xVals.get(j).split(":")[0]) * 60;
                        if(starttime < minutes && minutes < endtime) {
                            int tmp = time_spent.get(xVals.get(j));
                            time_spent.remove(xVals.get(j));
                            time_spent.put(xVals.get(j), tmp + minutes-starttime);
                            total_time = tmp + minutes-starttime;
                        }
                        else if(minutes > endtime) {
                            if(total_time == 0)
                                total_time = Integer.parseInt(activityHistoryList.get(i).time_spent);
                            time_spent.remove(xVals.get(j));
                            time_spent.put(xVals.get(j), total_time);
                        }
                    }
                }
                for (int i = 0; i < 7; i++) {
                    float tmp = ((float)time_spent.get(xVals.get(i)) / Integer.parseInt(target.total_time)) * 100;
                    if(tmp > 100) tmp = 100;
                    vals1.add(new Entry(tmp, i));
                }
                break;
            }
            case "weekly": {
                xVals.add("Sun");
                xVals.add("Mon");
                xVals.add("Tue");
                xVals.add("Wed");
                xVals.add("Thu");
                xVals.add("Fri");
                xVals.add("Sat");

                HashMap<Integer, Float> values = new HashMap<>();

                for(int i = 0; i < 7; i++) {
                    values.put(i, 0f);
                }

                Calendar c = Calendar.getInstance();
                for(int i = 0; i < activityHistoryList.size(); i ++) {
                    String date = activityHistoryList.get(i).activity_date;
                    c.set(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1])-1, Integer.parseInt(date.split("-")[2]));
                    float tmp = values.get(c.get(Calendar.DAY_OF_WEEK)-1);
                    values.remove(c.get(Calendar.DAY_OF_WEEK) - 1);
                    values.put(c.get(Calendar.DAY_OF_WEEK) - 1, tmp + Integer.parseInt(activityHistoryList.get(i).time_spent) / 60 + (float) (Integer.parseInt(activityHistoryList.get(i).time_spent) % 60 / 60));
                }
                for (int i = 0; i < 7; i++) {
                        vals1.add(new Entry(values.get(i), i));
                }
                break;
            }
            case "monthly": {
                //TODO: Posibila problema cand nr de zile dintr-o luna sa fie mare
                Calendar c = Calendar.getInstance();
                HashMap<Integer, Float> values = new HashMap<>();
                for(int i = 1; i <= c.get(Calendar.DAY_OF_MONTH); i++) {
                    values.put(i, 0f);
                    xVals.add(i + "");
                    for(int j = 0; j < activityHistoryList.size(); j ++) {
                        if(Integer.parseInt(activityHistoryList.get(j).activity_date.split("-")[2]) == i) {
                            float tmp = values.get(i);
                            values.remove(i);
                            values.put(i, tmp + Integer.parseInt(activityHistoryList.get(j).time_spent) / 60 + (float) (Integer.parseInt(activityHistoryList.get(j).time_spent) % 60 / 60));
                        }
                    }
                    vals1.add(new Entry(values.get(i), i-1));
                }
                break;
            }
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(true);
        set1.setDrawCircles(false);
        set1.setLineWidth(1.8f);
        set1.setCircleSize(2f);
        set1.setCircleColor(Color.WHITE);
        set1.setColor(0xFF8864C8);
        set1.setFillColor(0xFF8864C8);
        set1.setFillAlpha(255);
        set1.setDrawHorizontalHighlightIndicator(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTypeface(DataManager.getInstance().myriadpro_regular);
        data.setValueTextSize(12f);
        data.setDrawValues(false);

        // set data
        lineChart.setData(data);
    }


    protected PieData generatePieData() {

        ArrayList<PieEntry> entries1 = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        xVals.add("");
        entries1.add(new PieEntry(spent_time, 0));


        if(neccesary_time-spent_time > 0) {
            xVals.add("");
            entries1.add(new PieEntry(neccesary_time - spent_time, 1));
        }

        PieDataSet ds1 = new PieDataSet(entries1, "");
        final int[] color = {
                Color.rgb(173, 88, 203), Color.rgb(224, 223, 224)
        };
        ds1.setColors(color);
        ds1.setSliceSpace(0);
        ds1.setValueTextColor(Color.rgb(136, 100, 200));
        ds1.setValueTextSize(12f);
        ds1.setDrawValues(false);

        PieData d = new PieData(ds1);
        d.setValueTypeface(DataManager.getInstance().myriadpro_regular);

        return d;
    }

    protected PieData generatePieData2() {

        ArrayList<PieEntry> entries1 = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        xVals.add("");
        entries1.add(new PieEntry(spent_time - neccesary_time, 0));

        if(stretch_target == null) stretch_target = new Select().from(StretchTarget.class).where("target_id = ?", target.target_id).executeSingle();
        if(neccesary_time + Integer.parseInt(stretch_target.stretch_time) - spent_time > 0) {
            xVals.add("");
            entries1.add(new PieEntry(neccesary_time + Integer.parseInt(stretch_target.stretch_time) - spent_time, 1));
        }

        PieDataSet ds1 = new PieDataSet(entries1, "");
        final int[] color = {
                Color.rgb(200, 129, 225), Color.rgb(224, 223, 224)
        };
        ds1.setColors(color);
        ds1.setSliceSpace(0);
        ds1.setValueTextColor(Color.rgb(180, 100, 200));
        ds1.setValueTextSize(12f);
        ds1.setDrawValues(false);

        PieData d = new PieData(ds1);
        d.setValueTypeface(DataManager.getInstance().myriadpro_regular);

        return d;
    }

}

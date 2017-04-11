package com.studygoal.jisc.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TargetItem extends Fragment {

    View mainView;
    public Targets target;
    public TargetDetails reference;
    public int position;
    int neccesary_time;
    int spent_time;
    boolean piChart;
    TextView incomplete_textView;
    List<ActivityHistory> activityHistoryList;
    StretchTarget stretch_target;
    View.OnClickListener set_stretch_target;
    WebView webView;

    float webviewHeight;
    float webviewWidth;

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

        webView = (WebView)mainView.findViewById(R.id.piechart);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setPadding(0, 0, 0, 0);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/20 Safari/537.31");
        webView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                webviewHeight = Utils.pxToDp(webView.getHeight()-40);
                webviewWidth = Utils.pxToDp(webView.getWidth()-40);
            }
        });
        webView.loadDataWithBaseURL("", "<html><head></head><body><div style=\"height:100%;width:100%;background:white;\"></div></body></html>", "text/html", "UTF-8", "");

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
                                            NetworkManager.getInstance().getStretchTargets(DataManager.getInstance().user.id);

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    loadDataStretch();
                                                }
                                            }, 100);
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
                        showDialog();
                    }
                });
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            }, 100);

        } else {
            TextView complete_textView = (TextView) mainView.findViewById(R.id.target_item_complete_textview);
            complete_textView.setVisibility(View.VISIBLE);
            complete_textView.setTypeface(DataManager.getInstance().myriadpro_regular);
            complete_textView.setText(Utils.convertToHour(neccesary_time) + "/" + Utils.convertToHour(neccesary_time));

            if(stretch_target != null) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadDataStretch();
                    }
                }, 100);


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

    protected void loadData() {
        String html = getHighChartsHTML(false);
        html = html.replace("Y_MAX_VALUE",""+neccesary_time);
        html = html.replace("Y_VALUE",""+spent_time);
        html = html.replace("height:1000px","height:"+webviewHeight+"px !important");
        html = html.replace("width:1000px","width:"+webviewWidth+"px !important");

        webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
    }

    protected void loadDataStretch() {
        String html = getHighChartsHTML(true);
        html = html.replace("Y_VALUE",""+(spent_time - neccesary_time));
        html = html.replace("Y_MAX_VALUE",""+stretch_target.stretch_time);
        html = html.replace("height:1000px","height:"+webviewHeight+"px !important");
        html = html.replace("width:1000px","width:"+webviewWidth+"px !important");

        webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
    }

    public String getHighChartsHTML(boolean isStretch) {

        try {
            String path = "highcharts/piegraph.html";
            if(isStretch) {
                path = "highcharts/piestretchgraph.html";
            }

            StringBuilder buf = new StringBuilder();
            InputStream json = DataManager.getInstance().mainActivity.getAssets().open(path);
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
}

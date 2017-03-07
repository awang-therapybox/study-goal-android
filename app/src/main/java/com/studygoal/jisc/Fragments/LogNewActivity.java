package com.studygoal.jisc.Fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.ActivityTypeAdapter;
import com.studygoal.jisc.Adapters.ChooseActivityAdapter;
import com.studygoal.jisc.Adapters.ModuleAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.NotificationAlarm;
import com.studygoal.jisc.Models.Activity;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.Models.RunningActivity;
import com.studygoal.jisc.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LogNewActivity extends Fragment implements View.OnClickListener {

    View mainView;
    AppCompatTextView chooseActivity;
    AppCompatTextView module;
    AppCompatTextView activityType;

    EditText reminder_textView;
    TextView countdown_textView;

    AlarmManager am;
    PendingIntent pendingIntent;
    Timer timer;
    TimerTask timertask;
    Long timestamp;
    Long _pause;

    SharedPreferences saves;

    RelativeLayout addModuleLayout;

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.report_activity_title));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(8);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.log_fragment_new_activity, container, false);

        DataManager.getInstance().reload();

        addModuleLayout = (RelativeLayout)mainView.findViewById(R.id.add_new_module_layout);
        addModuleLayout.setVisibility(View.GONE);
        ((EditText)mainView.findViewById(R.id.add_module_edit_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView)mainView.findViewById(R.id.add_module_button_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        mainView.findViewById(R.id.add_module_button_text).setOnClickListener(this);

        countdown_textView = ((TextView) mainView.findViewById(R.id.new_activity_text_timer_2));
        countdown_textView.setTypeface(DataManager.getInstance().myriadpro_regular);

        ((TextView)mainView.findViewById(R.id.new_activity_text_minutes)).setTypeface(DataManager.getInstance().myriadpro_regular);

        am = (AlarmManager) DataManager.getInstance().mainActivity.getSystemService(Context.ALARM_SERVICE);

        module = (AppCompatTextView) mainView.findViewById(R.id.new_activity_module_textView);
        module.setTypeface(DataManager.getInstance().myriadpro_regular);
        module.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        module.setOnClickListener(this);

        activityType = (AppCompatTextView) mainView.findViewById(R.id.new_activity_activitytype_textView);
        activityType.setTypeface(DataManager.getInstance().myriadpro_regular);
        activityType.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        activityType.setOnClickListener(this);

        chooseActivity = (AppCompatTextView) mainView.findViewById(R.id.new_activity_choose_textView);
        chooseActivity.setTypeface(DataManager.getInstance().myriadpro_regular);
        chooseActivity.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        chooseActivity.setOnClickListener(this);

        reminder_textView = ((EditText) mainView.findViewById(R.id.new_activity_text_timer_1));
        reminder_textView.setTypeface(DataManager.getInstance().myriadpro_regular);
        reminder_textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() != 0) {
                    int value = Integer.parseInt(s.toString());
                    if (value < 0 || value > 60) {
                        reminder_textView.setText("");
                        reminder_textView.setSelection(reminder_textView.getText().length());
                    }
                }
            }
        });

        countdown_textView = ((TextView) mainView.findViewById(R.id.new_activity_text_timer_2));
        countdown_textView.setTypeface(DataManager.getInstance().myriadpro_regular);

        ((TextView)mainView.findViewById(R.id.new_activity_text_module)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView)mainView.findViewById(R.id.new_activity_text_choose)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView)mainView.findViewById(R.id.new_activity_activity_type_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView)mainView.findViewById(R.id.new_activity_btn_pause_text)).setTypeface(DataManager.getInstance().myriadpro_regular);

        ((TextView)mainView.findViewById(R.id.new_activity_btn_start_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView)mainView.findViewById(R.id.new_activity_btn_stop_text)).setTypeface(DataManager.getInstance().myriadpro_regular);

        if(!DataManager.getInstance().mainActivity.isLandscape) {
            ((TextView) mainView.findViewById(R.id.new_activity_text_reminder)).setTypeface(DataManager.getInstance().myriadpro_regular);
        } else {
            ((TextView) mainView.findViewById(R.id.header_1)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.header_2)).setTypeface(DataManager.getInstance().myriadpro_regular);
        }


        timer = new Timer();
        timertask = new TimerTask() {
            @Override
            public void run() {
                Long elapsed_time = System.currentTimeMillis() - timestamp;
                Long seconds = (elapsed_time / 1000) % 60;
                Long minutes = elapsed_time / 60000;

                String value = "";
                if(minutes < 10)
                    value += "0" + minutes + ":";
                else
                    value += minutes + ":";
                if(seconds < 10)
                    value += "0" + seconds;
                else
                    value += seconds;
                final String f_value = value;
                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        countdown_textView.setText(f_value);
                    }
                });

                if(minutes >= 180) {
                    mainView.findViewById(R.id.new_activity_btn_stop).callOnClick();
                }
            }
        };


        saves = DataManager.getInstance().mainActivity.getSharedPreferences("jisc", Context.MODE_PRIVATE);
        timestamp = saves.getLong("timer", 0);


        RunningActivity activity = new Select().from(RunningActivity.class).executeSingle();
        if(activity != null) {
            module.setText(((Module) new Select().from(Module.class).where("module_id = ?", activity.module_id).executeSingle()).name);
            activityType.setText(activity.activity_type);
            chooseActivity.setText(activity.activity);
        } else {
            List<Module> list_module = new Select().from(Module.class).execute();
            if(list_module.size() > 0)
                module.setText((list_module.get(0)).name);
            else
                module.setText(DataManager.getInstance().mainActivity.getString(R.string.no_module));
            activityType.setText(DataManager.getInstance().activity_type.get(0));
            chooseActivity.setText(DataManager.getInstance().choose_activity.get(DataManager.getInstance().activity_type.get(0)).get(0));
        }

        mainView.findViewById(R.id.new_activity_btn_start).setOnClickListener(this);
        mainView.findViewById(R.id.new_activity_btn_pause).setOnClickListener(this);

        if(saves.contains("pause")) {
            _pause = saves.getLong("pause", 0);
            if(timestamp == 0) {
                _pause = (long)0;
                saves.edit().putLong("pause", 0).apply();
            }
            if(_pause > 0) {
                Long elapsed_time = _pause - timestamp;
                Long seconds = (elapsed_time / 1000) % 60;
                Long minutes = elapsed_time / 60000;

                String value = "";
                if(minutes < 10)
                    value += "0" + minutes + ":";
                else
                    value += minutes + ":";
                if(seconds < 10)
                    value += "0" + seconds;
                else
                    value += seconds;
                final String f_value = value;
                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        countdown_textView.setText(f_value);
                    }
                });

                //mainView.findViewById(R.id.new_activity_btn_pause).callOnClick();
                mainView.findViewById(R.id.new_activity_btn_pause).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.new_activity_btn_start).setVisibility(View.GONE);
                mainView.findViewById(R.id.new_activity_btn_stop).setOnClickListener(this);
                ((TextView)mainView.findViewById(R.id.new_activity_btn_pause_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.resume));
            } else {
                mainView.findViewById(R.id.new_activity_btn_stop).setOnClickListener(this);
                mainView.findViewById(R.id.new_activity_btn_start).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.new_activity_btn_pause).setVisibility(View.GONE);
                if(timestamp > 0) {
                    mainView.findViewById(R.id.new_activity_btn_start).setVisibility(View.GONE);
                    mainView.findViewById(R.id.new_activity_btn_pause).setVisibility(View.VISIBLE);
                    timer.schedule(timertask, 0, 1000);
                }
            }
        } else {
            if (timestamp > 0) {
                mainView.findViewById(R.id.new_activity_btn_stop).setOnClickListener(this);
                mainView.findViewById(R.id.new_activity_btn_start).setVisibility(View.GONE);
                mainView.findViewById(R.id.new_activity_btn_pause).setVisibility(View.VISIBLE);
                if (timestamp > 0)
                    timer.schedule(timertask, 0, 1000);
            }
        }


//        mainView.findViewById(R.id.new_activity_activityhistory_btn).setOnClickListener(this);
//        mainView.findViewById(R.id.new_activity_logrecent_btn).setOnClickListener(this);

        return mainView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_activity_btn_stop: {
                if(saves.getLong("pause", 0) > 0) {
                    Long _pause = System.currentTimeMillis() - saves.getLong("pause", 0);
                    timestamp -= _pause;
                    saves.edit().putLong("pause", 0).apply();
                }

                timer.cancel();
//                    timer = new Timer();

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());

                mainView.findViewById(R.id.new_activity_btn_start).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.new_activity_btn_pause).setVisibility(View.GONE);

                HashMap<String, String> params = new HashMap<>();
                params.put("student_id", DataManager.getInstance().user.id);
                params.put("module_id", ((Module) (new Select().from(Module.class).where("module_name = ?", module.getText().toString()).executeSingle())).id);
                params.put("activity_type", DataManager.getInstance().api_values.get(activityType.getText().toString()));
                params.put("activity", DataManager.getInstance().api_values.get(chooseActivity.getText().toString()));
                params.put("activity_date", c.get(Calendar.YEAR) + "-" + ((c.get(Calendar.MONTH) + 1) < 10 ? "0" + (c.get(Calendar.MONTH) + 1) : (c.get(Calendar.MONTH) + 1)) + "-" + ((c.get(Calendar.DAY_OF_MONTH)) < 10 ? "0" + c.get(Calendar.DAY_OF_MONTH) : c.get(Calendar.DAY_OF_MONTH)));
                long duration = ((System.currentTimeMillis() - timestamp) / 60000);
                if (duration == 0) {
                    Snackbar.make(mainView.findViewById(R.id.container), R.string.activity_canceled_due_to_short_time, Snackbar.LENGTH_LONG).show();
                    new Delete().from(RunningActivity.class).execute();

                    saves.edit().putLong("timer", 0).apply();
                    saves.edit().putLong("pause", 0).apply();


                    Intent intent = new Intent(DataManager.getInstance().mainActivity, NotificationAlarm.class);
                    pendingIntent = PendingIntent.getBroadcast(DataManager.getInstance().mainActivity, 0,
                            intent, PendingIntent.FLAG_ONE_SHOT);
                    am.cancel(pendingIntent);

                    countdown_textView.setText("00:00");

                    ((TextView)mainView.findViewById(R.id.new_activity_btn_pause_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.pause));

                    ((CardView)mainView.findViewById(R.id.new_activity_btn_start)).setCardBackgroundColor(ContextCompat.getColor(DataManager.getInstance().mainActivity, R.color.default_blue));//getResources().getColor(R.color.default_blue));
                    mainView.findViewById(R.id.new_activity_btn_start).setOnClickListener(this);
                    mainView.findViewById(R.id.new_activity_btn_stop).setOnClickListener(null);
                    return;
                }
                //duration = 1;
                params.put("time_spent", duration + "");

                System.out.println("ADD_ACTIVITY: " + params.toString());
                String responseCode = NetworkManager.getInstance().addActivity(params);
                if (responseCode.equals("403")) {
                    Snackbar.make(mainView.findViewById(R.id.container), R.string.already_added_activity, Snackbar.LENGTH_LONG).show();
                } else if (!responseCode.equals("200")) {
                    Activity activity = new Activity();
                    activity.student_id = params.get("student_id");
                    activity.module_id = params.get("module_id");
                    activity.activity_type = params.get("activity_type");
                    activity.activity = params.get("activity");
                    activity.activity_date = params.get("activity_date");
                    activity.time_spent = params.get("time_spent");
                    activity.save();
                } else {
//                    NetworkManager.getInstance().getActivityHistory(DataManager.getInstance().user.id);
                }

                new Delete().from(RunningActivity.class).execute();

                saves.edit().putLong("timer", 0).apply();
                saves.edit().putLong("pause", 0).apply();
                ((TextView)mainView.findViewById(R.id.new_activity_btn_pause_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.pause));

                Intent intent = new Intent(DataManager.getInstance().mainActivity, NotificationAlarm.class);
                pendingIntent = PendingIntent.getBroadcast(DataManager.getInstance().mainActivity, 0,
                        intent, PendingIntent.FLAG_ONE_SHOT);
                am.cancel(pendingIntent);


                Snackbar.make(mainView.findViewById(R.id.container), R.string.activity_stopped, Snackbar.LENGTH_LONG).show();
                countdown_textView.setText("00:00");

                ((CardView)mainView.findViewById(R.id.new_activity_btn_start)).setCardBackgroundColor(ContextCompat.getColor(DataManager.getInstance().mainActivity, R.color.default_blue));//getResources().getColor(R.color.default_blue));
                mainView.findViewById(R.id.new_activity_btn_start).setOnClickListener(this);
                mainView.findViewById(R.id.new_activity_btn_stop).setOnClickListener(null);

                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DataManager.getInstance().mainActivity.onBackPressed();
                    }
                });
                break;
            }
            case R.id.new_activity_btn_pause: {
                if(((TextView)mainView.findViewById(R.id.new_activity_btn_pause_text)).getText().toString().equals(DataManager.getInstance().mainActivity.getString(R.string.pause))) {
                    Intent intent = new Intent(DataManager.getInstance().mainActivity, NotificationAlarm.class);
                    pendingIntent = PendingIntent.getBroadcast(DataManager.getInstance().mainActivity, 0,
                            intent, PendingIntent.FLAG_ONE_SHOT);
                    am.cancel(pendingIntent);

                    saves.edit().putLong("pause", System.currentTimeMillis()).apply();

                    timer.cancel();

                    Snackbar.make(mainView.findViewById(R.id.container), R.string.activity_paused, Snackbar.LENGTH_LONG).show();

                    ((TextView)mainView.findViewById(R.id.new_activity_btn_pause_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.resume));
                } else {
                    Long _pause = System.currentTimeMillis() - saves.getLong("pause", 0);
                    timestamp += _pause;
                    saves.edit().putLong("pause", 0).apply();
                    int reminder = (Integer.parseInt(reminder_textView.getText().toString().split(":")[0]) * 60) + Integer.parseInt(reminder_textView.getText().toString().split(":")[1]);
                    timer = new Timer();
                    timertask = new TimerTask() {
                        @Override
                        public void run() {
                            Long elapsed_time = System.currentTimeMillis() - timestamp;
                            Long seconds = (elapsed_time / 1000) % 60;
                            Long minutes = elapsed_time / 60000;

                            String value = "";
                            if(minutes < 10)
                                value += "0" + minutes + ":";
                            else
                                value += minutes + ":";
                            if(seconds < 10)
                                value += "0" + seconds;
                            else
                                value += seconds;
                            final String f_value = value;
                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    countdown_textView.setText(f_value);
                                }
                            });

                            if(minutes >= 180) {
                                mainView.findViewById(R.id.new_activity_btn_stop).callOnClick();
                            }
                        }
                    };
                    if(reminder != 0) {
                        Intent intent = new Intent(DataManager.getInstance().mainActivity, NotificationAlarm.class);
                        pendingIntent = PendingIntent.getBroadcast(DataManager.getInstance().mainActivity, 0,
                                intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        am.set(AlarmManager.RTC_WAKEUP,
                                System.currentTimeMillis() + reminder * 1000, pendingIntent);
                    }
                    timer.schedule(timertask, 0, 1000);
                    saves.edit().putLong("timer", timestamp).apply();

                    Snackbar.make(mainView.findViewById(R.id.container), R.string.activity_resumed, Snackbar.LENGTH_LONG).show();

                    ((TextView)mainView.findViewById(R.id.new_activity_btn_pause_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.pause));
                }
                break;
            }
            case R.id.new_activity_btn_start: {

                if(DataManager.getInstance().user.isDemo) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LogNewActivity.this.getActivity());
                    alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.demo_mode_addactivitylog) + "</font>"));
                    alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return;
                }

                if(module.getText().toString().equals(DataManager.getInstance().mainActivity.getString(R.string.no_module))) {
                    Snackbar.make(mainView.findViewById(R.id.container), R.string.no_module_selected, Snackbar.LENGTH_LONG).show();
                    return;
                }
                mainView.findViewById(R.id.new_activity_btn_start).setVisibility(View.GONE);
                mainView.findViewById(R.id.new_activity_btn_pause).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.new_activity_btn_stop).setOnClickListener(this);

                Snackbar.make(mainView.findViewById(R.id.container), R.string.activity_started, Snackbar.LENGTH_LONG).show();

                timestamp = System.currentTimeMillis();
                int reminder = (Integer.parseInt(reminder_textView.getText().toString().split(":")[0]) * 60) + Integer.parseInt(reminder_textView.getText().toString().split(":")[1]);
                timer = new Timer();
                timertask = new TimerTask() {
                    @Override
                    public void run() {
                        Long elapsed_time = System.currentTimeMillis() - timestamp;
                        Long seconds = (elapsed_time / 1000) % 60;
                        Long minutes = elapsed_time / 60000;

                        String value = "";
                        if(minutes < 10)
                            value += "0" + minutes + ":";
                        else
                            value += minutes + ":";
                        if(seconds < 10)
                            value += "0" + seconds;
                        else
                            value += seconds;
                        final String f_value = value;
                        DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                countdown_textView.setText(f_value);
                            }
                        });

                        if(minutes >= 180) {
                            mainView.findViewById(R.id.new_activity_btn_stop).callOnClick();
                        }
                    }
                };
                timer.schedule(timertask, 0, 1000);
                saves.edit().putLong("timer", timestamp).apply();


                Calendar c = Calendar.getInstance();
                RunningActivity activity = new RunningActivity();
                activity.student_id = DataManager.getInstance().user.id;
                activity.module_id = ((Module) (new Select().from(Module.class).where("module_name = ?", module.getText().toString()).executeSingle())).id;
                activity.activity_type = activityType.getText().toString();
                activity.activity = chooseActivity.getText().toString();
                activity.activity_date = c.get(Calendar.YEAR) + "-" + ((c.get(Calendar.MONTH)+1)<10?"0"+(c.get(Calendar.MONTH)+1):(c.get(Calendar.MONTH)+1)) + "-" + ((c.get(Calendar.DAY_OF_MONTH))<10?"0" + c.get(Calendar.DAY_OF_MONTH):c.get(Calendar.DAY_OF_MONTH));
                activity.save();

                ((CardView)mainView.findViewById(R.id.new_activity_btn_start)).setCardBackgroundColor(ContextCompat.getColor(DataManager.getInstance().mainActivity, R.color.light_grey));//getResources().getColor(R.color.light_grey));
                mainView.findViewById(R.id.new_activity_btn_start).setOnClickListener(null);

                if(reminder != 0) {
                    Intent intent = new Intent(DataManager.getInstance().mainActivity, NotificationAlarm.class);
                    //TODO: Bug Samsung devices - old code works on all except samsung
//                    pendingIntent = PendingIntent.getBroadcast(getActivity(), 0,
//                            intent, PendingIntent.FLAG_ONE_SHOT);
                    pendingIntent = PendingIntent.getBroadcast(DataManager.getInstance().mainActivity, 0,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    am.set(AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + reminder * 1000, pendingIntent);

                    //TODO: BUGFIX AICI
                }
                break;
            }
            case R.id.new_activity_text_timer_1: {
                final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.timespent_layout);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                if(DataManager.getInstance().mainActivity.isLandscape) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = (int) (displaymetrics.widthPixels * 0.3);

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = width;
                    dialog.getWindow().setAttributes(params);
                }

                int hour = Integer.parseInt(reminder_textView.getText().toString().split(":")[0]);
                int minute = Integer.parseInt(reminder_textView.getText().toString().split(":")[1]);

                final NumberPicker hourPicker = (NumberPicker)dialog.findViewById(R.id.hour_picker);
                hourPicker.setMinValue(0);
                hourPicker.setMaxValue(180);
                hourPicker.setValue(hour);
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
                minutePicker.setValue(minute);
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
                dialog.findViewById(R.id.timespent_save_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String clock = "";
                        int hour = hourPicker.getValue();
                        if (hour < 10)
                            clock += "0" + hour + ":";
                        else
                            clock += hour + ":";
                        int minute = minutePicker.getValue();
                        if (minute < 10)
                            clock += "0" + minute;
                        else
                            clock += minute;
                        reminder_textView.setText(clock);
                        dialog.dismiss();
                    }
                });
                ((TextView)dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);

                dialog.show();
                break;
            }
            case R.id.new_activity_module_textView: {
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

                ((TextView)dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView)dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_module);

                final ModuleAdapter moduleAdapter = new ModuleAdapter(DataManager.getInstance().mainActivity, module.getText().toString());
                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(moduleAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if(DataManager.getInstance().user.isSocial
                                && position == moduleAdapter.moduleList.size() - 1) {
                            //add new module
                            EditText add_module_edit_text = (EditText)addModuleLayout.findViewById(R.id.add_module_edit_text);
                            add_module_edit_text.setText("");
                            addModuleLayout.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        } else {
                            module.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                            RunningActivity activity = new Select().from(RunningActivity.class).executeSingle();
                            if (activity != null) {
                                activity.student_id = DataManager.getInstance().user.id;
                                activity.module_id = ((Module) (new Select().from(Module.class).where("module_name = ?", module.getText().toString()).executeSingle())).id;
                                activity.save();
                            }
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
                break;
            }
            case R.id.new_activity_activitytype_textView: {
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

                ((TextView)dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView)dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_activity_type);

                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(new ActivityTypeAdapter(DataManager.getInstance().mainActivity, activityType.getText().toString()));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        activityType.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                        chooseActivity.setText(DataManager.getInstance().choose_activity.get(activityType.getText().toString()).get(0));

                        RunningActivity activity = new Select().from(RunningActivity.class).executeSingle();
                        if(activity != null) {
                            activity.activity_type = activityType.getText().toString();
                            activity.activity = chooseActivity.getText().toString();
                            activity.save();
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            }
            case R.id.new_activity_choose_textView: {
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

                ((TextView)dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView)dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_activity);

                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(new ChooseActivityAdapter(DataManager.getInstance().mainActivity, chooseActivity.getText().toString(), activityType.getText().toString()));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        chooseActivity.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());

                        RunningActivity activity = new Select().from(RunningActivity.class).executeSingle();
                        if(activity != null) {
                            activity.activity = chooseActivity.getText().toString();
                            activity.save();
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            }
            case R.id.add_module_button_text: {
                EditText add_module_edit_text = (EditText)addModuleLayout.findViewById(R.id.add_module_edit_text);
                final String moduleName = add_module_edit_text.getText().toString();
                if(moduleName.length() == 0) {
                    Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.module_name_invalid, Snackbar.LENGTH_LONG).show();
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        final HashMap<String, String> params = new HashMap<>();
                        params.put("student_id", DataManager.getInstance().user.id);
                        params.put("module", moduleName);
                        params.put("is_social", "yes");

                        if (NetworkManager.getInstance().addModule(params)) {

                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        } else {
                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    (DataManager.getInstance().mainActivity).hideProgressBar();
                                    Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();

                addModuleLayout.setVisibility(View.GONE);

                return;
            }
        }
    }
}

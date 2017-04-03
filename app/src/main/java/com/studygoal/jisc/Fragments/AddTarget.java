package com.studygoal.jisc.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.ActivityTypeAdapter;
import com.studygoal.jisc.Adapters.ChooseActivityAdapter;
import com.studygoal.jisc.Adapters.GenericAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.Models.Targets;
import com.studygoal.jisc.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTarget extends Fragment implements View.OnClickListener {

    AppCompatTextView activityType, chooseActivity, every, in;
    EditText hours, minutes;
    EditText because;
    public Boolean isInEditMode = false;
    public Targets item;
    View body;

    RelativeLayout addModuleLayout;

    public AddTarget() {}

    @Override
    public void onResume() {
        super.onResume();
        if(isInEditMode)
            DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.edit_target));
        else
            DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.add_target));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(8);
        DataManager.getInstance().addTarget = 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View mainView = inflater.inflate(R.layout.target_addtarget, container, false);
        DataManager.getInstance().reload();
        body = mainView.findViewById(R.id.addtarget_container);

        ((TextView)mainView.findViewById(R.id.addtarget_activity_type_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        activityType = ((AppCompatTextView)mainView.findViewById(R.id.addtarget_activityType_textView));
        activityType.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        activityType.setTypeface(DataManager.getInstance().myriadpro_regular);

        ((TextView)mainView.findViewById(R.id.addtarget_text_choose)).setTypeface(DataManager.getInstance().myriadpro_regular);
        chooseActivity = ((AppCompatTextView)mainView.findViewById(R.id.addtarget_chooseActivity_textView));
        chooseActivity.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        chooseActivity.setTypeface(DataManager.getInstance().myriadpro_regular);

        addModuleLayout = (RelativeLayout)mainView.findViewById(R.id.add_new_module_layout);
        addModuleLayout.setVisibility(View.GONE);
        ((EditText)mainView.findViewById(R.id.add_module_edit_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView)mainView.findViewById(R.id.add_module_button_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        mainView.findViewById(R.id.add_module_button_text).setOnClickListener(this);

        ((TextView)mainView.findViewById(R.id.addtarget_text_for)).setTypeface(DataManager.getInstance().myriadpro_regular);

        TextWatcher hoursWatcher = new TextWatcher() {
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
                    if (value < 0 || value > 8) {
                        hours.setText("");
                        hours.setSelection(hours.getText().length());
                    }
                }
            }
        };

        because = ((EditText)mainView.findViewById(R.id.addtarget_edittext_because));

        TextWatcher minutesWatcher = new TextWatcher() {
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
                        minutes.setText("");
                        minutes.setSelection(minutes.getText().length());
                    }
                }
            }
        };

        final View contentView = container;
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int mPreviousHeight;

            @Override
            public void onGlobalLayout() {
                int newHeight = contentView.getHeight();
                if (mPreviousHeight != 0) {
                    if (mPreviousHeight > newHeight) {

                        // Height decreased: keyboard was shown
                        mainView.findViewById(R.id.content_scroll).setPadding(0, 0, 0, 200);

                        if(because.isFocused()) {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms

                                    ScrollView scrollView = (ScrollView) mainView.findViewById(R.id.addtarget_container);
                                    scrollView.scrollTo(0, mainView.findViewById(R.id.content_scroll).getHeight());
                                }
                            }, 100);
                        }

                    } else if (mPreviousHeight < newHeight) {
                        mainView.findViewById(R.id.content_scroll).setPadding(0, 0, 0, 0);
                    } else {
                        // No change
                    }
                }
                mPreviousHeight = newHeight;
            }
        });

        hours = ((EditText) mainView.findViewById(R.id.addtarget_text_timer_1));
        hours.setTypeface(DataManager.getInstance().myriadpro_regular);
        minutes = ((EditText) mainView.findViewById(R.id.addtarget_text_timer_3));
        minutes.setTypeface(DataManager.getInstance().myriadpro_regular);

        hours.addTextChangedListener(hoursWatcher);
        minutes.addTextChangedListener(minutesWatcher);

        ((TextView)mainView.findViewById(R.id.addtarget_text_hours)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView)mainView.findViewById(R.id.addtarget_text_minutes)).setTypeface(DataManager.getInstance().myriadpro_regular);

        every = ((AppCompatTextView)mainView.findViewById(R.id.addtarget_every_textView));
        every.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        every.setTypeface(DataManager.getInstance().myriadpro_regular);

        in = ((AppCompatTextView) mainView.findViewById(R.id.addtarget_in_textView));
        in.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        in.setTypeface(DataManager.getInstance().myriadpro_regular);

        ((TextView)mainView.findViewById(R.id.addtarget_every_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView)mainView.findViewById(R.id.addtarget_in_text)).setTypeface(DataManager.getInstance().myriadpro_regular);


        ((TextView)mainView.findViewById(R.id.addtarget_text_because_title)).setTypeface(DataManager.getInstance().myriadpro_regular);
        because.setTypeface(DataManager.getInstance().myriadpro_regular);
        because.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.log_activity_edittext_note) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        if(isInEditMode) {
            for(Map.Entry<String, String> entry : DataManager.getInstance().api_values.entrySet()) {
                if(entry.getValue().equals(item.activity_type))
                    activityType.setText(entry.getKey());
            }
            for(Map.Entry<String, String> entry : DataManager.getInstance().api_values.entrySet()) {
                if(entry.getValue().equals(item.activity))
                    chooseActivity.setText(entry.getKey());
            }

            hours.setText(Integer.parseInt(item.total_time) / 60 > 10 ? "" + Integer.parseInt(item.total_time) / 60 : "0" + Integer.parseInt(item.total_time) / 60);
            minutes.setText(Integer.parseInt(item.total_time) % 60 > 10 ? "" + Integer.parseInt(item.total_time) % 60 : "0" + Integer.parseInt(item.total_time) % 60);

            for(Map.Entry<String, String> entry : DataManager.getInstance().api_values.entrySet()) {
                if(entry.getValue().toLowerCase().equals(item.time_span.toLowerCase())) {
                    String value = entry.getKey();
                    value = value.substring(0, 1).toUpperCase() + value.substring(1, value.length());
                    every.setText(value);
                }
            }

            if(item.module_id.equals(""))
                in.setText(DataManager.getInstance().mainActivity.getString(R.string.any_module));
            else
                in.setText(((Module)(new Select().from(Module.class).where("module_id = ?", item.module_id).executeSingle())).name);

            because.setText(item.because);

        } else {
            activityType.setText(DataManager.getInstance().activity_type.get(0));
            chooseActivity.setText(DataManager.getInstance().choose_activity.get(DataManager.getInstance().activity_type.get(0)).get(0));
            activityType.setOnClickListener(this);
            chooseActivity.setOnClickListener(this);

            every.setText(DataManager.getInstance().period.get(0));
            in.setText(DataManager.getInstance().mainActivity.getString(R.string.any_module));
        }
        every.setOnClickListener(this);
        in.setOnClickListener(this);

        mainView.findViewById(R.id.addtarget_save_btn).setOnClickListener(this);

        return mainView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addtarget_activityType_textView : {
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
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            }
            case R.id.addtarget_chooseActivity_textView : {
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
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_activity);

                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(new ChooseActivityAdapter(DataManager.getInstance().mainActivity, chooseActivity.getText().toString(), activityType.getText().toString()));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        chooseActivity.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            }
            case R.id.addtarget_cardView_timespent: {
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

                final NumberPicker hourPicker = (NumberPicker)dialog.findViewById(R.id.hour_picker);
                hourPicker.setMinValue(0);
                if(every.getText().toString().equals(getString(R.string.daily)))
                    hourPicker.setMaxValue(23);
                else
                    hourPicker.setMaxValue(71);
                hourPicker.setValue(Integer.parseInt(hours.getText().toString()));
                hourPicker.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        return String.format("%02d", value);
                    }
                });
                final NumberPicker minutePicker = (NumberPicker)dialog.findViewById(R.id.minute_picker);
                minutePicker.setMinValue(0);
                minutePicker.setMaxValue(59);
                minutePicker.setValue(Integer.parseInt(minutes.getText().toString()));
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
                        int hour = hourPicker.getValue();
                        if (hour < 10)
                            hours.setText("0" + hour);
                        else
                            hours.setText("" + hour);
                        int minute = minutePicker.getValue();
                        if (minute < 10)
                            minutes.setText("0" + minute);
                        else
                            minutes.setText("" + minute);
                        dialog.dismiss();
                    }
                });
                ((TextView)dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);

                dialog.show();
                break;
            }

            case R.id.addtarget_every_textView: {
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
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_interval);

                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(new GenericAdapter(DataManager.getInstance().mainActivity, every.getText().toString(), DataManager.getInstance().period));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        every.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            }
            case R.id.addtarget_in_textView: {
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
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_module);

                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);

                final ArrayList<String> items = new ArrayList<>();
                items.add(DataManager.getInstance().mainActivity.getString(R.string.any_module));
                List<Module> modules = new Select().from(Module.class).execute();
                for(int i=0; i < modules.size(); i++)
                    items.add(modules.get(i).name);

                if(DataManager.getInstance().user.isSocial) {
                    items.add(AddTarget.this.getActivity().getString(R.string.add_module));
                }

                listView.setAdapter(new GenericAdapter(DataManager.getInstance().mainActivity, in.getText().toString(), items));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(DataManager.getInstance().user.isSocial
                                && position == items.size() - 1) {
                            //add new module
                            EditText add_module_edit_text = (EditText)addModuleLayout.findViewById(R.id.add_module_edit_text);
                            add_module_edit_text.setText("");
                            addModuleLayout.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        } else {
                            in.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
                break;
            }
            case R.id.addtarget_save_btn: {
                View view = getActivity().getCurrentFocus();
                if(view!=null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if(isInEditMode) {

                    if(DataManager.getInstance().user.isDemo) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddTarget.this.getActivity());
                        alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.demo_mode_edittarget) + "</font>"));
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

                    final int total_time = Integer.parseInt(hours.getText().toString()) * 60 + Integer.parseInt(minutes.getText().toString());
                    if(total_time == Integer.parseInt(item.total_time)
                            && every.getText().toString().toLowerCase().equals(item.time_span.toLowerCase())
                            && (item.because.equals(because.getText().toString()))
                            && (item.module_id.equals("") && in.getText().toString().equals(DataManager.getInstance().mainActivity.getString(R.string.any_module)))) {
                        DataManager.getInstance().mainActivity.onBackPressed();
                        return;
                    }
                    if (total_time == 0) {
                        Snackbar.make(body, R.string.fail_to_edit_target_insuficient_time, Snackbar.LENGTH_LONG).show();
                        return;
                    } else {
                        final HashMap<String, String> params = new HashMap<>();
                        params.put("student_id", DataManager.getInstance().user.id);
                        params.put("target_id", item.target_id);
                        params.put("total_time", total_time + "");
                        params.put("time_span", DataManager.getInstance().api_values.get(every.getText().toString().toLowerCase()));
                        if(!in.getText().toString().toLowerCase().equals(DataManager.getInstance().mainActivity.getString(R.string.any_module).toLowerCase()))
                            params.put("module", ((Module) (new Select().from(Module.class).where("module_name = ?", in.getText().toString()).executeSingle())).id);
                        if (because.getText().toString().length() > 0)
                            params.put("because", because.getText().toString());
                        System.out.println("EDIT_TARGET: " + params.toString());
                        DataManager.getInstance().mainActivity.showProgressBar(null);
                        Calendar calendar = Calendar.getInstance();
                        String modified_date = "";
                        modified_date += calendar.get(Calendar.YEAR) +  "-";
                        modified_date += (calendar.get(Calendar.MONTH)+1)<10? "0" + (calendar.get(Calendar.MONTH)+1) + "-" : (calendar.get(Calendar.MONTH)+1) + "-";
                        modified_date += calendar.get(Calendar.DAY_OF_MONTH)<10? "0" + calendar.get(Calendar.DAY_OF_MONTH) + " " : calendar.get(Calendar.DAY_OF_MONTH) + " ";
                        modified_date += calendar.get(Calendar.HOUR_OF_DAY)<10? "0" + calendar.get(Calendar.HOUR_OF_DAY) + ":" : calendar.get(Calendar.HOUR_OF_DAY) + ":";
                        modified_date += calendar.get(Calendar.MINUTE)<10? "0" + calendar.get(Calendar.MINUTE) + ":" : calendar.get(Calendar.MINUTE) + ":";
                        modified_date += calendar.get(Calendar.SECOND)<10? "0" + calendar.get(Calendar.SECOND) : calendar.get(Calendar.SECOND);

                        final String finalModified_date = modified_date;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (NetworkManager.getInstance().editTarget(params)) {
                                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            item.total_time = total_time + "";
                                            item.time_span = DataManager.getInstance().api_values.get(every.getText().toString().toLowerCase());
                                            if(!in.getText().toString().toLowerCase().equals(DataManager.getInstance().mainActivity.getString(R.string.any_module).toLowerCase()))
                                                item.module_id = ((Module)new Select().from(Module.class).where("module_name = ?", in.getText().toString()).executeSingle()).id;
                                            else
                                                item.module_id = "";
                                            item.because = because.getText().toString();
                                            item.modified_date = finalModified_date;

                                            DataManager.getInstance().mainActivity.hideProgressBar();
                                            DataManager.getInstance().mainActivity.onBackPressed();
//                                            Snackbar.make(body, R.string.target_saved, Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DataManager.getInstance().mainActivity.hideProgressBar();
                                            Snackbar.make(body, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                } else {

                    if(DataManager.getInstance().user.isDemo) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddTarget.this.getActivity());
                        alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.demo_mode_addtarget) + "</font>"));
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

                    int total_time = Integer.parseInt(hours.getText().toString()) * 60 + Integer.parseInt(minutes.getText().toString());
                    if (total_time == 0) {
                        Snackbar.make(body, R.string.fail_to_add_target_insufficient_time, Snackbar.LENGTH_LONG).show();
                        return;
                    } else {
                        Module module = ((new Select().from(Module.class).where("module_name = ?", in.getText().toString()).executeSingle()));
                        String id;
                        if(module == null) id = "";
                        else if(module.id == null) id = "";
                        else id = module.id;
                        if(new Select().from(Targets.class).where("activity = ?", chooseActivity.getText().toString()).and("time_span = ?", every.getText().toString()).and("module_id = ?", id).exists()) {
                            Snackbar.make(body, R.string.target_same_parameters, Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        final HashMap<String, String> params = new HashMap<>();
                        params.put("student_id", DataManager.getInstance().user.id);
                        params.put("activity_type", DataManager.getInstance().api_values.get(activityType.getText().toString()));
                        params.put("activity", DataManager.getInstance().api_values.get(chooseActivity.getText().toString()));
                        params.put("total_time", total_time + "");
                        params.put("time_span", DataManager.getInstance().api_values.get(every.getText().toString().toLowerCase()));
                        if(!in.getText().toString().toLowerCase().equals(DataManager.getInstance().mainActivity.getString(R.string.any_module).toLowerCase()))
                            params.put("module", ((Module) (new Select().from(Module.class).where("module_name = ?", in.getText().toString()).executeSingle())).id);
                        if (because.getText().toString().length() > 0)
                            params.put("because", because.getText().toString());

                        System.out.println("ADD_TARGET: " + params.toString());
                        DataManager.getInstance().mainActivity.showProgressBar(null);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (NetworkManager.getInstance().addTarget(params)) {
                                    NetworkManager.getInstance().getTargets(DataManager.getInstance().user.id);
                                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DataManager.getInstance().mainActivity.hideProgressBar();
                                            DataManager.getInstance().mainActivity.onBackPressed();
//                                            Snackbar.make(body, R.string.target_saved, Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            (DataManager.getInstance().mainActivity).hideProgressBar();
                                            Snackbar.make(body, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }).start();

                    }
                }
                break;
            }
            case R.id.add_module_button_text: {
                EditText add_module_edit_text = (EditText)addModuleLayout.findViewById(R.id.add_module_edit_text);
                final String moduleName = add_module_edit_text.getText().toString();
                if(moduleName.length() == 0) {
                    Snackbar.make(body, R.string.module_name_invalid, Snackbar.LENGTH_LONG).show();
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
                                    Snackbar.make(body, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
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

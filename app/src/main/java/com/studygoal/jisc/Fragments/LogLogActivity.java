package com.studygoal.jisc.Fragments;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.ActivityTypeAdapter;
import com.studygoal.jisc.Adapters.ChooseActivityAdapter;
import com.studygoal.jisc.Adapters.ModuleAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.ActivityHistory;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.Utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogLogActivity extends Fragment implements View.OnClickListener {

    View mainView;
    AppCompatTextView chooseActivity;
    AppCompatTextView module;
    AppCompatTextView activityType;

    EditText hours_spent, minutes_spent;
    TextView date;

    public Boolean isInEditMode;
    public ActivityHistory item;



    EditText note;
    private String init_date;
    private String init_note;
    private String init_timespent;

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(8);
        if(isInEditMode) {
            DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.edit_recent_activity));
        } else {
            DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.log_recent_activity));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.log_fragment_log_activity, container, false);

        DataManager.getInstance().reload();

        ((TextView)mainView.findViewById(R.id.log_activity_module_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView)mainView.findViewById(R.id.log_activity_text_choose)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView)mainView.findViewById(R.id.log_activity_activity_type_text)).setTypeface(DataManager.getInstance().myriadpro_regular);


        TextView log_activity_text_hours = (TextView) mainView.findViewById(R.id.log_activity_text_hours);
        log_activity_text_hours.setTypeface(DataManager.getInstance().myriadpro_regular);

        hours_spent = ((EditText) mainView.findViewById(R.id.log_activity_text_timer_1));
        hours_spent.setTypeface(DataManager.getInstance().myriadpro_regular);
        minutes_spent = ((EditText) mainView.findViewById(R.id.log_activity_text_timer_3));
        minutes_spent.setTypeface(DataManager.getInstance().myriadpro_regular);

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
                    if (value < 0 || value > 9) {
                        hours_spent.setText("0");
                    }
                }
            }
        };

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
                    if (value < 0 || value > 59) {
                        minutes_spent.setText("0");
                    }
                }
            }
        };

        hours_spent.addTextChangedListener(hoursWatcher);
        minutes_spent.addTextChangedListener(minutesWatcher);

        ((TextView)mainView.findViewById(R.id.log_activity_text_minutes)).setTypeface(DataManager.getInstance().myriadpro_regular);

        date = ((TextView)mainView.findViewById(R.id.log_activity_text_date));
        date.setTypeface(DataManager.getInstance().myriadpro_regular);


        chooseActivity = (AppCompatTextView) mainView.findViewById(R.id.log_activity_chooseActivity_textView);
        chooseActivity.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        chooseActivity.setTypeface(DataManager.getInstance().myriadpro_regular);
        chooseActivity.setText(DataManager.getInstance().choose_activity.get(DataManager.getInstance().activity_type.get(0)).get(0));


        activityType = (AppCompatTextView) mainView.findViewById(R.id.log_activity_activityType_textView);
        activityType.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        activityType.setTypeface(DataManager.getInstance().myriadpro_regular);
        activityType.setText(DataManager.getInstance().activity_type.get(0));


        module = (AppCompatTextView) mainView.findViewById(R.id.log_activity_module_textView);
        module.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        module.setTypeface(DataManager.getInstance().myriadpro_regular);
        List<Module> list_module = new Select().from(Module.class).execute();
        if(list_module.size() > 0)
            module.setText((list_module.get(0)).name);
        else
            module.setText(DataManager.getInstance().mainActivity.getString(R.string.no_module));

        Calendar c = Calendar.getInstance();

        mainView.findViewById(R.id.log_activity_save_btn).setOnClickListener(this);

        note = (EditText)mainView.findViewById(R.id.log_activity_edittext_note);
        note.setTypeface(DataManager.getInstance().myriadpro_regular);
        note.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
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


        mainView.findViewById(R.id.log_activity_date).setOnClickListener(this);

        if(!isInEditMode) {
            activityType.setOnClickListener(this);
            module.setOnClickListener(this);
            chooseActivity.setOnClickListener(this);

            date.setText(Utils.formatDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)));

            date.setTag(c.get(Calendar.YEAR) + "-" + ((c.get(Calendar.MONTH)+1)<10?"0"+(c.get(Calendar.MONTH)+1):(c.get(Calendar.MONTH)+1)) + "-" + ((c.get(Calendar.DAY_OF_MONTH))<10?"0" + c.get(Calendar.DAY_OF_MONTH):c.get(Calendar.DAY_OF_MONTH)));
        } else {
            //Is in editmode
            try {
                module.setText(((Module) new Select().from(Module.class).where("module_id = ?", item.module_id).executeSingle()).name);
            } catch (Exception e){
                e.printStackTrace();
                module.setText("");
            }
            for(Map.Entry<String, String> entry : DataManager.getInstance().api_values.entrySet()) {
                if(entry.getValue().equals(item.activity_type))
                    activityType.setText(entry.getKey());
            }
            for(Map.Entry<String, String> entry : DataManager.getInstance().api_values.entrySet()) {
                if(entry.getValue().equals(item.activity))
                    chooseActivity.setText(entry.getKey());
            }

            c.set(Integer.parseInt(item.activity_date.split("-")[0]), Integer.parseInt(item.activity_date.split("-")[1]) - 1, Integer.parseInt(item.activity_date.split("-")[2]));
            date.setText(Utils.formatDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)));
            date.setTag(c.get(Calendar.YEAR) + "-" + ((c.get(Calendar.MONTH)+1)<10?"0"+(c.get(Calendar.MONTH)+1):(c.get(Calendar.MONTH)+1)) + "-" + ((c.get(Calendar.DAY_OF_MONTH))<10?"0" + c.get(Calendar.DAY_OF_MONTH):c.get(Calendar.DAY_OF_MONTH)));
            note.setText(item.note);

            init_date = date.getText().toString();
            init_note = item.note;
            init_timespent = item.time_spent;

            int h_spent = Integer.parseInt(item.time_spent) / 60;
            int m_spent = Integer.parseInt(item.time_spent) % 60;
            hours_spent.setText(h_spent + "");
            minutes_spent.setText(m_spent<10?"0"+m_spent:m_spent+"");
        }

        if(DataManager.getInstance().mainActivity.isLandscape) {
            ((TextView) mainView.findViewById(R.id.header_1)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.header_2)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.header_3)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.header_4)).setTypeface(DataManager.getInstance().myriadpro_regular);
        } else {
            ((TextView)mainView.findViewById(R.id.log_activity_text_timespent)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView)mainView.findViewById(R.id.log_activity_text_date_title)).setTypeface(DataManager.getInstance().myriadpro_regular);
        }

        return mainView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.log_activity_save_btn: {
                if(isInEditMode) {
                    final int time_spent = Integer.parseInt(hours_spent.getText().toString()) * 60 + Integer.parseInt(minutes_spent.getText().toString());
                    if(init_date.equals(date.getText().toString()) && init_timespent.equals(time_spent+"") && init_note.equals(note.getText().toString())) {
                        DataManager.getInstance().mainActivity.onBackPressed();
                        return;
                    }
                    if (time_spent == 0) {
                        Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.you_must_spend_time_to_edit_activity, Snackbar.LENGTH_LONG).show();
                        return;
                    } else {
                        final HashMap<String, String> params = new HashMap<>();
                        params.put("student_id", DataManager.getInstance().user.id);
                        params.put("log_id", item.id);
                        params.put("activity_date", date.getTag().toString());
                        params.put("time_spent", time_spent + "");
                        if (note.getText().toString().length() > 0)
                            params.put("note", note.getText().toString());

                        DataManager.getInstance().mainActivity.showProgressBar(null);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(calendar.getTimeInMillis() - calendar.get(Calendar.ZONE_OFFSET));
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
                                if (NetworkManager.getInstance().editActivity(params)) {
                                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            item.activity_date = date.getTag().toString();
                                            item.time_spent = time_spent + "";
                                            item.note = note.getText().toString();
                                            item.modified_date = finalModified_date;
                                            item.save();
                                            DataManager.getInstance().mainActivity.hideProgressBar();
                                            DataManager.getInstance().mainActivity.onBackPressed();
                                            Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.activity_has_been_edited, Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DataManager.getInstance().mainActivity.hideProgressBar();
                                            Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }).start();

                    }
                } else {
                    if(module.getText().toString().equals(DataManager.getInstance().mainActivity.getString(R.string.no_module))) {
                        Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.no_module_selected, Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    int time_spent = Integer.parseInt(hours_spent.getText().toString()) * 60 + Integer.parseInt(minutes_spent.getText().toString());
                    if (time_spent == 0) {
                        Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.spend_more_time_to_submit_activity, Snackbar.LENGTH_LONG).show();
                        return;
                    } else {
                        final HashMap<String, String> params = new HashMap<>();
                        params.put("student_id", DataManager.getInstance().user.id);
                        params.put("module_id", ((Module) (new Select().from(Module.class).where("module_name = ?", module.getText().toString()).executeSingle())).id);
                        params.put("activity_type", DataManager.getInstance().api_values.get(activityType.getText().toString()));
                        params.put("activity", DataManager.getInstance().api_values.get(chooseActivity.getText().toString()));
                        params.put("activity_date", date.getTag().toString());
                        params.put("time_spent", time_spent + "");
                        if (note.getText().toString().length() > 0)
                            params.put("note", note.getText().toString());

                        System.out.println("ADD_ACTIVITY: " + params.toString());
                        DataManager.getInstance().mainActivity.showProgressBar(null);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String responseCode = NetworkManager.getInstance().addActivity(params);
                                if (responseCode.equals("200")) {
                                    NetworkManager.getInstance().getActivityHistory(DataManager.getInstance().user.id);
                                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DataManager.getInstance().mainActivity.hideProgressBar();
                                            DataManager.getInstance().mainActivity.onBackPressed();
                                        }
                                    });
                                } else if(responseCode.equals("403")) {
                                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DataManager.getInstance().mainActivity.hideProgressBar();
                                            Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.already_added_activity, Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DataManager.getInstance().mainActivity.hideProgressBar();
                                            Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }).start();

                    }
                }
                break;
            }
            case R.id.log_activity_module_textView: {
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

                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(new ModuleAdapter(DataManager.getInstance().mainActivity, module.getText().toString()));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        module.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            }
            case R.id.log_activity_activityType_textView : {
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
            case R.id.log_activity_chooseActivity_textView : {
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
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            }
            case R.id.log_activity_cardView_timespent: {
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
                final NumberPicker minutePicker = (NumberPicker)dialog.findViewById(R.id.minute_picker);

                hourPicker.setMinValue(0);
                hourPicker.setMaxValue(7);
                hourPicker.setValue(Integer.parseInt(hours_spent.getText().toString()));
//                hourPicker.setFormatter(new NumberPicker.Formatter() {
//                    @Override
//                    public String format(int value) {
//                        return String.format("%02d", value);
//                    }
//                });
                hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        if(newVal == 8 && minutePicker.getValue() != 0) {
                            minutePicker.setValue(0);
                        }
                    }
                });
                minutePicker.setMinValue(0);
                minutePicker.setMaxValue(59);
                minutePicker.setValue(Integer.parseInt(minutes_spent.getText().toString()));
                minutePicker.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        if (value < 10)
                            return "0" + value;
                        else
                            return value + "";
                    }
                });
                minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        if(hourPicker.getValue() == 8) {
                            picker.setValue(0);
                        }
                    }
                });
                ((TextView)dialog.findViewById(R.id.timespent_save_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                dialog.findViewById(R.id.timespent_save_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hour = hourPicker.getValue();
//                        if (hour < 10)
//                            hours_spent.setText("0" + hour);
//                        else
                            hours_spent.setText("" + hour);
                        int minute = minutePicker.getValue();
                        if (minute < 10)
                            minutes_spent.setText("0" + minute);
                        else
                            minutes_spent.setText("" + minute);
                        dialog.dismiss();
                    }
                });
                ((TextView)dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);

                dialog.show();

                hourPicker.invalidate();
                break;
            }
            case R.id.log_activity_date: {
                DialogFragment newFragment = new DatePickerFragment();
                ((DatePickerFragment) newFragment).fragment = this;
                newFragment.show(DataManager.getInstance().mainActivity.getSupportFragmentManager(), "datePicker");
                break;
            }
        }
    }
}

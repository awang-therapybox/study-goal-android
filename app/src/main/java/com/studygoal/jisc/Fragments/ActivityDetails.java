package com.studygoal.jisc.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.LinguisticManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.ActivityHistory;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.Models.Trophy;
import com.studygoal.jisc.Models.TrophyMy;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.Utils;

import java.util.HashMap;
import java.util.List;

public class ActivityDetails extends Fragment {

    View mainView;
    public ActivityHistory activityHistory;
    public String title;
    public EditText notes;
    public ImageView save;

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.activity_details));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.activity_activity_details, container, false);

        save = (ImageView) mainView.findViewById(R.id.save);

        Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.getInstance().images.get(activityHistory.activity)).into((ImageView)mainView.findViewById(R.id.activity_icon));

        TextView titleview = (TextView)mainView.findViewById(R.id.activity_details_text);
        titleview.setTypeface(DataManager.getInstance().myriadpro_regular);
        title = LinguisticManager.getInstance().translate(getActivity(), activityHistory.activity) + " " + getActivity().getString(R.string._for) + " " + Utils.getMinutesToHour(activityHistory.time_spent);
        titleview.setText(title);

        ((TextView) mainView.findViewById(R.id.trophies_available_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.my_trophies_text)).setTypeface(DataManager.getInstance().myriadpro_regular);

        final View trophies_my = mainView.findViewById(R.id.trophies_my);
        final View trophies_all = mainView.findViewById(R.id.trophies_all);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<TrophyMy> list = new Select().from(TrophyMy.class).where("activity_name = ?", activityHistory.activity).execute();
                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0; i < list.size(); i++) {
                            View convertView = inflater.inflate(R.layout.activity_details_trophy_item, null, false);
                            TrophyMy trophy = list.get(i);

                            TextView trophy_name = (TextView) convertView.findViewById(R.id.trophy_name);
                            TextView trophy_hours = (TextView) convertView.findViewById(R.id.trophy_hours);
                            trophy_name.setTypeface(DataManager.getInstance().myriadpro_regular);
                            trophy_name.setText(trophy.trophy_name);
                            trophy_hours.setTypeface(DataManager.getInstance().myriadpro_regular);
                            trophy_hours.setText(trophy.count);

                            Glide.with(DataManager.getInstance().mainActivity).load(trophy.getImageDrawable(DataManager.getInstance().mainActivity)).into((ImageView) convertView.findViewById(R.id.image));

                            ((LinearLayout) trophies_my).addView(convertView);
                        }
                    }
                });
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Trophy> list = new Select().from(Trophy.class).where("activity_name = ?", activityHistory.activity).execute();
                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0; i < list.size(); i++) {
                            View convertView = inflater.inflate(R.layout.activity_details_trophy_item, null, false);
                            Trophy trophy = list.get(i);

                            TextView trophy_name = (TextView) convertView.findViewById(R.id.trophy_name);
                            TextView trophy_hours = (TextView) convertView.findViewById(R.id.trophy_hours);
                            trophy_name.setTypeface(DataManager.getInstance().myriadpro_regular);
                            trophy_name.setText(trophy.trophy_name);
                            trophy_hours.setTypeface(DataManager.getInstance().myriadpro_regular);
                            trophy_hours.setText(trophy.count);

                            Glide.with(DataManager.getInstance().mainActivity).load(trophy.getImageDrawable(DataManager.getInstance().mainActivity)).into((ImageView) convertView.findViewById(R.id.image));

                            ((LinearLayout) trophies_all).addView(convertView);
                        }
                    }
                });
            }
        }).start();

        TextView date = (TextView) mainView.findViewById(R.id.activity_details_date);
        notes = (EditText) mainView.findViewById(R.id.activity_details_notes_edittext);

        ((TextView) mainView.findViewById(R.id.edit_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.delete_text)).setTypeface(DataManager.getInstance().myriadpro_regular);

        mainView.findViewById(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogLogActivity fragment = new LogLogActivity();
                fragment.isInEditMode = true;
                fragment.item = activityHistory;
                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        mainView.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
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
                ((TextView) dialog.findViewById(R.id.dialog_message)).setText(R.string.are_you_sure_you_want_to_delete_this_activity_log);

                ((TextView) dialog.findViewById(R.id.dialog_no_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                ((TextView) dialog.findViewById(R.id.dialog_no_text)).setText(R.string.no);

                ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setText(R.string.yes);

                dialog.findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        final HashMap<String, String> params = new HashMap<>();
                        params.put("log_id", activityHistory.id);
                        DataManager.getInstance().mainActivity.showProgressBar(null);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(NetworkManager.getInstance().deleteActivity(params)) {
                                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DataManager.getInstance().mainActivity.hideProgressBar();
                                            DataManager.getInstance().mainActivity.onBackPressed();
                                        }
                                    });
                                }
                                else {
                                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DataManager.getInstance().mainActivity.hideProgressBar();
                                            Snackbar.make(mainView.findViewById(R.id.parent), R.string.fail_to_delete_from_activity_history, Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }).start();
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

        ((TextView) mainView.findViewById(R.id.module_name)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.activitytype_name)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.activity_name)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.timer_name)).setTypeface(DataManager.getInstance().myriadpro_regular);

        TextView module = (TextView) mainView.findViewById(R.id.module_text);
        module.setTypeface(DataManager.getInstance().myriadpro_regular);
        TextView activitytype = (TextView) mainView.findViewById(R.id.activitytype_text);
        activitytype.setTypeface(DataManager.getInstance().myriadpro_regular);
        TextView activity = (TextView) mainView.findViewById(R.id.activity_text);
        activity.setTypeface(DataManager.getInstance().myriadpro_regular);
        TextView timer = (TextView) mainView.findViewById(R.id.timer_text);
        timer.setTypeface(DataManager.getInstance().myriadpro_regular);

        try {
            module.setText(((Module) new Select().from(Module.class).where("module_id = ?", activityHistory.module_id).executeSingle()).name);
        } catch (Exception e) {
            e.printStackTrace();
            module.setText("");
        }
        activitytype.setText(activityHistory.activity_type);
        activity.setText(activityHistory.activity);
        timer.setText((Integer.parseInt(activityHistory.time_spent)/60<10?("0"+ Integer.parseInt(activityHistory.time_spent)/60):(Integer.parseInt(activityHistory.time_spent)/60)) + ":" + (Integer.parseInt(activityHistory.time_spent)%60<10?"0"+ Integer.parseInt(activityHistory.time_spent)%60 : Integer.parseInt(activityHistory.time_spent)%60));

        ((TextView) mainView.findViewById(R.id.activity_details_notes)).setTypeface(DataManager.getInstance().myriadpro_regular);

        date.setTypeface(DataManager.getInstance().myriadpro_regular);
        String str = DataManager.getInstance().mainActivity.getString(R.string.date) + ":" + " " + Utils.getDate(activityHistory.activity_date);
        date.setText(str);

        notes.setTypeface(DataManager.getInstance().myriadpro_regular);

        if (activityHistory.note == null)
            notes.setText("");
        else notes.setText(activityHistory.note);

        notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(save.getVisibility() == View.INVISIBLE)
                    save.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(notes.getWindowToken(), 0);

                if(!activityHistory.note.equals(notes.getText().toString())) {
                    final HashMap<String, String> params = new HashMap<>();
                    params.put("log_id", activityHistory.id);
                    params.put("activity_date", activityHistory.activity_date);
                    params.put("time_spent", activityHistory.time_spent);
                    params.put("note", notes.getText().toString());
                    params.put("student_id", DataManager.getInstance().user.id);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (NetworkManager.getInstance().editActivity(params)) {
                                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        activityHistory.note = notes.getText().toString();
                                        activityHistory.save();
                                        Snackbar.make(mainView.findViewById(R.id.parent), R.string.saved_successfully, Snackbar.LENGTH_LONG).show();
                                        save.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }
                    }).start();
                } else {
                    save.setVisibility(View.INVISIBLE);
                }
            }
        });

        return mainView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(save.getVisibility() == View.VISIBLE) {
            if (!activityHistory.note.equals(notes.getText().toString())) {
                final HashMap<String, String> params = new HashMap<>();
                params.put("log_id", activityHistory.id);
                params.put("activity_date", activityHistory.activity_date);
                params.put("time_spent", activityHistory.time_spent);
                params.put("note", notes.getText().toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (NetworkManager.getInstance().editActivity(params)) {
                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    activityHistory.note = notes.getText().toString();
                                    activityHistory.save();
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }
}

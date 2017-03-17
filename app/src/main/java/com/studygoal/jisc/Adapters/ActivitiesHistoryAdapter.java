package com.studygoal.jisc.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.studygoal.jisc.Fragments.LogActivityHistory;
import com.studygoal.jisc.Fragments.LogLogActivity;
import com.studygoal.jisc.Fragments.LogNewActivity;
import com.studygoal.jisc.MainActivity;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.LinguisticManager;
import com.studygoal.jisc.Models.ActivityHistory;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.Models.RunningActivity;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.Utils;

import java.util.List;

public class ActivitiesHistoryAdapter extends BaseAdapter {

    public List<ActivityHistory> historyList;
    LayoutInflater inflater;
    Context context;
    LogActivityHistory fragment;
    Boolean hasRunning = false;

    public ActivitiesHistoryAdapter(LogActivityHistory fragment) {

        this.fragment = fragment;
        this.context = fragment.getActivity();

        historyList = new Select().from(ActivityHistory.class).where("student_id=?", DataManager.getInstance().user.id).orderBy("activity_date DESC").execute();

        inflater = LayoutInflater.from(context);
        checkRunning();
    }

    @Override
    public void notifyDataSetChanged() {

        checkRunning();
        super.notifyDataSetChanged();
    }

    private void checkRunning() {
        if (new Select().from(RunningActivity.class).count() > 0) {
            SharedPreferences saves = context.getSharedPreferences("jisc", Context.MODE_PRIVATE);
            Long timestamp = saves.getLong("timer", 0);
            Long pause;
            Long timespent;
            if(saves.contains("pause")) {
                if(saves.getLong("pause", 0) > 0) {
                    pause = saves.getLong("pause", 0);
                    timespent = (pause - timestamp) / 60000;
                } else
                    timespent = (System.currentTimeMillis() - timestamp)/60000;
            } else
                timespent = (System.currentTimeMillis() - timestamp)/60000;
            RunningActivity activity = new Select().from(RunningActivity.class).executeSingle();

            ActivityHistory history = new ActivityHistory();
            history.id = "";
            history.student_id = activity.student_id;
            history.module_id = "";//activity.module_id;
            history.activity_type = activity.activity_type;
            history.activity = activity.activity;
            history.activity_date = activity.activity_date;
            history.time_spent = timespent + "";
            history.note = "";
            history.created_date = "";
            history.modified_date = "";
            hasRunning = true;
            historyList.add(0, history);
        } else {
            hasRunning = false;
        }
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.history_item, parent, false);
        }
        final ActivityHistory activityHistory = historyList.get(position);

        try {
            Glide.with(context).load(LinguisticManager.getInstance().images.get(activityHistory.activity)).into((ImageView) convertView.findViewById(R.id.activity_icon));
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        if (hasRunning) {
            if (position > 0) {
                TextView textView = (TextView) convertView.findViewById(R.id.history_item_text);
                final com.daimajia.swipe.SwipeLayout swipeLayout = (com.daimajia.swipe.SwipeLayout) convertView.findViewById(R.id.swipelayout);
                swipeLayout.setSwipeEnabled(true);
                try {
                    convertView.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            swipeLayout.close(true);
                            LogLogActivity fragment = new LogLogActivity();
                            fragment.isInEditMode = true;
                            fragment.item = activityHistory;
                            ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final int finalPosition = position;
                try {
                    convertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.confirmation_dialog);
                            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            if (DataManager.getInstance().mainActivity.isLandscape) {
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
                                    swipeLayout.close(true);
                                    fragment.deleteLog(activityHistory, finalPosition);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    String text = LinguisticManager.getInstance().verbs.get(activityHistory.activity) + " " + context.getString(R.string._for) + " " + Utils.getMinutesToHour(activityHistory.time_spent); // + " " + context.getString(R.string.for_text) + " " + ((Module) (new Select().from(Module.class).where("module_id=?", activityHistory.module_id).executeSingle())).name + " " + context.getString(R.string.module);
                    textView.setTypeface(DataManager.getInstance().myriadpro_regular);
                    textView.setText(text);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.light_purple_text));
                    convertView.setTag(activityHistory.id + ";" + text);
                } catch (Exception ignored) {
                }
            } else {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_fragment, new LogNewActivity(), "newActivity")
                                .addToBackStack(null)
                                .commit();
                    }
                });
                TextView textView = (TextView) convertView.findViewById(R.id.history_item_text);
                final com.daimajia.swipe.SwipeLayout swipeLayout = (com.daimajia.swipe.SwipeLayout) convertView.findViewById(R.id.swipelayout);
                swipeLayout.setSwipeEnabled(false);

                try {
                    String text = LinguisticManager.getInstance().verbs.get(activityHistory.activity) + " " + context.getString(R.string._for) + " " + Utils.getMinutesToHour(activityHistory.time_spent);// + " " + context.getString(R.string.for_text) + " " + ((Module) (new Select().from(Module.class).where("module_id=?", activityHistory.module_id).executeSingle())).name + " " + context.getString(R.string.module);
                    textView.setTypeface(DataManager.getInstance().myriadpro_regular);
                    textView.setText(text);
                    textView.setTextColor(0xFFFC823B);
                    convertView.setTag(activityHistory.id + ";" + text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            TextView textView = (TextView) convertView.findViewById(R.id.history_item_text);
            final com.daimajia.swipe.SwipeLayout swipeLayout = (com.daimajia.swipe.SwipeLayout) convertView.findViewById(R.id.swipelayout);
            swipeLayout.setSwipeEnabled(true);
            try {
                convertView.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        swipeLayout.close(true);
                        LogLogActivity fragment = new LogLogActivity();
                        fragment.isInEditMode = true;
                        fragment.item = activityHistory;
                        ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_fragment, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            final int finalPosition = position;
            try {
                convertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.confirmation_dialog);
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        if (DataManager.getInstance().mainActivity.isLandscape) {
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
                                swipeLayout.close(true);
                                fragment.deleteLog(activityHistory, finalPosition);
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                Module module = new Select().from(Module.class).where("module_id=?", activityHistory.module_id).executeSingle();

                String text = LinguisticManager.getInstance().translate(context, activityHistory.activity) +
                        " " +
                        context.getString(R.string._for) + " " +
                        Utils.getMinutesToHour(activityHistory.time_spent) + " ";

                if(module != null) {
                    text += context.getString(R.string.for_text) + " " +
                            module.name + " " +
                            context.getString(R.string.module);
                }

                textView.setTypeface(DataManager.getInstance().myriadpro_regular);
                textView.setText(text);
                textView.setTextColor(ContextCompat.getColor(context, R.color.light_purple_text));
                convertView.setTag(activityHistory.id + ";" + text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }
}

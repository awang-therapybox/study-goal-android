package com.studygoal.jisc.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.studygoal.jisc.Fragments.AddTarget;
import com.studygoal.jisc.Fragments.TargetFragment;
import com.studygoal.jisc.MainActivity;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.LinguisticManager;
import com.studygoal.jisc.Models.ActivityHistory;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.Models.Targets;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TargetAdapter extends BaseAdapter {

    Context context;
    public List<Targets> list;
    public TargetFragment fragment;

    public TargetAdapter(TargetFragment fragment) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        list = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list.size();
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
        position = list.size() - 1 - position;
        final Targets item = list.get(position);

        Module module = new Select().from(Module.class).where("module_id = ?", item.module_id).executeSingle();

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.target_fragment_item, parent, false);
        }

        Calendar c = Calendar.getInstance();
        List<ActivityHistory> activityHistoryList;
        if (module != null) {
            activityHistoryList = new Select().from(ActivityHistory.class).where("module_id = ?", item.module_id).and("activity = ?", item.activity).execute();
        } else {
            activityHistoryList = new Select().from(ActivityHistory.class).where("activity = ?", item.activity).execute();
        }

        String current_date = c.get(Calendar.YEAR) + "-";
        current_date += (c.get(Calendar.MONTH) + 1) < 10 ? "0" + (c.get(Calendar.MONTH) + 1) + "-" : (c.get(Calendar.MONTH) + 1) + "-";
        current_date += c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + c.get(Calendar.DAY_OF_MONTH) + " " : c.get(Calendar.DAY_OF_MONTH) + " ";
        current_date += c.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + c.get(Calendar.HOUR_OF_DAY) + ":" : c.get(Calendar.HOUR_OF_DAY) + ":";
        current_date += c.get(Calendar.MINUTE) < 10 ? "0" + c.get(Calendar.MINUTE) + ":" : c.get(Calendar.MINUTE) + ":";
        current_date += c.get(Calendar.SECOND) < 10 ? "0" + c.get(Calendar.SECOND) : c.get(Calendar.SECOND);


        switch (item.time_span.toLowerCase()) {
            case "daily": {
                String time = current_date.split(" ")[0];
                List<ActivityHistory> tmp = new ArrayList<>();
                for (int i = 0; i < activityHistoryList.size(); i++) {
                    if (time.equals(activityHistoryList.get(i).created_date.split(" ")[0]))
                        tmp.add(activityHistoryList.get(i));
                }
                activityHistoryList.clear();
                activityHistoryList.addAll(tmp);
                break;
            }
            case "weekly": {
                List<ActivityHistory> tmp = new ArrayList<>();
                for (int i = 0; i < activityHistoryList.size(); i++) {
                    if (Utils.isInSameWeek(activityHistoryList.get(i).created_date.split(" ")[0]))
                        tmp.add(activityHistoryList.get(i));
                }
                activityHistoryList.clear();
                activityHistoryList.addAll(tmp);
                break;
            }
            case "monthly": {
                String time = current_date.split(" ")[0].split("-")[0] + "-" + current_date.split(" ")[0].split("-")[1];
                List<ActivityHistory> tmp = new ArrayList<>();
                for (int i = 0; i < activityHistoryList.size(); i++) {
                    if (time.equals(activityHistoryList.get(i).created_date.split(" ")[0].split("-")[0] + "-" + activityHistoryList.get(i).created_date.split(" ")[0].split("-")[1]))
                        tmp.add(activityHistoryList.get(i));
                }
                activityHistoryList.clear();
                activityHistoryList.addAll(tmp);
                break;
            }
        }

        int neccesary_time = Integer.parseInt(item.total_time);

        int spent_time = 0;
        for (int i = 0; i < activityHistoryList.size(); i++) {
            spent_time += Integer.parseInt(activityHistoryList.get(i).time_spent);
        }
        if (spent_time == 0)
            convertView.findViewById(R.id.colorbar).setBackgroundColor(0xFFFF0000);
        else if (spent_time >= neccesary_time)
            convertView.findViewById(R.id.colorbar).setBackgroundColor(0xFF00FF00);
        else
            convertView.findViewById(R.id.colorbar).setBackgroundColor(0xFFff7400);

        try {
            Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.getInstance().images.get(item.activity)).into((ImageView) convertView.findViewById(R.id.activity_icon));
        } catch (Exception e) {
        }

        TextView textView = (TextView) convertView.findViewById(R.id.target_item_text);
        textView.setTypeface(DataManager.getInstance().myriadpro_regular);

        String text = "";
        text += LinguisticManager.getInstance().present.get(item.activity) + " ";
        int hour = Integer.parseInt(item.total_time) / 60;
        int minute = Integer.parseInt(item.total_time) % 60;
        text += (hour == 1) ? "1 " + context.getString(R.string.hour) : hour + " " + context.getString(R.string.hours) + " ";
        if (minute > 0)
            text += ((minute == 1) ? " " + context.getString(R.string.and) + " 1 " + context.getString(R.string.minute) + " " : " " + context.getString(R.string.and) + " " + minute + " " + context.getString(R.string.minutes) + " ");

        if(item.time_span.length() > 0)
            text += item.time_span.toLowerCase();

        if(module != null && module.name.length() > 0) {
            text += " " + context.getString(R.string._for) + " " + module.name;
        }

        textView.setText(text);

        final com.daimajia.swipe.SwipeLayout swipeLayout = (com.daimajia.swipe.SwipeLayout) convertView.findViewById(R.id.swipelayout);
        convertView.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.close(true);
                AddTarget fragment = new AddTarget();
                fragment.isInEditMode = true;
                fragment.item = item;
                ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        final int finalPosition = position;
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
                        fragment.deleteTarget(item, finalPosition);
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

        convertView.setTag(item.target_id);
        return convertView;
    }
}

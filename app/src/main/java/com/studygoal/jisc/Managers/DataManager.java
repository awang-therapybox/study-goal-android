package com.studygoal.jisc.Managers;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.studygoal.jisc.MainActivity;
import com.studygoal.jisc.Models.CurrentUser;
import com.studygoal.jisc.Models.TrophyMy;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class DataManager {

    public static boolean isTestBuild = false;

    public Context context;
    public CurrentUser user;

    private static DataManager ourInstance = new DataManager();
    public Typeface myriadpro_regular;
    public Typeface myriadpro_bold;
    public Typeface oratorstd_typeface;

    public ArrayList<String> activity_type;
    public HashMap<String, ArrayList<String>> choose_activity;
    public HashMap<String, String> api_values;
    public HashMap<String, String> display_values;
    public ArrayList<String> period;

    public String home_screen;
    public String language;

    public NotificationManager mNotificationManager;
    public MainActivity mainActivity;

    public boolean isLandscape;
    public boolean fromTargetItem = false;
    public int addTarget;

    public String guid;
    private String jwt;
    public Integer fragment;
    public String institution;
    public android.app.Activity currActivity;
    public boolean toast = false;
    public boolean checkForbidden = false;

    public String selfie_url;

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    public String get_jwt() {
        return jwt;
    }

    public void set_jwt(String jwt) {
        this.jwt = jwt;

    }

    public void reload() {
        LinguisticManager.getInstance().reload(context);

        activity_type.clear();
        choose_activity.clear();
        period.clear();
        api_values.clear();
        display_values.clear();
        init();
    }
    public void init() {
        api_values = new HashMap<>();
        display_values = new HashMap<>();

        activity_type = new ArrayList<>();
        activity_type.add(context.getString(R.string.studying_arts));
        activity_type.add(context.getString(R.string.studying_science));
        activity_type.add(context.getString(R.string.coursework_exams));
        activity_type.add(context.getString(R.string.attending));

        api_values.put(context.getString(R.string.studying_arts), "Studying (arts)");
        api_values.put(context.getString(R.string.studying_science), "Studying (science)");
        api_values.put(context.getString(R.string.coursework_exams), "Coursework/Exams");
        api_values.put(context.getString(R.string.attending), "Attending");

        choose_activity = new HashMap<>();
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add(context.getString(R.string.reading));
        api_values.put(context.getString(R.string.reading), "Reading");
        tmp.add(context.getString(R.string.writing));
        api_values.put(context.getString(R.string.writing), "Writing");
        tmp.add(context.getString(R.string.research));
        api_values.put(context.getString(R.string.research), "Research");
        tmp.add(context.getString(R.string.in_group_study));
        api_values.put(context.getString(R.string.in_group_study), "Group Study");
        tmp.add(context.getString(R.string.designing));
        api_values.put(context.getString(R.string.designing), "Designing");
        tmp.add(context.getString(R.string.presenting));
        api_values.put(context.getString(R.string.presenting), "Presenting");
        tmp.add(context.getString(R.string.blogging));
        api_values.put(context.getString(R.string.blogging), "Blogging");
        tmp.add(context.getString(R.string.revising));
        api_values.put(context.getString(R.string.revising), "Revising");
        tmp.add(context.getString(R.string.practicing));
        api_values.put(context.getString(R.string.practicing), "Practicing");
        choose_activity.put(context.getString(R.string.studying_arts), tmp);

        tmp = new ArrayList<>();
        tmp.add(context.getString(R.string.reading));
        tmp.add(context.getString(R.string.writing));
        tmp.add(context.getString(R.string.research));
        tmp.add(context.getString(R.string.in_group_study));
        tmp.add(context.getString(R.string.experimenting));
        api_values.put(context.getString(R.string.experimenting), "Experimenting");
        tmp.add(context.getString(R.string.presenting));
        tmp.add(context.getString(R.string.blogging));
        tmp.add(context.getString(R.string.revising));
        choose_activity.put(context.getString(R.string.studying_science), tmp);

        tmp = new ArrayList<>();
        tmp.add(context.getString(R.string.completing_assignment));
        api_values.put(context.getString(R.string.completing_assignment), "Completing Assignment");
        tmp.add(context.getString(R.string.in_an_exam));
        api_values.put(context.getString(R.string.in_an_exam), "In an exam");
        tmp.add(context.getString(R.string.preparing_a_dissertation));
        api_values.put(context.getString(R.string.preparing_a_dissertation), "Preparing a dissertation");
        tmp.add(context.getString(R.string.revising));
        choose_activity.put(context.getString(R.string.coursework_exams), tmp);

        tmp = new ArrayList<>();
        tmp.add(context.getString(R.string.attending_lectures));
        api_values.put(context.getString(R.string.attending_lectures), "Attending Lectures");
        tmp.add(context.getString(R.string.attending_seminars));
        api_values.put(context.getString(R.string.attending_seminars), "Attending Seminars");
        tmp.add(context.getString(R.string.attending_tutorials));
        api_values.put(context.getString(R.string.attending_tutorials), "Attending Tutorials");
        tmp.add(context.getString(R.string.attending_labs));
        api_values.put(context.getString(R.string.attending_labs), "Attending Labs");
        choose_activity.put(context.getString(R.string.attending), tmp);

        period = new ArrayList<>();
        period.add(context.getString(R.string.day));
        api_values.put(context.getString(R.string.daily).toLowerCase(), "Daily");
        period.add(context.getString(R.string.week_week));
        api_values.put(context.getString(R.string.Weekly).toLowerCase(), "Weekly");
        period.add(context.getString(R.string.month));
        api_values.put(context.getString(R.string.monthly).toLowerCase(), "Monthly");

        api_values.put("Sunday", context.getString(R.string.sunday));
        api_values.put("Monday", context.getString(R.string.monday));
        api_values.put("Tuesday", context.getString(R.string.tuesday));
        api_values.put("Wednesday", context.getString(R.string.wednesday));
        api_values.put("Thursday", context.getString(R.string.thursday));
        api_values.put("Friday", context.getString(R.string.friday));
        api_values.put("Saturday", context.getString(R.string.saturday));

        api_values.put("Sun", context.getString(R.string.sun));
        api_values.put("Mon", context.getString(R.string.mon));
        api_values.put("Tue", context.getString(R.string.tue));
        api_values.put("Wed", context.getString(R.string.wed));
        api_values.put("Thu", context.getString(R.string.thu));
        api_values.put("Fri", context.getString(R.string.fri));
        api_values.put("Sat", context.getString(R.string.sat));


        api_values.put(context.getString(R.string.last_24_hours).toLowerCase(), "24h");
        api_values.put(context.getString(R.string.last_7_days).toLowerCase(), "7d");
        api_values.put(context.getString(R.string.last_30_days).toLowerCase(), "28d");
        api_values.put(context.getString(R.string.Overall).toLowerCase(), "overall");
    }

    public void loadFonts() {
        oratorstd_typeface = Typeface.createFromAsset(context.getAssets(), "fonts/oratorstd.ttf");
        myriadpro_bold = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Bold.ttf");
        myriadpro_regular = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Regular.ttf");
    }

    public void showTrophyNotification(final TrophyMy trophyMy) {
        if(DataManager.getInstance().mainActivity != null)
            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.trophy_notification);
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    TextView title = (TextView)dialog.findViewById(R.id.dialog_title);
                    title.setTypeface(DataManager.getInstance().oratorstd_typeface);
                    title.setText(DataManager.getInstance().mainActivity.getString(R.string.trophy));

                    TextView message = (TextView) dialog.findViewById(R.id.dialog_message);
                    message.setTypeface(DataManager.getInstance().myriadpro_regular);
                    message.setText(DataManager.getInstance().mainActivity.getString(R.string.you_have_earned) + " " + trophyMy.trophy_name);

                    Glide.with(DataManager.getInstance().mainActivity).load(DataManager.getInstance().mainActivity.getResources().getIdentifier(trophyMy.getImageName(), "drawable", DataManager.getInstance().mainActivity.getPackageName())).into((ImageView)dialog.findViewById(R.id.trophy_image));
                    dialog.show();
                }
            });
    }
}

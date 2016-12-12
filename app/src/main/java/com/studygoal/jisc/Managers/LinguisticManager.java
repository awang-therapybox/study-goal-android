package com.studygoal.jisc.Managers;

import android.content.Context;

import com.studygoal.jisc.R;

import java.util.Calendar;
import java.util.HashMap;

public class LinguisticManager {

    public HashMap<String, String> verbs;
    public HashMap<String, String> present;
    public HashMap<String, Integer> images;

    private static LinguisticManager ourInstance = new LinguisticManager();

    public static LinguisticManager getInstance() {
        return ourInstance;
    }

    public String translate(Context con, String value) {
        return con.getResources().getString(con.getResources().getIdentifier(value.toLowerCase().replace(" ", "_"), "string", con.getPackageName()));
    }

    public void reload(Context con) {
        verbs = new HashMap<>();
        verbs.put(con.getString(R.string.reading), con.getString(R.string.read));
        verbs.put(con.getString(R.string.writing), con.getString(R.string.wrote));
        verbs.put(con.getString(R.string.researching), con.getString(R.string.researched));
        verbs.put(con.getString(R.string.research), con.getString(R.string.researched));
        verbs.put(con.getString(R.string.in_group_study), con.getString(R.string.studied));
        verbs.put(con.getString(R.string.group_study), con.getString(R.string.studied));
        verbs.put(con.getString(R.string.designing), con.getString(R.string.designed));
        verbs.put(con.getString(R.string.presenting), con.getString(R.string.presented));
        verbs.put(con.getString(R.string.blogging), con.getString(R.string.blogged));
        verbs.put(con.getString(R.string.revising), con.getString(R.string.revised));
        verbs.put(con.getString(R.string.practicing), con.getString(R.string.practiced));
        verbs.put(con.getString(R.string.experimenting), con.getString(R.string.experimented));

        verbs.put(con.getString(R.string.completing_assignment), con.getString(R.string.completed_assignment));
        verbs.put(con.getString(R.string.in_an_exam), con.getString(R.string.in_an_exam_past));
        verbs.put(con.getString(R.string.in_an_exam_2), con.getString(R.string.in_an_exam_past_2));
        verbs.put(con.getString(R.string.preparing_a_dissertation), con.getString(R.string.prepared_a_dissertation));

        verbs.put(con.getString(R.string.attending_lectures), con.getString(R.string.attended_lectures));
        verbs.put(con.getString(R.string.attending_seminars), con.getString(R.string.attended_seminars));
        verbs.put(con.getString(R.string.attending_tutorials), con.getString(R.string.attended_tutorials));
        verbs.put(con.getString(R.string.attending_labs), con.getString(R.string.attended_labs));

        present = new HashMap<>();
        present.put(con.getString(R.string.reading), con.getString(R.string.read));
        present.put(con.getString(R.string.writing), con.getString(R.string.write));
        present.put(con.getString(R.string.researching), con.getString(R.string.research));
        present.put(con.getString(R.string.research), con.getString(R.string.research));
        present.put(con.getString(R.string.in_group_study), con.getString(R.string.in_group_study_past));
        present.put(con.getString(R.string.group_study), con.getString(R.string.study));
        present.put(con.getString(R.string.group_study), con.getString(R.string.study));
        present.put(con.getString(R.string.designing), con.getString(R.string.design));
        present.put(con.getString(R.string.presenting), con.getString(R.string.present));
        present.put(con.getString(R.string.blogging), con.getString(R.string.blog));
        present.put(con.getString(R.string.revising), con.getString(R.string.revise));
        present.put(con.getString(R.string.practicing), con.getString(R.string.practice));
        present.put(con.getString(R.string.experimenting), con.getString(R.string.experiment));

        present.put(con.getString(R.string.completing_assignment), con.getString(R.string.complete_assignment));
        present.put(con.getString(R.string.in_an_exam), con.getString(R.string.in_an_exam_past));
        present.put(con.getString(R.string.in_an_exam_2), con.getString(R.string.in_an_exam_past_2));
        present.put(con.getString(R.string.preparing_a_dissertation), con.getString(R.string.prepare_a_dissertation));

        present.put(con.getString(R.string.attending_lectures), con.getString(R.string.attend_lectures));
        present.put(con.getString(R.string.attending_seminars), con.getString(R.string.attend_seminars));
        present.put(con.getString(R.string.attending_tutorials), con.getString(R.string.attend_tutorials));
        present.put(con.getString(R.string.attending_labs), con.getString(R.string.attend_labs));

        images = new HashMap<>();
        images.put(con.getString(R.string.reading), R.drawable.activity_icon_1);
        images.put(con.getString(R.string.writing), R.drawable.activity_icon_2);
        images.put(con.getString(R.string.researching), R.drawable.activity_icon_3);
        images.put(con.getString(R.string.research), R.drawable.activity_icon_3);
        images.put(con.getString(R.string.in_group_study), R.drawable.activity_icon_4);
        images.put(con.getString(R.string.group_study), R.drawable.activity_icon_4);
        images.put(con.getString(R.string.designing), R.drawable.activity_icon_5);
        images.put(con.getString(R.string.presenting), R.drawable.activity_icon_6);
        images.put(con.getString(R.string.blogging), R.drawable.activity_icon_7);
        images.put(con.getString(R.string.revising), R.drawable.activity_icon_8);
        images.put(con.getString(R.string.practicing), R.drawable.activity_icon_9);
        images.put(con.getString(R.string.experimenting), R.drawable.activity_icon_10);

        images.put(con.getString(R.string.completing_assignment), R.drawable.activity_icon_11);
        images.put(con.getString(R.string.in_an_exam), R.drawable.activity_icon_12);
        images.put(con.getString(R.string.in_an_exam_2), R.drawable.activity_icon_12);
        images.put(con.getString(R.string.preparing_a_dissertation), R.drawable.activity_icon_13);

        images.put(con.getString(R.string.attending_lectures), R.drawable.activity_icon_14);
        images.put(con.getString(R.string.attending_seminars), R.drawable.activity_icon_15);
        images.put(con.getString(R.string.attending_tutorials), R.drawable.activity_icon_16);
        images.put(con.getString(R.string.attending_labs), R.drawable.activity_icon_17);

        images.put("Reading", R.drawable.activity_icon_1);
        images.put("Writing", R.drawable.activity_icon_2);
        images.put("Researching", R.drawable.activity_icon_3);
        images.put("Research", R.drawable.activity_icon_3);
        images.put("In Group Study", R.drawable.activity_icon_4);
        images.put("Group Study", R.drawable.activity_icon_4);
        images.put("Designing", R.drawable.activity_icon_5);
        images.put("Presenting", R.drawable.activity_icon_6);
        images.put("Blogging", R.drawable.activity_icon_7);
        images.put("Revising", R.drawable.activity_icon_8);
        images.put("Practicing", R.drawable.activity_icon_9);
        images.put("Experimenting", R.drawable.activity_icon_10);

        images.put("Completing Assignment", R.drawable.activity_icon_11);
        images.put("In an Exam", R.drawable.activity_icon_12);
        images.put("In an exam", R.drawable.activity_icon_12);
        images.put("Preparing a dissertation", R.drawable.activity_icon_13);

        images.put("Attending Lectures", R.drawable.activity_icon_14);
        images.put("Attending Seminars", R.drawable.activity_icon_15);
        images.put("Attending Tutorials", R.drawable.activity_icon_16);
        images.put("Attending Labs", R.drawable.activity_icon_17);
    }
    private LinguisticManager() {
        Context con  = DataManager.getInstance().context;

        verbs = new HashMap<>();
        verbs.put(con.getString(R.string.reading), con.getString(R.string.read));
        verbs.put(con.getString(R.string.writing), con.getString(R.string.wrote));
        verbs.put(con.getString(R.string.researching), con.getString(R.string.researched));
        verbs.put(con.getString(R.string.research), con.getString(R.string.researched));
        verbs.put(con.getString(R.string.in_group_study), con.getString(R.string.studied));
        verbs.put(con.getString(R.string.group_study), con.getString(R.string.studied));
        verbs.put(con.getString(R.string.designing), con.getString(R.string.designed));
        verbs.put(con.getString(R.string.presenting), con.getString(R.string.presented));
        verbs.put(con.getString(R.string.blogging), con.getString(R.string.blogged));
        verbs.put(con.getString(R.string.revising), con.getString(R.string.revised));
        verbs.put(con.getString(R.string.practicing), con.getString(R.string.practiced));
        verbs.put(con.getString(R.string.experimenting), con.getString(R.string.experimented));

        verbs.put(con.getString(R.string.completing_assignment), con.getString(R.string.completed_assignment));
        verbs.put(con.getString(R.string.in_an_exam), con.getString(R.string.in_an_exam_past));
        verbs.put(con.getString(R.string.in_an_exam_2), con.getString(R.string.in_an_exam_past_2));
        verbs.put(con.getString(R.string.preparing_a_dissertation), con.getString(R.string.prepared_a_dissertation));

        verbs.put(con.getString(R.string.attending_lectures), con.getString(R.string.attended_lectures));
        verbs.put(con.getString(R.string.attending_seminars), con.getString(R.string.attended_seminars));
        verbs.put(con.getString(R.string.attending_tutorials), con.getString(R.string.attended_tutorials));
        verbs.put(con.getString(R.string.attending_labs), con.getString(R.string.attended_labs));

        present = new HashMap<>();
        present.put(con.getString(R.string.reading), con.getString(R.string.read));
        present.put(con.getString(R.string.writing), con.getString(R.string.write));
        present.put(con.getString(R.string.researching), con.getString(R.string.research));
        present.put(con.getString(R.string.research), con.getString(R.string.research));
        present.put(con.getString(R.string.in_group_study), con.getString(R.string.in_group_study_past));
        present.put(con.getString(R.string.group_study), con.getString(R.string.study));
        present.put(con.getString(R.string.group_study), con.getString(R.string.study));
        present.put(con.getString(R.string.designing), con.getString(R.string.design));
        present.put(con.getString(R.string.presenting), con.getString(R.string.present));
        present.put(con.getString(R.string.blogging), con.getString(R.string.blog));
        present.put(con.getString(R.string.revising), con.getString(R.string.revise));
        present.put(con.getString(R.string.practicing), con.getString(R.string.practice));
        present.put(con.getString(R.string.experimenting), con.getString(R.string.experiment));

        present.put(con.getString(R.string.completing_assignment), con.getString(R.string.complete_assignment));
        present.put(con.getString(R.string.in_an_exam), con.getString(R.string.in_an_exam_past));
        present.put(con.getString(R.string.in_an_exam_2), con.getString(R.string.in_an_exam_past_2));
        present.put(con.getString(R.string.preparing_a_dissertation), con.getString(R.string.prepare_a_dissertation));

        present.put(con.getString(R.string.attending_lectures), con.getString(R.string.attend_lectures));
        present.put(con.getString(R.string.attending_seminars), con.getString(R.string.attend_seminars));
        present.put(con.getString(R.string.attending_tutorials), con.getString(R.string.attend_tutorials));
        present.put(con.getString(R.string.attending_labs), con.getString(R.string.attend_labs));

        images = new HashMap<>();
        images.put(con.getString(R.string.reading), R.drawable.activity_icon_1);
        images.put(con.getString(R.string.writing), R.drawable.activity_icon_2);
        images.put(con.getString(R.string.researching), R.drawable.activity_icon_3);
        images.put(con.getString(R.string.research), R.drawable.activity_icon_3);
        images.put(con.getString(R.string.in_group_study), R.drawable.activity_icon_4);
        images.put(con.getString(R.string.group_study), R.drawable.activity_icon_4);
        images.put(con.getString(R.string.designing), R.drawable.activity_icon_5);
        images.put(con.getString(R.string.presenting), R.drawable.activity_icon_6);
        images.put(con.getString(R.string.blogging), R.drawable.activity_icon_7);
        images.put(con.getString(R.string.revising), R.drawable.activity_icon_8);
        images.put(con.getString(R.string.practicing), R.drawable.activity_icon_9);
        images.put(con.getString(R.string.experimenting), R.drawable.activity_icon_10);

        images.put(con.getString(R.string.completing_assignment), R.drawable.activity_icon_11);
        images.put(con.getString(R.string.in_an_exam), R.drawable.activity_icon_12);
        images.put(con.getString(R.string.in_an_exam_2), R.drawable.activity_icon_12);
        images.put(con.getString(R.string.preparing_a_dissertation), R.drawable.activity_icon_13);

        images.put(con.getString(R.string.attending_lectures), R.drawable.activity_icon_14);
        images.put(con.getString(R.string.attending_seminars), R.drawable.activity_icon_15);
        images.put(con.getString(R.string.attending_tutorials), R.drawable.activity_icon_16);
        images.put(con.getString(R.string.attending_labs), R.drawable.activity_icon_17);

        images.put("Reading", R.drawable.activity_icon_1);
        images.put("Writing", R.drawable.activity_icon_2);
        images.put("Researching", R.drawable.activity_icon_3);
        images.put("Research", R.drawable.activity_icon_3);
        images.put("In Group Study", R.drawable.activity_icon_4);
        images.put("Group Study", R.drawable.activity_icon_4);
        images.put("Designing", R.drawable.activity_icon_5);
        images.put("Presenting", R.drawable.activity_icon_6);
        images.put("Blogging", R.drawable.activity_icon_7);
        images.put("Revising", R.drawable.activity_icon_8);
        images.put("Practicing", R.drawable.activity_icon_9);
        images.put("Experimenting", R.drawable.activity_icon_10);

        images.put("Completing Assignment", R.drawable.activity_icon_11);
        images.put("In an Exam", R.drawable.activity_icon_12);
        images.put("In an exam", R.drawable.activity_icon_12);
        images.put("Preparing a dissertation", R.drawable.activity_icon_13);

        images.put("Attending Lectures", R.drawable.activity_icon_14);
        images.put("Attending Seminars", R.drawable.activity_icon_15);
        images.put("Attending Tutorials", R.drawable.activity_icon_16);
        images.put("Attending Labs", R.drawable.activity_icon_17);
    }

    public String convertMonth(String date)
    {
        int date_int = Integer.parseInt(date);
        switch(date_int)
        {
            case 1:
                return DataManager.getInstance().context.getString(R.string.jan);
            case 2:
                return DataManager.getInstance().context.getString(R.string.feb);
            case 3:
                return DataManager.getInstance().context.getString(R.string.mar);
            case 4:
                return DataManager.getInstance().context.getString(R.string.apr);
            case 5:
                return DataManager.getInstance().context.getString(R.string.may);
            case 6:
                return DataManager.getInstance().context.getString(R.string.jun);
            case 7:
                return DataManager.getInstance().context.getString(R.string.jul);
            case 8:
                return DataManager.getInstance().context.getString(R.string.aug);
            case 9:
                return DataManager.getInstance().context.getString(R.string.sep);
            case 10:
                return DataManager.getInstance().context.getString(R.string.oct);
            case 11:
                return DataManager.getInstance().context.getString(R.string.nov);
            case 12:
                return DataManager.getInstance().context.getString(R.string.dec);
            default:
                return "";
        }
    }

    public String getShortWeekDay(int weekday) {
        String result = "";
        switch (weekday) {
            case Calendar.MONDAY: {
                result += DataManager.getInstance().context.getString(R.string.mon);
                break;
            }
            case Calendar.TUESDAY: {
                result += DataManager.getInstance().context.getString(R.string.tue);
                break;
            }
            case Calendar.WEDNESDAY: {
                result += DataManager.getInstance().context.getString(R.string.wed);
                break;
            }
            case Calendar.THURSDAY: {
                result += DataManager.getInstance().context.getString(R.string.thu);
                break;
            }
            case Calendar.FRIDAY: {
                result += DataManager.getInstance().context.getString(R.string.fri);
                break;
            }
            case Calendar.SATURDAY: {
                result += DataManager.getInstance().context.getString(R.string.sat);
                break;
            }
            case Calendar.SUNDAY: {
                result += DataManager.getInstance().context.getString(R.string.sun);
                break;
            }
        }
        return result;
    }
    public String getWeekDay(int weekday) {
        String result = "";
        switch (weekday) {
            case Calendar.MONDAY: {
                result += DataManager.getInstance().context.getString(R.string.monday);
                break;
            }
            case Calendar.TUESDAY: {
                result += DataManager.getInstance().context.getString(R.string.tuesday);
                break;
            }
            case Calendar.WEDNESDAY: {
                result += DataManager.getInstance().context.getString(R.string.wednesday);
                break;
            }
            case Calendar.THURSDAY: {
                result += DataManager.getInstance().context.getString(R.string.thursday);
                break;
            }
            case Calendar.FRIDAY: {
                result += DataManager.getInstance().context.getString(R.string.friday);
                break;
            }
            case Calendar.SATURDAY: {
                result += DataManager.getInstance().context.getString(R.string.saturday);
                break;
            }
            case Calendar.SUNDAY: {
                result += DataManager.getInstance().context.getString(R.string.sunday);
                break;
            }
        }
        return result;
    }

    public static int rankingImage(String number) {
        if(number.equals("undefined")) number = "0";
        int _number = Integer.parseInt(number);
        if(_number >= 70)
            return R.drawable.lowest;
        if(_number < 70 && _number >= 50)
            return R.drawable.low;
        if(_number < 50 && _number >= 30)
            return R.drawable.middle;
        if(_number < 30 && _number >= 10)
            return R.drawable.high;
        if(_number < 10)
            return R.drawable.highest;
        return R.drawable.lowest;
    }

    public static String convertRanking(String number) {
        if(number.equals("undefined")) number = "0";
        int _number = Integer.parseInt(number);
        if(_number >= 70)
            return DataManager.getInstance().mainActivity.getString(R.string.lowest_engagement);
        if(_number < 70 && _number >= 50)
            return DataManager.getInstance().mainActivity.getString(R.string.low_engagement);
        if(_number < 50 && _number >= 30)
            return DataManager.getInstance().mainActivity.getString(R.string.middle_engagement);
        if(_number < 30 && _number >= 10)
            return DataManager.getInstance().mainActivity.getString(R.string.high_engagement);
        if(_number < 10)
            return DataManager.getInstance().mainActivity.getString(R.string.very_high_engagement);
        return "";
    }

    public String getLanguageCode() {
        if(DataManager.getInstance().language == null) return "en";
        if(DataManager.getInstance().language.toLowerCase().equals("english")) return "en";
        if(DataManager.getInstance().language.toLowerCase().equals("welsh")) return "cy";
        return "en";
    }
}

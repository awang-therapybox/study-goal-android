package com.studygoal.jisc.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.LinguisticManager;
import com.studygoal.jisc.R;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Utils {

    public static boolean validate_email(String email){
        if(email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"))
        {
            return true;
        }
        return false;
    }

    public static String getStringBetween(String from, String to, String original)
    {
        if(original.contains(from))
        {
            String[] _aux = original.split(from);
            if(_aux[1].contains(to))
            {
                String[] _aux2 = _aux[1].split(to);
                if(_aux2[0] != null) {
                    return _aux2[0];
                }
                else
                {
                    return "";
                }
            }
            else
            {
                return "";
            }
        }
        else
        {
            return "";
        }
    }

    public static String calculate_distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        Location locationA = new Location("gps");

        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);

        Location locationB = new Location("gps");

        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);

        DecimalFormat df = new DecimalFormat("#.#");

        if(unit.equals("m"))
            return df.format(locationA.distanceTo(locationB));
        else
            return df.format((locationA.distanceTo(locationB)/1000));
    }

    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException ignored) {
        }
        return null;
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }



    public static String getMinutesToHour(String time_spent) {
        Long minutes = Long.parseLong(time_spent);
        Long hours = minutes/60;
        minutes = minutes % 60;
        if(hours == 0 && minutes == 0)
            return DataManager.getInstance().context.getString(R.string.less_than_1_minute);
        if(hours == 0 && minutes == 1)
            return "1 " + DataManager.getInstance().context.getString(R.string.minute);
        if(hours == 0 && minutes > 1)
            return minutes + " " + DataManager.getInstance().context.getString(R.string.minutes);
        if(hours == 1 && minutes == 0)
            return "1 " + DataManager.getInstance().context.getString(R.string.hour);
        if(hours == 1 && minutes == 1)
            return "1 " + DataManager.getInstance().context.getString(R.string.hour) + " " + DataManager.getInstance().context.getString(R.string.and) + " " + minutes + " " + DataManager.getInstance().context.getString(R.string.minute);
        if(hours == 1 && minutes > 1)
            return "1 " + DataManager.getInstance().context.getString(R.string.hour) + " " + DataManager.getInstance().context.getString(R.string.and) + " " + minutes + " " + DataManager.getInstance().context.getString(R.string.minutes);
        if(hours > 1 && minutes == 0)
            return hours + " " + DataManager.getInstance().context.getString(R.string.hours);
        if(hours > 1 && minutes == 1)
            return hours + " " + DataManager.getInstance().context.getString(R.string.hours) + " " + DataManager.getInstance().context.getString(R.string.and) + " 1 " + DataManager.getInstance().context.getString(R.string.minute);
        else
            return hours + " " + DataManager.getInstance().context.getString(R.string.hours) + " " + DataManager.getInstance().context.getString(R.string.and) + " " + minutes + " " + DataManager.getInstance().context.getString(R.string.minutes);

    }

    public static String getDate(String activity_date) {
        String[] split = activity_date.split("-");

        return split[2] + " " + LinguisticManager.getInstance().convertMonth(split[1]) + " " + split[0];
    }

    public static String getTime(String created_date) {
        String[] split = created_date.split(" ")[1].split(":");
        return split[0] + ":" + split[1];
    }

    public static String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        String[] suffixes =
                {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
                        "th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
                        "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
                        "th", "st"};

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d");

        String result = dateFormat.format(calendar.getTime()) + suffixes[day] + " ";
        dateFormat = new SimpleDateFormat("MMMM yyyy");
        result += dateFormat.format(calendar.getTime());

        return result;
    }



    public static boolean isInSameWeek(String s) {
        Calendar c = Calendar.getInstance();
        Long current_date_in_ms =  c.getTimeInMillis();
        ArrayList<String> dates = new ArrayList<>();

        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY: {
                dates.add(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                break;
            }
            case Calendar.MONDAY: {
                dates.add(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                c.setTimeInMillis(current_date_in_ms - 86400000);
                dates.add(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                break;
            }
            case Calendar.TUESDAY: {
                dates.add(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                for(int i = 1; i < Calendar.TUESDAY; i++) {
                    c.setTimeInMillis(current_date_in_ms -= 86400000);
                    dates.add(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                }
                break;
            }
            case Calendar.WEDNESDAY: {
                dates.add(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                for(int i = 1; i < Calendar.WEDNESDAY; i++) {
                    c.setTimeInMillis(current_date_in_ms -= 86400000);
                    dates.add(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                }
                break;
            }
            case Calendar.THURSDAY: {
                dates.add(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                for(int i = 1; i < Calendar.THURSDAY; i++) {
                    c.setTimeInMillis(current_date_in_ms -= 86400000);
                    dates.add(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                }
                break;
            }
            case Calendar.FRIDAY: {
                dates.add(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                for(int i = 1; i < Calendar.FRIDAY; i++) {
                    c.setTimeInMillis(current_date_in_ms -= 86400000);
                    dates.add(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                }
                break;
            }
            case Calendar.SATURDAY: {
                dates.add(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                for(int i = 1; i < Calendar.SATURDAY; i++) {
                    c.setTimeInMillis(current_date_in_ms -= 86400000);
                    dates.add(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                }
                break;
            }
        }
        s = Integer.parseInt(s.split("-")[0]) + "-" + Integer.parseInt(s.split("-")[1]) + "-" + Integer.parseInt(s.split("-")[2]);
        return dates.contains(s);
    }

    public static String convertToHour(int neccesary_time) {
        String hour = neccesary_time / 60 + "";
        neccesary_time = neccesary_time % 60;
        if(neccesary_time == 0) return hour;

        hour += ".";
        String tmp = ((float)neccesary_time/60 + "").split("\\.")[1];
        if(tmp.length() > 2) hour += tmp.substring(0, 2);
        else hour += tmp;
        return hour;
    }

    public static Map<String, Integer> sortByValues(Map<String, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static String getWeekPeriod(long timeInMillis) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        Calendar firstDay = GregorianCalendar.getInstance();
        firstDay.setTimeInMillis(timeInMillis - ((calendar.get(Calendar.DAY_OF_WEEK)-calendar.getFirstDayOfWeek()) * 86400000));

        Calendar lastDay = GregorianCalendar.getInstance();
        lastDay.setTimeInMillis(firstDay.getTimeInMillis() + (6 * 86400000));

        if(firstDay.get(Calendar.MONTH) == lastDay.get(Calendar.MONTH))
            return ((firstDay.get(Calendar.MONTH)+1)<10?"0"+(firstDay.get(Calendar.MONTH)+1):(firstDay.get(Calendar.MONTH)+1)) + "/" + firstDay.get(Calendar.DAY_OF_MONTH) + "-" + lastDay.get(Calendar.DAY_OF_MONTH);
        else
            return ((firstDay.get(Calendar.MONTH)+1)<10?"0"+(firstDay.get(Calendar.MONTH)+1):(firstDay.get(Calendar.MONTH)+1)) + "/" + firstDay.get(Calendar.DAY_OF_MONTH) + "-" + ((lastDay.get(Calendar.MONTH)+1)<10?"0"+(lastDay.get(Calendar.MONTH)+1):(lastDay.get(Calendar.MONTH)+1)) + "/" + lastDay.get(Calendar.DAY_OF_MONTH);
    }

    public static String attainmentDate(String date) {
        String[] _one = date.split("T")[0].split("-");
        return _one[2] + "-" + _one[1] + "-" + _one[0].substring(2, 4);
    }

    public static String jwtDecoded(String JWTEncoded) {
        try {
            String[] split = JWTEncoded.split("\\.");
            return getJson(split[1]);
        } catch (Exception e) {
            return "";
        }
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }

}

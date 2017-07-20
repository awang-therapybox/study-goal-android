package com.studygoal.jisc.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.studygoal.jisc.BuildConfig;
import com.studygoal.jisc.Models.ActivityHistory;
import com.studygoal.jisc.Models.Attainment;
import com.studygoal.jisc.Models.Courses;
import com.studygoal.jisc.Models.CurrentUser;
import com.studygoal.jisc.Models.ED;
import com.studygoal.jisc.Models.Feed;
import com.studygoal.jisc.Models.Friend;
import com.studygoal.jisc.Models.Institution;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.Models.News;
import com.studygoal.jisc.Models.PendingRequest;
import com.studygoal.jisc.Models.ReceivedRequest;
import com.studygoal.jisc.Models.StretchTarget;
import com.studygoal.jisc.Models.Targets;
import com.studygoal.jisc.Models.Trophy;
import com.studygoal.jisc.Models.TrophyMy;
import com.studygoal.jisc.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class NetworkManager {

    private static NetworkManager ourInstance = new NetworkManager();
    private static int NETWORK_TIMEOUT = 10;
    private String language;
    private SSLContext context;
    private Context appContext;
    private ExecutorService executorService;

    public String no_https_host = "http://stuapp.analytics.alpha.jisc.ac.uk/";
    public String host = "https://stuapp.analytics.alpha.jisc.ac.uk/";

    public static NetworkManager getInstance() {
        return ourInstance;
    }

    private NetworkManager() {
        executorService = Executors.newFixedThreadPool(10);
    }

    public void init(Context context) {
        this.appContext = context;
        setCertificate();
    }

    private void setCertificate() {
        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            InputStream cert = appContext.getAssets().open("cert/certificate.crt");
//            InputStream cert = appContext.getAssets().open("cert/stuapp.analytics.alpha.jisc.ac.uk.crt");
            InputStream caInput = new BufferedInputStream(cert);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteFeed(String feedId) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new deleteFeed(feedId));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateDeviceDetails() {
        try {
            URL url = new URL(host + "fn_register_device");
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setSSLSocketFactory(context.getSocketFactory());

            HashMap<String,String> params = new HashMap<>();
            params.put("student_id",DataManager.getInstance().user.id);
            params.put("version",BuildConfig.VERSION_NAME);
            params.put("build",""+BuildConfig.VERSION_CODE);
            params.put("bundle_identifier",""+ BuildConfig.APPLICATION_ID);
            params.put("is_active", (DataManager.getInstance().get_jwt().length() > 0?"1":"0"));
            params.put("is_social", (DataManager.getInstance().user.isSocial?"yes":"no"));
            params.put("device_token", Build.SERIAL);
            params.put("platform", "android");

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
            params.put("push_token",sharedPreferences.getString("push_token",""));

            String urlParameters = "";
            Iterator it = params.entrySet().iterator();
            for (int i = 0; it.hasNext(); i++) {
                Map.Entry entry = (Map.Entry) it.next();
                if (i == 0)
                    urlParameters += entry.getKey() + "=" + entry.getValue();
                else
                    urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
            }

            Log.e("JISC","PARAMS: "+urlParameters);

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = urlConnection.getResponseCode();
            forbidden(responseCode);
            if (responseCode != 200) {

                InputStream is = new BufferedInputStream(urlConnection.getErrorStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                Log.e("JISC","Response: "+sb.toString());
            }

            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();

            Log.e("JISC","Response: "+sb.toString());
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private class deleteFeed implements Callable<Boolean> {
        String feedId;

        deleteFeed(String feedId) {
           this.feedId = feedId;
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_delete_feed?student_id=" + DataManager.getInstance().user.id + "&feed_id=" + this.feedId + "&language=" + language
                    + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization","Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {

                    InputStream is = new BufferedInputStream(urlConnection.getErrorStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();

                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public Friend getStudentByEmail(String email) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Friend> future = executorService.submit(new getStudentByEmail(email));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class getStudentByEmail implements Callable<Friend> {

        String student_id;
        String email;

        getStudentByEmail(String email) {
            this.student_id = DataManager.getInstance().user.id;
            this.email = email;
        }

        @Override
        public Friend call() {
            try {
                String apiURL = host + "fn_search_student_by_email?student_id=" + student_id + "&email=" + email + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setSSLSocketFactory(context.getSocketFactory());


                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return null;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONObject jsonObject = new JSONArray(sb.toString()).getJSONObject(0);

                Friend item = new Friend();
                item.id = jsonObject.getInt("id") + "";

                if(jsonObject.has("jisc_student_id"))
                    item.jisc_student_id = jsonObject.getString("jisc_student_id");

                if(jsonObject.has("pid"))
                    item.pid = jsonObject.getString("pid");

                if(jsonObject.has("name"))
                    item.name = jsonObject.getString("name");

                if(jsonObject.has("email"))
                    item.email = jsonObject.getString("email");

                if(jsonObject.has("eppn"))
                    item.eppn = jsonObject.getString("eppn");

                if(jsonObject.has("affiliation"))
                    item.affiliation = jsonObject.getString("affiliation");

                if(jsonObject.has("profile_pic"))
                    item.profile_pic = jsonObject.getString("profile_pic");

                if(jsonObject.has("modules"))
                    item.modules = jsonObject.getString("modules");

                if(jsonObject.has("created_date"))
                    item.created_date = jsonObject.getString("created_date");

                if(jsonObject.has("modified_date"))
                    item.modified_date = jsonObject.getString("modified_date");

                if(jsonObject.has("hidden"))
                    item.hidden = jsonObject.getString("hidden").equals("yes");

                return item;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public String forgotPassword(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<String> future_result = executorService.submit(new forgotPassword(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return "400";
        }
    }

    private class forgotPassword implements Callable<String> {

        HashMap<String, String> params;

        forgotPassword(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social", (DataManager.getInstance().user.isSocial ? "yes":"no"));
        }

        @Override
        public String call() {
            try {
                URL url = new URL(host + "fn_forgot_password");
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return responseCode + "";
                }
                System.out.println(urlParameters);
                return "200";
            } catch (Exception e) {
                e.printStackTrace();
                return "400";
            }
        }
    }

    public boolean updateProfileImage(String path) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new uploadProfileImage(path));
        try {
            return future.get();
        } catch (Exception exception) {
            return false;
        }
    }

    private class uploadProfileImage implements Callable<Boolean> {

        final String api = "fn_edit_profile_picture";

        final String crlf = "\r\n";
        final String twoHyphens = "--";
        final String boundary = "---------------------------14737809831466499882746641449";
        String path;

        uploadProfileImage(String p) {
            path = p;
        }

        @Override
        public Boolean call() {
            try {
                URL url = new URL(host + api);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(20000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                wr.writeBytes(crlf + twoHyphens + boundary + crlf);
                String header = "Content-Disposition: form-data; name=\"language\"";
                wr.writeBytes(header);
                wr.writeBytes(crlf);
                wr.writeBytes(crlf);
                wr.writeBytes(language);


                wr.writeBytes(crlf + twoHyphens + boundary + crlf);
                header = "Content-Disposition: form-data; name=\"is_social\"";
                wr.writeBytes(header);
                wr.writeBytes(crlf);
                wr.writeBytes(crlf);
                wr.writeBytes((DataManager.getInstance().user.isStaff ? "yes" : "no"));


                wr.writeBytes(crlf + twoHyphens + boundary + crlf);

                header = "Content-Disposition: form-data; name=\"student_id\"";
                wr.writeBytes(header);
                wr.writeBytes(crlf);
                wr.writeBytes(crlf);
                wr.writeBytes(DataManager.getInstance().user.id);

                wr.writeBytes(crlf + twoHyphens + boundary + crlf);
                header = "Content-Disposition: attachment; name=\"image_data\"; filename=" + DataManager.getInstance().user.id + "_" + System.currentTimeMillis() + ".png" + crlf;
                wr.writeBytes(header);

                header = "Content-Type: image/png" + crlf + crlf;
                wr.writeBytes(header);

                Bitmap bm = BitmapFactory.decodeFile(path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);

                wr.write(baos.toByteArray());
                wr.writeBytes(crlf + twoHyphens + boundary + twoHyphens + crlf);

                wr.flush();
                wr.close();

                if(urlConnection.getResponseCode() != 200) {
                    BufferedInputStream is = new BufferedInputStream(urlConnection.getErrorStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    is.close();
                }

                BufferedInputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                is.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public List<ED> getEngagementGraph(String scope, String compareType, String compareValue, String filterType, String filterValue, boolean isCourse) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<List<ED>> future = executorService.submit(new getEngagementGraph(scope,compareType,compareValue,filterType,filterValue, isCourse));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private class getEngagementGraph implements Callable<List<ED>> {

        ArrayList<ED> engagement_list;
        String scope;
        String compareType;
        String compareValue;
        String filterType;
        String filterValue;
        boolean isCourse;

        getEngagementGraph(String scope, String compareType, String compareValue, String filterType, String filterValue, boolean isCourse) {
            engagement_list = new ArrayList<>();
            this.scope = scope;
            this.compareType = compareType;
            this.compareValue = compareValue;
            this.filterType = filterType;
            this.filterValue = filterValue;
            this.isCourse = isCourse;
        }

        @Override
        public List<ED> call() throws Exception {
            try {
                String apiURL = "https://app.analytics.alpha.jisc.ac.uk/v2/engagement?"
                        + "scope=" + this.scope
                        + "&compareType=" + this.compareType
                        + "&compareValue=" + this.compareValue
                        + "&filterType=" + this.filterType
                        + "&filterValue=" + this.filterValue
                        ;

                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection;
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.addRequestProperty("Authorization", DataManager.getInstance().get_jwt());
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {

                    InputStream is = new BufferedInputStream(urlConnection.getErrorStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();

                    return engagement_list;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                if(compareType.length() == 0) {
                    switch (this.scope) {
                        case "7d": {
                            Calendar c = Calendar.getInstance();
                            Calendar g = Calendar.getInstance();
                            JSONObject jsonObject = new JSONObject(sb.toString());
                            Iterator<String> iterator = jsonObject.keys();
                            int temp = jsonObject.length();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                ED item = new ED();
                                c.setTimeInMillis((g.getTimeInMillis() / 1000 - (temp * 3600 * 24)) * 1000);
                                item.day = LinguisticManager.getInstance().getWeekDay(c.get(Calendar.DAY_OF_WEEK));
                                item.activity_points = jsonObject.getInt(key);
                                temp--;
                                engagement_list.add(item);
                            }
                            break;
                        }
                        case "28d": {
                            Calendar c = Calendar.getInstance();
                            Calendar g = Calendar.getInstance();
                            JSONObject jsonObject = new JSONObject(sb.toString());
                            Iterator<String> iterator = jsonObject.keys();
                            int temp = jsonObject.length();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                ED item = new ED();
                                c.setTimeInMillis((g.getTimeInMillis() / 1000 - (temp * 3600 * 24)) * 1000);
                                item.day = (((c.get(Calendar.MONTH) + 1) < 10) ? ("0" + (c.get(Calendar.MONTH) + 1)) : ((c.get(Calendar.MONTH) + 1) + "")) + "/" + ((c.get(Calendar.DAY_OF_MONTH) < 10) ? ("0" + c.get(Calendar.DAY_OF_MONTH)) : (c.get(Calendar.DAY_OF_MONTH)));
                                item.activity_points = jsonObject.getInt(key);
                                temp--;
                                engagement_list.add(item);
                            }
                            break;
                        }
                        case "overall": {
                            JSONArray jsonArray = new JSONObject(sb.toString()).getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ED item = new ED();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONArray jsonArray1 = new JSONObject(jsonObject.toString()).getJSONArray("data");
                                JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                                item.day = jsonObject.getString("_id").replace("-", "/");
                                item.activity_points = jsonObject1.getInt("totalPoints");
                                engagement_list.add(item);
                            }
                            Collections.sort(engagement_list, new Comparator<ED>() {
                                @Override
                                public int compare(ED s1, ED s2) {
                                    return s1.day.compareToIgnoreCase(s2.day);
                                }
                            });
                            break;
                        }
                    }
                } else {
                    switch (this.scope) {
                        case "7d": {
                            Calendar c = Calendar.getInstance();
                            JSONArray jsonArray = new JSONArray(sb.toString());

                            for (int j = 0 ; j < jsonArray.length() ; j++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(j);
                                JSONObject jsonObject1 = jsonObject.getJSONObject("VALUES");
                                for (int i = 0; i < jsonObject1.length(); i++) {
                                    ED item = new ED();
                                    item.student_id = jsonObject.getString("STUDENT_ID");
                                    c.setTimeInMillis(c.getTimeInMillis() + 86400000 * -i);
                                    item.day = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
                                    item.position = String.valueOf(i).replace("-","");
                                    item.activity_points = jsonObject1.getInt(String.valueOf(-i));
                                    engagement_list.add(item);
                                }
                            }
                            break;
                        }
                        case "28d": {
                            Calendar c = Calendar.getInstance();
                            Calendar g = Calendar.getInstance();
                            JSONArray jsonArray = new JSONArray(sb.toString());
                            int temp = jsonArray.length();
                            for (int j = 0 ; j < jsonArray.length() ; j++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(j);
                                JSONObject jsonObject1 = jsonObject.getJSONObject("VALUES");
                                for (int i = 0; i < jsonObject1.length(); i++) {
                                    ED item = new ED();
                                    item.student_id = jsonObject.getString("STUDENT_ID");
                                    c.setTimeInMillis((g.getTimeInMillis() / 1000 - (temp * 3600 * 24)) * 1000);
                                    item.week = (((c.get(Calendar.MONTH) + 1) < 10) ? ("0" + (c.get(Calendar.MONTH) + 1)) : ((c.get(Calendar.MONTH) + 1) + "")) + "/" + ((c.get(Calendar.DAY_OF_MONTH) < 10) ? ("0" + c.get(Calendar.DAY_OF_MONTH)) : (c.get(Calendar.DAY_OF_MONTH)));
                                    item.activity_points = jsonObject1.getInt(String.valueOf(-i));
                                    item.position = String.valueOf(i).replace("-","");
                                    engagement_list.add(item);
                                }
                            }
                            break;
                        }
                        case "overall": {
                            JSONObject jsonObject = new JSONObject(sb.toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0 ; i < jsonArray.length() ; i++){
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                JSONArray jsonArray1 = jsonObject1.getJSONArray("data");
                                for (int j = 0 ; j < jsonArray1.length() ; j++){
                                    JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                                    ED item = new ED();
                                    item.date = jsonObject1.getString("_id");
                                    item.activity_points = jsonObject2.getInt("totalPoints");
                                    item.student_id = jsonObject2.getString("record");
                                    engagement_list.add(item);
                                }
                            }
                            break;
                        }
                    }
                }

                return engagement_list;
            } catch (Exception e) {
                e.printStackTrace();
                return engagement_list;
            }
        }
    }

    public boolean getMyTrophies() {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new getMyTrophies());
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getMyTrophies implements Callable<Boolean> {

        @Override
        public Boolean call() {
            try {

                String api = "fn_get_student_trophies?student_id=" + DataManager.getInstance().user.id + "&language=" + language;
                String apiURL = host + api
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    if (responseCode == 204) {
                        new Delete().from(TrophyMy.class).execute();
                    } else
                        Log.e("getMyTrophies", "Code: " + responseCode);
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONArray jsonArray = new JSONArray(sb.toString());


                ActiveAndroid.beginTransaction();
                try {
                    new Delete().from(TrophyMy.class).execute();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        TrophyMy item = new TrophyMy();

                        item.trophy_id = jsonObject.getString("id");
                        item.trophy_name = jsonObject.getString("trophy_name");
                        item.trophy_type = jsonObject.getString("type");
                        item.activity_name = jsonObject.getString("activity_name");
                        item.count = jsonObject.getString("count");
                        item.days = jsonObject.getString("days");
                        item.total = jsonObject.getString("total");

                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean getAllTrophies() {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new getAllTrophies());
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getAllTrophies implements Callable<Boolean> {

        @Override
        public Boolean call() {
            try {

                String api = "fn_get_trophies";
                String apiURL = host + api + "?language=" + language;
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {

                    Log.e("getAllTrophies", apiURL);

                    if (responseCode == 204) {
                        Log.i("getAllTrophies", "No records found");
                        new Delete().from(Trophy.class).execute();
                    } else
                        Log.e("getAllTrophies", "Code: " + responseCode);
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONArray jsonArray = new JSONArray(sb.toString());


                ActiveAndroid.beginTransaction();
                try {
                    new Delete().from(Trophy.class).execute();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Trophy item = new Trophy();
                        item.trophy_id = jsonObject.getString("id");
                        item.trophy_name = jsonObject.getString("trophy_name");
                        item.trophy_type = jsonObject.getString("type");
                        item.statement = jsonObject.getString("statement");
                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean getAssignmentRanking() {
        Future<Boolean> future = executorService.submit(new getAssigmentRanking());
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getAssigmentRanking implements Callable<Boolean> {

        getAssigmentRanking() {
        }

        @Override
        public Boolean call() {
            try {

                String apiURL = "https://app.analytics.alpha.jisc.ac.uk/v2/attainment?"
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", DataManager.getInstance().get_jwt());
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONArray jsonArray = new JSONArray(sb.toString());
                ActiveAndroid.beginTransaction();
                try {
                    new Delete().from(Attainment.class).execute();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Attainment attainment_item = new Attainment();
                        attainment_item.id = DataManager.getInstance().user.id;
                        attainment_item.date = ((JSONObject) jsonArray.get(i)).getString("CREATED_AT");
                        attainment_item.module = ((JSONObject) jsonArray.get(i)).getString("X_MOD_NAME");
                        attainment_item.percent = ((JSONObject) jsonArray.get(i)).getString("ASSESS_AGREED_GRADE");
                        attainment_item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public String getCurrentOverallRanking(String student_id) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<String> future = executorService.submit(new getCurrentOverallRanking(student_id));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }

    private class getCurrentOverallRanking implements Callable<String> {

        String student_id;

        getCurrentOverallRanking(String student_id) {
            this.student_id = student_id;
        }

        @Override
        public String call() {
            try {

                String apiURL = host + "fn_get_overall_ranking?student_id=" + student_id + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return "-1";
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONObject jsonObject = new JSONObject(sb.toString());
                //String average = jsonObject.getJSONObject("overall_points_till_date").get("average").toString();
                JSONObject studentsObject = jsonObject.getJSONObject("overall_points_till_date").getJSONObject("total");
                Iterator<String> studentIterator = studentsObject.keys();
                Map<String, Integer> mapOfMarks = new HashMap<>();
                while (studentIterator.hasNext()) {
                    String student_id = studentIterator.next();
                    mapOfMarks.put(student_id, studentsObject.getInt(student_id));
                }
                mapOfMarks = Utils.sortByValues(mapOfMarks);
                Iterator<String> iterator = mapOfMarks.keySet().iterator();
//                int total_unique = 0;
//                int student_position = 0;
//                int last_value = 0;
                int CL = 0;
                int FI = 0;
                while (iterator.hasNext()) {
                    int current_value = mapOfMarks.get(DataManager.getInstance().user.id);

                    String student_id = iterator.next();
                    int value = mapOfMarks.get(student_id);
//                    if(!DataManager.getInstance().user.id.equals(student_id)) {
                    if (current_value > value)
                        CL++;
                    if (current_value == value)
                        FI++;
//                    }
                }
                double value = (((double) CL + 0.5 * (double) FI) / mapOfMarks.size()) * 100;
                int topPercent = 100 - (int) value;
                return topPercent + "";
            } catch (Exception e) {
                e.printStackTrace();
                return "-1";
            }
        }
    }

    public String getCurrentRanking(String student_id) {
        Future<String> future = executorService.submit(new getCurrentRanking(student_id));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }

    private class getCurrentRanking implements Callable<String> {

        String student_id;

        getCurrentRanking(String student_id) {
            this.student_id = student_id;
        }

        @Override
        public String call() {
            try {

                String apiURL = host + "fn_get_current_week_ranking?student_id=" + student_id + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return "-1";
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONObject jsonObject = new JSONObject(sb.toString());
                String average = jsonObject.getJSONObject("last_week_points").get("average").toString();
                JSONObject studentsObject = jsonObject.getJSONObject("last_week_points").getJSONObject("total");
                Iterator<String> studentIterator = studentsObject.keys();
                Map<String, Integer> mapOfMarks = new HashMap<>();
                while (studentIterator.hasNext()) {
                    String student_id = studentIterator.next();
                    mapOfMarks.put(student_id, studentsObject.getInt(student_id));
                }
                mapOfMarks = Utils.sortByValues(mapOfMarks);
                Iterator<String> iterator = mapOfMarks.keySet().iterator();
//                int total_unique = 0;
//                int student_position = 0;
//                int last_value = 0;
                int CL = 0;
                int FI = 0;
                while (iterator.hasNext()) {
                    int current_value = mapOfMarks.get(DataManager.getInstance().user.id);

                    String student_id = iterator.next();
                    int value = mapOfMarks.get(student_id);
//                    if(!DataManager.getInstance().user.id.equals(student_id)) {
                    if (current_value > value)
                        CL++;
                    if (current_value == value)
                        FI++;
//                    }
                }
                double value = (((double) CL + 0.5 * (double) FI) / mapOfMarks.size()) * 100;
                int topPercent = 100 - (int) value;
//                int topPercent = (student_position*100)/total_unique;
                return topPercent + "";
            } catch (Exception e) {
                e.printStackTrace();
                return "-1";
            }
        }
    }

    public boolean getStudentActivityPoint() {
        Future<Boolean> future = executorService.submit(new getStudentActivityPoint());
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getStudentActivityPoint implements Callable<Boolean> {

        getStudentActivityPoint() {
        }

        @Override
        public Boolean call() {
            try {

                String apiURL = "https://app.analytics.alpha.jisc.ac.uk/v2/activity/points?scope=overall"
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.addRequestProperty("Authorization", DataManager.getInstance().get_jwt());
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONObject jsonObject = new JSONObject(sb.toString());
                DataManager.getInstance().user.overall_activity_points = jsonObject.getString("totalPoints");

                url = new URL("https://app.analytics.alpha.jisc.ac.uk/v2/activity/points?scope=7d");
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }

                is = new BufferedInputStream(urlConnection.getInputStream());
                reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                jsonObject = new JSONObject(sb.toString());
                DataManager.getInstance().user.last_week_activity_points = jsonObject.getString("totalPoints");

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean getAppSettings(String student_id) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new getAppSettings(student_id));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getAppSettings implements Callable<Boolean> {

        String student_id;

        getAppSettings(String student_id) {
            this.student_id = student_id;
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_get_student_app_settings?student_id=" + student_id + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    SharedPreferences preferences = DataManager.getInstance().mainActivity.getSharedPreferences("jisc", Context.MODE_PRIVATE);
                    DataManager.getInstance().home_screen = preferences.getString("home_screen", "feed");
                    DataManager.getInstance().language = preferences.getString("home_screen", "english");
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONObject jsonObject = new JSONObject(sb.toString());
                DataManager.getInstance().home_screen = jsonObject.getString("home_screen");
                DataManager.getInstance().language = jsonObject.getString("language");
                SharedPreferences preferences = DataManager.getInstance().mainActivity.getSharedPreferences("jisc", Context.MODE_PRIVATE);
                preferences.edit().putString("home_screen", DataManager.getInstance().home_screen).apply();
                preferences.edit().putString("language", DataManager.getInstance().language).apply();
                LinguisticManager.getInstance().reload(appContext);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                SharedPreferences preferences = DataManager.getInstance().mainActivity.getSharedPreferences("jisc", Context.MODE_PRIVATE);
                DataManager.getInstance().home_screen = preferences.getString("home_screen", "feed");
                DataManager.getInstance().language = preferences.getString("home_screen", "english");

                return false;
            }
        }
    }

    public boolean changeAppSettings(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new changeAppSettings(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class changeAppSettings implements Callable<Boolean> {

        HashMap<String, String> params;

        changeAppSettings(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_change_app_settings";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());


                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }

                System.out.println(urlParameters);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean postFeedMessage(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new postFeedMessage(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class postFeedMessage implements Callable<Boolean> {

        HashMap<String,String> params;

        postFeedMessage(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_post_message";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                System.out.println(urlParameters);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean hidePost(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new hidePost(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class hidePost implements Callable<Boolean> {

        HashMap<String, String> params;

        hidePost(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_hide_feed";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean acceptFriendRequest(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new acceptFriendRequest(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class acceptFriendRequest implements Callable<Boolean> {

        HashMap<String, String> params;

        acceptFriendRequest(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_accept_friend_request";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean deleteFriendRequest(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new deleteFriendRequest(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class deleteFriendRequest implements Callable<Boolean> {

        HashMap<String, String> params;

        deleteFriendRequest(HashMap<String, String> params) {
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_delete_friend_request?student_id=" + params.get("student_id") + "&deleted_user=" + params.get("deleted_user") + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setDoInput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean cancelFriendRequest(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new cancelFriendRequest(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class cancelFriendRequest implements Callable<Boolean> {

        HashMap<String, String> params;

        cancelFriendRequest(HashMap<String, String> params) {
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_cancel_pending_friend_request?student_id=" + params.get("student_id") + "&friend_id=" + params.get("friend_id") + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setDoInput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean deleteFriend(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new deleteFriend(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class deleteFriend implements Callable<Boolean> {

        HashMap<String, String> params;

        deleteFriend(HashMap<String, String> params) {
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_delete_friend?student_id=" + params.get("student_id") + "&friend_id=" + params.get("friend_id") + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setDoInput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean changeFriendSettings(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new changeFriendSettings(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class changeFriendSettings implements Callable<Boolean> {

        HashMap<String, String> params;

        changeFriendSettings(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_change_friend_settings";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean sendFriendRequest(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new sendFriendRequest(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class sendFriendRequest implements Callable<Boolean> {

        HashMap<String, String> params;

        sendFriendRequest(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_send_friend_request";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {

                    InputStream is = new BufferedInputStream(urlConnection.getErrorStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();

                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean getFriendRequests(String student_id) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new getFriendRequests(student_id));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getFriendRequests implements Callable<Boolean> {

        String student_id;

        getFriendRequests(String student_id) {
            this.student_id = student_id;
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_list_friend_requests?student_id=" + student_id + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    if (responseCode == 204) {
                        new Delete().from(ReceivedRequest.class).execute();
                    } else {

                    }
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONArray jsonArray = new JSONArray(sb.toString());

                ActiveAndroid.beginTransaction();
                try {
                    new Delete().from(ReceivedRequest.class).execute();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ReceivedRequest item = new ReceivedRequest();

                        item.id = jsonObject.getString("id");

                        if(jsonObject.has("institution_id"))
                            item.institution_id = jsonObject.getString("institution_id");

                        if(jsonObject.has("dob"))
                            item.dob = jsonObject.getString("dob");

                        if(jsonObject.has("race_code"))
                            item.race_code = jsonObject.getString("race_code");

                        if(jsonObject.has("sex_code"))
                            item.sex_code = jsonObject.getString("sex_code");

                        if(jsonObject.has("age"))
                            item.age = jsonObject.getString("age");

                        if(jsonObject.has("learning_difficulty_code"))
                            item.learning_difficulty_code = jsonObject.getString("learning_difficulty_code");

                        if(jsonObject.has("accommodation_code"))
                            item.accommodation_code = jsonObject.getString("accommodation_code");

                        if(jsonObject.has("disability_code"))
                            item.disability_code = jsonObject.getString("disability_code");

                        if(jsonObject.has("country_code"))
                            item.country_code = jsonObject.getString("country_code");

                        if(jsonObject.has("parents_qualification"))
                            item.parents_qualification = jsonObject.getString("parents_qualification");

                        if(jsonObject.has("overseas_code"))
                            item.overseas_code = jsonObject.getString("overseas_code");

                        if(jsonObject.has("first_name"))
                            item.first_name = jsonObject.getString("first_name");

                        if(jsonObject.has("last_name"))
                            item.last_name = jsonObject.getString("last_name");

                        if(jsonObject.has("address_line_1"))
                            item.address_line_1 = jsonObject.getString("address_line_1");

                        if(jsonObject.has("address_line_2"))
                            item.address_line_2 = jsonObject.getString("address_line_2");

                        if(jsonObject.has("address_line_3"))
                            item.address_line_3 = jsonObject.getString("address_line_3");

                        if(jsonObject.has("address_line_4"))
                            item.address_line_4 = jsonObject.getString("address_line_4");

                        if(jsonObject.has("postal_code"))
                            item.postal_code = jsonObject.getString("postal_code");

                        if(jsonObject.has("email"))
                            item.email = jsonObject.getString("email");

                        if(jsonObject.has("home_phone"))
                            item.home_phone = jsonObject.getString("home_phone");

                        if(jsonObject.has("mobile_phone"))
                            item.mobile_phone = jsonObject.getString("mobile_phone");

                        if(jsonObject.has("photo"))
                            item.photo = jsonObject.getString("photo");

                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean getSentFriendRequests(String student_id) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new getSentFriendRequests(student_id));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getSentFriendRequests implements Callable<Boolean> {

        String student_id;

        getSentFriendRequests(String student_id) {
            this.student_id = student_id;
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_list_sent_friend_requests?student_id=" + student_id + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    if (responseCode == 204) {
                        new Delete().from(PendingRequest.class).execute();
                    } else {

                    }
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONArray jsonArray = new JSONArray(sb.toString());

                ActiveAndroid.beginTransaction();
                try {
                    new Delete().from(PendingRequest.class).execute();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        PendingRequest item = new PendingRequest();
                        item.id = jsonObject.getString("id");

                        if(jsonObject.has("institution_id"))
                            item.institution_id = jsonObject.getString("institution_id");

                        if(jsonObject.has("dob"))
                            item.dob = jsonObject.getString("dob");

                        if(jsonObject.has("race_code"))
                            item.race_code = jsonObject.getString("race_code");

                        if(jsonObject.has("sex_code"))
                            item.sex_code = jsonObject.getString("sex_code");

                        if(jsonObject.has("age"))
                            item.age = jsonObject.getString("age");

                        if(jsonObject.has("learning_difficulty_code"))
                            item.learning_difficulty_code = jsonObject.getString("learning_difficulty_code");

                        if(jsonObject.has("accommodation_code"))
                            item.accommodation_code = jsonObject.getString("accommodation_code");

                        if(jsonObject.has("disability_code"))
                            item.disability_code = jsonObject.getString("disability_code");

                        if(jsonObject.has("country_code"))
                            item.country_code = jsonObject.getString("country_code");

                        if(jsonObject.has("parents_qualification"))
                            item.parents_qualification = jsonObject.getString("parents_qualification");

                        if(jsonObject.has("overseas_code"))
                            item.overseas_code = jsonObject.getString("overseas_code");

                        if(jsonObject.has("first_name"))
                            item.first_name = jsonObject.getString("first_name");

                        if(jsonObject.has("last_name"))
                            item.last_name = jsonObject.getString("last_name");

                        if(jsonObject.has("address_line_1"))
                            item.address_line_1 = jsonObject.getString("address_line_1");

                        if(jsonObject.has("address_line_2"))
                            item.address_line_2 = jsonObject.getString("address_line_2");

                        if(jsonObject.has("address_line_3"))
                            item.address_line_3 = jsonObject.getString("address_line_3");

                        if(jsonObject.has("address_line_4"))
                            item.address_line_4 = jsonObject.getString("address_line_4");

                        if(jsonObject.has("postal_code"))
                            item.postal_code = jsonObject.getString("postal_code");

                        if(jsonObject.has("email"))
                            item.email = jsonObject.getString("email");

                        if(jsonObject.has("home_phone"))
                            item.home_phone = jsonObject.getString("home_phone");

                        if(jsonObject.has("mobile_phone"))
                            item.mobile_phone = jsonObject.getString("mobile_phone");

                        if(jsonObject.has("photo"))
                            item.photo = jsonObject.getString("photo");
                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean getFriends(String student_id) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new getFriends(student_id));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getFriends implements Callable<Boolean> {

        String student_id;

        getFriends(String student_id) {
            this.student_id = student_id;
        }

        @Override
        public Boolean call() {
            try {

                ActiveAndroid.beginTransaction();
                new Delete().from(Friend.class).execute();
                ActiveAndroid.setTransactionSuccessful();
                ActiveAndroid.endTransaction();

                String apiURL = host + "fn_list_friends?student_id=" + student_id + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {

                    InputStream is = new BufferedInputStream(urlConnection.getErrorStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();

                    if (responseCode == 204) {
                        new Delete().from(Friend.class).execute();
                    } else {
                    }
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONArray jsonArray = new JSONArray(sb.toString());

                ActiveAndroid.beginTransaction();
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        Friend item = new Friend();
                        item.id = jsonObject.getInt("id") + "";
                        item.jisc_student_id = jsonObject.getString("jisc_student_id");
                        item.pid = jsonObject.getString("pid");
                        item.name = jsonObject.getString("name");
                        item.email = jsonObject.getString("email");
                        item.eppn = jsonObject.getString("eppn");
                        item.affiliation = jsonObject.getString("affiliation");
                        item.profile_pic = jsonObject.getString("profile_pic");
                        item.modules = jsonObject.getString("modules");
                        item.created_date = jsonObject.getString("created_date");
                        item.modified_date = jsonObject.getString("modified_date");
                        item.hidden = jsonObject.getString("hidden").equals("yes");
                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
                ActiveAndroid.endTransaction();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean getNewsFeed() {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new getNewsFeed());
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean postNotificationMessage(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new postNotificationMessage(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class postNotificationMessage implements Callable<Boolean> {

        HashMap<String,String> params;

        postNotificationMessage(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_add_push_notification";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                System.out.println(urlParameters);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private class getNewsFeed implements Callable<Boolean> {

        getNewsFeed() {
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_get_push_notifications?student_id="
                        + DataManager.getInstance().user.id
                        + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"&is_social=yes");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                if (responseCode != 200) {
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONArray jsonArray = new JSONArray(sb.toString());

                ActiveAndroid.beginTransaction();
                try {
                    new Delete().from(News.class).execute();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        News item = new News();
                        item.id = jsonObject.getString("id");
                        item.message_from = jsonObject.getString("message_from");
                        item.message = jsonObject.getString("message");
                        item.created_date = jsonObject.getString("created");
                        item.read = jsonObject.getString("is_read");
                        if(!item.read.equals("1"))
                            item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean markNewsAsRead(News item) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new markNewsAsRead(item.id));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class markNewsAsRead implements Callable<Boolean> {

        HashMap<String, String> params;
        markNewsAsRead(String id) {
            params = new HashMap<>();
            params.put("student_id", DataManager.getInstance().user.id);
            params.put("notification_id", id);
            params.put("is_social", "yes");
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_update_notifications_read_status";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("PUT");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode != 200) {
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean getFeed(String student_id) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new getFeed(student_id));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getFeed implements Callable<Boolean> {

        String student_id;

        getFeed(String student_id) {
            this.student_id = student_id;
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_get_feeds?student_id=" + student_id + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    if (responseCode == 204) {
                        new Delete().from(Feed.class).execute();
                    } else {
                    }
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONArray jsonArray = new JSONArray(sb.toString());
                ActiveAndroid.beginTransaction();
                try {
                    new Delete().from(Feed.class).execute();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Feed item = new Feed();
                        item.id = jsonObject.getString("id");
                        item.message_from = jsonObject.getString("message_from");
//                        item.message_to = jsonObject.getString("message_to");
                        item.message = jsonObject.getString("message");
                        item.activity_type = jsonObject.getString("activity_type");
                        item.is_hidden = "0";//jsonObject.getString("is_hidden");
                        item.created_date = jsonObject.getString("created_date");
                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean getStretchTargets(String student_id) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new getStretchTargets(student_id));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getStretchTargets implements Callable<Boolean> {

        String student_id;

        getStretchTargets(String student_id) {
            this.student_id = student_id;
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_get_stretch_targets?student_id=" + student_id + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    if (responseCode == 204) {
                        new Delete().from(StretchTarget.class).execute();
                    } else {

                    }

                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONArray jsonArray = new JSONArray(sb.toString());

                ActiveAndroid.beginTransaction();
                try {
                    new Delete().from(StretchTarget.class).execute();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        StretchTarget item = new StretchTarget();
                        item.id = jsonObject.getString("id");
                        item.target_id = jsonObject.getString("target_id");
                        item.stretch_time = jsonObject.getString("stretch_time");
                        item.status = jsonObject.getString("status");
                        item.created_date = jsonObject.getString("created_date");
                        item.student_id = jsonObject.getString("student_id");
                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean addStretchTarget(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new addStretchTarget(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class addStretchTarget implements Callable<Boolean> {

        HashMap<String, String> params;

        addStretchTarget(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_add_stretch_target";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());


                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                System.out.println(urlParameters);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean unhideFriend(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new unhideFriend(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class unhideFriend implements Callable<Boolean> {

        HashMap<String, String> params;

        unhideFriend(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_unhide_friend";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean hideFriend(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new hideFriend(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class hideFriend implements Callable<Boolean> {

        HashMap<String, String> params;

        hideFriend(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_hide_friend";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean deleteTarget(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new deleteTarget(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class deleteTarget implements Callable<Boolean> {

        HashMap<String, String> params;

        deleteTarget(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_delete_target";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean editTarget(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new editTarget(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class editTarget implements Callable<Boolean> {

        HashMap<String, String> params;

        editTarget(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_edit_target";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean addTarget(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new addTarget(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class addTarget implements Callable<Boolean> {

        HashMap<String, String> params;

        addTarget(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",(DataManager.getInstance().user.isSocial?"yes":"no"));
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_add_target";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                System.out.println(urlParameters);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean getTargets(String student_id) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new getTargets(student_id));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getTargets implements Callable<Boolean> {

        String student_id;

        getTargets(String student_id) {
            this.student_id = student_id;
        }

        @Override
        public Boolean call() {
            try {

                String apiURL = host + "fn_get_targets?student_id=" + student_id + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    if (responseCode == 204) {
                        new Delete().from(Targets.class).execute();
                    } else {

                    }

                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONArray jsonArray = new JSONArray(sb.toString());

                ActiveAndroid.beginTransaction();
                try {
                    new Delete().from(Targets.class).execute();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Targets target = new Targets();
                        target.target_id = jsonObject.getInt("id") + "";
                        target.student_id = jsonObject.getString("student_id");
                        target.activity_type = jsonObject.getString("activity_type");
                        target.activity = jsonObject.getString("activity");
                        target.total_time = jsonObject.getInt("total_time") + "";
                        if(!jsonObject.optString("time_span").equals("null")) {
                            target.time_span = jsonObject.getString("time_span");
                        } else {
                            target.time_span = "";
                        }
                        target.module_id = jsonObject.getString("module");
                        target.because = jsonObject.getString("because");
                        target.status = jsonObject.getInt("status") + "";
                        target.created_date = jsonObject.getString("created_date");
                        target.modified_date = jsonObject.getString("modified_date");
                        target.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean deleteActivity(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new deleteActivity(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class deleteActivity implements Callable<Boolean> {

        HashMap<String, String> params;

        deleteActivity(HashMap<String, String> params) {
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",DataManager.getInstance().user.isSocial?"yes":"no");
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_delete_activity_log?student_id=" + DataManager.getInstance().user.id + "&log_id=" + params.get("log_id") + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setDoInput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean editActivity(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future_result = executorService.submit(new editActivity(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class editActivity implements Callable<Boolean> {

        HashMap<String, String> params;

        editActivity(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social",DataManager.getInstance().user.isSocial?"yes":"no");
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_edit_activity_log";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public String addActivity(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<String> future_result = executorService.submit(new addActivity(params));
        try {
            return future_result.get(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return "400";
        }
    }

    private class addActivity implements Callable<String> {

        HashMap<String, String> params;

        addActivity(HashMap<String, String> params) {
            params.put("language", language);
            this.params = params;
            if(DataManager.getInstance().user.isSocial)
                this.params.put("is_social", (DataManager.getInstance().user.isSocial ? "yes":"no"));
        }

        @Override
        public String call() {
            try {
                String apiURL = host + "fn_add_activity_log";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {

                    InputStream is = new BufferedInputStream(urlConnection.getErrorStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();

                    return responseCode + "";
                }
                System.out.println(urlParameters);
                return "200";
            } catch (Exception e) {
                e.printStackTrace();
                return "400";
            }
        }
    }


    /**
     * loginStaff() - logs in staff
     *
     * @return true/false
     */
    public Boolean loginStaff() {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> futureResult = executorService.submit(new loginStaff());
        try {
            return futureResult.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class loginStaff implements Callable<Boolean> {

        loginStaff() {
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_staff_login";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "language=" + LinguisticManager.getInstance().getLanguageCode();

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONObject jsonObject = new JSONObject(sb.toString());

                new Delete().from(CurrentUser.class).execute();

                DataManager.getInstance().user = new CurrentUser();
                DataManager.getInstance().user.id = jsonObject.getInt("id") + "";
                DataManager.getInstance().user.staff_id = jsonObject.getString("staff_id");
                DataManager.getInstance().user.jisc_student_id = jsonObject.getString("staff_id");
                DataManager.getInstance().user.pid = jsonObject.getString("pid");
                DataManager.getInstance().user.name = jsonObject.getString("name");
                DataManager.getInstance().user.email = jsonObject.getString("email");
                DataManager.getInstance().user.eppn = jsonObject.getString("eppn");
                DataManager.getInstance().user.affiliation = jsonObject.getString("affiliation");
                DataManager.getInstance().user.profile_pic = jsonObject.getString("profile_pic");
                DataManager.getInstance().user.modules = jsonObject.getString("modules");
                DataManager.getInstance().user.created_date = jsonObject.getString("created_date");
                DataManager.getInstance().user.modified_date = jsonObject.getString("modified_date");
                DataManager.getInstance().user.isStaff = true;
                DataManager.getInstance().user.isSocial = false;

                DataManager.getInstance().user.save();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * login() - logs in user
     *
     * @return true/false
     */
    public Boolean login() {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> futureResult = executorService.submit(new login());
        try {
            return futureResult.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class login implements Callable<Boolean> {

        login() {
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_login";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "language=" + LinguisticManager.getInstance().getLanguageCode();

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONObject jsonObject = new JSONObject(sb.toString());

                new Delete().from(CurrentUser.class).execute();
                DataManager.getInstance().user = new CurrentUser();
                DataManager.getInstance().user.id = jsonObject.getInt("id") + "";
                DataManager.getInstance().user.jisc_student_id = jsonObject.getString("jisc_student_id");
                DataManager.getInstance().user.pid = jsonObject.getString("pid");
                DataManager.getInstance().user.name = jsonObject.getString("name");
                DataManager.getInstance().user.email = jsonObject.getString("email");
                DataManager.getInstance().user.eppn = jsonObject.getString("eppn");
                DataManager.getInstance().user.affiliation = jsonObject.getString("affiliation");
                DataManager.getInstance().user.profile_pic = jsonObject.getString("profile_pic");
                DataManager.getInstance().user.staff_id = "";
                DataManager.getInstance().user.isStaff = false;
                DataManager.getInstance().user.isSocial = false;
                DataManager.getInstance().user.modules = jsonObject.getString("modules");
                DataManager.getInstance().user.created_date = jsonObject.getString("created_date");
                DataManager.getInstance().user.modified_date = jsonObject.getString("modified_date");

                DataManager.getInstance().user.save();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * loginSocial() - logs in social user
     *
     * @return true/false
     */
    public Integer loginSocial(String email, String password) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Integer> futureResult = executorService.submit(new loginSocial(email, password));
        try {
            return futureResult.get();
        } catch (Exception e) {
            e.printStackTrace();
            return 404;
        }
    }

    private class loginSocial implements Callable<Integer> {

        String email = "";
        String password = "";

        loginSocial(String e, String p) {
            email = e;
            password = p;
        }

        @Override
        public Integer call() {
            try {
                String apiURL = host + "fn_social_login";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                String urlParameters = "language=" + LinguisticManager.getInstance().getLanguageCode() +
                                        "&social_id="+password+
                                        "&email="+email+
                                        "&full_name=test"+
                                        "&is_social=yes"+
                                        "&institution=1";

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    InputStream is = new BufferedInputStream(urlConnection.getErrorStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();

                    return responseCode;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONObject jsonObject = new JSONObject(sb.toString());

                new Delete().from(CurrentUser.class).execute();
                DataManager.getInstance().user = new CurrentUser();
                DataManager.getInstance().user.id = jsonObject.getInt("id") + "";
                DataManager.getInstance().user.password = password;
                DataManager.getInstance().user.jisc_student_id = jsonObject.getString("id");
                DataManager.getInstance().user.pid = jsonObject.getString("pid");
                DataManager.getInstance().user.name = jsonObject.getString("name");
                DataManager.getInstance().user.email = jsonObject.getString("email");
                DataManager.getInstance().user.eppn = jsonObject.getString("eppn");
                DataManager.getInstance().user.affiliation = jsonObject.getString("affiliation");
                DataManager.getInstance().user.profile_pic = jsonObject.getString("profile_pic");
                DataManager.getInstance().user.staff_id = "";
                DataManager.getInstance().user.isStaff = false;
                DataManager.getInstance().user.isSocial = true;
                DataManager.getInstance().user.modules = jsonObject.getString("modules");
                DataManager.getInstance().user.created_date = jsonObject.getString("created_date");
                DataManager.getInstance().user.modified_date = jsonObject.getString("modified_date");

                DataManager.getInstance().user.save();
                return responseCode;
            } catch (Exception e) {
                e.printStackTrace();
                return 404;
            }
        }
    }

    // **************************************************

    public boolean setUserPin(String pin_text_edit_text, String location) {
        Future<Boolean> futureResult = executorService.submit(new setUserPin(pin_text_edit_text, location));
        try {
            return futureResult.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class setUserPin implements Callable<Boolean> {

        String pin;
        String location;

        setUserPin(String pin, String location) {
            this.pin = pin;
            this.location = location;
        }

        @Override
        public Boolean call() {

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

                String apiURL = "https://api.x-staging.data.alpha.jisc.ac.uk/att/checkin?checkinpin="+this.pin+"&geo_tag="+this.location+"&timestamp="+sdf.format(new Date());
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection;
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();
                if(responseCode != 200) {
                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                try {
                    JSONArray jsonArray = new JSONArray(sb.toString());
                    if(jsonArray.length() == 0) {
                        return false;
                    }

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if(!jsonObject.has("ATTENDED")
                            || !jsonObject.has("id")) {
                        return false;
                    }

                    String attended = jsonObject.getString("ATTENDED");
                    String id = jsonObject.getString("id");

                    if(id.length() == 0) {
                        return false;
                    }

                    if(Integer.parseInt(attended) == 0) {
                        return false;
                    }

                    return true;

                } catch (Exception e) {
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * checkIfUserRegistered() => checks if the user is registered;
     *
     * @return true/false
     */

    public boolean checkIfUserRegistered() {
        Future<Boolean> futureResult = executorService.submit(new checkIfUserRegistered());
        try {
            return futureResult.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class checkIfUserRegistered implements Callable<Boolean> {

        checkIfUserRegistered() {
        }

        @Override
        public Boolean call() {

            try {

                String apiURL = "https://sp.data.alpha.jisc.ac.uk/student";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection;
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();
                if (responseCode != 200) {
                    return false;
                }
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONObject jsonObject = new JSONObject(sb.toString());

                if (jsonObject.has("APPSHIB_ID")
                        && !jsonObject.getString("APPSHIB_ID").equals("")
                            && !jsonObject.getString("APPSHIB_ID").equals("null")
                        ){
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * checkIfStaffRegistered() => checks if the user is registered;
     *
     * @return true/false
     */

    public boolean checkIfStaffRegistered() {
        Future<Boolean> futureResult = executorService.submit(new checkIfStaffRegistered());
        try {
            return futureResult.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private class checkIfStaffRegistered implements Callable<Boolean> {

        checkIfStaffRegistered() {
        }

        @Override
        public Boolean call() {

            try {

                String apiURL = "https://sp.data.alpha.jisc.ac.uk/staff";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();
                if (responseCode != 200) {
                    return false;
                }
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONObject jsonObject = new JSONObject(sb.toString());

                if (jsonObject.getString("APPSHIB_ID") != JSONObject.NULL && !jsonObject.getString("APPSHIB_ID").contentEquals(""))
                    return true;
                else
                    return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * downloadInstitution() => Downloads institutions and saves in database
     *
     * @return true/false => depends if it has successfully downloaded institutions
     */

    public boolean downloadInstitutions() {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<String> futureResult = executorService.submit(new downloadInstitutions());
        try {
            String result = futureResult.get();
            return result.equals("Success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private class downloadInstitutions implements Callable<String> {

        downloadInstitutions() {
        }

        @Override
        public String call() {
            try {
                String apiURL = "https://app.analytics.alpha.jisc.ac.uk/idps";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    if (responseCode == 204) {
                        new Delete().from(Institution.class).execute();
                    } else {
                    }
                    return "Error";
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONObject jsonObject = new JSONObject(sb.toString());

                ActiveAndroid.beginTransaction();
                new Delete().from(Institution.class).execute();
                try {
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        Institution institution = new Institution();
                        institution.name = key;
                        institution.url = jsonObject.getJSONObject(key).getString("url");
                        institution.ukprn = jsonObject.getJSONObject(key).getInt("ukprn");
                        institution.save();
                    }

                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return "Success";
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
        }
    }

    /**
     * getActivityHistory(String student_id)
     *
     * @param student_id => the ID of the student for which his list of activities to be retrieved
     * @return true/false if operation has succeed
     */
    public boolean getActivityHistory(String student_id) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new getActivityHistory(student_id));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getActivityHistory implements Callable<Boolean> {

        String student_id;

        getActivityHistory(String student_id) {
            this.student_id = student_id;
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_get_activity_logs?student_id=" + student_id + "&language=" + language
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.addRequestProperty("Authorization", "Bearer " + DataManager.getInstance().get_jwt());
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    if (responseCode == 204) {
                        new Delete().from(ActivityHistory.class).execute();
                    } else {

                    }

                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONArray jsonArray = new JSONArray(sb.toString());

                ActiveAndroid.beginTransaction();
                try {
                    new Delete().from(ActivityHistory.class).execute();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ActivityHistory activityHistory = new ActivityHistory();
                        activityHistory.id = jsonObject.getString("id");
                        activityHistory.student_id = jsonObject.getString("student_id");
                        activityHistory.module_id = jsonObject.getString("module");
                        activityHistory.activity_type = jsonObject.getString("activity_type");
                        activityHistory.activity = jsonObject.getString("activity");
                        activityHistory.activity_date = jsonObject.getString("activity_date");
                        activityHistory.time_spent = jsonObject.getString("time_spent");
                        activityHistory.note = jsonObject.getString("note");
                        activityHistory.created_date = jsonObject.getString("created_date");
                        activityHistory.modified_date = jsonObject.getString("modified_date");
//                        if(activityHistory.module_id.length() != 0 && activityHistory.activity_type.length() != 0 && activityHistory.activity.length() != 0)
                        activityHistory.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * getModules()
     *
     * @return true/false if operation has succeeded
     */
    public boolean getModules() {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new getModules());
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getModules implements Callable<Boolean> {

        getModules() {
        }

        @Override
        public Boolean call() {
            try {

                String apiURL = "https://app.analytics.alpha.jisc.ac.uk/v2/filter?"
                        + ((DataManager.getInstance().user.isSocial)?"&is_social=yes":"");

                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    if (responseCode == 204) {
                        Log.i("getModules", "No records found");
                        new Delete().from(Module.class).execute();
                    } else {

                    }

                    InputStream is = new BufferedInputStream(urlConnection.getErrorStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();

                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONObject jsonObject = new JSONObject(sb.toString());
                ActiveAndroid.beginTransaction();

                try {
                    new Delete().from(Module.class).execute();

                    if(!DataManager.getInstance().user.isStaff) {
                        JSONArray jsonObject2 = jsonObject.getJSONArray("modules");
                        for (int i = 0; i < jsonObject2.length(); i++) {

                            JSONObject object = jsonObject2.getJSONObject(i);

                            Module module = new Module();
                            module.id = object.keys().next();
                            module.name = object.getString(module.id);
                            module.save();
                        }
                    }

                    new Delete().from(Courses.class).execute();
                    JSONArray jsonObject3 = jsonObject.getJSONArray("courses");
                    for (int i = 0; i < jsonObject3.length(); i++) {

                        JSONObject object = jsonObject3.getJSONObject(i);

                        Courses course = new Courses();
                        course.id = object.keys().next();
                        course.name = object.getString(course.id);
                        course.save();

                    }

                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean getSocialModules() {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new getSocialModules());
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class getSocialModules implements Callable<Boolean> {

        getSocialModules() {
        }

        @Override
        public Boolean call() {
            try {

                String apiURL = host + "fn_get_modules?student_id="+DataManager.getInstance().user.jisc_student_id+"&language="+language;
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {
                    if (responseCode == 204) {
                        new Delete().from(Module.class).execute();
                    } else {
                    }

                    InputStream is = new BufferedInputStream(urlConnection.getErrorStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();

                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONArray jsonArray = new JSONArray(sb.toString());

                ActiveAndroid.beginTransaction();

                try {
                    new Delete().from(Module.class).execute();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Module modules = new Module();
                        String sModule = jsonArray.get(i).toString();

                        modules.id = sModule;
                        modules.name = sModule;

                        modules.save();
                    }

                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean addModule(HashMap<String, String> params) {
        language = LinguisticManager.getInstance().getLanguageCode();
        Future<Boolean> future = executorService.submit(new addModule(params));
        Future<Boolean> future1 = executorService.submit(new getSocialModules());
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private class addModule implements Callable<Boolean> {
        HashMap<String, String> params;

        addModule(HashMap<String, String> params) {
                params.put("language", language);
                this.params = params;
        }

        @Override
        public Boolean call() {
            try {
                String apiURL = host + "fn_add_module";
                URL url = new URL(apiURL);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.addRequestProperty("Authorization", DataManager.getInstance().get_jwt());
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setSSLSocketFactory(context.getSocketFactory());

                String urlParameters = "";
                Iterator it = params.entrySet().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (i == 0)
                        urlParameters += entry.getKey() + "=" + entry.getValue();
                    else
                        urlParameters += "&" + entry.getKey() + "=" + entry.getValue();
                }

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = urlConnection.getResponseCode();
                forbidden(responseCode);
                if (responseCode != 200) {

                    InputStream is = new BufferedInputStream(urlConnection.getErrorStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();

                    return false;
                }

                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                Module modules = new Module();

                modules.id = params.get("module");
                modules.name = params.get("module");

                modules.save();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private boolean forbidden (int code) {

        return true;

//        if (code == 401){
//            //cookie manager context
//            if (DataManager.getInstance().checkForbidden) {
//                CookieManager.getInstance().removeAllCookies(null);
//                DataManager.getInstance().set_jwt("");
//                new Delete().from(CurrentUser.class).execute();
//                DataManager.getInstance().toast = true;
//                DataManager.getInstance().checkForbidden = false;
//                Intent intent = new Intent(DataManager.getInstance().currActivity, LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                appContext.startActivity(intent);
//                DataManager.getInstance().currActivity.finish();
//                return false;
//            }
//        }
//        return true;
    }
}

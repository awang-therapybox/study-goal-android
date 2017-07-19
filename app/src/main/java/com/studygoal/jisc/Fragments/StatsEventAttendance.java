package com.studygoal.jisc.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.ModuleAdapter2;
import com.studygoal.jisc.MainActivity;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Models.Courses;
import com.studygoal.jisc.R;

import java.util.List;

public class StatsEventAttendance extends Fragment {

    private AppCompatTextView moduleTextView;
    private ListView actLisiview;

    static final String[] EVENTS = new String[] { "Calculate 101", "Calculate 102", "Calculate 103", "Calculate 101" };

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(getString(R.string.events_attended));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.stats_event_attendance, container, false);
        moduleTextView = (AppCompatTextView) mainView.findViewById(R.id.module_list);
        ((TextView) mainView.findViewById(R.id.module)).setTypeface(DataManager.getInstance().myriadpro_regular);

        setUpModule();

        actLisiview = (ListView) mainView.findViewById(R.id.event_attendance_listView);
        LayoutInflater i = getActivity().getLayoutInflater();
        ViewGroup header = (ViewGroup)i.inflate(R.layout.stats_event_attendance_list_view_header, actLisiview, false);
        actLisiview.addHeaderView(header, null, false);
        actLisiview.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.list_event_attendance, EVENTS));
        // ((MainActivity) getActivity()).showProgressBar(null);


        return mainView;
    }

    private void setUpModule() {
        moduleTextView.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        moduleTextView.setTypeface(DataManager.getInstance().myriadpro_regular);
        moduleTextView.setText(R.string.anymodule);
        moduleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.setCancelable(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((MainActivity) getActivity()).hideProgressBar();
                            }
                        });
                    }
                });

                if (DataManager.getInstance().mainActivity.isLandscape) {
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
                listView.setAdapter(new ModuleAdapter2(DataManager.getInstance().mainActivity, moduleTextView.getText().toString()));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String titleText = ((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString();

                        List<Courses> coursesList = new Select().from(Courses.class).execute();

                        for (int j = 0; j < coursesList.size(); j++) {
                            String courseName = coursesList.get(j).name;
                            if(courseName.equals(titleText)) {
                                return;
                            }
                        }

                        dialog.dismiss();
                        moduleTextView.setText(titleText);
                    }
                });
                ((MainActivity) getActivity()).showProgressBar2("");
                dialog.show();
            }
        });
    }

    private void hideProgressBar() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        getData();
//                        ((MainActivity) getActivity()).hideProgressBar();
//                    }
//                });
//            }
//        }).start();
        }

 }

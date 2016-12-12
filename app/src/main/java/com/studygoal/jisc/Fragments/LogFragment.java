//package com.studygoal.jisc.Fragments;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.studygoal.jisc.MainActivity;
//import com.studygoal.jisc.Managers.DataManager;
//import com.studygoal.jisc.R;
//
//public class LogFragment extends Fragment {
//
//    public LogFragment() {}
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.log_title));
//        DataManager.getInstance().mainActivity.hideAllButtons();
//        DataManager.getInstance().mainActivity.showCertainButtons(3);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View mainView = inflater.inflate(R.layout.log_fragment, container, false);
//
//        //Set the typeface of the buttons
//        ((TextView)mainView.findViewById(R.id.new_activity_btn_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
//        ((TextView)mainView.findViewById(R.id.log_activity_btn_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
//        ((TextView)mainView.findViewById(R.id.view_activities_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
//
//        SharedPreferences saves = DataManager.getInstance().mainActivity.getSharedPreferences("jisc", Context.MODE_PRIVATE);
//        long timestamp = saves.getLong("timer", 0);
//
//        if(timestamp > 0)
//            ((TextView) mainView.findViewById(R.id.new_activity_btn_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.running_activity));
//        else
//            ((TextView) mainView.findViewById(R.id.new_activity_btn_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.new_activity));
//
//
//        mainView.findViewById(R.id.new_activity_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.main_fragment, new LogNewActivity(), "newActivity")
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });
//        mainView.findViewById(R.id.log_activity_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogLogActivity fragment = new LogLogActivity();
//                fragment.isInEditMode = false;
//                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.main_fragment, fragment)
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });
//        mainView.findViewById(R.id.view_activities).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.main_fragment, new LogActivityHistory())
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });
//
//
//        return mainView;
//    }
//}

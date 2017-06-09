package com.studygoal.jisc.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.LinguisticManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.SocialManager;
import com.studygoal.jisc.R;

public class StatsPoints extends Fragment {

    View mainView;
    SwipeRefreshLayout layout;

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.points));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);

        if (!DataManager.getInstance().mainActivity.isLandscape) {
            ((TextView) mainView.findViewById(R.id.overall)).setText(getActivity().getString(R.string.overall));
            ((TextView) mainView.findViewById(R.id.this_week)).setText(getActivity().getString(R.string.this_week));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String _number = NetworkManager.getInstance().getCurrentRanking(DataManager.getInstance().user.id);
                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.rankingImage(_number)).into((ImageView) mainView.findViewById(R.id.first_table_image));
                            ((TextView) mainView.findViewById(R.id.first_table_title)).setText(LinguisticManager.convertRanking(_number));
                            String _number2 = _number;
                            _number2 = _number2.replace("%", "");
                            if (Integer.parseInt(_number) > 10)
                                _number2 = (100 - Integer.parseInt(_number2)) + "";
                            else
                                _number2 = DataManager.getInstance().mainActivity.getString(R.string.top) + " " + _number2;
                            final String text = DataManager.getInstance().mainActivity.getString(R.string.you_are_in_top).replace("@@", _number2);
                            ((TextView) mainView.findViewById(R.id.first_table_description)).setText(text);
                        }
                    });
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String _number = NetworkManager.getInstance().getCurrentOverallRanking(DataManager.getInstance().user.id);
                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.rankingImage(_number)).into((ImageView) mainView.findViewById(R.id.second_table_image));
                            ((TextView) mainView.findViewById(R.id.second_table_title)).setText(LinguisticManager.convertRanking(_number));
                            String _number2 = _number;
                            _number2 = _number2.replace("%", "");
                            if (Integer.parseInt(_number) > 10)
                                _number2 = (100 - Integer.parseInt(_number2)) + "";
                            else
                                _number2 = DataManager.getInstance().mainActivity.getString(R.string.top) + " " + _number2;
                            final String text2 = DataManager.getInstance().mainActivity.getString(R.string.you_are_in_top).replace("@@", _number2);
                            ((TextView) mainView.findViewById(R.id.second_table_description)).setText(text2);

                        }
                    });
                }
            }).start();
        } else {
            ((TextView) mainView.findViewById(R.id.overall2)).setText(getActivity().getString(R.string.overall));
            ((TextView) mainView.findViewById(R.id.this_week2)).setText(getActivity().getString(R.string.this_week));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    NetworkManager.getInstance().getStudentActivityPoint();
                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) mainView.findViewById(R.id.this_week_ap)).setText(DataManager.getInstance().user.last_week_activity_points + " " + DataManager.getInstance().mainActivity.getString(R.string.activity_points));
                            ((TextView) mainView.findViewById(R.id.overall_ap)).setText(DataManager.getInstance().user.overall_activity_points + " " + DataManager.getInstance().mainActivity.getString(R.string.activity_points));
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.stats_points, container, false);

        layout = (SwipeRefreshLayout) mainView.findViewById(R.id.stats_swipe_refresh);

        layout.setColorSchemeResources(R.color.colorPrimary);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!DataManager.getInstance().mainActivity.isLandscape) {
                    ((TextView) mainView.findViewById(R.id.overall)).setText(getActivity().getString(R.string.overall));
                    ((TextView) mainView.findViewById(R.id.this_week)).setText(getActivity().getString(R.string.this_week));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String _number = NetworkManager.getInstance().getCurrentRanking(DataManager.getInstance().user.id);
                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.rankingImage(_number)).into((ImageView) mainView.findViewById(R.id.first_table_image));
                                    ((TextView) mainView.findViewById(R.id.first_table_title)).setText(LinguisticManager.convertRanking(_number));
                                    String _number2 = _number;
                                    _number2 = _number2.replace("%", "");
                                    if (Integer.parseInt(_number) > 10)
                                        _number2 = (100 - Integer.parseInt(_number2)) + "";
                                    else
                                        _number2 = DataManager.getInstance().mainActivity.getString(R.string.top) + " " + _number2;
                                    final String text = DataManager.getInstance().mainActivity.getString(R.string.you_are_in_top).replace("@@", _number2);
                                    ((TextView) mainView.findViewById(R.id.first_table_description)).setText(text);
                                    call_refresh();
                                }
                            });
                        }
                    }).start();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String _number = NetworkManager.getInstance().getCurrentOverallRanking(DataManager.getInstance().user.id);
                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.rankingImage(_number)).into((ImageView) mainView.findViewById(R.id.second_table_image));
                                    ((TextView) mainView.findViewById(R.id.second_table_title)).setText(LinguisticManager.convertRanking(_number));
                                    String _number2 = _number;
                                    _number2 = _number2.replace("%", "");
                                    if (Integer.parseInt(_number) > 10)
                                        _number2 = (100 - Integer.parseInt(_number2)) + "";
                                    else
                                        _number2 = DataManager.getInstance().mainActivity.getString(R.string.top) + " " + _number2;
                                    final String text2 = DataManager.getInstance().mainActivity.getString(R.string.you_are_in_top).replace("@@", _number2);
                                    ((TextView) mainView.findViewById(R.id.second_table_description)).setText(text2);
                                    call_refresh();
                                }
                            });
                        }
                    }).start();
                } else {
                    ((TextView) mainView.findViewById(R.id.overall2)).setText(getActivity().getString(R.string.overall));
                    ((TextView) mainView.findViewById(R.id.this_week2)).setText(getActivity().getString(R.string.this_week));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            NetworkManager.getInstance().getStudentActivityPoint();
                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((TextView) mainView.findViewById(R.id.this_week_ap)).setText(DataManager.getInstance().user.last_week_activity_points + " " + DataManager.getInstance().mainActivity.getString(R.string.activity_points));
                                    ((TextView) mainView.findViewById(R.id.overall_ap)).setText(DataManager.getInstance().user.overall_activity_points + " " + DataManager.getInstance().mainActivity.getString(R.string.activity_points));
                                    call_refresh();
                                }
                            });
                        }
                    }).start();
                }
            }
        });


        mainView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.getInstance().shareOnIntent(((TextView) mainView.findViewById(R.id.this_week)).getText().toString());
            }
        });

        mainView.findViewById(R.id.share2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.getInstance().shareOnIntent(((TextView) mainView.findViewById(R.id.overall_ap)).getText().toString());
            }
        });


        if (!DataManager.getInstance().mainActivity.isLandscape) {

            ((TextView) mainView.findViewById(R.id.first_table_title)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.first_table_description)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.first_table_description)).setText("");
            ((TextView) mainView.findViewById(R.id.second_table_title)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.second_table_description)).setTypeface(DataManager.getInstance().myriadpro_regular);

            ((TextView) mainView.findViewById(R.id.this_week)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.overall)).setTypeface(DataManager.getInstance().myriadpro_regular);

            ((TextView) mainView.findViewById(R.id.overall)).setText(getActivity().getString(R.string.overall));

        } else {

            TextView this_week_ap = (TextView) mainView.findViewById(R.id.this_week_ap);
            if (this_week_ap != null) {
                this_week_ap.setTypeface(DataManager.getInstance().myriadpro_regular);
            }
            TextView this_week2 = (TextView) mainView.findViewById(R.id.this_week2);
            if (this_week2 != null) {
                this_week2.setTypeface(DataManager.getInstance().myriadpro_regular);
            }
            TextView overall2 = (TextView) mainView.findViewById(R.id.overall2);
            if (overall2 != null) {
                overall2.setTypeface(DataManager.getInstance().myriadpro_regular);
            }
            TextView overall_ap = (TextView) mainView.findViewById(R.id.overall_ap);
            if (overall_ap != null) {
                overall_ap.setTypeface(DataManager.getInstance().myriadpro_regular);
            }
        }

        final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (DataManager.getInstance().user.isStaff && preferences.getBoolean("stats_alert", true)) {

            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage(R.string.statistics_admin_view);
            alertDialogBuilder.setPositiveButton("Don't show again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("stats_alert", false);
                    editor.apply();
                }
            });
            alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return mainView;
    }

    private void call_refresh() {
        layout.setRefreshing(false);
    }
}

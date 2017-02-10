package com.studygoal.jisc.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.AttainmentAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.SocialManager;
import com.studygoal.jisc.Models.Attainment;
import com.studygoal.jisc.R;

import java.util.Timer;

public class Stats2 extends Fragment {

    private Timer timer;
    private Timer timer2;
    ListView list;
    AttainmentAdapter adapter;
    View mainView;
    private int contor;
    SwipeRefreshLayout layout;


    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.stats_title));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkManager.getInstance().getAssignmentRanking();
                adapter.list = new Select().from(Attainment.class).execute();
                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.stats2, container, false);

        ((TextView) mainView.findViewById(R.id.graphs)).setTypeface(DataManager.getInstance().myriadpro_regular);

        ((TextView) mainView.findViewById(R.id.title)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.activity_points_1_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        final TextView activity_points_thisweek;
        (activity_points_thisweek = (TextView) mainView.findViewById(R.id.activity_points_1)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.activity_points_2_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        final TextView activity_points_overall;
        (activity_points_overall = (TextView) mainView.findViewById(R.id.activity_points_2)).setTypeface(DataManager.getInstance().myriadpro_regular);

        NetworkManager.getInstance().getStudentActivityPoint();
        activity_points_thisweek.setText(DataManager.getInstance().user.last_week_activity_points);
        activity_points_overall.setText(DataManager.getInstance().user.overall_activity_points);

        ((TextView) mainView.findViewById(R.id.this_week)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.overall)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.attainment)).setTypeface(DataManager.getInstance().myriadpro_regular);

        layout = (SwipeRefreshLayout) mainView.findViewById(R.id.stats_swipe_refresh);

        layout.setColorSchemeResources(R.color.colorPrimary);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NetworkManager.getInstance().getStudentActivityPoint();
                        NetworkManager.getInstance().getAssignmentRanking();

                        DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity_points_thisweek.setText(DataManager.getInstance().user.last_week_activity_points);
                                activity_points_overall.setText(DataManager.getInstance().user.overall_activity_points);

                                adapter.list = new Select().from(Attainment.class).execute();
                                adapter.notifyDataSetChanged();
                                layout.setRefreshing(false);
                            }
                        });
                    }
                }).start();

            }
        });

        //Lista pt attainment + refresh
        list = (ListView) mainView.findViewById(R.id.list);
        list.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        adapter = new AttainmentAdapter(DataManager.getInstance().mainActivity);
        list.setAdapter(adapter);
        //

        mainView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.getInstance().mainActivity.onBackPressed();
            }
        });
        mainView.findViewById(R.id.graph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new Stats3())
                        .addToBackStack(null)
                        .commit();
            }
        });


        mainView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.setVisibility(View.GONE);
                SocialManager.getInstance().shareOnFacebook(((TextView) mainView.findViewById(R.id.activity_points_1)).getText().toString() + " " + ((TextView) mainView.findViewById(R.id.activity_points_1_text)).getText().toString());
//                mainView.findViewById(R.id.share_layout).setVisibility(View.VISIBLE);
//                timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mainView.findViewById(R.id.share_layout).setVisibility(View.GONE);
//                                mainView.findViewById(R.id.share).setVisibility(View.VISIBLE);
//                            }
//                        });
//                    }
//                }, 3000);
            }
        });

        mainView.findViewById(R.id.share2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.setVisibility(View.GONE);
                SocialManager.getInstance().shareOnFacebook(((TextView) mainView.findViewById(R.id.activity_points_2)).getText().toString() + " " + ((TextView) mainView.findViewById(R.id.activity_points_2_text)).getText().toString());
//                mainView.findViewById(R.id.share_layout2).setVisibility(View.VISIBLE);
//                timer2 = new Timer();
//                timer2.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mainView.findViewById(R.id.share_layout2).setVisibility(View.GONE);
//                                mainView.findViewById(R.id.share2).setVisibility(View.VISIBLE);
//                            }
//                        });
//                    }
//                }, 3000);
            }
        });

            mainView.findViewById(R.id.facebook).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnFacebook(((TextView) mainView.findViewById(R.id.activity_points_1)).getText().toString() + " " + ((TextView) mainView.findViewById(R.id.activity_points_1_text)).getText().toString());
                }
            });

            mainView.findViewById(R.id.facebook2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnFacebook(((TextView) mainView.findViewById(R.id.activity_points_2)).getText().toString() + " " + ((TextView) mainView.findViewById(R.id.activity_points_2_text)).getText().toString());
                }
            });
            mainView.findViewById(R.id.twitter).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnTwitter(((TextView) mainView.findViewById(R.id.activity_points_1)).getText().toString() + " " + ((TextView) mainView.findViewById(R.id.activity_points_1_text)).getText().toString());
                }
            });
            mainView.findViewById(R.id.twitter2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnTwitter(((TextView) mainView.findViewById(R.id.activity_points_2)).getText().toString() + " " + ((TextView) mainView.findViewById(R.id.activity_points_2_text)).getText().toString());
                }
            });
            mainView.findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnEmail(((TextView) mainView.findViewById(R.id.activity_points_1)).getText().toString() + " " + ((TextView) mainView.findViewById(R.id.activity_points_1_text)).getText().toString());
                }
            });
            mainView.findViewById(R.id.email2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnEmail(((TextView) mainView.findViewById(R.id.activity_points_2)).getText().toString() + " " + ((TextView) mainView.findViewById(R.id.activity_points_2_text)).getText().toString());
                }
            });

        mainView.findViewById(R.id.next).setVisibility(View.INVISIBLE);
//        mainView.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.main_fragment, new Stats3())
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });

        if(DataManager.getInstance().user.isStaff) {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage(R.string.statistics_admin_view);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(timer != null)
            timer.cancel();
        if(timer2 != null)
            timer2.cancel();
    }
}

package com.studygoal.jisc.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.google.android.gms.vision.text.Text;
import com.studygoal.jisc.Adapters.AttainmentAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.LinguisticManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.SocialManager;
import com.studygoal.jisc.Models.Attainment;
import com.studygoal.jisc.R;

import java.util.Timer;
import java.util.TimerTask;

public class Stats extends Fragment {

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
                for(int i = 0; i < adapter.list.size(); i++) {

                    Attainment attainment = adapter.list.get(i);

                    if(attainment.percent.length() > 1
                            && Integer.parseInt(attainment.percent.substring(0, attainment.percent.length()-1))==0)
                        adapter.list.remove(i);
                }

                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

        if(!DataManager.getInstance().mainActivity.isLandscape) {
            ((TextView) mainView.findViewById(R.id.overall)).setText(getActivity().getString(R.string.overall));
            ((TextView) mainView.findViewById(R.id.this_week)).setText(getActivity().getString(R.string.this_week));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String _number = NetworkManager.getInstance().getCurrentRanking(DataManager.getInstance().user.id);
                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.rankingImage(_number)).into((ImageView)mainView.findViewById(R.id.first_table_image));
                            ((TextView) mainView.findViewById(R.id.first_table_title)).setText(LinguisticManager.convertRanking(_number));
                            String _number2 = _number;
                            _number2 = _number2.replace("%", "");
                            if(Integer.parseInt(_number) > 10)
                                _number2 = (100-Integer.parseInt(_number2)) + "";
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
                            Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.rankingImage(_number)).into((ImageView)mainView.findViewById(R.id.second_table_image));
                            ((TextView) mainView.findViewById(R.id.second_table_title)).setText(LinguisticManager.convertRanking(_number));
                            String _number2 = _number;
                            _number2 = _number2.replace("%", "");
                            if(Integer.parseInt(_number) > 10)
                                _number2 = (100-Integer.parseInt(_number2)) + "";
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
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    final String _number = NetworkManager.getInstance().getCurrentRanking(DataManager.getInstance().user.id);
//                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.rankingImage(_number)).into((ImageView)mainView.findViewById(R.id.engagement_image));
//                                ((TextView) mainView.findViewById(R.id.engagement_type)).setText(LinguisticManager.convertRanking(_number));
//                                String _number2 = _number;
//                                _number2 = _number2.replace("%", "");
//                                if (Integer.parseInt(_number) > 10)
//                                    _number2 = (100 - Integer.parseInt(_number2)) + "";
//                                else
//                                    _number2 = getActivity().getString(R.string.top) + " " + _number2;
//                                final String text2 = DataManager.getInstance().mainActivity.getString(R.string.you_are_in_top).replace("@@", _number2);
//                                ((TextView) mainView.findViewById(R.id.engagement_percent)).setText(text2);
//                            } catch (Exception ignored) {}
//                        }
//                    });
//                }
//            }).start();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    final String _number = NetworkManager.getInstance().getCurrentOverallRanking(DataManager.getInstance().user.id);
//                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.rankingImage(_number)).into((ImageView)mainView.findViewById(R.id.engagement_image2));
//                                ((TextView) mainView.findViewById(R.id.engagement_type2)).setText(LinguisticManager.convertRanking(_number));
//                                String _number2 = _number;
//                                _number2 = _number2.replace("%", "");
//                                if (Integer.parseInt(_number) > 10)
//                                    _number2 = (100 - Integer.parseInt(_number2)) + "";
//                                else
//                                    _number2 = getActivity().getString(R.string.top) + " " + _number2;
//                                final String text2 = DataManager.getInstance().mainActivity.getString(R.string.you_are_in_top).replace("@@", _number2);
//                                ((TextView) mainView.findViewById(R.id.engagement_percent2)).setText(text2);
//                            } catch (Exception ignored) {}
//
//                        }
//                    });
//                }
//            }).start();
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

        ((TextView)mainView.findViewById(R.id.title)).setText(getActivity().getString(R.string.course_engagement));
        ((TextView)mainView.findViewById(R.id.attainment)).setText(getActivity().getString(R.string.attainment));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.stats, container, false);

        layout = (SwipeRefreshLayout) mainView.findViewById(R.id.stats_swipe_refresh);

        layout.setColorSchemeResources(R.color.colorPrimary);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                contor = 0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NetworkManager.getInstance().getAssignmentRanking();
                        adapter.list = new Select().from(Attainment.class).execute();
                        for(int i = 0; i < adapter.list.size(); i++) {
                            if(Integer.parseInt(adapter.list.get(i).percent.substring(0, adapter.list.get(i).percent.length()-1))==0) adapter.list.remove(i);
                        }
                        DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                call_refresh();
                            }
                        });
                    }
                }).start();

                if(!DataManager.getInstance().mainActivity.isLandscape) {
                    ((TextView) mainView.findViewById(R.id.overall)).setText(getActivity().getString(R.string.overall));
                    ((TextView) mainView.findViewById(R.id.this_week)).setText(getActivity().getString(R.string.this_week));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String _number = NetworkManager.getInstance().getCurrentRanking(DataManager.getInstance().user.id);
                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.rankingImage(_number)).into((ImageView)mainView.findViewById(R.id.first_table_image));
                                    ((TextView) mainView.findViewById(R.id.first_table_title)).setText(LinguisticManager.convertRanking(_number));
                                    String _number2 = _number;
                                    _number2 = _number2.replace("%", "");
                                    if(Integer.parseInt(_number) > 10)
                                        _number2 = (100-Integer.parseInt(_number2)) + "";
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
                                    Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.rankingImage(_number)).into((ImageView)mainView.findViewById(R.id.second_table_image));
                                    ((TextView) mainView.findViewById(R.id.second_table_title)).setText(LinguisticManager.convertRanking(_number));
                                    String _number2 = _number;
                                    _number2 = _number2.replace("%", "");
                                    if(Integer.parseInt(_number) > 10)
                                        _number2 = (100-Integer.parseInt(_number2)) + "";
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

                ((TextView)mainView.findViewById(R.id.title)).setText(getActivity().getString(R.string.course_engagement));
                ((TextView)mainView.findViewById(R.id.attainment)).setText(getActivity().getString(R.string.attainment));
            }
        });

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

        mainView.findViewById(R.id.graph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new Stats3())
                        .addToBackStack(null)
                        .commit();
            }
        });

        if(DataManager.getInstance().mainActivity.isLandscape) {
            mainView.findViewById(R.id.facebook).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnFacebook(((TextView) mainView.findViewById(R.id.this_week_ap)).getText().toString());
                }
            });

            mainView.findViewById(R.id.facebook2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnFacebook(((TextView) mainView.findViewById(R.id.overall_ap)).getText().toString());
                }
            });
            mainView.findViewById(R.id.twitter).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnTwitter(((TextView) mainView.findViewById(R.id.this_week_ap)).getText().toString());
                }
            });
            mainView.findViewById(R.id.twitter2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnTwitter(((TextView) mainView.findViewById(R.id.overall_ap)).getText().toString());
                }
            });
            mainView.findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnEmail(((TextView) mainView.findViewById(R.id.this_week_ap)).getText().toString());
                }
            });
            mainView.findViewById(R.id.email2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnEmail(((TextView) mainView.findViewById(R.id.overall_ap)).getText().toString());
                }
            });

            mainView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnFacebook(((TextView) mainView.findViewById(R.id.overall_ap)).getText().toString());
//                    v.setVisibility(View.GONE);
//                    mainView.findViewById(R.id.share_layout).setVisibility(View.VISIBLE);
//                    timer = new Timer();
//                    timer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mainView.findViewById(R.id.share_layout).setVisibility(View.GONE);
//                                    mainView.findViewById(R.id.share).setVisibility(View.VISIBLE);
//                                }
//                            });
//                        }
//                    }, 3000);
                }
            });

            mainView.findViewById(R.id.share2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    mainView.findViewById(R.id.share_layout2).setVisibility(View.VISIBLE);
                    timer2 = new Timer();
                    timer2.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mainView.findViewById(R.id.share_layout2).setVisibility(View.GONE);
                                    mainView.findViewById(R.id.share2).setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }, 3000);
                }
            });
        } else {
            mainView.findViewById(R.id.facebook).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnFacebook(((TextView) mainView.findViewById(R.id.first_table_description)).getText().toString());
                }
            });

            mainView.findViewById(R.id.facebook2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnFacebook(((TextView) mainView.findViewById(R.id.second_table_description)).getText().toString());
                }
            });
            mainView.findViewById(R.id.twitter).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnTwitter(((TextView) mainView.findViewById(R.id.first_table_description)).getText().toString());
                }
            });
            mainView.findViewById(R.id.twitter2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnTwitter(((TextView) mainView.findViewById(R.id.second_table_description)).getText().toString());
                }
            });
            mainView.findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnEmail(((TextView) mainView.findViewById(R.id.first_table_description)).getText().toString());
                }
            });
            mainView.findViewById(R.id.email2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnEmail(((TextView) mainView.findViewById(R.id.second_table_description)).getText().toString());
                }
            });

            mainView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    mainView.findViewById(R.id.share_layout).setVisibility(View.VISIBLE);
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mainView.findViewById(R.id.share_layout).setVisibility(View.GONE);
                                    mainView.findViewById(R.id.share).setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }, 3000);
                }
            });

            mainView.findViewById(R.id.share2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    mainView.findViewById(R.id.share_layout2).setVisibility(View.VISIBLE);
                    timer2 = new Timer();
                    timer2.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mainView.findViewById(R.id.share_layout2).setVisibility(View.GONE);
                                    mainView.findViewById(R.id.share2).setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }, 3000);
                }
            });

        }

        if(!DataManager.getInstance().mainActivity.isLandscape) {
            ((TextView) mainView.findViewById(R.id.graphs)).setTypeface(DataManager.getInstance().myriadpro_regular);

            ((TextView) mainView.findViewById(R.id.title)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.first_table_title)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.first_table_description)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.first_table_description)).setText("");
            ((TextView) mainView.findViewById(R.id.second_table_title)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.second_table_description)).setTypeface(DataManager.getInstance().myriadpro_regular);

            ((TextView) mainView.findViewById(R.id.this_week)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.overall)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.attainment)).setTypeface(DataManager.getInstance().myriadpro_regular);

            ((TextView) mainView.findViewById(R.id.this_week)).setText(getActivity().getString(R.string.this_week));
            ((TextView) mainView.findViewById(R.id.overall)).setText(getActivity().getString(R.string.overall));
            ((TextView) mainView.findViewById(R.id.attainment)).setText(getActivity().getString(R.string.attainment));


            mainView.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment, new Stats2())
                            .addToBackStack(null)
                            .commit();
                }
            });
        } else {

            ((TextView) mainView.findViewById(R.id.this_week_ap)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.this_week2)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.overall2)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.overall_ap)).setTypeface(DataManager.getInstance().myriadpro_regular);

        }

        final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        if(DataManager.getInstance().user.isStaff && preferences.getBoolean("stats_alert",true)) {

            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage(R.string.statistics_admin_view);
            alertDialogBuilder.setPositiveButton("Don't show again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("stats_alert",false);
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
        contor++;
        if(!DataManager.getInstance().mainActivity.isLandscape) {
            if(contor == 3) layout.setRefreshing(false);
        } else if(contor == 2) layout.setRefreshing(false);
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

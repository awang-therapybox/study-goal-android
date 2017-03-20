package com.studygoal.jisc.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.TargetAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.Targets;
import com.studygoal.jisc.R;

import java.util.HashMap;

public class TargetFragment extends Fragment {

    public ListView list;
    public TargetAdapter adapter;
    public View mainView, tutorial_message;
    SwipeRefreshLayout layout;

    public TargetFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.target));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(4);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DataManager.getInstance().mainActivity.showProgressBar(null);
                    }
                });
                NetworkManager.getInstance().getStretchTargets(DataManager.getInstance().user.id);
                NetworkManager.getInstance().getTargets(DataManager.getInstance().user.id);

                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.list = new Select().from(Targets.class).execute();
                        adapter.notifyDataSetChanged();
                        if(adapter.list.size() == 0)
                            tutorial_message.setVisibility(View.VISIBLE);
                        else
                            tutorial_message.setVisibility(View.GONE);
                        DataManager.getInstance().mainActivity.hideProgressBar();
                    }
                });

            }
        }).start();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.target_fragment, container, false);

        tutorial_message = mainView.findViewById(R.id.tutorial_message);
        layout = (SwipeRefreshLayout) mainView.findViewById(R.id.swipelayout);
        list = (ListView) mainView.findViewById(R.id.list);

        adapter = new TargetAdapter(this);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TargetDetails fragment = new TargetDetails();
                fragment.list = adapter.list;
                fragment.position = position;
                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        layout.setColorSchemeResources(R.color.colorPrimary);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NetworkManager.getInstance().getStretchTargets(DataManager.getInstance().user.id);
                        if(NetworkManager.getInstance().getTargets(DataManager.getInstance().user.id)) {
                            adapter.list = new Select().from(Targets.class).execute();
                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    layout.setRefreshing(false);
                                    if(adapter.list.size() == 0)
                                        tutorial_message.setVisibility(View.VISIBLE);
                                    else
                                        tutorial_message.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    layout.setRefreshing(false);
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        return mainView;
    }

    public void deleteTarget(final Targets target, final int finalPosition) {

        if(DataManager.getInstance().user.isDemo) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TargetFragment.this.getActivity());
            alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.demo_mode_deletetarget) + "</font>"));
            alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return;
        }

        final HashMap<String, String> params = new HashMap<>();
        params.put("student_id", DataManager.getInstance().user.id);
        params.put("target_id", target.target_id);
        DataManager.getInstance().mainActivity.showProgressBar(null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(NetworkManager.getInstance().deleteTarget(params)) {
                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            target.delete();
                            adapter.list.remove(finalPosition);
                            if(adapter.list.size() == 0)
                                tutorial_message.setVisibility(View.VISIBLE);
                            else
                                tutorial_message.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                            DataManager.getInstance().mainActivity.hideProgressBar();
                            Snackbar.make(mainView.findViewById(R.id.parent), R.string.target_deleted_successfully, Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DataManager.getInstance().mainActivity.hideProgressBar();
                            Snackbar.make(mainView.findViewById(R.id.parent), R.string.fail_to_delete_target_message, Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();

    }
}

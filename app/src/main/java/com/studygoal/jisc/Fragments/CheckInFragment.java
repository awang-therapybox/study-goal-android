package com.studygoal.jisc.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.studygoal.jisc.Adapters.ActivitiesHistoryAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.R;

public class CheckInFragment extends Fragment {

    View mainView;
    ListView list;
    ActivitiesHistoryAdapter adapter;
    SwipeRefreshLayout layout;

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) mainView.findViewById(R.id.pin_text_edit)).setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.checkin_fragment, container, false);

        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.check_in));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);

        final TextView pin_text_edit = (TextView) mainView.findViewById(R.id.pin_text_edit);
        pin_text_edit.setTypeface(DataManager.getInstance().oratorstd_typeface);

        ((TextView) mainView.findViewById(R.id.pin_send_button)).setTypeface(DataManager.getInstance().oratorstd_typeface);
        mainView.findViewById(R.id.pin_send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pin_text_edit_text = pin_text_edit.getText().toString();
                if(pin_text_edit_text.length() == 0) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CheckInFragment.this.getActivity());
                    alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.alert_invalid_pin) + "</font>"));
                    alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean result = NetworkManager.getInstance().setUserPin(pin_text_edit_text, "LOCATION");
                        CheckInFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String message;
                                if(result) {
                                    message = CheckInFragment.this.getActivity().getString(R.string.alert_valid_pin);
                                } else {
                                    message = CheckInFragment.this.getActivity().getString(R.string.alert_invalid_pin);
                                }

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CheckInFragment.this.getActivity());
                                alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + message + "</font>"));
                                alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        });
                    }
                }).start();
            }
        });


        GridLayout grid = (GridLayout) mainView.findViewById(R.id.grid_layout);
        int childCount = grid.getChildCount();

        for (int i= 0; i < childCount; i++){
            if(grid.getChildAt(i) instanceof ImageView) {
                final ImageView text = (ImageView) grid.getChildAt(i);
                text.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        // your click code here
                        String pin_text_edit_text = pin_text_edit.getText().toString();
                        if(pin_text_edit_text.length()>0)
                            pin_text_edit.setText(pin_text_edit_text.substring(0,pin_text_edit_text.length()-1));
                    }
                });
            } else {
                final TextView text = (TextView) grid.getChildAt(i);
                text.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        // your click code here
                        pin_text_edit.setText(pin_text_edit.getText().toString() + text.getText().toString());
                    }
                });
            }
        }

        return mainView;
    }
}
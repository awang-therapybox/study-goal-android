package com.studygoal.jisc.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.TargetPagerAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.Targets;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.PageControl;

import java.util.HashMap;
import java.util.List;

public class TargetDetails extends Fragment {

    View mainView;
    ViewPager pager;
    TargetPagerAdapter mAdapter;

    public List<Targets> list;
    public int position;

    public TargetDetails() {
    }

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.target));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(4);
        if(pager != null && mAdapter != null && mAdapter.list.size() != new Select().from(Targets.class).count()) {
                mAdapter = new TargetPagerAdapter(DataManager.getInstance().mainActivity.getSupportFragmentManager());
                mAdapter.reference = this;
                mAdapter.list = new Select().from(Targets.class).execute();
                mAdapter.notifyDataSetChanged();
                pager.setAdapter(mAdapter);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.target_details, container, false);

        pager = (ViewPager) mainView.findViewById(R.id.pager);
        mAdapter = new TargetPagerAdapter(DataManager.getInstance().mainActivity.getSupportFragmentManager());
        mAdapter.reference = this;
        if(list == null) {
            mAdapter.list = new Select().from(Targets.class).execute();
        } else {
            mAdapter.list = list;
        }

        pager.setAdapter(mAdapter);
        pager.setCurrentItem(position);

        final PageControl pageControl = (PageControl) mainView.findViewById(R.id.page_control);
        pageControl.setPageCount(list.size());
        pageControl.setActiveDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dot_active));
        pageControl.setInactiveDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dot_inactive));
        pageControl.setCurrentPage(position);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pageControl.setCurrentPage(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return mainView;

    }

    public void deleteTarget(final Targets target, final int finalPosition) {

        if(DataManager.getInstance().user.isDemo) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TargetDetails.this.getActivity());
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
                            mAdapter.list.remove(finalPosition);
                            mAdapter.notifyDataSetChanged();
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

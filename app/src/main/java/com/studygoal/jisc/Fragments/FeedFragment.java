package com.studygoal.jisc.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.FeedAdapter;
import com.studygoal.jisc.Adapters.GenericAdapter;
import com.studygoal.jisc.MainActivity;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.Feed;
import com.studygoal.jisc.Models.Friend;
import com.studygoal.jisc.Models.News;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.EditTextCustom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedFragment extends Fragment {

    public View mainView, tutorial_message;
    FeedAdapter adapter;
    SwipeRefreshLayout layout;

    public FeedFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();

        DataManager.getInstance().mainActivity.setTitle(getString(R.string.feed));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DataManager.getInstance().mainActivity.showProgressBar(null);
                    }
                });

                NetworkManager.getInstance().getNewsFeed();
                NetworkManager.getInstance().getFeed(DataManager.getInstance().user.id);
                adapter.feedList = new Select().from(Feed.class).where("is_hidden = 0").execute();
                adapter.newsList = new Select().from(News.class).where("read = 0").execute();

                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        if (adapter.feedList.size() == 0)
                            tutorial_message.setVisibility(View.VISIBLE);
                        else
                            tutorial_message.setVisibility(View.INVISIBLE);
                        DataManager.getInstance().mainActivity.hideProgressBar();
                    }
                });
            }
        }).start();
    }

    public void post() {
        String message = ((EditText) mainView.findViewById(R.id.message)).getText().toString();
        HashMap<String, String> map = new HashMap<>();
        map.put("student_id", DataManager.getInstance().user.id);
        map.put("message", message);
        if (NetworkManager.getInstance().postFeedMessage(map)) {
            if (NetworkManager.getInstance().getFeed(DataManager.getInstance().user.id)) {
                NetworkManager.getInstance().getNewsFeed();
                adapter.feedList = new Select().from(Feed.class).where("is_hidden = 0").execute();
                adapter.newsList = new Select().from(News.class).where("read = 0").execute();
                adapter.notifyDataSetChanged();
            }
            Snackbar.make(layout, R.string.posted_message, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(layout, R.string.fail_to_post_message, Snackbar.LENGTH_LONG).show();
        }
        mainView.findViewById(R.id.overlay).callOnClick();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.feed_fragment, container, false);
        layout = (SwipeRefreshLayout) mainView.findViewById(R.id.swipelayout);
        tutorial_message = mainView.findViewById(R.id.tutorial_message);

        final EditTextCustom myEditText = (EditTextCustom) mainView.findViewById(R.id.message);
        myEditText.fragment = FeedFragment.this;

        final View floating_btn = mainView.findViewById(R.id.floating_btn);
        floating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.getInstance().mainActivity.feedFragment = FeedFragment.this;
                DataManager.getInstance().mainActivity.hideAllButtons();
                DataManager.getInstance().mainActivity.showCertainButtons(6);
                floating_btn.setVisibility(View.GONE);
                myEditText.setText("");
                mainView.findViewById(R.id.message).requestFocus();
                InputMethodManager keyboard = (InputMethodManager) DataManager.getInstance().mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(mainView.findViewById(R.id.message), 0);
                mainView.findViewById(R.id.overlay).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.overlay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataManager.getInstance().mainActivity.hideAllButtons();
                        DataManager.getInstance().mainActivity.showCertainButtons(1);
                        mainView.findViewById(R.id.overlay).setVisibility(View.GONE);
                        View view = mainView.findViewById(R.id.message);
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) DataManager.getInstance().mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        floating_btn.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        layout.setColorSchemeResources(R.color.colorPrimary);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (NetworkManager.getInstance().getFeed(DataManager.getInstance().user.id)) {
                            NetworkManager.getInstance().getNewsFeed();
                            adapter.feedList = new Select().from(Feed.class).where("is_hidden = 0").execute();
                            adapter.newsList = new Select().from(News.class).where("read = 0").execute();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    layout.setRefreshing(false);
                                    if (adapter.feedList.size() == 0)
                                        tutorial_message.setVisibility(View.VISIBLE);
                                    else
                                        tutorial_message.setVisibility(View.INVISIBLE);
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

        RecyclerView recyclerView = (RecyclerView) mainView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(DataManager.getInstance().mainActivity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        NetworkManager.getInstance().getFriends(DataManager.getInstance().user.id);

        adapter = new FeedAdapter(DataManager.getInstance().mainActivity, layout);
        recyclerView.setAdapter(adapter);

        ((TextView) mainView.findViewById(R.id.send_to)).setTypeface(DataManager.getInstance().myriadpro_regular);

        final AppCompatTextView send_to_picker = (AppCompatTextView) mainView.findViewById(R.id.send_to_picker);
        send_to_picker.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        send_to_picker.setTypeface(DataManager.getInstance().myriadpro_regular);
        send_to_picker.setText(R.string.everyone);

        final View.OnClickListener sendToListener = new View.OnClickListener() {
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

                ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText("");

                ArrayList<String> items = new ArrayList<>();
                items.add(getString(R.string.everyone));

                List<Friend> friendList;
                friendList = new Select().from(Friend.class).execute();
                for (int i = 0; i < friendList.size(); i++)
                    items.add(friendList.get(i).name);

                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(new GenericAdapter(DataManager.getInstance().mainActivity, send_to_picker.getText().toString(), items));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        send_to_picker.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                        dialog.dismiss();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((MainActivity) getActivity()).hideProgressBar();
                                    }
                                });
                            }
                        }).start();
                    }
                });
                ((MainActivity) getActivity()).showProgressBar2("");
                dialog.show();
            }
        };
        send_to_picker.setOnClickListener(sendToListener);

        return mainView;
    }

}

package com.studygoal.jisc.Adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.LinguisticManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.SocialManager;
import com.studygoal.jisc.Models.Feed;
import com.studygoal.jisc.Models.Friend;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.CircleTransform;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    public List<Feed> feedList = new ArrayList<>();
    private Context context;
    SwipeRefreshLayout layout;

    public FeedAdapter(Context context, SwipeRefreshLayout layout) {
        this.context = context;
        this.layout = layout;
        feedList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    private void removeItem(int position) {
        feedList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder feedViewHolder, final int i) {
            final Feed item = feedList.get(i);

            feedViewHolder.share_layout.setVisibility(View.GONE);

            feedViewHolder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnFacebook(item.message);
                }
            });

            feedViewHolder.facebook_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnFacebook(item.message);

                }
            });
            feedViewHolder.twitter_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnTwitter(item.message);
                }
            });
            feedViewHolder.mail_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialManager.getInstance().shareOnEmail(item.message);
                }
            });

            feedViewHolder.open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedViewHolder.bottom_bar.setVisibility(View.GONE);
                    feedViewHolder.close.setVisibility(View.VISIBLE);
                    feedViewHolder.menu.setVisibility(View.VISIBLE);
                    feedViewHolder.feed.setVisibility(View.GONE);

                }
            });

            feedViewHolder.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedViewHolder.bottom_bar.setVisibility(View.VISIBLE);
                    feedViewHolder.close.setVisibility(View.GONE);
                    feedViewHolder.menu.setVisibility(View.GONE);
                    feedViewHolder.feed.setVisibility(View.VISIBLE);
                }
            });

            if (feedViewHolder.close.getVisibility() == View.VISIBLE)
                feedViewHolder.close.callOnClick();

            feedViewHolder.hide_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(DataManager.getInstance().user.isDemo) {
                        feedViewHolder.close.callOnClick();
                        removeItem(feedViewHolder.getAdapterPosition());
                        Snackbar.make(layout, R.string.post_hidden_message, Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    HashMap<String, String> map = new HashMap<>();
                    map.put("feed_id", item.id);
                    map.put("student_id", DataManager.getInstance().user.id);
                    if (NetworkManager.getInstance().hidePost(map)) {
                        feedViewHolder.close.callOnClick();
                        removeItem(feedViewHolder.getAdapterPosition());
                        Snackbar.make(layout, R.string.post_hidden_message, Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(layout, R.string.failed_to_hide_message, Snackbar.LENGTH_LONG).show();
                    }
                }
            });

            if (item.message_from.equals(DataManager.getInstance().user.id)) {
                feedViewHolder.share.setVisibility(View.VISIBLE);
                feedViewHolder.open.setVisibility(View.GONE);
                if (!DataManager.getInstance().user.profile_pic.equals(""))
                    Glide.with(context).load(NetworkManager.getInstance().host + DataManager.getInstance().user.profile_pic).transform(new CircleTransform(context)).placeholder(R.drawable.profilenotfound).into(feedViewHolder.profile_pic);
                else
                    Glide.with(context).load(R.drawable.profilenotfound).transform(new CircleTransform(context)).placeholder(R.drawable.profilenotfound).into(feedViewHolder.profile_pic);
            } else {
                feedViewHolder.share.setVisibility(View.GONE);
                feedViewHolder.open.setVisibility(View.VISIBLE);
                Friend friend = new Select().from(Friend.class).where("friend_id = ?", item.message_from).executeSingle();
                String photo;
                if (friend != null)
                    photo = friend.profile_pic;
                else
                    photo = "";
                if (photo.equals(""))
                    Glide.with(context).load(R.drawable.profilenotfound).transform(new CircleTransform(context)).placeholder(R.drawable.profilenotfound).into(feedViewHolder.profile_pic);
                else
                    Glide.with(context).load(NetworkManager.getInstance().host + photo).transform(new CircleTransform(context)).placeholder(R.drawable.profilenotfound).into(feedViewHolder.profile_pic);
            }

            Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("UTC"));
            long current_time = System.currentTimeMillis();

            c.set(Integer.parseInt(item.created_date.split(" ")[0].split("-")[0]), Integer.parseInt(item.created_date.split(" ")[0].split("-")[1]) - 1, Integer.parseInt(item.created_date.split(" ")[0].split("-")[2]), Integer.parseInt(item.created_date.split(" ")[1].split(":")[0]), Integer.parseInt(item.created_date.split(" ")[1].split(":")[1]));
            long created_date = c.getTimeInMillis();
            long diff = (current_time - created_date) / 60000;

            if (diff <= 1)
                feedViewHolder.time_ago.setText(context.getString(R.string.just_a_moment_ago));
            else if (diff < 59)
                feedViewHolder.time_ago.setText(diff + " " + context.getString(R.string.minutes_ago));
            else if (diff < 120)
                feedViewHolder.time_ago.setText("1 " + context.getString(R.string.hour_ago));
            else if (diff < 1440)
                feedViewHolder.time_ago.setText((diff / 60) + " " + context.getString(R.string.hours_ago));
            else
                feedViewHolder.time_ago.setText(context.getString(R.string.on)+ " " + item.created_date.split(" ")[0].split("-")[2] + " " + LinguisticManager.getInstance().convertMonth(item.created_date.split(" ")[0].split("-")[1]) + " " + item.created_date.split(" ")[0].split("-")[0]);
            //TODO: 3 Mesaj
            feedViewHolder.feed.setText(item.message);

        //Alte listener-uri
            if(item.activity_type.toLowerCase().equals("friend_request"))
                feedViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataManager.getInstance().mainActivity.friend.setTag("from_list");
                        DataManager.getInstance().mainActivity.friend.callOnClick();
                    }
                });
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView;
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.feed_item, viewGroup, false);
        return new FeedViewHolder(itemView);
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        protected TextView message;
        ImageView profile_pic;
        protected TextView feed;
        TextView time_ago;
        TextView hide_post;
        TextView hide_friend;
        TextView delete_friend;
        View menu;
        protected View close;
        View open;
        View bottom_bar;
        View share_layout, facebook_btn, twitter_btn, mail_btn;
        View selfPost;

        protected View share;

        public View view;

        FeedViewHolder(View v) {
            super(v);
            try {
                message = (TextView) v.findViewById(R.id.message);
                message.setTypeface(DataManager.getInstance().myriadpro_regular);
            } catch (Exception ignored) {}
            view = v;
            try {
                profile_pic = (ImageView) v.findViewById(R.id.feed_item_profile);
                feed = (TextView) v.findViewById(R.id.feed_item_feed);
                time_ago = (TextView) v.findViewById(R.id.feed_item_time_ago);
                hide_post = (TextView) v.findViewById(R.id.feed_item_hide_post);
                hide_friend = (TextView) v.findViewById(R.id.feed_item_hide_friend);
                delete_friend = (TextView) v.findViewById(R.id.feed_item_delete_friend);
                menu = v.findViewById(R.id.feed_item_menu);
                close = v.findViewById(R.id.feed_item_close);
                open = v.findViewById(R.id.feed_item_option);
                bottom_bar = v.findViewById(R.id.feed_item_bottom_bar);
                share = v.findViewById(R.id.feed_item_share);
                share_layout = v.findViewById(R.id.share_layout);
                facebook_btn = v.findViewById(R.id.facebook_btn);
                twitter_btn = v.findViewById(R.id.twitter_btn);
                mail_btn = v.findViewById(R.id.mail_btn);
                selfPost = v.findViewById(R.id.feed_item_selfpost);

                feed.setTypeface(DataManager.getInstance().myriadpro_regular);
                time_ago.setTypeface(DataManager.getInstance().myriadpro_regular);
                hide_post.setTypeface(DataManager.getInstance().myriadpro_regular);
                hide_friend.setTypeface(DataManager.getInstance().myriadpro_regular);
                delete_friend.setTypeface(DataManager.getInstance().myriadpro_regular);
            } catch (Exception ignored) {}
        }
    }
}
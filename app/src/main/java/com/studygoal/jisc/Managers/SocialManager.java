package com.studygoal.jisc.Managers;

import android.content.Intent;
import android.support.design.widget.Snackbar;

import com.studygoal.jisc.R;

public class SocialManager {
    private static SocialManager ourInstance = new SocialManager();

    public static SocialManager getInstance() {
        return ourInstance;
    }

    private SocialManager() {
    }

    public void shareOnFacebook(String text) {

        text += "\n" + R.string.sent_from;
        text = text.replace("You are", "I am");
        text = text.replace("You have added", "I have added");
        text = text.replace("you have", "I have");

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, text);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, text);
        DataManager.getInstance().mainActivity.startActivity(shareIntent);
    }

    public void shareOnTwitter(String text) {
        text += "\n" + R.string.sent_from;
        text = text.replace("You are", "I am");
        text = text.replace("You have added", "I have added");
        text = text.replace("you have", "I have");
        try
        {
            // Check if the Twitter app is installed on the phone.
            DataManager.getInstance().mainActivity.getPackageManager().getPackageInfo("com.twitter.android", 0);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setClassName("com.twitter.android", "com.twitter.android.composer.ComposerActivity");
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, text);
            DataManager.getInstance().mainActivity.startActivity(intent);
        }
        catch (Exception e)
        {
            Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), DataManager.getInstance().mainActivity.getString(R.string.twitter_not_available), Snackbar.LENGTH_LONG).show();
        }
    }

    public void shareOnEmail(String text) {
        text += "\n" + R.string.sent_from;
        text = text.replace("You are", "I am");
        text = text.replace("You have added", "I have added");
        text = text.replace("you have", "I have");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        DataManager.getInstance().mainActivity.startActivity(intent);
    }
}

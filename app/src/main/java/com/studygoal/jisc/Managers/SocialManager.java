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

    public void shareOnIntent(String text) {
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
}

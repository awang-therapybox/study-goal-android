package com.studygoal.jisc;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.studygoal.jisc.Managers.DataManager;

public class NotificationAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String text =  context.getString(R.string.jisc_reminder_title) + " " + context.getString(R.string.dont_forget_break);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.log_icon_1)
                        .setContentTitle(context.getString(R.string.jisc_reminder_title))
                        .setContentText(context.getString(R.string.dont_forget_break))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        // Creates an explicit intent for an Activity in your app
        //Intent resultIntent = new Intent(this, ResultActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        //        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        //        stackBuilder.addParentStack(ResultActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        //        stackBuilder.addNextIntent(resultIntent);
        //        PendingIntent resultPendingIntent =
        //                stackBuilder.getPendingIntent(
        //                        0,
        //                        PendingIntent.FLAG_UPDATE_CURRENT
        //                );
        //        mBuilder.setContentIntent(resultPendingIntent);
        DataManager.getInstance().mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        DataManager.getInstance().mNotificationManager.notify(1, mBuilder.build());

    }
}
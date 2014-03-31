package myApp.trainingdiary.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.entity.TrainingStamp;

/**
 * Created by root on 31.03.14.
 */
public class NotificationBroadcastReceiver  extends BroadcastReceiver {

    private static NotificationBroadcastReceiver mInstance = null;
    private Context context;
    private static final int REQUEST_CODE  = 99900;
    private DBHelper dbHelper;

    public NotificationBroadcastReceiver(Context c){
        context = c;
        dbHelper = DBHelper.getInstance(context);
    }

    public static NotificationBroadcastReceiver getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new NotificationBroadcastReceiver(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String workoutExpiringTimeout = PreferenceManager.getDefaultSharedPreferences(context).getString(Const.KEY_WORKOUT_EXPIRING, String.valueOf(Const.THREE_HOURS));
        Log.d(Const.LOG_TAG, "workoutExpiringTimeout: " + workoutExpiringTimeout);
        TrainingDurationManger.closeExpiredTrainingStamps(Integer.valueOf(workoutExpiringTimeout));
        TrainingStamp tr_stamp = dbHelper.READ.getOpenTrainingStamp();
        if (tr_stamp == null && dbHelper.READ.getLastTrainingSetTrainingStamp(tr_stamp.getId()) == null) {
            NotificationHelper.getInstance(context).stopShowNotification();
            stopNotificationReceiver();
        }

    }

    public void startNotificationReceiver() {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction(Integer.toString(REQUEST_CODE));
        intent.putExtra("id", REQUEST_CODE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000*60*1 , pendingIntent);
    }

    public void stopNotificationReceiver() {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction(Integer.toString(REQUEST_CODE));
        intent.putExtra("id", REQUEST_CODE);
        PendingIntent sender = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

    }
}

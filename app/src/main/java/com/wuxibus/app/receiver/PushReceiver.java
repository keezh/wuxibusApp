package com.wuxibus.app.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.wuxibus.app.R;
import com.wuxibus.app.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by kee on 1/31/16.
 */
public class PushReceiver extends BroadcastReceiver {
    public String TAG = "push-leancloud";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
  //          String action = intent.getAction();
//            String channel = intent.getExtras().getString("com.avos.avoscloud.Channel");
            //获取消息内容
            JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));

            //Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
            Iterator itr = json.keys();
            while (itr.hasNext()) {
                String key = (String) itr.next();
                Log.d(TAG, "..." + key + " => " + json.getString(key));
            }

            final String message = json.getString("alert");
            Intent resultIntent = new Intent(AVOSCloud.applicationContext, MainActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(AVOSCloud.applicationContext, 0, resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(AVOSCloud.applicationContext)
                            .setContentTitle(
                                    AVOSCloud.applicationContext.getResources().getString(R.string.app_name))
                            .setContentText(message)
                            .setTicker(message);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);

            int mNotificationId = 10086;
            NotificationManager mNotifyMgr =
                    (NotificationManager) AVOSCloud.applicationContext
                            .getSystemService(
                                    Context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }
}

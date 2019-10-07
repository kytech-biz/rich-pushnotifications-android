package biz.kytech.pushnotification.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import biz.kytech.pushnotification.activity.MainActivity;
import biz.kytech.pushnotification.R;
import biz.kytech.pushnotification.model.NotificationMessage;
import biz.kytech.pushnotification.receiver.NotificationReceiver;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    // For Android 8 later
    static private final String NOTIFICATION_CHANNEL_ID = "Notification_Information_Channel";
    static private final String NOTIFICATION_CHANNEL_NAME = "Notification";
    static private final String NOTIFICATION_DESCRIPTION = "Description";

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // TODO:
        // sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationMessage notificationMessage = new NotificationMessage(remoteMessage.getData());
        PendingIntent pendingIntent = getPendingIntent(this, notificationMessage);

        if (notificationMessage.getCategory() == NotificationMessage.Category.Image) {
            showRichNotification(this, pendingIntent, notificationMessage);
            return;
        }

        showNotification(this, pendingIntent, notificationMessage);
    }

    private void showNotification(Context context, PendingIntent pendingIntent, NotificationMessage notificationMessage) {
        NotificationManager manager = getNotificationManager(context);

        // If add buttons
        /*
        TaskStackBuilder target1Intent = TaskStackBuilder.create(this)
                .addNextIntent(Target1Activity.createIntnt(this))
                .getPendingIntent(1, PendingIntent.FLAG_ONE_SHOT);

        TaskStackBuilder target2Intent = TaskStackBuilder.create(this)
                .addNextIntent(Target2Activity.createIntnt(this))
                .getPendingIntent(1, PendingIntent.FLAG_ONE_SHOT);
         */

        // Prepare default notification
        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(notificationMessage.getTitle())
                .setContentText(notificationMessage.getBody())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true)
                // If add buttons
                //.addAction(0, "Button1", target1Intent)
                //.addAction(0, "Button2", target2Intent)
                .build();

        manager.notify(TAG,1, notification);
    }

    private void showRichNotification(Context context, PendingIntent pendingIntent, NotificationMessage notificationMessage) {
        NotificationManager manager = getNotificationManager(context);

        RemoteViews colappsedView = new RemoteViews(getPackageName(), R.layout.notification_collapsed);
        colappsedView.setTextViewText(R.id.title, notificationMessage.getTitle());
        colappsedView.setTextViewText(R.id.body, notificationMessage.getBody());
        colappsedView.setImageViewBitmap(R.id.imageView, notificationMessage.loadBitmap());

        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_expanded);
        expandedView.setTextViewText(R.id.title, notificationMessage.getTitle());
        expandedView.setTextViewText(R.id.body, notificationMessage.getBody());
        expandedView.setImageViewBitmap(R.id.imageView, notificationMessage.loadBitmap());
        //expandedView.setOnClickPendingIntent(R.id.btn, pendingIntent);

        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true)
                .setCustomContentView(colappsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        manager.notify(TAG,1, notification);
    }

    private static PendingIntent getPendingIntent(Context context, NotificationMessage notificationMessage) {
        // Open intent is broadcast case.
        //Intent intentBroadcast = new Intent(context, NotificationReceiver.class);
        //PendingIntent pendingIntentBroadcast = PendingIntent.getBroadcast(context, 0, intentBroadcast, 0);

        // Open intent is activity case.
        Intent intentActivity = new Intent(context, MainActivity.class);
        intentActivity.putExtra("notificationMessage", notificationMessage);
        PendingIntent pendingIntentActivity = PendingIntent.getActivity(context, 0, intentActivity, 0);

        return pendingIntentActivity;
    }

    private static NotificationManager getNotificationManager(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            // Android 8 (Oreo) former
        } else {
            // Android 8 (Oreo) later
            // get NotifyChannel and create it..
            if (manager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        NOTIFICATION_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(NOTIFICATION_DESCRIPTION);
                manager.createNotificationChannel(channel);
            }
        }

        return manager;
    }
}

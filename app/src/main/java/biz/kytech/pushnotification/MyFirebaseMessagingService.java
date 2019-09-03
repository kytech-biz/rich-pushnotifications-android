package biz.kytech.pushnotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    // For Android 8 later
    static private final String NOTIFICATION_CHANNEL_ID = "Notification_Information_Channel";
    static private final String NOTIFICATION_CHANNEL_NAME = "Notification";
    static private final String NOTIFICATION_DESCRIPTION = "Description";

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        //sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String titleAndMessage = data.get("default");
        int firstLFPos = titleAndMessage.indexOf('|');
        String title = titleAndMessage.substring(0, firstLFPos);
        String message = titleAndMessage.substring(firstLFPos + 1);

        long lSentTime = remoteMessage.getSentTime();   // UTC エポックミリ秒
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(lSentTime);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(cal.getTimeZone());
        String timestamp = df.format(cal.getTime());

        showNotification(this, title, message, timestamp);
    }

    private void showNotification(Context context, String title, String message, String timestamp) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                //| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        );

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

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

        // 通知にボタンをつける
        /*
        TaskStackBuilder target1Intent = TaskStackBuilder.create(this)
                .addNextIntent(Target1Activity.createIntnt(this))
                .getPendingIntent(1, PendingIntent.FLAG_ONE_SHOT);

        TaskStackBuilder target2Intent = TaskStackBuilder.create(this)
                .addNextIntent(Target2Activity.createIntnt(this))
                .getPendingIntent(1, PendingIntent.FLAG_ONE_SHOT);
         */

        // 拡張レイアウトを利用する
        // 最大の縦幅は256dpまで
        /*
        Bitmap profileBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.common_full_open_on_phone)).getBitmap();

        RemoteViews remoteView = new RemoteViews(this.getPackageName(), R.layout.custom_notification);
        remoteView.setTextViewText(R.id.title, title);
        remoteView.setTextViewText(R.id.message, message);
        remoteView.setImageViewBitmap(R.id.profile_photo, profileBitmap);
        remoteView.setOnClickPendingIntent(R.id.btn_1, pendingIntent1);
        remoteView.setOnClickPendingIntent(R.id.btn_2, pendingIntent2);
        */

        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.skate)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setAutoCancel(true)

                // プッシュ通知に画像を表示する
                //.setStyle(NotificationCompat.BigPictureStyle().bigPicture(imageBitmap))
                //.setLargeIcon(profileBitmap)

                // 通知にボタンをつける
                //.addAction(0, "Button1", target1Intent)
                //.addAction(0, "Button2", target2Intent)

                // 拡張レイアウトを利用する
                //.setCustomBigContentView(remoteView)
                //.setCustomHeadsUpContentView(remoteView)

                .build();

        manager.notify(TAG,1, notification);
    }
}

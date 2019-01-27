package saarland.cispa.bletrackerlib.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ForegroundNotification {

    public static final String NOTIFICATION_FOREGROUND_CHANNEL_ID = "NOTIFICATION_FOREGROUND_CHANNEL_ID";

    /**
     * Creates a permanent notification which is needed for forground scanning
     * @param context the Context or Activity
     * @param icon the icon to show in notification
     * @param activityToStart the activity which should start by taping the notification
     * @return a permanent notification
     */
    public static Notification create(Context context, int icon, Class activityToStart) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(icon);
        builder.setContentTitle("Scanning for Beacons");
        Intent intent = new Intent(context, activityToStart);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_FOREGROUND_CHANNEL_ID,
                    "Foreground Service", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notification channel only for the foreground service is running notification");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }
}

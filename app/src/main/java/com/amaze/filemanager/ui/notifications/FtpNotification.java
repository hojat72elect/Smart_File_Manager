package com.amaze.filemanager.ui.notifications;

import static android.app.PendingIntent.FLAG_ONE_SHOT;
import static com.amaze.filemanager.asynchronous.services.AbstractProgressiveService.getPendingIntentFlag;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.amaze.filemanager.R;
import com.amaze.filemanager.asynchronous.services.ftp.FtpService;
import com.amaze.filemanager.ui.activities.MainActivity;
import com.amaze.filemanager.utils.NetworkUtil;

import java.net.InetAddress;

public class FtpNotification {

    private static NotificationCompat.Builder buildNotification(
            Context context, @StringRes int contentTitleRes, String contentText, boolean noStopButton
    ) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent =
                PendingIntent.getActivity(context, 0, notificationIntent, getPendingIntentFlag(0));

        long when = System.currentTimeMillis();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, NotificationConstants.CHANNEL_FTP_ID)
                        .setContentTitle(context.getString(contentTitleRes))
                        .setContentText(contentText)
                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.ic_ftp_light)
                        .setTicker(context.getString(R.string.ftp_notif_starting))
                        .setWhen(when)
                        .setOngoing(true)
                        .setOnlyAlertOnce(true);

        if (!noStopButton) {
            int stopIcon = android.R.drawable.ic_menu_close_clear_cancel;
            CharSequence stopText = context.getString(R.string.ftp_notif_stop_server);
            Intent stopIntent =
                    new Intent(FtpService.ACTION_STOP_FTPSERVER).setPackage(context.getPackageName());
            PendingIntent stopPendingIntent =
                    PendingIntent.getBroadcast(context, 0, stopIntent, getPendingIntentFlag(FLAG_ONE_SHOT));

            builder.addAction(stopIcon, stopText, stopPendingIntent);
        }

        NotificationConstants.setMetadata(context, NotificationConstants.TYPE_FTP);

        return builder;
    }

    public static Notification startNotification(Context context, boolean noStopButton) {
        NotificationCompat.Builder builder =
                buildNotification(
                        context,
                        R.string.ftp_notif_starting_title,
                        context.getString(R.string.ftp_notif_starting),
                        noStopButton
                );

        return builder.build();
    }

    @android.annotation.SuppressLint("MissingPermission")
    public static void updateNotification(Context context, boolean noStopButton) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int port = sharedPreferences.getInt(FtpService.PORT_PREFERENCE_KEY, FtpService.DEFAULT_PORT);
        boolean secureConnection =
                sharedPreferences.getBoolean(FtpService.KEY_PREFERENCE_SECURE, FtpService.DEFAULT_SECURE);

        InetAddress address = NetworkUtil.getLocalInetAddress(context);

        String address_text = "Address not found";

        if (address != null) {
            address_text =
                    (secureConnection ? FtpService.INITIALS_HOST_SFTP : FtpService.INITIALS_HOST_FTP)
                            + address.getHostAddress()
                            + ":"
                            + port
                            + "/";
        }

        NotificationCompat.Builder builder =
                buildNotification(
                        context,
                        R.string.ftp_notif_title,
                        context.getString(R.string.ftp_notif_text, address_text),
                        noStopButton
                );

        notificationManager.notify(NotificationConstants.FTP_ID, builder.build());
    }
}

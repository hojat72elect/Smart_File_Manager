package com.amaze.filemanager.ui.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.amaze.filemanager.R;

public class NotificationConstants {

    public static final int COPY_ID = 0;
    public static final int EXTRACT_ID = 1;
    public static final int ZIP_ID = 2;
    public static final int DECRYPT_ID = 3;
    public static final int ENCRYPT_ID = 4;
    public static final int FTP_ID = 5;
    public static final int FAILED_ID = 6;
    public static final int WAIT_ID = 7;

    public static final int TYPE_NORMAL = 0, TYPE_FTP = 1;

    public static final String CHANNEL_NORMAL_ID = "normalChannel";
    public static final String CHANNEL_FTP_ID = "ftpChannel";

    /**
     * This creates a channel (API >= 26) or applies the correct metadata to a notification (API < 26)
     */
    public static void setMetadata(Context context, int type) {
        switch (type) {
            case TYPE_NORMAL:
                createNormalChannel(context);
                break;
            case TYPE_FTP:
                createFtpChannel(context);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized type:" + type);
        }
    }

    /**
     * You CANNOT call this from android < O. THis channel is set so it doesn't bother the user, but
     * it has importance.
     */
    private static void createFtpChannel(Context context) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager.getNotificationChannel(CHANNEL_FTP_ID) == null) {
            NotificationChannel mChannel =
                    new NotificationChannel(
                            CHANNEL_FTP_ID,
                            context.getString(R.string.channel_name_ftp),
                            NotificationManager.IMPORTANCE_HIGH
                    );
            // Configure the notification channel.
            mChannel.setDescription(context.getString(R.string.channel_description_ftp));
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    /**
     * You CANNOT call this from android < O. THis channel is set so it doesn't bother the user, with
     * the lowest importance.
     */
    private static void createNormalChannel(Context context) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager.getNotificationChannel(CHANNEL_NORMAL_ID) == null) {
            NotificationChannel mChannel =
                    new NotificationChannel(
                            CHANNEL_NORMAL_ID,
                            context.getString(R.string.channel_name_normal),
                            NotificationManager.IMPORTANCE_MIN
                    );
            // Configure the notification channel.
            mChannel.setDescription(context.getString(R.string.channel_description_normal));
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }
}

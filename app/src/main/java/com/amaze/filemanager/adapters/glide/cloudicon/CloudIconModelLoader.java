package com.amaze.filemanager.adapters.glide.cloudicon;

import static com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool.SSH_URI_PREFIX;
import static com.amaze.filemanager.filesystem.smb.CifsContexts.SMB_URI_PREFIX;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amaze.filemanager.database.CloudHandler;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

public class CloudIconModelLoader implements ModelLoader<String, Bitmap> {

    private final Context context;

    public CloudIconModelLoader(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public LoadData<Bitmap> buildLoadData(@NonNull String s, int width, int height, @NonNull Options options) {
        // we put key as current time since we're not disk caching the images for cloud,
        // as there is no way to differentiate input streams returned by different cloud services
        // for future instances and they don't expose concrete paths either
        return new LoadData<>(
                new ObjectKey(System.currentTimeMillis()),
                new CloudIconDataFetcher(context, s, width, height)
        );
    }

    @Override
    public boolean handles(String s) {
        return s.startsWith(CloudHandler.CLOUD_PREFIX_BOX)
                || s.startsWith(CloudHandler.CLOUD_PREFIX_DROPBOX)
                || s.startsWith(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE)
                || s.startsWith(CloudHandler.CLOUD_PREFIX_ONE_DRIVE)
                || s.startsWith(SMB_URI_PREFIX)
                || s.startsWith(SSH_URI_PREFIX);
    }
}

package com.amaze.filemanager.asynchronous.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.amaze.filemanager.asynchronous.loaders.AppListLoader;

/**
 * A broadcast receiver that watches over app installation and removal and notifies {@link
 * AppListLoader} for the same.
 */
public class PackageReceiver extends BroadcastReceiver {

    private final AppListLoader listLoader;

    public PackageReceiver(AppListLoader listLoader) {

        this.listLoader = listLoader;

        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        listLoader.getContext().registerReceiver(this, filter);

        // Register for events related to SD card installation
        IntentFilter sdcardFilter = new IntentFilter(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        sdcardFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        listLoader.getContext().registerReceiver(this, sdcardFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        listLoader.onContentChanged();
    }
}

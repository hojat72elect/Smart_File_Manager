package com.amaze.filemanager.asynchronous.loaders;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.format.Formatter;

import androidx.loader.content.AsyncTaskLoader;

import com.amaze.filemanager.adapters.data.AppDataParcelable;
import com.amaze.filemanager.adapters.data.AppDataSorter;
import com.amaze.filemanager.asynchronous.broadcast_receivers.PackageReceiver;
import com.amaze.filemanager.utils.InterestingConfigChange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class loads all the packages installed.
 */
public class AppListLoader extends AsyncTaskLoader<List<AppDataParcelable>> {

    private final Logger LOG = LoggerFactory.getLogger(AppListLoader.class);
    private final int sortBy;
    private final boolean isAscending;
    private final PackageManager packageManager;
    private PackageReceiver packageReceiver;
    private List<AppDataParcelable> mApps;

    public AppListLoader(Context context, int sortBy, boolean isAscending) {
        super(context);

        this.sortBy = sortBy;
        this.isAscending = isAscending;

        /*
         * using global context because of the fact that loaders are supposed to be used
         * across fragments and activities
         */
        packageManager = getContext().getPackageManager();
    }

    /**
     * Check if an App is under /system or has been installed as an update to a built-in system
     * application.
     */
    public static boolean isAppInSystemPartition(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags
                & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP))
                != 0);
    }

    @Override
    public List<AppDataParcelable> loadInBackground() {
        List<ApplicationInfo> apps =
                packageManager.getInstalledApplications(
                        PackageManager.MATCH_UNINSTALLED_PACKAGES
                                | PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS);

        mApps = new ArrayList<>(apps.size());
        PackageInfo androidInfo = null;
        try {
            androidInfo = packageManager.getPackageInfo("android", PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            LOG.warn("failed to find android package name while loading apps list", e);
        }

        for (ApplicationInfo object : apps) {
            if (object.sourceDir == null) {
                continue;
            }
            File sourceDir = new File(object.sourceDir);

            String label = object.loadLabel(packageManager).toString();
            PackageInfo info;

            try {
                info = packageManager.getPackageInfo(object.packageName, PackageManager.GET_SIGNATURES);
            } catch (PackageManager.NameNotFoundException e) {
                LOG.warn("failed to find package name {} while loading apps list", object.packageName, e);
                info = null;
            }
            boolean isSystemApp = isAppInSystemPartition(object) || isSignedBySystem(info, androidInfo);

            List<String> splitPathList = null;
            if (object.splitPublicSourceDirs != null) {
                splitPathList = Arrays.asList(object.splitPublicSourceDirs);
            }

            AppDataParcelable elem =
                    new AppDataParcelable(
                            label,
                            object.sourceDir,
                            splitPathList,
                            object.packageName,
                            object.flags + "_" + (info != null ? info.versionName : ""),
                            Formatter.formatFileSize(getContext(), sourceDir.length()),
                            sourceDir.length(),
                            sourceDir.lastModified(),
                            isSystemApp,
                            null
                    );

            mApps.add(elem);
        }

        mApps.sort(new AppDataSorter(sortBy, isAscending));
        return mApps;
    }

    @Override
    public void deliverResult(List<AppDataParcelable> data) {

        mApps = data;
        if (isStarted()) {
            // loader has been started, if we have data, return immediately
            super.deliverResult(mApps);
        }
    }

    @Override
    protected void onStartLoading() {

        if (mApps != null) {
            // we already have the results, load immediately
            deliverResult(mApps);
        }

        if (packageReceiver != null) {
            packageReceiver = new PackageReceiver(this);
        }

        boolean didConfigChange = InterestingConfigChange.isConfigChanged(getContext().getResources());

        if (takeContentChanged() || mApps == null || didConfigChange) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(List<AppDataParcelable> data) {
        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        // we're free to clear resources
        if (mApps != null) {
            mApps = null;
        }

        if (packageReceiver != null) {
            getContext().unregisterReceiver(packageReceiver);

            packageReceiver = null;
        }

        InterestingConfigChange.recycle();
    }

    /**
     * Check if an App is signed by system or not.
     */
    public boolean isSignedBySystem(PackageInfo piApp, PackageInfo piSys) {
        return (piApp != null
                && piSys != null
                && piApp.signatures != null
                && piSys.signatures[0].equals(piApp.signatures[0]));
    }
}

package com.amaze.filemanager.asynchronous.services;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.amaze.filemanager.R;
import com.amaze.filemanager.asynchronous.asynctasks.DeleteTask;
import com.amaze.filemanager.asynchronous.management.ServiceWatcherUtil;
import com.amaze.filemanager.database.CryptHandler;
import com.amaze.filemanager.database.models.explorer.EncryptedEntry;
import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException;
import com.amaze.filemanager.fileoperations.filesystem.OpenMode;
import com.amaze.filemanager.filesystem.FileProperties;
import com.amaze.filemanager.filesystem.HybridFile;
import com.amaze.filemanager.filesystem.HybridFileParcelable;
import com.amaze.filemanager.filesystem.Operations;
import com.amaze.filemanager.filesystem.files.CryptUtil;
import com.amaze.filemanager.filesystem.files.FileUtils;
import com.amaze.filemanager.filesystem.files.GenericCopyUtil;
import com.amaze.filemanager.filesystem.files.MediaConnectionUtils;
import com.amaze.filemanager.filesystem.root.CopyFilesCommand;
import com.amaze.filemanager.filesystem.root.MoveFileCommand;
import com.amaze.filemanager.ui.activities.MainActivity;
import com.amaze.filemanager.ui.notifications.NotificationConstants;
import com.amaze.filemanager.utils.DatapointParcelable;
import com.amaze.filemanager.utils.ObtainableServiceBinder;
import com.amaze.filemanager.utils.ProgressHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class CopyService extends AbstractProgressiveService {
    public static final String TAG_IS_ROOT_EXPLORER = "is_root";
    public static final String TAG_COPY_TARGET = "COPY_DIRECTORY";
    public static final String TAG_COPY_SOURCES = "FILE_PATHS";
    public static final String TAG_COPY_OPEN_MODE = "MODE"; // target open mode
    public static final String TAG_COPY_MOVE = "move";
    public static final String TAG_BROADCAST_COPY_CANCEL = "copycancel";
    private static final Logger LOG = LoggerFactory.getLogger(CopyService.class);
    private static final String TAG_COPY_START_ID = "id";
    private final IBinder mBinder = new ObtainableServiceBinder<>(this);
    private final ProgressHandler progressHandler = new ProgressHandler();
    // list of data packages, to initiate chart in process viewer fragment
    private final ArrayList<DatapointParcelable> dataPackages = new ArrayList<>();
    private final BroadcastReceiver receiver3 =
            new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    // cancel operation
                    progressHandler.setCancelled(true);
                }
            };
    private NotificationManagerCompat mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private Context c;
    private ServiceWatcherUtil watcherUtil;
    private ProgressListener progressListener;
    private RemoteViews customSmallContentViews, customBigContentViews;

    @Override
    public void onCreate() {
        super.onCreate();
        c = getApplicationContext();
        registerReceiver(receiver3, new IntentFilter(TAG_BROADCAST_COPY_CANCEL));
    }

    @android.annotation.SuppressLint("NotificationId0")
    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Bundle b = new Bundle();
        boolean isRootExplorer = intent.getBooleanExtra(TAG_IS_ROOT_EXPLORER, false);
        ArrayList<HybridFileParcelable> files = intent.getParcelableArrayListExtra(TAG_COPY_SOURCES);
        String targetPath = intent.getStringExtra(TAG_COPY_TARGET);
        int mode = intent.getIntExtra(TAG_COPY_OPEN_MODE, OpenMode.UNKNOWN.ordinal());
        final boolean move = intent.getBooleanExtra(TAG_COPY_MOVE, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        int accentColor =
                ((com.amaze.filemanager.application.AmazeFileManagerApplication) getApplication())
                        .getUtilsProvider()
                        .getColorPreference()
                        .getCurrentUserColorPreferences(this, sharedPreferences)
                        .getAccent();

        mNotifyManager = NotificationManagerCompat.from(getApplicationContext());
        b.putInt(TAG_COPY_START_ID, startId);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra(MainActivity.KEY_INTENT_PROCESS_VIEWER, true);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, getPendingIntentFlag(0));

        customSmallContentViews =
                new RemoteViews(getPackageName(), R.layout.notification_service_small);
        customBigContentViews = new RemoteViews(getPackageName(), R.layout.notification_service_big);

        Intent stopIntent = new Intent(TAG_BROADCAST_COPY_CANCEL);
        PendingIntent stopPendingIntent =
                PendingIntent.getBroadcast(c, 1234, stopIntent, getPendingIntentFlag(FLAG_UPDATE_CURRENT));
        NotificationCompat.Action action =
                new NotificationCompat.Action(
                        R.drawable.ic_content_copy_white_36dp, getString(R.string.stop_ftp), stopPendingIntent);

        mBuilder =
                new NotificationCompat.Builder(c, NotificationConstants.CHANNEL_NORMAL_ID)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_content_copy_white_36dp)
                        .setCustomContentView(customSmallContentViews)
                        .setCustomBigContentView(customBigContentViews)
                        .setCustomHeadsUpContentView(customSmallContentViews)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .addAction(action)
                        .setOngoing(true)
                        .setColor(accentColor);

        // set default notification views text

        NotificationConstants.setMetadata(c, NotificationConstants.TYPE_NORMAL);

        startForeground(NotificationConstants.COPY_ID, mBuilder.build());
        initNotificationViews();

        b.putBoolean(TAG_COPY_MOVE, move);
        b.putString(TAG_COPY_TARGET, targetPath);
        b.putInt(TAG_COPY_OPEN_MODE, mode);
        b.putParcelableArrayList(TAG_COPY_SOURCES, files);

        super.onStartCommand(intent, flags, startId);
        super.progressHalted();
        // going async
        new DoInBackground(isRootExplorer).execute(b);

        // If we get killed, after returning from here, restart
        return START_NOT_STICKY;
    }

    @Override
    protected NotificationManagerCompat getNotificationManager() {
        return mNotifyManager;
    }

    @Override
    protected NotificationCompat.Builder getNotificationBuilder() {
        return mBuilder;
    }

    @Override
    protected int getNotificationId() {
        return NotificationConstants.COPY_ID;
    }

    @Override
    protected RemoteViews getNotificationCustomViewSmall() {
        return customSmallContentViews;
    }

    @Override
    protected RemoteViews getNotificationCustomViewBig() {
        return customBigContentViews;
    }

    @Override
    @StringRes
    protected int getTitle(boolean move) {
        return move ? R.string.moving : R.string.copying;
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    @Override
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    protected ArrayList<DatapointParcelable> getDataPackages() {
        return dataPackages;
    }

    @Override
    protected ProgressHandler getProgressHandler() {
        return progressHandler;
    }

    @Override
    protected void clearDataPackages() {
        dataPackages.clear();
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver3);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    private class DoInBackground extends AsyncTask<Bundle, Void, Void> {
        private final boolean isRootExplorer;
        boolean move;
        private Copy copy;
        private String targetPath;
        private int sourceProgress = 0;

        private DoInBackground(boolean isRootExplorer) {
            this.isRootExplorer = isRootExplorer;
        }

        protected Void doInBackground(Bundle... p1) {

            ArrayList<HybridFileParcelable> sourceFiles = p1[0].getParcelableArrayList(TAG_COPY_SOURCES);

            // setting up service watchers and initial data packages
            // finding total size on background thread (this is necessary condition for SMB!)
            long totalSize = FileUtils.getTotalBytes(sourceFiles, c);
            int totalSourceFiles = sourceFiles.size();

            progressHandler.setSourceFiles(totalSourceFiles);
            progressHandler.setTotalSize(totalSize);

            progressHandler.setProgressListener((speed) -> publishResults(speed, false, move));

            watcherUtil = new ServiceWatcherUtil(progressHandler);

            addFirstDatapoint(sourceFiles.get(0).getName(c), sourceFiles.size(), totalSize, move);

            targetPath = p1[0].getString(TAG_COPY_TARGET);
            move = p1[0].getBoolean(TAG_COPY_MOVE);
            OpenMode openMode = OpenMode.getOpenMode(p1[0].getInt(TAG_COPY_OPEN_MODE));
            copy = new Copy();
            copy.execute(sourceFiles, targetPath, move, openMode);

            if (copy.failedFOps.isEmpty()) {

                // adding/updating new encrypted db entry if any encrypted file was copied/moved
                for (HybridFileParcelable sourceFile : sourceFiles) {
                    try {
                        findAndReplaceEncryptedEntry(sourceFile);
                    } catch (Exception e) {
                        // unable to modify encrypted entry in database
                        Toast.makeText(c, getString(R.string.encryption_fail_copy), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(Void b) {

            super.onPostExecute(b);
            //  publishResults(b, "", totalSourceFiles, totalSourceFiles, totalSize, totalSize, 0, true,
            // move);
            // stopping watcher if not yet finished
            watcherUtil.stopWatch();
            finalizeNotification(copy.failedFOps, move);

            Intent intent = new Intent(MainActivity.KEY_INTENT_LOAD_LIST);
            intent.putExtra(MainActivity.KEY_INTENT_LOAD_LIST_FILE, targetPath);
            sendBroadcast(intent);
            stopSelf();
        }

        /**
         * Iterates through every file to find an encrypted file and update/add a new entry about it's
         * metadata in the database
         *
         * @param sourceFile the file which is to be iterated
         */
        private void findAndReplaceEncryptedEntry(HybridFileParcelable sourceFile) {

            // even directories can end with CRYPT_EXTENSION
            if (sourceFile.isDirectory() && !sourceFile.getName(c).endsWith(CryptUtil.CRYPT_EXTENSION)) {
                // iterating each file inside source files which were copied to find instance of
                // any copied / moved encrypted file
                sourceFile.forEachChildrenFile(
                        getApplicationContext(),
                        isRootExplorer,
                        this::findAndReplaceEncryptedEntry
                );
            } else {

                if (sourceFile.getName(c).endsWith(CryptUtil.CRYPT_EXTENSION)) {
                    try {

                        CryptHandler cryptHandler = CryptHandler.INSTANCE;
                        EncryptedEntry oldEntry = cryptHandler.findEntry(sourceFile.getPath());
                        EncryptedEntry newEntry = new EncryptedEntry();

                        newEntry.setPassword(oldEntry.getPassword());
                        newEntry.setPath(targetPath + "/" + sourceFile.getName(c));

                        if (move) {

                            // file was been moved, update the existing entry
                            newEntry.setId(oldEntry.getId());
                            cryptHandler.updateEntry(newEntry);
                        } else {
                            // file was copied, create a new entry with same data
                            cryptHandler.addEntry(newEntry);
                        }
                    } catch (Exception e) {
                        LOG.warn("failed to find and replace encrypted entry after copy", e);
                        // couldn't change the entry, leave it alone
                    }
                }
            }
        }

        class Copy {

            ArrayList<HybridFile> failedFOps;
            ArrayList<HybridFileParcelable> toDelete;

            Copy() {
                failedFOps = new ArrayList<>();
                toDelete = new ArrayList<>();
            }

            /**
             * Method iterate through files to be copied
             *
             * @param mode target file open mode (current path's open mode)
             */
            public void execute(
                    final ArrayList<HybridFileParcelable> sourceFiles,
                    final String targetPath,
                    final boolean move,
                    OpenMode mode
            ) {

                // initial start of copy, initiate the watcher
                watcherUtil.watch(CopyService.this);

                if (FileProperties.checkFolder((targetPath), c) == 1) {
                    for (int i = 0; i < sourceFiles.size(); i++) {
                        sourceProgress = i;
                        HybridFileParcelable f1 = (sourceFiles.get(i));

                        try {

                            HybridFile hFile;
                            if (targetPath.contains(getExternalCacheDir().getPath())) {
                                // the target open mode is not the one we're currently in!
                                // we're processing the file for cache
                                hFile =
                                        new HybridFile(
                                                OpenMode.FILE, targetPath, sourceFiles.get(i).getName(c), f1.isDirectory());
                            } else {

                                // the target open mode is where we're currently at
                                hFile =
                                        new HybridFile(
                                                mode, targetPath, sourceFiles.get(i).getName(c), f1.isDirectory());
                            }

                            if (!progressHandler.isCancelled()) {

                                if ((f1.getMode() == OpenMode.ROOT || mode == OpenMode.ROOT) && isRootExplorer) {
                                    // either source or target are in root
                                    LOG.debug("either source or target are in root");
                                    progressHandler.setSourceFilesProcessed(++sourceProgress);
                                    copyRoot(f1, hFile, move);
                                    continue;
                                }
                                progressHandler.setSourceFilesProcessed(++sourceProgress);
                                copyFiles((f1), hFile, progressHandler);
                            } else {
                                break;
                            }
                        } catch (Exception e) {
                            LOG.error("Got exception checkout: {}", f1.getPath(), e);

                            failedFOps.add(sourceFiles.get(i));
                            for (int j = i + 1; j < sourceFiles.size(); j++)
                                failedFOps.add(sourceFiles.get(j));
                            break;
                        }
                    }
                } else if (isRootExplorer) {
                    for (int i = 0; i < sourceFiles.size(); i++) {
                        if (!progressHandler.isCancelled()) {
                            HybridFile hFile =
                                    new HybridFile(
                                            mode,
                                            targetPath,
                                            sourceFiles.get(i).getName(c),
                                            sourceFiles.get(i).isDirectory()
                                    );
                            progressHandler.setSourceFilesProcessed(++sourceProgress);
                            progressHandler.setFileName(sourceFiles.get(i).getName(c));
                            copyRoot(sourceFiles.get(i), hFile, move);
                        }
                    }
                } else {
                    failedFOps.addAll(sourceFiles);
                    return;
                }

                // making sure to delete files after copy operation is done
                // and not if the copy was cancelled
                if (move && !progressHandler.isCancelled()) {
                    ArrayList<HybridFileParcelable> toDelete = new ArrayList<>();
                    for (HybridFileParcelable a : sourceFiles) {
                        if (!failedFOps.contains(a)) toDelete.add(a);
                    }
                    new DeleteTask(c, true).execute((toDelete));
                }
            }

            void copyRoot(HybridFileParcelable sourceFile, HybridFile targetFile, boolean move) {

                try {
                    if (!move) {
                        CopyFilesCommand.INSTANCE.copyFiles(sourceFile.getPath(), targetFile.getPath());
                    } else {
                        MoveFileCommand.INSTANCE.moveFile(sourceFile.getPath(), targetFile.getPath());
                    }
                    ServiceWatcherUtil.position += sourceFile.getSize();
                } catch (ShellNotRunningException e) {
                    LOG.warn(
                            "failed to copy root file source: {} dest: {}",
                            sourceFile.getPath(),
                            targetFile.getPath(),
                            e
                    );
                    failedFOps.add(sourceFile);
                }
                MediaConnectionUtils.scanFile(c, new HybridFile[]{targetFile});
            }

            private void copyFiles(
                    final HybridFileParcelable sourceFile,
                    final HybridFile targetFile,
                    final ProgressHandler progressHandler
            )
                    throws IOException {

                if (progressHandler.isCancelled()) return;
                if (sourceFile.isDirectory()) {

                    if (!targetFile.exists()) {
                        targetFile.mkdir(c);
                    }

                    // various checks
                    // 1. source file and target file doesn't end up in loop
                    // 2. source file has a valid name or not
                    if (!Operations.isFileNameValid(sourceFile.getName(c))
                            || Operations.isCopyLoopPossible(sourceFile, targetFile)) {
                        failedFOps.add(sourceFile);
                        return;
                    }
                    targetFile.setLastModified(sourceFile.lastModified());

                    if (progressHandler.isCancelled()) return;
                    sourceFile.forEachChildrenFile(
                            c,
                            false,
                            file -> {
                                HybridFile destFile =
                                        new HybridFile(
                                                targetFile.getMode(),
                                                targetFile.getPath(),
                                                file.getName(c),
                                                file.isDirectory()
                                        );
                                try {
                                    copyFiles(file, destFile, progressHandler);
                                    destFile.setLastModified(file.lastModified());
                                } catch (IOException e) {
                                    throw new IllegalStateException(e); // throw unchecked exception, no throws needed
                                }
                            }
                    );
                } else {
                    if (!Operations.isFileNameValid(sourceFile.getName(c))) {
                        failedFOps.add(sourceFile);
                        return;
                    }

                    GenericCopyUtil copyUtil = new GenericCopyUtil(c, progressHandler);

                    progressHandler.setFileName(sourceFile.getName(c));
                    copyUtil.copy(
                            sourceFile,
                            targetFile,
                            () -> {
                                // we ran out of memory to map the whole channel, let's switch to streams
                                com.amaze.filemanager.application.AmazeFileManagerApplication.toast(c, c.getString(R.string.copy_low_memory));
                            },
                            ServiceWatcherUtil.UPDATE_POSITION
                    );
                    targetFile.setLastModified(sourceFile.lastModified());
                }
            }
        }
    }
}

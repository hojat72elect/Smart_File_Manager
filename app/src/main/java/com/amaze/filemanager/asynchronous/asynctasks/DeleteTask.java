package com.amaze.filemanager.asynchronous.asynctasks;

import static com.amaze.filemanager.ui.activities.MainActivity.TAG_INTENT_FILTER_FAILED_OPS;
import static com.amaze.filemanager.ui.activities.MainActivity.TAG_INTENT_FILTER_GENERAL;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.preference.PreferenceManager;

import com.amaze.filemanager.R;
import com.amaze.filemanager.database.CryptHandler;
import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException;
import com.amaze.filemanager.fileoperations.filesystem.OpenMode;
import com.amaze.filemanager.filesystem.HybridFile;
import com.amaze.filemanager.filesystem.HybridFileParcelable;
import com.amaze.filemanager.filesystem.SafRootHolder;
import com.amaze.filemanager.filesystem.cloud.CloudUtil;
import com.amaze.filemanager.filesystem.files.CryptUtil;
import com.amaze.filemanager.filesystem.files.MediaConnectionUtils;
import com.amaze.filemanager.ui.activities.MainActivity;
import com.amaze.filemanager.ui.fragments.CompressedExplorerFragment;
import com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants;
import com.amaze.filemanager.ui.notifications.NotificationConstants;
import com.amaze.filemanager.utils.DataUtils;
import com.amaze.filemanager.utils.OTGUtil;
import com.cloudrail.si.interfaces.CloudStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import jcifs.smb.SmbException;

public class DeleteTask
        extends AsyncTask<ArrayList<HybridFileParcelable>, String, AsyncTaskResult<Boolean>> {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteTask.class);
    private final Context applicationContext;
    private final boolean rootMode;
    private final DataUtils dataUtils = DataUtils.getInstance();
    private final boolean doDeletePermanently;
    private ArrayList<HybridFileParcelable> files;
    private CompressedExplorerFragment compressedExplorerFragment;

    public DeleteTask(@NonNull Context applicationContext, boolean doDeletePermanently) {
        this.applicationContext = applicationContext.getApplicationContext();
        this.doDeletePermanently = doDeletePermanently;
        rootMode =
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        .getBoolean(PreferencesConstants.PREFERENCE_ROOTMODE, false);
    }

    public DeleteTask(
            @NonNull Context applicationContext, CompressedExplorerFragment compressedExplorerFragment
    ) {
        this.applicationContext = applicationContext.getApplicationContext();
        this.doDeletePermanently = false;
        rootMode =
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        .getBoolean(PreferencesConstants.PREFERENCE_ROOTMODE, false);
        this.compressedExplorerFragment = compressedExplorerFragment;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(applicationContext, values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    @SafeVarargs
    protected final AsyncTaskResult<Boolean> doInBackground(
            final ArrayList<HybridFileParcelable>... p1
    ) {
        files = p1[0];
        boolean wasDeleted = true;
        if (files.isEmpty()) return new AsyncTaskResult<>(true);

        for (HybridFileParcelable file : files) {
            try {
                wasDeleted = doDeleteFile(file);
                if (!wasDeleted) break;
            } catch (Exception e) {
                return new AsyncTaskResult<>(e);
            }

            // delete file from media database
            if (!file.isSmb() && !file.isSftp())
                MediaConnectionUtils.scanFile(
                        applicationContext, files.toArray(new HybridFile[files.size()]));

            // delete file entry from encrypted database
            if (file.getName(applicationContext).endsWith(CryptUtil.CRYPT_EXTENSION)) {
                CryptHandler handler = CryptHandler.INSTANCE;
                handler.clear(file.getPath());
            }
        }

        return new AsyncTaskResult<>(wasDeleted);
    }

    @Override
    public void onPostExecute(AsyncTaskResult<Boolean> result) {

        Intent intent = new Intent(MainActivity.KEY_INTENT_LOAD_LIST);
        if (!files.isEmpty()) {
            String path = files.get(0).getParent(applicationContext);
            intent.putExtra(MainActivity.KEY_INTENT_LOAD_LIST_FILE, path);
            applicationContext.sendBroadcast(intent);
        }

        if (result.result == null || !result.result) {
            applicationContext.sendBroadcast(
                    new Intent(TAG_INTENT_FILTER_GENERAL)
                            .putParcelableArrayListExtra(TAG_INTENT_FILTER_FAILED_OPS, files));
        } else if (compressedExplorerFragment == null) {
            com.amaze.filemanager.application.AmazeFileManagerApplication.toast(applicationContext, R.string.done);
        }

        if (compressedExplorerFragment != null) {
            compressedExplorerFragment.files.clear();
        }

        // cancel any processing notification because of cut/paste operation
        NotificationManager notificationManager =
                (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationConstants.COPY_ID);
    }

    private boolean doDeleteFile(@NonNull HybridFileParcelable file) throws Exception {
        switch (file.getMode()) {
            case OTG:
                DocumentFile documentFile =
                        OTGUtil.getDocumentFile(file.getPath(), applicationContext, false);
                return documentFile.delete();
            case DOCUMENT_FILE:
                documentFile =
                        OTGUtil.getDocumentFile(
                                file.getPath(),
                                SafRootHolder.getUriRoot(),
                                applicationContext,
                                OpenMode.DOCUMENT_FILE,
                                false
                        );
                return documentFile.delete();
            case DROPBOX:
            case BOX:
            case GDRIVE:
            case ONEDRIVE:
                CloudStorage cloudStorage = dataUtils.getAccount(file.getMode());
                try {
                    cloudStorage.delete(CloudUtil.stripPath(file.getMode(), file.getPath()));
                    return true;
                } catch (Exception e) {
                    LOG.warn("failed to delete cloud files", e);
                    return false;
                }
            default:
                try {
                    /* SMB and SFTP (or any remote files that may support in the future) should not be
                     * supported by recycle bin. - TranceLove
                     */
                    if (!doDeletePermanently
                            && !OpenMode.SMB.equals(file.getMode())
                            && !OpenMode.SFTP.equals(file.getMode())) {
                        return file.moveToBin(applicationContext);
                    }
                    return file.delete(applicationContext, rootMode);
                } catch (ShellNotRunningException | SmbException e) {
                    LOG.warn("failed to delete files", e);
                    throw e;
                }
        }
    }
}

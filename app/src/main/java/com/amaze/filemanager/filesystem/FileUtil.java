package com.amaze.filemanager.filesystem;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import com.amaze.filemanager.R;
import com.amaze.filemanager.exceptions.NotAllowedException;
import com.amaze.filemanager.exceptions.OperationWouldOverwriteException;
import com.amaze.filemanager.ui.activities.MainActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Utility class for helping parsing file systems.
 */
public abstract class FileUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Determine the camera folder. There seems to be no Android API to work for real devices, so this
     * is a best guess.
     *
     * @return the default camera folder.
     */
    // TODO the function?
    @Nullable
    public static OutputStream getOutputStream(final File target, Context context)
            throws FileNotFoundException {
        OutputStream outStream = null;
        // First try the normal way
        if (FileProperties.isWritable(target)) {
            // standard way
            outStream = new FileOutputStream(target);
        } else {
            // Storage Access Framework
            DocumentFile targetDocument =
                    ExternalSdCardOperation.getDocumentFile(target, false, context);
            if (targetDocument == null) return null;
            outStream = context.getContentResolver().openOutputStream(targetDocument.getUri());
        }
        return outStream;
    }

    /**
     * Writes uri stream from external application to the specified path
     */
    public static final void writeUriToStorage(
            @NonNull final MainActivity mainActivity,
            @NonNull final List<Uri> uris,
            @NonNull final ContentResolver contentResolver,
            @NonNull final String currentPath
    ) {

        MaybeOnSubscribe<List<String>> writeUri =
                emitter -> {
                    java.util.List<String> retval = new java.util.ArrayList<>();

                    for (android.net.Uri uri : uris) {

                        java.io.BufferedInputStream bufferedInputStream = null;
                        try {
                            bufferedInputStream =
                                    new java.io.BufferedInputStream(contentResolver.openInputStream(uri));
                        } catch (java.io.FileNotFoundException e) {
                            emitter.onError(e);
                            return;
                        }

                        java.io.BufferedOutputStream bufferedOutputStream = null;

                        try {
                            androidx.documentfile.provider.DocumentFile documentFile = androidx.documentfile.provider.DocumentFile.fromSingleUri(mainActivity, uri);
                            String filename = documentFile.getName();
                            if (filename == null) {
                                filename = uri.getLastPathSegment();

                                // For cleaning up slashes. Back in #1217 there is a case of
                                // Uri.getLastPathSegment() end up with a full file path
                                if (filename.contains("/"))
                                    filename = filename.substring(filename.lastIndexOf('/') + 1);
                            }

                            String finalFilePath = currentPath + "/" + filename;
                            com.amaze.filemanager.utils.DataUtils dataUtils = com.amaze.filemanager.utils.DataUtils.getInstance();

                            com.amaze.filemanager.filesystem.HybridFile hFile = new com.amaze.filemanager.filesystem.HybridFile(com.amaze.filemanager.fileoperations.filesystem.OpenMode.UNKNOWN, currentPath);
                            hFile.generateMode(mainActivity);

                            switch (hFile.getMode()) {
                                case FILE:
                                case ROOT:
                                    java.io.File targetFile = new java.io.File(finalFilePath);
                                    if (!com.amaze.filemanager.filesystem.FileProperties.isWritableNormalOrSaf(
                                            targetFile.getParentFile(), mainActivity.getApplicationContext())) {
                                        emitter.onError(new com.amaze.filemanager.exceptions.NotAllowedException());
                                        return;
                                    }

                                    androidx.documentfile.provider.DocumentFile targetDocumentFile =
                                            com.amaze.filemanager.filesystem.ExternalSdCardOperation.getDocumentFile(
                                                    targetFile, false, mainActivity.getApplicationContext());

                                    // Fallback, in case getDocumentFile() didn't properly return a
                                    // DocumentFile
                                    // instance
                                    if (targetDocumentFile == null) {
                                        targetDocumentFile = androidx.documentfile.provider.DocumentFile.fromFile(targetFile);
                                    }

                                    // Lazy check... and in fact, different apps may pass in URI in different
                                    // formats, so we could only check filename matches
                                    // FIXME?: Prompt overwrite instead of simply blocking
                                    if (targetDocumentFile.exists() && targetDocumentFile.length() > 0) {
                                        emitter.onError(new com.amaze.filemanager.exceptions.OperationWouldOverwriteException());
                                        return;
                                    }

                                    bufferedOutputStream =
                                            new java.io.BufferedOutputStream(
                                                    contentResolver.openOutputStream(targetDocumentFile.getUri()));
                                    retval.add(targetFile.getPath());
                                    break;
                                case SMB:
                                    jcifs.smb.SmbFile targetSmbFile = com.amaze.filemanager.utils.smb.SmbUtil.create(finalFilePath);
                                    if (targetSmbFile.exists()) {
                                        emitter.onError(new com.amaze.filemanager.exceptions.OperationWouldOverwriteException());
                                        return;
                                    } else {
                                        java.io.OutputStream outputStream = targetSmbFile.getOutputStream();
                                        bufferedOutputStream = new java.io.BufferedOutputStream(outputStream);
                                        retval.add(com.amaze.filemanager.filesystem.HybridFile.parseAndFormatUriForDisplay(targetSmbFile.getPath()));
                                    }
                                    break;
                                case SFTP:
                                    // FIXME: implement support
                                    com.amaze.filemanager.application.AmazeFileManagerApplication.toast(mainActivity, mainActivity.getString(com.amaze.filemanager.R.string.not_allowed));
                                    emitter.onError(new kotlin.NotImplementedError());
                                    return;
                                case DROPBOX:
                                case BOX:
                                case ONEDRIVE:
                                case GDRIVE:
                                    com.amaze.filemanager.fileoperations.filesystem.OpenMode mode = hFile.getMode();

                                    com.cloudrail.si.interfaces.CloudStorage cloudStorage = dataUtils.getAccount(mode);
                                    String path = com.amaze.filemanager.filesystem.cloud.CloudUtil.stripPath(mode, finalFilePath);
                                    cloudStorage.upload(path, bufferedInputStream, documentFile.length(), true);
                                    retval.add(path);
                                    break;
                                case OTG:
                                    androidx.documentfile.provider.DocumentFile documentTargetFile =
                                            com.amaze.filemanager.utils.OTGUtil.getDocumentFile(finalFilePath, mainActivity, true);

                                    if (documentTargetFile.exists()) {
                                        emitter.onError(new com.amaze.filemanager.exceptions.OperationWouldOverwriteException());
                                        return;
                                    }

                                    bufferedOutputStream =
                                            new java.io.BufferedOutputStream(
                                                    contentResolver.openOutputStream(documentTargetFile.getUri()),
                                                    com.amaze.filemanager.filesystem.files.GenericCopyUtil.DEFAULT_BUFFER_SIZE
                                            );

                                    retval.add(documentTargetFile.getUri().getPath());
                                    break;
                                default:
                                    return;
                            }

                            int count = 0;
                            byte[] buffer = new byte[com.amaze.filemanager.filesystem.files.GenericCopyUtil.DEFAULT_BUFFER_SIZE];

                            while (count != -1) {
                                count = bufferedInputStream.read(buffer);
                                if (count != -1) {

                                    bufferedOutputStream.write(buffer, 0, count);
                                }
                            }
                            bufferedOutputStream.flush();
                        } catch (java.io.IOException e) {
                            emitter.onError(e);
                            return;
                        } finally {
                            try {
                                if (bufferedInputStream != null) {
                                    bufferedInputStream.close();
                                }
                                if (bufferedOutputStream != null) {
                                    bufferedOutputStream.close();
                                }
                            } catch (java.io.IOException e) {
                                emitter.onError(e);
                            }
                        }
                    }

                    if (retval.size() > 0) {
                        emitter.onSuccess(retval);
                    } else {
                        emitter.onError(new Exception());
                    }
                };

        Maybe.create(writeUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new MaybeObserver<List<String>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                            }

                            @Override
                            public void onSuccess(@NonNull List<String> paths) {
                                MediaScannerConnection.scanFile(
                                        mainActivity.getApplicationContext(),
                                        paths.toArray(new String[0]),
                                        new String[paths.size()],
                                        null
                                );
                                if (paths.size() == 1) {
                                    Toast.makeText(
                                                    mainActivity,
                                                    mainActivity.getString(R.string.saved_single_file, paths.get(0)),
                                                    Toast.LENGTH_LONG
                                            )
                                            .show();
                                } else {
                                    Toast.makeText(
                                                    mainActivity,
                                                    mainActivity.getString(R.string.saved_multi_files, paths.size()),
                                                    Toast.LENGTH_LONG
                                            )
                                            .show();
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (e instanceof OperationWouldOverwriteException) {
                                    com.amaze.filemanager.application.AmazeFileManagerApplication.toast(mainActivity, mainActivity.getString(R.string.cannot_overwrite));
                                    return;
                                }
                                if (e instanceof NotAllowedException) {
                                    com.amaze.filemanager.application.AmazeFileManagerApplication.toast(
                                            mainActivity, mainActivity.getResources().getString(R.string.not_allowed));
                                }
                                LOG.warn("Failed to write uri to storage", e);
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
    }
}

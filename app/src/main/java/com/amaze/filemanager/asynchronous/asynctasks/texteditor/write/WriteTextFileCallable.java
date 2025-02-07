package com.amaze.filemanager.asynchronous.asynctasks.texteditor.write;

import android.content.ContentResolver;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.documentfile.provider.DocumentFile;

import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException;
import com.amaze.filemanager.fileoperations.exceptions.StreamNotFoundException;
import com.amaze.filemanager.filesystem.EditableFileAbstraction;
import com.amaze.filemanager.filesystem.FileUtil;
import com.amaze.filemanager.filesystem.HybridFileParcelable;
import com.amaze.filemanager.filesystem.files.FileUtils;
import com.amaze.filemanager.filesystem.root.ConcatenateFileCommand;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.Callable;

import kotlin.Unit;

public class WriteTextFileCallable implements Callable<Unit> {
    private final WeakReference<Context> context;
    private final ContentResolver contentResolver;
    private final EditableFileAbstraction fileAbstraction;
    private final File cachedFile;
    private final boolean isRootExplorer;
    private final String dataToSave;

    public WriteTextFileCallable(
            Context context,
            ContentResolver contentResolver,
            EditableFileAbstraction file,
            String dataToSave,
            File cachedFile,
            boolean isRootExplorer
    ) {
        this.context = new WeakReference<>(context);
        this.contentResolver = contentResolver;
        this.fileAbstraction = file;
        this.cachedFile = cachedFile;
        this.dataToSave = dataToSave;
        this.isRootExplorer = isRootExplorer;
    }

    @WorkerThread
    @Override
    public Unit call()
            throws IOException,
            StreamNotFoundException,
            ShellNotRunningException,
            IllegalArgumentException {
        OutputStream outputStream;
        File destFile = null;

        switch (fileAbstraction.scheme) {
            case CONTENT:
                Objects.requireNonNull(fileAbstraction.uri);
                if (fileAbstraction.uri.getAuthority().equals(context.get().getPackageName())) {
                    DocumentFile documentFile =
                            DocumentFile.fromSingleUri(com.amaze.filemanager.application.AmazeFileManagerApplication.getInstance(), fileAbstraction.uri);
                    if (documentFile != null && documentFile.exists() && documentFile.canWrite()) {
                        outputStream = contentResolver.openOutputStream(fileAbstraction.uri, "wt");
                    } else {
                        destFile = FileUtils.fromContentUri(fileAbstraction.uri);
                        outputStream = openFile(destFile, context.get());
                    }
                } else {
                    outputStream = contentResolver.openOutputStream(fileAbstraction.uri, "wt");
                }
                break;
            case FILE:
                final HybridFileParcelable hybridFileParcelable = fileAbstraction.hybridFileParcelable;
                Objects.requireNonNull(hybridFileParcelable);

                Context context = this.context.get();
                if (context == null) {
                    return null;
                }
                outputStream = openFile(hybridFileParcelable.getFile(), context);
                destFile = fileAbstraction.hybridFileParcelable.getFile();
                break;
            default:
                throw new IllegalArgumentException(
                        "The scheme for '" + fileAbstraction.scheme + "' cannot be processed!");
        }

        Objects.requireNonNull(outputStream);

        outputStream.write(dataToSave.getBytes());
        outputStream.close();

        if (cachedFile != null && cachedFile.exists() && destFile != null) {
            // cat cache content to original file and delete cache file
            ConcatenateFileCommand.INSTANCE.concatenateFile(cachedFile.getPath(), destFile.getPath());
            cachedFile.delete();
        }
        return Unit.INSTANCE;
    }

    private OutputStream openFile(@NonNull File file, @NonNull Context context)
            throws IOException, StreamNotFoundException {
        OutputStream outputStream = FileUtil.getOutputStream(file, context);

        // try loading stream associated using root
        if (isRootExplorer && outputStream == null && cachedFile != null && cachedFile.exists()) {
            outputStream = new FileOutputStream(cachedFile);
        }

        if (outputStream == null) {
            throw new StreamNotFoundException("Cannot read or write text file!");
        }

        return outputStream;
    }
}

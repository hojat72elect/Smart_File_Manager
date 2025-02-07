package com.amaze.filemanager.asynchronous.asynctasks.texteditor.read;

import android.content.ContentResolver;

import androidx.annotation.WorkerThread;
import androidx.documentfile.provider.DocumentFile;

import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException;
import com.amaze.filemanager.fileoperations.exceptions.StreamNotFoundException;
import com.amaze.filemanager.filesystem.EditableFileAbstraction;
import com.amaze.filemanager.filesystem.HybridFileParcelable;
import com.amaze.filemanager.filesystem.files.FileUtils;
import com.amaze.filemanager.filesystem.root.CopyFilesCommand;
import com.amaze.filemanager.ui.activities.texteditor.ReturnedValueOnReadFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.Callable;

public class ReadTextFileCallable implements Callable<ReturnedValueOnReadFile> {

    public static final int MAX_FILE_SIZE_CHARS = 50 * 1024;

    private final ContentResolver contentResolver;
    private final EditableFileAbstraction fileAbstraction;
    private final File externalCacheDir;
    private final boolean isRootExplorer;

    private File cachedFile = null;

    public ReadTextFileCallable(
            ContentResolver contentResolver,
            EditableFileAbstraction file,
            File cacheDir,
            boolean isRootExplorer
    ) {
        this.contentResolver = contentResolver;
        this.fileAbstraction = file;
        this.externalCacheDir = cacheDir;
        this.isRootExplorer = isRootExplorer;
    }

    @WorkerThread
    @Override
    public ReturnedValueOnReadFile call()
            throws StreamNotFoundException, IOException, OutOfMemoryError, ShellNotRunningException {
        InputStream inputStream;

        switch (fileAbstraction.scheme) {
            case CONTENT:
                Objects.requireNonNull(fileAbstraction.uri);

                final com.amaze.filemanager.application.AmazeFileManagerApplication amazeFileManagerApplication = com.amaze.filemanager.application.AmazeFileManagerApplication.getInstance();

                if (fileAbstraction.uri.getAuthority().equals(amazeFileManagerApplication.getPackageName())) {
                    DocumentFile documentFile = DocumentFile.fromSingleUri(amazeFileManagerApplication, fileAbstraction.uri);

                    if (documentFile != null && documentFile.exists() && documentFile.canWrite()) {
                        inputStream = contentResolver.openInputStream(documentFile.getUri());
                    } else {
                        inputStream = loadFile(FileUtils.fromContentUri(fileAbstraction.uri));
                    }
                } else {
                    inputStream = contentResolver.openInputStream(fileAbstraction.uri);
                }
                break;
            case FILE:
                final HybridFileParcelable hybridFileParcelable = fileAbstraction.hybridFileParcelable;
                Objects.requireNonNull(hybridFileParcelable);

                File file = hybridFileParcelable.getFile();
                inputStream = loadFile(file);

                break;
            default:
                throw new IllegalArgumentException(
                        "The scheme for '" + fileAbstraction.scheme + "' cannot be processed!");
        }

        Objects.requireNonNull(inputStream);

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        char[] buffer = new char[MAX_FILE_SIZE_CHARS];

        final int readChars = inputStreamReader.read(buffer);
        boolean tooLong = -1 != inputStream.read();

        inputStreamReader.close();

        final String fileContents;

        if (readChars == -1) {
            fileContents = "";
        } else {
            fileContents = String.valueOf(buffer, 0, readChars);
        }

        return new ReturnedValueOnReadFile(fileContents, cachedFile, tooLong);
    }

    private InputStream loadFile(File file) throws ShellNotRunningException, IOException {
        InputStream inputStream;

        if (!file.canWrite() && isRootExplorer) {
            // try loading stream associated using root
            cachedFile = new File(externalCacheDir, file.getName());
            // Scrap previously cached file if exist
            if (cachedFile.exists()) {
                cachedFile.delete();
            }
            cachedFile.createNewFile();
            cachedFile.deleteOnExit();
            // creating a cache file
            CopyFilesCommand.INSTANCE.copyFiles(file.getAbsolutePath(), cachedFile.getPath());

            inputStream = new FileInputStream(cachedFile);
        } else if (file.canRead()) {
            // readable file in filesystem
            try {
                inputStream = new FileInputStream(file.getAbsolutePath());
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException(
                        "Unable to open file [" + file.getAbsolutePath() + "] for reading");
            }
        } else {
            throw new IOException("Cannot read or write text file!");
        }

        return inputStream;
    }
}

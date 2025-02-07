package com.amaze.filemanager.asynchronous.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Formatter;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.util.Pair;

import com.amaze.filemanager.R;
import com.amaze.filemanager.filesystem.HybridFileParcelable;
import com.amaze.filemanager.filesystem.files.FileUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class CountItemsOrAndSizeTask extends AsyncTask<Void, Pair<Integer, Long>, String> {

    private final Context context;
    private final AppCompatTextView itemsText;
    private final HybridFileParcelable file;
    private final boolean isStorage;

    public CountItemsOrAndSizeTask(
            Context c, AppCompatTextView itemsText, HybridFileParcelable f, boolean storage
    ) {
        this.context = c;
        this.itemsText = itemsText;
        file = f;
        isStorage = storage;
    }

    @Override
    protected String doInBackground(Void[] params) {
        String items;
        long fileLength = file.length(context);

        if (file.isDirectory(context)) {
            final AtomicInteger x = new AtomicInteger(0);
            file.forEachChildrenFile(context, false, file -> x.incrementAndGet());
            final int folderLength = x.intValue();
            long folderSize;

            if (isStorage) {
                folderSize = file.getUsableSpace();
            } else {
                folderSize =
                        FileUtils.folderSize(file, data -> publishProgress(new Pair<>(folderLength, data)));
            }

            items = getText(folderLength, folderSize, false);
        } else {
            items =
                    Formatter.formatFileSize(context, fileLength)
                            + (" ("
                            + fileLength
                            + " "
                            + context
                            .getResources()
                            .getQuantityString(
                                    R.plurals.bytes, (int) fileLength) // truncation is insignificant
                            + ")");
        }

        return items;
    }

    @Override
    protected void onProgressUpdate(Pair<Integer, Long>[] dataArr) {
        Pair<Integer, Long> data = dataArr[0];

        itemsText.setText(getText(data.first, data.second, true));
    }

    private String getText(int filesInFolder, long length, boolean loading) {
        String numOfItems =
                (filesInFolder != 0 ? filesInFolder + " " : "")
                        + context.getResources().getQuantityString(R.plurals.items, filesInFolder);

        return numOfItems + "; " + (loading ? ">" : "") + Formatter.formatFileSize(context, length);
    }

    protected void onPostExecute(String items) {
        itemsText.setText(items);
    }
}

package com.amaze.filemanager.asynchronous;

import androidx.annotation.NonNull;

import com.amaze.filemanager.database.UtilsHandler;
import com.amaze.filemanager.database.models.OperationData;
import com.amaze.filemanager.ui.views.drawer.Drawer;
import com.amaze.filemanager.utils.DataUtils;

import java.lang.ref.WeakReference;

public class SaveOnDataUtilsChange implements DataUtils.DataChangeListener {
    private final UtilsHandler utilsHandler = com.amaze.filemanager.application.AmazeFileManagerApplication.getInstance().getUtilsHandler();

    private final WeakReference<Drawer> drawer;

    public SaveOnDataUtilsChange(@NonNull Drawer drawer) {
        this.drawer = new WeakReference<>(drawer);
    }

    @Override
    public void onHiddenFileAdded(String path) {
        utilsHandler.saveToDatabase(new OperationData(UtilsHandler.Operation.HIDDEN, path));
    }

    @Override
    public void onHiddenFileRemoved(String path) {
        utilsHandler.removeFromDatabase(new OperationData(UtilsHandler.Operation.HIDDEN, path));
    }

    @Override
    public void onHistoryAdded(String path) {
        utilsHandler.saveToDatabase(new OperationData(UtilsHandler.Operation.HISTORY, path));
    }

    @Override
    public void onBookAdded(String[] path, boolean refreshdrawer) {
        utilsHandler.saveToDatabase(
                new OperationData(UtilsHandler.Operation.BOOKMARKS, path[0], path[1]));
        if (refreshdrawer) {
            final Drawer drawer = this.drawer.get();
            if (drawer != null) {
                drawer.refreshDrawer();
            }
        }
    }

    @Override
    public void onHistoryCleared() {
        utilsHandler.clearTable(UtilsHandler.Operation.HISTORY);
    }
}

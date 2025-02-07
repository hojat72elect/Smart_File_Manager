package com.amaze.filemanager.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amaze.filemanager.database.models.explorer.Tab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TabHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TabHandler.class);

    private final ExplorerDatabase database;

    private TabHandler(@NonNull ExplorerDatabase explorerDatabase) {
        this.database = explorerDatabase;
    }

    public static TabHandler getInstance() {
        return TabHandlerHolder.INSTANCE;
    }

    public Completable addTab(@NonNull Tab tab) {
        return database.tabDao().insertTab(tab).subscribeOn(Schedulers.io());
    }

    public void update(Tab tab) {
        database.tabDao().update(tab);
    }

    public Completable clear() {
        return database
                .tabDao()
                .clear()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Nullable
    public Tab findTab(int tabNo) {
        try {
            return database.tabDao().find(tabNo).subscribeOn(Schedulers.io()).blockingGet();
        } catch (Exception e) {
            // catch error to handle Single#onError for blockingGet
            LOG.error(e.getMessage());
            return null;
        }
    }

    public Tab[] getAllTabs() {
        List<Tab> tabList = database.tabDao().list().subscribeOn(Schedulers.io()).blockingGet();
        Tab[] tabs = new Tab[tabList.size()];
        return tabList.toArray(tabs);
    }

    private static class TabHandlerHolder {
        private static final TabHandler INSTANCE =
                new TabHandler(com.amaze.filemanager.application.AmazeFileManagerApplication.getInstance().getExplorerDatabase());
    }
}

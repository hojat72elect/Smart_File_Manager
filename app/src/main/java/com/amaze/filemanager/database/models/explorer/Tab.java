package com.amaze.filemanager.database.models.explorer;

import android.content.SharedPreferences;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.amaze.filemanager.database.ExplorerDatabase;
import com.amaze.filemanager.filesystem.files.FileUtils;


@Entity(tableName = ExplorerDatabase.TABLE_TAB)
public class Tab {

    @PrimaryKey
    @ColumnInfo(name = ExplorerDatabase.COLUMN_TAB_NO)
    public final int tabNumber;

    @ColumnInfo(name = ExplorerDatabase.COLUMN_PATH)
    public final String path;

    @ColumnInfo(name = ExplorerDatabase.COLUMN_HOME)
    public final String home;

    public Tab(int tabNumber, String path, String home) {
        this.tabNumber = tabNumber;
        this.path = path;
        this.home = home;
    }

    public String getOriginalPath(boolean savePaths, SharedPreferences sharedPreferences) {
        if (savePaths && FileUtils.isPathAccessible(path, sharedPreferences)) {
            return path;
        } else {
            return home;
        }
    }
}

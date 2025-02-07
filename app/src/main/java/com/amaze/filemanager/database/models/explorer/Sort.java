package com.amaze.filemanager.database.models.explorer;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.amaze.filemanager.database.ExplorerDatabase;


@Entity(tableName = ExplorerDatabase.TABLE_SORT)
public class Sort {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = ExplorerDatabase.COLUMN_SORT_PATH)
    public final String path;

    @ColumnInfo(name = ExplorerDatabase.COLUMN_SORT_TYPE)
    public final int type;

    public Sort(@NonNull String path, int type) {
        this.path = path;
        this.type = type;
    }
}

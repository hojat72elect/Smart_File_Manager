package com.amaze.filemanager.database.models.utilities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.amaze.filemanager.database.UtilitiesDatabase;

/**
 * Base class {@link Entity} representation of tables in utilities.db.
 *
 * <p>This class is the base classwith <code>id</code>, <code>path</code> columns common to all
 * tables.
 *
 * @see UtilitiesDatabase
 */
public abstract class OperationData {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = UtilitiesDatabase.COLUMN_ID)
    public int _id;

    @ColumnInfo(name = UtilitiesDatabase.COLUMN_PATH)
    public String path;

    public OperationData(@NonNull String path) {
        this.path = path;
    }

    @NonNull
    @Override
    public String toString() {
        return "OperationData type=[" +
                getClass().getSimpleName() +
                "],path=[" +
                path +
                "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        OperationData that = (OperationData) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        int result = getClass().getSimpleName().hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}

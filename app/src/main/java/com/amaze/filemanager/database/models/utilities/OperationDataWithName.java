package com.amaze.filemanager.database.models.utilities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.amaze.filemanager.database.UtilitiesDatabase;

/**
 * Base class {@link Entity} representation of tables in utilities.db.
 *
 * <p>This class is the base class extending {@link OperationData} adding the <code>name</code>
 * column.
 *
 * @see OperationData
 * @see UtilitiesDatabase
 */
public abstract class OperationDataWithName extends OperationData {

    @ColumnInfo(name = UtilitiesDatabase.COLUMN_NAME)
    public String name;

    public OperationDataWithName(@NonNull String name, @NonNull String path) {
        super(path);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OperationDataWithName that = (OperationDataWithName) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}

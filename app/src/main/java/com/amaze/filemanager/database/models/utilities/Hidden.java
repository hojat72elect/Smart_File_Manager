package com.amaze.filemanager.database.models.utilities;

import androidx.room.Entity;

import com.amaze.filemanager.database.UtilitiesDatabase;

/**
 * {@link Entity} representation of <code>hidden</code> table in utilities.db.
 *
 * @see UtilitiesDatabase
 */
@Entity(tableName = UtilitiesDatabase.TABLE_HIDDEN)
public class Hidden extends OperationData {

    public Hidden(String path) {
        super(path);
    }
}

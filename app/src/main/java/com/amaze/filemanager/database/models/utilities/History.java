package com.amaze.filemanager.database.models.utilities;

import androidx.room.Entity;

import com.amaze.filemanager.database.UtilitiesDatabase;

/**
 * {@link Entity} representation of <code>history</code> table in utilities.db.
 *
 * @see UtilitiesDatabase
 */
@Entity(tableName = UtilitiesDatabase.TABLE_HISTORY)
public class History extends OperationData {

    public History(String path) {
        super(path);
    }
}

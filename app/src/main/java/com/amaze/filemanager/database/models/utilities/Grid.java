package com.amaze.filemanager.database.models.utilities;

import androidx.room.Entity;

import com.amaze.filemanager.database.UtilitiesDatabase;

/**
 * {@link Entity} representation of <code>grid</code> table in utilities.db.
 *
 * @see UtilitiesDatabase
 */
@Entity(tableName = UtilitiesDatabase.TABLE_GRID)
public class Grid extends OperationData {

    public Grid(String path) {
        super(path);
    }
}

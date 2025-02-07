package com.amaze.filemanager.database.models.utilities;

import static com.amaze.filemanager.database.UtilitiesDatabase.COLUMN_NAME;
import static com.amaze.filemanager.database.UtilitiesDatabase.COLUMN_PATH;
import static com.amaze.filemanager.database.UtilitiesDatabase.TABLE_BOOKMARKS;

import androidx.room.Entity;
import androidx.room.Index;

import com.amaze.filemanager.database.UtilitiesDatabase;

/**
 * {@link Entity} representation of <code>bookmark</code> table in utilities.db.
 *
 * @see UtilitiesDatabase
 */
@Entity(
        tableName = TABLE_BOOKMARKS,
        indices = {
                @Index(
                        name = TABLE_BOOKMARKS + "_idx",
                        value = {COLUMN_NAME, COLUMN_PATH},
                        unique = true)
        })
public class Bookmark extends OperationDataWithName {
    public Bookmark(String name, String path) {
        super(name, path);
    }
}

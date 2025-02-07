package com.amaze.filemanager.database.typeconverters

import androidx.room.TypeConverter
import com.amaze.filemanager.fileoperations.filesystem.OpenMode

/** [TypeConverter] for [OpenMode] objects to database columns.  */
object OpenModeTypeConverter {
    /**
     * Convert given [OpenMode] to integer constant for database storage.
     */
    @JvmStatic
    @TypeConverter
    fun fromOpenMode(from: OpenMode): Int {
        return from.ordinal
    }

    /**
     * Convert value in database to [OpenMode].
     */
    @JvmStatic
    @TypeConverter
    fun fromDatabaseValue(from: Int): OpenMode {
        return OpenMode.getOpenMode(from)
    }
}

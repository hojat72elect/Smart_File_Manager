package com.amaze.filemanager.database.daos;

import static com.amaze.filemanager.database.UtilitiesDatabase.COLUMN_PATH;
import static com.amaze.filemanager.database.UtilitiesDatabase.TABLE_HIDDEN;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.amaze.filemanager.database.models.utilities.Hidden;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * {@link Dao} interface definition for {@link Hidden}. Concrete class is generated by Room during
 * build.
 *
 * @see Dao
 * @see Hidden
 * @see com.amaze.filemanager.database.UtilitiesDatabase
 */
@Dao
public interface HiddenEntryDao {

    @Insert
    Completable insert(Hidden instance);

    @Update
    Completable update(Hidden instance);

    @Query("SELECT " + COLUMN_PATH + " FROM " + TABLE_HIDDEN)
    Single<List<String>> listPaths();

    @Query("DELETE FROM " + TABLE_HIDDEN + " WHERE " + COLUMN_PATH + " = :path")
    Completable deleteByPath(String path);
}

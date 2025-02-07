package com.amaze.filemanager.database.daos;

import static com.amaze.filemanager.database.UtilitiesDatabase.COLUMN_NAME;
import static com.amaze.filemanager.database.UtilitiesDatabase.COLUMN_PATH;
import static com.amaze.filemanager.database.UtilitiesDatabase.TABLE_BOOKMARKS;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.amaze.filemanager.database.models.utilities.Bookmark;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * {@link Dao} interface definition for {@link Bookmark}. Concrete class is generated by Room during
 * build.
 *
 * @see Dao
 * @see Bookmark
 * @see com.amaze.filemanager.database.UtilitiesDatabase
 */
@Dao
public interface BookmarkEntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(Bookmark instance);

    @Update
    Completable update(Bookmark instance);

    @Query("SELECT * FROM " + TABLE_BOOKMARKS)
    Single<List<Bookmark>> list();

    @Query(
            "SELECT * FROM "
                    + TABLE_BOOKMARKS
                    + " WHERE "
                    + COLUMN_NAME
                    + " = :name AND "
                    + COLUMN_PATH
                    + " = :path")
    Single<Bookmark> findByNameAndPath(String name, String path);

    @Query(
            "DELETE FROM "
                    + TABLE_BOOKMARKS
                    + " WHERE "
                    + COLUMN_NAME
                    + " = :name AND "
                    + COLUMN_PATH
                    + " = :path")
    Completable deleteByNameAndPath(String name, String path);
}

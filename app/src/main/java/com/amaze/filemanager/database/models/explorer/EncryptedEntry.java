package com.amaze.filemanager.database.models.explorer;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.amaze.filemanager.database.ExplorerDatabase;
import com.amaze.filemanager.database.models.StringWrapper;
import com.amaze.filemanager.database.typeconverters.EncryptedStringTypeConverter;


@Entity(tableName = ExplorerDatabase.TABLE_ENCRYPTED)
public class EncryptedEntry {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ExplorerDatabase.COLUMN_ENCRYPTED_ID)
    private int _id;

    @ColumnInfo(name = ExplorerDatabase.COLUMN_ENCRYPTED_PATH)
    private String path;

    @ColumnInfo(name = ExplorerDatabase.COLUMN_ENCRYPTED_PASSWORD)
    @TypeConverters(EncryptedStringTypeConverter.class)
    private StringWrapper password;

    public EncryptedEntry() {
    }

    public EncryptedEntry(String path, String unencryptedPassword) {
        this.path = path;
        this.password = new StringWrapper(unencryptedPassword);
    }

    public int getId() {
        return this._id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public StringWrapper getPassword() {
        return this.password;
    }

    public void setPassword(StringWrapper password) {
        this.password = password;
    }
}

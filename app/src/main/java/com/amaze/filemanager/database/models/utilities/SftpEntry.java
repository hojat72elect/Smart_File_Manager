package com.amaze.filemanager.database.models.utilities;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.amaze.filemanager.database.UtilitiesDatabase;

/**
 * {@link Entity} representation of <code>sftp</code> table in utilities.db.
 *
 * @see UtilitiesDatabase
 */
@Entity(tableName = UtilitiesDatabase.TABLE_SFTP)
public class SftpEntry extends OperationDataWithName {

    @ColumnInfo(name = UtilitiesDatabase.COLUMN_HOST_PUBKEY)
    public String hostKey;

    @ColumnInfo(name = UtilitiesDatabase.COLUMN_PRIVATE_KEY_NAME)
    public String sshKeyName;

    @ColumnInfo(name = UtilitiesDatabase.COLUMN_PRIVATE_KEY)
    public String sshKey;

    public SftpEntry(String path, String name, String hostKey, String sshKeyName, String sshKey) {
        super(name, path);
        this.hostKey = hostKey;
        this.sshKeyName = sshKeyName;
        this.sshKey = sshKey;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());

        if (!TextUtils.isEmpty(hostKey)) sb.append(",hostKey=[").append(hostKey).append(']');

        if (!TextUtils.isEmpty(sshKeyName))
            sb.append(",sshKeyName=[").append(sshKeyName).append("],sshKey=[redacted]");

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SftpEntry sftpEntry = (SftpEntry) o;

        if (!hostKey.equals(sftpEntry.hostKey)) return false;
        return (sshKey != null && sshKey.equals(sftpEntry.sshKey))
                || sshKey == null && sftpEntry.sshKey == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + hostKey.hashCode();
        if (sshKey != null) {
            result = 31 * result + sshKey.hashCode();
        }
        return result;
    }
}

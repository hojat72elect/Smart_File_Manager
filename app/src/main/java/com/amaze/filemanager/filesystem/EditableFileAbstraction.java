package com.amaze.filemanager.filesystem;

import static com.amaze.filemanager.filesystem.EditableFileAbstraction.Scheme.CONTENT;
import static com.amaze.filemanager.filesystem.EditableFileAbstraction.Scheme.FILE;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;

import com.amaze.filemanager.utils.Utils;

/**
 * This is a special representation of a file that is to be used so that uris can be loaded as
 * editable files.
 */
public class EditableFileAbstraction {

    public final Uri uri;
    public final String name;
    public final Scheme scheme;
    public final HybridFileParcelable hybridFileParcelable;

    public EditableFileAbstraction(@NonNull Context context, @NonNull Uri uri) {
        switch (uri.getScheme()) {
            case ContentResolver.SCHEME_CONTENT:
                this.uri = uri;
                this.scheme = CONTENT;

                String tempName = null;
                Cursor c =
                        context
                                .getContentResolver()
                                .query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);

                if (c != null) {
                    c.moveToFirst();
                    try {
            /*
            The result and whether [Cursor.getString()] throws an exception when the column
            value is null or the column type is not a string type is implementation-defined.
            */
                        tempName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    } catch (Exception e) {
                        tempName = null;
                    }
                    c.close();
                }

                if (tempName == null) {
                    // At least we have something to show the user...
                    tempName = uri.getLastPathSegment();
                }

                this.name = tempName;

                this.hybridFileParcelable = null;
                break;
            case ContentResolver.SCHEME_FILE:
                this.scheme = FILE;

                String path = uri.getPath();
                if (path == null)
                    throw new NullPointerException("Uri '" + uri + "' is not hierarchical!");
                path = Utils.sanitizeInput(path);
                this.hybridFileParcelable = new HybridFileParcelable(path);

                String tempN = hybridFileParcelable.getName(context);
                if (tempN == null) tempN = uri.getLastPathSegment();
                this.name = tempN;

                this.uri = null;
                break;
            default:
                throw new IllegalArgumentException(
                        "The scheme '" + uri.getScheme() + "' cannot be processed!");
        }
    }

    public enum Scheme {
        CONTENT,
        FILE
    }
}

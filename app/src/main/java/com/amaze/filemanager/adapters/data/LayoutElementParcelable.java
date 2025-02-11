package com.amaze.filemanager.adapters.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.amaze.filemanager.fileoperations.filesystem.OpenMode;
import com.amaze.filemanager.filesystem.HybridFileParcelable;
import com.amaze.filemanager.filesystem.files.sort.ComparableParcelable;
import com.amaze.filemanager.ui.icons.Icons;
import com.amaze.filemanager.utils.Utils;

import java.io.File;

public class LayoutElementParcelable implements Parcelable, ComparableParcelable {

    public static final Parcelable.Creator<LayoutElementParcelable> CREATOR =
            new Parcelable.Creator<>() {
                public LayoutElementParcelable createFromParcel(Parcel in) {
                    return new LayoutElementParcelable(in);
                }

                public LayoutElementParcelable[] newArray(int size) {
                    return new LayoutElementParcelable[size];
                }
            };


    public final boolean isBack;
    public final int filetype;
    public final IconDataParcelable iconData;
    public final String title;
    public final String desc;
    public final String permissions;
    public final String symlink;
    public final boolean header;
    public String size;
    public boolean isDirectory;
    public long date, longSize;
    public String dateModification;
    // same as hfile.modes but different than openmode in Main.java
    private OpenMode mode = OpenMode.FILE;

    public LayoutElementParcelable(
            @NonNull Context c, String goback, boolean showThumbs
    ) {
        this(
                c,
                true,
                new File("..").getName(),
                "..",
                "",
                "",
                goback,
                0,
                false,
                "",
                true,
                showThumbs,
                OpenMode.UNKNOWN
        );
    }

    public LayoutElementParcelable(
            @NonNull Context c,
            String path,
            String permissions,
            String symlink,
            String size,
            long longSize,
            boolean header,
            String date,
            boolean isDirectory,
            boolean useThumbs,
            OpenMode openMode
    ) {
        this(
                c,
                new File(path).getName(),
                path,
                permissions,
                symlink,
                size,
                longSize,
                header,
                date,
                isDirectory,
                useThumbs,
                openMode
        );
    }

    public LayoutElementParcelable(
            @NonNull Context c,
            String title,
            String path,
            String permissions,
            String symlink,
            String size,
            long longSize,
            boolean header,
            String date,
            boolean isDirectory,
            boolean useThumbs,
            OpenMode openMode
    ) {
        this(
                c,
                false,
                title,
                path,
                permissions,
                symlink,
                size,
                longSize,
                header,
                date,
                isDirectory,
                useThumbs,
                openMode
        );
    }

    public LayoutElementParcelable(
            @NonNull Context c,
            boolean isBack,
            String title,
            String path,
            String permissions,
            String symlink,
            String size,
            long longSize,
            boolean header,
            String date,
            boolean isDirectory,
            boolean useThumbs,
            OpenMode openMode
    ) {
        filetype = Icons.getTypeOfFile(path, isDirectory);
        @DrawableRes int fallbackIcon = Icons.loadMimeIcon(path, isDirectory);
        this.mode = openMode;
        if (useThumbs) {
            switch (mode) {
                case SMB:
                case SFTP:
                case DROPBOX:
                case GDRIVE:
                case ONEDRIVE:
                case BOX:
                    if (!isDirectory
                            && (filetype == Icons.IMAGE || filetype == Icons.VIDEO || filetype == Icons.APK)) {
                        this.iconData =
                                new IconDataParcelable(IconDataParcelable.IMAGE_FROMCLOUD, path, fallbackIcon);
                    } else {
                        this.iconData = new IconDataParcelable(IconDataParcelable.IMAGE_RES, fallbackIcon);
                    }
                    break;
                // Until we find a way to properly handle threading issues with thread unsafe FTPClient,
                // we refrain from loading any files via FTP as file thumbnail. - TranceLove
                case FTP:
                    this.iconData = new IconDataParcelable(IconDataParcelable.IMAGE_RES, fallbackIcon);
                    break;
                default:
                    if (filetype == Icons.IMAGE || filetype == Icons.VIDEO || filetype == Icons.APK) {
                        this.iconData =
                                new IconDataParcelable(IconDataParcelable.IMAGE_FROMFILE, path, fallbackIcon);
                    } else {
                        this.iconData = new IconDataParcelable(IconDataParcelable.IMAGE_RES, fallbackIcon);
                    }
            }
        } else {
            this.iconData = new IconDataParcelable(IconDataParcelable.IMAGE_RES, fallbackIcon);
        }

        this.title = title;
        this.desc = path;
        this.permissions = permissions.trim();
        this.symlink = symlink.trim();
        this.size = size;
        this.header = header;
        this.longSize = longSize;
        this.isDirectory = isDirectory;
        if (!date.trim().isEmpty()) {
            this.date = Long.parseLong(date);
            this.dateModification = Utils.getDate(c, this.date);
        } else {
            this.date = 0;
            this.dateModification = "";
        }
        this.isBack = isBack;
    }

    // Hopefully it should be safe - nobody else is using this
    public LayoutElementParcelable(Parcel im) {
        filetype = im.readInt();
        iconData = im.readParcelable(IconDataParcelable.class.getClassLoader());
        title = im.readString();
        desc = im.readString();
        permissions = im.readString();
        symlink = im.readString();
        int j = im.readInt();
        date = im.readLong();
        int i = im.readInt();
        header = i != 0;
        isDirectory = j != 0;
        dateModification = im.readString();
        size = im.readString();
        longSize = im.readLong();
        isBack = im.readInt() != 0;
    }

    public OpenMode getMode() {
        return mode;
    }

    public void setMode(OpenMode mode) {
        this.mode = mode;
    }

    public HybridFileParcelable generateBaseFile() {
        HybridFileParcelable baseFile =
                new HybridFileParcelable(desc, permissions, date, longSize, isDirectory);
        baseFile.setMode(mode);
        baseFile.setName(title);
        return baseFile;
    }

    public boolean hasSymlink() {
        return symlink != null && !symlink.isEmpty();
    }

    @NonNull
    @Override
    public String toString() {
        return title + "\n" + desc;
    }

    @Override
    public int describeContents() {
        // TODO: Implement this method
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p1, int p2) {
        p1.writeInt(filetype);
        p1.writeParcelable(iconData, 0);
        p1.writeString(title);
        p1.writeString(desc);
        p1.writeString(permissions);
        p1.writeString(symlink);
        p1.writeInt(isDirectory ? 1 : 0);
        p1.writeLong(date);
        p1.writeInt(header ? 1 : 0);
        p1.writeString(dateModification);
        p1.writeString(size);
        p1.writeLong(longSize);
        p1.writeInt(isBack ? 1 : 0);
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @NonNull
    @Override
    public String getParcelableName() {
        return title;
    }

    @Override
    public long getDate() {
        return date;
    }

    @Override
    public long getSize() {
        return longSize;
    }
}

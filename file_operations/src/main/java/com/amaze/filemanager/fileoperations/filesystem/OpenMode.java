package com.amaze.filemanager.fileoperations.filesystem;

/**
 * Class denotes the type of file being handled.
 */
public enum OpenMode {
    UNKNOWN,
    FILE,
    SMB,
    /**
     * SSH/SCP/SFTP
     */
    SFTP,
    /**
     * FTP/FTP over SSL (FTPS)
     */
    FTP,
    /**
     * Network file system - reserved for #268
     */
    NFS,

    /**
     * Custom file types like apk/images/downloads (which don't have a defined path)
     */
    CUSTOM,

    ROOT,
    OTG,
    DOCUMENT_FILE,
    GDRIVE,
    DROPBOX,
    BOX,
    ONEDRIVE,

    ANDROID_DATA,
    TRASH_BIN;

    /**
     * Get open mode based on the id assigned. Generally used to retrieve this type after config
     * change or to send enum as argument
     *
     * @param ordinal the position of enum starting from 0 for first element
     */
    public static OpenMode getOpenMode(int ordinal) {
        return OpenMode.values()[ordinal];
    }
}

package com.amaze.filemanager.fileoperations.sevenz;

import java.util.BitSet;

class Archive {
    /// Offset from beginning of file + SIGNATURE_HEADER_SIZE to packed streams.
    long packPos;
    /// Size of each packed stream.
    long[] packSizes = new long[0];
    /// Whether each particular packed streams has a CRC.
    BitSet packCrcsDefined;
    /// CRCs for each packed stream, valid only if that packed stream has one.
    long[] packCrcs;
    /// Properties of solid compression blocks.
    Folder[] folders = Folder.EMPTY_FOLDER_ARRAY;
    /// Temporary properties for non-empty files (subsumed into the files array later).
    SubStreamsInfo subStreamsInfo;
    /// The files and directories in the archive.
    SevenZArchiveEntry[] files = SevenZArchiveEntry.EMPTY_SEVEN_Z_ARCHIVE_ENTRY_ARRAY;
    /// Mapping between folders, files and streams.
    StreamMap streamMap;

    private static String lengthOf(final long[] a) {
        return a == null ? "(null)" : String.valueOf(a.length);
    }

    private static String lengthOf(final Object[] a) {
        return a == null ? "(null)" : String.valueOf(a.length);
    }

    @Override
    public String toString() {
        return "Archive with packed streams starting at offset "
                + packPos
                + ", "
                + lengthOf(packSizes)
                + " pack sizes, "
                + lengthOf(packCrcs)
                + " CRCs, "
                + lengthOf(folders)
                + " folders, "
                + lengthOf(files)
                + " files and "
                + streamMap;
    }
}

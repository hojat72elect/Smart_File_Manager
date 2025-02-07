package com.amaze.filemanager.fileoperations.sevenz;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.TimeZone;
import org.apache.commons.compress.archivers.ArchiveEntry;

/**
 * An entry in a 7z archive. @NotThreadSafe
 *
 * @since 1.6
 */
public class SevenZArchiveEntry implements ArchiveEntry {
    static final SevenZArchiveEntry[] EMPTY_SEVEN_Z_ARCHIVE_ENTRY_ARRAY = new SevenZArchiveEntry[0];
    private String name;
    private boolean hasStream;
    private boolean isDirectory;
    private boolean isAntiItem;
    private boolean hasCreationDate;
    private boolean hasLastModifiedDate;
    private boolean hasAccessDate;
    private long creationDate;
    private long lastModifiedDate;
    private long accessDate;
    private boolean hasWindowsAttributes;
    private int windowsAttributes;
    private boolean hasCrc;
    private long crc, compressedCrc;
    private long size, compressedSize;
    private Iterable<? extends com.amaze.filemanager.fileoperations.sevenz.SevenZMethodConfiguration> contentMethods;

    public SevenZArchiveEntry() {
    }

    /**
     * Converts NTFS time (100 nanosecond units since 1 January 1601) to Java time.
     *
     * @param ntfsTime the NTFS time in 100 nanosecond units
     * @return the Java time
     */
    public static Date ntfsTimeToJavaTime(final long ntfsTime) {
        final Calendar ntfsEpoch = Calendar.getInstance();
        ntfsEpoch.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        ntfsEpoch.set(1601, 0, 1, 0, 0, 0);
        ntfsEpoch.set(Calendar.MILLISECOND, 0);
        final long realTime = ntfsEpoch.getTimeInMillis() + (ntfsTime / (10 * 1000));
        return new Date(realTime);
    }

    /**
     * Get this entry's name.
     *
     * <p>This method returns the raw name as it is stored inside of the archive.
     *
     * @return This entry's name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set this entry's name.
     *
     * @param name This entry's new name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Whether there is any content associated with this entry.
     *
     * @return whether there is any content associated with this entry.
     */
    public boolean hasStream() {
        return hasStream;
    }

    /**
     * Sets whether there is any content associated with this entry.
     *
     * @param hasStream whether there is any content associated with this entry.
     */
    public void setHasStream(final boolean hasStream) {
        this.hasStream = hasStream;
    }

    /**
     * Return whether or not this entry represents a directory.
     *
     * @return True if this entry is a directory.
     */
    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    /**
     * Sets whether or not this entry represents a directory.
     *
     * @param isDirectory True if this entry is a directory.
     */
    public void setDirectory(final boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    /**
     * Sets whether this is an "anti-item" used in differential backups, meaning it should delete the
     * same file from a previous backup.
     *
     * @param isAntiItem true if it is an anti-item, false otherwise
     */
    public void setAntiItem(final boolean isAntiItem) {
        this.isAntiItem = isAntiItem;
    }

    /**
     * Returns whether this entry has got a creation date at all.
     *
     * @return whether the entry has got a creation date
     */
    public boolean getHasCreationDate() {
        return hasCreationDate;
    }

    /**
     * Sets whether this entry has got a creation date at all.
     *
     * @param hasCreationDate whether the entry has got a creation date
     */
    public void setHasCreationDate(final boolean hasCreationDate) {
        this.hasCreationDate = hasCreationDate;
    }

    /**
     * Sets the creation date using NTFS time (100 nanosecond units since 1 January 1601)
     *
     * @param ntfsCreationDate the creation date
     */
    public void setCreationDate(final long ntfsCreationDate) {
        this.creationDate = ntfsCreationDate;
    }

    /**
     * Returns whether this entry has got a last modified date at all.
     *
     * @return whether this entry has got a last modified date at all
     */
    public boolean getHasLastModifiedDate() {
        return hasLastModifiedDate;
    }

    /**
     * Sets whether this entry has got a last modified date at all.
     *
     * @param hasLastModifiedDate whether this entry has got a last modified date at all
     */
    public void setHasLastModifiedDate(final boolean hasLastModifiedDate) {
        this.hasLastModifiedDate = hasLastModifiedDate;
    }

    /**
     * Gets the last modified date.
     *
     * @return the last modified date
     * @throws UnsupportedOperationException if the entry hasn't got a last modified date.
     */
    @Override
    public Date getLastModifiedDate() {
        if (hasLastModifiedDate) {
            return ntfsTimeToJavaTime(lastModifiedDate);
        }
        throw new UnsupportedOperationException("The entry doesn't have this timestamp");
    }

    /**
     * Sets the last modified date using NTFS time (100 nanosecond units since 1 January 1601)
     *
     * @param ntfsLastModifiedDate the last modified date
     */
    public void setLastModifiedDate(final long ntfsLastModifiedDate) {
        this.lastModifiedDate = ntfsLastModifiedDate;
    }

    /**
     * Returns whether this entry has got an access date at all.
     *
     * @return whether this entry has got an access date at all.
     */
    public boolean getHasAccessDate() {
        return hasAccessDate;
    }

    /**
     * Sets whether this entry has got an access date at all.
     *
     * @param hasAcessDate whether this entry has got an access date at all.
     */
    public void setHasAccessDate(final boolean hasAcessDate) {
        this.hasAccessDate = hasAcessDate;
    }

    /**
     * Sets the access date using NTFS time (100 nanosecond units since 1 January 1601)
     *
     * @param ntfsAccessDate the access date
     */
    public void setAccessDate(final long ntfsAccessDate) {
        this.accessDate = ntfsAccessDate;
    }

    /**
     * Returns whether this entry has windows attributes.
     *
     * @return whether this entry has windows attributes.
     */
    public boolean getHasWindowsAttributes() {
        return hasWindowsAttributes;
    }

    /**
     * Sets whether this entry has windows attributes.
     *
     * @param hasWindowsAttributes whether this entry has windows attributes.
     */
    public void setHasWindowsAttributes(final boolean hasWindowsAttributes) {
        this.hasWindowsAttributes = hasWindowsAttributes;
    }


    /**
     * Sets the windows attributes.
     *
     * @param windowsAttributes the windows attributes
     */
    public void setWindowsAttributes(final int windowsAttributes) {
        this.windowsAttributes = windowsAttributes;
    }

    /**
     * Returns whether this entry has got a crc.
     *
     * <p>In general entries without streams don't have a CRC either.
     *
     * @return whether this entry has got a crc.
     */
    public boolean getHasCrc() {
        return hasCrc;
    }

    /**
     * Sets whether this entry has got a crc.
     *
     * @param hasCrc whether this entry has got a crc.
     */
    public void setHasCrc(final boolean hasCrc) {
        this.hasCrc = hasCrc;
    }

    /**
     * Gets the CRC.
     *
     * @return the CRC
     * @deprecated use getCrcValue instead.
     */
    @Deprecated
    public int getCrc() {
        return (int) crc;
    }

    /**
     * Sets the CRC.
     *
     * @param crc the CRC
     * @deprecated use setCrcValue instead.
     */
    @Deprecated
    public void setCrc(final int crc) {
        this.crc = crc;
    }

    /**
     * Gets the CRC.
     *
     * @return the CRC
     * @since 1.7
     */
    public long getCrcValue() {
        return crc;
    }

    /**
     * Sets the CRC.
     *
     * @param crc the CRC
     * @since 1.7
     */
    public void setCrcValue(final long crc) {
        this.crc = crc;
    }

    /**
     * Gets the compressed CRC.
     *
     * @return the compressed CRC
     * @deprecated use getCompressedCrcValue instead.
     */
    @Deprecated
    int getCompressedCrc() {
        return (int) compressedCrc;
    }

    /**
     * Sets the compressed CRC.
     *
     * @param crc the CRC
     * @deprecated use setCompressedCrcValue instead.
     */
    @Deprecated
    void setCompressedCrc(final int crc) {
        this.compressedCrc = crc;
    }


    /**
     * Get this entry's file size.
     *
     * @return This entry's file size.
     */
    @Override
    public long getSize() {
        return size;
    }

    /**
     * Set this entry's file size.
     *
     * @param size This entry's new file size.
     */
    public void setSize(final long size) {
        this.size = size;
    }


    /**
     * Gets the (compression) methods to use for entry's content - the default is LZMA2.
     *
     * <p>Currently only {@link SevenZMethod#COPY}, {@link SevenZMethod#LZMA2}, {@link
     * SevenZMethod#BZIP2} and {@link SevenZMethod#DEFLATE} are supported when writing archives.
     *
     * <p>The methods will be consulted in iteration order to create the final output.
     *
     * @return the methods to use for the content
     * @since 1.8
     */
    public Iterable<? extends com.amaze.filemanager.fileoperations.sevenz.SevenZMethodConfiguration> getContentMethods() {
        return contentMethods;
    }

    /**
     * Sets the (compression) methods to use for entry's content - the default is LZMA2.
     *
     * <p>Currently only {@link SevenZMethod#COPY}, {@link SevenZMethod#LZMA2}, {@link
     * SevenZMethod#BZIP2} and {@link SevenZMethod#DEFLATE} are supported when writing archives.
     *
     * <p>The methods will be consulted in iteration order to create the final output.
     *
     * @param methods the methods to use for the content
     * @since 1.8
     */
    public void setContentMethods(final Iterable<? extends com.amaze.filemanager.fileoperations.sevenz.SevenZMethodConfiguration> methods) {
        if (methods != null) {
            final LinkedList<com.amaze.filemanager.fileoperations.sevenz.SevenZMethodConfiguration> l = new LinkedList<>();
            for (final com.amaze.filemanager.fileoperations.sevenz.SevenZMethodConfiguration m : methods) {
                l.addLast(m);
            }
            contentMethods = Collections.unmodifiableList(l);
        } else {
            contentMethods = null;
        }
    }

    @Override
    public int hashCode() {
        final String n = getName();
        return n == null ? 0 : n.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SevenZArchiveEntry other = (SevenZArchiveEntry) obj;
        return Objects.equals(name, other.name)
                && hasStream == other.hasStream
                && isDirectory == other.isDirectory
                && isAntiItem == other.isAntiItem
                && hasCreationDate == other.hasCreationDate
                && hasLastModifiedDate == other.hasLastModifiedDate
                && hasAccessDate == other.hasAccessDate
                && creationDate == other.creationDate
                && lastModifiedDate == other.lastModifiedDate
                && accessDate == other.accessDate
                && hasWindowsAttributes == other.hasWindowsAttributes
                && windowsAttributes == other.windowsAttributes
                && hasCrc == other.hasCrc
                && crc == other.crc
                && compressedCrc == other.compressedCrc
                && size == other.size
                && compressedSize == other.compressedSize
                && equalSevenZMethods(contentMethods, other.contentMethods);
    }

    private boolean equalSevenZMethods(
            final Iterable<? extends com.amaze.filemanager.fileoperations.sevenz.SevenZMethodConfiguration> c1,
            final Iterable<? extends com.amaze.filemanager.fileoperations.sevenz.SevenZMethodConfiguration> c2) {
        if (c1 == null) {
            return c2 == null;
        }
        if (c2 == null) {
            return false;
        }
        final Iterator<? extends com.amaze.filemanager.fileoperations.sevenz.SevenZMethodConfiguration> i2 = c2.iterator();
        for (com.amaze.filemanager.fileoperations.sevenz.SevenZMethodConfiguration element : c1) {
            if (!i2.hasNext()) {
                return false;
            }
            if (!element.equals(i2.next())) {
                return false;
            }
        }
        return !i2.hasNext();
    }
}

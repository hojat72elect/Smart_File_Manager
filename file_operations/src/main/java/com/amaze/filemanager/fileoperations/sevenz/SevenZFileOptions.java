package com.amaze.filemanager.fileoperations.sevenz;

/**
 * Collects options for reading 7z archives.
 */
public class SevenZFileOptions {
    private static final int DEFAUL_MEMORY_LIMIT_IN_KB = Integer.MAX_VALUE;
    private static final boolean DEFAULT_USE_DEFAULTNAME_FOR_UNNAMED_ENTRIES = false;
    private static final boolean DEFAULT_TRY_TO_RECOVER_BROKEN_ARCHIVES = false;
    /**
     * The default options.
     *
     * <ul>
     *   <li>no memory limit
     *   <li>don't modify the name of unnamed entries
     * </ul>
     */
    public static final SevenZFileOptions DEFAULT =
            new SevenZFileOptions(
                    DEFAUL_MEMORY_LIMIT_IN_KB,
                    DEFAULT_USE_DEFAULTNAME_FOR_UNNAMED_ENTRIES,
                    DEFAULT_TRY_TO_RECOVER_BROKEN_ARCHIVES);
    private final int maxMemoryLimitInKb;
    private final boolean useDefaultNameForUnnamedEntries;
    private final boolean tryToRecoverBrokenArchives;

    private SevenZFileOptions(
            final int maxMemoryLimitInKb,
            final boolean useDefaultNameForUnnamedEntries,
            final boolean tryToRecoverBrokenArchives) {
        this.maxMemoryLimitInKb = maxMemoryLimitInKb;
        this.useDefaultNameForUnnamedEntries = useDefaultNameForUnnamedEntries;
        this.tryToRecoverBrokenArchives = tryToRecoverBrokenArchives;
    }

    /**
     * Obtains a builder for SevenZFileOptions.
     *
     * @return a builder for SevenZFileOptions.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the maximum amount of memory to use for parsing the archive and during extraction.
     *
     * <p>Not all codecs will honor this setting. Currently only lzma and lzma2 are supported.
     *
     * @return the maximum amount of memory to use for extraction
     */
    public int getMaxMemoryLimitInKb() {
        return maxMemoryLimitInKb;
    }

    /**
     * Gets whether entries without a name should get their names set to the archive's default file
     * name.
     *
     * @return whether entries without a name should get their names set to the archive's default file
     * name
     */
    public boolean getUseDefaultNameForUnnamedEntries() {
        return useDefaultNameForUnnamedEntries;
    }

    /**
     * Whether {@link SevenZFile} shall try to recover from a certain type of broken archive.
     *
     * @return whether SevenZFile shall try to recover from a certain type of broken archive.
     */
    public boolean getTryToRecoverBrokenArchives() {
        return tryToRecoverBrokenArchives;
    }

    /**
     * Mutable builder for the immutable {@link SevenZFileOptions}.
     */
    public static class Builder {


        /**
         * Create the {@link SevenZFileOptions}.
         *
         * @return configured {@link SevenZFileOptions}.
         */
        public SevenZFileOptions build() {
            return new SevenZFileOptions(
                    DEFAUL_MEMORY_LIMIT_IN_KB, DEFAULT_USE_DEFAULTNAME_FOR_UNNAMED_ENTRIES, DEFAULT_TRY_TO_RECOVER_BROKEN_ARCHIVES);
        }
    }
}

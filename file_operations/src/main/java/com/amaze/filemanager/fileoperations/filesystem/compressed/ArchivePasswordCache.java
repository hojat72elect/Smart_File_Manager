package com.amaze.filemanager.fileoperations.filesystem.compressed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A little cache for archive passwords, backed by {@link Map<String, String>}, that only lives
 * within the app's session, i.e. archive passwords won't be saved to disk at all.
 *
 * <p>It uses the path of the archive file as key and the password as the corresponding value.
 *
 * <p>Implemented especially for the convenience of 7z archives, which it's possible to
 * password-protect even the file/folder list of the archive. It is not limited to 7z archives, of
 * course.
 *
 * <p>Implemented as a Singleton with Bill Pugh's Singleton implementation
 *
 * @see Map
 * @see com.amaze.filemanager.filesystem.compressed.sevenz.SevenZFile
 * @see net.lingala.zip4j.ZipFile
 * @see <a href="https://github.com/TeamAmaze/AmazeFileManager/pull/1391">DataUtils, which uses Bill
 * Pugh's Singleton Implementation too</a>
 */
public class ArchivePasswordCache implements Map<String, String> {

    /**
     * Backing map
     */
    private final Map<String, String> entries;

    private ArchivePasswordCache() {
        this.entries = new HashMap<>();
    }

    /**
     * Return instance
     *
     * @return {@link ArchivePasswordCache} instance
     */
    public static ArchivePasswordCache getInstance() {
        return ArchivePasswordCacheHolder.INSTANCE;
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return entries.containsKey(key);
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        return entries.containsValue(value);
    }

    @NonNull
    @Override
    public Set<Entry<String, String>> entrySet() {
        return entries.entrySet();
    }

    @Nullable
    @Override
    public String get(@Nullable Object key) {
        return entries.get(key);
    }

    @Nullable
    @Override
    public String put(@NonNull String key, @NonNull String value) {
        return entries.put(key, value);
    }

    @Override
    public String remove(@Nullable Object key) {
        return entries.remove(key);
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ? extends String> m) {
        entries.putAll(m);
    }

    @Override
    public void clear() {
        entries.clear();
    }

    @NonNull
    @Override
    public Set<String> keySet() {
        return entries.keySet();
    }

    @NonNull
    @Override
    public Collection<String> values() {
        return entries.values();
    }

    @NonNull
    @Override
    public String toString() {
        return entries.toString();
    }

    @Override
    public int hashCode() {
        return entries.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return entries.equals(obj);
    }

    private static class ArchivePasswordCacheHolder {
        private static final ArchivePasswordCache INSTANCE = new ArchivePasswordCache();
    }
}

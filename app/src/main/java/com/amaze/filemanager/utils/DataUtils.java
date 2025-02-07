package com.amaze.filemanager.utils;

import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.amaze.filemanager.adapters.data.LayoutElementParcelable;
import com.amaze.filemanager.fileoperations.filesystem.OpenMode;
import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Box;
import com.cloudrail.si.services.Dropbox;
import com.cloudrail.si.services.GoogleDrive;
import com.cloudrail.si.services.OneDrive;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Singleton class to handle data for various services
 */

// Central data being used across activity,fragments and classes
public class DataUtils {

    public static final int LIST = 0, GRID = 1;
    private static final Logger LOG = LoggerFactory.getLogger(DataUtils.class);
    private final LinkedList<String> history = new LinkedList<>();
    private ConcurrentRadixTree<VoidValue> hiddenfiles =
            new ConcurrentRadixTree<>(new DefaultCharArrayNodeFactory());
    private InvertedRadixTree<Integer> filesGridOrList =
            new ConcurrentInvertedRadixTree<>(new DefaultCharArrayNodeFactory());
    private ArrayList<String> storages = new ArrayList<>();

    private InvertedRadixTree<Integer> tree =
            new ConcurrentInvertedRadixTree<>(new DefaultCharArrayNodeFactory());

    private ArrayList<String[]> servers = new ArrayList<>();
    private ArrayList<String[]> books = new ArrayList<>();

    private ArrayList<CloudStorage> accounts = new ArrayList<>(4);

    /**
     * List of checked items to persist when drag and drop from one tab to another
     */
    private ArrayList<LayoutElementParcelable> checkedItemsList;

    private DataChangeListener dataChangeListener;

    private DataUtils() {
    }

    public static DataUtils getInstance() {
        return DataUtilsHolder.INSTANCE;
    }

    public int containsServer(String[] a) {
        return contains(a, servers);
    }

    public int containsServer(String path) {

        synchronized (servers) {
            if (servers == null) return -1;
            int i = 0;
            for (String[] x : servers) {
                if (x[1].equals(path)) return i;
                i++;
            }
        }
        return -1;
    }

    public int containsBooks(String[] a) {
        return contains(a, books);
    }

    public void clear() {
        hiddenfiles = new ConcurrentRadixTree<>(new DefaultCharArrayNodeFactory());
        filesGridOrList = new ConcurrentInvertedRadixTree<>(new DefaultCharArrayNodeFactory());
        history.clear();
        storages = new ArrayList<>();
        tree = new ConcurrentInvertedRadixTree<>(new DefaultCharArrayNodeFactory());
        servers = new ArrayList<>();
        books = new ArrayList<>();
        accounts = new ArrayList<>();
    }

    public void registerOnDataChangedListener(DataChangeListener l) {

        dataChangeListener = l;
        clear();
    }

    int contains(String a, ArrayList<String[]> b) {
        int i = 0;
        for (String[] x : b) {
            if (x[1].equals(a)) return i;
            i++;
        }
        return -1;
    }

    int contains(String[] a, ArrayList<String[]> b) {
        if (b == null) return -1;
        int i = 0;
        for (String[] x : b) {
            if (x[0].equals(a[0]) && x[1].equals(a[1])) return i;
            i++;
        }
        return -1;
    }

    public void removeBook(int i) {
        synchronized (books) {
            if (books.size() > i) books.remove(i);
        }
    }

    public synchronized void removeAccount(OpenMode serviceType) {
        for (CloudStorage storage : accounts) {
            switch (serviceType) {
                case BOX:
                    if (storage instanceof Box) {
                        accounts.remove(storage);
                        return;
                    }
                    break;
                case DROPBOX:
                    if (storage instanceof Dropbox) {
                        accounts.remove(storage);
                        return;
                    }
                    break;
                case GDRIVE:
                    if (storage instanceof GoogleDrive) {
                        accounts.remove(storage);
                        return;
                    }
                    break;
                case ONEDRIVE:
                    if (storage instanceof OneDrive) {
                        accounts.remove(storage);
                        return;
                    }
                    break;
                default:
                    return;
            }
        }
    }

    public void removeServer(int i) {
        synchronized (servers) {
            if (servers.size() > i) servers.remove(i);
        }
    }

    public void addBook(String[] i) {
        if (containsBooks(i) != -1) {
            return;
        }
        synchronized (books) {
            books.add(i);
        }
    }

    /**
     * @param i             The bookmark name and path.
     * @param refreshdrawer boolean flag to indicate if drawer refresh is desired.
     * @return True if operation successful, false if failure.
     */
    public boolean addBook(final String[] i, boolean refreshdrawer) {
        if (containsBooks(i) != -1) {
            // book exists
            return false;
        } else {
            synchronized (books) {
                books.add(i);
            }

            if (dataChangeListener != null) {
                dataChangeListener.onBookAdded(i, refreshdrawer);
            }

            return true;
        }
    }

    public void addAccount(CloudStorage storage) {
        accounts.add(storage);
    }

    public void addServer(String[] i) {
        servers.add(i);
    }

    public void addHiddenFile(final String i) {

        synchronized (hiddenfiles) {
            hiddenfiles.put(i, VoidValue.SINGLETON);
        }
        if (dataChangeListener != null) {
            dataChangeListener.onHiddenFileAdded(i);
        }
    }

    public void removeHiddenFile(final String i) {

        synchronized (hiddenfiles) {
            hiddenfiles.remove(i);
        }
        if (dataChangeListener != null) {
            dataChangeListener.onHiddenFileRemoved(i);
        }
    }

    public LinkedList<String> getHistory() {
        return history;
    }

    public void setHistory(LinkedList<String> s) {
        history.clear();
        history.addAll(s);
    }

    public void addHistoryFile(final String i) {
        history.push(i);
        if (dataChangeListener != null) {
            dataChangeListener.onHistoryAdded(i);
        }
    }

    public void sortBook() {
        books.sort(new BookSorter());
    }

    public synchronized ArrayList<String[]> getServers() {
        return servers;
    }

    public synchronized void setServers(ArrayList<String[]> servers) {
        if (servers != null) this.servers = servers;
    }

    public synchronized ArrayList<String[]> getBooks() {
        return books;
    }

    public synchronized void setBooks(ArrayList<String[]> books) {
        if (books != null) this.books = books;
    }

    public synchronized ArrayList<CloudStorage> getAccounts() {
        return accounts;
    }

    public synchronized CloudStorage getAccount(OpenMode serviceType) {
        for (CloudStorage storage : accounts) {
            switch (serviceType) {
                case BOX:
                    if (storage instanceof Box) return storage;
                    break;
                case DROPBOX:
                    if (storage instanceof Dropbox) return storage;
                    break;
                case GDRIVE:
                    if (storage instanceof GoogleDrive) return storage;
                    break;
                case ONEDRIVE:
                    if (storage instanceof OneDrive) return storage;
                    break;
                default:
                    LOG.error("Unable to determine service type of storage {}", storage);
                    return null;
            }
        }
        return null;
    }

    public boolean isFileHidden(String path) {
        try {
            return getHiddenFiles().getValueForExactKey(path) != null;
        } catch (IllegalStateException e) {
            LOG.warn("failed to get hidden file", e);
            return false;
        }
    }

    public ConcurrentRadixTree<VoidValue> getHiddenFiles() {
        return hiddenfiles;
    }

    public synchronized void setHiddenFiles(ConcurrentRadixTree<VoidValue> hiddenfiles) {
        if (hiddenfiles != null) this.hiddenfiles = hiddenfiles;
    }

    public synchronized void setGridfiles(ArrayList<String> gridfiles) {
        if (gridfiles != null) {
            for (String gridfile : gridfiles) {
                setPathAsGridOrList(gridfile, GRID);
            }
        }
    }

    public synchronized void setListfiles(ArrayList<String> listfiles) {
        if (listfiles != null) {
            for (String gridfile : listfiles) {
                setPathAsGridOrList(gridfile, LIST);
            }
        }
    }

    public void setPathAsGridOrList(String path, int value) {
        filesGridOrList.put(path, value);
    }

    public int getListOrGridForPath(String path, int defaultValue) {
        Integer value = filesGridOrList.getValueForLongestKeyPrefixing(path);
        return value != null ? value : defaultValue;
    }

    public void clearHistory() {
        history.clear();
        if (dataChangeListener != null) {
            com.amaze.filemanager.application.AmazeFileManagerApplication.getInstance().runInBackground(() -> dataChangeListener.onHistoryCleared());
        }
    }

    public synchronized List<String> getStorages() {
        return storages;
    }

    public synchronized void setStorages(ArrayList<String> storages) {
        this.storages = storages;
    }

    public boolean putDrawerPath(MenuItem item, String path) {
        if (!TextUtils.isEmpty(path)) {
            try {
                tree.put(path, item.getItemId());
                return true;
            } catch (IllegalStateException e) {
                LOG.warn("failed to put drawer path", e);
                return false;
            }
        }
        return false;
    }

    /**
     * @param path the path to find
     * @return the id of the longest containing MenuMetadata.path in getDrawerMetadata() or null
     */
    public @Nullable Integer findLongestContainingDrawerItem(CharSequence path) {
        return tree.getValueForLongestKeyPrefixing(path);
    }

    public ArrayList<LayoutElementParcelable> getCheckedItemsList() {
        return this.checkedItemsList;
    }

    public void setCheckedItemsList(ArrayList<LayoutElementParcelable> layoutElementParcelables) {
        this.checkedItemsList = layoutElementParcelables;
    }

    /**
     * Callbacks to do original changes in database (and ui if required) The callbacks are called in a
     * main thread
     */
    public interface DataChangeListener {
        void onHiddenFileAdded(String path);

        void onHiddenFileRemoved(String path);

        void onHistoryAdded(String path);

        void onBookAdded(String[] path, boolean refreshdrawer);

        void onHistoryCleared();
    }

    private static class DataUtilsHolder {
        private static final DataUtils INSTANCE = new DataUtils();
    }
}

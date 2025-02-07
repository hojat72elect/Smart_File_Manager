package com.amaze.filemanager.ui.activities

import android.app.Application
import android.content.Intent
import android.provider.MediaStore
import androidx.collection.LruCache
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.amaze.filemanager.R
import com.amaze.filemanager.adapters.data.LayoutElementParcelable
import com.amaze.filemanager.application.AmazeFileManagerApplication
import com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem.BasicSearch
import com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem.DeepSearch
import com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem.IndexedSearch
import com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem.SearchParameters
import com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem.SearchResult
import com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem.searchParametersFromBoolean
import com.amaze.filemanager.fileoperations.filesystem.OpenMode
import com.amaze.filemanager.filesystem.HybridFile
import com.amaze.filemanager.filesystem.files.MediaConnectionUtils.scanFile
import com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_REGEX
import com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_REGEX_MATCHES
import com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_SHOW_HIDDENFILES
import com.amaze.trashbin.MoveFilesCallback
import com.amaze.trashbin.TrashBinFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File

class MainActivityViewModel(val applicationContext: Application) :
    AndroidViewModel(applicationContext) {
    var mediaCacheHash: List<List<LayoutElementParcelable>?> = List(5) { null }
    private var listCache: LruCache<String, List<LayoutElementParcelable>> = LruCache(50)

    /** The [LiveData] of the last triggered search */
    var lastSearchLiveData: LiveData<List<SearchResult>> = MutableLiveData(listOf())
        private set

    /** The [Job] of the last triggered search */
    var lastSearchJob: Job? = null
        private set

    companion object {
        /**
         * size of list to be cached for local files
         */
        const val CACHE_LOCAL_LIST_THRESHOLD: Int = 100
        private val LOG = LoggerFactory.getLogger(MainActivityViewModel::class.java)
    }

    /**
     * Put list for a given path in cache
     */
    fun putInCache(
        path: String,
        listToCache: List<LayoutElementParcelable>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            listCache.put(path, listToCache)
        }
    }

    /**
     * Removes cache for a given path
     */
    fun evictPathFromListCache(path: String) {
        viewModelScope.launch(Dispatchers.Default) {
            listCache.remove(path)
        }
    }

    /**
     * Get cache from a given path and updates files / folder count
     */
    fun getFromListCache(path: String): List<LayoutElementParcelable>? {
        return listCache.get(path)
    }

    /**
     * Get cache from a given path and updates files / folder count
     */
    fun getFromMediaFilesCache(mediaType: Int): List<LayoutElementParcelable>? {
        return mediaCacheHash[mediaType]
    }

    /**
     * Perform basic search: searches on the current directory
     */
    fun basicSearch(
        mainActivity: MainActivity,
        query: String,
    ): LiveData<List<SearchResult>> {
        val searchParameters = createSearchParameters(mainActivity)

        val path = mainActivity.currentMainFragment?.currentPath ?: ""

        val basicSearch = BasicSearch(query, path, searchParameters, this.applicationContext)

        lastSearchJob =
            viewModelScope.launch(Dispatchers.IO) {
                basicSearch.search()
            }

        lastSearchLiveData = basicSearch.foundFilesLiveData
        return basicSearch.foundFilesLiveData
    }

    /**
     * Perform indexed search: on MediaStore items from the current directory & it's children
     */
    fun indexedSearch(
        mainActivity: MainActivity,
        query: String,
    ): LiveData<List<SearchResult>> {
        val projection =
            arrayOf(
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
            )
        val cursor =
            mainActivity
                .contentResolver
                .query(MediaStore.Files.getContentUri("external"), projection, null, null, null)
                ?: return MutableLiveData()

        val searchParameters = createSearchParameters(mainActivity)

        val path = mainActivity.currentMainFragment?.currentPath ?: ""

        val indexedSearch = IndexedSearch(query, path, searchParameters, cursor)

        lastSearchJob =
            viewModelScope.launch(Dispatchers.IO) {
                indexedSearch.search()
            }

        lastSearchLiveData = indexedSearch.foundFilesLiveData
        return indexedSearch.foundFilesLiveData
    }

    /**
     * Perform deep search: search recursively for files matching [query] in the current path
     */
    fun deepSearch(
        mainActivity: MainActivity,
        query: String,
    ): LiveData<List<SearchResult>> {
        val searchParameters = createSearchParameters(mainActivity)

        val path = mainActivity.currentMainFragment?.currentPath ?: ""
        val openMode =
            mainActivity.currentMainFragment?.mainFragmentViewModel?.openMode ?: OpenMode.FILE

        val context = this.applicationContext

        val deepSearch =
            DeepSearch(
                query,
                path,
                searchParameters,
                context,
                openMode,
            )

        lastSearchJob =
            viewModelScope.launch(Dispatchers.IO) {
                deepSearch.search()
            }

        lastSearchLiveData = deepSearch.foundFilesLiveData
        return deepSearch.foundFilesLiveData
    }

    private fun createSearchParameters(mainActivity: MainActivity): SearchParameters {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        return searchParametersFromBoolean(
            showHiddenFiles = sharedPref.getBoolean(PREFERENCE_SHOW_HIDDENFILES, false),
            isRegexEnabled = sharedPref.getBoolean(PREFERENCE_REGEX, false),
            isRegexMatchesEnabled = sharedPref.getBoolean(PREFERENCE_REGEX_MATCHES, false),
            isRoot = mainActivity.isRootExplorer,
        )
    }

    /**
     * Restore files from trash bin
     */
    fun restoreFromBin(mediaFileInfoList: List<LayoutElementParcelable>) {
        viewModelScope.launch(Dispatchers.IO) {
            LOG.info("Restoring media files from bin $mediaFileInfoList")
            val filesToRestore = mutableListOf<TrashBinFile>()
            for (element in mediaFileInfoList) {
                val restoreFile =
                    element.generateBaseFile()
                        .toTrashBinRestoreFile(applicationContext)
                if (restoreFile != null) {
                    filesToRestore.add(restoreFile)
                }
            }
            AmazeFileManagerApplication.getInstance().trashBinInstance.restore(
                filesToRestore,
                true,
                object : MoveFilesCallback {
                    override fun invoke(
                        source: String,
                        dest: String,
                    ): Boolean {
                        val sourceFile = File(source)
                        val destFile = File(dest)
                        if (destFile.exists()) {
                            AmazeFileManagerApplication.toast(
                                applicationContext,
                                applicationContext.getString(R.string.fileexist),
                            )
                            return false
                        }
                        if (destFile.parentFile != null && !destFile.parentFile!!.exists()) {
                            destFile.parentFile?.mkdirs()
                        }
                        if (!sourceFile.renameTo(destFile)) {
                            return false
                        }
                        val hybridFile =
                            HybridFile(
                                OpenMode.TRASH_BIN,
                                source,
                            )
                        scanFile(applicationContext, arrayOf(hybridFile))
                        val intent = Intent(MainActivity.KEY_INTENT_LOAD_LIST)
                        hybridFile.getParent(applicationContext)?.let {
                            intent.putExtra(MainActivity.KEY_INTENT_LOAD_LIST_FILE, it)
                            applicationContext.sendBroadcast(intent)
                        }
                        return true
                    }
                },
            )
        }
    }

}

package com.amaze.filemanager.asynchronous.handlers

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.amaze.filemanager.adapters.RecyclerAdapter
import com.amaze.filemanager.filesystem.CustomFileObserver
import com.amaze.filemanager.filesystem.HybridFile
import com.amaze.filemanager.ui.fragments.MainFragment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.ref.WeakReference

class FileHandler(
    mainFragment: MainFragment,
    private val listView: RecyclerView,
    private val useThumbs: Boolean,
) : Handler(
    Looper.getMainLooper(),
) {
    private val mainFragment: WeakReference<MainFragment> = WeakReference(mainFragment)
    private val log: Logger = LoggerFactory.getLogger(FileHandler::class.java)

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val main = mainFragment.get() ?: return
        val mainFragmentViewModel = main.mainFragmentViewModel ?: return
        val elementsList = main.elementsList ?: return
        if (main.activity == null) {
            return
        }

        val path = msg.obj as? String
        when (msg.what) {
            CustomFileObserver.GOBACK -> {
                main.goBack()
            }

            CustomFileObserver.NEW_ITEM -> {
                if (path == null) {
                    log.error("Path is empty for file")
                    return
                }
                val fileCreated =
                    HybridFile(
                        mainFragmentViewModel.openMode,
                        "${main.currentPath}/$path",
                    )
                val newElement = fileCreated.generateLayoutElement(main.requireContext(), useThumbs)
                main.elementsList?.add(newElement)
            }

            CustomFileObserver.DELETED_ITEM -> {
                val index =
                    elementsList.withIndex().find {
                        File(it.value.desc).name == path
                    }?.index

                if (index != null) {
                    main.elementsList?.removeAt(index)
                }
            }

            else -> {
                super.handleMessage(msg)
                return
            }
        }
        if (listView.visibility == View.VISIBLE) {
            if (elementsList.size == 0) {
                // no item left in list, recreate views
                main.reloadListElements(
                    true,
                    !mainFragmentViewModel.isList,
                )
            } else {
                listView.adapter?.let {
                    val itemList = main.elementsList ?: listOf()
                    // we already have some elements in list view, invalidate the adapter
                    (listView.adapter as RecyclerAdapter).setItems(listView, itemList)
                }
            }
        } else {
            // there was no list view, means the directory was empty
            main.loadlist(main.currentPath, true, mainFragmentViewModel.openMode, true)
        }
        main.currentPath?.let {
            main.mainActivityViewModel?.evictPathFromListCache(it)
        }
        main.computeScroll()
    }
}

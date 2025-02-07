package com.amaze.filemanager.asynchronous.asynctasks.compress

import androidx.annotation.WorkerThread
import com.amaze.filemanager.adapters.data.CompressedObjectParcelable
import org.apache.commons.compress.archivers.ArchiveException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Collections
import java.util.concurrent.Callable

abstract class CompressedHelperCallable internal constructor(
    private val createBackItem: Boolean,
) :
    Callable<ArrayList<CompressedObjectParcelable>> {
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    @WorkerThread
    @Throws(ArchiveException::class)
    override fun call(): ArrayList<CompressedObjectParcelable> {
        val elements = ArrayList<CompressedObjectParcelable>()
        if (createBackItem) {
            elements.add(0, CompressedObjectParcelable())
        }

        addElements(elements)
        Collections.sort(elements, CompressedObjectParcelable.Sorter())
        return elements
    }

    @Throws(ArchiveException::class)
    protected abstract fun addElements(elements: ArrayList<CompressedObjectParcelable>)
}

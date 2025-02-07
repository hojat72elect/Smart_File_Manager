package com.amaze.filemanager.adapters.glide.cloudicon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.amaze.filemanager.filesystem.cloud.CloudUtil
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import java.io.IOException
import java.io.InputStream

class CloudIconDataFetcher(
    private val context: Context,
    private val path: String,
    private val width: Int,
    private val height: Int,
) : DataFetcher<Bitmap> {
    companion object {
        private val TAG = CloudIconDataFetcher::class.java.simpleName
    }

    private var inputStream: InputStream? = null

    override fun loadData(
        priority: Priority,
        callback: DataFetcher.DataCallback<in Bitmap?>,
    ) {
        inputStream = CloudUtil.getThumbnailInputStreamForCloud(context, path)
        val options =
            BitmapFactory.Options().also {
                it.outWidth = width
                it.outHeight = height
            }
        val drawable = BitmapFactory.decodeStream(inputStream, null, options)
        callback.onDataReady(drawable)
    }

    override fun cleanup() {
        try {
            inputStream?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error cleaning up cloud icon fetch", e)
        }
    }

    override fun cancel() = Unit

    override fun getDataClass(): Class<Bitmap> = Bitmap::class.java

    override fun getDataSource(): DataSource = DataSource.REMOTE
}

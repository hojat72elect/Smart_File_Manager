package com.amaze.filemanager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.amaze.filemanager.adapters.glide.apkimage.ApkImageModelLoaderFactory
import com.amaze.filemanager.adapters.glide.cloudicon.CloudIconModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

/**
 * Creates a custom Glide module for our application (?but is it really necessary?).
 * This is where we can customize Glide's behavior.
 */
@GlideModule
class AmazeFileManagerGlideModule : AppGlideModule() {

    /**
     * The heart of the module, where we register custom components.
     */
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // registers a custom ModelLoader for loading images of APKs inside the device memory.
        registry.prepend(String::class.java, Drawable::class.java, ApkImageModelLoaderFactory(context))
        registry.prepend(String::class.java, Bitmap::class.java, CloudIconModelFactory(context))
    }
}
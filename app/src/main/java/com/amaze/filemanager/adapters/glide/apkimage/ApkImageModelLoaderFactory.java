package com.amaze.filemanager.adapters.glide.apkimage;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

public class ApkImageModelLoaderFactory implements ModelLoaderFactory<String, Drawable> {

    private final Context context;

    public ApkImageModelLoaderFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ModelLoader<String, Drawable> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new ApkImageModelLoader(context);
    }

    @Override
    public void teardown() {
    }
}

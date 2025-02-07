package com.amaze.filemanager.adapters.glide.cloudicon;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

public class CloudIconModelFactory implements ModelLoaderFactory<String, Bitmap> {

    private final Context context;

    public CloudIconModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ModelLoader<String, Bitmap> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new CloudIconModelLoader(context);
    }

    @Override
    public void teardown() {
    }
}

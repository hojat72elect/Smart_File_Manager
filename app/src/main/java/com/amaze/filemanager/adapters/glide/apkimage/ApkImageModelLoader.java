package com.amaze.filemanager.adapters.glide.apkimage;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

public class ApkImageModelLoader implements ModelLoader<String, Drawable> {

    private final Context context;

    public ApkImageModelLoader(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public LoadData<Drawable> buildLoadData(@NonNull String s, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(s), new ApkImageDataFetcher(context, s));
    }

    @Override
    public boolean handles(String s) {
        return s.substring(s.length() - 4).equalsIgnoreCase(".apk");
    }
}

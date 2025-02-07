package com.amaze.filemanager.application;

import androidx.multidex.MultiDexApplication;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

public class GlideApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);
    }
}

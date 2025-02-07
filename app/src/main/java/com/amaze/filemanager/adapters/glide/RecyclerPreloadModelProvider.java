package com.amaze.filemanager.adapters.glide;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amaze.filemanager.adapters.data.IconDataParcelable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Collections;
import java.util.List;

public class RecyclerPreloadModelProvider
        implements ListPreloader.PreloadModelProvider<IconDataParcelable> {

    private final List<IconDataParcelable> urisToLoad;
    private final RequestBuilder<Drawable> request;

    public RecyclerPreloadModelProvider(
            @NonNull Fragment fragment, @NonNull List<IconDataParcelable> uris, boolean isCircled
    ) {
        urisToLoad = uris;
        RequestBuilder<Drawable> incompleteRequest = Glide.with(fragment).asDrawable();

        if (isCircled) {
            request = incompleteRequest.circleCrop();
        } else {
            request = incompleteRequest.centerCrop();
        }
    }

    @Override
    @NonNull
    public List<IconDataParcelable> getPreloadItems(int position) {
        IconDataParcelable iconData = position < urisToLoad.size() ? urisToLoad.get(position) : null;
        if (iconData == null) return Collections.emptyList();
        return Collections.singletonList(iconData);
    }

    @Override
    @Nullable
    public RequestBuilder<Drawable> getPreloadRequestBuilder(IconDataParcelable iconData) {
        RequestBuilder<Drawable> requestBuilder;
        if (iconData.type == IconDataParcelable.IMAGE_FROMFILE) {
            requestBuilder = request.load(iconData.path);
        } else if (iconData.type == IconDataParcelable.IMAGE_FROMCLOUD) {
            requestBuilder = request.load(iconData.path).diskCacheStrategy(DiskCacheStrategy.NONE);
        } else {
            requestBuilder = request.load(iconData.image);
        }
        return requestBuilder;
    }
}

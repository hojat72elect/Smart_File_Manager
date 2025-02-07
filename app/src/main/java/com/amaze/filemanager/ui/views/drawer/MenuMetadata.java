package com.amaze.filemanager.ui.views.drawer;

public final class MenuMetadata {

    public static final int ITEM_ENTRY = 1, ITEM_INTENT = 2;

    public final int type;
    public final String path;
    public final boolean hideFabInMainFragment;
    public final OnClickListener onClickListener;

    public MenuMetadata(String path, boolean hideFabInMainFragment) {
        this.type = ITEM_ENTRY;
        this.path = path;
        this.hideFabInMainFragment = hideFabInMainFragment;
        this.onClickListener = null;
    }

    public MenuMetadata(OnClickListener onClickListener) {
        this.type = ITEM_INTENT;
        this.onClickListener = onClickListener;
        this.hideFabInMainFragment = false;
        this.path = null;
    }

    public interface OnClickListener {
        void onClick();
    }
}

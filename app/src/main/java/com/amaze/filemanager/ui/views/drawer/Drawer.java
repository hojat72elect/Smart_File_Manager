package com.amaze.filemanager.ui.views.drawer;

import static com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool.FTPS_URI_PREFIX;
import static com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool.FTP_URI_PREFIX;
import static com.amaze.filemanager.filesystem.ftp.NetCopyClientConnectionPool.SSH_URI_PREFIX;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_SHOW_SIDEBAR_FOLDERS;
import static com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants.PREFERENCE_SHOW_SIDEBAR_QUICKACCESSES;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.legacy.app.ActionBarDrawerToggle;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amaze.filemanager.BuildConfig;
import com.amaze.filemanager.R;
import com.amaze.filemanager.adapters.data.StorageDirectoryParcelable;
import com.amaze.filemanager.database.CloudHandler;
import com.amaze.filemanager.fileoperations.filesystem.OpenMode;
import com.amaze.filemanager.fileoperations.filesystem.usb.SingletonUsbOtg;
import com.amaze.filemanager.filesystem.HybridFile;
import com.amaze.filemanager.filesystem.RootHelper;
import com.amaze.filemanager.filesystem.cloud.CloudUtil;
import com.amaze.filemanager.filesystem.files.FileUtils;
import com.amaze.filemanager.ui.ExtensionsKt;
import com.amaze.filemanager.ui.activities.MainActivity;
import com.amaze.filemanager.ui.activities.PreferencesActivity;
import com.amaze.filemanager.ui.dialogs.GeneralDialogCreation;
import com.amaze.filemanager.ui.fragments.CloudSheetFragment;
import com.amaze.filemanager.ui.fragments.MainFragment;
import com.amaze.filemanager.ui.fragments.preferencefragments.QuickAccessesPrefsFragment;
import com.amaze.filemanager.ui.theme.AppTheme;
import com.amaze.filemanager.utils.DataUtils;
import com.amaze.filemanager.utils.OTGUtil;
import com.amaze.filemanager.utils.TinyDB;
import com.amaze.filemanager.utils.Utils;
import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Box;
import com.cloudrail.si.services.Dropbox;
import com.cloudrail.si.services.GoogleDrive;
import com.cloudrail.si.services.OneDrive;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;

public class Drawer implements NavigationView.OnNavigationItemSelectedListener {

    public static final int STORAGES_GROUP = 0,
            SERVERS_GROUP = 1,
            CLOUDS_GROUP = 2,
            FOLDERS_GROUP = 3,
            QUICKACCESSES_GROUP = 4,
            LASTGROUP = 5;
    public static final int[] GROUPS = {
            STORAGES_GROUP, SERVERS_GROUP, CLOUDS_GROUP, FOLDERS_GROUP, QUICKACCESSES_GROUP, LASTGROUP
    };

    @NonNull
    private final MainActivity mainActivity;
    private final DataUtils dataUtils;

    private final ActionViewStateManager actionViewStateManager;
    private final DrawerLayout mDrawerLayout;
    private final CustomNavigationView navView;
    private final RelativeLayout drawerHeaderParent;
    private final AppCompatImageView donateImageView;
    private volatile int phoneStorageCount =
            0; // number of storage available (internal/external/otg etc)
    private boolean isDrawerLocked = false;
    private FragmentTransaction pending_fragmentTransaction;
    private PendingPath pendingPath;
    private String firstPath = null, secondPath = null;
    private ActionBarDrawerToggle mDrawerToggle;
    /**
     * Tablet is defined as 'width > 720dp'
     */
    private boolean isOnTablet = false;

    @SuppressLint("WrongViewCast")
    public Drawer(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        dataUtils = DataUtils.getInstance();

        View drawerHeaderLayout = mainActivity.getLayoutInflater().inflate(R.layout.drawerheader, null);
        drawerHeaderParent = drawerHeaderLayout.findViewById(R.id.drawer_header_parent);
        View drawerHeaderView = drawerHeaderLayout.findViewById(R.id.drawer_header);
        donateImageView = drawerHeaderLayout.findViewById(R.id.donate);
        AppCompatImageView telegramImageView = drawerHeaderLayout.findViewById(R.id.telegram);
        AppCompatImageView instagramImageView = drawerHeaderLayout.findViewById(R.id.instagram);
        AppCompatTextView appVersion = drawerHeaderLayout.findViewById(R.id.app_version);
        if (BuildConfig.DEBUG) {
            appVersion.setVisibility(View.VISIBLE);
        }
        donateImageView.setOnClickListener(v -> Toast.makeText(mainActivity, "You can donate money to us later, not yet!", Toast.LENGTH_LONG).show());
        telegramImageView.setOnClickListener(v -> Utils.openTelegramURL(mainActivity));

        // TODO : this should later refer to our instagram account.
        instagramImageView.setOnClickListener(v -> Toast.makeText(mainActivity, " We don't have an instagram account right now", Toast.LENGTH_LONG).show());

        navView = mainActivity.findViewById(R.id.navigation);
        navView.setNavigationItemSelectedListener(this);

        int accentColor = mainActivity.getAccent(), idleColor;

        if (mainActivity.getAppTheme().equals(AppTheme.LIGHT)) {
            idleColor = mainActivity.getResources().getColor(R.color.item_light_theme);
        } else {
            idleColor = Color.WHITE;
        }

        actionViewStateManager = new ActionViewStateManager(idleColor, accentColor);

        ColorStateList drawerColors =
                new ColorStateList(
                        new int[][]{
                                new int[]{android.R.attr.state_checked},
                                new int[]{android.R.attr.state_enabled},
                                new int[]{android.R.attr.state_pressed},
                                new int[]{android.R.attr.state_focused},
                                new int[]{android.R.attr.state_pressed}
                        },
                        new int[]{accentColor, idleColor, idleColor, idleColor, idleColor}
                );

        navView.setItemTextColor(drawerColors);
        navView.setItemIconTintList(drawerColors);

        if (mainActivity.getAppTheme().equals(AppTheme.DARK)) {
            navView.setBackgroundColor(Utils.getColor(mainActivity, R.color.holo_dark_background));
        } else if (mainActivity.getAppTheme().equals(AppTheme.BLACK)) {
            navView.setBackgroundColor(Utils.getColor(mainActivity, android.R.color.black));
        } else {
            navView.setBackgroundColor(Color.WHITE);
        }

        mDrawerLayout = mainActivity.findViewById(R.id.drawer_layout);
        drawerHeaderView.setBackgroundResource(R.drawable.amaze_header);
        if (mainActivity.findViewById(R.id.tab_frame) != null) {
            isOnTablet = true;
            mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        }
        navView.addHeaderView(drawerHeaderLayout);

        if (!isOnTablet) {
            mDrawerToggle =
                    new ActionBarDrawerToggle(
                            mainActivity, /* host Activity */
                            mDrawerLayout, /* DrawerLayout object */
                            R.drawable.ic_drawer_l, /* nav drawer image to replace 'Up' caret */
                            R.string.drawer_open, /* "open drawer" description for accessibility */
                            R.string.drawer_close /* "close drawer" description for accessibility */
                    ) {
                        public void onDrawerClosed(View view) {
                            Drawer.this.onDrawerClosed();
                        }

                        public void onDrawerOpened(View drawerView) {
                            // title.setText("Amaze File Manager");
                            // creates call to onPrepareOptionsMenu()
                        }
                    };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mainActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_l);
            mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mainActivity.getSupportActionBar().setHomeButtonEnabled(true);
            mDrawerToggle.syncState();
        }
    }

    /**
     * Refactors lock mode based on orientation
     */
    public void refactorDrawerLockMode() {
        if (mainActivity.findViewById(R.id.tab_frame) != null) {
            isOnTablet = true;
            mDrawerLayout.setScrimColor(Color.TRANSPARENT);
            open();
            lock();
        } else {
            unlockIfNotOnTablet();
            close();
        }
    }

    public void refreshDrawer() {
        Menu menu = navView.getMenu();
        menu.clear();
        actionViewStateManager.deselectCurrentActionView();

        int order = 0;
        ArrayList<StorageDirectoryParcelable> storageDirectories = mainActivity.getStorageDirectories();
        ArrayList<String> storageDirectoryPaths = new ArrayList<>();
        phoneStorageCount = 0;
        for (StorageDirectoryParcelable storageDirectory : storageDirectories) {
            String file = storageDirectory.path;
            File f = new File(file);
            String name = storageDirectory.name;
            int icon = storageDirectory.iconRes;

            HybridFile hybridFile = new HybridFile(OpenMode.UNKNOWN, file);
            hybridFile.generateMode(mainActivity);

            long totalSpace = hybridFile.getTotal(mainActivity);
            long freeSpace = hybridFile.getUsableSpace();

            storageDirectoryPaths.add(file);

            if (file.contains(OTGUtil.PREFIX_OTG) || file.startsWith(OTGUtil.PREFIX_MEDIA_REMOVABLE)) {
                addNewItem(
                        menu,
                        STORAGES_GROUP,
                        order++,
                        "OTG",
                        new MenuMetadata(file, false),
                        R.drawable.ic_usb_white_24dp,
                        R.drawable.ic_show_chart_black_24dp,
                        Formatter.formatFileSize(mainActivity, freeSpace),
                        Formatter.formatFileSize(mainActivity, totalSpace)
                );
                continue;
            }

            if (f.isDirectory() || f.canExecute()) {
                addNewItem(
                        menu,
                        STORAGES_GROUP,
                        order++,
                        name,
                        new MenuMetadata(file, false),
                        icon,
                        R.drawable.ic_show_chart_black_24dp,
                        Formatter.formatFileSize(mainActivity, freeSpace),
                        Formatter.formatFileSize(mainActivity, totalSpace)
                );
                if (phoneStorageCount == 0) firstPath = file;
                else if (phoneStorageCount == 1) secondPath = file;

                phoneStorageCount++;
            }
        }
        dataUtils.setStorages(storageDirectoryPaths);

        if (!dataUtils.getServers().isEmpty()) {
            dataUtils.getServers().sort(new com.amaze.filemanager.utils.BookSorter());
            synchronized (dataUtils.getServers()) {
                for (String[] file : dataUtils.getServers()) {
                    addNewItem(
                            menu,
                            SERVERS_GROUP,
                            order++,
                            file[0],
                            new MenuMetadata(file[1], false),
                            R.drawable.ic_settings_remote_white_24dp,
                            R.drawable.ic_edit_24dp
                    );
                }
            }
        }

        ArrayList<String[]> accountAuthenticationList = new ArrayList<>();

        if (CloudSheetFragment.isCloudProviderAvailable(mainActivity)) {
            for (CloudStorage cloudStorage : dataUtils.getAccounts()) {
                @DrawableRes int deleteIcon = R.drawable.ic_delete_grey_24dp;

                if (cloudStorage instanceof Dropbox) {
                    addNewItem(
                            menu,
                            CLOUDS_GROUP,
                            order++,
                            CloudHandler.CLOUD_NAME_DROPBOX,
                            new MenuMetadata(CloudHandler.CLOUD_PREFIX_DROPBOX + "/", false),
                            R.drawable.ic_dropbox_white_24dp,
                            deleteIcon
                    );

                    accountAuthenticationList.add(
                            new String[]{
                                    CloudHandler.CLOUD_NAME_DROPBOX, CloudHandler.CLOUD_PREFIX_DROPBOX + "/",
                            });
                } else if (cloudStorage instanceof Box) {
                    addNewItem(
                            menu,
                            CLOUDS_GROUP,
                            order++,
                            CloudHandler.CLOUD_NAME_BOX,
                            new MenuMetadata(CloudHandler.CLOUD_PREFIX_BOX + "/", false),
                            R.drawable.ic_box_white_24dp,
                            deleteIcon
                    );

                    accountAuthenticationList.add(
                            new String[]{
                                    CloudHandler.CLOUD_NAME_BOX, CloudHandler.CLOUD_PREFIX_BOX + "/",
                            });
                } else if (cloudStorage instanceof OneDrive) {
                    addNewItem(
                            menu,
                            CLOUDS_GROUP,
                            order++,
                            CloudHandler.CLOUD_NAME_ONE_DRIVE,
                            new MenuMetadata(CloudHandler.CLOUD_PREFIX_ONE_DRIVE + "/", false),
                            R.drawable.ic_onedrive_white_24dp,
                            deleteIcon
                    );

                    accountAuthenticationList.add(
                            new String[]{
                                    CloudHandler.CLOUD_NAME_ONE_DRIVE, CloudHandler.CLOUD_PREFIX_ONE_DRIVE + "/",
                            });
                } else if (cloudStorage instanceof GoogleDrive) {
                    addNewItem(
                            menu,
                            CLOUDS_GROUP,
                            order++,
                            CloudHandler.CLOUD_NAME_GOOGLE_DRIVE,
                            new MenuMetadata(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE + "/", false),
                            R.drawable.ic_google_drive_white_24dp,
                            deleteIcon
                    );

                    accountAuthenticationList.add(
                            new String[]{
                                    CloudHandler.CLOUD_NAME_GOOGLE_DRIVE, CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE + "/",
                            });
                }
            }
            accountAuthenticationList.sort(new com.amaze.filemanager.utils.BookSorter());
        }

        if (mainActivity.getBoolean(PREFERENCE_SHOW_SIDEBAR_FOLDERS)) {
            if (!dataUtils.getBooks().isEmpty()) {

                dataUtils.getBooks().sort(new com.amaze.filemanager.utils.BookSorter());

                synchronized (dataUtils.getBooks()) {
                    for (String[] file : dataUtils.getBooks()) {
                        addNewItem(
                                menu,
                                FOLDERS_GROUP,
                                order++,
                                file[0],
                                new MenuMetadata(file[1], false),
                                R.drawable.ic_folder_white_24dp,
                                R.drawable.ic_edit_24dp
                        );
                    }
                }
            }
        }

        Boolean[] quickAccessPref =
                TinyDB.getBooleanArray(
                        mainActivity.getPrefs(),
                        QuickAccessesPrefsFragment.KEY,
                        QuickAccessesPrefsFragment.Companion.getDEFAULT()
                );

        if (mainActivity.getBoolean(PREFERENCE_SHOW_SIDEBAR_QUICKACCESSES)) {
            if (quickAccessPref[0]) {
                addNewItem(
                        menu,
                        QUICKACCESSES_GROUP,
                        order++,
                        R.string.quick,
                        new MenuMetadata("5", true),
                        R.drawable.ic_star_white_24dp
                );
            }
            if (quickAccessPref[1]) {
                addNewItem(
                        menu,
                        QUICKACCESSES_GROUP,
                        order++,
                        R.string.recent,
                        new MenuMetadata("6", true),
                        R.drawable.ic_history_white_24dp
                );
            }
            if (quickAccessPref[2]) {
                addNewItem(
                        menu,
                        QUICKACCESSES_GROUP,
                        order++,
                        R.string.images,
                        new MenuMetadata("0", true),
                        R.drawable.ic_photo_library_white_24dp
                );
            }
            if (quickAccessPref[3]) {
                addNewItem(
                        menu,
                        QUICKACCESSES_GROUP,
                        order++,
                        R.string.videos,
                        new MenuMetadata("1", true),
                        R.drawable.ic_video_library_white_24dp
                );
            }
            if (quickAccessPref[4]) {
                addNewItem(
                        menu,
                        QUICKACCESSES_GROUP,
                        order++,
                        R.string.audio,
                        new MenuMetadata("2", true),
                        R.drawable.ic_library_music_white_24dp
                );
            }
            if (quickAccessPref[5]) {
                addNewItem(
                        menu,
                        QUICKACCESSES_GROUP,
                        order++,
                        R.string.documents,
                        new MenuMetadata("3", true),
                        R.drawable.ic_library_books_white_24dp
                );
            }
            if (quickAccessPref[6]) {
                addNewItem(
                        menu,
                        QUICKACCESSES_GROUP,
                        order++,
                        R.string.apks,
                        new MenuMetadata("4", true),
                        R.drawable.ic_apk_library_white_24dp
                );
            }
        }

        // todo: this option of the drawer should be connected to Amaze Utilities app.
        addNewItem(
                menu,
                LASTGROUP,
                order++,
                R.string.analyse_storage,
                new MenuMetadata(() -> Toast.makeText(mainActivity, "We haven't added this feature to the app just yet!!!!", Toast.LENGTH_LONG).show()), R.drawable.ic_round_analytics_24
        );

        // initially load trash bin items with "7" but ones listed they're referred as
        // @link{OpenMode.TRASH_BIN}
        addNewItem(
                menu,
                LASTGROUP,
                order++,
                R.string.trash_bin,
                new MenuMetadata("7", true),
                R.drawable.round_delete_outline_24
        );

        // todo: this option of the drawer should be connected to Amaze Utilities app.
        addNewItem(
                menu,
                LASTGROUP,
                order++,
                R.string.apps,
                new MenuMetadata(
                        () -> Toast.makeText(mainActivity, "We haven't added this feature to the app just yet!!!!", Toast.LENGTH_LONG).show()),
                R.drawable.ic_android_white_24dp
        );

        addNewItem(
                menu,
                LASTGROUP,
                order,
                R.string.setting,
                new MenuMetadata(
                        () -> {
                            Intent in = new Intent(mainActivity, PreferencesActivity.class);
                            mainActivity.startActivity(in);
                            mainActivity.finish();
                        }),
                R.drawable.ic_settings_white_24dp
        );

        for (int i = 0; i < navView.getMenu().size(); i++) {
            navView.getMenu().getItem(i).setEnabled(true);
        }

        for (int group : GROUPS) {
            menu.setGroupCheckable(group, true, true);
        }

        MenuItem item = navView.getSelected();
        if (item != null) {
            item.setChecked(true);
            actionViewStateManager.selectActionView(item);
        }
    }

    public AppCompatImageView getDonateImageView() {
        return this.donateImageView;
    }

    private void addNewItem(
            android.view.Menu menu,
            int group,
            int order,
            @StringRes int text,
            MenuMetadata meta,
            @DrawableRes int icon
    ) {
        addNewItem(
                menu, group, order, mainActivity.getString(text), meta, icon, null, null, null);
    }

    private void addNewItem(
            Menu menu,
            int group,
            int order,
            String text,
            MenuMetadata meta,
            @DrawableRes int icon,
            @DrawableRes Integer actionViewIcon
    ) {
        addNewItem(menu, group, order, text, meta, icon, actionViewIcon, null, null);
    }

    private void addNewItem(
            @NonNull Menu menu,
            int group,
            int order,
            String text,
            @NonNull MenuMetadata meta,
            @DrawableRes int icon,
            @DrawableRes Integer actionViewIcon,
            @Nullable String freeSpace,
            @Nullable String totalSpace
    ) {
        if (BuildConfig.DEBUG && menu.findItem(order) != null)
            throw new IllegalStateException("Item already id exists: " + order);

        MenuItem item;

        if (freeSpace != null && totalSpace != null)
            item =
                    menu.add(group, order, order, getSpannableText(text, freeSpace, totalSpace))
                            .setIcon(icon);
        else item = menu.add(group, order, order, text).setIcon(icon);

        if (TextUtils.isEmpty(meta.path)) {
            DrawerViewModel model = new ViewModelProvider(mainActivity).get(DrawerViewModel.class);
            model.putDrawerMetadata(item, meta);
        } else {
            boolean success = dataUtils.putDrawerPath(item, meta.path);
            if (success) {
                DrawerViewModel model = new ViewModelProvider(mainActivity).get(DrawerViewModel.class);
                model.putDrawerMetadata(item, meta);
            }
        }

        if (actionViewIcon != null) {
            item.setActionView(R.layout.layout_draweractionview);

            AppCompatImageButton imageView = item.getActionView().findViewById(R.id.imageButton);
            imageView.setImageResource(actionViewIcon);
            if (!mainActivity.getAppTheme().equals(AppTheme.LIGHT)) {
                imageView.setColorFilter(Color.WHITE);
            }

            item.getActionView().setOnClickListener((view) -> onNavigationItemActionClick(item));
        }
    }

    public void closeIfNotLocked() {
        if (!isLocked()) {
            close();
        }
    }

    public boolean isLocked() {
        return isDrawerLocked;
    }

    public boolean isOpen() {
        return mDrawerLayout.isDrawerOpen(navView);
    }

    public void open() {
        mDrawerLayout.openDrawer(navView);
    }

    public void close() {
        mDrawerLayout.closeDrawer(navView);
    }

    public void onDrawerClosed() {
        if (pending_fragmentTransaction != null) {
            pending_fragmentTransaction.commit();
            pending_fragmentTransaction = null;
        }

        if (pendingPath != null) {
            HybridFile hFile = new HybridFile(OpenMode.UNKNOWN, pendingPath.getPath());
            hFile.generateMode(mainActivity);
            if (hFile.isSimpleFile()) {
                FileUtils.openFile(new File(pendingPath.getPath()), mainActivity, mainActivity.getPrefs());
                resetPendingPath();
                return;
            }

            MainFragment mainFragment = mainActivity.getCurrentMainFragment();
            if (mainFragment != null) {
                mainFragment.loadlist(pendingPath.getPath(), false, OpenMode.UNKNOWN, false);
                // Set if the FAB should be hidden when displaying the pendingPath
                mainFragment.setHideFab(pendingPath.getHideFabInMainFragment());
                resetPendingPath();
            } else {
                mainActivity.goToMain(pendingPath.getPath(), pendingPath.getHideFabInMainFragment());
                resetPendingPath();
                return;
            }
        }
        mainActivity.supportInvalidateOptionsMenu();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        actionViewStateManager.deselectCurrentActionView();
        actionViewStateManager.selectActionView(item);

        DrawerViewModel model = new ViewModelProvider(mainActivity).get(DrawerViewModel.class);
        String title = item.getTitle().toString();
        MenuMetadata meta = model.getDrawerMetadata(item);

        switch (meta.type) {
            case MenuMetadata.ITEM_ENTRY:
                if (dataUtils.containsBooks(new String[]{title, meta.path}) != -1) {
                    FileUtils.checkForPath(mainActivity, meta.path, mainActivity.isRootExplorer());
                }

                if (!dataUtils.getAccounts().isEmpty()
                        && (meta.path.startsWith(CloudHandler.CLOUD_PREFIX_BOX)
                        || meta.path.startsWith(CloudHandler.CLOUD_PREFIX_DROPBOX)
                        || meta.path.startsWith(CloudHandler.CLOUD_PREFIX_ONE_DRIVE)
                        || meta.path.startsWith(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE))) {
                    // we have cloud accounts, try see if token is expired or not
                    CloudUtil.checkToken(meta.path, mainActivity);
                }

                if ((meta.path.contains(OTGUtil.PREFIX_OTG)
                        || meta.path.startsWith(OTGUtil.PREFIX_MEDIA_REMOVABLE))
                        && SingletonUsbOtg.getInstance().getUsbOtgRoot() == null) {
                    MaterialDialog dialog = GeneralDialogCreation.showOtgSafExplanationDialog(mainActivity);
                    dialog
                            .getActionButton(DialogAction.POSITIVE)
                            .setOnClickListener(
                                    (v) -> {
                                        Intent safIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

                                        ExtensionsKt.runIfDocumentsUIExists(
                                                safIntent,
                                                mainActivity,
                                                () ->
                                                        mainActivity.startActivityForResult(
                                                                safIntent, MainActivity.REQUEST_CODE_SAF)
                                        );

                                        dialog.dismiss();
                                    });
                    dialog.show();
                } else {
                    pendingPath = new PendingPath(meta.path, meta.hideFabInMainFragment);
                    closeIfNotLocked();
                    if (isLocked()) {
                        onDrawerClosed();
                    }
                }

                break;
            case MenuMetadata.ITEM_INTENT:
                meta.onClickListener.onClick();
                break;
        }

        return true;
    }

    public void onNavigationItemActionClick(MenuItem item) {
        DrawerViewModel model = new ViewModelProvider(mainActivity).get(DrawerViewModel.class);
        String title = item.getTitle().toString();
        MenuMetadata meta = model.getDrawerMetadata(item);
        String path = meta.path;

        switch (item.getGroupId()) {
            case STORAGES_GROUP:
                if (!path.equals("/")) {
                    GeneralDialogCreation.showPropertiesDialogForStorage(
                            RootHelper.generateBaseFile(new File(path), true),
                            mainActivity,
                            mainActivity.getAppTheme()
                    );
                }
                break;
            // not to remove the first bookmark (storage) and permanent bookmarks
            case SERVERS_GROUP:
            case CLOUDS_GROUP:
            case FOLDERS_GROUP:
                if (dataUtils.containsBooks(new String[]{title, path}) != -1) {
                    mainActivity.renameBookmark(title, path);
                } else if (path.startsWith("smb:/")) {
                    mainActivity.showSMBDialog(title, path, true);
                } else if (path.startsWith(SSH_URI_PREFIX)
                        || path.startsWith(FTP_URI_PREFIX)
                        || path.startsWith(FTPS_URI_PREFIX)) {
                    mainActivity.showSftpDialog(title, path, true);
                } else if (path.startsWith(CloudHandler.CLOUD_PREFIX_DROPBOX)) {
                    GeneralDialogCreation.showCloudDialog(
                            mainActivity, mainActivity.getAppTheme(), OpenMode.DROPBOX);
                } else if (path.startsWith(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE)) {
                    GeneralDialogCreation.showCloudDialog(
                            mainActivity, mainActivity.getAppTheme(), OpenMode.GDRIVE);
                } else if (path.startsWith(CloudHandler.CLOUD_PREFIX_BOX)) {
                    GeneralDialogCreation.showCloudDialog(
                            mainActivity, mainActivity.getAppTheme(), OpenMode.BOX);
                } else if (path.startsWith(CloudHandler.CLOUD_PREFIX_ONE_DRIVE)) {
                    GeneralDialogCreation.showCloudDialog(
                            mainActivity, mainActivity.getAppTheme(), OpenMode.ONEDRIVE);
                }
        }
    }

    public void selectCorrectDrawerItemForPath(final String path) {
        Integer id = dataUtils.findLongestContainingDrawerItem(path);

        if (id == null) deselectEverything();
        else {
            selectCorrectDrawerItem(id);
        }
    }

    /**
     * Select given item id in navigation drawer
     *
     * @param id given item id from menu
     */
    public void selectCorrectDrawerItem(int id) {
        if (id < 0) {
            deselectEverything();
        } else {
            MenuItem item = navView.getMenu().findItem(id);
            navView.setCheckedItem(item);
            actionViewStateManager.selectActionView(item);
        }
    }

    /**
     * Get selected item id
     *
     * @return item id from menu
     */
    public int getDrawerSelectedItem() {
        if (navView.getSelected() == null) {
            return -1;
        }
        return navView.getSelected().getItemId();
    }

    public void setBackgroundColor(@ColorInt int color) {
        mDrawerLayout.setStatusBarBackgroundColor(color);
        drawerHeaderParent.setBackgroundColor(color);
    }

    public void resetPendingPath() {
        pendingPath = null;
    }

    public void syncState() {
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (mDrawerToggle != null) mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item);
    }

    public void setDrawerIndicatorEnabled() {
        if (mDrawerToggle != null) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer_l);
        }
    }

    public void deselectEverything() {
        actionViewStateManager
                .deselectCurrentActionView(); // If you set the item as checked the listener doesn't trigger
        if (navView.getSelected() == null) {
            return;
        }

        navView.deselectItems();

        for (int i = 0; i < navView.getMenu().size(); i++) {
            navView.getMenu().getItem(i).setChecked(false);
        }
    }

    /**
     * @throws IllegalArgumentException if you try to {{@link DrawerLayout#LOCK_MODE_LOCKED_OPEN} or
     *                                  {@link DrawerLayout#LOCK_MODE_UNDEFINED} on a tablet
     */
    private void lock() {

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, navView);
        isDrawerLocked = true;
    }

    /**
     * Does nothing on tablets {@link #isOnTablet}
     *
     * @param mode {@link DrawerLayout#LOCK_MODE_LOCKED_CLOSED}, {@link
     *             DrawerLayout#LOCK_MODE_LOCKED_OPEN} or {@link DrawerLayout#LOCK_MODE_UNDEFINED}
     */
    public void lockIfNotOnTablet(int mode) {
        if (isOnTablet) {
            return;
        }

        mDrawerLayout.setDrawerLockMode(mode, navView);
        isDrawerLocked = true;
    }

    public void unlockIfNotOnTablet() {
        if (isOnTablet) {
            return;
        }

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, navView);
        isDrawerLocked = false;
    }

    public String getFirstPath() {
        return firstPath;
    }

    public String getSecondPath() {
        return secondPath;
    }

    private SpannableString getSpannableText(String text, String freeSpace, String totalSpace) {

        String s = mainActivity.getString(R.string.free_of, text, freeSpace, totalSpace);

        SpannableString spannableString = new SpannableString(s);

        spannableString.setSpan(new RelativeSizeSpan(0.8f), text.length() + 1, s.length(), 0);

        spannableString.setSpan(
                new TextAppearanceSpan(mainActivity, R.style.DrawerItemDriveSizeTextStyle),
                text.length() + 1,
                s.length(),
                0
        );

        return spannableString;
    }
}

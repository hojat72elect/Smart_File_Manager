package com.amaze.filemanager.utils;

import static com.amaze.filemanager.fileoperations.filesystem.FolderStateKt.CAN_CREATE_FILES;
import static com.amaze.filemanager.fileoperations.filesystem.FolderStateKt.DOESNT_EXIST;
import static com.amaze.filemanager.fileoperations.filesystem.FolderStateKt.WRITABLE_OR_ON_SDCARD;
import static com.amaze.filemanager.fileoperations.filesystem.OperationTypeKt.COMPRESS;
import static com.amaze.filemanager.fileoperations.filesystem.OperationTypeKt.DELETE;
import static com.amaze.filemanager.fileoperations.filesystem.OperationTypeKt.EXTRACT;
import static com.amaze.filemanager.fileoperations.filesystem.OperationTypeKt.NEW_FILE;
import static com.amaze.filemanager.fileoperations.filesystem.OperationTypeKt.NEW_FOLDER;
import static com.amaze.filemanager.fileoperations.filesystem.OperationTypeKt.RENAME;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.documentfile.provider.DocumentFile;
import androidx.preference.PreferenceManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amaze.filemanager.R;
import com.amaze.filemanager.asynchronous.asynctasks.DeleteTask;
import com.amaze.filemanager.asynchronous.management.ServiceWatcherUtil;
import com.amaze.filemanager.asynchronous.services.ZipService;
import com.amaze.filemanager.database.CloudHandler;
import com.amaze.filemanager.database.CryptHandler;
import com.amaze.filemanager.database.models.explorer.EncryptedEntry;
import com.amaze.filemanager.fileoperations.filesystem.FolderState;
import com.amaze.filemanager.fileoperations.filesystem.OpenMode;
import com.amaze.filemanager.filesystem.ExternalSdCardOperation;
import com.amaze.filemanager.filesystem.FileProperties;
import com.amaze.filemanager.filesystem.HybridFile;
import com.amaze.filemanager.filesystem.HybridFileParcelable;
import com.amaze.filemanager.filesystem.Operations;
import com.amaze.filemanager.filesystem.SafRootHolder;
import com.amaze.filemanager.filesystem.compressed.CompressedHelper;
import com.amaze.filemanager.filesystem.compressed.showcontents.Decompressor;
import com.amaze.filemanager.filesystem.files.CryptUtil;
import com.amaze.filemanager.filesystem.files.FileUtils;
import com.amaze.filemanager.filesystem.ftp.NetCopyClientUtils;
import com.amaze.filemanager.ui.ExtensionsKt;
import com.amaze.filemanager.ui.activities.MainActivity;
import com.amaze.filemanager.ui.dialogs.GeneralDialogCreation;
import com.amaze.filemanager.ui.fragments.MainFragment;
import com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants;
import com.amaze.filemanager.ui.views.WarnableTextInputValidator;
import com.amaze.filemanager.utils.smb.SmbUtil;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivityHelper {

    private static final Logger LOG = LoggerFactory.getLogger(MainActivityHelper.class);

    private final MainActivity mainActivity;
    private final DataUtils dataUtils = DataUtils.getInstance();
    public final BroadcastReceiver mNotificationReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent != null) {
                        if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
                            Toast.makeText(mainActivity, "Media Mounted", Toast.LENGTH_SHORT).show();
                            String a = intent.getData().getPath();
                            if (a != null
                                    && !a.trim().isEmpty()
                                    && new File(a).exists()
                                    && new File(a).canExecute()) {
                                dataUtils.getStorages().add(a);
                                mainActivity.getDrawer().refreshDrawer();
                            } else {
                                mainActivity.getDrawer().refreshDrawer();
                            }
                        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {

                            mainActivity.getDrawer().refreshDrawer();
                        }
                    }
                }
            };
    private final int accentColor;

    public MainActivityHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        accentColor = mainActivity.getAccent();
    }

    public void showFailedOperationDialog(
            ArrayList<HybridFileParcelable> failedOps, Context context
    ) {
        MaterialDialog.Builder mat = new MaterialDialog.Builder(context);
        mat.title(context.getString(R.string.operation_unsuccesful));
        mat.theme(mainActivity.getAppTheme().getMaterialDialogTheme());
        mat.positiveColor(accentColor);
        mat.positiveText(R.string.cancel);
        StringBuilder content = new StringBuilder(context.getString(com.amaze.filemanager.R.string.operation_fail_following));
        int k = 1;
        for (HybridFileParcelable s : failedOps) {
            content.append("\n").append(k).append(". ").append(s.getName(context));
            k++;
        }
        mat.content(content.toString());
        mat.build().show();
    }

    /**
     * Prompt a dialog to user to input directory name
     *
     * @param path current path at which directory to create
     * @param ma   {@link MainFragment} current fragment
     */
    public void mkdir(final OpenMode openMode, final String path, final MainFragment ma) {
        mk(
                R.string.newfolder,
                "",
                (dialog, which) -> {
                    AppCompatEditText textField =
                            dialog.getCustomView().findViewById(R.id.singleedittext_input);
                    String parentPath = path;
                    if (OpenMode.DOCUMENT_FILE.equals(openMode)) {
                        parentPath = FileProperties.remapPathForApi30OrAbove(path, false);
                    }
                    mkDir(
                            new HybridFile(openMode, parentPath),
                            new HybridFile(openMode, parentPath, textField.getText().toString().trim(), true),
                            ma
                    );
                    dialog.dismiss();
                },
                (text) -> {
                    boolean isValidFilename = FileProperties.isValidFilename(text);

                    if (!isValidFilename || text.startsWith(" ")) {
                        return new WarnableTextInputValidator.ReturnState(
                                WarnableTextInputValidator.ReturnState.STATE_ERROR, R.string.invalid_name);
                    } else if (text.isEmpty()) {
                        return new WarnableTextInputValidator.ReturnState(
                                WarnableTextInputValidator.ReturnState.STATE_ERROR, R.string.field_empty);
                    }

                    return new WarnableTextInputValidator.ReturnState();
                }
        );
    }

    /**
     * Prompt a dialog to user to input file name
     *
     * @param path current path at which file to create
     * @param ma   {@link MainFragment} current fragment
     */
    public void mkfile(final OpenMode openMode, final String path, final MainFragment ma) {
        mk(
                R.string.newfile,
                AppConstants.NEW_FILE_DELIMITER.concat(AppConstants.NEW_FILE_EXTENSION_TXT),
                (dialog, which) -> {
                    AppCompatEditText textField =
                            dialog.getCustomView().findViewById(R.id.singleedittext_input);
                    mkFile(
                            new HybridFile(openMode, path),
                            new HybridFile(openMode, path, textField.getText().toString().trim(), false),
                            ma
                    );
                    dialog.dismiss();
                },
                (text) -> {
                    boolean isValidFilename = FileProperties.isValidFilename(text);

                    // The redundant equalsIgnoreCase() is needed since ".txt" itself does not end with .txt
                    // (i.e. recommended as ".txt.txt"
                    if (!text.isEmpty()) {
                        if (!isValidFilename || text.startsWith(" ")) {
                            return new WarnableTextInputValidator.ReturnState(
                                    WarnableTextInputValidator.ReturnState.STATE_ERROR, R.string.invalid_name);
                        } else {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);
                            if (text.startsWith(".")
                                    && !prefs.getBoolean(PreferencesConstants.PREFERENCE_SHOW_HIDDENFILES, false)) {
                                return new WarnableTextInputValidator.ReturnState(
                                        WarnableTextInputValidator.ReturnState.STATE_WARNING,
                                        R.string.create_hidden_file_warn
                                );
                            } else if (!text.toLowerCase()
                                    .endsWith(
                                            AppConstants.NEW_FILE_DELIMITER.concat(
                                                    AppConstants.NEW_FILE_EXTENSION_TXT))) {
                                return new WarnableTextInputValidator.ReturnState(
                                        WarnableTextInputValidator.ReturnState.STATE_WARNING,
                                        R.string.create_file_suggest_txt_extension
                                );
                            }
                        }
                    } else {
                        return new WarnableTextInputValidator.ReturnState(
                                WarnableTextInputValidator.ReturnState.STATE_ERROR, R.string.field_empty);
                    }
                    return new WarnableTextInputValidator.ReturnState();
                }
        );
    }

    private void mk(
            @StringRes int newText,
            String prefill,
            final MaterialDialog.SingleButtonCallback onPositiveAction,
            final WarnableTextInputValidator.OnTextValidate validator
    ) {
        MaterialDialog dialog =
                GeneralDialogCreation.showNameDialog(
                        mainActivity,
                        mainActivity.getResources().getString(R.string.entername),
                        prefill,
                        mainActivity.getResources().getString(newText),
                        mainActivity.getResources().getString(R.string.create),
                        mainActivity.getResources().getString(R.string.cancel),
                        null,
                        onPositiveAction,
                        validator
                );
        dialog.show();

        // place cursor at the beginning
        AppCompatEditText textField = dialog.getCustomView().findViewById(R.id.singleedittext_input);
        textField.post(
                () -> textField.setSelection(0));
    }

    public String getIntegralNames(String path) {
        String newPath = "";
        switch (Integer.parseInt(path)) {
            case 0:
                newPath = mainActivity.getString(R.string.images);
                break;
            case 1:
                newPath = mainActivity.getString(R.string.videos);
                break;
            case 2:
                newPath = mainActivity.getString(R.string.audio);
                break;
            case 3:
                newPath = mainActivity.getString(R.string.documents);
                break;
            case 4:
                newPath = mainActivity.getString(R.string.apks);
                break;
            case 5:
                newPath = mainActivity.getString(R.string.quick);
                break;
            case 6:
                newPath = mainActivity.getString(R.string.recent);
                break;
            case 7:
                newPath = mainActivity.getString(R.string.trash_bin);
                break;
        }
        return newPath;
    }

    public void guideDialogForLEXA(String path) {
        guideDialogForLEXA(path, 3);
    }

    public void guideDialogForLEXA(String path, int requestCode) {
        final MaterialDialog.Builder x = new MaterialDialog.Builder(mainActivity);
        x.theme(mainActivity.getAppTheme().getMaterialDialogTheme());
        x.title(R.string.needs_access);
        LayoutInflater layoutInflater =
                (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.lexadrawer, null);
        x.customView(view, true);
        // textView
        AppCompatTextView textView = view.findViewById(R.id.description);
        textView.setText(
                mainActivity.getString(R.string.needs_access_summary)
                        + path
                        + mainActivity.getString(R.string.needs_access_summary1));
        ((AppCompatImageView) view.findViewById(R.id.icon))
                .setImageResource(R.drawable.sd_operate_step);
        x.positiveText(R.string.open)
                .negativeText(R.string.cancel)
                .positiveColor(accentColor)
                .negativeColor(accentColor)
                .onPositive((dialog, which) -> triggerStorageAccessFramework(requestCode))
                .onNegative(
                        (dialog, which) ->
                                Toast.makeText(mainActivity, R.string.error, Toast.LENGTH_SHORT).show());
        final MaterialDialog y = x.build();
        y.show();
    }

    @SuppressLint("InlinedApi")
    private void triggerStorageAccessFramework(int requestCode) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        ExtensionsKt.runIfDocumentsUIExists(
                intent, mainActivity, () -> mainActivity.startActivityForResult(intent, requestCode));
    }

    public void rename(
            OpenMode mode,
            final String oldPath,
            final String newPath,
            final String newName,
            final boolean isDirectory,
            final Activity context,
            boolean rootMode
    ) {
        final Toast toast =
                Toast.makeText(context, context.getString(R.string.renaming), Toast.LENGTH_SHORT);
        toast.show();
        HybridFile oldFile = new HybridFile(mode, oldPath);
        HybridFile newFile;
        if (Utils.isNullOrEmpty(newName)) {
            newFile = new HybridFile(mode, newPath);
        } else {
            newFile = new HybridFile(mode, newPath, newName, isDirectory);
        }
        Operations.rename(
                oldFile,
                newFile,
                rootMode,
                context,
                new Operations.ErrorCallBack() {
                    @Override
                    public void exists(HybridFile file) {
                        context.runOnUiThread(
                                () -> {
                                    toast.cancel();
                                    Toast.makeText(
                                                    mainActivity, context.getString(R.string.fileexist), Toast.LENGTH_SHORT)
                                            .show();
                                });
                    }

                    @Override
                    public void launchSAF(HybridFile file) {
                    }

                    @Override
                    public void launchSAF(final HybridFile file, final HybridFile file1) {
                        context.runOnUiThread(
                                () -> {
                                    toast.cancel();
                                    mainActivity.oppathe = file.getPath();
                                    mainActivity.oppathe1 = file1.getPath();
                                    mainActivity.operation = RENAME;
                                    guideDialogForLEXA(mainActivity.oppathe1);
                                });
                    }

                    @Override
                    public void done(final HybridFile hFile, final boolean b) {
                        context.runOnUiThread(
                                () -> {
                                    /*
                                     * DocumentFile.renameTo() may return false even when rename is successful. Hence we need an extra check
                                     * instead of merely looking at the return value
                                     */
                                    if (b || newFile.exists(context)) {
                                        Intent intent = new Intent(MainActivity.KEY_INTENT_LOAD_LIST);

                                        intent.putExtra(
                                                MainActivity.KEY_INTENT_LOAD_LIST_FILE, hFile.getParent(context));
                                        mainActivity.sendBroadcast(intent);

                                        // update the database entry to reflect rename for encrypted file
                                        if (oldPath.endsWith(CryptUtil.CRYPT_EXTENSION)) {
                                            try {
                                                CryptHandler cryptHandler = CryptHandler.INSTANCE;
                                                EncryptedEntry oldEntry = cryptHandler.findEntry(oldPath);
                                                EncryptedEntry newEntry = new EncryptedEntry();
                                                newEntry.setId(oldEntry.getId());
                                                newEntry.setPassword(oldEntry.getPassword());
                                                newEntry.setPath(newPath);
                                                cryptHandler.updateEntry(newEntry);
                                            } catch (Exception e) {
                                                LOG.warn("failure after rename, couldn't change the encrypted entry", e);
                                                // couldn't change the entry, leave it alone
                                            }
                                        }
                                    } else
                                        Toast.makeText(
                                                        context,
                                                        context.getString(R.string.operation_unsuccesful),
                                                        Toast.LENGTH_SHORT
                                                )
                                                .show();
                                });
                    }

                    @Override
                    public void invalidName(final HybridFile file) {
                        context.runOnUiThread(
                                () -> {
                                    toast.cancel();
                                    Toast.makeText(
                                                    context,
                                                    context.getString(R.string.invalid_name) + ": " + file.getName(context),
                                                    Toast.LENGTH_LONG
                                            )
                                            .show();
                                });
                    }
                }
        );
    }

    public @FolderState int checkFolder(final @NonNull File folder, Context context) {
        return checkFolder(folder.getAbsolutePath(), OpenMode.FILE, context);
    }

    public @FolderState int checkFolder(final String path, OpenMode openMode, Context context) {
        if (OpenMode.SMB.equals(openMode)) {
            return SmbUtil.checkFolder(path);
        } else if (OpenMode.SFTP.equals(openMode) || OpenMode.FTP.equals(openMode)) {
            return NetCopyClientUtils.INSTANCE.checkFolder(path);
        } else if (OpenMode.DOCUMENT_FILE.equals(openMode)) {
            DocumentFile d =
                    DocumentFile.fromTreeUri(com.amaze.filemanager.application.AmazeFileManagerApplication.getInstance(), SafRootHolder.getUriRoot());
            if (d == null) return DOESNT_EXIST;
            else {
                return WRITABLE_OR_ON_SDCARD;
            }
        } else {
            File folder = new File(path);

            if (ExternalSdCardOperation.isOnExtSdCard(folder, context)) {
                if (!folder.exists() || !folder.isDirectory()) {
                    return DOESNT_EXIST;
                }

                // On Android 5, trigger storage access framework.
                if (!FileProperties.isWritableNormalOrSaf(folder, context)) {
                    guideDialogForLEXA(folder.getPath());
                    return CAN_CREATE_FILES;
                }

                return WRITABLE_OR_ON_SDCARD;
            } else if (FileProperties.isWritable(new File(folder, FileUtils.DUMMY_FILE))) {
                return WRITABLE_OR_ON_SDCARD;
            } else return DOESNT_EXIST;
        }
    }

    /**
     * Helper method to start Compress service
     *
     * @param file      the new compressed file
     * @param baseFiles list of {@link HybridFileParcelable} to be compressed
     */
    public void compressFiles(File file, ArrayList<HybridFileParcelable> baseFiles) {
        int mode = checkFolder(file.getParentFile(), mainActivity);
        if (mode == 2) {
            mainActivity.oppathe = (file.getPath());
            mainActivity.operation = COMPRESS;
            mainActivity.oparrayList = baseFiles;
        } else if (mode == 1) {
            Intent intent2 = new Intent(mainActivity, ZipService.class);
            intent2.putExtra(ZipService.KEY_COMPRESS_PATH, file.getPath());
            intent2.putExtra(ZipService.KEY_COMPRESS_FILES, baseFiles);
            ServiceWatcherUtil.runService(mainActivity, intent2);
        } else Toast.makeText(mainActivity, R.string.not_allowed, Toast.LENGTH_SHORT).show();
    }

    public void mkFile(final HybridFile parentFile, final HybridFile path, final MainFragment ma) {
        final Toast toast =
                Toast.makeText(ma.getActivity(), ma.getString(R.string.creatingfile), Toast.LENGTH_SHORT);
        toast.show();
        Operations.mkfile(
                parentFile,
                path,
                ma.getActivity(),
                mainActivity.isRootExplorer(),
                new Operations.ErrorCallBack() {
                    @Override
                    public void exists(final HybridFile file) {
                        ma.getActivity()
                                .runOnUiThread(
                                        () -> {
                                            toast.cancel();
                                            Toast.makeText(
                                                            mainActivity,
                                                            mainActivity.getString(R.string.fileexist),
                                                            Toast.LENGTH_SHORT
                                                    )
                                                    .show();
                                            if (ma.getActivity() != null) {
                                                // retry with dialog prompted again
                                                mkfile(
                                                        file.getMode(),
                                                        file.getParent(mainActivity.getApplicationContext()),
                                                        ma
                                                );
                                            }
                                        });
                    }

                    @Override
                    public void launchSAF(HybridFile file) {

                        ma.getActivity()
                                .runOnUiThread(
                                        () -> {
                                            toast.cancel();
                                            mainActivity.oppathe = path.getPath();
                                            mainActivity.operation = NEW_FILE;
                                            guideDialogForLEXA(mainActivity.oppathe);
                                        });
                    }

                    @Override
                    public void launchSAF(HybridFile file, HybridFile file1) {
                    }

                    @Override
                    public void done(HybridFile hFile, final boolean b) {
                        ma.getActivity()
                                .runOnUiThread(
                                        () -> {
                                            if (b) {
                                                ma.updateList(false);
                                            } else {
                                                Toast.makeText(
                                                                ma.getActivity(),
                                                                ma.getString(R.string.operation_unsuccesful),
                                                                Toast.LENGTH_SHORT
                                                        )
                                                        .show();
                                            }
                                        });
                    }

                    @Override
                    public void invalidName(final HybridFile file) {
                        ma.getActivity()
                                .runOnUiThread(
                                        () -> {
                                            toast.cancel();
                                            Toast.makeText(
                                                            ma.getActivity(),
                                                            ma.getString(R.string.invalid_name)
                                                                    + ": "
                                                                    + file.getName(ma.getMainActivity()),
                                                            Toast.LENGTH_LONG
                                                    )
                                                    .show();
                                        });
                    }
                }
        );
    }

    public void mkDir(final HybridFile parentPath, final HybridFile path, final MainFragment ma) {
        final Toast toast =
                Toast.makeText(ma.getActivity(), ma.getString(R.string.creatingfolder), Toast.LENGTH_SHORT);
        toast.show();
        Operations.mkdir(
                parentPath,
                path,
                ma.getActivity(),
                mainActivity.isRootExplorer(),
                new Operations.ErrorCallBack() {
                    @Override
                    public void exists(final HybridFile file) {
                        ma.getActivity()
                                .runOnUiThread(
                                        () -> {
                                            toast.cancel();
                                            Toast.makeText(
                                                            mainActivity,
                                                            mainActivity.getString(R.string.fileexist),
                                                            Toast.LENGTH_SHORT
                                                    )
                                                    .show();
                                            if (ma.getActivity() != null) {
                                                // retry with dialog prompted again
                                                mkdir(
                                                        file.getMode(),
                                                        file.getParent(mainActivity.getApplicationContext()),
                                                        ma
                                                );
                                            }
                                        });
                    }

                    @Override
                    public void launchSAF(HybridFile file) {
                        toast.cancel();
                        ma.getActivity()
                                .runOnUiThread(
                                        () -> {
                                            mainActivity.oppathe = path.getPath();
                                            mainActivity.operation = NEW_FOLDER;
                                            guideDialogForLEXA(mainActivity.oppathe);
                                        });
                    }

                    @Override
                    public void launchSAF(HybridFile file, HybridFile file1) {
                    }

                    @Override
                    public void done(HybridFile hFile, final boolean b) {
                        ma.getActivity()
                                .runOnUiThread(
                                        () -> {
                                            if (b) {
                                                ma.updateList(false);
                                            } else {
                                                Toast.makeText(
                                                                ma.getActivity(),
                                                                ma.getString(R.string.operation_unsuccesful),
                                                                Toast.LENGTH_SHORT
                                                        )
                                                        .show();
                                            }
                                        });
                    }

                    @Override
                    public void invalidName(final HybridFile file) {
                        ma.getActivity()
                                .runOnUiThread(
                                        () -> {
                                            toast.cancel();
                                            Toast.makeText(
                                                            ma.getActivity(),
                                                            ma.getString(R.string.invalid_name)
                                                                    + ": "
                                                                    + file.getName(ma.getMainActivity()),
                                                            Toast.LENGTH_LONG
                                                    )
                                                    .show();
                                        });
                    }
                }
        );
    }

    public void deleteFiles(ArrayList<HybridFileParcelable> files, boolean doDeletePermanently) {
        if (files == null || files.isEmpty()) return;
        if (files.get(0).isSmb() || files.get(0).isFtp()) {
            new DeleteTask(mainActivity, doDeletePermanently).execute(files);
            return;
        }
        @FolderState
        int mode =
                checkFolder(files.get(0).getParent(mainActivity), files.get(0).getMode(), mainActivity);
        if (mode == CAN_CREATE_FILES) {
            mainActivity.oparrayList = (files);
            mainActivity.operation = DELETE;
        } else if (mode == WRITABLE_OR_ON_SDCARD || mode == DOESNT_EXIST)
            new DeleteTask(mainActivity, doDeletePermanently).execute((files));
        else Toast.makeText(mainActivity, R.string.not_allowed, Toast.LENGTH_SHORT).show();
    }

    public void extractFile(@NonNull File file) {
        final File parent = file.getParentFile();
        if (parent == null) {
            Toast.makeText(mainActivity, R.string.error, Toast.LENGTH_SHORT).show();
            LOG.warn("File's parent is null {}", file.getPath());
            return;
        }

        @FolderState int mode = checkFolder(parent, mainActivity);
        switch (mode) {
            case WRITABLE_OR_ON_SDCARD:
                Decompressor decompressor = CompressedHelper.getCompressorInstance(mainActivity, file);
                if (decompressor == null) {
                    Toast.makeText(mainActivity, R.string.error_cant_decompress_that_file, Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                decompressor.decompress(file.getPath());
                break;
            case CAN_CREATE_FILES:
                mainActivity.oppathe = file.getPath();
                mainActivity.operation = EXTRACT;
                break;
            default:
                Toast.makeText(mainActivity, R.string.not_allowed, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Retrieve a path with {@link OTGUtil#PREFIX_OTG} as prefix
     */
    public String parseOTGPath(String path) {
        if (path.contains(OTGUtil.PREFIX_OTG)) return path;
        else return OTGUtil.PREFIX_OTG + path.substring(path.indexOf(":") + 1);
    }

    public String parseCloudPath(OpenMode serviceType, String path) {
        switch (serviceType) {
            case DROPBOX:
                if (path.contains(CloudHandler.CLOUD_PREFIX_DROPBOX)) return path;
                else
                    return CloudHandler.CLOUD_PREFIX_DROPBOX + path.substring(path.indexOf(":") + 1);
            case BOX:
                if (path.contains(CloudHandler.CLOUD_PREFIX_BOX)) return path;
                else return CloudHandler.CLOUD_PREFIX_BOX + path.substring(path.indexOf(":") + 1);
            case GDRIVE:
                if (path.contains(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE)) return path;
                else
                    return CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE + path.substring(path.indexOf(":") + 1);
            case ONEDRIVE:
                if (path.contains(CloudHandler.CLOUD_PREFIX_ONE_DRIVE)) return path;
                else
                    return CloudHandler.CLOUD_PREFIX_ONE_DRIVE + path.substring(path.indexOf(":") + 1);
            default:
                return path;
        }
    }
}

package com.amaze.filemanager.ui.activities.superclasses;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amaze.filemanager.R;
import com.amaze.filemanager.ui.dialogs.GeneralDialogCreation;
import com.amaze.filemanager.utils.Utils;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class PermissionsActivity extends ThemedActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final int PERMISSION_LENGTH = 4;
    public static final int STORAGE_PERMISSION = 0,
            INSTALL_APK_PERMISSION = 1,
            ALL_FILES_PERMISSION = 2,
            NOTIFICATION_PERMISSION = 3;
    private static final String TAG = PermissionsActivity.class.getSimpleName();
    private final OnPermissionGranted[] permissionCallbacks =
            new OnPermissionGranted[PERMISSION_LENGTH];

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION) {
            if (isGranted(grantResults)) {
                Utils.enableScreenRotation(this);
                permissionCallbacks[STORAGE_PERMISSION].onPermissionGranted();
                permissionCallbacks[STORAGE_PERMISSION] = null;
            } else {
                Toast.makeText(this, R.string.grantfailed, Toast.LENGTH_SHORT).show();
                requestStoragePermission(permissionCallbacks[STORAGE_PERMISSION], false);
            }
        } else if (requestCode == NOTIFICATION_PERMISSION) {
            if (isGranted(grantResults)) {
                Utils.enableScreenRotation(this);
            } else {
                Toast.makeText(this, R.string.grantfailed, Toast.LENGTH_SHORT).show();
                requestNotificationPermission(false);
            }
        } else if (requestCode == INSTALL_APK_PERMISSION) {
            if (isGranted(grantResults)) {
                permissionCallbacks[INSTALL_APK_PERMISSION].onPermissionGranted();
                permissionCallbacks[INSTALL_APK_PERMISSION] = null;
            }
        }
    }

    public boolean checkStoragePermission() {
        // Verify that all required contact permissions have been granted.

        return (ActivityCompat.checkSelfPermission(
                this, Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                == PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(
                this, Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                == PackageManager.PERMISSION_GRANTED)
                || Environment.isExternalStorageManager();
    }

    public boolean checkNotificationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestNotificationPermission(boolean isInitialStart) {
        Utils.disableScreenRotation(this);
        final MaterialDialog materialDialog =
                GeneralDialogCreation.showBasicDialog(
                        this,
                        R.string.grant_notification_permission,
                        R.string.grantper,
                        R.string.grant,
                        R.string.cancel
                );
        materialDialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(v -> finish());
        materialDialog.setCancelable(false);

        requestPermission(
                Manifest.permission.POST_NOTIFICATIONS,
                NOTIFICATION_PERMISSION,
                materialDialog,
                () -> {
                    // do nothing
                },
                isInitialStart
        );
    }

    public void requestStoragePermission(
            @NonNull final OnPermissionGranted onPermissionGranted, boolean isInitialStart
    ) {
        Utils.disableScreenRotation(this);
        final MaterialDialog materialDialog =
                GeneralDialogCreation.showBasicDialog(
                        this,
                        R.string.grant_storage_permission,
                        R.string.grantper,
                        R.string.grant,
                        R.string.cancel
                );
        materialDialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(v -> finish());
        materialDialog.setCancelable(false);

        requestPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION,
                materialDialog,
                onPermissionGranted,
                isInitialStart
        );
    }

    public void requestInstallApkPermission(
            @NonNull final OnPermissionGranted onPermissionGranted, boolean isInitialStart
    ) {
        final MaterialDialog materialDialog =
                GeneralDialogCreation.showBasicDialog(
                        this,
                        R.string.grant_apkinstall_permission,
                        R.string.grantper,
                        R.string.grant,
                        R.string.cancel
                );
        materialDialog
                .getActionButton(DialogAction.NEGATIVE)
                .setOnClickListener(v -> materialDialog.dismiss());
        materialDialog.setCancelable(false);

        requestPermission(
                Manifest.permission.REQUEST_INSTALL_PACKAGES,
                INSTALL_APK_PERMISSION,
                materialDialog,
                onPermissionGranted,
                isInitialStart
        );
    }

    /**
     * Requests permission, overrides {@param rationale}'s POSITIVE button dialog action.
     *
     * @param permission     The permission to ask for
     * @param code           {@link #STORAGE_PERMISSION} or {@link #INSTALL_APK_PERMISSION}
     * @param rationale      MaterialLayout to provide an additional rationale to the user if the
     *                       permission was not granted and the user would benefit from additional context for the use
     *                       of the permission. For example, if the request has been denied previously.
     * @param isInitialStart is the permission being requested for the first time in the application
     *                       lifecycle
     */
    private void requestPermission(
            final String permission,
            final int code,
            @NonNull final MaterialDialog rationale,
            @NonNull final OnPermissionGranted onPermissionGranted,
            boolean isInitialStart
    ) {
        permissionCallbacks[code] = onPermissionGranted;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            rationale
                    .getActionButton(DialogAction.POSITIVE)
                    .setOnClickListener(
                            v -> {
                                ActivityCompat.requestPermissions(
                                        PermissionsActivity.this, new String[]{permission}, code);
                                rationale.dismiss();
                            });
            rationale.show();
        } else if (isInitialStart) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, code);
        } else {
            Snackbar.make(findViewById(R.id.content_frame), R.string.grantfailed, BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(R.string.grant, v -> requestAllFilesAccessPermission(onPermissionGranted))
                    .show();
        }
    }

    /**
     * Request all files access on android 11+
     *
     * @param onPermissionGranted permission granted callback
     */
    public void requestAllFilesAccess(@NonNull final OnPermissionGranted onPermissionGranted) {
        if (!Environment.isExternalStorageManager()) {
            final MaterialDialog materialDialog =
                    GeneralDialogCreation.showBasicDialog(
                            this,
                            R.string.grant_all_files_permission,
                            R.string.grantper,
                            R.string.grant,
                            R.string.cancel
                    );
            materialDialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(v -> finish());
            materialDialog
                    .getActionButton(DialogAction.POSITIVE)
                    .setOnClickListener(
                            v -> {
                                requestAllFilesAccessPermission(onPermissionGranted);
                                materialDialog.dismiss();
                            });
            materialDialog.setCancelable(false);
            materialDialog.show();
        }
    }

    private void requestAllFilesAccessPermission(
            @NonNull final OnPermissionGranted onPermissionGranted
    ) {
        Utils.disableScreenRotation(this);
        permissionCallbacks[ALL_FILES_PERMISSION] = onPermissionGranted;
        try {
            Intent intent =
                    new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                            .setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (ActivityNotFoundException anf) {
            // fallback
            try {
                Intent intent =
                        new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                                .setData(Uri.parse("package:$packageName"));
                startActivity(intent);
            } catch (Exception e) {
                com.amaze.filemanager.application.AmazeFileManagerApplication.toast(this, getString(R.string.grantfailed));
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to initial activity to grant all files access", e);
            com.amaze.filemanager.application.AmazeFileManagerApplication.toast(this, getString(R.string.grantfailed));
        }
    }

    private boolean isGranted(int[] grantResults) {
        return grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    public interface OnPermissionGranted {
        void onPermissionGranted();
    }
}

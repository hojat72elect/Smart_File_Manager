package com.amaze.filemanager.ui.fragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amaze.filemanager.R;
import com.amaze.filemanager.database.CloudContract;
import com.amaze.filemanager.databinding.FragmentSheetCloudBinding;
import com.amaze.filemanager.fileoperations.filesystem.OpenMode;
import com.amaze.filemanager.ui.activities.MainActivity;
import com.amaze.filemanager.ui.dialogs.GeneralDialogCreation;
import com.amaze.filemanager.ui.dialogs.SftpConnectDialog;
import com.amaze.filemanager.ui.dialogs.SmbSearchDialog;
import com.amaze.filemanager.ui.theme.AppTheme;
import com.amaze.filemanager.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Class represents implementation of a new cloud connection sheet dialog.
 */
public class CloudSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    public static final String TAG_FRAGMENT = "cloud_fragment";

    /**
     * Determines whether cloud provider is installed or not
     */
    public static boolean isCloudProviderAvailable(Context context) {

        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(CloudContract.APP_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(
                dialog1 -> {
                    BottomSheetDialog d = (BottomSheetDialog) dialog1;

                    FrameLayout bottomSheet =
                            d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                });
        return dialog;
    }

    @android.annotation.SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View rootView = FragmentSheetCloudBinding.inflate(LayoutInflater.from(requireActivity())).getRoot();

        MainActivity activity = (MainActivity) getActivity();

        if (activity.getAppTheme().equals(AppTheme.DARK)) {
            rootView.setBackgroundColor(Utils.getColor(getContext(), R.color.holo_dark_background));
        } else if (activity.getAppTheme().equals(AppTheme.BLACK)) {
            rootView.setBackgroundColor(Utils.getColor(getContext(), android.R.color.black));
        } else {
            rootView.setBackgroundColor(Utils.getColor(getContext(), android.R.color.white));
        }

        LinearLayout mSmbLayout = rootView.findViewById(R.id.linear_layout_smb);
        LinearLayout mScpLayout = rootView.findViewById(R.id.linear_layout_scp);
        LinearLayout mBoxLayout = rootView.findViewById(R.id.linear_layout_box);
        LinearLayout mDropboxLayout = rootView.findViewById(R.id.linear_layout_dropbox);
        LinearLayout mGoogleDriveLayout = rootView.findViewById(R.id.linear_layout_google_drive);
        LinearLayout mOnedriveLayout = rootView.findViewById(R.id.linear_layout_onedrive);
        LinearLayout mGetCloudLayout = rootView.findViewById(R.id.linear_layout_get_cloud);

        if (isCloudProviderAvailable(getContext())) {

            mBoxLayout.setVisibility(View.VISIBLE);
            mDropboxLayout.setVisibility(View.VISIBLE);
            mGoogleDriveLayout.setVisibility(View.VISIBLE);
            mOnedriveLayout.setVisibility(View.VISIBLE);
            mGetCloudLayout.setVisibility(View.GONE);
        }

        mSmbLayout.setOnClickListener(this);
        mScpLayout.setOnClickListener(this);
        mBoxLayout.setOnClickListener(this);
        mDropboxLayout.setOnClickListener(this);
        mGoogleDriveLayout.setOnClickListener(this);
        mOnedriveLayout.setOnClickListener(this);
        mGetCloudLayout.setOnClickListener(this);

        dialog.setContentView(rootView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_layout_smb:
                dismiss();
                SmbSearchDialog smbDialog = new SmbSearchDialog();
                smbDialog.show(getActivity().getSupportFragmentManager(), "tab");
                return;
            case R.id.linear_layout_scp:
                dismiss();
                SftpConnectDialog sftpConnectDialog = new SftpConnectDialog();
                Bundle args = new Bundle();
                args.putBoolean("edit", false);
                sftpConnectDialog.setArguments(args);
                sftpConnectDialog.show(getFragmentManager(), "tab");
                return;
            case R.id.linear_layout_box:
                ((MainActivity) getActivity()).addConnection(OpenMode.BOX);
                break;
            case R.id.linear_layout_dropbox:
                ((MainActivity) getActivity()).addConnection(OpenMode.DROPBOX);
                break;
            case R.id.linear_layout_google_drive:
                GeneralDialogCreation.showSignInWithGoogleDialog((MainActivity) getActivity());
                break;
            case R.id.linear_layout_onedrive:
                ((MainActivity) getActivity()).addConnection(OpenMode.ONEDRIVE);
                break;
            case R.id.linear_layout_get_cloud:
                Intent cloudPluginIntent = new Intent(Intent.ACTION_VIEW);
                cloudPluginIntent.setData(Uri.parse(getString(R.string.cloud_plugin_google_play_uri)));
                try {
                    startActivity(cloudPluginIntent);
                } catch (ActivityNotFoundException ifGooglePlayIsNotInstalled) {
                    cloudPluginIntent.setData(
                            Uri.parse(getString(R.string.cloud_plugin_google_play_web_uri)));
                    startActivity(cloudPluginIntent);
                }
                break;
        }

        // dismiss this sheet dialog
        dismiss();
    }

    public interface CloudConnectionCallbacks {
        void addConnection(OpenMode service);

        void deleteConnection(OpenMode service);
    }
}

package com.amaze.filemanager.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amaze.filemanager.R;
import com.amaze.filemanager.ui.activities.superclasses.BasicActivity;
import com.amaze.filemanager.utils.DataUtils;
import com.amaze.filemanager.utils.SimpleTextWatcher;
import com.google.android.material.textfield.TextInputLayout;

public class RenameBookmark extends DialogFragment {

    private final DataUtils dataUtils = DataUtils.getInstance();
    private String title;
    private String path;
    private BookmarkCallback bookmarkCallback;

    public static RenameBookmark getInstance(String name, String path, int accentColor) {
        RenameBookmark renameBookmark = new RenameBookmark();
        Bundle bundle = new Bundle();
        bundle.putString("title", name);
        bundle.putString("path", path);
        bundle.putInt("accentColor", accentColor);

        renameBookmark.setArguments(bundle);
        return renameBookmark;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context c = getActivity();
        if (getActivity() instanceof BookmarkCallback)
            bookmarkCallback = (BookmarkCallback) getActivity();
        title = getArguments().getString("title");
        path = getArguments().getString("path");
        int accentColor = getArguments().getInt("accentColor");

        if (dataUtils.containsBooks(new String[]{title, path}) != -1) {
            final MaterialDialog materialDialog;
            String pa = path;
            MaterialDialog.Builder builder = new MaterialDialog.Builder(c);
            builder.title(R.string.rename_bookmark);
            builder.positiveColor(accentColor);
            builder.negativeColor(accentColor);
            builder.neutralColor(accentColor);
            builder.positiveText(R.string.save);
            builder.neutralText(R.string.cancel);
            builder.negativeText(R.string.delete);
            builder.theme(((BasicActivity) getActivity()).getAppTheme().getMaterialDialogTheme());
            builder.autoDismiss(false);
            View v2 = getActivity().getLayoutInflater().inflate(R.layout.rename, null);
            builder.customView(v2, true);
            final TextInputLayout t1 = v2.findViewById(R.id.t1);
            final TextInputLayout t2 = v2.findViewById(R.id.t2);
            final AppCompatEditText conName = v2.findViewById(R.id.editText4);
            conName.setText(title);
            final String s2 = getString(R.string.cant_be_empty, c.getString(R.string.path));
            conName.addTextChangedListener(
                    new SimpleTextWatcher() {
                        @Override
                        public void afterTextChanged(Editable s) {
                            if (conName.getText().toString().isEmpty()) t1.setError(s2);
                            else t1.setError("");
                        }
                    });
            final AppCompatEditText ip = v2.findViewById(R.id.editText);
            t2.setVisibility(View.GONE);
            ip.setText(pa);
            builder.onNeutral((dialog, which) -> dialog.dismiss());

            materialDialog = builder.build();
            materialDialog
                    .getActionButton(DialogAction.POSITIVE)
                    .setOnClickListener(
                            v -> {
                                String t = ip.getText().toString();
                                String name = conName.getText().toString();
                                int i;
                                if ((i = dataUtils.containsBooks(new String[]{title, path})) != -1) {
                                    if (!t.equals(title) && !t.isEmpty()) {
                                        dataUtils.removeBook(i);
                                        dataUtils.addBook(new String[]{name, t});
                                        dataUtils.sortBook();
                                        if (bookmarkCallback != null) {
                                            bookmarkCallback.modify(path, title, t, name);
                                        }
                                    }
                                }
                                materialDialog.dismiss();
                            });
            materialDialog
                    .getActionButton(DialogAction.NEGATIVE)
                    .setOnClickListener(
                            v -> {
                                int i;
                                if ((i = dataUtils.containsBooks(new String[]{title, path})) != -1) {
                                    dataUtils.removeBook(i);
                                    if (bookmarkCallback != null) {
                                        bookmarkCallback.delete(title, path);
                                    }
                                }
                                materialDialog.dismiss();
                            });
            return materialDialog;
        }
        return null;
    }

    public interface BookmarkCallback {
        void delete(String title, String path);

        void modify(String oldpath, String oldname, String newpath, String newname);
    }
}

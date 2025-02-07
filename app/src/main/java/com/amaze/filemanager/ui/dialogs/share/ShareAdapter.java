package com.amaze.filemanager.ui.dialogs.share;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amaze.filemanager.R;

import java.util.ArrayList;

class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {

    private final ArrayList<Intent> items;
    private final ArrayList<String> labels;
    private final ArrayList<Drawable> drawables;
    private final Context context;
    private MaterialDialog dialog;

    ShareAdapter(
            Context context,
            ArrayList<Intent> intents,
            ArrayList<String> labels,
            ArrayList<Drawable> arrayList1
    ) {
        items = new ArrayList<>(intents);
        this.context = context;
        this.labels = labels;
        this.drawables = arrayList1;
    }

    void updateMatDialog(MaterialDialog b) {
        this.dialog = b;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simplerow, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.render(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View rootView;

        private final AppCompatTextView textView;
        private final AppCompatImageView imageView;

        ViewHolder(View view) {
            super(view);

            rootView = view;

            textView = view.findViewById(R.id.firstline);
            imageView = view.findViewById(R.id.icon);
        }

        void render(final int position) {
            if (drawables.get(position) != null)
                imageView.setImageDrawable(drawables.get(position));
            textView.setVisibility(View.VISIBLE);
            textView.setText(labels.get(position));
            rootView.setOnClickListener(
                    v -> {
                        if (dialog != null && dialog.isShowing()) dialog.dismiss();
                        try {
                            context.startActivity(items.get(position));
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, R.string.no_app_found, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

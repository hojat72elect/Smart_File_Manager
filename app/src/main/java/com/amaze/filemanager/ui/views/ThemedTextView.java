package com.amaze.filemanager.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.amaze.filemanager.ui.activities.MainActivity;
import com.amaze.filemanager.ui.theme.AppTheme;
import com.amaze.filemanager.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * Class sets text color based on current theme, without explicit method call in app lifecycle To
 * be used only under themed activity context
 */
public class ThemedTextView extends AppCompatTextView {

    public ThemedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextViewColor(this, context);
    }

    public static void setTextViewColor(
            @NotNull AppCompatTextView textView, @NonNull Context context
    ) {
        if (((MainActivity) context).getAppTheme().equals(AppTheme.LIGHT)) {
            textView.setTextColor(Utils.getColor(context, android.R.color.black));
        } else if (((MainActivity) context).getAppTheme().equals(AppTheme.DARK)
                || ((MainActivity) context).getAppTheme().equals(AppTheme.BLACK)) {
            textView.setTextColor(Utils.getColor(context, android.R.color.white));
        }
    }
}

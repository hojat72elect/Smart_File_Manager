package com.amaze.filemanager.ui.views;

import android.content.Context;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatEditText;

import com.amaze.filemanager.R;
import com.amaze.filemanager.utils.SimpleTextWatcher;

import androidx.annotation.NonNull;

public final class WarnableTextInputValidator extends SimpleTextWatcher
        implements View.OnFocusChangeListener, View.OnTouchListener {
    private final Context context;
    private final AppCompatEditText editText;
    private final View button;
    private final WarnableTextInputLayout textInputLayout;
    private final OnTextValidate validator;
    private final @DrawableRes int warningDrawable;
    private final @DrawableRes int errorDrawable;

    public WarnableTextInputValidator(
            Context context,
            AppCompatEditText editText,
            WarnableTextInputLayout textInputLayout,
            View positiveButton,
            OnTextValidate validator
    ) {
        this.context = context;
        this.editText = editText;
        this.editText.setOnFocusChangeListener(this);
        this.editText.addTextChangedListener(this);
        this.textInputLayout = textInputLayout;
        button = positiveButton;
        button.setOnTouchListener(this);
        button.setEnabled(false);
        this.validator = validator;

        warningDrawable = R.drawable.ic_warning_24dp;
        errorDrawable = R.drawable.ic_error_24dp;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            int state = doValidate();
            button.setEnabled(state != ReturnState.STATE_ERROR);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return performClick();
    }

    public boolean performClick() {
        return doValidate() == com.amaze.filemanager.ui.views.WarnableTextInputValidator.ReturnState.STATE_ERROR;
    }

    @Override
    public void afterTextChanged(@NonNull Editable s) {
        doValidate();
    }

    /**
     * @return ReturnState.state
     */
    private int doValidate() {
        ReturnState state = validator.isTextValid(editText.getText().toString());
        switch (state.state) {
            case ReturnState.STATE_NORMAL:
                textInputLayout.removeError();
                setEditTextIcon(null);
                button.setEnabled(true);
                break;
            case ReturnState.STATE_ERROR:
                textInputLayout.setError(context.getString(state.text));
                setEditTextIcon(errorDrawable);
                button.setEnabled(false);
                break;
            case ReturnState.STATE_WARNING:
                textInputLayout.setWarning(state.text);
                setEditTextIcon(warningDrawable);
                button.setEnabled(true);
                break;
        }

        return state.state;
    }

    private void setEditTextIcon(@DrawableRes Integer drawable) {
        @DrawableRes int drawableInt = drawable != null ? drawable : 0;
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableInt, 0);
    }

    public interface OnTextValidate {
        ReturnState isTextValid(String text);
    }

    public static class ReturnState {
        public static final int STATE_NORMAL = 0, STATE_ERROR = -1, STATE_WARNING = -2;

        public final int state;
        public final @StringRes int text;

        public ReturnState() {
            state = STATE_NORMAL;
            text = 0;
        }

        public ReturnState(int state, @StringRes int text) {
            this.state = state;
            this.text = text;
        }
    }
}

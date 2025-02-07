package com.amaze.filemanager.database.models;

import androidx.annotation.NonNull;

/**
 * Simple StringWrapper.
 */
public class StringWrapper {

    public final String value;

    public StringWrapper(String value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}

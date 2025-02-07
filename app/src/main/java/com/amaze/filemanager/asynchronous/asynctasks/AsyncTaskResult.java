package com.amaze.filemanager.asynchronous.asynctasks;

/**
 * Container for AsyncTask results. Allow either result object or exception to be contained.
 *
 * @param <T> Result type
 */
public class AsyncTaskResult<T> {
    public final T result;
    public final Throwable exception;

    public AsyncTaskResult(T result) {
        this.result = result;
        this.exception = null;
    }

    public AsyncTaskResult(Throwable exception) {
        this.result = null;
        this.exception = exception;
    }
}

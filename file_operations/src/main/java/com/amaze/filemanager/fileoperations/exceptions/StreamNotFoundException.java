package com.amaze.filemanager.fileoperations.exceptions;

/**
 * Exception thrown when we can't get stream after trying any specific methods
 */
public class StreamNotFoundException extends Exception {
    private static final String MESSAGE = "Can't get stream";

    public StreamNotFoundException() {
        super(MESSAGE);
    }

    public StreamNotFoundException(String message) {
        super(message);
    }

    public StreamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StreamNotFoundException(Throwable cause) {
        super(MESSAGE, cause);
    }
}

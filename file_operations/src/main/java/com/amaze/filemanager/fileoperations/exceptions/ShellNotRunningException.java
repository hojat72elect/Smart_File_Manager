package com.amaze.filemanager.fileoperations.exceptions;

public class ShellNotRunningException extends Exception {
    public ShellNotRunningException() {
        super("Shell stopped running!");
    }
}

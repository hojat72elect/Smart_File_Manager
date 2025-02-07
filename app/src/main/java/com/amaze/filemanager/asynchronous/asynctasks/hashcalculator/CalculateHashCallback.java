package com.amaze.filemanager.asynchronous.asynctasks.hashcalculator;

import android.content.Context;

import androidx.annotation.WorkerThread;

import com.amaze.filemanager.filesystem.HybridFileParcelable;
import com.amaze.filemanager.filesystem.files.GenericCopyUtil;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Generates hashes from files (MD5 and SHA256)
 */
public class CalculateHashCallback implements Callable<Hash> {

    private final HybridFileParcelable file;
    private final Context context;
    private InputStream inputStreamMd5;
    private InputStream inputStreamSha;

    public CalculateHashCallback(HybridFileParcelable file, final Context context) {
        if (file.isSftp()) {
            throw new IllegalArgumentException("Use CalculateHashSftpCallback");
        }
        this.context = context;
        this.file = file;
    }

    @WorkerThread
    @Override
    public Hash call() throws Exception {
        boolean isNotADirectory = !file.isDirectory(context);
        this.inputStreamMd5 = file.getInputStream(context);
        this.inputStreamSha = file.getInputStream(context);

        String md5 = null;
        String sha256 = null;

        if (isNotADirectory) {
            md5 = getMD5Checksum();
            sha256 = getSHA256Checksum();
        }

        Objects.requireNonNull(md5);
        Objects.requireNonNull(sha256);

        return new Hash(md5, sha256);
    }

    // see this How-to for a faster way to convert a byte array to a HEX string

    private String getMD5Checksum() throws Exception {
        byte[] b = createChecksum();
        StringBuilder result = new StringBuilder();

        for (byte aB : b) {
            result.append(Integer.toString((aB & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    private String getSHA256Checksum() throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] input = new byte[GenericCopyUtil.DEFAULT_BUFFER_SIZE];
        int length;
        InputStream inputStream = inputStreamMd5;
        while ((length = inputStream.read(input)) != -1) {
            if (length > 0) messageDigest.update(input, 0, length);
        }

        byte[] hash = messageDigest.digest();

        StringBuilder hexString = new StringBuilder();

        for (byte aHash : hash) {
            // convert hash to base 16
            String hex = Integer.toHexString(0xff & aHash);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        inputStream.close();
        return hexString.toString();
    }

    private byte[] createChecksum() throws Exception {
        InputStream fis = inputStreamSha;

        byte[] buffer = new byte[8192];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }
}

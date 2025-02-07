package com.amaze.filemanager.fileoperations.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

class BoundedFileChannelInputStream extends InputStream {
    private static final int MAX_BUF_LEN = 8192;
    private final ByteBuffer buffer;
    private final FileChannel channel;
    private long bytesRemaining;

    public BoundedFileChannelInputStream(final FileChannel channel, final long size) {
        this.channel = channel;
        this.bytesRemaining = size;
        if (size < MAX_BUF_LEN && size > 0) {
            buffer = ByteBuffer.allocate((int) size);
        } else {
            buffer = ByteBuffer.allocate(MAX_BUF_LEN);
        }
    }

    @Override
    public int read() throws IOException {
        if (bytesRemaining > 0) {
            --bytesRemaining;
            int read = read(1);
            if (read < 0) {
                return read;
            }
            return buffer.get() & 0xff;
        }
        return -1;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (bytesRemaining == 0) {
            return -1;
        }
        int bytesToRead = len;
        if (bytesToRead > bytesRemaining) {
            bytesToRead = (int) bytesRemaining;
        }
        int bytesRead;
        ByteBuffer buf;
        if (bytesToRead <= buffer.capacity()) {
            buf = buffer;
            bytesRead = read(bytesToRead);
        } else {
            buf = ByteBuffer.allocate(bytesToRead);
            bytesRead = channel.read(buf);
            buf.flip();
        }
        if (bytesRead >= 0) {
            buf.get(b, off, bytesRead);
            bytesRemaining -= bytesRead;
        }
        return bytesRead;
    }

    private int read(int len) throws IOException {
        buffer.rewind().limit(len);
        int read = channel.read(buffer);
        buffer.flip();
        return read;
    }

    @Override
    public void close() {
        // the nested channel is controlled externally
    }
}

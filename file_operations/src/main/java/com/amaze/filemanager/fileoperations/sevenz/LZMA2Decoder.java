package com.amaze.filemanager.fileoperations.sevenz;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.MemoryLimitException;
import org.tukaani.xz.LZMA2InputStream;
import org.tukaani.xz.LZMA2Options;

class LZMA2Decoder extends CoderBase {
    LZMA2Decoder() {
        super(LZMA2Options.class, Number.class);
    }

    @Override
    InputStream decode(
            final String archiveName,
            final InputStream in,
            final long uncompressedLength,
            final com.amaze.filemanager.fileoperations.sevenz.Coder coder,
            final byte[] password,
            final int maxMemoryLimitInKb)
            throws IOException {
        try {
            final int dictionarySize = getDictionarySize(coder);
            final int memoryUsageInKb = LZMA2InputStream.getMemoryUsage(dictionarySize);
            if (memoryUsageInKb > maxMemoryLimitInKb) {
                throw new MemoryLimitException(memoryUsageInKb, maxMemoryLimitInKb);
            }
            return new LZMA2InputStream(in, dictionarySize);
        } catch (final IllegalArgumentException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    Object getOptionsFromCoder(final com.amaze.filemanager.fileoperations.sevenz.Coder coder, final InputStream in) throws IOException {
        return getDictionarySize(coder);
    }

    private int getDictionarySize(final com.amaze.filemanager.fileoperations.sevenz.Coder coder) throws IOException {
        if (coder.properties == null) {
            throw new IOException("Missing LZMA2 properties");
        }
        if (coder.properties.length < 1) {
            throw new IOException("LZMA2 properties too short");
        }
        final int dictionarySizeBits = 0xff & coder.properties[0];
        if ((dictionarySizeBits & (~0x3f)) != 0) {
            throw new IOException("Unsupported LZMA2 property bits");
        }
        if (dictionarySizeBits > 40) {
            throw new IOException("Dictionary larger than 4GiB maximum size");
        }
        if (dictionarySizeBits == 40) {
            return 0xFFFFffff;
        }
        return (2 | (dictionarySizeBits & 0x1)) << (dictionarySizeBits / 2 + 11);
    }

}

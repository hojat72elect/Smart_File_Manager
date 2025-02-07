package com.amaze.filemanager.fileoperations.sevenz;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.MemoryLimitException;
import org.apache.commons.compress.utils.ByteUtils;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.LZMAInputStream;

class LZMADecoder extends CoderBase {
    LZMADecoder() {
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
        if (coder.properties == null) {
            throw new IOException("Missing LZMA properties");
        }
        if (coder.properties.length < 1) {
            throw new IOException("LZMA properties too short");
        }
        final byte propsByte = coder.properties[0];
        final int dictSize = getDictionarySize(coder);
        if (dictSize > LZMAInputStream.DICT_SIZE_MAX) {
            throw new IOException("Dictionary larger than 4GiB maximum size used in " + archiveName);
        }
        final int memoryUsageInKb = LZMAInputStream.getMemoryUsage(dictSize, propsByte);
        if (memoryUsageInKb > maxMemoryLimitInKb) {
            throw new MemoryLimitException(memoryUsageInKb, maxMemoryLimitInKb);
        }
        final LZMAInputStream lzmaIn = new LZMAInputStream(in, uncompressedLength, propsByte, dictSize);
        lzmaIn.enableRelaxedEndCondition();
        return lzmaIn;
    }


    @Override
    Object getOptionsFromCoder(final com.amaze.filemanager.fileoperations.sevenz.Coder coder, final InputStream in) throws IOException {
        if (coder.properties == null) {
            throw new IOException("Missing LZMA properties");
        }
        if (coder.properties.length < 1) {
            throw new IOException("LZMA properties too short");
        }
        final byte propsByte = coder.properties[0];
        int props = propsByte & 0xFF;
        final int pb = props / (9 * 5);
        props -= pb * 9 * 5;
        final int lp = props / 9;
        final int lc = props - lp * 9;
        final LZMA2Options opts = new LZMA2Options();
        opts.setPb(pb);
        opts.setLcLp(lc, lp);
        opts.setDictSize(getDictionarySize(coder));
        return opts;
    }

    private int getDictionarySize(final com.amaze.filemanager.fileoperations.sevenz.Coder coder) throws IllegalArgumentException {
        return (int) ByteUtils.fromLittleEndian(coder.properties, 1, 4);
    }

}

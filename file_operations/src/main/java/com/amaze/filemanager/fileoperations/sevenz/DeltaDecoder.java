package com.amaze.filemanager.fileoperations.sevenz;

import java.io.IOException;
import java.io.InputStream;
import org.tukaani.xz.DeltaOptions;

class DeltaDecoder extends CoderBase {
    DeltaDecoder() {
        super(Number.class);
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
        return new DeltaOptions(getOptionsFromCoder(coder)).getInputStream(in);
    }

    @Override
    Object getOptionsFromCoder(final com.amaze.filemanager.fileoperations.sevenz.Coder coder, final InputStream in) {
        return getOptionsFromCoder(coder);
    }

    private int getOptionsFromCoder(final com.amaze.filemanager.fileoperations.sevenz.Coder coder) {
        if (coder.properties == null || coder.properties.length == 0) {
            return 1;
        }
        return (0xff & coder.properties[0]) + 1;
    }
}

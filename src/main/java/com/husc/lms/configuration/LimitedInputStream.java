package com.husc.lms.configuration;

import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends InputStream {
    private final InputStream wrapped;
    private long remaining;

    public LimitedInputStream(InputStream wrapped, long limit) {
        this.wrapped = wrapped;
        this.remaining = limit;
    }

    @Override
    public int read() throws IOException {
        if (remaining <= 0) return -1;
        int result = wrapped.read();
        if (result != -1) remaining--;
        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (remaining <= 0) return -1;
        len = (int) Math.min(len, remaining);
        int result = wrapped.read(b, off, len);
        if (result != -1) remaining -= result;
        return result;
    }
}


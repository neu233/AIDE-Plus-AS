package com.sdklite.sed;


import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;

public abstract class IOUtil {
    public static void consume(InputStream in) throws IOException {
        do {
            in.skip(Long.MAX_VALUE);
        } while (-1 != in.read());
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static byte[] readFully(InputStream in) throws IOException {
        try {
            return readFullyNoClose(in);
        } finally {
            closeQuietly(in);
        }
    }

    public static String readFully(Reader in) throws IOException {
        try {
            return readFullyNoClose(in);
        } finally {
            closeQuietly(in);
        }
    }

    public static byte[] readFullyNoClose(InputStream in) throws IOException {
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            int nbytes = in.read(buffer);
            if (-1 != nbytes) {
                out.write(buffer, 0, nbytes);
            } else {
                return out.toByteArray();
            }
        }
    }

    public static String readFullyNoClose(Reader in) throws IOException {
        StringWriter out = new StringWriter();
        char[] buffer = new char[4096];
        while (true) {
            int nchars = in.read(buffer);
            if (-1 != nchars) {
                out.write(buffer, 0, nchars);
            } else {
                return out.toString();
            }
        }
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int total = 0;
        while (true) {
            int c = in.read(buffer);
            if (c != -1) {
                total += c;
                out.write(buffer, 0, c);
            } else {
                return total;
            }
        }
    }

    private IOUtil() {
    }
}


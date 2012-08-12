package com.ryaltech.util;

import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class IOUtils {

    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
            }
        }
    }


    public static void copy(InputStream in, OutputStream out) {
        try {
            byte[] buffer = new byte[4096];
            while (true) {
                int count = in.read(buffer);
                if (count < 0) {
                    break;
                } else if (count > 0) {
                    out.write(buffer, 0, count);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copy(Reader reader, Writer writer) {
        try {
            char[] buffer = new char[4096];
            while (true) {
                int count = reader.read(buffer);
                if (count < 0) {
                    break;
                } else if (count > 0) {
                    writer.write(buffer, 0, count);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(File file, String encoding) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return toString(fis, encoding);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            close(fis);
        }
    }

    public static String toString(InputStream in, String encoding) {
        try {
            return toString(new InputStreamReader(in, encoding));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(Reader reader) {
        CharArrayWriter caw = new CharArrayWriter();
        copy(reader, caw);
        return caw.toString();
    }

    public static String toStringUtf8(File file) {
        return toString(file, "UTF-8");
    }

    public static String toStringUtf8(InputStream in) {
        return toString(in, "UTF-8");
    }

}

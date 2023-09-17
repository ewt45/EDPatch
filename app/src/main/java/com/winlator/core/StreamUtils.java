package com.winlator.core;

import org.apache.commons.compress.archivers.ArchiveInputStream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
    public static boolean copy(InputStream tar, OutputStream outStream) {
        try {
            return tar.available()>1000;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

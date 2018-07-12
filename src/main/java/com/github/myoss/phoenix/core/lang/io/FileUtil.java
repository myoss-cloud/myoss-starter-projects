/*
 * Copyright 2018-2018 https://github.com/myoss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.myoss.phoenix.core.lang.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import com.github.myoss.phoenix.core.exception.BizRuntimeException;

/**
 * 操作文件和文件夹工具类
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 上午10:17:26
 */
public class FileUtil {
    /**
     * 缓冲字节大小
     */
    public static final int BUFFER_SIZE = 4096;

    /**
     * 根据 Path 创建一个子文件夹，父级目录必须已经存在
     *
     * @param path path信息
     * @return Path对象
     */
    public static Path createDirectory(Path path) {
        if (!Files.isDirectory(path)) {
            try {
                return Files.createDirectory(path);
            } catch (IOException e) {
                throw new BizRuntimeException("create directory failed [" + path + "]", e);
            }
        }
        return path;
    }

    /**
     * 根据 Path 创建多个子文件夹
     *
     * @param path path信息
     * @return Path对象
     */
    public static Path createDirectories(Path path) {
        if (!Files.isDirectory(path)) {
            try {
                return Files.createDirectories(path);
            } catch (IOException e) {
                throw new BizRuntimeException("create directories failed [" + path + "]", e);
            }
        }
        return path;
    }

    /**
     * Copy the contents of the given InputStream into a new byte array. Leaves
     * the stream open when done.
     *
     * @param in the stream to copy from (may be {@code null} or empty)
     * @return the new byte array that has been copied to (possibly empty)
     */
    public static byte[] copyToByteArray(@Nullable InputStream in) {
        if (in == null) {
            return new byte[0];
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
        copy(in, out);
        return out.toByteArray();
    }

    /**
     * Copy the contents of the given InputStream into a String. Leaves the
     * stream open when done.
     *
     * @param in the InputStream to copy from (may be {@code null} or empty)
     * @param charset the InputStream charset
     * @return the String that has been copied to (possibly empty)
     * @see org.springframework.util.StreamUtils#copyToString(InputStream,
     *      Charset)
     */
    public static String copyToString(InputStream in, Charset charset) {
        if (in == null) {
            return "";
        }

        StringBuilder out = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in, charset);
        char[] buffer = new char[BUFFER_SIZE];
        try {
            int bytesRead = -1;
            while ((bytesRead = reader.read(buffer)) != -1) {
                out.append(buffer, 0, bytesRead);
            }
        } catch (IOException ex) {
            throw new BizRuntimeException(ex);
        }
        return out.toString();
    }

    /**
     * Copy the contents of the given byte array to the given OutputStream.
     * Leaves the stream open when done.
     *
     * @param in the byte array to copy from
     * @param out the OutputStream to copy to
     * @see org.springframework.util.StreamUtils#copy(byte[], OutputStream)
     */
    public static void copy(byte[] in, OutputStream out) {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No OutputStream specified");

        try {
            out.write(in);
        } catch (IOException ex) {
            throw new BizRuntimeException(ex);
        }
    }

    /**
     * Copy the contents of the given String to the given output OutputStream.
     * Leaves the stream open when done.
     *
     * @param in the String to copy from
     * @param charset the Charset
     * @param out the OutputStream to copy to
     * @see org.springframework.util.StreamUtils#copy(String, Charset,
     *      OutputStream)
     */
    public static void copy(String in, Charset charset, OutputStream out) {
        Assert.notNull(in, "No input String specified");
        Assert.notNull(charset, "No charset specified");
        Assert.notNull(out, "No OutputStream specified");

        Writer writer = new OutputStreamWriter(out, charset);
        try {
            writer.write(in);
            writer.flush();
        } catch (IOException ex) {
            throw new BizRuntimeException(ex);
        }

    }

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * Leaves both streams open when done.
     *
     * @param in the InputStream to copy from
     * @param out the OutputStream to copy to
     * @return the number of bytes copied
     * @see org.springframework.util.StreamUtils#copy(InputStream, OutputStream)
     */
    public static int copy(InputStream in, OutputStream out) {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");

        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }

            out.flush();
        } catch (IOException ex) {
            throw new BizRuntimeException(ex);
        }
        return byteCount;
    }
}

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

package app.myoss.cloud.core.lang.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import app.myoss.cloud.core.constants.MyossConstants;
import app.myoss.cloud.core.exception.BizRuntimeException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Stream流工具类
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 上午10:17:26
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamUtil {
    /**
     * 缓冲字节大小
     */
    public static final int BUFFER_SIZE     = 4096;
    /**
     * 压缩缓冲字节大小
     */
    public static final int COMPRESS_BUFFER = 10240;

    /**
     * Copy the contents of the given InputStream into a new byte array. Leaves
     * the stream open when done.
     *
     * @param in the stream to copy from (may be {@code null} or empty)
     * @return the new byte array that has been copied to (possibly empty)
     */
    public static byte[] copyToByteArray(InputStream in) {
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
        Objects.requireNonNull(in, "No input byte array specified");
        Objects.requireNonNull(out, "No OutputStream specified");

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
        Objects.requireNonNull(in, "No input String specified");
        Objects.requireNonNull(charset, "No charset specified");
        Objects.requireNonNull(out, "No OutputStream specified");

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
        Objects.requireNonNull(in, "No InputStream specified");
        Objects.requireNonNull(out, "No OutputStream specified");

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

    /**
     * 数据压缩
     *
     * @param inputStream 待压缩的数据流
     * @param outputStream 压缩后的数据流
     */
    public static void compress(InputStream inputStream, OutputStream outputStream) {
        try {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            int count;
            byte[] data = new byte[COMPRESS_BUFFER];
            while ((count = inputStream.read(data, 0, COMPRESS_BUFFER)) != -1) {
                gzipOutputStream.write(data, 0, count);
            }
            gzipOutputStream.finish();
            gzipOutputStream.flush();
            gzipOutputStream.close();
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
    }

    /**
     * 数据解压缩
     *
     * @param inputStream 待解压的数据流
     * @param outputStream 解压后的数据流
     */
    public static void decompress(InputStream inputStream, OutputStream outputStream) {
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            int count;
            byte[] data = new byte[COMPRESS_BUFFER];
            while ((count = gzipInputStream.read(data, 0, COMPRESS_BUFFER)) != -1) {
                outputStream.write(data, 0, count);
            }
            gzipInputStream.close();
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
    }

    /**
     * 数据压缩
     *
     * @param sourceData 待压缩的原始数据
     * @return 压缩后的数据流
     */
    public static byte[] compress(byte[] sourceData) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(sourceData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            compress(inputStream, outputStream);
            byte[] outputData = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return outputData;
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
    }

    /**
     * 数据解压缩
     *
     * @param sourceData 待解压的数据
     * @return 解压后的数据流
     */
    public static byte[] decompress(byte[] sourceData) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(sourceData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            decompress(inputStream, outputStream);
            byte[] outputData = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return outputData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 数据压缩
     *
     * @param sourceData 待压缩的原始数据
     * @return 压缩后的数据流
     */
    public static byte[] compressString(String sourceData) {
        return compress(sourceData.getBytes(MyossConstants.DEFAULT_CHARSET));
    }

    /**
     * 数据解压缩
     *
     * @param sourceData 待解压的数据
     * @return 解压后的数据流
     */
    public static String decompressString(byte[] sourceData) {
        byte[] decompress = decompress(sourceData);
        return new String(decompress, MyossConstants.DEFAULT_CHARSET);
    }

    /**
     * 使用 Base64 进行数据压缩
     *
     * @param sourceData 待压缩的原始数据
     * @return 压缩后的 Base64 字符串
     */
    public static String compressBase64String(String sourceData) {
        return Base64.getEncoder().encodeToString(compressString(sourceData));
    }

    /**
     * 使用 Base64 进行数据解压缩
     *
     * @param sourceData 待解压的数据
     * @return 解压后的 Base64 字符串
     */
    public static String decompressBase64String(String sourceData) {
        byte[] decode = Base64.getDecoder().decode(sourceData.getBytes(MyossConstants.DEFAULT_CHARSET));
        return decompressString(decode);
    }
}

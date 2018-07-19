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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;

import com.github.myoss.phoenix.core.exception.BizRuntimeException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 操作文件和文件夹工具类
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 上午10:17:26
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {
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
     * 转换 jar 文件路径，用于读取 jar 文件
     *
     * @param jarUrl jar 文件路径
     * @return 转换之后的 jar 文件路径
     */
    public static String toJarFilePath(String jarUrl) {
        jarUrl = (jarUrl.startsWith("jar:") ? jarUrl : String.format("jar:%s", jarUrl));
        return (jarUrl.indexOf("!/") > 0 ? jarUrl.substring(0, jarUrl.indexOf("!/") + 2) : (jarUrl + "!/"));
    }

    /**
     * 读取 jar 文件
     *
     * @param jarUrl jar 文件路径
     * @return jar 文件
     */
    public static JarFile toJarFile(String jarUrl) {
        String jarPath = toJarFilePath(jarUrl);
        try {
            URL url = new URL(jarPath);
            JarURLConnection jar = (JarURLConnection) url.openConnection();
            return jar.getJarFile();
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
    }

    /**
     * 获取 "jar包" 中的某个目录下的所有文件
     *
     * @param jarPath jar 包路径
     * @param directory jar 包中的目录文件名
     * @param excludeChildDirectory 排除 <code>directory</code> 子目录文件
     * @param addEmptyDirectory 是否包含空目录
     * @return jar 包中的某个目录下的所有文件
     */
    public static Map<String, InputStream> getFilesFromJar(String jarPath, String directory,
                                                           boolean excludeChildDirectory, boolean addEmptyDirectory) {
        String jarUrl = toJarFilePath(jarPath);
        JarFile jarFile = toJarFile(jarUrl);
        Enumeration<JarEntry> entries = jarFile.entries();
        Map<String, InputStream> files = new LinkedHashMap<>();
        if (entries == null || !entries.hasMoreElements()) {
            return files;
        }
        Map<String, InputStream> emptyDirectory = null;
        if (addEmptyDirectory) {
            emptyDirectory = new LinkedHashMap<>();
        }
        Path path = Paths.get(jarUrl);
        Path directoryPath = path.resolve(directory);
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (!name.startsWith(directory)) {
                continue;
            }
            if (excludeChildDirectory && !path.resolve(name).getParent().equals(directoryPath)) {
                // 排除子目录的文件
                continue;
            }
            if (entry.isDirectory()) {
                if (addEmptyDirectory) {
                    emptyDirectory.put(StringUtils.removeEnd(name, "/"), null);
                }
            } else {
                InputStream inputStream;
                try {
                    inputStream = jarFile.getInputStream(entry);
                } catch (IOException e) {
                    throw new BizRuntimeException(e);
                }
                files.put(name, inputStream);
            }
        }
        if (addEmptyDirectory) {
            Iterator<Entry<String, InputStream>> iterator = emptyDirectory.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, InputStream> entry = iterator.next();
                String key = entry.getKey();
                for (String item : files.keySet()) {
                    if (item.startsWith(key)) {
                        iterator.remove();
                        break;
                    }
                }
            }
            files.putAll(emptyDirectory);
        }
        return files;
    }

    /**
     * 获取某个目录下的所有文件
     *
     * @param rootDirectory 根目录的路径
     * @param directory 根目录下的文件夹路径
     * @param excludeChildDirectory 排除 <code>templateDirectory</code> 子目录文件
     * @param addEmptyDirectory 是否添加空目录文件夹（true: 添加; false: 不添加）
     * @return 某个目录下的所有文件
     */
    public static Map<String, InputStream> getFilesFromDirectory(String rootDirectory, Path directory,
                                                                 boolean excludeChildDirectory,
                                                                 boolean addEmptyDirectory) {
        try {
            Map<String, InputStream> files = new LinkedHashMap<>();
            Files.list(directory).forEach(path -> {
                if (excludeChildDirectory && !StringUtils.equals(path.getParent().toString(), rootDirectory)) {
                    // 排除子目录的文件
                    return;
                }
                if (Files.isDirectory(path)) {
                    Map<String, InputStream> childFiles = getFilesFromDirectory(rootDirectory, path,
                            excludeChildDirectory, addEmptyDirectory);
                    if (childFiles.isEmpty()) {
                        if (addEmptyDirectory) {
                            files.put(path.toString(), null);
                        }
                    } else {
                        files.putAll(childFiles);
                    }
                } else {
                    try {
                        InputStream inputStream = new FileInputStream(path.toFile());
                        files.put(path.toString(), inputStream);
                    } catch (FileNotFoundException e) {
                        throw new BizRuntimeException("path: " + path, e);
                    }
                }
            });
            return files;
        } catch (IOException e) {
            throw new BizRuntimeException("directory: " + directory, e);
        }
    }

    /**
     * 获取某个目录下的所有文件
     *
     * @param directory 目录的路径
     * @param excludeChildDirectory 排除 <code>templateDirectory</code> 子目录文件
     * @param addEmptyDirectory 是否添加空目录文件夹（true: 添加; false: 不添加）
     * @return 某个目录下的所有文件
     */
    public static Map<String, InputStream> getFilesFromDirectory(String directory, boolean excludeChildDirectory,
                                                                 boolean addEmptyDirectory) {
        return getFilesFromDirectory(directory, Paths.get(directory), excludeChildDirectory, addEmptyDirectory);
    }

    /**
     * 获取某个目录下的所有文件（不包括空目录文件夹）
     *
     * @param directory 目录的路径
     * @return 某个目录下的所有文件
     */
    public static Map<String, InputStream> getFilesFromDirectory(String directory) {
        return getFilesFromDirectory(directory, Paths.get(directory), false, false);
    }
}

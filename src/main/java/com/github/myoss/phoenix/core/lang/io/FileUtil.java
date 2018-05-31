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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.myoss.phoenix.core.exception.BizRuntimeException;

/**
 * 操作文件和文件夹工具类
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 上午10:17:26
 */
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
}

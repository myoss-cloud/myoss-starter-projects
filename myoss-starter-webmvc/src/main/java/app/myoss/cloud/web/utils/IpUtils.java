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

package app.myoss.cloud.web.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import app.myoss.cloud.core.exception.BizRuntimeException;

/**
 * Ip地址工具类
 *
 * @author Jerry.Chen
 * @since 2018年7月15日 下午12:54:38
 */
public class IpUtils {
    /**
     * 未知IP地址
     */
    public static final String UNKNOWN           = "unknown";
    /**
     * 代理服务器客户端 IP 地址设置的 Header key
     */
    public static String[]     PROXY_HEADER_KEYS = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR" };

    /**
     * 获取本机IP地址
     *
     * @return 本机IP地址
     */
    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new BizRuntimeException("Thrown to indicate that the IP address of a host could not be determined",
                    e);
        }
    }

    /**
     * 获取请求客户端的真实地址
     *
     * @param request http request
     * @return 客户端的真实地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = null;
        boolean flag = true;
        for (String key : PROXY_HEADER_KEYS) {
            ipAddress = request.getHeader(key);
            if (StringUtils.isNotBlank(ipAddress) && !UNKNOWN.equalsIgnoreCase(ipAddress)) {
                flag = false;
                break;
            }
        }
        if (flag) {
            ipAddress = request.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                ipAddress = getLocalIp();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP，多个IP按照','分割。**.***.***.***".length() = 15
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 掩码的IP地址转换为明码
     *
     * @param ip 2886796291
     * @return String xxx.xxx.xxx.xxx
     */
    public static String decodeIp(long ip) {
        if (ip <= 0) {
            return "";
        }
        return String.valueOf((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
                + (ip & 0xFF);
    }

    /**
     * 将明码的IP地址转码为数字
     *
     * @param ipString xxx.xxx.xxx.xxx
     * @return long 2886796291
     */
    public static long encodeIp(String ipString) {
        long ipNumber = 0;
        if (StringUtils.isNotBlank(ipString)) {
            String[] ipArray = StringUtils.split(ipString, ".");
            if (ipArray.length == 4) {
                ipNumber = (Long.parseLong(ipArray[0]) << 24) | (Long.parseLong(ipArray[1]) << 16)
                        | (Long.parseLong(ipArray[2]) << 8) | (Long.parseLong(ipArray[3]));
            }
        }
        return ipNumber;
    }
}

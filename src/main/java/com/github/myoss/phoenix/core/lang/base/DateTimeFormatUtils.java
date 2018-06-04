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

package com.github.myoss.phoenix.core.lang.base;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

/**
 * 日期时间格式化工具类
 *
 * @author Jerry.Chen
 * @since 2018年5月23日 上午1:41:48
 */
public class DateTimeFormatUtils {
    public static final String             YYYY_MM_DD_HH_MM_SS    = "yyyy-MM-dd HH:mm:ss";
    public static final String             YYYY_MM_DD             = "yyyy-MM-dd";
    public static final String             YYYYMMDDHHMMSS         = "yyyyMMddHHmmss";
    public static final String             YYYYMMDD               = "yyyyMMdd";
    public static final String             HH_MM_SS               = "HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER_CN = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
    private static final DateTimeFormatter DATE_FORMATTER         = DateTimeFormatter.ofPattern(YYYY_MM_DD);
    private static final DateTimeFormatter DATE_TIME_FORMATTER_EN = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS);
    private static final DateTimeFormatter DATE_FORMATTER_EN      = DateTimeFormatter.ofPattern(YYYYMMDD);
    private static final DateTimeFormatter TIME_FORMATTER         = DateTimeFormatter.ofPattern(HH_MM_SS);

    /**
     * 将 {@link LocalDateTime} 按默认时区转换为 {@link Date}
     *
     * @param localDateTime 表示与时区无关的日期和时间信息，不直接对应时刻，需要通过时区转换
     * @return Java 1.8 以前的日期
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from((localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
    }

    /**
     * 将 {@link LocalDate} 按默认时区转换为 {@link Date}
     *
     * @param localDate 表示与时区无关的日期，只有日期信息，没有时间信息
     * @return Java 1.8 以前的日期
     */
    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将 {@link LocalDateTime} 按默认时区转换为 {@link Calendar}
     *
     * @param localDateTime 表示与时区无关的日期和时间信息，不直接对应时刻，需要通过时区转换
     * @return Java 1.8 以前的日历
     */
    public static Calendar toCalendar(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return toCalendar(zonedDateTime);
    }

    /**
     * 将 {@link LocalDate} 按默认时区转换为 {@link Calendar}
     *
     * @param localDate 表示与时区无关的日期，只有日期信息，没有时间信息
     * @return Java 1.8 以前的日历
     */
    public static Calendar toCalendar(LocalDate localDate) {
        return toCalendar(localDate.atStartOfDay());
    }

    /**
     * 将 {@link ZonedDateTime} 按时区转换为 {@link Calendar}
     *
     * @param zonedDateTime 表示特定时区的日期和时间
     * @return Java 1.8 以前的日历
     */
    public static Calendar toCalendar(ZonedDateTime zonedDateTime) {
        TimeZone timeZone = TimeZone.getTimeZone(zonedDateTime.getZone());
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(zonedDateTime.toInstant().toEpochMilli());
        return calendar;
    }

    /**
     * 将 {@link Date} 按默认时区转换为 {@link LocalDateTime}
     *
     * @param date Java 1.8 以前的日期
     * @return 表示与时区无关的日期和时间信息，不直接对应时刻，需要通过时区转换
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将 {@link Date} 按默认时区转换为 {@link LocalDate}
     *
     * @param date Java 1.8 以前的日期
     * @return 表示与时区无关的日期，只有日期信息，没有时间信息
     */
    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 将 {@link ZonedDateTime} 按时区转换为 {@link Calendar}
     *
     * @param calendar Java 1.8 以前的日历
     * @return 表示特定时区的日期和时间
     */
    public static ZonedDateTime toZonedDateTime(Calendar calendar) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(calendar.getTimeInMillis()), calendar.getTimeZone()
                .toZoneId());
    }

    /**
     * 转换日期为：yyyy-MM-dd HH:mm:ss
     *
     * @param date 待转换的日期
     * @return 格式化后的“yyyy-MM-dd HH:mm:ss”字符串
     */
    public static String print2DateTimeCN(Date date) {
        return date == null ? null : DATE_TIME_FORMATTER_CN.format(date.toInstant().atZone(ZoneId.systemDefault()));
    }

    /**
     * 转换日期为：yyyy-MM-dd HH:mm:ss
     *
     * @param date 待转换的日期
     * @return 格式化后的“yyyy-MM-dd HH:mm:ss”字符串
     */
    public static String print2DateTimeCN(LocalDateTime date) {
        return date == null ? null : DATE_TIME_FORMATTER_CN.format(date);
    }

    /**
     * 将字符串日期：yyyy-MM-dd HH:mm:ss 转换为日期
     *
     * @param date “yyyy-MM-dd HH:mm:ss”字符串
     * @return 日期
     */
    public static Date parse2DateTimeCN(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(date, DATE_TIME_FORMATTER_CN);
        return toDate(localDateTime);
    }

    /**
     * 将字符串日期：yyyy-MM-dd HH:mm:ss 转换为日期
     *
     * @param date “yyyy-MM-dd HH:mm:ss”字符串
     * @return 日期
     */
    public static LocalDateTime parseToDateTimeCN(String date) {
        return StringUtils.isBlank(date) ? null : LocalDateTime.parse(date, DATE_TIME_FORMATTER_CN);
    }

    /**
     * 转换日期为：yyyy-MM-dd
     *
     * @param date 待转换的日期
     * @return 格式化后的“yyyy-MM-dd”字符串
     */
    public static String print2Date(Date date) {
        return date == null ? null : DATE_FORMATTER.format(date.toInstant().atZone(ZoneId.systemDefault()));
    }

    /**
     * 转换日期为：yyyy-MM-dd
     *
     * @param date 待转换的日期
     * @return 格式化后的“yyyy-MM-dd”字符串
     */
    public static String print2Date(LocalDateTime date) {
        return date == null ? null : DATE_FORMATTER.format(date);
    }

    /**
     * 将字符串日期：yyyy-MM-dd 转换为日期
     *
     * @param date “yyyy-MM-dd”字符串
     * @return 日期
     */
    public static Date parse2Date(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        return toDate(localDate);
    }

    /**
     * 将字符串日期：yyyy-MM-dd 转换为日期
     *
     * @param date “yyyy-MM-dd”字符串
     * @return 日期
     */
    public static LocalDate parseToDate(String date) {
        return StringUtils.isBlank(date) ? null : LocalDate.parse(date, DATE_FORMATTER);
    }

    /**
     * 转换日期为：yyyyMMddHHmmss
     *
     * @param date 待转换的日期
     * @return 格式化后的“yyyyMMddHHmmss”字符串
     */
    public static String print2DateTimeEN(Date date) {
        return date == null ? null : DATE_TIME_FORMATTER_EN.format(date.toInstant().atZone(ZoneId.systemDefault()));
    }

    /**
     * 转换日期为：yyyyMMddHHmmss
     *
     * @param date 待转换的日期
     * @return 格式化后的“yyyyMMddHHmmss”字符串
     */
    public static String print2DateTimeEN(LocalDateTime date) {
        return date == null ? null : DATE_TIME_FORMATTER_EN.format(date);
    }

    /**
     * 将字符串日期：yyyyMMddHHmmss 转换为日期
     *
     * @param date “yyyyMMddHHmmss”字符串
     * @return 日期
     */
    public static Date parse2DateTimeEN(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(date, DATE_TIME_FORMATTER_EN);
        return toDate(localDateTime);
    }

    /**
     * 将字符串日期：yyyyMMddHHmmss 转换为日期
     *
     * @param date “yyyyMMddHHmmss”字符串
     * @return 日期
     */
    public static LocalDateTime parseToDateTimeEN(String date) {
        return StringUtils.isBlank(date) ? null : LocalDateTime.parse(date, DATE_TIME_FORMATTER_EN);
    }

    /**
     * 转换日期为：yyyyMMdd
     *
     * @param date 待转换的日期
     * @return 格式化后的“yyyyMMdd”字符串
     */
    public static String print2DateEN(Date date) {
        return date == null ? null : DATE_FORMATTER_EN.format(date.toInstant().atZone(ZoneId.systemDefault()));
    }

    /**
     * 转换日期为：yyyyMMdd
     *
     * @param date 待转换的日期
     * @return 格式化后的“yyyyMMdd”字符串
     */
    public static String print2DateEN(LocalDate date) {
        return date == null ? null : DATE_FORMATTER_EN.format(date);
    }

    /**
     * 转换日期为：yyyyMMdd
     *
     * @param date 待转换的日期
     * @return 格式化后的“yyyyMMdd”字符串
     */
    public static String print2DateEN(LocalDateTime date) {
        return date == null ? null : DATE_FORMATTER_EN.format(date);
    }

    /**
     * 将字符串日期：yyyyMMdd 转换为日期
     *
     * @param date “yyyyMMdd”字符串
     * @return 日期
     */
    public static Date parse2DateEN(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        LocalDate localDateTime = LocalDate.parse(date, DATE_FORMATTER_EN);
        return toDate(localDateTime);
    }

    /**
     * 将字符串日期：yyyyMMdd 转换为日期
     *
     * @param date “yyyyMMdd”字符串
     * @return 日期
     */
    public static LocalDateTime parseToDateEN(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        return LocalDate.parse(date, DATE_FORMATTER_EN).atStartOfDay();
    }

    /**
     * 转换日期为：HH:mm:ss
     *
     * @param date 待转换的日期
     * @return 格式化后的“HH:mm:ss”字符串
     */
    public static String print2Time(Date date) {
        return date == null ? null : TIME_FORMATTER.format(date.toInstant().atZone(ZoneId.systemDefault()));
    }

    /**
     * 转换日期为：HH:mm:ss
     *
     * @param date 待转换的日期
     * @return 格式化后的“HH:mm:ss”字符串
     */
    public static String print2Time(LocalDateTime date) {
        return date == null ? null : TIME_FORMATTER.format(date);
    }

    /**
     * 校验时间区间是否有重叠，比较"源的时间区间"是否在"目标的时间区间"内
     *
     * @param srcEffectiveTime 源，生效时间
     * @param srcExpiryTime 源，过期时间
     * @param targetEffectiveTime 目标，生效时间
     * @param targetExpiryTime 目标，过去时间
     * @return true: 有重叠, false: 没有重叠
     */
    public static boolean checkDateTimeOverlap(Date srcEffectiveTime, Date srcExpiryTime, Date targetEffectiveTime,
                                               Date targetExpiryTime) {
        return !(srcEffectiveTime.after(targetExpiryTime) || srcExpiryTime.before(targetEffectiveTime));
    }

    /**
     * 校验时间区间是否有重叠，比较"源的时间区间"是否在"目标的时间区间"内
     *
     * @param srcEffectiveTime 源，生效时间
     * @param srcExpiryTime 源，过期时间
     * @param targetEffectiveTime 目标，生效时间
     * @param targetExpiryTime 目标，过去时间
     * @return true: 有重叠, false: 没有重叠
     */
    public static boolean checkDateTimeOverlap(LocalDateTime srcEffectiveTime, LocalDateTime srcExpiryTime,
                                               LocalDateTime targetEffectiveTime, LocalDateTime targetExpiryTime) {
        return !(srcEffectiveTime.isAfter(targetExpiryTime) || srcExpiryTime.isBefore(targetEffectiveTime));
    }

    /**
     * 获取日期当天的 00:00:00。 Example:
     *
     * <pre>
     * '2017-03-23 16:20:47' -&gt; '2017-03-23 00:00:00.000'
     * </pre>
     *
     * @param date 日期参数
     * @return 日期当天的 00:00:00.000
     */
    public static Date withTimeAtStartOfDay(Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
        return toDate(localDateTime);
    }

    /**
     * 获取日期当天的 00:00:00。 Example:
     *
     * <pre>
     * '2017-03-23 16:20:47' -&gt; '2017-03-23 00:00:00.000'
     * </pre>
     *
     * @param date 日期参数
     * @return 日期当天的 00:00:00.000
     */
    public static Date withTimeAtStartOfDay(LocalDateTime date) {
        Instant instant = date.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * 获取日期当天的 23:59:59.999 Example:
     *
     * <pre>
     * '2017-03-23 16:20:47' -&gt; '2017-03-23 23:59:59.999'
     * </pre>
     *
     * @param date 日期参数
     * @return 日期当天的 23:59:59.999
     */
    public static Date withTimeAtEndOfDay(Date date) {
        Instant instant = date.toInstant().atZone(ZoneId.systemDefault()).with(LocalTime.MAX).toInstant();
        return Date.from(instant);
    }

    /**
     * 获取日期当天的 23:59:59.999 Example:
     *
     * <pre>
     * '2017-03-23 16:20:47' -&gt; '2017-03-23 23:59:59.999'
     * </pre>
     *
     * @param date 日期参数
     * @return 日期当天的 23:59:59.999
     */
    public static Date withTimeAtEndOfDay(LocalDateTime date) {
        Instant instant = date.atZone(ZoneId.systemDefault()).with(LocalTime.MAX).toInstant();
        return Date.from(instant);
    }

    /**
     * 获取日期当天的 23:59:59.000 Example:
     *
     * <pre>
     * '2017-03-23 16:20:47' -&gt; '2017-03-23 23:59:59.000'
     * </pre>
     *
     * @param date 日期参数
     * @return 日期当天的 23:59:59.000
     */
    public static Date withTimeAtEndOfDayAndRemoveNano(LocalDateTime date) {
        Instant instant = date.atZone(ZoneId.systemDefault()).with(LocalTime.MAX).withNano(0).toInstant();
        return Date.from(instant);
    }

    /**
     * 获取日期当天的 23:59:59.000 Example:
     *
     * <pre>
     * '2017-03-23 16:20:47' -&gt; '2017-03-23 23:59:59.000'
     * </pre>
     *
     * @param date 日期参数
     * @return 日期当天的 23:59:59.000
     */
    public static Date withTimeAtEndOfDayAndRemoveNano(Date date) {
        Instant instant = date.toInstant().atZone(ZoneId.systemDefault()).with(LocalTime.MAX).withNano(0).toInstant();
        return Date.from(instant);
    }

    /**
     * 比较两个日期是否相同，会比较时间部分
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return true: 相同; false: 不相同
     */
    public static boolean isSameDay(Date date1, Date date2) {
        return date1 == null && date2 == null || !(date1 == null || date2 == null) && date1.equals(date2);
    }

    /**
     * 比较两个日期是否相同，会比较时间部分
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return true: 相同; false: 不相同
     */
    public static boolean isSameDay(LocalDateTime date1, LocalDateTime date2) {
        return date1 == null && date2 == null || !(date1 == null || date2 == null) && date1.isEqual(date2);
    }
}

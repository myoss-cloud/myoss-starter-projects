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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link DateTimeFormatUtils} 测试类
 *
 * @author Jerry.Chen
 * @since 2018年5月23日 上午1:41:48
 */
public class DateTimeFormatUtilsTests {

    @Test
    public void autoConvertTest1() {
        LocalDateTime now = LocalDateTime.now();
        Date date1 = DateTimeFormatUtils.toDate(now);
        LocalDateTime localDateTime = DateTimeFormatUtils.toLocalDateTime(date1);
        Assert.assertEquals(now, localDateTime);
        Assert.assertEquals(date1, DateTimeFormatUtils.toDate(localDateTime));

        LocalDate localDate = now.toLocalDate();
        Assert.assertEquals(localDate, DateTimeFormatUtils.toLocalDate(date1));

        ZonedDateTime zonedDateTime = now.atZone(ZoneId.systemDefault());
        Calendar calendar = DateTimeFormatUtils.toCalendar(zonedDateTime);
        Assert.assertEquals(zonedDateTime, DateTimeFormatUtils.toZonedDateTime(calendar));
    }

    /**
     * 夏令/冬令的日期测试
     */
    @Test
    public void parseDaylightSavingTimeTest1() {
        Date date1 = DateTimeFormatUtils.parse2Date("1987-04-12");
        Date date2 = DateTimeFormatUtils.parse2DateEN("19870412");
        Date date3 = DateTimeFormatUtils.parse2DateTimeCN("1987-04-12 00:00:00");
        Date date4 = DateTimeFormatUtils.parse2DateTimeEN("19870412000000");
        Assert.assertEquals(date1, date2);
        Assert.assertEquals(date1, date3);
        Assert.assertEquals(date1, date4);
    }

    @Test
    public void print2DateTimeCNTest1() throws IllegalAccessException {
        DateTimeFormatter dateTimeFormatterCn = (DateTimeFormatter) FieldUtils.getField(DateTimeFormatUtils.class,
                "DATE_TIME_FORMATTER_CN", true).get(DateTimeFormatUtils.class);
        LocalDateTime dateTime = LocalDateTime.of(2016, 8, 8, 8, 8, 8, 8);
        Assert.assertEquals("2016-08-08 08:08:08", dateTimeFormatterCn.format(dateTime));
    }

    @Test
    public void print2DateTimeCNTest2() {
        Date null1 = null;
        LocalDateTime null2 = null;
        Assert.assertNull(DateTimeFormatUtils.print2DateTimeCN(null1));
        Assert.assertNull(DateTimeFormatUtils.print2DateTimeCN(null2));

        LocalDateTime dateTime = LocalDateTime.of(2016, 8, 8, 8, 8, 8, 8);
        Date date = Date.from(dateTime.atZone(ZoneOffset.systemDefault()).toInstant());
        Assert.assertEquals("2016-08-08 08:08:08", DateTimeFormatUtils.print2DateTimeCN(date));
        Assert.assertEquals("2016-08-08 08:08:08", DateTimeFormatUtils.print2DateTimeCN(dateTime));
    }

    @Test
    public void print2TimeTest1() {
        Date null1 = null;
        LocalDateTime null2 = null;
        Assert.assertNull(DateTimeFormatUtils.print2Time(null1));
        Assert.assertNull(DateTimeFormatUtils.print2Time(null2));

        LocalDateTime dateTime = LocalDateTime.of(2016, 8, 8, 8, 8, 8, 8);
        Date date = Date.from(dateTime.atZone(ZoneOffset.systemDefault()).toInstant());
        Assert.assertEquals("08:08:08", DateTimeFormatUtils.print2Time(date));
        Assert.assertEquals("08:08:08", DateTimeFormatUtils.print2Time(dateTime));
    }

    @Test
    public void print2DateTest1() {
        Date null1 = null;
        LocalDateTime null2 = null;
        Assert.assertNull(DateTimeFormatUtils.print2Date(null1));
        Assert.assertNull(DateTimeFormatUtils.print2Date(null2));

        LocalDateTime dateTime = LocalDateTime.of(2016, 8, 8, 0, 0, 0, 0);
        Date date = DateTimeFormatUtils.toDate(dateTime);
        Assert.assertEquals("2016-08-08", DateTimeFormatUtils.print2Date(date));
        Assert.assertEquals("2016-08-08", DateTimeFormatUtils.print2Date(dateTime));
    }

    @Test
    public void parse2DateTest1() {
        Assert.assertNull(DateTimeFormatUtils.parse2DateTimeCN(null));
        Assert.assertNull(DateTimeFormatUtils.parse2Date(null));
        Assert.assertNull(DateTimeFormatUtils.parseToDateTimeCN(null));
        Assert.assertNull(DateTimeFormatUtils.parseToDate(null));

        LocalDateTime dateTime = LocalDateTime.of(2016, 8, 8, 0, 0, 0, 0);
        Date date = DateTimeFormatUtils.toDate(dateTime);
        Assert.assertEquals(date, DateTimeFormatUtils.parse2Date("2016-08-08"));
        Assert.assertEquals(dateTime.toLocalDate(), DateTimeFormatUtils.parseToDate("2016-08-08"));
    }

    @Test
    public void print2DateENTest1() {
        Date null1 = null;
        LocalDateTime null2 = null;
        Assert.assertNull(DateTimeFormatUtils.print2DateEN(null1));
        Assert.assertNull(DateTimeFormatUtils.print2DateEN(null2));

        LocalDateTime dateTime = LocalDateTime.of(2016, 8, 8, 0, 0, 0, 0);
        Date date = DateTimeFormatUtils.toDate(dateTime);
        Assert.assertEquals("20160808", DateTimeFormatUtils.print2DateEN(date));
        Assert.assertEquals("20160808", DateTimeFormatUtils.print2DateEN(dateTime));
    }

    @Test
    public void parse2DateENTest1() {
        Assert.assertNull(DateTimeFormatUtils.parse2DateEN(null));
        Assert.assertNull(DateTimeFormatUtils.parseToDateEN(null));

        LocalDateTime dateTime = LocalDateTime.of(2016, 8, 8, 0, 0, 0, 0);
        Date date = DateTimeFormatUtils.toDate(dateTime);
        Assert.assertEquals(date, DateTimeFormatUtils.parse2DateEN("20160808"));
        Assert.assertEquals(dateTime, DateTimeFormatUtils.parseToDateEN("20160808"));
    }

    @Test
    public void print2DateTimeENTest1() {
        Date null1 = null;
        LocalDateTime null2 = null;
        Assert.assertNull(DateTimeFormatUtils.print2DateTimeEN(null1));
        Assert.assertNull(DateTimeFormatUtils.print2DateTimeEN(null2));

        LocalDateTime dateTime = LocalDateTime.of(2016, 8, 8, 10, 20, 30, 40);
        Date date = DateTimeFormatUtils.toDate(dateTime);
        Assert.assertEquals("20160808102030", DateTimeFormatUtils.print2DateTimeEN(date));
        Assert.assertEquals("20160808102030", DateTimeFormatUtils.print2DateTimeEN(dateTime));
    }

    @Test
    public void parse2DateTimeENTest1() {
        Assert.assertNull(DateTimeFormatUtils.parse2DateTimeEN(null));
        Assert.assertNull(DateTimeFormatUtils.parseToDateTimeEN(null));

        LocalDateTime dateTime = LocalDateTime.of(2016, 8, 8, 10, 20, 30, 0);
        Date date = DateTimeFormatUtils.toDate(dateTime);
        Assert.assertEquals(date, DateTimeFormatUtils.parse2DateTimeEN("20160808102030"));
        Assert.assertEquals(dateTime, DateTimeFormatUtils.parseToDateTimeEN("20160808102030"));
    }

    @Test
    public void checkDateTimeOverlapTest1() {
        Date srcEffectiveTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-05 12:00:00");
        Date srcExpiryTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-15 12:00:00");

        Date targetEffectiveTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-06 12:00:00");
        Date targetExpiryTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-08 12:00:00");
        Assert.assertTrue(DateTimeFormatUtils.checkDateTimeOverlap(srcEffectiveTime, srcExpiryTime,
                targetEffectiveTime, targetExpiryTime));
        Assert.assertTrue(DateTimeFormatUtils.checkDateTimeOverlap(
                DateTimeFormatUtils.toLocalDateTime(srcEffectiveTime),
                DateTimeFormatUtils.toLocalDateTime(srcExpiryTime),
                DateTimeFormatUtils.toLocalDateTime(targetEffectiveTime),
                DateTimeFormatUtils.toLocalDateTime(targetExpiryTime)));
    }

    @Test
    public void checkDateTimeOverlapTest2() {
        Date srcEffectiveTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-05 12:00:00");
        Date srcExpiryTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-15 12:00:00");

        Date targetEffectiveTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-01 12:00:00");
        Date targetExpiryTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-05 12:00:00");
        Assert.assertTrue(DateTimeFormatUtils.checkDateTimeOverlap(srcEffectiveTime, srcExpiryTime,
                targetEffectiveTime, targetExpiryTime));
        Assert.assertTrue(DateTimeFormatUtils.checkDateTimeOverlap(
                DateTimeFormatUtils.toLocalDateTime(srcEffectiveTime),
                DateTimeFormatUtils.toLocalDateTime(srcExpiryTime),
                DateTimeFormatUtils.toLocalDateTime(targetEffectiveTime),
                DateTimeFormatUtils.toLocalDateTime(targetExpiryTime)));
    }

    @Test
    public void checkDateTimeOverlapTest3() {
        Date srcEffectiveTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-05 12:00:00");
        Date srcExpiryTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-15 12:00:00");

        Date targetEffectiveTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-01 12:00:00");
        Date targetExpiryTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-05 11:59:59");
        Assert.assertFalse(DateTimeFormatUtils.checkDateTimeOverlap(srcEffectiveTime, srcExpiryTime,
                targetEffectiveTime, targetExpiryTime));
        Assert.assertFalse(DateTimeFormatUtils.checkDateTimeOverlap(
                DateTimeFormatUtils.toLocalDateTime(srcEffectiveTime),
                DateTimeFormatUtils.toLocalDateTime(srcExpiryTime),
                DateTimeFormatUtils.toLocalDateTime(targetEffectiveTime),
                DateTimeFormatUtils.toLocalDateTime(targetExpiryTime)));
    }

    @Test
    public void checkDateTimeOverlapTest4() {
        Date srcEffectiveTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-05 12:00:00");
        Date srcExpiryTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-10 12:00:00");

        Date targetEffectiveTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-10 12:00:00");
        Date targetExpiryTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-15 11:59:59");
        Assert.assertTrue(DateTimeFormatUtils.checkDateTimeOverlap(srcEffectiveTime, srcExpiryTime,
                targetEffectiveTime, targetExpiryTime));
        Assert.assertTrue(DateTimeFormatUtils.checkDateTimeOverlap(
                DateTimeFormatUtils.toLocalDateTime(srcEffectiveTime),
                DateTimeFormatUtils.toLocalDateTime(srcExpiryTime),
                DateTimeFormatUtils.toLocalDateTime(targetEffectiveTime),
                DateTimeFormatUtils.toLocalDateTime(targetExpiryTime)));
    }

    @Test
    public void checkDateTimeOverlapTest5() {
        Date srcEffectiveTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-05 12:00:00");
        Date srcExpiryTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-10 11:59:59");

        Date targetEffectiveTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-10 12:00:00");
        Date targetExpiryTime = DateTimeFormatUtils.parse2DateTimeCN("2017-03-15 11:59:59");
        Assert.assertFalse(DateTimeFormatUtils.checkDateTimeOverlap(srcEffectiveTime, srcExpiryTime,
                targetEffectiveTime, targetExpiryTime));
        Assert.assertFalse(DateTimeFormatUtils.checkDateTimeOverlap(
                DateTimeFormatUtils.toLocalDateTime(srcEffectiveTime),
                DateTimeFormatUtils.toLocalDateTime(srcExpiryTime),
                DateTimeFormatUtils.toLocalDateTime(targetEffectiveTime),
                DateTimeFormatUtils.toLocalDateTime(targetExpiryTime)));
    }

    @Test
    public void withTimeAtStartOfDayTest1() {
        LocalDateTime dateTime = LocalDateTime.of(2017, 3, 23, 16, 20, 47);
        LocalDateTime excepted = LocalDateTime.of(2017, 3, 23, 0, 0, 0);

        Date date = DateTimeFormatUtils.toDate(dateTime);
        Date exceptedDate = DateTimeFormatUtils.toDate(excepted);

        Assert.assertEquals(exceptedDate, DateTimeFormatUtils.withTimeAtStartOfDay(date));
        Assert.assertEquals(exceptedDate, DateTimeFormatUtils.withTimeAtStartOfDay(dateTime));
        Assert.assertEquals("2017-03-23 16:20:47", DateTimeFormatUtils.print2DateTimeCN(date));
        Assert.assertEquals("2017-03-23 00:00:00", DateTimeFormatUtils.print2DateTimeCN(exceptedDate));
    }

    @Test
    public void withTimeAtEndOfDayTest1() {
        LocalDateTime dateTime = LocalDateTime.of(2017, 3, 23, 16, 20, 47);
        LocalDateTime excepted = LocalDateTime.of(2017, 3, 23, 23, 59, 59, 999999999);

        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date exceptedDte = Date.from(excepted.atZone(ZoneId.systemDefault()).toInstant());

        Assert.assertEquals(exceptedDte, DateTimeFormatUtils.withTimeAtEndOfDay(date));
        Assert.assertEquals(exceptedDte, DateTimeFormatUtils.withTimeAtEndOfDay(dateTime));
        Assert.assertEquals("2017-03-23 16:20:47", DateTimeFormatUtils.print2DateTimeCN(date));
        Assert.assertEquals("2017-03-23 23:59:59", DateTimeFormatUtils.print2DateTimeCN(exceptedDte));
    }

    @Test
    public void isSameDayTest1() {
        Date null1 = null;
        Date null2 = null;
        Assert.assertTrue(DateTimeFormatUtils.isSameDay(null1, null2));

        LocalDateTime dateTime1 = LocalDateTime.of(2017, 3, 23, 16, 20, 47);
        LocalDateTime dateTime2 = LocalDateTime.of(2017, 3, 23, 16, 20, 47);

        Date date1 = Date.from(dateTime1.atZone(ZoneId.systemDefault()).toInstant());
        Date date2 = Date.from(dateTime2.atZone(ZoneId.systemDefault()).toInstant());

        Assert.assertTrue(DateTimeFormatUtils.isSameDay(date1, date2));
    }

    @Test
    public void isSameDayTest2() {
        Date null1 = null;
        Date null2 = null;
        Assert.assertTrue(DateTimeFormatUtils.isSameDay(null1, null2));

        LocalDateTime dateTime1 = LocalDateTime.of(2017, 3, 23, 16, 20, 48);
        LocalDateTime dateTime2 = LocalDateTime.of(2017, 3, 23, 16, 20, 47);

        Date date1 = Date.from(dateTime1.atZone(ZoneId.systemDefault()).toInstant());
        Date date2 = Date.from(dateTime2.atZone(ZoneId.systemDefault()).toInstant());

        Assert.assertFalse(DateTimeFormatUtils.isSameDay(date1, date2));
    }

    @Test
    public void isSameDayTest3() {
        LocalDateTime null1 = null;
        LocalDateTime null2 = null;
        Assert.assertTrue(DateTimeFormatUtils.isSameDay(null1, null2));

        LocalDateTime dateTime1 = LocalDateTime.of(2017, 3, 23, 16, 20, 47);
        LocalDateTime dateTime2 = LocalDateTime.of(2017, 3, 23, 16, 20, 47);

        Assert.assertTrue(DateTimeFormatUtils.isSameDay(dateTime1, dateTime2));
    }

    @Test
    public void isSameDayTest4() {
        LocalDateTime null1 = null;
        LocalDateTime null2 = null;
        Assert.assertTrue(DateTimeFormatUtils.isSameDay(null1, null2));

        LocalDateTime dateTime1 = LocalDateTime.of(2017, 3, 23, 16, 20, 48);
        LocalDateTime dateTime2 = LocalDateTime.of(2017, 3, 23, 16, 20, 47);

        Assert.assertFalse(DateTimeFormatUtils.isSameDay(dateTime1, dateTime2));
    }
}

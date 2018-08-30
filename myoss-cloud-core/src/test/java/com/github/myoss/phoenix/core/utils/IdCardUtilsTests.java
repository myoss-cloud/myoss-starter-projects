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

package com.github.myoss.phoenix.core.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.myoss.phoenix.core.exception.BizRuntimeException;

/**
 * {@link IdCardUtils} 测试类
 *
 * @author Jerry.Chen
 * @since 2018年6月1日 上午2:32:26
 */
public class IdCardUtilsTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructorIsPrivate()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<IdCardUtils> constructor = IdCardUtils.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * 18位身份证测试1
     */
    @Test
    public void idCard18Test1() {
        String idCard = "110102198904169416";
        boolean isIdCard18 = IdCardUtils.validateIdCard18(idCard);
        Assert.assertTrue(isIdCard18);
        Assert.assertTrue(IdCardUtils.validateCard(idCard));
    }

    /**
     * 15位身份证测试1
     */
    @Test
    public void idCard15Test1() {
        String idCard = "370206340306481";
        boolean isIdCard15 = IdCardUtils.validateIdCard15(idCard);
        Assert.assertTrue(isIdCard15);
        Assert.assertTrue(IdCardUtils.validateCard(idCard));

        String actual = IdCardUtils.convertCard15To18(idCard);
        Assert.assertEquals("370206193403064815", actual);
    }

    /**
     * 15位身份证测试2
     */
    @Test
    public void idCard15Test2() {
        String idCard = "332621680413871";
        boolean isIdCard15 = IdCardUtils.validateIdCard15(idCard);
        Assert.assertTrue(isIdCard15);

        String actual = IdCardUtils.convertCard15To18(idCard);
        Assert.assertEquals("332621196804138719", actual);
    }

    @Test
    public void twIdCard15Test1() {
        String idCard = "I244759323";
        boolean isIdCard10 = IdCardUtils.validateCard(idCard);
        Assert.assertTrue(isIdCard10);

        IdCardUtils.validateTWCard(idCard);
        Assert.assertTrue(IdCardUtils.validateTWCard(idCard));

        String[] array = IdCardUtils.validateIdCard10(idCard);
        assertThat(array).contains("台湾", "F", "true");
    }

    @Test
    public void twIdCard15Test2() {
        String idCard = "Q137462190";
        boolean isIdCard10 = IdCardUtils.validateCard(idCard);
        Assert.assertTrue(isIdCard10);

        Assert.assertTrue(IdCardUtils.validateTWCard(idCard));

        String[] array = IdCardUtils.validateIdCard10(idCard);
        assertThat(array).contains("台湾", "M", "true");
    }

    @Test
    public void hkIdCard15Test1() {
        String idCard = "Z2595320";
        boolean isIdCard10 = IdCardUtils.validateCard(idCard);
        Assert.assertTrue(isIdCard10);

        Assert.assertTrue(IdCardUtils.validateHKCard(idCard));

        String[] array = IdCardUtils.validateIdCard10(idCard);
        assertThat(array).contains("香港", "N", "true");
    }

    @Test
    public void macaoIdCard15Test1() {
        String idCard = "1234567A";
        boolean isIdCard10 = IdCardUtils.validateCard(idCard);
        Assert.assertFalse(isIdCard10);

        String[] array = IdCardUtils.validateIdCard10(idCard);
        assertThat(array).contains("澳门", "N", "false");
    }

    @Test
    public void macaoIdCard15Test2() {
        String idCard = "10295838";
        boolean isIdCard10 = IdCardUtils.validateCard(idCard);
        Assert.assertTrue(isIdCard10);

        String[] array = IdCardUtils.validateIdCard10(idCard);
        assertThat(array).contains("澳门", "N", "true");
    }

    private void getBirthDateTest(String idCard) {
        Assert.assertTrue(IdCardUtils.validateCard(idCard));
        Date birthDate = IdCardUtils.getBirthDateByIdCard(idCard);

        String birthStr = IdCardUtils.getBirthByIdCard(idCard);
        Assert.assertNotNull(birthStr);
        Date parseDate;
        try {
            parseDate = DateUtils.parseDate(birthStr, "yyyyMMdd");
        } catch (ParseException e) {
            throw new BizRuntimeException(e);
        }
        Assert.assertEquals(birthDate, parseDate);

        String year = birthStr.substring(0, 4);
        String month = birthStr.substring(4, 6);
        String day = birthStr.substring(6, 8);
        LocalDate localDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Assert.assertEquals(birthDate, date);
    }

    /**
     * 夏令日期测试1
     */
    @Test
    public void summertimeTest1() {
        getBirthDateTest("110102198804105378");
    }

    /**
     * 夏令日期测试2
     */
    @Test
    public void summertimeTest2() {
        getBirthDateTest("110102198904164973");
    }

    @Test
    public void ciyCodeTest1() {
        Map<String, String> cityCode = IdCardUtils.getCityCode();
        thrown.expect(UnsupportedOperationException.class);
        cityCode.put("key", "value");
    }

    @Test
    public void twFirstCodeTest1() {
        Map<String, Integer> twFirstCode = IdCardUtils.getTwFirstCode();
        thrown.expect(UnsupportedOperationException.class);
        twFirstCode.put("key", 1);
    }

    @Test
    public void hkFirstCodeTest1() {
        Map<String, Integer> hkFirstCode = IdCardUtils.getHkFirstCode();
        thrown.expect(UnsupportedOperationException.class);
        hkFirstCode.put("key", 1);
    }
}

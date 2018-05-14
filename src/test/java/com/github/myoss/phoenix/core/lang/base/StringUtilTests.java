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

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link StringUtil} 测试类
 *
 * @author Jerry.Chen 2018年5月14日 上午11:50:10
 */
public class StringUtilTests {
    @Test
    public void toCamelCaseTest1() {
        String userName = StringUtil.toCamelCase("user_name");
        Assert.assertEquals("userName", userName);
    }

    @Test
    public void toCamelCaseTest2() {
        String userName = StringUtil.toCamelCase("USER_NAME");
        Assert.assertEquals("userName", userName);
    }

    @Test
    public void toCamelCaseTest3() {
        String userName = StringUtil.toCamelCase("_user_name_");
        Assert.assertEquals("userName", userName);
    }

    @Test
    public void toCamelCaseTest4() {
        String userName = StringUtil.toCamelCase("_USER_NAME_");
        Assert.assertEquals("userName", userName);
    }

    @Test
    public void toCamelCaseTest5() {
        String userName = StringUtil.toCamelCase("_USER-NAME_");
        Assert.assertEquals("userName", userName);
    }

    @Test
    public void toCamelCaseTest6() {
        String userName = StringUtil.toCamelCase("userName");
        Assert.assertEquals("userName", userName);
    }

    @Test
    public void toCamelCaseTest7() {
        String userName = StringUtil.toCamelCase("UserName");
        Assert.assertEquals("userName", userName);
    }

    @Test
    public void toPascalCaseTest1() {
        String userName = StringUtil.toPascalCase("user_name");
        Assert.assertEquals("UserName", userName);
    }

    @Test
    public void toPascalCaseTest2() {
        String userName = StringUtil.toPascalCase("USER_NAME");
        Assert.assertEquals("UserName", userName);
    }

    @Test
    public void toPascalCaseTest3() {
        String userName = StringUtil.toPascalCase("_user_name_");
        Assert.assertEquals("UserName", userName);
    }

    @Test
    public void toPascalCaseTest4() {
        String userName = StringUtil.toPascalCase("_USER_NAME_");
        Assert.assertEquals("UserName", userName);
    }

    @Test
    public void toPascalCaseTest5() {
        String userName = StringUtil.toPascalCase("_USER-NAME_");
        Assert.assertEquals("UserName", userName);
    }

    @Test
    public void toPascalCaseTest6() {
        String userName = StringUtil.toPascalCase("userName");
        Assert.assertEquals("UserName", userName);
    }

    @Test
    public void toPascalCaseTest7() {
        String userName = StringUtil.toPascalCase("UserName");
        Assert.assertEquals("UserName", userName);
    }
}

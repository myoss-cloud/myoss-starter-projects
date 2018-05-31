package com.github.myoss.phoenix.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * {@link NameStyle} 测试类
 *
 * @author Jerry.Chen
 * @since 2018年5月2日 上午9:53:36
 */
public class NameStyleTests {
    @Test
    public void originTest1() {
        String case1 = NameStyle.ORIGIN.transform("snake_case");
        assertEquals("snake_case", case1);

        String case2 = NameStyle.ORIGIN.transform("snake_case_");
        assertEquals("snake_case_", case2);

        String case3 = NameStyle.ORIGIN.transform("snakeCase_");
        assertEquals("snakeCase_", case3);

        String case4 = NameStyle.ORIGIN.transform("snakeCase");
        assertEquals("snakeCase", case4);

        String case5 = NameStyle.ORIGIN.transform("SnakeCase");
        assertEquals("SnakeCase", case5);

        String case6 = NameStyle.ORIGIN.transform("snakecase");
        assertEquals("snakecase", case6);

        String case7 = NameStyle.ORIGIN.transform("_snakeCase");
        assertEquals("_snakeCase", case7);

        String case8 = NameStyle.ORIGIN.transform("_SnakeCase");
        assertEquals("_SnakeCase", case8);

        String case9 = NameStyle.ORIGIN.transform("_snakecase");
        assertEquals("_snakecase", case9);
    }

    @Test
    public void snakeCaseTest1() {
        String case1 = NameStyle.SNAKE_CASE.transform("snake_case");
        assertEquals("snake_case", case1);

        String case2 = NameStyle.SNAKE_CASE.transform("snake_case_");
        assertEquals("snake_case_", case2);

        String case3 = NameStyle.SNAKE_CASE.transform("snakeCase_");
        assertEquals("snake_case_", case3);

        String case4 = NameStyle.SNAKE_CASE.transform("snakeCase");
        assertEquals("snake_case", case4);

        String case5 = NameStyle.SNAKE_CASE.transform("SnakeCase");
        assertEquals("snake_case", case5);

        String case6 = NameStyle.SNAKE_CASE.transform("snakecase");
        assertEquals("snakecase", case6);

        String case7 = NameStyle.SNAKE_CASE.transform("_snakeCase");
        assertEquals("_snake_case", case7);

        String case8 = NameStyle.SNAKE_CASE.transform("_SnakeCase");
        assertEquals("_snake_case", case8);

        String case9 = NameStyle.SNAKE_CASE.transform("_snakecase");
        assertEquals("_snakecase", case9);
    }

    @Test
    public void screamingSnakeCaseTest1() {
        String case1 = NameStyle.SCREAMING_SNAKE_CASE.transform("snake_case");
        assertEquals("SNAKE_CASE", case1);

        String case2 = NameStyle.SCREAMING_SNAKE_CASE.transform("snake_case_");
        assertEquals("SNAKE_CASE_", case2);

        String case3 = NameStyle.SCREAMING_SNAKE_CASE.transform("snakeCase_");
        assertEquals("SNAKE_CASE_", case3);

        String case4 = NameStyle.SCREAMING_SNAKE_CASE.transform("snakeCase");
        assertEquals("SNAKE_CASE", case4);

        String case5 = NameStyle.SCREAMING_SNAKE_CASE.transform("SnakeCase");
        assertEquals("SNAKE_CASE", case5);

        String case6 = NameStyle.SCREAMING_SNAKE_CASE.transform("snakecase");
        assertEquals("SNAKECASE", case6);

        String case7 = NameStyle.SCREAMING_SNAKE_CASE.transform("_snakeCase");
        assertEquals("_SNAKE_CASE", case7);

        String case8 = NameStyle.SCREAMING_SNAKE_CASE.transform("_SnakeCase");
        assertEquals("_SNAKE_CASE", case8);

        String case9 = NameStyle.SCREAMING_SNAKE_CASE.transform("_snakecase");
        assertEquals("_SNAKECASE", case9);
    }

    @Test
    public void upperCaseTest1() {
        String case1 = NameStyle.UPPER_CASE.transform("snake_case");
        assertEquals("SNAKE_CASE", case1);

        String case2 = NameStyle.UPPER_CASE.transform("snake_case_");
        assertEquals("SNAKE_CASE_", case2);

        String case3 = NameStyle.UPPER_CASE.transform("snakeCase_");
        assertEquals("SNAKECASE_", case3);

        String case4 = NameStyle.UPPER_CASE.transform("snakeCase");
        assertEquals("SNAKECASE", case4);

        String case5 = NameStyle.UPPER_CASE.transform("SnakeCase");
        assertEquals("SNAKECASE", case5);

        String case6 = NameStyle.UPPER_CASE.transform("snakecase");
        assertEquals("SNAKECASE", case6);

        String case7 = NameStyle.UPPER_CASE.transform("_snakeCase");
        assertEquals("_SNAKECASE", case7);

        String case8 = NameStyle.UPPER_CASE.transform("_SnakeCase");
        assertEquals("_SNAKECASE", case8);

        String case9 = NameStyle.UPPER_CASE.transform("_snakecase");
        assertEquals("_SNAKECASE", case9);
    }

    @Test
    public void lowerCaseTest1() {
        String case1 = NameStyle.LOWER_CASE.transform("snake_case");
        assertEquals("snake_case", case1);

        String case2 = NameStyle.LOWER_CASE.transform("snake_case_");
        assertEquals("snake_case_", case2);

        String case3 = NameStyle.LOWER_CASE.transform("snakeCase_");
        assertEquals("snakecase_", case3);

        String case4 = NameStyle.LOWER_CASE.transform("snakeCase");
        assertEquals("snakecase", case4);

        String case5 = NameStyle.LOWER_CASE.transform("SnakeCase");
        assertEquals("snakecase", case5);

        String case6 = NameStyle.LOWER_CASE.transform("snakecase");
        assertEquals("snakecase", case6);

        String case7 = NameStyle.LOWER_CASE.transform("_snakeCase");
        assertEquals("_snakecase", case7);

        String case8 = NameStyle.LOWER_CASE.transform("_SnakeCase");
        assertEquals("_snakecase", case8);

        String case9 = NameStyle.LOWER_CASE.transform("_snakecase");
        assertEquals("_snakecase", case9);
    }

    @Test
    public void pascalCaseTest1() {
        String case1 = NameStyle.PASCAL_CASE.transform("snake_case");
        assertEquals("SnakeCase", case1);

        String case2 = NameStyle.PASCAL_CASE.transform("snake_case_");
        assertEquals("SnakeCase_", case2);

        String case3 = NameStyle.PASCAL_CASE.transform("snakeCase_");
        assertEquals("SnakeCase_", case3);

        String case4 = NameStyle.PASCAL_CASE.transform("snakeCase");
        assertEquals("SnakeCase", case4);

        String case5 = NameStyle.PASCAL_CASE.transform("SnakeCase");
        assertEquals("SnakeCase", case5);

        String case6 = NameStyle.PASCAL_CASE.transform("snakecase");
        assertEquals("Snakecase", case6);

        String case7 = NameStyle.CAMEL_CASE.transform("_snakeCase");
        assertEquals("_SnakeCase", case7);

        String case8 = NameStyle.CAMEL_CASE.transform("_SnakeCase");
        assertEquals("_SnakeCase", case8);

        String case9 = NameStyle.CAMEL_CASE.transform("_snakecase");
        assertEquals("_Snakecase", case9);
    }

    @Test
    public void camelCaseTest1() {
        String case1 = NameStyle.CAMEL_CASE.transform("snake_case");
        assertEquals("snakeCase", case1);

        String case2 = NameStyle.CAMEL_CASE.transform("snake_case_");
        assertEquals("snakeCase_", case2);

        String case3 = NameStyle.CAMEL_CASE.transform("snakeCase_");
        assertEquals("snakeCase_", case3);

        String case4 = NameStyle.CAMEL_CASE.transform("snakeCase");
        assertEquals("snakeCase", case4);

        String case5 = NameStyle.CAMEL_CASE.transform("SnakeCase");
        assertEquals("snakeCase", case5);

        String case6 = NameStyle.CAMEL_CASE.transform("snakecase");
        assertEquals("snakecase", case6);

        String case7 = NameStyle.CAMEL_CASE.transform("_snakeCase");
        assertEquals("_SnakeCase", case7);

        String case8 = NameStyle.CAMEL_CASE.transform("_SnakeCase");
        assertEquals("_SnakeCase", case8);

        String case9 = NameStyle.CAMEL_CASE.transform("_snakecase");
        assertEquals("_Snakecase", case9);
    }
}

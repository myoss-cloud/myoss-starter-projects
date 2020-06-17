/*
 * Copyright 2018-2020 https://github.com/myoss
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

package app.myoss.cloud.core.lang.base;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import app.myoss.cloud.core.exception.BizRuntimeException;
import app.myoss.cloud.core.lang.json.JsonApi;
import app.myoss.cloud.core.lang.json.JsonArray;
import app.myoss.cloud.core.lang.json.JsonObject;

/**
 * Java类型转换器工具类
 *
 * @author Jerry.Chen
 * @since 2020年6月5日 上午11:37:47
 */
public final class JavaTypeUtils {
    private static final Class<?>[] PRIMITIVE_TYPES = { int.class, long.class, short.class, float.class, double.class,
            byte.class, boolean.class, char.class, Integer.class, Long.class, Short.class, Float.class, Double.class,
            Byte.class, Boolean.class, Character.class };

    /**
     * Check whether this primitive contains a Primitive or String value.
     *
     * @param value source value
     * @return true if this primitive contains a Primitive or String value,
     *         false otherwise.
     */
    public static boolean isPrimitiveOrString(Object value) {
        if (value instanceof String) {
            return true;
        }

        Class<?> classOfPrimitive = value.getClass();
        for (Class<?> standardPrimitive : PRIMITIVE_TYPES) {
            if (standardPrimitive.isAssignableFrom(classOfPrimitive)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether this primitive contains a String value.
     *
     * @param value source value
     * @return true if this primitive contains a String value, false otherwise.
     */
    public static boolean isString(Object value) {
        return value instanceof String;
    }

    /**
     * convert this value to a String.
     *
     * @param value source value
     * @return convert this value to a String.
     */
    public static String toString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    /**
     * Check whether this primitive contains a boolean value.
     *
     * @param value source value
     * @return true if this primitive contains a boolean value, false otherwise.
     */
    public static boolean isBoolean(Object value) {
        return value instanceof Boolean;
    }

    /**
     * convert this value as a {@link Boolean}.
     *
     * @param value source value
     * @return convert this value as a {@link Boolean}.
     */
    public static Boolean toBooleanWrapper(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof BigDecimal) {
            return toInt((BigDecimal) value) == 1;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }

        String s = toString(value);
        if (s.length() == 0 || "null".equals(s) || "NULL".equals(s)) {
            return null;
        }
        if ("true".equalsIgnoreCase(s) || "1".equals(s)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(s) || "0".equals(s)) {
            return Boolean.FALSE;
        }
        if ("Y".equalsIgnoreCase(s) || "T".equals(s)) {
            return Boolean.TRUE;
        }
        if ("F".equalsIgnoreCase(s) || "N".equals(s)) {
            return Boolean.FALSE;
        }
        return Boolean.parseBoolean(s);
    }

    /**
     * convert this value as a primitive boolean value.
     *
     * @param value source value
     * @return convert this value as a primitive boolean value.
     */
    public static boolean toBoolean(Object value) {
        if (isBoolean(value)) {
            return (Boolean) value;
        } else {
            return toBooleanWrapper(value);
        }
    }

    /**
     * Check whether this primitive contains a Number.
     *
     * @param value source value
     * @return true if this primitive contains a Number, false otherwise.
     */
    public static boolean isNumber(Object value) {
        return value instanceof Number;
    }

    /**
     * convert this value as a Number.
     *
     * @param value source value
     * @return convert this value a Number.
     * @throws NumberFormatException if the value contained is not a valid
     *             Number.
     */
    public static Number toNumber(Object value) {
        return value instanceof String ? new ParsedNumber((String) value) : (Number) value;
    }

    /**
     * convert this value as a primitive double.
     *
     * @param value source value
     * @return convert this value as a primitive double.
     * @throws NumberFormatException if the value contained is not a valid
     *             double.
     */
    public static Double toDouble(Object value) {
        return isNumber(value) ? ((Number) value).doubleValue() : Double.parseDouble(toString(value));
    }

    /**
     * convert this value as a {@link BigDecimal}.
     *
     * @param value source value
     * @return convert this value as a {@link BigDecimal}.
     * @throws NumberFormatException if the value contained is not a valid
     *             {@link BigDecimal}.
     */
    public static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
    }

    /**
     * convert this value as a {@link BigInteger}.
     *
     * @param value source value
     * @return convert this value as a {@link BigInteger}.
     * @throws NumberFormatException if the value contained is not a valid
     *             {@link BigInteger}.
     */
    public static BigInteger toBigInteger(Object value) {
        if (value == null) {
            return null;
        }
        return value instanceof BigInteger ? (BigInteger) value : new BigInteger(value.toString());
    }

    /**
     * convert this value as a Float.
     *
     * @param value source value
     * @return convert this value as a Float.
     * @throws NumberFormatException if the value contained is not a valid
     *             Float.
     */
    public static Float toFloat(Object value) {
        return isNumber(value) ? ((Number) value).floatValue() : Float.parseFloat(toString(value));
    }

    /**
     * convert this value as a primitive Long.
     *
     * @param value source value
     * @return convert this value as a primitive Long.
     * @throws NumberFormatException if the value contained is not a valid Long.
     */
    public static Long toLong(Object value) {
        return isNumber(value) ? ((Number) value).longValue() : Long.parseLong(toString(value));
    }

    /**
     * convert this value as a primitive Short.
     *
     * @param value source value
     * @return convert this value as a primitive Short.
     * @throws NumberFormatException if the value contained is not a valid Short
     *             value.
     */
    public static Short toShort(Object value) {
        return isNumber(value) ? ((Number) value).shortValue() : Short.parseShort(toString(value));
    }

    /**
     * convert this value as a primitive integer.
     *
     * @param value source value
     * @return convert this value as a primitive integer.
     * @throws NumberFormatException if the value contained is not a valid
     *             integer.
     */
    public static Integer toInt(Object value) {
        return isNumber(value) ? ((Number) value).intValue() : Integer.parseInt(toString(value));
    }

    /**
     * convert this value as a primitive byte.
     *
     * @param value source value
     * @return convert this value as a primitive byte.
     * @throws NumberFormatException if the value contained is not a valid byte.
     */
    public static Byte toByte(Object value) {
        return isNumber(value) ? ((Number) value).byteValue() : Byte.parseByte(toString(value));
    }

    /**
     * convert this value as a byte[].
     *
     * @param value source value
     * @return convert this value as a byte[].
     * @throws NumberFormatException if the value contained is not a valid
     *             byte[].
     */
    public static byte[] toBytes(Object value) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        if (value instanceof String) {
            return Base64.getDecoder().decode((String) value);
        }
        throw new BizRuntimeException("can not convert to byte[], value : " + value);
    }

    /**
     * convert this value as a primitive char.
     *
     * @param value source value
     * @return convert this value as a primitive char.
     * @throws IndexOutOfBoundsException if the {@code index} argument is
     *             negative or not less than the length of this string.
     */
    public static char toCharacter(Object value) {
        return toString(Objects.requireNonNull(value)).charAt(0);
    }

    public static byte toByte(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }
        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100) {
            return decimal.byteValue();
        }
        return decimal.byteValueExact();
    }

    public static short toShort(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }
        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100) {
            return decimal.shortValue();
        }
        return decimal.shortValueExact();
    }

    public static int toInt(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }
        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100) {
            return decimal.intValue();
        }
        return decimal.intValueExact();
    }

    public static long toLong(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }
        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100) {
            return decimal.longValue();
        }
        return decimal.longValueExact();
    }

    /**
     * Convenience method to get the specified value as a List.
     *
     * @param value source value.
     * @return the List corresponding to the specified value.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof List) {
            return (List<T>) value;
        }
        if (value instanceof Collection) {
            return new ArrayList<>((Collection<T>) value);
        }
        return JsonApi.fromJson(value.toString(), List.class);
    }

    /**
     * Convenience method to get the specified value as a JsonArray.
     *
     * @param value source value.
     * @return the JsonArray corresponding to the specified value.
     */
    @SuppressWarnings("unchecked")
    public static JsonArray toJsonArray(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof JsonArray) {
            return (JsonArray) value;
        }
        if (value instanceof Collection) {
            return new JsonArray((Collection<Object>) value);
        }
        return JsonApi.fromJson(value.toString(), JsonArray.class);
    }

    /**
     * Convenience method to get the specified value as a JsonObject.
     *
     * @param value source value.
     * @return the JsonObject corresponding to the specified value.
     */
    @SuppressWarnings("unchecked")
    public static JsonObject toJsonObject(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof JsonObject) {
            return (JsonObject) value;
        }
        if (value instanceof Map) {
            return new JsonObject((Map<String, Object>) value);
        }
        return JsonApi.fromJson(value.toString());
    }
}

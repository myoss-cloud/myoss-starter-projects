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

package app.myoss.cloud.core.lang.json;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import app.myoss.cloud.core.lang.base.JavaTypeUtils;

/**
 * A class representing an object type in Json.
 *
 * @author Jerry.Chen
 * @since 2020年6月5日 上午10:12:05
 */
public final class JsonObject implements Map<String, Object>, Serializable {
    private static final long         serialVersionUID = -143079352260106094L;
    private final Map<String, Object> members;

    /**
     * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance
     * with the default initial capacity (16) and load factor (0.75).
     */
    public JsonObject() {
        this(16, true);
    }

    /**
     * Constructs an <tt>Map</tt> instance with the same mappings as the
     * specified map.
     *
     * @param src the map whose mappings are to be placed in this map
     * @throws NullPointerException if the specified map is null
     */
    public JsonObject(Map<String, Object> src) {
        this.members = Objects.requireNonNull(src);
    }

    /**
     * Constructs an empty <tt>Map</tt> instance with the default initial
     * capacity (16) and load factor (0.75).
     *
     * @param ordered true: create <tt>LinkedHashMap</tt>; false: create
     *            <tt>HashMap</tt>
     */
    public JsonObject(boolean ordered) {
        this(16, ordered);
    }

    /**
     * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance
     * with the specified initial capacity, and a default load factor (0.75).
     *
     * @param initialCapacity the initial capacity
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    public JsonObject(int initialCapacity) {
        this(initialCapacity, true);
    }

    /**
     * Constructs an empty <tt>Map</tt> with the default initial capacity (16)
     * and load factor (0.75).
     *
     * @param initialCapacity the initial capacity
     * @param ordered true: create <tt>LinkedHashMap</tt>; false: create
     *            <tt>HashMap</tt>
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    public JsonObject(int initialCapacity, boolean ordered) {
        if (ordered) {
            this.members = new LinkedHashMap<>(initialCapacity);
        } else {
            this.members = new HashMap<>(initialCapacity);
        }
    }

    @Override
    public int size() {
        return this.members.size();
    }

    @Override
    public boolean isEmpty() {
        return this.members.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.members.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.members.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return this.members.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return this.members.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return this.members.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> src) {
        this.members.putAll(src);
    }

    @Override
    public void clear() {
        this.members.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.members.keySet();
    }

    @Override
    public Collection<Object> values() {
        return this.members.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return this.members.entrySet();
    }

    @Override
    public String toString() {
        return toJson();
    }

    @Override
    public Object clone() {
        return new JsonObject(members instanceof LinkedHashMap ? new LinkedHashMap<>(members) : new HashMap<>(members));
    }

    @Override
    public boolean equals(Object obj) {
        return this.members.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.members.hashCode();
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @return JSON字符串
     */
    public String toJson() {
        return JsonApi.toJson(this.members);
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param writer 将JSON字符串写入到 {@link Appendable} 中
     */
    public void toJson(Appendable writer) {
        JsonApi.toJson(this.members, writer);
    }

    /**
     * Convenience method to get the specified key as a List.
     *
     * @param key name of the element key.
     * @return the List corresponding to the specified key.
     */
    public <T> List<T> getAsList(String key) {
        Object value = get(key);
        return JavaTypeUtils.toList(value);
    }

    /**
     * Convenience method to get the specified key as a JsonArray.
     *
     * @param key name of the element key.
     * @return the JsonArray corresponding to the specified key.
     */
    public JsonArray getAsJsonArray(String key) {
        Object value = get(key);
        return JavaTypeUtils.toJsonArray(value);
    }

    /**
     * Convenience method to get the specified key as a JsonObject.
     *
     * @param key name of the element key.
     * @return the JsonObject corresponding to the specified key.
     */
    public JsonObject getAsJsonObject(String key) {
        Object value = get(key);
        return JavaTypeUtils.toJsonObject(value);
    }

    /**
     * convenience method to get this element as a {@link Boolean}.
     *
     * @param key name of the element key.
     * @return get this element as a {@link Boolean}.
     */
    Boolean getAsBooleanWrapper(String key) {
        Object value = get(key);
        return (Boolean) value;
    }

    /**
     * convenience method to get this element as a boolean value.
     *
     * @param key name of the element key.
     * @return get this element as a primitive boolean value.
     */
    public boolean getAsBoolean(String key) {
        Object value = get(key);
        return JavaTypeUtils.toBoolean(value);
    }

    /**
     * convenience method to get this element as a Number.
     *
     * @param key name of the element key.
     * @return get this element as a Number.
     * @throws NumberFormatException if the value contained is not a valid
     *             Number.
     */
    public Number getAsNumber(String key) {
        Object value = get(key);
        return JavaTypeUtils.toNumber(value);
    }

    /**
     * convenience method to get this element as a String.
     *
     * @param key name of the element key.
     * @return get this element as a String.
     */
    public String getAsString(String key) {
        Object value = get(key);
        return JavaTypeUtils.toString(value);
    }

    /**
     * convenience method to get this element as a primitive double.
     *
     * @param key name of the element key.
     * @return get this element as a primitive double.
     * @throws NumberFormatException if the value contained is not a valid
     *             double.
     */
    public Double getAsDouble(String key) {
        Object value = get(key);
        return JavaTypeUtils.toDouble(value);
    }

    /**
     * convenience method to get this element as a {@link BigDecimal}.
     *
     * @param key name of the element key.
     * @return get this element as a {@link BigDecimal}.
     * @throws NumberFormatException if the value contained is not a valid
     *             {@link BigDecimal}.
     */
    public BigDecimal getAsBigDecimal(String key) {
        Object value = get(key);
        return JavaTypeUtils.toBigDecimal(value);
    }

    /**
     * convenience method to get this element as a {@link BigInteger}.
     *
     * @param key name of the element key.
     * @return get this element as a {@link BigInteger}.
     * @throws NumberFormatException if the value contained is not a valid
     *             {@link BigInteger}.
     */
    public BigInteger getAsBigInteger(String key) {
        Object value = get(key);
        return JavaTypeUtils.toBigInteger(value);
    }

    /**
     * convenience method to get this element as a Float.
     *
     * @param key name of the element key.
     * @return get this element as a Float.
     * @throws NumberFormatException if the value contained is not a valid
     *             Float.
     */
    public Float getAsFloat(String key) {
        Object value = get(key);
        return JavaTypeUtils.toFloat(value);
    }

    /**
     * convenience method to get this element as a primitive Long.
     *
     * @param key name of the element key.
     * @return get this element as a primitive Long.
     * @throws NumberFormatException if the value contained is not a valid Long.
     */
    public Long getAsLong(String key) {
        Object value = get(key);
        return JavaTypeUtils.toLong(value);
    }

    /**
     * convenience method to get this element as a primitive Short.
     *
     * @param key name of the element key.
     * @return get this element as a primitive Short.
     * @throws NumberFormatException if the value contained is not a valid Short
     *             value.
     */
    public Short getAsShort(String key) {
        Object value = get(key);
        return JavaTypeUtils.toShort(value);
    }

    /**
     * convenience method to get this element as a primitive integer.
     *
     * @param key name of the element key.
     * @return get this element as a primitive integer.
     * @throws NumberFormatException if the value contained is not a valid
     *             integer.
     */
    public Integer getAsInt(String key) {
        Object value = get(key);
        return JavaTypeUtils.toInt(value);
    }

    /**
     * convenience method to get this element as a primitive byte.
     *
     * @param key name of the element key.
     * @return get this element as a primitive byte.
     * @throws NumberFormatException if the value contained is not a valid byte.
     */
    public Byte getAsByte(String key) {
        Object value = get(key);
        return JavaTypeUtils.toByte(value);
    }

    /**
     * convenience method to get this element as a primitive char.
     *
     * @param key name of the element key.
     * @return get this element as a primitive char.
     * @throws IndexOutOfBoundsException if the {@code index} argument is
     *             negative or not less than the length of this string.
     */
    public char getAsCharacter(String key) {
        Object value = get(key);
        return JavaTypeUtils.toCharacter(value);
    }

}

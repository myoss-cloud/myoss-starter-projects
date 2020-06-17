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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.RandomAccess;

import app.myoss.cloud.core.lang.base.JavaTypeUtils;

/**
 * A class representing an array type in Json.
 *
 * @author Jerry.Chen
 * @since 2020年6月5日 下午2:22:35
 */
public final class JsonArray implements RandomAccess, List<Object>, Serializable, Cloneable {
    private static final long  serialVersionUID = -6529430618590052689L;
    private final List<Object> elements;

    /**
     * Creates an empty JsonArray.
     */
    public JsonArray() {
        this.elements = new ArrayList<>();
    }

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity is
     *             negative
     */
    public JsonArray(int initialCapacity) {
        this.elements = new ArrayList<>(initialCapacity);
    }

    /**
     * Constructs a list containing the elements of the specified collection, in
     * the order they are returned by the collection's iterator.
     *
     * @param src the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public JsonArray(Collection<Object> src) {
        this.elements = new ArrayList<>(Objects.requireNonNull(src));
    }

    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.elements.contains(o);
    }

    @Override
    public Iterator<Object> iterator() {
        return this.elements.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.elements.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.elements.toArray(a);
    }

    @Override
    public boolean add(Object o) {
        return this.elements.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return this.elements.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.elements.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<?> c) {
        return this.elements.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        return this.elements.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.elements.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.elements.retainAll(c);
    }

    @Override
    public void clear() {
        this.elements.clear();
    }

    @Override
    public Object get(int index) {
        return this.elements.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        return this.elements.set(index, element);
    }

    @Override
    public void add(int index, Object element) {
        this.elements.add(index, element);
    }

    @Override
    public Object remove(int index) {
        return this.elements.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.elements.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.elements.lastIndexOf(o);
    }

    @Override
    public ListIterator<Object> listIterator() {
        return this.elements.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return this.elements.listIterator(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return this.elements.subList(fromIndex, toIndex);
    }

    @Override
    public Object clone() {
        return new JsonArray(new ArrayList<>(this.elements));
    }

    @Override
    public boolean equals(Object obj) {
        return this.elements.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.elements.hashCode();
    }

    /**
     * Convenience method to get the specified index as a JsonObject.
     *
     * @param index index of the member
     * @return the JsonObject corresponding to the specified index.
     */
    public JsonObject getAsJsonObject(int index) {
        Object value = get(index);
        return JavaTypeUtils.toJsonObject(value);
    }

    /**
     * Convenience method to get the specified index as a JsonArray.
     *
     * @param index index of the member
     * @return the JsonArray corresponding to the specified index.
     */
    public JsonArray getAsJsonArray(int index) {
        Object value = get(index);
        return JavaTypeUtils.toJsonArray(value);
    }
}

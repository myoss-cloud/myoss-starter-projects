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

package app.myoss.cloud.core.lang.dto;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import app.myoss.cloud.core.lang.dto.Sort.ModelValueDeserializer;

/**
 * Sort option for queries. You have to provide at least a list of properties to
 * sort for that must not include {@literal null} or empty strings. The
 * direction defaults to {@link Sort#DEFAULT_DIRECTION}.
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 下午3:27:19
 */
@JSONType(deserializer = ModelValueDeserializer.class)
public class Sort implements Iterable<Order>, Serializable {
    /**
     * Default direction
     */
    public static final Direction DEFAULT_DIRECTION = Direction.ASC;

    private static final long     serialVersionUID  = -6185297542401976741L;
    @JSONField(serialize = false, deserialize = false)
    private List<Order>           orders;

    /**
     * Creates a new {@link Sort} instance using the given {@link Order}s.
     *
     * @param orders must not be {@literal null}.
     */
    public Sort(Order... orders) {
        this(Arrays.asList(orders));
    }

    /**
     * Creates a new {@link Sort} instance.
     *
     * @param orders must not be {@literal null} or contain {@literal null}.
     */
    public Sort(List<Order> orders) {
        if (null == orders || orders.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one sort property to sort by!");
        }
        this.orders = orders;
    }

    /**
     * Creates a new {@link Sort} instance. Order defaults to
     * {@link Direction#ASC}.
     *
     * @param properties must not be {@literal null} or contain {@literal null}
     *            or empty strings
     */
    public Sort(String... properties) {
        this(DEFAULT_DIRECTION, properties);
    }

    /**
     * Creates a new {@link Sort} instance.
     *
     * @param direction defaults to {@link Sort#DEFAULT_DIRECTION} (for
     *            {@literal null} cases, too)
     * @param properties must not be {@literal null}, empty or contain
     *            {@literal null} or empty strings.
     */
    public Sort(Direction direction, String... properties) {
        this(direction, (properties != null ? Arrays.asList(properties) : new ArrayList<>()));
    }

    /**
     * Creates a new {@link Sort} instance.
     *
     * @param direction defaults to {@link Sort#DEFAULT_DIRECTION} (for
     *            {@literal null} cases, too)
     * @param properties must not be {@literal null} or contain {@literal null}
     *            or empty strings.
     */
    public Sort(Direction direction, List<String> properties) {
        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one property to sort by!");
        }

        this.orders = new ArrayList<>(properties.size());
        for (String property : properties) {
            this.orders.add(new Order(direction, property));
        }
    }

    /**
     * Returns a new {@link Sort} consisting of the {@link Order}s of the
     * current {@link Sort} combined with the given ones.
     *
     * @param sort can be {@literal null}.
     * @return new {@link Sort}
     */
    public Sort and(Sort sort) {
        if (sort == null) {
            return this;
        }

        ArrayList<Order> these = new ArrayList<>(this.orders);
        for (Order order : sort) {
            these.add(order);
        }
        return new Sort(these);
    }

    /**
     * Returns the order registered for the given property.
     *
     * @param property given property
     * @return the order registered
     */
    public Order getOrderFor(String property) {
        for (Order order : this) {
            if (order.getProperty().equals(property)) {
                return order;
            }
        }
        return null;
    }

    /**
     * Returns all orders
     *
     * @return all orders
     */
    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public Iterator<Order> iterator() {
        return this.orders.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Sort)) {
            return false;
        }

        Sort that = (Sort) obj;
        return this.orders.equals(that.orders);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + orders.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(orders);
    }

    /**
     * {@link Sort} 反序列化解析器
     */
    public static class ModelValueDeserializer implements ObjectDeserializer {
        @SuppressWarnings("unchecked")
        @Override
        public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            JSONArray jsonArray = (JSONArray) parser.parse();
            if (jsonArray == null) {
                return null;
            }
            List<Order> orders = new ArrayList<>(jsonArray.size());
            for (Object o : jsonArray) {
                JSONObject item = (JSONObject) o;
                String property = (String) item.get("property");
                String direction = (String) item.get("direction");
                Order order = new Order(Direction.fromStringOrNull(direction), property);
                orders.add(order);
            }
            return (T) new Sort(orders);
        }

        @Override
        public int getFastMatchToken() {
            return 0;
        }
    }
}

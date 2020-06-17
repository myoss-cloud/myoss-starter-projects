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

/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * {@link com.google.gson.internal.LazilyParsedNumber}
 */

package app.myoss.cloud.core.lang.base;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * This class holds a number value that is lazily converted to a specific number
 * type
 *
 * @author Inderjeet Singh
 * @author Jerry.Chen
 * @since 2020年6月5日 上午11:22:41
 */
public final class ParsedNumber extends Number {
    private static final long serialVersionUID = 6655538761473723478L;
    private final String      value;

    /**
     * Create ParsedNumber
     *
     * @param value must not be null
     */
    public ParsedNumber(String value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            try {
                return (int) Long.parseLong(value);
            } catch (NumberFormatException nfe) {
                return new BigDecimal(value).intValue();
            }
        }
    }

    @Override
    public long longValue() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return new BigDecimal(value).longValue();
        }
    }

    @Override
    public float floatValue() {
        return Float.parseFloat(value);
    }

    @Override
    public double doubleValue() {
        return Double.parseDouble(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ParsedNumber) {
            ParsedNumber other = (ParsedNumber) obj;
            return Objects.equals(value, other.value);
        }
        return false;
    }
}

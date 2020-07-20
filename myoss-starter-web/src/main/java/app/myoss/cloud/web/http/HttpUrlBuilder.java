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

package app.myoss.cloud.web.http;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.LinkedMultiValueMap;

import app.myoss.cloud.core.constants.MyossConstants;
import okio.Buffer;

/**
 * HttpUrl 构造工具类
 *
 * @author Jerry.Chen
 * @since 2018年12月26日 下午4:52:51
 * @see okhttp3.HttpUrl
 */
public class HttpUrlBuilder {
    private static final char[] HEX_DIGITS                   = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F' };
    static final String         QUERY_ENCODE_SET             = " \"'<>#";
    static final String         QUERY_COMPONENT_REENCODE_SET = " \"'<>#&=";
    static final String         QUERY_COMPONENT_ENCODE_SET   = " !\"#$&'(),/:;<=>?@[]\\^`{|}~";

    private List<String>        encodedQueryNamesAndValues;

    /**
     * Set the query of this URL, encoded for use in HTTP resource resolution.
     *
     * @param encodedQuery encoded query string
     * @return the corresponding {@code HttpUrlBuilder}
     */
    public HttpUrlBuilder encodedQuery(String encodedQuery) {
        this.encodedQueryNamesAndValues = (encodedQuery != null
                ? queryStringToNamesAndValues(canonicalize(encodedQuery, QUERY_ENCODE_SET, true, false, true, true))
                : null);
        return this;
    }

    /**
     * Returns the query of this URL, encoded for use in HTTP resource
     * resolution. The returned string may be null (for URLs with no query),
     * empty (for URLs with an empty query) or non-empty (all other URLs).
     * <p>
     * <em></em>
     * </p>
     * <table summary="">
     * <tr>
     * <th>URL</th>
     * <th>{@code encodedQuery()}</th>
     * </tr>
     * <tr>
     * <td>{@code http://host/}</td>
     * <td>null</td>
     * </tr>
     * <tr>
     * <td>{@code http://host/?}</td>
     * <td>{@code ""}</td>
     * </tr>
     * <tr>
     * <td>{@code http://host/?a=apple&k=key+lime}</td>
     * <td>{@code
     *       "a=apple&k=key+lime"}</td>
     * </tr>
     * <tr>
     * <td>{@code http://host/?a=apple&a=apricot}</td>
     * <td>{@code "a=apple&a=apricot"}</td>
     * </tr>
     * <tr>
     * <td>{@code http://host/?a=apple&b}</td>
     * <td>{@code "a=apple&b"}</td>
     * </tr>
     * </table>
     *
     * @return encoded query string
     */
    public String encodedQuery() {
        if (encodedQueryNamesAndValues == null) {
            // No query.
            return null;
        }
        StringBuilder result = new StringBuilder();
        namesAndValuesToQueryString(result, encodedQueryNamesAndValues);
        return result.toString();
    }

    /**
     * Returns this URL's query, like {@code "abc"} for
     * {@code http://host/?abc}.
     * <p>
     * <em></em>
     * </p>
     * <table summary="">
     * <tr>
     * <th>URL</th>
     * <th>{@code query()}</th>
     * </tr>
     * <tr>
     * <td>{@code http://host/}</td>
     * <td>null</td>
     * </tr>
     * <tr>
     * <td>{@code http://host/?}</td>
     * <td>{@code ""}</td>
     * </tr>
     * <tr>
     * <td>{@code http://host/?a=apple&k=key+lime}</td>
     * <td>{@code "a=apple&k=key
     *       lime"}</td>
     * </tr>
     * <tr>
     * <td>{@code http://host/?a=apple&a=apricot}</td>
     * <td>{@code "a=apple&a=apricot"}</td>
     * </tr>
     * <tr>
     * <td>{@code http://host/?a=apple&b}</td>
     * <td>{@code "a=apple&b"}</td>
     * </tr>
     * </table>
     *
     * @return this URL's query
     */
    public String query() {
        if (encodedQueryNamesAndValues == null) {
            // No query.
            return null;
        }
        StringBuilder result = new StringBuilder();
        List<String> queryParameterNameAndValues = percentDecode(encodedQueryNamesAndValues, true);
        namesAndValuesToQueryString(result, queryParameterNameAndValues);
        return result.toString();
    }

    /**
     * Return the map of query parameters. {@code null} if no query has been
     * set.
     *
     * @return the map of query parameters
     */
    public LinkedMultiValueMap<String, String> getQueryNamesAndValues() {
        return (this.encodedQueryNamesAndValues != null ? percentDecode2(this.encodedQueryNamesAndValues, true) : null);
    }

    /**
     * check the map of query parameters contains a query parameter name
     *
     * @param name query parameter name
     * @return true or false
     */
    public boolean containsQueryParameter(String name) {
        String encodedName = canonicalize(name, QUERY_COMPONENT_ENCODE_SET, false, false, true, true);
        return containsQueryParameters(encodedName);
    }

    /**
     * remove a query parameter name and value from query parameters
     *
     * @param name query parameter name
     * @param value query parameter value
     * @return the corresponding {@code HttpUrlBuilder}
     */
    public HttpUrlBuilder removeQueryParameter(String name, String value) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if (encodedQueryNamesAndValues == null) {
            return this;
        }
        String encodedName = canonicalize(name, QUERY_COMPONENT_ENCODE_SET, false, false, true, true);
        String encodedValue = (value != null ? canonicalize(value, QUERY_COMPONENT_ENCODE_SET, false, false, true, true)
                : null);
        removeAllCanonicalQueryParameters(encodedName, encodedValue);
        return this;
    }

    /**
     * Encodes the query parameter using UTF-8 and adds it to this URL's query
     * string.
     *
     * @param name query parameter name
     * @param value query parameter value
     * @return the corresponding {@code HttpUrlBuilder}
     */
    public HttpUrlBuilder addQueryParameter(String name, String value) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if (encodedQueryNamesAndValues == null) {
            encodedQueryNamesAndValues = new ArrayList<>();
        }
        encodedQueryNamesAndValues.add(canonicalize(name, QUERY_COMPONENT_ENCODE_SET, false, false, true, true));
        encodedQueryNamesAndValues
                .add(value != null ? canonicalize(value, QUERY_COMPONENT_ENCODE_SET, false, false, true, true) : null);
        return this;
    }

    /**
     * Adds the pre-encoded query parameter to this URL's query string.
     *
     * @param encodedName encoded query parameter name
     * @param encodedValue encoded query parameter value
     * @return the corresponding {@code HttpUrlBuilder}
     */
    public HttpUrlBuilder addEncodedQueryParameter(String encodedName, String encodedValue) {
        if (encodedName == null) {
            throw new NullPointerException("encodedName == null");
        }
        if (encodedQueryNamesAndValues == null) {
            encodedQueryNamesAndValues = new ArrayList<>();
        }
        encodedQueryNamesAndValues
                .add(canonicalize(encodedName, QUERY_COMPONENT_REENCODE_SET, true, false, true, true));
        encodedQueryNamesAndValues.add(
                encodedValue != null ? canonicalize(encodedValue, QUERY_COMPONENT_REENCODE_SET, true, false, true, true)
                        : null);
        return this;
    }

    /**
     * reset the query parameter.
     *
     * @param name query parameter name
     * @param value query parameter value
     * @return the corresponding {@code HttpUrlBuilder}
     */
    public HttpUrlBuilder setQueryParameter(String name, String value) {
        removeAllQueryParameters(name);
        addQueryParameter(name, value);
        return this;
    }

    /**
     * reset the query parameter.
     *
     * @param encodedName encoded query parameter name
     * @param encodedValue encoded query parameter value
     * @return the corresponding {@code HttpUrlBuilder}
     */
    public HttpUrlBuilder setEncodedQueryParameter(String encodedName, String encodedValue) {
        removeAllEncodedQueryParameters(encodedName);
        addEncodedQueryParameter(encodedName, encodedValue);
        return this;
    }

    /**
     * remove the query parameter.
     *
     * @param name query parameter name
     * @return the corresponding {@code HttpUrlBuilder}
     */
    public HttpUrlBuilder removeAllQueryParameters(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if (encodedQueryNamesAndValues == null) {
            return this;
        }
        String nameToRemove = canonicalize(name, QUERY_COMPONENT_ENCODE_SET, false, false, true, true);
        removeAllCanonicalQueryParameters(nameToRemove);
        return this;
    }

    /**
     * remove the query parameter.
     *
     * @param encodedName encoded query parameter name
     * @return the corresponding {@code HttpUrlBuilder}
     */
    public HttpUrlBuilder removeAllEncodedQueryParameters(String encodedName) {
        if (encodedName == null) {
            throw new NullPointerException("encodedName == null");
        }
        if (encodedQueryNamesAndValues == null) {
            return this;
        }
        removeAllCanonicalQueryParameters(
                canonicalize(encodedName, QUERY_COMPONENT_REENCODE_SET, true, false, true, true));
        return this;
    }

    private void removeAllCanonicalQueryParameters(String canonicalName) {
        for (int i = encodedQueryNamesAndValues.size() - 2; i >= 0; i -= 2) {
            if (canonicalName.equals(encodedQueryNamesAndValues.get(i))) {
                encodedQueryNamesAndValues.remove(i + 1);
                encodedQueryNamesAndValues.remove(i);
                if (encodedQueryNamesAndValues.isEmpty()) {
                    encodedQueryNamesAndValues = null;
                    return;
                }
            }
        }
    }

    private void removeAllCanonicalQueryParameters(String canonicalName, String canonicalValue) {
        for (int i = encodedQueryNamesAndValues.size() - 2; i >= 0; i -= 2) {
            if (canonicalName.equals(encodedQueryNamesAndValues.get(i))
                    && canonicalValue.equals(encodedQueryNamesAndValues.get(i + 1))) {
                encodedQueryNamesAndValues.remove(i + 1);
                encodedQueryNamesAndValues.remove(i);
                if (encodedQueryNamesAndValues.isEmpty()) {
                    encodedQueryNamesAndValues = null;
                    return;
                }
            }
        }
    }

    private boolean containsQueryParameters(String canonicalName) {
        for (int i = encodedQueryNamesAndValues.size() - 2; i >= 0; i -= 2) {
            if (canonicalName.equals(encodedQueryNamesAndValues.get(i))) {
                return true;
            }
        }
        return false;
    }

    private List<String> percentDecode(List<String> list, boolean plusIsSpace) {
        int size = list.size();
        List<String> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String s = list.get(i);
            result.add(s != null ? percentDecode(s, plusIsSpace) : null);
        }
        return Collections.unmodifiableList(result);
    }

    private LinkedMultiValueMap<String, String> percentDecode2(List<String> list, boolean plusIsSpace) {
        int size = list.size();
        LinkedMultiValueMap<String, String> result = new LinkedMultiValueMap<>((size / 2 + 1));
        for (int i = 0; i < size; i++) {
            String key = list.get(i++);
            String value = list.get(i);
            result.add(key, value != null ? percentDecode(value, plusIsSpace) : null);
        }
        return result;
    }

    static String percentDecode(String encoded, boolean plusIsSpace) {
        return percentDecode(encoded, 0, encoded.length(), plusIsSpace);
    }

    static String percentDecode(String encoded, int pos, int limit, boolean plusIsSpace) {
        for (int i = pos; i < limit; i++) {
            char c = encoded.charAt(i);
            if (c == '%' || (c == '+' && plusIsSpace)) {
                // Slow path: the character at i requires decoding!
                Buffer out = new Buffer();
                out.writeUtf8(encoded, pos, i);
                percentDecode(out, encoded, i, limit, plusIsSpace);
                return out.readUtf8();
            }
        }

        // Fast path: no characters in [pos..limit) required decoding.
        return encoded.substring(pos, limit);
    }

    static void percentDecode(Buffer out, String encoded, int pos, int limit, boolean plusIsSpace) {
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = encoded.codePointAt(i);
            if (codePoint == '%' && i + 2 < limit) {
                int d1 = decodeHexDigit(encoded.charAt(i + 1));
                int d2 = decodeHexDigit(encoded.charAt(i + 2));
                if (d1 != -1 && d2 != -1) {
                    out.writeByte((d1 << 4) + d2);
                    i += 2;
                    continue;
                }
            } else if (codePoint == '+' && plusIsSpace) {
                out.writeByte(' ');
                continue;
            }
            out.writeUtf8CodePoint(codePoint);
        }
    }

    static boolean percentEncoded(String encoded, int pos, int limit) {
        return pos + 2 < limit && encoded.charAt(pos) == '%' && decodeHexDigit(encoded.charAt(pos + 1)) != -1
                && decodeHexDigit(encoded.charAt(pos + 2)) != -1;
    }

    static int decodeHexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        return -1;
    }

    static void namesAndValuesToQueryString(StringBuilder out, List<String> namesAndValues) {
        for (int i = 0, size = namesAndValues.size(); i < size; i += 2) {
            String name = namesAndValues.get(i);
            String value = namesAndValues.get(i + 1);
            if (i > 0) {
                out.append('&');
            }
            out.append(name);
            if (value != null) {
                out.append('=');
                out.append(value);
            }
        }
    }

    /**
     * Cuts {@code encodedQuery} up into alternating parameter names and values.
     * This divides a query string like {@code subject=math&easy&problem=5-2=3}
     * into the list {@code ["subject", "math",
     * "easy", null, "problem", "5-2=3"]}. Note that values may be null and may
     * contain '=' characters.
     *
     * @param encodedQuery encoded query string
     * @return collection contains alternating parameter names and values
     */
    static List<String> queryStringToNamesAndValues(String encodedQuery) {
        List<String> result = new ArrayList<>();
        for (int pos = 0; pos <= encodedQuery.length();) {
            int ampersandOffset = encodedQuery.indexOf('&', pos);
            if (ampersandOffset == -1) {
                ampersandOffset = encodedQuery.length();
            }

            int equalsOffset = encodedQuery.indexOf('=', pos);
            if (equalsOffset == -1 || equalsOffset > ampersandOffset) {
                result.add(encodedQuery.substring(pos, ampersandOffset));
                // No value for this name.
                result.add(null);
            } else {
                result.add(encodedQuery.substring(pos, equalsOffset));
                result.add(encodedQuery.substring(equalsOffset + 1, ampersandOffset));
            }
            pos = ampersandOffset + 1;
        }
        return result;
    }

    /**
     * Returns a substring of {@code input} on the range {@code [pos..limit)}
     * with the following transformations:
     * <ul>
     * <li>Tabs, newlines, form feeds and carriage returns are skipped.
     * <li>In queries, ' ' is encoded to '+' and '+' is encoded to "%2B".
     * <li>Characters in {@code encodeSet} are percent-encoded.
     * <li>Control characters and non-ASCII characters are percent-encoded.
     * <li>All other characters are copied without transformation.
     * </ul>
     *
     * @param input source string
     * @param pos begin index
     * @param limit substring length
     * @param encodeSet encode set
     * @param alreadyEncoded true to leave '%' as-is; false to convert it to
     *            '%25'.
     * @param strict true to encode '%' if it is not the prefix of a valid
     *            percent encoding.
     * @param plusIsSpace true to encode '+' as "%2B" if it is not already
     *            encoded.
     * @param asciiOnly true to encode all non-ASCII codepoints.
     * @param charset which charset to use, null equals UTF-8.
     * @return a substring of {@code input} on the range {@code [pos..limit)}
     */
    static String canonicalize(String input, int pos, int limit, String encodeSet, boolean alreadyEncoded,
                               boolean strict, boolean plusIsSpace, boolean asciiOnly, Charset charset) {
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (codePoint < 0x20 || codePoint == 0x7f || codePoint >= 0x80 && asciiOnly
                    || encodeSet.indexOf(codePoint) != -1
                    || codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))
                    || codePoint == '+' && plusIsSpace) {
                // Slow path: the character at i requires encoding!
                Buffer out = new Buffer();
                out.writeUtf8(input, pos, i);
                canonicalize(out, input, i, limit, encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, charset);
                return out.readUtf8();
            }
        }

        // Fast path: no characters in [pos..limit) required encoding.
        return input.substring(pos, limit);
    }

    static void canonicalize(Buffer out, String input, int pos, int limit, String encodeSet, boolean alreadyEncoded,
                             boolean strict, boolean plusIsSpace, boolean asciiOnly, Charset charset) {
        // Lazily allocated.
        Buffer encodedCharBuffer = null;
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (alreadyEncoded && (codePoint == '\t' || codePoint == '\n' || codePoint == '\f' || codePoint == '\r')) {
                // Skip this character.
            } else if (codePoint == '+' && plusIsSpace) {
                // Encode '+' as '%2B' since we permit ' ' to be encoded as either '+' or '%20'.
                out.writeUtf8(alreadyEncoded ? "+" : "%2B");
            } else if (codePoint < 0x20 || codePoint == 0x7f || codePoint >= 0x80 && asciiOnly
                    || encodeSet.indexOf(codePoint) != -1
                    || codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))) {
                // Percent encode this character.
                if (encodedCharBuffer == null) {
                    encodedCharBuffer = new Buffer();
                }

                if (charset == null || charset.equals(MyossConstants.UTF_8)) {
                    encodedCharBuffer.writeUtf8CodePoint(codePoint);
                } else {
                    encodedCharBuffer.writeString(input, i, i + Character.charCount(codePoint), charset);
                }

                while (!encodedCharBuffer.exhausted()) {
                    int b = encodedCharBuffer.readByte() & 0xff;
                    out.writeByte('%');
                    out.writeByte(HEX_DIGITS[(b >> 4) & 0xf]);
                    out.writeByte(HEX_DIGITS[b & 0xf]);
                }
            } else {
                // This character doesn't need encoding. Just copy it over.
                out.writeUtf8CodePoint(codePoint);
            }
        }
    }

    static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict,
                               boolean plusIsSpace, boolean asciiOnly) {
        return canonicalize(input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, null);
    }
}

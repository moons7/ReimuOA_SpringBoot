package com.reimu.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Only adapt for JDK version 8+
 */
public final class EmptyUtils {

    //参考https://www.oschina.net/news/76993/java8-optional
    public static <T> Optional<T> createOptional(T object, boolean nullable) {
        return nullable?Optional.ofNullable(object):Optional.of(object);
    }

    //object could be null value in default
    public static <T> Optional<T> createOptional(T object) {
        return Optional.ofNullable(object);
    }


    public static boolean isEmpty(Object object) {
        return isEmpty(object, true);
    }

    public static boolean isEmpty(Object object, boolean nullable) {
        Optional optional = createOptional(object, nullable);
        return !optional.isPresent();
    }

    public static <T extends Collection> boolean isEmpty(T collection, boolean nullable) {
        Optional optional = createOptional(collection, nullable);
        return !optional.isPresent() ||  collection.isEmpty();
    }

    public static <T extends Collection> boolean isEmpty(T collection) {
        return isEmpty(collection, true);
    }

    public static boolean isEmpty(String string) {
        return isEmpty(string, true);
    }

    public static boolean isEmpty(String string, boolean nullable) {
        Optional optional = createOptional(string, nullable);
        return !optional.isPresent() || StringUtils.isBlank(string);
    }

    public static boolean isEmpty(Map map) {
        return isEmpty(map, true);
    }

    public static boolean isEmpty(Map map, boolean nullable) {
        Optional optional = createOptional(map, nullable);
        return !optional.isPresent() || map.isEmpty();
    }
}

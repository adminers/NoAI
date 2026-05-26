package com.dpv4.pectin.utils;

import java.util.*;
import java.util.stream.Collectors;

public final class CollectionUtils {
    
    private CollectionUtils() {}
    
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
    
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }
    
    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }
    
    public static <K, V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
    }
    
    public static <T> List<T> nullToEmpty(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }
    
    public static <K, V> Map<K, V> nullToEmpty(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }
    
    public static <T> List<T> newArrayList() {
        return new ArrayList<>();
    }
    
    @SafeVarargs
    public static <T> List<T> newArrayList(T... elements) {
        if (elements == null) {
            return new ArrayList<>();
        }
        List<T> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }
    
    public static <T> List<T> newArrayList(Collection<T> collection) {
        return collection == null ? new ArrayList<>() : new ArrayList<>(collection);
    }
    
    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<>();
    }
    
    public static <K, V> Map<K, V> newHashMap(int initialCapacity) {
        return new HashMap<>(initialCapacity);
    }
    
    public static <T> Set<T> newHashSet() {
        return new HashSet<>();
    }
    
    @SafeVarargs
    public static <T> Set<T> newHashSet(T... elements) {
        if (elements == null) {
            return new HashSet<>();
        }
        Set<T> set = new HashSet<>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }
    
    public static <T> T first(List<T> list) {
        return isEmpty(list) ? null : list.get(0);
    }
    
    public static <T> T last(List<T> list) {
        return isEmpty(list) ? null : list.get(list.size() - 1);
    }
    
    public static <T> T get(List<T> list, int index) {
        if (isEmpty(list) || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }
    
    public static <K, V> V get(Map<K, V> map, K key) {
        return map == null ? null : map.get(key);
    }
    
    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        if (map == null) {
            return defaultValue;
        }
        V value = map.get(key);
        return value != null ? value : defaultValue;
    }
    
    public static <T> List<T> subList(List<T> list, int fromIndex, int toIndex) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        int size = list.size();
        fromIndex = Math.max(0, fromIndex);
        toIndex = Math.min(size, toIndex);
        return fromIndex >= toIndex ? Collections.emptyList() : list.subList(fromIndex, toIndex);
    }
    
    public static <T> List<T> slice(List<T> list, int start) {
        return subList(list, start, list.size());
    }
    
    public static <T> List<T> slice(List<T> list, int start, int end) {
        return subList(list, start, end);
    }
    
    public static <T> boolean contains(List<T> list, T element) {
        return isNotEmpty(list) && list.contains(element);
    }
    
    public static <K> boolean containsKey(Map<K, ?> map, K key) {
        return isNotEmpty(map) && map.containsKey(key);
    }
    
    public static <V> boolean containsValue(Map<?, V> map, V value) {
        return isNotEmpty(map) && map.containsValue(value);
    }
    
    public static <T> int indexOf(List<T> list, T element) {
        return isEmpty(list) ? -1 : list.indexOf(element);
    }
    
    public static <T> int lastIndexOf(List<T> list, T element) {
        return isEmpty(list) ? -1 : list.lastIndexOf(element);
    }
    
    public static <T> List<T> reverse(List<T> list) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(list);
        Collections.reverse(result);
        return result;
    }
    
    public static <T> List<T> sort(List<T> list) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(list);
        Collections.sort(result);
        return result;
    }
    
    public static <T> List<T> sort(List<T> list, Comparator<? super T> comparator) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(list);
        result.sort(comparator);
        return result;
    }
    
    public static <T> List<T> distinct(List<T> list) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().distinct().collect(Collectors.toList());
    }
    
    public static <T> List<T> filter(List<T> list, java.util.function.Predicate<T> predicate) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().filter(predicate).collect(Collectors.toList());
    }
    
    public static <T, R> List<R> map(List<T> list, java.util.function.Function<T, R> mapper) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(mapper).collect(Collectors.toList());
    }
    
    public static <T> T findFirst(List<T> list, java.util.function.Predicate<T> predicate) {
        if (isEmpty(list)) {
            return null;
        }
        return list.stream().filter(predicate).findFirst().orElse(null);
    }
    
    public static <T> boolean anyMatch(List<T> list, java.util.function.Predicate<T> predicate) {
        return isNotEmpty(list) && list.stream().anyMatch(predicate);
    }
    
    public static <T> boolean allMatch(List<T> list, java.util.function.Predicate<T> predicate) {
        return isEmpty(list) || list.stream().allMatch(predicate);
    }
    
    public static <T> boolean noneMatch(List<T> list, java.util.function.Predicate<T> predicate) {
        return isEmpty(list) || list.stream().noneMatch(predicate);
    }
    
    public static <T> T reduce(List<T> list, T identity, java.util.function.BinaryOperator<T> accumulator) {
        if (isEmpty(list)) {
            return identity;
        }
        return list.stream().reduce(identity, accumulator);
    }
    
    public static <T> long count(List<T> list) {
        return isEmpty(list) ? 0 : list.size();
    }
    
    public static <T> long count(List<T> list, java.util.function.Predicate<T> predicate) {
        return isEmpty(list) ? 0 : list.stream().filter(predicate).count();
    }
    
    public static <T> void forEach(List<T> list, java.util.function.Consumer<T> action) {
        if (isNotEmpty(list)) {
            list.forEach(action);
        }
    }
    
    public static <K, V> void forEach(Map<K, V> map, java.util.function.BiConsumer<K, V> action) {
        if (isNotEmpty(map)) {
            map.forEach(action);
        }
    }
    
    public static <T> List<T> concat(List<T>... lists) {
        if (lists == null || lists.length == 0) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>();
        for (List<T> list : lists) {
            if (isNotEmpty(list)) {
                result.addAll(list);
            }
        }
        return result;
    }
    
    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        if (isEmpty(list1) || isEmpty(list2)) {
            return Collections.emptyList();
        }
        Set<T> set = new HashSet<>(list1);
        return list2.stream().filter(set::contains).collect(Collectors.toList());
    }
    
    public static <T> List<T> union(List<T> list1, List<T> list2) {
        if (isEmpty(list1)) {
            return nullToEmpty(list2);
        }
        if (isEmpty(list2)) {
            return new ArrayList<>(list1);
        }
        Set<T> set = new HashSet<>(list1);
        set.addAll(list2);
        return new ArrayList<>(set);
    }
    
    public static <T> List<T> difference(List<T> list1, List<T> list2) {
        if (isEmpty(list1)) {
            return Collections.emptyList();
        }
        if (isEmpty(list2)) {
            return new ArrayList<>(list1);
        }
        Set<T> set = new HashSet<>(list2);
        return list1.stream().filter(e -> !set.contains(e)).collect(Collectors.toList());
    }
}
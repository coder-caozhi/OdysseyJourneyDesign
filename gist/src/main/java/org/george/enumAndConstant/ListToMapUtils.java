package org.george.enumAndConstant;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class ListToMapUtils {

    /**
     * 将 List 转换为 Map
     * 冲突策略：保留第一个 (保留已存在的，忽略新传入的)
     */
    public static <E, K, V> Map<K, V> toMap(
            List<E> list,
            Function<? super E, ? extends K> keyMapper,
            Function<? super E, ? extends V> valueMapper) {

        return Optional.ofNullable(list)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        keyMapper,
                        valueMapper,
                        (existing, incoming) -> existing));
    }

    /**
     * 生产级：List 转 HashMap（完全按你的风格写）
     */
    public static <K, V> Map<K, V> toHashMap(List<V> list, Function<V, K> keyMapper) {
        return Optional.ofNullable(list)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .collect(
                        HashMap::new,
                        (map, item) -> map.put(
                                keyMapper.apply(item),
                                item
                        ),
                        Map::putAll
                );
    }
}

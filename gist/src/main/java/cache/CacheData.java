package cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 管理服务 RPC 接口返回的统一数据结构.
 *
 * <p>管理服务对外暴露的每个接口都返回此结构：
 * <ul>
 *   <li>{@code dataList}  —— 该表全部记录</li>
 *   <li>{@code latestUpdateTime} —— 所有记录中最新的 update_time，
 *       用于判断数据是否发生变化</li>
 * </ul>
 *
 * @param <T> 业务实体类型（例如 Badge、Gift）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheData<T> {

    /** 全量数据列表，为空时返回空列表而非 null */
    @Builder.Default
    private List<T> dataList = Collections.emptyList();

    /** 全量数据中最新的更新时间，用于增量判断 */
    private LocalDateTime latestUpdateTime;

    /**
     * 快捷构造：数据 + 时间.
     */
    public static <T> CacheData<T> of(List<T> dataList, LocalDateTime latestUpdateTime) {
        return CacheData.<T>builder()
                .dataList(dataList != null ? dataList : Collections.emptyList())
                .latestUpdateTime(latestUpdateTime)
                .build();
    }

    /**
     * 数据是否为空.
     */
    public boolean isEmpty() {
        return dataList == null || dataList.isEmpty();
    }
}

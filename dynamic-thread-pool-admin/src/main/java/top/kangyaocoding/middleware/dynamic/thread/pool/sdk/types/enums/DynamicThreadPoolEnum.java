package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 描述: 动态线程池枚举
 *
 * @author K·Herbert
 * @since 2024-09-21 13:39
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum DynamicThreadPoolEnum {

    THREAD_POOL_CONFIG_LIST_KEY("THREAD_POOL_CONFIG_LIST_KEY", "动态线程池配置列表"),
    THREAD_POOL_CONFIG_PARAMETER_LIST_KEY("THREAD_POOL_CONFIG_PARAMETER_LIST_KEY", "动态线程池配置参数列表"),
    DYNAMIC_THREAD_POOL_REDIS_TOPIC("DYNAMIC_THREAD_POOL_REDIS_TOPIC", "动态线程池redis主题"),
    ;

    private String code;

    private String info;

    public static DynamicThreadPoolEnum fromCode(String code) {
        for (DynamicThreadPoolEnum dynamicThreadPoolEnum : DynamicThreadPoolEnum.values()) {
            if (dynamicThreadPoolEnum.getCode().equals(code)) {
                return dynamicThreadPoolEnum;
            }
        }
        return null;
    }
}

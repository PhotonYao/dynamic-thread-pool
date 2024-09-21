package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.model.entity;

import lombok.Data;

/**
 * 描述: 线程池配置实体
 *
 * @author K·Herbert
 * @since 2024-09-15 00:30
 */

@Data
public class ThreadPoolConfigEntity {
    /**
     * 应用名称
     */
    private String appName;

    /**
     * 线程池名称
     */
    private String threadPoolName;

    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 最大线程数
     */
    private int maximumPoolSize;

    /**
     * 当前活跃线程数
     */
    private int activeCount;

    /**
     * 当前池中线程数
     */
    private int poolSize;

    /**
     * 队列类型
     */
    private String queueType;

    /**
     * 当前队列任务数
     */
    private int queueSize;

    /**
     * 队列剩余任务数
     */
    private int remainingCapacity;

}

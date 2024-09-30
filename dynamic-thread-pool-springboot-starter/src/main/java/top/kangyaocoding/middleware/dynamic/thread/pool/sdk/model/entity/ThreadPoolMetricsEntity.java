package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity;

/**
 * 描述: 线程池监控信息
 *
 * @author K·Herbert
 * @since 2024-09-29 18:51
 */
public class ThreadPoolMetricsEntity {

    private final ThreadPoolConfigEntity threadPoolConfigEntity;

    public ThreadPoolMetricsEntity(ThreadPoolConfigEntity threadPoolConfigEntity) {
        this.threadPoolConfigEntity = threadPoolConfigEntity;
    }

    public ThreadPoolConfigEntity getThreadPoolConfigEntity() {
        return threadPoolConfigEntity;
    }
}

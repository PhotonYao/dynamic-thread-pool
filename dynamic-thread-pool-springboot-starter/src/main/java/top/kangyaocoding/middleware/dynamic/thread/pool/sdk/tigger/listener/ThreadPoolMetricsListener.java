package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.tigger.listener;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.event.EventListener;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolConfigEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolMetricsEntity;

/**
 * 描述: 线程池监控监听器
 *
 * @author K·Herbert
 * @since 2024-09-29 20:19
 */
public class ThreadPoolMetricsListener {
    private final MeterRegistry meterRegistry;

    public ThreadPoolMetricsListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @EventListener
    public void handleThreadPoolMetricsEvent(ThreadPoolMetricsEntity event) {
        ThreadPoolConfigEntity threadPoolConfigEntity = event.getThreadPoolConfigEntity();

        String appName = threadPoolConfigEntity.getAppName();
        String threadPoolName = threadPoolConfigEntity.getThreadPoolName();

        // 注册核心线程数指标
        Gauge.builder("thread_pool_core_size", threadPoolConfigEntity, ThreadPoolConfigEntity::getCorePoolSize)
                .tag("app", appName)
                .tag("pool", threadPoolName)
                .description("核心线程数")
                .register(meterRegistry);

        // 注册最大线程数指标
        Gauge.builder("thread_pool_maximum_size", threadPoolConfigEntity, ThreadPoolConfigEntity::getMaximumPoolSize)
                .tag("app", appName)
                .tag("pool", threadPoolName)
                .description("最大线程数")
                .register(meterRegistry);

        // 注册当前活跃线程数指标
        Gauge.builder("thread_pool_active_count", threadPoolConfigEntity, ThreadPoolConfigEntity::getActiveCount)
                .tag("app", appName)
                .tag("pool", threadPoolName)
                .description("当前活跃线程数")
                .register(meterRegistry);

        // 注册当前队列任务数指标
        Gauge.builder("thread_pool_queue_size", threadPoolConfigEntity, ThreadPoolConfigEntity::getQueueSize)
                .tag("app", appName)
                .tag("pool", threadPoolName)
                .description("当前队列任务数")
                .register(meterRegistry);

        // 注册剩余队列容量指标
        Gauge.builder("thread_pool_remaining_capacity", threadPoolConfigEntity, ThreadPoolConfigEntity::getRemainingCapacity)
                .tag("app", appName)
                .tag("pool", threadPoolName)
                .description("队列剩余任务数")
                .register(meterRegistry);
    }
}

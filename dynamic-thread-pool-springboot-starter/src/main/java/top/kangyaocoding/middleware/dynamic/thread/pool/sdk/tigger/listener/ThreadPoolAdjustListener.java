package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.tigger.listener;

import com.alibaba.fastjson2.JSON;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.registry.IRegistry;

/**
 * 描述: 线程池动态调整监听器
 *
 * @author K·Herbert
 * @since 2024-09-15 13:17
 */
public class ThreadPoolAdjustListener implements MessageListener<ThreadPoolConfigEntity> {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolAdjustListener.class);

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    private final IRegistry registry;

    public ThreadPoolAdjustListener(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.registry = registry;
    }

    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
        logger.info("线程池动态调整配置。线程池名称：{}，核心线程数：{}，最大线程数：{}",
                threadPoolConfigEntity.getThreadPoolName(),
                threadPoolConfigEntity.getCorePoolSize(),
                threadPoolConfigEntity.getMaximumPoolSize());
        dynamicThreadPoolService.updateThreadPoolConfig(threadPoolConfigEntity);
        // 更新后上报数据
        registry.reportThreadPool(dynamicThreadPoolService.getThreadPoolConfigList());
        registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);

        logger.info("线程池动态调整配置完成。线程池配置：{}", JSON.toJSONString(threadPoolConfigEntity));
    }
}

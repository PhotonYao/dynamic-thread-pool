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

    /**
     * 接收消息并动态调整线程池配置
     *
     * @param charSequence 字符序列，此处未具体使用
     * @param threadPoolConfigEntity 线程池配置实体，包含线程池的核心线程数、最大线程数等信息
     */
    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
        // 记录线程池动态调整的日志信息
        logger.info("线程池动态调整配置。线程池名称：{}，核心线程数：{}，最大线程数：{}",
                threadPoolConfigEntity.getThreadPoolName(),
                threadPoolConfigEntity.getCorePoolSize(),
                threadPoolConfigEntity.getMaximumPoolSize());
        // 更新线程池配置
        dynamicThreadPoolService.updateThreadPoolConfig(threadPoolConfigEntity);
        // 更新后上报数据
        // 确保注册中心的线程池配置数据同步最新状态
        registry.reportThreadPool(dynamicThreadPoolService.getThreadPoolConfigList());
        // 上报具体的线程池配置参数
        registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);

        // 记录线程池动态调整完成的日志信息
        logger.info("线程池动态调整配置完成。线程池配置：{}", JSON.toJSONString(threadPoolConfigEntity));
    }

}

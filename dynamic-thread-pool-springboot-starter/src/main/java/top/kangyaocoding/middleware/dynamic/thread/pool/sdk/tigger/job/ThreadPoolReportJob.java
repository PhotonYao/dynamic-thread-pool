package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.tigger.job;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.registry.IRegistry;

import java.util.List;

/**
 * 描述: 动态线程池上报任务
 *
 * @author K·Herbert
 * @since 2024-09-15 11:53
 */
@Slf4j
public class ThreadPoolReportJob {

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    private final IRegistry registry;

    public ThreadPoolReportJob(IRegistry registry, IDynamicThreadPoolService dynamicThreadPoolService) {
        this.registry = registry;
        this.dynamicThreadPoolService = dynamicThreadPoolService;
    }

    // 定时任务，每20秒执行一次，用于上报线程池状态
    @Scheduled(cron = "*/20 * * * * ?")
    public void executeThreadPoolReportList() {
        // 获取所有线程池的配置信息
        List<ThreadPoolConfigEntity> threadPoolConfigEntityList = dynamicThreadPoolService.getThreadPoolConfigList();
        // 向注册中心上报线程池信息
        registry.reportThreadPool(threadPoolConfigEntityList);

        // 记录线程池信息的日志
        log.info("动态线程池上报，线程池信息: {}", JSON.toJSONString(threadPoolConfigEntityList));

        // 遍历所有线程池配置信息，逐一上报
        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntityList) {
            // 向注册中心上报单个线程池的配置参数
            registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
            // 记录单个线程池参数信息的日志
            log.info("动态线程池上报，线程池参数信息: {}", JSON.toJSONString(threadPoolConfigEntity));
        }
    }
}

package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.tigger.job;


import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolConfigEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolMetricsEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.IDynamicThreadPoolService;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.INotifyService;

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

    private final ApplicationEventPublisher eventPublisher; // 注入事件发布器

    @Autowired
    private INotifyService notifyService;

    public ThreadPoolReportJob(IRegistry registry, IDynamicThreadPoolService dynamicThreadPoolService, ApplicationEventPublisher eventPublisher) {
        this.registry = registry;
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.eventPublisher = eventPublisher;
    }

    // 定时任务，每20秒执行一次，用于上报线程池状态
    @Scheduled(cron = "*/20 * * * * ?")
    public void executeThreadPoolReportList() {
        List<ThreadPoolConfigEntity> threadPoolConfigEntityList = dynamicThreadPoolService.getThreadPoolConfigList();

        if (threadPoolConfigEntityList.isEmpty()) {
            log.info("动态线程池无需上报，线程池列表为空");
            return;
        }

        // 上报线程池列表信息
        registry.reportThreadPool(threadPoolConfigEntityList);
        notifyService.sendIfThreadPoolHasDanger(threadPoolConfigEntityList);
        log.info("动态线程池上报开始，线程池列表: {}", JSON.toJSONString(threadPoolConfigEntityList));

        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntityList) {
            try {
                registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
                log.info("动态线程池上报成功: {}", JSON.toJSONString(threadPoolConfigEntity));

                // 发布事件
                eventPublisher.publishEvent(new ThreadPoolMetricsEntity(threadPoolConfigEntity));

            } catch (Exception e) {
                log.error("动态线程池上报失败: {}", JSON.toJSONString(threadPoolConfigEntity), e);
            }
        }

        log.debug("动态线程池上报已完成，线程池数量: {}", threadPoolConfigEntityList.size());
    }

}

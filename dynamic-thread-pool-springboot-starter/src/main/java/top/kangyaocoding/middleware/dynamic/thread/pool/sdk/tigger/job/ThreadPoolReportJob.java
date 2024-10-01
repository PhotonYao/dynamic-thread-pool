package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.tigger.job;


import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config.DynamicThreadPoolReportProperties;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolConfigEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.IDynamicThreadPoolService;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.INotifyService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * 描述: 动态线程池上报任务
 *
 * @author K·Herbert
 * @since 2024-09-15 11:53
 */
@Slf4j
public class ThreadPoolReportJob {

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    private final DynamicThreadPoolReportProperties dynamicThreadPoolReportProperties;

    private ScheduledFuture<?> future;

    private final ThreadPoolTaskScheduler taskScheduler;

    private final IRegistry registry;

    @Autowired
    private INotifyService notifyService;

    public ThreadPoolReportJob(IRegistry registry, IDynamicThreadPoolService dynamicThreadPoolService, DynamicThreadPoolReportProperties dynamicThreadPoolReportProperties) {
        this.registry = registry;
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.dynamicThreadPoolReportProperties = dynamicThreadPoolReportProperties;
        this.taskScheduler = new ThreadPoolTaskScheduler();
        this.taskScheduler.initialize();
    }

    // 在应用启动时调度任务
    @PostConstruct
    public void scheduleTask() {
        updateCronTask(dynamicThreadPoolReportProperties.getReport().getCron());
    }

    // 更新定时任务
    public void updateCronTask(String cronExpression) {
        if (future != null) {
            future.cancel(false);  // 停止当前的任务
        }

        future = taskScheduler.schedule(this::executeThreadPoolReportList, new CronTrigger(cronExpression));
    }

    // 定时任务
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
            } catch (Exception e) {
                log.error("动态线程池上报失败: {}", JSON.toJSONString(threadPoolConfigEntity), e);
            }
        }

        log.debug("动态线程池上报已完成，线程池数量: {}", threadPoolConfigEntityList.size());
    }

}

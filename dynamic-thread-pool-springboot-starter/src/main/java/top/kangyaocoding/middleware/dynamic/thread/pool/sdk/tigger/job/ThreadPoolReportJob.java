package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.tigger.job;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class ThreadPoolReportJob {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolReportJob.class);

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    private final IRegistry registry;

    public ThreadPoolReportJob(IRegistry registry, IDynamicThreadPoolService dynamicThreadPoolService) {
        this.registry = registry;
        this.dynamicThreadPoolService = dynamicThreadPoolService;
    }

    // 20 秒执行一次
    @Scheduled(cron = "*/20 * * * * ?")
    public void executeThreadPoolReportList() {
        List<ThreadPoolConfigEntity> threadPoolConfigEntityList = dynamicThreadPoolService.getThreadPoolConfigList();
        registry.reportThreadPool(threadPoolConfigEntityList);

        logger.info("动态线程池上报，线程池信息: {}", JSON.toJSONString(threadPoolConfigEntityList));

        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntityList) {
            registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
            logger.info("动态线程池上报，线程池参数信息: {}", JSON.toJSONString(threadPoolConfigEntity));
        }
    }
}

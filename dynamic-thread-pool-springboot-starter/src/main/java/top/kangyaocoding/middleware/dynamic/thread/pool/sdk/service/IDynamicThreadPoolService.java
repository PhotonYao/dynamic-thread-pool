package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service;

import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * 描述: 动态线程池服务层接口
 *
 * @author K·Herbert
 * @since 2024-09-15 00:28
 */
public interface IDynamicThreadPoolService {

    List<ThreadPoolConfigEntity> getThreadPoolConfigList();

    ThreadPoolConfigEntity getThreadPoolConfigByName(String threadPoolName);

    boolean updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity);

    String getReportCron();

    String updateReportCron(String cron);
}

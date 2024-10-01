package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service;

import org.springframework.web.bind.annotation.RequestParam;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolConfigEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.model.Response;

import java.util.List;

/**
 * 描述: 动态线程池服务接口
 *
 * @author K·Herbert
 * @since 2024-09-21 13:27
 */

public interface IDynamicThreadPoolService {
    Response<List<ThreadPoolConfigEntity>> queryThreadPoolList();

    Response<ThreadPoolConfigEntity> queryThreadPoolConfig(String appName, String threadPoolName);

    Response<Boolean> updateThreadPoolConfig(ThreadPoolConfigEntity request);

}

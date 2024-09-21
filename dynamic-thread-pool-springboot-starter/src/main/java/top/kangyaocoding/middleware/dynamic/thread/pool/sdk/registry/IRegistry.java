package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.registry;

import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * 描述: 注册中心接口
 *
 * @author K·Herbert
 * @since 2024-09-15 11:22
 */
public interface IRegistry {
    void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntities);

    void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);
}

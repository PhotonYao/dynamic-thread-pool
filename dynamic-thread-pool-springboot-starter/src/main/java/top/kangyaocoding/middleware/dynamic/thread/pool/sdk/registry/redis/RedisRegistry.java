package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.registry.redis;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.model.vo.RegistryEnumVO;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.registry.IRegistry;

import java.time.Duration;
import java.util.List;

/**
 * 描述: redis 注册中心
 *
 * @author K·Herbert
 * @since 2024-09-15 11:30
 */

public class RedisRegistry implements IRegistry {

    private final RedissonClient redissonClient;

    public RedisRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 向Redisson客户端报告线程池配置列表
     * 此方法首先删除之前存在的配置，然后将新的线程池配置实体列表添加到Redisson的列表中
     *
     * @param threadPoolConfigEntities 线程池配置实体列表
     */
    @Override
    public void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntities) {
        // 采用 Hash 存储，避免重复
        RMap<String, ThreadPoolConfigEntity> map = redissonClient.getMap(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntities) {
            String threadPoolName = threadPoolConfigEntity.getThreadPoolName();
            // 直接调用 put 即可覆盖已有值
            map.put(threadPoolName, threadPoolConfigEntity);
        }
    }

    /**
     * 向Redisson客户端报告单个线程池配置参数
     * 此方法根据应用程序名称和线程池名称生成一个唯一的键，使用该键在Redisson中保存线程池配置实体
     *
     * @param threadPoolConfigEntity 线程池配置实体
     */
    @Override
    public void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity) {
        // 根据应用名和线程池名生成缓存键
        String cacheKey = RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" +
                threadPoolConfigEntity.getAppName() + "_" +
                threadPoolConfigEntity.getThreadPoolName();
        // 获取或创建一个Redisson的Bucket对象，用于存储单个线程池配置
        RBucket<ThreadPoolConfigEntity> bucket = redissonClient.getBucket(cacheKey);
        // 将线程池配置实体存储到Redisson的Bucket中，设置过期时间为30天
        bucket.set(threadPoolConfigEntity, Duration.ofDays(30));
    }
}

package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config;

import com.alibaba.fastjson2.JSON;
import jodd.util.StringUtil;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.model.vo.RegistryEnumVO;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.registry.redis.RedisRegistry;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.tigger.job.ThreadPoolReportJob;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.tigger.listener.ThreadPoolAdjustListener;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述: 动态线程池配置类
 *
 * @author K·Herbert
 * @since 2024-09-13 20:28
 */

@Configuration
@EnableConfigurationProperties(DynamicThreadPoolAutoConfigProperties.class)
@EnableScheduling
public class DynamicThreadPoolAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);

    private String applicationName;

    @Bean("dynamicThreadRedissonClient")
    public RedissonClient redissonClient(DynamicThreadPoolAutoConfigProperties properties) {
        Config config = new Config();
        // 根据需要可以设定编解码器；https://github.com/redisson/redisson/wiki/4.-%E6%95%B0%E6%8D%AE%E5%BA%8F%E5%88%97%E5%8C%96
        config.setCodec(JsonJacksonCodec.INSTANCE);

        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive())
        ;

        RedissonClient redissonClient = Redisson.create(config);

        logger.info("动态线程池，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());

        return redissonClient;
    }

    @Bean
    public IRegistry redisRegistry(RedissonClient dynamicThreadRedissonClient) {
        return new RedisRegistry(dynamicThreadRedissonClient);
    }


    /**
     * 定义名为 "dynamicThreadPoolAService" 的 Bean，用于管理动态线程池。
     * <p>
     * 1. 从应用程序上下文中获取应用名称，如果名称为空，记录警告日志。
     * 2. 从 Redis 中获取每个线程池的配置信息（通过线程池的 key 和应用名称拼接查询 Redis），
     * 并根据获取的配置，设置本地线程池的核心线程数和最大线程数。
     * 3. 记录当前所有线程池的 key 信息。
     * 4. 返回一个 DynamicThreadPoolService 实例。
     *
     * @param applicationContext 应用程序上下文，获取应用程序名称等配置信息。
     * @param threadPoolExecutorMap 包含应用程序中的线程池执行器的 Map，用于存储多个线程池实例。
     * @param dynamicThreadRedissonClient 用于与 Redis 交互的 Redisson 客户端，从 Redis 中获取线程池配置信息。
     *
     * @return DynamicThreadPoolService 动态线程池服务，包含应用程序名称和线程池执行器信息。
     */
    @Bean("dynamicThreadPoolAService")
    public DynamicThreadPoolService dynamicThreadPoolAService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap, RedissonClient dynamicThreadRedissonClient) {
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");

        if (StringUtil.isBlank(applicationName)) {
            logger.warn("应用程序名称为 null。您确定已配置应用程序吗？");
        }

        // 获取缓存数据，设置本地线程池
        Set<String> threadPoolExecutorKeys = threadPoolExecutorMap.keySet();
        for (String key : threadPoolExecutorKeys) {
            ThreadPoolConfigEntity threadPoolConfigEntity = dynamicThreadRedissonClient.<ThreadPoolConfigEntity>getBucket(RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + applicationName + "_" + key).get();
            if (null == threadPoolConfigEntity) continue;
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(key);
            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
        }

        logger.info("线程池信息：{}", JSON.toJSONString(threadPoolExecutorKeys));

        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);
    }

    @Bean
    public ThreadPoolReportJob threadPoolReportJob(IRegistry registry, IDynamicThreadPoolService dynamicThreadPoolService) {
        return new ThreadPoolReportJob(registry, dynamicThreadPoolService);
    }

    @Bean
    public ThreadPoolAdjustListener threadPoolAdjustListener(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        return new ThreadPoolAdjustListener(dynamicThreadPoolService, registry);
    }

    /**
     * 创建一个用于动态线程池调整的Redisson主题 bean
     * 该方法配置并返回一个Redisson主题(RTopic)，该主题用于监听线程池调整事件
     * 它将特定的监听器注册到与动态线程池相关的Redisson主题上
     *
     * @param dynamicThreadRedissonClient Redisson客户端实例，用于与Redis进行通信
     * @param threadPoolAdjustListener 线程池调整事件的监听器，当收到消息时会被触发
     * @return RTopic Redisson的主题实例，注册了线程池调整监听器
     */
    @Bean("dynamicThreadPoolRedisTopic")
    public RTopic threadPoolAdjustListener(RedissonClient dynamicThreadRedissonClient, ThreadPoolAdjustListener threadPoolAdjustListener) {
        // 构建主题名称，结合系统配置的动态线程池Redis主题前缀和应用名称
        RTopic topic = dynamicThreadRedissonClient.getTopic(RegistryEnumVO.DYNAMIC_THREAD_POOL_REDIS_TOPIC.getKey() + "_" + applicationName);
        // 为特定的类和监听器注册消息监听
        topic.addListener(ThreadPoolConfigEntity.class, threadPoolAdjustListener);
        // 返回配置好的主题实例
        return topic;

    }
}

package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config;

import com.alibaba.fastjson2.JSON;
import jodd.util.StringUtil;
import org.redisson.Redisson;
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
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.registry.redis.RedisRegistry;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.tigger.job.ThreadPoolReportJob;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
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


    @Bean("dynamicThreadPoolAService")
    public DynamicThreadPoolService dynamicThreadPoolAService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");

        if (StringUtil.isBlank(applicationName)) {
            logger.warn("应用程序名称为 null。您确定已配置应用程序吗？");
        }
        Set<String> threadPoolExecutorKeys = threadPoolExecutorMap.keySet();
        for (String threadPoolExecutorKey : threadPoolExecutorKeys) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolExecutorKey);
            int poolSize = threadPoolExecutor.getPoolSize();
            int corePoolSize = threadPoolExecutor.getCorePoolSize();
            BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
            String simpleName = queue.getClass().getSimpleName();
        }

        logger.info("Thread pool info: {}", JSON.toJSONString(threadPoolExecutorKeys));

        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);

    }

    @Bean
    public ThreadPoolReportJob threadPoolReportJob(IRegistry registry, IDynamicThreadPoolService dynamicThreadPoolService) {
        return new ThreadPoolReportJob(registry, dynamicThreadPoolService);
    }
}

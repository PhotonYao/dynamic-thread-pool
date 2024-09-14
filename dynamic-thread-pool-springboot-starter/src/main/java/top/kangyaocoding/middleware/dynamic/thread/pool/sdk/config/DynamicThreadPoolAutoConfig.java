package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config;

import com.alibaba.fastjson2.JSON;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述:
 *
 * @author K·Herbert
 * @since 2024-09-13 20:28
 */
@Configuration
public class DynamicThreadPoolAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);

    @Bean("dynamicThreadPoolAService")
    public DynamicThreadPoolService dynamicThreadPoolAService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");

        if (StringUtil.isBlank(applicationName)) {
            logger.warn("应用程序名称为 null。您确定已配置应用程序吗？");
        }
        Set<String> threadPoolExecutorKeys = threadPoolExecutorMap.keySet();
        for (String threadPoolExecutorKey : threadPoolExecutorKeys){
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolExecutorKey);
            int poolSize = threadPoolExecutor.getPoolSize();
            int corePoolSize = threadPoolExecutor.getCorePoolSize();
            BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
            String simpleName = queue.getClass().getSimpleName();
        }

        logger.info("Thread pool info: {}", JSON.toJSONString(threadPoolExecutorKeys));

        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);

    }
}

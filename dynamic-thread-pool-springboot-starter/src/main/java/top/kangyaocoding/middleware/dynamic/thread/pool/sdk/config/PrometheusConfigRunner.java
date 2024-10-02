package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config;

import com.alibaba.fastjson2.JSON;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class PrometheusConfigRunner implements ApplicationRunner {

    private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Registering ThreadPoolExecutor beans...");
        String[] beanNamesForType = applicationContext.getBeanNamesForType(ThreadPoolExecutor.class);
        log.info("Found {} ThreadPoolExecutor beans", JSON.toJSONString(beanNamesForType) );
        for (String beanName : beanNamesForType) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) applicationContext.getBean(beanName);
            registerThreadPool(
                    applicationContext.getEnvironment().getProperty("spring.application.name"),
                    beanName,
                    executor
            );
        }
    }

    private void registerThreadPool(
            String applicationName,
            String poolName,
            ThreadPoolExecutor executor
    ) {
        List<Tag> tags = Arrays.asList(
                new ImmutableTag("applicationName", applicationName),
                new ImmutableTag("poolName", poolName)
        );
        Metrics.gauge("thread_pool_core_size", tags, executor, ThreadPoolExecutor::getCorePoolSize);
        Metrics.gauge("thread_pool_max_size", tags, executor, ThreadPoolExecutor::getMaximumPoolSize);
        Metrics.gauge("thread_pool_active_thread_count", tags, executor, ThreadPoolExecutor::getActiveCount);
        Metrics.gauge("thread_pool_size", tags, executor, ThreadPoolExecutor::getPoolSize);
        Metrics.gauge("thread_pool_queue_size", tags, executor,
                (threadPoolExecutor) -> threadPoolExecutor.getQueue().size()
        );
        Metrics.gauge("thread_pool_queue_remaining_capacity", tags, executor,
                (threadPoolExecutor) -> threadPoolExecutor.getQueue().remainingCapacity()
        );
    }
}

package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述: 动态线程池服务实现
 *
 * @author K·Herbert
 * @since 2024-09-15 00:36
 */
public class DynamicThreadPoolService implements IDynamicThreadPoolService {

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolService.class);

    private final String applicationName;
    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;

    public DynamicThreadPoolService(String applicationName, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.applicationName = applicationName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    @Override
    public List<ThreadPoolConfigEntity> getThreadPoolConfigList() {

        Set<String> threadPoolExecutorBeanNames = threadPoolExecutorMap.keySet();
        List<ThreadPoolConfigEntity> threadPoolVOS = new ArrayList<>(threadPoolExecutorBeanNames.size());

        for (String beanName : threadPoolExecutorBeanNames) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(beanName);
            ThreadPoolConfigEntity threadPoolVO = new ThreadPoolConfigEntity(applicationName, beanName);
            threadPoolVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
            threadPoolVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
            threadPoolVO.setActiveCount(threadPoolExecutor.getActiveCount());
            threadPoolVO.setPoolSize(threadPoolExecutor.getPoolSize());
            threadPoolVO.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
            threadPoolVO.setQueueSize(threadPoolExecutor.getQueue().size());
            threadPoolVO.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());
            threadPoolVOS.add(threadPoolVO);
        }

        if (logger.isDebugEnabled()) {
            logger.info("动态线程池，配置查询，应用名: {} 池化配置列表: {}", applicationName, JSON.toJSONString(threadPoolVOS));
        }

        return threadPoolVOS;
    }

    @Override
    public ThreadPoolConfigEntity getThreadPoolConfigByName(String threadPoolName) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);

        if (null == threadPoolExecutor) {
            logger.warn("动态线程池，配置查询，线程池不存在，参数: {}", JSON.toJSONString(threadPoolName));
            return new ThreadPoolConfigEntity(applicationName, threadPoolName);
        }

        // 线程池数据封装
        ThreadPoolConfigEntity threadPoolVO = new ThreadPoolConfigEntity(applicationName, threadPoolName);
        threadPoolVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        threadPoolVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        threadPoolVO.setActiveCount(threadPoolExecutor.getActiveCount());
        threadPoolVO.setPoolSize(threadPoolExecutor.getPoolSize());
        threadPoolVO.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
        threadPoolVO.setQueueSize(threadPoolExecutor.getQueue().size());
        threadPoolVO.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());

        if (logger.isDebugEnabled()) {
            logger.info("动态线程池，配置查询，应用名: {} 线程名: {} 池化配置: {}", applicationName, threadPoolName, JSON.toJSONString(threadPoolVO));
        }

        return threadPoolVO;
    }

    @Override
    public boolean updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {

        if (null == threadPoolConfigEntity || !applicationName.equals(threadPoolConfigEntity.getAppName())) {
            logger.warn("动态线程池，配置更新失败，参数不合法，参数: {}", JSON.toJSONString(threadPoolConfigEntity));
            return false;
        }

        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());

        if (null == threadPoolExecutor) {
            logger.warn("动态线程池，配置更新失败，线程池不存在，参数: {}", JSON.toJSONString(threadPoolConfigEntity));
            return false;
        }

        // 设置线程池参数
        threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
        threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());

        logger.info("线程池配置更新完成");

        return true;
    }
}

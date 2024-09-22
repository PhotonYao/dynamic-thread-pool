package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.impl;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.dto.NotifyMessageDTO;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolConfigEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.IDynamicThreadPoolService;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.INotifyService;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述: 动态线程池服务实现
 *
 * @author K·Herbert
 * @since 2024-09-15 00:36
 */
@Slf4j
public class DynamicThreadPoolServiceImpl implements IDynamicThreadPoolService {

    private final String applicationName;
    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;
    private INotifyService notifyService;

    public DynamicThreadPoolServiceImpl(String applicationName, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.applicationName = applicationName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    /**
     * 获取线程池配置列表
     * 本方法通过遍历线程池执行器映射集，读取每个线程池执行器的配置参数，
     * 并将其封装到ThreadPoolConfigEntity对象中，最终形成一个线程池配置列表
     *
     * @return 包含所有线程池配置的列表
     */
    @Override
    public List<ThreadPoolConfigEntity> getThreadPoolConfigList() {

        // 获取所有线程池执行器的名称集合
        Set<String> threadPoolExecutorBeanNames = threadPoolExecutorMap.keySet();
        // 根据线程池执行器数量初始化配置实体列表
        List<ThreadPoolConfigEntity> threadPoolVOS = new ArrayList<>(threadPoolExecutorBeanNames.size());

        // 遍历线程池执行器名称集合
        for (String beanName : threadPoolExecutorBeanNames) {
            // 获取当前名称对应的线程池执行器实例
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(beanName);
            // 创建线程池配置实体，用于封装线程池信息
            ThreadPoolConfigEntity threadPoolVO = new ThreadPoolConfigEntity(applicationName, beanName);

            // 将线程池的各项配置参数封装到配置实体中
            threadPoolVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
            threadPoolVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
            threadPoolVO.setActiveCount(threadPoolExecutor.getActiveCount());
            threadPoolVO.setPoolSize(threadPoolExecutor.getPoolSize());
            threadPoolVO.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
            threadPoolVO.setQueueSize(threadPoolExecutor.getQueue().size());
            threadPoolVO.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());

            // 将封装好的配置实体添加到列表中
            threadPoolVOS.add(threadPoolVO);
        }

        log.info("动态线程池，配置查询，应用名: {} 池化配置列表: {}", applicationName, JSON.toJSONString(threadPoolVOS));

        // 返回线程池配置列表
        return threadPoolVOS;
    }

    /**
     * 根据线程池名称获取线程池配置信息
     * 此方法用于查询动态线程池中特定线程池的配置详情如果线程池不存在，则返回一个包含基本配置信息的新ThreadPoolConfigEntity对象
     * 对于存在的线程池，此方法收集并返回其核心池大小、最大池大小、当前活跃线程数、当前线程池大小、队列类型、队列大小和队列剩余容量等详细信息
     *
     * @param threadPoolName 线程池名称，用于标识目标线程池
     * @return ThreadPoolConfigEntity包含线程池配置信息的对象如果线程池不存在，则部分信息可能为空或默认值
     */
    @Override
    public ThreadPoolConfigEntity getThreadPoolConfigByName(String threadPoolName) {
        // 根据线程池名称从映射中获取对应的ThreadPoolExecutor实例
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);

        // 检查获取的线程池实例是否为null
        if (null == threadPoolExecutor) {
            // 如果线程池不存在，记录警告日志
            log.warn("动态线程池，配置查询，线程池不存在，参数: {}", JSON.toJSONString(threadPoolName));
            // 返回一个新的ThreadPoolConfigEntity对象，包含应用名和线程池名，其他信息为空或默认值
            return new ThreadPoolConfigEntity(applicationName, threadPoolName);
        }

        // 对于存在的线程池，创建一个ThreadPoolConfigEntity对象来封装线程池的数据
        ThreadPoolConfigEntity threadPoolVO = new ThreadPoolConfigEntity(applicationName, threadPoolName);
        // 设置线程池的核心池大小
        threadPoolVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        // 设置线程池的最大池大小
        threadPoolVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        // 设置线程池的当前活跃线程数
        threadPoolVO.setActiveCount(threadPoolExecutor.getActiveCount());
        // 设置线程池的当前池大小
        threadPoolVO.setPoolSize(threadPoolExecutor.getPoolSize());
        // 设置线程池的队列类型
        threadPoolVO.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
        // 设置线程池的队列大小
        threadPoolVO.setQueueSize(threadPoolExecutor.getQueue().size());
        // 设置线程池的队列剩余容量
        threadPoolVO.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());

        log.info("动态线程池，配置查询，应用名: {} 线程名: {} 池化配置: {}", applicationName, threadPoolName, JSON.toJSONString(threadPoolVO));

        // 返回封装后的线程池配置信息对象
        return threadPoolVO;
    }


    /**
     * 动态更新线程池配置
     *
     * @param threadPoolConfigEntity 新的线程池配置实体类
     * @return 如果更新成功则返回true，否则返回false
     */
    @Override
    public boolean updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {

        // 参数校验
        if (threadPoolConfigEntity == null) {
            log.warn("动态线程池，配置更新失败，参数为空");
            return false;
        }

        if (!Objects.equals(applicationName, threadPoolConfigEntity.getAppName())) {
            log.warn("动态线程池，配置更新失败，应用名称不匹配: {}", threadPoolConfigEntity.getAppName());
            return false;
        }

        if (threadPoolConfigEntity.getCorePoolSize() <= 0 || threadPoolConfigEntity.getMaximumPoolSize() <= 0) {
            log.warn("动态线程池，配置更新失败，线程池大小配置不合法: {}", JSON.toJSONString(threadPoolConfigEntity));
            return false;
        }

        if (threadPoolConfigEntity.getCorePoolSize() > threadPoolConfigEntity.getMaximumPoolSize()) {
            log.warn("动态线程池，配置更新失败，核心线程数不能大于最大线程数: {}", JSON.toJSONString(threadPoolConfigEntity));
            return false;
        }

        // 从线程池映射中获取线程池实例
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());

        if (threadPoolExecutor == null) {
            log.warn("动态线程池，配置更新失败，线程池 [{}] 不存在", threadPoolConfigEntity.getThreadPoolName());
            return false;
        }

        try {
            // 更新线程池参数
            synchronized (threadPoolExecutor) {
                threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
                threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
            }

            // 构建更新配置通知
            NotifyMessageDTO messageDTO = new NotifyMessageDTO();
            messageDTO.addParameter("应用名称: ", applicationName)
                    .addParameter("线程池名称: ", threadPoolConfigEntity.getThreadPoolName())
                    .addParameter("核心线程数: ", threadPoolConfigEntity.getCorePoolSize())
                    .addParameter("最大线程数: ", threadPoolConfigEntity.getMaximumPoolSize());
            notifyService.sendNotify(messageDTO);

            // 更新成功日志
            log.info("线程池配置更新完成: {}", JSON.toJSONString(threadPoolConfigEntity));
            return true;
        } catch (Exception e) {
            log.error("动态线程池，配置更新失败，异常信息: {}", JSON.toJSONString(threadPoolConfigEntity), e);
            return false;
        }
    }

}

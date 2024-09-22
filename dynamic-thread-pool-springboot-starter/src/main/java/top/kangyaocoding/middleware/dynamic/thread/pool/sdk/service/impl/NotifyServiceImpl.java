package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.impl;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config.DynamicThreadPoolNotifyAutoProperties;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.dto.NotifyMessageDTO;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolConfigEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.notify.INotifyStrategy;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.INotifyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述: 告警服务实现
 *
 * @author K·Herbert
 * @since 2024-09-21 23:44
 */
@Slf4j
public class NotifyServiceImpl implements INotifyService {

    private final Map<String, INotifyStrategy> strategies;
    private final DynamicThreadPoolNotifyAutoProperties properties;
    private final RedissonClient redissonClient;

    public NotifyServiceImpl(DynamicThreadPoolNotifyAutoProperties properties, RedissonClient redissonClient, List<INotifyStrategy> strategyList) {
        this.properties = properties;
        this.redissonClient = redissonClient;
        strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getClass().getSimpleName(),
                        strategy -> strategy));
    }

    @Override
    public void sendNotify(NotifyMessageDTO notifyMsg) {
        Boolean enabled = properties.getEnabled();
        List<String> platform = properties.getUsePlatform();
        if (!enabled || platform.isEmpty()) {
            log.info("告警服务未启用");
            return;
        }

        for (String platformName : platform) {
            INotifyStrategy strategy = strategies.get(platformName);
            if (strategy == null) {
                log.warn("告警平台: {} 不存在", platformName);
                continue;
            }
            try {
                strategy.sendNotify(notifyMsg);
            } catch (Exception e) {
                log.error("告警平台: {} 发送告警失败", platformName, e);
            }
        }

        log.info("推送告警: {}", JSON.toJSONString(notifyMsg));
    }

    private void sendNotifyWithRateLimit(NotifyMessageDTO notifyMsg) {
        String appName = notifyMsg.getParameters().get("应用名称: ");
        String lockKey = "notifyLock:" + appName;
        String counterKey = "notifyCounter:" + appName;
        String timestampKey = "notifyTimestamp:" + appName;

        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock();

            RBucket<Integer> counterBucket = redissonClient.getBucket(counterKey);
            RBucket<Long> timestampBucket = redissonClient.getBucket(timestampKey);

            int counter = counterBucket.get() == null ? 0 : counterBucket.get();
            long lastNotifyTime = timestampBucket.get() == null ? 0 : timestampBucket.get();

            long currentTime = System.currentTimeMillis();
            long fiveMinutes = 5 * 60 * 1000;
            long twoHours = 2 * 60 * 1000;

            if (currentTime - lastNotifyTime < fiveMinutes) {
                log.info("告警间隔未到5分钟，跳过本次告警");
                return;
            }

            if (counter >= 3 && currentTime - lastNotifyTime < twoHours) {
                log.info("连续告警超过3次且未到2小时，跳过本次告警");
                return;
            }

            if (currentTime - lastNotifyTime >= twoHours) {
                counter = 0;
            }

            sendNotify(notifyMsg);

            counter++;
            counterBucket.set(counter);
            timestampBucket.set(currentTime);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void sendIfThreadPoolHasDanger(List<ThreadPoolConfigEntity> pools) {
        List<ThreadPoolConfigEntity> dangerPools = new ArrayList<>();
        // 遍历线程池列表
        for (ThreadPoolConfigEntity pool : pools) {
            // 获取线程池当前状态
            int corePoolSize = pool.getCorePoolSize();
            int maximumPoolSize = pool.getMaximumPoolSize();
            int activeCount = pool.getActiveCount();
            int queueSize = pool.getQueueSize();
            int remainingCapacity = pool.getRemainingCapacity();

            // 定义危险条件
            boolean isThreadOverload = activeCount > corePoolSize * 0.8;   // 活跃线程数超过核心线程数的80%
            boolean isMaxThreadOverload = activeCount >= maximumPoolSize;  // 活跃线程数已经达到最大线程数
            boolean isQueueAlmostFull = remainingCapacity <= 0 && queueSize > 0; // 队列已满且还有任务在排队

            // 创建一个变量来存储触发的告警条件
            StringBuilder dangerReason = new StringBuilder();

            // 检查并记录触发的告警条件
            if (isThreadOverload) {
                dangerReason.append("活跃线程数超过核心线程数的80%；");
            }
            if (isMaxThreadOverload) {
                dangerReason.append("活跃线程数已经达到最大线程数；");
            }
            if (isQueueAlmostFull) {
                dangerReason.append("队列已满且还有任务在排队；");
            }

            // 如果满足任何一个危险条件，则将此线程池添加到危险列表中
            if (!dangerReason.isEmpty()) {
                pool.setAlarmReason(dangerReason.toString());
                dangerPools.add(pool);
            }
        }

        if (dangerPools.isEmpty()) {
            return;
        }

        NotifyMessageDTO notifyMessageDTO = new NotifyMessageDTO();
        notifyMessageDTO.setMessage("线程池告警!");
        notifyMessageDTO.addParameter("超出线程池处理能力", dangerPools.size());
        dangerPools.forEach(pool -> notifyMessageDTO
                .addParameter("告警原因:", pool.getAlarmReason())
                .addParameter("======", "======")
                .addParameter("应用名称: ", pool.getAppName())
                .addParameter("线程池名称: ", pool.getThreadPoolName())
                .addParameter("当前线程数: ", pool.getPoolSize())
                .addParameter("核心线程数: ", pool.getCorePoolSize())
                .addParameter("最大线程数: ", pool.getMaximumPoolSize())
                .addParameter("活跃线程数: ", pool.getActiveCount())
                .addParameter("队列类型: ", pool.getQueueType())
                .addParameter("队列中任务数: ", pool.getQueueSize())
                .addParameter("队列剩余容量: ", pool.getRemainingCapacity())
        );
        // 发送告警
        sendNotifyWithRateLimit(notifyMessageDTO);
        // 打印告警信息
        log.warn("线程池告警信息: {}", JSON.toJSONString(notifyMessageDTO));
    }
}


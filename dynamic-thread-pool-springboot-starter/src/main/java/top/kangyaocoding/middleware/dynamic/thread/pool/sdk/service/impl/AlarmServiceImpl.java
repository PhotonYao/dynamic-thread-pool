package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.impl;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.dto.AlarmMessageDTO;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolConfigEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.IAlarmService;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 告警服务实现
 *
 * @author K·Herbert
 * @since 2024-09-21 23:44
 */
@Slf4j
public class AlarmServiceImpl implements IAlarmService {

    private final RedissonClient redissonClient;

    public AlarmServiceImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void sendAlarm(AlarmMessageDTO alarmMsg) {
        // 实现发送告警的逻辑，比如发送到某个监控系统、钉钉、邮件等
        log.warn("发送告警: {}", JSON.toJSONString(alarmMsg));
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

        AlarmMessageDTO alarmMessageDTO = new AlarmMessageDTO();
        alarmMessageDTO.setMessage("线程池告警!");
        alarmMessageDTO.addParameter("超出线程池处理能力", dangerPools.size());
        dangerPools.forEach(pool -> alarmMessageDTO
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
        sendAlarm(alarmMessageDTO);
        // 打印告警信息
        log.warn("线程池告警信息: {}", JSON.toJSONString(alarmMessageDTO));
    }
}


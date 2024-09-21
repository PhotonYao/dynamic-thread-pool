package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service;

import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.dto.AlarmMessageDTO;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * 描述: 告警服务接口
 *
 * @author K·Herbert
 * @since 2024-09-21 23:08
 */
public interface IAlarmService {
    void sendAlarm(AlarmMessageDTO alarmMsg);
    void sendIfThreadPoolHasDanger(List<ThreadPoolConfigEntity> pools);
}

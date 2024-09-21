package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.impl;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolConfigEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.enums.DynamicThreadPoolEnum;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.enums.ResponseEnum;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.IDynamicThreadPoolService;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.model.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 动态线程池服务实现
 *
 * @author K·Herbert
 * @since 2024-09-21 13:31
 */

@Slf4j
@Service
public class DynamicThreadPoolService implements IDynamicThreadPoolService {

    private final RedissonClient redissonClient;

    public DynamicThreadPoolService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Response<List<ThreadPoolConfigEntity>> queryThreadPoolList() {
        try {
            // 获取 Hash 存储的线程池配置信息
            RMap<String, ThreadPoolConfigEntity> cacheMap = redissonClient
                    .getMap(DynamicThreadPoolEnum.THREAD_POOL_CONFIG_LIST_KEY.getCode());

            // 获取 Map 中的所有值，并转为 List
            List<ThreadPoolConfigEntity> configList = new ArrayList<>(cacheMap.values());

            return Response.<List<ThreadPoolConfigEntity>>builder()
                    .code(ResponseEnum.SUCCESS.getCode())
                    .info(ResponseEnum.SUCCESS.getInfo())
                    .data(configList)
                    .build();
        } catch (Exception e) {
            log.error("查询线程池数据异常", e);
            return Response.<List<ThreadPoolConfigEntity>>builder()
                    .code(ResponseEnum.UN_ERROR.getCode())
                    .info(ResponseEnum.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    public Response<ThreadPoolConfigEntity> queryThreadPoolConfig(String appName, String threadPoolName) {

        if (appName == null || threadPoolName == null) {
            log.error("请求参数为空");
            return Response.<ThreadPoolConfigEntity>builder()
                    .code(ResponseEnum.ILLEGAL_PARAMETER.getCode())
                    .info(ResponseEnum.ILLEGAL_PARAMETER.getInfo())
                    .build();
        }

        try {
            String cacheKey = DynamicThreadPoolEnum.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getCode()
                    + "_" + appName + "_" + threadPoolName;
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(cacheKey).get();

            return Response.<ThreadPoolConfigEntity>builder()
                    .code(ResponseEnum.SUCCESS.getCode())
                    .info(ResponseEnum.SUCCESS.getInfo())
                    .data(threadPoolConfigEntity)
                    .build();
        } catch (Exception e) {
            log.error("查询线程池配置异常", e);
            return Response.<ThreadPoolConfigEntity>builder()
                    .code(ResponseEnum.UN_ERROR.getCode())
                    .info(ResponseEnum.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    public Response<Boolean> updateThreadPoolConfig(ThreadPoolConfigEntity request) {

        if (request == null || request.getAppName() == null || request.getThreadPoolName() == null) {
            log.error("请求参数不完整：{}", JSON.toJSONString(request));
            return Response.<Boolean>builder()
                    .code(ResponseEnum.ILLEGAL_PARAMETER.getCode())
                    .info(ResponseEnum.ILLEGAL_PARAMETER.getInfo())
                    .data(false)
                    .build();
        }

        try {
            log.info("修改线程池配置开始 {} {} {}", request.getAppName(), request.getThreadPoolName(), JSON.toJSONString(request));
            // 获取发布消息的 Topic
            RTopic topic = redissonClient.getTopic(DynamicThreadPoolEnum.DYNAMIC_THREAD_POOL_REDIS_TOPIC.getCode()
                    + "_" + request.getAppName());
            // 发布消息
            topic.publish(request);

            log.info("修改线程池配置完成 {} {}", request.getAppName(), request.getThreadPoolName());

            return Response.<Boolean>builder()
                    .code(ResponseEnum.SUCCESS.getCode())
                    .info(ResponseEnum.SUCCESS.getInfo())
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("修改线程池配置异常 {}", JSON.toJSONString(request), e);
            return Response.<Boolean>builder()
                    .code(ResponseEnum.UN_ERROR.getCode())
                    .info(ResponseEnum.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }
}

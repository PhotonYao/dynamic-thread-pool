package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.tigger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.entity.ThreadPoolConfigEntity;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.IDynamicThreadPoolService;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.model.Response;

import java.util.List;

/**
 * 描述: 动态线程池控制中心
 *
 * @author K·Herbert
 * @since 2024-09-15 14:24
 */
@Slf4j
@RestController()
@CrossOrigin(origins = "${app.config.cross-origin}", allowedHeaders = "Authorization,Content-Type")
@RequestMapping("/api/${app.config.api-version}/dynamic/thread/pool/")
public class DynamicThreadPoolController {

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    public DynamicThreadPoolController(IDynamicThreadPoolService dynamicThreadPoolService) {
        this.dynamicThreadPoolService = dynamicThreadPoolService;
    }

    /**
     * 查询线程池数据
     * curl --request GET \
     * --url 'http://localhost:8089/api/v1/dynamic/thread/pool/query_thread_pool_list'
     */
    @RequestMapping(value = "query_thread_pool_list", method = RequestMethod.GET)
    public Response<List<ThreadPoolConfigEntity>> queryThreadPoolList() {
        return dynamicThreadPoolService.queryThreadPoolList();
    }

    /**
     * 查询线程池配置
     * curl --request GET \
     * --url 'http://localhost:8089/api/v1/dynamic/thread/pool/query_thread_pool_config?appName=dynamic-thread-pool-test-app&threadPoolName=threadPoolExecutor'
     */
    @RequestMapping(value = "query_thread_pool_config", method = RequestMethod.GET)
    public Response<ThreadPoolConfigEntity> queryThreadPoolConfig(@RequestParam String appName, @RequestParam String threadPoolName) {
        return dynamicThreadPoolService.queryThreadPoolConfig(appName, threadPoolName);
    }

    /**
     * 修改线程池配置
     * curl --request POST \
     * --url http://localhost:8089/api/v1/dynamic/thread/pool/update_thread_pool_config \
     * --header 'content-type: application/json' \
     * --data '{
     * "appName":"dynamic-thread-pool-test-app",
     * "threadPoolName": "threadPoolExecutor",
     * "corePoolSize": 1,
     * "maximumPoolSize": 10
     * }'
     */
    @RequestMapping(value = "update_thread_pool_config", method = RequestMethod.POST)
    public Response<Boolean> updateThreadPoolConfig(@RequestBody ThreadPoolConfigEntity request) {
        return dynamicThreadPoolService.updateThreadPoolConfig(request);
    }

    @RequestMapping(value = "get_grafana_url", method = RequestMethod.GET)
    public Response<String> getGrafanaUrl() {
        return dynamicThreadPoolService.getGrafanaUrl();
    }
}

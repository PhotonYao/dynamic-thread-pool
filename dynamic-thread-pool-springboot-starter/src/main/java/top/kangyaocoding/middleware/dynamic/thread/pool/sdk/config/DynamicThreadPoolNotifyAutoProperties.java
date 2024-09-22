package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 描述: 配置动态线程池通知相关属性
 *
 * @author K·Herbert
 * @since 2024-09-22 21:12
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "dynamic.thread.pool.notify", ignoreUnknownFields = true)
public class DynamicThreadPoolNotifyAutoProperties {
    private Boolean enabled = false;
    private List<String> usePlatform;
    private AccessToken accessToken;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccessToken {
        private String dingDing;
        private String feiShu;
    }
}

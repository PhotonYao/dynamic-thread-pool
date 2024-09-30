package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 配置动态线程池通知相关属性
 *
 * @author K·Herbert
 * @since 2024-09-22 21:12
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "dynamic-thread-pool.notify", ignoreUnknownFields = true)
public class DynamicThreadPoolNotifyAutoProperties {
    private Boolean enabled = false;
    private List<String> usePlatform = new ArrayList<>();
    private AccessToken accessToken;
    private Secret secret;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccessToken {
        private String dingDing;
        private String feiShu;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Secret {
        private String dingDing;
        private String feiShu;
    }
}

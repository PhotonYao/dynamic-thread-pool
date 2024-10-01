package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 描述: 线程池上报配置
 *
 * @author K·Herbert
 * @since 2024-10-01 17:18
 */

@ConfigurationProperties(prefix = "dynamic-thread-pool")
public class DynamicThreadPoolReportProperties {

    private Report report;

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public static class Report {
        private String cron;

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }
    }
}

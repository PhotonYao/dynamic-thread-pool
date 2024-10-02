package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.metrics.export.prometheus.PrometheusProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;

/**
 * 描述: prometheus自动配置
 *
 * @author K·Herbert
 * @since 2024-09-30 11:06
 */
@Slf4j
@Configuration
public class PrometheusAutoConfig {
    @Bean
    public PrometheusConfigRunner prometheusConfigRunner(
            ApplicationContext applicationContext,
            WebEndpointProperties webEndpointProperties,
            PrometheusProperties prometheusProperties
    ) {
        webEndpointProperties.getExposure().setInclude(
                new HashSet<>(Arrays.asList(
                        "health",
                        "prometheus"
                ))
        );
        log.info("PrometheusConfigRunner init");
        prometheusProperties.setEnabled(true);

        return new PrometheusConfigRunner(applicationContext);
    }

    @Bean
    public MeterFilter customMeterFilter() {
        return new MeterFilter() {
            @Override
            public MeterFilterReply accept(Meter.Id id) {
                if (id.getName().contains("thread_pool")) {
                    return MeterFilterReply.ACCEPT;
                }
                return MeterFilterReply.DENY;
            }
        };
    }
}

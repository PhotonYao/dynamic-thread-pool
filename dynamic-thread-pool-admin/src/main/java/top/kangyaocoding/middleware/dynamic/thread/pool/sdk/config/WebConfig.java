package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.interceptor.JwtAuthenticationInterceptor;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.utils.JwtTokenUtil;

/**
 * 描述: Web 配置
 *
 * @author K·Herbert
 * @since 2024-09-21 12:44
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public JwtAuthenticationInterceptor jwtAuthenticationInterceptor(JwtTokenUtil jwtTokenUtil) {
        return new JwtAuthenticationInterceptor(jwtTokenUtil);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor(jwtTokenUtil())).excludePathPatterns("/**");
//                .addPathPatterns("/**") // 拦截所有路径
//                .excludePathPatterns("/api/*/user/login"); // 排除某些路径
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }
}

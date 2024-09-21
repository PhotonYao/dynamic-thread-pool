package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.model.enums.ResponseEnum;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.JwtTokenUtil;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtTokenUtil jwtTokenUtil;

    // 构造函数注入 JwtTokenUtil
    public JwtAuthenticationInterceptor(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        // 构建返回对象的工具
        ObjectMapper objectMapper = new ObjectMapper();

        // 检查是否包含 Authorization 头
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // 返回 JSON 格式的响应
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(
                    Response.builder()
                            .code(ResponseEnum.UNAUTHORIZED.getCode())
                            .info(ResponseEnum.UNAUTHORIZED.getInfo())
                            .build()
            ));
            return false;
        }

        // 去掉 'Bearer ' 前缀
        token = token.substring(7);

        // 检查令牌是否过期
        if (jwtTokenUtil.isTokenExpired(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // 返回 JSON 格式的响应
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(
                    Response.builder()
                            .code(ResponseEnum.UNAUTHORIZED.getCode())
                            .info(ResponseEnum.UNAUTHORIZED.getInfo())
                            .build()
            ));
            return false;
        }

        return true; // 令牌有效，继续处理请求
    }
}
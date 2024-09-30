package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.enums.ResponseEnum;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service.IUserService;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.utils.JwtTokenUtil;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.model.Response;

/**
 * 描述: 用户服务
 *
 * @author K·Herbert
 * @since 2024-09-21 11:31
 */

@Slf4j
@Service
public class UserService implements IUserService {
    @Value("${app.config.username}")
    private String USERNAME;

    @Value("${app.config.password}")
    private String PASSWORD;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public Response<String> login(String username, String password) {
        try {
            // 验证用户名密码
            if (USERNAME.equals(username) && PASSWORD.equals(password)) {

                // 生成 token
                String token = jwtTokenUtil.generateToken(username);

                log.info("用户 {} 登录成功，token: {}", username, token);

                return Response.<String>builder()
                        .code(ResponseEnum.SUCCESS.getCode())
                        .info(ResponseEnum.SUCCESS.getInfo())
                        .data(token)
                        .build();
            }
            // 登录失败
            return Response.<String>builder()
                    .code(ResponseEnum.UNAUTHORIZED.getCode())
                    .info(ResponseEnum.UNAUTHORIZED.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("登录时发生未知错误", e);
            return Response.<String>builder()
                    .code(ResponseEnum.UN_ERROR.getCode())
                    .info(ResponseEnum.UN_ERROR.getInfo())
                    .build();
        }
    }
}

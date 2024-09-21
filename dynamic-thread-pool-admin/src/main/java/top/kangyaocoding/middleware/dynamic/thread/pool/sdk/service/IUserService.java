package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.service;

import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.model.Response;

/**
 * 描述: 用户服务接口
 *
 * @author K·Herbert
 * @since 2024-09-21 11:31
 */
public interface IUserService {
    Response<String> login(String username, String password);
}

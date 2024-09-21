package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.domain.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 描述: 登录数据传输对象
 *
 * @author K·Herbert
 * @since 2024-09-21 11:25
 */

@Data
public class LoginFormDTO implements Serializable {
    private String username;
    private String password;
}

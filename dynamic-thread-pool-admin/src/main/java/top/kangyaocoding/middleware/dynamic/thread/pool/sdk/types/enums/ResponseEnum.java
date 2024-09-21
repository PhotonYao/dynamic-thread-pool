package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 描述: 业务响应码
 * 用于定义系统中各种业务操作的响应状态码和信息
 *
 * @author K·Herbert
 * @since 2024-09-21 11:41
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ResponseEnum {

    SUCCESS("0000", "操作成功"),

    UN_ERROR("0001", "操作失败"),

    ILLEGAL_PARAMETER("0002", "非法参数"),

    UNAUTHORIZED("0003", "未授权"),

    FORBIDDEN("0004", "禁止访问"),

    METHOD_NOT_ALLOWED("0006", "方法不允许"),

    NOT_FOUND("0005", "资源未找到"),

    INTERNAL_SERVER_ERROR("0008", "服务器内部错误"),

    SERVICE_UNAVAILABLE("0009", "服务不可用"),
    ;

    private String code;

    private String info;

    /**
     * 通过响应码获取对应的枚举实例。
     *
     * @param code 响应码
     * @return 对应的 ResponseEnum 实例，如果不存在则返回 null
     */
    public static ResponseEnum fromCode(String code) {
        for (ResponseEnum response : ResponseEnum.values()) {
            if (response.getCode().equals(code)) {
                return response;
            }
        }
        return null;
    }
}

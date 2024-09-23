package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 描述: 通知策略枚举
 *
 * @author K·Herbert
 * @since 2024-09-23 22:17
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum NotifyStrategyEnumVO {

    DING_DING("DingDing", "钉钉"),
    FEI_SHU("FeiShu", "飞书");

    private String code;
    private String desc;
}

package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

/**
 * 描述: 告警消息
 *
 * @author K·Herbert
 * @since 2024-09-21 23:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotifyMessageDTO {

    private String message;
    private LinkedHashMap<String, String> parameters = new LinkedHashMap<>();;

    public <T> NotifyMessageDTO addParameter(String k, T v) {
        parameters.put(k, v.toString());
        return this;
    }
}

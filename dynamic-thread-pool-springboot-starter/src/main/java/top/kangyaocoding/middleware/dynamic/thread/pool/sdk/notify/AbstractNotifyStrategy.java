package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.notify;

import com.taobao.api.ApiException;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.dto.NotifyMessageDTO;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * 描述: 抽象通知策略
 *
 * @author K·Herbert
 * @since 2024-09-22 21:25
 */

public abstract class AbstractNotifyStrategy implements INotifyStrategy {

    public abstract String getStrategyName();

    protected String buildMsg(NotifyMessageDTO notifyMsg) {
        StringBuilder content = new StringBuilder();
        HashMap<String, String> parameters = notifyMsg.getParameters();

        content.append("【动态线程池告警】").append("\n").append(notifyMsg.getMessage()).append("\n");
        parameters.forEach(
                (k, v) -> content
                        .append(" ")
                        .append(k)
                        .append(v)
                        .append("\n")
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        content.append("⏰通知时间: ").append(LocalDateTime.now().format(formatter)).append("\n");
        return content.toString();
    }

    // 具体的通知方法由子类实现
    public abstract void sendNotify(NotifyMessageDTO notifyMsg) throws ApiException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException;

}

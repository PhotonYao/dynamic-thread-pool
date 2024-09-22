package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.notify;

import com.taobao.api.ApiException;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.dto.NotifyMessageDTO;

import java.util.HashMap;

/**
 * 描述: 抽象通知策略
 *
 * @author K·Herbert
 * @since 2024-09-22 21:25
 */
public abstract class AbstractNotifyStrategy implements INotifyStrategy {

    // 具体的通知方法由子类实现
    public abstract void sendNotify(NotifyMessageDTO notifyMsg) throws ApiException;

    protected String buildMsg(NotifyMessageDTO notifyMsg) {
        StringBuilder content = new StringBuilder();
        HashMap<String, String> parameters = notifyMsg.getParameters();

        content.append("【动态线程池告警】").append(notifyMsg.getMessage()).append("\n");
        parameters.forEach(
                (k, v) -> content
                        .append(" ")
                        .append(k)
                        .append(": ")
                        .append(v)
                        .append("\n")
        );
        return content.toString();
    }
}

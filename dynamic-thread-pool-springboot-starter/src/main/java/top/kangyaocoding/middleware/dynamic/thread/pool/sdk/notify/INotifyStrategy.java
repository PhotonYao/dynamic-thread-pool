package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.notify;

import com.taobao.api.ApiException;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.dto.NotifyMessageDTO;

/**
 * 描述: 通知策略
 *
 * @author K·Herbert
 * @since 2024-09-22 18:27
 */
public interface INotifyStrategy {
    void sendNotify(NotifyMessageDTO notifyMsg) throws ApiException;
}

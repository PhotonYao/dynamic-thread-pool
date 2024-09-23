package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.notify;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config.DynamicThreadPoolNotifyAutoProperties;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.dto.NotifyMessageDTO;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.vo.NotifyStrategyEnumVO;

/**
 * 描述: 飞书通知
 *
 * @author K·Herbert
 * @since 2024-09-22 21:00
 */
@Slf4j
@Component
public class FeiShuNotifyStrategy extends AbstractNotifyStrategy {

    private final DynamicThreadPoolNotifyAutoProperties notifyProperties;

    public FeiShuNotifyStrategy(DynamicThreadPoolNotifyAutoProperties notifyProperties) {
        this.notifyProperties = notifyProperties;
    }

    @Override
    public void sendNotify(NotifyMessageDTO notifyMsg) {
        log.debug("飞书通知暂未实现。");
    }

    @Override
    public String getStrategyName() {
        return NotifyStrategyEnumVO.FEI_SHU.getCode();
    }
}

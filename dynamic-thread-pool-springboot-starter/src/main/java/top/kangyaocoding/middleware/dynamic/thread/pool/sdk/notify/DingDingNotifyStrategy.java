package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.notify;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config.DynamicThreadPoolNotifyAutoProperties;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.dto.NotifyMessageDTO;

/**
 * 描述: 钉钉通知
 *
 * @author K·Herbert
 * @since 2024-09-22 20:49
 */
@Slf4j
@EnableAsync
public class DingDingNotifyStrategy extends AbstractNotifyStrategy {

    private final DynamicThreadPoolNotifyAutoProperties notifyProperties;

    public DingDingNotifyStrategy(DynamicThreadPoolNotifyAutoProperties notifyProperties) {
        this.notifyProperties = notifyProperties;
    }

    @Override
    public void sendNotify(NotifyMessageDTO notifyMsg) throws ApiException {
        String accessToken = notifyProperties.getAccessToken().getDingDing();

        DefaultDingTalkClient dingTalkClient = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send");
        OapiRobotSendRequest request = new OapiRobotSendRequest();

        // 定义文本消息
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(buildMsg(notifyMsg));

        // 定义通知对象
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setIsAtAll(true);

        // 配置消息类型
        request.setMsgtype("text");
        request.setText(text);
        request.setAt(at);

        // 发送消息
        OapiRobotSendResponse response = dingTalkClient.execute(request, accessToken);

        if (!response.isSuccess()) {
            log.error("钉钉通知失败");
            throw new ApiException(response.getErrcode().toString(), response.getErrmsg());
        }
        log.info("钉钉通知成功");
    }
}

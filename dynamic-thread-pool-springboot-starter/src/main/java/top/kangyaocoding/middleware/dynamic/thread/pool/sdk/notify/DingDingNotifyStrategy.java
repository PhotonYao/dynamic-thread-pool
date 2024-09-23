package top.kangyaocoding.middleware.dynamic.thread.pool.sdk.notify;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.config.DynamicThreadPoolNotifyAutoProperties;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.dto.NotifyMessageDTO;
import top.kangyaocoding.middleware.dynamic.thread.pool.sdk.model.vo.NotifyStrategyEnumVO;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 描述: 钉钉通知
 *
 * @author K·Herbert
 * @since 2024-09-22 20:49
 */
@Slf4j
@Component
public class DingDingNotifyStrategy extends AbstractNotifyStrategy {

    private final DynamicThreadPoolNotifyAutoProperties notifyProperties;

    public DingDingNotifyStrategy(DynamicThreadPoolNotifyAutoProperties notifyProperties) {
        this.notifyProperties = notifyProperties;
    }

    @Override
    public void sendNotify(NotifyMessageDTO notifyMsg) throws ApiException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
        String accessToken = notifyProperties.getAccessToken().getDingDing();
        String secret = notifyProperties.getSecret().getDingDing();

        Long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8);

        //sign字段和timestamp字段必须拼接到请求URL上，否则会出现 310000 的错误信息
        DingTalkClient dingTalkClient = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?sign=" + sign + "&timestamp=" + timestamp);
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

    @Override
    public String getStrategyName() {
        return NotifyStrategyEnumVO.DING_DING.getCode();
    }
}

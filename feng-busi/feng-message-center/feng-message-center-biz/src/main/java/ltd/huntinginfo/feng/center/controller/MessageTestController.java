package ltd.huntinginfo.feng.center.controller;

import lombok.RequiredArgsConstructor;
import ltd.huntinginfo.feng.center.strategy.IMqProducer;
import ltd.huntinginfo.feng.common.core.util.R;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class MessageTestController {

    // 这里注入的接口，会根据 YML 配置自动注入 Rabbit 或 Kafka 实现
    private final IMqProducer mqProducer;

    @PostMapping("/send")
    public R<String> sendMessage(@RequestBody Map<String, Object> msg) {
        // 模拟向 "feng_notice_topic" 发送消息
        mqProducer.sendMsg("feng_notice_topic", msg);
        return R.ok("发送成功");
    }
}

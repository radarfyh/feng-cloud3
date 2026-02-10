package ltd.huntinginfo.feng.agent.api.entity;

import lombok.Data;

/**
 * 消息接收请求
 */
@Data
public class MessageReceiveRequest {
    private String ybid;               // 查询ID
    private String ztbm;               // 消息主题编码
}

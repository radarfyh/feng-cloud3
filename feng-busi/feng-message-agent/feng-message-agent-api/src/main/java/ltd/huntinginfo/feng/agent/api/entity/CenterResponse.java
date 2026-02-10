package ltd.huntinginfo.feng.agent.api.entity;

import lombok.Data;

import java.util.List;

/**
 * 部级消息中心统一响应
 */
@Data
public class CenterResponse<T> {
    private String status;      // 状态码
    private String message;     // 状态消息
    private T data;             // 响应数据
}


package ltd.huntinginfo.feng.center.service.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.center.service.UmpMsgMainService;
import ltd.huntinginfo.feng.center.service.UmpMsgQueueService;
import ltd.huntinginfo.feng.center.service.processor.MessageQueueTaskProcessor;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 消息队列定时任务处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageQueueSchedule {
    
    private final MessageQueueTaskProcessor messageQueueTaskProcessor;
    private final UmpMsgMainService umpMsgMainService;
    private final UmpMsgQueueService umpMsgQueueService;
    
    /**
     * 处理分发队列任务（每10秒执行一次）
     */
    @Scheduled(fixedDelay = 10000, initialDelay = 5000)
    public void processDistributeTasks() {
        try {
            messageQueueTaskProcessor.processDistributeTasks(50);
        } catch (Exception e) {
            log.error("处理分发队列任务失败", e);
        }
    }
    
    /**
     * 处理推送队列任务（每30秒执行一次）
     */
    @Scheduled(fixedDelay = 30000, initialDelay = 10000)
    public void processPushTasks() {
        try {
            messageQueueTaskProcessor.processPushTasks(100);
        } catch (Exception e) {
            log.error("处理推送队列任务失败", e);
        }
    }
    
    /**
     * 处理广播分发队列任务（每60秒执行一次）
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 20000)
    public void processBroadcastDistributeTasks() {
        try {
            messageQueueTaskProcessor.processBroadcastDistributeTasks(20);
        } catch (Exception e) {
            log.error("处理广播分发队列任务失败", e);
        }
    }
    
    /**
     * 处理失败任务重试（每5分钟执行一次）
     */
    @Scheduled(fixedDelay = 300000, initialDelay = 30000)
    public void retryFailedTasks() {
        try {
            log.debug("开始处理失败任务重试...");
            
            // 调用队列服务批量重试失败任务
            int retriedCount = messageQueueTaskProcessor.batchRetryFailedTasks(
                100,                // 每次最多重试100个
                5,                  // 重试延迟5分钟
                LocalDateTime.now() // 重试时间基准
            );
            
            if (retriedCount > 0) {
                log.info("失败任务重试完成，已重试 {} 个任务", retriedCount);
            }
        } catch (Exception e) {
            log.error("处理失败任务重试失败", e);
        }
    }
    
    /**
     * 清理过期消息（每天凌晨2点执行）
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredMessages() {
        try {
            log.info("开始清理过期消息...");
            
            int cleanedCount = umpMsgMainService.processExpiredMessages();
            
            log.info("已清理{}条过期消息", cleanedCount);
            
        } catch (Exception e) {
            log.error("清理过期消息任务失败", e);
        }
    }
}

-- =============================================
-- 描述：统一消息平台数据库脚本
-- MySQL版本：8.4.7
-- 创建时间：2026年1月
-- =============================================

-- ----------------------------
-- 1. 数据库创建
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `unified_message_platform` 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE `unified_message_platform`;

-- ----------------------------
-- 2. 应用管理相关表
-- ----------------------------

-- 应用认证凭证表
-- 作用：管理所有接入统一消息平台的业务系统，包括直接接入和通过代理平台接入的应用
-- 关键业务逻辑：
-- 1. 应用通过app_key和app_secret进行身份认证
-- 2. 支持配置不同的推送模式（主动推送/等待轮询）
-- 3. 支持IP白名单限制，增强安全性
-- 4. 支持速率限制，防止恶意请求
DROP TABLE IF EXISTS `ump_app_credential`;
CREATE TABLE `ump_app_credential` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `app_key` varchar(64) NOT NULL COMMENT '应用唯一标识',
  `app_secret` varchar(256) NOT NULL COMMENT 'AES加密后的应用密钥',
  `app_name` varchar(100) NOT NULL COMMENT '应用名称',
  `app_type` varchar(20) NOT NULL DEFAULT 'DIRECT' COMMENT '应用类型:DIRECT-直接接入 AGENT-代理接入',
  
  -- 应用信息
  `app_desc` varchar(500) DEFAULT NULL COMMENT '应用描述',
  `app_icon` varchar(255) DEFAULT NULL COMMENT '应用图标地址',
  `home_url` varchar(255) DEFAULT NULL COMMENT '应用首页地址',
  
  -- 推送配置
  `default_push_mode` varchar(10) DEFAULT 'PUSH' COMMENT '默认推送方式:PUSH-推送 POLL-轮询',
  `callback_url` varchar(500) DEFAULT NULL COMMENT '默认回调地址',
  `callback_auth_mode` varchar(20) DEFAULT 'SIGNATURE' COMMENT '回调认证模式',
  
  -- 限制配置
  `rate_limit` int DEFAULT 1000 COMMENT 'API调用速率限制(次/分钟)',
  `max_msg_size` int DEFAULT 1048576 COMMENT '最大消息大小(字节)',
  `ip_whitelist` json DEFAULT NULL COMMENT 'IP白名单(JSON数组)',
  
  -- 状态
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
  `secret_expire_time` datetime DEFAULT NULL COMMENT '密钥过期时间',
  
  -- 系统审计字段
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_key` (`app_key`),
  KEY `idx_app_type` (`app_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='应用认证凭证表 - 管理所有接入的业务系统';

-- 应用权限表
-- 作用：管理应用的API访问权限，实现细粒度的权限控制
-- 关键业务逻辑：
-- 1. 控制应用可以访问哪些API资源
-- 2. 支持不同操作权限（读、写、全部）
-- 3. 权限与角色绑定，便于批量管理
DROP TABLE IF EXISTS `ump_app_permission`;
CREATE TABLE `ump_app_permission` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `app_key` varchar(64) NOT NULL COMMENT '应用标识',
  `resource_code` varchar(64) NOT NULL COMMENT '资源标识符',
  `resource_name` varchar(100) DEFAULT NULL COMMENT '资源描述',
  `operation` varchar(20) DEFAULT '*' COMMENT '操作:*所有 READ读 WRITE写',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
  
  -- 系统审计字段
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_resource` (`app_key`, `resource_code`),
  KEY `idx_resource_code` (`resource_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='应用权限表 - 管理API访问权限';

-- ----------------------------
-- 3. 消息核心表
-- ----------------------------

-- 消息主表
-- 作用：存储所有消息的核心元数据，是消息系统的中央存储
-- 关键业务逻辑：
-- 1. 所有消息的统一入口，支持直接消息和代理消息
-- 2. 消息状态流转：RECEIVED → DISTRIBUTING → DISTRIBUTED → SENT → READ
-- 3. 支持消息优先级，高优先级消息优先处理
-- 4. 支持消息过期，过期消息自动清理
-- 5. 支持代理消息的关联追踪
DROP TABLE IF EXISTS `ump_msg_main`;
CREATE TABLE `ump_msg_main` (
  `id` varchar(32) NOT NULL COMMENT '消息ID(UUID)',
  `msg_code` varchar(50) DEFAULT NULL COMMENT '消息编码(xxbm)',
  `msg_type` varchar(20) NOT NULL DEFAULT 'NOTICE' COMMENT '消息类型:NOTICE-通知 ALERT-提醒 BIZ-业务 AGENT-代理',
  
  -- 消息内容
  `title` varchar(200) DEFAULT NULL COMMENT '消息标题',
  `content` json NOT NULL COMMENT '消息内容(JSON格式)',
  `priority` tinyint DEFAULT 3 COMMENT '优先级1-5,数字越小优先级越高',
  
  -- 发送方信息
  `sender_app_key` varchar(64) NOT NULL COMMENT '发送应用标识',
  `sender_type` varchar(20) NOT NULL DEFAULT 'APP' COMMENT '发送者类型:APP-应用 USER-用户 SYSTEM-系统',
  `sender_id` varchar(64) DEFAULT NULL COMMENT '发送者ID',
  `sender_name` varchar(100) DEFAULT NULL COMMENT '发送者名称',
  `sender_org_code` varchar(50) DEFAULT NULL COMMENT '发送单位代码',
  `sender_org_name` varchar(100) DEFAULT NULL COMMENT '发送单位名称',
  
  -- 代理相关信息
  `agent_msg_id` varchar(32) DEFAULT NULL COMMENT '代理消息ID',
  `agent_app_key` varchar(64) DEFAULT NULL COMMENT '代理平台标识',
  
  -- 接收者范围
  `receiver_count` int DEFAULT 1 COMMENT '接收者数量',
  `receiver_type` varchar(20) DEFAULT 'USER' COMMENT '接收者类型:USER-个人 DEPT-部门 ORG-组织 AREA-区域 ALL-全体',
  `receiver_scope` json DEFAULT NULL COMMENT '接收者范围配置(JSON)',
  
  -- 推送配置
  `callback_url` varchar(500) DEFAULT NULL COMMENT '回调地址',
  `push_mode` varchar(10) DEFAULT 'PUSH' COMMENT '推送方式:PUSH-主动推送 POLL-等待轮询',
  `callback_config` json DEFAULT NULL COMMENT '回调配置(JSON)',
  
  -- 扩展信息
  `ext_params` json DEFAULT NULL COMMENT '扩展参数',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  
  -- 状态和时间
  `status` varchar(20) DEFAULT 'RECEIVED' COMMENT '状态:RECEIVED-已接收 DISTRIBUTING-分发中 DISTRIBUTED-已分发 SENDING-发送中 SENT-已发送 READ-已读 FAILED-失败',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `distribute_time` datetime DEFAULT NULL COMMENT '分发时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  
  -- 统计信息
  `total_receivers` int DEFAULT 0 COMMENT '总接收人数',
  `received_count` int DEFAULT 0 COMMENT '已接收人数',
  `read_count` int DEFAULT 0 COMMENT '已读人数',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_msg_code` (`msg_code`),
  KEY `idx_sender_app` (`sender_app_key`),
  KEY `idx_agent` (`agent_app_key`, `agent_msg_id`),
  KEY `idx_msg_type` (`msg_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_send_time` (`send_time`),
  KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='消息主表 - 存储所有消息的核心元数据，支持消息状态流转和统计'
PARTITION BY RANGE (YEAR(create_time) * 100 + MONTH(create_time))
(
    PARTITION p202501 VALUES LESS THAN (202502),
    PARTITION p202502 VALUES LESS THAN (202503),
    PARTITION p202503 VALUES LESS THAN (202504),
    PARTITION p202504 VALUES LESS THAN (202505),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 收件箱表
-- 作用：存储个人或小范围消息的分发记录，采用写扩散模式
-- 关键业务逻辑：
-- 1. 为每个接收者创建独立的收件箱记录
-- 2. 跟踪消息的接收状态和阅读状态
-- 3. 支持推送重试机制，确保消息可靠送达
-- 4. 提供高效的查询接口，支持按接收者、状态等条件查询
DROP TABLE IF EXISTS `ump_msg_inbox`;
CREATE TABLE `ump_msg_inbox` (
  `id` varchar(32) NOT NULL COMMENT '主键ID(UUID)',
  `msg_id` varchar(32) NOT NULL COMMENT '消息ID',
  `receiver_id` varchar(64) NOT NULL COMMENT '接收者ID',
  `receiver_type` varchar(20) NOT NULL COMMENT '接收者类型:USER/DEPT/ORG/AREA',
  `receiver_name` varchar(100) DEFAULT NULL COMMENT '接收者名称',
  `receiver_org_code` varchar(50) DEFAULT NULL COMMENT '接收者单位代码',
  `receiver_org_name` varchar(100) DEFAULT NULL COMMENT '接收者单位名称',
  
  -- 分发信息
  `distribute_mode` varchar(20) DEFAULT 'INBOX' COMMENT '分发方式:INBOX-收件箱 BROADCAST-广播',
  `distribute_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '分发时间',
  
  -- 接收状态
  `receive_status` varchar(20) DEFAULT 'PENDING' COMMENT '接收状态:PENDING-待接收 RECEIVED-已接收 FAILED-接收失败',
  `receive_time` datetime DEFAULT NULL COMMENT '接收时间',
  `read_status` tinyint DEFAULT 0 COMMENT '阅读状态:0-未读 1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  
  -- 推送信息
  `push_count` int DEFAULT 0 COMMENT '推送次数',
  `last_push_time` datetime DEFAULT NULL COMMENT '最后推送时间',
  `push_status` varchar(20) DEFAULT 'PENDING' COMMENT '推送状态',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_msg_receiver` (`msg_id`, `receiver_id`, `receiver_type`),
  KEY `idx_receiver` (`receiver_id`, `receiver_type`),
  KEY `idx_msg_id` (`msg_id`),
  KEY `idx_receive_status` (`receive_status`),
  KEY `idx_read_status` (`read_status`),
  KEY `idx_distribute_time` (`distribute_time`),
  KEY `idx_push_status` (`push_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='收件箱表 - 存储个人或小范围消息的分发记录，采用写扩散模式';

-- 广播信息筒表
-- 作用：存储广播消息的分发记录，采用读扩散模式，优化大范围消息分发
-- 关键业务逻辑：
-- 1. 广播消息只存储一份，减少存储空间
-- 2. 跟踪广播消息的分发进度和接收统计
-- 3. 支持广播范围动态配置，灵活适应不同业务场景
-- 4. 提供广播消息的接收状态查询接口
DROP TABLE IF EXISTS `ump_msg_broadcast`;
CREATE TABLE `ump_msg_broadcast` (
  `id` varchar(32) NOT NULL COMMENT '广播ID(UUID)',
  `msg_id` varchar(32) NOT NULL COMMENT '消息ID',
  `broadcast_type` varchar(20) NOT NULL COMMENT '广播类型:ALL-全体 DEPT-部门 ORG-组织 AREA-区域 CUSTOM-自定义',
  
  -- 广播范围
  `target_scope` json NOT NULL COMMENT '目标范围配置(JSON)',
  `target_description` varchar(500) DEFAULT NULL COMMENT '目标范围描述',
  
  -- 统计信息
  `total_receivers` int DEFAULT 0 COMMENT '总接收人数',
  `distributed_count` int DEFAULT 0 COMMENT '已分发数量',
  `received_count` int DEFAULT 0 COMMENT '已接收数量',
  `read_count` int DEFAULT 0 COMMENT '已读人数',
  
  -- 状态和时间
  `status` varchar(20) DEFAULT 'DISTRIBUTING' COMMENT '状态:DISTRIBUTING-分发中 COMPLETED-完成 PARTIAL-部分完成',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  
  PRIMARY KEY (`id`),
  KEY `idx_msg_id` (`msg_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='广播信息筒表 - 存储广播消息的分发记录，采用读扩散模式';

-- 广播消息接收记录表
-- 作用：记录重要广播消息的精准送达与阅读状态，用于关键消息审计
-- 关键业务逻辑：
-- 1. 仅当业务方通过“消息已读状态更新服务”接口（回执接口）上报状态时，才插入或更新记录，实现“按需创建”，避免海量数据存储。
-- 2. 采用UPSERT（INSERT ... ON DUPLICATE KEY UPDATE）方式，确保同一接收者对同一广播的状态更新是幂等的。
-- 3. 接收状态 (`receive_status`) 由平台推送逻辑更新，阅读状态 (`read_status`) 由业务系统回执触发更新，两者独立。
-- 4. 记录变更后，将异步触发更新 `ump_msg_broadcast` 表中的汇总统计字段（如 `read_count`），保证数据最终一致性。
-- 分区建议：初期可不分区。当单广播接收者预期超百万或总数据量极大时，可评估启用 `PARTITION BY HASH(broadcast_id)`。
DROP TABLE IF EXISTS `ump_broadcast_receive_record`;
CREATE TABLE `ump_broadcast_receive_record` (
  -- 使用复合主键，契合“广播-接收者”的核心查询模式，避免二级索引回表
  `broadcast_id` varchar(32) NOT NULL COMMENT '广播ID，关联 ump_msg_broadcast.id',
  `receiver_id` varchar(64) NOT NULL COMMENT '接收者ID',
  `receiver_type` varchar(20) NOT NULL DEFAULT 'USER' COMMENT '接收者类型:USER/DEPT/ORG/AREA',
  
  -- 状态信息：区分“接收”与“阅读”两个关键动作
  `receive_status` varchar(20) DEFAULT 'PENDING' COMMENT '接收状态:PENDING-待送达 DELIVERED-已送达 FAILED-送达失败',
  `receive_time` datetime DEFAULT NULL COMMENT '接收/送达时间',
  `read_status` tinyint DEFAULT 0 COMMENT '阅读状态:0-未读 1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  
  -- 系统审计字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后状态更新时间',
  
  -- 核心主键与索引设计
  PRIMARY KEY (`broadcast_id`, `receiver_id`, `receiver_type`), -- 复合主键，覆盖高频查询
  KEY `idx_receiver_read_status` (`receiver_id`, `read_status`, `update_time`) COMMENT '查询用户未读广播或阅读历史',
  KEY `idx_broadcast_status` (`broadcast_id`, `read_status`, `receive_status`) COMMENT '统计特定广播的已读/未读清单'
  
  -- 分区策略：按广播ID哈希，均匀分布数据
  -- PARTITION BY HASH(broadcast_id) PARTITIONS 16 -- 请根据实际数据量评估是否开启
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='广播消息接收记录表 - 记录重要广播消息的精准送达与阅读状态，用于关键消息审计';

-- ----------------------------
-- 4. 消息队列与任务表
-- ----------------------------

-- 消息队列表
-- 作用：存储待处理的异步任务，实现消息的异步处理和削峰填谷
-- 关键业务逻辑：
-- 1. 支持多种队列类型：发送、分发、回调、重试
-- 2. 支持任务优先级，高优先级任务优先处理
-- 3. 支持任务重试机制，失败任务自动重试
-- 4. 提供任务状态跟踪，便于监控和排查问题
DROP TABLE IF EXISTS `ump_msg_queue`;
CREATE TABLE `ump_msg_queue` (
  `id` varchar(32) NOT NULL COMMENT '主键ID(UUID)',
  `queue_type` varchar(20) NOT NULL COMMENT '队列类型:SEND-发送 DISTRIBUTE-分发 CALLBACK-回调 RETRY-重试',
  `queue_name` varchar(100) NOT NULL COMMENT '队列名称',
  `msg_id` varchar(32) NOT NULL COMMENT '消息ID',
  
  -- 任务信息
  `task_data` json NOT NULL COMMENT '任务数据(JSON)',
  `priority` tinyint DEFAULT 5 COMMENT '优先级1-10,数字越小优先级越高',
  `execute_time` datetime NOT NULL COMMENT '执行时间',
  `max_retry` int DEFAULT 3 COMMENT '最大重试次数',
  `current_retry` int DEFAULT 0 COMMENT '当前重试次数',
  
  -- 执行状态
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态:PENDING-等待 PROCESSING-处理中 SUCCESS-成功 FAILED-失败',
  `worker_id` varchar(64) DEFAULT NULL COMMENT '工作者ID',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  
  -- 执行结果
  `result_code` varchar(10) DEFAULT NULL COMMENT '结果代码',
  `result_message` varchar(500) DEFAULT NULL COMMENT '结果消息',
  `error_stack` text DEFAULT NULL COMMENT '错误堆栈',
  
  PRIMARY KEY (`id`),
  KEY `idx_queue_type` (`queue_type`),
  KEY `idx_status` (`status`),
  KEY `idx_execute_time` (`execute_time`),
  KEY `idx_priority` (`priority`),
  KEY `idx_msg_id` (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='消息队列表 - 存储待处理的异步任务，实现消息的异步处理和削峰填谷';

-- 回调记录表
-- 作用：记录所有回调请求的执行情况，确保回调的可靠性和可追溯性
-- 关键业务逻辑：
-- 1. 记录回调请求的详细信息，便于问题排查
-- 2. 支持回调重试机制，失败回调自动重试
-- 3. 提供回调状态监控，及时发现处理失败的回调
-- 4. 支持回调签名验证，确保回调来源的合法性
DROP TABLE IF EXISTS `ump_msg_callback`;
CREATE TABLE `ump_msg_callback` (
  `id` varchar(32) NOT NULL COMMENT '主键ID(UUID)',
  `msg_id` varchar(32) NOT NULL COMMENT '消息ID',
  `receiver_id` varchar(64) NOT NULL COMMENT '接收者ID',
  `callback_url` varchar(500) NOT NULL COMMENT '回调地址',
  `callback_method` varchar(10) DEFAULT 'POST' COMMENT '回调方法',
  
  -- 回调数据
  `callback_data` json NOT NULL COMMENT '回调数据(JSON)',
  `signature` varchar(256) DEFAULT NULL COMMENT '回调签名',
  `callback_id` varchar(64) DEFAULT NULL COMMENT '回调ID',
  
  -- 回调状态
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态:PENDING-等待 PROCESSING-处理中 SUCCESS-成功 FAILED-失败',
  `http_status` int DEFAULT NULL COMMENT 'HTTP状态码',
  `response_body` text DEFAULT NULL COMMENT '响应内容',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  
  -- 时间信息
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `response_time` datetime DEFAULT NULL COMMENT '响应时间',
  `cost_time` int DEFAULT NULL COMMENT '耗时(ms)',
  
  -- 重试信息
  `retry_count` int DEFAULT 0 COMMENT '重试次数',
  `next_retry_time` datetime DEFAULT NULL COMMENT '下次重试时间',
  
  PRIMARY KEY (`id`),
  KEY `idx_msg_id` (`msg_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_next_retry_time` (`next_retry_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='回调记录表 - 记录所有回调请求的执行情况，确保回调的可靠性和可追溯性';

-- ----------------------------
-- 5. 配置与字典表
-- ----------------------------

-- 消息主题表
-- 作用：管理消息主题，用于消息的分类和路由，借鉴消息代理平台的设计
-- 关键业务逻辑：
-- 1. 定义消息主题，便于消息的分类管理
-- 2. 支持主题订阅，业务系统可以订阅感兴趣的主题
-- 3. 主题作为消息路由的依据，实现消息的定向分发
-- 4. 支持主题的启用/禁用，灵活控制消息流转
DROP TABLE IF EXISTS `ump_msg_topic`;
CREATE TABLE `ump_msg_topic` (
  `id` varchar(32) NOT NULL COMMENT '主题ID(UUID)',
  `topic_code` varchar(50) NOT NULL COMMENT '主题代码',
  `topic_name` varchar(100) NOT NULL COMMENT '主题名称',
  `topic_type` varchar(20) DEFAULT 'SYSTEM' COMMENT '主题类型:SYSTEM-系统主题 CUSTOM-自定义主题',
  
  -- 主题配置
  `description` varchar(500) DEFAULT NULL COMMENT '主题描述',
  `default_msg_type` varchar(20) DEFAULT NULL COMMENT '默认消息类型',
  `default_priority` tinyint DEFAULT 3 COMMENT '默认优先级',
  `routing_rules` json DEFAULT NULL COMMENT '路由规则配置(JSON)',
  
  -- 订阅信息
  `subscriber_count` int DEFAULT 0 COMMENT '订阅者数量',
  `max_subscribers` int DEFAULT 1000 COMMENT '最大订阅者数量',
  
  -- 状态
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
  
  -- 审计字段
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_topic_code` (`topic_code`),
  KEY `idx_topic_type` (`topic_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='消息主题表 - 管理消息主题，用于消息的分类和路由';

-- 主题订阅表
-- 作用：记录业务系统对消息主题的订阅关系
-- 关键业务逻辑：
-- 1. 管理业务系统与消息主题的订阅关系
-- 2. 支持订阅配置，如推送方式、回调地址等
-- 3. 提供订阅状态管理，支持启用/禁用订阅
-- 4. 支持订阅统计，便于了解主题的使用情况
DROP TABLE IF EXISTS `ump_topic_subscription`;
CREATE TABLE `ump_topic_subscription` (
  `id` varchar(32) NOT NULL COMMENT '订阅ID(UUID)',
  `topic_code` varchar(50) NOT NULL COMMENT '主题代码',
  `app_key` varchar(64) NOT NULL COMMENT '应用标识',
  
  -- 订阅配置
  `subscription_config` json DEFAULT NULL COMMENT '订阅配置(JSON)',
  `callback_url` varchar(500) DEFAULT NULL COMMENT '回调地址',
  `push_mode` varchar(10) DEFAULT 'PUSH' COMMENT '推送方式:PUSH-推送 POLL-轮询',
  
  -- 订阅状态
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0-取消订阅 1-已订阅',
  `subscribe_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '订阅时间',
  `unsubscribe_time` datetime DEFAULT NULL COMMENT '取消订阅时间',
  
  -- 统计信息
  `message_count` int DEFAULT 0 COMMENT '接收消息数量',
  `last_message_time` datetime DEFAULT NULL COMMENT '最后消息时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_topic_app` (`topic_code`, `app_key`),
  KEY `idx_topic_code` (`topic_code`),
  KEY `idx_app_key` (`app_key`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='主题订阅表 - 记录业务系统对消息主题的订阅关系';

-- 消息状态码表
-- 作用：统一管理系统中所有状态码，便于状态的管理和国际化
-- 关键业务逻辑：
-- 1. 定义标准的状态码和状态描述
-- 2. 支持状态分类，便于按类别查询
-- 3. 支持状态层级关系，便于状态流转
-- 4. 支持状态的多语言描述（预留字段）
DROP TABLE IF EXISTS `ump_status_code`;
CREATE TABLE `ump_status_code` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `status_code` varchar(32) NOT NULL COMMENT '状态码',
  `status_name` varchar(50) NOT NULL COMMENT '状态名称',
  `status_desc` varchar(200) NOT NULL COMMENT '状态描述',
  `category` varchar(20) DEFAULT 'MESSAGE' COMMENT '分类:MESSAGE-消息 CALLBACK-回调 QUEUE-队列',
  `parent_code` varchar(32) DEFAULT NULL COMMENT '父状态码',
  
  -- 排序和显示
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `is_final` tinyint(1) DEFAULT 0 COMMENT '是否为最终状态:0-否 1-是',
  `can_retry` tinyint(1) DEFAULT 1 COMMENT '是否可重试:0-否 1-是',
  
  -- 状态
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
  
  -- 审计字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_status_code` (`status_code`),
  KEY `idx_category` (`category`),
  KEY `idx_parent_code` (`parent_code`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='消息状态码表 - 统一管理系统中所有状态码，便于状态的管理和国际化';

-- 系统配置表
-- 作用：管理系统运行参数，支持动态配置和热更新
-- 关键业务逻辑：
-- 1. 存储系统所有可配置参数
-- 2. 支持不同类型的配置值（字符串、数字、布尔值、JSON）
-- 3. 配置按类别组织，便于管理
-- 4. 支持配置的动态更新，无需重启系统
DROP TABLE IF EXISTS `ump_system_config`;
CREATE TABLE `ump_system_config` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text NOT NULL COMMENT '配置值',
  `config_type` varchar(20) DEFAULT 'STRING' COMMENT '配置类型:STRING/NUMBER/BOOLEAN/JSON',
  `config_desc` varchar(200) DEFAULT NULL COMMENT '配置描述',
  `category` varchar(50) DEFAULT 'COMMON' COMMENT '配置类别',
  
  -- 状态
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
  
  -- 审计字段
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='系统配置表 - 管理系统运行参数，支持动态配置和热更新';

-- 消息模板表
-- 作用：管理消息模板，支持模板化消息发送
-- 关键业务逻辑：
-- 1. 定义消息模板，支持变量替换
-- 2. 支持不同类型的消息模板
-- 3. 模板变量定义，便于模板使用
-- 4. 支持模板的启用/禁用，灵活控制模板使用
DROP TABLE IF EXISTS `ump_msg_template`;
CREATE TABLE `ump_msg_template` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `template_code` varchar(50) NOT NULL COMMENT '模板代码',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `template_type` varchar(20) DEFAULT 'NOTICE' COMMENT '模板类型',
  
  -- 模板内容
  `title_template` varchar(200) NOT NULL COMMENT '标题模板',
  `content_template` text NOT NULL COMMENT '内容模板',
  `variables` json DEFAULT NULL COMMENT '模板变量定义(JSON)',
  
  -- 配置信息
  `default_priority` tinyint DEFAULT 3 COMMENT '默认优先级',
  `default_push_mode` varchar(10) DEFAULT NULL COMMENT '默认推送方式',
  `default_callback_url` varchar(500) DEFAULT NULL COMMENT '默认回调地址',
  
  -- 状态
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
  
  -- 审计字段
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_template_type` (`template_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='消息模板表 - 管理消息模板，支持模板化消息发送';

-- ----------------------------
-- 6. 系统日志表（合并认证日志和操作日志）
-- 评估说明：
-- 1. 认证日志和操作日志在业务上有相似性，都是记录系统行为
-- 2. 两者字段高度重叠，合并可以减少表数量，简化维护
-- 3. 通过log_type字段区分不同类型的日志
-- 4. 通过log_level字段区分日志级别
-- 5. 合并后可以提供统一的日志查询和分析接口
-- 6. 需要考虑性能影响，可以通过分区和索引优化
DROP TABLE IF EXISTS `ump_system_log`;
CREATE TABLE `ump_system_log` (
  `id` varchar(32) NOT NULL COMMENT '日志ID(UUID)',
  `log_type` varchar(20) NOT NULL COMMENT '日志类型:AUTH-认证日志 OPERATION-操作日志 SYSTEM-系统日志',
  `log_level` varchar(10) DEFAULT 'INFO' COMMENT '日志级别:DEBUG/INFO/WARN/ERROR',
  
  -- 应用信息
  `app_key` varchar(64) DEFAULT NULL COMMENT '应用标识',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作者',
  `operation` varchar(50) DEFAULT NULL COMMENT '操作名称',
  
  -- 请求信息
  `request_id` varchar(64) DEFAULT NULL COMMENT '请求ID',
  `api_path` varchar(200) DEFAULT NULL COMMENT 'API路径',
  `http_method` varchar(10) DEFAULT NULL COMMENT 'HTTP方法',
  `request_params` json DEFAULT NULL COMMENT '请求参数',
  
  -- 认证特定字段（当log_type='AUTH'时使用）
  `auth_type` varchar(20) DEFAULT NULL COMMENT '认证类型:APPKEY/TOKEN',
  `auth_status` tinyint(1) DEFAULT NULL COMMENT '认证状态:0-失败 1-成功',
  `auth_error_code` varchar(20) DEFAULT NULL COMMENT '认证错误码',
  
  -- 响应信息
  `response_code` varchar(10) DEFAULT NULL COMMENT '响应代码',
  `response_message` varchar(500) DEFAULT NULL COMMENT '响应消息',
  `response_data` json DEFAULT NULL COMMENT '响应数据',
  
  -- 系统信息
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `server_host` varchar(100) DEFAULT NULL COMMENT '服务器主机',
  
  -- 性能信息
  `cost_time` int DEFAULT NULL COMMENT '耗时(ms)',
  `memory_usage` int DEFAULT NULL COMMENT '内存使用(KB)',
  
  -- 错误信息
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `error_stack` text DEFAULT NULL COMMENT '错误堆栈',
  
  -- 时间
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  PRIMARY KEY (`id`),
  KEY `idx_log_type` (`log_type`),
  KEY `idx_log_level` (`log_level`),
  KEY `idx_app_key` (`app_key`),
  KEY `idx_request_id` (`request_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_auth_status` (`auth_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='系统日志表 - 合并认证日志和操作日志，统一记录系统行为'
PARTITION BY RANGE (YEAR(create_time) * 100 + MONTH(create_time))
(
    PARTITION p202501 VALUES LESS THAN (202502),
    PARTITION p202502 VALUES LESS THAN (202503),
    PARTITION p202503 VALUES LESS THAN (202504),
    PARTITION p202504 VALUES LESS THAN (202505),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- ----------------------------
-- 7. 统计与监控表
-- ----------------------------

-- 消息统计表
-- 作用：按天统计消息发送和接收情况，用于业务分析和监控
-- 关键业务逻辑：
-- 1. 按应用和消息类型统计每日消息量
-- 2. 统计发送成功率、接收率、阅读率等关键指标
-- 3. 统计平均处理时间，监控系统性能
-- 4. 提供历史数据查询，支持趋势分析
DROP TABLE IF EXISTS `ump_msg_statistics`;
CREATE TABLE `ump_msg_statistics` (
  `id` varchar(32) NOT NULL COMMENT '统计ID(UUID)',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `app_key` varchar(64) DEFAULT NULL COMMENT '应用标识',
  `msg_type` varchar(20) DEFAULT NULL COMMENT '消息类型',
  
  -- 发送统计
  `send_count` int DEFAULT 0 COMMENT '发送数量',
  `send_success_count` int DEFAULT 0 COMMENT '发送成功数量',
  `send_failed_count` int DEFAULT 0 COMMENT '发送失败数量',
  
  -- 接收统计
  `receive_count` int DEFAULT 0 COMMENT '接收数量',
  `read_count` int DEFAULT 0 COMMENT '阅读数量',
  
  -- 延迟统计（毫秒）
  `avg_process_time` int DEFAULT 0 COMMENT '平均处理时间',
  `avg_receive_time` int DEFAULT 0 COMMENT '平均接收时间',
  `avg_read_time` int DEFAULT 0 COMMENT '平均阅读时间',
  
  -- 错误统计
  `error_count` int DEFAULT 0 COMMENT '错误数量',
  `retry_count` int DEFAULT 0 COMMENT '重试数量',
  
  -- 审计字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_app_type` (`stat_date`, `app_key`, `msg_type`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_app_key` (`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='消息统计表 - 按天统计消息发送和接收情况，用于业务分析和监控';

-- ----------------------------
-- 8. 视图
-- ----------------------------

-- 消息统计视图
CREATE OR REPLACE VIEW `v_message_statistics` AS
SELECT 
    DATE_FORMAT(m.create_time, '%Y-%m-%d') AS stat_date,
    m.sender_app_key,
    a.app_name,
    m.msg_type,
    COUNT(*) AS total_count,
    SUM(CASE WHEN m.status IN ('SENT', 'READ') THEN 1 ELSE 0 END) AS success_count,
    SUM(CASE WHEN m.status = 'FAILED' THEN 1 ELSE 0 END) AS failed_count,
    AVG(TIMESTAMPDIFF(SECOND, m.create_time, COALESCE(m.complete_time, NOW()))) AS avg_process_seconds
FROM ump_msg_main m
LEFT JOIN ump_app_credential a ON m.sender_app_key = a.app_key AND a.del_flag = '0'
WHERE m.create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY DATE_FORMAT(m.create_time, '%Y-%m-%d'), m.sender_app_key, m.msg_type;

-- 应用消息状态视图
CREATE OR REPLACE VIEW `v_app_message_status` AS
SELECT 
    a.app_key,
    a.app_name,
    a.app_type,
    m.status,
    COUNT(*) AS message_count,
    MAX(m.create_time) AS last_message_time
FROM ump_app_credential a
LEFT JOIN ump_msg_main m ON a.app_key = m.sender_app_key
WHERE a.del_flag = '0'
  AND a.status = 1
  AND m.create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY a.app_key, a.app_name, a.app_type, m.status;

-- 消息分发状态视图
CREATE OR REPLACE VIEW `v_message_distribution_status` AS
SELECT 
    m.id AS message_id,
    m.title,
    m.msg_type,
    m.sender_app_key,
    m.create_time,
    m.status AS message_status,
    COUNT(i.id) AS total_receivers,
    SUM(CASE WHEN i.receive_status = 'RECEIVED' THEN 1 ELSE 0 END) AS received_count,
    SUM(CASE WHEN i.read_status = 1 THEN 1 ELSE 0 END) AS read_count,
    CASE 
        WHEN COUNT(i.id) = 0 THEN 'NO_RECEIVERS'
        WHEN SUM(CASE WHEN i.receive_status = 'RECEIVED' THEN 1 ELSE 0 END) = COUNT(i.id) THEN 'ALL_RECEIVED'
        WHEN SUM(CASE WHEN i.receive_status = 'RECEIVED' THEN 1 ELSE 0 END) = 0 THEN 'NONE_RECEIVED'
        ELSE 'PARTIAL_RECEIVED'
    END AS distribution_status
FROM ump_msg_main m
LEFT JOIN ump_msg_inbox i ON m.id = i.msg_id
WHERE m.create_time >= DATE_SUB(NOW(), INTERVAL 1 DAY)
GROUP BY m.id, m.title, m.msg_type, m.sender_app_key, m.create_time, m.status;

-- 主题订阅统计视图
CREATE OR REPLACE VIEW `v_topic_subscription_stats` AS
SELECT 
    t.topic_code,
    t.topic_name,
    t.topic_type,
    COUNT(s.id) AS subscriber_count,
    SUM(s.message_count) AS total_messages,
    MAX(s.last_message_time) AS last_message_time
FROM ump_msg_topic t
LEFT JOIN ump_topic_subscription s ON t.topic_code = s.topic_code AND s.status = 1
WHERE t.status = 1 AND t.del_flag = '0'
GROUP BY t.topic_code, t.topic_name, t.topic_type;

-- ----------------------------
-- 9. 初始化数据
-- ----------------------------

-- 初始化状态码
INSERT INTO `ump_status_code` (`id`, `status_code`, `status_name`, `status_desc`, `category`, `parent_code`, `sort_order`, `is_final`, `can_retry`) VALUES
-- 消息状态
('1001', 'RECEIVED', '已接收', '消息已接收，待处理', 'MESSAGE', NULL, 1, 0, 1),
('1002', 'VALIDATING', '验证中', '消息验证中', 'MESSAGE', 'RECEIVED', 2, 0, 1),
('1003', 'VALIDATED', '已验证', '消息验证通过', 'MESSAGE', 'VALIDATING', 3, 0, 1),
('1004', 'DISTRIBUTING', '分发中', '消息分发中', 'MESSAGE', 'VALIDATED', 4, 0, 1),
('1005', 'DISTRIBUTED', '已分发', '消息已分发到收件箱', 'MESSAGE', 'DISTRIBUTING', 5, 0, 1),
('1006', 'SENDING', '发送中', '消息发送中', 'MESSAGE', 'DISTRIBUTED', 6, 0, 1),
('1007', 'SENT', '已发送', '消息已发送给接收者', 'MESSAGE', 'SENDING', 7, 0, 1),
('1008', 'READ', '已读', '消息已被阅读', 'MESSAGE', 'SENT', 8, 1, 0),
('1009', 'FAILED', '失败', '消息处理失败', 'MESSAGE', NULL, 9, 1, 1),
('1010', 'EXPIRED', '已过期', '消息已过期', 'MESSAGE', NULL, 10, 1, 0),

-- 回调状态
('2001', 'PENDING', '等待回调', '等待回调', 'CALLBACK', NULL, 1, 0, 1),
('2002', 'PROCESSING', '处理中', '回调处理中', 'CALLBACK', 'PENDING', 2, 0, 1),
('2003', 'SUCCESS', '成功', '回调成功', 'CALLBACK', 'PROCESSING', 3, 1, 0),
('2004', 'FAILED', '失败', '回调失败', 'CALLBACK', 'PROCESSING', 4, 0, 1),
('2005', 'RETRYING', '重试中', '回调重试中', 'CALLBACK', 'FAILED', 5, 0, 1),

-- 队列状态
('3001', 'PENDING', '等待执行', '任务等待执行', 'QUEUE', NULL, 1, 0, 1),
('3002', 'PROCESSING', '执行中', '任务执行中', 'QUEUE', 'PENDING', 2, 0, 1),
('3003', 'SUCCESS', '成功', '任务执行成功', 'QUEUE', 'PROCESSING', 3, 1, 0),
('3004', 'FAILED', '失败', '任务执行失败', 'QUEUE', 'PROCESSING', 4, 0, 1),
('3005', 'CANCELLED', '已取消', '任务已取消', 'QUEUE', NULL, 5, 1, 0);

-- 初始化系统配置
INSERT INTO `ump_system_config` (`id`, `config_key`, `config_value`, `config_type`, `config_desc`, `category`) VALUES
-- 系统配置
('SYS001', 'system.name', '统一消息平台', 'STRING', '系统名称', 'SYSTEM'),
('SYS002', 'system.version', '1.0.0', 'STRING', '系统版本', 'SYSTEM'),

-- 消息配置
('MSG001', 'message.max.size', '1048576', 'NUMBER', '最大消息大小(字节)', 'MESSAGE'),
('MSG002', 'message.expire.days', '30', 'NUMBER', '消息保存天数', 'MESSAGE'),
('MSG003', 'message.broadcast.threshold', '1000', 'NUMBER', '广播阈值(人数)', 'MESSAGE'),

-- 队列配置
('QUE001', 'queue.worker.count', '5', 'NUMBER', '队列工作者数量', 'QUEUE'),
('QUE002', 'queue.batch.size', '100', 'NUMBER', '队列批处理大小', 'QUEUE'),
('QUE003', 'queue.max.retry', '3', 'NUMBER', '最大重试次数', 'QUEUE'),

-- 回调配置
('CALL001', 'callback.timeout', '5000', 'NUMBER', '回调超时时间(毫秒)', 'CALLBACK'),
('CALL002', 'callback.max.retry', '3', 'NUMBER', '回调最大重试次数', 'CALLBACK'),
('CALL003', 'callback.retry.interval', '30000', 'NUMBER', '回调重试间隔(毫秒)', 'CALLBACK'),

-- 性能配置
('PERF001', 'database.pool.size', '20', 'NUMBER', '数据库连接池大小', 'PERFORMANCE'),
('PERF002', 'cache.enabled', 'true', 'BOOLEAN', '是否启用缓存', 'PERFORMANCE'),
('PERF003', 'cache.ttl.seconds', '300', 'NUMBER', '缓存TTL(秒)', 'PERFORMANCE'),

-- 主题配置
('TOPIC001', 'topic.default.max_subscribers', '1000', 'NUMBER', '主题默认最大订阅者数量', 'TOPIC'),
('TOPIC002', 'topic.auto_create', 'true', 'BOOLEAN', '是否自动创建主题', 'TOPIC');

-- 初始化消息主题
INSERT INTO `ump_msg_topic` (`id`, `topic_code`, `topic_name`, `topic_type`, `description`, `default_msg_type`, `default_priority`) VALUES
('TOPIC001', 'SYSTEM_NOTICE', '系统通知', 'SYSTEM', '系统级别的通知消息', 'NOTICE', 2),
('TOPIC002', 'BIZ_ALERT', '业务告警', 'SYSTEM', '业务系统产生的告警消息', 'ALERT', 1),
('TOPIC003', 'USER_MESSAGE', '用户消息', 'SYSTEM', '用户之间的消息', 'BIZ', 3),
('TOPIC004', 'AGENT_FORWARD', '代理转发', 'SYSTEM', '代理平台转发的消息', 'AGENT', 3);

-- 初始化测试应用
INSERT INTO `ump_app_credential` (`id`, `app_key`, `app_secret`, `app_name`, `app_type`, `app_desc`, `status`, `rate_limit`) VALUES
('TEST001', 'TEST_DIRECT_APP', 'TEST_SECRET_123456', '测试直连应用', 'DIRECT', '用于测试的直接接入应用', 1, 1000),
('TEST002', 'TEST_AGENT_APP', 'TEST_SECRET_654321', '测试代理应用', 'AGENT', '用于测试的代理接入应用', 1, 500);

-- 初始化主题订阅
INSERT INTO `ump_topic_subscription` (`id`, `topic_code`, `app_key`, `callback_url`, `push_mode`, `status`) VALUES
('SUB001', 'SYSTEM_NOTICE', 'TEST_DIRECT_APP', 'http://test-direct-app:8080/api/callback', 'PUSH', 1),
('SUB002', 'BIZ_ALERT', 'TEST_DIRECT_APP', 'http://test-direct-app:8080/api/callback', 'PUSH', 1),
('SUB003', 'AGENT_FORWARD', 'TEST_AGENT_APP', 'http://test-agent-app:8080/api/callback', 'POLL', 1);

-- ----------------------------
-- 10. 存储过程和函数
-- ----------------------------

-- 清理过期消息的存储过程
DELIMITER $$
CREATE PROCEDURE `sp_clean_expired_messages`()
BEGIN
    DECLARE expired_count INT DEFAULT 0;
    
    -- 标记过期消息为已过期状态
    UPDATE ump_msg_main 
    SET status = 'EXPIRED'
    WHERE expire_time IS NOT NULL 
      AND expire_time < NOW() 
      AND status NOT IN ('EXPIRED', 'FAILED');
    
    SET expired_count = ROW_COUNT();
    
    -- 记录清理日志
    INSERT INTO ump_system_log 
    (id, log_type, log_level, operation, response_message, create_time)
    VALUES 
    (UUID(), 'SYSTEM', 'INFO', '清理过期消息', 
     CONCAT('清理了 ', expired_count, ' 条过期消息'),
     NOW());
END$$
DELIMITER ;

-- 统计消息的存储过程
DELIMITER $$
CREATE PROCEDURE `sp_generate_message_statistics`()
BEGIN
    -- 删除当天的统计数据
    DELETE FROM ump_msg_statistics WHERE stat_date = CURDATE();
    
    -- 生成新的统计数据
    INSERT INTO ump_msg_statistics 
    (id, stat_date, app_key, msg_type, send_count, send_success_count, send_failed_count,
     receive_count, read_count, avg_process_time, error_count, retry_count)
    SELECT 
        UUID(),
        CURDATE(),
        m.sender_app_key,
        m.msg_type,
        COUNT(*) AS send_count,
        SUM(CASE WHEN m.status IN ('SENT', 'READ') THEN 1 ELSE 0 END) AS send_success_count,
        SUM(CASE WHEN m.status = 'FAILED' THEN 1 ELSE 0 END) AS send_failed_count,
        SUM(CASE WHEN i.receive_status = 'RECEIVED' THEN 1 ELSE 0 END) AS receive_count,
        SUM(CASE WHEN i.read_status = 1 THEN 1 ELSE 0 END) AS read_count,
        AVG(TIMESTAMPDIFF(MILLISECOND, m.create_time, COALESCE(m.complete_time, NOW()))) AS avg_process_time,
        (SELECT COUNT(*) FROM ump_system_log 
         WHERE log_level = 'ERROR' AND DATE(create_time) = CURDATE()) AS error_count,
        (SELECT COUNT(*) FROM ump_msg_queue 
         WHERE current_retry > 0 AND DATE(create_time) = CURDATE()) AS retry_count
    FROM ump_msg_main m
    LEFT JOIN ump_msg_inbox i ON m.id = i.msg_id
    WHERE DATE(m.create_time) = CURDATE()
    GROUP BY m.sender_app_key, m.msg_type;
    
    -- 记录统计日志
    INSERT INTO ump_system_log 
    (id, log_type, log_level, operation, response_message, create_time)
    VALUES 
    (UUID(), 'SYSTEM', 'INFO', '生成消息统计',
     CONCAT('生成了 ', CURDATE(), ' 的消息统计数据'),
     NOW());
END$$
DELIMITER ;

-- 获取消息详情的函数
DELIMITER $$
CREATE FUNCTION `fn_get_message_detail`(p_msg_id VARCHAR(32))
RETURNS TEXT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE result_json TEXT;
    
    SELECT JSON_OBJECT(
        'id', m.id,
        'msg_code', m.msg_code,
        'title', m.title,
        'content', m.content,
        'sender_app_key', m.sender_app_key,
        'sender_name', m.sender_name,
        'status', m.status,
        'create_time', m.create_time,
        'send_time', m.send_time,
        'total_receivers', m.total_receivers,
        'received_count', m.received_count,
        'read_count', m.read_count,
        'receivers', (
            SELECT JSON_ARRAYAGG(
                JSON_OBJECT(
                    'receiver_id', i.receiver_id,
                    'receiver_name', i.receiver_name,
                    'receive_status', i.receive_status,
                    'read_status', i.read_status,
                    'receive_time', i.receive_time,
                    'read_time', i.read_time
                )
            )
            FROM ump_msg_inbox i
            WHERE i.msg_id = m.id
        )
    ) INTO result_json
    FROM ump_msg_main m
    WHERE m.id = p_msg_id;
    
    RETURN result_json;
END$$
DELIMITER ;

-- ----------------------------
-- 11. 定时事件
-- ----------------------------

-- 清理过期消息的事件（每天凌晨1点执行）
DELIMITER $$
CREATE EVENT IF NOT EXISTS `event_clean_expired_messages`
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_DATE + INTERVAL 1 HOUR
DO
BEGIN
    CALL sp_clean_expired_messages();
END$$
DELIMITER ;

-- 生成统计数据的事件（每天凌晨2点执行）
DELIMITER $$
CREATE EVENT IF NOT EXISTS `event_generate_statistics`
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_DATE + INTERVAL 2 HOUR
DO
BEGIN
    CALL sp_generate_message_statistics();
END$$
DELIMITER ;

-- 清理旧日志的事件（每天凌晨3点执行）
DELIMITER $$
CREATE EVENT IF NOT EXISTS `event_clean_old_logs`
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_DATE + INTERVAL 3 HOUR
DO
BEGIN
    -- 删除30天前的系统日志
    DELETE FROM ump_system_log 
    WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
    
    -- 删除7天前的队列记录（成功状态）
    DELETE FROM ump_msg_queue 
    WHERE status = 'SUCCESS' 
      AND create_time < DATE_SUB(NOW(), INTERVAL 7 DAY);
    
    -- 删除30天前的回调记录（成功状态）
    DELETE FROM ump_msg_callback 
    WHERE status = 'SUCCESS' 
      AND create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
END$$
DELIMITER ;

-- ----------------------------
-- 12. 触发器
-- ----------------------------

-- 消息状态更新时更新相关统计
DELIMITER $$
CREATE TRIGGER `trg_msg_main_after_update`
AFTER UPDATE ON `ump_msg_main`
FOR EACH ROW
BEGIN
    -- 当消息状态变为已发送时，更新发送时间
    IF NEW.status = 'SENT' AND OLD.status != 'SENT' THEN
        UPDATE ump_msg_main 
        SET send_time = NOW() 
        WHERE id = NEW.id AND send_time IS NULL;
    END IF;
    
    -- 当消息状态变为完成时，更新完成时间
    IF NEW.status = 'READ' AND OLD.status != 'READ' THEN
        UPDATE ump_msg_main 
        SET complete_time = NOW() 
        WHERE id = NEW.id AND complete_time IS NULL;
    END IF;
END$$
DELIMITER ;

-- 收件箱记录更新时更新消息统计
DELIMITER $$
CREATE TRIGGER `trg_msg_inbox_after_update`
AFTER UPDATE ON `ump_msg_inbox`
FOR EACH ROW
BEGIN
    DECLARE v_received_count INT;
    DECLARE v_read_count INT;
    
    -- 计算已接收和已读数量
    SELECT 
        COUNT(CASE WHEN receive_status = 'RECEIVED' THEN 1 END),
        COUNT(CASE WHEN read_status = 1 THEN 1 END)
    INTO v_received_count, v_read_count
    FROM ump_msg_inbox
    WHERE msg_id = NEW.msg_id;
    
    -- 更新消息主表的统计信息
    UPDATE ump_msg_main 
    SET received_count = v_received_count,
        read_count = v_read_count,
        update_time = NOW()
    WHERE id = NEW.msg_id;
END$$
DELIMITER ;

-- ----------------------------
-- 13. 索引优化（后添加的索引）
-- ----------------------------

-- 为收件箱表添加联合索引
CREATE INDEX `idx_inbox_receiver_status_time` ON `ump_msg_inbox` 
(`receiver_id`, `receiver_type`, `receive_status`, `distribute_time`);

-- 为消息队列表添加状态和时间联合索引
CREATE INDEX `idx_queue_status_time` ON `ump_msg_queue` 
(`status`, `execute_time`, `priority`);

-- 为回调记录表添加状态和时间联合索引
CREATE INDEX `idx_callback_status_time` ON `ump_msg_callback` 
(`status`, `create_time`, `next_retry_time`);

-- 为系统日志表添加复合索引
CREATE INDEX `idx_syslog_type_time` ON `ump_system_log` 
(`log_type`, `create_time`, `log_level`);

-- 为主题订阅表添加状态和时间索引
CREATE INDEX `idx_subscription_status_time` ON `ump_topic_subscription` 
(`status`, `subscribe_time`);

-- ----------------------------
-- 数据库脚本执行完成
-- ----------------------------
SELECT '统一消息平台数据库脚本执行完成' AS `执行结果`;
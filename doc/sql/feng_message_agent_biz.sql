-- =============================================
-- 数据库：feng_message_agent_biz
-- 描述：消息代理平台数据库脚本
-- mysql版本：8.4.7
-- 创建时间：2026-01-29
-- =============================================

-- ----------------------------
-- 1. 应用认证凭证表，用于业务系统认证管理，业务系统标识对应appKey（应用唯一标识），此处业务系统和应用系统等价
-- ----------------------------
DROP TABLE IF EXISTS `msg_app_credential`;
CREATE TABLE `msg_app_credential` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `app_key` varchar(64) NOT NULL COMMENT '应用唯一标识(全局唯一)',
  `app_secret` varchar(256) NOT NULL COMMENT 'AES加密后的应用密钥',
  `app_type` varchar(2) NOT NULL COMMENT '应用类型:01-设备管理平台 02-执法系统 03-标准组件 04-同步录音录像管理子平台 05-远程讯问视音频管理子平台 06-签捺管理子平台 07-生物识别管理子平台...',
  `app_name` varchar(100) NOT NULL COMMENT '应用名称',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
  `agency_code` varchar(100) DEFAULT NULL COMMENT '所属单位编码',
  `zone_code` varchar(100) DEFAULT NULL COMMENT '所属功能区域编码',
  `expire_time` datetime DEFAULT NULL COMMENT '密钥过期时间(NULL表示永久有效)',
  `ip_address` varchar(100) DEFAULT NULL COMMENT '部署IP地址',
  `port` varchar(100) DEFAULT NULL COMMENT '部署端口',
  `place_code` varchar(100) DEFAULT NULL COMMENT '部署地址（所属场所）',
  `ip_whitelist` text DEFAULT NULL COMMENT 'IP白名单(JSON数组格式,空表示不限制)',
  `rate_limit` int DEFAULT 1000 COMMENT 'API调用速率限制(次/分钟)',
  `home_url` varchar(256) DEFAULT NULL COMMENT '应用系统所提供的首页地址，例如：http://data-platform:9002/#/home',
  `app_icon` varchar(256)  DEFAULT NULL COMMENT '应用系统图标地址',
  `app_desc` varchar(1000) NOT NULL COMMENT '应用描述',
  
  `callback_url` VARCHAR(500) DEFAULT NULL COMMENT '消息回调地址',
  `callback_auth_mode` VARCHAR(20) DEFAULT 'standard' COMMENT '回调认证模式:standard/legacy',
  `default_sqssdm` VARCHAR(6) DEFAULT NULL COMMENT '默认申请单位行政区划代码',
  `default_sqdwmc` VARCHAR(200) DEFAULT NULL COMMENT '默认申请单位名称',
  `default_sqrxm` VARCHAR(100) DEFAULT NULL COMMENT '默认申请人姓名',
  `default_sqrzjhm` VARCHAR(18) DEFAULT NULL COMMENT '默认申请人证件号码',
  `default_sqrdh` VARCHAR(50) DEFAULT NULL COMMENT '默认申请人电话',
  `center_token` VARCHAR(600) DEFAULT NULL COMMENT '统一消息中心Token',
  `center_expire_time` DATETIME DEFAULT NULL COMMENT '统一消息中心Token过期时间',
  `app_token` VARCHAR(600) DEFAULT NULL COMMENT '业务系统Token',
  `app_expire_time` DATETIME DEFAULT NULL COMMENT '业务系统Token过期时间',
  
  -- 系统审计字段
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_key` (`app_key`),
  KEY `idx_app_type` (`app_type`),
  KEY `idx_status_expire` (`status`, `expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='应用认证凭证表' 
;

INSERT INTO `msg_app_credential`(`id`, `app_key`, `app_secret`, `app_type`, `app_name`, `status`, `agency_code`, `zone_code`, `expire_time`, `ip_address`, `port`, `place_code`, `ip_whitelist`, `rate_limit`, `home_url`, `app_icon`, `app_desc`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES ('1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p', 'DEVICE_MGMT_PLATFORM', 'DMPe7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2', '01', '设备联网平台', 1, 'AGENCY_001', 'ZONE_001', NULL, '192.168.1.100', '8080', 'PLACE_001', '[\"192.168.1.0/24\", \"10.0.0.1\"]', 5000, 'http://44.103.129.2:5173/', 'http://cdn.example.com/icons/device.png', '设备管理平台系统，用于管理所有物联网设备', 'admin', '2025-07-29 15:09:48', 'admin', '2025-07-29 15:10:44', '0');

DROP TABLE IF EXISTS `msg_app_permission`;
CREATE TABLE `msg_app_permission` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `app_key` varchar(64) NOT NULL COMMENT '关联sys_app_credential.app_key',
  `resource_code` varchar(64) NOT NULL COMMENT '资源标识符(格式:服务:资源:操作,如api:device:read)',
  `resource_name` varchar(100) DEFAULT NULL COMMENT '资源描述',
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
COMMENT='应用权限表' 
;

DROP TABLE IF EXISTS `msg_auth_log`;
CREATE TABLE `msg_auth_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID，自增主键',
  `app_key` varchar(64) DEFAULT NULL COMMENT '应用标识',
  `auth_type` varchar(20) DEFAULT NULL COMMENT '认证类型:APPKEY/TOKEN',
  `request_ip` varchar(50) DEFAULT NULL COMMENT '请求IP',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '客户端UA',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态:0-失败 1-成功',
  `error_code` varchar(20) DEFAULT NULL COMMENT '错误码',
  `cost_time` int DEFAULT NULL COMMENT '耗时(ms)',
  
  -- 请求详情
  `request_id` varchar(64) DEFAULT NULL COMMENT '请求唯一ID',
  `timestamp` bigint DEFAULT NULL COMMENT '请求时间戳',
  `nonce` varchar(32) DEFAULT NULL COMMENT '防重放随机值',
  
  -- 系统审计字段
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  
  PRIMARY KEY (`id`),
  KEY `idx_app_key` (`app_key`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='认证日志表' 
;

-- ----------------------------
-- 2. 消息映射表，用于消息中心和代理平台的消息对照
-- ----------------------------
DROP TABLE IF EXISTS `msg_agent_mapping`;
CREATE TABLE `msg_agent_mapping` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `app_key` varchar(64) NOT NULL COMMENT '应用标识，即业务系统标识',
  `biz_id` varchar(64) NOT NULL COMMENT '业务ID，对于某个APPKEY，不能重复否则判定重复请求',
  
  -- 上级消息标识
  `xxbm` varchar(32) DEFAULT NULL COMMENT '上级消息编码',
  `center_msg_id` varchar(50) DEFAULT NULL COMMENT '部级消息ID',
  
  -- 消息内容摘要
  `msg_type` varchar(10) DEFAULT NULL COMMENT '消息类型',
  `msg_title` varchar(200) DEFAULT NULL COMMENT '消息标题',
  `priority` int DEFAULT 3 COMMENT '优先级1-5',
  `content` text COMMENT '消息内容',
  
  -- 代理平台发送方信息
  `sender_org_code` varchar(50) DEFAULT NULL COMMENT '发送者单位代码',
  `sender_org_name` varchar(100) DEFAULT NULL COMMENT '发送者单位名称',
  `sender_idcard` varchar(50) DEFAULT NULL COMMENT '发送者证件号码',
  `sender_name` varchar(100) DEFAULT NULL COMMENT '发送者姓名',
  
  -- 代理平台接收方信息
  `receiver_type` varchar(18) DEFAULT NULL COMMENT '接收者类型 USER/ROLE/DEPT/ORG',
  `receiver_org_code` varchar(50) DEFAULT NULL COMMENT '接收者单位代码',
  `receiver_org_name` varchar(100) DEFAULT NULL COMMENT '接收者单位名称',
  `receiver_idcard` varchar(50) DEFAULT NULL COMMENT '接收者证件号码',
  `receiver_name` varchar(100) DEFAULT NULL COMMENT '接收者姓名',
  
  -- 状态追踪
  `status` varchar(20) NOT NULL COMMENT '消息状态',
  `status_code` varchar(4) NOT NULL COMMENT '状态码',
  `status_detail` varchar(500) DEFAULT NULL COMMENT '状态详情',
  
  -- 时间戳
  `send_time` datetime NOT NULL COMMENT '发送时间',
  `center_receive_time` datetime DEFAULT NULL COMMENT '上级接收时间',
  `center_process_time` datetime DEFAULT NULL COMMENT '上级处理时间',
  `callback_time` datetime DEFAULT NULL COMMENT '回调业务系统时间',
  `biz_ack_time` datetime DEFAULT NULL COMMENT '业务系统确认时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  
  -- 重试信息
  `retry_count` int DEFAULT 0 COMMENT '重试次数',
  `max_retry_count` int DEFAULT 3 COMMENT '最大重试次数',
  `next_retry_time` datetime DEFAULT NULL COMMENT '下次重试时间',
  
  -- 回调信息
  `callback_url` varchar(500) DEFAULT NULL COMMENT '回调地址',
  `callback_method` varchar(10) DEFAULT 'POST' COMMENT '回调方法',
  `callback_auth_mode` varchar(20) DEFAULT 'standard' COMMENT '回调认证模式',
  `callback_signature` varchar(256) DEFAULT NULL COMMENT '回调签名',
  `callback_timestamp` bigint DEFAULT NULL COMMENT '回调时间戳(ms)',
  `callback_nonce` varchar(256) DEFAULT NULL COMMENT '回调随机字符串',
  
  -- 扩展信息
  `ext_params` json DEFAULT NULL COMMENT '扩展参数',
  
  -- 消息中心消息相关字段
  `center_xxlx` varchar(32) DEFAULT NULL COMMENT '消息中心消息类型',
  `center_xxbt` varchar(100) DEFAULT NULL COMMENT '消息中心消息标题',
  `center_xxnr` varchar(2000) DEFAULT NULL COMMENT '消息中心消息内容',
  `center_cldz` varchar(1000) DEFAULT NULL COMMENT '消息中心处理地址',
  `center_jjcd` varchar(10) DEFAULT NULL COMMENT '消息中心紧急程度',
  `center_ywcs` varchar(2000) DEFAULT NULL COMMENT '消息中心业务参数',
  `center_tb` text DEFAULT NULL COMMENT '消息中心图标(base64)',
  
  -- 消息中心发送方信息
  `center_fsdw` varchar(200) DEFAULT NULL COMMENT '消息中心发送单位',
  `center_fsdwdm` varchar(12) DEFAULT NULL COMMENT '消息中心发送单位代码',
  `center_fsr` varchar(100) DEFAULT NULL COMMENT '消息中心发送人',
  `center_fsrzjhm` varchar(18) DEFAULT NULL COMMENT '消息中心发送人证件号码',
  `center_fsdx` varchar(18) DEFAULT NULL COMMENT '消息中心发送对象',
  `center_fssj` datetime DEFAULT NULL COMMENT '消息中心发送时间',
  
  -- 消息中心接收方信息
  `center_jsdw` varchar(200) DEFAULT NULL COMMENT '消息中心接收单位',
  `center_jsdwdm` varchar(12) DEFAULT NULL COMMENT '消息中心接收单位代码',
  `center_jsr` varchar(100) DEFAULT NULL COMMENT '消息中心接收人',
  `center_jsrzjhm` varchar(18) DEFAULT NULL COMMENT '消息中心接收人证件号码',
  `center_clzt` varchar(1) DEFAULT '0' COMMENT '消息中心处理状态 0-未读 1-已读',
  
  -- 审计字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  
  PRIMARY KEY (`id`),
  INDEX `idx_xxbm` (`xxbm`),
  INDEX `idx_app_key` (`app_key`),
  INDEX `idx_status` (`status`),
  INDEX `idx_status_code` (`status_code`),
  INDEX `idx_send_time` (`send_time`),
  INDEX `idx_complete_time` (`complete_time`),
  INDEX `idx_create_time` (`create_time`),
  INDEX `idx_center_fssj` (`center_fssj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='消息映射表';

-- ----------------------------
-- 3. 接收者映射表：当从消息中心接收消息时，代理平台根据此表的映射来找到具体哪个业务系统和哪个账号来接收该消息
-- ----------------------------
DROP TABLE IF EXISTS `msg_receiver_mapping`;
CREATE TABLE `msg_receiver_mapping` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `app_key` varchar(64) NOT NULL COMMENT '应用标识，即业务系统标识',
  
  -- 业务系统接收者标识
  `biz_receiver_id` varchar(50) NOT NULL COMMENT '业务系统接收者ID',
  `biz_receiver_name` varchar(100) NOT NULL COMMENT '业务系统接收者名称',
  `biz_receiver_type` varchar(10) DEFAULT 'USER' COMMENT '接收者类型:USER/ROLE/DEPT/ORG',
  
  -- 统一消息中心接收者标识
  `center_receiver_type` varchar(10) NOT NULL COMMENT '上级接收者类型:1-个人 2-单位',
  `jsrzjhm` varchar(18) DEFAULT NULL COMMENT '接收人证件号码(个人时必填)',
  `jsr_name` varchar(100) DEFAULT NULL COMMENT '接收人姓名(个人时必填)',
  `jsdwdm` varchar(12) DEFAULT NULL COMMENT '接收单位代码(单位时必填)',
  `jsdwmc` varchar(200) DEFAULT NULL COMMENT '接收单位名称(单位时必填)',
  
  -- 状态
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
  `mapping_type` varchar(20) DEFAULT 'STATIC' COMMENT '映射类型:STATIC-静态 DYNAMIC-动态',
  
  -- 扩展信息
  `ext_params` json DEFAULT NULL COMMENT '扩展参数',
  
  -- 审计字段
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_biz_receiver` (`app_key`, `biz_receiver_id`),
  INDEX `idx_jsrzjhm` (`jsrzjhm`),
  INDEX `idx_jsdwdm` (`jsdwdm`),
  INDEX `idx_center_receiver_type` (`center_receiver_type`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='接收者映射表';

-- ----------------------------
-- 4. 消息中心Token管理表，用于管理消息中心的token，另外，为了方便统计和处理，消息中心的接口封装在此表对应的服务实现类中
-- ----------------------------
DROP TABLE IF EXISTS `msg_center_token`;
CREATE TABLE `msg_center_token` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `app_key` varchar(64) NOT NULL COMMENT '应用标识，即业务系统标识',
  
  -- Token信息
  `center_token` varchar(600) NOT NULL COMMENT '统一消息中心Token',
  `token_type` varchar(20) DEFAULT 'BEARER' COMMENT 'Token类型:BEARER',
  `expire_time` datetime NOT NULL COMMENT 'Token过期时间',
  `refresh_count` int DEFAULT 0 COMMENT '刷新次数',
  
  -- Token使用统计
  `total_requests` int DEFAULT 0 COMMENT '总请求次数',
  `success_requests` int DEFAULT 0 COMMENT '成功请求次数',
  `last_request_time` datetime DEFAULT NULL COMMENT '上次请求时间',
  `last_request_api` varchar(100) DEFAULT NULL COMMENT '上次请求API',
  
  -- 状态
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0-失效 1-有效',
  
  -- 审计字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_key` (`app_key`),
  INDEX `idx_expire_time` (`expire_time`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='统一消息中心Token管理表';

-- ----------------------------
-- 5. 轮询游标表，用于定时查询消息中心给本代理平台分配的特定业务类型的消息
-- ----------------------------
DROP TABLE IF EXISTS `msg_poll_cursor`;
CREATE TABLE `msg_poll_cursor` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `app_key` varchar(64) NOT NULL COMMENT '应用标识，即业务系统标识',
  
  -- 轮询信息
  `cursor_key` varchar(100) NOT NULL DEFAULT 'DEFAULT' COMMENT '游标键',
  `ybid` varchar(500) DEFAULT NULL COMMENT '上级游标值',
  `last_poll_time` datetime DEFAULT NULL COMMENT '上次轮询时间',
  `poll_interval` int DEFAULT 10 COMMENT '轮询间隔(秒)，≥10',
  
  -- 轮询统计
  `poll_count` int DEFAULT 0 COMMENT '轮询次数',
  `message_count` int DEFAULT 0 COMMENT '获取消息总数',
  `last_message_time` datetime DEFAULT NULL COMMENT '上次获取消息时间',
  
  -- 状态
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0-停止 1-运行',
  `error_count` int DEFAULT 0 COMMENT '连续错误次数',
  `last_error` varchar(500) DEFAULT NULL COMMENT '上次错误信息',
  `last_success_time` datetime DEFAULT NULL COMMENT '上次成功时间',
  
  -- 审计字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_cursor` (`app_key`, `cursor_key`),
  INDEX `idx_status` (`status`),
  INDEX `idx_last_poll_time` (`last_poll_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='上级消息轮询游标表';

-- ----------------------------
-- 6. 消息日志表：记录业务系统和代理平台、代理平台和消息中心的交互日志
-- ----------------------------
DROP TABLE IF EXISTS `msg_agent_log`;
CREATE TABLE `msg_agent_log` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `msg_id` varchar(50) COMMENT '代理平台消息ID: msg_agent_mapping.id',
  `app_key` varchar(64) NOT NULL COMMENT '应用标识，即业务系统标识',
  
  -- 日志信息
  `log_type` varchar(20) NOT NULL COMMENT '日志类型:SEND-发送 CALLBACK-回调 RETRY-重试 ERROR-错误 POLL-轮询 TOKEN-令牌 STATUS-状态更新',
  `log_level` varchar(10) DEFAULT 'INFO' COMMENT '日志级别:DEBUG/INFO/WARN/ERROR',
  `log_content` text NOT NULL COMMENT '日志内容',
  `log_detail` json DEFAULT NULL COMMENT '日志详情JSON',
  
  -- 操作信息
  `operation` varchar(50) DEFAULT NULL COMMENT '操作名称',
  `api_url` varchar(500) DEFAULT NULL COMMENT 'API地址',
  `http_method` varchar(10) DEFAULT NULL COMMENT 'HTTP方法',
  `http_status` int DEFAULT NULL COMMENT 'HTTP状态码',
  `response_time` int DEFAULT NULL COMMENT '响应时间(ms)',
  
  -- 审计字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  PRIMARY KEY (`id`),
  INDEX `idx_app_key` (`app_key`),
  INDEX `idx_log_type` (`log_type`),
  INDEX `idx_log_level` (`log_level`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='消息处理日志表';

-- ----------------------------
-- 7. 消息中心接口配置表：记录消息中心的地址、重试、轮询等信息
-- ----------------------------
DROP TABLE IF EXISTS `msg_center_config`;
CREATE TABLE `msg_center_config` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `config_key` varchar(50) NOT NULL COMMENT '配置键',
  `config_value` text NOT NULL COMMENT '配置值',
  `config_type` varchar(20) DEFAULT 'STRING' COMMENT '配置类型:STRING/NUMBER/BOOLEAN/JSON',
  `config_desc` varchar(200) DEFAULT NULL COMMENT '配置描述',
  `category` varchar(50) DEFAULT 'COMMON' COMMENT '配置类别:COMMON-通用 API-接口地址 TOKEN-令牌配置',
  
  -- 状态
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
  
  -- 审计字段
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  INDEX `idx_category` (`category`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='统一消息中心配置表';

-- ----------------------------
-- 8. 消息主题表：记录消息主题信息，消息主题是一种消息分类或者集合，由消息中心分配，用于消息路由
-- ----------------------------
DROP TABLE IF EXISTS `msg_topic`;
CREATE TABLE `msg_topic` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `code` varchar(50) NOT NULL COMMENT '主题代码',
  `name` varchar(100) NOT NULL COMMENT '主题名称',
  `description` varchar(2000)  COMMENT '描述',
 
  -- 状态
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
  
  -- 审计字段
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  
  PRIMARY KEY (`id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='消息模板表';

-- ----------------------------
-- 9. 消息发送队列表（用于异步处理）
-- ----------------------------
DROP TABLE IF EXISTS `msg_send_queue`;
CREATE TABLE `msg_send_queue` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `app_key` varchar(64) NOT NULL COMMENT '应用标识，即业务系统标识',
  
  -- 队列信息
  `queue_type` varchar(20) NOT NULL COMMENT '队列类型:SEND-发送消息 CALLBACK-回调业务系统 RETRY-重试',
  `queue_status` varchar(20) DEFAULT 'PENDING' COMMENT '队列状态:PENDING-等待 PROCESSING-处理中 SUCCESS-成功 FAILED-失败',
  `priority` int DEFAULT 5 COMMENT '优先级1-10，数字越小优先级越高',
  `execute_time` datetime NOT NULL COMMENT '执行时间',
  `max_retry` int DEFAULT 3 COMMENT '最大重试次数',
  `current_retry` int DEFAULT 0 COMMENT '当前重试次数',
  
  -- 任务数据
  `msg_id` varchar(32) NOT NULL COMMENT '消息ID，关联msg_agent_mapping.id',
  
  -- 执行结果
  `result_code` varchar(10) DEFAULT NULL COMMENT '结果代码',
  `result_message` varchar(500) DEFAULT NULL COMMENT '结果消息',
  `execute_start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
  `execute_end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  
  -- 审计字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  
  PRIMARY KEY (`id`),
  INDEX `idx_app_key` (`app_key`),
  INDEX `idx_queue_type` (`queue_type`),
  INDEX `idx_queue_status` (`queue_status`),
  INDEX `idx_execute_time` (`execute_time`),
  INDEX `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='消息发送队列表';

-- ----------------------------
-- 10. 消息状态码表
-- ----------------------------
DROP TABLE IF EXISTS `msg_status_code`;
CREATE TABLE `msg_status_code` (
  `id` varchar(32) NOT NULL COMMENT '唯一标识UUID',
  `status_code` varchar(32) NOT NULL COMMENT '状态码',
  `status_name` varchar(50) NOT NULL COMMENT '状态名称',
  `status_desc` varchar(200) NOT NULL COMMENT '状态描述',
  `category` varchar(20) DEFAULT 'PROXY' COMMENT '分类:PROXY-代理平台 CENTER-统一消息中心',
  `parent_code` varchar(4) DEFAULT NULL COMMENT '父状态码',
  
  -- 排序和显示
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `is_final` tinyint(1) DEFAULT 0 COMMENT '是否为最终状态:0-否 1-是',
  
  -- 状态
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0-禁用 1-启用',
  
  -- 审计字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_status_code` (`status_code`),
  INDEX `idx_category` (`category`),
  INDEX `idx_parent_code` (`parent_code`),
  INDEX `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='消息状态码表';

-- =============================================
-- 初始化数据
-- =============================================


-- ----------------------------
-- 11. 统一认证系统响应消息日志
-- ----------------------------
DROP TABLE IF EXISTS `msg_aus_auth_log`;
CREATE TABLE `msg_aus_auth_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID，自增主键',
  
  -- 请求基本信息（系统字段）
  `request_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '请求时间',
  `request_url` varchar(500) NOT NULL COMMENT '请求URL',
  `request_method` varchar(10) NOT NULL COMMENT '请求方法(POST/GET等)',
  `request_content_type` varchar(50) NOT NULL COMMENT '请求内容类型',
  
  -- 请求体参数
  `request_app_key` varchar(64) DEFAULT NULL COMMENT '应用代码',
  `request_app_secret` varchar(64) DEFAULT NULL COMMENT '应用密钥',
  `request_app_token` varchar(64) DEFAULT NULL COMMENT '应用凭证',
  
  `request_login_type` varchar(64) NOT NULL COMMENT '登录类型：0 个人，1 单位',
  `request_username` varchar(64) NOT NULL COMMENT '用户名',
  `request_password` varchar(64) DEFAULT NULL COMMENT '密码',
  `request_redirect_path` varchar(64) DEFAULT NULL COMMENT '重定向路径',
  
  `request_app_type` varchar(64) DEFAULT NULL COMMENT '子应用类型 01-设备管理（子）平台 02-执法办案管理系统 03-标准组件 04-采集设备 05-远程讯问视音频设备 06-同步录音录像设备 07-签摁终端',
  `request_access_token` varchar(64) DEFAULT NULL COMMENT '访问令牌',
  `request_refresh_token` varchar(64) DEFAULT NULL COMMENT '刷新令牌',
  
  -- 响应信息
  `response_time` datetime DEFAULT NULL COMMENT '响应时间',
  `status` varchar(3) DEFAULT NULL COMMENT '响应状态码',
  `code` varchar(5) DEFAULT NULL COMMENT '结果代码',
  `message` varchar(50) DEFAULT NULL COMMENT '响应消息',
  
  -- 返回数据（data对象中的字段）
  `local_token` varchar(64) NOT NULL COMMENT '本地令牌',
  
  `access_token` varchar(64) NOT NULL COMMENT '访问令牌',
  `refresh_token` varchar(64) DEFAULT NULL COMMENT '刷新令牌',
  `expires_time` datetime DEFAULT NULL COMMENT 'token到期时间',

  `user_id` varchar(64) NOT NULL COMMENT '用户账号',
  `aus_id` varchar(64) DEFAULT NULL COMMENT 'aus返回的用户唯一ID',

  `pro_id` varchar(64) DEFAULT NULL COMMENT '省份编号',
  `pro_code` varchar(64) DEFAULT NULL COMMENT '用户所属省份代码',
  `pro_name` varchar(128) DEFAULT NULL COMMENT '省份名称',
  `city_id` varchar(64) DEFAULT NULL COMMENT '城市编号',
  `city_code` varchar(64) DEFAULT NULL COMMENT '用户所属城市代码',
  `city_name` varchar(128) DEFAULT NULL COMMENT '城市名称',
  `reg_id` varchar(64) DEFAULT NULL COMMENT '区域编号',
  `reg_code` varchar(64) DEFAULT NULL COMMENT '用户所属区域代码',
  `reg_name` varchar(128) DEFAULT NULL COMMENT '区域名称',
  `unit_no` varchar(64) DEFAULT NULL COMMENT '用户所属单位代码',
  `org_id` varchar(64) DEFAULT NULL COMMENT '机构编号',
  `org_code` varchar(64) DEFAULT NULL COMMENT '用户所属机构代码',
  `org_name` varchar(128) DEFAULT NULL COMMENT '机构名称',
  
  `login_id` varchar(64) DEFAULT NULL COMMENT '用户账号，同user_id',
  `username` varchar(64) NOT NULL COMMENT '用户账号 对应login_id或者user_id',
  `nickname` varchar(128) DEFAULT NULL COMMENT '用户昵称',
  `name` varchar(128) DEFAULT NULL COMMENT '用户姓名',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `client_ip` varchar(18) DEFAULT NULL COMMENT '用户登录ip',
  `email` varchar(128) DEFAULT NULL COMMENT '用户邮箱',
  `mobile` varchar(128) DEFAULT NULL COMMENT '手机号码',
  `aus_role` varchar(1024) DEFAULT NULL COMMENT '用户拥有的公共角色以及在当前应用下拥有的角色，JSON对象：id，name',

  `type` char(1) DEFAULT NULL COMMENT '用户类型(0-个人用户 1-单位用户 2-其他)',
  `is_active` char(1) DEFAULT NULL COMMENT '单位账号是否激活 0:否,1:是',
  `classification` varchar(50) DEFAULT NULL COMMENT '分类代码',
  `classification_name` varchar(50) DEFAULT NULL COMMENT '分类名称',
  `parent_reg_code` varchar(50) DEFAULT NULL COMMENT '所属机关上级代码',
  `parent_reg_name` varchar(50) DEFAULT NULL COMMENT '所属机关上级名称',
  
  `manager` varchar(1024) DEFAULT NULL COMMENT '单位用户管理员,type=1,isActive=1 时返回，JSON对象:nickname-姓名,idCard-身份证，mobile-手机',

  `sex` int(3) DEFAULT NULL COMMENT '用户性别',
  `avatar` varchar(128) DEFAULT NULL COMMENT '用户头像',
  `login_ip` varchar(128) DEFAULT NULL COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  
  -- 处理信息
  `process_time` int(11) DEFAULT NULL COMMENT '处理耗时(毫秒)',
  `error_message` text DEFAULT NULL COMMENT '错误信息',
  
  -- 系统审计字段
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  
  PRIMARY KEY (`id`),
  KEY `idx_app_key` (`request_app_key`),
  KEY `idx_request_time` (`request_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一认证系统响应消息日志' ;

DROP TABLE IF EXISTS `msg_aus_auth_log_archive`;
CREATE TABLE `msg_aus_auth_log_archive` LIKE `msg_aus_auth_log`;

-- ----------------------------
-- 初始化上级接口配置
-- ----------------------------
INSERT INTO `msg_center_config` (`id`, `config_key`, `config_value`, `config_type`, `config_desc`, `category`, `status`) VALUES
-- 统一消息中心地址
('001', 'center.base.url', 'https://test.org:8000/api/v1', 'STRING', '统一消息中心基础地址', 'API', 1),
('002', 'center.resource', '010000-test-xxzx', 'STRING', '统一消息中心资源标识', 'API', 1),
('003', 'center.fh.base.url', 'http://test.org:8000/api/v1', 'STRING', '统一发号中心基础地址', 'API', 1),
('004', 'center.fh.resource', '010000-fhfw', 'STRING', '统一发号中心资源标识', 'API', 1),

-- 桩代码地址（测试环境）
('005', 'center.mock.base.url', 'https://192.168.0.157:3000/mock/53/', 'STRING', '统一消息中心桩代码地址', 'API', 1),
('006', 'center.mock.fh.base.url', 'http://192.168.0.157:3000/mock/59/', 'STRING', '统一发号中心桩代码地址', 'API', 1),

-- 接口路径
('007', 'api.message.send', '/external/xxfsfw', 'STRING', '消息发送服务接口路径', 'API', 1),
('008', 'api.message.receive', '/external/xxjsfw', 'STRING', '消息接收服务接口路径', 'API', 1),
('009', 'api.message.unread', '/external/unread/20', 'STRING', '查询未读消息接口路径', 'API', 1),
('010', 'api.message.status.update', '/external/xxztgxfw', 'STRING', '消息状态更新接口路径', 'API', 1),
('011', 'api.code.apply', '/test/xxbm/17/jcfh', 'STRING', '消息编码申请接口路径', 'API', 1),

-- 通用配置
('012', 'token.expire.buffer', '300', 'NUMBER', 'Token过期缓冲时间(秒)', 'TOKEN', 1),
('013', 'poll.interval.min', '10', 'NUMBER', '最小轮询间隔(秒)', 'COMMON', 1),
('014', 'retry.max.count', '3', 'NUMBER', '最大重试次数', 'COMMON', 1),
('015', 'retry.interval.base', '1000', 'NUMBER', '重试基础间隔(毫秒)', 'COMMON', 1),
('016', 'callback.timeout', '5000', 'NUMBER', '回调超时时间(毫秒)', 'COMMON', 1),
('017', 'enable.mock', 'false', 'BOOLEAN', '是否启用桩代码', 'COMMON', 1);

-- ----------------------------
-- 初始化代理平台消息状态码
-- ----------------------------
INSERT INTO `msg_status_code` (`id`, `status_code`, `status_name`, `status_desc`, `category`, `parent_code`, `sort_order`, `is_final`) VALUES
-- 代理平台状态码
('1000', '1000', 'ACCEPTED', '已接收，正在处理', 'PROXY', NULL, 1, 0),
('1001', '1001', 'SENDING', '正在发送到消息中心', 'PROXY', '1000', 2, 0),
('1002', '1002', 'CENTER_ACCEPTED', '消息中心已接收', 'PROXY', '1001', 3, 0),
('1003', '1003', 'CENTER_PROCESSING', '消息中心处理中', 'PROXY', '1002', 4, 0),
('1004', '1004', 'CENTER_COMPLETED', '消息中心处理完成', 'PROXY', '1003', 5, 0),
('1005', '1005', 'CALLBACK_SENT', '已发送回调到业务系统', 'PROXY', '1004', 6, 0),
('1006', '1006', 'CALLBACK_ACKED', '业务系统已确认接收', 'PROXY', '1005', 7, 0),
('1007', '1007', 'COMPLETED', '全部流程完成', 'PROXY', '1006', 8, 1),
('1008', '1008', 'PARTIAL_FAILED', '部分失败', 'PROXY', NULL, 9, 1),
('1009', '1009', 'FAILED', '完全失败', 'PROXY', NULL, 10, 1),

-- 统一消息中心状态码
('2000', '00000', 'SUCCESS', '服务处理成功', 'CENTER', NULL, 1, 1),
('2001', '10001', 'PARAM_ERROR', '参数错误', 'CENTER', NULL, 2, 1),
('2002', '10002', 'TOKEN_EXPIRED', 'Token已过期', 'CENTER', NULL, 3, 0),
('2003', '10003', 'PERMISSION_DENIED', '权限不足', 'CENTER', NULL, 4, 1),
('2004', '10004', 'SYSTEM_ERROR', '系统错误', 'CENTER', NULL, 5, 1);

-- =============================================
-- 创建视图（可选）
-- =============================================

-- ----------------------------
-- 视图：消息统计视图
-- ----------------------------
CREATE OR REPLACE VIEW `v_message_statistics` AS
SELECT 
    m.app_key,
    a.app_name,
    DATE(m.send_time) AS stat_date,
    COUNT(*) AS total_count,
    SUM(CASE WHEN m.status_code = '1007' THEN 1 ELSE 0 END) AS success_count,
    SUM(CASE WHEN m.status_code IN ('1008', '1009') THEN 1 ELSE 0 END) AS failed_count,
    AVG(TIMESTAMPDIFF(SECOND, m.send_time, m.complete_time)) AS avg_process_seconds
FROM msg_agent_mapping m
JOIN msg_app_credential a ON m.app_key = a.app_key AND a.del_flag = '0'
WHERE m.del_flag = '0'
GROUP BY m.app_key, DATE(m.send_time);

-- ----------------------------
-- 视图：今日消息状态视图
-- ----------------------------
CREATE OR REPLACE VIEW `v_today_message_status` AS
SELECT 
    m.app_key,
    a.app_name,
    m.status,
    m.status_code,
    s.status_name,
    s.status_desc,
    COUNT(*) AS message_count
FROM msg_agent_mapping m
JOIN msg_app_credential a ON m.app_key = a.app_key AND a.del_flag = '0'
JOIN msg_status_code s ON m.status_code = s.status_code AND s.category = 'PROXY'
WHERE m.del_flag = '0'
  AND DATE(m.send_time) = CURDATE()
GROUP BY m.app_key, m.status, m.status_code, s.status_name, s.status_desc;

-- =============================================
-- 创建事件（定时清理任务）
-- =============================================

-- ----------------------------
-- 事件：清理过期的消息日志（保留30天）
-- ----------------------------
DELIMITER $$
CREATE EVENT `event_clean_message_log`
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    -- 删除30天前的消息日志
    DELETE FROM msg_agent_log 
    WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
    
    -- 删除已完成且超过7天的消息映射记录
    UPDATE msg_agent_mapping 
    SET del_flag = '1' 
    WHERE del_flag = '0'
      AND status_code IN ('1007', '1008', '1009')
      AND complete_time < DATE_SUB(NOW(), INTERVAL 7 DAY);
END$$
DELIMITER ;

-- =============================================
-- 创建索引优化（后添加的索引）
-- =============================================

-- 为msg_agent_mapping表添加联合索引优化查询性能
CREATE INDEX `idx_app_status_time` ON `msg_agent_mapping` (`app_key`, `status`, `send_time`);

-- 为msg_agent_log表添加时间范围查询索引
CREATE INDEX `idx_create_time_range` ON `msg_agent_log` (`create_time`, `log_type`);

-- =============================================
-- 数据库脚本执行完成
-- =============================================
SELECT '数据库脚本执行完成' AS `执行结果`;
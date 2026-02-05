CREATE DATABASE IF NOT EXISTS `feng-ai`;
USE `feng-ai`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for aigc_app
-- ----------------------------
DROP TABLE IF EXISTS `aigc_app`;
CREATE TABLE `aigc_app` (
  `id`            INT(11)                                                       NOT NULL AUTO_INCREMENT COMMENT 'AI应用ID',
  `model_id`      int(11)                                                       NULL DEFAULT NULL       COMMENT '关联模型',
  `knowledge_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL            COMMENT '关联知识库，数组，例[\"393704ac13f67fde5da674ddd0742b03\"]',
  `cover`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL            COMMENT '封面',
  `name`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL            COMMENT '名称',
  `prompt`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci                                 COMMENT '提示词',
  `des`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL            COMMENT '描述',
  `save_time`     datetime                                                      DEFAULT NULL            COMMENT '保存时间',
  `create_time`   datetime                                                      DEFAULT NULL            COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词表';

-- ----------------------------
-- Records of aigc_app
-- ----------------------------
BEGIN;
INSERT INTO `aigc_app` (`id`, `model_id`, `knowledge_ids`, `cover`, `name`, `prompt`, `des`, `save_time`, `create_time`) VALUES (1, NULL, NULL, 'http://127.0.0.1/logo.jpg', 'feng-ai应用实例', '你是一个专业的文档分析师，你擅长从文档中提取关键内容并总结分析含义，下面你需要根据用户的问题做出解答。\n\n## 限制\n不要回答和文档无关的内容', '快速解答feng-ai项目相关的内容，feng-ai官方助手', now(), now());
COMMIT;

-- ----------------------------
-- Table structure for aigc_app_api
-- ----------------------------
DROP TABLE IF EXISTS `aigc_app_api`;
CREATE TABLE `aigc_app_api` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT 'AI API ID',
  `app_id` int(11) NULL DEFAULT NULL COMMENT '应用ID',
  `channel` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '应用渠道',
  `api_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Key',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用';

-- ----------------------------
-- Table structure for aigc_conversation
-- ----------------------------
DROP TABLE IF EXISTS `aigc_conversation`;
CREATE TABLE `aigc_conversation` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '对话ID',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `prompt_id` int(11) NULL DEFAULT NULL COMMENT '提示词ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话窗口表';


-- ----------------------------
-- Table structure for aigc_docs
-- ----------------------------
DROP TABLE IF EXISTS `aigc_docs`;
CREATE TABLE `aigc_docs` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '文档ID',
  `knowledge_id` int(11) NULL DEFAULT NULL COMMENT '知识库ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `origin` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '内容或链接',
  `size` int DEFAULT NULL COMMENT '文件大小',
  `slice_num` int DEFAULT NULL COMMENT '切片数量',
  `slice_status` tinyint(1) DEFAULT NULL COMMENT '切片状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档表';

-- ----------------------------
-- Records of aigc_docs
-- ----------------------------
BEGIN;
INSERT INTO `aigc_docs` (`id`, `knowledge_id`, `name`, `type`, `url`, `origin`, `content`, `size`, `slice_num`, `slice_status`, `create_time`) VALUES (1, 1, 'story-about-happy-carrot.pdf', 'UPLOAD', NULL, NULL, NULL, 35359, NULL, 0, now());
INSERT INTO `aigc_docs` (`id`, `knowledge_id`, `name`, `type`, `url`, `origin`, `content`, `size`, `slice_num`, `slice_status`, `create_time`) VALUES (2, 1, 'guide1', 'INPUT', NULL, NULL, 'feng-ai 是一个基于Java生态的企业AI知识库和大模型应用解决方案，帮助企业快速搭建AI大模型应用。 同时，feng-ai也集成了RBAC权限体系，为企业提供开箱即用的AI大模型产品解决方案。\n\nfeng-ai 使用Java生态，前后端分离，并采用最新的技术栈开发。后端基于SpringBoot3，前端基于Vue3。 feng-ai不仅为企业提供AI领域的产品解决方案，也是一个完整的Java企业级应用案例。这个系统带你全面了解SpringBoot3和Vue3的前后端开发流程、业务模块化，以及AI应用集成方案。 无论是企业开发，还是个人学习，feng-ai都为你提供丰富的学习案例', NULL, 1, 1, now());
INSERT INTO `aigc_docs` (`id`, `knowledge_id`, `name`, `type`, `url`, `origin`, `content`, `size`, `slice_num`, `slice_status`, `create_time`) VALUES (3, 1, 'story-about-happy-carrot.pdf', 'UPLOAD', 'http://127.0.0.1/feng-ai/2024080866b4b069cdb262aeea8da409.pdf', NULL, NULL, 35359, 37, 1, now());
INSERT INTO `aigc_docs` (`id`, `knowledge_id`, `name`, `type`, `url`, `origin`, `content`, `size`, `slice_num`, `slice_status`, `create_time`) VALUES (4, 1, 'story-about-happy-carrot.pdf', 'UPLOAD', NULL, NULL, NULL, 35359, NULL, 0, now());
COMMIT;

-- ----------------------------
-- Table structure for aigc_docs_slice
-- ----------------------------
DROP TABLE IF EXISTS `aigc_docs_slice`;
CREATE TABLE `aigc_docs_slice` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '切片ID',
  `vector_id` int(11) NULL DEFAULT NULL COMMENT '向量库的ID，关联aigc_embed_store',
  `docs_id` int(11) NULL DEFAULT NULL COMMENT '文档ID',
  `knowledge_id` int(11) NULL DEFAULT NULL COMMENT '知识库ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文档名称',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '切片内容',
  `word_num` int DEFAULT NULL COMMENT '字符数',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档切片表';

-- ----------------------------
-- Table structure for aigc_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `aigc_knowledge`;
CREATE TABLE `aigc_knowledge` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '知识ID',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `embed_store_id` int(11) NULL DEFAULT NULL COMMENT '向量数据库ID',
  `embed_model_id` int(11) NULL DEFAULT NULL COMMENT '向量模型ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '知识库名称',
  `des` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面',
  `create_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库表';

-- ----------------------------
-- Records of aigc_knowledge
-- ----------------------------
BEGIN;
INSERT INTO `aigc_knowledge` (`id`, `user_id`, `embed_store_id`, `embed_model_id`, `name`, `des`, `cover`, `create_time`) VALUES (1, NULL, NULL, NULL, 'feng-ai文档', 'feng-ai文档', NULL, now());
COMMIT;

-- ----------------------------
-- Table structure for aigc_message
-- ----------------------------
DROP TABLE IF EXISTS `aigc_message`;
CREATE TABLE `aigc_message` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `conversation_id` int(11) NULL DEFAULT NULL COMMENT '会话ID',
  `chat_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对话ID',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `role` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色，user和assistant',
  `model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模型名称',
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '消息内容',
  `tokens` int DEFAULT NULL,
  `prompt_tokens` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `conversation_id` (`conversation_id`) USING BTREE,
  KEY `role` (`role`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话消息表';


-- ----------------------------
-- Table structure for aigc_model
-- ----------------------------
DROP TABLE IF EXISTS `aigc_model`;
CREATE TABLE `aigc_model` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '模型ID',
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型: CHAT、Embedding、Image',
  `model` varchar(100) DEFAULT NULL COMMENT '模型名称',
  `provider` varchar(100) DEFAULT NULL COMMENT '供应商',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '别名',
  `response_limit` int DEFAULT NULL COMMENT '响应长度',
  `temperature` double DEFAULT NULL COMMENT '温度',
  `top_p` double DEFAULT NULL,
  `api_key` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `base_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `secret_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `endpoint` varchar(100) DEFAULT NULL,
  `azure_deployment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'azure模型参数',
  `gemini_project` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'gemini模型参数',
  `gemini_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'gemini模型参数',
  `image_size` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片大小',
  `image_quality` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片质量',
  `image_style` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片风格',
  `dimension` int DEFAULT NULL COMMENT '向量维数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='LLM模型配置表';

-- ----------------------------
-- Records of aigc_model
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for aigc_oss
-- ----------------------------
DROP TABLE IF EXISTS `aigc_oss`;
CREATE TABLE `aigc_oss` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `oss_id` int(11) NULL DEFAULT NULL COMMENT 'OSS对象ID',
  `file_id` varchar(64) NULL DEFAULT NULL COMMENT '文件ID',
  `original_filename` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原始文件名称',
  `filename` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件存储名称',
  `url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件地址',
  `base_path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '桶路径',
  `path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件的绝对路径',
  `size` int DEFAULT NULL COMMENT '文件大小',
  `ext` varchar(50) DEFAULT NULL COMMENT '文件后缀',
  `content_type` varchar(100) DEFAULT NULL COMMENT '文件头',
  `platform` varchar(50) DEFAULT NULL COMMENT '平台',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源文件表';

-- ----------------------------
-- Records of aigc_oss
-- ----------------------------
BEGIN;
INSERT INTO `aigc_oss` (`id`, `user_id`, `oss_id`, `original_filename`, `filename`, `url`, `base_path`, `path`, `size`, `ext`, `content_type`, `platform`, `create_time`) VALUES (1, 3, NULL, '36946717.JPEG', '66b6df5ecdb26cd406afc109.JPEG', 'http://127.0.0.1/feng-ai/2024081066b6df5ecdb26cd406afc109.JPEG', 'feng-ai/', '20240810', 11744, 'JPEG', 'image/jpeg', 'local', now());
INSERT INTO `aigc_oss` (`id`, `user_id`, `oss_id`, `original_filename`, `filename`, `url`, `base_path`, `path`, `size`, `ext`, `content_type`, `platform`, `create_time`) VALUES (2, 3, NULL, 'story-about-happy-carrot.pdf', '66b4afeecdb2c038a2624532.pdf', 'http://127.0.0.1/feng-ai/2024080866b4afeecdb2c038a2624532.pdf', 'feng-ai/', '20240808', 35359, 'pdf', 'application/pdf', 'qiniu', now());
INSERT INTO `aigc_oss` (`id`, `user_id`, `oss_id`, `original_filename`, `filename`, `url`, `base_path`, `path`, `size`, `ext`, `content_type`, `platform`, `create_time`) VALUES (3, 3, NULL, 'story-about-happy-carrot.pdf', '66b239dbcdb2ff916a0a092c.pdf', 'http://127.0.0.1/feng-ai/2024080666b239dbcdb2ff916a0a092c.pdf', 'feng-ai/', '20240806', 35359, 'pdf', 'application/pdf', 'qiniu', now());
INSERT INTO `aigc_oss` (`id`, `user_id`, `oss_id`, `original_filename`, `filename`, `url`, `base_path`, `path`, `size`, `ext`, `content_type`, `platform`, `create_time`) VALUES (4, 3, NULL, 'story-about-happy-carrot.pdf', '66b4b069cdb262aeea8da409.pdf', 'http://127.0.0.1/feng-ai/2024080866b4b069cdb262aeea8da409.pdf', 'feng-ai/', '20240808', 35359, 'pdf', 'application/pdf', 'local', now());
INSERT INTO `aigc_oss` (`id`, `user_id`, `oss_id`, `original_filename`, `filename`, `url`, `base_path`, `path`, `size`, `ext`, `content_type`, `platform`, `create_time`) VALUES (5, 3, NULL, 'story-about-happy-carrot.pdf', '66b489b0cdb2a4b1a529719f.pdf', 'http://127.0.0.1/feng-ai/2024080866b489b0cdb2a4b1a529719f.pdf', 'feng-ai/', '20240808', 35359, 'pdf', 'application/pdf', 'qiniu', now());
INSERT INTO `aigc_oss` (`id`, `user_id`, `oss_id`, `original_filename`, `filename`, `url`, `base_path`, `path`, `size`, `ext`, `content_type`, `platform`, `create_time`) VALUES (6, 3, NULL, '36946717.JPEG', '66b6e0fbcdb220c420fe6bae.JPEG', 'http://127.0.0.1/feng-ai/2024081066b6e0fbcdb220c420fe6bae.JPEG', 'feng-ai/', '20240810', 11744, 'JPEG', 'image/jpeg', 'local', now());
INSERT INTO `aigc_oss` (`id`, `user_id`, `oss_id`, `original_filename`, `filename`, `url`, `base_path`, `path`, `size`, `ext`, `content_type`, `platform`, `create_time`) VALUES (7, 3, NULL, '36946717.JPEG', '66b6e0a2cdb26cd406afc10a.JPEG', 'http://127.0.0.1/feng-ai/2024081066b6e0a2cdb26cd406afc10a.JPEG', 'feng-ai/', '20240810', 11744, 'JPEG', 'image/jpeg', 'local', now());
COMMIT;

-- ----------------------------
-- Table structure for aigc_embed_store
-- ----------------------------
DROP TABLE IF EXISTS `aigc_embed_store`;
CREATE TABLE `aigc_embed_store` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '向量库ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '别名',
  `provider` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '供应商',
  `host` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `port` int DEFAULT NULL COMMENT '端口',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
  `database_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据库名称',
  `table_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表名称',
  `dimension` int DEFAULT NULL COMMENT '向量维数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Embedding向量数据库配置表';

SET FOREIGN_KEY_CHECKS = 1;

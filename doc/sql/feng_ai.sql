
SET NAMES 'utf8mb4' COLLATE 'utf8mb4_unicode_ci';
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- create database `feng_ai_biz`
-- ----------------------------

drop database if exists `feng_ai_biz`;
create database `feng_ai_biz` default character set utf8mb4 collate utf8mb4_unicode_ci;

use `feng_ai_biz`;

-- ----------------------------
-- Table structure for aigc_model
-- ----------------------------
DROP TABLE IF EXISTS `aigc_model`;
CREATE TABLE `aigc_model`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '模型ID' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `type` varchar(100) DEFAULT NULL COMMENT '类型: CHAT、Embedding、Image',
  `model` varchar(100) DEFAULT NULL COMMENT '模型名称',
  `provider` varchar(100) DEFAULT NULL COMMENT '供应商',
  `name` varchar(100) DEFAULT NULL COMMENT '别名',
  `response_limit` int(11) DEFAULT NULL COMMENT '响应长度',
  `temperature` double DEFAULT NULL COMMENT '温度',
  `top_p` double DEFAULT NULL COMMENT '候选词百分比',
  `api_key` varchar(1024) DEFAULT NULL COMMENT '模型key',
  `base_url` varchar(100) DEFAULT NULL COMMENT '模型基准链接',
  `secret_key` varchar(100) DEFAULT NULL COMMENT '模型密钥',
  `endpoint` varchar(100) DEFAULT NULL COMMENT '模型端点',
  `azure_deployment_name` varchar(100) DEFAULT NULL COMMENT 'azure模型参数',
  `gemini_project` varchar(100) DEFAULT NULL COMMENT 'gemini模型参数-项目名称',
  `gemini_location` varchar(100) DEFAULT NULL COMMENT 'gemini模型参数-服务器位置',
  `image_size` varchar(50) DEFAULT NULL COMMENT '图片大小',
  `image_quality` varchar(50) DEFAULT NULL COMMENT '图片质量',
  `image_style` varchar(50) DEFAULT NULL COMMENT '图片风格',
  `dimension` int(11) DEFAULT NULL COMMENT '向量维数',
  `status` char(1) NULL DEFAULT '0' COMMENT '状态 启用1，禁用0',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) NULL DEFAULT '0' COMMENT '逻辑删 0-未删除 1-已删除',
  INDEX `idx_model_name` (name) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = 'LLM模型配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of aigc_model
-- ----------------------------
INSERT INTO `aigc_model` VALUES (1, 100, NULL, 'CHAT', 'deepseek-chat', 'DEEPSEEK', 'deepseek-chat', 8192, 0, 0.8, '', 'https://api.deepseek.com/v1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', 'admin', '2025-04-26 00:08:28', 'admin', '2025-06-22 01:14:36', '0');
INSERT INTO `aigc_model` VALUES (2, 100, NULL, 'CHAT', 'deepseek-coder', 'DEEPSEEK', 'deepseek-coder', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:16:42', '0');
INSERT INTO `aigc_model` VALUES (3, 100, NULL, 'CHAT', 'gpt-3.5-turbo', 'OPENAI', 'OpenAI-gpt-3.5-turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (4, 100, NULL, 'CHAT', 'gpt-4', 'OPENAI', 'OpenAI-gpt-4', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (5, 100, NULL, 'CHAT', 'gpt-4-32k', 'OPENAI', 'OpenAI-gpt-4-32k', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (6, 100, NULL, 'CHAT', 'gpt-4-turbo', 'OPENAI', 'OpenAI-gpt-4-turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (7, 100, NULL, 'CHAT', 'gpt-4o', 'OPENAI', 'OpenAI-gpt-4o', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (8, 100, NULL, 'CHAT', 'ERNIE-Bot', 'BAIDU', '百度千帆-ERNIE-Bot', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (9, 100, NULL, 'CHAT', 'ERNIE-Bot 4.0', 'BAIDU', '百度千帆-ERNIE-Bot 4.0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (10, 100, NULL, 'CHAT', 'ERNIE-Bot-8K', 'BAIDU', '百度千帆-ERNIE-Bot-8K', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (11, 100, NULL, 'CHAT', 'ERNIE-Bot-turbo', 'BAIDU', '百度千帆-ERNIE-Bot-turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (12, 100, NULL, 'CHAT', 'ERNIE-Speed-128K', 'BAIDU', '百度千帆-ERNIE-Speed-128K', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (13, 100, NULL, 'CHAT', 'EB-turbo-AppBuilder', 'BAIDU', '百度千帆-EB-turbo-AppBuilder', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (14, 100, NULL, 'CHAT', 'Yi-34B-Chat', 'BAIDU', '百度千帆-Yi-34B-Chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (15, 100, NULL, 'CHAT', 'BLOOMZ-7B', 'BAIDU', '百度千帆-BLOOMZ-7B', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (16, 100, NULL, 'CHAT', 'Qianfan-BLOOMZ-7B-compressed', 'BAIDU', '百度千帆-Qianfan-BLOOMZ-7B-compressed', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (17, 100, NULL, 'CHAT', 'Mixtral-8x7B-Instruct', 'BAIDU', '百度千帆-Mixtral-8x7B-Instruct', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (18, 100, NULL, 'CHAT', 'Llama-2-7b-chat', 'BAIDU', '百度千帆-Llama-2-7b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (19, 100, NULL, 'CHAT', 'Llama-2-13b-chat', 'BAIDU', '百度千帆-Llama-2-13b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (20, 100, NULL, 'CHAT', 'Llama-2-70b-chat', 'BAIDU', '百度千帆-Llama-2-70b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (21, 100, NULL, 'CHAT', 'Qianfan-Chinese-Llama-2-7B', 'BAIDU', '百度千帆-Qianfan-Chinese-Llama-2-7B', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (22, 100, NULL, 'CHAT', 'ChatGLM2-6B-32K', 'BAIDU', '百度千帆-ChatGLM2-6B-32K', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (23, 100, NULL, 'CHAT', 'AquilaChat-7B', 'BAIDU', '百度千帆-AquilaChat-7B', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (24, 100, NULL, 'CHAT', 'qwen-turbo', 'ALICLOUD', '阿里百炼-qwen-turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (25, 100, NULL, 'CHAT', 'qwen-plus', 'ALICLOUD', '阿里百炼-qwen-plus', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (26, 100, NULL, 'CHAT', 'qwen-max', 'ALICLOUD', '阿里百炼-qwen-max', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (27, 100, NULL, 'CHAT', 'qwen-max-longcontext', 'ALICLOUD', '阿里百炼-qwen-max-longcontext', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (28, 100, NULL, 'CHAT', 'qwen-7b-chat', 'ALICLOUD', '阿里百炼-qwen-7b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (29, 100, NULL, 'CHAT', 'qwen-14b-chat', 'ALICLOUD', '阿里百炼-qwen-14b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (30, 100, NULL, 'CHAT', 'qwen-72b-chat', 'ALICLOUD', '阿里百炼-qwen-72b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (31, 100, NULL, 'CHAT', 'glm-4', 'ZHIPU', '智谱清言-glm-4', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (32, 100, NULL, 'CHAT', 'glm-4v', 'ZHIPU', '智谱清言-glm-4v', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (33, 100, NULL, 'CHAT', 'glm-4-air', 'ZHIPU', '智谱清言-glm-4-air', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (34, 100, NULL, 'CHAT', 'glm-4-airx', 'ZHIPU', '智谱清言-glm-4-airx', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (35, 100, NULL, 'CHAT', 'glm-4-flash', 'ZHIPU', '智谱清言-glm-4-flash', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (36, 100, NULL, 'CHAT', 'glm-3-turbo', 'ZHIPU', '智谱清言-glm-3-turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (37, 100, NULL, 'CHAT', 'chatglm_turbo', 'ZHIPU', '智谱清言-chatglm_turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (38, 100, NULL, 'CHAT', 'Qwen2-72B-Instruct', 'BAAI', 'Gitee AI-Qwen2-72B-Instruct', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (39, 100, NULL, 'CHAT', 'Qwen2-7B-Instruct', 'BAAI', 'Gitee AI-Qwen2-7B-Instruct', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (40, 100, NULL, 'CHAT', 'Qwen2.5-72B-Instruct', 'BAAI', 'Gitee AI-Qwen2.5-72B-Instruct', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (41, 100, NULL, 'CHAT', 'glm-4-9b-chat', 'BAAI', 'Gitee AI-glm-4-9b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (42, 100, NULL, 'CHAT', 'deepseek-coder-33B-instruct', 'BAAI', 'Gitee AI-deepseek-coder-33B-instruct', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (43, 100, NULL, 'CHAT', 'codegeex4-all-9b', 'BAAI', 'Gitee AI-codegeex4-all-9b', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (44, 100, NULL, 'CHAT', 'Yi-34B-Chat', 'BAAI', 'Gitee AI-Yi-34B-Chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (45, 100, NULL, 'CHAT', 'code-raccoon-v1', 'BAAI', 'Gitee AI-code-raccoon-v1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (46, 100, NULL, 'CHAT', 'Qwen2.5-Coder-32B-Instruct', 'BAAI', 'Gitee AI-Qwen2.5-Coder-32B-Instruct', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0');
INSERT INTO `aigc_model` VALUES (47, 100, NULL, 'CHAT', 'deepseek-ai/DeepSeek-V2-Chat', 'DEEPSEEK', '硅基流动-DeepSeek-V2-Chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:16:08', '0');
INSERT INTO `aigc_model` VALUES (48, 100, NULL, 'CHAT', 'yi-lightning', 'OTHER', '零一万物-yi-lightning', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:14:05', '0');
INSERT INTO `aigc_model` VALUES (49, 100, NULL, 'CHAT', 'yi-large', 'OTHER', '零一万物-yi-large', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:14:11', '0');
INSERT INTO `aigc_model` VALUES (50, 100, NULL, 'CHAT', 'lite', 'XUNFEI', '讯飞星火-lite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:14:19', '0');
INSERT INTO `aigc_model` VALUES (51, 100, NULL, 'CHAT', 'generalv3', 'XUNFEI', '讯飞星火-generalv3', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:14:25', '0');
INSERT INTO `aigc_model` VALUES (52, 100, NULL, 'CHAT', 'gpt-3.5-turbo', 'OPENAI', 'Azure OpenAI-gpt-3.5-turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:14:32', '0');
INSERT INTO `aigc_model` VALUES (53, 100, NULL, 'CHAT', 'gemini-1.5-pro', 'GOOGLE', 'gemini-1.5-pro', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:14:54', '0');
INSERT INTO `aigc_model` VALUES (54, 100, NULL, 'CHAT', 'claude-3-opus', 'CLAUDE', 'claude-3-opus', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:15:41', '0');
INSERT INTO `aigc_model` VALUES (55, 100, NULL, 'EMBEDDING', 'text-embedding-3-small', 'OPENAI', 'OpenAI-text-embedding-3-small', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:27:25', NULL, NULL, '0');
INSERT INTO `aigc_model` VALUES (56, 100, NULL, 'EMBEDDING', 'text-embedding-3-large', 'OPENAI', 'OpenAI-text-embedding-3-large', NULL, 0.2, 0, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', 'edison', '2025-05-06 10:27:57', 'edison', '2025-06-22 01:15:04', '0');
INSERT INTO `aigc_model` VALUES (57, 100, NULL, 'EMBEDDING', 'bge-large-zh', 'BAIDU', '百度千帆-bge-large-zh', NULL, 0.2, 0, 'KGJmZxDOoU4KuH4BKwrd2EzC', 'https://qianfan.baidubce.com/v2/', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', 'edison', '2025-05-06 10:28:56', 'edison', '2025-06-22 01:15:20', '0');
INSERT INTO `aigc_model` VALUES (58, 100, NULL, 'EMBEDDING', 'bge-large-en', 'BAIDU', '百度千帆bge-large-en', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:29:19', NULL, NULL, '0');
INSERT INTO `aigc_model` VALUES (59, 100, NULL, 'EMBEDDING', 'tao-8k', 'BAIDU', '百度千帆-tao-8k', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:30:08', NULL, NULL, '0');
INSERT INTO `aigc_model` VALUES (60, 100, NULL, 'EMBEDDING', 'text-embedding-v3', 'ALICLOUD', '阿里百炼-text-embedding-v3', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:30:56', NULL, NULL, '0');
INSERT INTO `aigc_model` VALUES (61, 100, NULL, 'EMBEDDING', 'embedding-2', 'ZHIPU', '智谱清言-embedding-2', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:31:28', NULL, NULL, '0');
INSERT INTO `aigc_model` VALUES (62, 100, NULL, 'EMBEDDING', 'embedding-3', 'ZHIPU', '智谱清言embedding-3', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:32:00', NULL, NULL, '0');
INSERT INTO `aigc_model` VALUES (63, 100, NULL, 'EMBEDDING', 'text-240715', 'DOUBAO', '抖音豆包text-240715', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:32:42', NULL, NULL, '0');
INSERT INTO `aigc_model` VALUES (64, 100, NULL, 'EMBEDDING', 'text-240515', 'DOUBAO', '抖音豆包text-240515', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:33:11', NULL, NULL, '0');
INSERT INTO `aigc_model` VALUES (65, 100, NULL, 'EMBEDDING', 'text2vec-bge-large-chinese:latest', 'META', 'text2vec-bge-large-chinese:latest', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:33:37', 'edison', '2025-05-06 10:36:33', '0');
INSERT INTO `aigc_model` VALUES (66, 100, NULL, 'IMAGE', 'dall-e-2', 'OPENAI', 'openai-dall-e-2', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:38:04', NULL, NULL, '0');
INSERT INTO `aigc_model` VALUES (67, 100, NULL, 'IMAGE', 'dall-e-3', 'OPENAI', 'openai-dall-e-3', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:38:29', 'edison', '2025-05-06 10:39:00', '0');
INSERT INTO `aigc_model` VALUES (68, 100, NULL, 'IMAGE', 'cogview-3', 'ZHIPU', '智谱清言-cogview-3', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:38:51', NULL, NULL, '0');
-- ----------------------------
-- Table structure for aigc_app
-- ----------------------------
DROP TABLE IF EXISTS `aigc_app`;
CREATE TABLE `aigc_app`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'AI应用ID' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `model_id` int(11) DEFAULT NULL COMMENT '关联模型',
  `cover` varchar(255) DEFAULT NULL COMMENT '封面',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `des` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` char(1) NULL DEFAULT '0' COMMENT '状态 启用1，禁用0',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) NULL DEFAULT '0' COMMENT '逻辑删 0-未删除 1-已删除',
  CONSTRAINT `fk_app_model` foreign key (`model_id`) references `aigc_model` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = 'AI应用表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of aigc_app
-- ----------------------------
INSERT INTO `aigc_app` VALUES (1, 100, NULL, 1, 'assitant.png', '编程应用', '编写计算机软件系统', '0', 'admin', '2025-05-01 08:28:59', 'edison', '2025-06-11 07:56:15', '0');
INSERT INTO `aigc_app` VALUES (2, 100, NULL, 1, 'feng-bucket/5ac88ac6bbfb4a39a8ceb05dbf11ff83.jpg', '编书应用', '编写书籍', '0', 'edison', '2025-06-11 07:56:52', 'edison', '2025-06-11 07:57:14', '0');

-- ----------------------------
-- Table structure for aigc_app_api
-- ----------------------------
DROP TABLE IF EXISTS `aigc_app_api`;
CREATE TABLE `aigc_app_api`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '渠道ID' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `app_id` int(11) DEFAULT NULL COMMENT '应用ID',
  `channel` varchar(50) DEFAULT NULL COMMENT '应用渠道名称，例如web-Web端访问，wechat-微信小程序渠道，api-第三方调用接口，app-移动端APP，custom_xxx-某客户定制的专属接入（如嵌入内网），robot-内部聊天机器人',
  `api_key` varchar(50) DEFAULT NULL COMMENT '渠道Key',
  `base_url` varchar(100) DEFAULT NULL COMMENT '渠道基准链接',
  `secret_key` varchar(100) DEFAULT NULL COMMENT '渠道密钥',
  `status` char(1) NULL DEFAULT '0' COMMENT '状态 启用1，禁用0',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) NULL DEFAULT '0' COMMENT '逻辑删 0-未删除 1-已删除',
  CONSTRAINT `fk_app_api_app` foreign key (`app_id`) references `aigc_app` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '应用渠道' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of aigc_app_api
-- ----------------------------
INSERT INTO `aigc_app_api` VALUES (1, 100, NULL, 1, '编程通用渠道', 'sk-bc782e31d3814b9db99e2c38cad64118', 'https://api.deepseek.com/v1', NULL, '0', 'admin', '2025-05-01 08:28:59', 'admin', NULL, '0');

-- ----------------------------
-- Table structure for aigc_conversation
-- ----------------------------
DROP TABLE IF EXISTS `aigc_conversation`;
CREATE TABLE `aigc_conversation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '对话ID' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `app_api_id` int(11) DEFAULT NULL COMMENT '用户渠道ID',
  `knowledge_id` int(11) DEFAULT NULL COMMENT '知识库ID，为空表示普通对话聊天',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  `chat_total` INT DEFAULT 0 COMMENT '对话条数',
  `token_used` INT DEFAULT 0 COMMENT 'Token 消耗',
  `end_time` DATETIME DEFAULT NULL COMMENT '最后一次对话时间',
  `status` char(1) NULL DEFAULT '0' COMMENT '状态 启用1，禁用0',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) NULL DEFAULT '0' COMMENT '逻辑删 0-未删除 1-已删除',
  CONSTRAINT `fk_conversation_app_api` foreign key (`app_api_id`) references `aigc_app_api` (`id`),
  CONSTRAINT `fk_conversation_knowledge` foreign key (`knowledge_id`) references `aigc_knowledge` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '对话窗口表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of aigc_conversation
-- ----------------------------
INSERT INTO `aigc_conversation` VALUES (1, 100, NULL, 1, NULL, 3, '文档分析师', 0, 0, NULL, '0', 'admin', '2025-05-01 08:28:59', 'admin', NULL, '0');
INSERT INTO `aigc_conversation` VALUES (2, 100, NULL, 1, 1, 3, '知识库查询', 0, 0, NULL, '0', 'admin', '2025-05-01 08:28:59', 'admin', NULL, '0');
INSERT INTO `aigc_conversation` VALUES (3, 100, NULL, 1, NULL, 3, '程序员', 0, 0, NULL, '0', 'admin', '2025-05-01 08:28:59', 'admin', NULL, '0');

-- ----------------------------
-- Table structure for aigc_prompt
-- ----------------------------
DROP TABLE IF EXISTS `aigc_prompt`;
CREATE TABLE `aigc_prompt` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '提示词ID' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `name` VARCHAR(100) NOT NULL COMMENT '提示词名称',
  `content` TEXT NOT NULL COMMENT '提示内容',
  `app_api_id` INT(11) DEFAULT NULL COMMENT '应用渠道ID，提示语针对渠道而设置',
  `type` VARCHAR(20) COMMENT '提示词类型（system, instruction, template 等）',
  `status` char(1) NULL DEFAULT '0' COMMENT '状态 启用1，禁用0',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) NULL DEFAULT '0' COMMENT '逻辑删 0-未删除 1-已删除',
  CONSTRAINT `fk_prompt_app_api` foreign key (`app_api_id`) references `aigc_app_api` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Records of aigc_prompt
-- ----------------------------
insert into `aigc_prompt` values(1, 100, NULL, '文档分析师', '你是一个专业的文档分析师，你擅长从文档中提取关键内容并总结分析含义，下面你需要根据用户的问题做出解答。\n\n## 限制\n不要回答和文档无关的内容', 1, 'system', 0, 'admin', CURRENT_TIMESTAMP, 'admin', NULL, 0);

-- ----------------------------
-- Table structure for aigc_message
-- ----------------------------
DROP TABLE IF EXISTS `aigc_message`;
CREATE TABLE `aigc_message`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '消息ID' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `conversation_id` int(11) DEFAULT NULL COMMENT '会话ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `parent_message_id` INT DEFAULT NULL COMMENT '父消息ID',
  `chat_id` varchar(50) NOT NULL COMMENT '子对话ID, UUID',
  `username` varchar(100) DEFAULT NULL COMMENT '用户名',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `role` varchar(10) DEFAULT NULL COMMENT '角色:user,assistant,system',
  `model` varchar(50) DEFAULT NULL COMMENT '模型名称',
  `message` text NULL COMMENT '消息内容',
  `tokens` int(11) DEFAULT NULL COMMENT '令牌数',
  `prompt_tokens` int(11) DEFAULT NULL COMMENT '提示词令牌数',
  `is_final` TINYINT(1) DEFAULT 1 COMMENT '是否为最终回复',
  `status` char(1) NULL DEFAULT '0' COMMENT '状态 启用1，禁用0',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) NULL DEFAULT '0' COMMENT '逻辑删 0-未删除 1-已删除',
  CONSTRAINT `fk_message_conversation` foreign key (`conversation_id`) references `aigc_conversation` (`id`),
  INDEX `idx_message_role`(`role`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '对话消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of aigc_message
-- ----------------------------
INSERT INTO `aigc_message` VALUES (1, 100, NULL, 1, 3, NULL, '8630f400-8456-44f6-a908-6e0bebcad6a2', 'edison', '192.168.40.2', 'USER', NULL, 'mysql外键写在create table里面怎么命名', 0, 0, 1, '0', 'edison', '2025-05-09 10:47:10', NULL, NULL, '0');
INSERT INTO `aigc_message` VALUES (2, 100, NULL, 1, 3, NULL, '8630f400-8456-44f6-a908-6e0bebcad6a2', 'edison', '192.168.40.2', 'ASSISTANT', NULL, '在MySQL中创建表时定义外键约束，推荐遵循以下命名规范和语法格式：\n\n### 1. 外键命名规范（最佳实践）\n\n```sql\nfk_<当前表名>_<关联表名>_<关联字段>\n```\n\n示例：\n```sql\nfk_aigc_docs_slice_aigc_docs_docs_id  -- 关联aigc_docs表的docs_id字段\nfk_aigc_knowledge_embed_store_embed_store_id  -- 关联embed_store表的embed_store_id字段\n```\n\n### 2. 完整CREATE TABLE示例\n\n```sql\nCREATE TABLE aigc_docs_slice (\n    id INT AUTO_INCREMENT PRIMARY KEY,\n    docs_id INT NOT NULL,\n    knowledge_id INT NOT NULL,\n    embed_store_id INT NOT NULL,\n    content TEXT,\n    -- 其他字段...\n    \n    -- 定义外键约束\n    CONSTRAINT fk_aigc_docs_slice_aigc_docs_docs_id \n        FOREIGN KEY (docs_id) REFERENCES aigc_docs(id)\n        ON DELETE CASCADE\n        ON UPDATE CASCADE,\n        \n    CONSTRAINT fk_aigc_docs_slice_aigc_knowledge_knowledge_id\n        FOREIGN KEY (knowledge_id) REFERENCES aigc_knowledge(id)\n        ON DELETE CASCADE\n        ON UPDATE CASCADE,\n        \n    CONSTRAINT fk_aigc_docs_slice_aigc_embed_store_embed_store_id\n        FOREIGN KEY (embed_store_id) REFERENCES aigc_embed_store(id)\n        ON DELETE RESTRICT\n        ON UPDATE CASCADE\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n```\n\n### 3. 关键说明\n\n1. **命名组成**：\n   - `fk_` 前缀明确表示这是外键约束\n   - 当前表名（简写形式）\n   - 关联表名（简写形式）\n   - 关联字段名\n\n2. **动作参数**：\n   ```sql\n   ON DELETE [RESTRICT|CASCADE|SET NULL|NO ACTION]\n   ON UPDATE [RESTRICT|CASCADE|SET NULL|NO ACTION]\n   ```\n   - `CASCADE`：级联操作（慎用）\n   - `RESTRICT`：默认值，禁止操作（推荐）\n   - `SET NULL`：设为NULL（需字段允许NULL）\n\n3. **注意事项**：\n   - 确保关联字段类型完全一致\n   - 关联字段最好有索引（InnoDB会自动为外键创建索引）\n   - 表必须使用InnoDB引擎（MyISAM不支持外键）\n\n### 4. 修改已有表添加外键\n\n```sql\nALTER TABLE aigc_docs_slice\nADD CONSTRAINT fk_aigc_docs_slice_aigc_docs_docs_id\nFOREIGN KEY (docs_id) REFERENCES aigc_docs(id)\nON DELETE CASCADE;\n```\n\n### 5. 查看外键\n\n```sql\n-- 查看表的外键约束\nSELECT \n    TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME, \n    REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME\nFROM\n    INFORMATION_SCHEMA.KEY_COLUMN_USAGE\nWHERE\n    REFERENCED_TABLE_SCHEMA = \'您的数据库名\'\n    AND REFERENCED_TABLE_NAME IS NOT NULL;\n```\n\n这种命名方式既保持了清晰性，又能避免不同表之间的外键命名冲突，是MySQL开发中广泛采用的约定。', 750, 3136, 1, '0', 'edison', '2025-05-09 10:47:46', NULL, NULL, '0');

-- ----------------------------
-- Table structure for aigc_embed_store
-- ----------------------------
DROP TABLE IF EXISTS `aigc_embed_store`;
CREATE TABLE `aigc_embed_store`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '向量库ID' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `name` varchar(100) DEFAULT NULL COMMENT '别名',
  `provider` varchar(100) DEFAULT NULL COMMENT '供应商',
  `host` varchar(100) DEFAULT NULL COMMENT '地址',
  `port` int(11) DEFAULT NULL COMMENT '端口',
  `username` varchar(100) DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `database_name` varchar(100) DEFAULT NULL COMMENT '数据库名称',
  `table_name` varchar(100) DEFAULT NULL COMMENT '表名称',
  `dimension` int(11) DEFAULT NULL COMMENT '向量维数',
  `status` char(1) NULL DEFAULT '0' COMMENT '状态 启用1，禁用0',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) NULL DEFAULT '0' COMMENT '逻辑删 0-未删除 1-已删除',
  INDEX `idx_embed_store_name` (`name`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = 'Embedding向量数据库配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of aigc_embed_store
-- ----------------------------
INSERT INTO `aigc_embed_store` VALUES (1, 100, NULL, 'pgvector库', 'PGVECTOR', '127.0.0.1', 5432, 'postgres', '4N2M4ZmI', 'feng_ai_biz', 'aigc_embed_store_vector', 1024, '0', 'edison', '2025-05-07 09:51:39', 'edison', '2025-06-09 09:09:49', '0');

-- ----------------------------
-- Table structure for aigc_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `aigc_knowledge`;
CREATE TABLE `aigc_knowledge`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '知识ID' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `app_id` int(11) DEFAULT NULL COMMENT '应用ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `embed_store_id` int(11) DEFAULT NULL COMMENT '向量数据库ID',
  `embed_model_id` int(11) DEFAULT NULL COMMENT '向量模型ID',
  `name` varchar(50) DEFAULT NULL COMMENT '知识库名称',
  `des` varchar(255) DEFAULT NULL COMMENT '描述',
  `cover` varchar(255) DEFAULT NULL COMMENT '封面',
  `max_length` int(11) DEFAULT 500 COMMENT '切片最大长度',
  `overlap_size` int(11) DEFAULT 50 COMMENT '切片交叉覆盖大小',
  `slice_mode` varchar(32) DEFAULT 'SENTENCE' COMMENT '切片模式，关联数据字典',
  `status` char(1) NULL DEFAULT '0' COMMENT '状态 启用1，禁用0',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) NULL DEFAULT '0' COMMENT '逻辑删 0-未删除 1-已删除',
  CONSTRAINT `fk_knowledge_app` foreign key (`app_id`) references `aigc_app` (`id`),
  CONSTRAINT `fk_knowledge_embed_store` foreign key (`embed_store_id`) references `aigc_embed_store` (`id`),
  CONSTRAINT `fk_knowledge_embed_model` foreign key (`embed_model_id`) references `aigc_model` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '知识库表，和渠道平级' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of aigc_knowledge
-- ----------------------------
INSERT INTO `aigc_knowledge` VALUES (1, 100, NULL, 1, 3, 1, 57, 'feng-ai知识库', 'feng-ai知识库', 'assitant.png', 500, 50, 'SENTENCE', '0', 'admin', '2025-05-01 08:28:59', 'edison', '2025-06-09 17:53:22', '0');
INSERT INTO `aigc_knowledge` VALUES (4, 100, NULL, 1, NULL, 1, 57, '千帆知识库', NULL, NULL, 500, 50, 'SENTENCE', '0', 'edison', '2025-06-09 08:55:20', 'edison', '2025-06-09 17:53:29', '0');

-- ----------------------------
-- Table structure for aigc_docs
-- ----------------------------
DROP TABLE IF EXISTS `aigc_docs`;
CREATE TABLE `aigc_docs`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '文档ID' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `knowledge_id` int(11) DEFAULT NULL COMMENT '知识库ID',
  `oss_id` int(11) DEFAULT NULL COMMENT '文件ID',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `type` varchar(50) DEFAULT NULL COMMENT '类型，关联数据字典',  
  `origin` varchar(50) DEFAULT NULL COMMENT '来源',
  `content` text NULL COMMENT '内容或链接',
  `size` int(11) DEFAULT NULL COMMENT '内容文本大小',
  `slice_num` int(11) DEFAULT NULL COMMENT '切片数量',
  `max_length` int(11) DEFAULT NULL COMMENT '切片最大长度',
  `overlap_size` int(11) DEFAULT NULL COMMENT '切片交叉覆盖大小',
  `slice_mode` varchar(32) DEFAULT NULL COMMENT '切片模式，关联数据字典',
  `slice_status` tinyint(1) DEFAULT NULL COMMENT '切片状态：0待处理，1处理中，2已完成，3失败',
  `last_slice_time` datetime DEFAULT NULL COMMENT '最后切片时间',
  `status` char(1) NULL DEFAULT '0' COMMENT '状态 启用1，禁用0',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) NULL DEFAULT '0' COMMENT '逻辑删 0-未删除 1-已删除',
  CONSTRAINT `fk_docs_knowledge` foreign key (`knowledge_id`) references `aigc_knowledge` (`id`),
  CONSTRAINT `fk_docs_oss` foreign key (`oss_id`) references `aigc_oss` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '文档表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of aigc_docs
-- ----------------------------
INSERT INTO `aigc_docs` VALUES (1, 100, NULL, 1, NULL, '测试文档1', 'OSS', NULL, NULL, 35359, NULL, 500, 50, 'SENTENCE', 0, NULL, '0', 'admin', '2025-05-01 08:28:59', 'edison', '2025-05-07 09:31:19', '0');
INSERT INTO `aigc_docs` VALUES (2, 100, NULL, 1, NULL, '测试文档2', 'TEXT', NULL, 'feng-ai 是一个基于Java生态的企业AI知识库和大模型应用解决方案，帮助企业快速搭建AI大模型应用。 同时，feng-ai也集成了RBAC权限体系，为企业提供开箱即用的AI大模型产品解决方案。\n\nfeng-ai 使用Java生态，前后端分离，并采用最新的技术栈开发。后端基于SpringBoot3，前端基于Vue3。 feng-ai不仅为企业提供AI领域的产品解决方案，也是一个完整的Java企业级应用案例。这个系统带你全面了解SpringBoot3和Vue3的前后端开发流程、业务模块化，以及AI应用集成方案。 无论是企业开发，还是个人学习，feng-ai都为你提供丰富的学习案例', NULL, 1, 500, 50, 'SENTENCE', 1, NULL, '0', 'admin', '2025-05-01 08:28:59', 'edison', '2025-05-07 09:31:26', '0');

-- ----------------------------
-- Table structure for aigc_docs_slice
-- ----------------------------
DROP TABLE IF EXISTS `aigc_docs_slice`;
CREATE TABLE `aigc_docs_slice`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '切片ID' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `embed_store_id` int(11) DEFAULT NULL COMMENT '向量库的ID，关联aigc_embed_store',
  `vector_id` varchar(64) DEFAULT NULL COMMENT '向量ID，关联pgvector向量表的embedding_id',
  `docs_id` int(11) DEFAULT NULL COMMENT '文档ID',
  `oss_id` int(11) DEFAULT NULL COMMENT '文件ID',
  `knowledge_id` int(11) DEFAULT NULL COMMENT '知识库ID',
  `name` varchar(255) DEFAULT NULL COMMENT '文档名称',
  `slice_index` int(11) DEFAULT NULL COMMENT '切片序号',
  `content_hash` varchar(255) DEFAULT NULL COMMENT '切片哈希值',
  `keywords` varchar(255) DEFAULT NULL COMMENT '关键词提取',
  `summary` varchar(255) DEFAULT NULL COMMENT '摘要',
  `content` text NULL COMMENT '切片内容',
  `word_num` int(11) DEFAULT NULL COMMENT '字符数',
  `is_embedding` tinyint(1) DEFAULT NULL COMMENT '是否向量模型',
  `status` char(1) NULL DEFAULT '0' COMMENT '状态 启用1，禁用0',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) NULL DEFAULT '0' COMMENT '逻辑删 0-未删除 1-已删除',
  CONSTRAINT `fk_docs_slice_embed_store` foreign key (`embed_store_id`) references `aigc_embed_store` (`id`),
  CONSTRAINT `fk_docs_slice_docs` foreign key (`docs_id`) references `aigc_docs` (`id`),
  CONSTRAINT `fk_docs_slice_oss` foreign key (`oss_id`) references `aigc_oss` (`id`),
  CONSTRAINT `fk_docs_slice_knowledge` foreign key (`knowledge_id`) references `aigc_knowledge` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '文档切片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of aigc_docs_slice
-- ----------------------------
INSERT INTO `aigc_docs_slice` VALUES (1,  100, NULL, NULL, '72a72475-d573-45d2-8645-9c93d899ea61', 2, NULL, 1, '测试文档3', NULL, NULL, NULL, NULL, 'feng-ai 是一个基于Java生态的企业AI知识库和大模型应用解决方案，帮助企业快速搭建AI大模型应用。', 54, 0, '0', '3', '2025-06-09 17:36:40', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (2,  100, NULL, NULL, 'a2dcf18b-bf21-4b27-89fc-8614fc3e9eaa', 2, NULL, 1, '测试文档3', NULL, NULL, NULL, NULL, '同时，feng-ai也集成了RBAC权限体系，为企业提供开箱即用的AI大模型产品解决方案。', 45, 0, '0', '3', '2025-06-09 17:36:40', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (3,  100, NULL, NULL, 'e74d3144-4f21-4f53-9dae-3339bd19bc71', 2, NULL, 1, '测试文档3', NULL, NULL, NULL, NULL, 'feng-ai 使用Java生态，前后端分离，并采用最新的技术栈开发。', 35, 0, '0', '3', '2025-06-09 17:36:40', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (4,  100, NULL, NULL, 'ef63b9f1-1cb2-40a6-b1c2-5edd3905d018', 2, NULL, 1, '测试文档3', NULL, NULL, NULL, NULL, '后端基于SpringBoot3，前端基于Vue3。', 25, 0, '0', '3', '2025-06-09 17:36:40', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (5,  100, NULL, NULL, '94e8873e-1052-4cd7-bd9a-5212d04af18e', 2, NULL, 1, '测试文档3', NULL, NULL, NULL, NULL, 'feng-ai不仅为企业提供AI领域的产品解决方案，也是一个完整的Java企业级应用案例。', 45, 0, '0', '3', '2025-06-09 17:36:40', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (6,  100, NULL, NULL, '668be48a-f32a-4c4c-ad12-aa6169bfa263', 2, NULL, 1, '测试文档3', NULL, NULL, NULL, NULL, '这个系统带你全面了解SpringBoot3和Vue3的前后端开发流程、业务模块化，以及AI应用集成方案。', 52, 0, '0', '3', '2025-06-09 17:36:40', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (7,  100, NULL, NULL, 'a22acf88-076b-4f2d-9167-7c33864cf12d', 2, NULL, 1, '测试文档3', NULL, NULL, NULL, NULL, '无论是企业开发，还是个人学习，feng-ai都为你提供丰富的学习案例', 34, 0, '0', '3', '2025-06-09 17:36:40', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (8,  100, NULL, NULL, '6be54e89-2a5c-4059-a197-5e59ac55ce7c', 3, NULL, 4, '文档1-百度千帆知识库', NULL, NULL, NULL, NULL, '关于 RAG 的争论——RAG 已死，RAG 永存 ！', 27, 0, '0', '3', '2025-06-09 19:57:56', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (9,  100, NULL, NULL, 'cf2f38eb-f0f9-498c-b3a4-ca522e20c2d3', 3, NULL, 4, '文档1-百度千帆知识库', NULL, NULL, NULL, NULL, '2024 年在年初被称为 “RAG 发展元年”，虽然这并非共识性的说法，但事实证明，全年的进展无愧于这一称号。', 55, 0, '0', '3', '2025-06-09 19:57:56', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (10, 100, NULL, NULL, '7e430b02-97ae-464b-9d91-cdc7f419d36b', 3, NULL, 4, '文档1-百度千帆知识库', NULL, NULL, NULL, NULL, '在 LLM 使用的场景中，RAG 自始至终都在扮演着不可或缺的重要角色。', 36, 0, '0', '3', '2025-06-09 19:57:56', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (11, 100, NULL, NULL, '31221773-50a8-4901-a23c-15f168d9e77f', 3, NULL, 4, '文档1-百度千帆知识库', NULL, NULL, NULL, NULL, '然而，自诞生以来关于 RAG 的争论就没有停止过。', 25, 0, '0', '3', '2025-06-09 19:57:56', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (12, 100, NULL, NULL, '30178e0e-c8f1-4f2d-b683-25e06f2426f3', 3, NULL, 4, '文档1-百度千帆知识库', NULL, NULL, NULL, NULL, '由上图可以看到，2023 年 RAG 的称呼并不流行，一种看起来就非常临时的说法“外挂记忆体”、“外挂知识库” 是普遍的替代称谓，在当时，主要争论还在于究竟应该用临时的 “外挂” 还是 “永久性的” 微调，这个争论在 2024 年初已经终结：从成本和实时性角度，RAG 具有压倒性优势，而效果上相差也并不大，即使需要微调介入的场景，RAG 通常也不可或缺。', 178, 0, '0', '3', '2025-06-09 19:57:56', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (13, 100, NULL, NULL, '26553ec7-32e5-4b97-aff4-70a54d204fc0', 3, NULL, 4, '文档1-百度千帆知识库', NULL, NULL, NULL, NULL, '2024 年上半年对于 LLM 来说，对产业最重要的影响，就是开源 LLM 的能力逐步接近以 OpenAI 为代表的商业 LLM，这意味着类似摘要、指令跟随等能力相比 2023 年都有了显著提高，正是这种进展，解锁了以问答、客服、知识库为代表的 RAG 初级应用的普及。', 135, 0, '0', '3', '2025-06-09 19:57:56', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (14, 100, NULL, NULL, '8ff72d30-5376-49ee-8347-70cbd5ef1bec', 3, NULL, 4, '文档1-百度千帆知识库', NULL, NULL, NULL, NULL, '2024 年上半年 LLM 的另一个显著进展就是长上下文，它给 RAG 带来的争议伴随了整个上半年，直到年中才逐步偃旗息鼓，跟前一次争议类似，结论两者在能力上各有侧重，同样也是相互配合的关系', 95, 0, '0', '3', '2025-06-09 19:57:56', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (15, 100, NULL, NULL, '914702d9-e968-40d6-8c0d-26beba6a9a13', 4, NULL, 4, '千帆文档2', NULL, NULL, NULL, NULL, 'AI 技术选型\n我们在技术上有三个技术选型 ：Prompt / RAG / 微调。', 41, 0, '0', '3', '2025-06-09 21:08:49', NULL, NULL, '0');
INSERT INTO `aigc_docs_slice` VALUES (16, 100, NULL, NULL, '5758e988-eddf-46fe-a0d0-f3bea4f985c3', 4, NULL, 4, '千帆文档2', NULL, NULL, NULL, NULL, '• Prompt：效果略有提升，但是不能带来本质改变；\n• 微调：成本比较高，我们的数据还不断迭代过程中无法承受；\n• RAG：无论是成本、效果，还是可持续迭代性，都是目前最高投入产出比模式，因此我们采用了 RAG 为主的技术方案。', 116, 0, '0', '3', '2025-06-09 21:08:49', NULL, NULL, '0');

-- ----------------------------
-- Table structure for aigc_oss
-- ----------------------------
DROP TABLE IF EXISTS `aigc_oss`;
CREATE TABLE `aigc_oss`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '资源ID' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `knowledge_id` int(11) DEFAULT NULL COMMENT '文档ID',
  `doc_id` int(11) DEFAULT NULL COMMENT '文档ID',
  `oss_id` varchar(64) DEFAULT NULL COMMENT 'OSS服务中对象ID，若无则空',
  `file_id` varchar(64) DEFAULT NULL COMMENT '文件ID',
  `original_filename` varchar(50) DEFAULT NULL COMMENT '原始文件名称',
  `filename` varchar(50) DEFAULT NULL COMMENT '文件存储名称',
  `url` varchar(100) DEFAULT NULL COMMENT '文件地址',
  `base_path` varchar(100) DEFAULT NULL COMMENT '桶路径',
  `path` varchar(100) DEFAULT NULL COMMENT '文件的绝对路径',
  `size` int(11) DEFAULT NULL COMMENT '文件大小',
  `ext` varchar(50) DEFAULT NULL COMMENT '文件后缀',
  `content_type` varchar(100) DEFAULT NULL COMMENT '文件头',
  `extract_content` text NULL COMMENT '文件抽取内容',
  `platform` varchar(50) DEFAULT NULL COMMENT '平台',
  `slice_num` int(11) DEFAULT NULL COMMENT '预拆分数量',
  `max_length` int(11) DEFAULT NULL COMMENT '预拆分最大长度',
  `overlap_size` int(11) DEFAULT NULL COMMENT '预拆分交叉覆盖大小',
  `slice_mode` varchar(32) DEFAULT NULL COMMENT '预拆分模式,保留',
  `status` char(1) NULL DEFAULT '0' COMMENT '状态 启用1，禁用0',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) NULL DEFAULT '0' COMMENT '逻辑删 0-未删除 1-已删除',
  CONSTRAINT `fk_oss_doc` foreign key (`doc_id`) references `aigc_docs` (`id`),
  CONSTRAINT `fk_oss_knowledge` foreign key (`knowledge_id`) references `aigc_knowledge` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '资源文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of aigc_oss
-- ----------------------------


SET FOREIGN_KEY_CHECKS = 1;

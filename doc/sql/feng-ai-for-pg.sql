-- Set client encoding
SET client_encoding = 'UTF8';

-- Drop database if exists and create new one
DROP DATABASE IF EXISTS "feng_ai_biz";
CREATE DATABASE "feng_ai_biz" WITH ENCODING = 'UTF8' LC_COLLATE = 'en_US.utf8' LC_CTYPE = 'en_US.utf8';

-- Connect to the database 手工切换到feng_ai_biz数据库
--CONNECT TO "feng_ai_biz";
\c "feng_ai_biz";

-- Create extension for UUID if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ----------------------------
-- Table structure for aigc_model
-- ----------------------------
DROP TABLE IF EXISTS aigc_model CASCADE;
CREATE TABLE aigc_model (
  id SERIAL PRIMARY KEY,
  type VARCHAR(100) NULL,
  model VARCHAR(100) NULL,
  provider VARCHAR(100) NULL,
  name VARCHAR(100) NULL,
  response_limit INTEGER NULL,
  temperature DOUBLE PRECISION NULL,
  top_p DOUBLE PRECISION NULL,
  api_key VARCHAR(1024) NULL,
  base_url VARCHAR(100) NULL,
  secret_key VARCHAR(100) NULL,
  endpoint VARCHAR(100) NULL,
  azure_deployment_name VARCHAR(100) NULL,
  gemini_project VARCHAR(100) NULL,
  gemini_location VARCHAR(100) NULL,
  image_size VARCHAR(50) NULL,
  image_quality VARCHAR(50) NULL,
  image_style VARCHAR(50) NULL,
  dimension INTEGER NULL,
  status CHAR(1) DEFAULT '0',
  create_by VARCHAR(64) NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64) NULL,
  update_time TIMESTAMP NULL,
  del_flag CHAR(1) DEFAULT '0'
);

COMMENT ON TABLE aigc_model IS 'LLM模型配置表';
COMMENT ON COLUMN aigc_model.id IS '模型ID';
COMMENT ON COLUMN aigc_model.type IS '类型: CHAT、Embedding、Image';
COMMENT ON COLUMN aigc_model.model IS '模型名称';
COMMENT ON COLUMN aigc_model.provider IS '供应商';
COMMENT ON COLUMN aigc_model.name IS '别名';
COMMENT ON COLUMN aigc_model.response_limit IS '响应长度';
COMMENT ON COLUMN aigc_model.temperature IS '温度';
COMMENT ON COLUMN aigc_model.top_p IS '候选词百分比';
COMMENT ON COLUMN aigc_model.api_key IS '模型key';
COMMENT ON COLUMN aigc_model.base_url IS '模型基准链接';
COMMENT ON COLUMN aigc_model.secret_key IS '模型密钥';
COMMENT ON COLUMN aigc_model.endpoint IS '模型端点';
COMMENT ON COLUMN aigc_model.azure_deployment_name IS 'azure模型参数';
COMMENT ON COLUMN aigc_model.gemini_project IS 'gemini模型参数-项目名称';
COMMENT ON COLUMN aigc_model.gemini_location IS 'gemini模型参数-服务器位置';
COMMENT ON COLUMN aigc_model.image_size IS '图片大小';
COMMENT ON COLUMN aigc_model.image_quality IS '图片质量';
COMMENT ON COLUMN aigc_model.image_style IS '图片风格';
COMMENT ON COLUMN aigc_model.dimension IS '向量维数';
COMMENT ON COLUMN aigc_model.status IS '状态 1启用 0禁用';
COMMENT ON COLUMN aigc_model.create_by IS '创建者';
COMMENT ON COLUMN aigc_model.create_time IS '创建时间';
COMMENT ON COLUMN aigc_model.update_by IS '更新者';
COMMENT ON COLUMN aigc_model.update_time IS '修改时间';
COMMENT ON COLUMN aigc_model.del_flag IS '逻辑删 0-未删除 1-已删除';

CREATE INDEX idx_model_name ON aigc_model(name);

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_aigc_model_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_aigc_model_timestamp
BEFORE UPDATE ON aigc_model
FOR EACH ROW
EXECUTE FUNCTION update_aigc_model_timestamp();

-- ----------------------------
-- Records of aigc_model
-- ----------------------------
INSERT INTO aigc_model (id, type, model, provider, name, response_limit, temperature, top_p, api_key, base_url, secret_key, endpoint, azure_deployment_name, gemini_project, gemini_location, image_size, image_quality, image_style, dimension, status, create_by, create_time, update_by, update_time, del_flag) VALUES 
(1, 'CHAT', 'deepseek-chat', 'DEEPSEEK', 'deepseek-chat', 8192, 0, 0.8, 'sk-', 'https://api.deepseek.com/v1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-26 00:08:28', 'admin', '2025-04-28 16:16:35', '0'),
(2, 'CHAT', 'deepseek-coder', 'DEEPSEEK', 'deepseek-coder', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:16:42', '0'),
(3, 'CHAT', 'gpt-3.5-turbo', 'OPENAI', 'OpenAI-gpt-3.5-turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(4, 'CHAT', 'gpt-4', 'OPENAI', 'OpenAI-gpt-4', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(5, 'CHAT', 'gpt-4-32k', 'OPENAI', 'OpenAI-gpt-4-32k', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(6, 'CHAT', 'gpt-4-turbo', 'OPENAI', 'OpenAI-gpt-4-turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(7, 'CHAT', 'gpt-4o', 'OPENAI', 'OpenAI-gpt-4o', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(8, 'CHAT', 'ERNIE-Bot', 'BAIDU', '百度千帆-ERNIE-Bot', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(9, 'CHAT', 'ERNIE-Bot 4.0', 'BAIDU', '百度千帆-ERNIE-Bot 4.0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(10, 'CHAT', 'ERNIE-Bot-8K', 'BAIDU', '百度千帆-ERNIE-Bot-8K', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(11, 'CHAT', 'ERNIE-Bot-turbo', 'BAIDU', '百度千帆-ERNIE-Bot-turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(12, 'CHAT', 'ERNIE-Speed-128K', 'BAIDU', '百度千帆-ERNIE-Speed-128K', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(13, 'CHAT', 'EB-turbo-AppBuilder', 'BAIDU', '百度千帆-EB-turbo-AppBuilder', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(14, 'CHAT', 'Yi-34B-Chat', 'BAIDU', '百度千帆-Yi-34B-Chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(15, 'CHAT', 'BLOOMZ-7B', 'BAIDU', '百度千帆-BLOOMZ-7B', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(16, 'CHAT', 'Qianfan-BLOOMZ-7B-compressed', 'BAIDU', '百度千帆-Qianfan-BLOOMZ-7B-compressed', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(17, 'CHAT', 'Mixtral-8x7B-Instruct', 'BAIDU', '百度千帆-Mixtral-8x7B-Instruct', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(18, 'CHAT', 'Llama-2-7b-chat', 'BAIDU', '百度千帆-Llama-2-7b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(19, 'CHAT', 'Llama-2-13b-chat', 'BAIDU', '百度千帆-Llama-2-13b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(20, 'CHAT', 'Llama-2-70b-chat', 'BAIDU', '百度千帆-Llama-2-70b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(21, 'CHAT', 'Qianfan-Chinese-Llama-2-7B', 'BAIDU', '百度千帆-Qianfan-Chinese-Llama-2-7B', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(22, 'CHAT', 'ChatGLM2-6B-32K', 'BAIDU', '百度千帆-ChatGLM2-6B-32K', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(23, 'CHAT', 'AquilaChat-7B', 'BAIDU', '百度千帆-AquilaChat-7B', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(24, 'CHAT', 'qwen-turbo', 'ALICLOUD', '阿里百炼-qwen-turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(25, 'CHAT', 'qwen-plus', 'ALICLOUD', '阿里百炼-qwen-plus', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(26, 'CHAT', 'qwen-max', 'ALICLOUD', '阿里百炼-qwen-max', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(27, 'CHAT', 'qwen-max-longcontext', 'ALICLOUD', '阿里百炼-qwen-max-longcontext', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(28, 'CHAT', 'qwen-7b-chat', 'ALICLOUD', '阿里百炼-qwen-7b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(29, 'CHAT', 'qwen-14b-chat', 'ALICLOUD', '阿里百炼-qwen-14b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(30, 'CHAT', 'qwen-72b-chat', 'ALICLOUD', '阿里百炼-qwen-72b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(31, 'CHAT', 'glm-4', 'ZHIPU', '智谱清言-glm-4', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(32, 'CHAT', 'glm-4v', 'ZHIPU', '智谱清言-glm-4v', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(33, 'CHAT', 'glm-4-air', 'ZHIPU', '智谱清言-glm-4-air', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(34, 'CHAT', 'glm-4-airx', 'ZHIPU', '智谱清言-glm-4-airx', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(35, 'CHAT', 'glm-4-flash', 'ZHIPU', '智谱清言-glm-4-flash', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(36, 'CHAT', 'glm-3-turbo', 'ZHIPU', '智谱清言-glm-3-turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(37, 'CHAT', 'chatglm_turbo', 'ZHIPU', '智谱清言-chatglm_turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(38, 'CHAT', 'Qwen2-72B-Instruct', 'BAAI', 'Gitee AI-Qwen2-72B-Instruct', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(39, 'CHAT', 'Qwen2-7B-Instruct', 'BAAI', 'Gitee AI-Qwen2-7B-Instruct', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(40, 'CHAT', 'Qwen2.5-72B-Instruct', 'BAAI', 'Gitee AI-Qwen2.5-72B-Instruct', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(41, 'CHAT', 'glm-4-9b-chat', 'BAAI', 'Gitee AI-glm-4-9b-chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(42, 'CHAT', 'deepseek-coder-33B-instruct', 'BAAI', 'Gitee AI-deepseek-coder-33B-instruct', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(43, 'CHAT', 'codegeex4-all-9b', 'BAAI', 'Gitee AI-codegeex4-all-9b', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(44, 'CHAT', 'Yi-34B-Chat', 'BAAI', 'Gitee AI-Yi-34B-Chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(45, 'CHAT', 'code-raccoon-v1', 'BAAI', 'Gitee AI-code-raccoon-v1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(46, 'CHAT', 'Qwen2.5-Coder-32B-Instruct', 'BAAI', 'Gitee AI-Qwen2.5-Coder-32B-Instruct', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:11:46', NULL, '2025-04-28 16:12:27', '0'),
(47, 'CHAT', 'deepseek-ai/DeepSeek-V2-Chat', 'DEEPSEEK', '硅基流动-DeepSeek-V2-Chat', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:16:08', '0'),
(48, 'CHAT', 'yi-lightning', 'OTHER', '零一万物-yi-lightning', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:14:05', '0'),
(49, 'CHAT', 'yi-large', 'OTHER', '零一万物-yi-large', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:14:11', '0'),
(50, 'CHAT', 'lite', 'XUNFEI', '讯飞星火-lite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:14:19', '0'),
(51, 'CHAT', 'generalv3', 'XUNFEI', '讯飞星火-generalv3', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:14:25', '0'),
(52, 'CHAT', 'gpt-3.5-turbo', 'OPENAI', 'Azure OpenAI-gpt-3.5-turbo', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:14:32', '0'),
(53, 'CHAT', 'gemini-1.5-pro', 'GOOGLE', 'gemini-1.5-pro', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:14:54', '0'),
(54, 'CHAT', 'claude-3-opus', 'CLAUDE', 'claude-3-opus', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'admin', '2025-04-28 16:13:23', NULL, '2025-04-28 16:15:41', '0'),
(55, 'EMBEDDING', 'text-embedding-3-small', 'OPENAI', 'OpenAI-text-embedding-3-small', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:27:25', NULL, NULL, '0'),
(56, 'EMBEDDING', 'text-embedding-3-large', 'OPENAI', 'OpenAI-text-embedding-3-large', NULL, 0.2, 0, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:27:57', 'edison', '2025-05-06 10:28:14', '0'),
(57, 'EMBEDDING', 'bge-large-zh', 'BAIDU', '百度千帆-bge-large-zh', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:28:56', NULL, NULL, '0'),
(58, 'EMBEDDING', 'bge-large-en', 'BAIDU', '百度千帆bge-large-en', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:29:19', NULL, NULL, '0'),
(59, 'EMBEDDING', 'tao-8k', 'BAIDU', '百度千帆-tao-8k', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:30:08', NULL, NULL, '0'),
(60, 'EMBEDDING', 'text-embedding-v3', 'ALICLOUD', '阿里百炼-text-embedding-v3', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:30:56', NULL, NULL, '0'),
(61, 'EMBEDDING', 'embedding-2', 'ZHIPU', '智谱清言-embedding-2', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:31:28', NULL, NULL, '0'),
(62, 'EMBEDDING', 'embedding-3', 'ZHIPU', '智谱清言embedding-3', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:32:00', NULL, NULL, '0'),
(63, 'EMBEDDING', 'text-240715', 'DOUBAO', '抖音豆包text-240715', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:32:42', NULL, NULL, '0'),
(64, 'EMBEDDING', 'text-240515', 'DOUBAO', '抖音豆包text-240515', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:33:11', NULL, NULL, '0'),
(65, 'EMBEDDING', 'text2vec-bge-large-chinese:latest', 'META', 'text2vec-bge-large-chinese:latest', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:33:37', 'edison', '2025-05-06 10:36:33', '0'),
(66, 'TEXT_IMAGE', 'dall-e-2', 'OPENAI', 'openai-dall-e-2', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:38:04', NULL, NULL, '0'),
(67, 'TEXT_IMAGE', 'dall-e-3', 'OPENAI', 'openai-dall-e-3', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:38:29', 'edison', '2025-05-06 10:39:00', '0'),
(68, 'TEXT_IMAGE', 'cogview-3', 'ZHIPU', '智谱清言-cogview-3', NULL, 0.2, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', 'edison', '2025-05-06 10:38:51', NULL, NULL, '0');



-- ----------------------------
-- Table structure for aigc_app
-- ----------------------------
DROP TABLE IF EXISTS aigc_app CASCADE;
CREATE TABLE aigc_app (
  id SERIAL PRIMARY KEY,
  model_id INTEGER NULL,
  cover VARCHAR(255) NULL,
  name VARCHAR(50) NULL,
  des VARCHAR(255) NULL,
  status CHAR(1) DEFAULT '0',
  create_by VARCHAR(64) NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64) NULL,
  update_time TIMESTAMP NULL,
  del_flag CHAR(1) DEFAULT '0',
  CONSTRAINT fk_app_model FOREIGN KEY (model_id) REFERENCES aigc_model(id)
);

COMMENT ON TABLE aigc_app IS 'AI应用表';
COMMENT ON COLUMN aigc_app.id IS 'AI应用ID';
COMMENT ON COLUMN aigc_app.model_id IS '关联模型';
COMMENT ON COLUMN aigc_app.cover IS '封面';
COMMENT ON COLUMN aigc_app.name IS '名称';
COMMENT ON COLUMN aigc_app.des IS '描述';
COMMENT ON COLUMN aigc_app.status IS '状态 1启用 0禁用';
COMMENT ON COLUMN aigc_app.create_by IS '创建者';
COMMENT ON COLUMN aigc_app.create_time IS '创建时间';
COMMENT ON COLUMN aigc_app.update_by IS '更新者';
COMMENT ON COLUMN aigc_app.update_time IS '修改时间';
COMMENT ON COLUMN aigc_app.del_flag IS '逻辑删 0-未删除 1-已删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_aigc_app_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_aigc_app_timestamp
BEFORE UPDATE ON aigc_app
FOR EACH ROW
EXECUTE FUNCTION update_aigc_app_timestamp();

-- ----------------------------
-- Records of aigc_app
-- ----------------------------
INSERT INTO aigc_app (id, model_id, cover, name, des, status, create_by, create_time, update_by, update_time, del_flag) 
VALUES (1, 1, 'assitant.png', 'feng_ai_biz应用实例', '快速解答feng_ai_biz项目相关的内容，feng_ai_biz官方助手', '0', 'admin', '2025-05-01 08:28:59', 'edison', '2025-05-07 09:25:59', '0');


-- ----------------------------
-- Table structure for aigc_app_api
-- ----------------------------
DROP TABLE IF EXISTS aigc_app_api CASCADE;
CREATE TABLE aigc_app_api (
  id SERIAL PRIMARY KEY,
  app_id INTEGER NULL,
  channel VARCHAR(50) NULL,
  api_key VARCHAR(50) NULL,
  status CHAR(1) DEFAULT '0',
  create_by VARCHAR(64) NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64) NULL,
  update_time TIMESTAMP NULL,
  del_flag CHAR(1) DEFAULT '0',
  CONSTRAINT fk_app_api_app FOREIGN KEY (app_id) REFERENCES aigc_app(id)
);

COMMENT ON TABLE aigc_app_api IS '应用渠道';
COMMENT ON COLUMN aigc_app_api.id IS '渠道ID';
COMMENT ON COLUMN aigc_app_api.app_id IS '应用ID';
COMMENT ON COLUMN aigc_app_api.channel IS '应用渠道：web-Web端访问，wechat-微信小程序渠道，api-第三方调用接口，app-移动端APP，custom_xxx-某客户定制的专属接入（如嵌入内网），robot-内部聊天机器人';
COMMENT ON COLUMN aigc_app_api.api_key IS 'Key';
COMMENT ON COLUMN aigc_app_api.status IS '状态 1启用 0禁用';
COMMENT ON COLUMN aigc_app_api.create_by IS '创建者';
COMMENT ON COLUMN aigc_app_api.create_time IS '创建时间';
COMMENT ON COLUMN aigc_app_api.update_by IS '更新者';
COMMENT ON COLUMN aigc_app_api.update_time IS '修改时间';
COMMENT ON COLUMN aigc_app_api.del_flag IS '逻辑删 0-未删除 1-已删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_aigc_app_api_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_aigc_app_api_timestamp
BEFORE UPDATE ON aigc_app_api
FOR EACH ROW
EXECUTE FUNCTION update_aigc_app_api_timestamp();

-- ----------------------------
-- Records of aigc_app_api
-- ----------------------------
INSERT INTO aigc_app_api (id, app_id, channel, api_key, status, create_by, create_time, update_by, update_time, del_flag) 
VALUES (1, 1, 'web', 'sk-bc782e31d3814b9db99e2c38cad64118', '0', 'admin', '2025-05-01 08:28:59', 'admin', NULL, '0');



-- ----------------------------
-- Table structure for aigc_embed_store
-- ----------------------------
DROP TABLE IF EXISTS aigc_embed_store CASCADE;
CREATE TABLE aigc_embed_store (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NULL,
  provider VARCHAR(100) NULL,
  host VARCHAR(100) NULL,
  port INTEGER NULL,
  username VARCHAR(100) NULL,
  password VARCHAR(100) NULL,
  database_name VARCHAR(100) NULL,
  table_name VARCHAR(100) NULL,
  dimension INTEGER NULL,
  status CHAR(1) DEFAULT '0',
  create_by VARCHAR(64) NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64) NULL,
  update_time TIMESTAMP NULL,
  del_flag CHAR(1) DEFAULT '0'
);

COMMENT ON TABLE aigc_embed_store IS 'Embedding向量数据库配置表';
COMMENT ON COLUMN aigc_embed_store.id IS '向量库ID';
COMMENT ON COLUMN aigc_embed_store.name IS '别名';
COMMENT ON COLUMN aigc_embed_store.provider IS '供应商';
COMMENT ON COLUMN aigc_embed_store.host IS '地址';
COMMENT ON COLUMN aigc_embed_store.port IS '端口';
COMMENT ON COLUMN aigc_embed_store.username IS '用户名';
COMMENT ON COLUMN aigc_embed_store.password IS '密码';
COMMENT ON COLUMN aigc_embed_store.database_name IS '数据库名称';
COMMENT ON COLUMN aigc_embed_store.table_name IS '表名称';
COMMENT ON COLUMN aigc_embed_store.dimension IS '向量维数';
COMMENT ON COLUMN aigc_embed_store.status IS '状态 1启用 0禁用';
COMMENT ON COLUMN aigc_embed_store.create_by IS '创建者';
COMMENT ON COLUMN aigc_embed_store.create_time IS '创建时间';
COMMENT ON COLUMN aigc_embed_store.update_by IS '更新者';
COMMENT ON COLUMN aigc_embed_store.update_time IS '修改时间';
COMMENT ON COLUMN aigc_embed_store.del_flag IS '逻辑删 0-未删除 1-已删除';

CREATE INDEX idx_embed_store_name ON aigc_embed_store(name);

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_aigc_embed_store_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_aigc_embed_store_timestamp
BEFORE UPDATE ON aigc_embed_store
FOR EACH ROW
EXECUTE FUNCTION update_aigc_embed_store_timestamp();

-- ----------------------------
-- Records of aigc_embed_store
-- ----------------------------
INSERT INTO aigc_embed_store (id, name, provider, host, port, username, password, database_name, table_name, dimension, status, create_by, create_time, update_by, update_time, del_flag)
VALUES (1, 'pgvector库', 'PGVECTOR', '127.0.0.1', 5432, 'postgres', '4N2M4ZmI', 'feng_ai_biz', 'aigc_embed_store_vector', 1024, '0', 'edison', '2025-05-07 09:51:39', NULL, NULL, '0');


-- ----------------------------
-- Table structure for aigc_knowledge
-- ----------------------------
DROP TABLE IF EXISTS aigc_knowledge CASCADE;
CREATE TABLE aigc_knowledge (
  id SERIAL PRIMARY KEY,
  app_id INTEGER NULL,
  user_id INTEGER NULL,
  embed_store_id INTEGER NULL,
  embed_model_id INTEGER NULL,
  name VARCHAR(50) NULL,
  des VARCHAR(255) NULL,
  cover VARCHAR(255) NULL,
  status CHAR(1) DEFAULT '0',
  create_by VARCHAR(64) NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64) NULL,
  update_time TIMESTAMP NULL,
  del_flag CHAR(1) DEFAULT '0',
  CONSTRAINT fk_knowledge_app FOREIGN KEY (app_id) REFERENCES aigc_app(id),
  CONSTRAINT fk_knowledge_embed_store FOREIGN KEY (embed_store_id) REFERENCES aigc_embed_store(id),
  CONSTRAINT fk_knowledge_embed_model FOREIGN KEY (embed_model_id) REFERENCES aigc_model(id)
);

COMMENT ON TABLE aigc_knowledge IS '知识库表，和渠道平级';
COMMENT ON COLUMN aigc_knowledge.id IS '知识ID';
COMMENT ON COLUMN aigc_knowledge.app_id IS '应用ID';
COMMENT ON COLUMN aigc_knowledge.user_id IS '用户ID';
COMMENT ON COLUMN aigc_knowledge.embed_store_id IS '向量数据库ID';
COMMENT ON COLUMN aigc_knowledge.embed_model_id IS '向量模型ID';
COMMENT ON COLUMN aigc_knowledge.name IS '知识库名称';
COMMENT ON COLUMN aigc_knowledge.des IS '描述';
COMMENT ON COLUMN aigc_knowledge.cover IS '封面';
COMMENT ON COLUMN aigc_knowledge.status IS '状态 1启用 0禁用';
COMMENT ON COLUMN aigc_knowledge.create_by IS '创建者';
COMMENT ON COLUMN aigc_knowledge.create_time IS '创建时间';
COMMENT ON COLUMN aigc_knowledge.update_by IS '更新者';
COMMENT ON COLUMN aigc_knowledge.update_time IS '修改时间';
COMMENT ON COLUMN aigc_knowledge.del_flag IS '逻辑删 0-未删除 1-已删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_aigc_knowledge_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_aigc_knowledge_timestamp
BEFORE UPDATE ON aigc_knowledge
FOR EACH ROW
EXECUTE FUNCTION update_aigc_knowledge_timestamp();

-- ----------------------------
-- Records of aigc_knowledge
-- ----------------------------
INSERT INTO aigc_knowledge (id, app_id, user_id, embed_store_id, embed_model_id, name, des, cover, status, create_by, create_time, update_by, update_time, del_flag)
VALUES (1, 1, 3, 1, 56, 'feng_ai_biz知识库', 'feng_ai_biz知识库', 'assitant.png', '0', 'admin', '2025-05-01 08:28:59', 'edison', '2025-05-07 09:54:21', '0');




-- ----------------------------
-- Table structure for aigc_conversation
-- ----------------------------
DROP TABLE IF EXISTS aigc_conversation CASCADE;
CREATE TABLE aigc_conversation (
  id SERIAL PRIMARY KEY,
  app_api_id INTEGER NULL,
  knowledge_id INTEGER NULL,
  user_id INTEGER NULL,
  title VARCHAR(100) NULL,
  chat_total INTEGER DEFAULT 0,
  token_used INTEGER DEFAULT 0,
  end_time TIMESTAMP NULL,
  status CHAR(1) DEFAULT '0',
  create_by VARCHAR(64) NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64) NULL,
  update_time TIMESTAMP NULL,
  del_flag CHAR(1) DEFAULT '0',
  CONSTRAINT fk_conversation_app_api FOREIGN KEY (app_api_id) REFERENCES aigc_app_api(id),
  CONSTRAINT fk_conversation_knowledge FOREIGN KEY (knowledge_id) REFERENCES aigc_knowledge(id)
);

COMMENT ON TABLE aigc_conversation IS '对话窗口表';
COMMENT ON COLUMN aigc_conversation.id IS '对话ID';
COMMENT ON COLUMN aigc_conversation.app_api_id IS '用户渠道ID';
COMMENT ON COLUMN aigc_conversation.knowledge_id IS '知识库ID，为空表示普通对话聊天';
COMMENT ON COLUMN aigc_conversation.user_id IS '用户ID';
COMMENT ON COLUMN aigc_conversation.title IS '标题';
COMMENT ON COLUMN aigc_conversation.chat_total IS '对话条数';
COMMENT ON COLUMN aigc_conversation.token_used IS 'Token 消耗';
COMMENT ON COLUMN aigc_conversation.end_time IS '最后一次对话时间';
COMMENT ON COLUMN aigc_conversation.status IS '状态 1启用 0禁用';
COMMENT ON COLUMN aigc_conversation.create_by IS '创建者';
COMMENT ON COLUMN aigc_conversation.create_time IS '创建时间';
COMMENT ON COLUMN aigc_conversation.update_by IS '更新者';
COMMENT ON COLUMN aigc_conversation.update_time IS '修改时间';
COMMENT ON COLUMN aigc_conversation.del_flag IS '逻辑删 0-未删除 1-已删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_aigc_conversation_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_aigc_conversation_timestamp
BEFORE UPDATE ON aigc_conversation
FOR EACH ROW
EXECUTE FUNCTION update_aigc_conversation_timestamp();

-- ----------------------------
-- Records of aigc_conversation
-- ----------------------------
INSERT INTO aigc_conversation (id, app_api_id, knowledge_id, user_id, title, chat_total, token_used, end_time, status, create_by, create_time, update_by, update_time, del_flag) 
VALUES 
(1, 1, NULL, 3, '文档分析师', 0, 0, NULL, '0', 'admin', '2025-05-01 08:28:59', 'admin', NULL, '0'),
(2, 1, 1, 3, '知识库查询', 0, 0, NULL, '0', 'admin', '2025-05-01 08:28:59', 'admin', NULL, '0'),
(3, 1, NULL, 3, '程序员', 0, 0, NULL, '0', 'admin', '2025-05-01 08:28:59', 'admin', NULL, '0');



-- ----------------------------
-- Table structure for aigc_prompt
-- ----------------------------
DROP TABLE IF EXISTS aigc_prompt CASCADE;
CREATE TABLE aigc_prompt (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  content TEXT NOT NULL,
  app_api_id INTEGER NULL,
  type VARCHAR(20),
  status CHAR(1) DEFAULT '0',
  create_by VARCHAR(64) NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64) NULL,
  update_time TIMESTAMP NULL,
  del_flag CHAR(1) DEFAULT '0',
  CONSTRAINT fk_prompt_app_api FOREIGN KEY (app_api_id) REFERENCES aigc_app_api(id)
);

COMMENT ON TABLE aigc_prompt IS '提示词表';
COMMENT ON COLUMN aigc_prompt.id IS '提示词ID';
COMMENT ON COLUMN aigc_prompt.name IS '提示词名称';
COMMENT ON COLUMN aigc_prompt.content IS '提示内容';
COMMENT ON COLUMN aigc_prompt.app_api_id IS '应用渠道ID，提示语针对渠道而设置';
COMMENT ON COLUMN aigc_prompt.type IS '提示词类型（system, instruction, template 等）';
COMMENT ON COLUMN aigc_prompt.status IS '状态 1启用 0禁用';
COMMENT ON COLUMN aigc_prompt.create_by IS '创建者';
COMMENT ON COLUMN aigc_prompt.create_time IS '创建时间';
COMMENT ON COLUMN aigc_prompt.update_by IS '更新者';
COMMENT ON COLUMN aigc_prompt.update_time IS '修改时间';
COMMENT ON COLUMN aigc_prompt.del_flag IS '逻辑删 0-未删除 1-已删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_aigc_prompt_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_aigc_prompt_timestamp
BEFORE UPDATE ON aigc_prompt
FOR EACH ROW
EXECUTE FUNCTION update_aigc_prompt_timestamp();

-- ----------------------------
-- Records of aigc_prompt
-- ----------------------------
INSERT INTO aigc_prompt (id, name, content, app_api_id, type, status, create_by, create_time, update_by, update_time, del_flag)
VALUES (1, '文档分析师', '你是一个专业的文档分析师，你擅长从文档中提取关键内容并总结分析含义，下面你需要根据用户的问题做出解答。\n\n## 限制\n不要回答和文档无关的内容', 1, 'system', '0', 'admin', CURRENT_TIMESTAMP, 'admin', NULL, '0');



-- ----------------------------
-- Table structure for aigc_message
-- ----------------------------
DROP TABLE IF EXISTS aigc_message CASCADE;
CREATE TABLE aigc_message (
  id SERIAL PRIMARY KEY,
  conversation_id INTEGER NULL,
  user_id INTEGER NULL,
  parent_message_id INTEGER DEFAULT NULL,
  chat_id VARCHAR(50) NOT NULL,
  username VARCHAR(100) NULL,
  ip VARCHAR(50) NULL,
  role VARCHAR(10) NULL,
  model VARCHAR(50) NULL,
  message TEXT NULL,
  tokens INTEGER NULL,
  prompt_tokens INTEGER NULL,
  is_final BOOLEAN DEFAULT TRUE,
  status CHAR(1) DEFAULT '0',
  create_by VARCHAR(64) NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64) NULL,
  update_time TIMESTAMP NULL,
  del_flag CHAR(1) DEFAULT '0',
  CONSTRAINT fk_message_conversation FOREIGN KEY (conversation_id) REFERENCES aigc_conversation(id)
);

COMMENT ON TABLE aigc_message IS '对话消息表';
COMMENT ON COLUMN aigc_message.id IS '消息ID';
COMMENT ON COLUMN aigc_message.conversation_id IS '会话ID';
COMMENT ON COLUMN aigc_message.user_id IS '用户ID';
COMMENT ON COLUMN aigc_message.parent_message_id IS '父消息ID';
COMMENT ON COLUMN aigc_message.chat_id IS '子对话ID, UUID';
COMMENT ON COLUMN aigc_message.username IS '用户名';
COMMENT ON COLUMN aigc_message.ip IS 'IP地址';
COMMENT ON COLUMN aigc_message.role IS '角色:user,assistant,system';
COMMENT ON COLUMN aigc_message.model IS '模型名称';
COMMENT ON COLUMN aigc_message.message IS '消息内容';
COMMENT ON COLUMN aigc_message.tokens IS '令牌数';
COMMENT ON COLUMN aigc_message.prompt_tokens IS '提示词令牌数';
COMMENT ON COLUMN aigc_message.is_final IS '是否为最终回复';
COMMENT ON COLUMN aigc_message.status IS '状态 1启用 0禁用，当回复异常时，填1';
COMMENT ON COLUMN aigc_message.create_by IS '创建者';
COMMENT ON COLUMN aigc_message.create_time IS '创建时间';
COMMENT ON COLUMN aigc_message.update_by IS '更新者';
COMMENT ON COLUMN aigc_message.update_time IS '修改时间';
COMMENT ON COLUMN aigc_message.del_flag IS '逻辑删 0-未删除 1-已删除';

-- Create index for role
CREATE INDEX idx_message_role ON aigc_message(role);

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_aigc_message_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_aigc_message_timestamp
BEFORE UPDATE ON aigc_message
FOR EACH ROW
EXECUTE FUNCTION update_aigc_message_timestamp();

-- ----------------------------
-- Records of aigc_message
-- ----------------------------
INSERT INTO aigc_message (id, conversation_id, user_id, parent_message_id, chat_id, username, ip, role, model, message, tokens, prompt_tokens, is_final, status, create_by, create_time, update_by, update_time, del_flag) 
VALUES 
(1, 1, 3, NULL, '8630f400-8456-44f6-a908-6e0bebcad6a2', 'edison', '192.168.40.2', 'user', NULL, 'mysql外键写在create table里面怎么命名', 0, 0, TRUE, '0', 'edison', '2025-05-09 10:47:10', NULL, NULL, '0'),
(2, 1, 3, NULL, '8630f400-8456-44f6-a908-6e0bebcad6a2', 'edison', '192.168.40.2', 'assistant', NULL, '在MySQL中创建表时定义外键约束，推荐遵循以下命名规范和语法格式：', 0, 0, TRUE, '0', 'edison', '2025-05-09 10:47:12', NULL, NULL, '0');


-- ----------------------------
-- Table structure for aigc_docs
-- ----------------------------
DROP TABLE IF EXISTS aigc_docs CASCADE;
CREATE TABLE aigc_docs (
  id SERIAL PRIMARY KEY,
  knowledge_id INTEGER NULL,
  name VARCHAR(255) NULL,
  type VARCHAR(50) NULL,
  origin VARCHAR(50) NULL,
  content TEXT NULL,
  size INTEGER NULL,
  slice_num INTEGER NULL,
  slice_status SMALLINT NULL,
  last_slice_time TIMESTAMP NULL,
  status CHAR(1) DEFAULT '0',
  create_by VARCHAR(64) NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64) NULL,
  update_time TIMESTAMP NULL,
  del_flag CHAR(1) DEFAULT '0',
  CONSTRAINT fk_docs_knowledge FOREIGN KEY (knowledge_id) REFERENCES aigc_knowledge(id)
);

COMMENT ON TABLE aigc_docs IS '文档表';
COMMENT ON COLUMN aigc_docs.id IS '文档ID';
COMMENT ON COLUMN aigc_docs.knowledge_id IS '知识库ID';
COMMENT ON COLUMN aigc_docs.name IS '名称';
COMMENT ON COLUMN aigc_docs.type IS '类型：TEXT，FILE，OSS';
COMMENT ON COLUMN aigc_docs.origin IS '来源';
COMMENT ON COLUMN aigc_docs.content IS '内容或链接';
COMMENT ON COLUMN aigc_docs.size IS '文件大小';
COMMENT ON COLUMN aigc_docs.slice_num IS '切片数量';
COMMENT ON COLUMN aigc_docs.slice_status IS '切片状态：0待处理，1处理中，2已完成，3失败';
COMMENT ON COLUMN aigc_docs.last_slice_time IS '最后切片时间';
COMMENT ON COLUMN aigc_docs.status IS '状态 1启用 0禁用';
COMMENT ON COLUMN aigc_docs.create_by IS '创建者';
COMMENT ON COLUMN aigc_docs.create_time IS '创建时间';
COMMENT ON COLUMN aigc_docs.update_by IS '更新者';
COMMENT ON COLUMN aigc_docs.update_time IS '修改时间';
COMMENT ON COLUMN aigc_docs.del_flag IS '逻辑删 0-未删除 1-已删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_aigc_docs_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_aigc_docs_timestamp
BEFORE UPDATE ON aigc_docs
FOR EACH ROW
EXECUTE FUNCTION update_aigc_docs_timestamp();

-- ----------------------------
-- Records of aigc_docs
-- ----------------------------
INSERT INTO aigc_docs (id, knowledge_id, name, type, origin, content, size, slice_num, slice_status, last_slice_time, status, create_by, create_time, update_by, update_time, del_flag)
VALUES 
(1, 1, 'story-about-happy-carrot', 'FILE', NULL, NULL, 35359, NULL, 0, NULL, '0', 'admin', '2025-05-01 08:28:59', 'edison', '2025-05-07 09:31:19', '0'),
(2, 1, 'guide1', 'FILE', NULL, 'feng_ai_biz 是一个基于Java生态的企业AI知识库和大模型应用解决方案，帮助企业快速搭建AI大模型应用。 同时，feng_ai_biz也集成了RBAC权限体系，为企业提供开箱即用的AI大模型产品解决方案。

feng_ai_biz 使用Java生态，前后端分离，并采用最新的技术栈开发。后端基于SpringBoot3，前端基于Vue3。 feng_ai_biz不仅为企业提供AI领域的产品解决方案，也是一个完整的Java企业级应用案例。这个系统带你全面了解SpringBoot3和Vue3的前后端开发流程、业务模块化，以及AI应用集成方案。 无论是企业开发，还是个人学习，feng_ai_biz都为你提供丰富的学习案例', NULL, 1, 1, NULL, '0', 'admin', '2025-05-01 08:28:59', 'edison', '2025-05-07 09:31:26', '0');


-- ----------------------------
-- Table structure for aigc_oss
-- ----------------------------
DROP TABLE IF EXISTS aigc_oss CASCADE;
CREATE TABLE aigc_oss (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NULL,
  doc_id INTEGER NULL,
  oss_id VARCHAR(64) NULL,
  file_id VARCHAR(64) NULL,
  original_filename VARCHAR(50) NULL,
  filename VARCHAR(50) NULL,
  url VARCHAR(100) NULL,
  base_path VARCHAR(100) NULL,
  path VARCHAR(100) NULL,
  size INTEGER NULL,
  ext VARCHAR(50) NULL,
  content_type VARCHAR(100) NULL,
  extract_content TEXT NULL,
  platform VARCHAR(50) NULL,
  status CHAR(1) DEFAULT '0',
  create_by VARCHAR(64) NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64) NULL,
  update_time TIMESTAMP NULL,
  del_flag CHAR(1) DEFAULT '0',
  CONSTRAINT fk_oss_doc FOREIGN KEY (doc_id) REFERENCES aigc_docs(id)
);

COMMENT ON TABLE aigc_oss IS '资源文件表';
COMMENT ON COLUMN aigc_oss.id IS '资源ID';
COMMENT ON COLUMN aigc_oss.user_id IS '用户ID';
COMMENT ON COLUMN aigc_oss.doc_id IS '文档ID';
COMMENT ON COLUMN aigc_oss.oss_id IS 'OSS对象ID';
COMMENT ON COLUMN aigc_oss.file_id IS '文件ID';
COMMENT ON COLUMN aigc_oss.original_filename IS '原始文件名称';
COMMENT ON COLUMN aigc_oss.filename IS '文件存储名称';
COMMENT ON COLUMN aigc_oss.url IS '文件地址';
COMMENT ON COLUMN aigc_oss.base_path IS '桶路径';
COMMENT ON COLUMN aigc_oss.path IS '文件的绝对路径';
COMMENT ON COLUMN aigc_oss.size IS '文件大小';
COMMENT ON COLUMN aigc_oss.ext IS '文件后缀';
COMMENT ON COLUMN aigc_oss.content_type IS '文件头';
COMMENT ON COLUMN aigc_oss.extract_content IS '文件抽取内容';
COMMENT ON COLUMN aigc_oss.platform IS '平台';
COMMENT ON COLUMN aigc_oss.status IS '状态 1启用 0禁用';
COMMENT ON COLUMN aigc_oss.create_by IS '创建者';
COMMENT ON COLUMN aigc_oss.create_time IS '创建时间';
COMMENT ON COLUMN aigc_oss.update_by IS '更新者';
COMMENT ON COLUMN aigc_oss.update_time IS '修改时间';
COMMENT ON COLUMN aigc_oss.del_flag IS '逻辑删 0-未删除 1-已删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_aigc_oss_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_aigc_oss_timestamp
BEFORE UPDATE ON aigc_oss
FOR EACH ROW
EXECUTE FUNCTION update_aigc_oss_timestamp();

-- ----------------------------
-- Records of aigc_oss
-- ----------------------------
INSERT INTO aigc_oss (id, user_id, doc_id, oss_id, file_id, original_filename, filename, url, base_path, path, size, ext, content_type, extract_content, platform, status, create_by, create_time, update_by, update_time, del_flag)
VALUES 
(1, 3, 1, 'vc-upload-1746621802265-9', NULL, 'custom-banner.jpg', 'aeb161865ec546b3af3487aa1aedb69a.jpg', 'feng-bucket/aeb161865ec546b3af3487aa1aedb69a.jpg', 'feng-bucket/', 'http://127.0.0.1:5173/feng-bucket/aeb161865ec546b3af3487aa1aedb69a.jpg', 105983, 'jpg', 'image/jpeg', NULL, 'minio', '0', 'edison', '2025-05-01 08:28:59', 'edison', '2025-05-07 21:24:42', '0'),
(2, 3, 1, 'vc-upload-1746621802265-15', NULL, 'test5.pdf', 'a2b7adc339c94db3a3953ddfb81720fb.pdf', 'feng-bucket/a2b7adc339c94db3a3953ddfb81720fb.pdf', 'feng-bucket/', 'http://127.0.0.1:5173/feng-bucket/a2b7adc339c94db3a3953ddfb81720fb.pdf', 2038616, 'pdf', 'application/pdf', NULL, 'minio', '0', 'edison', '2025-05-01 08:28:59', 'edison', '2025-05-07 21:24:40', '0');


-- ----------------------------
-- Table structure for aigc_docs_slice
-- ----------------------------
DROP TABLE IF EXISTS aigc_docs_slice CASCADE;
CREATE TABLE aigc_docs_slice (
  id SERIAL PRIMARY KEY,
  embed_store_id INTEGER NULL,
  vector_id VARCHAR(64) NULL,
  docs_id INTEGER NULL,
  oss_id INTEGER NULL,
  knowledge_id INTEGER NULL,
  name VARCHAR(255) NULL,
  slice_index INTEGER NULL,
  content_hash VARCHAR(255) NULL,
  keywords VARCHAR(255) NULL,
  summary VARCHAR(255) NULL,
  content TEXT NULL,
  word_num INTEGER NULL,
  is_embedding BOOLEAN NULL,
  status CHAR(1) DEFAULT '0',
  create_by VARCHAR(64) NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_by VARCHAR(64) NULL,
  update_time TIMESTAMP NULL,
  del_flag CHAR(1) DEFAULT '0',
  CONSTRAINT fk_docs_slice_embed_store FOREIGN KEY (embed_store_id) REFERENCES aigc_embed_store(id),
  CONSTRAINT fk_docs_slice_docs FOREIGN KEY (docs_id) REFERENCES aigc_docs(id),
  CONSTRAINT fk_docs_slice_oss FOREIGN KEY (oss_id) REFERENCES aigc_oss(id),
  CONSTRAINT fk_docs_slice_knowledge FOREIGN KEY (knowledge_id) REFERENCES aigc_knowledge(id)
);

COMMENT ON TABLE aigc_docs_slice IS '文档切片表';
COMMENT ON COLUMN aigc_docs_slice.id IS '切片ID';
COMMENT ON COLUMN aigc_docs_slice.embed_store_id IS '向量库的ID，关联aigc_embed_store';
COMMENT ON COLUMN aigc_docs_slice.vector_id IS '向量ID，关联pgvector向量表的embedding_id';
COMMENT ON COLUMN aigc_docs_slice.docs_id IS '文档ID';
COMMENT ON COLUMN aigc_docs_slice.oss_id IS '文件ID';
COMMENT ON COLUMN aigc_docs_slice.knowledge_id IS '知识库ID';
COMMENT ON COLUMN aigc_docs_slice.name IS '文档名称';
COMMENT ON COLUMN aigc_docs_slice.slice_index IS '切片序号';
COMMENT ON COLUMN aigc_docs_slice.content_hash IS '切片哈希值';
COMMENT ON COLUMN aigc_docs_slice.keywords IS '关键词提取';
COMMENT ON COLUMN aigc_docs_slice.summary IS '摘要';
COMMENT ON COLUMN aigc_docs_slice.content IS '切片内容';
COMMENT ON COLUMN aigc_docs_slice.word_num IS '字符数';
COMMENT ON COLUMN aigc_docs_slice.is_embedding IS '是否向量模型';
COMMENT ON COLUMN aigc_docs_slice.status IS '状态 1启用 0禁用';
COMMENT ON COLUMN aigc_docs_slice.create_by IS '创建者';
COMMENT ON COLUMN aigc_docs_slice.create_time IS '创建时间';
COMMENT ON COLUMN aigc_docs_slice.update_by IS '更新者';
COMMENT ON COLUMN aigc_docs_slice.update_time IS '修改时间';
COMMENT ON COLUMN aigc_docs_slice.del_flag IS '逻辑删 0-未删除 1-已删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_aigc_docs_slice_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_aigc_docs_slice_timestamp
BEFORE UPDATE ON aigc_docs_slice
FOR EACH ROW
EXECUTE FUNCTION update_aigc_docs_slice_timestamp();

-- ----------------------------
-- Records of aigc_docs_slice
-- ----------------------------
-- (No records to insert)


-- Reset sequences after manual inserts
 SELECT setval('aigc_model_id_seq', (SELECT MAX(id) FROM aigc_model));
 SELECT setval('aigc_docs_slice_id_seq', (SELECT MAX(id) FROM aigc_docs_slice));
 SELECT setval('aigc_oss_id_seq', (SELECT MAX(id) FROM aigc_oss));
 SELECT setval('aigc_app_id_seq', (SELECT MAX(id) FROM aigc_app));
 SELECT setval('aigc_app_api_id_seq', (SELECT MAX(id) FROM aigc_app_api));
 SELECT setval('aigc_docs_id_seq', (SELECT MAX(id) FROM aigc_docs));
 SELECT setval('aigc_message_id_seq', (SELECT MAX(id) FROM aigc_message));
 SELECT setval('aigc_prompt_id_seq', (SELECT MAX(id) FROM aigc_prompt));
 SELECT setval('aigc_embed_store_id_seq', (SELECT MAX(id) FROM aigc_embed_store));
 SELECT setval('aigc_knowledge_id_seq', (SELECT MAX(id) FROM aigc_knowledge));
 SELECT setval('aigc_conversation_id_seq', (SELECT MAX(id) FROM aigc_conversation));

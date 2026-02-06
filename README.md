
# feng-cloud 项目说明

## 一、概述

风云项目旨在提供AI+基础服务，并提供少部分OA、CRM、ERP底层功能，基于SPRING和VUE3的MSA框架开发，用于数字化和智能化技术探索和研究。

业务图如下：

![架构图1](doc/dev-manual/业务图.png)

部署图如下：

![架构图2](doc/dev-manual/部署图.png)


## 二、后台公共组件版本规定

* spring-boot	2.7.18
* spring-cloud	2021.0.9
* alibaba		2021.1
* redis		    7.4.2
* Nacos			3.0.0
* pgvector      0.8

## 三、后台公共多模块支持

1. feng-cloud：父项目  
2. feng-library3-BOM：依赖包清单
3. feng-library3-swagger：OPENAPI3规范支持  
4. feng-library3-core：核心工具提供  
5. feng-library3-data：租户、缓存工具提供  
6. feng-library3-datasource：多数据源工具提供  
7. feng-library3-feign：远程通讯工具OPENFEIGN  
8. feng-library3-gateway：网关工具  
9. feng-library3-log：日志工具  
10. feng-library3-oss：对象存储工具  
11. feng-library3-security：安全工具
12. feng-library3-sentinel：熔断器工具  
13. feng-library3-mybatis基于苞米豆的数据库访问工具  

## 四、网关服务feng-gateway2

1. 图片验证码
2. 请求转发

## 五、用户中心服务feng-user2  

1. 实体类、DTO类、VO类 
2. 控制器：用户、角色、菜单权限、项目、日志、数据字典、数据源、员工、单位、团队、文件  

## 六、认证服务feng-auth

1. OAuth2支持
2. 租户认证支持

## 七、人工智能服务 feng-ai

1. 提供实体类、DTO类、VO类
2. 应用管理：提供增删改查API
3. 渠道管理：提供大语言模型渠道key、secret管理 
4. 模型管理：支持现有主流大语言生成模型、向量模型、图片生成模型
5. 会话、提示语和消息管理：支持AI对话的全流程管理，提供内容、消息的存储和追溯
6. 向量库管理：支持REDIS和PGVECTOR作为向量的存储数据库
7. 知识库、文档和OSS文件管理：支持知识库包含文件、文本的所有场景，可以单独切片文件或者 一段文本内容，适合本地知识库构建


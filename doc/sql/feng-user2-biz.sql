SET NAMES 'utf8mb4' COLLATE 'utf8mb4_unicode_ci';
SET FOREIGN_KEY_CHECKS = 0;

drop database if exists `feng_user2_biz`;
create database `feng_user2_biz` default charset utf8mb4 collate utf8mb4_unicode_ci;

use `feng_user2_biz`;

-- 基础管理

DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '租户ID（全局唯一）',
  
  -- 租户基础信息
  `tenant_code` VARCHAR(64) NOT NULL COMMENT '租户编码（唯一业务标识）',
  `tenant_name` VARCHAR(128) NOT NULL COMMENT '租户名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '租户描述',
  
  -- 租户状态控制
  `status` char(1) NOT NULL DEFAULT 0 COMMENT '租户状态: 0-试用,1-正式,2-禁用,3-过期',
  `enable_time` DATETIME DEFAULT NULL COMMENT '启用时间',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  
  -- 联系信息
  `contact_name` VARCHAR(64) DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `contact_email` VARCHAR(128) DEFAULT NULL COMMENT '联系邮箱',
  
  -- 计费相关
  `billing_plan` VARCHAR(32) NOT NULL DEFAULT 'BASIC' COMMENT '计费方案编码',
  `plan_name` VARCHAR(64) NOT NULL DEFAULT '基础版' COMMENT '方案名称',
  `payment_status` char(1) NOT NULL DEFAULT 0 COMMENT '支付状态：0-未支付,1-已支付',
  
  -- 数据隔离方案（关键字段）
  `isolation_mode` char(1) NOT NULL DEFAULT 0 COMMENT '隔离模式：0-共享库,1-独立库,2-独立Schema',
  
  -- 审计字段
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1)  DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
	
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_code` (`tenant_code`),
  KEY `idx_status_expire` (`status`, `expire_time`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户信息表';

insert into `sys_tenant` (`id`, `tenant_code`, `tenant_name`, `description`, `status`, `enable_time`, `expire_time`) values(100, 'default', '默认租户', '用于演示系统功能的租户1', 0, now(), NULL);
insert into `sys_tenant` (`id`, `tenant_code`, `tenant_name`, `description`, `status`, `enable_time`, `expire_time`) values(101, 'SLXXJS', '狩猎信息技术', '用于演示系统功能的租户2', 0, now(), NULL);

DROP TABLE IF EXISTS `sys_tenant_config`;
CREATE TABLE `sys_tenant_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  
  -- 业务配置
  `max_user` INT(11) NOT NULL DEFAULT 10 COMMENT '最大用户数',
  `max_storage` BIGINT(20) NOT NULL DEFAULT 1073741824 COMMENT '最大存储空间（字节, 默认1GB）',
  `allow_custom_logo` char(1) NOT NULL DEFAULT 0 COMMENT '允许自定义LOGO',
  
  -- 功能开关
  `api_access_enabled` char(1) NOT NULL DEFAULT 0 COMMENT 'API访问权限',
  `data_export_enabled` char(1) NOT NULL DEFAULT 0 COMMENT '数据导出权限',
  
  -- 安全配置
  `password_policy` VARCHAR(50) DEFAULT NULL COMMENT '密码策略',
  `login_fail_limit` char(1) NOT NULL DEFAULT 5 COMMENT '登录失败锁定阈值',
  -- 审计字段
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1)  DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant` (`tenant_id`),
  CONSTRAINT `fk_config_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `sys_tenant` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户配置表';


DROP TABLE IF EXISTS `sys_role_application`;
CREATE TABLE `sys_role_application`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `role_id` int(11) NOT NULL COMMENT '角色id',
  `application_id` int(11) NOT NULL COMMENT '应用id',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  UNIQUE KEY `uk_role_app_id` (`tenant_id`, `role_id`, `application_id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '角色应用关联表' ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `sys_application`;
CREATE TABLE `sys_application`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `app_name` varchar(255)  DEFAULT NULL COMMENT '应用名称',
  `application_code` varchar(100)  DEFAULT NULL COMMENT '应用编码',
  `manufacturer_id` int(11) DEFAULT NULL COMMENT '厂商id',
  `app_en_name` varchar(255)  DEFAULT NULL COMMENT '应用英文名称',
  `app_abbr` varchar(255)  DEFAULT NULL COMMENT '应用缩写名称',
  `status` char(1)  NULL DEFAULT '0' COMMENT '状态:0-启用 1-禁用',
  `app_desc` text  NULL COMMENT '描述',
  `is_feng_portal` char(1)  NULL DEFAULT '1' COMMENT '是否集成门户：0-否 1-是',
  `feng_type` char(1)  NULL DEFAULT '0' COMMENT '类型：0-内部 1-外部',
  `client_type` char(1)  NULL DEFAULT '0' COMMENT '标志系统的：0-BS 、 1-CS',
  `security_code` varchar(255)  DEFAULT NULL COMMENT '请求头认证code编码',
  `oauth_code` varchar(255)  DEFAULT NULL COMMENT 'oauth授权码编码',
  `integration_uri` varchar(255)  DEFAULT NULL COMMENT '系统url',
  `parameter_attribute` varchar(255)  DEFAULT NULL COMMENT '参数属性',
  `app_icon` varchar(255)  DEFAULT NULL COMMENT '系统图标地址',
  `is_micro` char(1)  DEFAULT NULL COMMENT '图标',
  `micro_prefix` varchar(255)  DEFAULT NULL,
  `micro_entry` varchar(255)  DEFAULT NULL,
  `app_id` varchar(255)  DEFAULT NULL COMMENT 'SSO统一应用标识',
  `app_secret` varchar(255)  DEFAULT NULL COMMENT 'SSO统一秘钥',
  `sys_is_show` char(1)  NULL DEFAULT '1' COMMENT '是否显示：0-不显示 1-显示，为1才能显示到前端',
  `display_form` char(1)  NULL DEFAULT '0' COMMENT '展示形式：0-PC 1-MOBILE',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '应用系统表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_application` VALUES (1, 100, NULL, '客户关系管理', 'CRM', 1, 'Customer relation management', 'CRM', NULL, NULL, '1', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '0', 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO `sys_application` VALUES (2, 100, NULL, '企业资源计划', 'ERP', 1, 'Enterprise resource plan', 'ERP', NULL, NULL, '1', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '0', 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO `sys_application` VALUES (3, 100, NULL, '系统管理', 'USR', 2, 'user management', 'USR', NULL, NULL, '1', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '0', 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO `sys_application` VALUES (4, 101, NULL, '系统管理', 'USR', 3, 'user management', 'USR', NULL, NULL, '1', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '0', 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');

DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` int(11) NOT NULL COMMENT 'id',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `no` int(11) DEFAULT NULL COMMENT '配置编号',
  `code` varchar(255)  DEFAULT NULL COMMENT '配置编码',
  `value` longtext  NULL COMMENT '配置值',
  `desc` text  NULL COMMENT '配置描述',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `dept_code` varchar(255)  DEFAULT NULL COMMENT '部门编码',
  `dept_name` varchar(100)  NOT NULL DEFAULT '0' COMMENT '部门名称',
  `parent_id` int NOT NULL DEFAULT 0 COMMENT '上级部门ID，0表示无上级',
  `parent_code` varchar(255)  NOT NULL DEFAULT '0' COMMENT '上级部门编码，0表示无上级',
  `sort` int(11) NULL DEFAULT 1 COMMENT '排序',
  `organ_code` varchar(64)  DEFAULT NULL COMMENT '所属机构编码',
  `subject_code` varchar(100)  DEFAULT NULL COMMENT '科目编码',
  `subject_name` varchar(100)  DEFAULT NULL COMMENT '科目名称',
  `dept_category_code` char(20)  DEFAULT NULL COMMENT '部门类别编码，和数据字典dept_category一致',
  `dept_category_name` varchar(100)  DEFAULT NULL COMMENT '部门类别名称',
  `business_subjection` char(1)  DEFAULT NULL COMMENT '业务隶属编码，和数据字典business_subjection一致',
  `dept_location` varchar(255)  DEFAULT NULL COMMENT '部门位置',
  `dept_introduction` text  NULL COMMENT '部门简介',
  `branch_code` varchar(255)  DEFAULT NULL COMMENT '分支编码',
  `branch_name` varchar(255)  DEFAULT NULL COMMENT '分支名称',
  `create_by` varchar(255)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_department` VALUES (1, 100, NULL, 'AQ', '安全科', 0, '0', 0, 'default', '', '', '1', '管理', '0', '', '', NULL, NULL, 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO `sys_department` VALUES (2, 100, NULL, 'XT', '信息技术部', 0, '0', 0, 'default', '', '', '1', '业务', '0', '', '', NULL, NULL, 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO `sys_department` VALUES (3, 100, NULL, 'XS', '销售科', 0, '0', 0, 'default', '', '', '1', '业务', '0', '', '', NULL, NULL, 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO `sys_department` VALUES (4, 100, NULL, 'AQYZ', '安全一组', 1, 'AQ', 0, 'default', '', '', '1', '管理', '0', '', '', NULL, NULL, 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO `sys_department` VALUES (5, 100, NULL, 'AQEZ', '安全二组', 1, 'AQ', 0, 'default', '', '', '1', '管理', '0', '', '', NULL, NULL, 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');

INSERT INTO `sys_department` VALUES (6, 101, NULL, 'AQ', '安全科', 0, '0', 0, 'default', '', '', '1', '管理', '0', '', '', NULL, NULL, 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO `sys_department` VALUES (7, 101, NULL, 'XT', '信息技术部', 0, '0', 0, 'default', '', '', '1', '业务', '0', '', '', NULL, NULL, 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO `sys_department` VALUES (8, 101, NULL, 'XS', '销售科', 0, '0', 0, 'default', '', '', '1', '业务', '0', '', '', NULL, NULL, 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');


DROP TABLE IF EXISTS `sys_dept_attribute`;
CREATE TABLE `sys_dept_attribute`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `dept_id` int(11) NOT NULL COMMENT '部门id',
  `dept_attribute` varchar(255)  NOT NULL COMMENT '部门属性编码',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
   UNIQUE INDEX `idx_dept_attribute`(`tenant_id`, `dept_id`, `dept_attribute`)  COMMENT '部门职责唯一索引'
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '部门职责表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_dept_attribute` (`tenant_id`, `dept_id`,`dept_attribute`) VALUES (100, 1, 'security');
INSERT INTO `sys_dept_attribute` (`tenant_id`, `dept_id`,`dept_attribute`) VALUES (100, 1, 'quality');
INSERT INTO `sys_dept_attribute` (`tenant_id`, `dept_id`,`dept_attribute`) VALUES (101, 1, 'security');
INSERT INTO `sys_dept_attribute` (`tenant_id`, `dept_id`,`dept_attribute`) VALUES (101, 1, 'quality');

DROP TABLE IF EXISTS `sys_dept_relation`;
CREATE TABLE `sys_dept_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `ancestor` int(11) NOT NULL COMMENT '祖先节点',
  `descendant` int(11) NOT NULL COMMENT '后代节点',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  INDEX `idx_ancestor`(`ancestor`) ,
  INDEX `idx_descendant`(`descendant`) ,
  UNIQUE INDEX `idx_dept_relation`(`tenant_id`, `ancestor`, `descendant`)  COMMENT '部门关系唯一索引'
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '部门关系表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_dept_relation`(`tenant_id`, `ancestor`, `descendant`) VALUES (100, 1, 4);
INSERT INTO `sys_dept_relation`(`tenant_id`, `ancestor`, `descendant`) VALUES (100, 1, 5);
INSERT INTO `sys_dept_relation`(`tenant_id`, `ancestor`, `descendant`) VALUES (101, 1, 4);
INSERT INTO `sys_dept_relation`(`tenant_id`, `ancestor`, `descendant`) VALUES (101, 1, 5);

-- 系统管理

DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `dict_key` varchar(100)  DEFAULT NULL COMMENT '字典key',
  `description` varchar(100)  DEFAULT NULL COMMENT '字典描述',
  `is_system` char(1)  NULL DEFAULT '0' COMMENT '是否是系统字典：0-否 1-是',
  `organ_code` varchar(64)  DEFAULT NULL COMMENT '所属机构编码',
  `remarks` varchar(255)  DEFAULT NULL COMMENT '备注',
  `create_by` varchar(255)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) ,
  INDEX `idx_del_flag`(`del_flag`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '字典表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'ORGAN_TYPE', '机构类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'ROLE_TYPE', '角色类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'USER_STATUS', '账号状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'DEPT_CATEGORY', '部门类别', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'BUSINESS_SUBJECTION', '业务隶属', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'JOB_CATEGORY', '岗位类别', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'ACTIVE_STATUS', '在岗状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'FENG_TYPE', '应用类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'MENU_TYPE', '菜单类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'FIELD_DATA_TYPE', '数据类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'STANDARD_TYPE', '标准类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'OPT_STATUS', '操作状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'RELEASE_STATUS', '发布状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'SYNC_STATUS', '同步状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'IDENTITY_TYPE', '证件类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'SYS_DB_TYPE', '数据源类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'STATUS', '状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'PROMPT_TYPE', '提示词类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'MODEL_PROVIDER', '模型提供商', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'MODEL_TYPE', '模型类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'DOC_TYPE', '文档类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'SLICE_MODE', '切片模式', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'EMBED_STORE_TYPE', '向量数据库类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'CUSTOMER_SOURCE', '客户来源', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'INDUSTRY', '行业', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'CUSTOMER_LEVEL', '客户等级', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'POSITION', '联系人职务', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'DEPT_DUTY', '部门职责', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'SOCIAL_TYPE', '社交账号类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'CUSTOMER_RELATIONSHIP_TYPE', '客户关系类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'CONTACT_RELATIONSHIP_TYPE', '联系人关系类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'FOLLOW_TYPE', '跟踪方式', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'TECHNICAL_QUALIFICATIONS', '专业技术职务', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'GENDER', '性别', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'AGENT_MSG_ROLE', '智能体消息角色', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'TENANT_STATUS', '租户状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'ISOLATION_MODE', '租户隔离模式', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'LOG_TYPE', '日志类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'CLIENT_TYPE', '系统类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (100, 'HTTP_METHOD', 'HTTP操作', '0');

INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'ORGAN_TYPE', '机构类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'ROLE_TYPE', '角色类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'USER_STATUS', '账号状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'DEPT_CATEGORY', '部门类别', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'BUSINESS_SUBJECTION', '业务隶属', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'JOB_CATEGORY', '岗位类别', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'ACTIVE_STATUS', '在岗状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'FENG_TYPE', '应用类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'MENU_TYPE', '菜单类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'FIELD_DATA_TYPE', '数据类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'STANDARD_TYPE', '标准类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'OPT_STATUS', '操作状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'RELEASE_STATUS', '发布状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'SYNC_STATUS', '同步状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'IDENTITY_TYPE', '证件类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'SYS_DB_TYPE', '数据源类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'STATUS', '状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'PROMPT_TYPE', '提示词类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'MODEL_PROVIDER', '模型提供商', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'MODEL_TYPE', '模型类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'DOC_TYPE', '文档类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'SLICE_MODE', '切片模式', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'EMBED_STORE_TYPE', '向量数据库类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'CUSTOMER_SOURCE', '客户来源', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'INDUSTRY', '行业', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'CUSTOMER_LEVEL', '客户等级', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'POSITION', '联系人职务', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'DEPT_DUTY', '部门职责', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'SOCIAL_TYPE', '社交账号类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'CUSTOMER_RELATIONSHIP_TYPE', '客户关系类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'CONTACT_RELATIONSHIP_TYPE', '联系人关系类型', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'FOLLOW_TYPE', '跟踪方式', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'TECHNICAL_QUALIFICATIONS', '专业技术职务', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'GENDER', '性别', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'AGENT_MSG_ROLE', '智能体消息角色', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'TENANT_STATUS', '租户状态', '0');
INSERT INTO `sys_dict`(`tenant_id`, `dict_key`, `description`, `is_system`) VALUES (101, 'ISOLATION_MODE', '租户隔离模式', '0');

update `sys_dict` set `dict_key` = upper(`dict_key`);

DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `dict_id` int(11) NOT NULL COMMENT '字典id',
  `value` varchar(100)  DEFAULT NULL COMMENT '字典项value:0、1、2、3',
  `label` varchar(100)  DEFAULT NULL COMMENT '字典项Value备注',
  `dict_key` varchar(100)  DEFAULT NULL COMMENT '所属字典key',
  `description` varchar(100)  DEFAULT NULL COMMENT '字典项描述',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序（升序）',
  `remarks` varchar(255)  DEFAULT NULL COMMENT '备注',
  `create_by` varchar(255)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) ,
  INDEX `idx_value`(`value`) ,
  INDEX `idx_label`(`label`) ,
  INDEX `idx_item_del_flag`(`del_flag`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '字典项' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 2, '0', '其他', 'organ_type', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 2, '1', '企业单位', 'organ_type', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 2, '2', '行政机构', 'ORGAN_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 2, '3', '事业单位', 'ORGAN_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 2, '4', '慈善机构', 'ORGAN_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 3, '0', '系统', 'ROLE_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 3, '1', '自定义', 'ROLE_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 4, '0', '禁用', 'USER_STATUS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 4, '1', '启用', 'USER_STATUS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 5, '1', '行政', 'DEPT_CATEGORY', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 5, '2', '财务', 'DEPT_CATEGORY', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 5, '3', '业务', 'DEPT_CATEGORY', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 6, '0', '不区分', 'BUSINESS_SUBJECTION', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 6, '1', '业务 ', 'BUSINESS_SUBJECTION', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 6, '2', '职能', 'BUSINESS_SUBJECTION', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 6, '3', '其他', 'BUSINESS_SUBJECTION', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 7, 'BUSINESS', '业务线', 'JOB_CATEGORY', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 7, 'MANAGEMENT', '管理线', 'JOB_CATEGORY', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 7, 'TECHNOLOGY', '技术线', 'JOB_CATEGORY', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 7, 'OTHER', '其他', 'JOB_CATEGORY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 8, '0', '离岗', 'ACTIVE_STATUS', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 8, '1', '在岗', 'ACTIVE_STATUS', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 9, '0', '内部应用', 'FENG_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 9, '1', '外部应用', 'FENG_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 10, '0', '菜单', 'MENU_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 10, '1', '按钮', 'MENU_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 11, '1', '字符型', 'FIELD_DATA_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 11, '2', '整数型', 'FIELD_DATA_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 11, '3', '日期型', 'FIELD_DATA_TYPE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 12, '1', '国标', 'STANDARD_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 12, '2', '行标', 'STANDARD_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 13, 'A', '新增', 'OPT_STATUS', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 13, 'U', '修改', 'OPT_STATUS', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 13, 'D', '删除', 'OPT_STATUS', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 13, 'H', '历史', 'OPT_STATUS', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 14, '0', '待审批', 'RELEASE_STATUS', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 14, '1', '待发布', 'RELEASE_STATUS', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 14, '2', '已发布', 'RELEASE_STATUS', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 14, '4', '驳回', 'RELEASE_STATUS', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 15, '0', '未开始', 'SYNC_STATUS', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 15, '1', '等待中', 'SYNC_STATUS', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 15, '2', '执行中', 'SYNC_STATUS', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 16, '1', '身份证', 'IDENTITY_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 16, '2', '其他证件', 'IDENTITY_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 17, 'MYSQL', 'MYSQL库', 'SYS_DB_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 17, 'POSTGRESQL', 'PG库', 'SYS_DB_TYPE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 18, '0', '正常', 'STATUS', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 18, '1', '异常', 'STATUS', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 19,  'SYSTEM',      '系统', 'PROMPT_TYPE', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 19,  'INSTRUCTION', '指令', 'PROMPT_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 19,  'TEMPLATE',    '模板', 'PROMPT_TYPE', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20,  'OPENAI',   'OPENAI', 'MODEL_PROVIDER', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20,  'DEEPSEEK', 'DEEPSEEK', 'MODEL_PROVIDER', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20,  'ZHIPU',    '智谱', 'MODEL_PROVIDER', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20,  'BAAI',     'BAAI', 'MODEL_PROVIDER', NULL, 50);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20,  'MOKAAI',   'MOKAAI', 'MODEL_PROVIDER', NULL, 60);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20,  'CLAUDE',   'CLAUDE', 'MODEL_PROVIDER', NULL, 70);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20,  'JURASSIC', 'JURASSIC', 'MODEL_PROVIDER', NULL, 80);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20,  'META',     '元宇宙脸书', 'MODEL_PROVIDER', NULL, 90);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20, 'GOOGLE',   '谷歌', 'MODEL_PROVIDER', NULL, 100);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20, 'DOUBAO',   '抖音豆包', 'MODEL_PROVIDER', NULL, 110);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20, 'NETEASE',  '‌网易', 'MODEL_PROVIDER', NULL, 120);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20, 'XUNFEI',   '科大讯飞', 'MODEL_PROVIDER', NULL, 130);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20, 'BAIDU',    '百度', 'MODEL_PROVIDER', NULL, 140);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20, 'ALICLOUD', '阿里云', 'MODEL_PROVIDER', NULL, 150);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 20, 'OTHER',    '其他', 'MODEL_PROVIDER', NULL, 150);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 21, 'CHAT',      '聊天', 'MODEL_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 21, 'EMBEDDING', '向量', 'MODEL_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 21, 'IMAGE','图形', 'MODEL_TYPE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 21, 'WEB_SEARCH','其他', 'MODEL_TYPE', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 22, 'TEXT', '文本输入', 'DOC_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 22, 'OSS',  '对象存储服务上传', 'DOC_TYPE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 23, 'SENTENCE', '语句切割', 'SLICE_MODE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 23, 'PARAGRAPH', '段落切割', 'SLICE_MODE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 23, 'FIXED',  '定长切割', 'SLICE_MODE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 24, 'REDIS', 'REDIS向量库', 'EMBED_STORE_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 24, 'PGVECTOR', 'PGVECTOR向量库', 'EMBED_STORE_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 24, 'MILVUS',  'MILVUS向量库', 'EMBED_STORE_TYPE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 25, 'WEBSITE',  '官网', 'CUSTOMER_SOURCE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 25, 'DOUYIN',  '抖音', 'CUSTOMER_SOURCE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 25, 'OFFLINE',  '地推', 'CUSTOMER_SOURCE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 25, 'AD',  '广告公司', 'CUSTOMER_SOURCE', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 25, 'OTHER',  '其他', 'CUSTOMER_SOURCE', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 26, 'A',  '农林牧渔业', 'INDUSTRY', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 26, 'A01',  '农业', 'INDUSTRY', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 26, 'B',  '采矿业', 'INDUSTRY', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 26, 'C',  '制造业', 'INDUSTRY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 26, 'D',  '电力、燃气及水生产和供应业', 'INDUSTRY', NULL, 50);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 26, 'G',  '交通运输、仓储和邮政业', 'INDUSTRY', NULL, 60);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 26, 'I',  '信息传输、软件和信息技术服务业', 'INDUSTRY', NULL, 70);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 26, 'M',  '科学研究和技术服务业', 'INDUSTRY', NULL, 80);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 26, 'N',  '水利、环境和公共设施管理业', 'INDUSTRY', NULL, 90);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 27, 'A',  'A类客户：重点投入资源，提供VIP服务', 'CUSTOMER_LEVEL', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 27, 'B',  'B类客户：定期跟进，推动升级', 'CUSTOMER_LEVEL', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 27, 'C',  'C类客户：优化服务以提升潜力', 'CUSTOMER_LEVEL', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 27, 'D',  'D类客户：逐步淘汰或减少投入', 'CUSTOMER_LEVEL', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 28, 'STAFF',  '部门职员', 'POSITION', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 28, 'HEAD',  '部门主任', 'POSITION', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 28, 'MANAGER',  '经理', 'POSITION', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 28, 'GENERAL',  '总经理', 'POSITION', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 29, 'STRATEGY',  '战略', 'DEPT_DUTY', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 29, 'SALARY',  '工资', 'DEPT_DUTY', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 29, 'ATTENDANCE',  '考勤', 'DEPT_DUTY', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 29, 'RESEARCH',  '研究', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 29, 'RECRUITMENT',  '招聘', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 29, 'PRODUCTION',  '生产', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 29, 'PURCHASE',  '采购', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 29, 'SALE',  '销售', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 29, 'INVOICE',  '发票', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 29, 'PRODUCT',  '产品', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 29, 'CUSTOMER SERVICE',  '客服', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 29, 'SECURITY',  '保障', 'DEPT_DUTY', NULL, 120);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 29, 'QUALITY',  '质量', 'DEPT_DUTY', NULL, 130);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 30, 'WECHAT',  '微信', 'SOCIAL_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 30, 'LINKEDIN',  '领英', 'SOCIAL_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 30, 'WEIBO',  '微博', 'SOCIAL_TYPE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 30, 'QQ',  'QQ', 'SOCIAL_TYPE', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 31, 'CUSTOMER', '客户', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 31, 'AGENT', '代理商', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 31, 'SUPPLIER', '供应商', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 31, 'SUPERIOR', '母公司 ', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 31, 'SUBSIDIARY', '子公司 ', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 31, 'SHAREHOLDER', '股东', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 31, 'INVESTMENT', '投资方 ', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 31, 'BRANCH', '分支机构 ', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 32, 'COLLEAGUE', '同事', 'CONTACT_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 32, 'PARTNER', '生意伙伴', 'CONTACT_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 32, 'FRIEND', '朋友', 'CONTACT_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 32, 'FAMILY', '家庭', 'CONTACT_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 32, 'RELATIVE', '亲戚', 'CONTACT_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 33, 'PHONE', '电话', 'FOLLOW_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 33, 'MEETING', '线下会议', 'FOLLOW_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 33, 'EMAIL', '电子邮件', 'FOLLOW_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 33, 'IM',    '即时通讯工具，视频会议', 'FOLLOW_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 33, 'VISIT', '面对面拜访', 'FOLLOW_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '010', '高等学校教师              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '011', '教授                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '012', '副教授                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '013', '讲师（高校）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '014', '助教（高校）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '020', '中等专业学校教师          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '022', '高级讲师（中专）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '023', '讲师（中专）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '024', '助理讲师（中专）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '025', '教员（中专）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '030', '技工学校教师              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '032', '高级讲师（技校）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '033', '讲师（技校）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '034', '助理讲师（技校）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '035', '教员（技校）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '040', '技工学校教师（实习指导）  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '042', '高级实习指导教师          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '043', '一级实习指导教师          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '044', '二级实习指导教师          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '045', '三级实习指导教师          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '050', '中学教师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '052', '高级教师（中学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '053', '一级教师（中学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '054', '二级教师（中学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '055', '三级教师（中学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '070', '实验技术人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '072', '高级实验师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '073', '实验师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '074', '助理实验师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '075', '实验员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '080', '工程技术人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '082', '高级工程师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '083', '工程师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '084', '助理工程师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '085', '技术员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '090', '农业技术人员（农艺）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '091', '农业技术推广研究员（农艺）', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '092', '高级农艺师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '093', '农艺师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '094', '助理农艺师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '095', '农业技术员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '100', '农业技术人员（兽医）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '101', '农业技术推广研究员（兽医）', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '102', '高级兽医师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '103', '兽医师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '104', '助理兽医师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '105', '兽医技术员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '110', '农业技术人员（畜牧）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '111', '农业技术推广研究员（畜牧）', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '112', '高级畜牧师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '113', '畜牧师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '114', '助理畜牧师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '115', '畜牧技术员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '120', '经济专业人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '122', '高级经济师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '123', '经济师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '124', '助理经济师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '125', '经济员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '130', '会计专业人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '132', '高级会计师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '133', '会计师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '134', '助理会计师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '135', '会计员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '140', '统计专业人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '142', '高级统计师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '143', '统计师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '144', '助理统计师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '145', '统计员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '150', '出版专业人员（编审）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '151', '编审                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '152', '副编审                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '153', '编辑                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '154', '助理编辑                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '160', '出版专业人员（技术编辑）  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '163', '技术编辑                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '164', '助理技术编辑              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '165', '技术设计员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '170', '出版专业人员（校对）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '173', '一级校对                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '174', '二级校对                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '175', '三级校对                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '180', '翻译人员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '181', '译审                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '182', '副译审                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '183', '翻译                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '184', '助理翻译                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '190', '新闻专业人员（记者）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '191', '高级记者                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '192', '主任记者                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '193', '记者                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '194', '助理记者                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '200', '新闻专业人员（编辑）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '201', '高级编辑                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '202', '主任编辑                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '203', '编辑                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '204', '助理编辑                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '220', '播音员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '221', '播音指导                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '222', '主任播音员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '223', '一级播音员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '224', '二级播音员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '225', '三级播音员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '230', '卫生技术人员（医师）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '231', '主任医师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '232', '副主任医师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '233', '主治医师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '234', '医师                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '235', '医士                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '240', '卫生技术人员（药剂）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '241', '主任药师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '242', '副主任药师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '243', '主管药师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '244', '药师                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '245', '药士                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '250', '卫生技术人员（护理）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '251', '主任护师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '252', '副主任护师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '253', '主管护师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '254', '护师                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '255', '护士                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '260', '卫生技术人员（技师）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '261', '主任技师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '262', '副主任技师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '263', '主管技师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '264', '技师                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '265', '技士                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '270', '工艺美术人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '272', '高级工艺美术师            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '273', '工艺美术师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '274', '助理工艺美术师            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '275', '工艺美术员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '280', '艺术人员（演员）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '281', '一级演员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '282', '二级演员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '283', '三级演员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '284', '四级演员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '290', '艺术人员（演奏员）        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '291', '一级演奏员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '292', '二级演奏员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '293', '三级演奏员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '294', '四级演奏员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '300', '艺术人员（编剧）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '301', '一级编剧                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '302', '二级编剧                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '303', '三级编剧                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '304', '四级编剧                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '310', '艺术人员（导演）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '311', '一级导演                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '312', '二级导演                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '313', '三级导演                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '314', '四级导演                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '320', '艺术人员（指挥）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '321', '一级指挥                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '322', '二级指挥                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '323', '三级指挥                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '324', '四级指挥                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '330', '艺术人员（作曲）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '331', '一级作曲                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '332', '二级作曲                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '333', '三级作曲                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '334', '四级作曲                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '340', '艺术人员（美术）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '341', '一级美术师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '342', '二级美术师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '343', '三级美术师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '344', '美术员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '350', '艺术人员（舞美设计）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '351', '一级舞美设计师            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '352', '二级舞美设计师            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '353', '三级舞美设计师            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '354', '舞美设计员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '360', '艺术人员（舞台技术）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '362', '主任舞台技师              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '363', '舞台技师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '364', '舞台技术员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '370', '体育锻炼                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '371', '国家级教练                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '372', '高级教练                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '373', '一级教练                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '374', '二级教练                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '375', '三级教练                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '390', '律师                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '391', '一级律师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '392', '二级律师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '393', '三级律师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '394', '四级律师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '395', '律师助理                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '400', '公证员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '401', '一级公证员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '402', '二级公证员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '403', '三级公证员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '404', '四级公证员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '405', '公证助理员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '410', '小学教师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '413', '高级教师（小学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '414', '一级教师（小学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '415', '二级教师（小学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '416', '三级教师（小学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '420', '船舶技术人员（驾驶）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '422', '高级船长                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '423', '船长（大副）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '424', '二副                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '425', '三副                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '430', '船舶技术人员（轮机）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '432', '高级轮机长                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '433', '轮机长（大管轮）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '434', '二管轮                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '435', '三管轮                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '440', '船舶技术人员（电机）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '442', '高级电机员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '443', '通用电机员（一等电机员）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '444', '二等电机员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '450', '船舶技术人员（报务）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '452', '高级报务员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '453', '通用报务员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '454', '二等报务员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '455', '限用报务员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '460', '飞行技术人员（驾驶）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '462', '一级飞行员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '463', '二级飞行员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '464', '三级飞行员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '465', '四级飞行员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '470', '飞行技术人员（领航）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '472', '一级领航员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '473', '二级领航员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '474', '三级领航员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '475', '四级领航员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '480', '飞行技术人员（通信）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '482', '一级飞行通信员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '483', '二级飞行通信员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '484', '三级飞行通信员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '485', '四级飞行通信员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '490', '飞行技术人员（机械）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '492', '一级飞行机械员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '493', '二级飞行机械员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '494', '三级飞行机械员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '495', '四级飞行机械员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '500', '船舶技术人员（引航）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '502', '高级引航员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '503', '一、二级引航员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '504', '三、四级引航员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '610', '自然科学研究人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '611', '研究员（自然科学）            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '612', '副研究员（自然科学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '613', '助理研究员（自然科学）        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '614', '研究实习员（自然科学）        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '620', '社会科学研究人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '621', '研究员（社会科学）            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '622', '副研究员（社会科学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '623', '助理研究员（社会科学）        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '624', '研究实习员（社会科学）        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '640', '图书、资料专业人员            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '641', '研究馆员（图书）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '642', '副研究馆员（图书）            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '643', '馆员（图书）                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '644', '助理馆员（图书）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '645', '管理员（图书）                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '650', '文博专业人员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '651', '研究馆员（文博）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '652', '副研究馆员（文博）            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '653', '馆员（文博）                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '654', '助理馆员（文博）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '655', '管理员（文博）                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '660', '档案专业人员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '661', '研究馆员（档案）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '662', '副研究馆员（档案）            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '663', '馆员（档案）                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '664', '助理馆员（档案）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '665', '管理员（档案）                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '670', '群众文化专业人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '671', '研究馆员（群众文化）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '672', '副研究馆员（群众文化）        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '673', '馆员（群众文化）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '674', '助理馆员（群众文化）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '675', '管理员（群众文化）            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '680', '审计专业人员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '682', '高级审计师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '683', '审计师                        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '684', '助理审计师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '685', '审计员                        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '690', '法医专业人员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '691', '主任法医师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '692', '副主任法医师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '693', '主检法医师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '694', '法医师                        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '695', '法医士                        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '980', '思想政治工作人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '982', '高级政工师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '983', '政工师                        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '984', '助理政工师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 34, '985', '政工员                        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 35, '1', '男', 'GENDER', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 35, '2', '女', 'GENDER', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 35, '3', '未知', 'GENDER', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 35, 'ASSISTANT', '助手', 'AGENT_MSG_ROLE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 35, 'SYSTEM', '系统', 'AGENT_MSG_ROLE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 35, 'USER', '用户', 'AGENT_MSG_ROLE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 36, '0', '试用', 'TENANT_STATUS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 36, '1', '正式', 'TENANT_STATUS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 36, '2', '禁用', 'TENANT_STATUS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 36, '3', '过期', 'TENANT_STATUS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 37, '0', '共享库', 'ISOLATION_MODE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 37, '1', '独立库', 'ISOLATION_MODE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 37, '2', '独立Schema', 'ISOLATION_MODE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '000', '用户详细日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '001', '安全日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '002', '审计日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '003', '重要业务日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '004', 'CRM业务日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '005', 'ERP业务日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '006', 'OA业务日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '007', 'AGENT业务日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '008', '硬件系统日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '009', '操作系统日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '010', 'IAAS日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '011', 'PAAS日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '012', 'SAAS日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '013', '错误日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '014', 'OMS日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '015', '微服务日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 38, '100', '其他日志', 'LOG_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 39, '0', '浏览器-服务器', 'CLIENT_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 39, '1', '客户端-服务器', 'CLIENT_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 40, 'GET', 'GET-查询', 'HTTP_METHOD', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 40, 'POST', 'POST-新增或者复杂查询', 'HTTP_METHOD', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 40, 'PUT', 'PUT-修改', 'HTTP_METHOD', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (100, 40, 'DELTE', 'DELTE-删除', 'HTTP_METHOD', NULL, 0);

INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 2, '0', '其他', 'organ_type', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 2, '1', '企业单位', 'organ_type', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 2, '2', '行政机构', 'ORGAN_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 2, '3', '事业单位', 'ORGAN_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 2, '4', '慈善机构', 'ORGAN_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 3, '0', '系统', 'ROLE_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 3, '1', '自定义', 'ROLE_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 4, '0', '禁用', 'USER_STATUS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 4, '1', '启用', 'USER_STATUS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 5, '1', '行政', 'DEPT_CATEGORY', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 5, '2', '财务', 'DEPT_CATEGORY', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 5, '3', '业务', 'DEPT_CATEGORY', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 6, '0', '不区分', 'BUSINESS_SUBJECTION', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 6, '1', '业务 ', 'BUSINESS_SUBJECTION', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 6, '2', '职能', 'BUSINESS_SUBJECTION', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 6, '3', '其他', 'BUSINESS_SUBJECTION', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 7, 'BUSINESS', '业务线', 'JOB_CATEGORY', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 7, 'MANAGEMENT', '管理线', 'JOB_CATEGORY', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 7, 'TECHNOLOGY', '技术线', 'JOB_CATEGORY', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 7, 'OTHER', '其他', 'JOB_CATEGORY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 8, '0', '离岗', 'ACTIVE_STATUS', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 8, '1', '在岗', 'ACTIVE_STATUS', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 9, '0', '内部应用', 'FENG_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 9, '1', '外部应用', 'FENG_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 10, '0', '菜单', 'MENU_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 10, '1', '按钮', 'MENU_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 11, '1', '字符型', 'FIELD_DATA_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 11, '2', '整数型', 'FIELD_DATA_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 11, '3', '日期型', 'FIELD_DATA_TYPE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 12, '1', '国标', 'STANDARD_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 12, '2', '行标', 'STANDARD_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 13, 'A', '新增', 'OPT_STATUS', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 13, 'U', '修改', 'OPT_STATUS', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 13, 'D', '删除', 'OPT_STATUS', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 13, 'H', '历史', 'OPT_STATUS', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 14, '0', '待审批', 'RELEASE_STATUS', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 14, '1', '待发布', 'RELEASE_STATUS', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 14, '2', '已发布', 'RELEASE_STATUS', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 14, '4', '驳回', 'RELEASE_STATUS', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 15, '0', '未开始', 'SYNC_STATUS', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 15, '1', '等待中', 'SYNC_STATUS', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 15, '2', '执行中', 'SYNC_STATUS', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 16, '1', '身份证', 'IDENTITY_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 16, '2', '其他证件', 'IDENTITY_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 17, 'MYSQL', 'MYSQL库', 'SYS_DB_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 17, 'POSTGRESQL', 'PG库', 'SYS_DB_TYPE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 18, '0', '正常', 'STATUS', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 18, '1', '异常', 'STATUS', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 19,  'SYSTEM',      '系统', 'PROMPT_TYPE', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 19,  'INSTRUCTION', '指令', 'PROMPT_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 19,  'TEMPLATE',    '模板', 'PROMPT_TYPE', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20,  'OPENAI',   'OPENAI', 'MODEL_PROVIDER', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20,  'DEEPSEEK', 'DEEPSEEK', 'MODEL_PROVIDER', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20,  'ZHIPU',    '智谱', 'MODEL_PROVIDER', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20,  'BAAI',     'BAAI', 'MODEL_PROVIDER', NULL, 50);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20,  'MOKAAI',   'MOKAAI', 'MODEL_PROVIDER', NULL, 60);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20,  'CLAUDE',   'CLAUDE', 'MODEL_PROVIDER', NULL, 70);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20,  'JURASSIC', 'JURASSIC', 'MODEL_PROVIDER', NULL, 80);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20,  'META',     '元宇宙脸书', 'MODEL_PROVIDER', NULL, 90);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20, 'GOOGLE',   '谷歌', 'MODEL_PROVIDER', NULL, 100);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20, 'DOUBAO',   '抖音豆包', 'MODEL_PROVIDER', NULL, 110);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20, 'NETEASE',  '‌网易', 'MODEL_PROVIDER', NULL, 120);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20, 'XUNFEI',   '科大讯飞', 'MODEL_PROVIDER', NULL, 130);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20, 'BAIDU',    '百度', 'MODEL_PROVIDER', NULL, 140);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20, 'ALICLOUD', '阿里云', 'MODEL_PROVIDER', NULL, 150);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 20, 'OTHER',    '其他', 'MODEL_PROVIDER', NULL, 150);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 21, 'CHAT',      '聊天', 'MODEL_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 21, 'EMBEDDING', '向量', 'MODEL_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 21, 'IMAGE','图形', 'MODEL_TYPE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 21, 'WEB_SEARCH','其他', 'MODEL_TYPE', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 22, 'TEXT', '文本输入', 'DOC_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 22, 'FILE', '文件系统上传', 'DOC_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 22, 'OSS',  '对象存储服务上传', 'DOC_TYPE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 23, 'SENTENCE', '语句切割', 'SLICE_MODE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 23, 'PARAGRAPH', '段落切割', 'SLICE_MODE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 23, 'FIXED',  '定长切割', 'SLICE_MODE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 24, 'REDIS', 'REDIS向量库', 'EMBED_STORE_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 24, 'PGVECTOR', 'PGVECTOR向量库', 'EMBED_STORE_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 24, 'MILVUS',  'MILVUS向量库', 'EMBED_STORE_TYPE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 25, 'WEBSITE',  '官网', 'CUSTOMER_SOURCE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 25, 'DOUYIN',  '抖音', 'CUSTOMER_SOURCE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 25, 'OFFLINE',  '地推', 'CUSTOMER_SOURCE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 25, 'AD',  '广告公司', 'CUSTOMER_SOURCE', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 25, 'OTHER',  '其他', 'CUSTOMER_SOURCE', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 26, 'A',  '农林牧渔业', 'INDUSTRY', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 26, 'A01',  '农业', 'INDUSTRY', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 26, 'B',  '采矿业', 'INDUSTRY', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 26, 'C',  '制造业', 'INDUSTRY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 26, 'D',  '电力、燃气及水生产和供应业', 'INDUSTRY', NULL, 50);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 26, 'G',  '交通运输、仓储和邮政业', 'INDUSTRY', NULL, 60);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 26, 'I',  '信息传输、软件和信息技术服务业', 'INDUSTRY', NULL, 70);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 26, 'M',  '科学研究和技术服务业', 'INDUSTRY', NULL, 80);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 26, 'N',  '水利、环境和公共设施管理业', 'INDUSTRY', NULL, 90);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 27, 'A',  'A类客户：重点投入资源，提供VIP服务', 'CUSTOMER_LEVEL', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 27, 'B',  'B类客户：定期跟进，推动升级', 'CUSTOMER_LEVEL', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 27, 'C',  'C类客户：优化服务以提升潜力', 'CUSTOMER_LEVEL', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 27, 'D',  'D类客户：逐步淘汰或减少投入', 'CUSTOMER_LEVEL', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 28, 'STAFF',  '部门职员', 'POSITION', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 28, 'HEAD',  '部门主任', 'POSITION', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 28, 'MANAGER',  '经理', 'POSITION', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 28, 'GENERAL',  '总经理', 'POSITION', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 29, 'STRATEGY',  '战略', 'DEPT_DUTY', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 29, 'SALARY',  '工资', 'DEPT_DUTY', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 29, 'ATTENDANCE',  '考勤', 'DEPT_DUTY', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 29, 'RESEARCH',  '研究', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 29, 'RECRUITMENT',  '招聘', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 29, 'PRODUCTION',  '生产', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 29, 'PURCHASE',  '采购', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 29, 'SALE',  '销售', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 29, 'INVOICE',  '发票', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 29, 'PRODUCT',  '产品', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 29, 'CUSTOMER SERVICE',  '客服', 'DEPT_DUTY', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 29, 'SECURITY',  '保障', 'DEPT_DUTY', NULL, 120);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 29, 'QUALITY',  '质量', 'DEPT_DUTY', NULL, 130);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 30, 'WECHAT',  '微信', 'SOCIAL_TYPE', NULL, 10);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 30, 'LINKEDIN',  '领英', 'SOCIAL_TYPE', NULL, 20);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 30, 'WEIBO',  '微博', 'SOCIAL_TYPE', NULL, 30);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 30, 'QQ',  'QQ', 'SOCIAL_TYPE', NULL, 40);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 31, 'CUSTOMER', '客户', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 31, 'AGENT', '代理商', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 31, 'SUPPLIER', '供应商', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 31, 'SUPERIOR', '母公司 ', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 31, 'SUBSIDIARY', '子公司 ', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 31, 'SHAREHOLDER', '股东', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 31, 'INVESTMENT', '投资方 ', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 31, 'BRANCH', '分支机构 ', 'CUSTOMER_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 32, 'COLLEAGUE', '同事', 'CONTACT_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 32, 'PARTNER', '生意伙伴', 'CONTACT_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 32, 'FRIEND', '朋友', 'CONTACT_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 32, 'FAMILY', '家庭', 'CONTACT_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 32, 'RELATIVE', '亲戚', 'CONTACT_RELATIONSHIP_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 33, 'PHONE', '电话', 'FOLLOW_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 33, 'MEETING', '线下会议', 'FOLLOW_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 33, 'EMAIL', '电子邮件', 'FOLLOW_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 33, 'IM',    '即时通讯工具，视频会议', 'FOLLOW_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 33, 'VISIT', '面对面拜访', 'FOLLOW_TYPE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '010', '高等学校教师              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '011', '教授                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '012', '副教授                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '013', '讲师（高校）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '014', '助教（高校）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '020', '中等专业学校教师          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '022', '高级讲师（中专）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '023', '讲师（中专）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '024', '助理讲师（中专）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '025', '教员（中专）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '030', '技工学校教师              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '032', '高级讲师（技校）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '033', '讲师（技校）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '034', '助理讲师（技校）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '035', '教员（技校）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '040', '技工学校教师（实习指导）  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '042', '高级实习指导教师          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '043', '一级实习指导教师          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '044', '二级实习指导教师          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '045', '三级实习指导教师          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '050', '中学教师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '052', '高级教师（中学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '053', '一级教师（中学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '054', '二级教师（中学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '055', '三级教师（中学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '070', '实验技术人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '072', '高级实验师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '073', '实验师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '074', '助理实验师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '075', '实验员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '080', '工程技术人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '082', '高级工程师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '083', '工程师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '084', '助理工程师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '085', '技术员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '090', '农业技术人员（农艺）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '091', '农业技术推广研究员（农艺）', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '092', '高级农艺师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '093', '农艺师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '094', '助理农艺师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '095', '农业技术员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '100', '农业技术人员（兽医）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '101', '农业技术推广研究员（兽医）', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '102', '高级兽医师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '103', '兽医师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '104', '助理兽医师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '105', '兽医技术员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '110', '农业技术人员（畜牧）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '111', '农业技术推广研究员（畜牧）', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '112', '高级畜牧师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '113', '畜牧师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '114', '助理畜牧师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '115', '畜牧技术员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '120', '经济专业人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '122', '高级经济师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '123', '经济师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '124', '助理经济师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '125', '经济员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '130', '会计专业人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '132', '高级会计师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '133', '会计师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '134', '助理会计师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '135', '会计员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '140', '统计专业人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '142', '高级统计师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '143', '统计师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '144', '助理统计师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '145', '统计员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '150', '出版专业人员（编审）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '151', '编审                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '152', '副编审                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '153', '编辑                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '154', '助理编辑                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '160', '出版专业人员（技术编辑）  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '163', '技术编辑                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '164', '助理技术编辑              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '165', '技术设计员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '170', '出版专业人员（校对）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '173', '一级校对                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '174', '二级校对                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '175', '三级校对                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '180', '翻译人员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '181', '译审                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '182', '副译审                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '183', '翻译                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '184', '助理翻译                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '190', '新闻专业人员（记者）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '191', '高级记者                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '192', '主任记者                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '193', '记者                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '194', '助理记者                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '200', '新闻专业人员（编辑）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '201', '高级编辑                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '202', '主任编辑                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '203', '编辑                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '204', '助理编辑                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '220', '播音员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '221', '播音指导                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '222', '主任播音员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '223', '一级播音员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '224', '二级播音员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '225', '三级播音员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '230', '卫生技术人员（医师）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '231', '主任医师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '232', '副主任医师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '233', '主治医师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '234', '医师                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '235', '医士                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '240', '卫生技术人员（药剂）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '241', '主任药师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '242', '副主任药师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '243', '主管药师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '244', '药师                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '245', '药士                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '250', '卫生技术人员（护理）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '251', '主任护师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '252', '副主任护师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '253', '主管护师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '254', '护师                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '255', '护士                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '260', '卫生技术人员（技师）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '261', '主任技师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '262', '副主任技师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '263', '主管技师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '264', '技师                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '265', '技士                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '270', '工艺美术人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '272', '高级工艺美术师            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '273', '工艺美术师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '274', '助理工艺美术师            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '275', '工艺美术员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '280', '艺术人员（演员）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '281', '一级演员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '282', '二级演员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '283', '三级演员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '284', '四级演员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '290', '艺术人员（演奏员）        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '291', '一级演奏员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '292', '二级演奏员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '293', '三级演奏员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '294', '四级演奏员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '300', '艺术人员（编剧）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '301', '一级编剧                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '302', '二级编剧                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '303', '三级编剧                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '304', '四级编剧                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '310', '艺术人员（导演）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '311', '一级导演                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '312', '二级导演                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '313', '三级导演                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '314', '四级导演                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '320', '艺术人员（指挥）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '321', '一级指挥                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '322', '二级指挥                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '323', '三级指挥                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '324', '四级指挥                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '330', '艺术人员（作曲）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '331', '一级作曲                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '332', '二级作曲                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '333', '三级作曲                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '334', '四级作曲                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '340', '艺术人员（美术）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '341', '一级美术师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '342', '二级美术师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '343', '三级美术师                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '344', '美术员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '350', '艺术人员（舞美设计）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '351', '一级舞美设计师            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '352', '二级舞美设计师            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '353', '三级舞美设计师            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '354', '舞美设计员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '360', '艺术人员（舞台技术）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '362', '主任舞台技师              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '363', '舞台技师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '364', '舞台技术员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '370', '体育锻炼                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '371', '国家级教练                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '372', '高级教练                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '373', '一级教练                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '374', '二级教练                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '375', '三级教练                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '390', '律师                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '391', '一级律师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '392', '二级律师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '393', '三级律师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '394', '四级律师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '395', '律师助理                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '400', '公证员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '401', '一级公证员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '402', '二级公证员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '403', '三级公证员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '404', '四级公证员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '405', '公证助理员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '410', '小学教师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '413', '高级教师（小学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '414', '一级教师（小学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '415', '二级教师（小学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '416', '三级教师（小学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '420', '船舶技术人员（驾驶）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '422', '高级船长                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '423', '船长（大副）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '424', '二副                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '425', '三副                      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '430', '船舶技术人员（轮机）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '432', '高级轮机长                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '433', '轮机长（大管轮）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '434', '二管轮                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '435', '三管轮                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '440', '船舶技术人员（电机）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '442', '高级电机员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '443', '通用电机员（一等电机员）      ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '444', '二等电机员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '450', '船舶技术人员（报务）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '452', '高级报务员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '453', '通用报务员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '454', '二等报务员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '455', '限用报务员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '460', '飞行技术人员（驾驶）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '462', '一级飞行员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '463', '二级飞行员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '464', '三级飞行员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '465', '四级飞行员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '470', '飞行技术人员（领航）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '472', '一级领航员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '473', '二级领航员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '474', '三级领航员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '475', '四级领航员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '480', '飞行技术人员（通信）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '482', '一级飞行通信员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '483', '二级飞行通信员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '484', '三级飞行通信员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '485', '四级飞行通信员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '490', '飞行技术人员（机械）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '492', '一级飞行机械员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '493', '二级飞行机械员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '494', '三级飞行机械员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '495', '四级飞行机械员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '500', '船舶技术人员（引航）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '502', '高级引航员                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '503', '一、二级引航员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '504', '三、四级引航员                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '610', '自然科学研究人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '611', '研究员（自然科学）            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '612', '副研究员（自然科学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '613', '助理研究员（自然科学）        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '614', '研究实习员（自然科学）        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '620', '社会科学研究人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '621', '研究员（社会科学）            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '622', '副研究员（社会科学）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '623', '助理研究员（社会科学）        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '624', '研究实习员（社会科学）        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '640', '图书、资料专业人员            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '641', '研究馆员（图书）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '642', '副研究馆员（图书）            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '643', '馆员（图书）                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '644', '助理馆员（图书）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '645', '管理员（图书）                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '650', '文博专业人员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '651', '研究馆员（文博）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '652', '副研究馆员（文博）            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '653', '馆员（文博）                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '654', '助理馆员（文博）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '655', '管理员（文博）                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '660', '档案专业人员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '661', '研究馆员（档案）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '662', '副研究馆员（档案）            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '663', '馆员（档案）                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '664', '助理馆员（档案）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '665', '管理员（档案）                ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '670', '群众文化专业人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '671', '研究馆员（群众文化）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '672', '副研究馆员（群众文化）        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '673', '馆员（群众文化）              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '674', '助理馆员（群众文化）          ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '675', '管理员（群众文化）            ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '680', '审计专业人员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '682', '高级审计师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '683', '审计师                        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '684', '助理审计师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '685', '审计员                        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '690', '法医专业人员                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '691', '主任法医师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '692', '副主任法医师                  ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '693', '主检法医师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '694', '法医师                        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '695', '法医士                        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '980', '思想政治工作人员              ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '982', '高级政工师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '983', '政工师                        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '984', '助理政工师                    ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 34, '985', '政工员                        ', 'TECHNICAL_QUALIFICATIONS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 35, '1', '男', 'GENDER', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 35, '2', '女', 'GENDER', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 35, '3', '未知', 'GENDER', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 35, 'ASSISTANT', '助手', 'AGENT_MSG_ROLE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 35, 'SYSTEM', '系统', 'AGENT_MSG_ROLE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 35, 'USER', '用户', 'AGENT_MSG_ROLE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 36, '0', '试用', 'TENANT_STATUS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 36, '1', '正式', 'TENANT_STATUS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 36, '2', '禁用', 'TENANT_STATUS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 36, '3', '过期', 'TENANT_STATUS', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 37, '0', '共享库', 'ISOLATION_MODE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 37, '1', '独立库', 'ISOLATION_MODE', NULL, 0);
INSERT INTO `sys_dict_item`(`tenant_id`, `dict_id`, `value`, `label`, `dict_key`, `description`, `sort`) VALUES (101, 37, '2', '独立Schema', 'ISOLATION_MODE', NULL, 0);
update `sys_dict_item` set `sort` = `id`*10, `label` = trim(`label`), value = UPPER(value);

DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `file_name` varchar(100)  DEFAULT NULL COMMENT '文件名',
  `bucket_name` varchar(200)  DEFAULT NULL COMMENT '桶名',
  `original` varchar(100)  DEFAULT NULL,
  `type` varchar(50)  DEFAULT NULL COMMENT '类型',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `organ_code` varchar(64)  DEFAULT NULL COMMENT '所属机构编码',
  `create_by` varchar(32)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `update_by` varchar(32)  DEFAULT NULL,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1)  NULL DEFAULT '0',
  PRIMARY KEY (`id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '文件管理表' ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `type` char(20) NULL DEFAULT '000' COMMENT '日志类型 关联LOG_TYPE',
  `title` varchar(255) DEFAULT NULL COMMENT '日志标题',
  `service_id` varchar(32) DEFAULT NULL COMMENT '访问服务名',
  `remote_addr` varchar(255) DEFAULT NULL COMMENT '访问IP地址',
  `user_agent` varchar(1000) DEFAULT NULL COMMENT '请求方式:User-Agent，访问工具',
  `request_uri` varchar(255) DEFAULT NULL COMMENT '请求url路径',
  `method` varchar(10)  DEFAULT NULL COMMENT '请求类型：POST GET PUT',
  `params` text  NULL COMMENT '请求参数',
  `request_header` text DEFAULT NULL COMMENT '请求头',
  `request_data` text DEFAULT NULL COMMENT '请求体JSON对象',
  `app_key` varchar(255) DEFAULT NULL COMMENT '请求之应用代码',
  `app_secret` varchar(255) DEFAULT NULL COMMENT '请求之应用密钥',
  `app_token` varchar(255) DEFAULT NULL COMMENT '请求之应用凭证',
  `time` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '执行时间',
  `header` text DEFAULT NULL COMMENT '响应头',
  `exception` text  NULL COMMENT '异常信息',
  `response_time` varchar(255) DEFAULT NULL COMMENT '响应时间',
  `status` varchar(255) DEFAULT NULL COMMENT '响应HTTP状态码',
  `message` varchar(255) DEFAULT NULL COMMENT '响应消息',
  `data` text DEFAULT NULL COMMENT '响应结果JSON对象',
  `code` varchar(255) DEFAULT NULL COMMENT '响应结果代码 0成功 1失败',
  `token` varchar(255) DEFAULT NULL COMMENT '应用凭证令牌',
  `status_text` varchar(255)  DEFAULT NULL COMMENT 'status文本描述',
  `organ_code` varchar(64)  DEFAULT NULL COMMENT '所属机构编码，备用',
  `create_by` varchar(255)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) ,
  INDEX `idx_log_create_by`(`create_by`) ,
  INDEX `idx_log_request_uri`(`request_uri`) ,
  INDEX `idx_log_type`(`type`) ,
  INDEX `idx_log_create_date`(`create_time`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '日志表' ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `sys_manufacturer`;
CREATE TABLE `sys_manufacturer`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `manufacturer_name` varchar(255)  DEFAULT NULL COMMENT '厂商名称',
  `manufacturer_code` varchar(255)  DEFAULT NULL COMMENT '厂商编码',
  `artisan_name` varchar(255)  DEFAULT NULL COMMENT '技术人员姓名',
  `artisan_phone` varchar(20)  DEFAULT NULL COMMENT '技术人员手机号',
  `service_name` varchar(255)  DEFAULT NULL COMMENT '业务人员姓名',
  `service_phone` varchar(20)  DEFAULT NULL COMMENT '业务人员手机号',
  `manufacturer_desc` varchar(255)  DEFAULT NULL COMMENT '厂商描述',
  `organ_code` varchar(64)  DEFAULT NULL COMMENT '所属机构编码',
  `create_by` varchar(255)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '厂商表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_manufacturer` VALUES (1, 100, NULL, '华为', 'huawei', 'edison', '13511111111', 'tom', '13522222222', '最大的通讯设备供应商', 'default', 'admin', '2025-03-24 22:33:09', 'admin', '2025-03-24 22:33:09', '0');
INSERT INTO `sys_manufacturer` VALUES (2, 100, NULL, '大疆', 'dajiang', '张三', '13133333333', '李四', '13112341234', '最大无人机供应商', 'default', 'admin', '2025-03-24 22:33:09', 'admin', '2025-03-24 22:33:09', '0');

INSERT INTO `sys_manufacturer` VALUES (3, 101, NULL, '华为', 'huawei', 'edison', '13511111111', 'tom', '13522222222', '最大的通讯设备供应商', 'default', 'admin', '2025-03-24 22:33:09', 'admin', '2025-03-24 22:33:09', '0');
INSERT INTO `sys_manufacturer` VALUES (4, 101, NULL, '大疆', 'dajiang', '张三', '13133333333', '李四', '13112341234', '最大无人机供应商', 'default', 'admin', '2025-03-24 22:33:09', 'admin', '2025-03-24 22:33:09', '0');

DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `menu_name` varchar(32)  DEFAULT NULL COMMENT '菜单名称',
  `permission` varchar(32)  DEFAULT NULL COMMENT '按钮权限唯一标识，和数据字典opt_status一致，暂未使用',
  `path` varchar(1024)  DEFAULT NULL COMMENT '前端路径',
  `parent_id` int(11) NULL DEFAULT 0 COMMENT '父菜单ID，0表示无上级',
  `icon` varchar(500)  DEFAULT NULL COMMENT '图标，参见https://3x.antdv.com/components/icon-cn',
  `menu_describe` varchar(255)  DEFAULT NULL COMMENT '菜单描述',
  `sort` int(11) NULL DEFAULT 1 COMMENT '排序值',
  `keep_alive` char(1)  NULL DEFAULT '0' COMMENT '是否开启路由缓冲 0-否 1-是',
  `type` char(1)  NULL DEFAULT '0' COMMENT '类型 0-菜单 1-按钮',
  `application_code` varchar(50)  DEFAULT NULL COMMENT '应用编码',
  `create_by` varchar(255)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_menu` VALUES (1, 100, NULL, '基础管理', '', '', 0, 'slack-square-outlined', NULL, 1000, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (2, 100, NULL, '菜单管理', '', 'System/Menu', 1, 'menu-outlined', NULL, 1010, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (3, 100, NULL, '用户管理', '', 'System/User', 1, 'user-outlined', NULL, 1020, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (4, 100, NULL, '字典管理', '', 'System/Dict', 1, 'schedule-outlined', NULL, 1030, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (5, 100, NULL, '角色管理', '', 'System/Role', 1, 'usergroup-add-outlined', NULL, 1040, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (6, 100, NULL, '机构管理', '', '', 0, 'group-outlined', NULL, 1200, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (7, 100, NULL, '单位管理', '', 'Organ/Organ', 6, 'hdd-outlined', NULL, 1210, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, '2025-06-16 10:52:19', '0');
INSERT INTO `sys_menu` VALUES (8, 100, NULL, '部门管理', '', 'Organ/Department', 6, 'apartment-outlined', NULL, 1220, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, '2025-06-16 10:52:22', '0');
INSERT INTO `sys_menu` VALUES (9, 100, NULL, '员工管理', '', 'Organ/Staff', 6, 'user-add-outlined', NULL, 1230, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, '2025-06-16 10:52:24', '0');
INSERT INTO `sys_menu` VALUES (10, 100, NULL, '资源管理', '', '', 0, 'bank-outlined', NULL, 1100, '0', '0', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (11, 100, NULL, '厂商管理', '', 'Resource/Manufacturer', 10, 'tag-outlined', NULL, 1110, '0', '0', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-16 10:52:33', '0');
INSERT INTO `sys_menu` VALUES (12, 100, NULL, '应用管理', '', 'Resource/Application', 10, 'appstore-outlined', NULL, 1120, '0', '0', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-16 10:52:35', '0');
INSERT INTO `sys_menu` VALUES (13, 100, NULL, '数源管理', '', 'Resource/Datasource', 10, 'box-plot-outlined', NULL, 1130, '0', '0', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-16 10:52:38', '0');
INSERT INTO `sys_menu` VALUES (14, 100, NULL, '权限管理', 'role_perm', NULL, 5, NULL, NULL, 10, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (15, 100, NULL, '增加菜单', 'menu_add', NULL, 2, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (16, 100, NULL, '删除菜单', 'menu_del', NULL, 2, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (17, 100, NULL, '修改菜单', 'menu_edit', NULL, 2, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (18, 100, NULL, '查询菜单', 'menu_query', NULL, 2, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (19, 100, NULL, '增加用户', 'user_add', NULL, 3, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (20, 100, NULL, '删除用户', 'user_del', NULL, 3, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (21, 100, NULL, '修改用户', 'user_edit', NULL, 3, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (22, 100, NULL, '查询用户', 'user_query', NULL, 3, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (23, 100, NULL, '增加字典', 'dict_add', NULL, 4, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (24, 100, NULL, '删除字典', 'dict_del', NULL, 4, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (25, 100, NULL, '修改字典', 'dict_edit', NULL, 4, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (26, 100, NULL, '查询字典', 'dict_query', NULL, 4, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (27, 100, NULL, '增加角色', 'role_add', NULL, 5, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (28, 100, NULL, '删除角色', 'role_del', NULL, 5, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (29, 100, NULL, '修改角色', 'role_edit', NULL, 5, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (30, 100, NULL, '查询角色', 'role_query', NULL, 5, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (31, 100, NULL, '增加单位', 'org_add', NULL, 7, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-16 10:53:45', '0');
INSERT INTO `sys_menu` VALUES (32, 100, NULL, '删除单位', 'org_del', NULL, 7, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-16 10:53:45', '0');
INSERT INTO `sys_menu` VALUES (33, 100, NULL, '修改单位', 'org_edit', NULL, 7, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:53:45', '0');
INSERT INTO `sys_menu` VALUES (34, 100, NULL, '查询单位', 'org_query', NULL, 7, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:53:45', '0');
INSERT INTO `sys_menu` VALUES (35, 100, NULL, '增加部门', 'dept_add', NULL, 8, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:53:58', '0');
INSERT INTO `sys_menu` VALUES (36, 100, NULL, '删除部门', 'dept_del', NULL, 8, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:53:59', '0');
INSERT INTO `sys_menu` VALUES (37, 100, NULL, '修改部门', 'dept_edit', NULL, 8, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:53:59', '0');
INSERT INTO `sys_menu` VALUES (38, 100, NULL, '查询部门', 'dept_query', NULL, 8, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:53:59', '0');
INSERT INTO `sys_menu` VALUES (39, 100, NULL, '增加员工', 'staff_add', NULL, 9, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:07', '0');
INSERT INTO `sys_menu` VALUES (40, 100, NULL, '删除员工', 'staff_del', NULL, 9, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:07', '0');
INSERT INTO `sys_menu` VALUES (41, 100, NULL, '修改员工', 'staff_edit', NULL, 9, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:07', '0');
INSERT INTO `sys_menu` VALUES (42, 100, NULL, '查询员工', 'staff_query', NULL, 9, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:07', '0');
INSERT INTO `sys_menu` VALUES (43, 100, NULL, '增加厂商', 'manuf_add', NULL, 11, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:27', '0');
INSERT INTO `sys_menu` VALUES (44, 100, NULL, '删除厂商', 'manuf_del', NULL, 11, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:28', '0');
INSERT INTO `sys_menu` VALUES (45, 100, NULL, '修改厂商', 'manuf_edit', NULL, 11, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:28', '0');
INSERT INTO `sys_menu` VALUES (46, 100, NULL, '查询厂商', 'manuf_query', NULL, 11, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:28', '0');
INSERT INTO `sys_menu` VALUES (47, 100, NULL, '增加应用', 'app_add', NULL, 12, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:37', '0');
INSERT INTO `sys_menu` VALUES (48, 100, NULL, '删除应用', 'app_del', NULL, 12, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:37', '0');
INSERT INTO `sys_menu` VALUES (49, 100, NULL, '修改应用', 'app_edit', NULL, 12, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:37', '0');
INSERT INTO `sys_menu` VALUES (50, 100, NULL, '查询应用', 'app_query', NULL, 12, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:37', '0');
INSERT INTO `sys_menu` VALUES (51, 100, NULL, '增加数源', 'ds_add', NULL, 13, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:49', '0');
INSERT INTO `sys_menu` VALUES (52, 100, NULL, '删除数源', 'ds_del', NULL, 13, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:49', '0');
INSERT INTO `sys_menu` VALUES (53, 100, NULL, '修改数源', 'ds_edit', NULL, 13, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:49', '0');
INSERT INTO `sys_menu` VALUES (54, 100, NULL, '查询数源', 'ds_query', NULL, 13, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-16 10:54:49', '0');
INSERT INTO `sys_menu` VALUES (55, 100, NULL, '增加字典条目', 'dict_item_add', NULL, 4, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (56, 100, NULL, '删除字典条目', 'dict_item_del', NULL, 4, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:11', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (57, 100, NULL, '修改字典条目', 'dict_item_edit', NULL, 4, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:11', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (58, 100, NULL, '查询字典条目', 'dict_item_query', NULL, 4, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:11', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (59, 100, NULL, '日志管理', '', '', 0, 'switcher-outlined', NULL, 700, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (61, 100, NULL, '日志查询', '', 'Log/Log', 59, 'appstore-outlined', NULL, 12, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-16 11:00:27', '0');
INSERT INTO `sys_menu` VALUES (63, 100, NULL, '智能体平台', '', '', 0, 'switcher-outlined', NULL, 600, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (64, 100, NULL, '机器人对话', '', 'Agent/chat', 63, 'tag-outlined', NULL, 670, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-16 11:03:48', '0');
INSERT INTO `sys_menu` VALUES (65, 100, NULL, 'AI应用管理', '', 'Aigc/App', 63, 'appstore-outlined', NULL, 630, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-16 11:03:48', '0');
INSERT INTO `sys_menu` VALUES (66, 100, NULL, '模型管理', '', 'Aigc/model', 63, 'bank-outlined', NULL, 620, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-16 11:03:48', '0');
INSERT INTO `sys_menu` VALUES (67, 100, NULL, '向量库管理', '', 'Aigc/EmbedStore', 63, 'bank-outlined', NULL, 680, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-16 11:03:48', '0');
INSERT INTO `sys_menu` VALUES (68, 100, NULL, '知识库管理', '', 'Aigc/knowledge', 63, 'bank-outlined', NULL, 690, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-16 11:03:48', '0');
INSERT INTO `sys_menu` VALUES (69, 100, NULL, '消息查询', '', 'Aigc/message', 63, 'bank-outlined', NULL, 672, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-16 11:03:48', '0');
INSERT INTO `sys_menu` VALUES (75, 100, NULL, '完成对话操作', 'chat:completions', NULL, 64, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-16 10:56:59', '0');
INSERT INTO `sys_menu` VALUES (76, 100, NULL, '清除聊天历史', 'chat:messages:clean', NULL, 64, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-16 10:56:59', '0');
INSERT INTO `sys_menu` VALUES (77, 100, NULL, '删除日志', 'log_del', NULL, 61, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-16 10:58:55', '0');
INSERT INTO `sys_menu` VALUES (78, 100, NULL, '查询日志', 'log_query', NULL, 61, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-16 10:58:55', '0');
INSERT INTO `sys_menu` VALUES (81, 100, NULL, '增加智能体应用', 'aigc:app:add', NULL, 65, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-16 11:02:07', '0');
INSERT INTO `sys_menu` VALUES (82, 100, NULL, '删除智能体应用', 'aigc:app:delete', NULL, 65, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:02:07', '0');
INSERT INTO `sys_menu` VALUES (83, 100, NULL, '修改智能体应用', 'aigc:app:update', NULL, 65, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:02:07', '0');
INSERT INTO `sys_menu` VALUES (84, 100, NULL, '查询智能体应用', 'aigc:app:query', NULL, 65, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:02:26', '0');
INSERT INTO `sys_menu` VALUES (85, 100, NULL, 'Chat权限', 'chat:completions', NULL, 64, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:02:58', '0');
INSERT INTO `sys_menu` VALUES (86, 100, NULL, '文本向量化', 'aigc:embedding:text', NULL, 68, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:23', '0');
INSERT INTO `sys_menu` VALUES (87, 100, NULL, '文档向量化', 'aigc:embedding:docs', NULL, 68, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:23', '0');
INSERT INTO `sys_menu` VALUES (88, 100, NULL, '增加大语言模型', 'aigc:model:add', NULL, 66, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:35', '0');
INSERT INTO `sys_menu` VALUES (89, 100, NULL, '删除大语言模型', 'aigc:model:delete', NULL, 66, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:35', '0');
INSERT INTO `sys_menu` VALUES (90, 100, NULL, '修改大语言模型', 'aigc:model:update', NULL, 66, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:35', '0');
INSERT INTO `sys_menu` VALUES (91, 100, NULL, '查询大语言模型', 'aigc:model:query', NULL, 66, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:35', '0');
INSERT INTO `sys_menu` VALUES (92, 100, NULL, '增加向量库', 'aigc:embed-store:add', NULL, 67, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:45', '0');
INSERT INTO `sys_menu` VALUES (93, 100, NULL, '删除向量库', 'aigc:embed-store:delete', NULL, 67, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:45', '0');
INSERT INTO `sys_menu` VALUES (94, 100, NULL, '修改向量库', 'aigc:embed-store:update', NULL, 67, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:45', '0');
INSERT INTO `sys_menu` VALUES (95, 100, NULL, '查询向量库', 'aigc:embed-store:query', NULL, 67, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:45', '0');
INSERT INTO `sys_menu` VALUES (96, 100, NULL, '增加知识库', 'aigc:knowledge:add', NULL, 68, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:55', '0');
INSERT INTO `sys_menu` VALUES (97, 100, NULL, '删除知识库', 'aigc:knowledge:delete', NULL, 68, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:56', '0');
INSERT INTO `sys_menu` VALUES (98, 100, NULL, '修改知识库', 'aigc:knowledge:update', NULL, 68, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:56', '0');
INSERT INTO `sys_menu` VALUES (99, 100, NULL, '查询知识库', 'aigc:knowledge:query', NULL, 68, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:04:56', '0');
INSERT INTO `sys_menu` VALUES (100, 100, NULL, '增加文档', 'aigc:docs:add', NULL, 124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:05:15', '0');
INSERT INTO `sys_menu` VALUES (101, 100, NULL, '删除文档', 'aigc:docs:delete', NULL, 124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:05:16', '0');
INSERT INTO `sys_menu` VALUES (102, 100, NULL, '修改文档', 'aigc:docs:update', NULL, 124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:05:16', '0');
INSERT INTO `sys_menu` VALUES (103, 100, NULL, '查询文档', 'aigc:docs:query', NULL, 124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:05:16', '0');
INSERT INTO `sys_menu` VALUES (104, 100, NULL, '删除对话数据', 'aigc:message:delete', NULL, 69, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:06:02', '0');
INSERT INTO `sys_menu` VALUES (105, 100, NULL, '查询对话数据', 'aigc:message:query', NULL, 69, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:06:03', '0');
INSERT INTO `sys_menu` VALUES (107, 100, NULL, '渠道管理', '', 'Aigc/AppApi', 63, 'key-outlined', 'AIGC 应用 API 管理', 640, '0', '0', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:06:38', '0');
INSERT INTO `sys_menu` VALUES (108, 100, NULL, '新增渠道', 'aigc:app-api:add', NULL, 107, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:06:45', '0');
INSERT INTO `sys_menu` VALUES (109, 100, NULL, '删除渠道', 'aigc:app-api:delete', NULL, 107, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-16 11:06:45', '0');
INSERT INTO `sys_menu` VALUES (110, 100, NULL, '修改渠道', 'aigc:app-api:update', NULL, 107, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:06:45', '0');
INSERT INTO `sys_menu` VALUES (111, 100, NULL, '查询渠道', 'aigc:app-api:query', NULL, 107, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:06:45', '0');
INSERT INTO `sys_menu` VALUES (112, 100, NULL, '提示语管理', '', 'Aigc/Prompt', 63, 'edit-outlined', 'AIGC 应用提示词管理', 660, '0', '0', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:06:48', '0');
INSERT INTO `sys_menu` VALUES (113, 100, NULL, '新增提示语', 'aigc:prompt:add', NULL, 112, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:06:54', '0');
INSERT INTO `sys_menu` VALUES (114, 100, NULL, '删除提示语', 'aigc:prompt:delete', NULL, 112, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:06:55', '0');
INSERT INTO `sys_menu` VALUES (115, 100, NULL, '修改提示语', 'aigc:prompt:update', NULL, 112, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:06:55', '0');
INSERT INTO `sys_menu` VALUES (116, 100, NULL, '查询提示语', 'aigc:prompt:query', NULL, 112, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:06:55', '0');
INSERT INTO `sys_menu` VALUES (117, 100, NULL, '对话管理', '', 'Aigc/Conversation', 63, 'message-outlined', 'AIGC 对话管理', 671, '0', '0', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:06:57', '0');
INSERT INTO `sys_menu` VALUES (118, 100, NULL, '新增对话', 'aigc:conversation:add', NULL, 117, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:07:04', '0');
INSERT INTO `sys_menu` VALUES (119, 100, NULL, '删除对话', 'aigc:conversation:delete', NULL, 117, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:07:04', '0');
INSERT INTO `sys_menu` VALUES (120, 100, NULL, '修改对话', 'aigc:conversation:update', NULL, 117, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:07:04', '0');
INSERT INTO `sys_menu` VALUES (121, 100, NULL, '查询对话', 'aigc:conversation:query', NULL, 117, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:07:04', '0');
INSERT INTO `sys_menu` VALUES (122, 100, NULL, '新增消息', 'aigc:message:add', NULL, 69, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:07:28', '0');
INSERT INTO `sys_menu` VALUES (123, 100, NULL, '修改消息', 'aigc:message:update', NULL, 69, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:07:28', '0');
INSERT INTO `sys_menu` VALUES (124, 100, NULL, '文档管理', '', 'Aigc/Doc', 63, 'copy-outlined', 'AIGC 文档管理', 691, '0', '0', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:07:49', '0');
INSERT INTO `sys_menu` VALUES (125, 100, NULL, '文档切片', '', 'Aigc/Slice', 63, 'dash-outlined', 'AIGC 文档切片管理', 692, '0', '0', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:07:51', '0');
INSERT INTO `sys_menu` VALUES (126, 100, NULL, '新增文档切片', 'aigc:docs:slice:add', NULL, 125, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:08:24', '0');
INSERT INTO `sys_menu` VALUES (127, 100, NULL, '删除文档切片', 'aigc:docs:slice:delete', NULL, 125, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:08:24', '0');
INSERT INTO `sys_menu` VALUES (128, 100, NULL, '修改文档切片', 'aigc:docs:slice:update', NULL, 125, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:08:24', '0');
INSERT INTO `sys_menu` VALUES (129, 100, NULL, '查询文档切片', 'aigc:docs:slice:query', NULL, 125, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:08:24', '0');
INSERT INTO `sys_menu` VALUES (130, 100, NULL, '新增文件', 'aigc:oss:add', NULL, 124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:08:35', '0');
INSERT INTO `sys_menu` VALUES (131, 100, NULL, '删除文件', 'aigc:oss:delete', NULL, 124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:08:35', '0');
INSERT INTO `sys_menu` VALUES (132, 100, NULL, '修改文件', 'aigc:oss:update', NULL, 124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:08:35', '0');
INSERT INTO `sys_menu` VALUES (133, 100, NULL, '查询文件', 'aigc:oss:query', NULL, 124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:08:35', '0');
INSERT INTO `sys_menu` VALUES (134, 100, NULL, '客户关系管理', '', '', 0, 'message-outlined', NULL, 100, '0', '0', 'CRM', NULL, '2025-06-16 10:51:13', NULL, NULL, '0');
INSERT INTO `sys_menu` VALUES (135, 100, NULL, '客户管理', '', 'Relation/Customer', 134, 'ie-outlined', '客户管理', 110, '0', '0', 'CRM', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:09:12', '0');
INSERT INTO `sys_menu` VALUES (136, 100, NULL, '新增客户', 'crm:customer:add', NULL, 135, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:09:23', '0');
INSERT INTO `sys_menu` VALUES (137, 100, NULL, '删除客户', 'crm:customer:delete', NULL, 135, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:13', NULL, '2025-06-16 11:09:23', '0');
INSERT INTO `sys_menu` VALUES (138, 100, NULL, '修改客户', 'crm:customer:update', NULL, 135, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:09:23', '0');
INSERT INTO `sys_menu` VALUES (139, 100, NULL, '查询客户', 'crm:customer:query', NULL, 135, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:09:23', '0');
INSERT INTO `sys_menu` VALUES (140, 100, NULL, '联系人管理', '', 'Relation/Contact', 134, 'ant-design-outlined', '联系人管理', 130, '0', '0', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:09:52', '0');
INSERT INTO `sys_menu` VALUES (141, 100, NULL, '新增联系人', 'crm:contact:add', NULL, 140, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:09:59', '0');
INSERT INTO `sys_menu` VALUES (142, 100, NULL, '删除联系人', 'crm:contact:delete', NULL, 140, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:00', '0');
INSERT INTO `sys_menu` VALUES (143, 100, NULL, '修改联系人', 'crm:contact:update', NULL, 140, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:00', '0');
INSERT INTO `sys_menu` VALUES (144, 100, NULL, '查询联系人', 'crm:contact:query', NULL, 140, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:00', '0');
INSERT INTO `sys_menu` VALUES (145, 100, NULL, '联系人社交关系', '', 'Relation/SocialRelationship', 134, 'yuque-outlined', '社会关系管理', 140, '0', '0', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:05', '0');
INSERT INTO `sys_menu` VALUES (146, 100, NULL, '新增关系', 'crm:relation:add', NULL, 145, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:13', '0');
INSERT INTO `sys_menu` VALUES (147, 100, NULL, '删除关系', 'crm:relation:delete', NULL, 145, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:13', '0');
INSERT INTO `sys_menu` VALUES (148, 100, NULL, '修改关系', 'crm:relation:update', NULL, 145, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:13', '0');
INSERT INTO `sys_menu` VALUES (149, 100, NULL, '查询关系', 'crm:relation:query', NULL, 145, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:13', '0');
INSERT INTO `sys_menu` VALUES (150, 100, NULL, '跟踪记录', '', 'Relation/FollowRecord', 134, 'radius-setting-outlined', '跟踪记录管理', 160, '0', '0', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:18', '0');
INSERT INTO `sys_menu` VALUES (151, 100, NULL, '新增跟踪', 'crm:follow:add', NULL, 150, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:28', '0');
INSERT INTO `sys_menu` VALUES (152, 100, NULL, '删除跟踪', 'crm:follow:delete', NULL, 150, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:28', '0');
INSERT INTO `sys_menu` VALUES (153, 100, NULL, '修改跟踪', 'crm:follow:update', NULL, 150, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:28', '0');
INSERT INTO `sys_menu` VALUES (154, 100, NULL, '查询跟踪', 'crm:follow:query', NULL, 150, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:28', '0');
INSERT INTO `sys_menu` VALUES (155, 100, NULL, '社交账号', '', 'Relation/SocialDetails', 134, 'message-outlined', '社交账号管理', 150, '0', '0', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:34', '0');
INSERT INTO `sys_menu` VALUES (156, 100, NULL, '新增社交账号', 'crm:social:add', NULL, 155, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:44', '0');
INSERT INTO `sys_menu` VALUES (157, 100, NULL, '删除社交账号', 'crm:social:delete', NULL, 155, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:45', '0');
INSERT INTO `sys_menu` VALUES (158, 100, NULL, '修改社交账号', 'crm:social:update', NULL, 155, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:45', '0');
INSERT INTO `sys_menu` VALUES (159, 100, NULL, '查询社交账号', 'crm:social:query', NULL, 155, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:45', '0');
INSERT INTO `sys_menu` VALUES (160, 100, NULL, '客户关系', '', 'Relation/CustomerRelation', 134, 'alibaba-outlined', '客户关系管理', 120, '0', '0', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:10:58', '0');
INSERT INTO `sys_menu` VALUES (161, 100, NULL, '新增客户关系', 'crm:customer-relation:add', NULL, 160, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:11:22', '0');
INSERT INTO `sys_menu` VALUES (162, 100, NULL, '删除客户关系', 'crm:customer-relation:delete', NULL, 160, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:11:22', '0');
INSERT INTO `sys_menu` VALUES (163, 100, NULL, '修改客户关系', 'crm:customer-relation:update', NULL, 160, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:11:22', '0');
INSERT INTO `sys_menu` VALUES (164, 100, NULL, '查询客户关系', 'crm:customer-relation:query', NULL, 160, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:11:22', '0');
INSERT INTO `sys_menu` VALUES (165, 100, NULL, '租户管理', '', '', 0, 'alibaba-outlined', '租户管理', 0, '0', '0', 'USR', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:12:03', '0');
INSERT INTO `sys_menu` VALUES (166, 100, NULL, '租户', '', 'Tenant/Tenant', 165, 'alibaba-outlined', '租户', 166, '0', '0', 'USR', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:12:03', '0');
INSERT INTO `sys_menu` VALUES (167, 100, NULL, '新增租户', 'usr:tenant:add', NULL, 166, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:14', NULL, '2025-06-16 11:12:08', '0');
INSERT INTO `sys_menu` VALUES (168, 100, NULL, '删除租户', 'usr:tenant:delete', NULL, 166, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:12:08', '0');
INSERT INTO `sys_menu` VALUES (169, 100, NULL, '修改租户', 'usr:tenant:update', NULL, 166, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:12:08', '0');
INSERT INTO `sys_menu` VALUES (170, 100, NULL, '查询租户', 'usr:tenant:query', NULL, 166, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:12:08', '0');
INSERT INTO `sys_menu` VALUES (171, 100, NULL, '租户配置', '', 'Tenant/TenantConfig', 165, 'alibaba-outlined', '租户管理', 171, '0', '0', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:13:41', '0');
INSERT INTO `sys_menu` VALUES (172, 100, NULL, '新增租户配置', 'usr:tenant-config:add', NULL, 171, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:13:37', '0');
INSERT INTO `sys_menu` VALUES (173, 100, NULL, '删除租户配置', 'usr:tenant-config:delete', NULL, 171, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:13:37', '0');
INSERT INTO `sys_menu` VALUES (174, 100, NULL, '修改租户配置', 'usr:tenant-config:update', NULL, 171, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:13:37', '0');
INSERT INTO `sys_menu` VALUES (175, 100, NULL, '查询租户配置', 'usr:tenant-config:query', NULL, 171, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:13:37', '0');
INSERT INTO `sys_menu` VALUES (176, 100, NULL, '实体维护', '', 'Tenant/Table', 165, 'alibaba-outlined', '实体维护管理', 176, '0', '0', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:13:58', '0');
INSERT INTO `sys_menu` VALUES (177, 100, NULL, '新增实体', 'usr:table:add', NULL, 176, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:15', '0');
INSERT INTO `sys_menu` VALUES (178, 100, NULL, '删除实体', 'usr:table:delete', NULL, 176, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:15', '0');
INSERT INTO `sys_menu` VALUES (179, 100, NULL, '修改实体', 'usr:table:update', NULL, 176, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:15', '0');
INSERT INTO `sys_menu` VALUES (180, 100, NULL, '查询实体', 'usr:table:query', NULL, 176, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:15', '0');
INSERT INTO `sys_menu` VALUES (181, 100, NULL, '实体属性维护', '', 'Tenant/TableField', 165, 'alibaba-outlined', '实体属性管理', 181, '0', '0', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:23', '0');
INSERT INTO `sys_menu` VALUES (182, 100, NULL, '新增实体属性', 'usr:field:add', NULL, 181, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:28', '0');
INSERT INTO `sys_menu` VALUES (183, 100, NULL, '删除实体属性', 'usr:field:delete', NULL, 181, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:28', '0');
INSERT INTO `sys_menu` VALUES (184, 100, NULL, '修改实体属性', 'usr:field:update', NULL, 181, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:28', '0');
INSERT INTO `sys_menu` VALUES (185, 100, NULL, '查询实体属性', 'usr:field:query', NULL, 181, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:28', '0');
INSERT INTO `sys_menu` VALUES (186, 100, NULL, '元数据定义', '', 'Tenant/MetaData', 165, 'alibaba-outlined', '元数据定义管理', 186, '0', '0', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:34', '0');
INSERT INTO `sys_menu` VALUES (187, 100, NULL, '新增元定义', 'usr:meta:add', NULL, 186, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:41', '0');
INSERT INTO `sys_menu` VALUES (188, 100, NULL, '删除元定义', 'usr:meta:delete', NULL, 186, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:41', '0');
INSERT INTO `sys_menu` VALUES (189, 100, NULL, '修改元定义', 'usr:meta:update', NULL, 186, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:41', '0');
INSERT INTO `sys_menu` VALUES (190, 100, NULL, '查询元定义', 'usr:meta:query', NULL, 186, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:41', '0');
INSERT INTO `sys_menu` VALUES (191, 100, NULL, '元素定义', '', 'Tenant/Element', 165, 'alibaba-outlined', '元素定义管理', 191, '0', '0', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:45', '0');
INSERT INTO `sys_menu` VALUES (192, 100, NULL, '新增元素', 'usr:element:add', NULL, 191, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:50', '0');
INSERT INTO `sys_menu` VALUES (193, 100, NULL, '删除元素', 'usr:element:delete', NULL, 191, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:50', '0');
INSERT INTO `sys_menu` VALUES (194, 100, NULL, '修改元素', 'usr:element:update', NULL, 191, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:50', '0');
INSERT INTO `sys_menu` VALUES (195, 100, NULL, '查询元素', 'usr:element:query', NULL, 191, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:15', NULL, '2025-06-16 11:14:50', '0');
INSERT INTO `sys_menu` VALUES (196, 100, NULL, '项目定义', '', 'Tenant/Project', 165, 'alibaba-outlined', '项目管理', 196, '0', '0', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:10', '0');
INSERT INTO `sys_menu` VALUES (197, 100, NULL, '新增项目', 'erp:project:add', NULL, 196, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:28', '0');
INSERT INTO `sys_menu` VALUES (198, 100, NULL, '删除项目', 'erp:project:delete', NULL, 196, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:28', '0');
INSERT INTO `sys_menu` VALUES (199, 100, NULL, '修改项目', 'erp:project:update', NULL, 196, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:28', '0');
INSERT INTO `sys_menu` VALUES (200, 100, NULL, '查询项目', 'erp:project:query', NULL, 196, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:28', '0');
INSERT INTO `sys_menu` VALUES (201, 100, NULL, '团队定义', '', 'Tenant/Team', 165, 'alibaba-outlined', '项目管理', 201, '0', '0', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:32', '0');
INSERT INTO `sys_menu` VALUES (202, 100, NULL, '新增团队', 'erp:team:add', NULL, 201, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:38', '0');
INSERT INTO `sys_menu` VALUES (203, 100, NULL, '删除团队', 'erp:team:delete', NULL, 201, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:38', '0');
INSERT INTO `sys_menu` VALUES (204, 100, NULL, '修改团队', 'erp:team:update', NULL, 201, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:38', '0');
INSERT INTO `sys_menu` VALUES (205, 100, NULL, '查询团队', 'erp:team:query', NULL, 201, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:38', '0');
INSERT INTO `sys_menu` VALUES (206, 100, NULL, '合作定义', '', 'Tenant/Affiliation', 165, 'alibaba-outlined', '项目管理', 206, '0', '0', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:42', '0');
INSERT INTO `sys_menu` VALUES (207, 100, NULL, '新增合作', 'erp:affiliation:add', NULL, 206, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:47', '0');
INSERT INTO `sys_menu` VALUES (208, 100, NULL, '删除合作', 'erp:affiliation:delete', NULL, 206, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:47', '0');
INSERT INTO `sys_menu` VALUES (209, 100, NULL, '修改合作', 'erp:affiliation:update', NULL, 206, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:47', '0');
INSERT INTO `sys_menu` VALUES (210, 100, NULL, '查询合作', 'erp:affiliation:query', NULL, 206, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:16', NULL, '2025-06-16 11:15:48', '0');

INSERT INTO `sys_menu` VALUES (1001, 101, NULL, '基础管理', '', '', 0, 'slack-square-outlined', NULL, 1000, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1002, 101, NULL, '菜单管理', '', 'System/Menu', 1001, 'menu-outlined', NULL, 1010, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1003, 101, NULL, '用户管理', '', 'System/User', 1001, 'user-outlined', NULL, 1020, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1004, 101, NULL, '字典管理', '', 'System/Dict', 1001, 'schedule-outlined', NULL, 1030, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1005, 101, NULL, '角色管理', '', 'System/Role', 1001, 'usergroup-add-outlined', NULL, 1040, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1006, 101, NULL, '机构管理', '', '', 0, 'group-outlined', NULL, 1200, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1007, 101, NULL, '单位管理', '', 'Organ/Organ', 1006, 'hdd-outlined', NULL, 1210, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1008, 101, NULL, '部门管理', '', 'Organ/Department', 1006, 'apartment-outlined', NULL, 1220, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1009, 101, NULL, '员工管理', '', 'Organ/Staff', 1006, 'user-add-outlined', NULL, 1230, '0', '0', 'USR', NULL, '2025-06-16 10:51:08', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1010, 101, NULL, '资源管理', '', '', 0, 'bank-outlined', NULL, 1100, '0', '0', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1011, 101, NULL, '厂商管理', '', 'Resource/Manufacturer', 1010, 'tag-outlined', NULL, 1110, '0', '0', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1012, 101, NULL, '应用管理', '', 'Resource/Application', 1010, 'appstore-outlined', NULL, 1120, '0', '0', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1013, 101, NULL, '数源管理', '', 'Resource/Datasource', 1010, 'box-plot-outlined', NULL, 1130, '0', '0', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1014, 101, NULL, '权限管理', 'role_perm', NULL, 1005, NULL, NULL, 10, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:48:41', '0');
INSERT INTO `sys_menu` VALUES (1015, 101, NULL, '增加菜单', 'menu_add', NULL, 1002, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:48:14', '0');
INSERT INTO `sys_menu` VALUES (1016, 101, NULL, '删除菜单', 'menu_del', NULL, 1002, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:48:32', '0');
INSERT INTO `sys_menu` VALUES (1017, 101, NULL, '修改菜单', 'menu_edit', NULL, 1002, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:48:32', '0');
INSERT INTO `sys_menu` VALUES (1018, 101, NULL, '查询菜单', 'menu_query', NULL, 1002, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:48:32', '0');
INSERT INTO `sys_menu` VALUES (1019, 101, NULL, '增加用户', 'user_add', NULL, 1003, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:48:07', '0');
INSERT INTO `sys_menu` VALUES (1020, 101, NULL, '删除用户', 'user_del', NULL, 1003, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:48:07', '0');
INSERT INTO `sys_menu` VALUES (1021, 101, NULL, '修改用户', 'user_edit', NULL, 1003, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:48:07', '0');
INSERT INTO `sys_menu` VALUES (1022, 101, NULL, '查询用户', 'user_query', NULL, 1003, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:48:07', '0');
INSERT INTO `sys_menu` VALUES (1023, 101, NULL, '增加字典', 'dict_add', NULL, 1004, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:47:58', '0');
INSERT INTO `sys_menu` VALUES (1024, 101, NULL, '删除字典', 'dict_del', NULL, 1004, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:47:58', '0');
INSERT INTO `sys_menu` VALUES (1025, 101, NULL, '修改字典', 'dict_edit', NULL, 1004, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:47:58', '0');
INSERT INTO `sys_menu` VALUES (1026, 101, NULL, '查询字典', 'dict_query', NULL, 1004, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:47:58', '0');
INSERT INTO `sys_menu` VALUES (1027, 101, NULL, '增加角色', 'role_add', NULL, 1005, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:47:51', '0');
INSERT INTO `sys_menu` VALUES (1028, 101, NULL, '删除角色', 'role_del', NULL, 1005, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:47:52', '0');
INSERT INTO `sys_menu` VALUES (1029, 101, NULL, '修改角色', 'role_edit', NULL, 1005, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:47:52', '0');
INSERT INTO `sys_menu` VALUES (1030, 101, NULL, '查询角色', 'role_query', NULL, 1005, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:47:52', '0');
INSERT INTO `sys_menu` VALUES (1031, 101, NULL, '增加单位', 'org_add', NULL, 1007, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:47:45', '0');
INSERT INTO `sys_menu` VALUES (1032, 101, NULL, '删除单位', 'org_del', NULL, 1007, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:09', NULL, '2025-06-18 16:47:45', '0');
INSERT INTO `sys_menu` VALUES (1033, 101, NULL, '修改单位', 'org_edit', NULL, 1007, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-18 16:47:45', '0');
INSERT INTO `sys_menu` VALUES (1034, 101, NULL, '查询单位', 'org_query', NULL, 1007, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-18 16:47:45', '0');
INSERT INTO `sys_menu` VALUES (1035, 101, NULL, '增加部门', 'dept_add', NULL, 1008, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-18 16:47:39', '0');
INSERT INTO `sys_menu` VALUES (1036, 101, NULL, '删除部门', 'dept_del', NULL, 1008, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-18 16:47:39', '0');
INSERT INTO `sys_menu` VALUES (1037, 101, NULL, '修改部门', 'dept_edit', NULL, 1008, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-18 16:47:39', '0');
INSERT INTO `sys_menu` VALUES (1038, 101, NULL, '查询部门', 'dept_query', NULL, 1008, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-18 16:47:39', '0');
INSERT INTO `sys_menu` VALUES (1039, 101, NULL, '增加员工', 'staff_add', NULL, 1009, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-18 16:47:34', '0');
INSERT INTO `sys_menu` VALUES (1040, 101, NULL, '删除员工', 'staff_del', NULL, 1009, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-18 16:47:34', '0');
INSERT INTO `sys_menu` VALUES (1041, 101, NULL, '修改员工', 'staff_edit', NULL, 1009, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-18 16:47:34', '0');
INSERT INTO `sys_menu` VALUES (1042, 101, NULL, '查询员工', 'staff_query', NULL, 1009, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-18 16:47:34', '0');
INSERT INTO `sys_menu` VALUES (1043, 101, NULL, '增加厂商', 'manuf_add', NULL, 1011, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1044, 101, NULL, '删除厂商', 'manuf_del', NULL, 1011, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1045, 101, NULL, '修改厂商', 'manuf_edit', NULL, 1011, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1046, 101, NULL, '查询厂商', 'manuf_query', NULL, 1011, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1047, 101, NULL, '增加应用', 'app_add', NULL, 1012, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1048, 101, NULL, '删除应用', 'app_del', NULL, 1012, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1049, 101, NULL, '修改应用', 'app_edit', NULL, 1012, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1050, 101, NULL, '查询应用', 'app_query', NULL, 1012, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1051, 101, NULL, '增加数源', 'ds_add', NULL, 1013, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1052, 101, NULL, '删除数源', 'ds_del', NULL, 1013, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1053, 101, NULL, '修改数源', 'ds_edit', NULL, 1013, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1054, 101, NULL, '查询数源', 'ds_query', NULL, 1013, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1055, 101, NULL, '增加字典条目', 'dict_item_add', NULL, 1004, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:10', NULL, '2025-06-18 16:47:22', '0');
INSERT INTO `sys_menu` VALUES (1056, 101, NULL, '删除字典条目', 'dict_item_del', NULL, 1004, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:11', NULL, '2025-06-18 16:47:19', '0');
INSERT INTO `sys_menu` VALUES (1057, 101, NULL, '修改字典条目', 'dict_item_edit', NULL, 1004, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:11', NULL, '2025-06-18 16:47:17', '0');
INSERT INTO `sys_menu` VALUES (1058, 101, NULL, '查询字典条目', 'dict_item_query', NULL, 1004, NULL, NULL, 1, '0', '1', 'USR', NULL, '2025-06-16 10:51:11', NULL, '2025-06-18 16:47:15', '0');
INSERT INTO `sys_menu` VALUES (1059, 101, NULL, '日志管理', '', '', 0, 'switcher-outlined', NULL, 700, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1061, 101, NULL, '日志查询', '', 'Log/Log', 1060, 'appstore-outlined', NULL, 12, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1063, 101, NULL, '智能体平台', '', '', 0, 'switcher-outlined', NULL, 600, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1064, 101, NULL, '机器人对话', '', 'Agent/chat', 1063, 'tag-outlined', NULL, 670, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1065, 101, NULL, 'AI应用管理', '', 'Aigc/App', 1063, 'appstore-outlined', NULL, 630, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1066, 101, NULL, '模型管理', '', 'Aigc/model', 1063, 'bank-outlined', NULL, 620, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1067, 101, NULL, '向量库管理', '', 'Aigc/EmbedStore', 1063, 'bank-outlined', NULL, 680, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1068, 101, NULL, '知识库管理', '', 'Aigc/knowledge', 1063, 'bank-outlined', NULL, 690, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1069, 101, NULL, '消息查询', '', 'Aigc/message', 1063, 'bank-outlined', NULL, 672, '0', '0', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1075, 101, NULL, '完成对话操作', 'chat:completions', NULL, 1064, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1076, 101, NULL, '清除聊天历史', 'chat:messages:clean', NULL, 1064, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1077, 101, NULL, '删除日志', 'log_del', NULL, 1061, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1078, 101, NULL, '查询日志', 'log_query', NULL, 1061, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1081, 101, NULL, '增加智能体应用', 'aigc:app:add', NULL, 1065, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:11', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1082, 101, NULL, '删除智能体应用', 'aigc:app:delete', NULL, 1065, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1083, 101, NULL, '修改智能体应用', 'aigc:app:update', NULL, 1065, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1084, 101, NULL, '查询智能体应用', 'aigc:app:query', NULL, 1065, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1085, 101, NULL, 'Chat权限', 'chat:completions', NULL, 1064, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1086, 101, NULL, '文本向量化', 'aigc:embedding:text', NULL, 1068, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1087, 101, NULL, '文档向量化', 'aigc:embedding:docs', NULL, 1068, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1088, 101, NULL, '增加大语言模型', 'aigc:model:add', NULL, 1066, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1089, 101, NULL, '删除大语言模型', 'aigc:model:delete', NULL, 1066, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1090, 101, NULL, '修改大语言模型', 'aigc:model:update', NULL, 1066, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1091, 101, NULL, '查询大语言模型', 'aigc:model:query', NULL, 1066, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1092, 101, NULL, '增加向量库', 'aigc:embed-store:add', NULL, 1067, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1093, 101, NULL, '删除向量库', 'aigc:embed-store:delete', NULL, 1067, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1094, 101, NULL, '修改向量库', 'aigc:embed-store:update', NULL, 1067, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1095, 101, NULL, '查询向量库', 'aigc:embed-store:query', NULL, 1067, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1096, 101, NULL, '增加知识库', 'aigc:knowledge:add', NULL, 1068, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1097, 101, NULL, '删除知识库', 'aigc:knowledge:delete', NULL, 1068, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1098, 101, NULL, '修改知识库', 'aigc:knowledge:update', NULL, 1068, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1099, 101, NULL, '查询知识库', 'aigc:knowledge:query', NULL, 1068, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1100, 101, NULL, '增加文档', 'aigc:docs:add', NULL, 1124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1101, 101, NULL, '删除文档', 'aigc:docs:delete', NULL, 1124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1102, 101, NULL, '修改文档', 'aigc:docs:update', NULL, 1124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1103, 101, NULL, '查询文档', 'aigc:docs:query', NULL, 1124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1104, 101, NULL, '删除对话数据', 'aigc:message:delete', NULL, 1069, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-18 16:46:56', '0');
INSERT INTO `sys_menu` VALUES (1105, 101, NULL, '查询对话数据', 'aigc:message:query', NULL, 1069, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-18 16:46:54', '0');
INSERT INTO `sys_menu` VALUES (1107, 101, NULL, '渠道管理', '', 'Aigc/AppApi', 1063, 'key-outlined', 'AIGC 应用 API 管理', 640, '0', '0', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-18 16:46:50', '0');
INSERT INTO `sys_menu` VALUES (1108, 101, NULL, '新增渠道', 'aigc:app-api:add', NULL, 1107, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1109, 101, NULL, '删除渠道', 'aigc:app-api:delete', NULL, 1107, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:12', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1110, 101, NULL, '修改渠道', 'aigc:app-api:update', NULL, 1107, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1111, 101, NULL, '查询渠道', 'aigc:app-api:query', NULL, 1107, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1112, 101, NULL, '提示语管理', '', 'Aigc/Prompt', 1063, 'edit-outlined', 'AIGC 应用提示词管理', 660, '0', '0', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-18 16:46:45', '0');
INSERT INTO `sys_menu` VALUES (1113, 101, NULL, '新增提示语', 'aigc:prompt:add', NULL, 1112, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1114, 101, NULL, '删除提示语', 'aigc:prompt:delete', NULL, 1112, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1115, 101, NULL, '修改提示语', 'aigc:prompt:update', NULL, 1112, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1116, 101, NULL, '查询提示语', 'aigc:prompt:query', NULL, 1112, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1117, 101, NULL, '对话管理', '', 'Aigc/Conversation', 1063, 'message-outlined', 'AIGC 对话管理', 671, '0', '0', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-18 16:46:40', '0');
INSERT INTO `sys_menu` VALUES (1118, 101, NULL, '新增对话', 'aigc:conversation:add', NULL, 1117, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1119, 101, NULL, '删除对话', 'aigc:conversation:delete', NULL, 1117, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1120, 101, NULL, '修改对话', 'aigc:conversation:update', NULL, 1117, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1121, 101, NULL, '查询对话', 'aigc:conversation:query', NULL, 1117, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1122, 101, NULL, '新增消息', 'aigc:message:add', NULL, 1069, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-18 16:46:37', '0');
INSERT INTO `sys_menu` VALUES (1123, 101, NULL, '修改消息', 'aigc:message:update', NULL, 1069, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-18 16:46:35', '0');
INSERT INTO `sys_menu` VALUES (1124, 101, NULL, '文档管理', '', 'Aigc/Doc', 1063, 'copy-outlined', 'AIGC 文档管理', 691, '0', '0', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-18 16:46:32', '0');
INSERT INTO `sys_menu` VALUES (1125, 101, NULL, '文档切片', '', 'Aigc/Slice', 1063, 'dash-outlined', 'AIGC 文档切片管理', 692, '0', '0', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-18 16:46:29', '0');
INSERT INTO `sys_menu` VALUES (1126, 101, NULL, '新增文档切片', 'aigc:docs:slice:add', NULL, 1125, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1127, 101, NULL, '删除文档切片', 'aigc:docs:slice:delete', NULL, 1125, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1128, 101, NULL, '修改文档切片', 'aigc:docs:slice:update', NULL, 1125, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1129, 101, NULL, '查询文档切片', 'aigc:docs:slice:query', NULL, 1125, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1130, 101, NULL, '新增文件', 'aigc:oss:add', NULL, 1124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1131, 101, NULL, '删除文件', 'aigc:oss:delete', NULL, 1124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1132, 101, NULL, '修改文件', 'aigc:oss:update', NULL, 1124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1133, 101, NULL, '查询文件', 'aigc:oss:query', NULL, 1124, NULL, NULL, 1, '0', '1', 'ERP', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1134, 101, NULL, '客户关系管理', '', '', 0, 'message-outlined', NULL, 100, '0', '0', 'CRM', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1135, 101, NULL, '客户管理', '', 'Relation/Customer', 1134, 'ie-outlined', '客户管理', 110, '0', '0', 'CRM', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1136, 101, NULL, '新增客户', 'crm:customer:add', NULL, 1135, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1137, 101, NULL, '删除客户', 'crm:customer:delete', NULL, 1135, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:13', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1138, 101, NULL, '修改客户', 'crm:customer:update', NULL, 1135, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1139, 101, NULL, '查询客户', 'crm:customer:query', NULL, 1135, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1140, 101, NULL, '联系人管理', '', 'Relation/Contact', 1134, 'ant-design-outlined', '联系人管理', 130, '0', '0', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1141, 101, NULL, '新增联系人', 'crm:contact:add', NULL, 1140, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1142, 101, NULL, '删除联系人', 'crm:contact:delete', NULL, 1140, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1143, 101, NULL, '修改联系人', 'crm:contact:update', NULL, 1140, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1144, 101, NULL, '查询联系人', 'crm:contact:query', NULL, 1140, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1145, 101, NULL, '联系人社交关系', '', 'Relation/SocialRelationship', 1134, 'yuque-outlined', '社会关系管理', 140, '0', '0', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1146, 101, NULL, '新增关系', 'crm:relation:add', NULL, 1145, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1147, 101, NULL, '删除关系', 'crm:relation:delete', NULL, 1145, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1148, 101, NULL, '修改关系', 'crm:relation:update', NULL, 1145, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1149, 101, NULL, '查询关系', 'crm:relation:query', NULL, 1145, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1150, 101, NULL, '跟踪记录', '', 'Relation/FollowRecord', 1134, 'radius-setting-outlined', '跟踪记录管理', 160, '0', '0', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1151, 101, NULL, '新增跟踪', 'crm:follow:add', NULL, 1150, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1152, 101, NULL, '删除跟踪', 'crm:follow:delete', NULL, 1150, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1153, 101, NULL, '修改跟踪', 'crm:follow:update', NULL, 1150, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1154, 101, NULL, '查询跟踪', 'crm:follow:query', NULL, 1150, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1155, 101, NULL, '社交账号', '', 'Relation/SocialDetails', 1134, 'message-outlined', '社交账号管理', 150, '0', '0', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1156, 101, NULL, '新增社交账号', 'crm:social:add', NULL, 1155, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1157, 101, NULL, '删除社交账号', 'crm:social:delete', NULL, 1155, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1158, 101, NULL, '修改社交账号', 'crm:social:update', NULL, 1155, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1159, 101, NULL, '查询社交账号', 'crm:social:query', NULL, 1155, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1160, 101, NULL, '客户关系', '', 'Relation/CustomerRelation', 1134, 'alibaba-outlined', '客户关系管理', 120, '0', '0', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1161, 101, NULL, '新增客户关系', 'crm:customer-relation:add', NULL, 1160, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1162, 101, NULL, '删除客户关系', 'crm:customer-relation:delete', NULL, 1160, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1163, 101, NULL, '修改客户关系', 'crm:customer-relation:update', NULL, 1160, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');
INSERT INTO `sys_menu` VALUES (1164, 101, NULL, '查询客户关系', 'crm:customer-relation:query', NULL, 1160, NULL, NULL, 1, '0', '1', 'CRM', NULL, '2025-06-16 10:51:14', NULL, '2025-06-17 11:20:51', '0');


DROP TABLE IF EXISTS `sys_oauth_client_details`;
CREATE TABLE `sys_oauth_client_details`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `client_id` varchar(32)  NOT NULL COMMENT 'Oauth2对应的clientId',
  `resource_ids` varchar(256)  DEFAULT NULL COMMENT '微服务id',
  `client_secret` varchar(256)  DEFAULT NULL COMMENT '指定客户端(client)的访问密匙',
  `scope` varchar(256)  DEFAULT NULL COMMENT '指定客户端申请的权限范围',
  `authorized_grant_types` varchar(256)  DEFAULT NULL COMMENT '指定客户端支持的grant_type',
  `web_server_redirect_uri` varchar(256)  DEFAULT NULL COMMENT '客户端的重定向URI,可为空',
  `authorities` varchar(256)  DEFAULT NULL COMMENT '指定客户端所拥有的Spring Security的权限值',
  `access_token_validity` int(11) DEFAULT NULL COMMENT '设定客户端的access_token的有效时间值',
  `refresh_token_validity` int(11) DEFAULT NULL COMMENT '设定客户端的refresh_token的有效时间值',
  `additional_information` varchar(4096)  DEFAULT NULL COMMENT 'JSON扩展：enc_flag加密标志，captcha_flag验证码标志',
  `autoapprove` varchar(256)  DEFAULT NULL COMMENT '是否开启验证码',
  `organ_code` varchar(64)  DEFAULT NULL COMMENT '所属机构编码',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '终端信息表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_oauth_client_details`(`tenant_id`, `client_id`,`client_secret`,`scope`,`authorized_grant_types`,`additional_information`,`autoapprove`,`organ_code`) VALUES (100, 'feng', 'Slxx@2025', 'server', 'password,refresh_token,authorization_code,client_credentials', '{ \"enc_flag\":\"1\",\"captcha_flag\":\"1\",\"online_quantity\":\"20\"}', 'true', 'default');
INSERT INTO `sys_oauth_client_details`(`tenant_id`, `client_id`,`client_secret`,`scope`,`authorized_grant_types`,`additional_information`,`autoapprove`,`organ_code`) VALUES (100, 'test', 'Slxx@2025', 'server', 'password,refresh_token','{ \"enc_flag\":\"0\",\"captcha_flag\":\"0\",\"online_quantity\":\"20\"}', 'true', 'default');
INSERT INTO `sys_oauth_client_details`(`tenant_id`, `client_id`,`client_secret`,`scope`,`authorized_grant_types`,`additional_information`,`autoapprove`,`organ_code`) VALUES (100, 'app', 'Msg@2025', 'server', 'client_credentials,refresh_token','{ \"enc_flag\":\"0\",\"captcha_flag\":\"0\",\"online_quantity\":\"20\"}', 'true', 'default');

INSERT INTO `sys_oauth_client_details`(`tenant_id`, `client_id`,`client_secret`,`scope`,`authorized_grant_types`,`additional_information`,`autoapprove`,`organ_code`) VALUES (101, 'feng', 'Slxx@2025', 'server', 'password,refresh_token,authorization_code,client_credentials', '{ \"enc_flag\":\"1\",\"captcha_flag\":\"1\",\"online_quantity\":\"20\"}', 'true', 'default');
INSERT INTO `sys_oauth_client_details`(`tenant_id`, `client_id`,`client_secret`,`scope`,`authorized_grant_types`,`additional_information`,`autoapprove`,`organ_code`) VALUES (101, 'test', 'Slxx@2025', 'server', 'password,refresh_token','{ \"enc_flag\":\"0\",\"captcha_flag\":\"0\",\"online_quantity\":\"20\"}', 'true', 'default');

DROP TABLE IF EXISTS `sys_organ`;
CREATE TABLE `sys_organ`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `organ_name` varchar(100)  DEFAULT NULL COMMENT '机构名称',
  `organ_code` varchar(64)  DEFAULT NULL COMMENT '机构编码',
  `organ_type` char(1)  NULL DEFAULT '0' COMMENT '机构类型编码，关联数据字典organ_type，例如政府、企业',
  `organ_alias_name` varchar(100)  DEFAULT NULL COMMENT '第二名称（简称）',
  `parent_id` int(11) NULL DEFAULT 0 COMMENT '上级机构',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `organ_category_code` varchar(100)  DEFAULT NULL COMMENT '行业编码，关联数据字典 industry，例如农业，工业',
  `organ_category_name` varchar(100)  DEFAULT NULL COMMENT '行业名称，关联数据字典 industry，例如农业，工业',
  `economic_type_code` varchar(100)  DEFAULT NULL COMMENT '经济类型编码，关联数据字典economic_type，例如国营，民营',
  `economic_type_name` varchar(100)  DEFAULT NULL COMMENT '经济类型名称，关联数据字典economic_type，例如国营，民营',
  `manage_class_code` varchar(100)  DEFAULT NULL COMMENT '管理类型代码，关联数据字典manage_class，例如公益、营利',
  `manage_class_name` varchar(100)  DEFAULT NULL COMMENT '管理类型名称，关联数据字典manage_class，例如公益、营利',
  `address` varchar(500)  DEFAULT NULL COMMENT '地址全路径',
  `addr_province` varchar(50)  DEFAULT NULL COMMENT '地址-省',
  `addr_city` varchar(50)  DEFAULT NULL COMMENT '地址-市',
  `addr_county` varchar(50)  DEFAULT NULL COMMENT '地址-县',
  `addr_town` varchar(50)  DEFAULT NULL COMMENT '地址-乡',
  `addr_village` varchar(50)  DEFAULT NULL COMMENT '地址-村',
  `addr_house_no` varchar(50)  DEFAULT NULL COMMENT '地址-门牌号',
  `administrative_division` varchar(100)  DEFAULT NULL COMMENT '行政区划',
  `zip_code` varchar(50)  DEFAULT NULL COMMENT '邮编',
  `telephone` varchar(20)  DEFAULT NULL COMMENT '电话',
  `email` varchar(50)  DEFAULT NULL COMMENT '邮箱',
  `website` varchar(100)  DEFAULT NULL COMMENT '网址',
  `establish_date` varchar(20)  DEFAULT NULL COMMENT '成立日期',
  `organ_introduction` text  NULL COMMENT '机构介绍',
  `traffic_route` text  NULL COMMENT '交通路线',
  `approval_authority` varchar(100)  DEFAULT NULL COMMENT '审批机关',
  `register_no` varchar(50)  DEFAULT NULL COMMENT '登记号',
  `legal_person` varchar(50)  DEFAULT NULL COMMENT '法人',
  `principal_name` varchar(50)  DEFAULT NULL COMMENT '主要负责人',
  `principal_telecom` varchar(50)  DEFAULT NULL COMMENT '负责人电话',
  `license_start_date` date DEFAULT NULL COMMENT '执业许可开始日期',
  `license_end_date` date DEFAULT NULL COMMENT '执业许可结束日期',
  `branching_quantity` int(11) DEFAULT NULL COMMENT '派出（分支）机构数量',
  `staff_quantity` int(11) DEFAULT NULL COMMENT '员工数',
  `daily_visits` int(11) DEFAULT NULL COMMENT '业务量',
  `product_quantity` int(11) DEFAULT NULL COMMENT '产品数',
  `status` char(1)  NULL DEFAULT '0' COMMENT '状态:0-启用 1-禁用',
  `organ_pictures` text  NULL COMMENT '机构图片',
  `license_pictures` text  NULL COMMENT '资质图片',
  `default_password` varchar(255)  DEFAULT NULL COMMENT '默认密码',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '机构表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_organ` VALUES (1, 100, NULL, '深圳市大疆创新科技有限公司', '914403007954257495', '1', 'DJ', 0, 10, NULL, NULL, NULL, NULL, NULL, NULL, '深圳市南山区西丽街道西丽社区仙元路53号大疆天空之城T2大堂', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2006-11-06', NULL, NULL, NULL, '440306102790880', '罗镇华', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_organ` VALUES (2, 100, NULL, '华为技术有限公司', '914403001922038216', '1', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, '深圳市龙岗区坂田华为总部办公楼', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1987-09-15', '程控交换机、传输设备、数据通信设备、宽带多媒体设备、电源、无线通信设备、微电子产品、软件、系统集成工程、计算机及配套设备、终端设备及相关通信信息产品、数据中心机房基础设施及配套产品（含供配电、空调制冷设备、智能管理监控等）的开发、生产、销售、技术服务、工程安装、维修、咨询、代理、租赁；信息系统设计、集成、运行维护；集成电路设计、研发；统一通信及协作类产品，服务器及配套软硬件产品，存储设备及相关软件的研发、生产、销售；', NULL, NULL, '440301103097413', '赵明路', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:26:28', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (3, 100, NULL, '中兴通讯股份有限公司', '9144030027939873X7', '1', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, '深圳市南山区高新技术产业园科技南路中兴通讯大厦', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1997-11-11', '生产程控交换系统、多媒体通讯系统、通讯传输系统；研制、生产移动通信系统设备、卫星通讯、微波通讯设备、寻呼机，计算机软硬件、闭路电视、微波通信、信号自动控制、计算机信息处理、过程监控系统、防灾报警系统、新能源发电及应用系统等项目的技术设计、开发、咨询、服务；铁路、地下铁路、城市轨道交通、公路、厂矿、港口码头、机场的有线无线通信等项目的技术设计、开发、咨询、服务（不含限制项目）；通信电源及配电系统的研发、生产、销售、技术服务、工程安装、维护；数据中心基础设施及配套产品（含供配电、空调制冷设备、冷通道、智能化管理系统等）的研发、生产、销售、技术服务、工程安装、维护；', NULL, NULL, '440301103852869', '徐子阳', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:26:57', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (4, 100, NULL, '中国电信集团有限公司', '91110000100017707H', '1', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, '北京市西城区金融大街31号', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1995-04-27', NULL, NULL, NULL, '100000000017708', '柯瑞文', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:37:23', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (5, 100, NULL, '中国电子科技集团有限公司', '91110000710929498G', '1', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, '北京市海淀区万寿路27号', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2002-02-25', '承担军事电子装备与系统集成、武器平台电子装备、军用软件和电子基础产品的研制、生产；国防电子信息基础设施与保障条件的建设；承担国家重大电子信息系统工程建设；', NULL, NULL, '100000000036399', '王海波', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:40:14', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (6, 100, NULL, '重庆市科学技术局', 'CQSKXJSJ', '2', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, '渝北区新溉大道2号生产力大厦', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2018-10-25', NULL, NULL, NULL, NULL, NULL, '明炬', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:45:50', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (7, 100, NULL, '重庆狩猎信息技术有限公司', '91500108075658333Q', '1', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, '重庆市南岸区弹子石街道弹子石新街57号5栋27-1号', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2013-07-31', '云计算装备技术服务；信息技术咨询服务；工程和技术研究和试验发展；', NULL, '重庆市南岸区市场监督管理局', '500108000213615', '冯永华', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:49:21', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (8, 100, NULL, '风云平台内置机构', 'default', '0', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:50:23', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (9, 100, NULL, '大唐电信科技股份有限公司', '91110000633709976B', '0', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:50:23', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (10,100, NULL, '杭州宇树科技股份有限公司', '91330108MA27YJ5H56', '0', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:50:23', NULL, NULL, '0');

INSERT INTO `sys_organ` VALUES (11, 101, NULL, '深圳市大疆创新科技有限公司', '914403007954257495', '1', 'DJ', 0, 10, NULL, NULL, NULL, NULL, NULL, NULL, '深圳市南山区西丽街道西丽社区仙元路53号大疆天空之城T2大堂', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2006-11-06', NULL, NULL, NULL, '440306102790880', '罗镇华', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_organ` VALUES (12, 101, NULL, '华为技术有限公司', '914403001922038216', '1', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, '深圳市龙岗区坂田华为总部办公楼', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1987-09-15', '程控交换机、传输设备、数据通信设备、宽带多媒体设备、电源、无线通信设备、微电子产品、软件、系统集成工程、计算机及配套设备、终端设备及相关通信信息产品、数据中心机房基础设施及配套产品（含供配电、空调制冷设备、智能管理监控等）的开发、生产、销售、技术服务、工程安装、维修、咨询、代理、租赁；信息系统设计、集成、运行维护；集成电路设计、研发；统一通信及协作类产品，服务器及配套软硬件产品，存储设备及相关软件的研发、生产、销售；', NULL, NULL, '440301103097413', '赵明路', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:26:28', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (13, 101, NULL, '中兴通讯股份有限公司', '9144030027939873X7', '1', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, '深圳市南山区高新技术产业园科技南路中兴通讯大厦', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1997-11-11', '生产程控交换系统、多媒体通讯系统、通讯传输系统；研制、生产移动通信系统设备、卫星通讯、微波通讯设备、寻呼机，计算机软硬件、闭路电视、微波通信、信号自动控制、计算机信息处理、过程监控系统、防灾报警系统、新能源发电及应用系统等项目的技术设计、开发、咨询、服务；铁路、地下铁路、城市轨道交通、公路、厂矿、港口码头、机场的有线无线通信等项目的技术设计、开发、咨询、服务（不含限制项目）；通信电源及配电系统的研发、生产、销售、技术服务、工程安装、维护；数据中心基础设施及配套产品（含供配电、空调制冷设备、冷通道、智能化管理系统等）的研发、生产、销售、技术服务、工程安装、维护；', NULL, NULL, '440301103852869', '徐子阳', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:26:57', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (14, 101, NULL, '中国电信集团有限公司', '91110000100017707H', '1', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, '北京市西城区金融大街31号', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1995-04-27', NULL, NULL, NULL, '100000000017708', '柯瑞文', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:37:23', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (15, 101, NULL, '中国电子科技集团有限公司', '91110000710929498G', '1', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, '北京市海淀区万寿路27号', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2002-02-25', '承担军事电子装备与系统集成、武器平台电子装备、军用软件和电子基础产品的研制、生产；国防电子信息基础设施与保障条件的建设；承担国家重大电子信息系统工程建设；', NULL, NULL, '100000000036399', '王海波', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:40:14', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (16, 101, NULL, '重庆市科学技术局', 'CQSKXJSJ', '2', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, '渝北区新溉大道2号生产力大厦', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2018-10-25', NULL, NULL, NULL, NULL, NULL, '明炬', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:45:50', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (17, 101, NULL, '重庆狩猎信息技术有限公司', '91500108075658333Q', '1', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, '重庆市南岸区弹子石街道弹子石新街57号5栋27-1号', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2013-07-31', '云计算装备技术服务；信息技术咨询服务；工程和技术研究和试验发展；', NULL, '重庆市南岸区市场监督管理局', '500108000213615', '冯永华', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:49:21', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (18, 101, NULL, '风云平台内置机构', 'default', '0', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:50:23', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (19, 101, NULL, '大唐电信科技股份有限公司', '91110000633709976B', '0', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:50:23', NULL, NULL, '0');
INSERT INTO `sys_organ` VALUES (20, 101, NULL, '杭州宇树科技股份有限公司', '91330108MA27YJ5H56', '0', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '123456', NULL, '2025-06-04 16:50:23', NULL, NULL, '0');


DROP TABLE IF EXISTS `sys_public_param`;
CREATE TABLE `sys_public_param`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `name` varchar(128)  DEFAULT NULL COMMENT '名称',
  `key` varchar(128)  DEFAULT NULL COMMENT '键',
  `value` varchar(128)  DEFAULT NULL COMMENT '值',
  `status` char(1)  NULL DEFAULT '0' COMMENT '状态:0-启用 1-禁用',
  `validate_code` varchar(64)  DEFAULT NULL COMMENT '校验码',
  `type` char(1)  NULL DEFAULT '0' COMMENT '类型',
  `system` char(1)  NULL DEFAULT '0' COMMENT '是否系统内置，1是，0否',
  `organ_code` varchar(64)  DEFAULT NULL COMMENT '所属机构编码',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '公共参数配置表' ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Id',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `role_name` varchar(64)  DEFAULT NULL COMMENT '角色名称',
  `role_code` varchar(64)  DEFAULT NULL COMMENT '角色编码',
  `role_desc` varchar(255)  DEFAULT NULL COMMENT '角色描述',
  `ds_type` int(11) NULL DEFAULT 0 COMMENT '数据权限类型：0-全部 1-自定义',
  `ds_scope` varchar(255)  DEFAULT NULL COMMENT '数据权限作用范围：部门id逗号隔开',
  `type` char(1)  NULL DEFAULT '1' COMMENT '角色类型:0-系统角色 1-自定义角色 2-项目角色',
  `organ_code` varchar(64)  DEFAULT NULL COMMENT '所属机构编码',
  `role_start_time` datetime DEFAULT NULL COMMENT '角色有效开始时间',
  `role_end_time` datetime DEFAULT NULL COMMENT '角色有效结束时间',
  `is_default` char(1)  NULL DEFAULT '0' COMMENT '是否内置：0-否 1-是',
  `create_by` varchar(255)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) ,
  INDEX `idx_role_code`(`role_code`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '系统角色表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_role` VALUES (1, 100, NULL, '超级管理员', 'admin', '超级管理员', 0, NULL, '0', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (2, 100, NULL, '普通职员', 'default', '普通职员', 0, NULL, '0', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (3, 100, NULL, '组长', 'leader', '项目领导', 0, NULL, '0', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (4, 100, NULL, '成员', 'member', '项目成员', 0, NULL, '0', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (5, 100, NULL, '助手', 'assistant', 'AI助手', 0, NULL, '0', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (6, 100, NULL, '系统', 'system', 'AI系统', 0, NULL, '0', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (7, 100, NULL, '用户', 'user', 'AI用户', 0, NULL, '1', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (8, 100, NULL, '工人', 'worker', '产线', 0, NULL, '1', 'default', NULL, NULL, '0', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (9, 100, NULL, '销售员', 'sale_role', '销售', 0, NULL, '1', 'default', NULL, NULL, '0', 'admin', '2025-03-24 22:33:15', 'admin', '2025-03-24 22:33:15', '0');

INSERT INTO `sys_role` VALUES (11, 101, NULL, '超级管理员', 'admin', '超级管理员', 0, NULL, '0', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (12, 101, NULL, '普通职员', 'default', '普通职员', 0, NULL, '0', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (13, 101, NULL, '组长', 'leader', '项目领导', 0, NULL, '0', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (14, 101, NULL, '成员', 'member', '项目成员', 0, NULL, '0', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (15, 101, NULL, '助手', 'assistant', 'AI助手', 0, NULL, '0', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (16, 101, NULL, '系统', 'system', 'AI系统', 0, NULL, '0', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (17, 101, NULL, '用户', 'user', 'AI用户', 0, NULL, '1', 'default', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (18, 101, NULL, '工人', 'worker', '产线', 0, NULL, '1', 'default', NULL, NULL, '0', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_role` VALUES (19, 101, NULL, '销售员', 'sale_role', '销售', 0, NULL, '1', 'default', NULL, NULL, '0', 'admin', '2025-03-24 22:33:15', 'admin', '2025-03-24 22:33:15', '0');

DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `menu_id` int(11) NOT NULL COMMENT '菜单ID',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  UNIQUE KEY `uk_role_menue_id` (`tenant_id`, `role_id`, `menu_id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '角色菜单表' ROW_FORMAT = DYNAMIC;
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 1);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 2);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 3);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 4);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 5);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 6);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 7);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 8);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 9);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 10);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 11);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 12);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 13);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 14);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 15);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 16);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 17);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 18);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 19);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 20);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 21);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 22);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 23);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 24);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 25);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 26);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 27);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 28);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 29);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 30);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 31);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 32);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 33);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 34);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 35);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 36);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 37);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 38);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 39);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 40);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 41);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 42);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 43);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 44);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 45);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 46);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 47);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 48);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 49);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 50);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 51);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 52);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 53);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 54);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 55);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 56);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 57);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 58);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 59);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 60);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 61);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 62);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 63);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 64);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 65);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 66);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 67);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 68);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 69);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 70);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 71);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 72);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 73);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 74);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 75);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 76);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 77);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 78);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 79);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 80);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 81);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 82);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 83);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 84);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 85);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 86);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 87);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 88);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 89);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 90);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 91);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 92);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 93);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 94);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 95);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 96);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 97);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 98);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 99);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 100);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 101);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 102);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 103);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 104);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 105);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 106);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 107);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 108);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 109);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 110);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 111);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 112);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 113);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 114);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 115);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 116);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 117);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 118);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 119);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 120);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 121);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 122);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 123);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 124);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 125);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 126);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 127);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 128);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 129);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 130);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 131);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 132);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 133);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 134);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 135);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 136);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 137);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 138);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 139);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 140);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 141);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 142);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 143);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 144);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 145);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 146);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 147);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 148);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 149);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 150);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 151);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 152);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 153);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 154);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 155);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 156);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 157);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 158);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 159);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 160);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 161);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 162);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 163);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 164);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 165);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 166);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 167);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 168);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 169);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 170);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 171);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 172);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 173);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 174);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 175);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 176);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 177);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 178);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 179);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 180);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 181);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 182);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 183);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 184);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 185);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 186);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 187);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 188);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 189);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 190);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 191);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 192);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 193);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 194);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 195);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 196);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 197);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 198);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 199);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 200);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 201);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 202);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 203);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 1, 204);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1001);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1002);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1003);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1004);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1005);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1006);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1007);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1008);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1009);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1010);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1011);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1012);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1013);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1014);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1015);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1016);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1017);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1018);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1019);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1020);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1021);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1022);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1023);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1024);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1025);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1026);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1027);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1028);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1029);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1030);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1031);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1032);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1033);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1034);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1035);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1036);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1037);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1038);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1039);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1040);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1041);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1042);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1043);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1044);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1045);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1046);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1047);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1048);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1049);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1050);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1051);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1052);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1053);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1054);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1055);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1056);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1057);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1058);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1059);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1060);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1061);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1062);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1063);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1064);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1065);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1066);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1067);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1068);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1069);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1070);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1071);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1072);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1073);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1074);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1075);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1076);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1077);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1078);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1079);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1080);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1081);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1082);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1083);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1084);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1085);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1086);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1087);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1088);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1089);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1090);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1091);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1092);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1093);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1094);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1095);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1096);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1097);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1098);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1099);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1100);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1101);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1102);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1103);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1104);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1105);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1106);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1107);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1108);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1109);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1110);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1111);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1112);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1113);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1114);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1115);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1116);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1117);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1118);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1119);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1120);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1121);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1122);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1123);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1124);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1125);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1126);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1127);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1128);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1129);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1130);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1131);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1132);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1133);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1134);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1135);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1136);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1137);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1138);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1139);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1140);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1141);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1142);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1143);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1144);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1145);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1146);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1147);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1148);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1149);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1150);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1151);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1152);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1153);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1154);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1155);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1156);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1157);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1158);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1159);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1160);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1161);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1162);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1163);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1164);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1165);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1166);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1167);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1168);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1169);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1170);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1171);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1172);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1173);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1174);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1175);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1176);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1177);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1178);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1179);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1180);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1181);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1182);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1183);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1184);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1185);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1186);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1187);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1188);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1189);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1190);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1191);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1192);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1193);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1194);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1195);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1196);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1197);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1198);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1199);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1200);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1201);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1202);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1203);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 11, 1204);

INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 1);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 2);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 3);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 4);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 5);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 7);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 8);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 9);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 10);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 11);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 12);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 13);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 14);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 15);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 16);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 17);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 18);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 19);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 20);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 21);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 22);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 23);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 24);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 25);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 26);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 27);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 28);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 29);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 30);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 31);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 35);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 36);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 37);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 38);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 39);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 40);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 41);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 42);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 43);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 44);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 45);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 46);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 47);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 48);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 49);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 50);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 51);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 52);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 53);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 54);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 55);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 56);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 57);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 58);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 59);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 60);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 61);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 62);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 63);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 64);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 65);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 66);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 67);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 68);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 69);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 70);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 71);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 72);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 73);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 74);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 75);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 76);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 77);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 78);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 79);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 80);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 81);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 82);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 83);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 84);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 85);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 86);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 87);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 88);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 89);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 90);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 91);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 92);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 93);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 94);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 95);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 96);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 97);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 98);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 99);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 100);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 101);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 102);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 103);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 104);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 105);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 106);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 107);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 108);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 109);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 110);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 111);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 112);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 113);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 114);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 115);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 116);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 117);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 118);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 119);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 120);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 121);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 122);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 123);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 124);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 125);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 126);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 127);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 128);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 129);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 130);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 131);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 132);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 133);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 134);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 135);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 136);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 137);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 138);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 139);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 140);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 141);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 142);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 143);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 144);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 145);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 146);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 147);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 148);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 149);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 150);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 151);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 152);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 153);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 154);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 155);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 156);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 157);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 158);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 159);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 160);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 161);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 162);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 163);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 164);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 165);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 166);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 167);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 168);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 169);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 170);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 171);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 172);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 173);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 174);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 175);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 176);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 177);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 178);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 179);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 180);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 181);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 182);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 183);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 184);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 185);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 186);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 187);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 188);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 189);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 190);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 191);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 192);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 193);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 194);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 195);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 196);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 197);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 198);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 199);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 200);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 201);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 202);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 203);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (100, 3, 204);

INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1001);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1002);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1003);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1004);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1005);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1007);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1008);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1009);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1010);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1011);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1012);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1013);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1014);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1015);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1016);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1017);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1018);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1019);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1020);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1021);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1022);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1023);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1024);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1025);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1026);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1027);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1028);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1029);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1030);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1031);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1035);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1036);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1037);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1038);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1039);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1040);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1041);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1042);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1043);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1044);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1045);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1046);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1047);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1048);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1049);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1050);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1051);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1052);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1053);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1054);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1055);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1056);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1057);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1058);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1059);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1060);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1061);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1062);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1063);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1064);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1065);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1066);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1067);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1068);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1069);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1070);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1071);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1072);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1073);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1074);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1075);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1076);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1077);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1078);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1079);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1080);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1081);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1082);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1083);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1084);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1085);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1086);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1087);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1088);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1089);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1090);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1091);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1092);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1093);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1094);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1095);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1096);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1097);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1098);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1099);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1100);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1101);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1102);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1103);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1104);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1105);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1106);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1107);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1108);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1109);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1110);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1111);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1112);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1113);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1114);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1115);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1116);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1117);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1118);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1119);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1120);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1121);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1122);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1123);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1124);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1125);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1126);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1127);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1128);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1129);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1130);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1131);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1132);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1133);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1134);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1135);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1136);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1137);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1138);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1139);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1140);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1141);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1142);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1143);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1144);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1145);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1146);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1147);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1148);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1149);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1150);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1151);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1152);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1153);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1154);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1155);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1156);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1157);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1158);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1159);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1160);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1161);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1162);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1163);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1164);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1165);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1166);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1167);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1168);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1169);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1170);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1171);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1172);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1173);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1174);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1175);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1176);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1177);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1178);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1179);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1180);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1181);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1182);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1183);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1184);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1185);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1186);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1187);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1188);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1189);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1190);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1191);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1192);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1193);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1194);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1195);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1196);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1197);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1198);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1199);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1200);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1201);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1202);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1203);
INSERT INTO `sys_role_menu`(`tenant_id`, `role_id`, `menu_id`) VALUES (101, 13, 1204);

-- 员工和用户

DROP TABLE IF EXISTS `sys_staff`;
CREATE TABLE `sys_staff`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `organ_code` varchar(64)  DEFAULT NULL COMMENT '所属机构编码',
  `dept_id` int(11) DEFAULT NULL COMMENT '所属部门id',
  `dept_code` varchar(128)  DEFAULT NULL COMMENT '所属部门编码，和dept_id对应',
  `staff_no` varchar(200)  DEFAULT NULL COMMENT '工号',
  `staff_name` varchar(200)  DEFAULT NULL COMMENT '姓名',
  `nationality_code` varchar(200)  DEFAULT NULL COMMENT '国籍编码 (GB/T 2659)',
  `nationality_name` varchar(200)  DEFAULT NULL COMMENT '国籍名称 (GB/T 2659)',
  `nation_code` varchar(200)  DEFAULT NULL COMMENT '民族编码 (GB/T 3304)',
  `nation_name` varchar(200)  DEFAULT NULL COMMENT '民族名称 (GB/T 3304)',
  `identification_no` varchar(200)  DEFAULT NULL COMMENT '身份证号',
  `gender_code` varchar(20)  DEFAULT NULL COMMENT '性别代码，和数据字典sex一致',
  `birthdate` date DEFAULT NULL COMMENT '出生日期',
  `telephone` varchar(200)  DEFAULT NULL COMMENT '电话',
  `marital_status_code` varchar(200)  DEFAULT NULL COMMENT '婚姻状况代码',
  `marital_status_name` varchar(200)  DEFAULT NULL COMMENT '婚姻状况名称',
  `native_place` varchar(200)  DEFAULT NULL COMMENT '籍贯',
  `politics_status_code` varchar(200)  DEFAULT NULL COMMENT '政治面貌代码',
  `politics_status_name` varchar(200)  DEFAULT NULL COMMENT '政治面貌名称',
  `addr_province` varchar(255)  DEFAULT NULL COMMENT '地址-省',
  `addr_city` varchar(255)  DEFAULT NULL COMMENT '地址-市',
  `addr_county` varchar(255)  DEFAULT NULL COMMENT '地址-县',
  `addr_town` varchar(255)  DEFAULT NULL COMMENT '地址-乡',
  `addr_village` varchar(255)  DEFAULT NULL COMMENT '地址-村',
  `addr_house_no` int(11) DEFAULT NULL COMMENT '地址-门牌号',
  `address` varchar(500)  DEFAULT NULL COMMENT '详细地址',
  `zip_code` varchar(255)  DEFAULT NULL COMMENT '邮编',
  `education_level_code` varchar(255)  DEFAULT NULL COMMENT '学历代码 (GB/T 4658)',
  `education_level_name` varchar(255)  DEFAULT NULL COMMENT '学历名称(GB/T 4658)',
  `degree_code` varchar(255)  DEFAULT NULL COMMENT '学位代码 (GB/T 6864)',
  `degree_name` varchar(255)  DEFAULT NULL COMMENT '学位名称(GB/T 6864)',
  `subject_code` varchar(255)  DEFAULT NULL COMMENT '专业代码 (GB/T 16835)',
  `subject_name` varchar(255)  DEFAULT NULL COMMENT '专业名称(GB/T 16835)',
  `graduate_school_name` varchar(255)  DEFAULT NULL COMMENT '毕业院校',
  `work_begin_date` date DEFAULT NULL COMMENT '参加工作日期',
  `job_category` varchar(255)  DEFAULT NULL COMMENT '岗位类别',
  `technical_qualifications_code` varchar(255)  DEFAULT NULL COMMENT '专业技术职务代码 (GB/T 8561)',
  `technical_qualifications_name` varchar(255)  DEFAULT NULL COMMENT '专业技术职务名称 (GB/T 8561)',
  `management_position_code` varchar(255)  DEFAULT NULL COMMENT '行政/业务管理职务代码 (GB/T 12403)',
  `management_position_name` varchar(255)  DEFAULT NULL COMMENT '行政/业务管理职务名称 (GB/T 12403)',
  `title_code` varchar(255)  DEFAULT NULL COMMENT '职称代码',
  `title_name` varchar(255)  DEFAULT NULL COMMENT '职称名称',
  `is_organizational` varchar(255)  DEFAULT NULL COMMENT '是否编制人员',
  `position` varchar(255)  DEFAULT NULL COMMENT '职务编码，关联数据字典position',
  `active_status_code` varchar(255)  DEFAULT NULL COMMENT '在岗状态代码',
  `active_status_name` varchar(255)  DEFAULT NULL COMMENT '在岗状态名称',
  `qualification_certificate_no` varchar(100)  DEFAULT NULL COMMENT '资格证书编号',
  `practising_certificate_no` varchar(100)  DEFAULT NULL COMMENT '执业证书编号',
  `expertise_field` varchar(255)  DEFAULT NULL COMMENT '擅长领域',
  `detailed_introduction` varchar(255)  DEFAULT NULL COMMENT '详细介绍',
  `is_general_staff` varchar(255)  DEFAULT NULL COMMENT '是否普通职员',
  `photograph` varchar(255)  DEFAULT NULL COMMENT '照片地址',
  `electronic_signature` varchar(255)  DEFAULT NULL COMMENT '电子签名',
  `qualification_certificate_pictures` varchar(255)  DEFAULT NULL COMMENT '资格证书图片地址',
  `practising_certificate_pictures` varchar(255)  DEFAULT NULL COMMENT '执业证书图片地址',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '员工表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_staff`(`id`, `tenant_id`, `organ_code`, `dept_id`, `dept_code`, `staff_no`, `staff_name`, `identification_no`, `gender_code`, `birthdate`, `telephone`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (1, 100, 'default', 1, '01', '1001', 'Amy', '922201197809031346', '2', '1978.09.03', '+86-10-2345-6789', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_staff`(`id`, `tenant_id`, `organ_code`, `dept_id`, `dept_code`, `staff_no`, `staff_name`, `identification_no`, `gender_code`, `birthdate`, `telephone`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (2, 100, 'default', 2, '02', '1002', 'Tom', '91213319711212421X', '1', '1971.11.21', '+86-10-2345-6789', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_staff`(`id`, `tenant_id`, `organ_code`, `dept_id`, `dept_code`, `staff_no`, `staff_name`, `identification_no`, `gender_code`, `birthdate`, `telephone`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (3, 100, 'default', 3, '03', '1003', 'Edison', '950111199502052915', '2', '1995.02.05', '+86-10-2345-6789', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_staff`(`id`, `tenant_id`, `organ_code`, `dept_id`, `dept_code`, `staff_no`, `staff_name`, `identification_no`, `gender_code`, `birthdate`, `telephone`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (4, 100, 'default', 2, '02', '1004', 'IT经理', '910106200309307226', '3', '2003.09.30', '+86-10-2345-6789', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_staff`(`id`, `tenant_id`, `organ_code`, `dept_id`, `dept_code`, `staff_no`, `staff_name`, `identification_no`, `gender_code`, `birthdate`, `telephone`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (5, 101, 'default', 7, '02', '1005', 'Tony', '910106200309307226', '3', '2003.09.30', '+86-10-2345-6789', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO `sys_staff`(`id`, `tenant_id`, `organ_code`, `dept_id`, `dept_code`, `staff_no`, `staff_name`, `identification_no`, `gender_code`, `birthdate`, `telephone`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (6, 101, 'default', 7, '02', '1006', '冯永华', '910106200309307226', '3', '2003.09.30', '+86-10-2345-6789', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');

DROP TABLE IF EXISTS `sys_staff_dept`;
CREATE TABLE `sys_staff_dept`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `staff_id` int(11) DEFAULT NULL COMMENT '人员id',
  `department_id` int(11) DEFAULT NULL COMMENT '部门id',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  UNIQUE KEY `uk_staff_department_id` (`tenant_id`, `staff_id`, `department_id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '人员部门关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
INSERT INTO `sys_staff_dept`(`staff_id`, `department_id`) VALUES (1, 1);
INSERT INTO `sys_staff_dept`(`staff_id`, `department_id`) VALUES (2, 1);
INSERT INTO `sys_staff_dept`(`staff_id`, `department_id`) VALUES (3, 1);

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表，备用',
  `nick_name` varchar(64)  DEFAULT NULL COMMENT '昵称',
  `username` varchar(64)  DEFAULT NULL COMMENT '用户名',
  `password` varchar(255)  DEFAULT NULL COMMENT '密码',
  `sex_code` char(20)  NULL DEFAULT '1' COMMENT '性别编码，和数据字典sex一致',
  `salt` varchar(255)  DEFAULT NULL COMMENT '随机盐',
  `phone` varchar(20)  DEFAULT NULL COMMENT '手机号',
  `email` varchar(20)  DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255)  DEFAULT NULL COMMENT '头像',
  `staff_id` int(11) DEFAULT NULL COMMENT '人员ID',
  `dept_id` int(11) NULL DEFAULT 0 COMMENT '部门ID',
  `organ_code` varchar(64)  DEFAULT NULL COMMENT '所属机构编码',
  `switch_code` varchar(64)  DEFAULT NULL COMMENT '备用',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间，定时任务条件查询修改过期状态',
  `expired_flag` char(1)  NULL DEFAULT '0' COMMENT '是否过期（0未过期 1过期）',
  `status` char(1)  NULL DEFAULT '0' COMMENT '状态:1-启用 0-禁用',
  `lock_flag` char(1)  NULL DEFAULT '1' COMMENT '账号锁定：0-锁定 1-正常',
  `first_login` char(1)  NULL DEFAULT '1' COMMENT '是否首次登录：0-否/1-是 默认1',
  `wx_openid` varchar(32)  DEFAULT NULL COMMENT '微信登录openId',
  `mini_openid` varchar(32)  DEFAULT NULL COMMENT '小程序openId',
  `qq_openid` varchar(32)  DEFAULT NULL COMMENT 'QQ openId',
  `gitee_login` varchar(100)  DEFAULT NULL COMMENT '码云标识',
  `osc_id` varchar(100)  DEFAULT NULL COMMENT '开源中国标识',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) ,
  INDEX `idx_username`(`username`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_user` VALUES (1, 100, NULL, '艾米', 'amy', '$2a$10$IVurf0aelX1HwuXtn.a1i.eFdIraSmBqJ4wJJpThMrKDTQQHJP3XK', '1', NULL, '13111111111', 'amy@gmail.com', 'assitant.png', 1, 1, 'default', '', NULL, 0, 1, 1, '0', NULL, NULL, NULL, NULL, NULL, 'admin', '2025-03-24 22:33:22', 'admin', '2025-03-24 22:33:22', '0');
INSERT INTO `sys_user` VALUES (2, 100, NULL, '汤米', 'tom', '$2a$10$C7PUtxOkNjuXhoDPdNZQYOF6ygq1/02YMwsMV/IfqpeJ2QoQxXqTG', '1', NULL, '13111111111', 'tom@gmail.com', 'assitant.png', 2, 2, 'default', '', NULL, 0, 1, 1, '0', NULL, NULL, NULL, NULL, NULL, 'admin', '2025-03-24 22:33:23', 'admin', '2025-03-24 22:33:22', '0');
INSERT INTO `sys_user` VALUES (3, 100, NULL, '爱迪生', 'edison', '$2a$10$oXFppqYngsw8PgJZDI0fr.Uy4.npNgk7WbGfeo9mQELDA2Y/60Fui', '1', NULL, '13111111111', 'edison@gmail.com', 'assitant.png', 3, 3, 'default', '', NULL, 0, 1, 1, '0', NULL, NULL, NULL, NULL, NULL, 'admin', '2025-03-24 22:33:24', 'admin', '2025-03-24 22:33:22', '0');
INSERT INTO `sys_user` VALUES (4, 101, NULL, '管理员', 'admin', '$2a$10$oXFppqYngsw8PgJZDI0fr.Uy4.npNgk7WbGfeo9mQELDA2Y/60Fui', '1', NULL, '13144444444', 'admin@gmail.com', 'assitant.png', 4, 2, 'default', '', NULL, 0, 1, 1, '0', NULL, NULL, NULL, NULL, NULL, 'admin', '2025-03-24 22:33:25', 'admin', '2025-03-24 22:33:25', '0');
INSERT INTO `sys_user` VALUES (5, 101, NULL, '托尼', 'tony', '$2a$10$IVurf0aelX1HwuXtn.a1i.eFdIraSmBqJ4wJJpThMrKDTQQHJP3XK', '1', NULL, '13111111111', 'amy@gmail.com', 'assitant.png', 5, 7, 'default', '', NULL, 0, 1, 1, '0', NULL, NULL, NULL, NULL, NULL, 'admin', '2025-03-24 22:33:22', 'admin', '2025-03-24 22:33:22', '0');
INSERT INTO `sys_user` VALUES (6, 101, NULL, '风雨', 'fengyh', '$2a$10$IVurf0aelX1HwuXtn.a1i.eFdIraSmBqJ4wJJpThMrKDTQQHJP3XK', '1', NULL, '13111111111', 'amy@gmail.com', 'assitant.png', 6, 7, 'default', '', NULL, 0, 1, 1, '0', NULL, NULL, NULL, NULL, NULL, 'admin', '2025-03-24 22:33:22', 'admin', '2025-03-24 22:33:22', '0');

DROP TABLE IF EXISTS `sys_user_department`;
CREATE TABLE `sys_user_department`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `dept_id` int(11) NOT NULL COMMENT '部门ID',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  UNIQUE KEY `uk_user_dept_id` (`tenant_id`, `user_id`, `dept_id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '用户部门表,用于一个用户有多个部门的情况' ROW_FORMAT = DYNAMIC;


DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
  `tenant_id` int(11) NOT NULL DEFAULT 100 COMMENT '租户ID，用于所有非全局表',
  `tenant_user_id` int(11) DEFAULT NULL COMMENT '租户内部用户ID，用于用户敏感表',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1)  NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  UNIQUE KEY `uk_user_role_id` (`tenant_id`, `user_id`, `role_id`) 
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '用户角色表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_user_role`(`tenant_id`, `user_id`, `role_id`) VALUES (100, 1, 1);
INSERT INTO `sys_user_role`(`tenant_id`, `user_id`, `role_id`) VALUES (100, 2, 2);
INSERT INTO `sys_user_role`(`tenant_id`, `user_id`, `role_id`) VALUES (100, 3, 3);
INSERT INTO `sys_user_role`(`tenant_id`, `user_id`, `role_id`) VALUES (101, 4, 11);
INSERT INTO `sys_user_role`(`tenant_id`, `user_id`, `role_id`) VALUES (101, 5, 13);
INSERT INTO `sys_user_role`(`tenant_id`, `user_id`, `role_id`) VALUES (101, 6, 14);


-- 民族代码表
DROP TABLE IF EXISTS `dict_ethnic_group`;
CREATE TABLE `dict_ethnic_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id自增',
  `name` varchar(256) NOT NULL COMMENT '民族名称',
  `code` char(2) NOT NULL COMMENT '民族代码',
  `remark` varchar(1024) DEFAULT '-' COMMENT '备注说明',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_ethnic_group_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='民族代码表(GB/T 3304-1991)';

INSERT INTO `dict_ethnic_group` VALUES (1, '1 - 汉族	', '1', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (2, '2 - 蒙古族	', '2', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (3, '3 - 回族	', '3', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (4, '4 - 藏族	', '4', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (5, '5 - 维吾尔族	', '5', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (6, '6 - 苗族	', '6', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (7, '7 - 彝族	', '7', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (8, '8 - 壮族	', '8', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (9, '9 - 布依族	', '9', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (10, '10 - 朝鲜族	', '10', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (11, '11 - 满族	', '11', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (12, '12 - 侗族	', '12', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (13, '13 - 瑶族	', '13', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (14, '14 - 白族	', '14', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (15, '15 - 土家族	', '15', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (16, '16 - 哈尼族	', '16', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (17, '17 - 哈萨克族	', '17', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (18, '18 - 傣族	', '18', '-', 'admin', '2025-11-18 15:24:13', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (19, '19 - 黎族	', '19', '-', 'admin', '2025-11-18 15:24:13', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (20, '20 - 傈僳族	', '20', '-', 'admin', '2025-11-18 15:24:13', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (21, '21 - 佤族	', '21', '-', 'admin', '2025-11-18 15:24:13', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (22, '22 - 畲族	', '22', '-', 'admin', '2025-11-18 15:24:13', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (23, '23 - 高山族	', '23', '-', 'admin', '2025-11-18 15:24:13', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (24, '24 - 拉祜族	', '24', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (25, '25 - 水族	', '25', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (26, '26 - 东乡族	', '26', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (27, '27 - 纳西族	', '27', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (28, '28 - 景颇族	', '28', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (29, '29 - 柯尔克孜族	', '29', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (30, '30 - 土族	', '30', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (31, '31 - 达斡尔族	', '31', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (32, '32 - 仫佬族	', '32', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (33, '33 - 羌族	', '33', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (34, '34 - 布朗族	', '34', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (35, '35 - 撒拉族	', '35', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (36, '36 - 毛难族	', '36', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (37, '37 - 仡佬族	', '37', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (38, '38 - 锡伯族	', '38', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (39, '39 - 阿昌族	', '39', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (40, '40 - 普米族	', '40', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (41, '41 - 塔吉克族	', '41', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (42, '42 - 怒族	', '42', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (43, '43 - 乌孜别克族	', '43', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (44, '44 - 俄罗斯族	', '44', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (45, '45 - 鄂温克族	', '45', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (46, '46 - 德昂族	', '46', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (47, '47 - 保安族	', '47', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (48, '48 - 裕固族	', '48', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (49, '49 - 京族	', '49', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (50, '50 - 塔塔尔族	', '50', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (51, '51 - 独龙族	', '51', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:53', '0');
INSERT INTO `dict_ethnic_group` VALUES (52, '52 - 鄂伦春族	', '52', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:53', '0');
INSERT INTO `dict_ethnic_group` VALUES (53, '53 - 赫哲族	', '53', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:53', '0');
INSERT INTO `dict_ethnic_group` VALUES (54, '54 - 门巴族	', '54', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:53', '0');
INSERT INTO `dict_ethnic_group` VALUES (55, '55 - 珞巴族	', '55', '-', 'admin', '2025-11-18 15:24:16', NULL, NULL, '0');
INSERT INTO `dict_ethnic_group` VALUES (56, '56 - 基诺族	', '56', '-', 'admin', '2025-11-18 15:24:17', NULL, NULL, '0');
INSERT INTO `dict_ethnic_group` VALUES (57, '66 - 其他	', '66', '-', 'admin', '2025-11-18 15:24:17', NULL, NULL, '0');
INSERT INTO `dict_ethnic_group` VALUES (58, '99 - 外籍人士	', '99', '-', 'admin', '2025-11-18 15:24:17', NULL, NULL, '0');

-- 行政区划代码表 初始记录见dict_administrative_division.sql
DROP TABLE IF EXISTS `dict_administrative_division`;
CREATE TABLE `dict_administrative_division` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id自增',
  `name` varchar(256) NOT NULL COMMENT '行政区划名称',
  `code` varchar(6) NOT NULL COMMENT '行政区划代码',
  `level` tinyint(1) NOT NULL COMMENT '行政级别(1:省级,2:市级,3:县级)',
  `parent_code` varchar(6) DEFAULT NULL COMMENT '上级行政区划代码',
  `remark` varchar(1024) DEFAULT '-' COMMENT '备注说明',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_division_code` (`code`),
  KEY `idx_division_parent` (`parent_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行政区划代码表(GB/T 2260-2013)';


SET FOREIGN_KEY_CHECKS = 1;

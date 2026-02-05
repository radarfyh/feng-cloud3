CREATE DATABASE IF NOT EXISTS `feng-user2-biz`;
USE `feng-user2-biz`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目名称',
  `project_code` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目编码',
  `project_desc` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '项目描述',
  `organ_code` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  `dept_id` INT(11) NULL DEFAULT NULL COMMENT '所属科室ID',
  `project_manager_staff_no` VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '项目经理工号',
  `project_manager_name` VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '项目经理姓名',
  `start_date` DATE NULL DEFAULT NULL COMMENT '项目开始日期',
  `end_date` DATE NULL DEFAULT NULL COMMENT '项目结束日期',
  `status_code` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '项目状态代码',
  `status_name` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '项目状态名称',
  `budget` DECIMAL(15, 2) NULL DEFAULT NULL COMMENT '项目预算',
  `actual_cost` DECIMAL(15, 2) NULL DEFAULT NULL COMMENT '实际成本',
  `progress` DECIMAL(5, 2) NULL DEFAULT NULL COMMENT '项目进度（百分比）',
  `priority_code` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '项目优先级代码',
  `priority_name` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '项目优先级名称',
  `status` CHAR(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态:0-启用 1-禁用',
  `create_by` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` CHAR(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_project_code` (`project_code`) USING BTREE COMMENT '项目编码唯一索引',
  INDEX `idx_organ_code` (`organ_code`) USING BTREE COMMENT '机构编码索引',
  INDEX `idx_dept_id` (`dept_id`) USING BTREE COMMENT '科室ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '项目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_staff
-- ----------------------------
INSERT INTO `project` (`project_name`, `project_code`, `project_desc`, `organ_code`, `dept_id`, `project_manager_staff_no`, `project_manager_name`, `start_date`, `end_date`, `status_code`, `status_name`, `budget`, `actual_cost`, `progress`, `priority_code`, `priority_name`, `status`, `create_by`, `update_by`)
VALUES
('ERP研发项目', 'ERP', '自研企业资源计划管理系统', 'DJ', 1, '1001', 'amy', '2023-01-01', '2023-12-31', 'P', '进行中', 100000.00, 50000.00, 50.00, 'H', '高', '0', 'admin', 'admin');

-- ----------------------------
-- Table structure for project_staff
-- ----------------------------
DROP TABLE IF EXISTS `project_staff`;
CREATE TABLE `project_staff`  (
  `project_id` int(11) NULL DEFAULT NULL COMMENT '项目id',
  `role_id` int(11) NULL DEFAULT NULL COMMENT '角色id',
  `staff_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '人员工号',
  `staff_notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '人员备注'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '项目成员表，暂未使用' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_staff
-- ----------------------------

-- ----------------------------
-- Table structure for role_application
-- ----------------------------
DROP TABLE IF EXISTS `role_application`;
CREATE TABLE `role_application`  (
  `role_id` int(11) NOT NULL COMMENT '角色id',
  `application_id` int(11) NOT NULL COMMENT '应用id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色应用关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role_application
-- ----------------------------

-- ----------------------------
-- Table structure for sys_affiliation
-- ----------------------------
DROP TABLE IF EXISTS `sys_affiliation`;
CREATE TABLE `sys_affiliation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `affiliation_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '名称',
  `affiliation_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '编码',
  `affiliation_introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '简介',
  `principal_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主要负责人',
  `principal_telephone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责人电话',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态:0-启用 1-禁用',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '联盟信息表，暂未使用' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_affiliation
-- ----------------------------

-- ----------------------------
-- Table structure for sys_affiliation_organ
-- ----------------------------
DROP TABLE IF EXISTS `sys_affiliation_organ`;
CREATE TABLE `sys_affiliation_organ`  (
  `affiliation_id` int(11) NULL DEFAULT NULL COMMENT '联盟id',
  `organ_id` int(11) NULL DEFAULT NULL COMMENT '机构id',
  `is_leader` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否为主联盟机构'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '联盟机构关联表，暂未使用' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_affiliation_organ
-- ----------------------------

-- ----------------------------
-- Table structure for sys_application
-- ----------------------------
DROP TABLE IF EXISTS `sys_application`;
CREATE TABLE `sys_application`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '应用名称',
  `application_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '应用编码',
  `manufacturer_id` int(11) NULL DEFAULT NULL COMMENT '厂商id',
  `app_en_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '应用英文名称',
  `app_abbr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '应用缩写名称',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '状态：0禁用-1-启用',
  `app_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `is_feng_portal` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否集成门户：0-否 1-是',
  `feng_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '类型：0-内部 1-外部',
  `client_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '标志系统的：0-BS 、 1-CS',
  `security_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求头认证code编码',
  `oauth_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'oauth授权码编码',
  `integration_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '系统url',
  `parameter_attribute` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '参数属性',
  `app_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '系统图标地址',
  `is_micro` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `micro_prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `micro_entry` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `app_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'SSO统一应用标识',
  `app_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'SSO统一秘钥',
  `sys_is_show` char(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否显示：0-不显示 1-显示，为1才能显示到前端',
  `display_form` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '展示形式：0-PC 1-MOBILE',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '应用系统表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_application
-- ----------------------------
INSERT INTO `sys_application` VALUES (1, '客户关系管理', 'CRM', 1, 'Customer relation management',  'CRM', NULL, NULL, '1', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_application` VALUES (2, '企业资源计划', 'ERP', 1, 'Enterprise resource plan',      'ERP', NULL, NULL, '1', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '0', 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_application` VALUES (3, '系统管理',     'USR', 2, 'user management',               'USR', NULL, NULL, '1', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '0', 'admin', now(), 'admin', now(), '0');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` int(11) NOT NULL COMMENT 'id',
  `config_no` int(11) NULL DEFAULT NULL COMMENT '配置编号',
  `config_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置编码',
  `config_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '配置值',
  `config_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '配置描述',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------

-- ----------------------------
-- Table structure for sys_datasource
-- ----------------------------
DROP TABLE IF EXISTS `sys_datasource`;
CREATE TABLE `sys_datasource`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '连接名',
  `ds_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据源类型',
  `conf_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '配置类型 （0 主机形式 | 1 url形式）',
  `host` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主机host地址',
  `port` int(11) NULL DEFAULT NULL COMMENT '主机端口号',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '拼接后的数据源地址',
  `ds_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据库名称',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `application_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '应用编码',
  `var_parameter` json NULL COMMENT '变量入参',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '数据源表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_datasource
-- ----------------------------

-- ----------------------------
-- Table structure for sys_department
-- ----------------------------
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `dept_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '科室编码',
  `dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '科室名称',
  `parent_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '上级科室编码，0表示无上级',
  `sort` int(11) NULL DEFAULT 1 COMMENT '排序',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  `subject_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '科目编码',
  `subject_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '科目名称',
  `dept_category_code` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '科室类别编码，和数据字典dept_category一致',
  `dept_category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '科室类别名称',
  `business_subjection` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务隶属编码，和数据字典business_subjection一致',
  `dept_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '科室位置',
  `dept_introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '科室简介',
  `branch_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分支编码',
  `branch_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分支名称',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '科室表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_department
-- ----------------------------
INSERT INTO `sys_department` VALUES (1, 'AQ',   '安全科',   '0',  0, 'F001', '', '', '1', '管理', '0', '', '', NULL, NULL, 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_department` VALUES (2, 'XT',   '系统科',   '0',  0, 'F001', '', '', '1', '业务', '0', '', '', NULL, NULL, 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_department` VALUES (3, 'XS',   '销售科',   '0',  0, 'F001', '', '', '1', '业务', '0', '', '', NULL, NULL, 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_department` VALUES (4, 'AQYZ', '安全一组', 'AQ', 0, 'F001', '', '', '1', '管理', '0', '', '', NULL, NULL, 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_department` VALUES (5, 'AQEZ', '安全二组', 'AQ', 0, 'F001', '', '', '1', '管理', '0', '', '', NULL, NULL, 'admin', now(), 'admin', now(), '0');

-- ----------------------------
-- Table structure for sys_dept_attribute
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_attribute`;
CREATE TABLE `sys_dept_attribute`  (
  `dept_id` int(11) NOT NULL COMMENT '科室id',
  `dept_attribute` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '科室属性编码'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '科室属性关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept_attribute
-- ----------------------------
INSERT INTO `sys_dept_attribute` VALUES (4, 'end_level_department');
INSERT INTO `sys_dept_attribute` VALUES (5, 'end_level_department');

-- ----------------------------
-- Table structure for sys_dept_relation
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_relation`;
CREATE TABLE `sys_dept_relation`  (
  `ancestor` int(11) NOT NULL COMMENT '祖先节点',
  `descendant` int(11) NOT NULL COMMENT '后代节点',
  INDEX `idx_ancestor`(`ancestor`) USING BTREE,
  INDEX `idx_descendant`(`descendant`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '科室关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept_relation
-- ----------------------------
INSERT INTO `sys_dept_relation` VALUES (1, 4);
INSERT INTO `sys_dept_relation` VALUES (1, 5);

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `dict_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典key：status',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典描述：状态',
  `is_system` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否是系统字典：0-否 1-是',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_del_flag`(`del_flag`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1,  'sex',                '性别',       '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (2,  'organ_type',         '机构类型',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (3,  'role_type',          '角色类型',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (4,  'user_status',        '账号状态',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (5,  'dept_category',      '科室类别',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (6,  'business_subjection','业务隶属',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (7,  'job_category',       '岗位类别',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (8,  'active_status',      '在岗状态',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (9,  'feng_type',          '应用类型',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (10, 'menu_type',          '菜单类型',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (11, 'field_data_type',    '数据类型',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (12, 'standard_type',      '标准类型',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (13, 'opt_status',         '操作状态',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (14, 'release_status',     '发布状态',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (15, 'sync_status',        '同步状态',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (16, 'identity_type',      '证件类型',   '0', 'F001', now(), now(), NULL, '0');
INSERT INTO `sys_dict` VALUES (17, 'sys_ds_type',        '数据源类型', '0', 'F001', now(), now(), NULL, '0');

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `dict_id` int(11) NOT NULL COMMENT '字典id',
  `value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典项value:0、1、2、3',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典项Value备注',
  `dict_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属字典key',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典项描述',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序（升序）',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_value`(`value`) USING BTREE,
  INDEX `idx_label`(`label`) USING BTREE,
  INDEX `idx_item_del_flag`(`del_flag`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` VALUES (1,  1,  '1',  '男',              'sex',                 NULL, 0,  NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (2,  1,  '2',  '女',              'sex',                 NULL, 0,  NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (3,  2,  '0',  '无',              'organ_type',          NULL, 0,  NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (4,  2,  '1',  '企业',            'organ_type',          NULL, 0,  NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (5,  2,  '2',  '政府',            'organ_type',          NULL, 0,  NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (6,  2,  '3',  '教育',            'organ_type',          NULL, 0,  NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (7,  2,  '4',  '医疗 ',           'organ_type',          NULL, 0,  NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (8,  3,  '0',  '系统',            'role_type',           NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (9,  3,  '1',  '自定义',          'role_type',           NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (50, 3,  '2',  '项目',            'role_type',           NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (10, 4,  '0',  '禁用',            'user_status',         NULL, 0,  NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (11, 4,  '1',  '启用',            'user_status',         NULL, 0,  NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (12, 5,  '1',  '行政',            'dept_category',       NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (13, 5,  '2',  '财务',            'dept_category',       NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (14, 5,  '3',  '业务',            'dept_category',       NULL, 30, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (15, 6,  '0',  '不区分',          'business_subjection', NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (16, 6,  '1',  '业务 ',           'business_subjection', NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (17, 6,  '2',  '职能',            'business_subjection', NULL, 30, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (18, 6,  '3',  '其他',            'business_subjection', NULL, 40, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (19, 8,  '0',  '离岗',            'active_status',       NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (20, 8,  '1',  '在岗',            'active_status',       NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (21, 9,  '0',  '内部应用',        'feng_type',           NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (22, 9,  '1',  '外部应用',        'feng_type',           NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (23, 10, '0',  '菜单',            'menu_type',           NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (24, 10, '1',  '按钮',            'menu_type',           NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (25, 11, '1',  '字符型',          'field_data_type',     NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (26, 11, '2',  '整数型',          'field_data_type',     NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (27, 11, '3',  '日期型',          'field_data_type',     NULL, 30, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (28, 12, '1',  '国标',            'standard_type',       NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (29, 12, '2',  '行标',            'standard_type',       NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (30, 13, 'A',  '新增',            'opt_status',          NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (31, 13, 'U',  '修改',            'opt_status',          NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (32, 13, 'D',  '删除',            'opt_status',          NULL, 30, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (33, 13, 'H',  '历史',            'opt_status',          NULL, 40, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (34, 14, '0',  '待审批',          'release_status',      NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (35, 14, '1',  '待发布',          'release_status',      NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (36, 14, '2',  '已发布',          'release_status',      NULL, 30, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (37, 14, '4',  '驳回',            'release_status',      NULL, 40, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (38, 15, '0',  '未开始',          'sync_status',         NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (39, 15, '1',  '等待中',          'sync_status',         NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (40, 15, '2',  '执行中',          'sync_status',         NULL, 30, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (41, 16, '1',  '身份证',          'identity_type',       NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (42, 16, '2',  '其他证件',        'identity_type',       NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (43, 17, '1',  'mysql',           'sys_ds_type',         NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (45, 17, '3',  'postgresql',      'sys_ds_type',         NULL, 30, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (46, 7,  '0',  '销售',            'job_category',        NULL, 10, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (47, 7,  '1',  '管理',            'job_category',        NULL, 20, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (48, 7,  '2',  '技术',            'job_category',        NULL, 30, NULL, now(), now(), '0');
INSERT INTO `sys_dict_item` VALUES (49, 7,  '3',  '其他',            'job_category',        NULL, 40, NULL, now(), now(), '0');

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `file_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件名',
  `bucket_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '桶名',
  `original` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型',
  `file_size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件管理表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '日志状态 0-正常',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日志标题',
  `service_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '访问服务名',
  `remote_addr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '访问IP地址',
  `user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求方式:User-Agent，访问工具',
  `request_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求url路径',
  `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求类型：POST GET PUT',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求参数',
  `time` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '执行时间',
  `exception` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '异常信息',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_log_create_by`(`create_by`) USING BTREE,
  INDEX `idx_request_uri`(`request_uri`) USING BTREE,
  INDEX `idx_log_type`(`type`) USING BTREE,
  INDEX `idx_log_create_date`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES (1, '0', '修改字典', 'sex', '192.168.1.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36', '/sysDict', 'PUT', '', '31', NULL, 'F001', 'fengyh', '2024-01-01 21:00:00', NULL, '0');

-- ----------------------------
-- Table structure for sys_manufacturer
-- ----------------------------
DROP TABLE IF EXISTS `sys_manufacturer`;
CREATE TABLE `sys_manufacturer`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `manufacturer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '厂商名称',
  `manufacturer_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '厂商编码',
  `artisan_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '技术人员姓名',
  `artisan_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '技术人员手机号',
  `service_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务人员姓名',
  `service_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务人员手机号',
  `manufacturer_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '厂商描述',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '厂商表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_manufacturer
-- ----------------------------
INSERT INTO `sys_manufacturer` VALUES (1, '华为', 'huawei', 'edison', '13511111111', 'tom', '13522222222', '最大的通讯设备供应商', 'F001', 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_manufacturer` VALUES (2, '大疆', 'dajiang', '张三', '13133333333', '李四', '13112341234', '最大无人机供应商',     'F001', 'admin', now(), 'admin', now(), '0');

-- ----------------------------
-- Table structure for sys_team
-- ----------------------------
DROP TABLE IF EXISTS `sys_team`;
CREATE TABLE `sys_team`  (
  `id` int(11) NOT NULL COMMENT 'id',
  `affiliation_id` int(11) NULL DEFAULT NULL COMMENT '联盟id',
  `team_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '小组名称',
  `team_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '小组编码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态:0-启用 1-禁用',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '小组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_team
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '菜单名称',
  `permission` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '按钮权限唯一标识，和数据字典opt_status一致，暂未使用',
  `path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '前端路径',
  `parent_id` int(11) NULL DEFAULT 0 COMMENT '父菜单ID，0表示无上级',
  `icon` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标，参见https://3x.antdv.com/components/icon-cn',
  `menu_describe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '菜单描述',
  `sort` int(11) NULL DEFAULT 1 COMMENT '排序值',
  `keep_alive` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否开启路由缓冲 0-否 1-是',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '类型 0-菜单 1-按钮',
  `application_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '应用编码',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1,  '基础管理', '',                        '',                      0,   'slack-square-outlined',    NULL, 1,  '0', '0', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (2,  '菜单管理', '',                        'System/Menu',           1,   'menu-outlined',            NULL, 2,  '0', '0', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (3,  '用户管理', '',                        'System/User',           1,   'user-outlined',            NULL, 3,  '0', '0', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (4,  '字典管理', '',                        'System/Dict',           1,   'schedule-outlined',        NULL, 4,  '0', '0', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (5,  '角色管理', '',                        'System/Role',           1,   'usergroup-add-outlined',   NULL, 5,  '0', '0', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (7,  '机构管理', '',                        '',                      0,   'group-outlined',           NULL, 4,  '0', '0', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (8,  '单位管理', '',                        'Organ/Organ',           7,   'hdd-outlined',             NULL, 5,  '0', '0', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (9,  '科室管理', '',                        'Organ/Department',      7,   'apartment-outlined',       NULL, 6,  '0', '0', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (10, '员工管理', '',                        'Organ/Staff',           7,   'user-add-outlined',        NULL, 7,  '0', '0', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (11, '资源管理', '',                        '',                      0,   'bank-outlined',            NULL, 6,  '0', '0', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (12, '厂商管理', '',                        'Resource/Manufacturer', 11,  'tag-outlined',             NULL, 7,  '0', '0', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (13, '应用管理', '',                        'Resource/Application',  11,  'appstore-outlined',        NULL, 8,  '0', '0', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (14, '数源管理', '',                        'Resource/Datasource',   11,  'box-plot-outlined',        NULL, 9,  '0', '0', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (15, '权限管理', 'role_perm',               NULL,                    5,   NULL,                       NULL, 10,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (16, '增加菜单', 'menu_add',                NULL,                    2,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (17, '删除菜单', 'menu_del',                NULL,                    2,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (18, '修改菜单', 'menu_edit',               NULL,                    2,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (19, '查询菜单', 'menu_query',              NULL,                    2,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (20, '增加用户', 'user_add',                NULL,                    3,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (21, '删除用户', 'user_del',                NULL,                    3,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (22, '修改用户', 'user_edit',               NULL,                    3,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (23, '查询用户', 'user_query',              NULL,                    3,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (24, '增加字典', 'dict_add',                NULL,                    4,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (25, '删除字典', 'dict_del',                NULL,                    4,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (26, '修改字典', 'dict_edit',               NULL,                    4,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (27, '查询字典', 'dict_query',              NULL,                    4,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (28, '增加角色', 'role_add',                NULL,                    5,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (29, '删除角色', 'role_del',                NULL,                    5,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (30, '修改角色', 'role_edit',               NULL,                    5,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (31, '查询角色', 'role_query',              NULL,                    5,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (32, '增加单位', 'org_add',                 NULL,                    8,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (33, '删除单位', 'org_del',                 NULL,                    8,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (34, '修改单位', 'org_edit',                NULL,                    8,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (35, '查询单位', 'org_query',               NULL,                    8,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (36, '增加科室', 'dept_add',                NULL,                    9,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (37, '删除科室', 'dept_del',                NULL,                    9,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (38, '修改科室', 'dept_edit',               NULL,                    9,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (39, '查询科室', 'dept_query',              NULL,                    9,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (40, '增加员工', 'staff_add',               NULL,                    10,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (41, '删除员工', 'staff_del',               NULL,                    10,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (42, '修改员工', 'staff_edit',              NULL,                    10,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (43, '查询员工', 'staff_query',             NULL,                    10,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (44, '增加厂商', 'manuf_add',               NULL,                    12,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (45, '删除厂商', 'manuf_del',               NULL,                    12,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (46, '修改厂商', 'manuf_edit',              NULL,                    12,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (47, '查询厂商', 'manuf_query',             NULL,                    12,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (48, '增加应用', 'app_add',                 NULL,                    13,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (49, '删除应用', 'app_del',                 NULL,                    13,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (50, '修改应用', 'app_edit',                NULL,                    13,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (51, '查询应用', 'app_query',               NULL,                    13,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (52, '增加数源', 'ds_add',                  NULL,                    14,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (53, '删除数源', 'ds_del',                  NULL,                    14,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (54, '修改数源', 'ds_edit',                 NULL,                    14,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (55, '查询数源', 'ds_query',                NULL,                    14,  NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (56, '增加字典条目', 'dict_item_add',       NULL,                    4,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (57, '删除字典条目', 'dict_item_del',       NULL,                    4,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (58, '修改字典条目', 'dict_item_edit',      NULL,                    4,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (59, '查询字典条目', 'dict_item_query',     NULL,                    4,   NULL,                       NULL, 1,  '0', '1', 'USR', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (60, '日志管理', '',                        '',                      0,   'switcher-outlined',        NULL, 6,  '0', '0', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (61, '类型维护', '',                        'Log/LogType',           60,  'tag-outlined',             NULL, 11, '0', '0', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (62, '日志查询', '',                        'Log/LoginLog',          60,  'appstore-outlined',        NULL, 12, '0', '0', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (63, '数据处理', '',                        'Log/DataIntegrity',     60,  'bank-outlined',            NULL, 13, '0', '0', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (64, '智能体平台', '',                      '',                      0,   'switcher-outlined',        NULL, 6,  '0', '0', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (65, '聊天', '',                            'Agent/chat',            64,  'tag-outlined',             NULL, 12, '0', '0', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (66, 'AI应用管理', '',                      'Agent/list',            64,  'appstore-outlined',        NULL, 13, '0', '0', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (67, '模型管理', '',                        'aigc/model',            64,  'bank-outlined',            NULL, 14, '0', '0', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (68, '向量库管理', '',                      'aigc/embed-store',      64,  'bank-outlined',            NULL, 15, '0', '0', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (69, '知识库管理', '',                      'aigc/knowledge',        64,  'bank-outlined',            NULL, 16, '0', '0', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (70, '大模型对话日志', '',                  'aigc/message',          64,  'bank-outlined',            NULL, 17, '0', '0', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (71, '账单查询', '',                        'aigc/order',            64,  'bank-outlined',            NULL, 18, '0', '0', 'ERP', now(), now(), '0');

INSERT INTO `sys_menu` VALUES (72,  '增加日志类型',   'log_type_add',             NULL,                    61,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (73,  '删除日志类型',   'log_type_del',             NULL,                    61,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (74,  '修改日志类型',   'log_type_edit',            NULL,                    61,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (75,  '查询日志类型',   'log_type_query',           NULL,                    61,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
                                                                                                                                                                  
INSERT INTO `sys_menu` VALUES (76,  '完成对话操作',   'chat:completions',         NULL,                    65,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (77,  '清除聊天历史',   'chat:messages:clean',      NULL,                    65,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
                                                                                                                                                                  
INSERT INTO `sys_menu` VALUES (78,  '删除日志',       'log_del',                  NULL,                    62,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (79,  '查询日志',       'log_query',                NULL,                    62,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
                                                                                                                                                                  
INSERT INTO `sys_menu` VALUES (80,  '删除处理日志',   'dict_item_del',            NULL,                    63,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (81,  '查询处理日志',   'dict_item_query',          NULL,                    63,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
                                                                                                                                                                  
INSERT INTO `sys_menu` VALUES (82,  '增加智能体应用', 'aigc:app:add',             NULL,                    66,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (83,  '删除智能体应用', 'aigc:app:delete',          NULL,                    66,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (84,  '修改智能体应用', 'aigc:app:update',          NULL,                    66,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (85,  '查询智能体应用', 'chat-docs:view',           NULL,                    66,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (86,  'Chat权限',       'chat:completions',         NULL,                    66,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (87,  '文本向量化',     'aigc:embedding:text',      NULL,                    66,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (88,  '文档向量化',     'aigc:embedding:docs',      NULL,                    66,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
                                                                                                                                                                  
INSERT INTO `sys_menu` VALUES (89,  '增加大语言模型', 'aigc:model:add',           NULL,                    67,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (90,  '删除大语言模型', 'aigc:model:delete',        NULL,                    67,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (91,  '修改大语言模型', 'aigc:model:update',        NULL,                    67,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (92,  '查询大语言模型', 'aigc:model:query',         NULL,                    67,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
                                                                                                                                                                  
INSERT INTO `sys_menu` VALUES (93,  '增加向量库',     'aigc:embed-store:add',     NULL,                    68,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (94,  '删除向量库',     'aigc:embed-store:delete',  NULL,                    68,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (95,  '修改向量库',     'aigc:embed-store:update',  NULL,                    68,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (96,  '查询向量库',     'aigc:embed-store:query',   NULL,                    68,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
                                                                                                                                                                  
INSERT INTO `sys_menu` VALUES (97,  '增加知识库',     'aigc:knowledge:add',       NULL,                    69,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (98,  '删除知识库',     'aigc:knowledge:delete',    NULL,                    69,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (99,  '修改知识库',     'aigc:knowledge:update',    NULL,                    69,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (100, '查询知识库',     'aigc:knowledge:query',     NULL,                    69,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (101, '增加文档',       'aigc:docs:add',            NULL,                    69,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (102, '删除文档',       'aigc:docs:delete',         NULL,                    69,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (103, '修改文档',       'aigc:docs:update',         NULL,                    69,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (104, '查询文档',       'aigc:docs:query',          NULL,                    69,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
                                                                                                                                                                  
INSERT INTO `sys_menu` VALUES (105, '删除对话数据',   'aigc:message:delete',      NULL,                    70,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
INSERT INTO `sys_menu` VALUES (106, '查询对话数据',   'aigc:message:query',       NULL,                    70,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');
                                                                                                                                                                  
INSERT INTO `sys_menu` VALUES (107, '查询账单',       'aigc:order:query',         NULL,                    71,   NULL,                       NULL, 1,  '0', '1', 'ERP', now(), now(), '0');

-- ----------------------------
-- Table structure for sys_oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `sys_oauth_client_details`;
CREATE TABLE `sys_oauth_client_details`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `client_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Oauth2对应的clientId',
  `resource_ids` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微服务id',
  `client_secret` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '指定客户端(client)的访问密匙',
  `scope` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '指定客户端申请的权限范围',
  `authorized_grant_types` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '指定客户端支持的grant_type',
  `web_server_redirect_uri` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户端的重定向URI,可为空',
  `authorities` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '指定客户端所拥有的Spring Security的权限值',
  `access_token_validity` int(11) NULL DEFAULT NULL COMMENT '设定客户端的access_token的有效时间值',
  `refresh_token_validity` int(11) NULL DEFAULT NULL COMMENT '设定客户端的refresh_token的有效时间值',
  `additional_information` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'JSON扩展：enc_flag加密标志，captcha_flag验证码标志',
  `autoapprove` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否开启验证码',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '终端信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oauth_client_details
-- ----------------------------
INSERT INTO `sys_oauth_client_details` VALUES (1, 'feng', NULL, 'Slxx@2025', 'server', 'password,refresh_token,authorization_code,client_credentials',  NULL, NULL, 86400, 86400, '{ \"enc_flag\":\"1\",\"captcha_flag\":\"1\"}', 'true', 'F001', '0');
INSERT INTO `sys_oauth_client_details` VALUES (2, 'test', NULL, 'Slxx@2025', 'server', 'password,refresh_token',                                        NULL, NULL, 86400, 86400, '{ \"enc_flag\":\"0\",\"captcha_flag\":\"0\"}', 'true', 'F001', '0');

-- ----------------------------
-- Table structure for sys_organ
-- ----------------------------
DROP TABLE IF EXISTS `sys_organ`;
CREATE TABLE `sys_organ`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `organ_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '机构名称',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '机构编码',
  `organ_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '机构类型，关联数据字典organ_type',
  `organ_alias_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '第二名称（简称）',
  `parent_id` int(11) NULL DEFAULT 0 COMMENT '上级机构',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `organ_level` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '级别，备用',
  `organ_grade` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '等次，备用',
  `organ_category_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '机构类别编码，备用',
  `organ_category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '机构类别名称，备用',
  `economic_type_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '经济类型编码，备用',
  `economic_type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '经济类型名称，备用',
  `manage_class_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类管理代码，备用',
  `manage_class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类管理名称，备用',
  `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址全路径',
  `addr_province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址-省',
  `addr_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址-市',
  `addr_county` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址-县',
  `addr_town` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址-乡',
  `addr_village` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址-村',
  `addr_house_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址-门牌号',
  `administrative_division` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '行政区划',
  `zip_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮编',
  `telephone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `website` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '网址',
  `establish_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '成立日期',
  `organ_introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '机构介绍',
  `traffic_route` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '交通路线',
  `approval_authority` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审批机关',
  `register_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登记号',
  `legal_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '法人',
  `principal_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主要负责人',
  `principal_telecom` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责人电话',
  `license_start_date` date NULL DEFAULT NULL COMMENT '执业许可开始日期',
  `license_end_date` date NULL DEFAULT NULL COMMENT '执业许可结束日期',
  `subject_services` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '科目',
  `major_subject_services` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '重点科目',
  `is_supervisory` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否监督机构',
  `supervisory_property_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督机构性质代码',
  `supervisory_property_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督机构性质名称',
  `supervisory_subordination_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督机构隶属关系代码',
  `supervisory_subordination_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督机构隶属名称',
  `administrative_level_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督机构行政级别代码',
  `administrative_level_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督机构行政级别名称',
  `supervisory_dept_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督机构科室编码',
  `supervisory_dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督机构科室名称',
  `branching_quantity` int(11) NULL DEFAULT NULL COMMENT '派出（分支）机构数量',
  `is_national_autonomy` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '机构所在地民族自治地方标志',
  `branch_org_level_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分支机构级别代码',
  `branch_org_level_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分支机构级别名称',
  `staff_quantity` int(11) NULL DEFAULT NULL COMMENT '员工数',
  `daily_visits` int(11) NULL DEFAULT NULL COMMENT '业务量',
  `product_quantity` int(11) NULL DEFAULT NULL COMMENT '产品数',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '状态',
  `organ_pictures` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '机构图片',
  `license_pictures` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '资质图片',
  `default_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '默认密码',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '机构表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_organ
-- ----------------------------
INSERT INTO `sys_organ` VALUES (1, '大疆公司', 'F001', '1', 'DJ', 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '弹子石新街', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', NULL, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '', NULL, '', NULL, '', NULL, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', NULL, '123456', 'admin', now(), 'admin', now(), '0');

-- ----------------------------
-- Table structure for sys_staff
-- ----------------------------
DROP TABLE IF EXISTS `sys_staff`;
CREATE TABLE `sys_staff`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  `dept_id` int(11) NULL DEFAULT NULL COMMENT '所属科室id',
  `staff_no` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工号',
  `staff_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `nationality_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '国籍编码 (GB/T 2659)',
  `nationality_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '国籍名称',
  `nation_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '民族编码 (GB/T 3304)',
  `nation_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '民族名称',
  `identification_no` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `gender_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别代码，和数据字典sex一致',
  `birthdate` date NULL DEFAULT NULL COMMENT '出生日期',
  `telephone` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `marital_status_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '婚姻状况代码',
  `marital_status_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '婚姻状况名称',
  `native_place` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '籍贯',
  `politics_status_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '政治面貌代码',
  `politics_status_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '政治面貌名称',
  `addr_province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址-省',
  `addr_city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址-省',
  `addr_county` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址-县',
  `addr_town` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址-乡',
  `addr_village` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址-村',
  `addr_house_no` int(11) NULL DEFAULT NULL COMMENT '地址-门牌号',
  `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '详细地址',
  `zip_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮编',
  `education_level_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学历代码 (GB/T 4658)',
  `education_level_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学历名称',
  `degree_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学位代码 (GB/T 6864)',
  `degree_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学位名称',
  `subject_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业代码 (GB/T 16835)',
  `subject_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业名称',
  `graduate_school_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '毕业院校',
  `work_begin_date` date NULL DEFAULT NULL COMMENT '参加工作日期',
  `job_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '岗位类别',
  `technical_qualifications_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业技术职务代码 (GB/T 8561)',
  `technical_qualifications_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业技术职务名称',
  `technical_position_category_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业技术职务类别代码，备用',
  `technical_position_category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业技术职务类别名称，备用',
  `management_position_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '行政/业务管理职务代码 (GB/T 12403)',
  `management_position_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '行政/业务管理职务名称',
  `administration_level_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '行政职级代码',
  `administration_level_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '行政职级名称',
  `title_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职称代码',
  `title_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职称名称',
  `title_level_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职称级别代码 (高级、中级、初级)',
  `title_level_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职称级别名称 (高级、中级、初级)',
  `is_organizational` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否编制人员',
  `position_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职务',
  `staff_category_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '人员类别代码',
  `staff_category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '人员类别名称',
  `active_status_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '在岗状态代码',
  `active_status_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '在岗状态名称',
  `qualification_certificate_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资格证书编号',
  `practising_certificate_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '执业证书编号',
  `expertise_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '擅长领域',
  `detailed_introduction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '详细介绍',
  `is_general_staff` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否普通职员',
  `is_country_staff` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否乡村职员',
  `is_supervisory` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督人员，备用',
  `supervisory_authorized_category_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督人员编制类别代码，备用',
  `supervisory_authorized_category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督人员编制类别名称，备用',
  `supervisory_employee_category_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督人员职工类别代码，备用',
  `supervisory_employee_category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督人员职工类别名称，备用',
  `supervisory_practice_scope_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督员执业范围代码，备用',
  `supervisory_practice_scope_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '监督员执业范围名称，备用',
  `photograph` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '照片地址',
  `electronic_signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电子签名',
  `qualification_certificate_pictures` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资格证书',
  `practising_certificate_pictures` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资格证书',
  `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'openId',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  `dept_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '科室编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '员工表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_staff
-- ----------------------------
INSERT INTO `sys_staff` VALUES (1, 'F001', 1, '1001', 'Amy',       NULL, NULL, NULL, NULL, '11111111',     '2', '女',    NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,      NULL,     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, now(), NULL, now(), '0', '01');
INSERT INTO `sys_staff` VALUES (2, 'F001', 1, '1002', 'Tom',       NULL, NULL, NULL, NULL, '22222222',     '1', '男',    NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,      NULL,     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, now(), NULL, now(), '0', '02');
INSERT INTO `sys_staff` VALUES (3, 'F001', 1, '1003', 'Edison',    NULL, NULL, NULL, NULL, '33333333',     '1', '男',    NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1',      '1',       NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, now(), NULL, now(), '0', '03');

-- ----------------------------
-- Table structure for sys_staff_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_staff_dept`;
CREATE TABLE `sys_staff_dept`  (
  `staff_id` int(11) NULL DEFAULT NULL COMMENT '人员id',
  `department_id` int(11) NULL DEFAULT NULL COMMENT '科室id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '人员科室关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_staff_dept
-- ----------------------------
INSERT INTO `sys_staff_dept` VALUES (1, 1);
INSERT INTO `sys_staff_dept` VALUES (2, 1);
INSERT INTO `sys_staff_dept` VALUES (3, 1);

-- ----------------------------
-- Table structure for sys_public_param
-- ----------------------------
DROP TABLE IF EXISTS `sys_public_param`;
CREATE TABLE `sys_public_param`  (
  `public_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `public_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `public_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `public_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0',
  `validate_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `public_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0',
  `system` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0',
  PRIMARY KEY (`public_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公共参数配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_public_param
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Id',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色名称',
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色编码',
  `role_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `ds_type` int(11) NULL DEFAULT 0 COMMENT '数据权限类型：0-全部 1-自定义',
  `ds_scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据权限作用范围：科室id逗号隔开',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '角色类型:0-系统角色 1-自定义角色 2-项目角色',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  `role_start_time` datetime NULL DEFAULT NULL COMMENT '角色有效开始时间',
  `role_end_time` datetime NULL DEFAULT NULL COMMENT '角色有效结束时间',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '是否内置：0-否 1-是',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_code`(`role_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin',        '超级管理员（内置，不允许删除）',     0, NULL, '0', 'F001', NULL, NULL, '1', 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_role` VALUES (2, '普通职员',   'default',      '普通职员（内置，不允许删除）',       0, NULL, '0', 'F001', NULL, NULL, '1', 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_role` VALUES (3, '组长',       'leader',       '项目领导（内置，不允许删除）',       0, NULL, '2', 'F001', NULL, NULL, '1', 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_role` VALUES (4, '成员',       'member',       '项目成员（内置，不允许删除）',       0, NULL, '2', 'F001', NULL, NULL, '1', 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_role` VALUES (5, '助手',       'assistant',    'AI Agent助手（内置，不允许删除）',   0, NULL, '0', 'F001', NULL, NULL, '1', 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_role` VALUES (6, '系统',       'system',       'AI Agent系统（内置，不允许删除）',   0, NULL, '0', 'F001', NULL, NULL, '1', 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_role` VALUES (7, '研发人员',   'tech_role',    '技术岗位',                           0, NULL, '1', 'F001', NULL, NULL, '0', 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_role` VALUES (8, '产线工人',   'worker',       '产线工人',                           0, NULL, '1', 'F001', NULL, NULL, '0', 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_role` VALUES (9, '销售人员',   'sale_role',    '销售岗位',                           0, NULL, '1', 'F001', NULL, NULL, '0', 'admin', now(), 'admin', now(), '0');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `menu_id` int(11) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------

INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 2);
INSERT INTO `sys_role_menu` VALUES (2, 3);
INSERT INTO `sys_role_menu` VALUES (2, 4);
INSERT INTO `sys_role_menu` VALUES (2, 5);
INSERT INTO `sys_role_menu` VALUES (2, 7);
INSERT INTO `sys_role_menu` VALUES (2, 8);
INSERT INTO `sys_role_menu` VALUES (2, 9);
INSERT INTO `sys_role_menu` VALUES (2, 10);
INSERT INTO `sys_role_menu` VALUES (2, 11);
INSERT INTO `sys_role_menu` VALUES (2, 12);
INSERT INTO `sys_role_menu` VALUES (2, 13);
INSERT INTO `sys_role_menu` VALUES (2, 14);

INSERT INTO `sys_role_menu` VALUES (2, 16);
INSERT INTO `sys_role_menu` VALUES (2, 17);
INSERT INTO `sys_role_menu` VALUES (2, 18);
INSERT INTO `sys_role_menu` VALUES (2, 19);

INSERT INTO `sys_role_menu` VALUES (2, 20);
INSERT INTO `sys_role_menu` VALUES (2, 21);
INSERT INTO `sys_role_menu` VALUES (2, 22);
INSERT INTO `sys_role_menu` VALUES (2, 23);

INSERT INTO `sys_role_menu` VALUES (2, 24);
INSERT INTO `sys_role_menu` VALUES (2, 25);
INSERT INTO `sys_role_menu` VALUES (2, 26);
INSERT INTO `sys_role_menu` VALUES (2, 27);
INSERT INTO `sys_role_menu` VALUES (2, 56);
INSERT INTO `sys_role_menu` VALUES (2, 57);
INSERT INTO `sys_role_menu` VALUES (2, 58);
INSERT INTO `sys_role_menu` VALUES (2, 59);

INSERT INTO `sys_role_menu` VALUES (2, 28);
INSERT INTO `sys_role_menu` VALUES (2, 29);
INSERT INTO `sys_role_menu` VALUES (2, 30);
INSERT INTO `sys_role_menu` VALUES (2, 31);

INSERT INTO `sys_role_menu` VALUES (2, 32);
INSERT INTO `sys_role_menu` VALUES (2, 33);
INSERT INTO `sys_role_menu` VALUES (2, 34);
INSERT INTO `sys_role_menu` VALUES (2, 35);

INSERT INTO `sys_role_menu` VALUES (2, 36);
INSERT INTO `sys_role_menu` VALUES (2, 37);
INSERT INTO `sys_role_menu` VALUES (2, 38);
INSERT INTO `sys_role_menu` VALUES (2, 39);

INSERT INTO `sys_role_menu` VALUES (2, 40);
INSERT INTO `sys_role_menu` VALUES (2, 41);
INSERT INTO `sys_role_menu` VALUES (2, 42);
INSERT INTO `sys_role_menu` VALUES (2, 43);

INSERT INTO `sys_role_menu` VALUES (2, 44);
INSERT INTO `sys_role_menu` VALUES (2, 45);
INSERT INTO `sys_role_menu` VALUES (2, 46);
INSERT INTO `sys_role_menu` VALUES (2, 47);

INSERT INTO `sys_role_menu` VALUES (2, 48);
INSERT INTO `sys_role_menu` VALUES (2, 49);
INSERT INTO `sys_role_menu` VALUES (2, 50);
INSERT INTO `sys_role_menu` VALUES (2, 51);

INSERT INTO `sys_role_menu` VALUES (2, 52);
INSERT INTO `sys_role_menu` VALUES (2, 53);
INSERT INTO `sys_role_menu` VALUES (2, 54);
INSERT INTO `sys_role_menu` VALUES (2, 55);

INSERT INTO `sys_role_menu` VALUES (2, 60);
INSERT INTO `sys_role_menu` VALUES (2, 61);
INSERT INTO `sys_role_menu` VALUES (2, 62);
INSERT INTO `sys_role_menu` VALUES (2, 63);
INSERT INTO `sys_role_menu` VALUES (2, 64);
INSERT INTO `sys_role_menu` VALUES (2, 65);
INSERT INTO `sys_role_menu` VALUES (2, 67);
INSERT INTO `sys_role_menu` VALUES (2, 68);
INSERT INTO `sys_role_menu` VALUES (2, 69);
INSERT INTO `sys_role_menu` VALUES (2, 70);
INSERT INTO `sys_role_menu` VALUES (2, 71);
INSERT INTO `sys_role_menu` VALUES (2, 72);
INSERT INTO `sys_role_menu` VALUES (2, 73);
INSERT INTO `sys_role_menu` VALUES (2, 74);
INSERT INTO `sys_role_menu` VALUES (2, 75);
INSERT INTO `sys_role_menu` VALUES (2, 76);
INSERT INTO `sys_role_menu` VALUES (2, 77);
INSERT INTO `sys_role_menu` VALUES (2, 78);
INSERT INTO `sys_role_menu` VALUES (2, 79);
INSERT INTO `sys_role_menu` VALUES (2, 80);
INSERT INTO `sys_role_menu` VALUES (2, 81);
INSERT INTO `sys_role_menu` VALUES (2, 82);
INSERT INTO `sys_role_menu` VALUES (2, 83);
INSERT INTO `sys_role_menu` VALUES (2, 84);
INSERT INTO `sys_role_menu` VALUES (2, 85);
INSERT INTO `sys_role_menu` VALUES (2, 87);
INSERT INTO `sys_role_menu` VALUES (2, 88);
INSERT INTO `sys_role_menu` VALUES (2, 89);
INSERT INTO `sys_role_menu` VALUES (2, 90);
INSERT INTO `sys_role_menu` VALUES (2, 91);
INSERT INTO `sys_role_menu` VALUES (2, 92);
INSERT INTO `sys_role_menu` VALUES (2, 93);
INSERT INTO `sys_role_menu` VALUES (2, 94);
INSERT INTO `sys_role_menu` VALUES (2, 95);
INSERT INTO `sys_role_menu` VALUES (2, 97);
INSERT INTO `sys_role_menu` VALUES (2, 98);
INSERT INTO `sys_role_menu` VALUES (2, 99);
INSERT INTO `sys_role_menu` VALUES (2, 100);
INSERT INTO `sys_role_menu` VALUES (2, 101);
INSERT INTO `sys_role_menu` VALUES (2, 102);
INSERT INTO `sys_role_menu` VALUES (2, 103);
INSERT INTO `sys_role_menu` VALUES (2, 104);
INSERT INTO `sys_role_menu` VALUES (2, 105);
INSERT INTO `sys_role_menu` VALUES (2, 107);
INSERT INTO `sys_role_menu` VALUES (2, 108);
INSERT INTO `sys_role_menu` VALUES (2, 109);
INSERT INTO `sys_role_menu` VALUES (2, 110);
INSERT INTO `sys_role_menu` VALUES (2, 111);
INSERT INTO `sys_role_menu` VALUES (2, 112);
INSERT INTO `sys_role_menu` VALUES (2, 113);
INSERT INTO `sys_role_menu` VALUES (2, 114);
INSERT INTO `sys_role_menu` VALUES (2, 115);
INSERT INTO `sys_role_menu` VALUES (2, 117);
INSERT INTO `sys_role_menu` VALUES (2, 118);
INSERT INTO `sys_role_menu` VALUES (2, 119);
INSERT INTO `sys_role_menu` VALUES (2, 120);
INSERT INTO `sys_role_menu` VALUES (2, 121);
INSERT INTO `sys_role_menu` VALUES (2, 122);
INSERT INTO `sys_role_menu` VALUES (2, 123);
INSERT INTO `sys_role_menu` VALUES (2, 124);
INSERT INTO `sys_role_menu` VALUES (2, 125);
INSERT INTO `sys_role_menu` VALUES (2, 127);
INSERT INTO `sys_role_menu` VALUES (2, 128);
INSERT INTO `sys_role_menu` VALUES (2, 129);
INSERT INTO `sys_role_menu` VALUES (2, 130);
INSERT INTO `sys_role_menu` VALUES (2, 131);
INSERT INTO `sys_role_menu` VALUES (2, 132);
INSERT INTO `sys_role_menu` VALUES (2, 133);
INSERT INTO `sys_role_menu` VALUES (2, 134);
INSERT INTO `sys_role_menu` VALUES (2, 135);
INSERT INTO `sys_role_menu` VALUES (2, 137);
INSERT INTO `sys_role_menu` VALUES (2, 138);
INSERT INTO `sys_role_menu` VALUES (2, 139);
INSERT INTO `sys_role_menu` VALUES (2, 140);
INSERT INTO `sys_role_menu` VALUES (2, 141);
INSERT INTO `sys_role_menu` VALUES (2, 142);
INSERT INTO `sys_role_menu` VALUES (2, 143);
INSERT INTO `sys_role_menu` VALUES (2, 144);
INSERT INTO `sys_role_menu` VALUES (2, 145);
INSERT INTO `sys_role_menu` VALUES (2, 147);
INSERT INTO `sys_role_menu` VALUES (2, 148);
INSERT INTO `sys_role_menu` VALUES (2, 149);
INSERT INTO `sys_role_menu` VALUES (2, 150);
INSERT INTO `sys_role_menu` VALUES (2, 151);
INSERT INTO `sys_role_menu` VALUES (2, 152);
INSERT INTO `sys_role_menu` VALUES (2, 153);
INSERT INTO `sys_role_menu` VALUES (2, 154);
INSERT INTO `sys_role_menu` VALUES (2, 155);
INSERT INTO `sys_role_menu` VALUES (2, 157);
INSERT INTO `sys_role_menu` VALUES (2, 158);
INSERT INTO `sys_role_menu` VALUES (2, 159);
INSERT INTO `sys_role_menu` VALUES (2, 160);
INSERT INTO `sys_role_menu` VALUES (2, 161);
INSERT INTO `sys_role_menu` VALUES (2, 162);
INSERT INTO `sys_role_menu` VALUES (2, 163);
INSERT INTO `sys_role_menu` VALUES (2, 164);
INSERT INTO `sys_role_menu` VALUES (2, 165);
INSERT INTO `sys_role_menu` VALUES (2, 167);
INSERT INTO `sys_role_menu` VALUES (2, 168);
INSERT INTO `sys_role_menu` VALUES (2, 169);
INSERT INTO `sys_role_menu` VALUES (2, 170);
INSERT INTO `sys_role_menu` VALUES (2, 171);
INSERT INTO `sys_role_menu` VALUES (2, 172);
INSERT INTO `sys_role_menu` VALUES (2, 173);
INSERT INTO `sys_role_menu` VALUES (2, 174);
INSERT INTO `sys_role_menu` VALUES (2, 175);
INSERT INTO `sys_role_menu` VALUES (2, 177);
INSERT INTO `sys_role_menu` VALUES (2, 178);
INSERT INTO `sys_role_menu` VALUES (2, 179);
INSERT INTO `sys_role_menu` VALUES (2, 180);
INSERT INTO `sys_role_menu` VALUES (2, 181);
INSERT INTO `sys_role_menu` VALUES (2, 182);
INSERT INTO `sys_role_menu` VALUES (2, 183);
INSERT INTO `sys_role_menu` VALUES (2, 184);
INSERT INTO `sys_role_menu` VALUES (2, 185);
INSERT INTO `sys_role_menu` VALUES (2, 187);
INSERT INTO `sys_role_menu` VALUES (2, 188);
INSERT INTO `sys_role_menu` VALUES (2, 189);


-- ----------------------------
-- Table structure for sys_route_conf
-- ----------------------------
DROP TABLE IF EXISTS `sys_route_conf`;
CREATE TABLE `sys_route_conf`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `route_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由名称',
  `route_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由id',
  `predicates` json NULL COMMENT '断言',
  `filters` json NULL COMMENT '过滤器，用于微服务接口限流过滤等作用',
  `uri` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由url',
  `sort_order` int(11) NULL DEFAULT 30 COMMENT '排序',
  `meta_data` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由元信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '路由配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_route_conf
-- ----------------------------
INSERT INTO `sys_route_conf` VALUES (1, '用户中心', 'feng-user2-biz', '[{\"args\": {\"_genkey_0\": \"/admin/**\"}, \"name\": \"Path\"}]',   '[{\"args\": {\"key-resolver\": \"#{@remoteAddrKeyResolver}\", \"redis-rate-limiter.burstCapacity\": \"1000\", \"redis-rate-limiter.replenishRate\": \"1000\"}, \"name\": \"RequestRateLimiter\"}]', 'lb://feng-user2-biz', 1,    NULL, now(), now(), '0');
INSERT INTO `sys_route_conf` VALUES (2, '认证中心', 'feng-auth',      '[{\"args\": {\"_genkey_0\": \"/auth/**\"}, \"name\": \"Path\"}]',    '[{\"args\": {}, \"name\": \"ValidateCodeGatewayFilter\"}, {\"args\": {}, \"name\": \"PasswordDecoderFilter\"}]',                                                                                    'lb://feng-auth',      2,    NULL, now(), now(), '0');
INSERT INTO `sys_route_conf` VALUES (3, '消息管理', 'feng-msg-biz',   '[{\"args\": {\"_genkey_0\": \"/msg/**\"}, \"name\": \"Path\"}]',     '[]',                                                                                                                                                                                                'lb://feng-msg-biz',   99,   NULL, now(), now(), '0');
INSERT INTO `sys_route_conf` VALUES (4, '日志服务', 'feng-log2-biz',  '[{\"args\": {\"_genkey_0\": \"/log/**\"}, \"name\": \"Path\"}]',     '[]',                                                                                                                                                                                                'lb://feng-log2-biz',  100,  NULL, now(), now(), '0');
INSERT INTO `sys_route_conf` VALUES (5, 'AI服务',   'feng-ai-biz',    '[{\"args\": {\"_genkey_0\": \"/aigc/**\"}, \"name\": \"Path\"}]',    '[]',                                                                                                                                                                                                'lb://feng-ai-biz',    100,  NULL, now(), now(), '0');

-- ----------------------------
-- Table structure for sys_social_details
-- ----------------------------
DROP TABLE IF EXISTS `sys_social_details`;
CREATE TABLE `sys_social_details`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主鍵',
  `type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `remark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `app_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,                      
  `app_secret` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `redirect_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统社交登录账号表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_social_details
-- ----------------------------

-- ----------------------------
-- Table structure for sys_team
-- ----------------------------
DROP TABLE IF EXISTS `sys_team`;
CREATE TABLE `sys_team`  (
  `id` int(11) NOT NULL COMMENT 'id',
  `affiliation_id` int(11) NULL DEFAULT NULL COMMENT '联盟id',
  `team_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '小组名称',
  `team_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '小组编码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态:0-启用 1-禁用',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '小组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_team
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `sex_code` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '性别编码，和数据字典sex一致',
  `salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '随机盐',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `staff_id` int(11) NULL DEFAULT NULL COMMENT '人员ID',
  `dept_id` int(11) NULL DEFAULT 0 COMMENT '科室ID',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  `switch_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '切换机构编码(第二机构)',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间，定时任务条件查询修改过期状态',
  `expired_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否过期（0未过期 1过期）',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '账号状态:0-禁用 1-启用',
  `lock_flag` tinyint(1) NULL DEFAULT 1 COMMENT '账号锁定：0-锁定 1-正常',
  `first_login` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否首次登录：0-否/1-是 默认1',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'amy',      'amy',     '$2a$10$IVurf0aelX1HwuXtn.a1i.eFdIraSmBqJ4wJJpThMrKDTQQHJP3XK', '1', NULL, '13111111111', 'amy@gmail.com',    '', 1001, 1, 'F001', '', NULL, 0, 1, 1, '0', 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_user` VALUES (2, 'tom',      'tom',     '$2a$10$C7PUtxOkNjuXhoDPdNZQYOF6ygq1/02YMwsMV/IfqpeJ2QoQxXqTG', '1', NULL, '13111111111', 'tom@gmail.com',    '', 1002, 2, 'F001', '', NULL, 0, 1, 1, '0', 'admin', now(), 'admin', now(), '0');
INSERT INTO `sys_user` VALUES (3, 'edison',   'edison',  '$2a$10$oXFppqYngsw8PgJZDI0fr.Uy4.npNgk7WbGfeo9mQELDA2Y/60Fui', '1', NULL, '13111111111', 'edison@gmail.com', '', 1003, 3, 'F001', '', NULL, 0, 1, 1, '0', 'admin', now(), 'admin', now(), '0');

-- ----------------------------
-- Table structure for sys_user_department
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_department`;
CREATE TABLE `sys_user_department`  (
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `dept_id` int(11) NOT NULL COMMENT '科室ID'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户科室表,暂未使用' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_department
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_ext_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_ext_config`;
CREATE TABLE `sys_user_ext_config`  (
  `id` int(11) NOT NULL COMMENT 'id',
  `ext_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '属性标识',
  `ext_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '属性名称',
  `ext_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '属性取值类型',
  `dict_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典取值URL',
  `dict_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '本地字典 json数组',
  `dict_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典value属性',
  `dict_label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典label属性',
  `fill_in_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '界面填写样式',
  `organ_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属机构编码',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户扩展属性配置，暂未使用' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_ext_config
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (3, 2);

-- ----------------------------
-- Table structure for team_staff
-- ----------------------------
DROP TABLE IF EXISTS `team_staff`;
CREATE TABLE `team_staff`  (
  `team_id` int(11) NULL DEFAULT NULL COMMENT '小组id',
  `staff_id` int(11) NULL DEFAULT NULL COMMENT '人员id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '小组人员关联表，暂未使用' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team_staff
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;

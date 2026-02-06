-- Set client encoding (PostgreSQL equivalent of SET NAMES)
SET client_encoding = 'UTF8';

-- Disable foreign key checks (PostgreSQL doesn't have an exact equivalent, but we can defer constraints)
SET CONSTRAINTS ALL DEFERRED;

-- ----------------------------
-- create database feng-user3-biz
-- ----------------------------

DROP DATABASE IF EXISTS "feng-user3-biz";
CREATE DATABASE "feng-user3-biz" 
    WITH 
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8';

\c "feng-user3-biz";


-- ----------------------------
-- Table structure for sys_role_application
-- ----------------------------
DROP TABLE IF EXISTS sys_role_application;
CREATE TABLE sys_role_application (
  role_id int NOT NULL,
  application_id int NOT NULL
);

COMMENT ON TABLE sys_role_application IS '角色应用关联表';
COMMENT ON COLUMN sys_role_application.role_id IS '角色id';
COMMENT ON COLUMN sys_role_application.application_id IS '应用id';

-- ----------------------------
-- Records of sys_role_application
-- ----------------------------


-- ----------------------------
-- Table structure for sys_application
-- ----------------------------
DROP TABLE IF EXISTS sys_application;
CREATE TABLE sys_application (
  id SERIAL PRIMARY KEY,
  app_name varchar(255) NULL,
  application_code varchar(100) NULL,
  manufacturer_id int NULL,
  app_en_name varchar(255) NULL,
  app_abbr varchar(255) NULL,
  status char(1) NULL DEFAULT '0',
  app_desc text NULL,
  is_feng_portal char(1) NULL DEFAULT '1',
  feng_type char(1) NULL DEFAULT '0',
  client_type char(1) NULL DEFAULT '0',
  security_code varchar(255) NULL,
  oauth_code varchar(255) NULL,
  integration_uri varchar(255) NULL,
  parameter_attribute varchar(255) NULL,
  app_icon varchar(255) NULL,
  is_micro char(1) NULL,
  micro_prefix varchar(255) NULL,
  micro_entry varchar(255) NULL,
  app_id varchar(255) NULL,
  app_secret varchar(255) NULL,
  sys_is_show char(1) NULL DEFAULT '1',
  display_form char(1) NULL DEFAULT '0',
  create_by varchar(64) NULL,
  create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(64) NULL,
  update_time timestamp NULL,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_application IS '应用系统表';
COMMENT ON COLUMN sys_application.id IS 'id';
COMMENT ON COLUMN sys_application.app_name IS '应用名称';
COMMENT ON COLUMN sys_application.application_code IS '应用编码';
COMMENT ON COLUMN sys_application.manufacturer_id IS '厂商id';
COMMENT ON COLUMN sys_application.app_en_name IS '应用英文名称';
COMMENT ON COLUMN sys_application.app_abbr IS '应用缩写名称';
COMMENT ON COLUMN sys_application.status IS '状态：0禁用-1-启用';
COMMENT ON COLUMN sys_application.app_desc IS '描述';
COMMENT ON COLUMN sys_application.is_feng_portal IS '是否集成门户：0-否 1-是';
COMMENT ON COLUMN sys_application.feng_type IS '类型：0-内部 1-外部';
COMMENT ON COLUMN sys_application.client_type IS '标志系统的：0-BS 、 1-CS';
COMMENT ON COLUMN sys_application.security_code IS '请求头认证code编码';
COMMENT ON COLUMN sys_application.oauth_code IS 'oauth授权码编码';
COMMENT ON COLUMN sys_application.integration_uri IS '系统url';
COMMENT ON COLUMN sys_application.parameter_attribute IS '参数属性';
COMMENT ON COLUMN sys_application.app_icon IS '系统图标地址';
COMMENT ON COLUMN sys_application.is_micro IS '图标';
COMMENT ON COLUMN sys_application.micro_prefix IS '微前端前缀';
COMMENT ON COLUMN sys_application.micro_entry IS '微前端入口';
COMMENT ON COLUMN sys_application.app_id IS 'SSO统一应用标识';
COMMENT ON COLUMN sys_application.app_secret IS 'SSO统一秘钥';
COMMENT ON COLUMN sys_application.sys_is_show IS '是否显示：0-不显示 1-显示，为1才能显示到前端';
COMMENT ON COLUMN sys_application.display_form IS '展示形式：0-PC 1-MOBILE';
COMMENT ON COLUMN sys_application.create_by IS '创建者';
COMMENT ON COLUMN sys_application.create_time IS '创建时间';
COMMENT ON COLUMN sys_application.update_by IS '更新者';
COMMENT ON COLUMN sys_application.update_time IS '修改时间';
COMMENT ON COLUMN sys_application.del_flag IS '逻辑删 0-正常 1-删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_sys_application_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_sys_application_update
BEFORE UPDATE ON sys_application
FOR EACH ROW
EXECUTE FUNCTION update_sys_application_timestamp();

-- ----------------------------
-- Records of sys_application
-- ----------------------------
INSERT INTO sys_application VALUES (1, '客户关系管理', 'CRM', 1, 'Customer relation management', 'CRM', NULL, NULL, '1', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '0', 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO sys_application VALUES (2, '企业资源计划', 'ERP', 1, 'Enterprise resource plan', 'ERP', NULL, NULL, '1', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '0', 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO sys_application VALUES (3, '系统管理', 'USR', 2, 'user management', 'USR', NULL, NULL, '1', '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '0', 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
  id SERIAL PRIMARY KEY,
  config_no int NULL,
  config_code varchar(255) NULL,
  config_value text NULL,
  config_desc text NULL,
  create_by varchar(64) NULL,
  create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(64) NULL,
  update_time timestamp NULL,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_config IS '系统配置表';
COMMENT ON COLUMN sys_config.id IS 'id';
COMMENT ON COLUMN sys_config.config_no IS '配置编号';
COMMENT ON COLUMN sys_config.config_code IS '配置编码';
COMMENT ON COLUMN sys_config.config_value IS '配置值';
COMMENT ON COLUMN sys_config.config_desc IS '配置描述';
COMMENT ON COLUMN sys_config.create_by IS '创建者';
COMMENT ON COLUMN sys_config.create_time IS '创建时间';
COMMENT ON COLUMN sys_config.update_by IS '更新者';
COMMENT ON COLUMN sys_config.update_time IS '修改时间';
COMMENT ON COLUMN sys_config.del_flag IS '逻辑删 0-正常 1-删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_sys_config_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_sys_config_update
BEFORE UPDATE ON sys_config
FOR EACH ROW
EXECUTE FUNCTION update_sys_config_timestamp();

-- ----------------------------
-- Records of sys_config
-- ----------------------------

-- ----------------------------
-- Table structure for sys_department
-- ----------------------------
DROP TABLE IF EXISTS sys_department;
CREATE TABLE sys_department (
  id SERIAL PRIMARY KEY,
  dept_code varchar(255) NULL,
  dept_name varchar(100) NOT NULL DEFAULT '0',
  parent_id int NOT NULL DEFAULT 0,
  parent_code varchar(255) NOT NULL DEFAULT '0',
  sort int NULL DEFAULT 1,
  organ_code varchar(64) NULL,
  subject_code varchar(100) NULL,
  subject_name varchar(100) NULL,
  dept_category_code char(20) NULL,
  dept_category_name varchar(100) NULL,
  business_subjection char(1) NULL,
  dept_location varchar(255) NULL,
  dept_introduction text NULL,
  branch_code varchar(255) NULL,
  branch_name varchar(255) NULL,
  create_by varchar(255) NULL,
  create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(64) NULL,
  update_time timestamp NULL,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_department IS '部门表';
COMMENT ON COLUMN sys_department.id IS 'id';
COMMENT ON COLUMN sys_department.dept_code IS '部门编码';
COMMENT ON COLUMN sys_department.dept_name IS '部门名称';
COMMENT ON COLUMN sys_department.parent_code IS '上级部门编码，0表示无上级';
COMMENT ON COLUMN sys_department.sort IS '排序';
COMMENT ON COLUMN sys_department.organ_code IS '所属机构编码';
COMMENT ON COLUMN sys_department.subject_code IS '科目编码';
COMMENT ON COLUMN sys_department.subject_name IS '科目名称';
COMMENT ON COLUMN sys_department.dept_category_code IS '部门类别编码，和数据字典dept_category一致';
COMMENT ON COLUMN sys_department.dept_category_name IS '部门类别名称';
COMMENT ON COLUMN sys_department.business_subjection IS '业务隶属编码，和数据字典business_subjection一致';
COMMENT ON COLUMN sys_department.dept_location IS '部门位置';
COMMENT ON COLUMN sys_department.dept_introduction IS '部门简介';
COMMENT ON COLUMN sys_department.branch_code IS '分支编码';
COMMENT ON COLUMN sys_department.branch_name IS '分支名称';
COMMENT ON COLUMN sys_department.create_by IS '创建者';
COMMENT ON COLUMN sys_department.create_time IS '创建时间';
COMMENT ON COLUMN sys_department.update_by IS '更新者';
COMMENT ON COLUMN sys_department.update_time IS '修改时间';
COMMENT ON COLUMN sys_department.del_flag IS '逻辑删 0-正常 1-删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_sys_department_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_sys_department_update
BEFORE UPDATE ON sys_department
FOR EACH ROW
EXECUTE FUNCTION update_sys_department_timestamp();

-- ----------------------------
-- Records of sys_department
-- ----------------------------
INSERT INTO sys_department VALUES (1, 'AQ', '安全科', 0, '0', 0, 'F001', '', '', '1', '管理', '0', '', '', NULL, NULL, 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO sys_department VALUES (2, 'XT', '信息技术部', 0, '0', 0, 'F001', '', '', '1', '业务', '0', '', '', NULL, NULL, 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO sys_department VALUES (3, 'XS', '销售科', 0, '0', 0, 'F001', '', '', '1', '业务', '0', '', '', NULL, NULL, 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO sys_department VALUES (4, 'AQYZ', '安全一组', 1, 'AQ', 0, 'F001', '', '', '1', '管理', '0', '', '', NULL, NULL, 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');
INSERT INTO sys_department VALUES (5, 'AQEZ', '安全二组', 1, 'AQ', 0, 'F001', '', '', '1', '管理', '0', '', '', NULL, NULL, 'admin', '2025-03-24 22:33:05', 'admin', '2025-03-24 22:33:05', '0');

-- ----------------------------
-- Table structure for sys_dept_attribute
-- ----------------------------
DROP TABLE IF EXISTS sys_dept_attribute;
CREATE TABLE sys_dept_attribute (
  dept_id int NOT NULL,
  dept_attribute varchar(255) NOT NULL
);

COMMENT ON TABLE sys_dept_attribute IS '部门属性关联表';
COMMENT ON COLUMN sys_dept_attribute.dept_id IS '部门id';
COMMENT ON COLUMN sys_dept_attribute.dept_attribute IS '部门属性编码';

-- ----------------------------
-- Records of sys_dept_attribute
-- ----------------------------
INSERT INTO sys_dept_attribute VALUES (1, 'security');
INSERT INTO sys_dept_attribute VALUES (1, 'quality');

-- ----------------------------
-- Table structure for sys_dept_relation
-- ----------------------------
DROP TABLE IF EXISTS sys_dept_relation;
CREATE TABLE sys_dept_relation (
  ancestor int NOT NULL,
  descendant int NOT NULL
);

CREATE INDEX idx_ancestor ON sys_dept_relation(ancestor);
CREATE INDEX idx_descendant ON sys_dept_relation(descendant);

COMMENT ON TABLE sys_dept_relation IS '部门关系表';
COMMENT ON COLUMN sys_dept_relation.ancestor IS '祖先节点';
COMMENT ON COLUMN sys_dept_relation.descendant IS '后代节点';

-- ----------------------------
-- Records of sys_dept_relation
-- ----------------------------
INSERT INTO sys_dept_relation VALUES (1, 4);
INSERT INTO sys_dept_relation VALUES (1, 5);

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict (
  id SERIAL PRIMARY KEY,
  dict_key varchar(100) NULL,
  description varchar(100) NULL,
  is_system char(1) NULL DEFAULT '0',
  organ_code varchar(64) NULL,
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  remarks varchar(255) NULL,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_dict IS '字典表';
COMMENT ON COLUMN sys_dict.id IS '编号';
COMMENT ON COLUMN sys_dict.dict_key IS '字典key';
COMMENT ON COLUMN sys_dict.description IS '字典描述';
COMMENT ON COLUMN sys_dict.is_system IS '是否是系统字典：0-否 1-是';
COMMENT ON COLUMN sys_dict.organ_code IS '所属机构编码';
COMMENT ON COLUMN sys_dict.create_time IS '创建时间';
COMMENT ON COLUMN sys_dict.update_time IS '更新时间';
COMMENT ON COLUMN sys_dict.remarks IS '备注';
COMMENT ON COLUMN sys_dict.del_flag IS '逻辑删 0-正常 1-删除';

CREATE INDEX idx_del_flag ON sys_dict(del_flag);

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_sys_dict_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_sys_dict_update
BEFORE UPDATE ON sys_dict
FOR EACH ROW
EXECUTE FUNCTION update_sys_dict_timestamp();

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO sys_dict VALUES (1, 'sex', '性别', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (2, 'organ_type', '机构类型', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (3, 'role_type', '角色类型', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (4, 'user_status', '账号状态', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (5, 'dept_category', '部门类别', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (6, 'business_subjection', '业务隶属', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (7, 'job_category', '岗位类别', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (8, 'active_status', '在岗状态', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (9, 'feng_type', '应用类型', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (10, 'menu_type', '菜单类型', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (11, 'field_data_type', '数据类型', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (12, 'standard_type', '标准类型', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (13, 'opt_status', '操作状态', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (14, 'release_status', '发布状态', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (15, 'sync_status', '同步状态', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (16, 'identity_type', '证件类型', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (17, 'sys_ds_type', '数据源类型', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (18, 'status', '状态', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (19, 'prompt_type', '提示词类型', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (20, 'model_provider', '模型提供商', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (21, 'model_type', '模型类型', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (22, 'doc_type', '文档类型', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (23, 'slice_mode', '切片模式', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (24, 'embed_store_type', '向量数据库类型', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (25, 'customer_source', '客户来源', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (26, 'customer_class', '客户行业', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (27, 'customer_level', '客户等级', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (28, 'contact_position', '联系人职务', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');
INSERT INTO sys_dict VALUES (29, 'dept_duty', '部门职责', '0', 'F001', '2025-03-24 22:33:06', '2025-03-24 22:33:06', NULL, '0');

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS sys_dict_item;
CREATE TABLE sys_dict_item (
  id SERIAL PRIMARY KEY,
  dict_id int NOT NULL,
  value varchar(100) NULL,
  label varchar(100) NULL,
  dict_key varchar(100) NULL,
  description varchar(100) NULL,
  sort int NOT NULL DEFAULT 0,
  remarks varchar(255) NULL,
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_dict_item IS '字典项';
COMMENT ON COLUMN sys_dict_item.id IS '编号';
COMMENT ON COLUMN sys_dict_item.dict_id IS '字典id';
COMMENT ON COLUMN sys_dict_item.value IS '字典项value:0、1、2、3';
COMMENT ON COLUMN sys_dict_item.label IS '字典项Value备注';
COMMENT ON COLUMN sys_dict_item.dict_key IS '所属字典key';
COMMENT ON COLUMN sys_dict_item.description IS '字典项描述';
COMMENT ON COLUMN sys_dict_item.sort IS '排序（升序）';
COMMENT ON COLUMN sys_dict_item.remarks IS '备注';
COMMENT ON COLUMN sys_dict_item.create_time IS '创建时间';
COMMENT ON COLUMN sys_dict_item.update_time IS '更新时间';
COMMENT ON COLUMN sys_dict_item.del_flag IS '逻辑删 0-正常 1-删除';

CREATE INDEX idx_value ON sys_dict_item(value);
CREATE INDEX idx_label ON sys_dict_item(label);
CREATE INDEX idx_item_del_flag ON sys_dict_item(del_flag);

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_sys_dict_item_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_sys_dict_item_update
BEFORE UPDATE ON sys_dict_item
FOR EACH ROW
EXECUTE FUNCTION update_sys_dict_item_timestamp();

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO sys_dict_item VALUES (1, 1, '1', '男', 'sex', NULL, 0, NULL, '2025-03-24 22:33:06', '2025-03-24 22:33:06', '0');
INSERT INTO sys_dict_item VALUES (2, 1, '2', '女', 'sex', NULL, 0, NULL, '2025-03-24 22:33:06', '2025-03-24 22:33:06', '0');
INSERT INTO sys_dict_item VALUES (3, 2, '0', '无', 'organ_type', NULL, 0, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (4, 2, '1', '企业', 'organ_type', NULL, 0, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (5, 2, '2', '政府', 'organ_type', NULL, 0, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (6, 2, '3', '教育', 'organ_type', NULL, 0, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (7, 2, '4', '医疗 ', 'organ_type', NULL, 0, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (8, 3, '0', '系统', 'role_type', NULL, 10, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (9, 3, '1', '自定义', 'role_type', NULL, 20, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (10, 4, '0', '禁用', 'user_status', NULL, 0, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (11, 4, '1', '启用', 'user_status', NULL, 0, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (12, 5, '1', '行政', 'dept_category', NULL, 10, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (13, 5, '2', '财务', 'dept_category', NULL, 20, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (14, 5, '3', '业务', 'dept_category', NULL, 30, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (15, 6, '0', '不区分', 'business_subjection', NULL, 10, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (16, 6, '1', '业务 ', 'business_subjection', NULL, 20, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (17, 6, '2', '职能', 'business_subjection', NULL, 30, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (18, 6, '3', '其他', 'business_subjection', NULL, 40, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (19, 8, '0', '离岗', 'active_status', NULL, 10, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (20, 8, '1', '在岗', 'active_status', NULL, 20, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (21, 9, '0', '内部应用', 'feng_type', NULL, 10, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (22, 9, '1', '外部应用', 'feng_type', NULL, 20, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (23, 10, '0', '菜单', 'menu_type', NULL, 10, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (24, 10, '1', '按钮', 'menu_type', NULL, 20, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (25, 11, '1', '字符型', 'field_data_type', NULL, 10, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (26, 11, '2', '整数型', 'field_data_type', NULL, 20, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (27, 11, '3', '日期型', 'field_data_type', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (28, 12, '1', '国标', 'standard_type', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (29, 12, '2', '行标', 'standard_type', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (30, 13, 'A', '新增', 'opt_status', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (31, 13, 'U', '修改', 'opt_status', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (32, 13, 'D', '删除', 'opt_status', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (33, 13, 'H', '历史', 'opt_status', NULL, 40, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (34, 14, '0', '待审批', 'release_status', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (35, 14, '1', '待发布', 'release_status', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (36, 14, '2', '已发布', 'release_status', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (37, 14, '4', '驳回', 'release_status', NULL, 40, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (38, 15, '0', '未开始', 'sync_status', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (39, 15, '1', '等待中', 'sync_status', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (40, 15, '2', '执行中', 'sync_status', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (41, 16, '1', '身份证', 'identity_type', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (42, 16, '2', '其他证件', 'identity_type', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (43, 17, 'mysql', 'mysql库', 'sys_ds_type', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (45, 17, 'postgresql', 'pg库', 'sys_ds_type', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (46, 7, '0', '销售', 'job_category', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (47, 7, '1', '管理', 'job_category', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (48, 7, '2', '技术', 'job_category', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (49, 7, '3', '其他', 'job_category', NULL, 40, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');

INSERT INTO sys_dict_item VALUES (51, 1, '0', '正常', 'status', NULL, 40, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (52, 2, '1', '异常', 'status', NULL, 20, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (53, 1, 'system', '系统', 'prompt_type', NULL, 40, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (54, 2, 'instruction', '指令', 'prompt_type', NULL, 20, NULL, '2025-03-24 22:33:07', '2025-03-24 22:33:07', '0');
INSERT INTO sys_dict_item VALUES (55, 3, 'template', '模板', 'prompt_type', NULL, 40, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (56, 1, 'OPENAI', 'OpenAI', 'model_provider', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (57, 2, 'DEEPSEEK', 'DEEPSEEK', 'model_provider', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (59, 4, 'ZHIPU', '智谱', 'model_provider', NULL, 40, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (60, 5, 'BAAI', 'BAAI', 'model_provider', NULL, 50, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (61, 6, 'MOKAAI', 'MokaAI', 'model_provider', NULL, 60, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (62, 7, 'CLAUDE', 'Claude', 'model_provider', NULL, 70, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (63, 8, 'JURASSIC', 'Jurassic', 'model_provider', NULL, 80, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (64, 9, 'META', '元宇宙脸书', 'model_provider', NULL, 90, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (65, 10, 'GOOGLE', '谷歌', 'model_provider', NULL, 100, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (66, 11, 'DOUBAO', '抖音豆包', 'model_provider', NULL, 110, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (67, 12, 'NETEASE', '‌网易', 'model_provider', NULL, 120, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (68, 13, 'XUNFEI', '科大讯飞', 'model_provider', NULL, 130, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (69, 14, 'BAIDU', '百度', 'model_provider', NULL, 140, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (70, 15, 'ALICLOUD', '阿里云', 'model_provider', NULL, 150, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (71, 16, 'OTHER', '其他', 'model_provider', NULL, 150, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (72, 1, 'CHAT', '聊天', 'model_type', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (73, 2, 'EMBEDDING', '向量', 'model_type', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (74, 3, 'TEXT_IMAGE', '图形', 'model_type', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (75, 4, 'WEB_SEARCH', '其他', 'model_type', NULL, 40, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (76, 1, 'TEXT', '文本输入', 'doc_type', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (77, 2, 'FILE', '文件系统上传', 'doc_type', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (78, 3, 'OSS', '对象存储服务上传', 'doc_type', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (81, 1, 'SENTENCE', '语句切割', 'slice_mode', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (82, 2, 'PARAGRAPH', '段落切割', 'slice_mode', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (83, 3, 'FIXED', '定长切割', 'slice_mode', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (86, 1, 'REDIS', 'REDIS向量库', 'embed_store_type', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (87, 2, 'PGVECTOR', 'PGVECTOR向量库', 'embed_store_type', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (88, 3, 'MILVUS', 'MILVUS向量库', 'embed_store_type', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');

INSERT INTO sys_dict_item VALUES (90, 1, 'website',  '官网', 'customer_source', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (91, 2, 'douyin',  '抖音', 'customer_source', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (92, 3, 'offline',  '线下', 'customer_source', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (93, 4, 'activity',  '活动', 'customer_source', NULL, 40, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (100, 1, 'A',  '农林牧渔业', 'customer_class', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (101, 2, 'A01',  '农业', 'customer_class', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (102, 3, 'B',  '采矿业', 'customer_class', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (103, 4, 'C',  '制造业', 'customer_class', NULL, 40, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (104, 5, 'D',  '电力、燃气及水生产和供应业', 'customer_class', NULL, 50, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (105, 6, 'G',  '交通运输、仓储和邮政业', 'customer_class', NULL, 60, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (106, 7, 'I',  '信息传输、软件和信息技术服务业', 'customer_class', NULL, 70, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (107, 8, 'M',  '科学研究和技术服务业', 'customer_class', NULL, 80, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (108, 9, 'N',  '水利、环境和公共设施管理业', 'customer_class', NULL, 90, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (120, 1, 'A',  'A类客户：重点投入资源，提供VIP服务', 'customer_level', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (121, 2, 'B',  'B类客户：定期跟进，推动升级', 'customer_level', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (122, 3, 'C',  'C类客户：优化服务以提升潜力', 'customer_level', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (123, 4, 'D',  'D类客户：逐步淘汰或减少投入', 'customer_level', NULL, 40, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (130, 1, 'A',  '部门职员、初级或其对等职务', 'contact_positon', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (131, 2, 'B',  '部门主任、中级或其对等职务', 'contact_positon', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (132, 3, 'C',  '经理、高级或其对等职务', 'contact_positon', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (133, 4, 'D',  '总经理、最高决策层或其对等职务', 'contact_positon', NULL, 40, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');

INSERT INTO sys_dict_item VALUES (140, 1, 'strategy',  '战略', 'dept_duty', NULL, 10, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (141, 2, 'salary',  '工资', 'dept_duty', NULL, 20, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (142, 3, 'attendance',  '考勤', 'dept_duty', NULL, 30, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (143, 4, 'research',  '研究', 'dept_duty', NULL, 40, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (144, 5, 'recruitment',  '招聘', 'dept_duty', NULL, 50, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (145, 6, 'production',  '生产', 'dept_duty', NULL, 60, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (146, 7, 'purchase',  '采购', 'dept_duty', NULL, 70, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (147, 8, 'sale',  '销售', 'dept_duty', NULL, 80, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (148, 9, 'invoice',  '发票', 'dept_duty', NULL, 90, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (149, 10, 'product',  '产品', 'dept_duty', NULL, 100, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (150, 11, 'customer service',  '客服', 'dept_duty', NULL, 110, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (151, 12, 'security',  '保障', 'dept_duty', NULL, 120, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');
INSERT INTO sys_dict_item VALUES (152, 13, 'quality',  '质量', 'dept_duty', NULL, 130, NULL, '2025-03-24 22:33:08', '2025-03-24 22:33:08', '0');

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS sys_file;
CREATE TABLE sys_file (
  id BIGSERIAL PRIMARY KEY,
  file_name varchar(100) NULL,
  bucket_name varchar(200) NULL,
  original varchar(100) NULL,
  type varchar(50) NULL,
  file_size bigint NULL,
  organ_code varchar(64) NULL,
  create_by varchar(32) NULL,
  create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(32) NULL,
  update_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_file IS '文件管理表';
COMMENT ON COLUMN sys_file.id IS '编号';
COMMENT ON COLUMN sys_file.file_name IS '文件名';
COMMENT ON COLUMN sys_file.bucket_name IS '桶名';
COMMENT ON COLUMN sys_file.original IS '原始文件名';
COMMENT ON COLUMN sys_file.type IS '类型';
COMMENT ON COLUMN sys_file.file_size IS '文件大小';
COMMENT ON COLUMN sys_file.organ_code IS '所属机构编码';
COMMENT ON COLUMN sys_file.create_by IS '创建者';
COMMENT ON COLUMN sys_file.create_time IS '上传时间';
COMMENT ON COLUMN sys_file.update_by IS '更新者';
COMMENT ON COLUMN sys_file.update_time IS '更新时间';
COMMENT ON COLUMN sys_file.del_flag IS '删除标志';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_sys_file_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_sys_file_update
BEFORE UPDATE ON sys_file
FOR EACH ROW
EXECUTE FUNCTION update_sys_file_timestamp();

-- ----------------------------
-- Records of sys_file
-- ----------------------------

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
  id BIGSERIAL PRIMARY KEY,
  type char(1) NULL DEFAULT '0',
  title varchar(255) NULL,
  service_id varchar(32) NULL,
  remote_addr varchar(255) NULL,
  user_agent text NULL,
  request_uri varchar(255) NULL,
  method varchar(10) NULL,
  params text NULL,
  time text NULL,
  exception text NULL,
  organ_code varchar(64) NULL,
  create_by varchar(255) NULL,
  create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  update_time timestamp NULL,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_log IS '日志表';
COMMENT ON COLUMN sys_log.id IS 'id';
COMMENT ON COLUMN sys_log.type IS '日志状态 0-正常';
COMMENT ON COLUMN sys_log.title IS '日志标题';
COMMENT ON COLUMN sys_log.service_id IS '访问服务名';
COMMENT ON COLUMN sys_log.remote_addr IS '访问IP地址';
COMMENT ON COLUMN sys_log.user_agent IS '请求方式:User-Agent，访问工具';
COMMENT ON COLUMN sys_log.request_uri IS '请求url路径';
COMMENT ON COLUMN sys_log.method IS '请求类型：POST GET PUT';
COMMENT ON COLUMN sys_log.params IS '请求参数';
COMMENT ON COLUMN sys_log.time IS '执行时间';
COMMENT ON COLUMN sys_log.exception IS '异常信息';
COMMENT ON COLUMN sys_log.organ_code IS '所属机构编码';
COMMENT ON COLUMN sys_log.create_by IS '创建者';
COMMENT ON COLUMN sys_log.create_time IS '创建时间';
COMMENT ON COLUMN sys_log.update_time IS '更新时间';
COMMENT ON COLUMN sys_log.del_flag IS '逻辑删 0-正常 1-删除';

CREATE INDEX idx_log_create_by ON sys_log(create_by);
CREATE INDEX idx_log_request_uri ON sys_log(request_uri);
CREATE INDEX idx_log_type ON sys_log(type);
CREATE INDEX idx_log_create_date ON sys_log(create_time);

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_sys_log_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_sys_log_update
BEFORE UPDATE ON sys_log
FOR EACH ROW
EXECUTE FUNCTION update_sys_log_timestamp();

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO sys_log VALUES (1, '0', '修改字典', 'sex', '192.168.1.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36', '/sysDict', 'PUT', '', '31', NULL, 'F001', 'fengyh', '2024-01-01 21:00:00', NULL, '0');

-- ----------------------------
-- Table structure for sys_manufacturer
-- ----------------------------
DROP TABLE IF EXISTS sys_manufacturer;
CREATE TABLE sys_manufacturer (
  id SERIAL PRIMARY KEY,
  manufacturer_name varchar(255) NULL,
  manufacturer_code varchar(255) NULL,
  artisan_name varchar(255) NULL,
  artisan_phone varchar(20) NULL,
  service_name varchar(255) NULL,
  service_phone varchar(20) NULL,
  manufacturer_desc varchar(255) NULL,
  organ_code varchar(64) NULL,
  create_by varchar(255) NULL,
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(255) NULL,
  update_time timestamp NULL,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_manufacturer IS '厂商表';
COMMENT ON COLUMN sys_manufacturer.id IS 'id';
COMMENT ON COLUMN sys_manufacturer.manufacturer_name IS '厂商名称';
COMMENT ON COLUMN sys_manufacturer.manufacturer_code IS '厂商编码';
COMMENT ON COLUMN sys_manufacturer.artisan_name IS '技术人员姓名';
COMMENT ON COLUMN sys_manufacturer.artisan_phone IS '技术人员手机号';
COMMENT ON COLUMN sys_manufacturer.service_name IS '业务人员姓名';
COMMENT ON COLUMN sys_manufacturer.service_phone IS '业务人员手机号';
COMMENT ON COLUMN sys_manufacturer.manufacturer_desc IS '厂商描述';
COMMENT ON COLUMN sys_manufacturer.organ_code IS '所属机构编码';
COMMENT ON COLUMN sys_manufacturer.create_by IS '创建者';
COMMENT ON COLUMN sys_manufacturer.create_time IS '创建时间';
COMMENT ON COLUMN sys_manufacturer.update_by IS '更新者';
COMMENT ON COLUMN sys_manufacturer.update_time IS '更新时间';
COMMENT ON COLUMN sys_manufacturer.del_flag IS '逻辑删 0-正常 1-删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_sys_manufacturer_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_sys_manufacturer_update
BEFORE UPDATE ON sys_manufacturer
FOR EACH ROW
EXECUTE FUNCTION update_sys_manufacturer_timestamp();

-- ----------------------------
-- Records of sys_manufacturer
-- ----------------------------
INSERT INTO sys_manufacturer VALUES (1, '华为', 'huawei', 'edison', '13511111111', 'tom', '13522222222', '最大的通讯设备供应商', 'F001', 'admin', '2025-03-24 22:33:09', 'admin', '2025-03-24 22:33:09', '0');
INSERT INTO sys_manufacturer VALUES (2, '大疆', 'dajiang', '张三', '13133333333', '李四', '13112341234', '最大无人机供应商', 'F001', 'admin', '2025-03-24 22:33:09', 'admin', '2025-03-24 22:33:09', '0');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
  id SERIAL PRIMARY KEY,
  menu_name varchar(32) NULL,
  permission varchar(32) NULL,
  path varchar(1024) NULL,
  parent_id integer NULL DEFAULT 0,
  icon varchar(500) NULL,
  menu_describe varchar(255) NULL,
  sort integer NULL DEFAULT 1,
  keep_alive char(1) NULL DEFAULT '0',
  type char(1) NULL DEFAULT '0',
  application_code varchar(50) NULL,
  create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  update_time timestamp NULL,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_menu IS '菜单权限表';
COMMENT ON COLUMN sys_menu.id IS '菜单ID';
COMMENT ON COLUMN sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_menu.permission IS '按钮权限唯一标识，和数据字典opt_status一致，暂未使用';
COMMENT ON COLUMN sys_menu.path IS '前端路径';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID，0表示无上级';
COMMENT ON COLUMN sys_menu.icon IS '图标，参见https://3x.antdv.com/components/icon-cn';
COMMENT ON COLUMN sys_menu.menu_describe IS '菜单描述';
COMMENT ON COLUMN sys_menu.sort IS '排序值';
COMMENT ON COLUMN sys_menu.keep_alive IS '是否开启路由缓冲 0-否 1-是';
COMMENT ON COLUMN sys_menu.type IS '类型 0-菜单 1-按钮';
COMMENT ON COLUMN sys_menu.application_code IS '应用编码';
COMMENT ON COLUMN sys_menu.create_time IS '创建时间';
COMMENT ON COLUMN sys_menu.update_time IS '更新时间';
COMMENT ON COLUMN sys_menu.del_flag IS '逻辑删 0-正常 1-删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_sys_menu_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_sys_menu_update
BEFORE UPDATE ON sys_menu
FOR EACH ROW
EXECUTE FUNCTION update_sys_menu_timestamp();

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:09', '0', 'slack-square-outlined', 1, '0', NULL, '基础管理', 0, '', '', 1, '0', '2025-03-24 22:33:09');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:09', '0', 'menu-outlined', 2, '0', NULL, '菜单管理', 1, 'System/Menu', '', 2, '0', '2025-03-24 22:33:09');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:09', '0', 'user-outlined', 3, '0', NULL, '用户管理', 1, 'System/User', '', 3, '0', '2025-03-24 22:33:09');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:09', '0', 'schedule-outlined', 4, '0', NULL, '字典管理', 1, 'System/Dict', '', 4, '0', '2025-03-24 22:33:09');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:09', '0', 'usergroup-add-outlined', 5, '0', NULL, '角色管理', 1, 'System/Role', '', 5, '0', '2025-03-24 22:33:09');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:09', '0', 'group-outlined', 7, '0', NULL, '机构管理', 0, '', '', 4, '0', '2025-03-24 22:33:09');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:09', '0', 'hdd-outlined', 8, '0', NULL, '单位管理', 7, 'Organ/Organ', '', 5, '0', '2025-03-24 22:33:09');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:09', '0', 'apartment-outlined', 9, '0', NULL, '部门管理', 7, 'Organ/Department', '', 6, '0', '2025-03-24 22:33:09');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:09', '0', 'user-add-outlined', 10, '0', NULL, '员工管理', 7, 'Organ/Staff', '', 7, '0', '2025-03-24 22:33:09');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:09', '0', 'bank-outlined', 11, '0', NULL, '资源管理', 0, '', '', 6, '0', '2025-03-24 22:33:09');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:09', '0', 'tag-outlined', 12, '0', NULL, '厂商管理', 11, 'Resource/Manufacturer', '', 7, '0', '2025-03-24 22:33:09');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', 'appstore-outlined', 13, '0', NULL, '应用管理', 11, 'Resource/Application', '', 8, '0', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', 'box-plot-outlined', 14, '0', NULL, '数源管理', 11, 'Resource/Datasource', '', 9, '0', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 15, '0', NULL, '权限管理', 5, NULL, 'role_perm', 10, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 16, '0', NULL, '增加菜单', 2, NULL, 'menu_add', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 17, '0', NULL, '删除菜单', 2, NULL, 'menu_del', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 18, '0', NULL, '修改菜单', 2, NULL, 'menu_edit', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 19, '0', NULL, '查询菜单', 2, NULL, 'menu_query', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 20, '0', NULL, '增加用户', 3, NULL, 'user_add', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 21, '0', NULL, '删除用户', 3, NULL, 'user_del', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 22, '0', NULL, '修改用户', 3, NULL, 'user_edit', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 23, '0', NULL, '查询用户', 3, NULL, 'user_query', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 24, '0', NULL, '增加字典', 4, NULL, 'dict_add', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 25, '0', NULL, '删除字典', 4, NULL, 'dict_del', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 26, '0', NULL, '修改字典', 4, NULL, 'dict_edit', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 27, '0', NULL, '查询字典', 4, NULL, 'dict_query', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 28, '0', NULL, '增加角色', 5, NULL, 'role_add', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 29, '0', NULL, '删除角色', 5, NULL, 'role_del', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 30, '0', NULL, '修改角色', 5, NULL, 'role_edit', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 31, '0', NULL, '查询角色', 5, NULL, 'role_query', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 35, '0', NULL, '查询单位', 8, NULL, 'org_query', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 36, '0', NULL, '增加部门', 9, NULL, 'dept_add', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:10', '0', NULL, 37, '0', NULL, '删除部门', 9, NULL, 'dept_del', 1, '1', '2025-03-24 22:33:10');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 38, '0', NULL, '修改部门', 9, NULL, 'dept_edit', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 39, '0', NULL, '查询部门', 9, NULL, 'dept_query', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 40, '0', NULL, '增加员工', 10, NULL, 'staff_add', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 41, '0', NULL, '删除员工', 10, NULL, 'staff_del', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 42, '0', NULL, '修改员工', 10, NULL, 'staff_edit', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 43, '0', NULL, '查询员工', 10, NULL, 'staff_query', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 44, '0', NULL, '增加厂商', 12, NULL, 'manuf_add', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 45, '0', NULL, '删除厂商', 12, NULL, 'manuf_del', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 46, '0', NULL, '修改厂商', 12, NULL, 'manuf_edit', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 47, '0', NULL, '查询厂商', 12, NULL, 'manuf_query', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 48, '0', NULL, '增加应用', 13, NULL, 'app_add', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 49, '0', NULL, '删除应用', 13, NULL, 'app_del', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 50, '0', NULL, '修改应用', 13, NULL, 'app_edit', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 51, '0', NULL, '查询应用', 13, NULL, 'app_query', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 52, '0', NULL, '增加数源', 14, NULL, 'ds_add', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 53, '0', NULL, '删除数源', 14, NULL, 'ds_del', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 54, '0', NULL, '修改数源', 14, NULL, 'ds_edit', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 55, '0', NULL, '查询数源', 14, NULL, 'ds_query', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 56, '0', NULL, '增加字典条目', 4, NULL, 'dict_item_add', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 57, '0', NULL, '删除字典条目', 4, NULL, 'dict_item_del', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 58, '0', NULL, '修改字典条目', 4, NULL, 'dict_item_edit', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('USR', '2025-03-24 22:33:11', '0', NULL, 59, '0', NULL, '查询字典条目', 4, NULL, 'dict_item_query', 1, '1', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:11', '0', 'tag-outlined', 61, '0', NULL, '类型维护', 60, 'Log/LogType', '', 11, '0', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:11', '0', 'appstore-outlined', 62, '0', NULL, '日志查询', 60, 'Log/LoginLog', '', 12, '0', '2025-03-24 22:33:11');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', 'bank-outlined', 63, '0', NULL, '数据处理', 60, 'Log/DataIntegrity', '', 13, '0', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', 'tag-outlined', 65, '0', NULL, '聊天', 64, 'Agent/chat', '', 12, '0', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', 'appstore-outlined', 66, '0', NULL, 'AI应用管理', 64, 'Aigc/App', '', 13, '0', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', 'bank-outlined', 67, '0', NULL, '模型管理', 64, 'Aigc/model', '', 14, '0', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', 'form-outlined', 64, '0', NULL, '智能体平台', 0, '', '', 6, '0', '2025-05-29 23:33:35.869609');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', 'redo-outlined', 68, '0', NULL, '向量库管理', 64, 'Aigc/EmbedStore', '', 15, '0', '2025-05-29 23:55:13.309347');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', 'underline-outlined', 69, '0', NULL, '知识库管理', 64, 'Aigc/knowledge', '', 16, '0', '2025-05-29 23:55:30.526505');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', 'zoom-out-outlined', 70, '0', NULL, '消息查询', 64, 'Aigc/message', '', 17, '0', '2025-05-29 23:55:44.274501');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', 'dingding-outlined', 71, '0', NULL, '账单查询', 64, 'Aigc/order', '', 18, '0', '2025-05-29 23:55:56.101778');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:11', '0', 'switcher-outlined', 60, '0', NULL, '日志管理', 0, '', '', 101, '0', '2025-05-29 23:58:24.316637');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 72, '0', NULL, '增加日志类型', 61, NULL, 'log_type_add', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 73, '0', NULL, '删除日志类型', 61, NULL, 'log_type_del', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 74, '0', NULL, '修改日志类型', 61, NULL, 'log_type_edit', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 75, '0', NULL, '查询日志类型', 61, NULL, 'log_type_query', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 76, '0', NULL, '完成对话操作', 65, NULL, 'chat:completions', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 77, '0', NULL, '清除聊天历史', 65, NULL, 'chat:messages:clean', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 78, '0', NULL, '删除日志', 62, NULL, 'log_del', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 79, '0', NULL, '查询日志', 62, NULL, 'log_query', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 80, '0', NULL, '删除处理日志', 63, NULL, 'dict_item_del', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 81, '0', NULL, '查询处理日志', 63, NULL, 'dict_item_query', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 82, '0', NULL, '增加智能体应用', 66, NULL, 'aigc:app:add', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 83, '0', NULL, '删除智能体应用', 66, NULL, 'aigc:app:delete', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 84, '0', NULL, '修改智能体应用', 66, NULL, 'aigc:app:update', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 85, '0', NULL, '查询智能体应用', 66, NULL, 'chat-docs:view', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 86, '0', NULL, 'Chat权限', 66, NULL, 'chat:completions', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:12', '0', NULL, 87, '0', NULL, '文本向量化', 66, NULL, 'aigc:embedding:text', 1, '1', '2025-03-24 22:33:12');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 88, '0', NULL, '文档向量化', 66, NULL, 'aigc:embedding:docs', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 89, '0', NULL, '增加大语言模型', 67, NULL, 'aigc:model:add', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 90, '0', NULL, '删除大语言模型', 67, NULL, 'aigc:model:delete', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 91, '0', NULL, '修改大语言模型', 67, NULL, 'aigc:model:update', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 92, '0', NULL, '查询大语言模型', 67, NULL, 'aigc:model:query', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 93, '0', NULL, '增加向量库', 68, NULL, 'aigc:embed-store:add', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 94, '0', NULL, '删除向量库', 68, NULL, 'aigc:embed-store:delete', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 95, '0', NULL, '修改向量库', 68, NULL, 'aigc:embed-store:update', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 96, '0', NULL, '查询向量库', 68, NULL, 'aigc:embed-store:query', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 97, '0', NULL, '增加知识库', 69, NULL, 'aigc:knowledge:add', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 98, '0', NULL, '删除知识库', 69, NULL, 'aigc:knowledge:delete', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 99, '0', NULL, '修改知识库', 69, NULL, 'aigc:knowledge:update', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 100, '0', NULL, '查询知识库', 69, NULL, 'aigc:knowledge:query', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 101, '0', NULL, '增加文档', 125, NULL, 'aigc:docs:add', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 102, '0', NULL, '删除文档', 125, NULL, 'aigc:docs:delete', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 103, '0', NULL, '修改文档', 125, NULL, 'aigc:docs:update', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 104, '0', NULL, '查询文档', 125, NULL, 'aigc:docs:query', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 105, '0', NULL, '删除对话数据', 70, NULL, 'aigc:message:delete', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 106, '0', NULL, '查询对话数据', 70, NULL, 'aigc:message:query', 1, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', 'key-outlined', 108, '0', 'AIGC 应用 API 管理', '渠道管理', 64, 'Aigc/AppApi', '', 108, '0', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 109, '0', NULL, '新增渠道', 108, NULL, 'aigc:app-api:add', 109, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 110, '0', NULL, '删除渠道', 108, NULL, 'aigc:app-api:delete', 110, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 111, '0', NULL, '修改渠道', 108, NULL, 'aigc:app-api:update', 111, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 112, '0', NULL, '查询渠道', 108, NULL, 'aigc:app-api:query', 112, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', 'edit-outlined', 113, '0', 'AIGC 应用提示词管理', '提示语管理', 64, 'Aigc/Prompt', '', 113, '0', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 114, '0', NULL, '新增提示语', 113, NULL, 'aigc:prompt:add', 114, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 115, '0', NULL, '删除提示语', 113, NULL, 'aigc:prompt:delete', 115, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 116, '0', NULL, '修改提示语', 113, NULL, 'aigc:prompt:update', 116, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 117, '0', NULL, '查询提示语', 113, NULL, 'aigc:prompt:query', 117, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', 'message-outlined', 118, '0', 'AIGC 对话管理', '对话管理', 64, 'Aigc/Conversation', '', 118, '0', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 119, '0', NULL, '新增对话', 118, NULL, 'aigc:conversation:add', 119, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 120, '0', NULL, '删除对话', 118, NULL, 'aigc:conversation:delete', 120, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 121, '0', NULL, '修改对话', 118, NULL, 'aigc:conversation:update', 121, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 122, '0', NULL, '查询对话', 118, NULL, 'aigc:conversation:query', 122, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 123, '0', NULL, '新增消息', 70, NULL, 'aigc:message:add', 123, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 124, '0', NULL, '修改消息', 70, NULL, 'aigc:message:update', 124, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 126, '0', NULL, '新增文件', 125, NULL, 'aigc:oss:add', 126, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 127, '0', NULL, '删除文件', 125, NULL, 'aigc:oss:delete', 127, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 128, '0', NULL, '修改文件', 125, NULL, 'aigc:oss:update', 128, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 129, '0', NULL, '查询文件', 125, NULL, 'aigc:oss:query', 129, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 131, '0', NULL, '新增文档切片', 130, NULL, 'aigc:docs:slice:add', 131, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 132, '0', NULL, '删除文档切片', 130, NULL, 'aigc:docs:slice:delete', 132, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 133, '0', NULL, '修改文档切片', 130, NULL, 'aigc:docs:slice:update', 133, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', NULL, 134, '0', NULL, '查询文档切片', 130, NULL, 'aigc:docs:slice:query', 134, '1', '2025-03-24 22:33:13');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', 'yahoo-outlined', 125, '0', 'AIGC 文档管理', '文档管理', 64, 'Aigc/Doc', '', 125, '0', '2025-05-29 23:56:05.683365');
insert into "sys_menu" ("application_code", "create_time", "del_flag", "icon", "id", "keep_alive", "menu_describe", "menu_name", "parent_id", "path", "permission", "sort", "type", "update_time") values ('ERP', '2025-03-24 22:33:13', '0', 'alibaba-outlined', 130, '0', 'AIGC 文档切片管理', '文档切片', 64, 'Aigc/Slice', '', 130, '0', '2025-05-29 23:56:13.874159');

-- ----------------------------
-- Table structure for sys_oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS sys_oauth_client_details;
CREATE TABLE sys_oauth_client_details (
  id SERIAL PRIMARY KEY,
  client_id varchar(32) NOT NULL,
  resource_ids varchar(256) NULL,
  client_secret varchar(256) NULL,
  scope varchar(256) NULL,
  authorized_grant_types varchar(256) NULL,
  web_server_redirect_uri varchar(256) NULL,
  authorities varchar(256) NULL,
  access_token_validity integer NULL,
  refresh_token_validity integer NULL,
  additional_information text NULL,
  autoapprove varchar(256) NULL,
  organ_code varchar(64) NULL,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_oauth_client_details IS '终端信息表';
COMMENT ON COLUMN sys_oauth_client_details.id IS 'ID';
COMMENT ON COLUMN sys_oauth_client_details.client_id IS 'Oauth2对应的clientId';
COMMENT ON COLUMN sys_oauth_client_details.resource_ids IS '微服务id';
COMMENT ON COLUMN sys_oauth_client_details.client_secret IS '指定客户端(client)的访问密匙';
COMMENT ON COLUMN sys_oauth_client_details.scope IS '指定客户端申请的权限范围';
COMMENT ON COLUMN sys_oauth_client_details.authorized_grant_types IS '指定客户端支持的grant_type';
COMMENT ON COLUMN sys_oauth_client_details.web_server_redirect_uri IS '客户端的重定向URI,可为空';
COMMENT ON COLUMN sys_oauth_client_details.authorities IS '指定客户端所拥有的Spring Security的权限值';
COMMENT ON COLUMN sys_oauth_client_details.access_token_validity IS '设定客户端的access_token的有效时间值';
COMMENT ON COLUMN sys_oauth_client_details.refresh_token_validity IS '设定客户端的refresh_token的有效时间值';
COMMENT ON COLUMN sys_oauth_client_details.additional_information IS 'JSON扩展：enc_flag加密标志，captcha_flag验证码标志';
COMMENT ON COLUMN sys_oauth_client_details.autoapprove IS '是否开启验证码';
COMMENT ON COLUMN sys_oauth_client_details.organ_code IS '所属机构编码';
COMMENT ON COLUMN sys_oauth_client_details.del_flag IS '逻辑删 0-正常 1-删除';

-- ----------------------------
-- Records of sys_oauth_client_details
-- ----------------------------
INSERT INTO sys_oauth_client_details VALUES (1, 'feng', NULL, 'Slxx@2025', 'server', 'password,refresh_token,authorization_code,client_credentials', NULL, NULL, 86400, 86400, '{ "enc_flag":"1","captcha_flag":"1"}', 'true', 'F001', '0');
INSERT INTO sys_oauth_client_details VALUES (2, 'test', NULL, 'Slxx@2025', 'server', 'password,refresh_token', NULL, NULL, 86400, 86400, '{ "enc_flag":"0","captcha_flag":"0"}', 'true', 'F001', '0');

-- ----------------------------
-- Table structure for sys_organ
-- ----------------------------
DROP TABLE IF EXISTS sys_organ;
CREATE TABLE sys_organ (
  id SERIAL PRIMARY KEY,
  organ_name varchar(100) NULL,
  organ_code varchar(64) NULL,
  organ_type char(1) NULL DEFAULT '0',
  organ_alias_name varchar(100) NULL,
  parent_id integer NULL DEFAULT 0,
  sort integer NULL DEFAULT 0,
  organ_level varchar(50) NULL,
  organ_grade varchar(50) NULL,
  organ_category_code varchar(100) NULL,
  organ_category_name varchar(100) NULL,
  economic_type_code varchar(100) NULL,
  economic_type_name varchar(100) NULL,
  manage_class_code varchar(100) NULL,
  manage_class_name varchar(100) NULL,
  address varchar(500) NULL,
  addr_province varchar(50) NULL,
  addr_city varchar(50) NULL,
  addr_county varchar(50) NULL,
  addr_town varchar(50) NULL,
  addr_village varchar(50) NULL,
  addr_house_no varchar(50) NULL,
  administrative_division varchar(100) NULL,
  zip_code varchar(50) NULL,
  telephone varchar(20) NULL,
  email varchar(50) NULL,
  website varchar(100) NULL,
  establish_date varchar(20) NULL,
  organ_introduction text NULL,
  traffic_route text NULL,
  approval_authority varchar(100) NULL,
  register_no varchar(50) NULL,
  legal_person varchar(50) NULL,
  principal_name varchar(50) NULL,
  principal_telecom varchar(50) NULL,
  license_start_date date NULL,
  license_end_date date NULL,
  subject_services text NULL,
  major_subject_services text NULL,
  is_supervisory char(1) NULL,
  supervisory_property_code varchar(100) NULL,
  supervisory_property_name varchar(100) NULL,
  supervisory_subordination_code varchar(100) NULL,
  supervisory_subordination_name varchar(100) NULL,
  administrative_level_code varchar(100) NULL,
  administrative_level_name varchar(100) NULL,
  supervisory_dept_code varchar(50) NULL,
  supervisory_dept_name varchar(50) NULL,
  branching_quantity integer NULL,
  is_national_autonomy char(1) NULL,
  branch_org_level_code varchar(100) NULL,
  branch_org_level_name varchar(100) NULL,
  staff_quantity integer NULL,
  daily_visits integer NULL,
  product_quantity integer NULL,
  status char(1) NULL DEFAULT '0',
  organ_pictures text NULL,
  license_pictures text NULL,
  default_password varchar(255) NULL,
  create_by varchar(64) NULL,
  create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(64) NULL,
  update_time timestamp NULL,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_organ IS '机构表';
COMMENT ON COLUMN sys_organ.id IS 'id';
COMMENT ON COLUMN sys_organ.organ_name IS '机构名称';
COMMENT ON COLUMN sys_organ.organ_code IS '机构编码';
COMMENT ON COLUMN sys_organ.organ_type IS '机构类型，关联数据字典organ_type';
COMMENT ON COLUMN sys_organ.organ_alias_name IS '第二名称（简称）';
COMMENT ON COLUMN sys_organ.parent_id IS '上级机构';
COMMENT ON COLUMN sys_organ.sort IS '排序';
COMMENT ON COLUMN sys_organ.organ_level IS '级别，备用';
COMMENT ON COLUMN sys_organ.organ_grade IS '等次，备用';
COMMENT ON COLUMN sys_organ.organ_category_code IS '机构类别编码，备用';
COMMENT ON COLUMN sys_organ.organ_category_name IS '机构类别名称，备用';
COMMENT ON COLUMN sys_organ.economic_type_code IS '经济类型编码，备用';
COMMENT ON COLUMN sys_organ.economic_type_name IS '经济类型名称，备用';
COMMENT ON COLUMN sys_organ.manage_class_code IS '分类管理代码，备用';
COMMENT ON COLUMN sys_organ.manage_class_name IS '分类管理名称，备用';
COMMENT ON COLUMN sys_organ.address IS '地址全路径';
COMMENT ON COLUMN sys_organ.addr_province IS '地址-省';
COMMENT ON COLUMN sys_organ.addr_city IS '地址-市';
COMMENT ON COLUMN sys_organ.addr_county IS '地址-县';
COMMENT ON COLUMN sys_organ.addr_town IS '地址-乡';
COMMENT ON COLUMN sys_organ.addr_village IS '地址-村';
COMMENT ON COLUMN sys_organ.addr_house_no IS '地址-门牌号';
COMMENT ON COLUMN sys_organ.administrative_division IS '行政区划';
COMMENT ON COLUMN sys_organ.zip_code IS '邮编';
COMMENT ON COLUMN sys_organ.telephone IS '电话';
COMMENT ON COLUMN sys_organ.email IS '邮箱';
COMMENT ON COLUMN sys_organ.website IS '网址';
COMMENT ON COLUMN sys_organ.establish_date IS '成立日期';
COMMENT ON COLUMN sys_organ.organ_introduction IS '机构介绍';
COMMENT ON COLUMN sys_organ.traffic_route IS '交通路线';
COMMENT ON COLUMN sys_organ.approval_authority IS '审批机关';
COMMENT ON COLUMN sys_organ.register_no IS '登记号';
COMMENT ON COLUMN sys_organ.legal_person IS '法人';
COMMENT ON COLUMN sys_organ.principal_name IS '主要负责人';
COMMENT ON COLUMN sys_organ.principal_telecom IS '负责人电话';
COMMENT ON COLUMN sys_organ.license_start_date IS '执业许可开始日期';
COMMENT ON COLUMN sys_organ.license_end_date IS '执业许可结束日期';
COMMENT ON COLUMN sys_organ.subject_services IS '科目';
COMMENT ON COLUMN sys_organ.major_subject_services IS '重点科目';
COMMENT ON COLUMN sys_organ.is_supervisory IS '是否监督机构';
COMMENT ON COLUMN sys_organ.supervisory_property_code IS '监督机构性质代码';
COMMENT ON COLUMN sys_organ.supervisory_property_name IS '监督机构性质名称';
COMMENT ON COLUMN sys_organ.supervisory_subordination_code IS '监督机构隶属关系代码';
COMMENT ON COLUMN sys_organ.supervisory_subordination_name IS '监督机构隶属名称';
COMMENT ON COLUMN sys_organ.administrative_level_code IS '监督机构行政级别代码';
COMMENT ON COLUMN sys_organ.administrative_level_name IS '监督机构行政级别名称';
COMMENT ON COLUMN sys_organ.supervisory_dept_code IS '监督机构部门编码';
COMMENT ON COLUMN sys_organ.supervisory_dept_name IS '监督机构部门名称';
COMMENT ON COLUMN sys_organ.branching_quantity IS '派出（分支）机构数量';
COMMENT ON COLUMN sys_organ.is_national_autonomy IS '机构所在地民族自治地方标志';
COMMENT ON COLUMN sys_organ.branch_org_level_code IS '分支机构级别代码';
COMMENT ON COLUMN sys_organ.branch_org_level_name IS '分支机构级别名称';
COMMENT ON COLUMN sys_organ.staff_quantity IS '员工数';
COMMENT ON COLUMN sys_organ.daily_visits IS '业务量';
COMMENT ON COLUMN sys_organ.product_quantity IS '产品数';
COMMENT ON COLUMN sys_organ.status IS '状态';
COMMENT ON COLUMN sys_organ.organ_pictures IS '机构图片';
COMMENT ON COLUMN sys_organ.license_pictures IS '资质图片';
COMMENT ON COLUMN sys_organ.default_password IS '默认密码';
COMMENT ON COLUMN sys_organ.create_by IS '创建者';
COMMENT ON COLUMN sys_organ.create_time IS '创建时间';
COMMENT ON COLUMN sys_organ.update_by IS '更新者';
COMMENT ON COLUMN sys_organ.update_time IS '更新时间';
COMMENT ON COLUMN sys_organ.del_flag IS '逻辑删 0-正常 1-删除';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_sys_organ_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_sys_organ_update
BEFORE UPDATE ON sys_organ
FOR EACH ROW
EXECUTE FUNCTION update_sys_organ_timestamp();

-- ----------------------------
-- Records of sys_organ
-- ----------------------------
INSERT INTO sys_organ VALUES (1, '大疆公司', 'F001', '1', 'DJ', 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '弹子石新街', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', NULL, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', '', NULL, '', NULL, '', NULL, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', NULL, '123456', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');

-- ----------------------------
-- Table structure for sys_public_param
-- ----------------------------
DROP TABLE IF EXISTS sys_public_param;
CREATE TABLE sys_public_param (
  public_id BIGSERIAL PRIMARY KEY,
  public_name varchar(128) NULL,
  public_key varchar(128) NULL,
  public_value varchar(128) NULL,
  status char(1) NULL DEFAULT '0',
  validate_code varchar(64) NULL,
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time timestamp NULL,
  public_type char(1) NULL DEFAULT '0',
  system char(1) NULL DEFAULT '0',
  organ_code varchar(64) NULL,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_public_param IS '公共参数配置表';
COMMENT ON COLUMN sys_public_param.public_id IS '编号';
COMMENT ON COLUMN sys_public_param.public_name IS '参数名称';
COMMENT ON COLUMN sys_public_param.public_key IS '参数键';
COMMENT ON COLUMN sys_public_param.public_value IS '参数值';
COMMENT ON COLUMN sys_public_param.status IS '状态';
COMMENT ON COLUMN sys_public_param.validate_code IS '验证码';
COMMENT ON COLUMN sys_public_param.create_time IS '创建时间';
COMMENT ON COLUMN sys_public_param.update_time IS '修改时间';
COMMENT ON COLUMN sys_public_param.public_type IS '参数类型';
COMMENT ON COLUMN sys_public_param.system IS '系统参数';
COMMENT ON COLUMN sys_public_param.organ_code IS '所属机构编码';
COMMENT ON COLUMN sys_public_param.del_flag IS '删除标志';

-- Create trigger for update_time
CREATE OR REPLACE FUNCTION update_sys_public_param_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.update_time = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_sys_public_param_update
BEFORE UPDATE ON sys_public_param
FOR EACH ROW
EXECUTE FUNCTION update_sys_public_param_timestamp();

DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  id serial PRIMARY KEY NOT NULL,
  role_name varchar(64) NULL,
  role_code varchar(64) NULL,
  role_desc varchar(255) NULL,
  ds_type int NULL DEFAULT 0,
  ds_scope varchar(255) NULL,
  type char(1) NULL DEFAULT '1',
  organ_code varchar(64) NULL,
  role_start_time timestamp NULL,
  role_end_time timestamp NULL,
  is_default char(1) NULL DEFAULT '0',
  create_by varchar(255) NULL,
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(255) NULL,
  update_time timestamp NULL,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_role IS '系统角色表';
COMMENT ON COLUMN sys_role.id IS 'Id';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_code IS '角色编码';
COMMENT ON COLUMN sys_role.role_desc IS '角色描述';
COMMENT ON COLUMN sys_role.ds_type IS '数据权限类型：0-全部 1-自定义';
COMMENT ON COLUMN sys_role.ds_scope IS '数据权限作用范围：部门id逗号隔开';
COMMENT ON COLUMN sys_role.type IS '角色类型:0-系统角色 1-自定义角色 2-项目角色';
COMMENT ON COLUMN sys_role.organ_code IS '所属机构编码';
COMMENT ON COLUMN sys_role.role_start_time IS '角色有效开始时间';
COMMENT ON COLUMN sys_role.role_end_time IS '角色有效结束时间';
COMMENT ON COLUMN sys_role.is_default IS '是否内置：0-否 1-是';
COMMENT ON COLUMN sys_role.create_by IS '创建者';
COMMENT ON COLUMN sys_role.create_time IS '创建时间';
COMMENT ON COLUMN sys_role.update_by IS '更新者';
COMMENT ON COLUMN sys_role.update_time IS '更新时间';
COMMENT ON COLUMN sys_role.del_flag IS '逻辑删 0-正常 1-删除';

CREATE INDEX idx_role_code ON sys_role(role_code);

-- Records of sys_role
INSERT INTO sys_role VALUES (1, '超级管理员', 'admin', '超级管理员（内置）', 0, NULL, '0', 'F001', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO sys_role VALUES (2, '普通职员', 'default', '普通职员（内置）', 0, NULL, '0', 'F001', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO sys_role VALUES (3, '组长', 'leader', '项目领导（内置）', 0, NULL, '0', 'F001', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO sys_role VALUES (4, '成员', 'member', '项目成员（内置）', 0, NULL, '0', 'F001', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO sys_role VALUES (5, '助手', 'assistant', 'AI助手（内置）', 0, NULL, '0', 'F001', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO sys_role VALUES (6, '系统', 'system', 'AI系统（内置）', 0, NULL, '0', 'F001', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO sys_role VALUES (7, '用户', 'user', 'AI用户（内置）', 0, NULL, '0', 'F001', NULL, NULL, '1', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO sys_role VALUES (8, '工人', 'worker', '产线', 0, NULL, '1', 'F001', NULL, NULL, '0', 'admin', '2025-03-24 22:33:14', 'admin', '2025-03-24 22:33:14', '0');
INSERT INTO sys_role VALUES (9, '销售员', 'sale_role', '销售', 0, NULL, '1', 'F001', NULL, NULL, '0', 'admin', '2025-03-24 22:33:15', 'admin', '2025-03-24 22:33:15', '0');

-- Table structure for sys_role_menu
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
  role_id int NOT NULL,
  menu_id int NOT NULL,
  PRIMARY KEY (role_id, menu_id)
);

COMMENT ON TABLE sys_role_menu IS '角色菜单表';

-- Records of sys_role_menu
INSERT INTO sys_role_menu VALUES (2, 1);
INSERT INTO sys_role_menu VALUES (2, 2);
INSERT INTO sys_role_menu VALUES (2, 3);
INSERT INTO sys_role_menu VALUES (2, 4);
INSERT INTO sys_role_menu VALUES (2, 5);
INSERT INTO sys_role_menu VALUES (2, 7);
INSERT INTO sys_role_menu VALUES (2, 8);
INSERT INTO sys_role_menu VALUES (2, 9);
INSERT INTO sys_role_menu VALUES (2, 10);
INSERT INTO sys_role_menu VALUES (2, 11);
INSERT INTO sys_role_menu VALUES (2, 12);
INSERT INTO sys_role_menu VALUES (2, 13);
INSERT INTO sys_role_menu VALUES (2, 14);
--INSERT INTO sys_role_menu VALUES (2, 15);
INSERT INTO sys_role_menu VALUES (2, 16);
INSERT INTO sys_role_menu VALUES (2, 17);
INSERT INTO sys_role_menu VALUES (2, 18);
INSERT INTO sys_role_menu VALUES (2, 19);
INSERT INTO sys_role_menu VALUES (2, 20);
INSERT INTO sys_role_menu VALUES (2, 21);
INSERT INTO sys_role_menu VALUES (2, 22);
INSERT INTO sys_role_menu VALUES (2, 23);
INSERT INTO sys_role_menu VALUES (2, 24);
INSERT INTO sys_role_menu VALUES (2, 25);
INSERT INTO sys_role_menu VALUES (2, 26);
INSERT INTO sys_role_menu VALUES (2, 27);
INSERT INTO sys_role_menu VALUES (2, 28);
INSERT INTO sys_role_menu VALUES (2, 29);
INSERT INTO sys_role_menu VALUES (2, 30);
INSERT INTO sys_role_menu VALUES (2, 31);
INSERT INTO sys_role_menu VALUES (2, 35);
INSERT INTO sys_role_menu VALUES (2, 36);
INSERT INTO sys_role_menu VALUES (2, 37);
INSERT INTO sys_role_menu VALUES (2, 38);
INSERT INTO sys_role_menu VALUES (2, 39);
INSERT INTO sys_role_menu VALUES (2, 40);
INSERT INTO sys_role_menu VALUES (2, 41);
INSERT INTO sys_role_menu VALUES (2, 42);
INSERT INTO sys_role_menu VALUES (2, 43);
INSERT INTO sys_role_menu VALUES (2, 44);
INSERT INTO sys_role_menu VALUES (2, 45);
INSERT INTO sys_role_menu VALUES (2, 46);
INSERT INTO sys_role_menu VALUES (2, 47);
INSERT INTO sys_role_menu VALUES (2, 48);
INSERT INTO sys_role_menu VALUES (2, 49);
INSERT INTO sys_role_menu VALUES (2, 50);
INSERT INTO sys_role_menu VALUES (2, 51);
INSERT INTO sys_role_menu VALUES (2, 52);
INSERT INTO sys_role_menu VALUES (2, 53);
INSERT INTO sys_role_menu VALUES (2, 54);
INSERT INTO sys_role_menu VALUES (2, 55);
INSERT INTO sys_role_menu VALUES (2, 56);
INSERT INTO sys_role_menu VALUES (2, 57);
INSERT INTO sys_role_menu VALUES (2, 58);
INSERT INTO sys_role_menu VALUES (2, 59);
INSERT INTO sys_role_menu VALUES (2, 60);
INSERT INTO sys_role_menu VALUES (2, 61);
INSERT INTO sys_role_menu VALUES (2, 62);
INSERT INTO sys_role_menu VALUES (2, 63);
INSERT INTO sys_role_menu VALUES (2, 64);
INSERT INTO sys_role_menu VALUES (2, 65);
INSERT INTO sys_role_menu VALUES (2, 66);
INSERT INTO sys_role_menu VALUES (2, 67);
INSERT INTO sys_role_menu VALUES (2, 68);
INSERT INTO sys_role_menu VALUES (2, 69);
INSERT INTO sys_role_menu VALUES (2, 70);
INSERT INTO sys_role_menu VALUES (2, 71);
INSERT INTO sys_role_menu VALUES (2, 72);
INSERT INTO sys_role_menu VALUES (2, 73);
INSERT INTO sys_role_menu VALUES (2, 74);
INSERT INTO sys_role_menu VALUES (2, 75);
INSERT INTO sys_role_menu VALUES (2, 76);
INSERT INTO sys_role_menu VALUES (2, 77);
INSERT INTO sys_role_menu VALUES (2, 78);
INSERT INTO sys_role_menu VALUES (2, 79);
INSERT INTO sys_role_menu VALUES (2, 80);
INSERT INTO sys_role_menu VALUES (2, 81);
INSERT INTO sys_role_menu VALUES (2, 82);
INSERT INTO sys_role_menu VALUES (2, 83);
INSERT INTO sys_role_menu VALUES (2, 84);
INSERT INTO sys_role_menu VALUES (2, 85);
INSERT INTO sys_role_menu VALUES (2, 86);
INSERT INTO sys_role_menu VALUES (2, 87);
INSERT INTO sys_role_menu VALUES (2, 88);
INSERT INTO sys_role_menu VALUES (2, 89);
INSERT INTO sys_role_menu VALUES (2, 90);
INSERT INTO sys_role_menu VALUES (2, 91);
INSERT INTO sys_role_menu VALUES (2, 92);
INSERT INTO sys_role_menu VALUES (2, 93);
INSERT INTO sys_role_menu VALUES (2, 94);
INSERT INTO sys_role_menu VALUES (2, 95);
INSERT INTO sys_role_menu VALUES (2, 96);
INSERT INTO sys_role_menu VALUES (2, 97);
INSERT INTO sys_role_menu VALUES (2, 98);
INSERT INTO sys_role_menu VALUES (2, 99);
INSERT INTO sys_role_menu VALUES (2, 100);
INSERT INTO sys_role_menu VALUES (2, 101);
INSERT INTO sys_role_menu VALUES (2, 102);
INSERT INTO sys_role_menu VALUES (2, 103);
INSERT INTO sys_role_menu VALUES (2, 104);
INSERT INTO sys_role_menu VALUES (2, 105);
INSERT INTO sys_role_menu VALUES (2, 106);
INSERT INTO sys_role_menu VALUES (2, 107);
INSERT INTO sys_role_menu VALUES (2, 108);
INSERT INTO sys_role_menu VALUES (2, 109);
INSERT INTO sys_role_menu VALUES (2, 110);
INSERT INTO sys_role_menu VALUES (2, 111);
INSERT INTO sys_role_menu VALUES (2, 112);
INSERT INTO sys_role_menu VALUES (2, 113);
INSERT INTO sys_role_menu VALUES (2, 114);
INSERT INTO sys_role_menu VALUES (2, 115);
INSERT INTO sys_role_menu VALUES (2, 116);
INSERT INTO sys_role_menu VALUES (2, 117);
INSERT INTO sys_role_menu VALUES (2, 118);
INSERT INTO sys_role_menu VALUES (2, 119);
INSERT INTO sys_role_menu VALUES (2, 120);
INSERT INTO sys_role_menu VALUES (2, 121);
INSERT INTO sys_role_menu VALUES (2, 122);
INSERT INTO sys_role_menu VALUES (2, 123);
INSERT INTO sys_role_menu VALUES (2, 124);
INSERT INTO sys_role_menu VALUES (2, 125);
INSERT INTO sys_role_menu VALUES (2, 126);
INSERT INTO sys_role_menu VALUES (2, 127);
INSERT INTO sys_role_menu VALUES (2, 128);
INSERT INTO sys_role_menu VALUES (2, 129);
INSERT INTO sys_role_menu VALUES (2, 130);
INSERT INTO sys_role_menu VALUES (2, 131);
INSERT INTO sys_role_menu VALUES (2, 132);
INSERT INTO sys_role_menu VALUES (2, 133);
INSERT INTO sys_role_menu VALUES (2, 134);
INSERT INTO sys_role_menu VALUES (2, 135);
INSERT INTO sys_role_menu VALUES (2, 136);
INSERT INTO sys_role_menu VALUES (2, 137);
INSERT INTO sys_role_menu VALUES (2, 138);
INSERT INTO sys_role_menu VALUES (2, 139);
INSERT INTO sys_role_menu VALUES (2, 140);
INSERT INTO sys_role_menu VALUES (2, 141);
INSERT INTO sys_role_menu VALUES (2, 142);
INSERT INTO sys_role_menu VALUES (2, 143);
INSERT INTO sys_role_menu VALUES (2, 144);
INSERT INTO sys_role_menu VALUES (2, 145);
INSERT INTO sys_role_menu VALUES (2, 146);
INSERT INTO sys_role_menu VALUES (2, 147);
INSERT INTO sys_role_menu VALUES (2, 148);
INSERT INTO sys_role_menu VALUES (2, 149);
INSERT INTO sys_role_menu VALUES (2, 150);
INSERT INTO sys_role_menu VALUES (2, 151);
INSERT INTO sys_role_menu VALUES (2, 152);
INSERT INTO sys_role_menu VALUES (2, 153);
INSERT INTO sys_role_menu VALUES (2, 154);
INSERT INTO sys_role_menu VALUES (2, 155);
INSERT INTO sys_role_menu VALUES (2, 156);
INSERT INTO sys_role_menu VALUES (2, 157);
INSERT INTO sys_role_menu VALUES (2, 158);
INSERT INTO sys_role_menu VALUES (2, 159);
INSERT INTO sys_role_menu VALUES (2, 160);
INSERT INTO sys_role_menu VALUES (2, 161);
INSERT INTO sys_role_menu VALUES (2, 162);
INSERT INTO sys_role_menu VALUES (2, 163);
INSERT INTO sys_role_menu VALUES (2, 164);
INSERT INTO sys_role_menu VALUES (2, 165);
INSERT INTO sys_role_menu VALUES (2, 166);
INSERT INTO sys_role_menu VALUES (2, 167);
INSERT INTO sys_role_menu VALUES (2, 168);
INSERT INTO sys_role_menu VALUES (2, 169);
INSERT INTO sys_role_menu VALUES (2, 170);
INSERT INTO sys_role_menu VALUES (2, 171);
INSERT INTO sys_role_menu VALUES (2, 172);
INSERT INTO sys_role_menu VALUES (2, 173);
INSERT INTO sys_role_menu VALUES (2, 174);
INSERT INTO sys_role_menu VALUES (2, 175);
INSERT INTO sys_role_menu VALUES (2, 176);
INSERT INTO sys_role_menu VALUES (2, 177);
INSERT INTO sys_role_menu VALUES (2, 178);
INSERT INTO sys_role_menu VALUES (2, 179);
INSERT INTO sys_role_menu VALUES (2, 180);
INSERT INTO sys_role_menu VALUES (2, 181);
INSERT INTO sys_role_menu VALUES (2, 182);
INSERT INTO sys_role_menu VALUES (2, 183);
INSERT INTO sys_role_menu VALUES (2, 184);
INSERT INTO sys_role_menu VALUES (2, 185);
INSERT INTO sys_role_menu VALUES (2, 186);
INSERT INTO sys_role_menu VALUES (2, 187);
INSERT INTO sys_role_menu VALUES (2, 188);
INSERT INTO sys_role_menu VALUES (2, 189);


-- Table structure for sys_staff
DROP TABLE IF EXISTS sys_staff;
CREATE TABLE sys_staff (
  id serial PRIMARY KEY NOT NULL,
  organ_code varchar(64) NULL,
  dept_id int NULL,
  staff_no varchar(200) NULL,
  staff_name varchar(200) NULL,
  nationality_code varchar(200) NULL,
  nationality_name varchar(200) NULL,
  nation_code varchar(200) NULL,
  nation_name varchar(200) NULL,
  identification_no varchar(200) NULL,
  gender_code varchar(200) NULL,
  gender_name varchar(200) NULL,
  birthdate date NULL,
  telephone varchar(200) NULL,
  marital_status_code varchar(200) NULL,
  marital_status_name varchar(200) NULL,
  native_place varchar(200) NULL,
  politics_status_code varchar(200) NULL,
  politics_status_name varchar(200) NULL,
  addr_province varchar(255) NULL,
  addr_city varchar(255) NULL,
  addr_county varchar(255) NULL,
  addr_town varchar(255) NULL,
  addr_village varchar(255) NULL,
  addr_house_no int NULL,
  address varchar(500) NULL,
  zip_code varchar(255) NULL,
  education_level_code varchar(255) NULL,
  education_level_name varchar(255) NULL,
  degree_code varchar(255) NULL,
  degree_name varchar(255) NULL,
  subject_code varchar(255) NULL,
  subject_name varchar(255) NULL,
  graduate_school_name varchar(255) NULL,
  work_begin_date date NULL,
  job_category varchar(255) NULL,
  technical_qualifications_code varchar(255) NULL,
  technical_qualifications_name varchar(255) NULL,
  technical_position_category_code varchar(255) NULL,
  technical_position_category_name varchar(255) NULL,
  management_position_code varchar(255) NULL,
  management_position_name varchar(255) NULL,
  administration_level_code varchar(255) NULL,
  administration_level_name varchar(255) NULL,
  title_code varchar(255) NULL,
  title_name varchar(255) NULL,
  title_level_code varchar(255) NULL,
  title_level_name varchar(255) NULL,
  is_organizational varchar(255) NULL,
  position_name varchar(255) NULL,
  staff_category_code varchar(255) NULL,
  staff_category_name varchar(255) NULL,
  active_status_code varchar(255) NULL,
  active_status_name varchar(255) NULL,
  qualification_certificate_no varchar(100) NULL,
  practising_certificate_no varchar(100) NULL,
  expertise_field varchar(255) NULL,
  detailed_introduction varchar(255) NULL,
  is_general_staff varchar(255) NULL,
  is_country_staff varchar(255) NULL,
  is_supervisory varchar(255) NULL,
  supervisory_authorized_category_code varchar(255) NULL,
  supervisory_authorized_category_name varchar(255) NULL,
  supervisory_employee_category_code varchar(255) NULL,
  supervisory_employee_category_name varchar(255) NULL,
  supervisory_practice_scope_code varchar(255) NULL,
  supervisory_practice_scope_name varchar(255) NULL,
  photograph varchar(255) NULL,
  electronic_signature varchar(255) NULL,
  qualification_certificate_pictures varchar(255) NULL,
  practising_certificate_pictures varchar(255) NULL,
  open_id varchar(255) NULL,
  create_by varchar(64) NULL,
  create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(64) NULL,
  update_time timestamp NULL,
  del_flag char(1) NULL DEFAULT '0',
  dept_code varchar(128) NULL
);

COMMENT ON TABLE sys_staff IS '员工表';
COMMENT ON COLUMN sys_staff.id IS 'id';
COMMENT ON COLUMN sys_staff.organ_code IS '所属机构编码';
COMMENT ON COLUMN sys_staff.dept_id IS '所属部门id';
COMMENT ON COLUMN sys_staff.staff_no IS '工号';
COMMENT ON COLUMN sys_staff.staff_name IS '姓名';
COMMENT ON COLUMN sys_staff.nationality_code IS '国籍编码 (GB/T 2659)';
COMMENT ON COLUMN sys_staff.nationality_name IS '国籍名称';
COMMENT ON COLUMN sys_staff.nation_code IS '民族编码 (GB/T 3304)';
COMMENT ON COLUMN sys_staff.nation_name IS '民族名称';
COMMENT ON COLUMN sys_staff.identification_no IS '身份证号';
COMMENT ON COLUMN sys_staff.gender_code IS '性别代码，和数据字典sex一致';
COMMENT ON COLUMN sys_staff.gender_name IS '性别名称';
COMMENT ON COLUMN sys_staff.birthdate IS '出生日期';
COMMENT ON COLUMN sys_staff.telephone IS '电话';
COMMENT ON COLUMN sys_staff.marital_status_code IS '婚姻状况代码';
COMMENT ON COLUMN sys_staff.marital_status_name IS '婚姻状况名称';
COMMENT ON COLUMN sys_staff.native_place IS '籍贯';
COMMENT ON COLUMN sys_staff.politics_status_code IS '政治面貌代码';
COMMENT ON COLUMN sys_staff.politics_status_name IS '政治面貌名称';
COMMENT ON COLUMN sys_staff.addr_province IS '地址-省';
COMMENT ON COLUMN sys_staff.addr_city IS '地址-市';
COMMENT ON COLUMN sys_staff.addr_county IS '地址-县';
COMMENT ON COLUMN sys_staff.addr_town IS '地址-乡';
COMMENT ON COLUMN sys_staff.addr_village IS '地址-村';
COMMENT ON COLUMN sys_staff.addr_house_no IS '地址-门牌号';
COMMENT ON COLUMN sys_staff.address IS '详细地址';
COMMENT ON COLUMN sys_staff.zip_code IS '邮编';
COMMENT ON COLUMN sys_staff.education_level_code IS '学历代码 (GB/T 4658)';
COMMENT ON COLUMN sys_staff.education_level_name IS '学历名称';
COMMENT ON COLUMN sys_staff.degree_code IS '学位代码 (GB/T 6864)';
COMMENT ON COLUMN sys_staff.degree_name IS '学位名称';
COMMENT ON COLUMN sys_staff.subject_code IS '专业代码 (GB/T 16835)';
COMMENT ON COLUMN sys_staff.subject_name IS '专业名称';
COMMENT ON COLUMN sys_staff.graduate_school_name IS '毕业院校';
COMMENT ON COLUMN sys_staff.work_begin_date IS '参加工作日期';
COMMENT ON COLUMN sys_staff.job_category IS '岗位类别';
COMMENT ON COLUMN sys_staff.technical_qualifications_code IS '专业技术职务代码 (GB/T 8561)';
COMMENT ON COLUMN sys_staff.technical_qualifications_name IS '专业技术职务名称';
COMMENT ON COLUMN sys_staff.technical_position_category_code IS '专业技术职务类别代码，备用';
COMMENT ON COLUMN sys_staff.technical_position_category_name IS '专业技术职务类别名称，备用';
COMMENT ON COLUMN sys_staff.management_position_code IS '行政/业务管理职务代码 (GB/T 12403)';
COMMENT ON COLUMN sys_staff.management_position_name IS '行政/业务管理职务名称';
COMMENT ON COLUMN sys_staff.administration_level_code IS '行政职级代码';
COMMENT ON COLUMN sys_staff.administration_level_name IS '行政职级名称';
COMMENT ON COLUMN sys_staff.title_code IS '职称代码';
COMMENT ON COLUMN sys_staff.title_name IS '职称名称';
COMMENT ON COLUMN sys_staff.title_level_code IS '职称级别代码 (高级、中级、初级)';
COMMENT ON COLUMN sys_staff.title_level_name IS '职称级别名称 (高级、中级、初级)';
COMMENT ON COLUMN sys_staff.is_organizational IS '是否编制人员';
COMMENT ON COLUMN sys_staff.position_name IS '职务';
COMMENT ON COLUMN sys_staff.staff_category_code IS '人员类别代码';
COMMENT ON COLUMN sys_staff.staff_category_name IS '人员类别名称';
COMMENT ON COLUMN sys_staff.active_status_code IS '在岗状态代码';
COMMENT ON COLUMN sys_staff.active_status_name IS '在岗状态名称';
COMMENT ON COLUMN sys_staff.qualification_certificate_no IS '资格证书编号';
COMMENT ON COLUMN sys_staff.practising_certificate_no IS '执业证书编号';
COMMENT ON COLUMN sys_staff.expertise_field IS '擅长领域';
COMMENT ON COLUMN sys_staff.detailed_introduction IS '详细介绍';
COMMENT ON COLUMN sys_staff.is_general_staff IS '是否普通职员';
COMMENT ON COLUMN sys_staff.is_country_staff IS '是否乡村职员';
COMMENT ON COLUMN sys_staff.is_supervisory IS '监督人员，备用';
COMMENT ON COLUMN sys_staff.supervisory_authorized_category_code IS '监督人员编制类别代码，备用';
COMMENT ON COLUMN sys_staff.supervisory_authorized_category_name IS '监督人员编制类别名称，备用';
COMMENT ON COLUMN sys_staff.supervisory_employee_category_code IS '监督人员职工类别代码，备用';
COMMENT ON COLUMN sys_staff.supervisory_employee_category_name IS '监督人员职工类别名称，备用';
COMMENT ON COLUMN sys_staff.supervisory_practice_scope_code IS '监督员执业范围代码，备用';
COMMENT ON COLUMN sys_staff.supervisory_practice_scope_name IS '监督员执业范围名称，备用';
COMMENT ON COLUMN sys_staff.photograph IS '照片地址';
COMMENT ON COLUMN sys_staff.electronic_signature IS '电子签名';
COMMENT ON COLUMN sys_staff.qualification_certificate_pictures IS '资格证书';
COMMENT ON COLUMN sys_staff.practising_certificate_pictures IS '资格证书';
COMMENT ON COLUMN sys_staff.open_id IS 'openId';
COMMENT ON COLUMN sys_staff.create_by IS '创建者';
COMMENT ON COLUMN sys_staff.create_time IS '创建时间';
COMMENT ON COLUMN sys_staff.update_by IS '更新者';
COMMENT ON COLUMN sys_staff.update_time IS '修改时间';
COMMENT ON COLUMN sys_staff.del_flag IS '逻辑删 0-正常 1-删除';
COMMENT ON COLUMN sys_staff.dept_code IS '部门编码';

-- Records of sys_staff
INSERT INTO sys_staff VALUES (1, 'F001', 1, '1001', 'Amy', NULL, NULL, NULL, NULL, '13983371796', '2', '女', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-03-24 22:33:14', NULL, '2025-03-24 22:33:14', '0', '01');
INSERT INTO sys_staff VALUES (2, 'F001', 1, '1002', 'Tom', NULL, NULL, NULL, NULL, '19122287881', '1', '男', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-03-24 22:33:14', NULL, '2025-03-24 22:33:14', '0', '02');
INSERT INTO sys_staff VALUES (3, 'F001', 1, '1003', 'Edison', NULL, NULL, NULL, NULL, '13585823603', '1', '男', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-03-24 22:33:14', NULL, '2025-03-24 22:33:14', '0', '03');
INSERT INTO sys_staff VALUES (4, 'F001', 1, '1004', 'IT总监', NULL, NULL, NULL, NULL, '18696777215', '1', '男', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-03-24 22:33:14', NULL, '2025-03-24 22:33:14', '0', '02');

-- Table structure for sys_staff_dept
DROP TABLE IF EXISTS sys_staff_dept;
CREATE TABLE sys_staff_dept (
  staff_id int NULL,
  department_id int NULL
);

COMMENT ON TABLE sys_staff_dept IS '人员部门关联表';
COMMENT ON COLUMN sys_staff_dept.staff_id IS '人员id';
COMMENT ON COLUMN sys_staff_dept.department_id IS '部门id';

-- Records of sys_staff_dept
INSERT INTO sys_staff_dept VALUES (1, 1);
INSERT INTO sys_staff_dept VALUES (2, 1);
INSERT INTO sys_staff_dept VALUES (3, 1);

-- Table structure for sys_user
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id serial PRIMARY KEY NOT NULL,
  nick_name varchar(64) NULL,
  username varchar(64) NULL,
  password varchar(255) NULL,
  sex_code char(1) NULL DEFAULT '1',
  salt varchar(255) NULL,
  phone varchar(20) NULL,
  email varchar(20) NULL,
  avatar varchar(255) NULL,
  staff_id int NULL,
  dept_id int NULL DEFAULT 0,
  organ_code varchar(64) NULL,
  switch_code varchar(64) NULL,
  expire_time timestamp NULL,
  expired_flag char(1) NULL DEFAULT '0',
  status char(1) NULL DEFAULT '0',
  lock_flag char(1) NULL DEFAULT '1',
  first_login char(1) NULL DEFAULT '1',
  create_by varchar(64) NULL,
  create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(64) NULL,
  update_time timestamp NULL,
  del_flag char(1) NULL DEFAULT '0'
);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.id IS '主键ID';
COMMENT ON COLUMN sys_user.nick_name IS '昵称';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码';
COMMENT ON COLUMN sys_user.sex_code IS '性别编码，和数据字典sex一致';
COMMENT ON COLUMN sys_user.salt IS '随机盐';
COMMENT ON COLUMN sys_user.phone IS '手机号';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.avatar IS '头像';
COMMENT ON COLUMN sys_user.staff_id IS '人员ID';
COMMENT ON COLUMN sys_user.dept_id IS '部门ID';
COMMENT ON COLUMN sys_user.organ_code IS '所属机构编码';
COMMENT ON COLUMN sys_user.switch_code IS '切换机构编码(第二机构)';
COMMENT ON COLUMN sys_user.expire_time IS '过期时间，定时任务条件查询修改过期状态';
COMMENT ON COLUMN sys_user.expired_flag IS '是否过期（0未过期 1过期）';
COMMENT ON COLUMN sys_user.status IS '账号状态:0-禁用 1-启用';
COMMENT ON COLUMN sys_user.lock_flag IS '账号锁定：0-锁定 1-正常';
COMMENT ON COLUMN sys_user.first_login IS '是否首次登录：0-否/1-是 默认1';
COMMENT ON COLUMN sys_user.create_by IS '创建者';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_by IS '更新者';
COMMENT ON COLUMN sys_user.update_time IS '修改时间';
COMMENT ON COLUMN sys_user.del_flag IS '逻辑删 0-正常 1-删除';

CREATE INDEX idx_username ON sys_user(username);

-- Records of sys_user
INSERT INTO sys_user VALUES (1, 'amy', 'amy', '$2a$10$IVurf0aelX1HwuXtn.a1i.eFdIraSmBqJ4wJJpThMrKDTQQHJP3XK', '1', NULL, '13111111111', 'amy@gmail.com', '', 1, 1, 'F001', '', NULL, 0, 1, 1, '0', 'admin', '2025-03-24 22:33:22', 'admin', '2025-03-24 22:33:22', '0');
INSERT INTO sys_user VALUES (2, 'tom', 'tom', '$2a$10$C7PUtxOkNjuXhoDPdNZQYOF6ygq1/02YMwsMV/IfqpeJ2QoQxXqTG', '1', NULL, '13122222222', 'tom@gmail.com', '', 2, 2, 'F001', '', NULL, 0, 1, 1, '0', 'admin', '2025-03-24 22:33:23', 'admin', '2025-03-24 22:33:23', '0');
INSERT INTO sys_user VALUES (3, 'edison', 'edison', '$2a$10$oXFppqYngsw8PgJZDI0fr.Uy4.npNgk7WbGfeo9mQELDA2Y/60Fui', '1', NULL, '13133333333', 'edison@gmail.com', '', 3, 3, 'F001', '', NULL, 0, 1, 1, '0', 'admin', '2025-03-24 22:33:24', 'admin', '2025-03-24 22:33:24', '0');
INSERT INTO sys_user VALUES (4, '管理员', 'admin', '$2a$10$oXFppqYngsw8PgJZDI0fr.Uy4.npNgk7WbGfeo9mQELDA2Y/60Fui', '1', NULL, '13144444444', 'admin@gmail.com', '', 4, 2, 'F001', '', NULL, 0, 1, 1, '0', 'admin', '2025-03-24 22:33:25', 'admin', '2025-03-24 22:33:25', '0');

-- Table structure for sys_user_department
DROP TABLE IF EXISTS sys_user_department;
CREATE TABLE sys_user_department (
  user_id int NOT NULL,
  dept_id int NOT NULL
);

COMMENT ON TABLE sys_user_department IS '用户部门表,暂未使用';
COMMENT ON COLUMN sys_user_department.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_department.dept_id IS '部门ID';


-- Table structure for sys_user_role
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
  user_id int NOT NULL,
  role_id int NOT NULL,
  PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '用户角色表';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';

-- Records of sys_user_role
INSERT INTO sys_user_role VALUES (1, 1);
INSERT INTO sys_user_role VALUES (2, 2);
INSERT INTO sys_user_role VALUES (3, 2);
INSERT INTO sys_user_role VALUES (4, 1);


SELECT setval('sys_application_id_seq', (SELECT MAX(id) FROM sys_application));
SELECT setval('sys_config_id_seq', (SELECT MAX(id) FROM sys_config));
SELECT setval('sys_department_id_seq', (SELECT MAX(id) FROM sys_department));

SELECT setval('sys_dict_id_seq', (SELECT MAX(id) FROM sys_dict));
SELECT setval('sys_dict_item_id_seq', (SELECT MAX(id) FROM sys_dict_item));
SELECT setval('sys_file_id_seq', (SELECT MAX(id) FROM sys_file));
SELECT setval('sys_log_id_seq', (SELECT MAX(id) FROM sys_log));
SELECT setval('sys_manufacturer_id_seq', (SELECT MAX(id) FROM sys_manufacturer));
SELECT setval('sys_menu_id_seq', (SELECT MAX(id) FROM sys_menu));
SELECT setval('sys_oauth_client_details_id_seq', (SELECT MAX(id) FROM sys_oauth_client_details));
SELECT setval('sys_organ_id_seq', (SELECT MAX(id) FROM sys_organ));
SELECT setval('sys_public_param_public_id_seq', (SELECT MAX(public_id) FROM sys_public_param));
SELECT setval('sys_role_id_seq', (SELECT MAX(id) FROM sys_role));

SELECT setval('sys_staff_id_seq', (SELECT MAX(id) FROM sys_staff));

SELECT setval('sys_user_id_seq', (SELECT MAX(id) FROM sys_user));

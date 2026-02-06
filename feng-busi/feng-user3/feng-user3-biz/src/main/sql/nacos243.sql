/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : localhost:3306
 Source Schema         : nacos243

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 21/02/2025 00:31:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for config_info
-- ----------------------------
DROP TABLE IF EXISTS `config_info`;
CREATE TABLE `config_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'group_id',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'configuration description',
  `c_use` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'configuration usage',
  `effect` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '配置生效的描述',
  `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '配置的类型',
  `c_schema` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '配置的模式',
  `encrypted_data_key` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '密钥',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfo_datagrouptenant`(`data_id`, `group_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_info' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info
-- ----------------------------
INSERT INTO `config_info` VALUES (1, 'application-dev.yml', 'DEFAULT_GROUP', 'jasypt:\n  encryptor:\n    password: feng\n    algorithm: PBEWithMD5AndDES\n    iv-generator-classname: org.jasypt.iv.NoIvGenerator\n\nspring:\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    password: radar\n\n  klock:\n    address: 127.0.0.1:6379\n    password: radar\n    database: 3\n    waitTime: 10\n    leaseTime: 10\n    \n  servlet:\n    multipart:\n      max-file-size: 100MB\n      max-request-size: 100MB\n  cloud:\n    sentinel:\n      eager: true\n      filter:\n        enabled: false\n      transport:\n        dashboard: feng-sentinel:5020\n\nmanagement:\n  metrics.export.prometheus.enabled: true\n  endpoints:\n    enabled-by-default: false\n    web:\n      exposure:\n        include: \'*\'          \n  endpoint:\n    metrics:\n      enabled: true\n    prometheus:\n      enabled: true\n    restart:\n      enabled: true\n    health:\n      show-details: ALWAYS\n\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 20000\n        readTimeout: 20000\n  compression:\n    request:\n      enabled: true\n    response:\n      enabled: true\n\n\ngray:\n  rule:\n    enabled: true\n\nmybatis-plus:\n  tenant-enable: ture\n  mapper-locations: classpath:/mapper/*Mapper.xml\n  global-config:\n    capitalMode: true\n    banner: false\n    db-config:\n      id-type: auto\n      select-strategy: NOT_NULL\n      insert-strategy: NOT_NULL\n      update-strategy: NOT_NULL\n  type-handlers-package:  work.metanet.feng.common.data.handler\n  configuration:\n    jdbc-type-for-null: \'null\'\n    call-setters-on-nulls: true\n\nknife4j:\n  enable: true\nswagger:\n  enabled: true\n  title: Feng Swagger API\n  license: Powered By Feng\n  licenseUrl: https://metanet.work/\n  terms-of-service-url: https://metanet.work/\n  contact:\n    email: radarfyh@gmail.com\n    url: https://metanet.work//about.html\n  authorization:\n    name: oauth2\n    auth-regex: ^.*$\n    authorization-scope-list:\n      - scope: server\n        description: server all\n    token-url-list:\n      - http://127.0.0.1:2000/auth/oauth/token\n\nsecurity:\n  oauth2:\n    client:\n      ignore-urls:\n        - /css/**\n        - /error\n        - /actuator/**\n        - /v2/api-docs\n        - /doc.html\n    resource:\n      loadBalanced: true\n      token-info-uri: http://127.0.0.1:2000/auth/oauth/check_token\n\nparams:\n  dataTimeType: date\n\nfeng:\n  websocket:\n    lifespan: 60', '93d8586a5d40eab436dd8218b25c6cbb', '2025-02-21 00:31:38', '2025-02-21 00:31:38', '', '0:0:0:0:0:0:0:1', '', 'dev', NULL, NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (2, 'feng-user3-biz-dev.yml', 'DEFAULT_GROUP', 'security:\n  oauth2:\n    client:\n      client-id: feng\n      client-secret: feng\n      scope: server\n      ignore-urls:\n        - /error\n        - /druid/**\n        - /actuator/**\n        - /v2/api-docs\n        - /report/v2/api-docs\n        - /sysDepartment/getDepartmentById\n        - /sysDepartment/odin\n        - /sysStaffDept/getDeptsByStaffIdAndBi\n        - /sysStaffDept/odin\n        - /sysStaffDept/getDeptsByPraNo\n        - /sysStaff/getRoleAndDeptByUsername\n        - /sysStaff/getStaffByPhone\n        - /sysStaff/odin\n        - /sysDictItem/item/list\nspring:\n  main: \n    allow-bean-definition-overriding: true\n  datasource:\n    dynamic:\n      primary: mysql\n      strict: false\n      datasource:\n        mysql:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          username: root\n          password: radar\n          url: jdbc:mysql://127.0.0.1:3306/feng_user2_biz?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true&allowPublicKeyRetrieval=true\n          type: com.alibaba.druid.pool.DruidDataSource\n\n      druid:\n        initialSize: 5\n        minIdle: 10\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        maxEvictableIdleTimeMillis: 900000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        webStatFilter:\n          enabled: true\n        stat-view-servlet:\n          enabled: true\n          allow: \"\"\n          url-pattern: /druid/*\n        filter:\n          stat:\n            enabled: true\n            log-slow-sql: true\n            slow-sql-millis: 10000\n            merge-sql: false\n          wall:\n            config:\n              multi-statement-allow: true\nfile:\n  bucketName: feng-bucket\n  oss:\n    enable: true\n    path-style-access: false\n    endpoint: http://127.0.0.1:9000\n    access-key: admin\n    secret-key: admin123\n\nlogging:\n  level:\n    work.metanet.feng.admin: DEBUG\n    work.metanet.feng.common: DEBUG\n    com.baomidou.dynamic.datasource: INFO\n    com.alibaba.druid: INFO\n    org.springframework: INFO\n    root: INFO\nfeng:\n  organ:\n    column: organ_code\n    tables:\n      - sys_department\n      - sys_staff\n      - sys_datasource\n\nbi: \n  token: FN6pZIQG0PZFzUfolJQnsGsZeRPPxLFuavSKCauhDq4=\ndebug: true\n', '0d10d548f8de1c8f056a3698e82d3376', '2025-02-21 00:31:38', '2025-02-21 00:31:38', '', '0:0:0:0:0:0:0:1', '', 'dev', NULL, NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (3, 'feng-log2-biz-dev.yml', 'DEFAULT_GROUP', 'spring:\n  datasource:\n    dynamic:\n      primary: mysql\n      strict: false\n      datasource:\n        mysql:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          username: root\n          password: radar\n          url: jdbc:mysql://127.0.0.1:3306/feng_log2_biz?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true&allowPublicKeyRetrieval=true\n      druid:\n        initialSize: 5\n        minIdle: 10\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        maxEvictableIdleTimeMillis: 900000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        webStatFilter:\n          enabled: true\n        stat-view-servlet:\n          enabled: true\n          allow: \"\"\n          url-pattern: /druid/*\n        filter:\n          stat:\n            enabled: true\n            log-slow-sql: true\n            slow-sql-millis: 10000\n            merge-sql: false\n          wall:\n            config:\n              multi-statement-allow: true\n\n  elasticsearch:\n    uris: [\"http://elastic:rtyl123456@192.168.20.244:9202\",\"http://elastic:rtyl123456@192.168.20.200:9202\"]\n\n  kafka:\n    bootstrap-servers: 192.168.20.199:9092\n    producer:\n      key-serializer: org.apache.kafka.common.serialization.StringSerializer\n      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer\n      retries: 0\n      batch-size: 16384\n      buffer-memory: 33554432\n\n    consumer:\n      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer\n      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer\n      group-id: log_consumer_group\n      enable-auto-commit: true\n      auto-commit-interval: 1000\n      acks: 0\n      properties:\n        spring:\n          json:\n            trusted:\n              packages: \'*\'\n\nlogging:\n  level:\n    work.metanet.log2.mapper: INFO\n    com.baomidou.dynamic.datasource: INFO\n    com.alibaba.druid: INFO', 'f74fe9de8de9ad042d27d307220b9a7b', '2025-02-21 00:31:39', '2025-02-21 00:31:39', '', '0:0:0:0:0:0:0:1', '', 'dev', NULL, NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (4, 'feng-auth-dev.yml', 'DEFAULT_GROUP', 'spring:\n  freemarker:\n    allow-request-override: false\n    allow-session-override: false\n    cache: true\n    charset: UTF-8\n    check-template-location: true\n    content-type: text/html\n    enabled: true\n    expose-request-attributes: false\n    expose-session-attributes: false\n    expose-spring-macro-helpers: true\n    prefer-file-system-access: true\n    suffix: .ftl\n    template-loader-path: classpath:/templates/\nlogging:\n  level:\n    work.metanet.feng.auth: DEBUG\n    org.springframework: INFO\ndebug: \n  true', '619c542ddb4f835b6d98dd3bf100cdd8', '2025-02-21 00:31:39', '2025-02-21 00:31:39', '', '0:0:0:0:0:0:0:1', '', 'dev', NULL, NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (5, 'feng-msg-biz-dev.yml', 'DEFAULT_GROUP', 'spring:\n  datasource:\n    dynamic:\n      primary: mysql\n      strict: false\n      datasource:\n        mysql:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          username: root\n          password: radar\n          url: jdbc:mysql://127.0.0.1:3306/feng_msg_biz?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true&allowPublicKeyRetrieval=true\n      druid:\n        initialSize: 5\n        minIdle: 10\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        maxEvictableIdleTimeMillis: 900000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        webStatFilter:\n          enabled: true\n        stat-view-servlet:\n          enabled: true\n          allow: 127.0.0.1\n          url-pattern: /druid/*\n          login-username: admin\n          login-password: 123456\n        filter:\n          stat:\n            enabled: true\n            log-slow-sql: true\n            slow-sql-millis: 10000\n            merge-sql: false\n          wall:\n            config:\n              multi-statement-allow: true\n\n  mail:\n    host: \n    username: \n    password: \n    properties.mail.smtp.auth: true\n    properties.mail.smtp.starttls.enable: true\n    default-encoding: utf-8\n\n# Logger Config\nlogging:\n  level:\n    work.metanet.feng.msg.mapper: debug\n    com.baomidou.dynamic.datasource: DEBUG\n    com.alibaba.druid: DEBUG\nfeng:\n  websocket:\n    lifespan: 90\n\nwx:\n  miniapp:\n    appid: 123\n    secret: 123', '803c8b6ee71c41fe9da33f94ce50efdb', '2025-02-21 00:31:39', '2025-02-21 00:31:39', '', '0:0:0:0:0:0:0:1', '', 'dev', NULL, NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (6, 'feng-upms-biz-dev.yml', 'DEFAULT_GROUP', 'security:\n  oauth2:\n    client:\n      client-id: ENC(HhmU+VE7Wz7FoBp+zGJEMA==)\n      client-secret: ENC(HhmU+VE7Wz7FoBp+zGJEMA==)\n      scope: server\n      ignore-urls:\n        - /error\n        - /druid/**\n        - /actuator/**\n        - /v2/api-docs\n        - /report/v2/api-docs\n        - /sysDepartment/getDepartmentById\n        - /sysDepartment/odin\n        - /sysStaffDept/getDeptsByStaffIdAndBi\n        - /sysStaffDept/odin\n        - /sysStaffDept/getDeptsByPraNo\n        - /sysStaff/getRoleAndDeptByUsername\n        - /sysStaff/getStaffByPhone\n        - /sysStaff/odin\n        - /sysDictItem/item/list\n\nspring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    druid:\n      driver-class-name: com.mysql.cj.jdbc.Driver\n      username: root\n      password: radar\n      url: jdbc:mysql://localhost:3306/hip_upms?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true&allowPublicKeyRetrieval=true\n      stat-view-servlet:\n        enabled: true\n        allow: \"\"\n        url-pattern: /druid/*\n      filter:\n        stat:\n          enabled: true\n          log-slow-sql: true\n          slow-sql-millis: 10000\n          merge-sql: false\n#        wall:\n#          config:\n#            multi-statement-allow: true\n\nfile:\n  bucketName: hip-bucket\n  oss:\n    enable: true\n    path-style-access: false\n    endpoint: http://192.168.20.199:9000\n    access-key: admin\n    secret-key: admin123\n\nlogging:\n  level:\n    work.metanet.feng.admin.mapper: debug\n\nhip:\n  organ:\n    column: organ_code\n    tables:\n      - sys_department\n      - sys_staff\n      - sys_datasource\n\nbi: \n token: FN6pZIQG0PZFzUfolJQnsGsZeRPPxLFuavSKCauhDq4=', '52dc07ef815c5bb4ee647ee728290a9e', '2025-02-21 00:31:39', '2025-02-21 00:31:39', '', '0:0:0:0:0:0:0:1', '', 'dev', NULL, NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (7, 'feng-gateway2-dev.yml', 'DEFAULT_GROUP', 'gateway:\n  encode-key: \'storm-winds-rain\'\n\nswagger:\n  enabled: true\n\nribbon:\n  rule:\n    gray-enabled: true\n\naj:\n  captcha:\n    water-mark: feng\n    click-offset: 30\n    font-size: 25\n    font-type: WenQuanZhengHei.ttf\nht:\n  captcha:\n    type: circle\n    height: 10\n    width: 40\n    interfereCount: 3\n    textAlpha: 0.3\n    expireSeconds: 300\n    \n    code:\n      type: random  # or math\n      length: 6\n    font:\n      name: Arial\n      size: 24\n      weight: 2\nlogging:\n  level:\n    work.metanet.feng.gateway: DEBUG', 'ff6152887d15177821329e85e2bff075', '2025-02-21 00:31:39', '2025-02-21 00:31:39', '', '0:0:0:0:0:0:0:1', '', 'dev', NULL, NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (8, 'feng-erp-biz-dev.yml', 'DEFAULT_GROUP', 'security:\n  oauth2:\n    client:\n      client-id: ENC(HhmU+VE7Wz7FoBp+zGJEMA==)\n      client-secret: ENC(HhmU+VE7Wz7FoBp+zGJEMA==)\n      scope: server\n      ignore-urls:\n        - /error\n        - /druid/**\n        - /actuator/**\n        - /v2/api-docs\n        - /report/v2/api-docs\n        - /sysDepartment/getDepartmentById\n        - /sysDepartment/odin\n        - /sysStaffDept/getDeptsByStaffIdAndBi\n        - /sysStaffDept/odin\n        - /sysStaffDept/getDeptsByPraNo\n        - /sysStaff/getRoleAndDeptByUsername\n        - /sysStaff/getStaffByPhone\n        - /sysStaff/odin\n        - /sysDictItem/item/list\nspring:\n  freemarker:\n    allow-request-override: false\n    allow-session-override: false\n    cache: true\n    charset: UTF-8\n    check-template-location: true\n    content-type: text/html\n    enabled: true\n    expose-request-attributes: false\n    expose-session-attributes: false\n    expose-spring-macro-helpers: true\n    prefer-file-system-access: true\n    suffix: .ftl\n    template-loader-path: classpath:/templates/\n  datasource:\n    dynamic:\n      primary: mysql\n      strict: false\n      datasource:\n        mysql:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          username: root\n          password: radar\n          url: jdbc:mysql://127.0.0.1:3306/feng-erp-biz?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true&allowPublicKeyRetrieval=true\n          type: com.alibaba.druid.pool.DruidDataSource\n\n      druid:\n        initialSize: 5\n        minIdle: 10\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        maxEvictableIdleTimeMillis: 900000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        webStatFilter:\n          enabled: true\n        stat-view-servlet:\n          enabled: true\n          allow: \"\"\n          url-pattern: /druid/*\n        filter:\n          stat:\n            enabled: true\n            log-slow-sql: true\n            slow-sql-millis: 10000\n            merge-sql: false\n          wall:\n            config:\n              multi-statement-allow: true\n  mail:\n    host: smtp.qq.com\n    username: 1509515977@qq.com\n    password: \n    properties.mail.smtp.auth: true\n    properties.mail.smtp.starttls.enable: true\n    default-encoding: utf-8\n\n  elasticsearch:\n    uris: [\"http://elastic:rtyl123456@192.168.20.244:9202\",\"http://elastic:rtyl123456@192.168.20.200:9202\"]\n\n  kafka:\n    bootstrap-servers: 192.168.20.199:9092\n    producer:\n      key-serializer: org.apache.kafka.common.serialization.StringSerializer\n      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer\n      retries: 0\n      batch-size: 16384\n      buffer-memory: 33554432\n\n    consumer:\n      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer\n      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer\n      group-id: log_consumer_group\n      enable-auto-commit: true\n      auto-commit-interval: 1000\n      acks: 0\n      properties:\n        spring:\n          json:\n            trusted:\n              packages: \'*\'\n\nfile:\n  bucketName: feng-bucket\n  oss:\n    enable: true\n    path-style-access: false\n    endpoint: http://127.0.0.1:9000\n    access-key: admin\n    secret-key: admin123\n\nlogging:\n  level:\n    work.metanet.feng.admin: DEBUG\n    work.metanet.feng.common: DEBUG\n    work.metanet.feng.log.mapper: DEBUG\n    work.metanet.feng.msg.mapper: DEBUG\n    com.baomidou.dynamic.datasource: INFO\n    com.alibaba.druid: INFO\n    org.springframework: INFO\n    root: INFO\n\nfeng:\n  organ:\n    column: organ_code\n    tables:\n      - sys_department\n      - sys_staff\n      - sys_datasource\n  websocket:\n    lifespan: 90\nbi: \n  token: FN6pZIQG0PZFzUfolJQnsGsZeRPPxLFuavSKCauhDq4=\n\nwx:\n  miniapp:\n    appid: \n    secret: \n\n  encode-key: \'storm-winds-rain\'\n\n\nswagger:\n  enabled: true\nribbon:\n  rule:\n    gray-enabled: true\n\naj:\n  captcha:\n    water-mark: feng\n    click-offset: 30\n    font-size: 25\n    font-type: WenQuanZhengHei.ttf\nht:\n  captcha:\n    type: circle\n    height: 10\n    width: 40\n    interfereCount: 3\n    textAlpha: 0.3\n    expireSeconds: 300\n    \n    code:\n      type: random  # 或 math\n      length: 6\n    font:\n      name: Arial\n      size: 24\n      weight: 2\n      \ndebug: true\n', '902f3a7bcad579dfb00adcab6c71faef', '2025-02-21 00:31:39', '2025-02-21 00:31:39', '', '0:0:0:0:0:0:0:1', '', 'dev', NULL, NULL, NULL, 'yaml', NULL, '');

-- ----------------------------
-- Table structure for config_info_aggr
-- ----------------------------
DROP TABLE IF EXISTS `config_info_aggr`;
CREATE TABLE `config_info_aggr`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'datum_id',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfoaggr_datagrouptenantdatum`(`data_id`, `group_id`, `tenant_id`, `datum_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '增加租户字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_aggr
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_beta
-- ----------------------------
DROP TABLE IF EXISTS `config_info_beta`;
CREATE TABLE `config_info_beta`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '密钥',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfobeta_datagrouptenant`(`data_id`, `group_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_info_beta' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_beta
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_tag
-- ----------------------------
DROP TABLE IF EXISTS `config_info_tag`;
CREATE TABLE `config_info_tag`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfotag_datagrouptenanttag`(`data_id`, `group_id`, `tenant_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_info_tag' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_tag
-- ----------------------------

-- ----------------------------
-- Table structure for config_tags_relation
-- ----------------------------
DROP TABLE IF EXISTS `config_tags_relation`;
CREATE TABLE `config_tags_relation`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增长标识',
  PRIMARY KEY (`nid`) USING BTREE,
  UNIQUE INDEX `uk_configtagrelation_configidtag`(`id`, `tag_name`, `tag_type`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_tag_relation' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_tags_relation
-- ----------------------------

-- ----------------------------
-- Table structure for group_capacity
-- ----------------------------
DROP TABLE IF EXISTS `group_capacity`;
CREATE TABLE `group_capacity`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_id`(`group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '集群、各Group容量信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for his_config_info
-- ----------------------------
DROP TABLE IF EXISTS `his_config_info`;
CREATE TABLE `his_config_info`  (
  `id` bigint(20) UNSIGNED NOT NULL COMMENT 'id',
  `nid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增标识',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
  `op_type` char(10) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'operation type',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '密钥',
  PRIMARY KEY (`nid`) USING BTREE,
  INDEX `idx_gmt_create`(`gmt_create`) USING BTREE,
  INDEX `idx_gmt_modified`(`gmt_modified`) USING BTREE,
  INDEX `idx_did`(`data_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '多租户改造' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of his_config_info
-- ----------------------------
INSERT INTO `his_config_info` VALUES (0, 1, 'application-dev.yml', 'DEFAULT_GROUP', '', 'jasypt:\n  encryptor:\n    password: feng\n    algorithm: PBEWithMD5AndDES\n    iv-generator-classname: org.jasypt.iv.NoIvGenerator\n\nspring:\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n  redis:\n    host: 127.0.0.1\n    port: 6379\n    password: radar\n\n  klock:\n    address: 127.0.0.1:6379\n    password: radar\n    database: 3\n    waitTime: 10\n    leaseTime: 10\n    \n  servlet:\n    multipart:\n      max-file-size: 100MB\n      max-request-size: 100MB\n  cloud:\n    sentinel:\n      eager: true\n      filter:\n        enabled: false\n      transport:\n        dashboard: feng-sentinel:5020\n\nmanagement:\n  metrics.export.prometheus.enabled: true\n  endpoints:\n    enabled-by-default: false\n    web:\n      exposure:\n        include: \'*\'          \n  endpoint:\n    metrics:\n      enabled: true\n    prometheus:\n      enabled: true\n    restart:\n      enabled: true\n    health:\n      show-details: ALWAYS\n\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 20000\n        readTimeout: 20000\n  compression:\n    request:\n      enabled: true\n    response:\n      enabled: true\n\n\ngray:\n  rule:\n    enabled: true\n\nmybatis-plus:\n  tenant-enable: ture\n  mapper-locations: classpath:/mapper/*Mapper.xml\n  global-config:\n    capitalMode: true\n    banner: false\n    db-config:\n      id-type: auto\n      select-strategy: NOT_NULL\n      insert-strategy: NOT_NULL\n      update-strategy: NOT_NULL\n  type-handlers-package:  work.metanet.feng.common.data.handler\n  configuration:\n    jdbc-type-for-null: \'null\'\n    call-setters-on-nulls: true\n\nknife4j:\n  enable: true\nswagger:\n  enabled: true\n  title: Feng Swagger API\n  license: Powered By Feng\n  licenseUrl: https://metanet.work/\n  terms-of-service-url: https://metanet.work/\n  contact:\n    email: radarfyh@gmail.com\n    url: https://metanet.work//about.html\n  authorization:\n    name: oauth2\n    auth-regex: ^.*$\n    authorization-scope-list:\n      - scope: server\n        description: server all\n    token-url-list:\n      - http://127.0.0.1:2000/auth/oauth/token\n\nsecurity:\n  oauth2:\n    client:\n      ignore-urls:\n        - /css/**\n        - /error\n        - /actuator/**\n        - /v2/api-docs\n        - /doc.html\n    resource:\n      loadBalanced: true\n      token-info-uri: http://127.0.0.1:2000/auth/oauth/check_token\n\nparams:\n  dataTimeType: date\n\nfeng:\n  websocket:\n    lifespan: 60', '93d8586a5d40eab436dd8218b25c6cbb', '2025-02-21 00:31:38', '2025-02-20 16:31:38', '', '0:0:0:0:0:0:0:1', 'I', 'dev', '');
INSERT INTO `his_config_info` VALUES (0, 2, 'feng-user3-biz-dev.yml', 'DEFAULT_GROUP', '', 'security:\n  oauth2:\n    client:\n      client-id: feng\n      client-secret: feng\n      scope: server\n      ignore-urls:\n        - /error\n        - /druid/**\n        - /actuator/**\n        - /v2/api-docs\n        - /report/v2/api-docs\n        - /sysDepartment/getDepartmentById\n        - /sysDepartment/odin\n        - /sysStaffDept/getDeptsByStaffIdAndBi\n        - /sysStaffDept/odin\n        - /sysStaffDept/getDeptsByPraNo\n        - /sysStaff/getRoleAndDeptByUsername\n        - /sysStaff/getStaffByPhone\n        - /sysStaff/odin\n        - /sysDictItem/item/list\nspring:\n  main: \n    allow-bean-definition-overriding: true\n  datasource:\n    dynamic:\n      primary: mysql\n      strict: false\n      datasource:\n        mysql:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          username: root\n          password: radar\n          url: jdbc:mysql://127.0.0.1:3306/feng_user2_biz?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true&allowPublicKeyRetrieval=true\n          type: com.alibaba.druid.pool.DruidDataSource\n\n      druid:\n        initialSize: 5\n        minIdle: 10\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        maxEvictableIdleTimeMillis: 900000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        webStatFilter:\n          enabled: true\n        stat-view-servlet:\n          enabled: true\n          allow: \"\"\n          url-pattern: /druid/*\n        filter:\n          stat:\n            enabled: true\n            log-slow-sql: true\n            slow-sql-millis: 10000\n            merge-sql: false\n          wall:\n            config:\n              multi-statement-allow: true\nfile:\n  bucketName: feng-bucket\n  oss:\n    enable: true\n    path-style-access: false\n    endpoint: http://127.0.0.1:9000\n    access-key: admin\n    secret-key: admin123\n\nlogging:\n  level:\n    work.metanet.feng.admin: DEBUG\n    work.metanet.feng.common: DEBUG\n    com.baomidou.dynamic.datasource: INFO\n    com.alibaba.druid: INFO\n    org.springframework: INFO\n    root: INFO\nfeng:\n  organ:\n    column: organ_code\n    tables:\n      - sys_department\n      - sys_staff\n      - sys_datasource\n\nbi: \n  token: FN6pZIQG0PZFzUfolJQnsGsZeRPPxLFuavSKCauhDq4=\ndebug: true\n', '0d10d548f8de1c8f056a3698e82d3376', '2025-02-21 00:31:38', '2025-02-20 16:31:38', '', '0:0:0:0:0:0:0:1', 'I', 'dev', '');
INSERT INTO `his_config_info` VALUES (0, 3, 'feng-log2-biz-dev.yml', 'DEFAULT_GROUP', '', 'spring:\n  datasource:\n    dynamic:\n      primary: mysql\n      strict: false\n      datasource:\n        mysql:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          username: root\n          password: radar\n          url: jdbc:mysql://127.0.0.1:3306/feng_log2_biz?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true&allowPublicKeyRetrieval=true\n      druid:\n        initialSize: 5\n        minIdle: 10\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        maxEvictableIdleTimeMillis: 900000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        webStatFilter:\n          enabled: true\n        stat-view-servlet:\n          enabled: true\n          allow: \"\"\n          url-pattern: /druid/*\n        filter:\n          stat:\n            enabled: true\n            log-slow-sql: true\n            slow-sql-millis: 10000\n            merge-sql: false\n          wall:\n            config:\n              multi-statement-allow: true\n\n  elasticsearch:\n    uris: [\"http://elastic:rtyl123456@192.168.20.244:9202\",\"http://elastic:rtyl123456@192.168.20.200:9202\"]\n\n  kafka:\n    bootstrap-servers: 192.168.20.199:9092\n    producer:\n      key-serializer: org.apache.kafka.common.serialization.StringSerializer\n      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer\n      retries: 0\n      batch-size: 16384\n      buffer-memory: 33554432\n\n    consumer:\n      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer\n      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer\n      group-id: log_consumer_group\n      enable-auto-commit: true\n      auto-commit-interval: 1000\n      acks: 0\n      properties:\n        spring:\n          json:\n            trusted:\n              packages: \'*\'\n\nlogging:\n  level:\n    work.metanet.log2.mapper: INFO\n    com.baomidou.dynamic.datasource: INFO\n    com.alibaba.druid: INFO', 'f74fe9de8de9ad042d27d307220b9a7b', '2025-02-21 00:31:38', '2025-02-20 16:31:39', '', '0:0:0:0:0:0:0:1', 'I', 'dev', '');
INSERT INTO `his_config_info` VALUES (0, 4, 'feng-auth-dev.yml', 'DEFAULT_GROUP', '', 'spring:\n  freemarker:\n    allow-request-override: false\n    allow-session-override: false\n    cache: true\n    charset: UTF-8\n    check-template-location: true\n    content-type: text/html\n    enabled: true\n    expose-request-attributes: false\n    expose-session-attributes: false\n    expose-spring-macro-helpers: true\n    prefer-file-system-access: true\n    suffix: .ftl\n    template-loader-path: classpath:/templates/\nlogging:\n  level:\n    work.metanet.feng.auth: DEBUG\n    org.springframework: INFO\ndebug: \n  true', '619c542ddb4f835b6d98dd3bf100cdd8', '2025-02-21 00:31:38', '2025-02-20 16:31:39', '', '0:0:0:0:0:0:0:1', 'I', 'dev', '');
INSERT INTO `his_config_info` VALUES (0, 5, 'feng-msg-biz-dev.yml', 'DEFAULT_GROUP', '', 'spring:\n  datasource:\n    dynamic:\n      primary: mysql\n      strict: false\n      datasource:\n        mysql:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          username: root\n          password: radar\n          url: jdbc:mysql://127.0.0.1:3306/feng_msg_biz?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true&allowPublicKeyRetrieval=true\n      druid:\n        initialSize: 5\n        minIdle: 10\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        maxEvictableIdleTimeMillis: 900000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        webStatFilter:\n          enabled: true\n        stat-view-servlet:\n          enabled: true\n          allow: 127.0.0.1\n          url-pattern: /druid/*\n          login-username: admin\n          login-password: 123456\n        filter:\n          stat:\n            enabled: true\n            log-slow-sql: true\n            slow-sql-millis: 10000\n            merge-sql: false\n          wall:\n            config:\n              multi-statement-allow: true\n\n  mail:\n    host: \n    username: \n    password: \n    properties.mail.smtp.auth: true\n    properties.mail.smtp.starttls.enable: true\n    default-encoding: utf-8\n\n# Logger Config\nlogging:\n  level:\n    work.metanet.feng.msg.mapper: debug\n    com.baomidou.dynamic.datasource: DEBUG\n    com.alibaba.druid: DEBUG\nfeng:\n  websocket:\n    lifespan: 90\n\nwx:\n  miniapp:\n    appid: 123\n    secret: 123', '803c8b6ee71c41fe9da33f94ce50efdb', '2025-02-21 00:31:38', '2025-02-20 16:31:39', '', '0:0:0:0:0:0:0:1', 'I', 'dev', '');
INSERT INTO `his_config_info` VALUES (0, 6, 'feng-upms-biz-dev.yml', 'DEFAULT_GROUP', '', 'security:\n  oauth2:\n    client:\n      client-id: ENC(HhmU+VE7Wz7FoBp+zGJEMA==)\n      client-secret: ENC(HhmU+VE7Wz7FoBp+zGJEMA==)\n      scope: server\n      ignore-urls:\n        - /error\n        - /druid/**\n        - /actuator/**\n        - /v2/api-docs\n        - /report/v2/api-docs\n        - /sysDepartment/getDepartmentById\n        - /sysDepartment/odin\n        - /sysStaffDept/getDeptsByStaffIdAndBi\n        - /sysStaffDept/odin\n        - /sysStaffDept/getDeptsByPraNo\n        - /sysStaff/getRoleAndDeptByUsername\n        - /sysStaff/getStaffByPhone\n        - /sysStaff/odin\n        - /sysDictItem/item/list\n\nspring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    druid:\n      driver-class-name: com.mysql.cj.jdbc.Driver\n      username: root\n      password: radar\n      url: jdbc:mysql://localhost:3306/hip_upms?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true&allowPublicKeyRetrieval=true\n      stat-view-servlet:\n        enabled: true\n        allow: \"\"\n        url-pattern: /druid/*\n      filter:\n        stat:\n          enabled: true\n          log-slow-sql: true\n          slow-sql-millis: 10000\n          merge-sql: false\n#        wall:\n#          config:\n#            multi-statement-allow: true\n\nfile:\n  bucketName: hip-bucket\n  oss:\n    enable: true\n    path-style-access: false\n    endpoint: http://192.168.20.199:9000\n    access-key: admin\n    secret-key: admin123\n\nlogging:\n  level:\n    work.metanet.feng.admin.mapper: debug\n\nhip:\n  organ:\n    column: organ_code\n    tables:\n      - sys_department\n      - sys_staff\n      - sys_datasource\n\nbi: \n token: FN6pZIQG0PZFzUfolJQnsGsZeRPPxLFuavSKCauhDq4=', '52dc07ef815c5bb4ee647ee728290a9e', '2025-02-21 00:31:38', '2025-02-20 16:31:39', '', '0:0:0:0:0:0:0:1', 'I', 'dev', '');
INSERT INTO `his_config_info` VALUES (0, 7, 'feng-gateway2-dev.yml', 'DEFAULT_GROUP', '', 'gateway:\n  encode-key: \'storm-winds-rain\'\n\nswagger:\n  enabled: true\n\nribbon:\n  rule:\n    gray-enabled: true\n\naj:\n  captcha:\n    water-mark: feng\n    click-offset: 30\n    font-size: 25\n    font-type: WenQuanZhengHei.ttf\nht:\n  captcha:\n    type: circle\n    height: 10\n    width: 40\n    interfereCount: 3\n    textAlpha: 0.3\n    expireSeconds: 300\n    \n    code:\n      type: random  # or math\n      length: 6\n    font:\n      name: Arial\n      size: 24\n      weight: 2\nlogging:\n  level:\n    work.metanet.feng.gateway: DEBUG', 'ff6152887d15177821329e85e2bff075', '2025-02-21 00:31:38', '2025-02-20 16:31:39', '', '0:0:0:0:0:0:0:1', 'I', 'dev', '');
INSERT INTO `his_config_info` VALUES (0, 8, 'feng-erp-biz-dev.yml', 'DEFAULT_GROUP', '', 'security:\n  oauth2:\n    client:\n      client-id: ENC(HhmU+VE7Wz7FoBp+zGJEMA==)\n      client-secret: ENC(HhmU+VE7Wz7FoBp+zGJEMA==)\n      scope: server\n      ignore-urls:\n        - /error\n        - /druid/**\n        - /actuator/**\n        - /v2/api-docs\n        - /report/v2/api-docs\n        - /sysDepartment/getDepartmentById\n        - /sysDepartment/odin\n        - /sysStaffDept/getDeptsByStaffIdAndBi\n        - /sysStaffDept/odin\n        - /sysStaffDept/getDeptsByPraNo\n        - /sysStaff/getRoleAndDeptByUsername\n        - /sysStaff/getStaffByPhone\n        - /sysStaff/odin\n        - /sysDictItem/item/list\nspring:\n  freemarker:\n    allow-request-override: false\n    allow-session-override: false\n    cache: true\n    charset: UTF-8\n    check-template-location: true\n    content-type: text/html\n    enabled: true\n    expose-request-attributes: false\n    expose-session-attributes: false\n    expose-spring-macro-helpers: true\n    prefer-file-system-access: true\n    suffix: .ftl\n    template-loader-path: classpath:/templates/\n  datasource:\n    dynamic:\n      primary: mysql\n      strict: false\n      datasource:\n        mysql:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          username: root\n          password: radar\n          url: jdbc:mysql://127.0.0.1:3306/feng-erp-biz?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true&allowPublicKeyRetrieval=true\n          type: com.alibaba.druid.pool.DruidDataSource\n\n      druid:\n        initialSize: 5\n        minIdle: 10\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        maxEvictableIdleTimeMillis: 900000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        webStatFilter:\n          enabled: true\n        stat-view-servlet:\n          enabled: true\n          allow: \"\"\n          url-pattern: /druid/*\n        filter:\n          stat:\n            enabled: true\n            log-slow-sql: true\n            slow-sql-millis: 10000\n            merge-sql: false\n          wall:\n            config:\n              multi-statement-allow: true\n  mail:\n    host: smtp.qq.com\n    username: 1509515977@qq.com\n    password: \n    properties.mail.smtp.auth: true\n    properties.mail.smtp.starttls.enable: true\n    default-encoding: utf-8\n\n  elasticsearch:\n    uris: [\"http://elastic:rtyl123456@192.168.20.244:9202\",\"http://elastic:rtyl123456@192.168.20.200:9202\"]\n\n  kafka:\n    bootstrap-servers: 192.168.20.199:9092\n    producer:\n      key-serializer: org.apache.kafka.common.serialization.StringSerializer\n      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer\n      retries: 0\n      batch-size: 16384\n      buffer-memory: 33554432\n\n    consumer:\n      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer\n      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer\n      group-id: log_consumer_group\n      enable-auto-commit: true\n      auto-commit-interval: 1000\n      acks: 0\n      properties:\n        spring:\n          json:\n            trusted:\n              packages: \'*\'\n\nfile:\n  bucketName: feng-bucket\n  oss:\n    enable: true\n    path-style-access: false\n    endpoint: http://127.0.0.1:9000\n    access-key: admin\n    secret-key: admin123\n\nlogging:\n  level:\n    work.metanet.feng.admin: DEBUG\n    work.metanet.feng.common: DEBUG\n    work.metanet.feng.log.mapper: DEBUG\n    work.metanet.feng.msg.mapper: DEBUG\n    com.baomidou.dynamic.datasource: INFO\n    com.alibaba.druid: INFO\n    org.springframework: INFO\n    root: INFO\n\nfeng:\n  organ:\n    column: organ_code\n    tables:\n      - sys_department\n      - sys_staff\n      - sys_datasource\n  websocket:\n    lifespan: 90\nbi: \n  token: FN6pZIQG0PZFzUfolJQnsGsZeRPPxLFuavSKCauhDq4=\n\nwx:\n  miniapp:\n    appid: \n    secret: \n\n  encode-key: \'storm-winds-rain\'\n\n\nswagger:\n  enabled: true\nribbon:\n  rule:\n    gray-enabled: true\n\naj:\n  captcha:\n    water-mark: feng\n    click-offset: 30\n    font-size: 25\n    font-type: WenQuanZhengHei.ttf\nht:\n  captcha:\n    type: circle\n    height: 10\n    width: 40\n    interfereCount: 3\n    textAlpha: 0.3\n    expireSeconds: 300\n    \n    code:\n      type: random  # 或 math\n      length: 6\n    font:\n      name: Arial\n      size: 24\n      weight: 2\n      \ndebug: true\n', '902f3a7bcad579dfb00adcab6c71faef', '2025-02-21 00:31:38', '2025-02-20 16:31:39', '', '0:0:0:0:0:0:0:1', 'I', 'dev', '');

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'role',
  `resource` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'resource',
  `action` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'action',
  UNIQUE INDEX `uk_role_permission`(`role`, `resource`, `action`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permissions
-- ----------------------------

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'username',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'role',
  UNIQUE INDEX `idx_user_role`(`username`, `role`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------

-- ----------------------------
-- Table structure for tenant_capacity
-- ----------------------------
DROP TABLE IF EXISTS `tenant_capacity`;
CREATE TABLE `tenant_capacity`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数',
  `max_aggr_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '租户容量信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tenant_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for tenant_info
-- ----------------------------
DROP TABLE IF EXISTS `tenant_info`;
CREATE TABLE `tenant_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_info_kptenantid`(`kp`, `tenant_id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'tenant_info' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tenant_info
-- ----------------------------
INSERT INTO `tenant_info` VALUES (8, '1', 'dev', 'dev', 'dev', 'nacos', 1739271652743, 1739271652743);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'username',
  `password` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'password',
  `enabled` tinyint(1) NOT NULL COMMENT 'enabled',
  PRIMARY KEY (`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', 1);

SET FOREIGN_KEY_CHECKS = 1;

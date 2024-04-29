-- Active: 1712216087041@@127.0.0.1@3306@oasys

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `department` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '部门名称',
  `role` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '职位名称',
  `is_applicant` BOOLEAN NOT NULL COMMENT '是否为申请人',
  `is_approver` BOOLEAN NOT NULL COMMENT '是否为审批人',
  `is_operator` BOOLEAN NOT NULL COMMENT '是否为操作人',
  PRIMARY KEY (`id`),
  UNIQUE KEY (`department`, `role`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Record of permission
-- ----------------------------
INSERT INTO `permission` VALUES (1,'研发部', '前端工程师', 1, 0, 1);
INSERT INTO `permission` VALUES (2,'研发部', '后端工程师', 1, 0, 1);
INSERT INTO `permission` VALUES (3,'研发部', '算法工程师', 1, 0, 1);
INSERT INTO `permission` VALUES (4,'研发部', '产品经理', 1, 0, 1);
INSERT INTO `permission` VALUES (5,'研发部', '项目经理', 1, 1, 0);
INSERT INTO `permission` VALUES (6,'研发部', '部门主管', 1, 1, 0);
INSERT INTO `permission` VALUES (7,'测试部', '测试工程师', 1, 0, 1);
INSERT INTO `permission` VALUES (8,'测试部', '测试架构师', 1, 0, 1);
INSERT INTO `permission` VALUES (9,'测试部', '项目经理', 1, 1, 0);
INSERT INTO `permission` VALUES (10,'测试部', '部门主管', 1, 1, 0);
INSERT INTO `permission` VALUES (11,'运维部', '运维工程师', 1, 0, 1);
INSERT INTO `permission` VALUES (12,'运维部', '安全工程师', 1, 1, 1);
INSERT INTO `permission` VALUES (13,'运维部', '项目经理', 1, 1, 0);
INSERT INTO `permission` VALUES (14,'运维部', '部门主管', 1, 1, 0);
INSERT INTO `permission` VALUES (15,'财务部', '会计', 1, 0, 1);
INSERT INTO `permission` VALUES (16,'财务部', '出纳', 1, 0, 1);
INSERT INTO `permission` VALUES (17,'财务部', '财务助理', 1, 1, 1);
INSERT INTO `permission` VALUES (18,'财务部', '财务经理', 1, 1, 0);
INSERT INTO `permission` VALUES (19,'财务部', '财务总监', 1, 1, 0);
INSERT INTO `permission` VALUES (20,'行政部', 'HR专员', 1, 0, 1);
INSERT INTO `permission` VALUES (21,'行政部', '行政助理', 1, 1, 1);
INSERT INTO `permission` VALUES (22,'行政部', '办公室主任', 1, 1, 0);
INSERT INTO `permission` VALUES (23,'市场部', '销售专员', 1, 0, 1);
INSERT INTO `permission` VALUES (24,'市场部', '采购专员', 1, 1, 1);
INSERT INTO `permission` VALUES (25,'市场部', '部门经理', 1, 1, 1);
INSERT INTO `permission` VALUES (26,'市场部', '市场总监', 1, 1, 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '姓名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `email` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '邮箱',
  `phone` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '电话',
  `city` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '城市',
  `permission_id` BIGINT(20) NOT NULL COMMENT '权限id',
  PRIMARY KEY (`id`),
  UNIQUE KEY (`name`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '申请人' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Record of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'user1', '123456', 'user1@163.com', '10086', '北京', 1);
INSERT INTO `user` VALUES (2, 'user2', '123456', 'user2@163.com', '10086', '北京', 2);
INSERT INTO `user` VALUES (3, 'user3', '123456', 'user3@163.com', '10086', '北京', 3);
INSERT INTO `user` VALUES (4, 'user4', '123456', 'user4@163.com', '10086', '北京', 4);
INSERT INTO `user` VALUES (5, 'user5', '123456', 'user5@163.com', '10086', '北京', 5);
INSERT INTO `user` VALUES (6, 'user6', '123456', 'user6@163.com', '10086', '北京', 6);
INSERT INTO `user` VALUES (7, 'user7', '123456', 'user7@163.com', '10086', '北京', 7);
INSERT INTO `user` VALUES (8, 'user8', '123456', 'user8@163.com', '10086', '北京', 8);
INSERT INTO `user` VALUES (9, 'user9', '123456', 'user9@163.com', '10086', '北京', 9);
INSERT INTO `user` VALUES (10, 'user10', '123456', 'user10@163.com', '10086', '北京', 10);
INSERT INTO `user` VALUES (11, 'user11', '123456', 'user11@163.com', '10086', '北京', 11);
INSERT INTO `user` VALUES (12, 'user12', '123456', 'user12@163.com', '10086', '北京', 12);
INSERT INTO `user` VALUES (13, 'user13', '123456', 'user13@163.com', '10086', '北京', 13);
INSERT INTO `user` VALUES (14, 'user14', '123456', 'user14@163.com', '10086', '北京', 14);
INSERT INTO `user` VALUES (15, 'user15', '123456', 'user15@163.com', '10086', '北京', 15);
INSERT INTO `user` VALUES (16, 'user16', '123456', 'user16@163.com', '10086', '北京', 16);
INSERT INTO `user` VALUES (17, 'user17', '123456', 'user17@163.com', '10086', '北京', 17);
INSERT INTO `user` VALUES (18, 'user18', '123456', 'user18@163.com', '10086', '北京', 18);
INSERT INTO `user` VALUES (19, 'user19', '123456', 'user19@163.com', '10086', '北京', 19);
INSERT INTO `user` VALUES (20, 'user20', '123456', 'user20@163.com', '10086', '北京', 20);
INSERT INTO `user` VALUES (21, 'user21', '123456', 'user21@163.com', '10086', '北京', 21);
INSERT INTO `user` VALUES (22, 'user22', '123456', 'user22@163.com', '10086', '北京', 22);
INSERT INTO `user` VALUES (23, 'user23', '123456', 'user23@163.com', '10086', '北京', 23);
INSERT INTO `user` VALUES (24, 'user24', '123456', 'user24@163.com', '10086', '北京', 24);
INSERT INTO `user` VALUES (25, 'user25', '123456', 'user25@163.com', '10086', '北京', 25);
INSERT INTO `user` VALUES (26, 'user26', '123456', 'user26@163.com', '10086', '北京', 26);

-- ----------------------------
-- Table structure for applicant_processInstance
-- ----------------------------
DROP TABLE IF EXISTS `applicant_processinstance`;
CREATE Table `applicant_processinstance` (
  `applicant_id` BIGINT(20) NOT NULL COMMENT '申请人id',
  `processinstance_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '流程实例id',
  PRIMARY KEY (`applicant_id`,`processinstance_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT ='申请人-流程实例' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for approver_processInstance
-- ----------------------------
DROP TABLE IF EXISTS `approver_processinstance`;
CREATE Table `approver_processinstance` (
  `approver_id` BIGINT(20) NOT NULL COMMENT '审批人id',
  `processinstance_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '流程实例id',
  PRIMARY KEY (`approver_id`,`processinstance_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT ='审批人-流程实例' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for operator_processInstance
-- ----------------------------
DROP TABLE IF EXISTS `operator_processinstance`;
CREATE Table `operator_processinstance` (
  `operator_id` BIGINT(20) NOT NULL COMMENT '操作人id',
  `processinstance_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '流程实例id',
  PRIMARY KEY (`operator_id`,`processinstance_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT ='操作人-流程实例' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for administrator
-- ----------------------------
DROP TABLE IF EXISTS `administrator`;
CREATE TABLE `administrator` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '姓名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `email` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '邮箱',
  `phone` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '电话',
  PRIMARY KEY (`id`),
  UNIQUE KEY (`name`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT ='管理员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Record of administrator
-- ----------------------------
INSERT INTO `administrator` VALUES (1, 'admin1', '123456', 'admin1@163.com', '10086');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分组名称',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '分组描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY (`name`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT ='分组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Record of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '研发部', '研发部');
INSERT INTO `category` VALUES (2, '测试部', '测试部');
INSERT INTO `category` VALUES (3, '运维部', '运维部');
INSERT INTO `category` VALUES (4, '财务部', '财务部');
INSERT INTO `category` VALUES (5, '行政部', '行政部');
INSERT INTO `category` VALUES (6, '市场部', '市场部');
INSERT INTO `category` VALUES (7, '申请人', '申请人');
INSERT INTO `category` VALUES (8, '审批人', '审批人');
INSERT INTO `category` VALUES (9, '操作人', '操作人');
INSERT INTO `category` VALUES (10, '基层员工', '基层员工');
INSERT INTO `category` VALUES (11, '部门组长', '部门组长');
INSERT INTO `category` VALUES (12, '部门主管', '部门主管');

-- ----------------------------
-- Table structure for permission_category
-- ----------------------------
DROP TABLE IF EXISTS `permission_category`;
CREATE TABLE `permission_category` (
  `permission_id` bigint(20) NOT NULL COMMENT '权限id',
  `category_id` bigint(20) NOT NULL COMMENT '分组id',
  PRIMARY KEY (`permission_id`,`category_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限-分组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Record of permission_category
-- ----------------------------
INSERT INTO `permission_category` VALUES (1, 1);
INSERT INTO `permission_category` VALUES (2, 1);
INSERT INTO `permission_category` VALUES (3, 1);
INSERT INTO `permission_category` VALUES (4, 1);
INSERT INTO `permission_category` VALUES (5, 1);
INSERT INTO `permission_category` VALUES (6, 1);
INSERT INTO `permission_category` VALUES (7, 2);
INSERT INTO `permission_category` VALUES (8, 2);
INSERT INTO `permission_category` VALUES (9, 2);
INSERT INTO `permission_category` VALUES (10, 2);
INSERT INTO `permission_category` VALUES (11, 3);
INSERT INTO `permission_category` VALUES (12, 3);
INSERT INTO `permission_category` VALUES (13, 3);
INSERT INTO `permission_category` VALUES (14, 3);
INSERT INTO `permission_category` VALUES (15, 4);
INSERT INTO `permission_category` VALUES (16, 4);
INSERT INTO `permission_category` VALUES (17, 4);
INSERT INTO `permission_category` VALUES (18, 4);
INSERT INTO `permission_category` VALUES (19, 4);
INSERT INTO `permission_category` VALUES (20, 5);
INSERT INTO `permission_category` VALUES (21, 5);
INSERT INTO `permission_category` VALUES (22, 5);
INSERT INTO `permission_category` VALUES (23, 6);
INSERT INTO `permission_category` VALUES (24, 6);
INSERT INTO `permission_category` VALUES (25, 6);
INSERT INTO `permission_category` VALUES (26, 6);

INSERT INTO `permission_category` VALUES (5, 8);
INSERT INTO `permission_category` VALUES (6, 8);
INSERT INTO `permission_category` VALUES (9, 8);
INSERT INTO `permission_category` VALUES (10, 8);
INSERT INTO `permission_category` VALUES (12, 8);
INSERT INTO `permission_category` VALUES (13, 8);
INSERT INTO `permission_category` VALUES (14, 8);
INSERT INTO `permission_category` VALUES (17, 8);
INSERT INTO `permission_category` VALUES (18, 8);
INSERT INTO `permission_category` VALUES (19, 8);
INSERT INTO `permission_category` VALUES (21, 8);
INSERT INTO `permission_category` VALUES (22, 8);
INSERT INTO `permission_category` VALUES (24, 8);
INSERT INTO `permission_category` VALUES (25, 8);
INSERT INTO `permission_category` VALUES (26, 8);
INSERT INTO `permission_category` VALUES (1, 9);
INSERT INTO `permission_category` VALUES (2, 9);
INSERT INTO `permission_category` VALUES (3, 9);
INSERT INTO `permission_category` VALUES (4, 9);
INSERT INTO `permission_category` VALUES (7, 9);
INSERT INTO `permission_category` VALUES (8, 9);
INSERT INTO `permission_category` VALUES (11, 9);
INSERT INTO `permission_category` VALUES (12, 9);
INSERT INTO `permission_category` VALUES (15, 9);
INSERT INTO `permission_category` VALUES (16, 9);
INSERT INTO `permission_category` VALUES (17, 9);
INSERT INTO `permission_category` VALUES (20, 9);
INSERT INTO `permission_category` VALUES (21, 9);
INSERT INTO `permission_category` VALUES (23, 9);
INSERT INTO `permission_category` VALUES (24, 9);
INSERT INTO `permission_category` VALUES (25, 9);

INSERT INTO `permission_category` VALUES (1, 10);
INSERT INTO `permission_category` VALUES (2, 10);
INSERT INTO `permission_category` VALUES (3, 10);
INSERT INTO `permission_category` VALUES (7, 10);
INSERT INTO `permission_category` VALUES (8, 10);
INSERT INTO `permission_category` VALUES (11, 10);
INSERT INTO `permission_category` VALUES (12, 10);
INSERT INTO `permission_category` VALUES (15, 10);
INSERT INTO `permission_category` VALUES (16, 10);
INSERT INTO `permission_category` VALUES (17, 10);
INSERT INTO `permission_category` VALUES (20, 10);
INSERT INTO `permission_category` VALUES (23, 10);
INSERT INTO `permission_category` VALUES (24, 10);

INSERT INTO `permission_category` VALUES (5, 11);
INSERT INTO `permission_category` VALUES (9, 11);
INSERT INTO `permission_category` VALUES (13, 11);
INSERT INTO `permission_category` VALUES (18, 11);
INSERT INTO `permission_category` VALUES (21, 11);
INSERT INTO `permission_category` VALUES (25, 11);

INSERT INTO `permission_category` VALUES (6, 12);
INSERT INTO `permission_category` VALUES (10, 12);
INSERT INTO `permission_category` VALUES (14, 12);
INSERT INTO `permission_category` VALUES (19, 12);
INSERT INTO `permission_category` VALUES (22, 12);
INSERT INTO `permission_category` VALUES (26, 12);


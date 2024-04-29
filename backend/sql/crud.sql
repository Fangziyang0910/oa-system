-- Active: 1712216087041@@127.0.0.1@3306@oasys
SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- operation on user
-- ----------------------------
SELECT * FROM user;
-- 使用场景：用户登录、用户信息查询
SELECT * FROM user WHERE name='user1';
-- 使用场景：用户注册
INSERT INTO user VALUES (27, 'user27', '123456', 'user27@163.com', '10086', '北京', 1);

-- ----------------------------
-- operation on permission
-- ----------------------------
-- 使用场景：任务委派
SELECT * FROM permission WHERE department='研发部';
-- 使用场景：审批委派
SELECT * FROM permission WHERE is_applicant=1 AND department='研发部';
-- 使用场景：操作委派
SELECT * FROM permission WHERE is_operator=1 AND department='研发部';
-- 使用场景：新增权限
INSERT INTO permission(department,role,is_applicant,is_approver,is_operator) VALUES('研发部','算法工程师'1,0,1);

-- ----------------------------
-- operation on user_permission
-- ----------------------------
-- 使用场景：用户详细信息
SELECT *
FROM `user`,`permission`
WHERE `user`.id=permission.id AND `user`.name='user1';

-- ----------------------------
-- operation on applicant_processInstace
-- ----------------------------
-- 使用场景：查询申请人流程实例
SELECT * FROM applicant_processinstance WHERE applicant_id='1';

-- ----------------------------
-- operation on approver_processInstace
-- ----------------------------
-- 使用场景：查询审批人流程实例
SELECT * FROM approver_processinstance WHERE approver_id='1';

-- ----------------------------
-- operation on operator_processInstance
-- ----------------------------
-- 使用场景：查询操作人流程实例
SELECT * FROM operator_processinstance WHERE operator_id='1';
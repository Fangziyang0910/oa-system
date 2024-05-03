# 项目介绍

**课题方向**：运维工单系统的开发

**课题背景介绍**
目前企业有大量的运维工作，为了规范生产和管理，需要严格按单施工，逐层审批，操作留痕，需要通过系统进行管理。
为此需要开发一个运维工单管理系统，支持省市各部门发起维护工单申请（如故障、需求等），然后经过各层级审批，通过后提交到操作单位，操作人员执行后及时回单。每个操作人员登录后及时看到各自待办工单信息，也可以派单到其他单位协助处理。每日每周提供工单流转完成情况统计报表和超时预警工单。需要支持电脑WEB门户和手机APP门户等使用模式。

**参考资料推荐：**

工作流引擎
**硬件要求：**
PC机 1台：4C 8GB 内存 20GB硬盘
智能手机 1台
**数据要求：**
无
**测试环境：**
连接互联网

# 项目进度

## 可行性分析

## 项目计划

指定开发计划，确定项目的大小、范围、复杂程度，分配任务，安排进度，确定环境和工具

## 需求分析

完成《需求分析规格说明书》

- 用例视图（用例图），逻辑视图（ER图/类图），进程视图（活动图/顺序图），实现视图（部件图），部署视图（部署图）

- 参与者：各部门单位、各级审批单位、操作人员单位
- 用例：
    - 使用部门提交运维申请
    - 各级部门审批申请
    - 操作人员回复工单
    - 操作人员分派工单
        - 自动派单
        - 管理员派单
        - 执行人员抢单
    - 操作人员查看统计报表
    - 操作人员查看超时预警工单

## 概要设计

完成《概要设计文档》

## 详细设计

完成《详细设计说明书》

```
backend
    |__src
    |   |__main
    |   |   |__java
    |   |   |   |__com.whaler.oasys
    |   |   |       |__config
    |   |   |       |__model
    |   |   |       |   |__param
    |   |   |       |   |   |__LoginParam.java
    |   |   |       |   |   |__UserParam.java
    |   |   |       |   |   |__GroupParam.java
    |   |   |       |   |__entity
    |   |   |       |   |   |__BaseEntity.java
    |   |   |       |   |   |__UserEntity.java
    |   |   |       |   |   |__PermissionEntity.java
    |   |   |       |   |   |__ApplicantEntity.java
    |   |   |       |   |   |__ApprovalEntity.java
    |   |   |       |   |   |__OperatorEntity.java
    |   |   |       |   |   |__AdministratorEntity.java
    |   |   |       |   |   |__UserPermissionEntity.java
    |   |   |       |   |   |__GroupEntity.java
    |   |   |       |   |__vo
    |   |   |       |   |   |__ResultVo.java
    |   |   |       |   |   |__UserVo.java
    |   |   |       |   |   |__ApplicantVo.java
    |   |   |       |   |   |__ApproverVo.java
    |   |   |       |   |   |__OperatorVo.java
    |   |   |       |   |   |__GroupVo.java
    |   |   |       |   |   |__ProcessDefinitionVo.java
    |   |   |       |   |   |__ProcessInstanceVo.java
    |   |   |       |   |   |__TaskVo.java
    |   |   |       |   |   |__FormVo.java
    |   |   |       |   |   |__FormInfoVo.java
    |   |   |       |   |__enums
    |   |   |       |   |   |__ResultCode.java
    |   |   |       |   |__exception
    |   |   |       |   |   |__ApiException.java
    |   |   |       |__security
    |   |   |       |   |__UserContext.java
    |   |   |       |   |__JwtManager.java
    |   |   |       |   |__LoginInterceptor.java
    |   |   |       |   |__AuthInterceptor.java
    |   |   |       |__task
    |   |   |       |__mapper
    |   |   |       |   |__AdministratorMapper.java
    |   |   |       |   |__UserMapper.java
    |   |   |       |   |__PermissionMapper.java
    |   |   |       |   |__ApplicantMapper.java
    |   |   |       |   |__ApproverMapper.java
    |   |   |       |   |__OperatorMapper.java
    |   |   |       |__service
    |   |   |       |   |__impl
    |   |   |       |   |   |__AdministratorServiceImpl.java
    |   |   |       |   |   |__UserServiceImpl.java
    |   |   |       |   |   |__PermissionServiceImpl.java
    |   |   |       |   |   |__ApplicantServiceImpl.java
    |   |   |       |   |   |__ApproverServiceImpl.java
    |   |   |       |   |   |__OperatorServiceImpl.java
    |   |   |       |   |   |__IdmGroupService.java
    |   |   |       |   |__AdministratorService.java
    |   |   |       |   |__UserService.java
    |   |   |       |   |__PermissionService.java
    |   |   |       |   |__ApplicantService.java
    |   |   |       |   |__ApproverService.java
    |   |   |       |   |__OperatorService.java
    |   |   |       |   |__IdmGroupService.java
    |   |   |       |__controller
    |   |   |       |   |__api
    |   |   |       |   |__ResultControllerAdvice.java
    |   |   |       |   |__ExceptionControllerAdvice.java
    |   |   |       |__Main.java
    |   |   |__resource
    |   |       |__mapper
    |   |       |   |__ApplicantMapper.xml
    |   |       |   |__ApproverMapper.xml
    |   |       |   |__OperatorMapper.xml
    |   |       |   |__AdministratorMapper.xml
    |   |       |__processes
    |   |       |   |__leaveProcess.bpmn20.xml
    |   |       |__forms
    |   |       |   |__leaveAsk.form
    |   |       |   |__leaderApproval.form
    |   |       |   |__managerApproval.form
    |   |       |   |__.form
    |   |       |__application.yml
    |   |__test
    |       |__java.com.whaler.oasys
    |           |__mapper
    |           |__service
    |           |__controller
    |__target
    |__sql
    |   |__oasys.sql
    |__pom.xml
```

## 编码

确定开发语言，遵循代码规范、命名规范、注释规范

 
- 详细设计和编码一起进行，先建立数据库表，确定了用户表，权限表，用户-权限表，申请人-流程实例表，审批人-任务表，执行人-任务表🆗
    - 建立基本的管理系统，包括用户登录、会话信息保存、用户信息获取，entity类编码（mapper层存储结果，包括连表结果）🆗，mapper层及其单元测试🆗，vo类编码（一对多的实体关系，service层，返回结果）、param类（controller层，传入参数）🆗完成登录认证拦截器🆗，service层基础功能及其单元测试🆗，用户相关基础controller🆗，拦截配置、全局异常处理，mock模拟接口测试🆗
    - 增加了角色组表，表示角色和角色组的多对多的关系🆗
- 开发关键业务，明确需求并编写接口，测试挂载表单。先预设流程和表单，做好部署，根据需求编码数据结构和处理方法，
    - 管理员上传部署流程和表单🆗，返回部署结果🆗
    - 审批人查询所有任务（包括assign和candidate）🆗，获取开始表单🆗，获取任务表单🆗，完成任务🆗
    - 操作人查询所有任务（包括assign和candidate）🆗，获取开始表单🆗，获取任务表单🆗，完成任务🆗，派单🆗
    - 申请人查询流程模板🆗，选择流程创建实例🆗，查询工单模板🆗，提交工单🆗，查询流程进度🆗，获取流程状态🆗，获取回单🆗
    - 每日、每周提供工单流转完成情况统计报表、超时预警工单，统计流程（完成的、未完成的、超时的），超时预警？发送邮件提醒候选人来处理

## 测试

制定测试数据，完成《测试说明书》

## 接口文档API使用说明
- 本项目使用 swagger 构建接口文档，前端测试环境搭建步骤如下
    - 根据 `/backend/sql/oasys.sql` 创建数据库 `oasys`
    - 在 idea 中导入项目，直接运行 `Main.java`
    - 访问 `http://localhost:8080/swagger-ui.html`
    - 注意，每次访问需要携带令牌，需要先登录获取令牌，然后在请求头 `Authorization` 手动添加令牌（令牌可以认为不会过期）

⚠ 注意：请将 idea 的配置文件、前端的npm包加入到 .gitignore 中，不要提交到 git

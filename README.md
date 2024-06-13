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

| 工作任务       | 开始日期 | 完成日期 | 所需资源 | 里程碑事件                         |
| ------------------------ | ------------------ | ------------------ | ------------------ | -------------------------------------------- |
| 需求分析       | 2024年4月1日       | 2024年4月7日       | 用户调研工具       | 需求说明书完成并通过用户确认                 |
| 系统设计       | 2024年4月8日       | 2024年4月14日      | 设计工具           | 系统设计方案完成并通过内部评审               |
| 编码实现       | 2024年4月15日      | 2024年5月25日      | 开发工具、IDE      | 前端和后端代码完成，并通过单元测试           |
| 测试           | 2024年5月26日      | 2024年6月5日       | 测试工具           | 系统通过集成测试和用户验收测试               |
| 文件编制       | 2024年6月5日       | 2024年6月15日      | 文档工具           | 所有文件编制完成并通过审批                   |
| 用户培训       | 2024年6月16日      | 2024年6月25日      | 培训材料           | 用户培训完成，用户能够熟练操作系统           |
| 软件安装和维护 | 2024年6月26日      | 2024年6月30日      | 安装工具           | 系统安装完成，系统正常运行，并提供必要的维护 |

## 项目结构

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
    |   |   |       |   |   |__CategoryEntity.java
    |   |   |       |   |   |__ReportEntity.java
    |   |   |       |   |__vo
    |   |   |       |   |   |__ResultVo.java
    |   |   |       |   |   |__UserVo.java
    |   |   |       |   |   |__ApplicantVo.java
    |   |   |       |   |   |__ApproverVo.java
    |   |   |       |   |   |__OperatorVo.java
    |   |   |       |   |   |__CategoryVo.java
    |   |   |       |   |   |__ProcessDefinitionVo.java
    |   |   |       |   |   |__ProcessInstanceVo.java
    |   |   |       |   |   |__TaskVo.java
    |   |   |       |   |   |__FormVo.java
    |   |   |       |   |   |__FormFieldVo.java
    |   |   |       |   |   |__CategoryVo.java
    |   |   |       |   |   |__MsgVo.java
    |   |   |       |   |   |__ReportVo.java
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
    |   |   |       |   |__LeaveAskAssignDelegate.java
    |   |   |       |   |__SetLeaderDelegate.java
    |   |   |       |   |__SetManagerDelegate.java
    |   |   |       |   |__TimeoutWarningDelegate.java
    |   |   |       |   |__EndExecutionListener.java
    |   |   |       |   |__AbortExecutionListener.java
    |   |   |       |   |__TimeoutWarningListener.java
    |   |   |       |__mapper
    |   |   |       |   |__AdministratorMapper.java
    |   |   |       |   |__UserMapper.java
    |   |   |       |   |__PermissionMapper.java
    |   |   |       |   |__ApplicantMapper.java
    |   |   |       |   |__ApproverMapper.java
    |   |   |       |   |__OperatorMapper.java
    |   |   |       |   |__CategoryMapper.java
    |   |   |       |__service
    |   |   |       |   |__impl
    |   |   |       |   |   |__AdministratorServiceImpl.java
    |   |   |       |   |   |__UserServiceImpl.java
    |   |   |       |   |   |__PermissionServiceImpl.java
    |   |   |       |   |   |__ApplicantServiceImpl.java
    |   |   |       |   |   |__ApproverServiceImpl.java
    |   |   |       |   |   |__OperatorServiceImpl.java
    |   |   |       |   |   |__CategoryServiceImpl.java
    |   |   |       |   |   |__ReportServiceImpl.java
    |   |   |       |   |   |__ScheduleServiceImpl.java
    |   |   |       |   |__AdministratorService.java
    |   |   |       |   |__UserService.java
    |   |   |       |   |__PermissionService.java
    |   |   |       |   |__ApplicantService.java
    |   |   |       |   |__ApproverService.java
    |   |   |       |   |__OperatorService.java
    |   |   |       |   |__CategoryService.java
    |   |   |       |   |__ReportService.java
    |   |   |       |   |__ScheduleService.java
    |   |   |       |__controller
    |   |   |       |   |__api
    |   |   |       |   |   |__UserContorller.java
    |   |   |       |   |   |__AdministratorContorller.java
    |   |   |       |   |   |__ApplicantContorller.java
    |   |   |       |   |   |__ApprovalContorller.java
    |   |   |       |   |   |__OperatorContorller.java
    |   |   |       |   |   |__PermisssionContorller.java
    |   |   |       |   |__ResultControllerAdvice.java
    |   |   |       |   |__ExceptionControllerAdvice.java
    |   |   |       |__controller
    |   |   |       |   |__MyBpmnModelModifier.java
    |   |   |       |   |__MyDefaultProcessDiagramCanvas.java
    |   |   |       |   |__MyDefaultProcessDiagramGenerator.java
    |   |   |       |   |__MyMsgSender.java
    |   |   |       |__Main.java
    |   |   |__resource
    |   |       |__mapper
    |   |       |   |__AdministratorMapper.xml
    |   |       |   |__UserMapper.xml
    |   |       |   |__PermissionMapper.xml
    |   |       |   |__ApplicantMapper.xml
    |   |       |   |__ApproverMapper.xml
    |   |       |   |__OperatorMapper.xml
    |   |       |   |__CategoryMapper.xml
    |   |       |__processes
    |   |       |   |__leaveProcess.bpmn20.xml
    |   |       |__forms
    |   |       |   |__leaveAsk.form
    |   |       |   |__leaderApproval.form
    |   |       |   |__managerApproval.form
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

## 配置信息

- Nginx配置

  1. 安装Nginx

     ```
     sudo apt update
     sudo apt install nginx
     ```

  2. 配置反向代理，编辑Nginx配置文件 /etc/nginx/sites-available/default

     ```
     server {
         listen 80;
         server_name example.com;
     
         location / {
             proxy_pass http://localhost:8080;
             proxy_set_header Host $host;
             proxy_set_header X-Real-IP $remote_addr;
             proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
             proxy_set_header X-Forwarded-Proto $scheme;
         }
     }
     ```

  3. 启动Nginx服务

     ```
     sudo systemctl start nginx
     sudo systemctl enable nginx
     ```

- Spring应用程序部署

  1. 运行Spring应用程序

     ```
     mvn springboot:run
     ```

  2. 打包Spring应用程序

     ```
     mvn clean package
     ```

  3. 部署Spring应用程序，使用以下命令启动应用程序：

     ```
     java -jar oa-sys.jar
     ```

- RabbitMQ配置

  1. 安装RabbitMQ

     ```
     sudo apt-get install rabbitmq-server
     ```

  2. 在 application.properties 中配置RabbitMQ

     ```
     spring.rabbitmq.host=localhost
     spring.rabbitmq.port=5672
     spring.rabbitmq.username=guest
     spring.rabbitmq.password=guest
     ```

  3. 启动RabbitMQ服务

     ```
     sudo systemctl start rabbitmq-server
     sudo systemctl enable rabbitmq-server
     ```

- 系统防火墙配置

  1. 开放必要端口

     ```
     sudo firewall-cmd --zone=public --add-port=allow 80/tcp  # Nginx
     sudo firewall-cmd --zone=public --add-port=8080/tcp  # Spring应用程序
     sudo firewall-cmd --zone=public --add-port=5672/tcp  # RabbitMQ
     ```

  2. 启动防火墙

     ```
     sudo firewall-cmd reload
     ```

## 项目文档
```
项目文档
    |__1.可行性研究报告.doc
    |__2.项目开发计划.doc
    |__3.软件需求说明书.doc
    |__4.概要设计说明书.doc
    |__5.详细设计说明书.doc
    |__6.测试分析报告.doc
    |__7.用户手册.doc
    |__8.项目开发总结报告.doc
```
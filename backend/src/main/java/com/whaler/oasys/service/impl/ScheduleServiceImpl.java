package com.whaler.oasys.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whaler.oasys.Main;
import com.whaler.oasys.model.entity.ReportEntity;
import com.whaler.oasys.model.entity.UserEntity;
import com.whaler.oasys.service.CategoryService;
import com.whaler.oasys.service.ReportService;
import com.whaler.oasys.service.ScheduleService;
import com.whaler.oasys.service.UserService;
import com.whaler.oasys.tool.MyMesgSender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleServiceImpl
implements ScheduleService {
    @Autowired
    private HistoryService historyService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private MyMesgSender myMesgSender;
    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.fromMail.address}")
    private String from;

    /**
     * 执行每天定时任务的方法。此方法通过@Scheduled注解使用了cron表达式"0 0 23 * * ?"，
     * 以确保任务每天的23点整执行一次。
     * 该方法不接受参数，也不返回任何值。
     * 主要完成以下任务：
     * 1. 根据指定的时间范围（当日0点至当前时间）获取任务信息。
     * 2. 查询所有的流程定义信息。
     * 3. 遍历流程定义，获取每个流程在指定时间范围内的详细信息。
     * 4. 将任务信息和流程详细信息封装成报告，并以JSON格式存储。
     * 5. 创建日报实体，包括报告标题、内容、创建时间和类型，并插入数据库。
     */
    @Override
    @Scheduled(cron = "0 0 23 * * ?")
    public void dailyScheduledTask(){
        // 初始化开始时间为当日0点
        Calendar start=Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        
        // 使用当前时间作为结束时间
        Calendar end=Calendar.getInstance();

        // 准备存放报告信息的容器
        Map<String,Object>report=new HashMap<>();

        // 获取并封装任务信息
        Map<String,String>taskReport=getTaskInfo(start, end);
        report.put("任务信息", taskReport);

        // 查询所有流程定义信息
        List<ProcessDefinition>processDefinitions=repositoryService.createProcessDefinitionQuery().list();

        // 准备存放流程详细信息的容器
        List<Map<String,String>>subreports=new ArrayList<>();
        for(ProcessDefinition processDefinition:processDefinitions){
            try{
                // 尝试获取每个流程的详细信息并封装
                Map<String,String>subreport=getProcessInfo(processDefinition.getKey(), processDefinition.getName() ,start, end);
                subreports.add(subreport);
            }catch(Exception e){
                // 对异常情况做忽略处理
                continue;
            }
        }
        // 将流程详细信息添加到报告中
        report.put("流程详细信息", subreports);
        
        // 获取当前时间，用于设置报告的创建时间
        LocalDate createTime=end.getTime().toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDate();
        // 设置报告标题和类型
        String title=createTime+"日报";
        String type="日报";

        // 创建报告实体并设置其属性
        ReportEntity reportEntity=new ReportEntity()
            .setTitle(title)
            .setContent(JSON.toJSONString(report))
            .setCreateTime(createTime)
            .setType(type);

        try{
            // 尝试将报告实体插入数据库
            reportService.getBaseMapper().insert(reportEntity);
        }catch(Exception e){
            // 对异常情况做忽略处理
            ;
        }
    }

    /**
     * 定时任务，用于每天晚上执行超时预警。
     * 该任务查询所有在明日前即将超时的任务，并向相关用户发送电子邮件提醒。
     * 无参数和返回值。
     */
    @Override
    @Scheduled(cron = "0 0 23 * * ?")
    public void dueWarningTask(){
        // 获取当前时间并设置为明日晚上23点，用于查询所有在明日前到期的任务
        Calendar tomorrow=Calendar.getInstance();
        tomorrow.setTimeInMillis(tomorrow.getTimeInMillis()+24*60*60*1000);
        HistoricTaskInstanceQuery historicTaskInstanceQuery=historyService.createHistoricTaskInstanceQuery()
            .taskDueBefore(tomorrow.getTime());

        // 查询所有用户，并为每个用户发送即将到期的任务提醒邮件
        LambdaQueryWrapper<UserEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        List<UserEntity>users=userService.getBaseMapper().selectList(lambdaQueryWrapper);
        for (UserEntity userEntity : users) {
            Long userId=userEntity.getId();
            Long permissionId=userEntity.getPermissionId();
            // 根据用户权限查询其可以接收的任务类别ID
            List<String>categoryIds=categoryService.selectCategoryIdsByPermissionId(permissionId)
                .stream().map(categoryId->Long.toString(categoryId)).collect(Collectors.toList());
            // 查询用户即将到期的任务
            List<HistoricTaskInstance>willDueTasks=historicTaskInstanceQuery.or()
                .taskAssignee(userId.toString())
                .taskCandidateUser(permissionId.toString())
                .taskCandidateGroupIn(categoryIds)
                .list();
            // 提取任务名称以便在邮件中显示
            List<String>taskNames=willDueTasks.stream().map(task->task.getTaskDefinitionKey()).collect(Collectors.toList());
            // 如果有即将到期的任务，则发送邮件提醒
            if (taskNames.size()>0) {
                StringBuilder content=new StringBuilder();
                content.append("您有");
                content.append(taskNames.size());
                content.append("个任务即将在一天内超时，请及时处理。");
                content.append("任务名称：\n");
                for (String taskName : taskNames) {
                    content.append(taskName+"\n");
                }
                // 发送邮件提醒
                sendMail(userEntity.getEmail(), "超时提醒",content.toString());
            }
        }
    }

    /**
     * 执行每周定时任务。
     * 通过@Scheduled注解配置，任务将在每周日的凌晨0点执行。
     * 该方法不接受参数，也不返回任何值。
     * 主要完成以下任务：
     * 1. 根据指定的时间范围（过去一周）收集任务信息和流程信息。
     * 2. 将收集到的信息整理成报告，并以JSON格式存储。
     * 3. 生成周报的标题、内容和类型，并保存到数据库中。
     */
    @Override
    @Scheduled(cron = "0 30 11 ? * SUN")
    public void weeklyScheduledTask(){
        // 初始化报告开始时间，设置为本周日凌晨0点
        Calendar start=Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        // 因为要获取过去一周的信息，所以开始时间需设置为上周同一时间
        start.setTimeInMillis(start.getTimeInMillis()-7*24*60*60*1000);
        // 初始化报告结束时间为当前时间
        Calendar end=Calendar.getInstance();

        // 准备报告信息
        Map<String,Object>report=new HashMap<>();

        // 收集任务信息
        Map<String,String>taskReport=getTaskInfo(start, end);
        report.put("任务信息", taskReport);

        // 查询所有流程定义信息
        List<ProcessDefinition>processDefinitions=repositoryService.createProcessDefinitionQuery().list();

        // 遍历流程定义，收集每个流程的详细信息
        List<Map<String,String>>subreports=new ArrayList<>();
        for(ProcessDefinition processDefinition:processDefinitions){
            Map<String,String>subreport=getProcessInfo(processDefinition.getKey(), processDefinition.getName() ,start, end);
            subreports.add(subreport);
        }
        // 将流程详细信息添加到报告中
        report.put("流程详细信息", subreports);
        
        // 生成报告创建时间，格式化为LocalDate
        LocalDate createTime=end.getTime().toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDate();
        // 生成报告标题，格式为"创建时间 周报"
        String title=createTime+"周报";
        String type="周报";
        // 构建报告实体
        ReportEntity reportEntity=new ReportEntity()
            .setTitle(title)
            .setContent(JSON.toJSONString(report))
            .setCreateTime(createTime)
            .setType(type);

        try{
            // 将报告实体插入数据库
            reportService.getBaseMapper().insert(reportEntity);
        }catch(Exception e){
            // 异常处理，此处为空实现
            ;
        }
    }

    /**
     * 执行每5分钟定时任务的函数。
     * 该任务会查询当前所有流程定义和运行中的实例、任务等信息，并将这些信息整理成报告，
     * 之后通过消息方式发送给"admin"用户，同时将消息内容存储到共享数据中。
     * 该任务通过@Scheduled注解配置，每隔5分钟执行一次。
     */
    @Override
    @Scheduled(cron = "0 */5 * * * ?")
    public void adminBoardScheduledTask(){
        // 查询所有流程定义
        List<ProcessDefinition>processDefinitions=repositoryService.createProcessDefinitionQuery().list();
        List<Map<String,String>> reports=new ArrayList<>();
        
        // 遍历每个流程定义，获取并整理相关信息
        processDefinitions.forEach(
            processDefinition->{
                try{
                    // 获取流程定义的名称和键
                    String processDefinitionName=processDefinition.getName();
                    String processDefinitionKey=processDefinition.getKey();
                    
                    // 统计运行中的实例、审批任务、操作任务和超时任务的数量
                    Integer processInstances=runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).list().size();
                    Integer approvalTasks=taskService.createTaskQuery().taskDescription("审批").processDefinitionKey(processDefinitionKey).list().size();
                    Integer operateTasks=taskService.createTaskQuery().taskDescription("操作").processDefinitionKey(processDefinitionKey).list().size();
                    Integer duedTasks=taskService.createTaskQuery().taskDueBefore(
                        Calendar.getInstance().getTime()
                    ).processDefinitionKey(processDefinitionKey).list().size();
                    
                    // 将整理好的信息添加到报告列表中
                    Map<String,String> subreport=new HashMap<>();
                    subreport.put("流程名称", processDefinitionName);
                    subreport.put("运行中的流程实例数量", processInstances.toString());
                    subreport.put("未完成的审批任务数量", approvalTasks.toString());
                    subreport.put("未完成的操作任务数量", operateTasks.toString());
                    subreport.put("已经超时的任务数量", duedTasks.toString());
   
                    reports.add(subreport);
                }catch(Exception e){
                    // 出现异常时，终止当前循环
                    return;
                }
            }
        );

        // 整理汇总信息，并添加到报告中
        Map<String,Object>report=new HashMap<>();
        report.put("流程详细信息", reports);
        
        // 统计全局的运行中的实例、审批任务、操作任务和超时任务的数量
        Integer processInstances=runtimeService.createProcessInstanceQuery().list().size();
        Integer approvalTasks=taskService.createTaskQuery().taskDescription("审批").list().size();
        Integer operateTasks=taskService.createTaskQuery().taskDescription("操作").list().size();
        Integer duedTasks=taskService.createTaskQuery().taskDueBefore(
            Calendar.getInstance().getTime()
        ).list().size();

        // 将全局统计信息添加到报告的汇总部分
        Map<String,String> subreport=new HashMap<>();
        subreport.put("运行中的流程实例数量", processInstances.toString());
        subreport.put("未完成的审批任务数量", approvalTasks.toString());
        subreport.put("未完成的操作任务数量", operateTasks.toString());
        subreport.put("已经超时的任务数量", duedTasks.toString());

        report.put("汇总信息", subreport);

        // 将报告转换成JSON字符串，准备发送消息
        String msg=JSON.toJSONString(report);
        
        // 发送消息给"admin"用户，并将消息内容存储到共享数据中
        myMesgSender.sendMessage("admin", "管理员面板", msg);
        synchronized (Main.sharedData) {
            Main.sharedData.put("msg", msg);
        }
    }


        /**
     * 异步发送邮件的私有方法。
     * 使用Spring的@Async注解，指定使用"multiThreadExecutor"作为任务执行器，使得邮件发送不在当前线程同步执行，提高应用响应性能。
     *
     * @param to 邮件接收人的邮箱地址。
     * @param subject 邮件的主题。
     * @param content 邮件的正文内容。
     * 该方法不返回任何内容。
     */
    @Async("multiThreadExecutor")
    private void sendMail(String to, String subject, String content){
        // 创建一个SimpleMailMessage实例并设置其基本属性
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from); // 设置发件人
        message.setTo(to); // 设置收件人
        message.setSubject(subject); // 设置邮件主题
        message.setText(content); // 设置邮件内容

        try {
            // 尝试发送邮件
            mailSender.send(message);
        } catch (Exception e) {
            // 捕获异常并记录日志
            log.error("发送简单邮件时发生异常！", e);
        }
    }

        /**
     * 获取指定流程定义在一定时间范围内的相关信息统计。
     * 
     * @param processDefinitionKey 流程定义的键。
     * @param processDefinitionName 流程定义的名称。
     * @param start 统计开始的时间。
     * @param end 统计结束的时间。
     * @return 返回一个包含流程名称、创建的流程实例数、完成的流程实例数、未结束的流程实例数以及流程实例平均处理时间的Map。
     */
    private Map<String,String> getProcessInfo(
        String processDefinitionKey,
        String processDefinitionName,
        Calendar start,
        Calendar end
    ) {
        Map<String,String>report=new HashMap<>(); // 创建用于存储统计信息的Map

        // 填充流程名称到报告中
        report.put("流程名称", processDefinitionName);

        // 统计在指定时间范围内创建的流程实例数量
        List<HistoricProcessInstance>createdProcesses=historyService.createHistoricProcessInstanceQuery()
            .processDefinitionKey(processDefinitionKey).startedAfter(start.getTime()).list();
        report.put("创建流程实例数", Integer.toString(createdProcesses.size()));

        // 统计在指定时间范围内已完成的流程实例数量
        List<HistoricProcessInstance>completedProcesses=historyService.createHistoricProcessInstanceQuery()
            .processDefinitionKey(processDefinitionKey).finishedAfter(start.getTime()).list();
        report.put("完成流程实例数", Integer.toString(completedProcesses.size()));

        // 统计在指定时间范围内未结束的流程实例数量
        List<HistoricProcessInstance>uncompletedProcesses=historyService.createHistoricProcessInstanceQuery()
            .processDefinitionKey(processDefinitionKey).unfinished().list();
        report.put("未结束流程实例数", Integer.toString(uncompletedProcesses.size()));

        // 计算并填充流程实例平均处理时间到报告中
        if(completedProcesses.size()>0){
            long totalTime=0;
            for(HistoricProcessInstance process:completedProcesses){
                totalTime+=process.getDurationInMillis();
            }
            Duration duration = Duration.ofSeconds(totalTime);
            String time = String.format("%d 小时 %d 分 %d 秒", 
                duration.toHours(), duration.toMinutes(), duration.toMillis());
            report.put("流程实例平均处理时间", time);
        }else{
            // 如果没有完成的流程实例，则报告中填充“无数据”
            report.put("流程实例平均处理时间", "无数据");
        }
        return report; // 返回统计报告
    }

       /**
     * 获取指定时间段内的任务信息统计报告。
     * 
     * @param start 统计开始时间，Calendar类型
     * @param end 统计结束时间，Calendar类型
     * @return 返回一个Map<String,String>，键为统计项名称，值为统计结果
     */
    private Map<String,String> getTaskInfo(
        Calendar start,
        Calendar end
    ) {
        Map<String,String>report=new HashMap<>();

        // 统计开始时间之后创建的所有用户任务的数量
        List<HistoricTaskInstance>createdTasks=historyService.createHistoricTaskInstanceQuery()
            .taskCreatedAfter(start.getTime()).list();
        report.put("开始的用户任务数", Integer.toString(createdTasks.size()));

        // 统计当前仍未结束的所有用户任务的数量
        List<HistoricTaskInstance>unfinishedTasks=historyService.createHistoricTaskInstanceQuery()
            .unfinished().list();
        report.put("未结束的用户任务数", Integer.toString(unfinishedTasks.size()));

        // 统计在指定时间段内超时且未完成的所有用户任务的数量
        List<HistoricTaskInstance>duedTasks=historyService.createHistoricTaskInstanceQuery()
            .taskDueAfter(start.getTime())
            .taskDueBefore(end.getTime())
            .unfinished().list();
        report.put("超时的用户任务数", Integer.toString(duedTasks.size()));

        // 统计在开始时间之后完成的所有用户任务的数量以及平均处理时间
        List<HistoricTaskInstance>completedTasks=historyService.createHistoricTaskInstanceQuery()
            .taskCompletedAfter(start.getTime()).list();
        report.put("完成的用户任务数", Integer.toString(completedTasks.size()));
        // 计算并格式化任务平均处理时间
        if(completedTasks.size()>0){
            long totalTime=0;
            for(HistoricTaskInstance completedTask:completedTasks){
                totalTime+=completedTask.getDurationInMillis();
            }
            Duration duration = Duration.ofSeconds(totalTime);
            String time = String.format("%d 小时 %d 分 %d 秒", 
                duration.toHours(), duration.toMinutes(), duration.toMillis());
            report.put("任务平均处理时间", time);
        }else{
            report.put("任务平均处理时间", "无数据");
        }

        return report;
    }


}

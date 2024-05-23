package com.whaler.oasys.service.impl;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.flowable.engine.runtime.ProcessInstance;
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
    private Main main;
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
     * 执行每天定时任务的方法。
     * @Scheduled注解使用了cron表达式"0 0 23 * * ?"，指定了任务每天23点执行。
     */
    @Override
    @Scheduled(cron = "0 0 23 * * ?")
    public void dailyScheduledTask(){
        Calendar start=Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        Calendar end=Calendar.getInstance();

        Map<String,Object>report=new HashMap<>();

        Map<String,String>taskReport=getTaskInfo(start, end);
        report.put("任务信息", taskReport);

        List<ProcessDefinition>processDefinitions=repositoryService.createProcessDefinitionQuery().list();

        List<Map<String,String>>subreports=new ArrayList<>();
        for(ProcessDefinition processDefinition:processDefinitions){
            try{
                Map<String,String>subreport=getProcessInfo(processDefinition.getKey(), processDefinition.getName() ,start, end);
                subreports.add(subreport);
            }catch(Exception e){
                continue;
            }
        }
        report.put("流程详细信息", subreports);
        
        // SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        LocalDate createTime=end.getTime().toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDate();
        String title=createTime+"日报";
        String type="日报";

        ReportEntity reportEntity=new ReportEntity()
            .setTitle(title)
            .setContent(JSON.toJSONString(report))
            .setCreateTime(createTime)
            .setType(type);

        try{
            reportService.getBaseMapper().insert(reportEntity);
        }catch(Exception e){
            ;
        }
    }

    @Override
    @Scheduled(cron = "0 0 23 * * ?")
    public void dueWarningTask(){
        // 查询在明日前即将超时的所有任务，用于超时预警
        Calendar tomorrow=Calendar.getInstance();
        tomorrow.setTimeInMillis(tomorrow.getTimeInMillis()+24*60*60*1000);
        HistoricTaskInstanceQuery historicTaskInstanceQuery=historyService.createHistoricTaskInstanceQuery()
            .taskDueBefore(tomorrow.getTime());

        // 向用户发送超时预警
        LambdaQueryWrapper<UserEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        List<UserEntity>users=userService.getBaseMapper().selectList(lambdaQueryWrapper);
        for (UserEntity userEntity : users) {
            Long userId=userEntity.getId();
            Long permissionId=userEntity.getPermissionId();
            List<String>categoryIds=categoryService.selectCategoryIdsByPermissionId(permissionId)
                .stream().map(categoryId->Long.toString(categoryId)).collect(Collectors.toList());
            List<HistoricTaskInstance>willDueTasks=historicTaskInstanceQuery.or()
                .taskAssignee(userId.toString())
                .taskCandidateUser(permissionId.toString())
                .taskCandidateGroupIn(categoryIds)
                .list();
            List<String>taskNames=willDueTasks.stream().map(task->task.getTaskDefinitionKey()).collect(Collectors.toList());
            if (taskNames.size()>0) {
                StringBuilder content=new StringBuilder();
                content.append("您有");
                content.append(taskNames.size());
                content.append("个任务即将在一天内超时，请及时处理。");
                content.append("任务名称：\n");
                for (String taskName : taskNames) {
                    content.append(taskName+"\n");
                }
                sendMail(userEntity.getEmail(), "超时提醒",content.toString());
            }
        }
    }

    /**
     * 执行每周定时任务。
     * 通过@Scheduled注解配置，任务将在每周日的凌晨0点执行。
     */
    @Override
    @Scheduled(cron = "0 30 11 ? * SUN")
    public void weeklyScheduledTask(){
        Calendar start=Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.setTimeInMillis(start.getTimeInMillis()-7*24*60*60*1000);
        Calendar end=Calendar.getInstance();

        Map<String,Object>report=new HashMap<>();

        Map<String,String>taskReport=getTaskInfo(start, end);
        report.put("任务信息", taskReport);

        List<ProcessDefinition>processDefinitions=repositoryService.createProcessDefinitionQuery().list();

        List<Map<String,String>>subreports=new ArrayList<>();
        for(ProcessDefinition processDefinition:processDefinitions){
            Map<String,String>subreport=getProcessInfo(processDefinition.getKey(), processDefinition.getName() ,start, end);
            subreports.add(subreport);
        }
        report.put("流程详细信息", subreports);
        
        // SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        LocalDate createTime=end.getTime().toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDate();
        String title=createTime+"周报";
        String type="周报";
        ReportEntity reportEntity=new ReportEntity()
            .setTitle(title)
            .setContent(JSON.toJSONString(report))
            .setCreateTime(createTime)
            .setType(type);

        try{
            reportService.getBaseMapper().insert(reportEntity);
        }catch(Exception e){
            ;
        }
    }

    /**
     * 执行每5分钟定时任务。
     * 通过@Scheduled注解配置，任务将每隔5分钟执行一次  。
     */
    @Override
    @Scheduled(cron = "0 */5 * * * ?")
    public void adminBoardScheduledTask(){
        List<ProcessDefinition>processDefinitions=repositoryService.createProcessDefinitionQuery().list();
        List<Map<String,String>> reports=new ArrayList<>();
        processDefinitions.forEach(
            processDefinition->{
                try{
                    String processDefinitionName=processDefinition.getName();
                    String processDefinitionKey=processDefinition.getKey();
                    Integer processInstances=runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).list().size();
                    Integer approvalTasks=taskService.createTaskQuery().taskDescription("审批").processDefinitionKey(processDefinitionKey).list().size();
                    Integer operateTasks=taskService.createTaskQuery().taskDescription("操作").processDefinitionKey(processDefinitionKey).list().size();
                    Integer duedTasks=taskService.createTaskQuery().taskDueBefore(
                        Calendar.getInstance().getTime()
                    ).processDefinitionKey(processDefinitionKey).list().size();
            
                    Map<String,String> subreport=new HashMap<>();
                    subreport.put("流程名称", processDefinitionName);
                    subreport.put("运行中的流程实例数量", processInstances.toString());
                    subreport.put("未完成的审批任务数量", approvalTasks.toString());
                    subreport.put("未完成的操作任务数量", operateTasks.toString());
                    subreport.put("已经超时的任务数量", duedTasks.toString());
   
                    reports.add(subreport);
                }catch(Exception e){
                    return;
                }
            }
        );

        Map<String,Object>report=new HashMap<>();
        report.put("流程详细信息", reports);
        
        Integer processInstances=runtimeService.createProcessInstanceQuery().list().size();
        Integer approvalTasks=taskService.createTaskQuery().taskDescription("审批").list().size();
        Integer operateTasks=taskService.createTaskQuery().taskDescription("操作").list().size();
        Integer duedTasks=taskService.createTaskQuery().taskDueBefore(
            Calendar.getInstance().getTime()
        ).list().size();

        Map<String,String> subreport=new HashMap<>();
        subreport.put("运行中的流程实例数量", processInstances.toString());
        subreport.put("未完成的审批任务数量", approvalTasks.toString());
        subreport.put("未完成的操作任务数量", operateTasks.toString());
        subreport.put("已经超时的任务数量", duedTasks.toString());

        report.put("汇总信息", subreport);

        String msg=JSON.toJSONString(report);
        myMesgSender.sendMessage("admin", "管理员面板", msg);
        synchronized (Main.sharedData) {
            Main.sharedData.put("msg", msg);
        }
    }


    @Async("multiThreadExecutor")
    private void sendMail(String to, String subject, String content){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("发送简单邮件时发生异常！", e);
        }
    }

    private Map<String,String> getProcessInfo(
        String processDefinitionKey,
        String processDefinitionName,
        Calendar start,
        Calendar end
    ) {
        Map<String,String>report=new HashMap<>();

        report.put("流程名称", processDefinitionName);

        // 查询所有创建的流程实例，用于统计
        List<HistoricProcessInstance>createdProcesses=historyService.createHistoricProcessInstanceQuery()
            .processDefinitionKey(processDefinitionKey).startedAfter(start.getTime()).list();
        report.put("创建流程实例数", Integer.toString(createdProcesses.size()));

        // 查询所有已完成的流程实例，用于统计
        List<HistoricProcessInstance>completedProcesses=historyService.createHistoricProcessInstanceQuery()
            .processDefinitionKey(processDefinitionKey).finishedAfter(start.getTime()).list();
        report.put("完成流程实例数", Integer.toString(completedProcesses.size()));

        // 查询所有未结束的流程实例，用于统计
        List<HistoricProcessInstance>uncompletedProcesses=historyService.createHistoricProcessInstanceQuery()
            .processDefinitionKey(processDefinitionKey).unfinished().list();
        report.put("未结束流程实例数", Integer.toString(uncompletedProcesses.size()));

        // 平均实例处理时间
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
            report.put("流程实例平均处理时间", "无数据");
        }
        return report;
    }

    private Map<String,String> getTaskInfo(
        Calendar start,
        Calendar end
    ) {
        Map<String,String>report=new HashMap<>();

        // 查询开始的所有的用户任务，用于统计
        List<HistoricTaskInstance>createdTasks=historyService.createHistoricTaskInstanceQuery()
            .taskCreatedAfter(start.getTime()).list();
        report.put("开始的用户任务数", Integer.toString(createdTasks.size()));

        // 查询所有未结束的用户任务，用于统计
        List<HistoricTaskInstance>unfinishedTasks=historyService.createHistoricTaskInstanceQuery()
            .unfinished().list();
        report.put("未结束的用户任务数", Integer.toString(unfinishedTasks.size()));

        // 查询在这段时间内已经超时的未完成的所有用户任务，用于统计
        List<HistoricTaskInstance>duedTasks=historyService.createHistoricTaskInstanceQuery()
            .taskDueAfter(start.getTime())
            .taskDueBefore(end.getTime())
            .unfinished().list();
        report.put("超时的用户任务数", Integer.toString(duedTasks.size()));

        // 查询当天完成的所有用户任务，用于统计
        List<HistoricTaskInstance>completedTasks=historyService.createHistoricTaskInstanceQuery()
            .taskCompletedAfter(start.getTime()).list();
        report.put("完成的用户任务数", Integer.toString(completedTasks.size()));
        // 平均任务处理时间
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

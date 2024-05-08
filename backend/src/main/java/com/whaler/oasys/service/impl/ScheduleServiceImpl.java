package com.whaler.oasys.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
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
import com.whaler.oasys.model.entity.ReportEntity;
import com.whaler.oasys.model.entity.UserEntity;
import com.whaler.oasys.service.CategoryService;
import com.whaler.oasys.service.ReportService;
import com.whaler.oasys.service.ScheduleService;
import com.whaler.oasys.service.UserService;

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
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Map<String,Integer>report=new HashMap<>();
        // 查询所有未结束的流程实例，用于统计
        List<HistoricProcessInstance>processes=historyService.createHistoricProcessInstanceQuery()
            .unfinished().list();
        report.put("未结束的流程", processes.size());

        // 查询当天所有创建的流程实例，用于统计
        List<HistoricProcessInstance>createdProcesses=historyService.createHistoricProcessInstanceQuery()
            .startedAfter(calendar.getTime()).list();
        report.put("今日创建的流程", createdProcesses.size());

        // 查询当天所有已完成的流程实例，用于统计
        List<HistoricProcessInstance>completedProcesses=historyService.createHistoricProcessInstanceQuery()
            .finishedAfter(calendar.getTime()).list();
        report.put("今日完成的流程", completedProcesses.size());

        // 查询所有未结束的用户任务，用于统计
        List<HistoricTaskInstance>unfinishedTasks=historyService.createHistoricTaskInstanceQuery()
            .unfinished().list();
        report.put("未结束的用户任务", unfinishedTasks.size());

        // 查询当天开始的所有的用户任务，用于统计
        List<HistoricTaskInstance>createdTasks=historyService.createHistoricTaskInstanceQuery()
            .taskCreatedAfter(calendar.getTime()).list();
        report.put("今日开始的用户任务", createdTasks.size());

        // 查询当天完成的所有用户任务，用于统计
        List<HistoricTaskInstance>completedTasks=historyService.createHistoricTaskInstanceQuery()
            .taskCompletedAfter(calendar.getTime()).list();
        report.put("今日完成的用户任务", completedTasks.size());

        // 查询在今日已经超时的所有用户任务，用于统计
        List<HistoricTaskInstance>duedTasks=historyService.createHistoricTaskInstanceQuery()
            .taskDueAfter(calendar.getTime())
            .taskDueBefore(Calendar.getInstance().getTime()).list();
        report.put("今日超时的用户任务", duedTasks.size());
        
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        String createTime=ft.format(calendar.getTime());
        String title=createTime+"日报";
        String type="日报";
        ReportEntity reportEntity=new ReportEntity()
            .setTitle(title)
            .setContent(JSON.toJSONString(report))
            .setCreateTime(createTime)
            .setType(type);
        reportService.getBaseMapper().insert(reportEntity);
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
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.setTimeInMillis(calendar.getTimeInMillis()-7*24*60*60*1000);

        Map<String,Integer>report=new HashMap<>();
        // 查询所有未结束的流程实例，用于统计
        List<HistoricProcessInstance>processes=historyService.createHistoricProcessInstanceQuery()
            .unfinished().list();
        report.put("未结束的流程", processes.size());

        // 查询当天所有创建的流程实例，用于统计
        List<HistoricProcessInstance>createdProcesses=historyService.createHistoricProcessInstanceQuery()
            .startedAfter(calendar.getTime()).list();
        report.put("本周创建的流程", createdProcesses.size());

        // 查询当天所有已完成的流程实例，用于统计
        List<HistoricProcessInstance>completedProcesses=historyService.createHistoricProcessInstanceQuery()
            .finishedAfter(calendar.getTime()).list();
        report.put("本周完成的流程", completedProcesses.size());

        // 查询所有未结束的用户任务，用于统计
        List<HistoricTaskInstance>unfinishedTasks=historyService.createHistoricTaskInstanceQuery()
            .unfinished().list();
        report.put("未结束的用户任务", unfinishedTasks.size());

        // 查询当天开始的所有的用户任务，用于统计
        List<HistoricTaskInstance>createdTasks=historyService.createHistoricTaskInstanceQuery()
            .taskCreatedAfter(calendar.getTime()).list();
        report.put("本周开始的用户任务", createdTasks.size());

        // 查询当天完成的所有用户任务，用于统计
        List<HistoricTaskInstance>completedTasks=historyService.createHistoricTaskInstanceQuery()
            .taskCompletedAfter(calendar.getTime()).list();
        report.put("本周完成的用户任务", completedTasks.size());

        // 查询在今日已经超时的所有用户任务，用于统计
        List<HistoricTaskInstance>duedTasks=historyService.createHistoricTaskInstanceQuery()
            .taskDueAfter(calendar.getTime())
            .taskDueBefore(Calendar.getInstance().getTime()).list();
        report.put("本周超时的用户任务", duedTasks.size());
        
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        String createTime=ft.format(calendar.getTime());
        String title=createTime+"周报";
        String type="周报";
        ReportEntity reportEntity=new ReportEntity()
            .setTitle(title)
            .setContent(JSON.toJSONString(report))
            .setCreateTime(createTime)
            .setType(type);
        reportService.getBaseMapper().insert(reportEntity);

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
}

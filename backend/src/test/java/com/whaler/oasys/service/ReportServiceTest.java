package com.whaler.oasys.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whaler.oasys.Main;
import com.whaler.oasys.model.entity.ReportEntity;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@Rollback(true)
@Slf4j
public class ReportServiceTest {
    @Autowired
    private ReportService reportService;
    @Autowired
    private ScheduleService scheduleService;

    @Test
    @Transactional
    public void testInsertEntity(){
        Map<String,Integer> report=new HashMap<>();
        report.put("1", 1);
        report.put("2", 2);
        Calendar calendar = Calendar.getInstance();
        // SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        LocalDate createTime=calendar.getTime().toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDate();
        String title=createTime+"日报";
        String type="日报";
        ReportEntity reportEntity=new ReportEntity()
            .setTitle(title)
            .setContent(JSON.toJSONString(report))
            .setCreateTime(createTime)
            .setType(type);
        reportService.getBaseMapper().insert(reportEntity);
        log.info("insert report:{}",
            reportService.getBaseMapper().selectOne(
                new LambdaQueryWrapper<ReportEntity>()
            )
        );
    }

    @Test
    public void testDailyScheduledTask(){
        scheduleService.dailyScheduledTask();
    }

    @Test
    public void testWeeklyScheduledTask(){
        scheduleService.weeklyScheduledTask();
    }
}

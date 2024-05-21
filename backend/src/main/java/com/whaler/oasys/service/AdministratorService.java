package com.whaler.oasys.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.AdministratorEntity;
import com.whaler.oasys.model.entity.ReportEntity;
import com.whaler.oasys.model.param.AdministratorParam;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.vo.AdministratorVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.model.vo.ReportVo;

public interface AdministratorService
extends IService<AdministratorEntity> {
    AdministratorVo login(LoginParam loginParam);

    void register(AdministratorParam administratorParam);

    List<ProcessDefinitionVo> listProcessDefinitions();

    void deployProcessDefinition(InputStream[] files, String[] fileNames);

    List<ReportEntity> listReports();    

    List<ReportVo> listWeeklyReports();

    ReportEntity getWeeklyReport(String reportId);

    ReportEntity getDailyReport(LocalDate localDate);
}

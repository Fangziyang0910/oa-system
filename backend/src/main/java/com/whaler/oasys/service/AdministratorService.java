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

/**
 * 管理员服务接口，提供登录、注册、流程定义管理、报告管理等操作。
 * 继承自IService<AdministratorEntity>接口。
 */
public interface AdministratorService
extends IService<AdministratorEntity> {
    
    /**
     * 用户登录。
     * 
     * @param loginParam 登录参数，包含用户名和密码。
     * @return 登录成功返回用户信息的视图对象。
     */
    AdministratorVo login(LoginParam loginParam);

    /**
     * 用户注册。
     * 
     * @param administratorParam 注册参数，包含用户名、密码等信息。
     */
    void register(AdministratorParam administratorParam);

    /**
     * 列出所有流程定义信息。
     * 
     * @return 流程定义的视图对象列表。
     */
    List<ProcessDefinitionVo> listProcessDefinitions();

    /**
     * 部署流程定义文件。
     * 
     * @param files 流程定义文件的输入流数组。
     * @param fileNames 文件名数组，与输入流一一对应。
     */
    void deployProcessDefinition(InputStream[] files, String[] fileNames);

    /**
     * 列出所有报告实体。
     * 
     * @return 报告实体列表。
     */
    List<ReportEntity> listReports();    

    /**
     * 列出所有周报的视图对象。
     * 
     * @return 周报视图对象列表。
     */
    List<ReportVo> listWeeklyReports();

    /**
     * 根据报告ID获取周报实体。
     * 
     * @param reportId 周报的ID。
     * @return 周报实体。
     */
    ReportEntity getWeeklyReport(String reportId);

    /**
     * 根据日期获取日报实体。
     * 
     * @param localDate 指定的日期。
     * @return 日报实体。
     */
    ReportEntity getDailyReport(LocalDate localDate);
}

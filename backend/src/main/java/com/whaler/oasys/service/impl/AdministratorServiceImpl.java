package com.whaler.oasys.service.impl;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.AdministratorMapper;
import com.whaler.oasys.model.entity.AdministratorEntity;
import com.whaler.oasys.model.entity.ReportEntity;
import com.whaler.oasys.model.param.AdministratorParam;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.vo.AdministratorVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.model.vo.ReportVo;
import com.whaler.oasys.security.JwtManager;
import com.whaler.oasys.service.AdministratorService;
import com.whaler.oasys.service.ReportService;
import com.whaler.oasys.tool.MyBpmnModelModifier;

@Service
public class AdministratorServiceImpl
extends ServiceImpl<AdministratorMapper,AdministratorEntity>
implements AdministratorService {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private JwtManager jwtManager;
    @Autowired
    private MyBpmnModelModifier myBpmnModelModifier;

    @Override
    public AdministratorVo login(LoginParam loginParam){
        AdministratorEntity administratorEntity=
        this.baseMapper.selectByName(loginParam.getName());
        if(administratorEntity==null){
            throw new ApiException("用户名不存在");
        }
        if(!administratorEntity.getPassword().equals(loginParam.getPassword())){
            throw new ApiException("密码错误");
        }
        String token=jwtManager.generate(administratorEntity.getId());
        AdministratorVo administratorVo=new AdministratorVo();
        administratorVo.setName(administratorEntity.getName())
            .setToken(token)
            .setEmail(administratorEntity.getEmail())
            .setPhone(administratorEntity.getPhone());
        return administratorVo;
    }

    @Override
    public void register(AdministratorParam administratorParam){
        AdministratorEntity administratorEntity=
        this.baseMapper.selectByName(administratorParam.getName());
        if (administratorEntity!=null) {
            throw new ApiException("用户名已存在");
        }
        // 密码加密
        String newpassword=administratorParam.getPassword();
        this.baseMapper.insertAdministrator(
            administratorParam.getName(),
            newpassword,
            administratorParam.getEmail(),
            administratorParam.getPhone()
        );
    }

    @Override
    public List<ProcessDefinitionVo> listProcessDefinitions() {
        List<ProcessDefinitionVo>processDefinitionVoList=
            repositoryService.createProcessDefinitionQuery().list()
            .stream()
            .map(processDefinition -> new ProcessDefinitionVo()
                .setProcessDefinitionId(processDefinition.getId())
                .setProcessDefinitionKey(processDefinition.getKey())
                .setProcessDefinitionName(processDefinition.getName())
                .setProcessDefinitionCategory(processDefinition.getCategory())
                .setProcessDefinitionDescription(processDefinition.getDescription())
                .setProcessDefinitionVersion(processDefinition.getVersion())
            ).collect(Collectors.toList());

        return processDefinitionVoList;
    }

    @Override
    public void deployProcessDefinition(InputStream[] files, String[] fileNames) {
        DeploymentBuilder deploymentBuilder=repositoryService.createDeployment();
        try{
            for (int i = 0; i < files.length; i++) {
                deploymentBuilder.addInputStream(fileNames[i], files[i]);
            }
        }catch(Exception e){
            throw new ApiException("文件上传失败");
        }
        Deployment deployment=deploymentBuilder.deploy();
        String deploymentId=deployment.getId();
        ProcessDefinition pd=repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        if (pd==null) {
            throw new ApiException("部署失败");
        }
        BpmnModel bpmnModel=repositoryService.getBpmnModel(pd.getId());
        myBpmnModelModifier.setBpmnModel(bpmnModel);
    }

    @Override
    public List<ReportEntity> listReports() {
        LambdaQueryWrapper<ReportEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        List<ReportEntity>reportEntities=reportService.getBaseMapper().selectList(lambdaQueryWrapper);
        return reportEntities;
    }

    @Override
    public List<ReportVo> listWeeklyReports() {
        LambdaQueryWrapper<ReportEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReportEntity::getType, "周报");
        List<ReportEntity>reportEntities=reportService.getBaseMapper()
            .selectList(lambdaQueryWrapper);
        List<ReportVo>reportVos=new ArrayList<>();
        reportEntities.forEach(
            reportEntity->{
                ReportVo reportVo=new ReportVo();
                LocalDate startTime=reportEntity.getCreateTime().minusDays(7);
                reportVo.setReportId(Long.toString(reportEntity.getId()))
                    .setStartTime(startTime)
                    .setEndTime(reportEntity.getCreateTime());
                reportVos.add(reportVo);
            }
        );
        return reportVos;
    }

    @Override
    public ReportEntity getWeeklyReport(String reportId){
        ReportEntity reportEntity= reportService.getBaseMapper().selectById(Long.parseLong(reportId));
        if (reportEntity==null) {
            throw new ApiException("周报不存在");
        }
        return reportEntity;
    }

    @Override
    public ReportEntity getDailyReport(LocalDate localDate) {
        LambdaQueryWrapper<ReportEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReportEntity::getType, "日报");
        lambdaQueryWrapper.eq(ReportEntity::getCreateTime, localDate);
        ReportEntity reportEntity=reportService.getBaseMapper().selectOne(lambdaQueryWrapper);
        if (reportEntity==null) {
            throw new ApiException("日报不存在");
        }
        return reportEntity;
    }
}

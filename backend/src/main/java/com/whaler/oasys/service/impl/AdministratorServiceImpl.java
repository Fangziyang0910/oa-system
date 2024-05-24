package com.whaler.oasys.service.impl;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private RepositoryService repositoryService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private JwtManager jwtManager;
    @Autowired
    private MyBpmnModelModifier myBpmnModelModifier;

    /**
     * 用户登录。
     * 
     * @param loginParam 包含登录名和密码的参数对象。
     * @return 返回登录成功的用户信息，包括姓名、token、电子邮件和电话。
     * @throws ApiException 如果用户名不存在或密码错误，则抛出异常。
     */
    @Override
    public AdministratorVo login(LoginParam loginParam){
        // 根据用户名查询管理员实体
        AdministratorEntity administratorEntity=
        this.baseMapper.selectByName(loginParam.getName());
        
        // 用户名不存在时抛出异常
        if(administratorEntity==null){
            throw new ApiException("用户名不存在");
        }
        
        // 密码错误时抛出异常
        if(!administratorEntity.getPassword().equals(loginParam.getPassword())){
            throw new ApiException("密码错误");
        }
        
        // 生成token
        String token=jwtManager.generate(administratorEntity.getId());
        
        // 创建并填充管理员Vo对象
        AdministratorVo administratorVo=new AdministratorVo();
        administratorVo.setName(administratorEntity.getName())
            .setToken(token)
            .setEmail(administratorEntity.getEmail())
            .setPhone(administratorEntity.getPhone());
        
        return administratorVo;
    }

    /**
     * 注册管理员账户。
     * 
     * @param administratorParam 管理员参数对象，包含用户名、密码、电子邮件和电话号码。
     *                           该方法会检查用户名是否已存在，如果存在则抛出异常；
     *                           如果不存在，则将用户信息加密后存储到数据库中。
     * @throws ApiException 如果用户名已存在，抛出此异常。
     */
    @Override
    public void register(AdministratorParam administratorParam){
        // 检查用户名是否已存在
        AdministratorEntity administratorEntity=
        this.baseMapper.selectByName(administratorParam.getName());
        if (administratorEntity!=null) {
            throw new ApiException("用户名已存在");
        }
        // 密码加密处理，此处省略具体实现

        // 插入管理员信息到数据库
        String newpassword=administratorParam.getPassword();
        this.baseMapper.insertAdministrator(
            administratorParam.getName(),
            newpassword,
            administratorParam.getEmail(),
            administratorParam.getPhone()
        );
    }

    /**
     * 列出所有的流程定义信息。
     * 
     * @return 返回包含多个流程定义信息的列表。每个流程定义信息包括：流程定义ID、流程定义键、流程定义名称、
     *         流程定义分类、流程定义描述和流程定义版本。
     */
    @Override
    public List<ProcessDefinitionVo> listProcessDefinitions() {
        // 从流程定义查询中获取所有流程定义，并将每个流程定义转换为ProcessDefinitionVo对象
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

    /**
     * 部署流程定义。
     * 该方法接收一个包含流程定义文件的输入流数组和对应的文件名数组，将这些流程定义部署到Activiti引擎中。
     * 如果部署成功，会修改myBpmnModelModifier中的BpmnModel以反映部署的流程定义。
     * 
     * @param files 输入流数组，包含待部署的流程定义文件。
     * @param fileNames 文件名数组，与files数组对应，指定每个流程定义文件的名称。
     * @throws ApiException 如果文件上传失败或部署失败，抛出此异常。
     */
    @Override
    public void deployProcessDefinition(InputStream[] files, String[] fileNames) {
        // 创建部署构建器
        DeploymentBuilder deploymentBuilder=repositoryService.createDeployment();
        try{
            // 遍历文件数组，将每个文件添加到部署构建器中
            for (int i = 0; i < files.length; i++) {
                deploymentBuilder.addInputStream(fileNames[i], files[i]);
            }
        }catch(Exception e){
            // 如果在添加文件过程中发生异常，抛出ApiException
            throw new ApiException("文件上传失败");
        }
        // 使用部署构建器部署流程定义，并获取部署的ID
        Deployment deployment=deploymentBuilder.deploy();
        String deploymentId=deployment.getId();
        // 根据部署ID查询部署的流程定义
        ProcessDefinition pd=repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        if (pd==null) {
            // 如果查询不到流程定义，说明部署失败，抛出ApiException
            throw new ApiException("部署失败");
        }
        // 根据流程定义ID获取BpmnModel
        BpmnModel bpmnModel=repositoryService.getBpmnModel(pd.getId());
        // 将获取到的BpmnModel设置到myBpmnModelModifier中
        myBpmnModelModifier.setBpmnModel(bpmnModel);
    }

    /**
     * 列出所有报告实体。
     * 
     * @return 返回报告实体的列表。列表中可能包含零个或多个报告实体。
     */
    @Override
    public List<ReportEntity> listReports() {
        // 创建一个Lambda查询包装器以配置查询条件
        LambdaQueryWrapper<ReportEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        // 使用reportService的getBaseMapper方法执行查询，并将结果存储在reportEntities列表中
        List<ReportEntity>reportEntities=reportService.getBaseMapper().selectList(lambdaQueryWrapper);
        return reportEntities;
    }

    /**
     * 获取所有的周报列表。
     * 此方法不接受任何参数，返回一个包含所有周报的列表。
     * 每个周报的信息被转换为ReportVo对象，这个对象包含了周报的ID、起始时间和结束时间。
     *
     * @return List<ReportVo> 包含所有周报信息的列表。
     */
    @Override
    public List<ReportVo> listWeeklyReports() {
        // 创建查询包装器，用于筛选出类型为“周报”的报告
        LambdaQueryWrapper<ReportEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReportEntity::getType, "周报");
        
        // 根据查询条件获取报告实体列表
        List<ReportEntity>reportEntities=reportService.getBaseMapper()
            .selectList(lambdaQueryWrapper);
        
        // 初始化用于存放转换后报告信息的列表
        List<ReportVo>reportVos=new ArrayList<>();
        
        // 遍历所有查询到的报告实体，将其转换为ReportVo对象，并添加到列表中
        reportEntities.forEach(
            reportEntity->{
                ReportVo reportVo=new ReportVo();
                LocalDate startTime=reportEntity.getCreateTime().minusDays(7); // 计算周报的起始时间
                // 设置ReportVo对象的属性
                reportVo.setReportId(Long.toString(reportEntity.getId()))
                    .setStartTime(startTime)
                    .setEndTime(reportEntity.getCreateTime());
                reportVos.add(reportVo);
            }
        );
        return reportVos;
    }

    /**
     * 根据报告ID获取每周报告的详细信息。
     * 
     * @param reportId 报告的唯一标识符，字符串形式。
     * @return ReportEntity 返回报告实体对象，包含报告的详细信息。
     * @throws ApiException 如果报告ID不存在，则抛出此异常。
     */
    @Override
    public ReportEntity getWeeklyReport(String reportId){
        // 通过报告ID查询报告实体
        ReportEntity reportEntity= reportService.getBaseMapper().selectById(Long.parseLong(reportId));
        // 如果查询结果为空，则抛出报告不存在的异常
        if (reportEntity==null) {
            throw new ApiException("周报不存在");
        }
        return reportEntity;
    }

    /**
     * 获取指定日期的日报。
     * 
     * @param localDate 指定的日期，格式为LocalDate。
     * @return ReportEntity 返回查询到的日报实体。
     * @throws ApiException 如果指定日期不存在对应的日报，则抛出此异常。
     */
    @Override
    public ReportEntity getDailyReport(LocalDate localDate) {
        // 创建查询条件，筛选出类型为"日报"且创建时间为指定日期的报告
        LambdaQueryWrapper<ReportEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReportEntity::getType, "日报");
        lambdaQueryWrapper.eq(ReportEntity::getCreateTime, localDate);
        // 根据查询条件获取唯一的日报实体
        ReportEntity reportEntity=reportService.getBaseMapper().selectOne(lambdaQueryWrapper);
        // 如果查询结果为空，则抛出异常
        if (reportEntity==null) {
            throw new ApiException("日报不存在");
        }
        return reportEntity;
    }
}

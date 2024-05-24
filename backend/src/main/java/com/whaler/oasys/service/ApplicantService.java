package com.whaler.oasys.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.ApplicantEntity;
import com.whaler.oasys.model.vo.ApplicantVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.model.vo.ProcessInstanceVo;
import com.whaler.oasys.model.vo.TaskVo;

public interface ApplicantService
extends IService<ApplicantEntity> {
    /**
     * 插入申请人-流程实例联系。
     * 
     * @param applicantId 申请人的唯一标识符。
     * @param processinstanceId 申请相关的流程实例ID。
     * @return 返回插入操作影响的行数。
     */
    int insertApplicantEntity(Long applicantId, String processinstanceId);

    /**
     * 删除申请人-流程实例联系。
     * 
     * @param applicantId 申请人的ID，用于标识要删除的申请人。
     * @param processinstanceId 流程实例ID，标识该申请人实体所关联的流程实例。
     * @return 返回删除操作的影响行数，通常为1表示删除成功，0表示删除失败或未找到相关实体。
     */
    int deleteApplicantEntity(Long applicantId, String processinstanceId);

    /**
     * 根据申请人ID查询申请人-流程实例。
     * 
     * @param applicantId 申请人的唯一标识ID。
     * @return 返回申请人的详细信息，封装在ApplicantVo对象中。
     */
    ApplicantVo selectByApplicantId(Long applicantId);

    /**
     * 查询指定申请人的所有流程实例
     * 
     * @param applicantId 申请人ID，用于筛选流程实例的发起人
     * @return 返回一个包含所有符合条件的流程实例的列表
     */
    List<ProcessInstanceVo> listProcessInstances(Long applicantId);

    /**
     * 获取流程定义列表
     * 
     * @return List<ProcessDefinitionVo> 返回流程定义的列表，列表中每个元素都代表一个流程的定义信息
     */
    List<ProcessDefinitionVo> listProcessDefinitions();

    /**
     * 创建流程实例
     * 
     * @param processDefinitionKey 流程定义的键值，用于标识要启动的流程定义
     * @return ProcessInstanceVo 流程实例的详细信息，包括实例ID等
     */
    ProcessInstanceVo createProcessInstance(String processDefinitionKey);

    /**
     * 获取指定流程实例的启动表单。
     * 
     * @param processInstanceId 流程实例的ID，用于标识特定的流程实例。
     * @return FormVo 返回表单的视图对象，包含表单的相关信息。
     */
    String getStartForm(String processDefinitionKey);

    /**
     * 提交开始表单的函数。
     * 该方法用于在指定的流程实例中提交开始表单。开始表单可以包含任何启动流程所需的信息。
     *
     * @param processInstanceId 流程实例的ID。此ID用于指定在哪个流程实例中提交表单。
     * @param startForm 启动表单的数据映射。该映射包含表单中所有字段的键值对。
     */
    void submitStartForm(String processInstanceId, Map<String,String>startForm);

    /**
     * 获取指定流程实例的进度信息。
     * 
     * @param processInstanceId 流程实例的ID，用于标识唯一流程实例。
     * @return 返回一个包含任务进度信息的列表。每个任务信息封装在TaskVo对象中。
     */
    List<TaskVo> getProcessInstanceProgress(String processInstanceId);

    /**
     * 获取指定任务ID的历史表单对象。
     * 
     * @param taskId 任务的唯一标识符，用于查询该任务对应的历史表单。
     * @return 返回一个FormVo对象，包含指定任务的历史表单信息。
     */
    FormVo getHistoricalForm(String taskId);

    /**
     * 根据流程实例ID获取流程实例信息。
     * 
     * @param processInstanceId 流程实例的唯一标识符。
     * @return ProcessInstanceVo 流程实例的详细信息对象。
     */
    ProcessInstanceVo getProcessInstance(String processInstanceId);

    /**
     * 中止指定的流程实例。
     * 
     * @param processInstanceId 要中止的流程实例的ID。
     * @param reason 中止流程实例的原因。
     */
    void abortProcessInstance(String processInstanceId, String reason);

    /**
     * 获取指定流程定义键的原始流程图。
     * 
     * @param processDefinitionKey 流程定义的键，用于唯一标识一个流程定义。
     * @return 返回一个InputStream对象，该对象包含指定流程定义的原始流程图数据。
     */
    InputStream getOriginalProcessDiagram(String processDefinitionKey);

    /**
     * 获取指定流程实例的图表输入流。
     * 
     * @param processInstanceId 流程实例的ID，用于标识特定的流程实例。
     * @return 返回一个InputStream对象，该对象包含指定流程实例的图表数据。
     */
    InputStream getProcessInstanceDiagram(String processInstanceId);
}

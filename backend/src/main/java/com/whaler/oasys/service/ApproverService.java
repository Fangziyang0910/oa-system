package com.whaler.oasys.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.ApproverEntity;
import com.whaler.oasys.model.vo.ApproverVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.TaskVo;

/**
 * ApproverService 接口，提供审批流程相关的服务方法。
 * 继承自IService<ApproverEntity>，针对ApproverEntity实体提供基本的数据库操作方法。
 */
public interface ApproverService
extends IService<ApproverEntity> {
    
    /**
     * 插入审批实体。
     * @param approverId 审批者的ID。
     * @param processinstanceId 审批流程的实例ID。
     * @return 插入成功返回1，失败返回0。
     */
    int insertApproverEntity(Long approverId, String processinstanceId);

    /**
     * 删除审批实体。
     * @param approverId 审批者的ID。
     * @param processinstanceId 审批流程的实例ID。
     * @return 删除成功返回1，失败返回0。
     */
    int deleteApproverEntity(Long approverId, String processinstanceId);

    /**
     * 根据审批者ID选择审批实体。
     * @param approverId 审批者的ID。
     * @return 审批实体的视图对象。
     */
    ApproverVo selectByApproverId(Long approverId);

    /**
     * 列出所有待审批任务。
     * @return 待审批任务的视图对象列表。
     */
    List<TaskVo> listApprovalTasks();

    /**
     * 列出所有候选审批任务。
     * @return 候选审批任务的视图对象列表。
     */
    List<TaskVo> listApprovalCandidateTasks();

    /**
     * 声明候选审批任务。
     * @param taskId 任务的ID。
     */
    void claimCandidateTask(String taskId);

    /**
     * 取消声明候选审批任务。
     * @param taskId 任务的ID。
     */
    void unclaimCandidateTask(String taskId);
    
    /**
     * 获取未完成的任务详情。
     * @param taskId 任务的ID。
     * @return 未完成任务的视图对象。
     */
    TaskVo getTaskNotCompleted(String taskId);

    /**
     * 列出所有分配的审批任务。
     * @return 分配的审批任务的视图对象列表。
     */
    List<TaskVo> listApprovalAssignTasks();

    /**
     * 获取启动表单。
     * @param taskId 任务的ID。
     * @return 启动表单的视图对象。
     */
    FormVo getStartForm(String taskId);

    /**
     * 获取任务表单数据。
     * @param taskId 任务的ID。
     * @return 任务表单数据的视图对象。
     */
    FormVo getTaskFormData(String taskId);

    /**
     * 获取任务表单。
     * @param taskId 任务的ID。
     * @return 任务表单的内容。
     */
    String getTaskForm(String taskId);

    /**
     * 保存审批任务数据。
     * @param taskId 任务的ID。
     * @param form 包含表单数据的Map。
     */
    void saveApprovalTask(String taskId, Map<String, String> form);

    /**
     * 完成自有审批任务。
     * @param taskId 任务的ID。
     */
    void completeApprovalOwnTask(String taskId);

    /**
     * 完成审批任务。
     * @param taskId 任务的ID。
     * @param form 包含表单数据的Map。
     */
    void completeApprovalTask(String taskId, Map<String, String> form);

    /**
     * 获取历史任务详情。
     * @param taskId 任务的ID。
     * @return 历史任务的视图对象。
     */
    TaskVo getHistoricalDetails(String taskId);
}
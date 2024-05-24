package com.whaler.oasys.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.OperatorEntity;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.OperatorVo;
import com.whaler.oasys.model.vo.TaskVo;

/**
 * OperatorService 接口，提供操作员相关服务的定义。
 * 继承自IService<OperatorEntity>接口，扩展对操作员实体的数据库操作。
 */
public interface OperatorService
extends IService<OperatorEntity> {
    
    /**
     * 插入操作员实体到数据库。
     * @param operatorId 操作员ID，长整型。
     * @param processinstanceId 流程实例ID，字符串类型。
     * @return 插入成功返回1，失败返回0。
     */
    int insertOperatorEntity(Long operatorId, String processinstanceId);
    
    /**
     * 从数据库删除操作员实体。
     * @param operatorId 操作员ID，长整型。
     * @param processinstanceId 流程实例ID，字符串类型。
     * @return 删除成功返回1，失败返回0。
     */
    int deleteOperatorEntity(Long operatorId, String processinstanceId);

    /**
     * 根据操作员ID从数据库选择操作员信息。
     * @param operatorId 操作员ID，长整型。
     * @return 操作员信息的Vo对象。
     */
    OperatorVo selectByOperatorId(Long operatorId);

    /**
     * 列出所有操作员的任务。
     * @return 任务Vo对象的列表。
     */
    List<TaskVo> listOperatorTasks();

    /**
     * 列出操作员候选任务。
     * @return 任务Vo对象的列表。
     */
    List<TaskVo> listOperatorCandidateTasks();

    /**
     * 列出操作员被分配的任务。
     * @return 任务Vo对象的列表。
     */
    List<TaskVo> listOperatorAssignTasks();

    /**
     * 申领候选任务。
     * @param taskId 任务ID，字符串类型。
     */
    void claimCandidateTask(String taskId);

    /**
     * 取消申领候选任务。
     * @param taskId 任务ID，字符串类型。
     */
    void unclaimCandidateTask(String taskId);

    /**
     * 获取未完成的任务详情。
     * @param taskId 任务ID，字符串类型。
     * @return 任务Vo对象。
     */
    TaskVo getTaskNotCompleted(String taskId);

    /**
     * 列出指定任务的候选用户。
     * @param taskId 任务ID，字符串类型。
     * @return 用户ID的字符串列表。
     */
    List<String> listOperatorCandidateUsers(String taskId);

    /**
     * 分配任务给指定用户。
     * @param taskId 任务ID，字符串类型。
     * @param name 用户名，字符串类型。
     */
    void assignTask(String taskId, String name);

    /**
     * 取消任务分配。
     * @param taskId 任务ID，字符串类型。
     */
    void unassignTask(String taskId);

    /**
     * 获取启动表单。
     * @param taskId 任务ID，字符串类型。
     * @return 表单Vo对象。
     */
    FormVo getStartForm(String taskId);

    /**
     * 获取任务表单数据。
     * @param taskId 任务ID，字符串类型。
     * @return 表单Vo对象。
     */
    FormVo getTaskFormData(String taskId);

    /**
     * 获取任务表单HTML内容。
     * @param taskId 任务ID，字符串类型。
     * @return 表单HTML内容，字符串类型。
     */
    String getTaskForm(String taskId);

    /**
     * 保存操作员任务表单数据。
     * @param taskId 任务ID，字符串类型。
     * @param form 表单数据，键值对映射。
     */
    void saveOperatorTask(String taskId, Map<String, String> form);

    /**
     * 结束已分配的任务。
     * @param taskId 任务ID，字符串类型。
     */
    void endAssignedTask(String taskId);

    /**
     * 完成操作员自己的任务。
     * @param taskId 任务ID，字符串类型。
     */
    void completeOperatorOwnTask(String taskId);

    /**
     * 完成操作员任务。
     * @param taskId 任务ID，字符串类型。
     * @param form 表单数据，键值对映射。
     */
    void completeOperatorTask(String taskId, Map<String, String> form);

    /**
     * 获取历史任务详情。
     * @param taskId 任务ID，字符串类型。
     * @return 任务Vo对象。
     */
    TaskVo getHistoricalDetails(String taskId);
}

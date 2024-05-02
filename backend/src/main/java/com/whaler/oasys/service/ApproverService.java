package com.whaler.oasys.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.ApproverEntity;
import com.whaler.oasys.model.vo.ApproverVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.TaskVo;

public interface ApproverService
extends IService<ApproverEntity> {
    int insertApproverEntity(Long approverId, String processinstanceId);

    int deleteApproverEntity(Long approverId, String processinstanceId);

    ApproverVo selectByApproverId(Long approverId);

    List<TaskVo> listApprovalTasks();

    FormVo getStartForm(String taskId);

    FormVo getTaskForm(String taskId);

    void finishApprovalTask(String taskId, Map<String, String> form);

    TaskVo getHistoricalDetails(String taskId);
}
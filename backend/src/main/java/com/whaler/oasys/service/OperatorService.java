package com.whaler.oasys.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.OperatorEntity;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.OperatorVo;
import com.whaler.oasys.model.vo.TaskVo;

public interface OperatorService
extends IService<OperatorEntity> {
    int insertOperatorEntity(Long operatorId, String processinstanceId);
    
    int deleteOperatorEntity(Long operatorId, String processinstanceId);

    OperatorVo selectByOperatorId(Long operatorId);

    List<TaskVo> listOperatorTasks();

    FormVo getStartForm(String taskId);

    FormVo getTaskForm(String taskId);

    void finishOperatorTask(String taskId, Map<String, String> form);

    
    // void getHistoricalDetails();
}

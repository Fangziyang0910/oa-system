package com.whaler.oasys.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 申请人实例实体类
 * 用于映射申请人相关的流程实例数据
 * 
 * @param operatorId 操作人ID
 * @param processInstanceId 流程实例ID
 */
@Data
@Accessors(chain = true)
@TableName("applicant_processInstance")
public class ApplicantEntity {
    private Long operatorId; // 操作人ID
    private String processInstanceId; // 流程实例ID
}

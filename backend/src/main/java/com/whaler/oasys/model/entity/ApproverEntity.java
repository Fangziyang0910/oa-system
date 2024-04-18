package com.whaler.oasys.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 审批者实体类，
 * 用于映射数据库中的approver_processInstance表。
 * 
 * @param approverId 审批者ID
 * @param processInstanceId 流程实例ID
 */
@Data
@Accessors(chain = true)
@TableName("approver_processinstance")
public class ApproverEntity {
    private String approverId; // 审批者ID
    private String processinstanceId; // 流程实例ID
}

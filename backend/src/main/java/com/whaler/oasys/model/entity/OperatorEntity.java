package com.whaler.oasys.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * OperatorEntity 类表示了一个操作员实体。
 * 这个类用來映射数据库中的operator_processInstance表。
 * 
 * @param operatorId 操作员的唯一标识符。
 * @param processInstanceId 与操作员相关联的流程实例的唯一标识符。
 */
@Data
@Accessors(chain = true)
@TableName("operator_processInstance")
public class OperatorEntity {
    private String operatorId; // 操作员ID
    private String processInstanceId; // 流程实例ID
}

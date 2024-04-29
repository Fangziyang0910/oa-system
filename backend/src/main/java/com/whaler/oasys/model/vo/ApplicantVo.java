package com.whaler.oasys.model.vo;

import java.util.Set;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 申请人信息模型类
 * 用于封装与申请人相关的信息，包括申请人ID和相关流程实例ID集合。
 * @param applicantId 
 * @param processinstanceIds
 */
@Data
@Accessors(chain = true)
public class ApplicantVo {
    private Long applicantId; // 申请人ID，唯一标识一个申请人
    private Set<String> processinstanceIds; // 相关流程实例ID集合，申请人可能关联多个流程实例
}

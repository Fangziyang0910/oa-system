package com.whaler.oasys.model.vo;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 审批人信息模型类
 * 用于封装审批人与相关任务的信息
 * @param approverId 审批人ID
 * @param taskIds 任务ID列表
 */
@Data
@Accessors(chain = true)
public class ApproverVo {
    /**
     * 审批人ID
     * 长整型，唯一标识一个审批人
     */
    private Long approverId;
    
    /**
     * 任务ID列表
     * 字符串列表，包含该审批人相关的所有任务ID
     */
    private List<String> taskIds;
}

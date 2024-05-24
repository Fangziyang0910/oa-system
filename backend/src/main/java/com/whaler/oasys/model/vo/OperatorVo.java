package com.whaler.oasys.model.vo;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * OperatorVo类用于封装操作员相关信息的视图对象。
 * 该类包含了操作员的ID和其所负责的任务ID列表。
 * @param operatorId 操作员ID，唯一标识一个操作员
 * @param taskIds 操作员负责的任务ID列表，每个任务用字符串表示
 */
@Data
@Accessors(chain = true)
public class OperatorVo {
    private Long operatorId; // 操作员ID，唯一标识一个操作员
    private List<String> taskIds; // 操作员负责的任务ID列表，每个任务用字符串表示
}

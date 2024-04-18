package com.whaler.oasys.model.vo;

import java.util.Set;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ApproverVo {
    private Long approverId;
    private Set<String> processinstanceIds;
}

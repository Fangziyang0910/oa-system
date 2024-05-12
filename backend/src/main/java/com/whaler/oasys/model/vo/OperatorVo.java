package com.whaler.oasys.model.vo;

import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OperatorVo {
    private Long operatorId;
    private List<String> taskIds;
}

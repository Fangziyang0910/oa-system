package com.whaler.oasys.model.vo;

import java.util.Set;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ApplicantVo {
    private Long applicantId;
    private Set<String> processinstanceIds;
}

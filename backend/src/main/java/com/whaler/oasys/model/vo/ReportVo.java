package com.whaler.oasys.model.vo;

import java.time.LocalDate;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReportVo {
    private String reportId;
    private LocalDate startTime;
    private LocalDate endTime;
}

package com.whaler.oasys.model.vo;

import java.time.LocalDate;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ReportVo类用于定义报告的视图对象。
 * 它包含了报告的基本信息，包括报告ID，以及报告的开始和结束时间。
 * @param reportId 报告的唯一标识符
 * @param startTime 报告的开始时间
 * @param endTime 报告的结束时间
 */
@Data
@Accessors(chain = true)
public class ReportVo {
    private String reportId; // 报告的唯一标识符
    private LocalDate startTime; // 报告的开始时间
    private LocalDate endTime; // 报告的结束时间
}

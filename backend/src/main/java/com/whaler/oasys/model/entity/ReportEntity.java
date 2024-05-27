package com.whaler.oasys.model.entity;

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * ReportEntity 类表示一个报告实体，用于映射数据库中的report表。
 * 它继承了 BaseEntity 类，包含了报告的基本信息。
 * @param title 报告的标题
 * @param content 报告的内容
 * @param createTime 报告的创建时间
 * @param type 报告的类型
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("report")
public class ReportEntity
extends BaseEntity {
    // 报告的标题
    private String title;
    // 报告的内容
    private String content;
    // 报告的创建时间
    private LocalDate createTime;
    // 报告的类型
    private String type;
}

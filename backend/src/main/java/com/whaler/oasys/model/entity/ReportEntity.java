package com.whaler.oasys.model.entity;

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("report")
public class ReportEntity
extends BaseEntity {
    private String title;
    private String content;
    private LocalDate createTime;
    private String type;
}

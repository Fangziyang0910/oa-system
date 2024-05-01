package com.whaler.oasys.model.vo;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 表单视图对象类，用于封装表单相关的信息。
 * @param formKey 表单的key
 * @param formName 表单名称
 * @param formFields 表单字段
 */
@Data
@Accessors(chain = true)
public class FormVo {
    // 表单的唯一标识符
    private String formKey;
    // 表单的字段集合
    private String formName;
    // 表单字段的集合，每个字段包含具体的表单信息
    private List<FormFieldVo> formFields;
}

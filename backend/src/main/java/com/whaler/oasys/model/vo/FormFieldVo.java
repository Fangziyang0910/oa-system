package com.whaler.oasys.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 表单字段视图对象类，用于封装表单字段的相关信息。
 * @param id 字段ID
 * @param name 字段名称
 * @param type 字段类型
 * @param value 字段值
 * @param required 是否必填
 * @param readable 是否可读
 * @param writable 是否可写
 */
@Data
@Accessors(chain = true)
public class FormFieldVo {
    // 字段ID，唯一标识一个表单字段
    private String id;
    // 字段名称，描述表单字段的名称
    private String name;
    // 字段类型，定义表单字段的数据类型
    private String type;
    // 字段值，存储表单字段的当前值
    private Object value;
    // 字段是否可写，true表示可写，false表示不可写
    private Boolean readOnly;
    // 字段是否为必填项，true表示必填，false表示非必填
    private Boolean required;
}

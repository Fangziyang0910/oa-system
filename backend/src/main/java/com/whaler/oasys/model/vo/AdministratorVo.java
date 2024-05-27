package com.whaler.oasys.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * AdministratorVo 类定义了管理员的视图对象。
 * 该类包含了管理员的基本信息，用于在系统中表示管理员的身份和联系信息。
 * @param name 管理员姓名
 * @param token 管理员登录验证的令牌
 * @param email 管理员的电子邮箱地址
 * @param phone 管理员的联系电话号码
 */
@Data
@Accessors(chain = true)
public class AdministratorVo {
    private String name; // 管理员姓名
    private String token; // 管理员登录验证的令牌
    private String email; // 管理员的电子邮箱地址
    private String phone; // 管理员的联系电话号码
}

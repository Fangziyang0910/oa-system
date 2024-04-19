package com.whaler.oasys.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AdministratorVo {
    private String name;
    private String token;
    private String email;
    private String phone;  
}

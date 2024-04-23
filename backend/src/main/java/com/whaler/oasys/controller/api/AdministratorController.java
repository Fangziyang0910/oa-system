package com.whaler.oasys.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whaler.oasys.model.param.AdministratorParam;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.vo.AdministratorVo;
import com.whaler.oasys.service.AdministratorService;

@RestController
@RequestMapping("/admin")
public class AdministratorController {
    @Autowired
    private AdministratorService administratorService;

    @PostMapping("/login")
    public AdministratorVo login(@RequestBody @Validated LoginParam loginParam) {
        return administratorService.login(loginParam);
    }

    @PostMapping("/register")
    public void register(@RequestBody @Validated AdministratorParam AdministratorParam) {
        administratorService.register(AdministratorParam);
    }
}

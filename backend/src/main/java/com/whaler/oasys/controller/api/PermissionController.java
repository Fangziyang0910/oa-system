package com.whaler.oasys.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whaler.oasys.service.PermissionService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @ApiOperation("获取部门职位字典")
    @GetMapping("/getDepartmentRoles")
    public String getDepartmentRoles() {
        return permissionService.getDepartmentRoles();
    }
}

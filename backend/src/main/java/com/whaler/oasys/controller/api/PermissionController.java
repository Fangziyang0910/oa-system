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

    /**
     * 获取部门职位字典的接口。
     * <p>
     * 该接口不需要接收任何参数，调用后会返回一个包含部门职位信息的字符串。
     * 主要用于从权限服务中获取部门职位相关信息，以便于在前端进行展示或进一步处理。
     *
     * @return 返回一个字符串，该字符串包含了部门职位的字典信息。
     */
    @ApiOperation("获取部门职位字典")
    @GetMapping("/getDepartmentRoles")
    public String getDepartmentRoles() {
        // 从权限服务中获取部门职位字典
        return permissionService.getDepartmentRoles();
    }

    /**
     * 获取部门列表的接口。
     * <p>
     * 该接口不需要任何参数，调用后会返回部门的列表信息。
     * 
     * @return 返回部门列表的信息，类型为String。
     */
    @ApiOperation("获取部门列表")
    @GetMapping("/getDepartments")
    public String getDepartments() {
        // 通过permissionService获取部门列表
        return permissionService.getDepartments();
    }
}

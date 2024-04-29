package com.whaler.oasys.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.flowable.engine.FormService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.h2.command.dml.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whaler.oasys.model.entity.PermissionEntity;
import com.whaler.oasys.service.PermissionService;

import liquibase.pro.packaged.id;

// @SpringBootConfiguration
public class FlowableConfig {
    @Bean
    public CommandLineRunner init(
        final RepositoryService repositoryService,
        final RuntimeService runtimeService,
        final TaskService taskService,
        final FormService formService,
        final IdentityService identityService,
        final PermissionService permissionService
    ){
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                // 将权限表数据同步到flowable
                List<PermissionEntity>permissionEntities=permissionService.list();
                for (PermissionEntity permissionEntity : permissionEntities) {
                    User user=identityService.newUser(Long.toString(permissionEntity.getId()));
                    user.setFirstName(permissionEntity.getDepartment());
                    user.setLastName(permissionEntity.getRole());
                    identityService.saveUser(user);
                }
                // 设置部门分组
                List<String>departments=new ArrayList<String>(Arrays.asList("研发部","测试部","运维部门","财务部","行政部","市场部"));
                for (String department : departments) {
                    List<User>users=identityService.createUserQuery().userFirstName(department).list();
                    Group group=identityService.newGroup(department);
                    for (User user : users) {
                        identityService.createMembership(user.getId(), group.getId());
                    }
                    identityService.saveGroup(group);
                }
                // 设置职能分组
                List<LambdaQueryWrapper<PermissionEntity>>lambdaQueryWrappers=Arrays.asList(
                    new LambdaQueryWrapper<PermissionEntity>().eq(PermissionEntity::getIsApplicant, true),
                    new LambdaQueryWrapper<PermissionEntity>().eq(PermissionEntity::getIsApprover, true),
                    new LambdaQueryWrapper<PermissionEntity>().eq(PermissionEntity::getIsOperator, true)
                );
                permissionEntities=permissionService.list(lambdaQueryWrappers.get(0));
                Group group=identityService.newGroup("申请人");
                for (PermissionEntity permissionEntity : permissionEntities) {
                    identityService.createMembership(Long.toString(permissionEntity.getId()), "申请人");
                }
                identityService.saveGroup(group);
                permissionEntities=permissionService.list(lambdaQueryWrappers.get(1));
                group=identityService.newGroup("审批人");
                for (PermissionEntity permissionEntity : permissionEntities) {
                    identityService.createMembership(Long.toString(permissionEntity.getId()), "审批人");
                }
                identityService.saveGroup(group);
                permissionEntities=permissionService.list(lambdaQueryWrappers.get(3));
                group=identityService.newGroup("操作人");
                for (PermissionEntity permissionEntity : permissionEntities) {
                    identityService.createMembership(Long.toString(permissionEntity.getId()), "操作人");
                }
                identityService.saveGroup(group);
                // 设置职级分组
                
            }
        };
    }
}

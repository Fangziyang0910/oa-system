package com.whaler.oasys.task;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.whaler.oasys.service.CategoryService;

import lombok.extern.slf4j.Slf4j;

/**
 * LeaveAskAssignDelegate类负责处理假期申请的部门分配逻辑。
 * 它实现了JavaDelegate接口，用于在流程引擎中执行具体的业务逻辑。
 * @Component注解将其注册为一个Spring Bean，名称为"LeaveAskAssignDelegate"。
 * @Scope注解指定该Bean的作用域为原型，即每次请求都创建一个新的实例。
 */
@Slf4j
@Component("LeaveAskAssignDelegate")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LeaveAskAssignDelegate
implements JavaDelegate{
    // 注入CategoryService，用于查询分类信息
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 执行具体的业务逻辑。
     * 该方法会根据申请人的部门查询相应的分类信息，进而获取对应的权限ID列表，
     * 并通过这些ID确定领导和经理的列表。
     * @param execution 代表流程执行实例的DelegateExecution对象，用于获取和设置变量。
     */
    @Override
    public void execute(DelegateExecution execution){
        // 获取申请人所在的部门
        String applicantDepartment=(String)execution.getVariable("applicantDepartment");
        // 根据部门名称查询分类ID
        Long categoryId= categoryService.selectByCategoryName(applicantDepartment).getCategoryId();
        // 根据分类ID查询权限ID列表
        List<Long>permissionIds1=categoryService.selectByCategoryId(categoryId).getPermissionIds();
        // 查询指定ID（11L）的分类对应的权限ID列表
        List<Long>permissionIds2=categoryService.selectByCategoryId(11L).getPermissionIds();
        // 查询指定ID（12L）的分类对应的权限ID列表
        List<Long>permissionIds3=categoryService.selectByCategoryId(12L).getPermissionIds();

        // 分别计算领导和经理的权限ID列表
        List<Long> leaders = CollectionUtils.intersection(permissionIds1, permissionIds2).stream().collect(Collectors.toList());
        List<Long> managers = CollectionUtils.intersection(permissionIds1, permissionIds3).stream().collect(Collectors.toList());
        // 将领导和经理的ID列表转换为JSON字符串
        String strLeaders=JSONArray.toJSON(leaders).toString();
        strLeaders=strLeaders.substring(1, strLeaders.length()-1);
        String strManagers=JSONArray.toJSON(managers).toString();
        strManagers=strManagers.substring(1, strManagers.length()-1);
        // 记录领导和经理的ID列表
        log.info("leaders:{}",strLeaders);
        log.info("managers:{}",strManagers);

        // 将领导和经理的ID列表设置为流程变量
        execution.setVariable("leader", strLeaders);
        execution.setVariable("manager", strManagers);
    }
}

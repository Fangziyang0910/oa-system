package com.whaler.oasys.task;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.config.BeanDefinition;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.whaler.oasys.service.CategoryService;

/**
 * SetManagerDelegate类是一个处理部门和权限关系的组件，用于在流程执行中动态确定经理人。
 * 它通过Autowired自动注入CategoryService和RuntimeService来实现对数据库中类别和权限信息的操作，
 * 以及对流程执行的控制。
 */
@Component("SetManagerDelegate")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SetManagerDelegate
implements JavaDelegate {
    @Autowired
    private CategoryService categoryService; // 用于访问类别信息的服务
    @Autowired
    private RuntimeService runtimeService; // 用于流程控制的服务

    /**
     * 根据申请部门获取相应的类别ID，进而获取权限ID列表，并确定共同拥有的权限，
     * 从而推导出经理人列表。最后，将经理人列表设置为流程变量，以供后续使用。
     * 
     * @param execution 代表当前流程执行实例的DelegateExecution对象，用于访问和设置流程变量。
     */
    @Override
    public void execute(DelegateExecution execution){
        // 获取申请人所在部门
        String applicantDepartment=(String)execution.getVariable("applicantDepartment");
        // 根据部门名获取类别ID
        Long categoryId= categoryService.selectByCategoryName(applicantDepartment).getCategoryId();
        // 分别根据categoryId和固定值12L获取两个权限ID列表
        List<Long>permissionIds1=categoryService.selectByCategoryId(categoryId).getPermissionIds();
        List<Long>permissionIds3=categoryService.selectByCategoryId(12L).getPermissionIds();

        // 计算两个权限ID列表的交集，即确定共同拥有的权限
        List<Long> managers = CollectionUtils.intersection(permissionIds1, permissionIds3).stream().collect(Collectors.toList());
        // 将经理人ID列表转换为JSON字符串格式
        String strManagers=JSONArray.toJSON(managers).toString();
        // 移除JSON字符串两端的括号
        strManagers=strManagers.substring(1, strManagers.length()-1);

        // 将经理人列表设置为流程变量
        execution.setVariable("manager", strManagers);

        // 更新流程进度
        // 获取并更新当前执行实例的“serviceList”变量，记录已处理的服务
        String jsonString=(String)runtimeService.getVariable(execution.getId(), "serviceList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add(execution.getCurrentFlowElement().getId());
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(execution.getId(), "serviceList", jsonString);
    }
}

package com.whaler.oasys.task;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.whaler.oasys.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;

@Component("SetLeaderDelegate")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
/**
 * SetLeaderDelegate类用于设置领导审批流程。
 * 该类实现了JavaDelegate接口，用于在流程引擎中执行具体的业务逻辑。
 */
public class SetLeaderDelegate implements JavaDelegate {
    @Autowired
    private CategoryService categoryService; // 分类服务，用于查询分类信息和权限ID
    @Autowired
    private RuntimeService runtimeService; // 运行时服务，用于流程实例的交互

    /**
     * 执行具体的业务逻辑。
     * @param execution 代表当前流程实例的执行对象，可以从中获取变量和执行流程操作。
     */
    @Override
    public void execute(DelegateExecution execution) {
        // 查询与申请人部门和默认主管部门相关的权限角色ID，并求交集
        String applicantDepartment = (String) execution.getVariable("applicantDepartment");
        Long categoryId = categoryService.selectByCategoryName(applicantDepartment).getCategoryId();
        List<Long> permissionIds1 = categoryService.selectByCategoryId(categoryId).getPermissionIds();
        List<Long> permissionIds2 = categoryService.selectByCategoryId(11L).getPermissionIds();
        
        List<Long> leaders = CollectionUtils.intersection(permissionIds1, permissionIds2).stream().collect(Collectors.toList());
        String strLeaders = JSONArray.toJSON(leaders).toString();
        strLeaders = strLeaders.substring(1, strLeaders.length() - 1); // 移除JSONArray的首尾括号

        execution.setVariable("leader", strLeaders); // 将领导ID集合设置为流程变量

        // 更新当前流程实例的进度
        String jsonString = (String) runtimeService.getVariable(execution.getId(), "serviceList");
        List<String> formList = JSON.parseArray(jsonString, String.class);
        formList.add(execution.getCurrentFlowElement().getId()); // 添加当前处理的流程节点ID
        jsonString = JSON.toJSONString(formList);
        runtimeService.setVariable(execution.getId(), "serviceList", jsonString); // 更新流程变量
    }

}
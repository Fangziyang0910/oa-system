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

@Component("SetManagerDelegate")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SetManagerDelegate
implements JavaDelegate {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution execution){
        String applicantDepartment=(String)execution.getVariable("applicantDepartment");
        Long categoryId= categoryService.selectByCategoryName(applicantDepartment).getCategoryId();
        List<Long>permissionIds1=categoryService.selectByCategoryId(categoryId).getPermissionIds();
        List<Long>permissionIds3=categoryService.selectByCategoryId(12L).getPermissionIds();

        List<Long> managers = CollectionUtils.intersection(permissionIds1, permissionIds3).stream().collect(Collectors.toList());
        String strManagers=JSONArray.toJSON(managers).toString();
        strManagers=strManagers.substring(1, strManagers.length()-1);

        execution.setVariable("manager", strManagers);

        // 更新流程进度
        String jsonString=(String)runtimeService.getVariable(execution.getId(), "serviceList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add(execution.getCurrentFlowElement().getId());
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(execution.getId(), "serviceList", jsonString);
    }
}

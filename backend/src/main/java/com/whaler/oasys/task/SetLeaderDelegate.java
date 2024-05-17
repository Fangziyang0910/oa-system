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
public class SetLeaderDelegate
implements JavaDelegate {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution execution){
        // 查询部门分组和主管分组的所有权限角色
        String applicantDepartment=(String)execution.getVariable("applicantDepartment");
        Long categoryId= categoryService.selectByCategoryName(applicantDepartment).getCategoryId();
        List<Long>permissionIds1=categoryService.selectByCategoryId(categoryId).getPermissionIds();
        List<Long>permissionIds2=categoryService.selectByCategoryId(11L).getPermissionIds();

        // 求交集
        List<Long> leaders = CollectionUtils.intersection(permissionIds1, permissionIds2).stream().collect(Collectors.toList());
        String strLeaders=JSONArray.toJSON(leaders).toString();
        strLeaders=strLeaders.substring(1, strLeaders.length()-1);

        execution.setVariable("leader", strLeaders);

        // 更新流程进度
        String jsonString=(String)runtimeService.getVariable(execution.getId(), "serviceList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add(execution.getCurrentFlowElement().getId());
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(execution.getId(), "serviceList", jsonString);
    }

}

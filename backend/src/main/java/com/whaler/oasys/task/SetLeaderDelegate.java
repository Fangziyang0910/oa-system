package com.whaler.oasys.task;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.whaler.oasys.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
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

    @Override
    public void execute(DelegateExecution execution){
        String applicantDepartment=(String)execution.getVariable("applicantDepartment");
        Long categoryId= categoryService.selectByCategoryName(applicantDepartment).getCategoryId();
        List<Long>permissionIds1=categoryService.selectByCategoryId(categoryId).getPermissionIds();
        List<Long>permissionIds2=categoryService.selectByCategoryId(11L).getPermissionIds();

        List<Long> leaders = CollectionUtils.intersection(permissionIds1, permissionIds2).stream().collect(Collectors.toList());
        String strLeaders=JSONArray.toJSON(leaders).toString();
        strLeaders=strLeaders.substring(1, strLeaders.length()-1);

        execution.setVariable("leader", strLeaders);
    }

}

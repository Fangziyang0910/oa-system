package com.whaler.oasys.task;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.whaler.oasys.model.vo.CategoryVo;
import com.whaler.oasys.service.CategoryService;

@Service
public class LeaveAskAssignDelegate
implements JavaDelegate{
    @Autowired
    private CategoryService categoryService;
    @Override
    public void execute(DelegateExecution execution){
        
        String department=(String)execution.getVariable("applicantDepartment");
        // department="研发部";
        // execution.setVariable("leader", "研发部项目经理");
        // execution.setVariable("manager", "研发部部门主管");
        // return;
        CategoryVo categoryVo=categoryService.selectByCategoryName(department);
        CategoryVo leaders=categoryService.selectByCategoryName("部门组长");
        CategoryVo managers=categoryService.selectByCategoryName("部门主管");
        for (Long a : categoryVo.getPermissionIds()) {
            for (Long b : leaders.getPermissionIds()) {
                if(a.equals(b)){
                    execution.setVariable("leader", a);
                }
            }
            for (Long c : managers.getPermissionIds()) {
                if(a.equals(c)){
                    execution.setVariable("manager", a);
                }
            }
        }
    }
}

package com.whaler.oasys.task;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;

@Component("EndExecutionListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class EndExecutionListener
implements ExecutionListener {
    @Autowired
    private RuntimeService runtimeService;
    @Override
    public void notify(DelegateExecution execution) {
        // 更新流程进度
        log.info("你妈死了");
        String jsonString=(String)runtimeService.getVariable(execution.getId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add("endEvent");
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(execution.getId(), "formList", jsonString);
    }
}

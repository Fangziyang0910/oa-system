package com.whaler.oasys.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.GraphicInfo;
import org.flowable.engine.FormService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.h2.command.dml.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whaler.oasys.model.entity.PermissionEntity;
import com.whaler.oasys.service.PermissionService;
import com.whaler.oasys.tool.MyBpmnModelModifier;

import liquibase.pro.packaged.id;

@SpringBootConfiguration
public class FlowableConfig {
    @Bean
    public CommandLineRunner init(
        final RepositoryService repositoryService,
        final MyBpmnModelModifier myBpmnModelModifier
    ){
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                List<ProcessDefinition>processDefinitions=repositoryService.createProcessDefinitionQuery().list();
                List<String>processDefinitionIds=processDefinitions.stream()
                    .map(processDefinition->processDefinition.getId()).collect(Collectors.toList());
                for (String processDefinitionId : processDefinitionIds) {
                    BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
                    myBpmnModelModifier.setBpmnModel(bpmnModel);
                }


            }
        };
    }
}

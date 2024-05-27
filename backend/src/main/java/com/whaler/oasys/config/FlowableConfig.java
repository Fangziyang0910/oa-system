package com.whaler.oasys.config;

import java.util.List;
import java.util.stream.Collectors;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import com.whaler.oasys.tool.MyBpmnModelModifier;

/**
 * Flowable配置类，用于设置和初始化Flowable相关服务。
 */
@SpringBootConfiguration
public class FlowableConfig {
    
    /**
     * 创建一个CommandLineRunner Bean，用于在应用启动时执行初始化流程。
     * 
     * @param repositoryService 用于访问流程定义存储的服务。
     * @param myBpmnModelModifier 用于修改BPMN模型的自定义服务。
     * @return 返回一个CommandLineRunner实例，该实例在应用启动时运行，执行初始化逻辑。
     */
    @Bean
    public CommandLineRunner init(
        final RepositoryService repositoryService,
        final MyBpmnModelModifier myBpmnModelModifier
    ){
        return new CommandLineRunner() {
            /**
             * CommandLineRunner的run方法，应用启动时执行该方法。
             * 
             * @param strings 命令行参数，此处未使用。
             * @throws Exception 如果执行逻辑中发生异常，则抛出。
             */
            @Override
            public void run(String... strings) throws Exception {
                // 查询所有流程定义
                List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
                
                // 提取流程定义ID列表
                List<String> processDefinitionIds = processDefinitions.stream()
                    .map(processDefinition -> processDefinition.getId()).collect(Collectors.toList());
                
                // 遍历流程定义ID列表，加载并修改每个流程的BPMN模型
                for (String processDefinitionId : processDefinitionIds) {
                    BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
                    myBpmnModelModifier.setBpmnModel(bpmnModel);
                }
            }
        };
    }
}

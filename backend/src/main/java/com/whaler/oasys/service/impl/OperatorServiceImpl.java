package com.whaler.oasys.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.form.api.FormInfo;
import org.flowable.form.model.FormField;
import org.flowable.form.model.SimpleFormModel;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.OperatorMapper;
import com.whaler.oasys.model.entity.OperatorEntity;
import com.whaler.oasys.model.entity.PermissionEntity;
import com.whaler.oasys.model.entity.UserEntity;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.vo.FormFieldVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.OperatorVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.CategoryService;
import com.whaler.oasys.service.OperatorService;
import com.whaler.oasys.service.PermissionService;
import com.whaler.oasys.service.UserService;
import com.whaler.oasys.tool.MyMesgSender;

@Service
public class OperatorServiceImpl
extends ServiceImpl<OperatorMapper,OperatorEntity>
implements OperatorService {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FormService formService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private MyMesgSender myMesgSender;

    /**
     * 插入操作员实体到数据库。
     * 
     * @param operatorId 操作员的ID，标识唯一的操作员。
     * @param processinstanceId 流程实例的ID，标识唯一的流程实例。
     * @return 返回插入操作的影响行数，通常为1表示插入成功。
     */
    @Override
    public int insertOperatorEntity(Long operatorId, String processinstanceId) {
        // 调用BaseMapper的insertOperatorEntity方法插入操作员实体
        return this.baseMapper.insertOperatorEntity(operatorId, processinstanceId);
    }

    /**
     * 删除操作员实体
     * 
     * @param operatorId 操作员ID，用于指定要删除的操作员
     * @param processinstanceId 流程实例ID，用于指定操作员所属的流程实例
     * @return 返回删除操作影响的行数，通常为1表示删除成功，为0表示未找到对应的实体，小于0表示删除操作出现错误
     */
    @Override
    public int deleteOperatorEntity(Long operatorId, String processinstanceId) {
        // 调用baseMapper删除指定操作员实体
        return this.baseMapper.deleteOperatorEntity(operatorId, processinstanceId);
    }

    /**
     * 根据操作员ID查询操作员信息及其对应的任务ID列表。
     * 
     * @param operatorId 操作员的唯一标识符。
     * @return OperatorVo 操作员视图对象，包含了操作员ID和相关任务ID列表。
     */
    @Override
    public OperatorVo selectByOperatorId(Long operatorId) {
        // 根据操作员ID查询操作员实体列表
        List<OperatorEntity> operatorEntities = this.baseMapper.selectByOperatorId(operatorId);
        OperatorVo operatorVo=new OperatorVo();
        operatorVo.setOperatorId(operatorId);
        
        // 将操作员实体列表中的过程实例ID转换为任务ID列表，并设置到操作员视图对象中
        operatorVo.setTaskIds(
            operatorEntities.stream()
            .map(OperatorEntity::getProcessinstanceId)
            .collect(Collectors.toList())
        );
        return operatorVo;
    }

    /**
     * 列出当前操作员的任务列表。
     * 该方法不接受任何参数，返回当前用户操作的任务列表的视图对象。
     *
     * @return 返回一个包含当前用户可以操作的任务的TaskVo对象列表。
     */
    @Override
    public List<TaskVo> listOperatorTasks() {
        // 获取当前用户ID
        Long userId=UserContext.getCurrentUserId();
        // 根据用户ID获取用户名
        String userName=userService.selectByUserId(userId).getName();
        // 获取当前用户权限ID
        Long permissionId=userService.getById(UserContext.getCurrentUserId()).getPermissionId();
        // 根据权限ID获取类别ID列表，并转换为字符串列表
        List<String> categoryIds=categoryService.selectCategoryIdsByPermissionId(permissionId)
            .stream().map(categoryId->Long.toString(categoryId)).collect(Collectors.toList());

        // 查询当前用户可以操作的任务
        List<Task>operateTasks=taskService.createTaskQuery().or()
            .taskAssignee(userName)
            .taskCandidateUser(Long.toString(permissionId))
            .taskCandidateGroupIn(categoryIds).endOr()
            .taskDescription("操作")
            .list();

        // 将查询到的任务转换为TaskVo对象列表
        List<TaskVo>taskVos=operateTasks.stream()
            .map(task->{
                TaskVo taskVo=new TaskVo();
                // 设置任务的基础信息
                taskVo.setTaskId(task.getId());
                taskVo.setTaskName(task.getName());
                taskVo.setExecutionId(task.getExecutionId());
                // 获取任务发起人姓名
                String starterId=(String)runtimeService.getVariable(task.getExecutionId(), "starter");
                String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
                taskVo.setStarterName(starter);
                taskVo.setOwnerName(task.getOwner());
                taskVo.setAssigneeName(task.getAssignee());
                // 获取任务所属流程定义的名称
                String processDefinitionName=repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId()).singleResult().getName();
                taskVo.setProcessDefinitionName(processDefinitionName);
                taskVo.setDescription(task.getDescription());
                // 如果任务有截止日期，则设置截止时间字符串
                if (task.getDueDate() != null) {
                    taskVo.setDueTime(task.getDueDate().toString());   
                }
                return taskVo;
            }).collect(Collectors.toList());
        return taskVos;
    }

    /**
     * 列出操作员候选任务的列表。
     * 该方法不接受任何参数，返回当前用户有权限操作的任务列表。
     * 返回值:
     * - List<TaskVo>: 包含任务ID、任务名称、执行ID、启动者名称、流程定义名称、任务描述和到期时间的任务视图对象列表。
     */
    @Override
    public List<TaskVo> listOperatorCandidateTasks() {
        // 获取当前用户的权限ID
        Long permissionId=userService.getById(UserContext.getCurrentUserId()).getPermissionId();
        
        // 根据权限ID获取类别ID列表，并转换为字符串列表
        List<String> categoryIds=categoryService.selectCategoryIdsByPermissionId(permissionId)
            .stream().map(categoryId->Long.toString(categoryId)).collect(Collectors.toList());

        // 查询当前用户有操作权的任务，包括候选用户为权限ID或候选组为类别ID的任务，且任务描述为"操作"
        List<Task>operateTasks=taskService.createTaskQuery().or()
            .taskCandidateUser(Long.toString(permissionId))
            .taskCandidateGroupIn(categoryIds).endOr()
            .taskDescription("操作")
            .list();

        // 将查询到的任务转换为TaskVo对象列表，包括任务的各种详细信息
        List<TaskVo>taskVos=operateTasks.stream()
            .map(task->{
                TaskVo taskVo=new TaskVo();
                taskVo.setTaskId(task.getId());
                taskVo.setTaskName(task.getName());
                taskVo.setExecutionId(task.getExecutionId());
                
                // 获取任务启动者的名称
                String starterId=(String)runtimeService.getVariable(task.getExecutionId(), "starter");
                String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
                taskVo.setStarterName(starter);
                
                // 获取任务所属流程定义的名称
                String processDefinitionName=repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId()).singleResult().getName();
                taskVo.setProcessDefinitionName(processDefinitionName);
                taskVo.setDescription(task.getDescription());
                
                // 如果任务有到期时间，则设置到期时间
                if (task.getDueDate() != null) {
                    taskVo.setDueTime(task.getDueDate().toString());   
                }
                return taskVo;
            }).collect(Collectors.toList());
        return taskVos;

    }

    /**
     * 认领候选任务。
     * 该方法用于将指定的任务声明为特定用户的工作任务。
     * 当任务不存在或已被签收时，将抛出异常。
     * 
     * @param taskId 任务的ID，用于标识需要声明的任务。
     * @throws ApiException 如果任务不存在或已被签收，则抛出此异常。
     */
    @Override
    public void claimCandidateTask(String taskId) {
        // 获取当前用户的名字
        String userName=userService.getById(UserContext.getCurrentUserId()).getName();
        // 根据任务ID查询任务
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        // 如果任务不存在，抛出异常
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        try{
            // 尝试声明任务
            taskService.claim(taskId, userName);
            // 设置任务所有者
            taskService.setOwner(taskId, userName);
        }catch(Exception e){
            // 如果任务已被签收，抛出异常
            throw new ApiException("任务已被签收");
        }
    }

    /**
     * 取消认领候选任务。
     * 该方法将取消指定任务的认领状态，清空任务相关的表单数据，并将任务的分配者和所有者设置为null。
     * 
     * @param taskId 任务的ID，用于标识需要取消认领的任务。
     * @throws ApiException 如果任务不存在或者表单不存在，抛出此异常。
     */
    @Override
    public void unclaimCandidateTask(String taskId) {
        // 根据任务ID查询任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        
        // 尝试获取任务的表单信息
        FormInfo taskFormInfo = taskService.getTaskFormModel(taskId);
        if(taskFormInfo==null){
            throw new ApiException("表单不存在");
        }
        
        // 清空表单数据：设置所有表单字段的值为null
        List<FormField> formFields = ((SimpleFormModel)taskFormInfo.getFormModel()).getFields();
        Map<String, String> nullMap = new HashMap<>();
        formFields.forEach(formField -> {
            nullMap.put(formField.getId(), null);
        });
        formService.saveFormData(taskId, nullMap);

        // 取消任务的认领和拥有状态
        taskService.setAssignee(taskId, null);
        taskService.setOwner(taskId, null);
    }
    
    /**
     * 列出当前用户被分配的操作任务。
     * 该方法不接受任何参数，返回当前用户所有待处理和进行中的任务列表。
     * 
     * @return List<TaskVo> 任务视图对象列表，每个任务视图包含任务的基本信息、负责人、创建人、截止时间等。
     */
    @Override
    public List<TaskVo> listOperatorAssignTasks() {
        // 获取当前用户的姓名
        String userName=userService.selectByUserId(UserContext.getCurrentUserId()).getName();

        // 查询当前用户作为负责人或分配者的任务，以及任务描述包含"操作"的任务
        List<Task>operateTasks=taskService.createTaskQuery().or()
            .taskOwner(userName)
            .taskAssignee(userName).endOr()
            .taskDescription("操作")
            .list();

        // 将查询到的任务转换为TaskVo对象列表，包括任务的各种详细信息
        List<TaskVo>taskVos=operateTasks.stream()
            .map(task->{
                TaskVo taskVo=new TaskVo();
                taskVo.setTaskId(task.getId());
                taskVo.setTaskName(task.getName());
                taskVo.setExecutionId(task.getExecutionId());
                taskVo.setOwnerName(task.getOwner());
                taskVo.setAssigneeName(task.getAssignee());
                // 获取任务的启动人姓名
                String starterId=(String)runtimeService.getVariable(task.getExecutionId(), "starter");
                String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
                taskVo.setStarterName(starter);
                // 获取任务所属流程定义的名称
                String processDefinitionName=repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId()).singleResult().getName();
                taskVo.setProcessDefinitionName(processDefinitionName);
                taskVo.setDescription(task.getDescription());
                // 如果任务有截止日期，则设置截止时间的字符串表示
                if (task.getDueDate() != null) {
                    taskVo.setDueTime(task.getDueDate().toString());   
                }
                return taskVo;
            }).collect(Collectors.toList());
        return taskVos;

    }

    /**
     * 获取未完成的任务信息。
     * 
     * @param taskId 任务ID，用于查询特定的任务。
     * @return TaskVo 任务视图对象，包含任务的详细信息。
     * @throws ApiException 如果任务不存在或获取任务信息时发生异常，则抛出ApiException。
     */
    @Override
    public TaskVo getTaskNotCompleted(String taskId){
        // 根据任务ID查询任务，如果任务不存在，则抛出异常
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        try{
            // 获取任务启动者的ID和名称
            String starterId=(String)runtimeService.getVariable(task.getExecutionId(), "starter");
            String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
            // 获取流程定义的名称
            String processDefinitionName=repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId()).singleResult().getName();
            
            // 创建任务视图对象，并设置任务相关信息
            TaskVo taskVo=new TaskVo();
            taskVo.setTaskId(taskId)
                .setTaskName(task.getName())
                .setExecutionId(task.getExecutionId())
                .setStarterName(starter)
                .setOwnerName(task.getOwner())
                .setAssigneeName(task.getAssignee())
                .setProcessDefinitionName(processDefinitionName)
                .setDescription(task.getDescription())
                .setEndTime(null);
            // 如果任务有截止日期，则设置截止时间
            if (task.getDueDate()!=null) {
                taskVo.setDueTime(task.getDueDate().toString());
            }
            return taskVo;
        }catch(Exception e){
            // 如果在获取任务信息的过程中发生异常，则抛出ApiException
            throw new ApiException("任务信息获取异常");
        }
    }

    /**
     * 列出任务的候选用户。
     * 
     * @param taskId 任务ID，用于查询任务和相关候选用户。
     * @return 返回一个包含所有候选用户名称的列表。
     * @throws ApiException 如果任务不存在，则抛出此异常。
     */
    @Override
    public List<String> listOperatorCandidateUsers(String taskId) {
        // 根据任务ID查询任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        Set<String> userNames = new HashSet<>();
        LambdaQueryWrapper<UserEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 获取所有具有操作员权限的权限ID
        List<Long> permissionIds = permissionService.list().stream()
            .filter(permission -> permission.getIsOperator())
            .map(permission -> permission.getId())
            .collect(Collectors.toList());
        List<Long> candidateUsers = new ArrayList<>();

        // 根据权限ID查询任务的候选用户
        permissionIds.forEach(
            permissionId -> {
                Task tmp = taskService.createTaskQuery()
                    .taskId(taskId)
                    .taskCandidateUser(permissionId.toString()).singleResult();
                if (tmp != null) {
                    candidateUsers.add(permissionId);
                }
            }
        );

        // 根据候选用户权限ID查询对应的用户
        candidateUsers.forEach(
            candidateUser -> {
                lambdaQueryWrapper.eq(UserEntity::getPermissionId, candidateUser);
                List<String> names = userService.getBaseMapper().selectList(lambdaQueryWrapper)
                    .stream().map(userEntity -> userEntity.getName()).collect(Collectors.toList());
                names.forEach(name -> userNames.add(name));
                lambdaQueryWrapper.clear();
            }
        );

        // 获取所有候选组别ID
        List<Long> categoryIds = categoryService.list().stream()
            .map(category -> category.getId()).collect(Collectors.toList());
        List<Long> candidateGroups = new ArrayList<>();

        // 根据类别ID查询任务的候选组别
        categoryIds.forEach(
            categoryId -> {
                Task tmp = taskService.createTaskQuery()
                    .taskId(taskId)
                    .taskCategory(categoryId.toString()).singleResult();
                if (tmp != null) {
                    candidateGroups.add(categoryId);
                }
            }
        );

        // 根据候选组别查询对应的用户权限，并收集用户名称
        candidateGroups.forEach(
            candidateGroup -> {
                List<Long> ids = categoryService.selectCategoryIdsByPermissionId(candidateGroup);
                // 查找符合用户权限的所有用户
                ids.forEach(
                    id -> {
                        lambdaQueryWrapper.eq(UserEntity::getPermissionId, id);
                        List<String> names = userService.getBaseMapper().selectList(lambdaQueryWrapper)
                            .stream().map(userEntity -> userEntity.getName()).collect(Collectors.toList());
                        names.forEach(name -> userNames.add(name));
                        lambdaQueryWrapper.clear();
                    }
                );
            }
        );

        // 若未设置任何候选用户，返回当前部门的所有用户
        if (userNames.size() == 0) {
            // 获取当前用户的部门名称
            String department = userService.selectByUserId(UserContext.getCurrentUserId()).getDepartment();
            LambdaQueryWrapper<PermissionEntity> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper2.eq(PermissionEntity::getDepartment, department)
                .eq(PermissionEntity::getIsOperator, true);
            // 查询该部门具有操作员权限的所有用户权限ID
            List<Long> ids = permissionService.getBaseMapper().selectList(lambdaQueryWrapper2)
                .stream().map(permissionEntity -> permissionEntity.getId()).collect(Collectors.toList());
            lambdaQueryWrapper2.clear();
            // 根据部门权限ID查询并收集用户名称
            ids.forEach(
                id -> {
                    lambdaQueryWrapper.eq(UserEntity::getPermissionId, id);
                    List<String> names = userService.getBaseMapper().selectList(lambdaQueryWrapper)
                        .stream().map(userEntity -> userEntity.getName()).collect(Collectors.toList());
                    names.forEach(name -> userNames.add(name));
                    lambdaQueryWrapper.clear();
                }
            );
        }

        // 删除当前用户本人
        String myName = userService.selectByUserId(UserContext.getCurrentUserId()).getName();
        if (userNames.contains(myName)) {
            userNames.remove(myName);
        }
        
        // 返回收集到的所有候选用户名称列表
        return userNames.stream().collect(Collectors.toList());
    }

    /**
     * 分配任务给指定的执行者。
     * 
     * @param taskId 任务的唯一标识符。
     * @param name 将要分配任务给的执行者的名称。
     * @throws ApiException 如果任务不存在或者任务禁止委派时抛出。
     */
    @Override
    public void assignTask(String taskId, String name) {
        // 根据任务ID查询任务
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        
        // 检查任务是否存在
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        
        // 检查任务是否禁止委派
        if (!task.getOwner().equals(task.getAssignee())) {
            throw new ApiException("任务禁止委派");
        }
        
        // 分配任务给指定的执行者
        taskService.setAssignee(taskId, name);
    }

    /**
     * 取消分配特定的任务。
     * 该方法通过任务ID查找任务，并将任务的分配者重置为任务的创建者。
     * 如果任务不存在，则抛出自定义异常。
     *
     * @param taskId 任务的唯一标识符。
     * @throws ApiException 如果任务不存在，则抛出此异常。
     */
    @Override
    public void unassignTask(String taskId) {
        // 根据任务ID查询任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        
        // 如果任务不存在，抛出异常
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        
        // 将任务的分配者设置为任务的创建者
        taskService.setAssignee(taskId, task.getOwner());
    }

    /**
     * 获取开始表单的信息。
     * 
     * @param taskId 任务的ID，用于查询任务及其相关的开始表单。
     * @return FormVo 表示表单信息的视图对象，包括表单键、表单名称和表单字段。
     * @throws ApiException 如果任务不存在或表单不存在，则抛出此异常。
     */
    @Override
    public FormVo getStartForm(String taskId) {
        // 查询指定ID的任务
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        // 如果任务不存在，则抛出异常
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        
        // 从任务执行过程中获取开始表单的ID
        String startFormTaskId=runtimeService.getVariable(task.getExecutionId(), "startFormTask").toString();
        
        // 根据表单任务ID获取开始表单的信息
        FormInfo startFormInfo = taskService.getTaskFormModel(startFormTaskId);
        // 如果表单不存在，则抛出异常
        if(startFormInfo==null){
            throw new ApiException("表单不存在");
        }
        
        // 创建表单视图对象，并设置表单的键和名称
        FormVo formVo=new FormVo();
        formVo.setFormKey(startFormInfo.getKey());
        formVo.setFormName(startFormInfo.getName());
        
        // 将表单字段信息从SimpleFormModel转换为FormFieldVo列表
        List<FormField>formFields=((SimpleFormModel)startFormInfo.getFormModel()).getFields();
        List<FormFieldVo>formFieldVos=formFields.stream().map(formField -> 
                new FormFieldVo()
                .setId(formField.getId())
                .setName(formField.getName())
                .setType(formField.getType())
                .setValue(formField.getValue())
                .setReadOnly(formField.isReadOnly())
                .setRequired(formField.isRequired())
            ).collect(Collectors.toList());
        // 将字段信息设置到表单视图对象中
        formVo.setFormFields(formFieldVos);
        return formVo;
    }

    /**
     * 根据任务ID获取任务表单数据。
     * 
     * @param taskId 任务的唯一标识符。
     * @return FormVo 类型的表单数据视图对象，包含表单的键、名称和字段信息。
     * @throws ApiException 如果任务不存在或表单不存在时抛出。
     */
    @Override
    public FormVo getTaskFormData(String taskId){
        // 查询指定ID的任务
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        
        // 获取任务对应的表单模型
        FormInfo taskFormInfo = taskService.getTaskFormModel(taskId);
        if(taskFormInfo==null){
            throw new ApiException("表单不存在");
        }
        
        // 初始化FormVo对象，用于存储最终的表单数据视图
        FormVo formVo=new FormVo();
        formVo.setFormKey(taskFormInfo.getKey()); // 设置表单的键
        formVo.setFormName(taskFormInfo.getName()); // 设置表单的名称
        
        // 将表单模型中的字段信息转换为FormFieldVo对象列表，供FormVo使用
        List<FormField>formFields=((SimpleFormModel)taskFormInfo.getFormModel()).getFields();
        List<FormFieldVo>formFieldVos=formFields.stream().map(formField -> 
                new FormFieldVo()
                .setId(formField.getId())
                .setName(formField.getName())
                .setType(formField.getType())
                .setValue(formField.getValue())
                .setReadOnly(formField.isReadOnly())
                .setRequired(formField.isRequired())
            ).collect(Collectors.toList());
        formVo.setFormFields(formFieldVos);
        
        return formVo;
    }

    /**
     * 根据任务ID获取任务表单。
     * 
     * @param taskId 任务的唯一标识符。
     * @return 返回表单内容的字符串表示。
     * @throws ApiException 如果任务不存在或者表单不存在，则抛出此异常。
     */
    @Override
    public String getTaskForm(String taskId) {
        // 查询指定taskId的任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 如果任务不存在，则抛出异常
        if (task == null) {
            throw new ApiException("任务不存在");
        }

        // 获取任务关联的表单key
        String formKey = task.getFormKey();
        // 如果表单key不存在，则抛出异常
        if (formKey == null) {
            throw new ApiException("表单不存在");
        }
        // 构建表单资源路径
        String path = "/forms/" + formKey;
        ClassPathResource classPathResource = new ClassPathResource(path);
        InputStream inputStream = null;
        String taskForm = null;
        try {
            // 尝试从资源路径加载表单内容
            inputStream = classPathResource.getInputStream();
            taskForm = IOUtils.toString(inputStream, "UTF-8");
            // 将表单内容转换为JSON对象，再转回字符串
            JSONObject jsonobject = JSON.parseObject(taskForm);
            taskForm = jsonobject.toJSONString();
        } catch (Exception e) {
            // 如果加载或转换过程中出现异常，则抛出表单不存在异常
            throw new ApiException("表单不存在");
        }
        return taskForm;
    }

    /**
     * 保存操作员任务信息。
     * 该方法通过给定的任务ID和表单数据，更新任务的相关表单信息。
     * 
     * @param taskId 任务的唯一标识符，用于查找任务。
     * @param form 包含表单字段及其值的映射，用于更新任务的表单数据。
     * @throws ApiException 如果任务不存在，则抛出此异常。
     */
    @Override
    public void saveOperatorTask(String taskId, Map<String, String> form) {
        // 根据任务ID查询任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 如果任务不存在，抛出异常
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        // 保存表单数据
        formService.saveFormData(taskId, form);
    }

    /**
     * 结束指定的任务并将其分配给原所有者。
     * @param taskId 要结束的任务的ID。
     * @throws ApiException 如果任务不存在，则抛出此异常。
     */
    @Override
    public void endAssignedTask(String taskId){
        // 根据任务ID查询任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 如果任务不存在，抛出异常
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        // 将任务重新分配给原所有者
        taskService.setAssignee(taskId, task.getOwner());
        
        // 获取当前操作用户的姓名
        String userName=userService.selectByUserId(UserContext.getCurrentUserId()).getName();
        
        // 更新任务的协作历史
        String jsonString=(String)runtimeService.getVariable(task.getExecutionId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add(task.getName()+"由 "+userName+" 协作中");
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(task.getExecutionId(), "formList", jsonString);

        // 发送任务完成的通知消息给原任务所有者
        String msgName="任务协作完成通知";
        String msgContent=String.format(
            "您委派的任务 %s 已经完成，请及时处理！\n",
            task.getName()
        );
        myMesgSender.sendMessage(task.getOwner(), msgName, msgContent);
    }

    /**
     * 完成操作员自己的任务。
     * @param taskId 任务的ID，用于标识需要完成的任务。
     * @throws ApiException 如果任务不存在，则抛出异常。
     */
    @Override
    public void completeOperatorOwnTask(String taskId) {
        // 根据任务ID查询任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
        }

        // 更新流程中的表单列表变量，将当前任务ID添加到列表中
        String jsonString=(String)runtimeService.getVariable(task.getExecutionId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add(taskId);
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(task.getExecutionId(), "formList", jsonString);

        // 完成任务，并插入操作员实体信息
        taskService.complete(taskId);
        insertOperatorEntity(UserContext.getCurrentUserId(), taskId);
    }

    /**
     * 完成操作员任务。
     * 根据给定的任务ID和表单数据，完成任务并更新流程状态。
     * 
     * @param taskId 任务的唯一标识符。
     * @param form 包含任务表单数据的键值对集合。
     * @throws ApiException 如果任务不存在，则抛出异常。
     */
    @Override
    public void completeOperatorTask(String taskId, Map<String, String> form) {
        // 查询任务以验证任务存在性
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        
        // 更新流程中的表单列表和当前操作者信息
        String jsonString=(String)runtimeService.getVariable(task.getExecutionId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add(taskId);
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(task.getExecutionId(), "formList", jsonString);
        String userName=userService.selectByUserId(UserContext.getCurrentUserId()).getName();
        runtimeService.setVariable(task.getExecutionId(), taskId, userName);

        // 提交任务表单数据
        formService.submitTaskFormData(taskId, form);
        // 记录操作员实体
        insertOperatorEntity(UserContext.getCurrentUserId(), taskId);
    }

    /**
     * 获取历史任务的详细信息。
     * 
     * @param taskId 任务的ID，用于查询历史任务实例。
     * @return TaskVo 返回任务的详细信息模型对象，包括任务名称、任务ID、执行ID、发起人名称、指派人名称、
     *         流程定义名称、任务描述、截止时间、完成时间等。
     * @throws ApiException 如果指定的任务ID不存在，则抛出此异常。
     */
    @Override
    public TaskVo getHistoricalDetails(String taskId) {
        // 根据任务ID查询历史任务实例
        HistoricTaskInstance task=historyService.createHistoricTaskInstanceQuery()
            .taskId(taskId).singleResult();
        // 如果任务不存在，则抛出异常
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        TaskVo taskVo=new TaskVo();

        // 查询发起人ID，并转换为用户名
        String starterId=(String)historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(task.getProcessInstanceId()).variableName("starter").singleResult()
            .getValue();
        String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
        
        // 查询流程定义名称
        String processDefinitionName=repositoryService.createProcessDefinitionQuery().processDefinitionId(
            task.getProcessDefinitionId()
        ).singleResult().getName();
        
        // 查询任务的指派人名称
        String userName=(String)historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(task.getProcessInstanceId()).variableName(taskId)
            .singleResult().getValue();
        
        // 设置任务详情Vo对象的属性
        taskVo.setTaskId(task.getId())
            .setTaskName(task.getName())
            .setExecutionId(task.getExecutionId())
            .setStarterName(starter)
            .setAssigneeName(userName)
            .setProcessDefinitionName(processDefinitionName)
            .setDescription(task.getDescription());
        
        // 如果任务有截止时间，则设置截止时间的字符串表示
        if (task.getDueDate() != null) {
            taskVo.setDueTime(task.getDueDate().toString());   
        }
        
        // 设置任务结束时间的字符串表示
        taskVo.setEndTime(task.getEndTime().toString());
        return taskVo;
    }
}

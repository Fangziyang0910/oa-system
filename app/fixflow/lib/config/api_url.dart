class ApiUrls {
  // server address
  static const String baseUrl = 'http://139.199.168.63:8080';
  /*-------------- user api --------------*/
  // user login
  static const String userLogin = '$baseUrl/user/login';
  // token check
  static const String tokenCheck = '$baseUrl/user/validateToken';

  /*-------------- applicant api ---------*/
  // get ProcessDefinitions list
  static const String listProcessDefinitions = '$baseUrl/applicant/listProcessDefinitions';
  // get ProcessInstances list
  static const String listProcessInstances = '$baseUrl/applicant/listProcessInstancesNotCompleted';
  //get ProcessDefinitionForm
  static const String getStartForm = '$baseUrl/applicant/getStartForm';
  // create a process instance
  static const String createProcessInstance = '$baseUrl/applicant/createProcessInstance';
  //submit the startform
  static const String submitStartForm = '$baseUrl/applicant/submitStartForm';
  // get origin processdiagram
  static const String getOriginalProcessDiagram = '$baseUrl/applicant/getOriginalProcessDiagram';
  // get ProcessInstance
  static const String getProcessInstance = '$baseUrl/applicant/getProcessInstance';
  //get processInstanceDiagram
  static const String getProcessInstanceDiagram = '$baseUrl/applicant/getProcessInstanceDiagram';
  //abort processInstance
  static const String abortProcessInstance = '$baseUrl/applicant/abortProcessInstance';
  //get HistoricalForm
  static const String getHistoricalForm = '$baseUrl/applicant/getHistoricalForm';
  //list ProcessInstancesCompleted
  static const String listProcessInstancesCompleted = '$baseUrl/applicant/listProcessInstancesCompleted';

  /* --------------approval-------------- api */
  //list ApprovalTasksNotCompleted
  static const String listApprovalTasksNotCompleted = '$baseUrl/approver/listApprovalTasksNotCompleted';
  //get TaskStartForm
  static const String getTaskStartForm = '$baseUrl/approver/getStartForm';
  //get TaskForm
  static const String getTaskForm = '$baseUrl/approver/getTaskForm';
  //get ProcessProgress
  static const String getProcessProgress = '$baseUrl/approver/getProcessProgress';

  static const String completeApprovalTask = '$baseUrl/approver/completeApprovalTask';
}

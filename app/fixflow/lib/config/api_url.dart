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
  static const String listProcessInstances = '$baseUrl/applicant/listProcessInstances';
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

}

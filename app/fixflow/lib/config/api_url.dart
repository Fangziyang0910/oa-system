class ApiUrls {
  // server address
  static const String baseUrl = 'http://139.199.168.63:8080';
  /*-------------- user api --------------*/
  // user login
  static const String userLogin = '$baseUrl/user/login';
  // token check
  static const String tokenCheck = '$baseUrl/user/validateToken';
  // get ProcessDefinitions list
  static const String listProcessDefinitions = '$baseUrl/applicant/listProcessDefinitions';
}

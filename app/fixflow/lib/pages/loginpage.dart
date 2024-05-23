import 'dart:convert';

import 'package:fixflow/component/error_snackbar.dart';
import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:fixflow/pages/adminHomePage.dart';
import 'package:fixflow/pages/homepage.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

// the login pages
class LoginWidget extends StatefulWidget {
  const LoginWidget({super.key});

  @override
  State<LoginWidget> createState() => _LoginWidgetState();
}

class _LoginWidgetState extends State<LoginWidget> {
  TextEditingController _usernameController = TextEditingController();
  TextEditingController _passwordController = TextEditingController();

  @override
  void dispose() {
    _usernameController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  // Interact with the backend to complete the login
  void _login(String username, String password, bool isAdmin) async {
    if (username.isEmpty || password.isEmpty) {
      ErrorSnackbar.showSnackBar(context, "用户名或密码不能为空");
      return;
    }

    final Map<String, String> loginParam = {
      'name': username,
      'password': password,
    };

    try {
      final http.Response response = await http.post(
        Uri.parse(isAdmin ? ApiUrls.adminLogin : ApiUrls.userLogin),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        body: jsonEncode(loginParam),
      );

      if (response.statusCode == 200) {
        // 解码响应数据并处理中文乱码
        final decodedResponse = utf8.decode(response.bodyBytes);
        Map<String, dynamic> responseData = jsonDecode(decodedResponse);

        int code = responseData['code'] ?? -1;
        if (code == 0) {
          final token = responseData['data']['token'];
          // Set token using UserTokenProvider
          Provider.of<UserTokenProvider>(context, listen: false)
              .setToken(token, username, password, isAdmin);
          // 将 responseData['data'] 存储到 SharedPreferences
          String userDataJson = jsonEncode(responseData['data']);
          await (await SharedPreferences.getInstance())
              .setString('userData', userDataJson);

          // 登录成功后跳转到相应的主页
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (context) => isAdmin ? const AdminHomePage() : const HomePage()),
          );
        } else if (code == 1004) {
          // 用户名或密码错误，弹出提示框
          ErrorSnackbar.showSnackBar(context, "用户名或密码错误");
        } else {
          // 其他错误，弹出提示框
          ErrorSnackbar.showSnackBar(context, "登录失败，请稍后重试");
        }
      } else {
        ErrorSnackbar.showSnackBar(context, "登录失败，请稍后重试");
      }
    } catch (e) {
      ErrorSnackbar.showSnackBar(context, "登录失败，请检查网络");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            const Text(
              '欢迎登录运维管理系统',
              style: TextStyle(
                fontSize: 28.0,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 40.0),
            TextFormField(
              controller: _usernameController,
              decoration: const InputDecoration(
                labelText: '用户名',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 20.0),
            TextFormField(
              controller: _passwordController,
              obscureText: true,
              decoration: const InputDecoration(
                labelText: '密码',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 40.0),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: () {
                      // get username & password and login
                      String username = _usernameController.text;
                      String password = _passwordController.text;
                      _login(username, password, true);
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.lightBlueAccent, // Light color for admin login
                      minimumSize: Size(double.infinity, 50), // Set the button height
                    ),
                    child: const Text('管理员登录'),
                  ),
                ),
                SizedBox(width: 10), // Space between buttons
                Expanded(
                  child: ElevatedButton(
                    onPressed: () {
                      // get username & password and login
                      String username = _usernameController.text;
                      String password = _passwordController.text;
                      _login(username, password, false);
                    },
                    style: ElevatedButton.styleFrom(
                      minimumSize: Size(double.infinity, 50), // Set the button height
                    ),
                    child: const Text('用户登录'),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

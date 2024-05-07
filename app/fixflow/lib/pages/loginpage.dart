// ignore_for_file: prefer_final_fields, unused_local_variable, unused_import, avoid_print, use_build_context_synchronously

import 'dart:convert';

import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/user_token_provider.dart';
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

  // clean and release the controller
  @override
  void dispose() {
    _usernameController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  // Interact with the backend to complete the login
  void _login(String username, String password) async {
    if (username.isEmpty || password.isEmpty) {
      _showErrorDialog('用户名或密码不能为空');
      return;
    }

    final Map<String, String> loginParam = {
      'name': username,
      'password': password,
    };

    final http.Response response = await http.post(
      Uri.parse(ApiUrls.userLogin),
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
        Provider.of<UserTokenProvider>(context, listen: false).setToken(token);
        // 将 responseData['data'] 存储到 SharedPreferences
        String userDataJson = jsonEncode(responseData['data']);
        await (await SharedPreferences.getInstance())
            .setString('userData', userDataJson);

        // 登录成功后跳转到主页面
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => const HomePage()),
        );
      } else if (code == 1004) {
        // 用户名或密码错误，弹出提示框
        _showErrorDialog('用户名或密码错误');
      } else {
        // 其他错误，弹出提示框
        _showErrorDialog('登录失败，请稍后重试');
      }
    } else {
      print('Failed to login: ${response.statusCode}');
      // 弹出提示框
      _showErrorDialog('登录失败，请稍后重试');
    }
  }

  // 弹出错误提示框
  void _showErrorDialog(String message) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('登录失败'),
          content: Text(message),
          actions: <Widget>[
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: const Text('确定'),
            ),
          ],
        );
      },
    );
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
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () {
                  // get username & password and login
                  String username = _usernameController.text;
                  String password = _passwordController.text;
                  _login(username, password);
                },
                child: const Text('用户登录'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

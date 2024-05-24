// ignore_for_file: library_private_types_in_public_api, prefer_const_constructors, unnecessary_string_interpolations

import 'dart:convert';

import 'package:fixflow/pages/loginpage.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../config/user_token_provider.dart';

class PersonalInfo extends StatefulWidget {
  const PersonalInfo({super.key});

  @override
  _PersonalInfoState createState() => _PersonalInfoState();
}

class _PersonalInfoState extends State<PersonalInfo> {
  // User information fields
  String _username = '';
  String _email = '';
  String _phone = '';
  String _city = '';
  String _department = '';
  String _role = '';

  @override
  void initState() {
    super.initState();
    // Load user information on initialization
    _loadUserInfo();
  }

  // Load user information from shared preferences
  void _loadUserInfo() async {
    String? userDataJson = (await SharedPreferences.getInstance()).getString('userData');
    if (userDataJson != null) {
      Map<String, dynamic> userData = jsonDecode(userDataJson);
      setState(() {
        _username = userData['name'] ?? '';
        _email = userData['email'] ?? '';
        _phone = userData['phone'] ?? '';
        _city = userData['city'] ?? '';
        _department = userData['department'] ?? '';
        _role = userData['role'] ?? '';
      });
    }
  }
  // Logout and clear user information
  void _logout() async {
    // 清空存储在硬盘的用户信息
    await (await SharedPreferences.getInstance()).remove('userData');
    // 获取UserTokenProvider的实例
    final userTokenProvider = Provider.of<UserTokenProvider>(context, listen: false);
    // 调用clearToken方法
    await userTokenProvider.clearToken();
    // 跳转到登录界面
   // 登录成功后跳转到主页面
      Navigator.pushReplacement(
        // ignore: use_build_context_synchronously
        context,
        MaterialPageRoute(builder: (context) => const LoginWidget()),
      );
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Container(
        padding: const EdgeInsets.all(20.0),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(10.0),
          color: Colors.grey[200],
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const CircleAvatar(
                  radius: 50.0,
                  backgroundImage: AssetImage('assets/avatar.png'), // 设置头像图片
                ),
                const SizedBox(width: 20.0),
                Text(
                  '$_username',
                  style: TextStyle(fontSize: 24.0, fontWeight: FontWeight.bold),
                ),
              ],
            ),
            SizedBox(height: 20.0),
            ListTile(
              title: Text('邮箱:', style: TextStyle(fontSize: 20.0)),
              trailing: Text(_email, style: TextStyle(fontSize: 20.0)),
            ),
            ListTile(
              title: Text('电话:', style: TextStyle(fontSize: 20.0)),
              trailing: Text(_phone, style: TextStyle(fontSize: 20.0)),
            ),
            ListTile(
              title: Text('城市:', style: TextStyle(fontSize: 20.0)),
              trailing: Text(_city, style: TextStyle(fontSize: 20.0)),
            ),
            ListTile(
              title: Text('部门:', style: TextStyle(fontSize: 20.0)),
              trailing: Text(_department, style: TextStyle(fontSize: 20.0)),
            ),
            ListTile(
              title: Text('角色:', style: TextStyle(fontSize: 20.0)),
              trailing: Text(_role, style: TextStyle(fontSize: 20.0)),
            ),
            SizedBox(height: 100.0),
            SizedBox( // 添加 SizedBox 以撑开空间
              width: double.infinity, // 宽度撑满
              child: ElevatedButton(
                onPressed: _logout,
                child: const Text('退出登录', style: TextStyle(fontSize: 20.0)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

import 'dart:convert';

import 'package:fixflow/pages/loginpage.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../config/user_token_provider.dart';

class AdminInfoPage extends StatefulWidget {
  const AdminInfoPage({super.key});

  @override
  _AdminInfoPageState createState() => _AdminInfoPageState();
}

class _AdminInfoPageState extends State<AdminInfoPage> {
  String _username = '';
  String _email = '';
  String _phone = '';

  @override
  void initState() {
    super.initState();
    _loadUserInfo();
  }

  // 加载用户信息
  void _loadUserInfo() async {
    String? userDataJson = (await SharedPreferences.getInstance()).getString('userData');
    if (userDataJson != null) {
      Map<String, dynamic> userData = jsonDecode(userDataJson);
      setState(() {
        _username = userData['name'] ?? '';
        _email = userData['email'] ?? '';
        _phone = userData['phone'] ?? '';
      });
    }
  }

  // 退出登录
  void _logout() async {
    // 清空存储在硬盘的用户信息
    await (await SharedPreferences.getInstance()).remove('userData');
    // 获取UserTokenProvider的实例
    final userTokenProvider = Provider.of<UserTokenProvider>(context, listen: false);
    // 调用clearToken方法
    await userTokenProvider.clearToken();
    // 跳转到登录界面
    Navigator.pushReplacement(
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
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Text(
                          '$_username',
                          style: TextStyle(fontSize: 24.0, fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(width: 10.0),
                        Text(
                          '管理员',
                          style: TextStyle(fontSize: 16.0, color: Colors.red),
                        ),
                      ],
                    ),
                  ],
                ),
              ],
            ),
            const SizedBox(height: 20.0),
            ListTile(
              title: Text('邮箱:', style: TextStyle(fontSize: 20.0)),
              trailing: Text(_email, style: TextStyle(fontSize: 20.0)),
            ),
            ListTile(
              title: Text('电话:', style: TextStyle(fontSize: 20.0)),
              trailing: Text(_phone, style: TextStyle(fontSize: 20.0)),
            ),
            const SizedBox(height: 100.0),
            SizedBox(
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

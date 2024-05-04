// ignore_for_file: unused_import

import 'package:fixflow/pages/homepage.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'pages/loginpage.dart';

void main() {
  runApp(MaterialApp(
    locale: const Locale('zh', 'CN'), // 设置默认的语言环境为中文
    localizationsDelegates: const [
      DefaultMaterialLocalizations.delegate,
      DefaultWidgetsLocalizations.delegate,
    ],
    home: const LoginWidget(),
    theme: ThemeData(
      appBarTheme: const AppBarTheme(
        elevation: 0.0,
        color: Colors.transparent,
        systemOverlayStyle: SystemUiOverlayStyle.dark,
      ),
    ),
  ));
}


// ignore_for_file: prefer_const_constructors

import 'package:fixflow/config/user_token_provider.dart';
import 'package:fixflow/pages/initpage.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(MyApp());  
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) {
        final provider = UserTokenProvider();
        return provider;
      },
      child: MaterialApp(
        title: 'FixFlow',
        theme: ThemeData(
          primarySwatch: Colors.blue,
        ),
        home: const InitialPage(),
      ),
    );
  }
}

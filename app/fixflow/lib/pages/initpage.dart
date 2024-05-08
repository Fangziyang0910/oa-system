// ignore_for_file: prefer_const_constructors

import 'package:fixflow/config/user_token_provider.dart';
import 'package:fixflow/pages/homepage.dart';
import 'package:fixflow/pages/loginpage.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class InitialPage extends StatefulWidget {
  const InitialPage({super.key});

  @override
  State<InitialPage> createState() => _InitialPageState();
}

class _InitialPageState extends State<InitialPage> {
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadToken();
  }

  Future<void> _loadToken() async {
    await Provider.of<UserTokenProvider>(context, listen: false).loadToken();
    setState(() {
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(
        body: Center(
          child: CircularProgressIndicator(),
        ),
      );
    } else {
      return Consumer<UserTokenProvider>(
        builder: (context, userTokenProvider, child) {
          // Check the validation of the token
          if (userTokenProvider.isValid) {
            // Token is valid, show the home page
            return HomePage();
          } else {
            // Token is not valid, show the login page
            return LoginWidget();
          }
        },
      );
    }
  }
}
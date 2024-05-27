import 'package:flutter/material.dart';

class ErrorSnackbar {
  static void showSnackBar(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        duration: Duration(seconds: 1), // 设置持续时间为2秒
      ),
    );
  }
}

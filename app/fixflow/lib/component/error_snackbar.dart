import 'package:flutter/material.dart';

class ErrorSnackbar {
  static void showSnackBar(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        action: SnackBarAction(
          label: '确定',
          onPressed: () {
            // 关闭SnackBar
          },
        ),
      ),
    );
  }
}

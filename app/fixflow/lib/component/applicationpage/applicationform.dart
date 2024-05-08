// ignore_for_file: use_super_parameters, prefer_const_constructors

import 'package:flutter/material.dart';

class ApplicationForm extends StatelessWidget {
  final String? processDefinitionKey;

  const ApplicationForm({Key? key, this.processDefinitionKey}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('申请表单'),
      ),
      body: Center(
        child: Text('所选流程定义的Key为: $processDefinitionKey'),
      ),
    );
  }
}
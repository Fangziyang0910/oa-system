// ignore_for_file: use_super_parameters, library_private_types_in_public_api, prefer_const_constructors

import 'package:flutter/material.dart';

class ProcessInstanceDetailWidget extends StatefulWidget {
  final String processInstanceId;

  const ProcessInstanceDetailWidget({Key? key, required this.processInstanceId}) : super(key: key);

  @override
  _ProcessInstanceDetailWidgetState createState() => _ProcessInstanceDetailWidgetState();
}

class _ProcessInstanceDetailWidgetState extends State<ProcessInstanceDetailWidget> {
  @override
  Widget build(BuildContext context) {
    // 这里可以添加您想要展示的Widget，使用widget.processInstanceId作为流程实例ID
    // 例如，显示流程实例ID
    return Scaffold(
      appBar: AppBar(
        title: Text('流程实例详情'),
      ),
      body: Center(
        child: Text(
          '流程实例ID: ${widget.processInstanceId}',
          style: TextStyle(fontSize: 20),
        ),
      ),
    );
  }
}
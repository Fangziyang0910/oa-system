// ignore_for_file: prefer_interpolation_to_compose_strings, unused_field

import 'dart:convert';

import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/function.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:provider/provider.dart';

class ProcessInstanceDetailWidget extends StatefulWidget {
  final String processInstanceId;

  const ProcessInstanceDetailWidget({Key? key, required this.processInstanceId}) : super(key: key);

  @override
  _ProcessInstanceDetailWidgetState createState() => _ProcessInstanceDetailWidgetState();
}

class _ProcessInstanceDetailWidgetState extends State<ProcessInstanceDetailWidget> {
  late Future<ProcessInstanceDetail> _processInstanceDetail;

  @override
  void initState() {
    super.initState();
    _processInstanceDetail = _fetchProcessInstanceDetail();
  }

  Future<ProcessInstanceDetail> _fetchProcessInstanceDetail() async {
  final token = Provider.of<UserTokenProvider>(context, listen: false).token;
  final String url = ApiUrls.getProcessInstance + '/' + widget.processInstanceId;

  try {
    final response = await http.get(
      Uri.parse(url),
      headers: {
        'Accept': '*/*',
        'Authorization': token ?? '',
      },
    );

    if (response.statusCode == 200) {
      final decodedResponse = utf8.decode(response.bodyBytes);
      final responseData = jsonDecode(decodedResponse);
      final int code = responseData['code'];
      if (code == 0) {
        final data = responseData['data'];
        return ProcessInstanceDetail.fromJson(data);
      } else {
        throw Exception('Failed to load process instance detail');
      }
    } else {
      throw Exception('Failed to load process instance detail');
    }
  } catch (e) {
    throw Exception('Failed to connect to the server');
  }
}




Widget _buildDetailItem(String title, String content) {
  return Card(
    child: Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 16.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        crossAxisAlignment: CrossAxisAlignment.start, // 让文本顶部对齐
        children: [
          Flexible( // 使用 Flexible 包裹文本，使其自动换行
            child: Text(
              title,
              style: TextStyle(
                fontSize: 18, // 增加字体大小
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
          SizedBox(width: 8), // 添加间距，使标题和内容之间有一定的距离
          Expanded( // 使用 Expanded 包裹文本，使其自动换行
            child: Text(
              content,
              style: TextStyle(
                fontSize: 18, // 增加字体大小
              ),
            ),
          ),
        ],
      ),
    ),
  );
}


@override
Widget build(BuildContext context) {
  return Scaffold(
    appBar: AppBar(
      title: Text('流程实例详情'),
    ),
    body: FutureBuilder<ProcessInstanceDetail>(
      future: _processInstanceDetail,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return Center(child: CircularProgressIndicator());
        } else if (snapshot.hasError) {
          return Center(child: Text('Error: ${snapshot.error}'));
        } else {
          final processInstanceDetail = snapshot.data!;
          String formattedEndTime = processInstanceDetail.endTime != null
              ? parseDateFormat(processInstanceDetail.endTime!)
              : '暂无';
          return ListView(
            padding: EdgeInsets.all(16.0),
            children: [
              _buildDetailItem('流程名称', processInstanceDetail.processDefinition.processDefinitionName),
              _buildDetailItem('流程ID', processInstanceDetail.processInstanceId),
              _buildDetailItem('开始时间', parseDateFormat(processInstanceDetail.startTime)),
              _buildDetailItem('结束时间', formattedEndTime),
              _buildDetailItem('是否完成', processInstanceDetail.isCompleted ? '是' : '否'),
              SizedBox(height: 16),
            ],
          );
        }
      },
    ),
  );
}


}


class ProcessInstanceDetail {
  final String processInstanceId;
  final ProcessDefinition processDefinition;
  final String startTime;
  final String? endTime;
  final bool isCompleted;
  final List<Progress> progress;

  ProcessInstanceDetail({
    required this.processInstanceId,
    required this.processDefinition,
    required this.startTime,
    this.endTime,
    required this.isCompleted,
    required this.progress,
  });

  factory ProcessInstanceDetail.fromJson(Map<String, dynamic> json) {
    return ProcessInstanceDetail(
      processInstanceId: json['processInstanceId'],
      processDefinition: ProcessDefinition.fromJson(json['processDefinition']),
      startTime: json['startTime'],
      endTime: json['endTime'],
      isCompleted: json['isCompeleted'],
      progress: (json['progress'] as List)
          .map((progressJson) => Progress.fromJson(progressJson))
          .toList(),
    );
  }
}

class ProcessDefinition {
  final String processDefinitionId;
  final String processDefinitionKey;
  final String processDefinitionName;
  final String processDefinitionCategory;
  final String processDefinitionDescription;
  final int processDefinitionVersion;

  ProcessDefinition({
    required this.processDefinitionId,
    required this.processDefinitionKey,
    required this.processDefinitionName,
    required this.processDefinitionCategory,
    required this.processDefinitionDescription,
    required this.processDefinitionVersion,
  });

  factory ProcessDefinition.fromJson(Map<String, dynamic> json) {
    return ProcessDefinition(
      processDefinitionId: json['processDefinitionId'],
      processDefinitionKey: json['processDefinitionKey'],
      processDefinitionName: json['processDefinitionName'],
      processDefinitionCategory: json['processDefinitionCategory'],
      processDefinitionDescription: json['processDefinitionDescription'],
      processDefinitionVersion: json['processDefinitionVersion'],
    );
  }
}

class Progress {
  final String taskId;
  final String taskName;
  final String executionId;
  final String? description;
  final String endTime;

  Progress({
    required this.taskId,
    required this.taskName,
    required this.executionId,
    this.description,
    required this.endTime,
  });

  factory Progress.fromJson(Map<String, dynamic> json) {
    return Progress(
      taskId: json['taskId'],
      taskName: json['taskName'],
      executionId: json['executionId'],
      description: json['description'],
      endTime: json['endTime'],
    );
  }
}


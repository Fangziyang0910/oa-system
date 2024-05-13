// ignore_for_file: prefer_interpolation_to_compose_strings

import 'dart:convert';

import 'package:fixflow/component/applicationpage/historyForm.dart';
import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/classDefinition.dart';
import 'package:fixflow/config/function.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:http/http.dart' as http;
import 'package:timeline_tile/timeline_tile.dart';

class HistoryOperationDetailWidget extends StatefulWidget {
  final String taskId;
  const HistoryOperationDetailWidget({super.key, required this.taskId});

  @override
  State<HistoryOperationDetailWidget> createState() => _HistoryOperationDetailWidgetState();
}

class _HistoryOperationDetailWidgetState extends State<HistoryOperationDetailWidget> {
  late Future<List<myFormField>> _taskForm;
  late Future<List<Progress>> _progress;


  Future<List<myFormField>> fetchTaskForm(String taskId) async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    final String url = ApiUrls.getHistoricalOperationForm + '/' + widget.taskId;
    try {
      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Accept': 'application/json',
          'Authorization': token ?? '',
        },
      );

      if (response.statusCode == 200) {
        final decodedResponse = utf8.decode(response.bodyBytes);
        final responseData = jsonDecode(decodedResponse);
        final int code = responseData['code'];

        if (code == 0) {
          List<dynamic> fields = responseData['data']['formFields'];

          return fields.map((field) => myFormField.fromJson(field)).toList();
        } else {
          throw Exception('Failed to load form data: ${responseData['msg']}');
        }
      } else {
        throw Exception(
            'Failed to load form data with status code: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Failed to connect to the server: $e');
    }
  }
  
  Future<List<Progress>> _fetchProcessProgress() async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    final String url = ApiUrls.getOperationProcessProgress + '/' + widget.taskId;

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
          List<Progress> progressList = (responseData['data'] as List)
              .map((data) => Progress.fromJson(data))
              .toList();
          return progressList;
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

  @override
  void initState() {
    super.initState();
    _taskForm = fetchTaskForm(widget.taskId);
    _progress = _fetchProcessProgress();
  }
  
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('审批历史详情'),
      ),
      body: CustomScrollView(
        slivers: [
          SliverToBoxAdapter(
            child: FutureBuilder<List<myFormField>>(
              future:
                  _taskForm, // 这里假设 _startform 是你的 Future<List<myFormField>>
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return Center(child: CircularProgressIndicator());
                } else if (snapshot.hasError) {
                  return Center(child: Text('Error: ${snapshot.error}'));
                } else if (snapshot.hasData) {
                  return ExpansionTile(
                    title: Text("审批信息",
                        style: TextStyle(
                            fontSize: 20.0, fontWeight: FontWeight.bold)),
                    initiallyExpanded: true,
                    children: snapshot.data!.map((field) {
                      return ListTile(
                        title: Text('${field.name}:',
                            style: TextStyle(fontSize: 18.0)),
                        trailing: Text('${field.value}',
                            style: TextStyle(fontSize: 18.0)),
                      );
                    }).toList(),
                  );
                } else {
                  return Center(child: Text('No data available'));
                }
              },
            ),
          ),

          SliverToBoxAdapter(
            child: SizedBox(
              width: double.infinity,
              child: Padding(
                padding: const EdgeInsets.symmetric(
                    vertical: 16.0, horizontal: 8.0), // 增加垂直方向的间距
                child: Text(
                  '流程进度记录',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ),

          SliverList(
            delegate: SliverChildBuilderDelegate(
              (context, index) {
                return FutureBuilder<List<Progress>>(
                  future: _progress,
                  builder: (context, snapshot) {
                    if (snapshot.connectionState == ConnectionState.waiting) {
                      return Center(child: CircularProgressIndicator());
                    } else if (snapshot.hasError) {
                      return Center(child: Text('Error: ${snapshot.error}'));
                    } else if (snapshot.hasData) {
                      final progressList = snapshot.data!;
                      return Column(
                        children: progressList
                            .map((progress) => _buildTimelineTile(
                                progress,
                                progress == progressList.first,
                                progress == progressList.last))
                            .toList(),
                      );
                    } else {
                      return SizedBox.shrink();
                    }
                  },
                );
              },
              childCount: 1,
            ),
          ),

          

          
        ],
      ),
    );
  }

   Widget _buildTimelineTile(
      Progress progress, bool isFirstItem, bool isLastItem) {
    return TimelineTile(
      alignment: TimelineAlign.manual,
      lineXY: 0.1, // 定义线条和内容的相对位置
      isFirst: isFirstItem,
      isLast: isLastItem,
      indicatorStyle: IndicatorStyle(
        width: 20,
        color: Colors.blue,
        padding: EdgeInsets.all(6),
      ),
      endChild: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  Text(progress.taskName,
                      style:
                          TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                  SizedBox(height: 4),
                  Text(parseDateFormat(progress.endTime),
                      style: TextStyle(fontSize: 14, color: Colors.grey)),
                  SizedBox(height: 4),
                  Text("执行人：" + (progress.assigneeName ?? '未指定'),
                      style: TextStyle(fontSize: 14)),
                ],
              ),
            ),
            PopupMenuButton<String>(
              onSelected: (String value) {
                _handleMenuItemClick(value, progress.taskId, context);
              },
              itemBuilder: (BuildContext context) => <PopupMenuEntry<String>>[
                const PopupMenuItem<String>(
                  value: 'ViewForm',
                  child: Text('查看表单'),
                ),
              ],
              icon: Icon(Icons.more_vert),
            ),
          ],
        ),
      ),
    );
  }

  void _handleMenuItemClick(String value, String taskId, BuildContext context) {
    if (value == 'ViewForm') {
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => FormPage(taskId: taskId)),
      );
    }
  }
}
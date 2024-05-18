import 'dart:convert';

import 'package:fixflow/component/error_snackbar.dart';
import 'package:fixflow/component/operationpage/operationDetail.dart';
import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:http/http.dart' as http;

class CurrentOperationListWidget extends StatefulWidget {
  const CurrentOperationListWidget({super.key});

  @override
  State<CurrentOperationListWidget> createState() =>
      _CurrentOperationListWidgetState();
}

class _CurrentOperationListWidgetState
    extends State<CurrentOperationListWidget> {
  List<Map<String, dynamic>> _tasks = [];
  bool _isLoading = false;
  Future<void> _fetchData({bool refresh = false}) async {
    if (!refresh) {
      setState(() {
        _isLoading = true;
      });
    }

    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    if (token == null || token.isEmpty) {
      print('Token is null or empty');
      return;
    }

    try {
      final response = await http.get(
        Uri.parse(ApiUrls.listOperatorAssignTasks),
        headers: {
          'Accept': 'application/json',
          'Authorization': token,
        },
      );

      if (response.statusCode == 200) {
        final decodedResponse = utf8.decode(response.bodyBytes);
        final Map<String, dynamic> responseData = jsonDecode(decodedResponse);
        final int code = responseData['code'];

        if (code == 0) {
          final List<dynamic> dataList = responseData['data'];
          final List<Map<String, dynamic>> updatedTasks = dataList.map((data) {
            return data as Map<String, dynamic>;
          }).toList();

          setState(() {
            _tasks = updatedTasks;
            _isLoading = false;
          });
        } else {
          // 根据不同的code值显示不同的弹窗
          String message;
          switch (code) {
            case 1001:
              message = "您的登录状态已过期，请重新登陆";
              break;
            case 1002:
              message = "您没有相关权限";
              break;
            case 1003:
              message = "参数校验失败";
              break;
            case 1004:
              message = "接口异常";
              break;
            case 5000:
              message = "未知错误";
              break;
            default:
              message = "未知错误";
          }
          ErrorSnackbar.showSnackBar(context, message);
        }
      } else {
        ErrorSnackbar.showSnackBar(context, '请求失败');
      }
    } catch (e) {
      ErrorSnackbar.showSnackBar(context, '请检查网络');
    } finally {
      if (!refresh) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  @override
  void initState() {
    super.initState();
    _fetchData();
  }

  Future<void> onRefresh() async {
    await _fetchData(refresh: true);
  }

  @override
  Widget build(BuildContext context) {
    final currentUser = Provider.of<UserTokenProvider>(context).username ?? '';

    return Scaffold(
      body: _isLoading
          ? Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: onRefresh,
              child: ListView.builder(
                itemCount: _tasks.length,
                itemBuilder: (context, index) {
                  final task = _tasks[index];
                  final bool isOwner = task['ownerName'] == currentUser;

                  return Card(
                    margin: EdgeInsets.all(16.0),
                    child: ListTile(
                      title: Row(
                        children: <Widget>[
                          Expanded(
                            child: Text(
                              task['processDefinitionName'],
                              style: TextStyle(
                                  fontSize: 20, fontWeight: FontWeight.bold),
                            ),
                          ),
                          Text(
                            '发起人：' + task['starterName'],
                            style: TextStyle(fontSize: 16),
                          ),
                        ],
                      ),
                      subtitle: Text(
                        '当前任务阶段：' + task['taskName'],
                        style: TextStyle(fontSize: 14, color: Colors.grey),
                      ),
                      trailing: isOwner
                          ? null
                          : Text(
                              '协助',
                              style: TextStyle(
                                fontSize: 16,
                                color: Colors.red,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => OperationDetailWidget(
                              taskId: task['taskId'],
                            ),
                          ),
                        );
                      },
                    ),
                  );
                },
              ),
            ),
    );
  }
}

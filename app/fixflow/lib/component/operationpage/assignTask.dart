import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:fixflow/config/api_url.dart';

class AssignTaskWidget extends StatefulWidget {
  final String taskId;

  const AssignTaskWidget({
    super.key,
    required this.taskId,
  });

  @override
  State<AssignTaskWidget> createState() => _AssignTaskWidgetState();
}

class _AssignTaskWidgetState extends State<AssignTaskWidget> {
  String? _selectedAssignee;
  late Future<Map<String, dynamic>> _taskDetailsFuture;
  late Future<List<String>> _candidatesFuture;

  @override
  void initState() {
    super.initState();
    _fetchData();
  }

  void _fetchData() {
    _taskDetailsFuture = fetchTaskDetails();
    _candidatesFuture = fetchCandidates();
  }

  Future<Map<String, dynamic>> fetchTaskDetails() async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    final String url = '${ApiUrls.getTaskNotCompleted}/${widget.taskId}';

    try {
      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Accept': '*/*',
          'Authorization': token ?? '',
        },
      );

      if (response.statusCode == 200) {
        final responseData = jsonDecode(utf8.decode(response.bodyBytes));
        final int code = responseData['code'];
        if (code == 0) {
          return {
            'ownerName': responseData['data']['ownerName'],
            'assigneeName': responseData['data']['assigneeName']
          };
        } else {
          throw Exception(
              'Failed to load task details: ${responseData['msg']}');
        }
      } else {
        throw Exception(
            'Failed to load task details with status code: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Failed to connect to the server: $e');
    }
  }

  Future<List<String>> fetchCandidates() async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    final String url = '${ApiUrls.listOperatorCandidateUsers}/${widget.taskId}';

    try {
      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Accept': '*/*',
          'Authorization': token ?? '',
        },
      );

      if (response.statusCode == 200) {
        final responseData = jsonDecode(utf8.decode(response.bodyBytes));
        final int code = responseData['code'];
        if (code == 0) {
          return List<String>.from(responseData['data']);
        } else {
          throw Exception('Failed to load candidates: ${responseData['msg']}');
        }
      } else {
        throw Exception(
            'Failed to load candidates with status code: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Failed to connect to the server: $e');
    }
  }

  Future<void> assignTask() async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    final String url =
        '${ApiUrls.assignTask}?taskId=${widget.taskId}&userName=$_selectedAssignee';

    try {
      final response = await http.post(
        Uri.parse(url),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token ?? '',
        },
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        showDialog(
          context: context,
          builder: (BuildContext context) {
            return AlertDialog(
              title: Text('成功'),
              content: Text('任务委派成功'),
              actions: <Widget>[
                TextButton(
                  onPressed: () {
                    Navigator.of(context).pop();
                    setState(() {
                      _fetchData(); // Re-fetch data to update the task details
                    });
                  },
                  child: Text('确定'),
                ),
              ],
            );
          },
        );
      } else {
        showDialog(
          context: context,
          builder: (BuildContext context) {
            return AlertDialog(
              title: Text('失败'),
              content: Text('任务委派失败'),
              actions: <Widget>[
                TextButton(
                  onPressed: () {
                    Navigator.of(context).pop();
                  },
                  child: Text('确定'),
                ),
              ],
            );
          },
        );
      }
    } catch (e) {
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: Text('错误'),
            content: Text('网络连接失败: $e'),
            actions: <Widget>[
              TextButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: Text('确定'),
              ),
            ],
          );
        },
      );
    }
  }

  Future<void> unassignTask() async {
  final token = Provider.of<UserTokenProvider>(context, listen: false).token;
  final String url = '${ApiUrls.unassignTask}/${widget.taskId}';

  try {
    final response = await http.get(
      Uri.parse(url),
      headers: {
        'Accept': 'application/json',
        'Authorization': token ?? '',
      },
    );

    if (response.statusCode == 200) {
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: Text('成功'),
            content: Text('任务协作已撤销'),
            actions: <Widget>[
              TextButton(
                onPressed: () {
                  Navigator.of(context).pop();
                  setState(() {
                    _fetchData(); // Re-fetch data to update the task details
                  });
                },
                child: Text('确定'),
              ),
            ],
          );
        },
      );
    } else {
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: Text('失败'),
            content: Text('任务协作撤销失败'),
            actions: <Widget>[
              TextButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: Text('确定'),
              ),
            ],
          );
        },
      );
    }
  } catch (e) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('错误'),
          content: Text('网络连接失败: $e'),
          actions: <Widget>[
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: Text('确定'),
            ),
          ],
        );
      },
    );
  }
}


  @override
Widget build(BuildContext context) {
  final currentUser = Provider.of<UserTokenProvider>(context).username ?? '';

  return Scaffold(
    appBar: AppBar(
      title: const Text('运维协助'),
    ),
    body: FutureBuilder<Map<String, dynamic>>(
      future: _taskDetailsFuture,
      builder: (context, taskSnapshot) {
        if (taskSnapshot.connectionState == ConnectionState.waiting) {
          return Center(child: CircularProgressIndicator());
        } else if (taskSnapshot.hasError) {
          return Center(child: Text('Error: ${taskSnapshot.error}'));
        } else if (taskSnapshot.hasData) {
          final taskDetails = taskSnapshot.data!;
          final String ownerName = taskDetails['ownerName'];
          final String assigneeName = taskDetails['assigneeName'];
          final bool isOwner = ownerName == currentUser;
          final bool isAssignee = assigneeName == currentUser;
          final bool isOwnerAndAssigneeSame = isOwner && isAssignee;

          return Center(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Card(
                elevation: 4,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(
                            '任务主人:',
                            style: TextStyle(
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                              color: Colors.blueAccent,
                            ),
                          ),
                          Text(
                            ownerName,
                            style: TextStyle(
                              fontSize: 18,
                              color: Colors.black87,
                            ),
                          ),
                        ],
                      ),
                      SizedBox(height: 16),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(
                            '协助人:',
                            style: TextStyle(
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                              color: Colors.green,
                            ),
                          ),
                          Text(
                            isOwnerAndAssigneeSame ? '无' : assigneeName,
                            style: TextStyle(
                              fontSize: 18,
                              color: Colors.black87,
                            ),
                          ),
                        ],
                      ),
                      if (isOwnerAndAssigneeSame)
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            SizedBox(height: 16),
                            Text(
                              '请选发起协作的对象:',
                              style: TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                                color: Colors.orange,
                              ),
                            ),
                            SizedBox(height: 8),
                            FutureBuilder<List<String>>(
                              future: _candidatesFuture,
                              builder: (context, snapshot) {
                                if (snapshot.connectionState == ConnectionState.waiting) {
                                  return CircularProgressIndicator();
                                } else if (snapshot.hasError) {
                                  return Text('Error: ${snapshot.error}');
                                } else if (snapshot.hasData) {
                                  final candidates = snapshot.data!;
                                  return DropdownButtonFormField<String>(
                                    value: _selectedAssignee,
                                    onChanged: (value) {
                                      setState(() {
                                        _selectedAssignee = value;
                                      });
                                    },
                                    items: candidates.map((String candidate) {
                                      return DropdownMenuItem<String>(
                                        value: candidate,
                                        child: Text(candidate),
                                      );
                                    }).toList(),
                                    decoration: InputDecoration(
                                      border: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(10.0),
                                      ),
                                    ),
                                    validator: (value) {
                                      if (value == null || value.isEmpty) {
                                        return '请选择协作对象';
                                      }
                                      return null;
                                    },
                                  );
                                } else {
                                  return Text('No candidates available');
                                }
                              },
                            ),
                            SizedBox(height: 16),
                            ElevatedButton(
                              onPressed: _selectedAssignee == null ? null : () {
                                assignTask();
                              },
                              child: Text('发起协作'),
                            ),
                          ],
                        ),
                      if (isOwner && !isAssignee)
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            SizedBox(height: 16),
                            Text(
                              '你已经发起了协作，请等待协作完成',
                              style: TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                                color: Colors.orange,
                              ),
                            ),
                            SizedBox(height: 16),
                            ElevatedButton(
                              onPressed: () {
                                unassignTask();
                              },
                              child: Text('撤销协作'),
                            ),
                          ],
                        ),
                      if (!isOwner && isAssignee)
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            SizedBox(height: 16),
                            Text(
                              '你正在协助此任务',
                              style: TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                                color: Colors.orange,
                              ),
                            ),
                          ],
                        ),
                    ],
                  ),
                ),
              ),
            ),
          );
        } else {
          return Center(child: Text('No task details available'));
        }
      },
    ),
  );
}

}

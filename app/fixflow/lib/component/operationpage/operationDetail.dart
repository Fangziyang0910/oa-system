import 'dart:convert';

import 'package:fixflow/component/error_snackbar.dart';
import 'package:fixflow/component/operationpage/assignTask.dart';
import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/classDefinition.dart';
import 'package:fixflow/config/function.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:http/http.dart' as http;

class OperationDetailWidget extends StatefulWidget {
  final String taskId;

  const OperationDetailWidget({super.key, required this.taskId});

  @override
  State<OperationDetailWidget> createState() => _OperationDetailWidgetState();
}

class _OperationDetailWidgetState extends State<OperationDetailWidget> {
  late Future<List<myFormField>> _startform;
  late Future<List<Map<String, dynamic>>> _dataForm;
  late Future<Map<String, String>> _taskDetails;
  final Map<String, dynamic> _formState = {};
  final Map<String, TextEditingController> _controllers = {};
  final _formKey = GlobalKey<FormState>();
  String _formName = '';
  Map<String, List<String>> _dropdownCache = {};
  late Future<List<Progress>> _progress;

  Future<List<myFormField>> fetchStartForm(String taskId) async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    final String url = ApiUrls.getOperationStartForm + '/' + widget.taskId;
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
    final String url =
        ApiUrls.getOperationProcessProgress + '/' + widget.taskId;

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

  Future<List<Map<String, dynamic>>> _fetchDataForm() async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    final String url = ApiUrls.getOperationForm + '/' + widget.taskId;
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
          final data = responseData['data'];
          final fieldsList = data['formFields'];
          final processForm = List<Map<String, dynamic>>.from(fieldsList);

          for (final field in processForm) {
            _formState[field['id']] = field['value'];
            _controllers[field['id']] =
                TextEditingController(text: field['value']);
          }

          setState(() {
            _formName = data['formName'] ?? '';
          });

          return processForm;
        } else {
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
          return [];
        }
      } else {
        return [];
      }
    } catch (e) {
      ErrorSnackbar.showSnackBar(context, '请检查网络');
      return [];
    }
  }

  Future<Map<String, String>> _fetchTaskDetails() async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    final String url = '${ApiUrls.getTaskNotCompleted}/${widget.taskId}';
    try {
      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Accept': 'application/json',
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

  Future<List<String>> _fetchUrlList(String url) async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
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
        final List<String> departmentList =
            List<String>.from(responseData['data']);
        return departmentList;
      } else {
        throw Exception('Failed to load department list');
      }
    } catch (e) {
      throw Exception('Failed to connect to the server');
    }
  }

  @override
  void initState() {
    super.initState();
    _startform = fetchStartForm(widget.taskId);
    _progress = _fetchProcessProgress();
    _dataForm = _fetchDataForm();
    _taskDetails = _fetchTaskDetails();
  }

  Future<void> _completeAssistance() async {
    // 先保存表单
    bool saveSuccess = await _saveForm();

    if (!saveSuccess) {
      return;
    }

    // 如果保存表单成功，再完成协助
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    final String url = '${ApiUrls.endAssignedTask}/${widget.taskId}';

    try {
      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Accept': 'application/json',
          'Authorization': token ?? '',
        },
      );

      if (response.statusCode == 200) {
        final responseData = jsonDecode(utf8.decode(response.bodyBytes));
        final int code = responseData['code'];
        if (code == 0) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('协助已完成')),
          );
          Navigator.of(context).pop();
        } else {
          ErrorSnackbar.showSnackBar(context, '协助完成失败: ${responseData['msg']}');
        }
      } else {
        ErrorSnackbar.showSnackBar(context, '无法完成协助，状态码: ${response.statusCode}');
      }
    } catch (e) {
      ErrorSnackbar.showSnackBar(context, '请检查网络');
    }
  }

  void _handleMenuItemClick(String value) {
    switch (value) {
      case 'unclaimTask':
        // Show confirmation dialog for unclaim task
        showDialog(
          context: context,
          builder: (BuildContext context) {
            return AlertDialog(
              title: Text('确认'),
              content: Text('确定要放弃任务吗？'),
              actions: [
                TextButton(
                  onPressed: () {
                    Navigator.of(context).pop(); // Dismiss the dialog
                  },
                  child: Text('取消'),
                ),
                TextButton(
                  onPressed: () async {
                    final token =
                        Provider.of<UserTokenProvider>(context, listen: false)
                            .token;
                    final String url =
                        '${ApiUrls.unclaimCandidateTask}/${widget.taskId}';
                    // Add your unclaim task logic here
                    print('Unclaim Task');
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
                          Navigator.of(context).pop(); // Dismiss the dialog
                          Navigator.of(context)
                              .pop(); // Go back to the previous page
                        } else {
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
                        ErrorSnackbar.showSnackBar(context, '放弃任务失败');
                      }
                    } catch (e) {
                      ErrorSnackbar.showSnackBar(context, '请检查网络');
                    }
                  },
                  child: Text('确定'),
                ),
              ],
            );
          },
        );
        break;
      case 'assistTask':
        // Handle assist task action
        print('Assist Task');
        // Add your logic here
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => AssignTaskWidget(
              taskId: widget.taskId,
            ),
          ),
        );
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    final currentUser = Provider.of<UserTokenProvider>(context).username ?? '';

    return Scaffold(
      appBar: AppBar(
        title: Text(_formName),
        actions: [
          PopupMenuButton<String>(
            onSelected: _handleMenuItemClick,
            itemBuilder: (BuildContext context) {
              return [
                PopupMenuItem<String>(
                  value: 'unclaimTask',
                  child: Text('放弃任务'),
                ),
                PopupMenuItem<String>(
                  value: 'assistTask',
                  child: Text('任务协助'),
                ),
              ];
            },
          ),
        ],
      ),
      body: FutureBuilder<Map<String, String>>(
        future: _taskDetails,
        builder: (context, taskDetailsSnapshot) {
          if (taskDetailsSnapshot.connectionState == ConnectionState.waiting) {
            return Center(child: CircularProgressIndicator());
          } else if (taskDetailsSnapshot.hasError) {
            return Center(child: Text('Error: ${taskDetailsSnapshot.error}'));
          } else if (taskDetailsSnapshot.hasData) {
            final taskDetails = taskDetailsSnapshot.data!;
            final String ownerName = taskDetails['ownerName']!;
            final String assigneeName = taskDetails['assigneeName']!;
            final bool isOwner = ownerName == currentUser;
            final bool isAssignee = assigneeName == currentUser;
            final bool showCompleteAssistanceButton = !isOwner && isAssignee;

            return CustomScrollView(
              slivers: [
                SliverToBoxAdapter(
                  child: FutureBuilder<List<myFormField>>(
                    future: _startform,
                    builder: (context, snapshot) {
                      if (snapshot.connectionState == ConnectionState.waiting) {
                        return Center(child: CircularProgressIndicator());
                      } else if (snapshot.hasError) {
                        return Center(child: Text('Error: ${snapshot.error}'));
                      } else if (snapshot.hasData) {
                        return ExpansionTile(
                          title: Text("申请信息",
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
                          vertical: 16.0, horizontal: 8.0),
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
                          if (snapshot.connectionState ==
                              ConnectionState.waiting) {
                            return Center(child: CircularProgressIndicator());
                          } else if (snapshot.hasError) {
                            return Center(
                                child: Text('Error: ${snapshot.error}'));
                          } else if (snapshot.hasData) {
                            final progressList = snapshot.data!;
                            return Column(
                              children: progressList
                                  .map((progress) => buildTimelineTile(
                                      progress,
                                      progress == progressList.first,
                                      progress == progressList.last,
                                      context))
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
                SliverToBoxAdapter(
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: Text(
                      '审批表单',
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ),
                SliverToBoxAdapter(
                  child: FutureBuilder<List<Map<String, dynamic>>>(
                    future: _dataForm,
                    builder: (context, snapshot) {
                      if (snapshot.connectionState == ConnectionState.waiting) {
                        return Center(child: CircularProgressIndicator());
                      } else if (snapshot.hasError) {
                        return Center(child: Text('Error: ${snapshot.error}'));
                      } else {
                        final List<Map<String, dynamic>> formFields =
                            snapshot.data ?? [];
                        return Padding(
                          padding: EdgeInsets.all(16.0),
                          child: Form(
                            key: _formKey,
                            child: Column(
                              children: [
                                ...formFields
                                    .map((field) => _buildField(field))
                                    .toList(),
                                SizedBox(height: 16),
                                Row(
                                  children: [
                                    Expanded(
                                      child: ElevatedButton(
                                        style: ElevatedButton.styleFrom(
                                          backgroundColor: Colors.lightGreen,
                                        ),
                                        onPressed: () {
                                          _saveForm();
                                        },
                                        child: Text('暂存表单'),
                                      ),
                                    ),
                                    SizedBox(width: 16),
                                    Expanded(
                                      child: ElevatedButton(
                                        onPressed: showCompleteAssistanceButton
                                            ? () {
                                                _completeAssistance();
                                              }
                                            : () {
                                                _submitForm();
                                              },
                                        child: Text(showCompleteAssistanceButton
                                            ? '完成协助'
                                            : '提交表单'),
                                      ),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        );
                      }
                    },
                  ),
                ),
              ],
            );
          } else {
            return Center(child: Text('No task details available'));
          }
        },
      ),
    );
  }

  Widget _buildField(Map<String, dynamic> field) {
    final String? fieldType = field['type'];
    final String? fieldid = field['id'];
    final String? fieldName = field['name'];
    final String? placeholder = field['placeholder'];
    final bool required = field['required'];

    _controllers.putIfAbsent(fieldid!, () => TextEditingController());

    Widget inputWidget;

    if (placeholder != null && placeholder != 'empty') {
      if (_dropdownCache.containsKey(placeholder)) {
        inputWidget = DropdownButtonFormField<String>(
          value: _formState[fieldid],
          onChanged: (value) {
            setState(() {
              _formState[fieldid] = value;
            });
          },
          items: _dropdownCache[placeholder]!.map((department) {
            return DropdownMenuItem<String>(
              value: department,
              child: Text(department),
            );
          }).toList(),
          decoration: InputDecoration(
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(10.0),
            ),
          ),
          validator: (value) {
            if (required && (value == null || value.isEmpty)) {
              return '请选择' + fieldName!;
            }
            return null;
          },
        );
      } else {
        inputWidget = FutureBuilder<List<String>>(
          future: _fetchUrlList(ApiUrls.baseUrl + placeholder),
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return CircularProgressIndicator();
            } else if (snapshot.hasError) {
              return Text('Error: ${snapshot.error}');
            } else {
              final List<String> departmentList = snapshot.data ?? [];
              _dropdownCache[placeholder] = departmentList;
              return DropdownButtonFormField<String>(
                value: _formState[fieldid],
                onChanged: (value) {
                  setState(() {
                    _formState[fieldid] = value;
                  });
                },
                items: departmentList.map((department) {
                  return DropdownMenuItem<String>(
                    value: department,
                    child: Text(department),
                  );
                }).toList(),
                decoration: InputDecoration(
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(10.0),
                  ),
                ),
                validator: (value) {
                  if (required && (value == null || value.isEmpty)) {
                    return '请选择' + fieldName!;
                  }
                  return null;
                },
              );
            }
          },
        );
      }
    } else {
      switch (fieldType) {
        case 'integer':
          inputWidget = TextFormField(
            controller: _controllers[fieldid],
            decoration: InputDecoration(
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(10.0),
              ),
              labelText: '',
            ),
            keyboardType: TextInputType.number,
            onChanged: (value) {
              setState(() {
                _formState[fieldid] = int.tryParse(value);
              });
            },
            validator: (value) {
              if (required && (value == null || value.isEmpty)) {
                return '请输入' + fieldName!;
              }
              if (value != null && value.isNotEmpty) {
                final isValid =
                    int.tryParse(value) != null && int.parse(value) > 0;
                if (!isValid) {
                  return '请输入正整数';
                }
              }
              return null;
            },
          );
          break;
        case 'date':
          inputWidget = TextFormField(
            controller: _controllers[fieldid],
            decoration: InputDecoration(
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(10.0),
              ),
              labelText: '',
              suffixIcon: IconButton(
                icon: Icon(Icons.calendar_today),
                onPressed: () async {
                  final DateTime? pickedDate = await showDatePicker(
                    context: context,
                    initialDate: DateTime.now(),
                    firstDate: DateTime(1900),
                    lastDate: DateTime(2100),
                  );
                  if (pickedDate != null) {
                    setState(() {
                      final formattedDate =
                          '${pickedDate.year}-${pickedDate.month}-${pickedDate.day}';
                      _controllers[fieldid]!.text = formattedDate;
                      _formState[fieldid] = formattedDate;
                    });
                  }
                },
              ),
            ),
            keyboardType: TextInputType.datetime,
            readOnly: true,
            validator: (value) {
              if (required && (value == null || value.isEmpty)) {
                return '请选择' + fieldName!;
              }
              return null;
            },
          );
          break;
        case 'boolean':
          // Boolean dropdown input
          inputWidget = DropdownButtonFormField<bool>(
            value: _formState[fieldid],
            onChanged: (value) {
              setState(() {
                _formState[fieldid] = value;
              });
            },
            items: [
              DropdownMenuItem<bool>(
                value: true,
                child: Text('是'),
              ),
              DropdownMenuItem<bool>(
                value: false,
                child: Text('否'),
              ),
            ],
            decoration: InputDecoration(
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(10.0),
              ),
            ),
            validator: (value) {
              if (required && value == null) {
                return '请选择' + fieldName!;
              }
              return null;
            },
          );
          break;
        default:
          inputWidget = TextFormField(
            controller: _controllers[fieldid],
            decoration: InputDecoration(
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(10.0),
              ),
              labelText: '',
            ),
            onChanged: (value) {
              setState(() {
                _formState[fieldid] = value;
              });
            },
            validator: (value) {
              if (required && (value == null || value.isEmpty)) {
                return '请输入' + fieldName!;
              }
              return null;
            },
          );
      }
    }

    return Padding(
      padding: EdgeInsets.symmetric(vertical: 8.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Text(
                fieldName ?? '',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              if (required) Text('*', style: TextStyle(color: Colors.red)),
            ],
          ),
          SizedBox(height: 8),
          inputWidget,
        ],
      ),
    );
  }

  void _submitForm() async {
    if (_formKey.currentState!.validate()) {
      await createProcessInstance();
    }
  }

  Future<void> createProcessInstance() async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;

    final Map<String, dynamic> formData = {
      "form": _formState, // Directly using the _formState map.
      "taskId": widget.taskId
    };

    try {
      final response = await http.post(
        Uri.parse(ApiUrls.completeOperatorTask),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': token ?? '',
        },
        body: jsonEncode(formData),
      );

      if (response.statusCode == 200) {
        final decodedResponse = utf8.decode(response.bodyBytes);
        final responseData = jsonDecode(decodedResponse);
        final int code = responseData['code'];
        if (code == 0) {
          Navigator.pop(context);
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('提交成功')),
          );
        } else {
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
        ErrorSnackbar.showSnackBar(context, '无法提交表单');
      }
    } catch (e) {
      ErrorSnackbar.showSnackBar(context, '请检查网络');
    }
    return;
  }

  Future<bool> _saveForm() async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;

    final Map<String, dynamic> formData = {
      "form": _formState, // 直接使用 _formState map
      "taskId": widget.taskId
    };

    try {
      final response = await http.post(
        Uri.parse(ApiUrls.saveOperatorTask),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': token ?? '',
        },
        body: jsonEncode(formData),
      );

      if (response.statusCode == 200) {
        final decodedResponse = utf8.decode(response.bodyBytes);
        final responseData = jsonDecode(decodedResponse);
        final int code = responseData['code'];
        if (code == 0) {
          ErrorSnackbar.showSnackBar(context, '成功暂存表单');
          return true;
        } else {
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
          return false;
        }
      } else {
        ErrorSnackbar.showSnackBar(context, '无法暂存表单');
        return false;
      }
    } catch (e) {
      ErrorSnackbar.showSnackBar(context, '请检查网络');
      return false;
    }
  }
}

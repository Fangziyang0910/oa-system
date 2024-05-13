// ignore_for_file: prefer_interpolation_to_compose_strings

import 'dart:convert';

import 'package:fixflow/component/applicationpage/historyForm.dart';
import 'package:fixflow/component/error_snackbar.dart';
import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/classDefinition.dart';
import 'package:fixflow/config/function.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:http/http.dart' as http;
import 'package:timeline_tile/timeline_tile.dart';

class OperationDetailWidget extends StatefulWidget {
  final String taskId;
  const OperationDetailWidget({super.key, required this.taskId});

  @override
  State<OperationDetailWidget> createState() => _OperationDetailWidgetState();
}

class _OperationDetailWidgetState extends State<OperationDetailWidget> {
  late Future<List<myFormField>> _startform;
  late Future<List<Map<String, dynamic>>> _dataForm;
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
          final dataString = responseData['data'];
          final dataMap = jsonDecode(dataString);
          final fieldsList = dataMap['fields'];
          final processForm = List<Map<String, dynamic>>.from(fieldsList);

          for (final field in processForm) {
            _formState[field['id']] = null;
          }

          setState(() {
            _formName = dataMap['name'] ?? '';
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

  Future<List<String>> _fetchUrlList(String url) async {
    print(url);
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
        final dataString = responseData['data'];
        final List<String> departmentList =
            List<String>.from(jsonDecode(dataString));
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
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_formName),
      ),
      body: CustomScrollView(
        slivers: [
          SliverToBoxAdapter(
            child: FutureBuilder<List<myFormField>>(
              future:
                  _startform, // 这里假设 _startform 是你的 Future<List<myFormField>>
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

          // SliverToBoxAdapter for displaying form
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
                          SizedBox(
                            width: double.infinity, // 让SizedBox占据整个屏幕的宽度
                            child: ElevatedButton(
                              onPressed: () {
                                _submitForm();
                              },
                              child: Text('提交表单'),
                            ),
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
              final isValid = int.tryParse(value) != null && int.parse(value) > 0;
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
      "form": _formState,  // Directly using the _formState map.
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
}
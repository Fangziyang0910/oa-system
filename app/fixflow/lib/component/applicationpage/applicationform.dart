// ignore_for_file: use_super_parameters, prefer_interpolation_to_compose_strings, use_build_context_synchronously, prefer_const_constructors

import 'dart:convert';

import 'package:fixflow/component/error_snackbar.dart';
import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:fixflow/pages/homepage.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:http/http.dart' as http;

class ApplicationForm extends StatefulWidget {
  final String? processDefinitionKey;

  const ApplicationForm({Key? key, this.processDefinitionKey})
      : super(key: key);

  @override
  State<ApplicationForm> createState() => _ApplicationFormState();
}

class _ApplicationFormState extends State<ApplicationForm> {
  late Future<List<Map<String, dynamic>>> _dataForm;
  final Map<String, dynamic> _formState = {}; // 表单状态映射
  final Map<String, TextEditingController> _controllers = {};
  String _formName = ''; // 表单名称
  final _formKey = GlobalKey<FormState>();

  @override
  void initState() {
    super.initState();
    _dataForm = _fetchDataForm();
  }

  Future<List<Map<String, dynamic>>> _fetchDataForm() async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    final String url =
        ApiUrls.getStartForm + '/' + widget.processDefinitionKey!;
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
        final List<String> departmentList = List<String>.from(jsonDecode(dataString));
        return departmentList;
      } else {
        // Handle error cases
        throw Exception('Failed to load department list');
      }
    } catch (e) {
      // Handle network errors
      throw Exception('Failed to connect to the server');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_formName),
        leading: IconButton(
          icon: Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.of(context).pop();
          },
        ),
      ),
      body: FutureBuilder<List<Map<String, dynamic>>>(
        future: _dataForm,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else {
            final List<Map<String, dynamic>> formFields = snapshot.data ?? [];
            return Padding(
              padding: EdgeInsets.all(16.0),
              child: SingleChildScrollView(
                child: Form(
                  key: _formKey,
                  child: Column(
                    children: [
                      ...formFields.map((field) => _buildField(field)).toList(),
                      SizedBox(height: 16), // Add some space before the button
                      ElevatedButton(
                        onPressed: () {
                          _submitForm();
                        },
                        child: Text('提交表单'),
                      ),
                    ],
                  ),
                ),
              ),
            );
          }
        },
      ),
    );
  }

  Widget _buildField(Map<String, dynamic> field) {
    final String? fieldType = field['type'];
  final String? fieldid = field['id'];
  final String? fieldName = field['name'];
  final String? placeholder = field['placeholder']; // New: Get the placeholder value
  final bool required = field['required'];

    _controllers.putIfAbsent(fieldid!, () => TextEditingController());

    Widget inputWidget;

    if (placeholder != null && placeholder != 'empty') {
    // If placeholder is not 'empty', construct a dropdown button
    inputWidget = FutureBuilder<List<String>>(
      future: _fetchUrlList(ApiUrls.baseUrl + placeholder),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return CircularProgressIndicator();
        } else if (snapshot.hasError) {
          return Text('Error: ${snapshot.error}');
        } else {
          final List<String> departmentList = snapshot.data ?? [];
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
  } else {

    if (fieldType == 'integer') {
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
            _formState[fieldid] = value;
          });
        },
        validator: (value) {
          if (required && (value == null || value.isEmpty)) {
            return '请输入' + fieldName!;
          }
          // Additional validation for positive integer
          if (value != null && value.isNotEmpty) {
            final isValid = int.tryParse(value) != null && int.parse(value) > 0;
            if (!isValid) {
              return '请输入正整数';
            }
          }
          return null;
        },
      );
    } else if (fieldType == 'date') {
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
    } else {
      // Default to string type
      inputWidget = TextFormField(
        controller: _controllers[fieldid],
        decoration: InputDecoration(
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(10.0),
          ),
          labelText: '',
        ),
        keyboardType: TextInputType.text,
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

// 修改 _submitForm 方法，添加校验逻辑，并在有错误时设置对应输入框的错误提示
  void _submitForm() async {
    if (_formKey.currentState!.validate()) {
      await createProcessInstance();
    }
  }

  @override
  void dispose() {
    _controllers.values.forEach((controller) => controller.dispose());
    super.dispose();
  }

  Future<void> createProcessInstance() async {
    final String createurl =
        ApiUrls.createProcessInstance + '/' + widget.processDefinitionKey!;
    final String submiturl = ApiUrls.submitStartForm;
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    String? processInstanceId;

    try {
      final response = await http.get(
        Uri.parse(createurl),
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
          processInstanceId = responseData['data']['processInstanceId'];
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
        ErrorSnackbar.showSnackBar(context, '创建流程失败');
      }
    } catch (e) {
      ErrorSnackbar.showSnackBar(context, '请检查网络');
    }

    if (processInstanceId == null) {
      return;
    }

    // Prepare form data
    final Map<String, dynamic> formData = {
      "form": {
        "applicantName": _formState['applicantName'],
        "applicantDepartment": _formState['applicantDepartment'],
        "applicantRole": _formState['applicantRole'],
        "applicantEmail": _formState['applicantEmail'],
        "applicantPhone": _formState['applicantPhone'],
        "leaveDays": _formState['leaveDays'],
        "leaveReason": _formState['leaveReason'],
        "startTime": _formState['startTime'],
        "endTime": _formState['endTime']
      },
      "processInstanceId": processInstanceId
    };

    try {
      final response = await http.post(
        Uri.parse(submiturl),
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
          Navigator.pushReplacement(
          context,
          MaterialPageRoute(
            builder: (context) => HomePage(),
          ),
        );
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

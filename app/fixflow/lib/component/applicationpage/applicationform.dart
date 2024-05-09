// ignore_for_file: use_super_parameters, prefer_const_constructors, unused_field, prefer_interpolation_to_compose_strings, unused_local_variable

import 'dart:convert';

import 'package:fixflow/component/error_snackbar.dart';
import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:http/http.dart' as http;


class ApplicationForm extends StatefulWidget {
  final String? processDefinitionKey;
  const ApplicationForm({Key? key, this.processDefinitionKey}) : super(key: key);

  @override
  State<ApplicationForm> createState() => _ApplicationFormState();
}

class _ApplicationFormState extends State<ApplicationForm> {
  late Future<List<Map<String, dynamic>>> _dataForm;

  @override
  void initState() {
    super.initState();
    // 在初始化时请求服务器获取流程列表数据
    _dataForm = _fetchDataForm();
  }

  Future<List<Map<String, dynamic>>> _fetchDataForm() async {
    // 从Provider中获取token
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    // 将 processDefinitionKey 拼接到 API 地址中
    final String url = ApiUrls.getStartForm + '/' + widget.processDefinitionKey!;
    // 发送请求获取流程列表数据
    try {
      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Accept': 'application/json',
          'Authorization': token ?? '',
        },
      );

      if (response.statusCode == 200) {
        // 解码响应数据并处理中文乱码
        final decodedResponse = utf8.decode(response.bodyBytes);
        Map<String, dynamic> responseData = jsonDecode(decodedResponse);
        // 解析响应数据
        final int code = responseData['code'];
        if (code == 0) {
          // "data" 字段是一个 JSON 字符串，需要再次解码
        final dataString = responseData['data'];
        final Map<String, dynamic> dataMap = jsonDecode(dataString);

        // "fields" 字段是一个列表，我们直接提取并转换为 List<Map<String, dynamic>>
        final List<dynamic> fieldsList = dataMap['fields'];
        final List<Map<String, dynamic>> processForm = List<Map<String, dynamic>>.from(fieldsList);

        // 返回解析后的数据
        return processForm;
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
          return [];
        }
      } else {
        // 请求失败时返回空列表
        return [];
      }
    } catch (e) {
      ErrorSnackbar.showSnackBar(context, '请检查网络');
      return [];
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('申请表单'),
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
              child: Form(
                child: Column(
                  children: formFields.map((field) => _buildField(field)).toList(),
                ),
              ),
            );
          }
        },
      ),
    );
  }

  // 根据字段类型构建表单字段
  Widget _buildField(Map<String, dynamic> field) {
    final String? fieldName = field['name'];
    final String? fieldType = field['type'];
    final String? fieldPlaceholder = field['placeholder'];
    final bool required = field['required'];

    if (fieldType == 'string') {
      return TextField(
        decoration: InputDecoration(
          labelText: fieldName,
          hintText: fieldPlaceholder,
        ),
        onChanged: (value) {
          // 处理文本变化，可能需要将值存储在状态中以供后续使用
        },
      );
    } else if (fieldType == 'integer') {
      // 对于整型字段，您可以使用输入掩码或数字键盘
      return TextField(
        keyboardType: TextInputType.number,
        decoration: InputDecoration(
          labelText: fieldName,
          hintText: fieldPlaceholder,
        ),
        onChanged: (value) {
          // 处理数值变化
        },
      );
    } else if (fieldType == 'date') {
      // 对于日期字段，您可能需要使用日期选择器
      // 这里仅作为示例，实际实现可能需要一个日期选择器小部件
      return Text('Date Field: $fieldName');
    }

    // 如果字段类型未知，返回一个简单的标签
    return Text('Unknown Field: $fieldName');
  }

}
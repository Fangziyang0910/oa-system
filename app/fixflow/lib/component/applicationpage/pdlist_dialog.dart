import 'package:fixflow/component/applicationpage/applicationform.dart';
import 'package:fixflow/component/error_snackbar.dart';
import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:provider/provider.dart';

class PDListDialog extends StatefulWidget {
  const PDListDialog({Key? key}) : super(key: key);

  @override
  _PDListDialogState createState() => _PDListDialogState();
}

class _PDListDialogState extends State<PDListDialog> {
  late Future<List<Map<String, dynamic>>> _fetchProcessDefinitions;

  @override
  void initState() {
    super.initState();
    // 在初始化时请求服务器获取流程列表数据
    _fetchProcessDefinitions = _fetchData();
  }

  Future<List<Map<String, dynamic>>> _fetchData() async {
    // 从Provider中获取token
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    // 发送请求获取流程列表数据
    try {
      final response = await http.get(
        Uri.parse(ApiUrls.listProcessDefinitions),
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
        if (code == 0000) {
          final List<dynamic> dataList = responseData['data'];
          // 将数据转换为List<Map<String, dynamic>>类型
          final List<Map<String, dynamic>> processDefinitions =
              List<Map<String, dynamic>>.from(dataList);
          return processDefinitions;
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
    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(8.0),
      ),
      elevation: 0.0,
      backgroundColor: Colors.transparent,
      child: Container(
        padding: EdgeInsets.all(16.0),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(8.0),
        ),
        constraints: BoxConstraints(
          maxWidth: MediaQuery.of(context).size.width *
              0.8, // Limit the max width to 80% of the screen width
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              '请选择申请流程',
              style: TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 18.0,
              ),
            ),
            SizedBox(height: 16.0),
            FutureBuilder<List<Map<String, dynamic>>>(
              future: _fetchProcessDefinitions,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return CircularProgressIndicator();
                } else if (snapshot.hasError) {
                  return Text('Failed to load data: ${snapshot.error}');
                } else {
                  final List<Map<String, dynamic>> processDefinitions =
                      snapshot.data ?? [];
                  return ListView.builder(
                    shrinkWrap: true,
                    itemCount: processDefinitions.length,
                    itemBuilder: (context, index) {
                      final processDefinition = processDefinitions[index];
                      return GestureDetector(
                        onTap: () {
                          // 获取所点击的流程定义
                          final processDefinition = processDefinitions[index];
                          // 导航到新的小部件，并传递processDefinitionKey作为参数
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => ApplicationForm(
                                  processDefinitionKey: processDefinition[
                                      'processDefinitionKey']),
                            ),
                          );
                        },
                        child: Container(
                          margin: EdgeInsets.symmetric(vertical: 4.0),
                          padding: EdgeInsets.all(8.0),
                          decoration: BoxDecoration(
                            color: Colors.grey[200], // Default color
                            borderRadius: BorderRadius.circular(8.0),
                          ),
                          child: ListTile(
                            title: Text(
                                processDefinition['processDefinitionName'] ??
                                    ''),
                            subtitle: Text(
                                processDefinition['processDefinitionKey'] ??
                                    ''),
                          ),
                        ),
                      );
                    },
                  );
                }
              },
            ),
          ],
        ),
      ),
    );
  }
}

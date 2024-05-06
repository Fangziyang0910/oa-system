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
    final response = await http.get(
      Uri.parse(ApiUrls.listProcessDefinitions),
      headers: {
        'Accept': 'application/json',
        'Authorization': token ?? '',
      },
    );

    if (response.statusCode == 200) {
      // 解析响应数据
      final Map<String, dynamic> responseData = jsonDecode(response.body);
      final List<dynamic> dataList = responseData['data'];
      // 将数据转换为List<Map<String, dynamic>>类型
      final List<Map<String, dynamic>> processDefinitions = List<Map<String, dynamic>>.from(dataList);
      return processDefinitions;
    } else {
      // 请求失败时返回空列表
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
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              '请选择申请流程', // 标题
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
                  // 请求数据中，显示loading动画
                  return CircularProgressIndicator();
                } else if (snapshot.hasError) {
                  // 请求数据失败，显示错误信息
                  return Text('Failed to load data: ${snapshot.error}');
                } else {
                  // 请求数据成功，显示流程列表
                  final List<Map<String, dynamic>> processDefinitions = snapshot.data ?? [];
                  return Expanded(
                    child: ListView.builder(
                      itemCount: processDefinitions.length,
                      itemBuilder: (context, index) {
                        final processDefinition = processDefinitions[index];
                        return ListTile(
                          title: Text(processDefinition['processDefinitionName'] ?? ''),
                          subtitle: Text(processDefinition['processDefinitionKey'] ?? ''),
                          onTap: () {
                            // 处理流程点击事件
                            // 可以在这里添加处理流程点击的逻辑
                          },
                        );
                      },
                    ),
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

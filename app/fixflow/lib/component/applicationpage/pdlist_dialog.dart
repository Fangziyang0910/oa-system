// ignore_for_file: use_super_parameters, library_private_types_in_public_api, use_build_context_synchronously, prefer_const_constructors

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
    _fetchProcessDefinitions = _fetchData();
  }

  Future<List<Map<String, dynamic>>> _fetchData() async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    try {
      final response = await http.get(
        Uri.parse(ApiUrls.listProcessDefinitions),
        headers: {
          'Accept': 'application/json',
          'Authorization': token ?? '',
        },
      );

      if (response.statusCode == 200) {
        final decodedResponse = utf8.decode(response.bodyBytes);
        Map<String, dynamic> responseData = jsonDecode(decodedResponse);
        final int code = responseData['code'];
        if (code == 0000) {
          final List<dynamic> dataList = responseData['data'];
          return List<Map<String, dynamic>>.from(dataList);
        } else {
          _handleErrorCode(context, code);
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

  void _handleErrorCode(BuildContext context, int code) {
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
      default:
        message = "未知错误";
    }
    ErrorSnackbar.showSnackBar(context, message);
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      clipBehavior: Clip.antiAlias,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(8.0),
      ),
      elevation: 0.0,
      backgroundColor: Colors.transparent,
      child: Container(
        padding: EdgeInsets.all(16.0),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16.0),
        ),
        constraints: BoxConstraints(
          maxWidth: MediaQuery.of(context).size.width * 0.8,
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
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
                  return Center(child: CircularProgressIndicator());
                } else if (snapshot.hasError) {
                  return Text('Failed to load data: ${snapshot.error}');
                } else {
                  final List<Map<String, dynamic>> processDefinitions = snapshot.data ?? [];
                  return Flexible(
                    child: ListView.builder(
                      shrinkWrap: true,
                      itemCount: processDefinitions.length,
                      itemBuilder: (context, index) {
                        final processDefinition = processDefinitions[index];
                        return GestureDetector(
                          onTap: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => ApplicationForm(
                                  processDefinitionKey: processDefinition['processDefinitionKey'],
                                ),
                              ),
                            );
                          },
                          child: Card(
                            child: Container(
                              padding: EdgeInsets.all(8.0),
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(8.0),
                              ),
                              child: ListTile(
                                title: Text(
                                  processDefinition['processDefinitionName'] ?? 'Unnamed Process',
                                  style: TextStyle(fontWeight: FontWeight.bold),
                                ),
                                subtitle: Text(
                                  processDefinition['processDefinitionKey'] ?? 'No Key',
                                ),
                              ),
                            ),
                          ),
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


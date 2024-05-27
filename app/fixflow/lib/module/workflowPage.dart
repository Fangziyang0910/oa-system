import 'dart:convert';
import 'package:fixflow/component/adminpage/processDefinitionPage.dart';
import 'package:fixflow/component/error_snackbar.dart';
import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:provider/provider.dart';

class WorkflowManagementPage extends StatefulWidget {
  const WorkflowManagementPage({super.key});

  @override
  State<WorkflowManagementPage> createState() => _WorkflowManagementPageState();
}

class _WorkflowManagementPageState extends State<WorkflowManagementPage> {
  late Future<List<Map<String, dynamic>>> _fetchProcessDefinitions; // Future to fetch process definitions
  bool _isLoading = true; // Loading state
  List<Map<String, dynamic>> _processDefinitions = [];  // List of process definitions

  @override
  void initState() {  // Initialize the fetch process
    super.initState();
    _fetchProcessDefinitions = _fetchData();
    _fetchProcessDefinitions.then((data) {
      setState(() {
        _processDefinitions = data;
        _isLoading = false;
      });
    });
  }

  Future<List<Map<String, dynamic>>> _fetchData() async {  // Fetch data from API
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    try {
      final response = await http.get(
        Uri.parse(ApiUrls.adminlistProcessDefinitions),
        headers: {
          'Accept': 'application/json',
          'Authorization': token ?? '',
        },
      );

      if (response.statusCode == 200) {
        final decodedResponse = utf8.decode(response.bodyBytes);
        Map<String, dynamic> responseData = jsonDecode(decodedResponse);
        final int code = responseData['code'];
        if (code == 0) {
          final List<dynamic> dataList = responseData['data'];
          final List<Map<String, dynamic>> processDefinitions =
              List<Map<String, dynamic>>.from(dataList);
          return processDefinitions;
        } else {
          _showErrorMessage(context, code);
          return [];
        }
      } else {
        return [];
      }
    } catch (e) {
      // Show network error snackbar
      ErrorSnackbar.showSnackBar(context, '请检查网络');
      return [];
    }
  }
  // Refresh data when pulled down
  Future<void> _refreshData() async {
    setState(() {
      _isLoading = true;
    });
    final data = await _fetchData();
    setState(() {
      _processDefinitions = data;
      _isLoading = false;
    });
  }
  // Display error messages based on error codes
  void _showErrorMessage(BuildContext context, int code) {
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('流程管理'),
      ),
      body: _isLoading
          ? Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _refreshData,
              child: ListView.builder(
                itemCount: _processDefinitions.length,
                itemBuilder: (context, index) {
                  final processDefinition = _processDefinitions[index];
                  return Card(
                    margin: EdgeInsets.all(16.0),
                    child: ListTile(
                      title: Row(
                        children: <Widget>[
                          Expanded(
                            child: Text(
                              processDefinition['processDefinitionName'],
                              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                            ),
                          ),
                          Text(
                            '版本: ${processDefinition['processDefinitionVersion']}',
                            style: TextStyle(fontSize: 16),
                          ),
                          
                        ],
                      ),
                      subtitle: Text(
                        processDefinition['processDefinitionKey'],
                        style: TextStyle(fontSize: 14, color: Colors.grey),
                      ),
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => ProcessDefinitionPage(
                              processDefinition: processDefinition,
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

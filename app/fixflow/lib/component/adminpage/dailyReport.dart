// ignore_for_file: prefer_interpolation_to_compose_strings

import 'dart:convert';
import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:intl/intl.dart';
import 'package:provider/provider.dart'; // 需要添加 intl 包

class DailyReport extends StatefulWidget {
  const DailyReport({super.key});

  @override
  State<DailyReport> createState() => _DailyReportState();
}

class _DailyReportState extends State<DailyReport> {
  TextEditingController _dateController = TextEditingController();
  Map<String, dynamic>? _reportData;
  bool _loading = false;
  bool _hasError = false;
  String _errorMessage = '';

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: DateTime.now(),
      firstDate: DateTime(2000),
      lastDate: DateTime(2100),
    );
    if (picked != null) {
      setState(() {
        _dateController.text = DateFormat('yyyy-MM-dd').format(picked);
        _fetchReport(_dateController.text);
      });
    }
  }

  Future<void> _fetchReport(String date) async {
    setState(() {
      _loading = true;
      _hasError = false;
      _reportData = null;
      _errorMessage = '';
    });

    final String url = ApiUrls.getDailyReport + '/' + date;
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;

    try {
      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Accept': 'application/json',
          'Authorization': token ?? '',
        },
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> data = jsonDecode(utf8.decode(response.bodyBytes))['data'];
        setState(() {
          _reportData = data;
          _loading = false;
        });
      } else if (response.statusCode == 500) {
        setState(() {
          _errorMessage = '该日暂无日报';
          _hasError = true;
          _loading = false;
        });
      } else {
        setState(() {
          _errorMessage = '发生错误';
          _hasError = true;
          _loading = false;
        });
      }
    } catch (e) {
      setState(() {
        _errorMessage = '发生错误: $e';
        _hasError = true;
        _loading = false;
      });
    }
  }

  Widget _buildReportContent() {
    if (_hasError) {
      return Center(child: Text(_errorMessage, style: TextStyle(fontSize: 18, color: Colors.red)));
    }

    if (_reportData == null) {
      return Center(child: Text('请选择一个日期', style: TextStyle(fontSize: 18)));
    }

    final String createTime = _reportData!['createTime'];
    final Map<String, dynamic> contentData = jsonDecode(_reportData!['content']);
    final Map<String, dynamic> taskInfo = contentData['任务信息'];
    final List<dynamic> processDetails = contentData['流程详细信息'];

    return ListView(
      children: [
        ListTile(
          title: Text(
            '生成日期',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          subtitle: Text(
            createTime,
            style: TextStyle(fontSize: 16),
          ),
        ),
        ExpansionTile(
          title: Text(
            '当日运维概览',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          initiallyExpanded: true, // 默认展开
          children: taskInfo.entries.map<Widget>((entry) {
            return Padding(
              padding: const EdgeInsets.symmetric(vertical: 4.0, horizontal: 16.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    entry.key,
                    style: TextStyle(fontSize: 16),
                  ),
                  Text(
                    entry.value,
                    style: TextStyle(fontSize: 16),
                  ),
                ],
              ),
            );
          }).toList(),
        ),
        ...processDetails.map<Widget>((process) {
          final Map<String, dynamic> processMap = process as Map<String, dynamic>;
          final String processName = processMap['流程名称'];

          return ExpansionTile(
            title: Text(
              processName,
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            initiallyExpanded: true, // 默认展开
            children: processMap.entries.map<Widget>((entry) {
              if (entry.key == '流程名称') return Container();
              return Padding(
                padding: const EdgeInsets.symmetric(vertical: 4.0, horizontal: 16.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      entry.key,
                      style: TextStyle(fontSize: 16),
                    ),
                    Text(
                      entry.value,
                      style: TextStyle(fontSize: 16),
                    ),
                  ],
                ),
              );
            }).toList(),
          );
        }).toList(),
      ],
    );
  }

  @override
  void dispose() {
    _dateController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            TextField(
              controller: _dateController,
              readOnly: true,
              onTap: () => _selectDate(context),
              decoration: InputDecoration(
                labelText: '选择日期',
                suffixIcon: Icon(Icons.calendar_today),
                border: OutlineInputBorder(),
              ),
            ),
            SizedBox(height: 16),
            if (_loading) Center(child: CircularProgressIndicator()),
            if (!_loading && _hasError) Center(child: Text(_errorMessage, style: TextStyle(color: Colors.red))),
            if (!_loading && !_hasError) Expanded(child: _buildReportContent()),
          ],
        ),
      ),
    );
  }
}

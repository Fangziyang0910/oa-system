// ignore_for_file: prefer_interpolation_to_compose_strings

import 'dart:convert';
import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:provider/provider.dart';

class WeeklyReport extends StatefulWidget {
  const WeeklyReport({super.key});

  @override
  State<WeeklyReport> createState() => _WeeklyReportState();
}

class _WeeklyReportState extends State<WeeklyReport> {
  Map<String, List<dynamic>> _weeklyReportsByYear = {};
  String? _selectedReport;
  String? _selectedReportId;
  bool _loading = false;
  bool _hasError = false;
  String _errorMessage = '';
  Map<String, dynamic>? _reportDetails;

  @override
  void initState() {
    super.initState();
    _fetchWeeklyReports();
  }

  Future<void> _fetchWeeklyReports() async {
    setState(() {
      _loading = true;
      _hasError = false;
      _errorMessage = '';
    });

    final String url = ApiUrls.listWeeklyReports;
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
        final Map<String, dynamic> data = jsonDecode(utf8.decode(response.bodyBytes));
        final List<dynamic> reports = data['data'];

        final Map<String, List<dynamic>> reportsByYear = {};
        for (var report in reports) {
          final String startTime = report['startTime'];
          final String endTime = report['endTime'];
          final String year = startTime.split('-')[0];
          final String startMonthDay = startTime.substring(5);
          final String endMonthDay = endTime.substring(5);

          if (!reportsByYear.containsKey(year)) {
            reportsByYear[year] = [];
          }
          reportsByYear[year]!.add({
            'dateRange': '$startMonthDay - $endMonthDay',
            'startTime': startTime,
            'endTime': endTime,
            'reportId': report['reportId']
          });
        }

        setState(() {
          _weeklyReportsByYear = reportsByYear;
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

  Future<void> _fetchWeeklyReportDetails(String reportId) async {
    setState(() {
      _loading = true;
      _hasError = false;
      _errorMessage = '';
      _reportDetails = null;
    });

    final String url = ApiUrls.getWeeklyReport + '/' + reportId;
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
        final Map<String, dynamic> data = jsonDecode(utf8.decode(response.bodyBytes));
        setState(() {
          _reportDetails = data['data'];
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

  String _formatProcessingTime(String processingTime) {
    final regex = RegExp(r'(\d+) 小时');
    final match = regex.firstMatch(processingTime);
    if (match != null) {
      return '${match.group(1)} 小时';
    }
    return processingTime;
  }

  Widget _buildReportDetails() {
    if (_reportDetails == null) {
      return Center(child: Text('请选择一个周报'));
    }

    final String createTime = _reportDetails!['createTime'];
    final Map<String, dynamic> contentData = jsonDecode(_reportDetails!['content']);
    final Map<String, dynamic> taskInfo = contentData['任务信息'];
    final List<dynamic> processDetails = contentData['流程详细信息'];

    return ListView(
      shrinkWrap: true,
      physics: NeverScrollableScrollPhysics(),
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
            '本周运维概览',
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
                    entry.key.contains('处理时间') ? _formatProcessingTime(entry.value) : entry.value,
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
                      entry.key.contains('处理时间') ? _formatProcessingTime(entry.value) : entry.value,
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

  Widget _buildReportContent() {
    if (_hasError) {
      return Center(child: Text(_errorMessage, style: TextStyle(fontSize: 18, color: Colors.red)));
    }

    if (_loading) {
      return Center(child: CircularProgressIndicator());
    }

    if (_weeklyReportsByYear.isEmpty) {
      return Center(child: Text('暂无周报', style: TextStyle(fontSize: 18)));
    }

    return ListView(
      shrinkWrap: true,
      physics: NeverScrollableScrollPhysics(),
      children: _weeklyReportsByYear.entries.map((entry) {
        final String year = entry.key;
        final List<dynamic> reports = entry.value;
        return ExpansionTile(
          title: Text('$year年', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
          children: reports.map((report) {
            final String dateRange = report['dateRange'];
            final String reportId = report['reportId'];
            return Padding(
              padding: const EdgeInsets.all(8.0),
              child: ChoiceChip(
                label: Text(dateRange, style: TextStyle(fontSize: 16)),
                selected: _selectedReport == dateRange,
                onSelected: (bool selected) {
                  setState(() {
                    _selectedReport = selected ? dateRange : null;
                    _selectedReportId = selected ? reportId : null;
                    if (_selectedReportId != null) {
                      _fetchWeeklyReportDetails(_selectedReportId!);
                    }
                  });
                },
              ),
            );
          }).toList(),
        );
      }).toList(),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SingleChildScrollView(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: _buildReportContent(),
            ),
            if (_selectedReportId != null)
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: _buildReportDetails(),
              ),
          ],
        ),
      ),
    );
  }
}

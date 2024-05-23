import 'package:fixflow/component/adminpage/dailyReport.dart';
import 'package:fixflow/component/adminpage/weeklyRepoet.dart';
import 'package:flutter/material.dart';


class OperationalReportPage extends StatefulWidget {
  const OperationalReportPage({super.key});

  @override
  State<OperationalReportPage> createState() => _OperationalReportPageState();
}

class _OperationalReportPageState extends State<OperationalReportPage> {
  String _selectedReportType = '周报'; // 初始化为周报

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: PopupMenuButton<String>(
          icon: Icon(Icons.menu), // 三条横线的图标
          onSelected: (String newValue) {
            setState(() {
              _selectedReportType = newValue;
            });
          },
          itemBuilder: (BuildContext context) {
            return ['周报', '日报'].map((String choice) {
              return PopupMenuItem<String>(
                value: choice,
                child: Text(choice),
              );
            }).toList();
          },
        ),
        title: Text('运维$_selectedReportType'),
      ),
      body: _selectedReportType == '周报' ? WeeklyReport() : DailyReport(),
    );
  }
}

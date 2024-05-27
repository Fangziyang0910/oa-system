import 'package:fixflow/component/adminpage/dailyReport.dart';
import 'package:fixflow/component/adminpage/weeklyRepoet.dart';
import 'package:flutter/material.dart';

// Stateful widget for the operational report page
class OperationalReportPage extends StatefulWidget {
  const OperationalReportPage({super.key});

  @override
  State<OperationalReportPage> createState() => _OperationalReportPageState();
}

class _OperationalReportPageState extends State<OperationalReportPage> {
  // State variable to keep track of the selected report type, initialized to '周报' (Weekly Report)
  String _selectedReportType = '周报'; // 初始化为周报

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // AppBar with a title and a PopupMenuButton to select report type
      appBar: AppBar(
        leading: PopupMenuButton<String>(
          icon: Icon(Icons.menu), // 三条横线的图标
          onSelected: (String newValue) {
            setState(() {
              _selectedReportType = newValue;
            });
          },
          itemBuilder: (BuildContext context) {
            // Create menu items for '周报' (Weekly Report) and '日报' (Daily Report)
            return ['周报', '日报'].map((String choice) {
              return PopupMenuItem<String>(
                value: choice,
                child: Text(choice),
              );
            }).toList();
          },
        ),
        // Display the selected report type in the AppBar title
        title: Text('运维$_selectedReportType'),
      ),
      // Display the corresponding report widget based on the selected report type
      body: _selectedReportType == '周报' ? WeeklyReport() : DailyReport(),
    );
  }
}

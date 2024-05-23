// ignore_for_file: prefer_const_constructors

import 'package:fixflow/module/admininfopage.dart';
import 'package:fixflow/module/operationalReportPage.dart';
import 'package:fixflow/module/operationalStatusPage.dart';
import 'package:fixflow/module/workflowPage.dart';
import 'package:flutter/material.dart';

class AdminHomePage extends StatefulWidget {
  const AdminHomePage({super.key});

  @override
  State<AdminHomePage> createState() => _AdminHomePageState();
}

class _AdminHomePageState extends State<AdminHomePage> {
  int _selectedIndex = 0;

  final List<Widget> _widgetOptions = <Widget>[
    WorkflowManagementPage(), // Placeholder widget for "流程管理"
    OperationalStatusPage(), // Placeholder widget for "运维实况"
    OperationalReportPage(), // Placeholder widget for "运维报告"
    AdminInfoPage(), // Placeholder widget for "个人中心"
  ];

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const SizedBox.shrink(), // Hide the title
        toolbarHeight: 0, // Set title's height to zero
      ),
      body: Center(
        child: _widgetOptions.elementAt(_selectedIndex), // Show the selected widget
      ),
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed, // Set type to fixed
        items: const <BottomNavigationBarItem>[
          BottomNavigationBarItem(
            icon: Icon(Icons.work_outline),
            label: '流程管理', // "Workflow Management"
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.monitor),
            label: '运维实况', // "Operational Status"
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.report),
            label: '运维报告', // "Operational Report"
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person),
            label: '个人中心', // "Personal Info"
          ),
        ],
        currentIndex: _selectedIndex,
        onTap: _onItemTapped,
      ),
    );
  }
}







// ignore_for_file: prefer_const_constructors, use_super_parameters

import 'dart:convert';
import 'package:fixflow/module/applicationpage.dart';
import 'package:fixflow/module/approvalpage.dart';
import 'package:fixflow/module/operationpage.dart';
import 'package:fixflow/module/personalinfopage.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  int _selectedIndex = 0;
  late Future<List<BottomNavigationBarItem>> _loadUserPermissionsFuture;
  late List<Widget> _widgetOptions;


  @override
  void initState() {
    super.initState();
    _loadUserPermissionsFuture = _loadUserPermissions();
  }

  Future<List<BottomNavigationBarItem>> _loadUserPermissions() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? userDataJson = prefs.getString('userData');
    if (userDataJson == null) {
      throw Exception("User data not found in SharedPreferences");
    }
    Map<String, dynamic> userData = jsonDecode(userDataJson);
    
    bool isApplicant = userData['isApplicant'] ?? false;
    bool isApprover = userData['isApprover'] ?? false;
    bool isOperator = userData['isOperator'] ?? false;

    List<BottomNavigationBarItem> bottomNavBarItems = [];
    _widgetOptions = [];

    if (isApplicant) {
      bottomNavBarItems.add(
        BottomNavigationBarItem(
          icon: Icon(Icons.assignment), // Icon for "My Applications"
          label: '我的申请', // Label for "My Applications"
        ),
      );
      _widgetOptions.add(ApplicationPage()); // Widget for "我的申请"
    }

    if (isApprover) {
      bottomNavBarItems.add(
        BottomNavigationBarItem(
          icon: Icon(Icons.approval), // Icon for "My Approvals"
          label: '我的审批', // Label for "My Approvals"
        ),
      );
      _widgetOptions.add(ApprovalPage()); // Widget for "我的审批"
    }

    if (isOperator) {
      bottomNavBarItems.add(
        BottomNavigationBarItem(
          icon: Icon(Icons.build), // Icon for "My Operations"
          label: '我的运维', // Label for "My Operations"
        ),
      );
      _widgetOptions.add(OperatorPage()); // Widget for "我的运维"
    }

    bottomNavBarItems.add(
      BottomNavigationBarItem(
        icon: Icon(Icons.person), // Icon for "Personal Info"
        label: '个人中心', // Label for "Personal Info"
      ),
    );
    _widgetOptions.add(PersonalInfo()); // Widget for "个人中心"

    return bottomNavBarItems;
  }

  // Handling of click events
  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List<BottomNavigationBarItem>>(
      future: _loadUserPermissionsFuture,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return Scaffold(body: Center(child: CircularProgressIndicator()));
        } else if (snapshot.hasError) {
          return Scaffold(body: Center(child: Text('Error: ${snapshot.error}')));
        } else {
          List<BottomNavigationBarItem> bottomNavBarItems = snapshot.data ?? [];

          return Scaffold(
            appBar: AppBar(
              title: const SizedBox.shrink(), // hide the title
              toolbarHeight: 0, // set title's height = zero
            ),
            body: Center(
              child: _widgetOptions.elementAt(_selectedIndex), // 根据索引显示对应的 widget
            ),
            bottomNavigationBar: BottomNavigationBar(
              type: BottomNavigationBarType.fixed, // 设置类型为固定
              items: bottomNavBarItems,
              currentIndex: _selectedIndex,
              onTap: _onItemTapped,
            ),
          );
        }
      },
    );
  }
}
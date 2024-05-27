// ignore_for_file: use_super_parameters, library_private_types_in_public_api, prefer_const_constructors

import 'package:fixflow/component/approvalpage/current_approvallist.dart';
import 'package:fixflow/component/approvalpage/current_candidateApprovalList.dart';
import 'package:fixflow/component/approvalpage/history_approvallist.dart';
import 'package:flutter/material.dart';

// Stateful widget for the approval page
class ApprovalPage extends StatefulWidget {
  const ApprovalPage({Key? key}) : super(key: key);

  @override
  _ApprovalPageState createState() => _ApprovalPageState();
}

class _ApprovalPageState extends State<ApprovalPage> {
  int _selectedIndex = 0; // Default selection index for the first option

  // 选项名称
  final List<String> _options = ['我的审批', '历史审批', '审批申领'];

  // 页面组件列表
  final List<Widget> _pages = [
    CurrentApprovalListWidget(),
    HistoryApprovalListWidget(),
    CurrentCandidateApprovalList(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        // Display the selected option name as the title
        title: Text(_options[_selectedIndex]), // 显示选项名称
        leading: IconButton(
          icon: Icon(Icons.menu),// Rounded corners for the menu
          onPressed: () {
            // Show popup menu for selecting options
            showMenu<int>(
              context: context,
              position: RelativeRect.fromLTRB(0, kToolbarHeight, 0, 0),
              elevation: 8, // 菜单的阴影
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(8.0), // 菜单的边角
              ),
              items: [
                PopupMenuItem<int>(
                  value: 0,
                  child: Text('我的审批'),// Option for "My Approvals"
                ),
                PopupMenuItem<int>(
                  value: 1,
                  child: Text('历史审批'),// Option for "Historical Approvals"
                ),
                PopupMenuItem<int>(
                  value: 2,
                  child: Text('审批申领'),// Option for "Approval Application"
                ),
              ],
            ).then((value) {
              if (value != null) {
                // Update the selected index and refresh the UI
                setState(() {
                  _selectedIndex = value;
                });
              }
            });
          },
        ),
      ),
      // Display the page corresponding to the selected index
      body: _pages[_selectedIndex],
    );
  }
}

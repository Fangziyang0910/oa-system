// ignore_for_file: use_super_parameters, library_private_types_in_public_api, prefer_const_constructors

import 'package:fixflow/component/approvalpage/current_approvallist.dart';
import 'package:fixflow/component/approvalpage/history_approvallist.dart';
import 'package:flutter/material.dart';

class ApprovalPage extends StatefulWidget {
  const ApprovalPage({Key? key}) : super(key: key);

  @override
  _ApprovalPageState createState() => _ApprovalPageState();
}

class _ApprovalPageState extends State<ApprovalPage> {
  int _selectedIndex = 0; // 默认选中第一个选项

  // 选项名称
  final List<String> _options = ['当前审批', '历史审批'];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_options[_selectedIndex]), // 显示选项名称
        leading: IconButton(
          icon: Icon(Icons.menu),
          onPressed: () {
            // Show popup menu
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
                  child: Text('当前审批'),
                ),
                PopupMenuItem<int>(
                  value: 1,
                  child: Text('历史审批'),
                ),
              ],
            ).then((value) {
              if (value != null) {
                setState(() {
                  _selectedIndex = value;
                });
              }
            });
          },
        ),
      ),
      body: _selectedIndex == 0 ? CurrentApprovalListWidget() : HistoryApprovalListWidget(),
    );
  }
}

// ignore_for_file: prefer_const_constructors, use_super_parameters, library_private_types_in_public_api

import 'package:fixflow/component/applicationpage/current_applicationlist.dart';
import 'package:fixflow/component/applicationpage/history_applicationlist.dart';
import 'package:flutter/material.dart';
import 'package:fixflow/component/applicationpage/pdlist_dialog.dart';

class ApplicationPage extends StatefulWidget {
  const ApplicationPage({Key? key}) : super(key: key);

  @override
  _ApplicationPageState createState() => _ApplicationPageState();
}

class _ApplicationPageState extends State<ApplicationPage> {
  int _selectedIndex = 0; // 默认选中第一个选项

  // 选项名称
  final List<String> _options = ['当前申请', '历史申请'];

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
                  child: Text('当前申请'),
                ),
                PopupMenuItem<int>(
                  value: 1,
                  child: Text('历史申请'),
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
      body: _selectedIndex == 0 ? CurrentApplicationListWidget() : HistoryApplicationListWidget(),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          // Show dialog
          showDialog(
            context: context,
            builder: (BuildContext context) {
              return PDListDialog();
            },
          );
        },
        child: Icon(Icons.add),
      ),
    );
  }
}

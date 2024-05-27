
import 'package:fixflow/component/applicationpage/historyForm.dart';
import 'package:fixflow/config/classDefinition.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:timeline_tile/timeline_tile.dart';

String parseDateFormat(String originalDate) {
  final DateFormat inputFormat = DateFormat("EEE MMM dd HH:mm:ss 'CST' yyyy");
  final DateTime date = inputFormat.parse(originalDate);
  final DateFormat outputFormat = DateFormat("yyyy-MM-dd HH:mm");
  return outputFormat.format(date);
}


Widget buildTimelineTile(Progress progress, bool isFirstItem, bool isLastItem, BuildContext context) {
  return TimelineTile(
    alignment: TimelineAlign.manual,
    lineXY: 0.1, // 定义线条和内容的相对位置
    isFirst: isFirstItem,
    isLast: isLastItem,
    indicatorStyle: IndicatorStyle(
      width: 20,
      color: Colors.blue,
      padding: EdgeInsets.all(6),
    ),
    endChild: Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                Text(progress.taskName, style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                SizedBox(height: 4),
                Text(parseDateFormat(progress.endTime!), style: TextStyle(fontSize: 14, color: Colors.grey)),
                SizedBox(height: 4),
                Text("执行人：" + (progress.assigneeName ?? '系统'), style: TextStyle(fontSize: 14)),
              ],
            ),
          ),
          if (progress.taskId != "null")
            PopupMenuButton<String>(
              onSelected: (String value) {
                _handleMenuItemClick(value, progress.taskId, context as BuildContext);
              },
              itemBuilder: (BuildContext context) => <PopupMenuEntry<String>>[
                const PopupMenuItem<String>(
                  value: 'ViewForm',
                  child: Text('查看表单'),
                ),
              ],
              icon: Icon(Icons.more_vert),
            ),
        ],
      ),
    ),
  );
}

void _handleMenuItemClick(String value, String taskId, BuildContext context) {
  if (value == 'ViewForm') {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => FormPage(taskId: taskId)),
    );
  }
}

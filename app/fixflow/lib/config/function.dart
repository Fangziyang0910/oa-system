import 'package:intl/intl.dart';

String parseDateFormat(String originalDate) {
  final DateFormat inputFormat = DateFormat("EEE MMM dd HH:mm:ss 'CST' yyyy");
  final DateTime date = inputFormat.parse(originalDate);
  final DateFormat outputFormat = DateFormat("yyyy-MM-dd HH:mm");
  return outputFormat.format(date);
}
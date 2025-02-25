import 'package:flutter_local_notifications/flutter_local_notifications.dart';


// A service class for managing notifications using Flutter Local Notifications plugin.
class NotificationService {
  final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
  FlutterLocalNotificationsPlugin();
  int _notificationId = 0;

  NotificationService() {
    init();
  }

  Future<void> init() async {
    const AndroidInitializationSettings initializationSettingsAndroid =
    AndroidInitializationSettings('ic_launcher');
    final InitializationSettings initializationSettings = InitializationSettings(
      android: initializationSettingsAndroid,
    );

    await flutterLocalNotificationsPlugin.initialize(initializationSettings);
  }

  // Shows a notification with the given [title] and [body].
  Future<void> showNotification(String title, String body) async {
    const AndroidNotificationDetails androidPlatformChannelSpecifics =
    AndroidNotificationDetails(
      'alert_channel', // Channel ID
      'Alert', // Channel name
      channelDescription: 'Channel for alert notifications',
      importance: Importance.max,
      priority: Priority.high,
      showWhen: false,
    );

    const NotificationDetails platformChannelSpecifics =
    NotificationDetails(android: androidPlatformChannelSpecifics);

    await flutterLocalNotificationsPlugin.show(
      _notificationId++, // Increment notification ID for each new notification
      title,
      body,
      platformChannelSpecifics,
    );
  }
}

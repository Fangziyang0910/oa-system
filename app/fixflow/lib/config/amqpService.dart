import 'package:dart_amqp/dart_amqp.dart' as amqp;
import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/notificationService.dart';
import 'package:fixflow/config/user_token_provider.dart';

class AMQPService {
  final NotificationService notificationService;
  final UserTokenProvider userTokenProvider;

  AMQPService(this.notificationService, this.userTokenProvider);

  void startListening() {
    final username = userTokenProvider.username;
    final password = userTokenProvider.password;

    if (username == null || password == null) {
      print("Username or password is null");
      return;
    }

    final client = amqp.Client(
      settings: amqp.ConnectionSettings(
        host: ApiUrls.baseUrl,
        port: 5672,
        authProvider: amqp.PlainAuthenticator(username, password),
      ),
    );

    client.channel().then((amqp.Channel channel) {
      return channel.queue("rabbitmq.topic.$username", durable: true);
    }).then((amqp.Queue queue) {
      print(" [*] Waiting for messages in ${queue.name}. To exit press CTRL+C");

      queue.consume().then((amqp.Consumer consumer) {
        consumer.listen((amqp.AmqpMessage message) {
          print(" [x] Received ${message.payloadAsString}");
          notificationService.showNotification("New Message", message.payloadAsString);
        });
      });
    });
  }
}

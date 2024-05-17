import 'dart:async';
import 'package:fixflow/config/api_url.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:fixflow/config/amqpService.dart';
import 'package:fixflow/config/notificationService.dart';

class UserTokenProvider extends ChangeNotifier {
  String? _token;
  bool _isValid = false;
  String? _username;
  String? _password;
  AMQPService? _amqpService;
  Timer? _amqpTimer;
  bool _isListening = false;

  String? get token => _token;
  bool get isValid => _isValid;
  String? get username => _username;
  String? get password => _password;

  final NotificationService notificationService = NotificationService();


  Future<void> loadToken() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    _token = prefs.getString('token');
    _username = prefs.getString('username');
    _password = prefs.getString('password');
    if (_token != null) {
      _isValid = await _validateToken(_token!);
      if (_isValid) {
        startAMQP();
      }
    }
    notifyListeners();
  }

  Future<bool> _validateToken(String token) async {
    try {
      final response = await http.get(
        Uri.parse(ApiUrls.tokenCheck),
        headers: {
          'Authorization': token,
          'Accept': 'application/json',
        },
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return responseData['code'] == 0;
      } else {
        return false;
      }
    } catch (e) {
      print('Token validation failed: $e');
      return false;
    }
  }

  Future<void> setToken(String token, String username, String password) async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    _token = token;
    _username = username;
    _password = password;
    await prefs.setString('token', token);
    await prefs.setString('username', username);
    await prefs.setString('password', password);
    _isValid = true;
    print("Token, username and password set successfully");
    startAMQP();
    notifyListeners();
  }

  Future<void> clearToken() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    _token = null;
    _username = null;
    _password = null;
    await prefs.remove('token');
    await prefs.remove('username');
    await prefs.remove('password');
    _isValid = false;
    stopAMQP();
    notifyListeners();
  }

  void startAMQP() {
    if (_amqpService == null) {
      _amqpService = AMQPService(notificationService, this);
    }
    if (_amqpTimer == null || !_amqpTimer!.isActive) {
      _amqpTimer = Timer.periodic(Duration(minutes: 1), (timer) {
        _amqpService?.startListening();
      });
      print("AMQP timer started");
    }
    if (!_isListening) {
      _amqpService?.startListening();
      _isListening = true;
      print("AMQP consumer started");
    } else {
      print("AMQP consumer is already running");
    }
  }

  void stopAMQP() {
    _amqpTimer?.cancel();
    _amqpTimer = null;
    _amqpService?.stopListening();
    _amqpService = null;
    _isListening = false;
    print("AMQP consumer stopped");
  }
}

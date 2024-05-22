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
  String? _username;
  String? _password;
  bool _isAdmin = false;
  int _validationResult = 0; // 0: login, 1: user home, 2: admin home
  AMQPService? _amqpService;
  Timer? _amqpTimer;
  bool _isListening = false;

  String? get token => _token;
  String? get username => _username;
  String? get password => _password;
  bool get isAdmin => _isAdmin;
  int get validationResult => _validationResult;

  final NotificationService notificationService = NotificationService();

  Future<void> loadToken() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    _token = prefs.getString('token');
    _username = prefs.getString('username');
    _password = prefs.getString('password');
    _isAdmin = prefs.getBool('isAdmin') ?? false;
    if (_token != null) {
      _validateToken(_token!, _isAdmin);
    }
    notifyListeners();
  }

  Future<void> _validateToken(String token, bool isAdmin) async {
    try {
      final response = await http.get(
        Uri.parse(isAdmin ? ApiUrls.adminTokenCheck : ApiUrls.tokenCheck),
        headers: {
          'Authorization': token,
          'Accept': 'application/json',
        },
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        _validationResult = responseData['code'] == 0 ? (isAdmin ? 2 : 1) : 0;
      } else {
        _validationResult = 0;
      }
    } catch (e) {
      print('Token validation failed: $e');
      _validationResult = 0;
    } finally {
      if (_validationResult == 1) {
        startAMQP();
      }
      notifyListeners();
    }
  }

  Future<void> setToken(String token, String username, String password, bool isAdmin) async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    _token = token;
    _username = username;
    _password = password;
    _isAdmin = isAdmin;
    _validationResult = isAdmin ? 2 : 1;
    await prefs.setString('token', token);
    await prefs.setString('username', username);
    await prefs.setString('password', password);
    await prefs.setBool('isAdmin', isAdmin);
    await prefs.setInt('validationResult', _validationResult);
    print("Token, username, password, and isAdmin set successfully");
    startAMQP();
    notifyListeners();
  }

  Future<void> clearToken() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    _token = null;
    _username = null;
    _password = null;
    _isAdmin = false;
    _validationResult = 0;
    await prefs.remove('token');
    await prefs.remove('username');
    await prefs.remove('password');
    await prefs.remove('isAdmin');
    await prefs.setInt('validationResult', _validationResult);
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
